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


import de.iritgo.aktera.authentication.Authenticator;
import de.iritgo.aktera.authentication.DomainPrincipal;
import de.iritgo.aktera.authentication.GroupPrincipal;
import de.iritgo.aktera.authentication.LoginPrincipal;
import de.iritgo.aktera.authentication.UidPrincipal;
import de.iritgo.aktera.authentication.UserDescripPrincipal;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Base methods for Login
 *
 * @author      Michael Nash
 * @version     $Id: LoginHelper.java,v 1.2 2006/06/08 15:07:40 haardt Exp $
 */
public class LoginHelper extends AbstractKeelServiceable
{
	/**
	 * Processes the login request.
	 */
	protected Subject attemptLogin(String domain, String loginName, String providedPassword, String passwordSeq,
					Logger log, Configuration config, ServiceManager myManager)
		throws LoginException, FailedLoginException
	{
		Subject subject = null;

		try
		{
			if (log.isDebugEnabled())
			{
				log.debug("Attempting login in LoginHelper (persistent)  for user " + loginName + " in domain "
								+ domain);
			}

			try
			{
				service(myManager);
			}
			catch (ServiceException se)
			{
				log.error("Service Exception", se);
				throw new LoginException("Service exception setting ServiceManager:" + se.getMessage());
			}

			SuperString.assertNotBlank(domain, "Must specify domain");
			SuperString.assertNotBlank(loginName, "Must specify login name");

			Persistent myUser = null;

			AuthorizationManager bypassAm = null;

			try
			{
				String bypassAmName = config.getChild("bypass-am").getValue("*");

				bypassAm = (AuthorizationManager) getService(AuthorizationManager.ROLE, bypassAmName);
			}
			catch (ServiceException e1)
			{
				log.error("Error trying to get bypass auth manager ", e1);
				throw new LoginException("Error trying to get bypass auth manager " + e1.getMessage());
			}

			boolean authenticated = false;

			PersistentFactory pf = null;

			try
			{
				PersistentFactory pf1 = (PersistentFactory) getService(PersistentFactory.ROLE, domain);

				pf = (PersistentFactory) getService(PersistentFactory.ROLE, pf1.getSecurity());
				myUser = pf.create("keel.user");
				myUser.setBypassAuthorizationManager(bypassAm);
				myUser.setField("name", loginName);

				if (! myUser.find())
				{
					log.debug("Bad login name '" + loginName + "' - no such user");
					throw new FailedLoginException("Bad login name");
				}

				/* If the user persistent supports an account status, check it */
				if (myUser.getMetaData().getFieldNames().contains("status"))
				{
					if (! myUser.getFieldString("status").equals("A"))
					{
						log.debug("Account for '" + loginName + "' inactive");
						throw new FailedLoginException("Account not active");
					}
				}
			}
			catch (ServiceException e)
			{
				log.error("ServiceException during login", e);
				throw new LoginException(e.toString());
			}
			catch (PersistenceException e)
			{
				log.error("PersistenceException during login", e);
				throw new LoginException(e.toString());
			}

			try
			{
				List<Authenticator> authenticators = (List<Authenticator>) SpringTools.getBean(Authenticator.LIST_ID);

				for (Authenticator authenticator : authenticators)
				{
					if (authenticator.authenticate(loginName, providedPassword))
					{
						authenticated = true;

						break;
					}
				}

				if (! authenticated)
				{
					log.debug("Invalid user name or password for user '" + loginName + "'");
					throw new FailedLoginException("Invalid user name or password for user '" + loginName + "'");
				}

				log.debug("Login successful - setting subject");
				subject = new Subject();

				Set s = subject.getPrincipals();

				s.clear();

				Principal p = new LoginPrincipal(myUser.getFieldString("name"));

				s.add(p);
				p = new UserDescripPrincipal(myUser.getFieldString("name"));
				s.add(p);
				p = new UidPrincipal(myUser.getFieldString("uid"));
				s.add(p);
				p = new DomainPrincipal(domain);
				s.add(p);

				Persistent groupmembers = pf.create("keel.groupmembers");

				groupmembers.setBypassAuthorizationManager(bypassAm);
				groupmembers.setField("uid", new Integer(myUser.getFieldString("uid")));

				List memberOf = groupmembers.query();
				Persistent oneMember = null;

				for (Iterator gi = memberOf.iterator(); gi.hasNext();)
				{
					oneMember = (Persistent) gi.next();
					p = new GroupPrincipal(oneMember.getFieldString("groupname"));
					s.add(p);
				}
			}
			catch (PersistenceException e)
			{
				log.error("PersistenceException during login", e);
				throw new LoginException(e.toString());
			}
		}
		finally
		{
			releaseServices();
		}

		return subject;
	}
}
