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


import java.sql.Connection;
import java.util.*;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import de.iritgo.aktera.address.entity.AddressLDAPStore;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroup;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.*;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


public class ModuleUpdateHandler extends UpdateHandler
{
	@Override
	public void updateDatabase(ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between("0.0.0", "2.1.2"))
		{
			// Remove version entry of obsolete module 'aktera-addressbook'
			update("DELETE FROM Version where name = 'aktera-addressbook'");

			// Rename Address primary key column to 'id'
			renameColumn("Address", "addressId", "id");

			// Rename PhoneNumber primary key column to 'id'
			renameColumn("PhoneNumber", "phoneNumberId", "id");

			// Remove obsolete AddressBook entity. The categories 'G' and 'P',
			// the
			// owner and the remark are now stored in new fields of the Address
			// entity.
			update("ALTER TABLE Address ADD ownerId int");
			update("ALTER TABLE Address ADD remark text");

			List<Map<String, ?>> res = (List<Map<String, ?>>) new QueryRunner().query(connection,
							"SELECT partyId, category, ownerid FROM Addressbook", new MapListHandler());

			for (Map<String, ?> row : res)
			{
				update("UPDATE Address SET category = ?, ownerId = ?, remark = ? where partyId = ?", new Object[]
				{
					row.get("category"), "G".equals(row.get("category")) ? null : row.get("ownerId"),
					row.get("remark"), row.get("partyId")
				});
			}

			update("DROP TABLE Addressbook");

			// PhoneNumber entities now contain a foreign key to Address
			// entities
			// instead of Party entities.
			update("ALTER TABLE PhoneNumber ADD addressId int");

			res = (List<Map<String, ?>>) new QueryRunner()
							.query(connection,
											"SELECT PhoneNumber.id as phoneNumberId, Address.id as addressId FROM PhoneNumber LEFT JOIN Address ON PhoneNumber.partyId = Address.partyId",
											new MapListHandler());

			for (Map<String, ?> row : res)
			{
				update("UPDATE PhoneNumber set addressId = ? where id = ?", new Object[]
				{
					row.get("addressId"), row.get("phoneNumberId")
				});
			}

			update("ALTER TABLE PhoneNumber DROP COLUMN partyId");

			currentVersion.setVersion("2.1.2");
		}

		if (currentVersion.between("2.1.2", "2.1.3"))
		{
			// Update Permissions: Convert address permissions to the new format
			List<Map<String, ?>> res = (List<Map<String, ?>>) new QueryRunner().query(connection,
							"SELECT permissionId, permission FROM Permission", new MapListHandler());

			for (Map<String, ?> row : res)
			{
				String permission = (String) row.get("permission");

				permission = permission.replaceAll("addressbook", "address");
				update("UPDATE Permission set permission = ? where permissionId = ?", new Object[]
				{
					permission, row.get("permissionId")
				});
			}

			currentVersion.setVersion("2.1.3");
		}

		if (currentVersion.between("2.1.3", "2.1.4"))
		{
			// Address DO extensions (based on GESIS data export)
			update("ALTER TABLE Address ALTER COLUMN email TYPE varchar(255)");
			update("ALTER TABLE Address ALTER COLUMN company TYPE varchar(255)");

			if (! hasColumn("Address", "contactNumber"))
			{
				update("ALTER TABLE Address ADD contactNumber varchar(80)");
				update("ALTER TABLE Address ADD companyNumber varchar(80)");
				update("ALTER TABLE Address ADD sourceSystemId varchar(80)");
				update("ALTER TABLE Address ADD sourceSystemClient varchar(80)");
				update("ALTER TABLE Address ADD lastModified abstime");
			}

			currentVersion.setVersion("2.1.4");
		}

		if (currentVersion.between("2.1.4", "2.1.5"))
		{
			update("ALTER TABLE Address ALTER COLUMN lastName TYPE varchar(255)");
			update("ALTER TABLE Address ALTER COLUMN internalLastName TYPE varchar(255)");
			update("ALTER TABLE Address ALTER COLUMN company TYPE varchar(255)");
			update("ALTER TABLE Address ALTER COLUMN internalCompany TYPE varchar(255)");

			currentVersion.setVersion("2.1.5");
		}

