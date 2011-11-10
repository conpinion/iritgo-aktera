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


import de.iritgo.aktera.authorization.AuthorizationException;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.util.List;


/**
 * This is the interface implemented by services that contain the environment
 * for the logged-in user.
 *
 * A DefaultUserEnvironment object is normally stored in the Context passed to
 * Model objects, and may be used by the Model to provide environment
 * information to other factories and services that it requires. It's purpose
 * is to store information about the current user session (if there is one),
 * including the currently selected database, the ID of the current user, and
 * so forth.
 *
 * @version        $Revision: 1.4 $        $Date: 2004/04/04 23:21:36 $
 * @author Mike Nash
 * @author Shash Chatterjee
 * Created on Jun 8, 2003
 */
public interface UserEnvironment
{
	/**
	 * The key used to store the user env. in the request context
	 */
	public static final String CONTEXT_KEY = "uenvironment";

	/**
	 * Constants identifying the "unknown" user in the not-logged-in state
	 */
	public static final int ANONYMOUS_UID = 0;

	public static final String ANONYMOUS_LOGINNAME = "anonymous";

	public static final String ANONYMOUS_GROUPNAME = "anonymous";

	public static final String ANONYMOUS_USERDESC = "anonymous";

	public static final String ANONYMOUS_DOMAIN = "default";

	/**
	 * After succsful login, store the login context for later use
	 * by at least the logout function.
	 * @param lc The user's JAAS login context
	 * @throws AuthorizationException
	 */
	public void setLoginContext(LoginContext lc) throws AuthorizationException;

	/**
	 * The login context in this env.
	 * @return The login context
	 * @throws AuthorizationException
	 */
	public LoginContext getLoginContext() throws AuthorizationException;

	/**
	 * The JAAS subject from the login context
	 * @return JAAS Subject
	 * @throws AuthorizationException
	 */
	public Subject getSubject() throws AuthorizationException;

	/**
	 * Get the LoginPrincipal
	 * @return the login name
	 * @throws AuthorizationException
	 */
	public String getLoginName() throws AuthorizationException;

	/**
	 * Get the DomainPrincipal
	 * @return The Keel domain name
	 * @throws AuthorizationException
	 */
	public String getDomain() throws AuthorizationException;

	/**
	 * Get the UserDescripPrincipal
	 * @return A description of the user
	 * @throws AuthorizationException
	 */
	public String getUserDescrip() throws AuthorizationException;

	/**
	 * Get the UidPrincipal
	 * @return The UID
	 * @throws AuthorizationException
	 */
	public int getUid() throws AuthorizationException;

	/**
	 * Get the GroupPrincipals
	 * @return The list of groups user belongs to
	 * @throws AuthorizationException
	 */
	public List<String> getGroups() throws AuthorizationException;

	/**
	 * Used by logout functions to reset the SUbject back to "anonymous"
	 * @throws AuthorizationException
	 */
	public void reset() throws AuthorizationException;

	public void setAttribute(String attributeName, Object attributeContents);

	public Object getAttribute(String attributeName);

	public void removeAttribute(String attributeName);

	public void clearAttributes();
}
