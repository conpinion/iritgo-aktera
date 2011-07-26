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

package de.iritgo.aktera.journal.command;


import de.iritgo.aktario.core.command.Command;
import de.iritgo.aktario.framework.client.Client;
import de.iritgo.aktario.framework.dataobject.gui.QueryPane;


/**
 * Address execute program
 *
 * @version $Id: CallNumber.java,v 1.12 2006/09/28 12:42:06 grappendorf Exp $
 */
public class RefreshJournalPaneCommand extends Command
{
	/**
	 * Create a new address execute program command
	 */
	public RefreshJournalPaneCommand ()
	{
		super ("aktario-journal.RefreshJournalPane");
	}

	/**
	 * Execute the command.
	 */
	public void perform ()
	{
		QueryPane queryPane = ((QueryPane) Client.instance ().getClientGUI ().getDesktopManager ().getDisplay (
						"de.iritgo.aktera.journal.EmbeddedJournal").getGUIPane ());

		if (queryPane != null)
		{
			queryPane.refresh ();
		}
	}
}
