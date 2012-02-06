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

package de.iritgo.aktera.core.container;


import de.iritgo.aktera.authorization.lifecycle.AuthorizationLifecycleAccessExtension;
import de.iritgo.aktera.authorization.lifecycle.AuthorizationLifecycleCreateExtension;
import de.iritgo.aktera.core.config.KeelConfigurationMerger;
import de.iritgo.aktera.core.config.KeelConfigurationUtil;
import de.iritgo.aktera.core.config.KeelErrorHandler;
import de.iritgo.aktera.core.exception.NestedException;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.excalibur.logger.Log4JConfLoggerManager;
import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.fortress.MetaInfoEntry;
import org.apache.avalon.fortress.MetaInfoManager;
import org.apache.avalon.fortress.RoleEntry;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.impl.role.ConfigurableRoleManager;
import org.apache.avalon.fortress.impl.role.FortressRoleManager;
import org.apache.avalon.fortress.impl.role.Role2MetaInfoManager;
import org.apache.avalon.fortress.util.ContextManagerConstants;
import org.apache.avalon.fortress.util.LifecycleExtensionManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.lifecycle.Accessor;
import org.apache.avalon.lifecycle.Creator;
import org.apache.log.Hierarchy;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * This is a helper class that is used to instantiate the top-level Keel container.
 *
 * @version  $Revision: 1.1 $  $Date: 2003/12/30 01:29:31 $
 * @author Shash Chatterjee
 * @deprecated Use Container/ContainerFactory interfaces
 * Created on Oct 17, 2002
 */
public class KeelContainerBuilder
{
	public final static String CONFIG_PROPERTY = "keel.config.dir";

	private final static String PREFIX = "[KeelContainerBuilder] ";

	private static Configuration roleConfig = null;

	private static Configuration systemConfig = null;

	private static Configuration logConfig = null;

	private static String logConfigFilename = "logkit.xconf";

	private static Configuration instrConfig = null;

	private static RoleManager utilRoleManager = null;

	private static MetaInfoManager utilMetaInfoManager = null;

	protected KeelContainer myContainer = null;

	protected Logger primordialLogger = getPrimordialLogger();

	protected LoggerManager primordialLoggerManager = null;

	protected Logger logger = null;

	protected LoggerManager loggerManager = null;

	protected ContainerManager containerManager = null;

	protected ServiceManager serviceManager = null;

	protected DefaultContext context = null;

	public KeelContainerBuilder()
	{
		super();
	}

	public KeelContainerBuilder(Logger primordialLogger)
	{
		super();
		this.primordialLogger = primordialLogger;
	}

