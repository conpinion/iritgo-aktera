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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;


/**
 *
 * @author Santanu Dutt
 * @version                 $Revision: 1.1 $ $Date: 2004/03/27 16:02:19 $
 *
 * DatabaseType for Informix
 */
public class InformixDatabaseType extends JDBCDatabaseType
{
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

	protected StringBuffer getCreateTableStatement (PersistentMetaData pmd, DataSourceComponent dataSource)
		throws PersistenceException
	{
		String fieldName = null;

		boolean addComma = false;
		String identityFieldName = null;

		StringBuffer createStatement = new StringBuffer ("CREATE TABLE ");

		createStatement.append (pmd.getTableName () + "(");

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

					//Added for compatability w/Informix standard?
				}
			}

			addComma = true;
		} /* for each field */
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
	 * Don't say "null" after fields that *do* allow null - just use "not null" on
	 * fields that *don't* allow it...
	 */
	protected boolean useExplicitNull ()
	{
		return false;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isAlterKeySupported()
	 */
	public boolean isAlterKeySupported ()
	{
		return false;
	}

	/*
	 * @return jkh
	 */
	public String getDateTimeSelectFormat ()
	{
		return ("yyyy-MM-dd hh:mm:ss");
	}

	/*
	 * @return jkh
	 */
	public String getDateTimeUpdateFormat ()
	{
		return ("yyyy-MM-dd hh:mm:ss");
	}
}
