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

package de.iritgo.aktera.persist.base;


import de.iritgo.aktera.core.config.KeelConfigurationUtil;
import de.iritgo.aktera.persist.DatabaseType;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.base.AbstractPersistentMetaData.Index;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import java.io.BufferedReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * A DatabaseType provides information and methods that allow persistent objects to talk to a specific kind
 * of database. This type is for "generic" JDBC Relational Databases, such as Hypersonic, MySQL, etc.
 *
 * quikdraw: Changed all private variables to protected so subclasses (in this
 * case OracleDatabaseType) could use/see them. Also changed supportsTransactions type from
 * String to boolean. Changed isSubSetSupported() to test against DatabaseType.SUBSET_NOT_SUPPORTED
 * instead of the constant "0". Changed getDistinctKeyWord() to return uniqueRowKeyword
 * instead of the literal "DISTINCT", meaning uniqueRowKeyword was never used.
 */
public class JDBCDatabaseType implements DatabaseType, LogEnabled, Configurable, Poolable
{
	protected static Logger log = null;

	/*
	 * SQL keyword that removes duplicate rows int a query - may be specific to this
	 * database engine implementation
	 */
	protected static String uniqueRowKeyword = "DISTINCT";

	/** Driver object name to use for JDBC connection */
	protected String dbDriver = new String ("");

	/** URL to use when connecting via the given driver */
	protected String dbURL = new String ("");

	/** Format to use for the connect statement */
	protected String dbConnectFormat = new String ("");

	protected int subsetPosition = DatabaseType.SUBSET_NOT_SUPPORTED;

	protected String subsetSyntax = null;

	protected boolean supportsTransactions = false;

	/**
	 * Data type mappings indexed by the java.sql.Types type as a string
	 */
	protected Map mapByType = new HashMap ();

	protected Configuration myConfig = null;

	protected boolean configured = false;

	protected boolean typesMapped = false;

	/**
	 * This method returns "true" by default, but specific database type implementations
	 * can return "false" if needed. It controls whether or not the "null" keyword
	 * is used after fields that *can* contain null. "not null" is always used for fields
	 * that cannot, but some databases allow/require "null" after other fields - some
	 * (notably mysql) don't allow it.
	 */
	protected boolean useExplicitNull ()
	{
		return true;
	}

	/**
	 * Given an object, return the database type mapped for this database when storing objects
	 * of this type. Return null if no type is mapped at all.
	 */
	public String getTypeFor (Object o)
	{
		return null;
	}

	public String getDBType (String type) throws PersistenceException
	{
		TypeMapEntry tm = (TypeMapEntry) mapByType.get (type.toUpperCase ());

		if (tm == null)
		{
			String t = "Type '" + type + "' is not mapped to any valid type";

			log.error (t);
			log.error ("Type map is: -----------------");

			String oneTypeString = null;

			log
							.error ("\tDB Type\tSQL Type\tPrecision\tPrefix\tSuffix\tCreateParams\tNullable\tCaseSensitive\tSearchable\tUnsigned\tFixedPrecision\tAutoIncrement\tLocalName\tMinScale\tMaxScale\tNumPrecRadix");

			for (Iterator i = mapByType.keySet ().iterator (); i.hasNext ();)
			{
				oneTypeString = (String) i.next ();
				tm = (TypeMapEntry) mapByType.get (oneTypeString);
				log.error (oneTypeString + ":" + tm.getTypeName () + "\t" + typeToString (tm.getDataType ()) + "\t"
								+ tm.getPrecision () + "\t" + tm.getLiteralPrefix () + "\t" + tm.getLiteralSuffix ()
								+ "\t" + tm.getCreateParams () + "\t" + tm.getNullable () + "\t"
								+ tm.getCaseSensitive () + "\t" + tm.getSearchable () + "\t" + tm.getUnsigned () + "\t"
								+ tm.getFixedPrecision () + "\t" + tm.getAutoIncrement () + "\t"
								+ tm.getLocalTypeName () + "\t" + tm.getMinScale () + "\t" + tm.getMaxScale () + "\t"
								+ tm.getNumPrecRadix ());
			}

			log.error ("----------------");
			throw new PersistenceException ("Type '" + type + "' is not mapped to any valid type");
		}

		return tm.getTypeName ();
	}

	/**
	 * Send back the name of the JDBC driver class being used to access
	 * the database
	 *
	 * @return    String The name of the driver class being used
	 *     to connect to the database.
	 */
	public String getDriverClass ()
	{
		return dbDriver;
	}

	public boolean supportsTransactions ()
	{
		return supportsTransactions;
	} // supportsTransactions

	/**
	 *
	 */
	public int getSubSetPosition ()
	{
		return subsetPosition;
	}

	/**
	 */
	void setSubSetPosition (int pos) throws PersistenceException
	{
		subsetPosition = pos;
	}

	/**
	 *
	 */
	public synchronized String getSubSetSyntax ()
	{
		return subsetSyntax;
	}

	/**
	 * Attempt to return a mapping of java types to actual type names
	 * used by this database.
	 *
	 * @return
	 */
	public Map getTypeMap ()
	{
		return mapByType;
	}

	/* getTypeMap() */

	/**
	 * Return a vector of wild card characters for this database type.
	 * This can be used to determine if the search criteria supplied by a
	 * user has wild-card characters in it or is an exact match
	 *
	 * @return    A list of the wild-card characters used by
	 *            this database connection
	 */
	public Set getWildCards ()
	{
		HashSet newChars = new HashSet (4);

		newChars.add (new String ("%"));
		newChars.add (new String ("_"));
		newChars.add (new String ("["));
		newChars.add (new String ("]"));

		return newChars;
	}

	public void enableLogging (Logger newLog)
	{
		log = newLog;
	}

	public boolean allowTextQueries ()
	{
		return true;
	}

	/**
	 * Does a given field value denote a range?
	 *
	 * @param fieldValue
	 * @return The "range" string if the value starts with a range indicator, null if not
	 */
	public String denotesRange (String fieldValue)
	{
		String returnValue = null;

		if (fieldValue.startsWith ("between "))
		{
			returnValue = "between ";
		}
		else if (fieldValue.startsWith ("BETWEEN "))
		{
			returnValue = "BETWEEN ";
		}
		else if (fieldValue.startsWith ("<> "))
		{
			returnValue = "<> ";
		}
		else if (fieldValue.startsWith ("< "))
		{
			returnValue = "< ";
		}
		else if (fieldValue.startsWith ("> "))
		{
			returnValue = "> ";
		}
		else if (fieldValue.startsWith ("<>"))
		{
			returnValue = "<>";
		}
		else if (fieldValue.startsWith ("<"))
		{
			returnValue = "<";
		}
		else if (fieldValue.startsWith (">"))
		{
			returnValue = ">";
		}

		return returnValue;
	} /* denotesRange(String) */

