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

package de.iritgo.aktera.aktario;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.gui.IAction;
import de.iritgo.aktario.core.gui.IMenuItem;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.core.plugin.PluginEventListener;
import de.iritgo.aktario.core.plugin.PluginStateEvent;
import de.iritgo.aktario.framework.client.gui.ClientGUI;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.manager.ClientManager;
import de.iritgo.aktera.aktario.gui.AkteraAktarioGUI;
import javax.swing.JMenuItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 *
 */
public class AkteraAktarioClientManager extends BaseObject implements Manager, ClientManager, PluginEventListener
{
	/** Manager ID */
	public static final String ID = "client";

	/** Image ID */
	public static final String IMAGE_LOGIN = "login";

	/** Image ID */
	public static final String IMAGE_LOGO_24 = "logo24";

	/** The main aktario gui */
	private AkteraAktarioGUI aktarioGUI;

	/** Extension point for adding items to the toolbar */
	private List<IAction> toolBarItems = new ArrayList();

	/** Extension point for adding items to the extras menu */
	private List<IAction> extrasMenuItems = new ArrayList();

	/** Extension point for adding items to the system tray menu */
	private List<JMenuItem> systemTrayMenuItems = new ArrayList();

	/**
	 * Create a new client manager.
	 */
	public AkteraAktarioClientManager()
	{
		super("client");
	}

	public List<IAction> getToolBarItems()
	{
		return toolBarItems;
	}

	public void addToolBarItem(IAction item)
	{
		toolBarItems.add(item);
	}

	public List<IAction> getExtrasMenuItems()
	{
		return extrasMenuItems;
	}

	public void addExtrasMenuItem(IAction item)
	{
		extrasMenuItems.add(item);
	}

	public List<JMenuItem> getSystemTrayMenuItems()
	{
		return systemTrayMenuItems;
	}

	public void addSystemTrayMenuItem(JMenuItem item)
	{
		systemTrayMenuItems.add(item);
	}

	/**
	 * Initialize the client manager.
	 */
	public void init()
	{
		aktarioGUI = new AkteraAktarioGUI();
		Engine.instance().getEventRegistry().addListener("Plugin", this);
	}

	/**
	 * Free all client manager resources.
	 */
	public void unload()
	{
	}

	/**
	 * Retrieve the main aktario gui.
	 *
	 * @return The aktario gui.
	 */
	public ClientGUI getClientGUI()
	{
		return aktarioGUI;
	}

	public void pluginEvent(PluginStateEvent event)
	{
		if (event.allPluginsInitialized())
		{
			Properties props = new Properties();

			props.setProperty("command", "test.aktera-aktario.showBuddyListCommand");
			props.setProperty("name", "showBuddyList");

			CommandTools.performSimple("aktario-xmlrpc.AddXmlRpcCommand", props);
		}
	}
}
