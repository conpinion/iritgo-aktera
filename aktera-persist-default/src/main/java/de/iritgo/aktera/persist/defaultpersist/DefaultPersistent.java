/**
 * This file is part of the Iritgo/Aktera Framework.
 *
 * Copyright (C) 2005-2011 Iritgo Technologies.
 * Copyright (C) 2003-2005 BueroByte GbR.
 *
 * Iritgo licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iritgo.aktera.persist.defaultpersist;


import EDU.oswego.cs.dl.util.concurrent.Mutex;
import de.iritgo.aktera.persist.DatabaseType;
import de.iritgo.aktera.persist.Helper;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.Relation;
import de.iritgo.aktera.persist.Transaction;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.commons.beanutils.PropertyUtils;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * @version $Revision: 1.5 $ $Date: 2006/10/05 15:12:02 $
 * @author Michael Nash
 */
public class DefaultPersistent extends DefaultPersistentBase implements Persistent
{
	/**
	 * Add a new record to the target table. Assumes that the fields of this
	 * object are populated with data for the new record. All key fields at
	 * least must be supplied with values, and all fields that are specified as
	 * "no nulls". This method also validates all referential integrity
	 * constraints specified by the object.
	 *
	 * @throws PersistenceException
	 *             If the record cannot be added - this includes if the record
	 *             has a duplicate key
	 */
	public synchronized void add() throws PersistenceException
	{
		checkAllowed("A");

		//Added by Santanu Dutt to clear all previous errors due to Add
		this.currentErrors.clear();
		currentErrors.putAll(validateAll());

		if (currentErrors.size() > 0)
		{
			throw new PersistenceException("Field validation errors in persistent '" + myMetaData.getName() + "': ",
							getErrors());
		}

		/*
		 * Go through all the fields, setting to their default values if they
		 * are null
		 */
		String oneFieldName = null;
		Object oneFieldValue = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();
			oneFieldValue = getField(oneFieldName);

			if (getField(oneFieldName) == null)
			{
				setField(oneFieldName, myMetaData.getDefaultValue(oneFieldName));
			}
			else if (oneFieldValue.toString().equals(""))
			{
				setField(oneFieldName, myMetaData.getDefaultValue(oneFieldName));
			}
		}

		boolean needComma = false;

		haveAllKeys(false, true);

		if (getHelper() != null)
		{
			getHelper().beforeAdd(this);
		}

		SuperString sqlCommand = new SuperString(96);

		sqlCommand.append("INSERT INTO ");
		sqlCommand.append(myMetaData.getTableName());
		sqlCommand.append(" (");

		SuperString valuesCommand = new SuperString(64);

		valuesCommand.append(") VALUES (");

		boolean needCommaValues = false;

		String identityFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			checkField(oneFieldName, getFieldString(oneFieldName));

			if ("identity".equals(myMetaData.getAutoIncremented(oneFieldName)))
			{
				identityFieldName = oneFieldName;

				continue;
			}

			if (needComma)
			{
				sqlCommand.append(", ");
			}

			sqlCommand.append(myMetaData.getDBFieldName(oneFieldName));
			needComma = true;

			if (needCommaValues)
			{
				valuesCommand.append(", ");
			}

			if (myMetaData.isAutoIncremented(oneFieldName))
			{
				/*
				 * If identityFieldName is not null, we've already used the one
				 * (and only) identity for this table
				 */
				if ((identityFieldName == null) && myMetaData.isKeyField(oneFieldName)
								&& myMetaData.getDatabaseType().isIdentitySupported()
								&& (myMetaData.getIdGenerator(oneFieldName) == null))
				{
					// Native auto-inc is supported using "IDENTITY" type
					// syntax
					identityFieldName = oneFieldName;
					valuesCommand.append(myMetaData.getDatabaseType().getInsertIdentitySyntax());
				}
				else
				{
					setAutoIncrement(oneFieldName);
					valuesCommand.append("?");
				}
			}
			else
			{
				valuesCommand.append("?");
			}

