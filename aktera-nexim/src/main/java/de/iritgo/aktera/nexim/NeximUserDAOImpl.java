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

package de.iritgo.aktera.nexim;


import com.sun.net.httpserver.Authenticator;
import de.iritgo.nexim.tools.Digester;
import de.iritgo.nexim.user.User;
import de.iritgo.nexim.user.UserDAO;
import de.iritgo.nexim.user.UserManager;
import de.iritgo.simplelife.string.StringTools;
import java.util.List;


/**
 *
 */
public class NeximUserDAOImpl implements UserDAO
{
	/** Aktera user DAO */
	de.iritgo.aktera.authentication.defaultauth.entity.UserDAO akteraUserDao;

	/** Authenticator service */
	de.iritgo.aktera.authentication.Authenticator akteraAuthenticator;

	/**
	 * Set the Aktera user DAO.
	 *
	 * @param akteraUserDao The user DAO
	 */
	public void setAkteraUserDao(de.iritgo.aktera.authentication.defaultauth.entity.UserDAO akteraUserDao)
	{
		this.akteraUserDao = akteraUserDao;
	}

	/**
	 * @param akteraAuthenticator The new akteraAuthenticator.
	 */
	public void setAkteraAuthenticator(de.iritgo.aktera.authentication.Authenticator akteraAuthenticator)
	{
		this.akteraAuthenticator = akteraAuthenticator;
	}

	/**
	 * @see de.iritgo.nexim.user.UserDAO#addUser(de.iritgo.nexim.user.User)
	 */
	public void addUser(User newUser)
	{
	}

	/**
	 * @see de.iritgo.nexim.user.UserDAO#getUser(java.lang.String)
	 */
	public User getUser(String username)
	{
		de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser akteraUser = akteraUserDao
						.findUserByName(username);

		if (akteraUser != null)
		{
			User user = new User();

			user.setName(akteraUser.getName());
			user.setPassword(akteraUser.getPassword());

			return user;
		}

		return null;
	}

	/**
	 * @see de.iritgo.nexim.user.UserDAO#getUsers(java.lang.String)
	 */
	public List<User> getUsers(String searchPattern)
	{
		return null;
	}

	/**
	 * @see de.iritgo.nexim.user.UserDAO#removeUser(java.lang.String)
	 */
	public User removeUser(String username)
	{
		return null;
	}

	/**
	 * @see de.iritgo.nexim.user.UserDAO#isValidPassword(de.iritgo.nexim.user.User, java.lang.String, de.iritgo.nexim.user.UserManager.AuthenticationType)
	 */
	public boolean isValidPassword(User user, UserManager.AuthenticationType type, String password, String sessionId)
	{
		switch (type)
		{
			case PLAIN:
				return akteraAuthenticator.authenticate(user.getName(), password);

			default:
				return false;
		}
	}
}
