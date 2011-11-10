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


import org.springframework.security.core.context.SecurityContextHolder;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;


/**
 *
 */
public class SecurityContextImpl implements SecurityContext
{
	/** User DAO */
	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	/**
	 * @see de.iritgo.aktera.authentication.SecurityContext#getContext()
	 */
	public org.springframework.security.core.context.SecurityContext getContext()
	{
		return SecurityContextHolder.getContext();
	}

	/**
	 * @see de.iritgo.aktera.authentication.SecurityContext#setContext(org.springframework.security.context.SecurityContext)
	 */
	public void setContext(org.springframework.security.core.context.SecurityContext securityContext)
	{
		SecurityContextHolder.setContext(securityContext);
	}

	/**
	 * @see de.iritgo.aktera.authentication.SecurityContext#getUserName()
	 */
	public String getUserName()
	{
		return getContext().getAuthentication().getName();
	}

	/**
	 * @see de.iritgo.aktera.authentication.SecurityContext#getUser()
	 */
	public AkteraUser getUser()
	{
		return userDAO.findUserByName(getUserName());
	}

	/**
	 * @see de.iritgo.aktera.authentication.SecurityContext#hasRole(java.lang.String)
	 */
	public boolean hasRole(AkteraUser user, String role)
	{
		return userDAO.userHasRole(user, role);
	}

	/**
	 * @see de.iritgo.aktera.authentication.SecurityContext#checkRole(de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser, java.lang.String)
	 */
	public void checkRole(AkteraUser user, String role) throws SecurityException
	{
		if (! hasRole(user, role))
		{
			throw new SecurityException("Insufficient rights for user: " + user.getName());
		}
	}
}
