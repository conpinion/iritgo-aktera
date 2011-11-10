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

package de.iritgo.aktera.startup;


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;


/**
 *
 */
public abstract class AbstractStartupHandler implements StartupHandler
{
	/** Service configuration. */
	@SuppressWarnings("unused")
	protected Configuration configuration;

	/** The logger. */
	@SuppressWarnings("unused")
	protected Logger logger;

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public abstract void startup() throws StartupException;

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public abstract void shutdown() throws ShutdownException;

	/**
	 * Get the configuration.
	 *
	 * @return The configuration
	 */
	public Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#setConfiguration(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Get the logger.
	 *
	 * @return The logger.
	 */
	public Logger getLogger()
	{
		return logger;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#setLogger(org.apache.avalon.framework.logger.Logger)
	 */
	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}
}
