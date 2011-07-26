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

package de.iritgo.aktera.importer.module;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.CreateHandler;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import org.apache.avalon.framework.logger.Logger;
import java.sql.Connection;
import java.sql.SQLException;


/**
 *
 */
public class ModuleCreateHandler extends CreateHandler
{
	public void createData (PersistentFactory persistentFactory, Connection connection, Logger logger,
					ModelRequest request) throws ModelException, PersistenceException, SQLException
	{
		createComponentSecurity ("de.iritgo.aktera.importer.ui.Import", "user", "*");
		createComponentSecurity ("de.iritgo.aktera.importer.ui.ImportReport", "user", "*");
		createComponentSecurity ("de.iritgo.aktera.importer.ui.ImportAnalyseReport", "user", "*");
	}
}
