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


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.HashMap;
import java.util.Map;


/**
 * A helper class to provide uniform way of allowing Keel components to acquire
 * collaborator components.  When looking up services/components from the
 * container, a "hint" is needed.  When "default" or a particular service
 * hint is hardcoded for acquiring the service, it prevents the use of
 * configuration to specify which implementation to use for a particular
 * service. Instead this class allows a standard way of acquiring the
 * hint from configuration, before looking up  a service, thereby allowing
 * a particular installation to change which component is used.
 *
 * To use, a class variable is first declared:
 * <pre>
 *      ServiceConfig svcConfig = null;
 * </pre>
 * and initialized somewhere before use:
 * <pre>
 *      svcConfig = new ServiceConfig(configuration);
 * </pre>
 * where "configuration" is standard Avalon XML config for a component.
 *
 * This class looks for configuration of the form:
 * <pre>
 *      <services>
 *          <service
 *              name="...."
 *              hint="...." />
 *          <service ..... />
 *          <service ..... />
 *      </services>
 * </pre>
 *
 *  Finally, when looking up a service, the hint is provided as:
 * <pre>
 *      svcCOnfig.getHint(someRole);
 * </pre>
 * If no configuration is provided at all (i.e. current config files),
 *  then getHint simply returns "default".
 *
 * @author Shash Chatterjee
 * @date Mar 9, 2004
 * @version $Revision: 1.3 $ $Date: 2004/03/10 03:36:15 $
 */
public class ServiceConfig
{
	// role/hint pairs
	protected Map serviceHints = new HashMap();

	// disallow use of default constructor, need configuration
	private ServiceConfig()
	{
	}

	/**
	 * Cretae the configuration helper and configure role/hint pairs
	 * @param configuration Avalon/Fortress XML configuration for a component
	 * @throws ConfigurationException If there is error processing the XML configuration
	 */
	public ServiceConfig(Configuration configuration) throws ConfigurationException
	{
		if (configuration == null)
		{
			throw new ConfigurationException("Cannot build list from null configuration");
		}

		Configuration[] serviceConf = configuration.getChild("services", true).getChildren("service");

		for (int i = 0; i < serviceConf.length; i++)
		{
			Configuration oneServiceConf = serviceConf[i];
			String name = oneServiceConf.getAttribute("name");
			String hint = oneServiceConf.getAttribute("hint");

			serviceHints.put(name, hint);
		}
	}

	/**
	 * Get hint associated with a role
	 * @param name The role whose hint is required
	 * @return The configured hint or "*"
	 */
	public String getHint(String name)
	{
		String hint = (String) serviceHints.get(name);

		if (hint == null)
		{
			hint = "*";
		}

		return hint;
	}

	/**
	 * Allow setting of role/hint programatically
	 * @param name The role whose hint to set
	 * @param hint The hint to set
	 */
	public void setHint(String name, String hint)
	{
		serviceHints.put(name, hint);
	}
}
