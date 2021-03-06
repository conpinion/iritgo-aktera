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

package de.iritgo.aktera.script.module;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.CreateHandler;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.PersistentFactory;
import org.apache.avalon.framework.logger.Logger;
import java.sql.Connection;
import java.sql.SQLException;


/**
 *
 */
public class ModuleCreateHandler extends CreateHandler
{
	public void createTables(ModelRequest request, PersistentFactory persistentFactory, Connection connection,
					Logger logger) throws ModelException, PersistenceException, SQLException
	{
		// Create the script table.
		update("CREATE TABLE script (" + " author varchar(255) NULL," + " code text NULL,"
						+ " copyright varchar(255) NULL," + " description text NULL," + " id serial primary key,"
						+ " language varchar(32) NOT NULL," + " name varchar(255) NOT NULL,"
						+ " displayname varchar(255) NOT NULL," + " version varchar(80) NULL" + ")");
	}

	/**
	 * @see de.iritgo.aktera.base.services.persist.CreateHandler#createData(de.iritgo.aktera.persist.PersistentFactory, java.sql.Connection, Logger, ModelRequest)
	 */
	@Override
	public void createData(PersistentFactory persistentFactory, Connection connection, Logger logger,
					ModelRequest request) throws ModelException, PersistenceException, SQLException
	{
	}
}