	public KeelContainer getContainer() throws NestedException
	{
		if (myContainer == null)
		{
			System.err.println(PREFIX + "Initializing Keel Container...");
			/*
			 *  Verify and merge all the config files
			 */
			buildConfigs();

			if (roleConfig == null)
			{
				throw new NestedException("Role configuration is null");
			}

			if (logConfig == null)
			{
				throw new NestedException("Log configuration is null");
			}

			if (systemConfig == null)
			{
				throw new NestedException("System configuration is null");
			}

			if (instrConfig == null)
			{
				throw new NestedException("Instrumentation configuration is null");
			}

			/*
			 * Build the context for the container
			 */
			DefaultContext c = new DefaultContext();
			String contextPath = System.getProperty(CONFIG_PROPERTY) + System.getProperty("file.separator") + "..";

			c.put("current-dir", contextPath);
			c.put(ContextManagerConstants.THREAD_TIMEOUT, new Long(1000));
			c.put(ContextManagerConstants.THREADS_CPU, new Integer(2));
			c.put(ContextManagerConstants.CONTEXT_DIRECTORY, new File(contextPath));
			c.put(ContextManagerConstants.WORK_DIRECTORY, new File(contextPath + "/tmp"));

			ClassLoader loader = Thread.currentThread().getContextClassLoader();

			c.put(ClassLoader.class.getName(), loader);
			c.put(ContextManagerConstants.PARAMETERS, new Parameters());
			c.put(ContextManagerConstants.CONTAINER_CLASS, KeelContainer.class);
			c.put(ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION, logConfig);
			c.put(ContextManagerConstants.CONFIGURATION, systemConfig);
			c.put(ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION, instrConfig);
			c.put(ContextManagerConstants.LOG_CATEGORY, "keel");

			/*
			 * Set the context for the getLoggerManager() calls below to work
			 * we'll set the context again later
			 */
			setContext(c);

			/*
			 * Create a custom meta-info manager.  This gets around the fact that
			 * keel-server needs its own sandboxed classpath, which KeelURLClassloader
			 * provides for classes.  But, java.lang.Classpath doesn't let one
			 * override the fact that resources gotten via the load.getResources() is
			 * always looked up from the parent loader in addition to the current loader.
			 */
			final MetaInfoManager metaManager = createMetaManager(loader);

			c.put(MetaInfoManager.ROLE, metaManager);

			/*
			 * Put a logger manager in the Context.
			 * NOTE: getLoggerManager() needs a context set in the first place
			 */
			c.put(LoggerManager.ROLE, getLoggerManager());
			setContext(c);

			/*
			 * So far we have been using the primordial logger since
			 * the logger config hadn't been read and we did not have
			 * enough context.
			 * Now we do, so create a proper logger
			 */
			try
			{
				logger = getLoggerManager().getLoggerForCategory("keelserver");
			}
			catch (Exception e)
			{
				getLogger().debug("Cannot create Keel logger, using defult logger", e);
			}

			c.put(LifecycleExtensionManager.ROLE, createLifeCycleExtensionManager(c));
			setContext(c);

			/*
			 * Create the container
			 */
			try
			{
				containerManager = new DefaultContainerManager(context);

				if (containerManager instanceof Initializable)
				{
					((Initializable) containerManager).initialize();
				}

				myContainer = (KeelContainer) containerManager.getContainer();

				if (myContainer == null)
				{
					throw new NestedException(
									"Container returned from containerManager was null - unable to initialize. Check log");
				}

				serviceManager = myContainer.getServiceManager();
			}
			catch (ClassNotFoundException e)
			{
				getLogger().error("Error loading KeelContainer class", e);
				throw new NestedException(e);
			}
			catch (Exception e)
			{
				getLogger().error("Error initializing Container Manager", e);
				throw new NestedException(e);
			}

			try
			{
				setProperties(System.getProperty(CONFIG_PROPERTY));
			}
			catch (Exception e)
			{
				throw new NestedException(e);
			}
		}

		return myContainer;
	}

	private MetaInfoManager createMetaManager(ClassLoader loader) throws NestedException
	{
		// Create a logger for the role manager
		final Logger rmLogger = getLoggerManager().getLoggerForCategory(
						roleConfig.getAttribute("logger", "system.roles"));

		// Create a parent role manager with all the default roles
		final FortressRoleManager frm = new FortressRoleManager(null, loader);

		frm.enableLogging(rmLogger.getChildLogger("defaults"));
		frm.initialize();

		// Create a role manager with the configured roles
		final ConfigurableRoleManager rm = new ConfigurableRoleManager(frm);

		rm.enableLogging(rmLogger);

		try
		{
			rm.configure(roleConfig);
		}
		catch (ConfigurationException e)
		{
			throw new NestedException("Error configuring role manager", e);
		}

		final KeelMetaInfoManager metaManager = new KeelMetaInfoManager(new Role2MetaInfoManager(rm), loader);

		metaManager.enableLogging(getLoggerManager().getLoggerForCategory("system.meta"));

		try
		{
			metaManager.initialize();
		}
		catch (Exception e)
		{
			throw new NestedException("Error initializing meta manager", e);
		}

		utilRoleManager = rm;
		utilMetaInfoManager = metaManager;

		return metaManager;
	}

	protected LifecycleExtensionManager createLifeCycleExtensionManager(DefaultContext c)
	{
		LifecycleExtensionManager extensions = new LifecycleExtensionManager();

		Accessor accessor = new AuthorizationLifecycleAccessExtension();

		if (accessor instanceof LogEnabled)
		{
			((LogEnabled) accessor).enableLogging(getLogger());
		}

		extensions.addAccessorExtension(accessor);

		Creator creator = new AuthorizationLifecycleCreateExtension();

		if (creator instanceof LogEnabled)
		{
			((LogEnabled) creator).enableLogging(getLogger());
		}

		extensions.addCreatorExtension(creator);

		return extensions;
	}

