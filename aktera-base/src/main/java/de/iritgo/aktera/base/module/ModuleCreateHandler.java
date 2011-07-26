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
 * Database creation.
 */
public class ModuleCreateHandler extends CreateHandler
{
	/**
	 * @see de.iritgo.aktera.persist.CreateHandler#createTables(ModelRequest, de.iritgo.aktera.persist.PersistentFactory, java.sql.Connection, Logger)
	 */
	@Override
	public void createTables (ModelRequest request, PersistentFactory persistentFactory, Connection connection,
					Logger logger) throws ModelException, PersistenceException, SQLException
	{
		createTable ("Profile", "birthDate date", "lastLogin timestamp", "partyId int4 not null",
						"publishInformation boolean");

		createTable ("PreferencesConfig", "category varchar(16) not null", "name varchar(32) not null",
						"type varchar(1) not null", "userId int4 not null", "validValues text", "value text");
	}

	/**
	 * @see de.iritgo.aktera.persist.CreateHandler#createData(de.iritgo.aktera.persist.PersistentFactory, java.sql.Connection, Logger, ModelRequest)
	 */
	@Override
	public void createData (PersistentFactory persistentFactory, Connection connection, Logger logger,
					ModelRequest request) throws ModelException, PersistenceException, SQLException
	{
		createComponentSecurity ("de.iritgo.aktera.base.appointment.GetCalendar", "user", "*");
		createComponentSecurity ("de.iritgo.aktera.base.my.MyPersonal", "user", "*");
		createComponentSecurity ("de.iritgo.aktera.base.tools.Menu", "user", "*");

		createInstanceSecurity ("de.iritgo.aktera.ui.form.Edit", "aktera.my.settings.edit", "user", "*");
		createInstanceSecurity ("de.iritgo.aktera.ui.form.Save", "aktera.my.settings.save", "user", "*");

		Persistent adminGroup = persistentFactory.create ("aktera.AkteraGroup");

		adminGroup.setField ("name", "administrator");
		adminGroup.find ();

		Persistent managerGroup = persistentFactory.create ("aktera.AkteraGroup");

		managerGroup.setField ("name", "manager");
		managerGroup.find ();

		// Users
		Persistent profile;
		Persistent party;
		Persistent user;
		Persistent preferences;

		user = persistentFactory.create ("keel.user");
		user.setField ("name", "anonymous");

		if (user.find ())
		{
			party = persistentFactory.create ("aktera.Party");
			party.setField ("userId", user.getField ("uid"));

			if (party.find ())
			{
				profile = persistentFactory.create ("aktera.Profile");
				profile.setField ("partyId", party.getField ("partyId"));
				profile.add ();
			}

			preferences = persistentFactory.create ("aktera.Preferences");
			preferences.setField ("userId", user.getField ("uid"));
			preferences.setField ("protect", Boolean.TRUE);
			preferences.setField ("language", "de");
			preferences.setField ("theme", "iritgong");
			preferences.setField ("powerUser", Boolean.FALSE);
			preferences.setField ("canChangePassword", Boolean.FALSE);
			preferences.add ();
		}

		user = persistentFactory.create ("keel.user");
		user.setField ("name", "admin");

		if (user.find ())
		{
			party = persistentFactory.create ("aktera.Party");
			party.setField ("userId", user.getField ("uid"));

			if (party.find ())
			{
				profile = persistentFactory.create ("aktera.Profile");
				profile.setField ("partyId", party.getField ("partyId"));
				profile.add ();
			}

			preferences = persistentFactory.create ("aktera.Preferences");
			preferences.setField ("userId", user.getField ("uid"));
			preferences.setField ("protect", Boolean.FALSE);
			preferences.setField ("language", "de");
			preferences.setField ("theme", "iritgong");
			preferences.setField ("powerUser", Boolean.FALSE);
			preferences.setField ("canChangePassword", Boolean.TRUE);
			preferences.add ();
		}

		user = persistentFactory.create ("keel.user");
		user.setField ("name", "manager");

		if (user.find ())
		{
			party = persistentFactory.create ("aktera.Party");
			party.setField ("userId", user.getField ("uid"));

			if (party.find ())
			{
				profile = persistentFactory.create ("aktera.Profile");
				profile.setField ("partyId", party.getField ("partyId"));
				profile.add ();
			}

			preferences = persistentFactory.create ("aktera.Preferences");
			preferences.setField ("userId", user.getField ("uid"));
			preferences.setField ("protect", Boolean.FALSE);
			preferences.setField ("language", "de");
			preferences.setField ("theme", "iritgong");
			preferences.setField ("powerUser", Boolean.FALSE);
			preferences.setField ("canChangePassword", Boolean.TRUE);
			preferences.add ();
		}

		Integer userGroupId = selectInt ("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_USER + "'");

		createPermission ("G", userGroupId, "de.iritgo.aktera.client.login");
		createPermission ("G", userGroupId, "de.iritgo.aktera.web.login");

		Integer managerGroupId = selectInt ("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_MANAGER + "'");

		createPermission ("G", managerGroupId, "de.iritgo.aktera.client.login");
		createPermission ("G", managerGroupId, "de.iritgo.aktera.web.login");
	}
}
