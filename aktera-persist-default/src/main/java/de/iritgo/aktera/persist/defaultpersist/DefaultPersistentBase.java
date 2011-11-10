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


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.InstanceSecurable;
import de.iritgo.aktera.authorization.Operation;
import de.iritgo.aktera.authorization.defaultauth.DefaultOperation;
import de.iritgo.aktera.persist.DatabaseType;
import de.iritgo.aktera.persist.Helper;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.Relation;
import de.iritgo.aktera.persist.Transaction;
import de.iritgo.aktera.persist.base.JDBCDatabaseType;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.ids.IdException;
import org.apache.avalon.excalibur.datasource.ids.IdGenerator;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @version $Revision: 1.2 $ $Date: 2005/03/06 16:47:22 $
 * @author Michael Nash
 */
public abstract class DefaultPersistentBase implements InstanceSecurable
{
	/**
	 * The class that takes the actual Perl5 Compiled Pattern and matches it
	 * against a string.
	 */
	transient private static PatternMatcher patternMatcher = new Perl5Matcher();

	/**
	 * This map holds a per-persistent mutex used for granting exclusive
	 * read-write access to a DB-table Note: The lock()/unlock() is valid
	 * across a single JVM only.
	 */
	protected static Map dbLocks = null;

	/**
	 * JavaBean object underlying this persistent. Defined by the "class"
	 * attribute in configuration
	 */
	protected Object myBean = null;

	protected AuthorizationManager myAuthManager = null;

	protected AuthorizationManager myBypassAuthManager = null;

	protected DefaultPersistentFactory myFactory = null;

	/**
	 * Our private reference to the data source service handed to us by our
	 * container
	 */
	protected DataSourceComponent myDataSource = null;

	/**
	 * Our private reference to the current transaction we are participating
	 * in, or null if no such transaction
	 */
	protected Transaction currentTransaction = null;

	/**
	 * Persistents themselves do not contain the "meta data" or the definition
	 * of what fields they contain and other information. Instead, a
	 * PersistentMetaData object (one per TYPE of Persistent) contains this
	 * info and is looked up as needed.
	 */
	protected PersistentMetaData myMetaData = null;

	/**
	 * A Persistent instance often refers to a single row, and this hash map
	 * contains the row data for this instance
	 */
	protected Map fieldData = null;

	/** The ArrayList of Persistents retrieved by the last query method */
	protected List recordSet = null;

	protected int currentStatus = Persistent.NEW;

	/**
	 * The number of records we must skip over before we start reading the
	 * <code>ResultSet</code> proper in a query. A value of 0 means no limit
	 */
	protected int offsetRecord = 0;

	/**
	 * The max number of records we retrieve in a query. 0 means no limit
	 */
	protected int maxRecords = 0;

	/**
	 * The list of fields by which this object should be sorted when records
	 * are retrieved
	 */
	protected Set sortKeys = null;

	protected List foundKeys = null;

	/**
	 * If we are using a custom where clause for this query, it's stored here.
	 * If null, then build the where clause
	 */
	protected String customWhereClause = null;

	/** Attributes of this Persistent */
	protected Map attributes = null;

	/* Our logger */
	transient protected Logger log = null;

	protected boolean checkZeroUpdate = true;

	protected Helper myHelper = null;

	/**
	 * This map holds a set of errors from the last operation, such as an add
	 * or update. It is empty if the last operation was successful. The index
	 * of this hash is a string, where the string is a field name if the error
	 * was related to a specific field, or just a sequential number if the
	 * error was in fact not related to one particular field
	 */
	protected Map currentErrors = new HashMap();

	protected final void addError(Throwable t)
	{
		int nofErrors = currentErrors.size();

		currentErrors.put("" + (nofErrors + 1), t);
	}

	protected final void addError(String fieldName, Throwable t)
	{
		currentErrors.put(fieldName, t);
	}

	protected final void addError(String fieldName, String errorMsg)
	{
		currentErrors.put(fieldName, new PersistenceException(errorMsg));
	}

	/**
	 * Build and return a string consisting of an SQL 'where' clause using the
	 * current field values as criteria for the search. See
	 * setCustomWhereClause for information on specifying a more complex where
	 * clause.
	 *
	 * @param allFields
	 *            True if all fields are to be used, false for only key fields
	 * @throws PersistenceException
	 * @deprecated Use buildWhereClauseBuffer instead
	 * @return The where clause
	 */
	protected String buildWhereClause(boolean useAllFields) throws PersistenceException
	{
		return buildWhereClauseBuffer(useAllFields).toString();
	} /* buildWhereClause(boolean) */

	/**
	 * Build and return a SuperString consisting of an SQL 'where' clause using
	 * the current field values as criteria for the search. See
	 * setCustomWhereClause for information on specifying a more complex where
	 * clause.
	 *
	 * @param allFields
	 *            True if all fields are to be used, false for only key fields
	 * @throws PersistenceException
	 * @return A SuperString containing the "where" clause for the SQL
	 *         statement
	 */
	protected SuperString buildWhereClauseBuffer(boolean useAllFields) throws PersistenceException
	{
		Iterator fieldsToUse = null;
		SuperString myStatement = new SuperString(32);

		if (useAllFields)
		{
			fieldsToUse = myMetaData.getFieldNames().iterator();
		}
		else
		{
			fieldsToUse = myMetaData.getKeyFieldNames().iterator();
		}

		/* Now go thru each field - if it is non-empty, add it's criteria */
		/* to the where clause. If it is empty, just skip to the next one */
		boolean addWhere = true;
		boolean addAnd = false;
		String oneFieldName = null;
		String oneFieldValue = null;

		boolean skipText = myMetaData.getDatabaseType().allowTextQueries();

		boolean skipField = false;

		while (fieldsToUse.hasNext())
		{
			oneFieldName = (String) fieldsToUse.next();
			skipField = false;

			oneFieldValue = SuperString.notNull(getFieldString(oneFieldName));

			String rangeString = myMetaData.getDatabaseType().denotesRange(oneFieldValue);

			if (! oneFieldValue.equals(""))
			{
				//Changed by Adam. Do formatting here for date/time types, or
				// quote
				//if needed.
				//oneFieldValue = quoteIfNeeded(oneFieldName, rangeString);
				oneFieldValue = getValueForUpdate(oneFieldName, rangeString);
			}

			if (oneFieldValue == null)
			{
				skipField = true;
			}

			if (oneFieldValue.trim().equals("\'\'"))
			{
				skipField = true;
			}

			if (myMetaData.getType(oneFieldName).equalsIgnoreCase("text"))
			{
				if (skipText)
				{
					skipField = true;

					if (log.isDebugEnabled())
					{
						log.debug("Skipping criteria in text field '" + oneFieldName + "'");
					}
				}
				else
				{
					if (oneFieldValue.indexOf("\n") > 0)
					{
						oneFieldValue = SuperString.replace(oneFieldValue, "\n", "");
					}

					if (oneFieldValue.indexOf("\r") > 0)
					{
						oneFieldValue = SuperString.replace(oneFieldValue, "\r", "");
					}

					if (oneFieldValue.equals("\'\'"))
					{
						skipField = true;
					}
				}
			} /* if text field */
			if (oneFieldValue.trim().equals(""))
			{
				skipField = true;
			}

			if (! skipField)
			{
				if (addWhere)
				{
					myStatement.append(" WHERE ");
					addWhere = false;
				}

				if (addAnd)
				{
					myStatement.append(" AND ");
				}

				if (containsWildCards(oneFieldValue))
				{
					myStatement.append(myMetaData.getDBFieldName(oneFieldName));
					myStatement.append(" LIKE ");
					myStatement.append(oneFieldValue);
				}
				else if (rangeString != null)
				{
					myStatement.append(myMetaData.getDBFieldName(oneFieldName));
					myStatement.append(" " + rangeString + " ");
					myStatement.append(oneFieldValue);
				}
				else if ((oneFieldValue.trim().equalsIgnoreCase("is null"))
								|| (oneFieldValue.trim().equalsIgnoreCase("is not null")))
				{
					myStatement.append(myMetaData.getDBFieldName(oneFieldName));
					myStatement.append(" ");
					myStatement.append(oneFieldValue.trim());
				}
				else
				{
					myStatement.append(myMetaData.getDBFieldName(oneFieldName));
					myStatement.append(" = ");
					myStatement.append(oneFieldValue);
				}

				addAnd = true;
			}

			/* if field is not skipped for some reason */
		}

		/* for each field */
		if (log.isDebugEnabled())
		{
			log.debug("Built where clause '" + myStatement.toString() + "'");
		}

		return myStatement;
	}

