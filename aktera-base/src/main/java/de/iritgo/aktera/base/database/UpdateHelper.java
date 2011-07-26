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

package de.iritgo.aktera.base.database;


import de.iritgo.aktera.configuration.preferences.KeelPreferencesManager;
import de.iritgo.aktera.event.EventManager;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.UpdateHandler;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.SystemTools;
import de.iritgo.simplelife.process.NullStreamHandler;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.dbutils.DbUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


/**
 *
 */
public class UpdateHelper
{
	public static boolean updateChecked = false;

	public static boolean updateNeeded = false;

	public static boolean needUpdate (ModelRequest req) throws ModelException, ConfigurationException
	{
		if (updateChecked)
		{
			return updateNeeded;
		}

		createVersionTableIfNotExists (req);

		PersistentFactory pf = (PersistentFactory) req.getService (PersistentFactory.ROLE, req.getDomain ());

		List<Configuration> moduleConfigs = de.iritgo.aktera.base.module.ModuleInfo
						.moduleConfigsSortedByDependency (req);

		for (Configuration moduleConfig : moduleConfigs)
		{
			String moduleId = moduleConfig.getAttribute ("id", "unkown");
			ModuleVersion newVersion = new ModuleVersion (moduleConfig.getChild ("version").getValue ());

			ModuleVersion currentVersion = new ModuleVersion ();

			try
			{
				Persistent version = pf.create ("aktera.Version");

				version.setField ("type", "M");
				version.setField ("name", moduleId);

				if (version.find ())
				{
					currentVersion = new ModuleVersion (version.getFieldString ("version"));
				}
				else
				{
					currentVersion = new ModuleVersion ("0.0.0");
				}
			}
			catch (Exception x)
			{
			}

			if (newVersion.greater (currentVersion))
			{
				updateChecked = true;
				updateNeeded = true;

				return true;
			}
		}

		updateChecked = true;
		updateNeeded = false;

		return false;
	}

	public static void createVersionTableIfNotExists (ModelRequest request) throws ModelException
	{
		PersistentFactory pf = (PersistentFactory) request.getService (PersistentFactory.ROLE, request.getDomain ());

		Persistent version = null;

		try
		{
			version = pf.create ("aktera.Version");
			version.setField ("type", "M");
			version.setField ("name", "dummy");
			version.find ();
		}
		catch (Exception x)
		{
			try
			{
				PersistentMetaData versionMeta = version.getMetaData ();

				versionMeta.getDatabaseType ().createTable (versionMeta, versionMeta.getDataSource ());
			}
			catch (PersistenceException xx)
			{
				throw new ModelException (xx);
			}
		}
	}

