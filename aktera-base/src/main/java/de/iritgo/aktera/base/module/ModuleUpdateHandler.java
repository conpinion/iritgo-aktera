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

package de.iritgo.aktera.base.module;


import de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroup;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.UpdateHandler;
import org.apache.avalon.framework.logger.Logger;
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
		if (currentVersion.between("1.5.14", "2.1.1"))
		{
			// Add primary key attribute to AkteraGroupEntry.
			createKeelIdColumn("AkteraGroupEntry");

			// New configuration entries
			createSystemConfigEntryWithKeelId("tb2", "webAppUrlAuto", "B", "true");
			createSystemConfigEntryWithKeelId("tb2", "logLevel", "C", "INFO", "FATAL_ERROR|ERROR|WARN|INFO|DEBUG");
			createSystemConfigEntryWithKeelId("tb2", "tcpipForwarding", "B", "false");
			createSystemConfigEntryWithKeelId("tb2", "enableWebAppRestart", "B", "false");
			createSystemConfigEntryWithKeelId("tb2", "webAppRestartTime", "T", "02:30:00");

			// Security table clean up.
			deleteComponentSecurity("de.buerobyte.aktera.tools.DeleteConfirm", "user");
			deleteComponentSecurity("de.buerobyte.aktera.importer.Import", "manager");
			deleteComponentSecurity("de.buerobyte.aktera.importer.ImportReport", "manager");
			deleteComponentSecurity("de.buerobyte.aktera.importer.ImportAnalyseReport", "manager");

			currentVersion.setVersion("2.1.1");
		}

		if (currentVersion.between("2.1.1", "2.1.2"))
		{
			currentVersion.setVersion("2.1.2");
		}

		if (currentVersion.lessThan("2.2.1"))
		{
			update("DELETE FROM ids where table_name = 'TestObject'");

			for (Map<String, Object> row : (List<Map<String, Object>>) query(
							"SELECT Component, GroupName FROM ComponentSecurity", new MapListHandler()))
			{
				String component = (String) row.get("Component");

				if (component.startsWith("de.buerobyte.aktera."))
				{
					String newComponent = "de.iritgo.aktera." + component.substring(20);

					update("UPDATE ComponentSecurity SET Component = '" + newComponent + "' WHERE Component = '"
									+ component + "' AND GroupName = '" + row.get("GroupName") + "'");
				}
			}

			update("UPDATE InstanceSecurity SET Component = 'de.iritgo.aktera.persist.defaultpersist.DefaultPersistent' where Component = 'org.keel.services.persist.defaultpersist.DefaultPersistent'");

			update("UPDATE InvokationSecurity SET Component = 'de.iritgo.aktera.persist.defaultpersist.RowSecurablePersistent' where Component = 'org.keel.services.persist.defaultpersist.RowSecurablePersistent'");

			deleteComponentSecurity("de.iritgo.aktera.tools.Menu", "user");
			createComponentSecurity("de.iritgo.aktera.base.tools.Menu", "user", "*");

			update("UPDATE AktarioUserPreferences set colorScheme='com.jgoodies.looks.plastic.theme.KDE' where colorScheme='BueroByte'");

			currentVersion.setVersion("2.2.1");
		}

		if (currentVersion.lessThan("2.3.1"))
		{
			Integer userGroupId = selectInt("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_USER + "'");
			insert("Permission", "principalId", userGroupId.toString(), "principalType", "'G'", "permission",
							"'de.iritgo.aktera.client.login'", "negative", "false");
			insert("Permission", "principalId", userGroupId.toString(), "principalType", "'G'", "permission",
							"'de.iritgo.aktera.web.login'", "negative", "false");

			currentVersion.setVersion("2.3.1");
		}
	}
}
