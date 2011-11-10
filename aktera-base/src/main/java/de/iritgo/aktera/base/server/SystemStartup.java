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


import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.service.ServiceException;


/**
 *
 */
public class SystemStartup implements StartupHandler
{
	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup() throws StartupException
	{
		ModelRequest request = null;

		try
		{
			request = ModelTools.createModelRequest();

			SystemConfigManager systemConfigManager = (SystemConfigManager) request
							.getSpringBean(SystemConfigManager.ID);

			if (! StringTools.isTrimEmpty(systemConfigManager.getString("tb2", "logLevel")))
			{
				KeelContainer.defaultContainer().setLogLevel(systemConfigManager.getString("tb2", "logLevel"));
			}
		}
		catch (ServiceException x)
		{
		}
		catch (ModelException x)
		{
		}
		finally
		{
			ModelTools.releaseModelRequest(request);
		}
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown() throws ShutdownException
	{
	}
}
