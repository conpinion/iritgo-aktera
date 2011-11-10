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


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.base.module.ModuleTools;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import java.util.Iterator;


/**
 * This model creates a list of menus from it's model configuration.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.menu"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.menu" id="aktera.tools.menu" logger="aktera"
 */
public class Menu extends SecurableStandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		Configuration config = getConfiguration();

		if (config == null)
		{
			return res;
		}

		UserEnvironment userEnv = null;

		try
		{
			userEnv = (UserEnvironment) req.getContext().get(UserEnvironment.CONTEXT_KEY);
		}
		catch (ContextException x)
		{
		}

		Output outMenuList = res.createOutput("menuList");

		res.add(outMenuList);

		Configuration[] menus = config.getChildren("menu");

		for (int i = 0; i < menus.length; ++i)
		{
			Configuration menuConfig = menus[i];

			try
			{
				if (menuConfig.getAttribute("ifModule", null) != null
								&& ! ModuleTools.moduleExists(req, menuConfig.getAttribute("ifModule")))
				{
					continue;
				}
			}
			catch (ConfigurationException x)
			{
			}

			String menuId = "menu_" + menuConfig.getAttribute("id", "");

			Output outMenu = null;

			for (Iterator j = outMenuList.getAll().iterator(); j.hasNext();)
			{
				Output aMenu = (Output) j.next();

				if (aMenu.getName().equals(menuId))
				{
					outMenu = aMenu;

					break;
				}
			}

			if (outMenu == null)
			{
				outMenu = res.createOutput(menuId);
				outMenuList.add(outMenu);
				outMenu.setAttribute("title", menuConfig.getAttribute("title", "$noTitle"));
			}

			Command cmd = null;

			Configuration[] items = menus[i].getChildren("item");

			int numVisibleItems = 0;

			for (int j = 0; j < items.length; ++j)
			{
				Configuration itemConfig = items[j];

				boolean itemAllowed = true;

				String model = itemConfig.getAttribute("model", null);

				if (model == null)
				{
					log.info("No model specified for menu item " + i + "/" + j);

					continue;
				}

				try
				{
					boolean validUser = itemConfig.getAttributeAsBoolean("validUser");

					try
					{
						itemAllowed = itemAllowed
										&& (validUser == (userEnv != null && userEnv.getUid() != UserEnvironment.ANONYMOUS_UID));
					}
					catch (AuthorizationException x)
					{
						itemAllowed = ! validUser;
					}
				}
				catch (ConfigurationException x)
				{
				}

				try
				{
					String userGroup = itemConfig.getAttribute("userGroup");

					try
					{
						itemAllowed = itemAllowed && userEnv != null && userEnv.getGroups().contains(userGroup);
					}
					catch (AuthorizationException x)
					{
						itemAllowed = false;
					}

					String notUserGroup = itemConfig.getAttribute("notUserGroup");

					if (notUserGroup != null)
					{
						try
						{
							itemAllowed = itemAllowed && userEnv != null
											&& ! userEnv.getGroups().contains(notUserGroup);
						}
						catch (AuthorizationException x)
						{
							itemAllowed = false;
						}
					}
				}
				catch (ConfigurationException x)
				{
				}

				if (itemAllowed)
				{
					cmd = res.createCommand(model);
					outMenu.add(cmd);

					cmd.setLabel(itemConfig.getAttribute("title", "$noTitle"));
					cmd.setAttribute("hasNext", new Boolean(j + 1 < items.length));
					cmd.setAttribute("bundle", itemConfig.getAttribute("bundle", "Aktera"));

					if (itemConfig.getAttribute("icon", null) != null)
					{
						cmd.setAttribute("icon", itemConfig.getAttribute("icon", "menu-bullet"));
					}

					++numVisibleItems;
				}
			}

			outMenu.setAttribute("numVisibleItems", new Integer(numVisibleItems));

			if (cmd != null)
			{
				cmd.setAttribute("last", new Boolean(true));
			}
		}

		return res;
	}
}
