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

package de.iritgo.aktera.journal;


import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.iobject.IObject;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.framework.base.IObjectCreatedEvent;
import de.iritgo.aktario.framework.base.IObjectCreatedListener;
import de.iritgo.aktario.framework.dataobject.DynDataObject;
import de.iritgo.aktario.framework.server.Server;
import de.iritgo.aktario.framework.user.User;
import de.iritgo.aktario.framework.user.UserEvent;
import de.iritgo.aktario.framework.user.UserListener;
import de.iritgo.aktera.aktario.akteraconnector.AkteraQuery;


/**
 *
 */
public class JournalServerManager extends BaseObject implements Manager, UserListener
{
	/**
	 * Create a new client manager.
	 */
	public JournalServerManager ()
	{
		super ("de.iritgo.aktera.journal.JournalServerManager");
	}

	public void init ()
	{
		Engine.instance ().getEventRegistry ().addListener ("User", this);
		cleanupJournalEntries ();
	}

	/**
	 * Free all client manager resources.
	 */
	public void unload ()
	{
	}

	/**
	 * This method is called when user has logged in.
	 *
	 * Loggin/Logoff the user in the keel-framework.
	 *
	 * @param event The user event.
	 */
	public void userEvent (UserEvent event)
	{
		if ((event != null) && (event.isLoggedIn ()))
		{
		}

		if ((event != null) && (event.isLoggedOut ()))
		{
			User user = event.getUser ();

			// Remove unused journal entry caches
			LinkedList<DynDataObject> removeList = new LinkedList<DynDataObject> ();
			for (Iterator i = Engine.instance ().getBaseRegistry ().iterator ("aktera.journal.list.notvisible"); i
							.hasNext ();)
			{
				DynDataObject akteraQuery = (DynDataObject) i.next ();
				if (Long.valueOf (akteraQuery.getStringAttribute ("userUniqueId")) == user.getUniqueId ())
				{
					removeList.add (akteraQuery);
				}
			}
			for (DynDataObject akteraQuery : removeList)
			{
				Engine.instance ().getBaseRegistry ().remove (akteraQuery);
			}
			System.out.println ("DynDataObjects removed: " + removeList.size ());
		}
	}

	public void cleanupJournalEntries ()
	{
		Calendar date = Calendar.getInstance ();
		date.roll (Calendar.DAY_OF_MONTH, true);
		date.set (Calendar.HOUR_OF_DAY, 1);
		date.set (Calendar.MINUTE, 0);
		date.set (Calendar.SECOND, 0);
		date.set (Calendar.MILLISECOND, 0);
		System.out.println ("Journal cache refresh at: " + date.getTime ());
		Timer timer = new Timer ("CleanupJournalEntriesThread");
		timer.schedule (new TimerTask ()
		{
			@Override
			public void run ()
			{
				int count = 0;
				LinkedList<DynDataObject> removeList = new LinkedList<DynDataObject> ();
				for (Iterator j = Server.instance ().getUserRegistry ().userIterator (); j.hasNext ();)
				{
					User user = (User) j.next ();
					long userId = user.getUniqueId ();
					for (Iterator i = Engine.instance ().getBaseRegistry ().iterator ("aktera.journal.list.notvisible"); i
									.hasNext ();)
					{
						DynDataObject akteraQuery = (DynDataObject) i.next ();
						if (Long.valueOf (akteraQuery.getStringAttribute ("userUniqueId")) == userId)
						{
							++count;
							if (count > 120)
								removeList.add (akteraQuery);
						}
					}
					for (DynDataObject akteraQuery : removeList)
					{
						Engine.instance ().getBaseRegistry ().remove (akteraQuery);
					}
					// Remove after succefull test
					System.out.println ("Cleanup journal entry cache: " + removeList.size () + " removed.");
				}
			}

		}, date.getTime (), 1000 * 60 * 60 * 24);
	}
}
