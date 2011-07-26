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


import de.iritgo.aktera.authentication.AuthenticationManager;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.core.container.ServiceConfig;
import de.iritgo.aktera.usergroupmgr.Group;
import de.iritgo.aktera.usergroupmgr.GroupManager;
import de.iritgo.aktera.usergroupmgr.User;
import de.iritgo.aktera.usergroupmgr.UserManager;
import de.iritgo.aktera.usergroupmgr.UserMgrException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author schatterjee
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractAuthenticationManager extends AbstractKeelServiceable implements AuthenticationManager,
				Configurable, LogEnabled
{
	protected Logger log = null;

	protected ServiceConfig svcConfig = null;

	protected String username = UserEnvironment.ANONYMOUS_LOGINNAME;

	protected String password = "";

	protected String domain = UserEnvironment.ANONYMOUS_DOMAIN;

	protected Map otherConfig = null;

	protected Configuration configuration = null;

	protected abstract Subject addKeelCredentials (Subject s);

	protected abstract String getDefaultLoginModuleName ();

	/**
	 * @see de.iritgo.aktera.authentication.AuthenticationManager#setUsername(java.lang.String)
	 */
	public void setUsername (String name)
	{
		this.username = name;
	}

	/**
	 * @see de.iritgo.aktera.authentication.AuthenticationManager#setPassword(java.lang.String)
	 */
	public void setPassword (String password)
	{
		this.password = password;
	}

	/**
	 * @see de.iritgo.aktera.authentication.AuthenticationManager#setDomain(java.lang.String)
	 */
	public void setDomain (String domain)
	{
		this.domain = domain;
	}

	/**
	 * @see de.iritgo.aktera.authentication.AuthenticationManager#setOtherConfig(java.util.Map)
	 */
	public void setOtherConfig (Map otherConfig)
	{
		this.otherConfig = otherConfig;
	}

	protected String getUsername ()
	{
		return username;
	}

	protected String getPassword ()
	{
		return password;
	}

	protected String getDomain ()
	{
		return domain;
	}

	protected Map getOtherConfig ()
	{
		return otherConfig;
	}

	protected Object getConfigItem (Object key)
	{
		if (otherConfig == null)
		{
			return null;
		}

		return otherConfig.get (key);
	}

	protected LoginContext getLoginContext (String name) throws LoginException
	{
		Boolean rem = (Boolean) getConfigItem ("remember");

		if (rem == null)
		{
			rem = new Boolean (false);
		}

		LoginCallbackHandler cbh = new LoginCallbackHandler (getUsername (), getPassword ().trim (), getDomain (), rem
						.booleanValue (), configuration, log, getServiceManager ());
		LoginContext lc = new LoginContext (name, cbh);

		return lc;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure (Configuration configuration) throws ConfigurationException
	{
		this.configuration = configuration;
		svcConfig = new ServiceConfig (configuration);
	}

	/**
	 * @see de.iritgo.aktera.authentication.AuthenticationManager#login(de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public void login (UserEnvironment ue) throws LoginException
	{
		log.debug ("Logging in");

		LoginContext lc = getLoginContext (getLoginModuleName ());

		lc.login ();

		addKeelCredentials (lc.getSubject ());

		if (log.isDebugEnabled ())
		{
			Iterator i = lc.getSubject ().getPrincipals ().iterator ();

			while (i.hasNext ())
			{
				Principal p = (Principal) i.next ();

				log.debug ("Principal - " + p.toString ());
			}
		}

		try
		{
			ue.setLoginContext (lc);
		}
		catch (AuthorizationException e)
		{
			log.debug ("Error setting subject", e);
			throw new LoginException ("Error setting subject in user env. - " + e.toString ());
		}
	}

	/**
	 * @return
	 */
	private String getLoginModuleName ()
	{
		String moduleName = configuration.getChild ("login-module").getValue (getDefaultLoginModuleName ());

		return moduleName;
	}

	/**
	 * @see de.iritgo.aktera.authentication.AuthenticationManager#logout(de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public void logout (UserEnvironment ue) throws LoginException
	{
		try
		{
			LoginContext lc = ue.getLoginContext ();

			lc.logout ();
			ue.reset ();
		}
		catch (AuthorizationException e)
		{
			throw new LoginException ("Error setting subject in user env. - " + e.toString ());
		}
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging (Logger logger)
	{
		this.log = logger;
	}

	public String getDomainDescrip (String domainName)
	{
		Configuration[] domains = configuration.getChildren ("domain");

		for (int i = 0; i < domains.length; i++)
		{
			Configuration oneDomain = domains[i];

			if (oneDomain.getAttribute ("name", "").equals (domainName))
			{
				return oneDomain.getAttribute ("descrip", "");
			}
		}

		return "";
	}

	public List getAllowedDomains (String loginName)
	{
		UserManager um = null;
		GroupManager gm = null;

		ArrayList returnList = new ArrayList ();

		try
		{
			um = (UserManager) getService (UserManager.ROLE, svcConfig.getHint (UserManager.ROLE));
			gm = (GroupManager) getService (GroupManager.ROLE, svcConfig.getHint (GroupManager.ROLE));

			User u = um.find (User.Property.NAME, loginName);
			Configuration[] domains = configuration.getChildren ("domain");

			for (int i = 0; i < domains.length; i++)
			{
				Configuration oneDomain = domains[i];
				Configuration[] groups = oneDomain.getChildren ("group");
				String oneGroupName = null;

				for (int j = 0; j < groups.length; j++)
				{
					oneGroupName = groups[j].getValue ();

					Group[] usersGroups = gm.listGroups (u);

					for (int k = 0; k < usersGroups.length; k++)
					{
						if (usersGroups[k].get (Group.Property.NAME).equals (oneGroupName))
						{
							returnList.add (oneDomain.getAttribute ("name"));
						}
					}
				}

				if (groups.length == 0)
				{
					/* No groups at all means that anyone can get to this domain */
					returnList.add (oneDomain.getAttribute ("name"));
				}
			}
		}
		catch (ConfigurationException ce)
		{
			log.error ("Unable to determine allowed domains", ce);
			throw new IllegalArgumentException (ce.getMessage ());
		}
		catch (ServiceException e)
		{
			throw new RuntimeException (e);
		}
		catch (UserMgrException e)
		{
			throw new RuntimeException (e);
		}

		return returnList;
	}
}