	public static void update (ModelRequest request, ModelResponse response, Logger logger)
		throws ModelException, ServiceException
	{
		Output outModuleList = response.createOutput ("modules");

		response.add (outModuleList);

		PersistentFactory pf = (PersistentFactory) request.getService (PersistentFactory.ROLE, request.getDomain ());

		DataSourceComponent dataSourceComponent = (DataSourceComponent) request.getService (DataSourceComponent.ROLE,
						"keel-dbpool");

		updatePreProcessing (request, dataSourceComponent);

		boolean needReboot = false;
		boolean newUserPreferences = false;

		List<Configuration> moduleConfigs = de.iritgo.aktera.base.module.ModuleInfo
						.moduleConfigsSortedByDependency (request);

		for (Configuration moduleConfig : moduleConfigs)
		{
			String moduleId = moduleConfig.getAttribute ("id", "unkown");
			String moduleName = moduleConfig.getChild ("name").getValue ("unkown");
			ModuleVersion newVersion = new ModuleVersion (moduleConfig.getChild ("version").getValue (null));

			Output outModule = response.createOutput ("module_" + moduleConfig.getAttribute ("id", "unknown"));

			outModuleList.add (outModule);
			outModule.setAttribute ("name", moduleName);
			outModule.setAttribute ("description", moduleConfig.getChild ("description").getValue (""));
			outModule.setAttribute ("newVersion", newVersion.toString ());

			ModuleVersion currentVersion = new ModuleVersion ();

			try
			{
				Persistent version = pf.create ("aktera.Version");

				version.setField ("type", "M");
				version.setField ("name", moduleId);

				if (version.find ())
				{
					currentVersion = new ModuleVersion (version.getFieldString ("version"));
				}
				else
				{
					currentVersion = new ModuleVersion ("0.0.0");
				}
			}
			catch (Exception x)
			{
				outModule.setAttribute ("oldVersion", currentVersion.toString ());
				outModule.setAttribute ("error", "unableToRetrieveCurrentVersion");
				outModule.setAttribute ("errorException", x.toString ());

				response.addOutput ("updateError", "Y");

				continue;
			}

			outModule.setAttribute ("oldVersion", currentVersion.toString ());

			String updateHandlerClassName = moduleConfig.getChild ("update").getAttribute ("class", null);

			if (updateHandlerClassName != null)
			{
				try
				{
					System.out.println ("UpdateDatabase: Updating module '" + moduleId + "' with handler '"
									+ updateHandlerClassName + "'");

					Class klass = Class.forName (updateHandlerClassName);

					if (klass != null)
					{
						UpdateHandler updateHandler = (UpdateHandler) klass.newInstance ();

						Connection con = null;

						try
						{
							con = dataSourceComponent.getConnection ();
							updateHandler.setConnection (con);
							updateHandler.updateDatabase (request, logger, con, pf, (ModuleVersion) currentVersion
											.clone (), newVersion);
						}
						finally
						{
							con.close ();
						}

						needReboot = needReboot || updateHandler.needReboot ();
						newUserPreferences = newUserPreferences || updateHandler.hasNewUserPreferences ();
					}
					else
					{
						System.out.println ("UpdateDatabase: Unable to find update handler for module '" + moduleId
										+ "'");
					}
				}
				catch (ClassNotFoundException x)
				{
					System.out.println ("UpdateDatabase: Unable call update handler for module '" + moduleId + "': "
									+ x);
				}
				catch (Exception x)
				{
					outModule.setAttribute ("error", "errorDuringUpdate");
					outModule.setAttribute ("errorException", x.toString ());

					response.addOutput ("updateError", "Y");

					continue;
				}
			}

			try
			{
				Persistent version = pf.create ("aktera.Version");

				version.setField ("type", "M");
				version.setField ("name", moduleId);

				if (version.find ())
				{
					version.setField ("version", newVersion.toString ());
					version.update ();
				}
				else
				{
					version.setField ("version", newVersion.toString ());
					version.add ();
				}
			}
			catch (Exception x)
			{
				outModule.setAttribute ("error", "unableToUpdateVersion");
				outModule.setAttribute ("errorException", x.toString ());
				response.addOutput ("updateError", "Y");
			}
		}

		if (newUserPreferences)
		{
			try
			{
				Persistent sample = pf.create ("keel.user");

				for (Persistent user : sample.query ())
				{
					KeelPreferencesManager.createDefaultValues (request, user.getFieldInt ("uid"));
				}
			}
			catch (PersistenceException x)
			{
				response.addOutput ("updateError", "Y");
				response.addOutput ("databaseError", x.getMessage ());

				StringWriter stw = new StringWriter ();
				PrintWriter pw = new PrintWriter (stw);

				x.printStackTrace (pw);
				response.addOutput ("databaseErrorStackTrace", stw.getBuffer ().toString ().replaceAll ("\n", "<br>"));
			}
		}

		if (response.get ("updateError") == null)
		{
			try
			{
				Model appInfo = (Model) request.getService (Model.ROLE, "aktera.app-info");

				Configuration[] appConfigs = appInfo.getConfiguration ().getChildren ("app");

				for (int i = 0; i < appConfigs.length; ++i)
				{
					Configuration appConfig = appConfigs[i];

					Persistent version = pf.create ("aktera.Version");

					version.setField ("type", "A");
					version.setField ("name", appConfig.getAttribute ("id", "unkown"));

					if (version.find ())
					{
						version.setField ("version", appConfig.getChild ("version").getValue ("0.0.0"));
						version.update ();
					}
					else
					{
						version.setField ("version", appConfig.getChild ("version").getValue ("0.0.0"));
						version.add ();
					}
				}
			}
			catch (Exception x)
			{
				System.out.println (x.toString ());
			}
		}

		updatePostProcessing (request, dataSourceComponent);

		if (response.get ("updateError") == null)
		{
			updateChecked = false;
			updateNeeded = false;

			if (needReboot)
			{
				response.addOutput ("needReboot", "Y");

				try
				{
					new Thread ()
					{
						public void run ()
						{
							try
							{
								sleep (5000);

								Process proc = SystemTools
												.startAkteraProcess ("/usr/bin/sudo", "/sbin/shutdown -r now");

								new NullStreamHandler (proc.getInputStream ());
								new NullStreamHandler (proc.getErrorStream ());
							}
							catch (Exception x)
							{
							}
						}
					}.start ();
				}
				catch (Exception x)
				{
				}
			}
		}
	}

