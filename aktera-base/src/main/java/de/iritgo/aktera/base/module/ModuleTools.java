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
import org.apache.avalon.framework.configuration.Configuration;


/**
 * Module utility methods.
 */
public class ModuleTools
{
	/**
	 * Check for the presence of a specific module.
	 *
	 * @param req A model request.
	 * @param name Name of the module to check.
	 * @return True if the module exists.
	 */
	public static boolean moduleExists (ModelRequest req, String name)
	{
		try
		{
			Model moduleInfoModel = (Model) req.getService (Model.ROLE, "aktera.module-info");

			Configuration[] modules = moduleInfoModel.getConfiguration ().getChildren ("module");

			for (int i = 0; i < modules.length; ++i)
			{
				Configuration module = modules[i];

				if (name.equals (module.getAttribute ("id", null)))
				{
					return LicenseTools.getLicenseInfo ().moduleAllowed (name);
				}
			}
		}
		catch (ModelException x)
		{
		}

		return false;
	}
}
