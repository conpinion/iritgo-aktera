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


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.command.Command;
import de.iritgo.aktario.framework.action.ActionTools;
import de.iritgo.aktario.framework.base.action.CommandAction;
import de.iritgo.aktario.framework.dataobject.DataObjectManager;
import de.iritgo.aktario.framework.dataobject.QueryRegistry;
import de.iritgo.aktario.framework.server.Server;
import de.iritgo.aktario.framework.user.User;
import de.iritgo.aktario.framework.user.UserRegistry;
import de.iritgo.aktera.aktario.akteraconnector.AkteraQuery;
import java.util.Iterator;
import java.util.Properties;


/**
 * Show an embedded journal pane.
 */
public class RefreshJournal extends Command
{
	/**
	 * Initialize the command.
	 */
	public RefreshJournal ()
	{
		super ("de.iritgo.aktera.journal.RefreshJournal");
	}

	/**
	 * Display the embedded journal pane.
	 */
	@Override
	public void perform ()
	{
		String akteraUserName = properties.getProperty ("akteraUserName");

		DataObjectManager dom = (DataObjectManager) Engine.instance ().getManagerRegistry ().getManager (
						"DataObjectManager");
		QueryRegistry queryRegistry = dom.getQueryRegistry ();

		for (Iterator i = queryRegistry.queryIterator ("aktera.journal.list.notvisible"); i.hasNext ();)
		{
			AkteraQuery query = (AkteraQuery) i.next ();
			long userUniqueId = query.getUserUniqueId ();
			UserRegistry userRegistry = Server.instance ().getUserRegistry ();
			User iritgoUser = userRegistry.getUser (userUniqueId);

			if (akteraUserName.equals (iritgoUser.getName ()))
			{
				Properties props = new Properties ();

				props.put ("akteraQuery", query.getUniqueId ());

				ActionTools.sendToClient (iritgoUser.getName (), new CommandAction (
								"aktario-journal.RefreshJournalPane", props));
			}
		}
	}
}
