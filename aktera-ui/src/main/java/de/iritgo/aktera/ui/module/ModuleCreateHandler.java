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

package de.iritgo.aktera.ui.module;


import java.sql.*;
import org.apache.avalon.framework.logger.Logger;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.*;


public class ModuleCreateHandler extends CreateHandler
{
	@Override
	public void createTables(ModelRequest request, PersistentFactory persistentFactory, Connection connection,
					Logger logger) throws ModelException, PersistenceException, SQLException
	{
		// Create the Preferences table
		createTable("preferences", "canChangePassword boolean null", "language varchar(8) null",
						"pin varchar(16) null", "powerUser boolean null", "protect boolean null",
						"security varchar(16) null", "theme varchar(32) null", "userId int4 not null");
	}

	@Override
	public void createData(PersistentFactory persistentFactory, Connection connection, Logger logger,
					ModelRequest request) throws ModelException, PersistenceException, SQLException
	{
		insert("preferencesconfig", "category", "'gui'", "name", "'tableRowsPerPage'", "type", "'I'", "userId", "1",
						"value", "''");
		insert("preferencesconfig", "category", "'gui'", "name", "'tableRowsPerPage'", "type", "'I'", "userId", "2",
						"value", "''");
	}
}
