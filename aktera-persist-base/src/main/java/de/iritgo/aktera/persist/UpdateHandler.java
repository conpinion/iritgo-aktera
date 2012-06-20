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

package de.iritgo.aktera.persist;


import java.io.*;
import java.sql.*;
import java.util.*;
import lombok.*;
import org.apache.avalon.framework.logger.*;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import de.iritgo.aktera.core.exception.*;
import de.iritgo.aktera.crypto.*;
import de.iritgo.aktera.model.*;
import de.iritgo.simplelife.collection.*;
import de.iritgo.simplelife.math.*;
import de.iritgo.simplelife.process.*;
import de.iritgo.simplelife.string.*;


/**
 * Database update handler.
 */
public class UpdateHandler
{
	/** Set to true if the system should reboot after the update */
	protected boolean rebootNeeded = false;

	/** Set to true if the user preferences have changed */
	protected boolean newUserPreferences = false;

	/** Database connection */
	@Setter
	protected Connection connection;

	protected void addColumn(String tableName, String columnName, String typeDef) throws SQLException
	{
		update("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + typeDef);
	}

	protected void addColumn(String tableName, String columnName, String typeDef, String defaultValue)
		throws SQLException
	{
		addColumn(tableName, columnName, typeDef);
		new QueryRunner().update(connection, "UPDATE " + tableName + " SET " + columnName + " = " + defaultValue);
	}

	protected void addField(Persistent persistent, String fieldName) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().addField(persistentMeta, fieldName);
	}

	protected void addField(Persistent persistent, String fieldName, Object defaultValue) throws PersistenceException
	{
		addField(persistent, fieldName);

		for (Iterator i = persistent.query().iterator(); i.hasNext();)
		{
			Persistent p = (Persistent) i.next();

			p.setField(fieldName, defaultValue);
			p.update();
		}
	}

