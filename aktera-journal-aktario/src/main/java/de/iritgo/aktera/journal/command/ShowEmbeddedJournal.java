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
import de.iritgo.aktario.framework.client.command.ShowWindow;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.dataobject.DataObjectTools;
import de.iritgo.aktera.aktario.akteraconnector.AkteraQuery;
import java.util.Properties;


/**
 * Show an embedded journal pane.
 */
public class ShowEmbeddedJournal extends Command
{
	/** Indicates special handling for the first time execution. */
	private boolean firstActivation = true;

	/**
	 * Initialize the command.
	 */
	public ShowEmbeddedJournal ()
	{
		super ("de.iritgo.aktera.journal.ShowEmbeddedJournal");
	}

	/**
	 * Display the embedded journal pane.
	 */
	@Override
	public void perform ()
	{
		if (firstActivation)
		{
			Properties props = new Properties ();

			props.put ("closable", false);
			props.put ("iconifiable", false);
			props.put ("maximizable", false);
			props.put ("maximized", false);
			props.put ("titlebar", false);
			props.put ("searchbutton", "no");
			props.put ("toolbar", "no");
			props.put ("rowHeight", 60);
			props.put ("headerOff", "true");

			AkteraQuery journalQuery = new AkteraQuery ("aktera.journal.list.notvisible",
							"de.iritgo.aktera.journal.EmbeddedJournal");

			DataObjectTools.executeQuery (journalQuery);
			CommandTools.performAsync (new ShowWindow ("QueryPane", "de.iritgo.aktera.journal.EmbeddedJournal",
							"default", journalQuery), props);
			firstActivation = false;
		}
		else
		{
			if (Client.instance ().getClientGUI ().getDesktopManager ().getDisplay (
							"de.iritgo.aktera.journal.EmbeddedJournal") == null)
			{
				firstActivation = true;
				perform ();

				return;
			}

			Client.instance ().getClientGUI ().getDesktopManager ().getDisplay (
							"de.iritgo.aktera.journal.EmbeddedJournal").bringToFront ();
			Client.instance ().getClientGUI ().getDesktopManager ().getDisplay (
							"de.iritgo.aktera.journal.EmbeddedJournal").show ();
		}
	}
}
