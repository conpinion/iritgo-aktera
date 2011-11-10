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

package de.iritgo.aktera.aktario.gui;


import de.iritgo.aktario.core.gui.IAction;
import de.iritgo.aktario.core.gui.IDisplay;
import de.iritgo.aktario.core.gui.SwingDesktopManager;
import de.iritgo.aktario.framework.client.Client;
import de.iritgo.aktario.framework.client.command.ShowOtherFrame;
import de.iritgo.aktario.framework.client.command.ShowWindow;
import de.iritgo.aktario.framework.client.gui.OtherFrame;
import de.iritgo.aktario.framework.client.gui.OtherFrameCloseListener;
import de.iritgo.aktario.framework.client.gui.OtherJDesktopPane;
import de.iritgo.aktario.framework.command.CommandTools;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.util.Properties;


@SuppressWarnings("serial")
public class ChatExtrasMenuItem extends IAction implements OtherFrameCloseListener
{
	private boolean forwardingToggle = true;

	public ChatExtrasMenuItem()
	{
		super("chatConference", new ImageIcon(ChatExtrasMenuItem.class.getResource("/resources/chat.png")));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (forwardingToggle)
		{
			Properties props = new Properties();

			props.put("closable", Boolean.FALSE);
			props.put("iconifiable", Boolean.FALSE);
			props.put("maximizable", Boolean.FALSE);
			props.put("maximized", Boolean.TRUE);
			props.put("titlebar", Boolean.FALSE);
			CommandTools.performSimple(new ShowOtherFrame(this, "common.chatview", "common.chatview",
							OtherFrame.SIZE_PACK));

			CommandTools.performAsync(new ShowWindow("common.chatview", "common.chatview"), props);

			forwardingToggle = false;
		}
		else
		{
			IDisplay display = (IDisplay) Client.instance().getClientGUI().getDesktopManager().getDisplay(
							"common.chatview");
			OtherJDesktopPane odp = (OtherJDesktopPane) ((SwingDesktopManager) Client.instance().getClientGUI()
							.getDesktopManager()).getDesktopPane(display.getDesktopId());

			odp.closeAll();

			forwardingToggle = true;
		}
	}

	public void otherFrameClosed(OtherFrame otherFrame)
	{
		forwardingToggle = true;
	}
}