			needCommaValues = true;
		} /* for each field */
		//Now we merge the values of the parallel loops
		sqlCommand.append(valuesCommand);
		sqlCommand.append(")");

		try
		{
			Connection myConnection = null;
			PreparedStatement ps = null;
			Statement s = null;
			ResultSet rs = null;

			try
			{
				if (currentTransaction != null)
				{
					myConnection = currentTransaction.getConnection();
				}
				else
				{
					try
					{
						myConnection = myDataSource.getConnection();
					}
					catch (ConcurrentModificationException cme)
					{
						addError(cme);
						throw new PersistenceException("Unable to prepare statement", cme, getErrors());
					}
				}

				ps = myConnection.prepareStatement(sqlCommand.toString());

				// Lock the DB table so we can retrieve the identity field
				// in a concurrent-safe fashion
				this.lock();

				// populate the fields for the update
				int count = 1;

				for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
				{
					oneFieldName = (String) i.next();

					if ("identity".equals(myMetaData.getAutoIncremented(oneFieldName)))
					{
						continue;
					}

					//ACR: Changed to properly set based on types. Still some
					// work
					//to do for blobs....
					this.setPreparedStatementObject(ps, count, oneFieldName);
					count++;

					//}
				} /* for each field */
				if (log.isDebugEnabled())
				{
					log.debug("Executing: " + sqlCommand.toString() + "(" + ps.toString() + ") on " + toString());
				}

				int updateCount = ps.executeUpdate();

				if ((updateCount == 0) && (getCheckZeroUpdate()))
				{
					throw new PersistenceException("No records updated", getErrors());
				}

				ps.close();
				ps = null;

				if (identityFieldName != null)
				{
					s = myConnection.createStatement();
					rs = s.executeQuery(myMetaData.getDatabaseType().getRetrieveIdentitySyntax(myMetaData,
									identityFieldName));

					if (rs.next())
					{
						String identityValue = rs.getString(1);

						setField(identityFieldName, identityValue);
						rs.close();
						rs = null;
						s.close();
						s = null;
					}
					else
					{
						throw new PersistenceException("No value returned for identity field: " + identityFieldName,
										getErrors());
					}
				}
			}
			catch (ClassCastException cce)
			{
				addError(cce);
				throw new PersistenceException("Unable to add record to database" + toString() + ":" + cce.getMessage()
								+ " (" + sqlCommand + ")", getErrors());
			}
			catch (PersistenceException de)
			{
				addError(de);
				throw new PersistenceException("Unable to add record to database" + toString() + ":" + de.getMessage()
								+ " (" + sqlCommand + ")", getErrors());
			}
			finally
			{
				//unlock the DB table
				this.unlock();
				cleanUp(myConnection, s, rs, ps);
			}

			setStatus(Persistent.CURRENT);

			if (getHelper() != null)
			{
				getHelper().afterAdd(this);
			}
		}
		catch (SQLException se)
		{
			addError(se);
			throw new PersistenceException(se, getErrors());
		}
	} /* add() */

	/**
	 * Determine if a record with this key exists already - if not, add a new
	 * record. Note that this method uses just the key fields to determine if
	 * the record already exists.
	 */
	public synchronized void addIfNeeded() throws PersistenceException
	{
		Persistent searchObj = getFactory().create(getName());
		String oneFieldName = null;

		for (Iterator i = myMetaData.getKeyFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();
			searchObj.setField(oneFieldName, getField(oneFieldName));
		}

		if (! searchObj.find())
		{
			add();
		}
	} /* addOrUpdate() */

	/**
	 * Determine if a record with these fields exists already - if so, update.
	 * If not, add a new record. Note that this method uses *all* of the fields
	 * currently set as search criteria (e.g. not just the key)
	 */
	public synchronized void addOrUpdate() throws PersistenceException
	{
		Persistent searchObj = getFactory().create(getName());
		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();
			searchObj.setField(oneFieldName, getField(oneFieldName));
		}

		if (! searchObj.find())
		{
			add();
		}
		else
		{
			update();
		}
	} /* addOrUpdate() */

	/**
	 * Find the average of the values in the specified field of records
	 * se;lected by the Persistent selection criteria.
	 *
	 * @param String
	 *            Persistent fieldName to average
	 * @return double Average of the records matching the criteria
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public double average(String fieldName) throws PersistenceException
	{
		return sqlAggrFunction("AVG", fieldName);
	} /* average() */

	/**
	 * Set all fields to empty value & clear the last result set
	 *
	 * @throws PersistenceException
	 *             If the fields cannot be cleared
	 */
	public synchronized void clear() throws PersistenceException
	{
		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();
			setField(oneFieldName, null);
		}

		sortKeys = null;
	} /* clear() */

	/**
	 * Just like find, but only retrieves the count, not the records
	 * themselves.
	 *
	 * @return integer Count of the records matching the criteria
	 * @throws PersistenceException
	 *             If the search could not be completed
	 * @see #find
	 */
	public synchronized int count() throws PersistenceException
	{
		if (! (this instanceof RowSecurablePersistent))
		{
			checkAllowed("L");
		}

		foundKeys = null;

		SuperString myStatement = new SuperString(48);

		myStatement.append("SELECT COUNT(*) FROM ");
		myStatement.append(myMetaData.getTableName());

		if (customWhereClause != null)
		{
			myStatement.append(customWhereClause);
		}
		else
		{
			myStatement.append(buildWhereClauseBuffer(true));
		}

		int retVal = 0;

		Statement s = null;
		ResultSet rs = null;

		Connection myConnection = null;

		try
		{
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			s = myConnection.createStatement();

			if (log.isDebugEnabled())
			{
				log.debug("Executing: " + myStatement.toString());
			}

			rs = s.executeQuery(myStatement.toString());

			if (rs.next())
			{
				retVal = rs.getInt(1);
			}

			s.close();
			s = null;

			rs.close();
			rs = null;
		}
		catch (Exception de)
		{
			throw new PersistenceException(de);
		}
		finally
		{
			cleanUp(myConnection, s, rs, null);
		}

		return retVal;
	} /* count() */

	/**
	 * Delete a record from the target table. The current field values for the
	 * key of this object tell us which record to delete. This default version
	 * of delete also deletes associated detail records (e.g. performs a
	 * "cascading delete" of details.
	 *
	 * @throws PersistenceException
	 *             If the delete fails or if the referential integrity would be
	 *             violated by the delete
	 */
	public synchronized void delete() throws PersistenceException
	{
		delete(true);
	}

	/**
	 * Delete this record, optionally deleting any associated detail records.
	 * WARNING: Deleting details is *recursive* - e.g. any details of those
	 * details will also be deleted. Use with caution on extremely large data
	 * sets - might be a better idea to use deleteAll on each appropriate
	 * table, which is non-recursive and uses a single SQL statement to delete
	 * groups of records.
	 *
	 * @param deleteDetails
	 *            Delete associated detail records if true
	 * @throws PersistenceException
	 *             If the delete fails or if the referential integrity would be
	 *             violated by the delete
	 */
	public synchronized void delete(boolean deleteDetails) throws PersistenceException
	{
		checkAllowed("D");

		currentErrors.clear();

		if (getHelper() != null)
		{
			getHelper().beforeDelete(this);
		}

		haveAllKeys(true, true);

		SuperString sqlCommand = new SuperString(64);

		sqlCommand.append("DELETE FROM ");
		sqlCommand.append(myMetaData.getTableName());

		if (customWhereClause != null)
		{
			sqlCommand.append(customWhereClause);
		}
		else
		{
			sqlCommand.append(buildWhereClauseBuffer(false));
		}

		Connection myConnection = null;
		Statement s = null;

		try
		{
			if (deleteDetails)
			{
				deleteDetails();
			} /* if we delete the details */
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			s = myConnection.createStatement();

			if (log.isDebugEnabled())
			{
				log.debug("Executing: " + sqlCommand.toString());
			}

			int updateCount = s.executeUpdate(sqlCommand.toString());

			s.close();
			s = null;

			if ((updateCount == 0) && (getCheckZeroUpdate()))
			{
				log.error("No record(s) deleted for SQL '" + sqlCommand.toString() + "'");
				throw new PersistenceException("No record(s) deleted.");
			}
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			cleanUp(myConnection, s, null, null);
		}

		setStatus(Persistent.DELETED);

		if (getHelper() != null)
		{
			getHelper().afterDelete(this);
		}
	} /* delete() */

	/**
	 * Delete all of the records specified by the current search criteria.
	 * IMPORTANT: deleteAll does *not* check for details, nor does it offer the
	 * option of deleting associated detail records. In order to do this,
	 * retrieve each record with query and use delete(true), not deleteAll().
	 * deleteAll() is more efficient for deleting a large number of records
	 * from a single table.
	 *
	 * @throws PersistenceException
	 *             If the delete fails or if the referential integrity would be
	 *             violated by the delete
	 */
	public synchronized void deleteAll() throws PersistenceException
	{
		currentErrors.clear();

		if (recordSet == null)
		{
			recordSet = new ArrayList();
		}
		else
		{
			recordSet.clear();
		}

		SuperString myStatement = new SuperString(64);

		myStatement.append("DELETE FROM ");

		myStatement.append(myMetaData.getTableName());

		if (customWhereClause != null)
		{
			myStatement.append(customWhereClause);
			customWhereClause = null;
		}
		else
		{
			myStatement.append(buildWhereClauseBuffer(true));
		}

		Connection myConnection = null;
		Statement s = null;

		try
		{
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			s = myConnection.createStatement();
			/* int up[dateCount = */
			s.executeUpdate(myStatement.toString());
			s.close();
			s = null;

			/*
			 * Can't do the below check on MySQL (at least with current
			 * drivers)
			 */

			/* the records are deleted, but the update count still says zero */
			//            if ((updateCount == 0) && (getCheckZeroUpdate())) {
			//                throw new PersistenceException("No records deleted for " +
			// myStatement.toString());
			//            }
		}
		catch (PersistenceException de)
		{
			throw de;
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			cleanUp(myConnection, s, null, null);
		}
	} /* deleteAll() */

	/**
	 * A lot like retrieve, but works with any fields, not just the key field.
	 * Does not throw an exception if the record is not found, just returns
	 * false. Finds only first record matching the criteria. The current fields
	 * in this object are populated with the data in the first (or only) record
	 * found after the call to find() if the find is successful.
	 *
	 * @return boolean If the search resulted in a record
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public synchronized boolean find() throws PersistenceException
	{
		boolean checkSecurity = false;

		if (this instanceof RowSecurablePersistent)
		{
			checkSecurity = true;
		}
		else
		{
			checkAllowed("L");
		}

		boolean needComma = false;

		foundKeys = null;

		ArrayList retrievedFieldList = new ArrayList();

		SuperString myStatement = new SuperString(64);

		myStatement.append("SELECT ");

		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			if (needComma)
			{
				myStatement.append(", ");
			}

			retrievedFieldList.add(oneFieldName);
			myStatement.append(selectFieldString(oneFieldName));

			needComma = true;
		} /* for */
		myStatement.append(" FROM ");
		myStatement.append(myMetaData.getTableName());

		if (customWhereClause != null)
		{
			myStatement.append(customWhereClause);
			customWhereClause = null;
		}
		else
		{
			myStatement.append(buildWhereClauseBuffer(true));
		}

		Connection myConnection = null;
		ResultSet rs = null;
		Statement s = null;

		try
		{
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			s = myConnection.createStatement();

			if (log.isDebugEnabled())
			{
				log.debug("Executing: " + myStatement.toString());
			}

			rs = s.executeQuery(myStatement.toString());

			Object oneFieldValue = null;
			StringBuffer oneKeyString = new StringBuffer("");

			if (foundKeys == null)
			{
				foundKeys = new ArrayList();
			}

			if (rs.next())
			{
				log.debug("Found record");

				int i = 1;

				for (Iterator it = retrievedFieldList.iterator(); it.hasNext();)
				{
					oneFieldName = (String) it.next();

					if (rs.getString(i) == null && myMetaData.allowsNull(oneFieldName))
					{
						i++;
					}
					else
					{
						oneFieldValue = retrieveField(oneFieldName, rs, i);
						setField(oneFieldName, oneFieldValue);
						i++;
					}

					if (myMetaData.isKeyField(oneFieldName))
					{
						if (i > 1)
						{
							oneKeyString.append("/");
						}

						oneKeyString.append(oneFieldValue);
					} /* if field is key */
				} /* for each field */
				foundKeys.add(oneKeyString.toString());
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("No matching record for " + myStatement.toString());
				}

				return false;
			}

			if (checkSecurity)
			{
				checkAllowed("L");
			}

			setStatus(Persistent.CURRENT);

			return true;
		}
		catch (SQLException se)
		{
			addError(se);
			throw new PersistenceException(se.getMessage() + " - " + toString());
		}
		finally
		{
			cleanUp(myConnection, s, rs, null);
		}
	} /* find() */

	/**
	 * Return an "attribute". Attributes are temporary (e.g. not stored in the
	 * DBMS) values associated with this particular persistent instance.
	 *
	 * @param attribName
	 * @return
	 */
	public Object getAttribute(String attribName)
	{
		Object returnValue = null;

		if (attributes != null)
		{
			returnValue = attributes.get(attribName);
		}

		return returnValue;
	} /* getAttribute(String) */

	public Map getAttributes()
	{
		return attributes;
	}

	/**
	 * Query the general-purpose attribute for a field
	 */
	public Object getAttribute(String fieldName, String attribName) throws PersistenceException
	{
		return myMetaData.getAttribute(fieldName, attribName);
	}

	/**
	 * Return the value of a field as a Date object
	 *
	 * @param fieldName
	 *            The field to be retrieved
	 * @param formatPattern
	 *            A formatting pattern according to java.text.DecimalFormat.
	 *            Leave null for the default format for this locale.
	 *
	 * <pre>
	 *  Symbol Meaning 0 a digit # a digit, zero shows as absent . placeholder for decimal separator , placeholder for grouping separator. E separates mantissa and exponent for exponential formats. ; separates formats. - default negative prefix. % multiply by 100 and show as percentage ? multiply by 1000 and show as per mille ? currency sign; replaced by currency symbol; if doubled, replaced by international currency symbol. If present in a pattern, the monetary decimal separator is used instead of the decimal separator. X any other characters can be used in the prefix or suffix ' used to quote special characters in a prefix or suffix.
	 * </pre>
	 *
	 *
	 * @return The Date object equivilant to this field's value
	 * @throws PersistenceException
	 *             If the field does not exist or it's value is not a date or
	 *             cannot be converted to a date
	 * @see java.text.DecimalFormat
	 */
	public String getFieldDecimalFormatted(String fieldName, String formatPattern) throws PersistenceException
	{
		if (! myMetaData.hasField(fieldName))
		{
			throw new PersistenceException("(" + getName() + ") No such field as " + fieldName);
		}

		Object o = getFieldData(fieldName);

		if (o == null)
		{
			return null;
		}

		String strVal = o.toString();

		if (strVal.equals(""))
		{
			return null;
		}

		Double myDouble = new Double(strVal);
		DecimalFormat df = null;

		if (formatPattern == null)
		{
			df = new DecimalFormat();
		}
		else
		{
			df = new DecimalFormat(formatPattern);
		}

		String returnValue = df.format(myDouble);

		return returnValue;
	} /* getFieldDecimalFormatted(String, String) */

	/**
	 * Get the integer value of a field in this object
	 *
	 * @param fieldName
	 *            Name of a field in this object
	 * @return int The value of the field as a float
	 * @throws PersistenceException
	 *             if there is no such field or it's value cannot be converted
	 *             to an integer.
	 */
	public int getFieldInt(String fieldName) throws PersistenceException
	{
		assertField(fieldName);

		Object o = getFieldData(fieldName);

		if (o == null)
		{
			throw new PersistenceException("Field '" + fieldName + "' was null, cannot return as int");
		}

		String strVal = "";

		try
		{
			strVal = o.toString();

			if (strVal == null)
			{
				throw new PersistenceException("(" + getName() + ") Unable to get int value from field " + fieldName
								+ ", value was '" + strVal + "'");
			}

			return Integer.parseInt(strVal);
		}
		catch (NumberFormatException ex)
		{
			throw new PersistenceException("(" + getName() + ") Unable to parse an integer value from field "
							+ fieldName + " which contained '" + strVal + "'", ex);
		}
	} /* getFieldInt(String) */

	/**
	 * Get the name of this object
	 *
	 * @return String The Persistent name, which may or may not bear any
	 *         relationship to the table name
	 */
	public String getName()
	{
		return myMetaData.getSchemaName() + "." + myMetaData.getName();
	} /* getName() */

	/**
	 * Find the maximum of the values in the specified field of records
	 * selected by the Persistent selection criteria.
	 *
	 * @param String
	 *            Persistent fieldName to average
	 * @return double Maximum of the records matching the criteria
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public double max(String fieldName) throws PersistenceException
	{
		return sqlAggrFunction("MAX", fieldName);
	} /* max() */

	/**
	 * Find the minimum of the values in the specified field of records
	 * selected by the Persistent selection criteria.
	 *
	 * @param String
	 *            Persistent fieldName to average
	 * @return double Minimum of the records matching the criteria
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public double min(String fieldName) throws PersistenceException
	{
		return sqlAggrFunction("MIN", fieldName);
	} /* min() */

	/**
	 * Get a single record from the database into this Persistent's fields
	 * Assumes that the key fields are set to the key of the object to be
	 * retrieved
	 *
	 * @throws PersistenceException
	 *             If the record could not be retrieved, or if the key fields
	 *             were not set. If you are not sure if the record exists, use
	 *             find() instead.
	 * @see #find
	 */
	public synchronized void retrieve() throws PersistenceException
	{
		/* If this instance is a RowSecurablePersistent, then we must */
		/* check for authorization *after* the row is retrieved */
		/* otherwise it is not fully populated, and we may not have */
		boolean doRowLevelCheck = false;

		if (! (this instanceof RowSecurablePersistent))
		{
			checkAllowed("L");
		}
		else
		{
			doRowLevelCheck = true;
		}

		boolean needComma = false;

		haveAllKeys(true, true);

		StringBuffer myStatement = new StringBuffer("SELECT ");
		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			if (needComma)
			{
				myStatement.append(", ");
			}

			myStatement.append(selectFieldString(oneFieldName));

			needComma = true;
		} /* for */
		myStatement.append(" FROM ");
		myStatement.append(myMetaData.getTableName());

		if (customWhereClause != null)
		{
			myStatement.append(customWhereClause);
			customWhereClause = null;
		}
		else
		{
			myStatement.append(buildWhereClauseBuffer(false));
		}

		Connection myConnection = null;
		Statement s = null;
		ResultSet rs = null;

		try
		{
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			s = myConnection.createStatement();

			if (log.isDebugEnabled())
			{
				log.debug("Executing: " + myStatement.toString());
			}

			rs = s.executeQuery(myStatement.toString());

			StringBuffer oneKeyString = new StringBuffer("");
			Object oneFieldValue = null;

			if (foundKeys == null)
			{
				foundKeys = new ArrayList();
			}

			if (rs.next())
			{
				int i = 1;

				for (Iterator it = myMetaData.getFieldNames().iterator(); it.hasNext();)
				{
					oneFieldName = (String) it.next();
					oneFieldValue = retrieveField(oneFieldName, rs, i);

					i++;
					setFieldData(oneFieldName, oneFieldValue);

					if (myMetaData.isKeyField(oneFieldName))
					{
						if (i > 1)
						{
							oneKeyString.append("/");
						}

						oneKeyString.append(oneFieldValue);
					}
				} /* for each field */
				foundKeys.add(oneKeyString.toString());
			}
			else
			{
				throw new PersistenceException("(" + getName() + ") No such record" + toString());
			}

			if (doRowLevelCheck)
			{
				checkAllowed("L");
			}

			setStatus(Persistent.CURRENT);
		}
		catch (PersistenceException de)
		{
			throw de;
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			cleanUp(myConnection, s, rs, null);
		}
	} /* retrieve() */

	/**
	 * Find a set of records of all of the objects that match the current
	 * search critieria in the fields and retrieve the list of all records that
	 * match this criteria NOTE: Criteria in 'text' type colums is ignored (SQL
	 * Server limitation)
	 *
	 * @return A List of new database objects containing the results of the
	 *         search
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public synchronized List<Persistent> query() throws PersistenceException
	{
		boolean checkRowSecurity = false;

		if (this instanceof RowSecurablePersistent)
		{
			checkRowSecurity = true;
		}
		else
		{
			checkAllowed("L");
		}

		currentErrors.clear();

		boolean needComma = false;
		ArrayList retrievedFieldList = new ArrayList();

		if (getHelper() != null)
		{
			getHelper().beforeQuery(this);
		}

		if (recordSet == null)
		{
			recordSet = new ArrayList();
		}
		else
		{
			recordSet.clear();
		}

		//myUpdates = null;
		SuperString myStatement = new SuperString(64);

		myStatement.append("SELECT ");

		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			if (needComma)
			{
				myStatement.append(", ");
			}

			retrievedFieldList.add(oneFieldName);
			myStatement.append(selectFieldString(oneFieldName));

			needComma = true;
		} /* for each field */
		myStatement.append(" FROM ");
		myStatement.append(myMetaData.getTableName());

		Connection myConnection = null;
		Statement s = null;
		ResultSet rs = null;

		try
		{
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			if (myMetaData.getDatabaseType().getSubSetPosition() == DatabaseType.SUBSET_AFTER_TABLE
							&& (offsetRecord > 0 || maxRecords > 0))
			{
				// Insert limitation stub after table nomination
				String limitStub = makeSubSetStub();

				myStatement.append(" ");
				myStatement.append(limitStub);
				myStatement.append(" ");
			}

			SuperString whereClause;

			if (customWhereClause != null)
			{
				whereClause = new SuperString(customWhereClause);
				myStatement.append(customWhereClause);
				customWhereClause = null;
			}
			else
			{
				whereClause = buildWhereClauseBuffer(true);
				myStatement.append(whereClause);
			}

			if (myMetaData.getDatabaseType().getSubSetPosition() == DatabaseType.SUBSET_AFTER_WHERE
							&& (offsetRecord > 0 || maxRecords > 0))
			{
				// Insert limitation stub after table nomination
				String limitStub = makeSubSetStub();

				if (whereClause.length() > 0)
				{
					myStatement.append(" AND");
				}

				myStatement.append(" ");
				myStatement.append(limitStub);
				myStatement.append(" ");
			}

			/* Add the ORDER BY clause if any sortKeys are specified */
			if (sortKeys != null && sortKeys.size() > 0)
			{
				myStatement.append(" ORDER BY ");

				boolean needComma2 = false;

				for (Iterator i = sortKeys.iterator(); i.hasNext();)
				{
					if (needComma2)
					{
						myStatement.append(", ");
					}

					//                     myStatement.append(
					//                         myMetaData.getDBFieldName((String) i.next()));
					String fieldName = (String) i.next();
					int descIndex = fieldName.indexOf("DESC");
					int ascIndex = fieldName.indexOf("ASC");

					if (descIndex > 0)
					{
						fieldName = fieldName.substring(0, descIndex).trim();
						myStatement.append(myMetaData.getDBFieldName(fieldName));
						myStatement.append(" DESC");
					}
					else if (ascIndex > 0)
					{
						fieldName = fieldName.substring(0, ascIndex).trim();
						myStatement.append(myMetaData.getDBFieldName(fieldName));
						myStatement.append(" ASC");
					}
					else
					{
						myStatement.append(myMetaData.getDBFieldName(fieldName));
					}

					needComma2 = true;
				}

				if (myMetaData.getDatabaseType().getSubSetPosition() == DatabaseType.SUBSET_AFTER_ORDER_BY
								&& (offsetRecord > 0 || maxRecords > 0))
				{
					myStatement.append(" ");
					myStatement.append(makeSubSetStub());
				}
			}

			s = myConnection.createStatement();

			if (log.isDebugEnabled())
			{
				log.debug("Executing: " + myStatement.toString());
			}

			rs = s.executeQuery(myStatement.toString());

			int recordCount = 0;
			int retrieveCount = 0;

			while (rs.next())
			{
				recordCount++;
				retrieveCount++;

				//If there's limitation syntax on, then the first record will
				// be the
				//maximum record.
				if ((retrieveCount < offsetRecord)
								&& (offsetRecord > 0)
								&& (myMetaData.getDatabaseType().getSubSetPosition() == DatabaseType.SUBSET_NOT_SUPPORTED))
				{
					continue;
				}
				else if ((retrieveCount == offsetRecord)
								&& (offsetRecord > 0)
								&& (myMetaData.getDatabaseType().getSubSetPosition() == DatabaseType.SUBSET_NOT_SUPPORTED))
				{
					recordCount = 0;

					//Reset count for counting for max records.
					continue;
				}

				if ((recordCount > maxRecords) && (maxRecords > 0))
				{
					setAttribute("More Records", "Y");

					break;
				}

				//Only allocate if we're gonna load this record
				Persistent myObj = getFactory().create(getName());
				int i = 1;
				Object oneFieldValue = null;

				for (Iterator it = retrievedFieldList.iterator(); it.hasNext();)
				{
					oneFieldName = (String) it.next();

					oneFieldValue = retrieveField(oneFieldName, rs, i);
					i++;
					myObj.setField(oneFieldName, oneFieldValue);
				} /* for each retrieved field name */
				// TODO - how to tell the new object it's now current
				//myObj.setStatus(Persistent.CURRENT);
				if (checkRowSecurity)
				{
					if (myObj.allowed(QUERY))
					{
						recordSet.add(myObj);
					}
				}
				else
				{
					recordSet.add(myObj);
				}
			}
		}
		catch (PersistenceException de)
		{
			throw de;
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			cleanUp(myConnection, s, rs, null);
		}

		if (getHelper() != null)
		{
			getHelper().afterQuery(this);
		}

		return recordSet;
	} /* query() */

	/**
	 * Search and retrieve in a particular order
	 *
	 * @param sortKeyString
	 *            A comma-delimited list of key fields to sort the returned set
	 *            by
	 * @return A List of new database objects retrieved by the search
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public synchronized List<Persistent> query(String sortKeyString) throws PersistenceException
	{
		if (sortKeys == null)
		{
			sortKeys = new LinkedHashSet();
		}
		else
		{
			sortKeys.clear();
		}

		StringTokenizer stk = new StringTokenizer(sortKeyString, ",");

		while (stk.hasMoreTokens())
		{
			sortKeys.add(stk.nextToken());
		}

		return query();
	} /* query(String) */

	/**
	 * Set an attribute. Attributes are temporary (e.g. not stored in the DBMS)
	 * values associated with this particular Persistent instance.
	 *
	 * @param attribName
	 *            The name of the attribute being defined
	 * @param attribValue
	 *            The object to store under this attribute name
	 */
	public synchronized void setAttribute(String attribName, Object attribValue)
	{
		if (attributes == null)
		{
			attributes = new HashMap();
		}

		attributes.put(attribName, attribValue);

		if (attribName.equalsIgnoreCase("checkzeroupdate"))
		{
			setCheckZeroUpdate(new Boolean(attribValue.toString()).booleanValue());
		}
	} /* setAttribute(String, Object) */

	/**
	 * Specify a custom "where" clause for the SQL used to retrieve records for
	 * this object. The where clause 'reset' after each call to query() or
	 * other retrieval methods, so it must be set just before the call to
	 * retrieve the records is made. If no custom where clause is specified by
	 * this method, the where clause is built from the field values in the
	 * object.
	 *
	 * @param newCustomWhere
	 *            java.lang.String
	 */
	public synchronized void setCustomWhereClause(String newCustomWhere)
	{
		customWhereClause = " WHERE " + newCustomWhere;
	} /* setCustomWhereClause(String) */

	/**
	 * Specify a maximum number of records to be retrieved in any subsequent
	 * query() call. Records will be retrieved (in the specified sort order)
	 * until the specified maximum is reached, then the remainder of the result
	 * set is discarded. Specifying zero indicates that all records are to be
	 * retrieved.
	 *
	 * @param newMax
	 *            The maximum number of records to retrieve.
	 * @throws PersistenceException
	 *             If the max number is less than 0
	 */
	public synchronized void setMaxRecords(int newMax)
	{
		if (maxRecords < 0)
		{
			maxRecords = 0;
		}
		else
		{
			maxRecords = newMax;
		}
	} /* setMaxRecords(int) */

	/**
	 * Specifies the number of records that should be skipped over before any
	 * data from the <code>ResultSet</code> is retrieved in any subsequent
	 * query() call. Records will be skipped over (in the specified sort order)
	 * until the record counts is equal to or greater than the offset record.
	 * Specifying zero indicates that no records should be skipped over and the
	 * <code>ResultSet</code> immediately from the start.
	 *
	 * @param newMax
	 *            The maximum number of records to retrieve.
	 * @throws PersistenceException
	 *             If the max number is less than 0
	 *
	 * @author Peter Pilgrim
	 *         <peterp  at  xenonsoft dot demon dot co dot  uk>
	 *         @date Tue Feb 05 23:06:38 GMT 2002
	 */
	public synchronized void setOffsetRecord(int newOffset)
	{
		if (newOffset < 0)
		{
			offsetRecord = 0;
		}

		offsetRecord = newOffset;
	} /* setOffsetRecord(int) */

	/**
	 * Find the sum of the values in the specified field of records selected by
	 * the Persistent selection criteria.
	 *
	 * @param String
	 *            Persistent fieldName to average
	 * @return double Sum of the records matching the criteria
	 * @throws PersistenceException
	 *             If the search could not be completed
	 */
	public double sum(String fieldName) throws PersistenceException
	{
		return sqlAggrFunction("SUM", fieldName);
	} /* sum() */

	/**
	 * Update the database with the new info. Update affects only one record,
	 * and uses the current field values to write to the DBMS. Referential
	 * integrity is verified before the update is performed.
	 *
	 * @throws PersistenceException
	 *             if the update to the database fails due to a database error
	 */
	public synchronized void update() throws PersistenceException
	{
		checkAllowed("U");

		if (getHelper() != null)
		{
			getHelper().beforeUpdate(this);
		}

		currentErrors.clear();
		currentErrors.putAll(validateAll());

		if (currentErrors.size() > 0)
		{
			throw new PersistenceException("Field validation errors in persistent '" + myMetaData.getName() + "': ",
							getErrors());
		}

		boolean needComma = false;

		/* build an update statement - again we must call haveKeyFields */
		/* to be sure we have all of the keys */
		haveAllKeys(true, true);

		StringBuffer sqlCommand = new StringBuffer("UPDATE ");

		sqlCommand.append(myMetaData.getTableName());
		sqlCommand.append(" SET ");

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			String oneFieldName = (String) i.next();

			/* We skip any key fields in the update part (not allowed to */
			/* upate them). Also skip any virtual fields */
			if ((! myMetaData.isKeyField(oneFieldName)) && (! myMetaData.isAutoIncremented(oneFieldName)))
			{
				checkField(oneFieldName, getField(oneFieldName));

				if (needComma)
				{
					sqlCommand.append(", ");
				}

				if (! myMetaData.isKeyField(oneFieldName))
				{
					sqlCommand.append(myMetaData.getDBFieldName(oneFieldName));
					sqlCommand.append(" = ");
					//updated by Adam to do proper prep for update SQL
					//sqlCommand.append(quoteIfNeeded(oneFieldName, null));
					sqlCommand.append("?");
					sqlCommand.append(" ");
					needComma = true;
				}
			} /* if it's not a key field */
		}

		sqlCommand.append(buildWhereClauseBuffer(false));

		if (sqlCommand == null)
		{
			throw new PersistenceException("Internal error: (" + getName() + ") No SQL command built for this update");
		}

		PreparedStatement ps = null;
		Connection myConnection = null;

		//Statement s = null;
		try
		{
			if (currentTransaction != null)
			{
				myConnection = currentTransaction.getConnection();
			}
			else
			{
				myConnection = myDataSource.getConnection();
			}

			ps = myConnection.prepareStatement(sqlCommand.toString());

			//			populate the fields for the update
			int count = 1;
			String oneFieldName = null;

			for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
			{
				oneFieldName = (String) i.next();

				if ((! myMetaData.isKeyField(oneFieldName)) && (! myMetaData.isAutoIncremented(oneFieldName)))
				{
					setPreparedStatementObject(ps, count, oneFieldName);

					count++;
				}

				//}
			} /* for each field */
			//s = myConnection.createStatement();
			if (log.isDebugEnabled())
			{
				log.debug("Executing: " + sqlCommand.toString());
			}

			//int updateCount = s.executeUpdate(sqlCommand.toString());
			int updateCount = ps.executeUpdate();

			if ((updateCount == 0) && (getCheckZeroUpdate()))
			{
				log.error("No record updated for SQL '" + sqlCommand.toString() + "'");
				throw new PersistenceException("(" + getName() + ") No records updated for" + toString());
			}
		}
		catch (PersistenceException de)
		{
			throw de;
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			cleanUp(myConnection, null, null, ps);
		}

		setStatus(Persistent.CURRENT);

		if (getHelper() != null)
		{
			getHelper().afterUpdate(this);
		}
	} /* update() */

	public void setTransaction(Transaction newTrx)
	{
		currentTransaction = newTrx;
	}

	public boolean allowed(int operation) throws PersistenceException
	{
		switch (operation)
		{
			case Persistent.ADD:
				return isAllowed("A");

			case Persistent.UPDATE:
				return isAllowed("U");

			case Persistent.DELETE:
				return isAllowed("D");

			case Persistent.QUERY:
				return isAllowed("L");

			default:
				throw new PersistenceException("Operation " + operation + " is not understood");
		}
	}

	public Map validateAll()
	{
		HashMap returnMap = new HashMap();
		String oneFieldName = null;
		String oneError = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			try
			{
				oneError = validateField(oneFieldName, getField(oneFieldName));
			}
			catch (PersistenceException pe)
			{
				log.error("Error while validating field '" + oneFieldName + "'", pe);
				returnMap.put(oneFieldName, pe.getMessage());
			}

			if (oneError != null)
			{
				returnMap.put(oneFieldName, oneError);
			}
		}

		return returnMap;
	}

	public String validateField(String fieldName, Object oneFieldValue)
	{
		String returnValue = null;

		try
		{
			if ((oneFieldValue == null) || ("".equals(oneFieldValue)))
			{
				oneFieldValue = myMetaData.getDefaultValue(fieldName);

				if (((oneFieldValue == null) || ("".equals(oneFieldValue))) && (! myMetaData.allowsNull(fieldName))
								&& (! myMetaData.isAutoIncremented(fieldName)))
				{
					return "Field '" + fieldName + "' may not be null";
				}
				else
				{
					return returnValue;
				}
			}
			else
			{
				validateType(fieldName);
			}

			if (myMetaData.isMultiValued(fieldName))
			{
				if (! getValidValues(fieldName).containsKey(oneFieldValue.toString()))
				{
					return "Value '" + oneFieldValue.toString() + "' is not a valid value for field '" + fieldName
									+ "'";
				}
			}
		}
		catch (PersistenceException pe)
		{
			returnValue = pe.getMessage();
		}

		return returnValue;
	}

	public Map getErrors()
	{
		return currentErrors;
	}

	/*
	 * @see de.iritgo.aktera.persist.Persistent#lock()
	 */
	public synchronized void lock() throws PersistenceException
	{
		String dbKeyName = getName();

		if (log.isDebugEnabled())
		{
			log.debug("Acquiring mutex lock for " + dbKeyName);
		}

		if (dbLocks == null)
		{
			// Lazy initialization of the mutex map
			if (log.isDebugEnabled())
			{
				log.debug("Creating mutex lock map...");
			}

			dbLocks = new HashMap(16);
		}

		// The map of mutexes is keyed by the name of this persistent.
		// Try to find it first, If not found, create a new mutex and
		// add it to the map for this and future use.
		Mutex dbMutex = null;

		if ((dbMutex = (Mutex) dbLocks.get(dbKeyName)) == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Creating new mutex for " + dbKeyName);
			}

			dbMutex = new Mutex();
			dbLocks.put(dbKeyName, dbMutex);
		}

		//Got handle to mutex, now acquire it
		try
		{
			dbMutex.acquire();
		}
		catch (InterruptedException ie)
		{
			throw new PersistenceException(ie);
		}

		if (log.isDebugEnabled())
		{
			log.debug("Acquired mutex lock for " + dbKeyName);
		}
	}

	/*
	 * @see de.iritgo.aktera.persist.Persistent#unlock()
	 */
	public synchronized void unlock() throws PersistenceException
	{
		if (dbLocks == null)
		{
			throw new PersistenceException("$keelNoDBLockMap");
		}

		String dbKeyName = getName();
		Mutex dbMutex = null;

		if ((dbMutex = (Mutex) dbLocks.get(dbKeyName)) == null)
		{
			throw new PersistenceException("DB lock mutex not found for " + dbKeyName);
		}

		dbMutex.release();

		if (log.isDebugEnabled())
		{
			log.debug("Released mutex lock for " + dbKeyName);
		}
	}

	/**
	 * This method has been overriden to print the table name and all
	 * name/value pairs as a single string. This is useful for debugging.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer("");

		sb.append("factory:");

		if (myFactory == null)
		{
			sb.append("null");
		}
		else
		{
			sb.append(myFactory.getName());
		}

		sb.append(",name:");
		sb.append(getName());
		sb.append(",table_name:");
		sb.append(this.myMetaData.getTableName());
		sb.append(",field_values:");

		String oneFieldName = null;
		Object oneFieldValue = null;

		try
		{
			for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
			{
				oneFieldName = (String) i.next();
				oneFieldValue = getField(oneFieldName);
				sb.append(oneFieldName);
				sb.append("=");

				if (oneFieldValue != null)
				{
					sb.append(oneFieldValue.toString());
				}

				sb.append("|");
			}
		}
		catch (PersistenceException pe)
		{
			sb.append(pe.toString());
		}

		return sb.toString();
	}

	public List getDetails(String detailName) throws PersistenceException
	{
		PersistentMetaData pmd = getMetaData();
		Relation r = pmd.getRelation(detailName);

		if (r == null)
		{
			throw new PersistenceException("No such relation as '" + detailName + "'");
		}

		if (r.getType() != Relation.DETAIL)
		{
			throw new PersistenceException("Relation '" + detailName + "' is not a detail relation");
		}

		String toPersistent = r.getToPersistent();
		Persistent detList = myFactory.create(toPersistent);
		String oneFieldName = null;
		String oneFromFieldName = null;
		Iterator fromFields = r.getFromFields().iterator();

		for (Iterator i = r.getToFields().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();
			oneFromFieldName = (String) fromFields.next();
			detList.setField(oneFieldName, getField(oneFromFieldName));
		}

		return detList.query();
	}

	/**
	 * Set the persistent fields from the fields of a bean. The bean is
	 * retained internally, and can be accessed via "getBean()", but it is
	 * important to note that getBean may return a different instance of the
	 * bean than setBean stored.
	 */
	public void setBean(Object newBean) throws PersistenceException
	{
		assert newBean != null;

		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(newBean);

		if (props.length == 0)
		{
			throw new PersistenceException("Class '" + newBean.getClass().getName() + "' declares no fields");
		}

		PropertyDescriptor oneProp = null;
		String fieldName = null;

		for (int i = 0; i < props.length; i++)
		{
			oneProp = props[i];
			fieldName = getFieldNameIgnoreCase(oneProp.getName());

			if (myMetaData.getFieldNames().contains(fieldName))
			{
				Object oneValue = null;

				try
				{
					oneValue = PropertyUtils.getProperty(newBean, oneProp.getName());
				}
				catch (Exception ie)
				{
					throw new PersistenceException("Unable to retrieve property '" + oneProp.getName()
									+ "' from object of class '" + newBean.getClass().getName() + "'", ie);
				}

				setField(fieldName, oneValue);
			}
			else
			{
				if (log.isDebugEnabled())
				{
					/*
					 * Every class has a "class" attribute - don't clutter up
					 * the log
					 */
					if (! fieldName.equals("class"))
					{
						log.debug("No field '" + fieldName + "' in persistent definition '" + myMetaData.getName()
										+ "', unable to persist");
					}
				}
			}
		}
	}

	public Helper getHelper() throws PersistenceException
	{
		Helper h = super.getHelper();

		if (h == null)
		{
			return null;
		}

		h.setPersistent(this);

		return h;
	}
} /* DefaultPersistent */
