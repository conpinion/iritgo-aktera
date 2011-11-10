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


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.UpdateHandler;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import java.sql.Connection;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class ModuleUpdateHandler extends UpdateHandler
{
	@Override
	public void updateDatabase(ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between("0.0.0", "2.1.2"))
		{
			currentVersion.setVersion("2.1.2");
		}

		if (currentVersion.between("2.1.2", "2.1.3"))
		{
			// Fix component securities for renamed packages
			deleteComponentSecurity("de.buerobyte.aktera.importer.Import", "user");
			deleteComponentSecurity("de.buerobyte.aktera.importer.ImportReport", "user");
			deleteComponentSecurity("de.buerobyte.aktera.importer.ImportAnalyseReport", "user");

			createComponentSecurity("de.iritgo.aktera.importer.ui.Import", "user", "*");
			createComponentSecurity("de.iritgo.aktera.importer.ui.ImportReport", "user", "*");
			createComponentSecurity("de.iritgo.aktera.importer.ui.ImportAnalyseReport", "user", "*");

			currentVersion.setVersion("2.1.3");
		}

		if (currentVersion.lessThan("2.2.1"))
		{
			currentVersion.setVersion("2.2.1");
		}
	}
}
