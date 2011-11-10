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

package de.iritgo.aktera.authorization.module;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.CreateHandler;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.PersistentFactory;
import org.apache.avalon.framework.logger.Logger;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Database creation.
 */
public class ModuleCreateHandler extends CreateHandler
{
	/**
	 * @see de.iritgo.aktera.persist.CreateHandler#createTables(ModelRequest, de.iritgo.aktera.persist.PersistentFactory, java.sql.Connection, Logger)
	 */
	@Override
	public void createTables(ModelRequest request, PersistentFactory persistentFactory, Connection connection,
					Logger logger) throws ModelException, PersistenceException, SQLException
	{
		createTable("InstanceSecurity", "Component varchar(132) not null", "GroupName varchar(80) not null",
						"Instance varchar(60) not null", "operationsAllowed varchar(30) not null");

		createTable("InvokationSecurity", "Comparator varchar(2) not null", "Component varchar(132) not null",
						"GroupName varchar(80) not null", "Instance varchar(60) not null",
						"OperationsAllowed varchar(30) not null", "Property varchar(80) not null",
						"Value varchar(80) not null");

		createTable("ComponentSecurity", "Component varchar(132) not null", "GroupName varchar(80) not null",
						"OperationsAllowed varchar(30) not null");
	}
}
