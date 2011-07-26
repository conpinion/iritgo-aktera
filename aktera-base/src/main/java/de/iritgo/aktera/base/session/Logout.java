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


import de.iritgo.aktera.authentication.AuthenticationManager;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.configuration.preferences.PreferencesManager;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.spring.SpringTools;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import java.util.HashMap;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.session.logout"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.session.logout" id="aktera.session.logout" logger="aktera"
 */
public class Logout extends LoginBase
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

		Context c = req.getContext ();

		if (c == null)
		{
			throw new ModelException ("No context established - request should have established context");
		}

		UserEnvironment userEnv = null;

		try
		{
			userEnv = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null)
			{
				int uid = userEnv.getUid ();

				if (uid != UserEnvironment.ANONYMOUS_UID)
				{
					AuthenticationManager authMgr = (AuthenticationManager) req.getService (AuthenticationManager.ROLE,
									"*");

					authMgr.setUsername (userEnv.getLoginName ());
					authMgr.setPassword ("");
					authMgr.setDomain (userEnv.getDomain ());

					HashMap map = new HashMap ();

					map.put ("request", req);
					map.put ("response", res);
					map.put ("remember", new Boolean ("off"));
					map.put ("configuration", getConfiguration ());
					authMgr.setOtherConfig (map);

					//Clear cookies if the clear-cookies config. attribute is true
					boolean clearCookies = configuration.getAttributeAsBoolean ("clear-cookies", true);

					if (clearCookies)
					{
						HashMap cookies = new HashMap ();

						cookies.put (getLoginCookieName (configuration), "");
						cookies.put (getPasswordCookieName (configuration), "");
						cookies.put (getDomainCookieName (configuration), "");
						res.setAttribute ("cookies", cookies);
					}

					try
					{
						authMgr.logout (userEnv);
					}
					catch (Exception ee)
					{
						log.error ("Logout Error", ee);
						throw new ModelException (ee);
					}

					userEnv.clearAttributes ();
					userEnv.reset ();

					//					DefaultContext dc = (DefaultContext) req.getContext ();
					//					dc.put (UserEnvironment.CONTEXT_KEY, null);
					PreferencesManager preferencesManager = (PreferencesManager) SpringTools
									.getBean (PreferencesManager.ID);

					preferencesManager.clearCache (uid);
				}
			}
		}
		catch (ContextException e)
		{
			throw new ModelException ("Unable to retrieve user env. to context", e);
		}
		catch (AuthorizationException e)
		{
			throw new ModelException ("Authroization error", e);
		}

		res.addOutput ("loggedOff", "Logged Off");

		return res;
	}
}
