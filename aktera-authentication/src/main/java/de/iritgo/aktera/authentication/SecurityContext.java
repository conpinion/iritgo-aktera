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


/**
 *
 */
public interface SecurityContext
{
	/**
	 * Retrieve the current security context.
	 *
	 * @return A security context
	 */
	org.springframework.security.core.context.SecurityContext getContext();

	/**
	 * Set the current security context.
	 *
	 * @param securityContext A security context
	 */
	void setContext(org.springframework.security.core.context.SecurityContext securityContext);

	/**
	 * Get the name of the authenticated user.
	 *
	 * @return The user name
	 */
	public String getUserName();

	/**
	 * Get the authenticated user DAO.
	 *
	 * @return The user DAO
	 */
	public AkteraUser getUser();

	/**
	 * Check if the user has the specified role.
	 *
	 * @param user The user to check
	 * @param role The role name
	 * @return True if the user has this role
	 */
	public boolean hasRole(AkteraUser user, String role);

	/**
	 * Check if the user has got the specified role.
	 *
	 * @param user The user to check
	 * @param role The role name
	 * @throws SecurityException If the user hasn't got the specified role
	 */
	public void checkRole(AkteraUser user, String role) throws SecurityException;
}
