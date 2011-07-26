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

package de.iritgo.aktera.aktario.module;


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.UpdateHandler;
import de.iritgo.simplelife.string.StringTools;
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
	public void updateDatabase (ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between ("0.0.0", "2.1.2"))
		{
			// Remove version entry of obsolete module 'aktera-aktario'
			update ("DELETE FROM version where name = 'aktera-aktario'");

			currentVersion.setVersion ("2.1.2");
		}

		if (currentVersion.between ("2.1.2", "2.1.3"))
		{
			List<Map<String, ?>> res = (List<Map<String, ?>>) new QueryRunner ().query (connection,
							"SELECT id, colorscheme FROM aktariouserpreferences", new MapListHandler ());

			for (Map<String, ?> row : res)
			{
				update ("UPDATE aktariouserpreferences set colorscheme = ? where id = ?", new Object[]
				{
								StringTools.trim (row.get ("colorscheme")).replace ("jgoodies.plaf", "jgoodies.looks"),
								row.get ("id")
				});
			}

			currentVersion.setVersion ("2.1.3");
		}

		if (currentVersion.lessThan ("2.2.1"))
		{
			updateColumnType ("AktarioUser", "password", "varchar(255)");
			updateColumnType ("IritgoUser", "password", "varchar(255)");

			currentVersion.setVersion ("2.2.1");
		}
	}
}
