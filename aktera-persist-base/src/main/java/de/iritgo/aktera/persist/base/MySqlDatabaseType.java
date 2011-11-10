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
import org.apache.avalon.excalibur.pool.Poolable;


/**
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.DatabaseType
 * @x-avalon.info name=mysql
 * @x-avalon.lifestyle type=singleton
 *
 * @author Shash Chatterjee Jul 16, 2002
 *
 * This class is a specialization of JDBCDatabaseType for MySql.
 * @version                 $Revision: 1.2 $ $Date: 2005/04/11 16:44:02 $
 */
public class MySqlDatabaseType extends JDBCDatabaseType implements Poolable
{
	/* @see de.iritgo.aktera.persist.DatabaseType#getCreateIdentitySyntax()
	 */
	public String getCreateIdentitySyntax() throws PersistenceException
	{
		return "INT NOT NULL AUTO_INCREMENT PRIMARY KEY";
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getInsertIdentitySyntax()
	 */
	public String getInsertIdentitySyntax() throws PersistenceException
	{
		return "null";
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getRetrieveIdentitySyntax()
	 */
	public String getRetrieveIdentitySyntax(PersistentMetaData pmd, String idFieldName) throws PersistenceException
	{
		return "SELECT LAST_INSERT_ID()";
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isIdentitySupported()
	 */
	public boolean isIdentitySupported()
	{
		return true;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isAlterKeySupported()
	 */
	public boolean isAlterKeySupported()
	{
		return true;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#skipIdentityInAlterKey()
	 */
	public boolean skipIdentityInAlterKey()
	{
		return true;
	}

	/**
	 * Don't say "null" after fields that *do* allow null - just use "not null" on
	 * fields that *don't* allow it...
	 */
	protected boolean useExplicitNull()
	{
		return false;
	}

	public boolean supportsTransactions()
	{
		return false;
	}

	/**
	 * Get the statement to rename a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 * @return The rename statement.
	 */
	public String getRenameFieldStatement(PersistentMetaData pmd, String oldFieldName, String newFieldName)
		throws PersistenceException
	{
		StringBuffer sb = new StringBuffer();

		sb.append("ALTER TABLE ");
		sb.append(pmd.getTableName());
		sb.append(" CHANGE ");
		sb.append(oldFieldName);
		sb.append(" ");
		sb.append(newFieldName);
		sb.append(" ");
		sb.append(getCompleteDBType(pmd, newFieldName));

		return sb.toString();
	}

	/**
	 * Get the statement to update the type of a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName Field name.
	 * @return The update statement.
	 */
	public String getUpdateTypeFieldStatement(PersistentMetaData pmd, String fieldName) throws PersistenceException
	{
		StringBuffer sb = new StringBuffer();

		sb.append("ALTER TABLE ");
		sb.append(pmd.getTableName());
		sb.append(" CHANGE ");
		sb.append(fieldName);
		sb.append(" ");
		sb.append(fieldName);
		sb.append(" ");
		sb.append(getCompleteDBType(pmd, fieldName));

		return sb.toString();
	}
}