		if (currentVersion.lessThan("2.2.1"))
		{
			createPrimaryKeySequenceFromIdTable("Address", "id");
			createPrimaryKeySequenceFromIdTable("Party", "partyId");
			createPrimaryKeySequenceFromIdTable("PhoneNumber", "id");

			update("DELETE FROM ids where table_name = 'Addressbook'");

			// New data object AddressDataSource.
			createTable("AddressDataSource", "id serial primary key", "name varchar(255)", "type varchar(32) not null",
							"localCategory varchar(255)", "localCheckOwner boolean", "ldapAuthDn varchar(255)",
							"ldapAuthPassword varchar(255)", "ldapBaseDn varchar(255)", "ldapHost varchar(255)",
							"ldapPort int4", "ldapQuery varchar(255)", "ldapScope varchar(32)", "ldapPageSize int4",
							"ldapMaxEntries int4", "ldapAttributeNames text", "ldapSearchAttributes varchar(255)");

			// Column lastModified is used by hibernate for optimistic locking.
			// Currently we have no optimistic locking strategy implemented.
			// Hibernate is confused about this, so we drop this column.
			dropColumn("Address", "lastModified");

			// Address categories are now user definable. More characters are
			// needed.
			updateColumnType("Address", "category", "varchar(80)");

			// Remove the not null constrained from Address.partyId, so that
			// addresses
			// can exist without a party reference.
			// Delete all party objects that don't belong to a user and null
			// their
			// references is the associated address objects.
			update("ALTER TABLE Address ALTER COLUMN partyId DROP NOT NULL");
			update("UPDATE Address SET partyId=NULL WHERE NOT partyId IS NULL AND (SELECT userId FROM Party WHERE Party.partyId = Address.partyId) IS NULL");
			update("DELETE FROM Party where userId IS NULL");

			createIndex("Address", "firstName");
			createIndex("Address", "lastName");
			createIndex("Address", "street");
			createIndex("Address", "city");

			createIndex("PhoneNumber", "addressId");
			createIndex("PhoneNumber", "number");
			createIndex("PhoneNumber", "internalNumber");

			// Eventually missing not-null constraint
			update("ALTER TABLE PhoneNumber ALTER COLUMN addressId SET NOT NULL");

			// Some permission names have changed
			for (Map<String, Object> row : (List<Map<String, Object>>) query(
							"SELECT permissionId, permission FROM Permission", new MapListHandler()))
			{
				String permission = (String) row.get("permission");
				if (permission.startsWith("de.buerobyte.aktera.address."))
				{
					String newPermission = "de.iritgo.aktera.address." + permission.substring(28);
					update("UPDATE Permission SET permission = '" + newPermission + "' WHERE permissionId = "
									+ row.get("permissionId"));
				}
			}

			currentVersion.setVersion("2.2.1");
		}

		if (currentVersion.between("2.2.1", "2.2.2"))
		{
			addColumn("AddressDataSource", "numberLookup", "boolean");
			addColumn("AddressDataSource", "serverUrl", "varchar(255)");
			addColumn("AddressDataSource", "authUser", "varchar(255)");
			addColumn("AddressDataSource", "authPassword", "varchar(255)");
			addIncrementedIntColumn("AddressDataSource", "position", "id");

			currentVersion.setVersion("2.2.2");
		}

		if (currentVersion.between("2.2.2", "2.2.3"))
		{
			setNewUserPreferences();
			currentVersion.setVersion("2.2.3");
		}

		if (currentVersion.between("2.2.2", "2.2.4"))
		{
			addColumn("AddressDataSource", "emptySearchReturnsAllEntries", "boolean");

			update("UPDATE AddressDataSource set emptySearchReturnsAllEntries = false");

			setReboot();

			currentVersion.setVersion("2.2.4");
		}