	/**
	 *
	 * @param theType
	 * @return
	 */
	public boolean isNumericType (String theType)
	{
		boolean returnValue = false;

		if (theType.equalsIgnoreCase ("numeric") || theType.equalsIgnoreCase ("int")
						|| theType.equalsIgnoreCase ("integer") || theType.equalsIgnoreCase ("long")
						|| theType.equalsIgnoreCase ("double") || theType.equalsIgnoreCase ("float")
						|| theType.equalsIgnoreCase ("money") || theType.equalsIgnoreCase ("decimal"))
		{
			returnValue = true;
		}

		return returnValue;
	} /* isNumericType(String) */

	/**
	 *
	 *
	 * @param theType
	 * @return
	 */
	public boolean isQuotedType (String theType)
	{
		boolean returnValue = false;

		if (theType.equalsIgnoreCase ("char") || theType.equalsIgnoreCase ("varchar")
						|| theType.equalsIgnoreCase ("varchar2") || theType.equalsIgnoreCase ("longvarchar")
						|| theType.equalsIgnoreCase ("text") || isDateType (theType))
		{
			returnValue = true;
		}

		return returnValue;
	} /* isQuotedType(String) */

	public Set getReservedWords ()
	{
		return new HashSet ();
	}

	public String getDistinctKeyWord ()
	{
		return uniqueRowKeyword;
	}

	public String getDateUpdateFormat ()
	{
		return null;
	}

	public String getDateTimeUpdateFormat ()
	{
		return null;
	}

	public String getTimeUpdateFormat ()
	{
		return null;
	}

	public String getDateUpdateFunction ()
	{
		return null;
	}

	public String getDateTimeUpdateFunction ()
	{
		return null;
	}

	public String getTimeUpdateFunction ()
	{
		return null;
	}

	public String getDateSelectFormat ()
	{
		return null;
	}

	public String getDateSelectFunction ()
	{
		return null;
	}

	public String getDateTimeSelectFormat ()
	{
		return null;
	}

	public String getDateTimeSelectFunction ()
	{
		return null;
	}

	public String getTimeSelectFormat ()
	{
		return null;
	}

	public String getTimeSelectFunction ()
	{
		return null;
	}

	public boolean isDateType (String s)
	{
		boolean returnValue = false;

		if (s.equalsIgnoreCase ("DATE") || s.equalsIgnoreCase ("DATETIME") || s.equalsIgnoreCase ("TIME")
						|| s.equalsIgnoreCase ("TIMESTAMP"))
		{
			returnValue = true;
		}

		return returnValue;
	}

	public void configure (Configuration conf) throws ConfigurationException
	{
		if (configured)
		{
			return;
		}

		configured = true;
		myConfig = conf;

		Configuration[] children = conf.getChildren ();
		Configuration oneChild = null;

		for (int i = 0; i < children.length; i++)
		{
			oneChild = children[i];

			/* Type mappings */
			if (oneChild.getName ().equals ("type"))
			{
				TypeMapEntry oneType = new TypeMapEntry ();

				oneType.setTypeName (oneChild.getAttribute ("to"));
				oneType.setDataType (stringToType (oneChild.getAttribute ("from")));
				oneType.setPrecision (oneChild.getAttributeAsInteger ("precision", 0));
				oneType.setLiteralPrefix (oneChild.getAttribute ("prefix", ""));
				oneType.setLiteralSuffix (oneChild.getAttribute ("suffix", ""));
				oneType.setCreateParams (oneChild.getAttribute ("create-params", ""));

				String nullable = oneChild.getAttribute ("nullable", "");

				/** Explanation of the below strangeness: believe it or not, the valid values for the "nullable" attribute, which
				 * is defined by JDBC as a "short", are actually "int"s. We do the below in order to turn the int into a short... :-)
				 */
				if (nullable.equals (""))
				{
					oneType.setNullable (new Short (Integer.toString (java.sql.DatabaseMetaData.typeNullableUnknown))
									.shortValue ());
				}
				else
				{
					if (SuperString.toBoolean (nullable))
					{
						oneType.setNullable (new Short (Integer.toString (java.sql.DatabaseMetaData.typeNullable))
										.shortValue ());
					}
					else
					{
						oneType.setNullable (new Short (Integer.toString (java.sql.DatabaseMetaData.typeNoNulls))
										.shortValue ());
					}
				}

				oneType.setCaseSensitive (oneChild.getAttributeAsBoolean ("case-sensitive", false));

				if (oneChild.getAttributeAsBoolean ("searchable", true))
				{
					oneType.setSearchable (new Short (Integer.toString (java.sql.DatabaseMetaData.typeSearchable))
									.shortValue ());
				}
				else
				{
					oneType.setSearchable (new Short (Integer.toString (java.sql.DatabaseMetaData.typePredNone))
									.shortValue ());
				}

				oneType.setUnsigned (oneChild.getAttributeAsBoolean ("unsigned", false));
				oneType.setFixedPrecision (oneChild.getAttributeAsBoolean ("fixed-precision", false));
				oneType.setAutoIncrement (oneChild.getAttributeAsBoolean ("auto-increment", false));

				oneType.setLocalTypeName (oneChild.getAttribute ("local-name", ""));
				oneType.setMinScale (new Short (oneChild.getAttribute ("min-scale", "0")).shortValue ());
				oneType.setMaxScale (new Short (oneChild.getAttribute ("max-scale", "0")).shortValue ());
				oneType.setNumPrecRadix (oneChild.getAttributeAsInteger ("num-prec-radix", 0));
				mapByType.put (typeToString (oneType.getDataType ()), oneType);
			}
		}
	}

	public boolean isSubSetSupported ()
	{
		boolean returnValue = false;

		if (getSubSetPosition () != DatabaseType.SUBSET_NOT_SUPPORTED)
		{
			returnValue = false;
		}

		return returnValue;
	}

