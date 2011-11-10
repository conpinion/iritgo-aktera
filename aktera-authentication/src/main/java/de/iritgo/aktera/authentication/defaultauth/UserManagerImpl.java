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

package de.iritgo.aktera.authentication.defaultauth;


import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.event.EventManager;
import de.iritgo.simplelife.string.StringTools;
import java.util.Properties;


/**
 * Implementation of the user manager.
 */
public class UserManagerImpl implements UserManager
{
	/** User DAO */
	private UserDAO userDAO;

	/** Event manager */
	private EventManager eventManager;

	/**
	 * Set the user DAO.
	 *
	 * @param userDAO The user DAO
	 */
	public void setUserDAO(UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	/**
	 * @param eventManager The new eventManager.
	 */
	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.UserManager#renameUser(java.lang.String, java.lang.String, String, String, String)
	 */
	public void renameUser(String oldName, String newName, String newFirstName, String newLastName, String newEmail)
	{
		AkteraUser user = userDAO.findUserByName(oldName);

		if (user == null)
		{
			return;
		}

		user.setName(newName);

		if (StringTools.isNotTrimEmpty(newEmail))
		{
			user.setEmail(newEmail);
		}

		userDAO.updateUser(user);

		Properties props = new Properties();

		props.put("id", user.getId());
		props.put("oldName", oldName);
		props.put("newName", newName);
		props.put("newFirstName", newFirstName);
		props.put("newLastName", newLastName);
		props.put("newEmail", newEmail);
		eventManager.fire("aktera.user.renamed", props);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.UserManager#isUserInGroup(java.lang.Integer, java.lang.Integer)
	 */
	public boolean isUserInGroup(Integer userId, Integer groupId)
	{
		return userDAO.findAkteraGroupEntryByUserIdAndGroupId(userId, groupId) != null;
	}
}