	/**
	 * This method reads the top-level configuration and looks for an
	 * element called "system-properties". Each sub-element of this element
	 * should be a "property" element, with a "name" and "value"
	 * attribute. This sets a single system property for each such
	 * element - e.g.
	 * <br>
	 * &lt;system-properties&gt;
	 *   &lt;property name="java.security.auth.login.config" value="%conf%/jaas.config"/&gt;
	 * &lt;/system-properties&gt;
	 *
	 * Would set the system property "java.security.auth.login.config"
	 * to the jaas.config file. The %conf% prefix is a shorthand for specifying
	 * the name of the directory in which the top-level config file is found.
	 * E.g. if your top-level config is system.xconf, and it's in /home/keel/keel-build/deploy/server/conf,
	 * then "%conf%" will expand to that directory.
	 */
	private void setProperties(String configPath) throws ConfigurationException, IOException
	{
		try
		{
			Configuration props = systemConfig.getChild("system-properties");

			if (props == null)
			{
				return;
			}

			Configuration[] children = props.getChildren();
			Configuration oneChild = null;

			for (int i = 0; i < children.length; i++)
			{
				oneChild = children[i];

				if (oneChild.getName().equals("property"))
				{
					String oneValue = oneChild.getAttribute("value");

					if (oneValue.indexOf("%conf%") >= 0)
					{
						oneValue = SuperString.replace(oneValue, "%conf%", configPath);
					}

					System.setProperty(oneChild.getAttribute("name"), oneValue);
					System.err.println(PREFIX + "Setting system property '" + oneChild.getAttribute("name") + "' to '"
									+ oneValue + "'");
					getLogger().debug(
									"Setting system property '" + oneChild.getAttribute("name") + "' to '" + oneValue
													+ "'");
				}
			}
		}
		catch (Exception e)
		{
			throw new ConfigurationException("Error setting system properties", e);
		}
	}

	private void verifyConfiguration(String configFile)
	{
		getLogger().debug("Verifying " + configFile);

		DOMParser parser = new DOMParser();

		try
		{
			KeelErrorHandler kh = new KeelErrorHandler();

			parser.setErrorHandler(kh);
			parser.parse(configFile);
		}
		catch (Exception ie)
		{
			getLogger().error("Unable to parse '" + configFile + "'", ie);
		}

		getLogger().debug(configFile + " verified");
	}