	public void createTable (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException
	{
		if (tableExists (pmd.getTableName (), dataSource))
		{
			// TODO: Read existing table definition, report on differences
			log.info ("Table " + pmd.getTableName () + " already exists. No action taken");

			return;
		}

		Connection myConnection = null;
		StringBuffer createStatement = new StringBuffer ("");

		try
		{
			/* Create the table itself
			 */
			createStatement = getCreateTableStatement (pmd, dataSource);
			myConnection = dataSource.getConnection ();
			log.info ("Running Create:" + createStatement.toString ());

			Statement s = myConnection.createStatement ();

			s.executeUpdate (createStatement.toString ());

			/* If the ALTER TABLE syntax is supported, then
			 * specify columns
			 */
			if (isAlterKeySupported ())
			{
				createKeys (pmd, myConnection);
			}

			/* Create indexes as necessary
			 */
			createIndices (pmd, myConnection, true);
		}
		catch (Exception e)
		{
			log.error ("Error building create statement", e);
			throw new PersistenceException ("Unable to build/execute create statement '" + createStatement.toString ()
							+ "' for table '" + pmd.getTableName () + "'", e);
		}
		finally
		{
			try
			{
				if (myConnection != null)
				{
					myConnection.close ();
				}
			}
			catch (SQLException se)
			{
				throw new PersistenceException ("Unable to close/release connection", se);
			}
		}
	}

	/**
	 * Creates indices that are loaded into the PersistentMetaData. The "duringTableCreation"
	 * boolean flag denotes whether this method is being called from a method creating tables.
	 * Each index knows whether it should be created during table creation.
	 *
	 * @param pmd - the PersistentMetaData that has the index information.
	 * @param myConnection - the connection to the data source containing the persistent
	 * @param duringTableCreation - boolean denoting if table creation is taking place.
	 *
	 * @throws PersistenceException
	 */
	protected void createIndices (PersistentMetaData pmd, Connection myConnection, boolean duringTableCreation)
		throws PersistenceException
	{
		Statement stmt = null;

		try
		{
			stmt = myConnection.createStatement ();

			Set indicies = pmd.getIndicies ();

			for (Iterator ii = indicies.iterator (); ii.hasNext ();)
			{
				Index index = (Index) ii.next ();

				if (! duringTableCreation || index.createWithTable ())
				{
					String oneIndexName = index.getIndexName ();
					String createIndex = null;

					try
					{
						createIndex = constructIndexSQL (pmd, index, true);
						log.debug ("Creating index " + index.getIndexName () + " with sql syntax: " + createIndex);
						stmt.executeUpdate (createIndex);
					}
					catch (SQLException s)
					{
						log.error ("Could not create index " + oneIndexName + " on table " + pmd.getTableName () + ".");
						log.error ("Error creating index:", s);
					}
				}
			}

			/* for each index */
		}
		catch (SQLException s)
		{
			throw new PersistenceException (s);
		}
		finally
		{
			try
			{
				stmt.close ();
			}
			catch (SQLException s)
			{
				throw new PersistenceException ("Unable to close statement when creating index", s);
			}
		}
	}

	/**
	 * Creates indices that are loaded into the PersistentMetaData belonging to the supplied
	 * data source.
	 *
	 * @param pmd - the PersistentMetaData that has the index information.
	 * @param dataSource - data source where persistent resides.
	 *
	 * @throws PersistenceException
	 */
	public void createIndices (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException
	{
		Connection myConnection = null;

		try
		{
			myConnection = dataSource.getConnection ();
			createIndices (pmd, myConnection, false);
		}
		catch (SQLException s)
		{
			throw new PersistenceException (s);
		}
		finally
		{
			try
			{
				myConnection.close ();
			}
			catch (SQLException s)
			{
				throw new PersistenceException ("Unable to close connection when creating indices", s);
			}
		}
	}

	/**
	 * Drops indices that are loaded into the PersistentMetaData belonging to the supplied
	 * data source.
	 *
	 * @param pmd - the PersistentMetaData that has the index information.
	 * @param dataSource - data source where persistent resides.
	 *
	 * @throws PersistenceException
	 */
	public void dropIndices (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException
	{
		Connection myConnection = null;
		Statement stmt = null;

		try
		{
			myConnection = dataSource.getConnection ();
			stmt = myConnection.createStatement ();

			Set indicies = pmd.getIndicies ();

			for (Iterator ii = indicies.iterator (); ii.hasNext ();)
			{
				Index index = (Index) ii.next ();
				String oneIndexName = index.getIndexName ();
				String dropIndex = null;

				try
				{
					dropIndex = constructIndexSQL (pmd, index, false);
					log.debug ("Dropping index " + index.getIndexName () + " with sql syntax: " + dropIndex);
					stmt.executeUpdate (dropIndex);
				}
				catch (SQLException s)
				{
					log.error ("Could not drop index " + oneIndexName + " on table " + pmd.getTableName () + ".");
					log.error ("SQL syntax = " + dropIndex);
					log.error ("Error: " + s.getMessage ());
				}
			}

			/* for each index */
		}
		catch (SQLException s)
		{
			throw new PersistenceException (s);
		}
		finally
		{
			try
			{
				stmt.close ();
			}
			catch (SQLException s)
			{
				//--- quikdraw: Should this be ignored?
			}

			try
			{
				myConnection.close ();
			}
			catch (SQLException s)
			{
				//--- quikdraw: Should this be ignored?
			}
		}
	}

	protected void createKeys (PersistentMetaData pmd, Connection myConnection)
		throws SQLException, PersistenceException
	{
		StringBuffer pkStatement = new StringBuffer ("alter table " + pmd.getTableName () + " add primary key(");

		boolean addComma = false;
		short keyCount = 0;
		short keySkipCount = 0;

		Iterator i = pmd.getKeyFieldNames ().iterator ();

		while (i.hasNext ())
		{
			String oneKey = (String) i.next ();

			/* If we are using a db-based sequence, instead of the ids */
			/* table, we skip auto-incremented fields */
			boolean skipField = false;

			if (pmd.isAutoIncremented (oneKey))
			{
				if (pmd.getIdGenerator (oneKey) == null)
				{
					/* then we're using a sequence - skip this one field */
					skipField = true;
				}
			}

			if (! skipField)
			{
				if (addComma)
				{
					pkStatement.append (", ");
				}

				pkStatement.append (pmd.getDBFieldName (oneKey));
				addComma = true;
				keyCount++;
			}
			else
			{
				keySkipCount++;
			}
		} // while

		pkStatement.append (")");

		if (keyCount > 0)
		{
			if (log.isDebugEnabled ())
			{
				log.debug ("Executing:" + pkStatement.toString ());
			}

			Statement ss = myConnection.createStatement ();

			ss.executeUpdate (pkStatement.toString ());
		}
		else
		{
			if (keySkipCount == 0)
			{
				log.warn ("No primary key on table '" + pmd.getTableName () + "'");
			}
		}
	}

	protected StringBuffer getCreateTableStatement (PersistentMetaData pmd, DataSourceComponent dataSource)
		throws PersistenceException
	{
		String fieldName = null;

		boolean addComma = false;
		String identityFieldName = null;

		StringBuffer createStatement = new StringBuffer ("CREATE TABLE ");

		createStatement.append (pmd.getTableName () + " (");

		StringBuffer oneType = new StringBuffer ("");

		for (Iterator lf = pmd.getFieldNames ().iterator (); lf.hasNext ();)
		{
			fieldName = (String) lf.next ();

			if (addComma)
			{
				createStatement.append (", ");
			}

			if (pmd.isAutoIncremented (fieldName))
			{
				if (pmd.isKeyField (fieldName) && (identityFieldName == null) && isIdentitySupported ()
								&& (pmd.getIdGenerator (fieldName) == null))
				{
					identityFieldName = fieldName;
					createStatement.append (pmd.getDBFieldName (fieldName) + " ");
					createStatement.append (getCreateIdentitySyntax ());
				}
				else if (isSequenceSupported ())
				{
					createStatement.append (getCreateSequenceSyntax (fieldName));
				}
				else
				{
					createStatement.append (pmd.getDBFieldName (fieldName) + " ");
					createStatement.append (pmd.getDBType (fieldName));
					createStatement.append (" not null");
				}
			}
			else
			{
				createStatement.append (pmd.getDBFieldName (fieldName) + " ");
				oneType = new StringBuffer ("");
				oneType.append (pmd.getDBType (fieldName));

				if (pmd.getLength (fieldName) != 0)
				{
					oneType.append ("(" + pmd.getLength (fieldName));

					if (pmd.getPrecision (fieldName) > 0)
					{
						oneType.append (", " + pmd.getPrecision (fieldName));
					}

					oneType.append (")");
				}

				createStatement.append (oneType.toString ());

				if (! pmd.allowsNull (fieldName))
				{
					createStatement.append (" not null");
				}
				else
				{
					if (useExplicitNull ())
					{
						createStatement.append (" null");
					}

					//Added for compatability w/Sybase...sql-92 standard?
				}
			}

			addComma = true;
		} // for

		/* for each field */
		/* Keys are normally added using the alter table, unless
		 * the particular DBType in use doesn't support that syntax.
		 * In that case, the key is specified as part of the CREATE TABLE syntax
		 */
		if (! isAlterKeySupported ())
		{
			short keyCount = 0;

			addComma = false;

			for (Iterator kf = pmd.getKeyFieldNames ().iterator (); kf.hasNext ();)
			{
				fieldName = (String) kf.next ();

				if (! (pmd.isAutoIncremented (fieldName) && isIdentitySupported ()))
				{
					keyCount++;

					if (addComma)
					{
						createStatement.append (",");
					}
					else
					{
						createStatement.append (", PRIMARY KEY (");
					}

					createStatement.append (pmd.getDBFieldName (fieldName));
					addComma = true;
				}
			}

			if (keyCount != 0)
			{
				createStatement.append (")");
			}

			if (pmd.getKeyFieldNames ().size () == 0)
			{
				log.warn ("No primary key on table '" + pmd.getTableName () + "'");
			}
		}

		createStatement.append (")");

		return createStatement;
	}

	/**
	 * Initialize the default type map from this datasource.
	 */
	public void setDataSource (DataSourceComponent ds) throws PersistenceException
	{
		if (typesMapped)
		{
			return;
		}

		Connection myConnection = null;
		Set dups = new HashSet ();

		try
		{
			myConnection = ds.getConnection ();

			if (myConnection == null)
			{
				throw new PersistenceException ("Got a null connection from data source " + ds.toString ());
			}

			DatabaseMetaData dm = myConnection.getMetaData ();

			if (dm.supportsTransactions ())
			{
				supportsTransactions = true;
			}
			else
			{
				supportsTransactions = false;
			}

			ResultSet rsx = dm.getTypeInfo ();

			while (rsx.next ())
			{
				TypeMapEntry oneType = new TypeMapEntry ();

				//--- quikdraw: Changed the resultSet extraction from indexes to
				//	column names as defined in the Sun JavaDoc for JDBC.
				oneType.setTypeName (rsx.getString ("TYPE_NAME"));
				oneType.setDataType (rsx.getShort ("DATA_TYPE"));

				try
				{
					oneType.setPrecision (rsx.getInt ("PRECISION"));
				}
				catch (Exception e)
				{
					log.warn ("Driver returned bad precision, setting to 0");
					oneType.setPrecision (0);
				}

				oneType.setLiteralPrefix (rsx.getString ("LITERAL_PREFIX"));
				oneType.setLiteralSuffix (rsx.getString ("LITERAL_SUFFIX"));
				oneType.setCreateParams (rsx.getString ("CREATE_PARAMS"));
				oneType.setNullable (rsx.getShort ("NULLABLE"));
				oneType.setCaseSensitive (rsx.getBoolean ("CASE_SENSITIVE"));
				oneType.setSearchable (rsx.getShort ("SEARCHABLE"));
				oneType.setUnsigned (rsx.getBoolean ("UNSIGNED_ATTRIBUTE"));
				oneType.setFixedPrecision (rsx.getBoolean ("FIXED_PREC_SCALE"));
				oneType.setAutoIncrement (rsx.getBoolean ("AUTO_INCREMENT"));
				oneType.setLocalTypeName (rsx.getString ("LOCAL_TYPE_NAME"));
				oneType.setMinScale (rsx.getShort ("MINIMUM_SCALE"));
				oneType.setMaxScale (rsx.getShort ("MAXIMUM_SCALE"));
				//--- quikdraw: This was index 16, but the JavaDoc for DatabaseMetaData
				//	states the index for the Radix is 18.
				oneType.setNumPrecRadix (rsx.getInt ("NUM_PREC_RADIX"));

				String typeAsString = typeToString (oneType.getDataType ());

				TypeMapEntry oldType = (TypeMapEntry) mapByType.get (typeAsString);

				if (oldType == null)
				{
					mapByType.put (typeAsString, oneType);
				}
				else
				{
					dups.add (typeAsString);
				}
			}
		}
		catch (SQLException se)
		{
			throw new PersistenceException (se);
		}
		finally
		{
			try
			{
				if (myConnection != null)
				{
					myConnection.close ();
				}
			}
			catch (SQLException se)
			{
				throw new PersistenceException (se);
			}
		}

		if (dups.size () > 0)
		{
			for (Iterator i = dups.iterator (); i.hasNext ();)
			{
				String typeAsString = (String) i.next ();

				log.debug ("Type '" + typeAsString + "' overridden by configuration or mapped twice");
			}
		}

		if (dups.size () > 0)
		{
			log.debug ("Configuration was:" + KeelConfigurationUtil.list (myConfig, "database type"));
		}
	}

	public static String typeToString (int sqlType)
	{
		String returnValue = null;

		switch (sqlType)
		{
			case java.sql.Types.ARRAY:
				returnValue = "ARRAY";

				break;

			case java.sql.Types.BIGINT:
				returnValue = "BIGINT";

				break;

			case java.sql.Types.BINARY:
				returnValue = "BINARY";

				break;

			case java.sql.Types.BIT:
				returnValue = "BIT";

				break;

			case java.sql.Types.BLOB:
				returnValue = "BLOB";

				break;

			case java.sql.Types.BOOLEAN:
				returnValue = "BOOLEAN";

				break;

			case java.sql.Types.CHAR:
				returnValue = "CHAR";

				break;

			case java.sql.Types.CLOB:
				returnValue = "CLOB";

				break;

			case java.sql.Types.DATALINK:
				returnValue = "DATALINK";

				break;

			case java.sql.Types.DATE:
				returnValue = "DATE";

				break;

			case java.sql.Types.DECIMAL:
				returnValue = "DECIMAL";

				break;

			case java.sql.Types.DISTINCT:
				returnValue = "DISTINCT";

				break;

			case java.sql.Types.DOUBLE:
				returnValue = "DOUBLE";

				break;

			case java.sql.Types.FLOAT:
				returnValue = "FLOAT";

				break;

			case java.sql.Types.INTEGER:
				returnValue = "INTEGER";

				break;

			case java.sql.Types.JAVA_OBJECT:
				returnValue = "JAVA_OBJECT";

				break;

			case java.sql.Types.LONGVARBINARY:
				returnValue = "LONGVARBINARY";

				break;

			case java.sql.Types.LONGVARCHAR:
				returnValue = "LONGVARCHAR";

				break;

			case java.sql.Types.NULL:
				returnValue = "NULL";

				break;

			case java.sql.Types.NUMERIC:
				returnValue = "NUMERIC";

				break;

			case java.sql.Types.OTHER:
				returnValue = "OTHER";

				break;

			case java.sql.Types.REAL:
				returnValue = "REAL";

				break;

			case java.sql.Types.REF:
				returnValue = "REF";

				break;

			case java.sql.Types.SMALLINT:
				returnValue = "SMALLINT";

				break;

			case java.sql.Types.STRUCT:
				returnValue = "STRUCT";

				break;

			case java.sql.Types.TIME:
				returnValue = "TIME";

				break;

			case java.sql.Types.TIMESTAMP:
				returnValue = "TIMESTAMP";

				break;

			case java.sql.Types.TINYINT:
				returnValue = "TINYINT";

				break;

			case java.sql.Types.VARBINARY:
				returnValue = "VARBINARY";

				break;

			case java.sql.Types.VARCHAR:
				returnValue = "VARCHAR";

				break;

			default:
				throw new IllegalArgumentException ("Unknown java.sql.Types type " + sqlType);
		} // switch

		return returnValue;
	}

	public static short stringToType (String stringType)
	{
		short returnValue = - 1;

		if (stringType.equalsIgnoreCase ("ARRAY"))
		{
			returnValue = java.sql.Types.ARRAY;
		}
		else if (stringType.equalsIgnoreCase ("BIGINT"))
		{
			returnValue = java.sql.Types.BIGINT;

			// Allow "long" to be used as a synonym for BIGINT
		}
		else if (stringType.equalsIgnoreCase ("LONG"))
		{
			returnValue = java.sql.Types.BIGINT;
		}
		else if (stringType.equalsIgnoreCase ("BINARY"))
		{
			returnValue = java.sql.Types.BINARY;
		}
		else if (stringType.equalsIgnoreCase ("BIT"))
		{
			returnValue = java.sql.Types.BIT;
		}
		else if (stringType.equalsIgnoreCase ("BLOB"))
		{
			returnValue = java.sql.Types.BLOB;
		}
		else if (stringType.equalsIgnoreCase ("BOOLEAN"))
		{
			returnValue = java.sql.Types.BOOLEAN;
		}
		else if (stringType.equalsIgnoreCase ("CHAR"))
		{
			returnValue = java.sql.Types.CHAR;
		}
		else if (stringType.equalsIgnoreCase ("CLOB"))
		{
			returnValue = java.sql.Types.CLOB;
		}
		else if (stringType.equalsIgnoreCase ("DATALINK"))
		{
			return java.sql.Types.DATALINK;
		}
		else if (stringType.equalsIgnoreCase ("DATE"))
		{
			returnValue = java.sql.Types.DATE;
		}
		else if (stringType.equalsIgnoreCase ("DECIMAL"))
		{
			returnValue = java.sql.Types.DECIMAL;
		}
		else if (stringType.equalsIgnoreCase ("DISTINCT"))
		{
			returnValue = java.sql.Types.DISTINCT;
		}
		else if (stringType.equalsIgnoreCase ("DOUBLE"))
		{
			returnValue = java.sql.Types.DOUBLE;
		}
		else if (stringType.equalsIgnoreCase ("FLOAT"))
		{
			returnValue = java.sql.Types.FLOAT;
		}
		else if (stringType.equalsIgnoreCase ("INTEGER"))
		{
			returnValue = java.sql.Types.INTEGER;

			// Allow "int" to be used as a synonym for INTEGER
		}
		else if (stringType.equalsIgnoreCase ("INT"))
		{
			returnValue = java.sql.Types.INTEGER;
		}
		else if (stringType.equalsIgnoreCase ("JAVA_OBJECT"))
		{
			returnValue = java.sql.Types.JAVA_OBJECT;
		}
		else if (stringType.equalsIgnoreCase ("LONGVARBINARY"))
		{
			returnValue = java.sql.Types.LONGVARBINARY;

			// Allow "text" to be used as a synonym for LONGVARCHAR
		}
		else if (stringType.equalsIgnoreCase ("TEXT"))
		{
			returnValue = java.sql.Types.LONGVARCHAR;
		}
		else if (stringType.equalsIgnoreCase ("LONGVARCHAR"))
		{
			returnValue = java.sql.Types.LONGVARCHAR;
		}
		else if (stringType.equalsIgnoreCase ("NULL"))
		{
			returnValue = java.sql.Types.NULL;
		}
		else if (stringType.equalsIgnoreCase ("NUMERIC"))
		{
			returnValue = java.sql.Types.NUMERIC;
		}
		else if (stringType.equalsIgnoreCase ("OTHER"))
		{
			returnValue = java.sql.Types.OTHER;
		}
		else if (stringType.equalsIgnoreCase ("REAL"))
		{
			returnValue = java.sql.Types.REAL;
		}
		else if (stringType.equalsIgnoreCase ("REF"))
		{
			returnValue = java.sql.Types.REF;
		}
		else if (stringType.equalsIgnoreCase ("SMALLINT"))
		{
			returnValue = java.sql.Types.SMALLINT;
		}
		else if (stringType.equalsIgnoreCase ("STRUCT"))
		{
			returnValue = java.sql.Types.STRUCT;
		}
		else if (stringType.equalsIgnoreCase ("TIME"))
		{
			returnValue = java.sql.Types.TIME;
		}
		else if (stringType.equalsIgnoreCase ("TIMESTAMP"))
		{
			returnValue = java.sql.Types.TIMESTAMP;
		}
		else if (stringType.equalsIgnoreCase ("TINYINT"))
		{
			returnValue = java.sql.Types.TINYINT;
		}
		else if (stringType.equalsIgnoreCase ("VARBINARY"))
		{
			returnValue = java.sql.Types.VARBINARY;
		}
		else if (stringType.equalsIgnoreCase ("VARCHAR"))
		{
			returnValue = java.sql.Types.VARCHAR;
		}
		else
		{
			throw new IllegalArgumentException ("Unknown type '" + stringType + "'");
		}

		return returnValue;
	}

	public boolean tableExists (String tableName, DataSourceComponent ds) throws PersistenceException
	{
		Connection myConnection = null;
		boolean returnValue = false;

		try
		{
			myConnection = ds.getConnection ();

			Statement s = myConnection.createStatement ();

			s.execute ("SELECT COUNT(*) FROM " + tableName);

			returnValue = true;
		}
		catch (SQLException se)
		{
			returnValue = false;
		}
		finally
		{
			if (myConnection != null)
			{
				try
				{
					myConnection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}

		return returnValue;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getBlobResult()
	 */
	public byte[] getBlobResult (String fieldName, ResultSet rs, int index) throws PersistenceException
	{
		byte[] bArray = null;

		try
		{
			bArray = rs.getBytes (index);
		}
		catch (java.sql.SQLException sqe)
		{
			throw new PersistenceException ("An error occured while trying to fetch a BLOB from field " + fieldName
							+ ", error: " + sqe.toString ());
		}

		return bArray;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getBlobResult()
	 */
	public List getClobResult (String fieldName, ResultSet rs, int index) throws PersistenceException
	{
		String oneLine = null;
		ArrayList allLines = null;

		//note: probably not the most efficient structure (ArrayList).
		//If we return the BufferedReader, will it die when the connection is closed?
		try
		{
			Clob clob = (Clob) rs.getBlob ("CLOBFIELD");
			BufferedReader clobData = new BufferedReader (clob.getCharacterStream ());

			while ((oneLine = clobData.readLine ()) != null)
			{
				allLines.add (oneLine);
			}
		}
		catch (java.sql.SQLException sqe)
		{
			throw new PersistenceException ("An error occured while trying to fetch a BLOB from field " + fieldName
							+ ", error: " + sqe.toString ());
		}
		catch (java.io.IOException ioe)
		{
			throw new PersistenceException ("An error occured while trying to fetch a BLOB from field " + fieldName
							+ ", error: " + ioe.toString ());
		}

		return allLines;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getCreateIdentitySyntax()
	 */
	public String getCreateIdentitySyntax () throws PersistenceException
	{
		throw new PersistenceException ("SQL Identity not supported");
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getCreateSequenceSyntax(String)
	 */
	public String getCreateSequenceSyntax (String sequenceName) throws PersistenceException
	{
		throw new PersistenceException ("SQL Sequence not supported");
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.persist.DatabaseType#getDropSequenceSyntax(String)
	 */
	public String getDropSequenceSyntax (String sequenceName) throws PersistenceException
	{
		throw new PersistenceException ("SQL Sequence not supported");
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getInsertIdentitySyntax()
	 */
	public String getInsertIdentitySyntax () throws PersistenceException
	{
		throw new PersistenceException ("SQL Identity not supported");
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getRetrieveIdentitySyntax()
	 */
	public String getRetrieveIdentitySyntax (PersistentMetaData pmd, String idFieldName) throws PersistenceException
	{
		throw new PersistenceException ("SQL Identity not supported");
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getRetrieveSequenceSyntax(String)
	 */
	public String getRetrieveSequenceSyntax (String sequenceName) throws PersistenceException
	{
		throw new PersistenceException ("SQL Sequence not supported");
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isIdentitySupported()
	 */
	public boolean isIdentitySupported ()
	{
		return false;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isSequenceSupported()
	 */
	public boolean isSequenceSupported ()
	{
		return false;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isAlterKeySupported()
	 */
	public boolean isAlterKeySupported ()
	{
		return true;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#skipIdentityInAlterKey()
	 */
	public boolean skipIdentityInAlterKey ()
	{
		return false;
	}

	/**
	 * This method constructs a sql statement that creates or drops an index depending
	 * on whether the create parameter is true or false. If the sql generated by this
	 * command does not run on a certain database, you must override the method in the
	 * appropriate subclass.
	 *
	 * @param index - the index that will be created or dropped.
	 * @param create - boolean denoting whether to generate the
	 *                "create index" or "drop index" sql statement.
	 * @return A sql statement that creates or drops an index.
	 * @throws PersistenceException
	 */
	protected String constructIndexSQL (PersistentMetaData pmd, Index index, boolean create)
		throws PersistenceException
	{
		String indexName = index.getIndexName ();
		String tableName = index.getTableName ();
		String fieldNames = index.getFieldNames ();

		if (indexName == null)
		{
			throw new PersistenceException ("Index_ConstructSQL_IllegalArgument");
		}

		if (tableName == null)
		{
			throw new PersistenceException ("Index_ConstructSQL_IllegalArgument");
		}

		if (fieldNames == null)
		{
			throw new PersistenceException ("Index_ConstructSQL_IllegalArgument");
		}

		StringBuffer statement = new StringBuffer (100);

		if (create)
		{
			statement.append ("CREATE ");

			if (index.isUnique () == true)
			{
				statement.append ("UNIQUE ");
			}
		}
		else
		{
			statement.append ("DROP ");
		}

		statement.append ("INDEX ");
		statement.append (indexName);
		statement.append (" ON ");
		statement.append (tableName);

		if (create)
		{
			statement.append ("(");

			StringBuffer dbFieldNames = new StringBuffer ();
			boolean firstTime = true;
			String oneFieldName = null;
			StringTokenizer stk = new StringTokenizer (fieldNames, ",");

			while (stk.hasMoreTokens ())
			{
				if (! firstTime)
				{
					dbFieldNames.append (",");
				}

				oneFieldName = stk.nextToken ();
				dbFieldNames.append (pmd.getDBFieldName (oneFieldName));
				firstTime = false;
			}

			statement.append (fieldNames);
			statement.append (")");
		}

		return statement.toString ();
	}

	/**
	 * Get the complete database type of a field including all modifiers
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName Field name.
	 * @return The database type.
	 */
	public String getCompleteDBType (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		StringBuffer sb = new StringBuffer ();

		if (pmd.isAutoIncremented (fieldName))
		{
			if (pmd.isKeyField (fieldName) && isIdentitySupported () && (pmd.getIdGenerator (fieldName) == null))
			{
				sb.append (getCreateIdentitySyntax ());
			}
			else if (isSequenceSupported ())
			{
				sb.append (getCreateSequenceSyntax (fieldName));
			}
			else
			{
				sb.append (pmd.getDBType (fieldName));
				sb.append (" NOT NULL");
			}
		}
		else
		{
			StringBuffer oneType = new StringBuffer ("");

			oneType = new StringBuffer ("");
			oneType.append (pmd.getDBType (fieldName));

			if (pmd.getLength (fieldName) != 0)
			{
				oneType.append ("(" + pmd.getLength (fieldName));

				if (pmd.getPrecision (fieldName) > 0)
				{
					oneType.append (", " + pmd.getPrecision (fieldName));
				}

				oneType.append (")");
			}

			sb.append (oneType.toString ());

			if (! pmd.allowsNull (fieldName))
			{
				sb.append (" NOT NULL");
			}
			else
			{
				if (useExplicitNull ())
				{
					sb.append (" NULL");
				}
			}
		}

		return sb.toString ();
	}

	/**
	 * Get the complete database type of a field including all modifiers
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName Field name.
	 * @return The database type.
	 */
	public String getDBTypeWithPrecision (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		StringBuffer sb = new StringBuffer ();

		if (pmd.isAutoIncremented (fieldName))
		{
			if (pmd.isKeyField (fieldName) && isIdentitySupported () && (pmd.getIdGenerator (fieldName) == null))
			{
				sb.append (getCreateIdentitySyntax ());
			}
			else if (isSequenceSupported ())
			{
				sb.append (getCreateSequenceSyntax (fieldName));
			}
			else
			{
				sb.append (pmd.getDBType (fieldName));
			}
		}
		else
		{
			StringBuffer oneType = new StringBuffer ("");

			oneType = new StringBuffer ("");
			oneType.append (pmd.getDBType (fieldName));

			if (pmd.getLength (fieldName) != 0)
			{
				oneType.append ("(" + pmd.getLength (fieldName));

				if (pmd.getPrecision (fieldName) > 0)
				{
					oneType.append (", " + pmd.getPrecision (fieldName));
				}

				oneType.append (")");
			}

			sb.append (oneType.toString ());
		}

		return sb.toString ();
	}

	/**
	 * Get the statement to rename a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 * @return The rename statement.
	 */
	public String getRenameFieldStatement (PersistentMetaData pmd, String oldFieldName, String newFieldName)
		throws PersistenceException
	{
		throw new PersistenceException ("[JDBCDatabaseType] No getRenameFieldStatement() method defined.");
	}

	/**
	 * Rename a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 */
	public void renameField (PersistentMetaData pmd, String oldFieldName, String newFieldName)
		throws PersistenceException
	{
		Connection myConnection = null;

		try
		{
			myConnection = pmd.getDataSource ().getConnection ();

			Statement s = myConnection.createStatement ();
			String sql = getRenameFieldStatement (pmd, oldFieldName, newFieldName);

			//  			System.out.println ("JDBCDatabaseType.renameField: " + sql);
			s.executeUpdate (sql);
		}
		catch (SQLException se)
		{
			log.error (se.toString ());

			throw new PersistenceException ("Unable to rename field '" + oldFieldName + "' in table '"
							+ pmd.getTableName () + "' to '" + newFieldName + "'");
		}
		finally
		{
			if (myConnection != null)
			{
				try
				{
					myConnection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Check wether a persistent table contains a specified field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The field name.
	 * @return True if the table contains the field.
	 */
	public boolean containsField (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		Connection connection = null;
		ResultSet res = null;

		try
		{
			connection = pmd.getDataSource ().getConnection ();

			DatabaseMetaData dmd = connection.getMetaData ();

			res = dmd.getColumns (null, null, pmd.getTableName (), fieldName);

			if (res.next ())
			{
				return true;
			}

			return false;
		}
		catch (SQLException se)
		{
			log.error (se.toString ());

			throw new PersistenceException ("Unable to check for field '" + fieldName + "' in table '"
							+ pmd.getTableName ());
		}
		finally
		{
			if (res != null)
			{
				try
				{
					res.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}

			if (connection != null)
			{
				try
				{
					connection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Get the statement to rename a table.
	 *
	 * @param oldTableName Old table name.
	 * @param newTableName Old table name.
	 * @return The rename statement.
	 */
	public String getRenameTableStatement (String oldTableName, String newTableName) throws PersistenceException
	{
		StringBuffer sb = new StringBuffer ();

		sb.append ("ALTER TABLE ");
		sb.append (oldTableName);
		sb.append (" RENAME TO ");
		sb.append (newTableName);

		return sb.toString ();
	}

	/**
	 * Rename a table.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldTableName Old table name.
	 * @param newTableName Old table name.
	 */
	public void renameTable (PersistentMetaData pmd, String oldTableName, String newTableName)
		throws PersistenceException
	{
		Connection connection = null;

		try
		{
			connection = pmd.getDataSource ().getConnection ();

			Statement s = connection.createStatement ();
			String sql = getRenameTableStatement (oldTableName, newTableName);

			// 			System.out.println ("JDBCDatabaseType.renameTable: " + sql);
			s.executeUpdate (sql);
		}
		catch (SQLException se)
		{
			log.error (se.toString ());

			throw new PersistenceException ("Unable to rename table '" + oldTableName + "' to '" + newTableName + "'");
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Drop a table.
	 *
	 * @param pmd Persistent meta data.
	 */
	public void dropTable (PersistentMetaData pmd) throws PersistenceException
	{
		Connection connection = null;

		try
		{
			connection = pmd.getDataSource ().getConnection ();

			Statement s = connection.createStatement ();
			String sql = "DROP TABLE " + pmd.getTableName ();

			// 			System.out.println ("JDBCDatabaseType.dropTable: " + sql);
			s.executeUpdate (sql);
		}
		catch (SQLException se)
		{
			log.error (se.toString ());
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Get the statement to add a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return The add field statement.
	 */
	public String getAddFieldStatement (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		StringBuffer sb = new StringBuffer ();

		sb.append ("ALTER TABLE ");
		sb.append (pmd.getTableName ());
		sb.append (" ADD COLUMN ");
		sb.append (fieldName);
		sb.append (" ");
		sb.append (getCompleteDBType (pmd, fieldName));

		return sb.toString ();
	}

	/**
	 * Get the statement to update a field to it's default value.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return The update field statement.
	 */
	public String getUpdateFieldDefaultValueStatement (PersistentMetaData pmd, String fieldName)
		throws PersistenceException
	{
		StringBuffer sb = new StringBuffer ();

		sb.append ("UPDATE ");
		sb.append (pmd.getTableName ());
		sb.append (" SET ");
		sb.append (fieldName);
		sb.append (" = ?");

		return sb.toString ();
	}

	/**
	 * Add a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 */
	public void addField (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		Connection myConnection = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;

		try
		{
			myConnection = pmd.getDataSource ().getConnection ();
			stmt = myConnection.createStatement ();

			String sql = getAddFieldStatement (pmd, fieldName);

			//  			System.out.println ("JDBCDatabaseType.addField: " + sql);
			stmt.executeUpdate (sql);

			if (pmd.getDefaultValue (fieldName) != null)
			{
				pstmt = myConnection.prepareStatement (getUpdateFieldDefaultValueStatement (pmd, fieldName));
				pstmt.setString (1, pmd.getDefaultValue (fieldName));
				pstmt.executeUpdate ();
			}
		}
		catch (SQLException se)
		{
			log.error (se.toString ());

			throw new PersistenceException ("Unable to add field '" + fieldName + "' in table '" + pmd.getTableName ()
							+ "'");
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}

			if (pstmt != null)
			{
				try
				{
					pstmt.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}

			if (myConnection != null)
			{
				try
				{
					myConnection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Add fields.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldNames A string array of field names to add.
	 */
	public void addFields (PersistentMetaData pmd, String[] fieldNames) throws PersistenceException
	{
		Connection myConnection = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		String fieldName = "";

		try
		{
			myConnection = pmd.getDataSource ().getConnection ();

			for (int i = 0; i < fieldNames.length; ++i)
			{
				stmt = myConnection.createStatement ();
				fieldName = fieldNames[i];

				String sql = getAddFieldStatement (pmd, fieldName);

				// 				System.out.println ("JDBCDatabaseType.addField: " + sql);
				stmt.executeUpdate (sql);

				if (pmd.getDefaultValue (fieldName) != null)
				{
					pstmt = myConnection.prepareStatement (getUpdateFieldDefaultValueStatement (pmd, fieldName));
					pstmt.setString (1, pmd.getDefaultValue (fieldName));
					pstmt.executeUpdate ();
				}

				stmt.close ();
			}
		}
		catch (SQLException se)
		{
			log.error (se.toString ());

			throw new PersistenceException ("Unable to add field '" + fieldName + "' in table '" + pmd.getTableName ()
							+ "'");
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}

			if (pstmt != null)
			{
				try
				{
					pstmt.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}

			if (myConnection != null)
			{
				try
				{
					myConnection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Get the statement to update the type of a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName Field name.
	 * @return The update statement.
	 */
	public String getUpdateTypeFieldStatement (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		throw new PersistenceException ("[JDBCDatabaseType] No getUpdateTypeFieldStatement() method defined.");
	}

	/**
	 * Get the statement to update the 'not null' flag of a table column. If all updates can
	 * be performed in the getUpdateTypeFieldStatement() method, return null here.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName Field name.
	 * @return The update statement.
	 */
	public String getUpdateNotNullFieldStatement (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		return null;
	}

	/**
	 * Update the type of a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 */
	public void updateTypeField (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		Connection myConnection = null;

		try
		{
			myConnection = pmd.getDataSource ().getConnection ();

			Statement s = myConnection.createStatement ();
			String sql = getUpdateTypeFieldStatement (pmd, fieldName);

			//  			System.out.println ("JDBCDatabaseType.updateTypeField: " + sql);
			s.executeUpdate (sql);

			sql = getUpdateNotNullFieldStatement (pmd, fieldName);

			if (sql != null)
			{
				s.close ();
				s = myConnection.createStatement ();
				// 				System.out.println ("JDBCDatabaseType.updateTypeField: " + sql);
				s.executeUpdate (sql);
			}
		}
		catch (SQLException se)
		{
			log.error (se.toString ());

			throw new PersistenceException ("Unable to update type of field '" + fieldName + "' in table '"
							+ pmd.getTableName () + "'");
		}
		finally
		{
			if (myConnection != null)
			{
				try
				{
					myConnection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Get the statement to add a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return The add field statement.
	 */
	public String getDeleteFieldStatement (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		StringBuffer sb = new StringBuffer ();

		sb.append ("ALTER TABLE ");
		sb.append (pmd.getTableName ());
		sb.append (" DROP COLUMN ");
		sb.append (fieldName);

		return sb.toString ();
	}

	/**
	 * Add a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 */
	public void deleteField (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		Connection myConnection = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;

		try
		{
			myConnection = pmd.getDataSource ().getConnection ();
			stmt = myConnection.createStatement ();

			String sql = "SELECT COUNT(" + fieldName + ") FROM " + pmd.getTableName ();

			//  			System.out.println ("JDBCDatabaseType.deleteField: " + sql);
			stmt.executeQuery (sql);

			stmt.close ();

			try
			{
				stmt = myConnection.createStatement ();
				sql = getDeleteFieldStatement (pmd, fieldName);
				// 				System.out.println ("JDBCDatabaseType.deleteField: " + sql);
				stmt.executeUpdate (sql);
			}
			catch (SQLException se)
			{
				log.error (se.toString ());

				throw new PersistenceException ("Unable to delete field '" + fieldName + "' in table '"
								+ pmd.getTableName () + "'");
			}
			finally
			{
				if (stmt != null)
				{
					try
					{
						stmt.close ();
					}
					catch (SQLException se)
					{
						throw new PersistenceException (se);
					}
				}

				if (pstmt != null)
				{
					try
					{
						pstmt.close ();
					}
					catch (SQLException se)
					{
						throw new PersistenceException (se);
					}
				}

				if (myConnection != null)
				{
					try
					{
						myConnection.close ();
					}
					catch (SQLException se)
					{
						throw new PersistenceException (se);
					}
				}
			}
		}
		catch (SQLException se)
		{
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close ();
				}
				catch (SQLException se)
				{
				}
			}

			if (myConnection != null)
			{
				try
				{
					myConnection.close ();
				}
				catch (SQLException se)
				{
					throw new PersistenceException (se);
				}
			}
		}
	}

	/**
	 * Check for field existence.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return True if the field exists.
	 */
	public boolean hasField (PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		Connection myConnection = null;
		Statement stmt = null;

		try
		{
			myConnection = pmd.getDataSource ().getConnection ();
			stmt = myConnection.createStatement ();

			String sql = "SELECT COUNT(" + fieldName + ") FROM " + pmd.getTableName ();

			stmt.executeQuery (sql);
		}
		catch (SQLException se)
		{
			return false;
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close ();
				}
				catch (SQLException se)
				{
				}
			}
		}

		return true;
	}

	/**
	 * Get the 'like' condition statement.
	 *
	 * @return The 'like' condition statement.
	 */
	public String getLikeStatement () throws PersistenceException
	{
		return "LIKE";
	}
}
