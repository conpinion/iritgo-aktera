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

package de.iritgo.aktera.address.module;


import java.sql.*;
import org.apache.avalon.framework.logger.*;
import de.iritgo.aktera.authentication.defaultauth.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.*;


public class ModuleCreateHandler extends CreateHandler
{
	@Override
	public void createTables(ModelRequest request, PersistentFactory persistentFactory, Connection connection,
					Logger logger) throws ModelException, PersistenceException, SQLException
	{
		createTable("Address", "category varchar(80) not null", "city varchar(80)", "company varchar(255)",
						"companyNumber varchar(80)", "contactNumber varchar(80)", "country varchar(80)",
						"division varchar(80)", "email varchar(255)", "firstName varchar(80)", "id serial primary key",
						"internalCompany varchar(255)", "internalLastname varchar(255)", "lastName varchar(255)",
						"mobile varchar(80)", "ownerId int4", "partyId int4", "phone varchar(80)",
						"position varchar(80)", "postalCode varchar(32)", "remark text", "salutation varchar(16)",
						"sourceSystemClient varchar(80)", "sourceSystemId varchar(80)", "street varchar(80)",
						"web varchar(80)");

		createIndex("Address", "firstName");
		createIndex("Address", "lastName");
		createIndex("Address", "street");
		createIndex("Address", "city");

		createTable("AddressStore", "id serial primary key", "name varchar(255) not null",
						"type varchar(255) not null", "title varchar(255)", "position int4 not null",
						"systemStore boolean not null", "defaultStore boolean not null", "editable boolean not null",
						"numberLookup boolean not null", "emptySearchReturnsAllEntries boolean not null",
						"numberNormalization varchar(64)");

		createIndex("AddressStore", "name");

		createTable("AddressDAOStore", "id int4 primary key references AddressStore(id)",
						"category varchar(255) not null", "checkOwner boolean not null");

		createTable("AddressLDAPStore", "id int4 primary key references AddressStore(id)",
						"host varchar(255) not null", "port int4", "authDn varchar(255)", "authPassword varchar(255)",
						"baseDn varchar(255) not null", "query varchar(255)", "scope varchar(32)", "pageSize int4",
						"maxEntries int4", "attributeNames text", "searchAttributes varchar(255)");

		createTable("AddressGoogleStore", "id int4 primary key references AddressStore(id)",
						"url varchar(255) not null", "authUser varchar(255) not null",
						"authPassword varchar(255) not null");

		createTable("Party", "category varchar(8) not null", "partyId serial primary key", "userId int4");

		createTable("PhoneNumber", "addressId int4 not null", "category varchar(32) not null", "id serial primary key",
						"internalNumber varchar(80)", "number varchar(80)");

		createIndex("PhoneNumber", "addressId");
		createIndex("PhoneNumber", "number");
		createIndex("PhoneNumber", "internalNumber");
	}

	@Override
	public void createData(PersistentFactory persistentFactory, Connection connection, Logger logger,
					ModelRequest request) throws ModelException, PersistenceException, SQLException
	{
		createComponentSecurity("de.iritgo.aktera.address.ui.GetPhoneNumbersByStoreAndAddress", "user", "*");

		createInstanceSecurity("de.iritgo.aktera.ui.form.Edit", "aktera.address.edit", "user", "*");
		createInstanceSecurity("de.iritgo.aktera.ui.form.Save", "aktera.address.save", "user", "*");
		createInstanceSecurity("de.iritgo.aktera.ui.form.Delete", "aktera.address.delete", "user", "*");
		createInstanceSecurity("de.iritgo.aktera.ui.listing.List", "aktera.address.list", "user", "*");
		createInstanceSecurity("de.iritgo.aktera.ui.listing.List", "aktera.address.manage", "user", "*");
		createInstanceSecurity("de.iritgo.aktera.ui.listing.List", "aktera.address.callmanager.list", "user", "*");

		update("INSERT INTO Party (partyId, userId, category) values (0, 0, 'P')");
		update("INSERT INTO Address (id, partyId, category, lastName, email)"
						+ " values (0, 0, 'G', 'Anonymous', 'Anonymous@unknown')");

		update("INSERT INTO Party (partyId, userId, category) values (1, 1, 'P')");
		update("INSERT INTO Address (id, partyId, category, lastName, email)"
						+ " values (1, 1, 'G', 'Administrator', 'administrator@unknown')");

		update("INSERT INTO Party (partyId, userId, category) values (2, 2, 'P')");
		update("INSERT INTO Address (id, partyId, category, lastName, email)"
						+ " values (2, 2, 'G', 'Manager', 'manager@unknown')");

		update("ALTER SEQUENCE Party_partyId_seq START WITH 3");
		update("ALTER SEQUENCE Address_id_seq START WITH 3");

		Integer userGroupId = selectInt("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_USER + "'");

		insert("AddressStore", "name", "'de.iritgo.aktera.address.AddressLocalGlobalStore'", "type",
						"'de.iritgo.aktera.address.entity.AddressDAOStore'", "title", "'$AkteraAddress:global'",
						"position", "1", "systemStore", "true", "defaultStore", "true", "editable", "true",
						"numberLookup", "true", "emptysearchreturnsallentries", "true");

		insert("AddressDAOStore", "id", String.valueOf(selectInt("AddressStore", "id",
						"name = 'de.iritgo.aktera.address.AddressLocalGlobalStore'")), "category", "'G'", "checkOwner",
						"false");

		insert("AddressStore", "name", "'de.iritgo.aktera.address.AddressLocalPrivateStore'", "type",
						"'de.iritgo.aktera.address.entity.AddressDAOStore'", "title", "'$AkteraAddress:private'",
						"position", "2", "systemStore", "true", "defaultStore", "true", "editable", "true",
						"numberLookup", "true", "emptysearchreturnsallentries", "true");

		insert("AddressDAOStore", "id", String.valueOf(selectInt("AddressStore", "id",
						"name = 'de.iritgo.aktera.address.AddressLocalPrivateStore'")), "category", "'P'",
						"checkOwner", "true");

		createPermission("G", userGroupId, "de.iritgo.aktera.address.view",
						"de.iritgo.aktera.address.entity.AddressStore", selectInt("AddressStore", "id",
										"name = 'de.iritgo.aktera.address.AddressLocalGlobalStore'"));

		createPermission("G", userGroupId, "de.iritgo.aktera.address.*",
						"de.iritgo.aktera.address.entity.AddressStore", selectInt("AddressStore", "id",
										"name = 'de.iritgo.aktera.address.AddressLocalPrivateStore'"));

		Integer managerGroupId = selectInt("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_MANAGER + "'");

		createPermission("G", managerGroupId, "de.iritgo.aktera.address.*",
						"de.iritgo.aktera.address.entity.AddressStore", selectInt("AddressStore", "id",
										"name = 'de.iritgo.aktera.address.AddressLocalGlobalStore'"));
	}
}
