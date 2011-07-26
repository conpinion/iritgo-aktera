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

package de.iritgo.aktera.aktario.user;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.user.AktarioUser;
import de.iritgo.aktario.core.user.AktarioUserManager;
import de.iritgo.aktario.core.user.AktarioUserRegistry;
import de.iritgo.aktera.event.Event;
import de.iritgo.simplelife.string.StringTools;


/**
 * Implementation of the iritgo user manager.
 */
public class IritgoUserManagerImpl implements IritgoUserManager
{
	/** Iritgo user DAO */
	private IritgoUserDAO iritgoUserDAO;

	/**
	 * @param iritgoUserDAO The new iritgoUserDAO.
	 */
	public void setIritgoUserDAO (IritgoUserDAO iritgoUserDAO)
	{
		this.iritgoUserDAO = iritgoUserDAO;
	}

	/**
	 * @see de.iritgo.aktera.aktario.user.IritgoUserManager#onUserRenamed(de.iritgo.aktera.event.Event)
	 */
	public void onUserRenamed (Event event)
	{
		String oldName = event.getProperties ().getProperty ("oldName");
		String newName = event.getProperties ().getProperty ("newName");
		String newFirstName = event.getProperties ().getProperty ("newFirstName");
		String newLastName = event.getProperties ().getProperty ("newLastName");
		String newEmail = event.getProperties ().getProperty ("newEmail");

		AktarioUserManager aktarioUserManager = (AktarioUserManager) Engine.instance ().getManager (
						"AktarioUserManager");
		AktarioUserRegistry aktarioUserRegistry = aktarioUserManager.getUserRegistry ();

		AktarioUser aktarioUser = aktarioUserRegistry.getUserByName (oldName);

		if (aktarioUser != null)
		{
			aktarioUser.setAttribute ("name", newName);

			if (StringTools.isNotTrimEmpty (newEmail))
			{
				aktarioUser.setEmail (newEmail);
			}

			if (StringTools.isNotTrimEmpty (newFirstName) || StringTools.isNotTrimEmpty (newLastName))
			{
				StringBuilder newFullName = new StringBuilder (StringTools.trim (newFirstName));

				StringTools.appendWithDelimiter (newFullName, StringTools.trim (newLastName), " ");
				aktarioUser.setFullName (newFullName.toString ());
			}

			aktarioUserManager.modifyUser (aktarioUser, oldName);
		}
	}
}