	protected void addFields(Persistent persistent, String[] fieldNames) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().addFields(persistentMeta, fieldNames);
	}

	/**
	 * Describe method addIncrementedIntColumn() here.
	 *
	 * @param tableName
	 * @param columnName
	 * @param idColumnName
	 *
	 * @throws SQLException
	 */
	protected void addIncrementedIntColumn(String tableName, String columnName, String idColumnName)
		throws SQLException
	{
		addColumn(tableName, columnName, "int4");

		int count = 1;
		List<Map<String, ?>> res = (List<Map<String, ?>>) new QueryRunner().query(connection, "select " + idColumnName
						+ " from " + tableName, new MapListHandler());

		for (Map<String, ?> row : res)
		{
			update("update " + tableName + " set " + columnName + " = ? where " + idColumnName + " = ?", new Object[]
			{
				count++, row.get(idColumnName)
			});
		}

		update("alter table " + tableName + " alter column " + columnName + " set not null");
	}

	protected boolean containsField(Persistent persistent, String fieldName) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		return persistentMeta.getDatabaseType().containsField(persistentMeta, fieldName);
	}

	protected long count(String tableName, String where) throws SQLException
	{
		return (Long) query("select count(*) from " + tableName + " where " + where, new ScalarHandler());
	}

	protected void createAutoIncrement(String persistentName, int initialId) throws SQLException
	{
		QueryRunner run = new QueryRunner();

		run.update(connection, "INSERT INTO ids (table_name, next_id) values (?, ?)", new Object[]
		{
			persistentName, initialId
		});
	}

	protected void createAutoIncrement(Persistent persistent, int initialId) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		Persistent idsPersistent = persistentMeta.getFactory().create("ids.ids");

		idsPersistent.setField("table_name", persistent.getName().substring(persistent.getName().lastIndexOf(".") + 1));
		idsPersistent.setField("next_id", new Integer(initialId));
		idsPersistent.add();
	}

	protected void createComponentSecurity(String component, String groupName, String operationsAllowed)
		throws SQLException
	{
		update("INSERT INTO componentsecurity (component, groupname, operationsallowed) VALUES (?, ?, ?)", new Object[]
		{
			component, groupName, operationsAllowed
		});
	}

	protected void createConfigEntry(String tableName, String category, String name, String type, String value)
		throws SQLException
	{
		update("INSERT INTO " + tableName + " (category, name, type, value) VALUES (?, ?, ?, ?)", new Object[]
		{
			category, name, type, value
		});
	}

	protected void createConfigEntry(String tableName, String category, String name, String type, String value,
					String validValues) throws SQLException
	{
		update("INSERT INTO " + tableName + " (category, name, type, value, validValues) VALUES (?, ?, ?, ?, ?)",
						new Object[]
						{
							category, name, type, value, validValues
						});
	}

	protected void createConfigEntryWithKeelId(String tableName, String category, String name, String type, String value)
		throws SQLException
	{
		update("INSERT INTO " + tableName + " (id, category, name, type, value) VALUES (?, ?, ?, ?, ?)", new Object[]
		{
			createKeelId(tableName), category, name, type, value
		});
	}

	protected void createConfigEntryWithKeelId(String tableName, String category, String name, String type,
					String value, String validValues) throws SQLException
	{
		update("INSERT INTO " + tableName + " (id, category, name, type, value, validValues) VALUES (?, ?, ?, ?, ?, ?)",
						new Object[]
						{
							createKeelId(tableName), category, name, type, value, validValues
						});
	}

	protected void deleteConfigEntry(String tableName, String category, String name) throws SQLException
	{
		update("DELETE FROM " + tableName + " WHERE category = ? AND name = ?", new Object[]
		{
			category, name
		});
	}

	protected void createIndex(String tableName, String columnName) throws SQLException
	{
		update("CREATE INDEX " + tableName + "_" + columnName + "_idx ON " + tableName + " (" + columnName + ")");
	}

	protected void createInstanceSecurity(String component, String instance, String groupName, String operationsAllowed)
		throws SQLException
	{
		update("INSERT INTO instancesecurity (component, instance, groupname, operationsallowed) VALUES (?, ?, ?, ?)",
						new Object[]
						{
							component, instance, groupName, operationsAllowed
						});
	}

	protected void createInvokationSecurity(String component, String instance, String property, String comparator,
					String value, String groupName, String operationsAllowed) throws SQLException
	{
		update("INSERT INTO invokationsecurity (component, instance, property, comparator, value, groupname, operationsallowed) VALUES (?, ?, ?, ?, ?, ?, ?)",
						new Object[]
						{
							component, instance, property, comparator, value, groupName, operationsAllowed
						});
	}

	protected synchronized int createKeelId(String persistentName) throws SQLException
	{
		QueryRunner run = new QueryRunner();
		int nextId = NumberTools.toInt(run.query(connection, "SELECT next_id from ids where table_name = '"
						+ persistentName + "'", new ScalarHandler()), 0);

		run.update(connection, "UPDATE ids SET next_id = " + (nextId + 1) + " WHERE table_name = '" + persistentName
						+ "'");

		return nextId;
	}

	protected void createKeelIdColumn(String persistentName) throws SQLException
	{
		String tableName = persistentName.toLowerCase();
		String columnName = "id";
		QueryRunner run = new QueryRunner();

		run.update(connection, "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + "_tmp serial");
		run.update(connection, "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " int NOT NULL default 0");
		run.update(connection, "UPDATE " + tableName + " SET " + columnName + " = CAST(" + columnName
						+ "_tmp AS integer)");
		run.update(connection, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName + "_tmp");

		int maxId = NumberTools.toInt(
						run.query(connection, "SELECT MAX(" + columnName + ") from " + tableName, new ScalarHandler()),
						0);

		run.update(connection, "INSERT INTO ids (table_name, next_id) values (?, ?)", new Object[]
		{
			persistentName, maxId + 1
		});
	}

	protected void createPrimaryKeySequenceFromIdTable(String table, String idColumn) throws SQLException
	{
		int nextId = NumberTools.toInt(
						new QueryRunner().query(connection, "SELECT next_id from ids where table_name = '" + table
										+ "'", new ScalarHandler()), - 1);

		if (nextId == - 1)
		{
			throw new SQLException("Unable to find id with name '" + table + "' in the ids table.");
		}

		String seqName = table + "_" + idColumn + "_seq";

		update("CREATE SEQUENCE " + seqName + " START " + nextId);
		update("ALTER TABLE " + table + " ALTER COLUMN " + idColumn + " SET DEFAULT nextval('" + seqName + "')");
		update("DELETE FROM ids where table_name = '" + table + "'");
	}

	protected void createSystemConfigEntry(String category, String name, String type, String value) throws SQLException
	{
		createConfigEntry("SystemConfig", category, name, type, value);
	}

	protected void createSystemConfigEntry(String category, String name, String type, String value, String validValues)
		throws SQLException
	{
		createConfigEntry("SystemConfig", category, name, type, value, validValues);
	}

	protected void createSystemConfigEntryIfNotExists(String category, String name, String type, String value)
		throws SQLException
	{
		long count = (Long) query("SELECT count (*) FROM SystemConfig WHERE category = '" + category + "' AND name = '"
						+ name + "'", new ScalarHandler());

		if (count == 0)
		{
			createConfigEntry("SystemConfig", category, name, type, value);
		}
	}

	protected void createSystemConfigEntryIfNotExists(String category, String name, String type, String value,
					String validValues) throws SQLException
	{
		long count = (Long) query("SELECT count (*) FROM SystemConfig WHERE category = '" + category + "' AND name = '"
						+ name + "'", new ScalarHandler());

		if (count == 0)
		{
			createConfigEntry("SystemConfig", category, name, type, value, validValues);
		}
	}

	protected void createSystemConfigEntryWithKeelId(String category, String name, String type, String value)
		throws SQLException
	{
		createConfigEntryWithKeelId("SystemConfig", category, name, type, value);
	}

	protected void createSystemConfigEntryWithKeelId(String category, String name, String type, String value,
					String validValues) throws SQLException
	{
		createConfigEntryWithKeelId("SystemConfig", category, name, type, value, validValues);
	}

	protected void deleteSystemConfigEntry(String category, String name) throws SQLException
	{
		deleteConfigEntry("SystemConfig", category, name);
	}

	protected void createTable(String tableName, String... columnDefinitions) throws SQLException
	{
		StringBuilder sb = new StringBuilder();
		StringTools.appendWithDelimiter(sb, columnDefinitions, ", ");
		update("CREATE TABLE " + tableName + " (" + sb.toString() + ")");
	}

	protected void createTable(Persistent persistent) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().createTable(persistentMeta, persistentMeta.getDataSource());
		persistentMeta.getDatabaseType().createIndices(persistentMeta, persistentMeta.getDataSource());
	}

	protected void createUserPreference(Integer userId, String category, String name, String type, String value,
					String validValues) throws SQLException
	{
		update("insert into PreferencesConfig (userId, category, name, type, value, validValues) values (?, ?, ?, ?, ?, ?)",
						new Object[]
						{
							userId, category, name, type, value, validValues
						});
	}

	protected void deleteComponentSecurity(String component, String groupName) throws SQLException
	{
		update("DELETE FROM componentsecurity WHERE component = ? AND groupname = ?", new Object[]
		{
			component, groupName
		});
	}

	protected void deleteField(Persistent persistent, String fieldName) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().deleteField(persistentMeta, fieldName);
	}

	protected void deleteInstanceSecurity(String component, String instance, String groupName) throws SQLException
	{
		update("DELETE FROM instancesecurity WHERE component = ? AND instance = ? AND groupname = ?", new Object[]
		{
			component, instance, groupName
		});
	}

	protected void dropColumn(String tableName, String columnName) throws SQLException
	{
		update("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	protected void dropTable(String tableName) throws SQLException
	{
		update("DROP TABLE " + tableName);
	}

	protected void dropTable(Persistent persistent) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().dropTable(persistentMeta);
	}

	/**
	 * Encode the password: hash->base64->trim
	 *
	 * @param request
	 * @param password
	 * @return
	 * @throws ModelException
	 */
	protected String encodePassword(ModelRequest request, String password) throws ModelException
	{
		Encryptor oneEncryptor = (Encryptor) request.getService(Encryptor.ROLE, "base64");

		String encrypted = password;

		try
		{
			encrypted = new String(oneEncryptor.hash(password.getBytes("UTF-8")), "UTF-8");
			encrypted = new String(oneEncryptor.encrypt(encrypted.getBytes("UTF-8")), "UTF-8");

			if (encrypted.length() > password.length())
			{
				encrypted = encrypted.substring(0, password.length());
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (NestedException e)
		{
			e.printStackTrace();
		}

		return encrypted;
	}

	protected void forEachUser(Procedure1<Map<String, Object>> procedure) throws Exception
	{
		List<Map<String, Object>> res;
		res = (List<Map<String, Object>>) new QueryRunner().query(connection, "select * from keelusers",
						new MapListHandler());
		for (Map<String, Object> row : res)
		{
			Long iritgoUserId = (Long) query("select id from IritgoUser where name = '" + row.get("username") + "'",
							new ScalarHandler());
			if (iritgoUserId != null)
			{
				row.put("iritgouserid", iritgoUserId);
			}
			procedure.execute(row);
		}
	}

	protected boolean hasColumn(String tableName, String columnName)
	{
		try
		{
			query("SELECT count (" + columnName + ") FROM " + tableName, new ScalarHandler());
		}
		catch (SQLException x)
		{
			return false;
		}

		return true;
	}

	protected boolean hasField(Persistent persistent, String fieldName) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		return persistentMeta.getDatabaseType().hasField(persistentMeta, fieldName);
	}

	public boolean hasNewUserPreferences()
	{
		return newUserPreferences;
	}

	protected void insert(String tableName, String... fieldsAndValues) throws SQLException
	{
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (Pair<String, String> fieldAndValue : new PairwiseIterator<String, String, String>(fieldsAndValues))
		{
			StringTools.appendWithDelimiter(fields, fieldAndValue.get1(), ",");
			StringTools.appendWithDelimiter(values, fieldAndValue.get2(), ",");
		}
		update("INSERT INTO " + tableName + " (" + fields + ") values (" + values + ")");
	}

	protected void insertIfNoThere(String tableName, String... fieldsAndValues) throws SQLException
	{
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder where = new StringBuilder();
		for (Pair<String, String> fieldAndValue : new PairwiseIterator<String, String, String>(fieldsAndValues))
		{
			StringTools.appendWithDelimiter(fields, fieldAndValue.get1(), ",");
			StringTools.appendWithDelimiter(values, fieldAndValue.get2(), ",");
			StringTools.appendWithDelimiter(where, fieldAndValue.get1() + "=" + fieldAndValue.get2(), " AND ");
		}
		if (count(tableName, where.toString()) == 0)
		{
			update("INSERT INTO " + tableName + " (" + fields + ") values (" + values + ")");
		}
	}

	public boolean needReboot()
	{
		return rebootNeeded;
	}

	protected Object query(String sql, ResultSetHandler handler) throws SQLException
	{
		return new QueryRunner().query(connection, sql, handler);
	}

	protected void renameColumn(String tableName, String oldName, String newName) throws SQLException
	{
		update("ALTER table " + tableName + " RENAME COLUMN " + oldName + " TO " + newName);
	}

	protected void renameField(Persistent persistent, String oldFieldName, String newFieldName)
		throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().renameField(persistentMeta, oldFieldName, newFieldName);
	}

	protected void renameIdColumn(String tableName, String oldIdColumnName, String newIdColumnName) throws SQLException
	{
		renameColumn(tableName, oldIdColumnName, newIdColumnName);
		renameTable(tableName + "_" + oldIdColumnName + "_seq", tableName + "_" + newIdColumnName + "_seq");
	}

	protected void renamePersistentId(String oldPersistentName, String newPersistentName) throws SQLException
	{
		update("UPDATE ids SET table_name = '" + newPersistentName + "' WHERE table_name = '" + oldPersistentName + "'");
	}

	protected void renameTable(String oldName, String newName) throws SQLException
	{
		update("ALTER table " + oldName + " RENAME TO " + newName);
	}

	protected void renameTable(Persistent persistent, String oldTableName, String newTableName)
		throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().renameTable(persistentMeta, oldTableName, newTableName);
	}

	protected String selectString(String tableName, String field, String where) throws SQLException
	{
		return String.valueOf(query("select " + field + " from " + tableName + " where " + where, new ScalarHandler()));
	}

	protected Integer selectInt(String tableName, String field, String where) throws SQLException
	{
		return NumberTools.toIntInstance(
						query("select " + field + " from " + tableName + " where " + where, new ScalarHandler()), - 1);
	}

	protected Long selectLong(String tableName, String field, String where) throws SQLException
	{
		return NumberTools.toLongInstance(
						query("select " + field + " from " + tableName + " where " + where, new ScalarHandler()), - 1);
	}

	protected Map<String, Object> selectMap(String tableName, String where) throws SQLException
	{
		return (Map<String, Object>) query("select * from " + tableName + " where " + where, new MapHandler());
	}

	protected List<Map<String, Object>> selectMapList(String tableName, String where) throws SQLException
	{
		return (List<Map<String, Object>>) query("select * from " + tableName + " where " + where, new MapListHandler());
	}

	protected void setNewUserPreferences()
	{
		newUserPreferences = true;
	}

	protected void setReboot()
	{
		rebootNeeded = true;
	}

	public void update(String sql) throws SQLException
	{
		new QueryRunner().update(connection, sql);
	}

	protected void update(String sql, Object... params) throws SQLException
	{
		new QueryRunner().update(connection, sql, params);
	}

	protected void updateColumnToNotNull(String tableName, String columnName) throws SQLException
	{
		update("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET NOT NULL");		
	}
	
	protected void updateColumnToNotNull(String tableName, String columnName, Object defaultValue) throws SQLException
	{
		String delim = (defaultValue instanceof String) ? "'" : "";

		update("UPDATE " + tableName + " SET " + columnName + " = " + delim + defaultValue + delim + " WHERE "
						+ columnName + " IS NULL");
		update("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET DEFAULT " + delim + defaultValue
						+ delim);
		update("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET NOT NULL");
	}

	protected void updateColumnType(String tableName, String columnName, String typeDef) throws SQLException
	{
		update("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE " + typeDef);
	}

	public void updateDatabase(@SuppressWarnings("unused")
	ModelRequest req, @SuppressWarnings("unused")
	Logger logger, @SuppressWarnings("unused")
	Connection connection, @SuppressWarnings("unused")
	PersistentFactory pf, @SuppressWarnings("unused")
	ModuleVersion currentVersion, @SuppressWarnings("unused")
	ModuleVersion newVersion) throws Exception
	{
	}

	protected void updateNextSequenceValue(String tableName, String idColumnName, long nextValue) throws SQLException
	{
		update("ALTER SEQUENCE " + tableName + "_" + idColumnName + "_seq RESTART WITH " + nextValue);
	}

	protected void updateTypeField(Persistent persistent, String fieldName) throws PersistenceException
	{
		PersistentMetaData persistentMeta = persistent.getMetaData();

		persistentMeta.getDatabaseType().updateTypeField(persistentMeta, fieldName);
	}

	protected void createPermission(String principalTYpe, Integer principalId, String permission) throws SQLException
	{
		insert("Permission", "principalType", "'" + principalTYpe + "'", "principalId", principalId.toString(),
						"permission", "'" + permission + "'", "negative", "false");
	}

	protected void createPermission(String principalTYpe, Integer principalId, String permission, String objectType,
					Integer objectId) throws SQLException
	{
		insert("Permission", "principalType", "'" + principalTYpe + "'", "principalId", principalId.toString(),
						"permission", "'" + permission + "'", "objectType", "'" + objectType + "'", "objectId",
						objectId.toString(), "negative", "false");
	}

	protected void createNegativePermission(String principalTYpe, Integer principalId, String permission,
					String objectType, Integer objectId) throws SQLException
	{
		insert("Permission", "principalType", "'" + principalTYpe + "'", "principalId", principalId.toString(),
						"permission", "'" + permission + "'", "objectType", "'" + objectType + "'", "objectId",
						objectId.toString(), "negative", "true");
	}
}
