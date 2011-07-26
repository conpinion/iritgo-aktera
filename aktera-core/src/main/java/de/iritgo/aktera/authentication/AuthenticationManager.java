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


import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;


/**
 * This is the interface implemented by services that provide user
 * authentication in Keel.
 *
 * The contract for the authentication service is that the
 * class using the service will set the loginname, password, domain
 * by calling the appropriate setters, prior to calling login/logout methods.
 * In addition, the other-config Map will contain the request, response,
 * as well as "remember" parameters. After login, the AuthenticationManager
 * will set the JAAS login-context in the user-env., as well as clearing it
 * out after logout.
 *
 * @version        $Revision: 1.3 $        $Date: 2003/12/10 22:58:48 $
 * @author Shash Chatterjee
 * Created on Jun 8, 2003
 */
public interface AuthenticationManager
{
	/**
	 * The Avalon role for the authentication service
	 */
	public static String ROLE = AuthenticationManager.class.getName ();

	/**
	 * Set the user's login-name prior to login/logout
	 * @param name The login name
	 */
	public void setUsername (String name);

	/**
	 * Set the unencoded, text password prior to login/logout
	 * @param password The supplied passowrd
	 */
	public void setPassword (String password);

	/**
	 * The login domain.  Each domain has a unique schema and user/group mappings
	 * @param domain The login domain
	 */
	public void setDomain (String domain);

	/**
	 * A place to hold arbitrary data that might be required by the various
	 * JAAS login modules.  Other than name, password, domain, everything else
	 * should be passed in this map.
	 * @param config Extra config items
	 */
	public void setOtherConfig (Map config);

	/**
	 * Attempt to log the user in
	 * @param ue The user environment which holds the login-context and JAAS subject
	 * @throws LoginException
	 */
	public void login (UserEnvironment ue) throws LoginException;

	/**
	 * Attempt to log the user out
	 * @param ue The user environment which holds the login-context and JAAS subject
	 * @throws LoginException
	 */
	public void logout (UserEnvironment ue) throws LoginException;

	public String getDomainDescrip (String domainName);

	public List getAllowedDomains (String loginName);
}
