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


import java.sql.Connection;
import org.apache.avalon.framework.logger.Logger;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.*;


public class ModuleUpdateHandler extends UpdateHandler
{
	@Override
	public void updateDatabase(ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between("0.0.0", "2.1.2"))
		{
			// Some model ids have changed. Update the security tables.
			update("UPDATE instancesecurity SET component = 'de.iritgo.aktera.ui.form.Edit' where component = 'de.buerobyte.aktera.formular.Edit'");
			update("UPDATE instancesecurity SET component = 'de.iritgo.aktera.ui.form.Save' where component = 'de.buerobyte.aktera.formular.Save'");
			update("UPDATE instancesecurity SET component = 'de.iritgo.aktera.ui.form.Delete' where component = 'de.buerobyte.aktera.formular.Delete'");
			update("UPDATE instancesecurity SET component = 'de.iritgo.aktera.ui.listing.List' where component = 'de.buerobyte.aktera.listing.List'");

			currentVersion.setVersion("2.1.2");
		}

		if (currentVersion.lessThan("2.2.1"))
		{
			currentVersion.setVersion("2.2.1");
		}

		if (currentVersion.lessThan("2.2.2"))
		{
			if (count("preferencesconfig", "category='gui' and name='tableRowsPerPage' and userid=1") == 0)
			{
				insert("preferencesconfig", "category", "'gui'", "name", "'tableRowsPerPage'", "type", "'I'", "userId",
								"1", "value", "''");
			}

			if (count("preferencesconfig", "category='gui' and name='tableRowsPerPage' and userid=2") == 0)
			{
				insert("preferencesconfig", "category", "'gui'", "name", "'tableRowsPerPage'", "type", "'I'", "userId",
								"2", "value", "''");
			}

			currentVersion.setVersion("2.2.2");
		}
	}
}
