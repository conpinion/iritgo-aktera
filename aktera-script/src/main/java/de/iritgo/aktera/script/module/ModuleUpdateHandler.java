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


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.UpdateHandler;
import org.apache.avalon.framework.logger.Logger;
import java.sql.Connection;


/**
 * @version $Id: ModuleUpdateHandler.java,v 1.2 2005/11/15 11:31:52 grappendorf Exp $
 */
public class ModuleUpdateHandler extends UpdateHandler
{
	@Override
	public void updateDatabase(ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between("0.0.0", "2.1.2"))
		{
			// Create the script table.
			update("CREATE TABLE script (" + " author varchar(255) NULL," + " code text NULL,"
							+ " copyright varchar(255) NULL," + " description text NULL," + " id int4 NOT NULL,"
							+ " language varchar(32) NOT NULL," + " name varchar(255) NOT NULL,"
							+ " displayname varchar(255) NOT NULL," + " version varchar(80) NULL" + ")");

			createAutoIncrement("Script", 1);

			currentVersion.setVersion("2.1.2");
		}

		if (currentVersion.lessThan("2.2.1"))
		{
			update("ALTER TABLE Script ADD PRIMARY KEY (id)");

			createPrimaryKeySequenceFromIdTable("Script", "id");

			currentVersion.setVersion("2.2.1");
		}
	}
}