	/**
	 * Check that a given value is valid for a given field. This method is
	 * overriden by specific Persistents to do their own field-level
	 * validations - they should also call super in order to do the standard
	 * stuff. Every field is automatically checked by this method before the
	 * database is updated.
	 *
	 * @param fieldName
	 *            Name of the field to verify
	 * @param fieldValue
	 *            Value of the field to be evaluated
	 * @throws PersistenceException
	 *             If the value is not valid
	 */
	protected synchronized void checkField(String fieldName, Object fieldValue) throws PersistenceException
	{
		assertField(fieldName);

		// Allow autoinc fields through since they're added last minute and may
		// not
		// currently have a valid value
		if (myMetaData.isAutoIncremented(fieldName))
		{
			return;
		}

		if (! myMetaData.allowsNull(fieldName))
		{
			if (fieldValue == null)
			{
				throw new PersistenceException("$keelNullNotAllowed|" + myMetaData.getTableName() + "."
								+ myMetaData.getDescription(fieldName));
			}
			else if (fieldValue.toString().equals(""))
			{
				throw new PersistenceException("$keelNullNotAllowed|" + myMetaData.getTableName() + "."
								+ myMetaData.getDescription(fieldName));
			}
		}
		else
		{ /* It's null and it's allowed to be, so do no further checks */

			if (fieldValue == null)
			{
				return;
			}
		}

		Pattern mask = myMetaData.getPattern(fieldName);

		if (mask != null)
		{
			if (! patternMatcher.matches(fieldValue.toString(), mask))
			{
				throw new PersistenceException("$keelFieldNotMatch|" + fieldName + "|" + mask.getPattern());
			}
		}

		/*
		 * For multi-valued fields, the value specified must be one of the
		 * valid values Note: I have changed this behavior slightly from
		 * previous versions. If a field has been marked as "allowsNull", it
		 * should pass the check below, even if the field has also been marked
		 * as "multiValued". In other words, a mutlivalued, allowsnull field
		 * should pass the checkField method if it has a null value. - Adam
		 */
		if (myMetaData.isMultiValued(fieldName) && (! fieldValue.equals("")))
		{
			//Added this line to fix object comparison problems - ACR
			String stringFieldValue = fieldValue.toString();
			Map values = getValidValues(fieldName);

			if (values == null)
			{
				throw new PersistenceException("(" + getName() + ") '" + myMetaData.getDescription(fieldName)
								+ "' is set as multi-valued, but there are no defined " + "valid values" + toString());
			}

			Object oneValue = null;

			for (Iterator e = values.keySet().iterator(); e.hasNext();)
			{
				oneValue = (String) e.next();

				//Changed this line to fix object comparison issues. - ACR
				if (stringFieldValue.equals(oneValue))
				{
					return;
				}
			} /* for each valid value */
			throw new PersistenceException("(" + getName() + ") '" + fieldValue + "' is not a valid value for field '"
							+ fieldName + "(" + myMetaData.getDescription(fieldName) + ")' " + toString());
		} /* if field is multi-valued */
		if (isDateType(myMetaData.getType(fieldName)))
		{
			String stringDate = fieldValue.toString();

			String format = "yyyy-MM-dd hh:mm:ss";
			String type = myMetaData.getType(fieldName);

			if (type.equalsIgnoreCase("date"))
			{
				format = "yyyy-MM-dd";
			}
			else if (type.equalsIgnoreCase("time"))
			{
				format = "hh:mm:ss";
			}

			//--- quikdraw: This is doing exactly what?
			try
			{
				new SimpleDateFormat(format).parse(stringDate);
			}
			catch (ParseException pe)
			{
				throw new PersistenceException("Value '" + stringDate + "' for field '"
								+ myMetaData.getDescription(fieldName) + "' cannot be parsed as a date");
			}
		}
	} /* checkField(String, String) */

	/**
	 * See if this field value contains wild cards (pattern matching criteria
	 * for the database). The wild cards can be configured via the properties
	 * file.
	 *
	 * @param fieldValue
	 *            The field value to check for wild cards
	 * @return True if the string does contain wild cards, False if it does not
	 * @throws PersistenceException
	 *             If there is an error accessing meta-data
	 */
	protected synchronized boolean containsWildCards(String fieldValue) throws PersistenceException
	{
		String fieldVal = fieldValue;

		if (fieldValue == null)
		{
			fieldVal = "";
		}

		for (Iterator it = myMetaData.getDatabaseType().getWildCards().iterator(); it.hasNext();)
		{
			if (fieldVal.indexOf((String) it.next()) > 0)
			{
				return true;
			}
		}

		return false;
	} /* containsWildCards(String) */

	/**
	 * If this Persistent has associated detail objects, locate the appropriate
	 * related detail records and delete them as well. This is a "cascading
	 * delete" process.
	 */
	protected void deleteDetails() throws PersistenceException
	{
		String oneDet = null;

		for (Iterator ee = myMetaData.getDetailNames().iterator(); ee.hasNext();)
		{
			oneDet = (String) ee.next();

			Relation oneDetRelation = myMetaData.getRelation(oneDet);
			Persistent detailObj = null;

			try
			{
				detailObj = getFactory().create(oneDetRelation.getToPersistent());
			}
			catch (Exception e)
			{
				throw new PersistenceException("Unable to instantiate " + "detail db object '"
								+ oneDetRelation.getToPersistent() + "'", e);
			}

			if (currentTransaction != null)
			{
				detailObj.setTransaction(currentTransaction);
			}

			if (! detailObj.allowed(Persistent.DELETE))
			{
				throw new PersistenceException("Delete of '" + oneDetRelation.getToPersistent() + "' not allowed");
			}

			Iterator stkLocal = oneDetRelation.getFromFields().iterator();
			Iterator stkForeign = oneDetRelation.getToFields().iterator();

			while (stkLocal.hasNext())
			{
				String localField = (String) stkLocal.next();
				String foreignField = (String) stkForeign.next();

				detailObj.setField(foreignField, getField(localField));
			}

			Persistent oneDetailObj = null;

			for (Iterator di = detailObj.query().iterator(); di.hasNext();)
			{
				oneDetailObj = (Persistent) di.next();
				oneDetailObj.setTransaction(currentTransaction);
				oneDetailObj.delete(true);
			}
		} /* for each detail */
	} /* deleteDetails */

	/**
	 * Method getValueForDelete. This method prepares a field value for
	 * addition to the database. Date formatting is taken care of, as well as
	 * quoting as needed.
	 *
	 * @param fieldName
	 * @return String - a string ready for use in an INSERT SQL statement.
	 * @throws PersistenceException
	 */
	protected String getValueForDelete(String fieldName, String rangeString) throws PersistenceException
	{
		String returnString = null;

		if (getField(fieldName) == null)
		{
			return null;
		}

		if (getField(fieldName).equals(""))
		{
			return null;
		}

		String myType = myMetaData.getType(fieldName);

		if (myType.equalsIgnoreCase("date") || myType.equalsIgnoreCase("datetime") || myType.equalsIgnoreCase("time")
						|| myType.equalsIgnoreCase("timestamp"))
		{
			returnString = formatDateTime(fieldName);
		}
		else
		{
			returnString = quoteIfNeeded(fieldName, rangeString);
		}

		return returnString;
	}

	/**
	 * Method getValueForUpdate. This method prepares a field value for use in
	 * a SQL UPDATE statement. Date formatting is done, and the value is quoted
	 * as needed.
	 *
	 * @param fieldName
	 * @return String
	 * @throws PersistenceException
	 */
	protected String getValueForUpdate(String fieldName, String rangeString) throws PersistenceException
	{
		return getValueForDelete(fieldName, rangeString);
	}

	/**
	 * Return the current object's character se.t
	 *
	 * @return The String representation of the current object's character set.
	 */
	protected synchronized String getCharset() throws PersistenceException
	{
		return myMetaData.getCharset();
	} /* getCharset() */

