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


import de.iritgo.aktera.base.module.ModuleTools;
import de.iritgo.aktera.base.tools.CheckerTools;
import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.license.LicenseTools;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * This component creates a function menu from it's configuration.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.menu"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.menu" id="aktera.menu" logger="aktera"
 */
public class Menu extends StandardLogEnabledModel
{
	/**
	 * Menu group.
	 */
	protected class MenuGroup
	{
		public String id;

		public String label;

		public String bundle;

		public int position;
	}

	/**
	 * Function item.
	 */
	protected class FunctionItem
	{
		public String category;

		public String menu;

		public String menuItem;

		public String model;

		public String bean;

		public String label;

		public String bundle;

		public String icon;

		public String inactiveIcon;

		public String bigIcon;

		public int position;

		public String check;

		public String feature;

		public String role;

		public String group;

		public String id;

		public String permission;
	}

	/** Sort by position (default). */
	protected static final int SORT_POS = 0;

	/** Sort by name. */
	protected static final int SORT_NAME = 1;

	/** True if the configuration was already read. */
	protected Boolean configRead = false;

	/** Function list. */
	protected List<FunctionItem> functions = new LinkedList();

	/** Menu groups. */
	protected List<MenuGroup> menuGroups = new LinkedList();

	/** Menu style. */
	protected String style;