	public void buildConfigs() throws NestedException
	{
		roleConfig = null;
		systemConfig = null;
		logConfig = null;

		System.err.println(PREFIX + "Building configurations...");
		getLogger().debug("Building Configurations...");

		try
		{
			scanDir(System.getProperty(CONFIG_PROPERTY));
			System.err.println("");
		}
		catch (Exception ee)
		{
			throw new NestedException("Exception scanning configuration directory '"
							+ System.getProperty("keel.config.dir") + "'", ee);
		}

		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty(CONFIG_PROPERTY)
							+ "/merged.config"));

			out.write(KeelConfigurationUtil.list(systemConfig, "Merged System Configuration"));
			out.write(KeelConfigurationUtil.list(roleConfig, "Merged Role Configuration"));
			out.write(KeelConfigurationUtil.list(logConfig, "Merged Log Configuration"));
			out.write(KeelConfigurationUtil.list(instrConfig, "Merged Instrumentation Configuration"));
			out.flush();
			out.close();
			getLogger()
							.info(
											"Merged configuration written to " + System.getProperty(CONFIG_PROPERTY)
															+ "/merged.config");
		}
		catch (IOException ie)
		{
			System.err.println("Unable to write merged.config");
		}
	}

	private void scanDir(String configDir) throws NestedException, IOException, ConfigurationException, SAXException
	{
		String dirToUse = configDir;

		System.err.println(PREFIX + "Reading " + dirToUse);

		if (! dirToUse.endsWith("/"))
		{
			dirToUse = dirToUse + "/";
		}

		File dirFile = new File(dirToUse);

		if (! dirFile.isDirectory())
		{
			throw new NestedException(dirToUse + "' is not a directory.");
		}

		String[] dir = dirFile.list();

		if (dir == null)
		{
			throw new NestedException("Null array reading directory " + " of " + dirToUse);
		}

		String oneFileName = null;

		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

		// Process only files in the current directory first
		for (int i = 0; i < dir.length; i++)
		{
			oneFileName = dir[i].trim();

			File oneFile = new File(configDir + oneFileName);

			if (! oneFile.isDirectory())
			{
				if (oneFileName.endsWith("roles.xconf"))
				{
					roleConfig = readConfigFile(dirToUse, oneFileName, builder, roleConfig);
				}
				else if (oneFileName.endsWith("system.xconf"))
				{
					systemConfig = readConfigFile(dirToUse, oneFileName, builder, systemConfig);
				}
				else if (oneFileName.endsWith(logConfigFilename))
				{
					logConfig = readConfigFile(dirToUse, oneFileName, builder, logConfig);
				}
				else if (oneFileName.endsWith("system.instruments"))
				{
					instrConfig = readConfigFile(dirToUse, oneFileName, builder, instrConfig);
				}
				else
				{
					getLogger().debug("Ignoring file '" + dirToUse + oneFileName + "'");
				}
			}
		}

		// Process sub-directories of current directory next
		for (int i = 0; i < dir.length; i++)
		{
			oneFileName = dir[i].trim();

			File oneFile = new File(dirToUse + oneFileName);

			if (oneFile.isDirectory())
			{
				scanDir(dirToUse + oneFileName);
			}
		}
	}

	private Configuration readConfigFile(String configDir, String oneFileName, DefaultConfigurationBuilder builder,
					Configuration config) throws SAXException, IOException, ConfigurationException
	{
		Configuration mergeWith = config;

		getLogger().info("Reading configuration from: '" + configDir + oneFileName + "'");
		verifyConfiguration(configDir + oneFileName);

		Configuration newConfig = null;

		try
		{
			newConfig = builder.buildFromFile(configDir + oneFileName);
		}
		catch (Exception ee)
		{
			System.err.println("Configuration exception in file '" + configDir + oneFileName + "'");
			throw new ConfigurationException("Configuration problem in file '" + configDir + oneFileName + "'", ee);
		}

		if (mergeWith == null)
		{
			mergeWith = newConfig;
		}
		else
		{
			try
			{
				mergeWith = KeelConfigurationMerger.merge(newConfig, mergeWith);
			}
			catch (ConfigurationException ce)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty(CONFIG_PROPERTY)
								+ "/error.config"));

				out.write(KeelConfigurationUtil.list(mergeWith, "Error Merging into the following Configuration"));
				out.flush();
				out.close();
				throw new ConfigurationException("Exception when merging file '" + configDir + oneFileName
								+ "'. Configuration merged so far written to error.config", ce);
			}
		}

		return mergeWith;
	}

	/**
	 * Returns the logConfig.
	 * @return Configuration
	 */
	public static Configuration getLogConfig()
	{
		return logConfig;
	}

	/**
	 * Returns the roleConfig.
	 * @return Configuration
	 */
	public static Configuration getRoleConfig()
	{
		return roleConfig;
	}

	/**
	 * Returns the systemConfig.
	 * @return Configuration
	 */
	public static Configuration getSystemConfig()
	{
		return systemConfig;
	}

	public static Configuration getInstrConfig()
	{
		return instrConfig;
	}

	public Context getContext()
	{
		Context returnValue = null;

		if (context == null)
		{
			returnValue = new DefaultContext();
		}
		else
		{
			returnValue = context;
		}

		return returnValue;
	}

	private Logger getPrimordialLogger()
	{
		if (primordialLogger == null)
		{
			primordialLogger = getPrimordialLoggerManager().getLoggerForCategory("keelserver");
		}

		return primordialLogger;
	}

	public Logger getLogger()
	{
		Logger returnValue = null;

		if (logger == null)
		{
			returnValue = getPrimordialLogger();
		}
		else
		{
			returnValue = logger;
		}

		return returnValue;
	}

	public LoggerManager getPrimordialLoggerManager()
	{
		if (primordialLoggerManager == null)
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			String logConfigDir = System.getProperty(CONFIG_PROPERTY);

			if (! logConfigDir.endsWith("/"))
			{
				logConfigDir = logConfigDir + "/";
			}

			File primordialLogConfigFile = new File(logConfigDir + logConfigFilename);

			if (primordialLogConfigFile.exists())
			{
				//Continue with logkit configuration          
				primordialLoggerManager = new LogKitLoggerManager(null, new Hierarchy(), new ConsoleLogger(
								ConsoleLogger.LEVEL_INFO));
			}
			else
			{
				System.err.println("Logkit config file " + logConfigDir + logConfigFilename
								+ " does not exist - trying log4j");
				//If a logkit configuration file does not exist, attempt to use log4j to implement our logging facade
				//To remain consistent with Keel's configuration practices, we will NOT rely on log4j's
				//default initialization sequence, instead we will search for a configuration file only in logConfigDir. 

				//If the log4j standard initialization system property is set, use it              
				logConfigFilename = System.getProperty("log4j.configuration");

				if (logConfigFilename == null || logConfigFilename.length() < 1)
				{
					logConfigFilename = "log4j.xconf";

					//else default the log4j configuration file name.
				}

				primordialLogConfigFile = new File(logConfigDir + logConfigFilename);

				if (primordialLogConfigFile.exists())
				{
					primordialLoggerManager = new Log4JConfLoggerManager();
				}
				else
				{
					throw new RuntimeException("No configuration file found for logkit or log4j");
				}
			}

			Configuration primordialLogConfig = null;

			try
			{
				primordialLogConfig = builder.buildFromFile(primordialLogConfigFile);
			}
			catch (ConfigurationException e)
			{
				throw new RuntimeException("Error building primordial logger-manager configuration", e);
			}
			catch (SAXException e)
			{
				throw new RuntimeException("Error parsing primordial logger-manager configuration", e);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Error reading primordial logger-manager configuration", e);
			}

			DefaultContext c = new DefaultContext();

			c.put("current-dir", logConfigDir + "..");

			try
			{
				if (primordialLoggerManager instanceof Contextualizable)
				{
					((Contextualizable) primordialLoggerManager).contextualize(c);
				}

				if (primordialLoggerManager instanceof Configurable)
				{
					((Configurable) primordialLoggerManager).configure(primordialLogConfig);
				}
			}
			catch (ConfigurationException e)
			{
				throw new RuntimeException("Error configuring primordial logger-manager", e);
			}
			catch (ContextException e)
			{
				throw new RuntimeException("Error contextualizing primordial logger-manager", e);
			}
		}

		return primordialLoggerManager;
	}

	public LoggerManager getLoggerManager()
	{
		if (loggerManager == null)
		{
			Logger logger = getLogger();

			if (logger instanceof LogKitLogger)
			{
				loggerManager = new LogKitLoggerManager(null, new Hierarchy(), getLogger());
			}
			else if (logger instanceof Log4JLogger)
			{
				loggerManager = new Log4JConfLoggerManager();
			}

			try
			{
				if (loggerManager instanceof Contextualizable)
				{
					((Contextualizable) loggerManager).contextualize(getContext());
				}

				if (loggerManager instanceof Configurable && logConfig != null)
				{
					((Configurable) loggerManager).configure(logConfig);
				}
			}
			catch (ConfigurationException e)
			{
				throw new RuntimeException("Error configuring logger-manager", e);
			}
			catch (ContextException e)
			{
				throw new RuntimeException("Error contextualizing logger-manager", e);
			}
		}

		return loggerManager;
	}

	/**
	 * @param context
	 */
	public void setContext(DefaultContext context)
	{
		this.context = context;
	}

	public ServiceManager getServiceManager()
	{
		return serviceManager;
	}

	public void dispose()
	{
		if (containerManager instanceof Disposable)
		{
			((Disposable) containerManager).dispose();
		}
	}

	public static Class getClassForShortName(String shortName)
	{
		RoleEntry re = utilRoleManager.getRoleForShortName(shortName);

		if (re != null)
		{
			return re.getComponentClass();
		}

		MetaInfoEntry me = utilMetaInfoManager.getMetaInfoForShortName(shortName);

		if (me != null)
		{
			return me.getComponentClass();
		}

		return null;
	}
}
