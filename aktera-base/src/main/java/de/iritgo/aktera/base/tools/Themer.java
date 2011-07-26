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

package de.iritgo.aktera.base.tools;


import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.configuration.preferences.Preferences;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.ui.tools.UserTools;
import org.apache.avalon.framework.configuration.Configuration;


/**
 * This model creates output elements describing the various themable parts
 * of the user interface.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.themer"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.themer" id="aktera.tools.themer" logger="aktera"
 */
public class Themer extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		PersistentFactory persistentManager = (PersistentFactory) req.getService (PersistentFactory.ROLE, req
						.getDomain ());

		SystemConfigManager systemConfigManager = (SystemConfigManager) req.getSpringBean (SystemConfigManager.ID);

		ModelResponse res = req.createResponse ();

		Configuration config = getConfiguration ();

		String layoutName = "iritgo";

		try
		{
			layoutName = systemConfigManager.getString ("gui", "layout");
		}
		catch (Exception x)
		{
		}

		String themeName = "iritgong";

		try
		{
			themeName = (String) systemConfigManager.get ("gui", "defaultTheme");

			if (themeName == null && config != null)
			{
				themeName = config.getChild ("theme-name").getValue ("root");
			}

			if (themeName == null)
			{
				themeName = "iritgong";
			}

			if (! systemConfigManager.getBool ("gui", "disableThemeChange"))
			{
				Preferences preferences = (Preferences) UserTools.getUserEnvObject (req, "sessionPreferences");

				if (preferences != null)
				{
					themeName = preferences.getTheme ();

					try
					{
						if (! persistentManager.create ("aktera.Preferences").getValidValues ("theme").containsKey (
										themeName))
						{
							themeName = "iritgong";
						}
					}
					catch (PersistenceException x)
					{
						themeName = "iritgong";
					}
				}
			}
		}
		catch (Exception x)
		{
		}

		res.addOutput ("themeStyleUrl", "/aktera/styles/" + themeName + ".css");
		res.addOutput ("themeImagesUrl", "/aktera/images/" + themeName);
		res.addOutput ("layoutUrl", "/aktera/templates/" + layoutName + "/");
		res.addOutput ("includeUrl", "/aktera/include/" + layoutName + "-");

		return res;
	}
}
