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

package de.iritgo.aktera.journal.gui;


import de.iritgo.aktario.core.gui.IAction;
import de.iritgo.aktario.framework.appcontext.AppContext;
import de.iritgo.aktario.framework.command.CommandTools;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.util.Properties;


/**
 *
 */
@SuppressWarnings("serial")
public class JournalToolBarItem extends IAction
{
	public JournalToolBarItem()
	{
		setSmallIcon(new ImageIcon(getClass().getResource("/resources/journal-16.png")));
		setToolTipText("journalButton");
	}

	public void actionPerformed(ActionEvent e)
	{
		Properties props = new Properties();

		props.setProperty("model", "de.telcat.iptell.base.journal.TurnOffJournalIndicationLed");
		props.setProperty("name", AppContext.instance().getUser().getName());
		CommandTools.performAsync("AkteraAktarioKeelCommand", props);
		CommandTools.performSimple("de.iritgo.aktera.journal.ShowEmbeddedJournal");
	}
}
