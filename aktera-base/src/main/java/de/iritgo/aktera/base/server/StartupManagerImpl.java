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

package de.iritgo.aktera.base.server;


import de.iritgo.aktera.base.database.*;
import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.startup.AbstractStartupHandler;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import de.iritgo.aktera.startup.StartupManager;
import de.iritgo.aktera.tools.*;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.startup.StartupManager"
 * @x-avalon.info name="aktera.startup"
 * @x-avalon.lifestyle type="singleton"
 */
public class StartupManagerImpl extends AbstractKeelServiceable implements StartupManager, LogEnabled, Configurable,
				Initializable
{
	/** The logger. */
	private Logger log;

	/** Service configuration. */
	private Configuration configuration;

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger logger)
	{
		this.log = logger;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration configuration) throws ConfigurationException
	{
		this.configuration = configuration;
	}

	/**
	 * Retrieve a list of dependency-sorted startup configurations.
	 *
	 * @return A list of startup configurations
	 * @throws ConfigurationException In case of a configuration exception
	 */
	synchronized private List<Configuration> resolveStartupConfig() throws ConfigurationException
	{
		List<Configuration> sortedConfigs = new LinkedList<Configuration>();

		List<String> resolvedStartupIds = new LinkedList<String>();

		LinkedList<Configuration> configs = new LinkedList<Configuration>();

		Configuration[] startupConfigs = configuration.getChildren("startup");

		for (Configuration config : startupConfigs)
		{
			configs.add(config);
		}

		while (! configs.isEmpty())
		{
			Configuration config = configs.poll();

			String startupId = config.getAttribute("id", "unknown");

			Configuration[] dependConfigs = config.getChildren("depends");

			boolean resolved = true;

			for (Configuration dependConfig : dependConfigs)
			{
				DependencyType depType = DependencyType.valueOf(dependConfig.getAttribute("type").toUpperCase());

				switch (depType)
				{
					case STARTUP:
					{
						String dependendStartupId = dependConfig.getValue();

						if (! StringTools.isTrimEmpty(dependendStartupId)
										&& ! resolvedStartupIds.contains(dependendStartupId))
						{
							configs.addLast(config);
							resolved = false;
						}
					}

						break;
				}
			}

			if (resolved)
			{
				sortedConfigs.add(config);
				resolvedStartupIds.add(startupId);
			}
		}

		return sortedConfigs;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Initializable#initialize()
	 */
	public void initialize() throws Exception
	{
		List<Configuration> sortedConfigs = resolveStartupConfig();

		/**
		 * TODO: Very bad hack!
		 * Wir dürfen das System nicht vor einem fertigen DB-Update initialisieren...
		 */
		ModelRequest model = ModelTools.createModelRequest();
		boolean oneShotDisableFirewall = true;
		while (UpdateHelper.needUpdate(model))
		{
			if (oneShotDisableFirewall)
			{
				SystemFirewall.disable();
				oneShotDisableFirewall = false;
			}
			try
			{
				TimeUnit.SECONDS.sleep(10);
			}
			catch (Exception x)
			{
			}
		}
		ModelTools.releaseModelRequest(model);

		for (Configuration config : sortedConfigs)
		{
			StartupHandler startup = null;

			String id = config.getAttribute("id");

			if (config.getAttribute("class", null) != null)
			{
				String className = config.getAttribute("class", null);

				startup = (StartupHandler) Class.forName(className).newInstance();
			}
			else if (config.getAttribute("bean", null) != null)
			{
				String beanName = config.getAttribute("bean", null);

				startup = (StartupHandler) KeelContainer.defaultContainer().getSpringBean(beanName);
			}

			if (startup instanceof AbstractStartupHandler)
			{
				if (((AbstractStartupHandler) startup).getLogger() == null)
				{
					((AbstractStartupHandler) startup).setLogger(log);
				}

				if (((AbstractStartupHandler) startup).getConfiguration() == null)
				{
					((AbstractStartupHandler) startup).setConfiguration(config);
				}
			}

			try
			{
				log.info("Starting component " + id);
				startup.startup();
			}
			catch (StartupException x)
			{
				log.error("Startup exception in handler '" + id + "': " + x);
			}
			catch (Exception x)
			{
				log.error("Unable to call startup handler '" + id + "': " + x);
			}

			log.debug("Successfully called startup handler: " + id);
		}
	}

	/**
	 * Describe method shutdown() here.
	 *
	 * @throws Shutdown
	 */
	public void shutdown() throws Exception
	{
		List<Configuration> sortedConfigs = resolveStartupConfig();

		Collections.reverse(sortedConfigs);

		for (Configuration config : sortedConfigs)
		{
			StartupHandler startup = null;

			String id = config.getAttribute("id");

			if (config.getAttribute("class", null) != null)
			{
				String className = config.getAttribute("class", null);

				startup = (StartupHandler) Class.forName(className).newInstance();
			}
			else if (config.getAttribute("bean", null) != null)
			{
				String beanName = config.getAttribute("bean", null);

				startup = (StartupHandler) KeelContainer.defaultContainer().getSpringBean(beanName);
			}

			if (startup instanceof AbstractStartupHandler)
			{
				((AbstractStartupHandler) startup).setLogger(log);
				((AbstractStartupHandler) startup).setConfiguration(config);
			}

			try
			{
				log.info("Stopping component " + id);
				startup.shutdown();
			}
			catch (ShutdownException x)
			{
				log.error("Shutdown exception in handler '" + id + "': " + x);
			}
			catch (Exception x)
			{
				log.error("Unable to call shutdown handler '" + id + "': " + x);
			}

			log.debug("Successfully called shutdown handler: " + id);
		}
	}
}