	/**
	 * Return the default value for a field, if any. Return null if no default
	 * value specified.
	 */
	protected String getDefaultValue(String fieldName) throws PersistenceException
	{
		return myMetaData.getDefaultValue(fieldName);
	}

	/**
	 * Access the "raw" field data for the named field.
	 */
	protected Object getFieldData(String fieldName)
	{
		if (fieldData == null)
		{
			fieldData = new HashMap();
		}

		return fieldData.get(fieldName);
	}

	protected void assertField(String fieldName) throws PersistenceException
	{
		if (! myMetaData.hasField(fieldName))
		{
			throw new PersistenceException("Persistent '" + getName() + "' contains no such field as " + fieldName);
		}
	}

	/**
	 * Get a string consisting of the values of each key field for this object
	 * appended together with a | between them.
	 *
	 * @return Value of all keys appended with a | between
	 * @throws PersistenceException
	 *             If the key list cannot be built.
	 */
	protected String getMyKeys() throws PersistenceException
	{
		StringBuffer myKeys = new StringBuffer();
		boolean needPipe = false;

		for (Iterator i = myMetaData.getKeyFieldNames().iterator(); i.hasNext();)
		{
			if (needPipe)
			{
				myKeys.append("|");
			}

			myKeys.append(getFieldString((String) i.next()));
			needPipe = true;
		}

		return myKeys.toString();
	} /* getMyKeys() */

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
	 * This function is called whenever the Persistent is about to be written
	 * to the database. It may do additional processing such as encryption
	 * depending on the field attributes.
	 *
	 * @throws PersistenceException
	 * @return the value to write to the data source.
	 */
	protected String getSerializedForm(String fieldName) throws PersistenceException
	{
		String returnValue = null;

		try
		{
			if (! myMetaData.isEncrypted(fieldName))
			{
				Object o = getFieldData(fieldName);

				if (o != null)
				{
					returnValue = o.toString();
				}
			}
		}
		catch (Exception e)
		{
			throw new PersistenceException(e);
		}

		return returnValue;
	} /* getSerializedForm() */

	/**
	 * See if we have a value for each of the key fields
	 *
	 * @param withAuto
	 *            True if we must have values for even auto-incremented keys,
	 *            if false, then auto-incremented key fields are allowed to be
	 *            blank or null
	 * @param throwException
	 *            Throw a PersistenceException if all keys are not present, as
	 *            opposed to simply returning false.
	 * @return True if all key fields have a value, false if not
	 * @throws PersistenceException
	 */
	protected boolean haveAllKeys(boolean withAuto, boolean throwException) throws PersistenceException
	{
		Object fieldValue = null;
		String strVal = null;
		String oneFieldName = null;

		for (Iterator i = myMetaData.getKeyFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();
			fieldValue = getField(oneFieldName);

			if (fieldValue == null)
			{
				strVal = "";
			}
			else
			{
				strVal = fieldValue.toString();
			}

			if (strVal.equals(""))
			{
				if (withAuto)
				{
					if (throwException)
					{
						throw new PersistenceException("$keelNullNotAllowed|" + myMetaData.getDescription(oneFieldName));
					}

					return false;
				}

				if (! myMetaData.isAutoIncremented(oneFieldName))
				{
					if (throwException)
					{
						throw new PersistenceException("$keelNullNotAllowed|" + myMetaData.getDescription(oneFieldName));
					}

					return false;
				}
			}
		}

		return true;
	} /* haveAllKeys() */

	/**
	 * Return true if the given type is a date/time type of field
	 *
	 * @param theType
	 * @return
	 */
	protected static boolean isDateType(String theType)
	{
		boolean returnValue = false;

		if (theType.equalsIgnoreCase("date") || theType.equalsIgnoreCase("time")
						|| theType.equalsIgnoreCase("datetime") || theType.equalsIgnoreCase("timestamp"))
		{
			returnValue = true;
		}

		return returnValue;
	} /* isDateType(String) */

