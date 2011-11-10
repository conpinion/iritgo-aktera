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

package de.iritgo.aktera.aktario.akteraconnector;


import de.iritgo.aktario.core.gui.IDisplay;
import de.iritgo.aktario.core.gui.SwingDesktopManager;
import de.iritgo.aktario.framework.client.Client;
import de.iritgo.aktario.framework.client.gui.OtherJDesktopPane;
import de.iritgo.aktario.framework.dataobject.gui.DataObjectCommand;
import java.util.Properties;


/**
 *
 */
public class CancelAkteraObjectCommand extends DataObjectCommand
{
	/**
	 * Create a new startup command.
	 */
	public CancelAkteraObjectCommand()
	{
		super("CancelAkteraObjectCommand");
		properties = new Properties();
	}

	/**
	 *
	 */
	public void perform()
	{
		IDisplay display = (IDisplay) Client.instance().getClientGUI().getDesktopManager().getDisplay(
						swingGUIPane.getOnScreenUniqueId());

		OtherJDesktopPane odp = (OtherJDesktopPane) ((SwingDesktopManager) Client.instance().getClientGUI()
						.getDesktopManager()).getDesktopPane(display.getDesktopId());

		odp.closeAll();
	}
}
