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

package de.iritgo.aktera.journal.module;


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.UpdateHandler;
import org.apache.avalon.framework.logger.Logger;
import java.sql.Connection;


/**
 *
 */
public class ModuleUpdateHandler extends UpdateHandler
{
	@Override
	public void updateDatabase(ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between("0.0.0", "2.2.1"))
		{
			createTable("JournalEntry", "id serial primary key", "occurredat timestamp not null", "producerId int4",
							"producerType varchar(255)", "ownerId int4", "ownerType varchar(255)", "ownerGroupId int4",
							"ownerGroupType varchar(255)", "extendedInfoId int4", "extendedInfoType varchar(255)",
							"message varchar(255)", "shortMessage varchar(255)", "tags varchar(255)",
							"primaryType varchar(255)", "secondaryType varchar(255)", "newFlag boolean");
			createInstanceSecurity("de.iritgo.aktera.ui.listing.List", "aktera.journal.list", "user", "*");
			createIndex("JournalEntry", "occurredat");
			currentVersion.setVersion("2.2.1");
		}

		if (currentVersion.between("2.2.1", "2.2.2"))
		{
			createTable("JournalData", "id serial primary key", "type varchar(255)", "category varchar(255)",
							"timestamp1 timestamp", "timestamp2 timestamp", "key int4", "integer1 int4",
							"integer2 int4", "string1 varchar(255)", "string2 varchar(255)", "string3 varchar(255)",
							"string4 varchar(255)", "string5 varchar(255)", "string6 varchar(255)");
			createComponentSecurity("aktera.journal.delete-journal-entry", "user", "*");
			createComponentSecurity("aktera.journal.execute-journal-entry", "user", "*");
			currentVersion.setVersion("2.2.2");
		}

		if (currentVersion.between("2.2.2", "2.2.3"))
		{
			addColumn("JournalEntry", "searchableText", "varchar(255)");
			addColumn("JournalEntry", "misc", "varchar(255)");
			addColumn("JournalEntry", "rawData", "varchar(255)");

			currentVersion.setVersion("2.2.3");
		}

		if (currentVersion.between("2.2.3", "2.2.4"))
		{
			createInstanceSecurity("de.iritgo.aktera.ui.listing.List", "aktera.journal.list.notvisible", "user", "*");

			currentVersion.setVersion("2.2.4");
		}

		if (currentVersion.lessThan("2.3.1"))
		{
			currentVersion.setVersion("2.3.1");
		}

		if (currentVersion.between("2.3.1", "2.3.2"))
		{
			addColumn("JournalData", "occurredAt", "timestamp");
			update("update journaldata set occurredat = (select occurredat from journalentry where journalentry.extendedinfoid = journaldata.id)");
			update("delete from journaldata where occurredat is null");
			updateColumnToNotNull("JournalData", "occurredAt");
			
			currentVersion.setVersion("2.3.2");
		}

	}
}
