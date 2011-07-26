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

package de.iritgo.aktera.base.session;


import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.security.auth.login.LoginException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import de.iritgo.aktera.authentication.AuthenticationManager;
import de.iritgo.aktera.authentication.DefaultUserEnvironment;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.tools.UserTools;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.session.login"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.session.login" id="aktera.session.login" logger="aktera"
 * @model.attribute name="forward" value="aktera.session.login-success"
 * @model.parameter name="dmn" required="true"
 * @model.parameter name="LoginName" required="true"
 */
public class Login extends LoginBase
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		String originalURL = null;
		try
		{
			originalURL = (String) req.getContext ().get ("originalURL");
		}
		catch (ContextException x)
		{
		}
		if (originalURL != null)
		{
			res.addOutput ("originalURL", originalURL.toString ());
		}

		String domain = (String) req.getParameter ("domain");

		String loginName = (String) req.getParameter ("loginName");
		if (loginName.equals (""))
		{
			res.addError ("GLOBAL_loginError", "$youNeedToProvideALoginName");
			return res.createCommand (Constants.PROMPT_LOGIN).execute (req, res);
		}

		PermissionManager permissionManager = (PermissionManager) SpringTools.getBean (PermissionManager.ID);
		if (! permissionManager.hasPermission (loginName, "de.iritgo.aktera.web.login"))
		{
			res.addError ("GLOBAL_loginError", "$youAreNotAllowedToLogin");
			return res.createCommand (Constants.PROMPT_LOGIN).execute (req, res);
		}

		try
		{
			String providedPassword = (String) req.getParameter ("password");
			if (providedPassword.equals (""))
			{
				res.addError ("GLOBAL_loginError", "$youNeedToProvideAPassword");
				return res.createCommand (Constants.PROMPT_LOGIN).execute (req, res);
			}
			boolean remember = false;
			if (req.getParameters ().containsKey ("remember"))
			{
				remember = true;
			}

			AuthenticationManager authMgr = (AuthenticationManager) req.getService (AuthenticationManager.ROLE, domain);
			authMgr.setUsername (loginName);
			authMgr.setPassword (providedPassword);
			authMgr.setDomain (domain);

			HashMap map = new HashMap ();
			map.put ("request", req);
			map.put ("response", res);
			map.put ("remember", new Boolean (remember));
			map.put ("configuration", getConfiguration ());
			authMgr.setOtherConfig (map);

			UserEnvironment ue = null;
			Context c = req.getContext ();

			try
			{
				ue = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);
			}
			catch (ContextException e)
			{
				if (c instanceof DefaultContext)
				{
					ue = new DefaultUserEnvironment ();
					((DefaultContext) c).put (UserEnvironment.CONTEXT_KEY, ue);
				}
				else
				{
					throw new ModelException ("Unable to write user env. to context, was '" + c.getClass ().getName ());
				}
			}

			authMgr.login (ue);

			try
			{
				HashMap cookies = new HashMap ();

				if (remember)
				{
					String[] cookieSeq = getCryptSeq (configuration, "cookie");
					cookies.put (getLoginCookieName (configuration), encodeWithSeq (cookieSeq, loginName, req));
					cookies.put (getPasswordCookieName (configuration),
									encodeWithSeq (cookieSeq, providedPassword, req));
					cookies.put (getDomainCookieName (configuration), encodeWithSeq (cookieSeq, domain, req));
					res.addOutput ("remembered", "$loginRemembered");
				}
				else
				{
					cookies.put (getLoginCookieName (configuration), "");
					cookies.put (getPasswordCookieName (configuration), "");
					cookies.put (getDomainCookieName (configuration), "");
					res.addOutput ("remembered", "$loginNotRemembered");
				}

				res.setAttribute ("cookies", cookies);
			}
			catch (ModelException e)
			{
				throw new LoginException ("Error setting cookies - " + e.getMessage ());
			}

			if (log.isDebugEnabled ())
			{
				log.debug ("Logged in authenticated user: " + loginName + " domain: " + domain);
				log.debug ("\tPrincipals:");

				Iterator i = ue.getSubject ().getPrincipals ().iterator ();
				while (i.hasNext ())
				{
					Principal p = (Principal) i.next ();
					log.debug (p.toString ());
				}
			}
		}
		catch (LoginException e)
		{
			if (e.getMessage ().matches (".*([Ll]ogin|[Aa]ccount).*"))
			{
				res.addError ("GLOBAL_loginError", "$badLoginName");
			}
			else if (e.getMessage ().matches (".*[Pp]assword.*"))
			{
				res.addError ("GLOBAL_loginError", "$badPassword");
			}
			else
			{
				res.addError ("GLOBAL_loginError", "$loginError");
			}

			log.warn ("Login error for user '" + loginName + "': " + e.getMessage ());

			return res.createCommand (Constants.PROMPT_LOGIN).execute (req, res);
		}
		catch (Exception dbe)
		{
			res.addError ("GLOBAL_loginError", dbe);
			log.error ("loginError", dbe);

			return res.createCommand (Constants.PROMPT_LOGIN).execute (req, res);
		}

		try
		{
			log.debug (loginName + " logged in successfully");

			res.addCommand (Constants.LOGOFF, "Log Off");
		}
		catch (Exception x)
		{
			log.error ("Login Error", x);
			throw new ModelException (x);
		}

		if (res.getErrors ().size () == 0)
		{
			Properties props = new Properties ();

			props.put ("command", "login");
			ModelTools.callModel (req, "aktera.session.session-manager", props);

			Configuration[] loginHandlerConfigs = getConfiguration ().getChildren ("handler");

			for (int i = 0; i < loginHandlerConfigs.length; ++i)
			{
				String handlerClassName = loginHandlerConfigs[i].getAttribute ("class", null);

				try
				{
					Class handlerClass = Class.forName (handlerClassName);

					if (handlerClass != null)
					{
						handlerClass.getMethod ("login", new Class[]
						{
										ModelRequest.class, String.class
						}).invoke (null, new Object[]
						{
										req, loginName
						});
					}
				}
				catch (Exception x)
				{
					log.error ("Login: Unable to retrieve login handler class " + handlerClassName + ": " + x);
				}
			}

			UserTools.removeContextObject (req, "aktera.currentMenuItem");
		}

		return res;
	}
}
