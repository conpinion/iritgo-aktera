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

package de.iritgo.aktera.base.module;


import de.iritgo.aktera.license.LicenseTools;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.LinkedList;
import java.util.List;


/**
 * Help.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.module-info"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.module-info" id="aktera.module-info" logger="aktera"
 */
public class ModuleInfo extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		Output outModuleList = res.createOutput("modules");

		res.add(outModuleList);

		try
		{
			Configuration config = getConfiguration();

			Configuration[] modules = config.getChildren("module");

			for (int i = 0; i < modules.length; ++i)
			{
				Configuration module = modules[i];

				if (LicenseTools.getLicenseInfo().moduleAllowed(module.getAttribute("id")))
				{
					Output outModule = res.createOutput("module_" + module.getAttribute("id"));

					outModuleList.add(outModule);
					outModule.setAttribute("name", module.getChild("name").getValue());
					outModule.setAttribute("version", module.getChild("version").getValue());
					outModule.setAttribute("type", module.getAttribute("type", "application"));
					outModule.setAttribute("description", module.getChild("description").getValue());
					outModule.setAttribute("copyright", module.getChild("copyright").getValue("").replaceAll("\\\\n",
									"<br />"));
				}
			}
		}
		catch (ConfigurationException x)
		{
		}

		return res;
	}

	/**
	 * Create a list of module configurations, sorted by dependencies.
	 *
	 * @return The configuration list.
	 */
	public static List<Configuration> moduleConfigsSortedByDependency(ModelRequest req)
	{
		List<Configuration> sortedConfigs = new LinkedList<Configuration>();

		List<String> resolvedModuleIds = new LinkedList<String>();

		try
		{
			LinkedList<Configuration> configs = new LinkedList<Configuration>();

			Model moduleInfo = (Model) req.getService(Model.ROLE, "aktera.module-info");

			Configuration[] moduleConfigs = moduleInfo.getConfiguration().getChildren("module");

			for (Configuration config : moduleConfigs)
			{
				configs.add(config);
			}

			while (! configs.isEmpty())
			{
				Configuration config = configs.poll();

				String moduleId = config.getAttribute("id", "unkown");

				String[] deps = config.getChild("dependencies").getAttribute("modules", "").split(",");

				boolean resolved = true;

				for (String dep : deps)
				{
					if (! StringTools.isTrimEmpty(dep) && ! resolvedModuleIds.contains(dep))
					{
						configs.addLast(config);
						resolved = false;

						break;
					}
				}

				if (resolved)
				{
					sortedConfigs.add(config);
					resolvedModuleIds.add(moduleId);
				}
			}
		}
		catch (ModelException x)
		{
			System.out.println("[ModuleInfo] " + x);
		}

		return sortedConfigs;
	}
}