		if (currentVersion.lessThan("2.3.1"))
		{
			createTable("AddressStore", "id serial primary key", "name varchar(255) not null",
							"type varchar(255) not null", "title varchar(255)", "position int4 not null",
							"systemStore boolean not null", "defaultStore boolean not null",
							"editable boolean not null", "numberLookup boolean not null",
							"emptySearchReturnsAllEntries boolean not null");

			createIndex("AddressStore", "name");

			createTable("AddressDAOStore", "id int4 primary key references AddressStore(id)",
							"category varchar(255) not null", "checkOwner boolean not null");

			createTable("AddressLDAPStore", "id int4 primary key references AddressStore(id)",
							"host varchar(255) not null", "port int4", "authDn varchar(255)",
							"authPassword varchar(255)", "baseDn varchar(255) not null", "query varchar(255)",
							"scope varchar(32)", "pageSize int4", "maxEntries int4", "attributeNames text",
							"searchAttributes varchar(255)");

			createTable("AddressGoogleStore", "id int4 primary key references AddressStore(id)",
							"url varchar(255) not null", "authUser varchar(255) not null",
							"authPassword varchar(255) not null");

			insert("AddressStore", "name", "'de.iritgo.aktera.address.AddressLocalGlobalStore'", "type",
							"'de.iritgo.aktera.address.entity.AddressDAOStore'", "title", "'$AkteraAddress:global'",
							"position", "1", "systemStore", "true", "defaultStore", "true", "editable", "true",
							"numberLookup", "true", "emptysearchreturnsallentries", "true");

			insert("AddressDAOStore", "id", String.valueOf(selectInt("AddressStore", "id",
							"name = 'de.iritgo.aktera.address.AddressLocalGlobalStore'")), "category", "'G'",
							"checkOwner", "false");

			insert("AddressStore", "name", "'de.iritgo.aktera.address.AddressLocalPrivateStore'", "type",
							"'de.iritgo.aktera.address.entity.AddressDAOStore'", "title", "'$AkteraAddress:private'",
							"position", "2", "systemStore", "true", "defaultStore", "true", "editable", "true",
							"numberLookup", "true", "emptysearchreturnsallentries", "true");

			insert("AddressDAOStore", "id", String.valueOf(selectInt("AddressStore", "id",
							"name = 'de.iritgo.aktera.address.AddressLocalPrivateStore'")), "category", "'P'",
							"checkOwner", "true");

			Integer userGroupId = selectInt("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_USER + "'");
			Integer managerGroupId = selectInt("AkteraGroup", "id", "name = '" + AkteraGroup.GROUP_NAME_MANAGER + "'");

			Map<String, String> oldToNewTypes = new HashMap();
			oldToNewTypes.put("local", "de.iritgo.aktera.address.entity.AddressDAOStore");
			oldToNewTypes.put("ldap", "de.iritgo.aktera.address.entity.AddressLDAPStore");
			oldToNewTypes.put("google", "de.iritgo.aktera.address.entity.AddressGoogleStore");
			int position = 3;
			for (Map<String, Object> row : (List<Map<String, Object>>) query(
							"select * from AddressDataSource order by position", new MapListHandler()))
			{
				String name = (String) row.get("name");
				String oldType = (String) row.get("type");
				if (oldToNewTypes.get(oldType) == null)
				{
					logger.error("Unknown address data source type '" + oldType + "' not upated");
					continue;
				}
				insert("AddressStore", "name", "'" + name + "'", "type", "'" + oldToNewTypes.get(oldType) + "'",
								"position", "" + (position++), "systemStore", "false", "defaultStore", "false",
								"editable", "local".equals(oldType) ? "true" : "false", "numberLookup",
								"" + row.get("numberLookup"), "emptySearchReturnsAllEntries",
								"" + row.get("emptySearchReturnsAllEntries"));
				int id = selectInt("AddressStore", "id", "name = '" + name + "'");
				if ("local".equals(oldType))
				{
					insert("AddressDAOStore", "id", "" + id, "category", "'" + row.get("localCategory") + "'",
									"checkOwner", "'" + row.get("localCheckOwner") + "'");
				}
				else if ("ldap".equals(oldType))
				{
					insert("AddressLDAPStore",
									"id",
									"" + id,
									"host",
									"'" + row.get("ldapHost") + "'",
									"port",
									"" + (0 + NumberTools.toInt(row.get("ldapPort"), 0)),
									"authDn",
									"'" + row.get("ldapAuthDn") + "'",
									"authPassword",
									"'" + row.get("ldapAuthPassword") + "'",
									"baseDn",
									"'" + row.get("ldapBaseDn") + "'",
									"query",
									"'" + row.get("ldapQuery") + "'",
									"scope",
									"'"
													+ ("level".equals(row.get("ldapScope")) ? AddressLDAPStore.SearchScope.LEVEL
																	: AddressLDAPStore.SearchScope.SUBTREE) + "'",
									"pageSize", "" + (0 + NumberTools.toInt(row.get("ldapPageSize"), 0)), "maxEntries",
									"" + (0 + NumberTools.toInt(row.get("ldapMaxEntries"), 0)), "attributeNames", "'"
													+ row.get("ldapAttributeNames") + "'", "searchAttributes", "'"
													+ row.get("ldapSearchAttributes") + "'");
				}
				else if ("google".equals(oldType))
				{
					insert("AddressGoogleStore", "id", "" + id, "url", "'" + row.get("serverUrl") + "'", "authUser",
									"'" + row.get("authUser") + "'", "authPassword", "'" + row.get("authPassword")
													+ "'");
				}

				createPermission("G", managerGroupId, "de.iritgo.aktera.address.*",
								"de.iritgo.aktera.address.entity.AddressStore",
								selectInt("AddressStore", "id", "name = '" + name + "'"));

				createPermission("G", userGroupId, "de.iritgo.aktera.address.view",
								"de.iritgo.aktera.address.entity.AddressStore",
								selectInt("AddressStore", "id", "name = '" + name + "'"));
			}

			dropTable("AddressDataSource");

			createPermission(
							"G",
							userGroupId,
							"de.iritgo.aktera.address.view",
							"de.iritgo.aktera.address.entity.AddressStore",
							selectInt("AddressStore", "id", "name = 'de.iritgo.aktera.address.AddressLocalGlobalStore'"));

			createPermission(
							"G",
							userGroupId,
							"de.iritgo.aktera.address.*",
							"de.iritgo.aktera.address.entity.AddressStore",
							selectInt("AddressStore", "id",
											"name = 'de.iritgo.aktera.address.AddressLocalPrivateStore'"));

			createPermission(
							"G",
							managerGroupId,
							"de.iritgo.aktera.address.*",
							"de.iritgo.aktera.address.entity.AddressStore",
							selectInt("AddressStore", "id", "name = 'de.iritgo.aktera.address.AddressLocalGlobalStore'"));

			update("delete from Permission where permission like 'de.iritgo.aktera.address.global%'");

			deleteComponentSecurity("de.iritgo.aktera.address.ui.GetPhoneNumbers", "user");
			createComponentSecurity("de.iritgo.aktera.address.ui.GetPhoneNumbersByStoreAndAddress", "user", "*");

			deleteComponentSecurity("de.iritgo.aktera.address.ui.CreateDefaultPhoneNumbers", "user");
			deleteComponentSecurity("de.iritgo.aktera.address.ui.LoadDefaultPhoneNumbers", "user");
			deleteComponentSecurity("de.iritgo.aktera.address.ui.UpdateDefaultPhoneNumbers", "user");

			currentVersion.setVersion("2.3.1");
		}

		if (currentVersion.lessThan("2.3.2"))
		{
			for (Map<String, Object> row : (List<Map<String, Object>>) query("select * from AddressLdapStore",
							new MapListHandler()))
			{
				update("update AddressLdapStore set authPassword='" + StringTools.encode(row.get("authPassword").toString())
								+ "' where id=" + row.get("id"));
			}
			setReboot();
			currentVersion.setVersion("2.3.2");
		}

		if (currentVersion.between("2.3.2", "2.3.3"))
		{
			addColumn("AddressStore", "numberNormalization", "varchar(64)");
			update("UPDATE AddressStore set numberNormalization = 'NO_NORMALIZATION'");

			setReboot();
			currentVersion.setVersion("2.3.3");
		}

		if (currentVersion.between("2.3.3", "2.3.4"))
		{
			updateColumnToNotNull("AddressStore", "numberNormalization");

			currentVersion.setVersion("2.3.4");
		}

		if (currentVersion.between("2.3.4", "2.3.5"))
		{
			addColumn("AddressStore", "mainNumber", "varchar(255)");
			addColumn("AddressStore", "internalNumberLength", "int4");

			currentVersion.setVersion("2.3.5");
		}
}
}
