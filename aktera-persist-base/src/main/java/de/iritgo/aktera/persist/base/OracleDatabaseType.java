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
import org.apache.avalon.excalibur.pool.Poolable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.DatabaseType
 * @x-avalon.info name=oracle
 * @x-avalon.lifestyle type=singleton
 *
 * @author Santanu Dutt Aug 16, 2002
 *
 * This class is a specialization of JDBCDatabaseType for Oracle.
 * @version         $Revision: 1.1 $ $Date: 2004/03/27 16:02:19 $
 */
public class OracleDatabaseType extends JDBCDatabaseType implements Poolable
{
	/**
	 * Initialize the default type map from this datasource.
	 */
	public void setDataSource(DataSourceComponent ds) throws PersistenceException
	{
		Connection myConnection = null;

		try
		{
			myConnection = ds.getConnection();

			DatabaseMetaData dm = myConnection.getMetaData();

			if (! super.supportsTransactions())
			{
				if (dm.supportsTransactions())
				{
					supportsTransactions = true;
				}
				else
				{
					supportsTransactions = false;
				}
			}

			ResultSet rsx = dm.getTypeInfo();

			while (rsx.next())
			{
				TypeMapEntry oneType = new TypeMapEntry();

				//--- quikdraw: Changed the resultSet extraction from indexes to
				//	column names as defined in the Sun JavaDoc for JDBC.
				oneType.setTypeName(rsx.getString("TYPE_NAME"));
				oneType.setDataType(rsx.getShort("DATA_TYPE"));

				if (! "BLOB".equals(oneType.getTypeName()) && ! "CLOB".equals(oneType.getTypeName()))
				{
					oneType.setPrecision(rsx.getInt("PRECISION"));
					oneType.setLiteralPrefix(rsx.getString("LITERAL_PREFIX"));
					oneType.setLiteralSuffix(rsx.getString("LITERAL_SUFFIX"));
					oneType.setCreateParams(rsx.getString("CREATE_PARAMS"));
					oneType.setNullable(rsx.getShort("NULLABLE"));
					oneType.setCaseSensitive(rsx.getBoolean("CASE_SENSITIVE"));
					oneType.setSearchable(rsx.getShort("SEARCHABLE"));
					oneType.setUnsigned(rsx.getBoolean("UNSIGNED_ATTRIBUTE"));
					oneType.setFixedPrecision(rsx.getBoolean("FIXED_PREC_SCALE"));
					oneType.setAutoIncrement(rsx.getBoolean("AUTO_INCREMENT"));
					oneType.setLocalTypeName(rsx.getString("LOCAL_TYPE_NAME"));
					oneType.setMinScale(rsx.getShort("MINIMUM_SCALE"));
					oneType.setMaxScale(rsx.getShort("MAXIMUM_SCALE"));
					//--- quikdraw: This was index 16, but the JavaDoc for DatabaseMetaData
					//	states the index for the Radix is 18.
					oneType.setNumPrecRadix(rsx.getInt("NUM_PREC_RADIX"));
				}

				String typeAsString = typeToString(oneType.getDataType());

				TypeMapEntry oldType = (TypeMapEntry) this.getTypeMap().get(typeAsString);

				if (oldType == null)
				{
					this.getTypeMap().put(typeAsString, oneType);
				}
				else
				{
					log.warn("Type '" + typeAsString + "' overridden by configuration or mapped twice");
				}
			}
		}
		catch (SQLException se)
		{
			throw new PersistenceException(se);
		}
		finally
		{
			try
			{
				if (myConnection != null)
				{
					myConnection.close();
				}
			}
			catch (SQLException se)
			{
				throw new PersistenceException(se);
			}
		}
	} // setDataSource

