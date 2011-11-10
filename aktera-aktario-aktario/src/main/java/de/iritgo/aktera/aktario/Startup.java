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


import de.iritgo.aktario.client.gui.AktarioGUI;
import de.iritgo.aktario.core.command.Command;
import de.iritgo.aktario.core.gui.IDockingDesktopLayouter;
import de.iritgo.aktario.core.logger.Log;
import de.iritgo.aktario.framework.appcontext.AppContext;
import de.iritgo.aktario.framework.base.DataObject;
import de.iritgo.aktario.framework.client.Client;
import de.iritgo.aktario.framework.client.command.ShowWindow;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.dataobject.DataObjectTools;
import javax.swing.JDesktopPane;
import java.awt.Rectangle;
import java.util.Properties;


/**
 * Startup.
 *
 * <table>
 *   <tr><th>Parameter</th><th>Type</th><th>Description</th></tr>
 *   <tr><td>user</td><td>AktarioUser</td><td>The current user.</td></td>
 * </table>
 */
public class Startup extends Command
{
	/**
	 * Create a new startup command.
	 */
	public Startup()
	{
		super("Startup");
	}

	/**
	 * This command is called after a successful server login.
	 * It opens all initial client windows.
	 */
	public void perform()
	{
		if (properties.get("user") == null)
		{
			Log.logError("client", "Startup", "Missing user");

			return;
		}

		try
		{
			JDesktopPane frame = ((AktarioGUI) Client.instance().getClientGUI()).getDesktopPane();

			Properties props = new Properties();

			props.put("closable", false);
			props.put("iconifiable", false);
			props.put("maximizable", false);
			props.put("maximized", false);
			props.put("titlebar", false);
			props.put("visible", true);

			DataObject dataObject = DataObjectTools.registerDataObject("BuddyList", AppContext.instance().getUser()
							.getUniqueId());

			CommandTools.performAsync(new ShowWindow("BuddyListPane", dataObject), props);

			props = new Properties();
			props.put("closable", false);
			props.put("iconifiable", false);
			props.put("maximizable", false);
			props.put("maximized", false);
			props.put("titlebar", false);
			props.put("visible", true);
			CommandTools.performAsync(new ShowWindow("CallManagerInstantCallPane"), props);
		}
		catch (Exception x)
		{
			Log.logError("client", "DefaultStartup", x.toString());
		}
	}
}