	/** Sorting method. */
	protected int sort = SORT_POS;

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		try
		{
			SystemConfigManager systemConfigManager = (SystemConfigManager) SpringTools.getBean(SystemConfigManager.ID);

			PermissionManager permissionManager = (PermissionManager) SpringTools.getBean(PermissionManager.ID);

			ModelResponse res = req.createResponse();

			boolean byGroups = NumberTools.toBool(req.getParameter("byGroups"), false);

			readConfig(req);

			int itemsPerRow = 3;

			if ("toolBar".equals(style))
			{
				itemsPerRow = 16;
			}

			String currentItem = (String) UserTools.getContextObject(req, "aktera.currentMenuItem");

			if (currentItem == null && UserTools.currentUserIsInGroup(req, "admin"))
			{
				currentItem = (String) systemConfigManager.get("system", "startMenuItemAdmin");
			}

			if (currentItem == null && UserTools.currentUserIsInGroup(req, "manager"))
			{
				currentItem = (String) systemConfigManager.get("system", "startMenuItemManager");
			}

			if (currentItem == null && UserTools.getCurrentUserId(req) != null)
			{
				currentItem = (String) systemConfigManager.get("system", "startMenuItem");
			}

			Output outFunctions = res.createOutput("functions");

			if (! byGroups)
			{
				outFunctions.setAttribute("style", style == null ? "none" : style);
				res.add(outFunctions);
			}

			String title = getConfiguration().getChild("title").getValue("functions");

			outFunctions.setAttribute("title", title);

			String bundle = getConfiguration().getChild("bundle").getValue("Aktera");

			outFunctions.setAttribute("bundle", bundle);

			Map<String, List<Command>> tmpMenuGroups = new Hashtable();

			int num = 0;

			for (Iterator i = functions.iterator(); i.hasNext();)
			{
				FunctionItem item = (FunctionItem) i.next();

				if (item.feature != null && ! LicenseTools.getLicenseInfo().hasFeature(item.feature))
				{
					continue;
				}

				if (item.check != null && ! CheckerTools.check(item.check, req, new Properties()))
				{
					continue;
				}

				if (item.role != null && ! UserTools.currentUserIsInGroup(req, "admin")
								&& ! UserTools.currentUserIsInGroup(req, item.role))
				{
					continue;
				}

				if (item.permission != null)
				{
					boolean hasPermission = false;

					for (String p : item.permission.split("\\|"))
					{
						if (permissionManager.hasPermission(UserTools.getCurrentUserName(req), p))
						{
							hasPermission = true;

							break;
						}
					}

					if (! hasPermission)
					{
						continue;
					}
				}

				Command cmd = res.createCommand("aktera.select-menu-item");

				cmd.setName("cmd" + num);
				cmd.setLabel(item.label);
				cmd.setAttribute("bundle", item.bundle);
				cmd.setAttribute("id", item.id);
				cmd.setParameter("item", item.category);

				if (item.bean != null)
				{
					cmd.setBean("de.iritgo.aktera.base.SelectMenuItem");
					cmd.setParameter("targetBean", item.bean);
				}
				else
				{
					cmd.setParameter("targetModel", item.model);
				}

				if (item.menu != null)
				{
					cmd.setParameter("menu", item.menu);
				}

				if (item.menuItem != null)
				{
					cmd.setParameter("menuItem", item.menuItem);
				}

				boolean active = item.category.equals(currentItem);

				//				String icon = active ? item.icon : item.inactiveIcon;
				String icon = item.icon;

				cmd.setAttribute("icon", icon != null ? icon : "menu-bullet");
				cmd.setAttribute("bigIcon", item.bigIcon);

				if (active)
				{
					cmd.setAttribute("active", "Y");
				}

				if (num % itemsPerRow == itemsPerRow - 1)
				{
					cmd.setAttribute("lastInRow", "Y");
				}

				if (! byGroups)
				{
					outFunctions.add(cmd);
				}

				if (byGroups && item.group != null)
				{
					List<Command> menuItems = tmpMenuGroups.get(item.group);

					if (menuItems == null)
					{
						menuItems = new LinkedList<Command>();
						tmpMenuGroups.put(item.group, menuItems);
					}

					menuItems.add(cmd);
				}

				num++;
			}

			if (byGroups)
			{
				Output outMenuGroups = res.createOutput("menuGroups");

				res.add(outMenuGroups);

				for (MenuGroup menuGroup : menuGroups)
				{
					Output outMenuGroup = res.createOutput(menuGroup.id, menuGroup.id);

					outMenuGroups.add(outMenuGroup);
					outMenuGroup.setAttribute("label", menuGroup.label);
					outMenuGroup.setAttribute("bundle", menuGroup.bundle);

					List<Command> menuItems = tmpMenuGroups.get(menuGroup.id);

					if (menuItems != null)
					{
						for (Command cmd : menuItems)
						{
							outMenuGroup.add(cmd);
						}
					}
				}
			}

			return res;
		}
		catch (ConfigurationException x)
		{
			throw new ModelException(x);
		}
	}

	/**
	 * Retrieve the model configuration.
	 *
	 * @param req The model configuration.
	 */
	public void readConfig(ModelRequest req) throws ModelException, ConfigurationException
	{
		if (configRead)
		{
			return;
		}

		synchronized (configRead)
		{
			if (configRead)
			{
				return;
			}
			configRead = true;

			Configuration config = getConfiguration();

			for (Configuration groupConfig : config.getChildren("group"))
			{
				MenuGroup group = new MenuGroup();

				group.id = groupConfig.getAttribute("id", "null");
				group.label = groupConfig.getAttribute("label", group.id);
				group.bundle = groupConfig.getAttribute("bundle", "Aktera");
				group.position = groupConfig.getAttributeAsInteger("position", 0);
				menuGroups.add(group);
			}

			Collections.sort(menuGroups, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					return ((MenuGroup) o1).position - ((MenuGroup) o2).position;
				}
			});

			style = config.getChild("style").getValue("default");

			for (Configuration itemConfig : config.getChildren("item"))
			{
				if (itemConfig.getAttribute("ifModule", null) != null
						&& ! ModuleTools.moduleExists(req, itemConfig.getAttribute("ifModule")))
				{
					continue;
				}

				if (itemConfig.getAttribute("id", null) == null)
				{
					System.out.println("[Menu] No id defined for menu item in menu '" + config.getAttribute("id") + "'");

					continue;
				}

				int position = 0;
				String pos = itemConfig.getAttribute("pos", "C");

				if ("SS".equals(pos))
				{
					position = - 100000;
				}
				else if ("S".equals(pos))
				{
					position = - 10000;
				}
				else if ("H".equals(pos))
				{
					position = - 1000;
				}
				else if ("C".equals(pos))
				{
					position = 0;
				}
				else if ("T".equals(pos))
				{
					position = 1000;
				}
				else if ("E".equals(pos))
				{
					position = 10000;
				}
				else if ("EE".equals(pos))
				{
					position = 100000;
				}
				else
				{
					position = NumberTools.toInt(pos, 0);
				}

				FunctionItem functionItem = new FunctionItem();

				functionItem.id = itemConfig.getAttribute("id", null);
				functionItem.category = itemConfig.getAttribute("id", null);
				functionItem.menu = itemConfig.getAttribute("menu", null);
				functionItem.menuItem = itemConfig.getAttribute("menuItem", null);
				functionItem.model = itemConfig.getAttribute("model", "unknown");
				functionItem.bean = itemConfig.getAttribute("bean", null);
				functionItem.label = itemConfig.getAttribute("title", "unknown");
				functionItem.bundle = itemConfig.getAttribute("bundle", "Aktera");
				functionItem.icon = itemConfig.getAttribute("icon", "menu-bullet");
				functionItem.bigIcon = itemConfig.getAttribute("bigIcon", functionItem.icon);
				functionItem.inactiveIcon = itemConfig.getAttribute("inactiveIcon", itemConfig.getAttribute("icon",
						"menu-bullet"));
				functionItem.position = position;
				functionItem.check = itemConfig.getAttribute("ifCheck", null);
				functionItem.feature = itemConfig.getAttribute("ifFeature", null);
				functionItem.role = itemConfig.getAttribute("ifRole", null);
				functionItem.group = itemConfig.getAttribute("group", null);
				functionItem.permission = itemConfig.getAttribute("ifPermission", null);

				functions.add(functionItem);
			}

			Collections.sort(functions, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					return ((FunctionItem) o1).position - ((FunctionItem) o2).position;
				}
			});
		}
	}
}