	/**
	 * Return true if every field in this object is empty or null
	 *
	 * @return boolean: True if the record is "empty" (all fields blank), False
	 *         if not.
	 * @throws PersistenceException
	 *             If the list of fields cannot be traversed
	 */
	protected synchronized boolean isEmpty() throws PersistenceException
	{
		String oneName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneName = (String) i.next();

			if (! getFieldString(oneName).equalsIgnoreCase(""))
			{
				return false;
			}
		} /* for each field */
		return true;
	} /* isEmpty() */

	/**
	 * Creates the limitation syntax optimisation stub to embed inside the SQL
	 * command that performs search and retrieve.
	 *
	 * <p>
	 * This method takes the limitation syntax string and performs a string
	 * replacement on the following tokens
	 *
	 * <ul>
	 *
	 * <li><b>%offset%</b>
	 * <li><br>the number of rows in the <code>ResultSet</code> to skip
	 * before reading the data.
	 *
	 * <li><b>%maxrecord%</b>
	 * <li><br>the maximum number of rows to read from the <code>ResultSet</code>.
	 * Also known as the <i>rowlength</i>.
	 *
	 * <li><b>%endrecord%</b>
	 * <li><br>the last record of in the <code>ResultSet</code> that the
	 * search and retrieved should retrieve. The end record number is equal to
	 * <code>( %offset% + %maxrecord% - 1 )</code>
	 *
	 * </ul>
	 *
	 * </p>
	 *
	 * @see de.iritgo.aktera.persist.DatabaseType#getSubSetSyntax()
	 * @see #query()
	 * @see #setOffsetRecord( int )
	 * @see #setMaxRecords( int )
	 */
	protected String makeSubSetStub()
	{
		String limit = myMetaData.getDatabaseType().getSubSetSyntax();
		int endrec = offsetRecord + maxRecords - 1;

		limit = SuperString.replace(limit, "%offset%", Integer.toString(offsetRecord));
		limit = SuperString.replace(limit, "%maxrecords%", Integer.toString(maxRecords));
		// limit = SuperString.replace( limit, "%length%", Integer.toString(
		// maxRecords ) );
		limit = SuperString.replace(limit, "%endrecord%", Integer.toString(endrec));

		return limit;
	} /* makeSubSetStub() */

	/**
	 * Return the value of this field, placing double quotes around it if the
	 * field's datatype requires it.
	 *
	 * @param fieldName
	 *            The name of the field to be used
	 * @return A string, quoted if necessary, to be used in building an SQL
	 *         statement
	 * @throws PersistenceException
	 *             If there is no such field or it's value cannot be determined
	 */
	protected String quoteIfNeeded(String fieldName, String rangeString) throws PersistenceException
	{
		String fieldValue = null;

		assertField(fieldName);

		fieldValue = getSerializedForm(fieldName);

		/* if the field is null, we don't need to worry about quotes */
		if (fieldValue == null)
		{
			return "null";
		}

		if (rangeString != null)
		{
			fieldValue = fieldValue.substring(rangeString.length());
		}

		if (myMetaData.getDatabaseType().isNumericType(myMetaData.getType(fieldName)))
		{
			if (fieldValue.equals("") || fieldValue.equals(" "))
			{
				return "0";
			}

			return fieldValue.trim();
		} /* if a numeric type */
		if (myMetaData.getDatabaseType().isQuotedType(myMetaData.getType(fieldName)))
		{
			if ((fieldValue.indexOf("\'") != - 1) || (fieldValue.indexOf("\"") != - 1))
			{
				return "\'" + SuperString.noQuotes(fieldValue.trim()) + "\'";
			}
			else
			{
				return "\'" + fieldValue.trim() + "\'";
			}
		} /* if a quoted type */
		if (myMetaData.getDatabaseType().isDateType(myMetaData.getType(fieldName)))
		{
			return "\'" + fieldValue + "\'";
		}

		return fieldValue.trim();
	} /* quoteIfNeeded(String) */

	/**
	 * Build an appropriate String for use in the select part of an SQL
	 * statement by doing the
	 *
	 * @return The portion of the select clause with the appropriate function
	 *         wrapped around it
	 * @param fieldName
	 *            The name of the field to be handled
	 * @throws PersistenceException
	 */
	protected String selectFieldString(String fieldName) throws PersistenceException
	{
		String dbFieldName = myMetaData.getDBFieldName(fieldName);
		DatabaseType myDBType = myMetaData.getDatabaseType();

		String fieldType = myMetaData.getType(fieldName);

		if (fieldType.equalsIgnoreCase("date"))
		{
			if (! SuperString.notNull(myDBType.getDateSelectFunction()).equals(""))
			{
				return (SuperString.replace(myDBType.getDateSelectFunction(), "%s", dbFieldName));
			}
		}

		if (fieldType.equalsIgnoreCase("time"))
		{
			if (! SuperString.notNull(myDBType.getTimeSelectFunction()).equals(""))
			{
				return (SuperString.replace(myDBType.getTimeSelectFunction(), "%s", dbFieldName));
			}
		}

		if (fieldType.equalsIgnoreCase("datetime"))
		{
			if (! SuperString.notNull(myDBType.getDateTimeSelectFunction()).equals(""))
			{
				return (SuperString.replace(myDBType.getDateTimeSelectFunction(), "%s", dbFieldName));
			}
		}

		return dbFieldName;
	} /* selectFieldString(String) */

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
	 * Gets the check zero update flags for this Persistent.
	 *
	 * @return boolean value that denotes if check zero update is on or off.
	 *
	 */
	protected synchronized boolean getCheckZeroUpdate()
	{
		return checkZeroUpdate;
	} /* setCheckZeroUpdate(boolean) */

	/**
	 * Turn on or off the facility to verify that when an update is made that
	 * at least one record got updated. If this flag is on, and no records get
	 * updated, the update() method throws an Exception. Note that for some
	 * databases, if the existing record is not changed (e.g. it was already
	 * identical to what was being updated) this counts "no update" (notably,
	 * mySQL does this).
	 *
	 * @param newFlag
	 *            True to turn on checking, false to turn it off
	 */
	protected synchronized void setCheckZeroUpdate(boolean newFlag)
	{
		checkZeroUpdate = newFlag;
	} /* setCheckZeroUpdate(boolean) */

	protected synchronized void setFieldData(String fieldName, Object fieldValue) throws PersistenceException
	{
		if (fieldData == null)
		{
			fieldData = new HashMap();
		}

		fieldData.put(fieldName, fieldValue);
		setBeanField(fieldName, fieldValue);
	}

	protected void setPreparedStatementObject(PreparedStatement ps, int index, String fieldName)
		throws PersistenceException, SQLException
	{
		int typeToUse = JDBCDatabaseType.stringToType(myMetaData.getType(fieldName));

		if (log.isDebugEnabled())
		{
			log.debug("Setting Field #/FieldName/Value" + index + '/' + fieldName + '/' + this.getFieldData(fieldName));
			log.debug("FieldType: " + typeToUse);
		}

		Object o = this.getFieldData(fieldName);

		if (o == null)
		{
			ps.setNull(index, typeToUse);

			return;
		}

		try
		{
			switch (typeToUse)
			{
				case java.sql.Types.VARCHAR:
					log.debug("FieldTypeName: VARCHAR");
					ps.setString(index, this.getFieldString(fieldName));

					break;

				case java.sql.Types.ARRAY:
					log.debug("FieldTypeName: ARRAY");
					ps.setArray(index, (Array) this.getFieldData(fieldName));

					break;

				case java.sql.Types.BIGINT: // No getBigInt?
					log.debug("FieldTypeName: BIGINT");
					ps.setLong(index, this.getFieldLong(fieldName));

					break;

				case java.sql.Types.BIT:
					log.debug("FieldTypeName: BIT");
					ps.setBoolean(index, this.getFieldBoolean(fieldName));

					break;

				//really not sure here....
				case java.sql.Types.BLOB:
					log.debug("FieldTypeName: BLOB");
					ps.setBlob(index, (Blob) this.getFieldData(fieldName));

					break;

				case java.sql.Types.BOOLEAN:
					log.debug("FieldTypeName: BOOLEAN");
					ps.setBoolean(index, this.getFieldBoolean(fieldName));

					break;

				case java.sql.Types.CHAR:
					log.debug("FieldTypeName: CHAR");
					ps.setString(index, this.getFieldString(fieldName));

					break;

				case java.sql.Types.CLOB:
					log.debug("FieldTypeName: CLOB");
					ps.setClob(index, (Clob) this.getFieldData(fieldName));

					break;

				case java.sql.Types.DATE:
					log.debug("FieldTypeName: DATE");
					ps.setDate(index, getFieldDate(fieldName));

					break;

				case java.sql.Types.DECIMAL:
					log.debug("FieldTypeName: DECIMAL");
					ps.setBigDecimal(index, new BigDecimal(getFieldString(fieldName)));

					break;

				//case java.sql.Types.DISTINCT :
				//	return rs.getObject(index);
				case java.sql.Types.DOUBLE:
					log.debug("FieldTypeName: DOUBLE");
					ps.setDouble(index, new Double(getFieldString(fieldName)).doubleValue());

					break;

				case java.sql.Types.FLOAT:
					log.debug("FieldTypeName: FLOAT");
					ps.setFloat(index, new Float(getFieldString(fieldName)).floatValue());

					break;

				case java.sql.Types.INTEGER:
					log.debug("FieldTypeName: INTEGER");

					try
					{
						ps.setInt(index, new Integer(getFieldString(fieldName)).intValue());
					}
					catch (NumberFormatException x)
					{
						ps.setNull(index, java.sql.Types.INTEGER);
					}

					break;

				case java.sql.Types.JAVA_OBJECT:
					log.debug("FieldTypeName: JAVA_OBJECT");
					ps.setObject(index, getFieldData(fieldName));

					break;

				case java.sql.Types.LONGVARBINARY:
					log.debug("FieldTypeName: LONGVARBINARY");
					ps.setObject(index, getFieldData(fieldName), java.sql.Types.LONGVARBINARY);

					break;

				case java.sql.Types.LONGVARCHAR:
					log.debug("FieldTypeName: LONGVARCHAR");
					ps.setString(index, this.getFieldString(fieldName));

					break;

				case java.sql.Types.NULL:
					log.debug("FieldTypeName: NULL");
					ps.setNull(index, java.sql.Types.NULL);

					break;

				case java.sql.Types.NUMERIC:
					log.debug("FieldTypeName: NUMBERIC");
					ps.setLong(index, this.getFieldLong(fieldName));

					break;

				case java.sql.Types.OTHER:
					log.debug("FieldTypeName: OTHER");
					ps.setObject(index, getFieldData(fieldName), java.sql.Types.OTHER);

					break;

				case java.sql.Types.REAL:
					log.debug("FieldTypeName: REAL");
					ps.setFloat(index, new Float(getFieldString(fieldName)).floatValue());

					break;

				case java.sql.Types.REF:
					log.debug("FieldTypeName: REF");
					ps.setObject(index, getFieldData(fieldName), java.sql.Types.REF);

					break;

				case java.sql.Types.SMALLINT:
					log.debug("FieldTypeName: SMALLINT");
					ps.setShort(index, new Short(getFieldString(fieldName)).shortValue());

					break;

				case java.sql.Types.STRUCT:
					log.debug("FieldTypeName: STRUCT");
					ps.setObject(index, getFieldData(fieldName), java.sql.Types.STRUCT);

					break;

				case java.sql.Types.TIME:
					log.debug("FieldTypeName: TIME");
					ps.setTime(index, new java.sql.Time(getFieldDate(fieldName).getTime()));

					break;

				case java.sql.Types.TIMESTAMP:
					log.debug("FieldTypeName: TIMESTAMP");

					java.util.Date fieldDate = this.getFieldDate(fieldName);
					java.sql.Timestamp ts = null;

					if (fieldDate != null)
					{
						ts = new Timestamp(fieldDate.getTime());
						ps.setTimestamp(index, ts);
					}
					else
					{
						ps.setNull(index, Types.TIMESTAMP);
					}

					break;

				case java.sql.Types.TINYINT:
					log.debug("FieldTypeName: TINYINT");
					ps.setShort(index, new Short(getFieldString(fieldName)).shortValue());

					break;

				case java.sql.Types.VARBINARY:
					log.debug("FieldTypeName: VARBINARY");
					ps.setBlob(index, (Blob) this.getFieldData(fieldName));

					break;

				default:
					log.debug("FieldTypeName: DEFAULT");
					ps.setObject(index, this.getFieldData(fieldName));

					break;
			}
		}
		catch (ClassCastException cce)
		{
			String msg = "Error on field " + fieldName + ".  Cannot cast data of type "
							+ this.getFieldData(fieldName).getClass().getName()
							+ " to the data type used by java.sql.Types " + myMetaData.getType(fieldName) + ".";

			cce.printStackTrace();
			//Screen is not always logged, esepcially on MS, so record in
			// application log for later analysis.
			log.error(msg, cce);
			throw new PersistenceException(msg, cce);
		}
		catch (NumberFormatException ne)
		{
			String msg = "Error on field " + fieldName + ". Cannot parse value '" + getFieldString(fieldName) + "'"
							+ " into type " + myMetaData.getType(fieldName);

			throw new PersistenceException(msg, ne);
		}

		if (log.isDebugEnabled())
		{
			log.debug("Set Field #/FieldName/Value" + index + '/' + fieldName + '/' + this.getFieldData(fieldName));
		}
	}

	/**
	 * Set the current status of a record
	 *
	 * @param newStatus
	 *            <p>
	 *            NEW: Record is new
	 *            </p>
	 *            <p>
	 *            CURRENT: Record is synchronized with the database
	 *            </p>
	 *            <p>
	 *            DELETED: Record has been deleted
	 *            </p>
	 *            <p>
	 *            MODIFIED: Record has been updated since it was last written
	 *            to the database
	 *            </p>
	 */
	protected void setStatus(int newStatus)
	{
		if ((newStatus == Persistent.NEW) || (newStatus == Persistent.CURRENT) || (newStatus == Persistent.MODIFIED)
						|| (newStatus == Persistent.DELETED))
		{
			currentStatus = newStatus;
		}
		else
		{
			throw new IllegalArgumentException("Unknown status '" + newStatus + "'");
		}
	} /* setStatus(int) */

	/**
	 * This allows the invocation of the SQL AVG, MIN, MAX and SUM aggregate
	 * functions on one of the DB's columns (the Persistent fieldname is
	 * supplied).
	 *
	 * This function is not meant to be used directly, but is here as the
	 * common/shared code used by the following methods in Persistent:
	 * avg(String), min(String), max(String), sum(String).
	 *
	 * @param String
	 *            The SQL aggregate function - must be one of AVG, MIN, MAX,
	 *            SUM
	 * @param String
	 *            The Persistent fieldName to use for the aggregate function
	 * @return double The result of the SQL function invocation
	 * @throws PersistenceException
	 *             If the function could not be completed
	 */
	protected synchronized double sqlAggrFunction(String func, String fieldName) throws PersistenceException
	{
		checkAllowed("L");

		SuperString myStatement = new SuperString(48);

		myStatement.append("SELECT " + func);
		myStatement.append("(" + fieldName + ") FROM ");
		myStatement.append(myMetaData.getTableName());

		myStatement.append(buildWhereClauseBuffer(true));

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

			if (rs.next())
			{
				return rs.getDouble(1);
			}
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			cleanUp(myConnection, s, rs, null);
		}

		return 0.0;
	} /* sqlAggrFunction() */

	/**
	 * Our PersistentMetaData hands us a data source service. We use this data
	 * source to get connections *if* we are not participating in a
	 * transaction. If we are in a transaction, we use the Connection from the
	 * transaction instead
	 */
	protected void setDataSource(DataSourceComponent newDataSource)
	{
		myDataSource = newDataSource;
	}

	public void setLogger(Logger newLog)
	{
		assert newLog != null;
		log = newLog;
	}

	/**
	 * Called by the factory to pass this persistent it's meta data. We take
	 * this opportunity to initialize our underlying bean, if one is defined.
	 */
	public void setMetaData(PersistentMetaData newMeta)
	{
		myMetaData = newMeta;
		setDataSource(myMetaData.getDataSource());

		try
		{
			createBean();
		}
		catch (PersistenceException pe)
		{
			log.error("Unable to create bean:", pe);
			throw new IllegalArgumentException(pe.getMessage());
		}
	}

	protected DefaultPersistentFactory getFactory() throws PersistenceException
	{
		return myFactory;
	}

	protected void setAutoIncrement(String fieldName) throws PersistenceException
	{
		IdGenerator myIdGenerator = myMetaData.getIdGenerator(fieldName);

		try
		{
			setField(fieldName, myIdGenerator.getNextIntegerId());
		}
		catch (IdException ie)
		{
			log.error("Unable to auto-increment field", ie);
			throw new PersistenceException("$keelFieldNoAutoInc|" + myMetaData.getDescription(fieldName), ie);
		}
	}

	protected Object retrieveField(String fieldName, ResultSet rs, int index) throws SQLException, PersistenceException
	{
		int typeToUse = JDBCDatabaseType.stringToType(myMetaData.getType(fieldName));
		Object returnValue = null;

		switch (typeToUse)
		{
			case java.sql.Types.ARRAY:
				returnValue = rs.getArray(index);

				break;

			case java.sql.Types.BIGINT: // No getBigInt?
				returnValue = rs.getBigDecimal(index);

				break;

			case java.sql.Types.BINARY:
				returnValue = rs.getBinaryStream(index);

				break;

			case java.sql.Types.BIT:
				returnValue = new Boolean(rs.getBoolean(index));

				break;

			case java.sql.Types.BLOB:
				returnValue = rs.getBlob(index);

				break;

			case java.sql.Types.BOOLEAN:
				returnValue = new Boolean(rs.getBoolean(index));

				break;

			case java.sql.Types.CHAR:
				returnValue = rs.getString(index);

				break;

			case java.sql.Types.CLOB:
				returnValue = rs.getClob(index);

				break;

			// BELOW FOR JDK 1.4+ only
			//case java.sql.Types.DATALINK:
			//  returnValue = "DATALINK";
			//  break;
			case java.sql.Types.DATE:
				returnValue = rs.getDate(index);

				break;

			case java.sql.Types.DECIMAL:
				returnValue = rs.getBigDecimal(index);

				break;

			case java.sql.Types.DISTINCT:
				returnValue = rs.getObject(index);

				break;

			case java.sql.Types.DOUBLE:
				returnValue = new Double(rs.getDouble(index));

				break;

			case java.sql.Types.FLOAT:
				returnValue = new Float(rs.getFloat(index));

				break;

			case java.sql.Types.INTEGER:
				returnValue = new Integer(rs.getInt(index));

				break;

			case java.sql.Types.JAVA_OBJECT:
				returnValue = rs.getString(index);

				break;

			case java.sql.Types.LONGVARBINARY:
				returnValue = rs.getBlob(index);

				break;

			case java.sql.Types.LONGVARCHAR:
				returnValue = rs.getString(index);

				break;

			case java.sql.Types.NULL:
				returnValue = rs.getObject(index);

				break;

			case java.sql.Types.NUMERIC:
				returnValue = new Long(rs.getLong(index));

				break;

			case java.sql.Types.OTHER:
				returnValue = rs.getObject(index);

				break;

			case java.sql.Types.REAL:
				returnValue = new Float(rs.getFloat(index));

				break;

			case java.sql.Types.REF:
				returnValue = rs.getObject(index);

				break;

			case java.sql.Types.SMALLINT:
				returnValue = new Short(rs.getShort(index));

				break;

			case java.sql.Types.STRUCT:
				returnValue = rs.getObject(index);

				break;

			case java.sql.Types.TIME:
				returnValue = rs.getTime(index);

				break;

			case java.sql.Types.TIMESTAMP:
				returnValue = rs.getTimestamp(index);

				break;

			case java.sql.Types.TINYINT:
				returnValue = new Short(rs.getShort(index));

				break;

			case java.sql.Types.VARBINARY:
				returnValue = rs.getBlob(index);

				break;

			case java.sql.Types.VARCHAR:
				returnValue = rs.getString(index);

				break;

			default:
				throw new PersistenceException("Unknown type " + myMetaData.getType(fieldName));
		} // switch

		return returnValue;
	}

	void setFactory(DefaultPersistentFactory newFactory)
	{
		myFactory = newFactory;
	}

	protected boolean isAllowed(String operationCode) throws SecurityException
	{
		if (! getMetaData().isSecurable())
		{
			if (log.isDebugEnabled())
			{
				log.debug("Persistent " + getName() + " is not securable - operation allowed");
			}

			return true;
		}

		Operation o = new DefaultOperation();

		o.setOperationCode(operationCode);

		o.setService(this);

		try
		{
			DefaultPersistentFactory dpf = getFactory();
			Context c = null;
			AuthorizationManager am = myAuthManager;

			if (myBypassAuthManager != null)
			{
				am = myBypassAuthManager;
			}
			else
			{
				c = dpf.getKeelContext();

				if (c == null)
				{
					throw new SecurityException(
									"No context available and no bypass auth manager specified - unable to access secure persistent object "
													+ getInstanceIdentifier());
				}
			}

			if (am == null)
			{
				String msg = "Authmanager was not set for " + getName() + ", unable to verify authorization";

				log.error(msg);
				throw new SecurityException(msg);
			}

			return am.allowed(o, c);
		}
		catch (PersistenceException pe)
		{
			log.error("DB Error, unable to verify authorization", pe);
			throw new SecurityException("DB error, unable to verify authorization");
		}
		catch (AuthorizationException e)
		{
			log.error("Unable to verify authorization", e);
			throw new SecurityException("Unable to verify authorization");
		}
	}

	public void setAuthorizationManager(AuthorizationManager am)
	{
		myAuthManager = am;
	}

	public String getInstanceIdentifier()
	{
		return myMetaData.getSchemaName() + "." + myMetaData.getName();
	}

	protected void checkAllowed(String operation) throws SecurityException
	{
		if (log.isDebugEnabled())
		{
			log.debug("Checking access to " + this.toString());
		}

		if (isAllowed(operation))
		{
			return;
		}

		String failedUserName = "unknown";
		String failedUserId = "unknown";

		try
		{
			DefaultPersistentFactory dpf = getFactory();
			Context c = dpf.getKeelContext();

			if (c != null)
			{
				UserEnvironment ue = (UserEnvironment) c.get(UserEnvironment.CONTEXT_KEY);

				if (ue == null)
				{
					failedUserName = "(No user logged in)";
				}
				else
				{
					failedUserName = ue.getLoginName();
					failedUserId = "" + ue.getUid();
				}
			}
		}
		catch (Exception pe)
		{
			log.error("Unable to determine id of rejected user", pe);
		}

		StringBuffer failMessage = new StringBuffer();

		if (operation.equals("A"))
		{
			failMessage.append("Add operation");
		}
		else if (operation.equals("L"))
		{
			failMessage.append("Select operation");
		}
		else if (operation.equals("U"))
		{
			failMessage.append("Update operation");
		}
		else if (operation.equals("D"))
		{
			failMessage.append("Delete operation");
		}

		failMessage.append(" not authorized for user " + failedUserName + " (uid=" + failedUserId + ") for persistent "
						+ toString());
		throw new SecurityException(failMessage.toString());
	}

	/**
	 * Create the underlying bean - bean may be replaced with a setBean call
	 */
	protected void createBean() throws PersistenceException
	{
		String className = myMetaData.getClassName();

		if (className == null)
		{
			className = "";
		}

		if (! className.equals(""))
		{
			/* Use an instance of class name supplied */
			try
			{
				Class c = Class.forName(className);

				myBean = c.newInstance();
			}
			catch (Exception e)
			{
				throw new PersistenceException("Unable to create bean '" + className + "'", e);
			}
		}
		else
		{
			log.warn("No bean class defined for '" + getName() + "'");
		}
	}

	protected void setBeanField(String fieldName, Object fieldValue) throws PersistenceException
	{
		if (myBean == null)
		{
			return;
		}

		String propertyName = getPropertyNameIgnoreCase(fieldName);

		PropertyDescriptor oneProp = null;

		try
		{
			oneProp = PropertyUtils.getPropertyDescriptor(myBean, propertyName);
		}
		catch (Exception ne)
		{
			log.warn("Bean for '" + getName() + "' does not declare field '" + propertyName + "'", ne);

			return;
		}

		if (oneProp == null)
		{
			log.warn("Bean for '" + getName() + "' does not declare field '" + propertyName + "'");

			return;
		}

		if (oneProp.getWriteMethod() == null)
		{
			log.warn("Property '" + propertyName + "' for bean of '" + getName() + "' is not writable");

			return;
		}

		Class oneType = oneProp.getPropertyType();
		String typeString = oneType.getName();
		String convertedType = "";
		String origValue = null;
		String strVal = null;

		if (oneProp.getName() == null)
		{
			throw new PersistenceException("Null property name for field '" + fieldName + "'");
		}

		try
		{
			Object oneValue = fieldValue;

			if (oneValue != null)
			{
				origValue = oneValue.toString();
				strVal = ConvertUtils.convert(oneValue);

				Object converted = ConvertUtils.convert(strVal, oneType);

				if (converted != null)
				{
					convertedType = converted.getClass().getName();
				}

				PropertyUtils.setProperty(myBean, oneProp.getName(), converted);
			}
			else
			{
				PropertyUtils.setProperty(myBean, oneProp.getName(), null);
			}
		}
		catch (Exception ie)
		{
			addError(fieldName, "Unable to set property '" + oneProp.getName() + "' (of type " + typeString
							+ ") original value '" + origValue + "' in object of class '" + myBean.getClass().getName()
							+ "'. Converted to string '" + strVal + "', Converted value of type " + convertedType + ":"
							+ ie.getMessage());
		}
	}

	protected String getFieldNameIgnoreCase(String fieldName)
	{
		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			if (oneFieldName.equalsIgnoreCase(fieldName))
			{
				return oneFieldName;
			}
		}

		return fieldName;
	}

	protected String getPropertyNameIgnoreCase(String fieldName) throws PersistenceException
	{
		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(myBean);

		if (props.length == 0)
		{
			throw new PersistenceException("Class '" + myBean.getClass().getName() + "' declares no fields");
		}

		PropertyDescriptor oneProp = null;

		for (int i = 0; i < props.length; i++)
		{
			oneProp = props[i];

			if (oneProp.getName().equalsIgnoreCase(fieldName))
			{
				return oneProp.getName();
			}
		}

		return fieldName;
	}

	protected void cleanUp(Connection c, Statement s, ResultSet rs, PreparedStatement ps) throws PersistenceException
	{
		if (rs != null)
		{
			try
			{
				rs.close();
				rs = null;
			}
			catch (SQLException se)
			{
				throw new PersistenceException("Unable to close result set", se);
			}
		}

		if (ps != null)
		{
			try
			{
				ps.close();
				ps = null;
			}
			catch (SQLException se)
			{
				throw new PersistenceException("Unable to close prepared statement", se);
			}
		}

		if (s != null)
		{
			try
			{
				s.close();
				s = null;
			}
			catch (SQLException se)
			{
				throw new PersistenceException("Unable to close statement", se);
			}
		}

		if ((c != null) && (currentTransaction == null))
		{
			try
			{
				c.close();
				c = null;
			}
			catch (SQLException se)
			{
				throw new PersistenceException("Unable to close connection", se);
			}
		}
	}

	/**
	 * Given the value of a date/time or date/time field, return the value
	 * formatted as appropriate for the current DBMS. Can be configured using
	 * property file values.
	 *
	 * @return java.lang.String The formatted date time, ready for use in the
	 *         DBMS
	 * @param fieldValue
	 *            java.lang.String The value for the date/time field.
	 * @throws PersistenceException
	 */
	protected String formatDateTime(String fieldName) throws PersistenceException
	{
		Date originalDate = getFieldDate(fieldName);

		String convertFormat = new String("");
		String convertFunction = new String("");

		if (! myMetaData.hasField(fieldName))
		{
			throw new PersistenceException("(" + getName() + ") No such field as " + fieldName);
		}

		if (getField(fieldName) == null)
		{
			return null;
		}

		if (getField(fieldName).equals(""))
		{
			return null;
		}

		String myType = myMetaData.getType(fieldName);

		if (myType.equalsIgnoreCase("date"))
		{
			convertFormat = myMetaData.getDatabaseType().getDateUpdateFormat();
			convertFunction = myMetaData.getDatabaseType().getDateUpdateFunction();
		}
		else if (myType.equalsIgnoreCase("datetime") || myType.equalsIgnoreCase("timestamp"))
		{
			convertFormat = myMetaData.getDatabaseType().getDateTimeUpdateFormat();
			convertFunction = myMetaData.getDatabaseType().getDateTimeUpdateFunction();
		}
		else if (myType.equalsIgnoreCase("time"))
		{
			convertFormat = myMetaData.getDatabaseType().getTimeUpdateFormat();
			convertFunction = myMetaData.getDatabaseType().getTimeUpdateFunction();
		}
		else
		{
			throw new PersistenceException("Field '" + fieldName + "' is not a date, datetime or time - it is a "
							+ myType + ", which cannot be formatted " + "as a Date/Time type");
		}

		convertFormat = SuperString.notNull(convertFormat);
		convertFunction = SuperString.notNull(convertFunction);

		/* If no format was specified, don't change the existing field */
		if (convertFormat.equals(""))
		{
			if (! convertFunction.equals(""))
			{
				return SuperString.replace(convertFunction, "%s", "'" + getField(fieldName) + "'");
			}
			else
			{
				return "'" + getField(fieldName) + "'";
			}
		}

		String returnValue = null;

		SimpleDateFormat formatter = new SimpleDateFormat(convertFormat);

		returnValue = "'" + formatter.format(originalDate) + "'";

		if (returnValue == null)
		{
			throw new PersistenceException("(" + getName() + ") Unable to format date value from field " + fieldName
							+ ", value was " + getField(fieldName));
		}

		if (! convertFunction.equals(""))
		{
			return SuperString.replace(convertFunction, "%s", returnValue);
		}

		return returnValue;
	} /* formatDateTime(String) */

	/**
	 * Get the long value of a field in this object
	 *
	 * @param fieldName
	 *            Name of a field in this object
	 * @return long The value of the field as a float
	 * @throws PersistenceException
	 *             if there is no such field or it's value cannot be converted
	 *             to an long.
	 */
	public long getFieldLong(String fieldName) throws PersistenceException
	{
		Object o = getFieldData(fieldName);

		if (o == null)
		{
			return Long.parseLong("0");
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

			return Long.parseLong(strVal);
		}
		catch (NumberFormatException ex)
		{
			throw new PersistenceException("(" + getName() + ") Unable to parse an integer value from field "
							+ fieldName + " which contained '" + strVal, ex);
		}
	} /* getFieldLong(String) */

	/**
	 * Boolean typesafe version of setField
	 */
	public synchronized void setField(String fieldName, boolean fieldValue) throws PersistenceException
	{
		if (fieldValue == true)
		{
			setField(fieldName, "true");
		}
		else
		{
			setField(fieldName, "false");
		}
	}

	/**
	 * Integer Typesafe version of setField
	 */
	public synchronized void setField(String fieldName, int fieldValue) throws PersistenceException
	{
		setField(fieldName, Integer.toString(fieldValue));
	}

	public PersistentMetaData getMetaData()
	{
		return myMetaData;
	}

	/**
	 * Set the given field to a given value. Call checkField before setting the
	 * value to verify that it is allowed for this field The subclass must
	 * override checkField as required
	 *
	 * @param fieldName
	 *            The name of the field
	 * @param fieldValue
	 *            The value to set the field
	 * @throws PersistenceException
	 *             If the given field does not exist in this object or the
	 *             value supplied is not allowed for this field
	 */
	public synchronized void setField(String fieldName, Object fieldValue) throws PersistenceException
	{
		Object useValue = fieldValue;

		SuperString.assertNotBlank(fieldName, "Field name may not be blank");
		assertField(fieldName);

		if (getHelper() != null)
		{
			getHelper().beforeSetField(fieldName, getField(fieldName), useValue);
		}

		if (getStatus() == Persistent.CURRENT)
		{
			setStatus(Persistent.MODIFIED);
		}

		/*
		 * if it's not a text field and it has newlines or returns embedded,
		 * take 'em out
		 */
		if (useValue != null)
		{
			String thisType = myMetaData.getType(fieldName);

			if (thisType.equalsIgnoreCase("varchar") || thisType.equalsIgnoreCase("char"))
			{
				useValue = SuperString.noNewLine(useValue.toString());
			} /* if not a text field */
			/* Check for returns or newlines in key fields - remove if found */
			if (myMetaData.getType(fieldName).equalsIgnoreCase("text") && myMetaData.isKeyField(fieldName))
			{
				useValue = SuperString.noNewLine(useValue.toString());
			}

			//Used to help mesh boolean values with checkboxes in html
			//fields
			if (myMetaData.getType(fieldName).equalsIgnoreCase("boolean"))
			{
				if (useValue.toString().equalsIgnoreCase("Y"))
				{
					useValue = "true";
				}
				else if (useValue.toString().equalsIgnoreCase("N"))
				{
					useValue = "false";
				}
			}

			/*
			 * If it's a date, and the value was an empty string, use null
			 * instead
			 */
			if (isDateType(myMetaData.getType(fieldName)))
			{
				if (useValue.toString().trim().equals(""))
				{
					useValue = null;
				}
			}
		} /* if field was not null */
		setFieldData(fieldName, useValue);

		if (getHelper() != null)
		{
			getHelper().afterSetField(fieldName, getField(fieldName), useValue);
		}
	} /* setField(String, String) */

	protected Helper getHelper() throws PersistenceException
	{
		if (myHelper == null)
		{
			if ((getMetaData().getClassName() != null) && (getMetaData().getClassName().equals("")))
			{
				if (getBean() instanceof Helper)
				{
					return (Helper) getBean();
				}
			}

			if (SuperString.notNull(myMetaData.getHelperClassName()).equals(""))
			{
				return null;
			}

			/* create the class and return it */
			try
			{
				Class c = Class.forName(myMetaData.getHelperClassName());

				myHelper = (Helper) c.newInstance();
			}
			catch (Exception e)
			{
				throw new PersistenceException(e);
			}
		}

		return myHelper;
	}

	/**
	 * If this persistent has a "class" defined for it populate it with the
	 * persistent data, then return it to the caller.
	 */
	public Object getBean() throws PersistenceException
	{
		final boolean debugging = log.isDebugEnabled();

		/* If the bean has not already been created, do so now */
		if (myBean == null)
		{
			if (debugging)
			{
				log.debug("No bean present, creating bean");
			}

			createBean();
		}

		if (myBean == null)
		{
			throw new PersistenceException("No bean class defined for " + getName());
		}

		String oneFieldName = null;

		for (Iterator i = myMetaData.getFieldNames().iterator(); i.hasNext();)
		{
			oneFieldName = (String) i.next();

			if (debugging)
			{
				log.debug("Setting Bean key/value: " + oneFieldName + '/' + getField(oneFieldName));
			}

			setBeanField(oneFieldName, getField(oneFieldName));
		}

		return myBean;
	}

	/**
	 * Get the value of a field in this object
	 *
	 * @param fieldname
	 *            Name of the field to fetch
	 * @return The value of the given field as a string - if the field is null,
	 *         an empty string is returned.
	 * @throws PersistenceException
	 *             If there is no such field or it's value cannot be accessed
	 */
	public synchronized Object getField(String fieldName) throws PersistenceException
	{
		assert fieldName != null;

		assertField(fieldName);

		Object returnValue = getFieldData(fieldName);

		return returnValue;
	} /* getField(String) */

	/**
	 * @see de.iritgo.aktera.persist.Persistent#setBypassAuthorizationManager(de.iritgo.aktera.authorization.AuthorizationManager)
	 */
	public void setBypassAuthorizationManager(AuthorizationManager bypassAm) throws PersistenceException
	{
		if (myMetaData.isAuthManagerBypassAllowed())
		{
			myBypassAuthManager = bypassAm;
		}
		else
		{
			throw new PersistenceException("Bypass of AuthorizationManager not allowed for " + getName());
		}
	}

	/**
	 * A field may have a list of valid values specified for it through three
	 * different methods: i. through static validvalue declarations in the
	 * schema XML file, ex:
	 * <field db-name="UseSortOrder" descrip="Use Sort Order?" length="1"
	 * name="UseSortOrder" null-allowed="false" type="char"> <valid-values>
	 * <valid-value value="N" descrip="No"/>
	 * <valid-value value="Y" descrip="Yes"/></valid-values></field>
	 *
	 * ii. by specifying a lookup object, which will read all values from the
	 * object and use them as valid values, ex:
	 *
	 * I am not sure...Anyone using this?
	 *
	 * iii. by specifying a KeelList of valid values. The KeelList is part of
	 * the keel schema, and is stored in KeelListHeader and KeelListItems.
	 * These two tables can store an unlimited number of lists, grouped
	 * together by ListName. Fields defined in this manner must be integer
	 * types (or an equivalent numeric). This can be used automatically by
	 * using the following entry in the schema XML file:
	 *
	 * <field db-name="SOME_FIELD_NAME" descrip="Just a field"
	 * name="SOME_FIELD_NAME" multi-valued="true" null-allowed="false"
	 * type="integer">
	 * <list-valid-values name="SOME_LIST_NAME" /></field>
	 *
	 * @param fieldName
	 *            The name of the field for which a valid value Map is
	 *            requested
	 * @return A Map of ValidValue objects
	 * @throws PersistenceException
	 */
	public synchronized Map getValidValues(String fieldName) throws PersistenceException
	{
		if (! myMetaData.isMultiValued(fieldName))
		{
			throw new PersistenceException("Field '" + fieldName + "' in object '" + getName()
							+ "' is not specified as multi-valued, so you cannot "
							+ "call getValidValues for this field");
		}

		//First look for static values. These take precedence.
		Map vvMap = myMetaData.getStaticValidValues(fieldName);

		if (vvMap.size() > 0)
		{
			return vvMap;
		}

		//Now look for ListValidValues (from the database tables).
		if (myMetaData.hasListValidValues(fieldName))
		{
			vvMap = myMetaData.getListValidValues(fieldName);

			if (vvMap.size() > 0)
			{
				return vvMap;
			}
		}

		//finally we try the lookup method.
		String lookupObjName = myMetaData.getLookupObject(fieldName);

		if (log.isDebugEnabled())
		{
			log.debug("Lookup is " + lookupObjName);
		}

		if (SuperString.notNull(lookupObjName).equals(""))
		{
			//return an empty map.
			return (Map) new HashMap();
		}

		//Ok, look for the lookup.
		Persistent lookupObj = getFactory().create(myMetaData.getSchemaName() + "." + lookupObjName);

		//Added by Santanu Dutt to build validValues Map from lookup object
		String lookUpField = myMetaData.getLookupField(fieldName);
		String lookUpFieldDisplay = myMetaData.getLookupFieldDisplay(fieldName);

		List lookups = lookupObj.query();

		vvMap = new HashMap();

		for (Iterator i = lookups.iterator(); i.hasNext();)
		{
			Persistent oneLookUp = (Persistent) i.next();

			vvMap.put(oneLookUp.getFieldString(lookUpField), oneLookUp.getFieldString(lookUpFieldDisplay));
		}

		return vvMap;
	} /* getValidValues(String) */

	/**
	 * Get the status of this record.
	 *
	 * @return <p>
	 *         NEW: Database object just created, not read from database
	 *         </p>
	 *
	 * <p>
	 * CURRENT: Database object in sync with database (just read, add or
	 * updated)
	 * </p>
	 * <p>
	 * MODIFIED: Database object has had changed made since it was last read,
	 * added, or updated
	 * </p>
	 *
	 * <p>
	 * DELETED: Record has been removed from the database
	 * </p>
	 *
	 * @return String
	 */
	public int getStatus()
	{
		return currentStatus;
	} /* getStatus() */

	public String getFieldString(String fieldName) throws PersistenceException
	{
		Object o = getField(fieldName);

		if (o == null)
		{
			return null;
		}

		String returnString = null;
		int typeToUse = JDBCDatabaseType.stringToType(myMetaData.getType(fieldName));

		if (Types.CLOB == typeToUse)
		{
			try
			{
				Clob fieldClob = (Clob) o;
				long myLong = 1;
				int len = new Long(fieldClob.length()).intValue();

				returnString = fieldClob.getSubString(myLong, len);
			}
			catch (SQLException sqe)
			{
				returnString = "CLOB field unavailable from database";
			}
		}
		else
		{
			returnString = getField(fieldName).toString();
		}

		return returnString;
	}

	/**
	 * Return the value of a field as a Date object
	 *
	 * @param fieldName
	 *            The field to be retrieved
	 * @return The java.sql.Date object equivilant to this field's value
	 * @throws PersistenceException
	 *             If the field does not exist or it's value is not a date or
	 *             cannot be converted to a date
	 */
	public Date getFieldDate(String fieldName) throws PersistenceException
	{
		assertField(fieldName);

		Object o = getFieldData(fieldName);

		if (o == null)
		{
			return null;
		}

		if (o instanceof java.sql.Date)
		{
			return (java.sql.Date) o;
		}
		else if (o instanceof java.util.Date)
		{
			java.util.Date myDate = (java.util.Date) o;

			return new Date(myDate.getTime());
		}
		else if (o instanceof java.sql.Timestamp)
		{
			return (java.sql.Date) o;
		}

		/** If it's not one of the above types, try to get it from the string */
		String strVal = o.toString();

		if (strVal.equals(""))
		{
			return null;
		}

		try
		{
			//DateFormat df = DateFormat.getDateTimeInstance();
			//changed by Adam...this was not working for timestamps...
			String type = myMetaData.getType(fieldName);
			String format = "yyyy-MM-dd hh:mm:ss";

			if (type.equalsIgnoreCase("date"))
			{
				format = "yyyy-MM-dd";
			}
			else if (type.equalsIgnoreCase("time"))
			{
				format = "hh:mm:ss";
			}

			SimpleDateFormat formatter = new SimpleDateFormat(format);
			java.util.Date myDate = formatter.parse(o.toString());

			return new Date(myDate.getTime());
		}
		catch (Exception de)
		{
			throw new PersistenceException("Could not extract a date value from " + o.getClass().getName() + " '"
							+ o.toString() + "' for field '" + fieldName + "'", de);
		}
	} /* getFieldDate(String) */

	/**
	 * Boolean typesafe getField
	 */
	public boolean getFieldBoolean(String fieldName) throws PersistenceException
	{
		if (! myMetaData.hasField(fieldName))
		{
			throw new PersistenceException("(" + getName() + ") No such field as " + fieldName);
		}

		Object o = getFieldData(fieldName);

		if (o == null)
		{
			return false;
		}

		if (o instanceof Boolean)
		{
			Boolean b = (Boolean) o;

			return b.booleanValue();
		}

		String strVal = o.toString();

		return SuperString.toBoolean(strVal);
	}

	/**
	 * @see de.iritgo.aktera.authorization.Securable#getAuthorizationManager()
	 */
	public AuthorizationManager getAuthorizationManager()
	{
		return myAuthManager;
	}

	/**
	 * Do very basic data-type validation on field values
	 *
	 * @param fieldName
	 *            Name of the field to validate
	 * @throws PersistenceException
	 *             If the field is not of the correct type, or a value which
	 *             can be converted to the correct type.
	 */
	protected void validateType(String fieldName) throws PersistenceException
	{
		int typeToUse = JDBCDatabaseType.stringToType(myMetaData.getType(fieldName));

		String value = getFieldString(fieldName);

		switch (typeToUse)
		{
			case java.sql.Types.BIGINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.TINYINT:

				if (! SuperString.isNumber(value))
				{
					throw new PersistenceException("Value '" + value + "' is not a number");
				}

				break;

			case java.sql.Types.BIT:
			case java.sql.Types.BOOLEAN:

				try
				{
					SuperString.assertBoolean(value, "Value '" + value + "' is not boolean");
				}
				catch (IllegalArgumentException ie)
				{
					throw new PersistenceException(ie.getMessage());
				}

				break;

			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:

				try
				{
					//                     SuperString ss = new SuperString(value);
					SuperString ss = new SuperString(getFieldDate(fieldName).toString());

					ss.toDate();
				}
				catch (IllegalArgumentException ie)
				{
					throw new PersistenceException(ie.getMessage());
				}

				break;

			case java.sql.Types.DECIMAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.REAL:

				if (! SuperString.isRealNumber(value))
				{
					throw new PersistenceException("Value '" + value + "' is not a number");
				}

				break;

			default:

				/* Default case is one of the "String" types, so any value */
				/* is acceptable */
				break;
		}
	}
} /* DefaultPersistentBase */
