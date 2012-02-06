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

package de.iritgo.aktera.core.log;


import org.apache.avalon.excalibur.logger.Log4JConfLoggerManager;
import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.log.Hierarchy;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;


/**
 * <Replace with description for DefaultLoggerFactory>
 *
 * @version $Revision: 1.1 $ $Date: 2003/12/30 01:29:31 $
 * @author Schatterjee Created on Dec 23, 2003
 */
public class DefaultLoggerFactory implements LoggerFactory, LogEnabled
{
	private static LoggerFactory theFactory = null;

	private static final String CONFIG_PROPERTY = "keel.config.dir";

	private static final String LOGKIT_FILE_NAME = "logkit.xconf";

	private static final String LOG4J_FILE_NAME = "log4j.conf";

	private Logger logger = null;

	private LoggerManager loggerManager = null;

	private File logConfigFile = null;

	private DefaultLoggerFactory()
	{
		//Disable access to constructor - this is a singleton
	}

	/**
	 * @see de.iritgo.aktera.core.log.LoggerFactory#getLoggerForCategory(java.lang.String)
	 */
	public Logger getLoggerForCategory(String category)
	{
		return getLoggerManager().getLoggerForCategory(category);
	}

	/**
	 * @see de.iritgo.aktera.core.log.LoggerFactory#getDefaultLogger()
	 */
	public Logger getDefaultLogger()
	{
		return getLoggerManager().getDefaultLogger();
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger logger)
	{
		this.logger = logger;
	}

	private LoggerManager getLoggerManager()
	{
		if (loggerManager == null)
		{
			loggerManager = findLoggerManager();
		}

		return loggerManager;
	}

	/**
	 * @return
	 */
	private LoggerManager findLoggerManager()
	{
		String logConfigDir = System.getProperty(CONFIG_PROPERTY);

		if (! logConfigDir.endsWith("/"))
		{
			logConfigDir = logConfigDir + "/";
		}

		LoggerManager lm = createLogkitLoggerManager(logConfigDir);

		if (lm == null)
		{
			//If a logkit configuration file does not exist, attempt to
			// use log4j to implement our logging facade
			System.err.println("Logkit config file does not exist - trying log4j");
			lm = createLog4jLoggerManager(logConfigDir);
		}

		if (lm == null)
		{
			throw new RuntimeException("No configuration file found for logkit or log4j");
		}

		Configuration logConfig = getLoggerManagerConfiguration();

		setupLoggerManager(lm, logConfigDir, logConfig);

		return lm;
	}

	/**
	 * @param logConfigDir
	 * @param logConfig
	 */
	private void setupLoggerManager(LoggerManager lm, String logConfigDir, Configuration logConfig)
	{
		DefaultContext c = new DefaultContext();

		c.put("current-dir", logConfigDir + "..");

		try
		{
			ContainerUtil.contextualize(lm, c);
			ContainerUtil.configure(lm, logConfig);
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

	/**
	 * @return
	 */
	private Configuration getLoggerManagerConfiguration()
	{
		Configuration logConfig = null;

		try
		{
			logConfig = new DefaultConfigurationBuilder().buildFromFile(logConfigFile);
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

		return logConfig;
	}

	/**
	 * @param logConfigDir
	 * @param lm
	 * @return
	 */
	private LoggerManager createLog4jLoggerManager(String logConfigDir)
	{
		//To remain consistent with Keel's configuration practices, we
		// will NOT rely on log4j's
		//default initialization sequence, instead we will search for
		// a configuration file only in logConfigDir.
		//If the log4j standard initialization system property is set,
		// use it
		LoggerManager lm = null;
		String logConfigFilename = System.getProperty("log4j.configuration");

		if (logConfigFilename == null || logConfigFilename.length() < 1)
		{
			logConfigFilename = LOG4J_FILE_NAME;

			//else default the log4j configuration file name.
		}

		logConfigFile = new File(logConfigDir + logConfigFilename);

		if (logConfigFile.exists())
		{
			lm = new Log4JConfLoggerManager();
		}

		return lm;
	}

	/**
	 * @param logConfigFile
	 * @return
	 */
	private LoggerManager createLogkitLoggerManager(String logConfigDir)
	{
		LoggerManager lm = null;
		String logConfigFilename = logConfigDir + LOGKIT_FILE_NAME;

		logConfigFile = new File(logConfigFilename);

		if (logConfigFile.exists())
		{
			//Continue with logkit configuration
			lm = new LogKitLoggerManager(null, new Hierarchy(), new ConsoleLogger(ConsoleLogger.LEVEL_INFO));
		}
		else
		{
			lm = null;
		}

		return lm;
	}

	public static LoggerFactory getInstance()
	{
		if (theFactory == null)
		{
			theFactory = new DefaultLoggerFactory();
		}

		return theFactory;
	}
}