	private static void updatePreProcessing (ModelRequest request, DataSourceComponent dataSourceComponent)
	{
		Connection connection = null;

		try
		{
			connection = dataSourceComponent.getConnection ();
			UpdateHandler handler = new UpdateHandler ();
			handler.setConnection (connection);
			handler.update ("ALTER TABLE version ALTER COLUMN name TYPE varchar(255)");
			handler.update ("UPDATE version set name='iritgo-aktera-address' where name='svc-address' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-aktario' where name='svc-aktario' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-authentication' where name='svc-authentication-persist' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-authorization' where name='svc-authorization-persist' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-base' where name='aktera' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-configuration' where name='svc-configuration' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-crypto-bas64' where name='svc-crypto-base64' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-email' where name='svc-email' and type='M'");
			handler.update ("DELETE FROM version where name='svc-mail-javamail' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-event' where name='svc-event' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-fsm' where name='svc-fsm' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-hibernate' where name='svc-persist-hibernate' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-i18n' where name='svc-i18n' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-importer' where name='svc-importer' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-jpassgen' where name='svc-password-jpassgen' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-logger' where name='svc-logger' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-model' where name='svc-model-default' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-permissions' where name='svc-permissions' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-persist-base' where name='svc-persist-base' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-persist-default' where name='svc-persist-default' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-query-jdbc' where name='svc-query-jdbc' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-reporting' where name='svc-reporting' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-scheduler' where name='svc-scheduler' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-script' where name='svc-script' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-spring' where name='svc-spring' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-startup' where name='svc-startup' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-tools' where name='svc-tools' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera-ui' where name='svc-ui' and type='M'");
			handler
							.update ("UPDATE version set name='iritgo-aktera-usergroup' where name='svc-usergroup-persist' and type='M'");
			handler.update ("UPDATE version set name='iritgo-aktera' where name='iritgo' and type='A'");
			handler.update ("UPDATE version set name='system' where name='system' and type='A'");
		}
		catch (Exception x)
		{
			System.out.println ("[UpdateHelper.updatePreProcessing] Error: " + x.getMessage ());
			x.printStackTrace ();
		}
		finally
		{
			DbUtils.closeQuietly (connection);
		}

		EventManager em = (EventManager) SpringTools.getBean (EventManager.ID);
		Properties props = new Properties ();

		props.put ("request", request);
		props.put ("datasource", dataSourceComponent);
		em.fire ("aktera.database.update.pre", request, props);
	}

	private static void updatePostProcessing (ModelRequest request, DataSourceComponent dataSourceComponent)
	{
		EventManager em = (EventManager) SpringTools.getBean (EventManager.ID);
		Properties props = new Properties ();

		props.put ("request", request);
		props.put ("datasource", dataSourceComponent);
		em.fire ("aktera.database.update.post", request, props);
	}

	public static boolean databaseExists (ModelRequest request)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet res = null;

		try
		{
			DataSourceComponent dataSourceComponent = (DataSourceComponent) request.getService (
							DataSourceComponent.ROLE, "keel-dbpool");

			connection = dataSourceComponent.getConnection ();
			stmt = connection.createStatement ();
			res = stmt
							.executeQuery ("SELECT value FROM systemconfig where name = 'databaseCreated' and category = 'system'");

			if (res.next ())
			{
				return "true".equals (res.getString ("value"));
			}

			return false;
		}
		catch (Exception x)
		{
			System.out.println ("[UpdateHelper.databaseExists] Error: " + x.getMessage ());
		}
		finally
		{
			DbUtils.closeQuietly (res);
			DbUtils.closeQuietly (stmt);
			DbUtils.closeQuietly (connection);
		}

		return false;
	}
}