	protected StringBuffer getCreateTableStatement(PersistentMetaData pmd, DataSourceComponent dataSource)
		throws PersistenceException
	{
		String fieldName = null;
		boolean addComma = false;
		String identityFieldName = null;

		StringBuffer createStatement = new StringBuffer("CREATE TABLE ");

		createStatement.append(pmd.getTableName() + " (");

		StringBuffer oneType = new StringBuffer("");

		for (Iterator lf = pmd.getFieldNames().iterator(); lf.hasNext();)
		{
			fieldName = (String) lf.next();

			if (addComma)
			{
				createStatement.append(", ");
			}

			if (pmd.isAutoIncremented(fieldName))
			{
				if (pmd.isKeyField(fieldName) && (identityFieldName == null) && isIdentitySupported()
								&& (pmd.getIdGenerator(fieldName) == null))
				{
					identityFieldName = fieldName;
					createStatement.append(pmd.getDBFieldName(fieldName) + " ");
					createStatement.append(getCreateIdentitySyntax());
				}
				else if (isSequenceSupported())
				{
					createStatement.append(getCreateSequenceSyntax(fieldName));
				}
				else
				{
					createStatement.append(pmd.getDBFieldName(fieldName) + " ");
					createStatement.append(pmd.getDBType(fieldName));
					createStatement.append(" not null");
				}
			}
			else
			{
				createStatement.append(pmd.getDBFieldName(fieldName) + " ");
				oneType = new StringBuffer("");
				oneType.append(pmd.getDBType(fieldName));

				if (pmd.getLength(fieldName) != 0)
				{
					oneType.append("(" + pmd.getLength(fieldName));

					if (pmd.getPrecision(fieldName) > 0)
					{
						oneType.append(", " + pmd.getPrecision(fieldName));
					}

					oneType.append(")");
				}
				else
				{
					//varchar2 or varchar datatypes must have length defined
					if ("varchar2".equalsIgnoreCase(pmd.getDBType(fieldName))
									|| "varchar".equalsIgnoreCase(pmd.getDBType(fieldName)))
					{
						oneType.append("(2000)");
					}
				}

				createStatement.append(oneType.toString());

				if (! pmd.allowsNull(fieldName))
				{
					createStatement.append(" not null");
				}
				else
				{
					createStatement.append(" null");
				}
			}

			addComma = true;
		} /* for each field */
		/* Keys are normally added using the alter table, unless
		 * the particular DBType in use doesn't support that syntax.
		 * In that case, the key is specified as part of the CREATE TABLE syntax
		 */
		if (! isAlterKeySupported())
		{
			short keyCount = 0;

			addComma = false;

			for (Iterator kf = pmd.getKeyFieldNames().iterator(); kf.hasNext();)
			{
				fieldName = (String) kf.next();

				if (! (pmd.isAutoIncremented(fieldName) && isIdentitySupported()))
				{
					keyCount++;

					if (addComma)
					{
						createStatement.append(",");
					}
					else
					{
						createStatement.append(", PRIMARY KEY (");
					}

					createStatement.append(fieldName);
					addComma = true;
				}
			}

			if (keyCount != 0)
			{
				createStatement.append(")");
			}

			if (pmd.getKeyFieldNames().size() == 0)
			{
				log.warn("No primary key on table '" + pmd.getTableName() + "'");
			}
		}

		createStatement.append(")");

		return createStatement;
	} // getCreateTableStatement

	/**
	 * @see de.iritgo.aktera.persist.defaultpersist.JDBCDatabaseType#getDateTimeUpdateFormat()
	 */
	public String getDateTimeUpdateFormat()
	{
		return "yyyy-MM-dd HH mm ss";
	} // getDateTimeUpdateFormat

	/**
	 * @see de.iritgo.aktera.persist.defaultpersist.JDBCDatabaseType#getDateTimeUpdateFunction()
	 */
	public String getDateTimeUpdateFunction()
	{
		return "TO_DATE(%s, 'YYYY-MM-DD HH24 MI SS')";
	} // getDateTimeUpdateFunction

	/**
	 * @see de.iritgo.aktera.persist.defaultpersist.JDBCDatabaseType#getDateUpdateFormat()
	 */
	public String getDateUpdateFormat()
	{
		return "yyyy-MM-dd";
	} // getDateUpdateFormat

	/**
	 * @see de.iritgo.aktera.persist.defaultpersist.JDBCDatabaseType#getDateUpdateFunction()
	 */
	public String getDateUpdateFunction()
	{
		return "TO_DATE(%s, 'YYYY-MM-DD')";
	} // getDateUpdateFunction

	/**
	 * @see de.iritgo.aktera.persist.DatabaseType#isSequenceSupported()
	 */
	public boolean isSequenceSupported()
	{
		return false;
	} // isSequenceSupported

	/**
	 * @see de.iritgo.aktera.persist.DatabaseType#getDropSequenceSyntax(String)
	 */
	public String getDropSequenceSyntax(String sequenceName) throws PersistenceException
	{
		return "DROP SEQUENCE " + sequenceName;
	} // getDropSequenceSyntax

	/**
	 * This is the same as JDBCDatabaseType. It is specified explicitely here
	 * in case the parent's default is changed - since Oracle supports this.
	 * @see de.iritgo.aktera.persist.DatabaseType#isAlterKeySupported()
	 */
	public boolean isAlterKeySupported()
	{
		return true;
	} // isAlterKeySupported

	/**
	 * Return a vector of wild card characters for this database type.
	 * This can be used to determine if the search criteria supplied by a
	 * user has wild-card characters in it or is an exact match
	 *
	 * @return    A list of the wild-card characters used by
	 *            this database connection
	 */
	public Set getWildCards()
	{
		HashSet newChars = new HashSet(1);

		newChars.add(new String("%"));

		return newChars;
	} // getWildCards

	/**
	 * @see de.iritgo.aktera.persist.DatabaseType#getCreateSequenceSyntax(String)
	 */
	public String getCreateSequenceSyntax(String sequenceName) throws PersistenceException
	{
		return "CREATE SEQUENCE " + sequenceName + " START WITH 1000 INCREMENT BY 1";
	} // getCreateSequenceSyntax

	public String getRetrieveSequenceSyntax(String sequenceName) throws PersistenceException
	{
		return "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
	} // getRetrieveSequenceSyntax
} // class OracleDatabaseType
