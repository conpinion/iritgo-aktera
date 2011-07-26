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

package de.iritgo.aktera.authentication;


import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.simplelife.string.StringTools;


/**
 *
 */
public class AuthenticatorImpl implements Authenticator
{
	private UserDAO userDAO;

	public void setUserDAO (UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	/**
	 * @see de.iritgo.aktera.authentication.Authenticator#authenticate(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean authenticate (String userName, String loginPassword)
	{
		AkteraUser user = userDAO.findUserByName (userName);

		if (user == null || ! StringTools.digest (loginPassword).equals (user.getPassword ()))
		{
			return false;
		}

		return true;
	}
}
