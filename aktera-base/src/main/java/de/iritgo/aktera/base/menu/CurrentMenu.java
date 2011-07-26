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

package de.iritgo.aktera.base.menu;


import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.tools.UserTools;


/**
 * This component creates the current menu.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.current-menu"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.current-menu" id="aktera.current-menu" logger="aktera"
 */
public class CurrentMenu extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		SystemConfigManager systemConfigManager = (SystemConfigManager) req.getSpringBean (SystemConfigManager.ID);

		String menu = (String) UserTools.getContextObject (req, "aktera.currentMenu");

		if (menu == null && UserTools.currentUserIsInGroup (req, "admin"))
		{
			menu = (String) systemConfigManager.get ("system", "startMenuAdmin");
		}

		if (menu == null && UserTools.currentUserIsInGroup (req, "manager"))
		{
			menu = (String) systemConfigManager.get ("system", "startMenuManager");
		}

		if (menu == null && UserTools.getCurrentUserId (req) != null)
		{
			menu = (String) systemConfigManager.get ("system", "startMenu");
		}

		if (menu != null)
		{
			return ModelTools.callModel (req, menu);
		}
		else
		{
			return req.createResponse ();
		}
	}
}
