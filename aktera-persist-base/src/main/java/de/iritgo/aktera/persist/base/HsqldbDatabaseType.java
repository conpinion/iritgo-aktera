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


/**
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.DatabaseType
 * @x-avalon.info name=hsqldb
 * @x-avalon.lifestyle type=singleton
 *
 * @author Shash Chatterjee Jul 16, 2002
 *
 * This class is a specialization of JDBCDatabaseType for HsqlDB.
 * HsqlDB does not allow "ALTER TABLE" statments with the "primary key" keywords
 *  @version                 $Revision: 1.1 $ $Date: 2004/03/27 16:02:18 $
 */
public class HsqldbDatabaseType extends JDBCDatabaseType
{
	/* @see de.iritgo.aktera.persist.DatabaseType#getCreateIdentitySyntax()
	 */
	public String getCreateIdentitySyntax () throws PersistenceException
	{
		return "IDENTITY";
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getInsertIdentitySyntax()
	 */
	public String getInsertIdentitySyntax () throws PersistenceException
	{
		return "null";
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#getRetrieveIdentitySyntax()
	 */
	public String getRetrieveIdentitySyntax (PersistentMetaData pmd, String idFieldName) throws PersistenceException
	{
		return "CALL IDENTITY()";
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isIdentitySupported()
	 */
	public boolean isIdentitySupported ()
	{
		//Although HsqlDB does support Identity columns, it does not
		// allow use of additional primary keys when an identity column is
		// used...a feature which Keel uses.
		return false;
	}

	/* @see de.iritgo.aktera.persist.DatabaseType#isAlterKeySupported()
	 */
	public boolean isAlterKeySupported ()
	{
		return false;
	}

	/**
	 * Theoretically Hypersonic does support transactions,
	 * but the unit tests say otherwise...
	 */
	public boolean supportsTransactions ()
	{
		return false;
	} // supportsTransactions
}
