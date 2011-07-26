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


import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.PersistentMetaData;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.DatabaseType
 * @x-avalon.info name=postgresql
 * @x-avalon.lifestyle type=singleton
 *
 * @author Santanu Dutt
 * @version  $Revision: 1.3 $ $Date: 2005/11/29 23:40:23 $
 */
public class PostgresDatabaseType extends JDBCDatabaseType
{
	/**
	 * This method checks on the features of the existing table and tries to match with the current schema.
	 * Logs all differences found as warnings.
	 */
	public void checkTable (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException
	{
		Connection myConnection = null;

		try
		{
			myConnection = dataSource.getConnection ();

			Statement stmt = myConnection.createStatement ();

			ResultSet rs = stmt.executeQuery ("SELECT * FROM " + pmd.getTableName ());
			ResultSetMetaData rsmd = rs.getMetaData ();
			DatabaseMetaData dbmd = myConnection.getMetaData ();
			int numberOfCols = rsmd.getColumnCount ();

			for (Iterator lf = pmd.getFieldNames ().iterator (); lf.hasNext ();)
			{
				String fieldName = (String) lf.next ();
				boolean fieldFound = false;

				for (int i = 0; i < numberOfCols; i++)
				{
					if (fieldName.equalsIgnoreCase (rsmd.getColumnName (i + 1)))
					{
						fieldFound = true;

						boolean columnIsOk = true;

						//Check for column SQL type
						if (! pmd.getDBType (fieldName).equals (rsmd.getColumnTypeName (i + 1)))
						{
							log.warn ("Existing table " + pmd.getTableName () + " has different SQL type for column "
											+ fieldName);
							columnIsOk = false;

							break;
						}

						//Check for NULL
						if (columnIsOk)
						{
							if (pmd.allowsNull (fieldName))
							{
								if (rsmd.isNullable (i + 1) == ResultSetMetaData.columnNoNulls)
								{
									log.warn ("Column " + fieldName + " in existing table " + pmd.getTableName ()
													+ " is not nullable as specified in schema");
								}
							}
							else
							{
								if (rsmd.isNullable (i + 1) == ResultSetMetaData.columnNullable)
								{
									log.warn ("Column " + fieldName + " in existing table " + pmd.getTableName ()
													+ " is nullable contrary to as in schema");
								}
							}
						}
					}
				}

				//Check for non-existence of column in database table
				if (! fieldFound)
				{
					log.warn ("Column " + fieldName + " is not found in the existing table " + pmd.getTableName ()
									+ " in the database");
				}
			}

			//Using DatabaseMetaData to check on Primary keys
			Set keyFields = pmd.getKeyFieldNames ();

			ResultSet keysOnTable = dbmd.getPrimaryKeys (null, pmd.getSchemaName (), pmd.getTableName ());

			HashSet keysOnTableSet = new HashSet ();

			while (keysOnTable.next ())
			{
				String keyName = keysOnTable.getString ("COLUMN_NAME").toLowerCase ();

				keysOnTableSet.add (keyName);

				boolean foundMatch = false;

				for (Iterator lf = keyFields.iterator (); lf.hasNext ();)
				{
					String oneKeyOnSchema = (String) lf.next ();

					//if(!keyFields.contains(keyName)){
					if (keyName.equals (oneKeyOnSchema.toLowerCase ()))
					{
						foundMatch = true;
					}
				}

				if (! foundMatch)
				{
					log.warn ("Column " + keyName + " in table " + pmd.getTableName ()
									+ " is defined as key contrary to table schema");
				}
			}

			for (Iterator lf = keyFields.iterator (); lf.hasNext ();)
			{
				String oneKeyOnSchema = (String) lf.next ();

				if (! keysOnTableSet.contains (oneKeyOnSchema.toLowerCase ()))
				{
					log.warn ("Column " + oneKeyOnSchema + " in table " + pmd.getTableName ()
									+ " is not defined as primary key contrary to table schema");
				}
			}

			//Primary keys check ends

			//Check for extra columns in database table
			if (numberOfCols > pmd.getFieldNames ().size ())
			{
				//log.warn("Existing " +pmd.getTableName()+ " table in database has more columns defined");
				for (int i = 0; i < numberOfCols; i++)
				{
					if (! pmd.getFieldNames ().contains (rsmd.getColumnName (i + 1)))
					{
						log.warn ("Column " + rsmd.getColumnName (i + 1) + " exists in the table "
										+ pmd.getTableName () + " but is missing from schema");
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new PersistenceException (e);
		}
		finally
		{
			try
			{
				myConnection.close ();
			}
			catch (SQLException se)
			{
				throw new PersistenceException ("Unable to close/release connection", se);
			}
		}
	}

	/**
	 * Get the 'like' condition statement.
	 *
	 * @return The 'like' condition statement.
	 */
	public String getLikeStatement () throws PersistenceException
	{
		return "ILIKE";
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
		StringBuffer sb = new StringBuffer ();

		sb.append ("ALTER TABLE ");
		sb.append (pmd.getTableName ());
		sb.append (" RENAME COLUMN ");
		sb.append (oldFieldName);
		sb.append (" TO ");
		sb.append (newFieldName);

		return sb.toString ();
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
		StringBuffer sb = new StringBuffer ();

		sb.append ("ALTER TABLE ");
		sb.append (pmd.getTableName ());
		sb.append (" ALTER COLUMN ");
		sb.append (fieldName);
		sb.append (" TYPE ");
		sb.append (getDBTypeWithPrecision (pmd, fieldName));

		return sb.toString ();
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
		if (! pmd.allowsNull (fieldName))
		{
			StringBuffer sb = new StringBuffer ();

			sb.append ("ALTER TABLE ");
			sb.append (pmd.getTableName ());
			sb.append (" ALTER COLUMN ");
			sb.append (fieldName);
			sb.append (" SET NOT NULL");

			return sb.toString ();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see de.iritgo.aktera.persist.base.JDBCDatabaseType#getRetrieveIdentitySyntax(PersistentMetaData, String)
	 */
	public String getRetrieveIdentitySyntax (PersistentMetaData pmd, String idFieldName) throws PersistenceException
	{
		return "SELECT lastval()";
	}
}
