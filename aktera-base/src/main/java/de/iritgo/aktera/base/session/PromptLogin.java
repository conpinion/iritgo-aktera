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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.HashMap;
import java.util.TreeMap;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.session.prompt-login"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.session.prompt-login" id="aktera.session.prompt-login" logger="aktera"
 * @model.attribute name="forward" value="aktera.session.login"
 */
public class PromptLogin extends LoginBase
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();
		String fixedDomain = null;
		String rememberConfig = configuration.getAttribute("remember", "prompt");
		String defaultLoginName = null;
		String defaultPassword = null;
		String defaultDomain = null;
		HashMap cookies = (HashMap) req.getAttribute("cookies");

		if (cookies != null)
		{
			defaultLoginName = SuperString.notNull((String) cookies.get(getLoginCookieName(configuration)));
			defaultPassword = SuperString.notNull((String) cookies.get(getPasswordCookieName(configuration)));
			defaultDomain = SuperString.notNull((String) cookies.get(getDomainCookieName(configuration)));
		}

		String[] cookieSeq = getCryptSeq(configuration, "cookie");

		if (defaultLoginName != null && ! "".equals(defaultLoginName))
		{
			defaultLoginName = decodeWithSeq(cookieSeq, defaultLoginName, req);
		}

		if (defaultPassword != null && ! "".equals(defaultPassword))
		{
			defaultPassword = decodeWithSeq(cookieSeq, defaultPassword, req);
		}

		if (defaultDomain != null && ! "".equals(defaultDomain))
		{
			defaultDomain = decodeWithSeq(cookieSeq, defaultDomain, req);
		}

		String domain = SuperString.notNull((String) req.getParameter("domain"));

		if (domain != null && ! "".equals(domain))
		{
			res.addOutput("currentDomain", domain);
			fixedDomain = domain;
		}
		else
		{
			try
			{
				int domainCount = 0;
				TreeMap valids = new TreeMap();
				Configuration domainConfig = getConfiguration().getChild("domains");

				Configuration[] dChildren = domainConfig.getChildren();
				Configuration oneDomain = null;

				for (int j = 0; j < dChildren.length; j++)
				{
					oneDomain = dChildren[j];
					domainCount++;
					valids.put(oneDomain.getAttribute("name"), oneDomain.getAttribute("descrip"));
					fixedDomain = oneDomain.getAttribute("name");
				}

				if (domainCount > 1)
				{
					Input domainInput = res.createInput("domain");

					domainInput.setLabel("Domain");
					domainInput.setDefaultValue(defaultDomain);
					domainInput.setValidValues(valids);
					res.add(domainInput);
					fixedDomain = defaultDomain;
				}
			}
			catch (ConfigurationException x)
			{
				throw new ModelException(x);
			}
		}

		Input loginName = res.createInput("loginName");

		loginName.setDefaultValue(defaultLoginName);
		loginName.setLabel("Login");
		res.add(loginName);

		Input password = res.createInput("password");

		password.setDefaultValue(defaultPassword);
		password.setLabel("Password");

		password.setAttribute("type", "password");
		res.add(password);

		String loginModel = Constants.LOGIN;

		if (req.getParameter("loginmodel") != null)
		{
			loginModel = (String) req.getParameter("loginmodel");
		}

		Command login = res.createCommand(loginModel);

		login.setName("login");
		login.setLabel("$login");

		if (fixedDomain != null)
		{
			login.setParameter("domain", fixedDomain);
		}

		if (rememberConfig.equals("prompt") || rememberConfig.equals("last"))
		{
			Input remember = res.createInput("remember");

			remember.setLabel("Remember Login");
			remember.setAttribute("checkbox", "Y");

			String rememberChecked = configuration.getAttribute("remember-checked", "false");

			if (rememberConfig.equals("last"))
			{
				remember.setDefaultValue((defaultLoginName != null && ! defaultLoginName.equals("")) ? "on" : "off");
			}
			else if (rememberChecked.equals("true"))
			{
				remember.setDefaultValue("on");
			}

			res.add(remember);
		}
		else if (rememberConfig.equals("always"))
		{
			login.setParameter("remember", "Y");
		}
		else if (rememberConfig.equals("never"))
		{
			login.setParameter("remember", "N");
		}

		res.add(login);

		res.setDefaultsFromPrevious();

		Command logout = res.createCommand(Constants.LOGOFF);

		logout.setLabel("$logoff");
		res.add(logout);

		Command promptSendPassword = res.createCommand(Constants.PROMPT_SEND_PASSWORD);

		promptSendPassword.setName("sendPassword");
		promptSendPassword.setLabel("$sendPassword");
		res.add(promptSendPassword);

		Command promptRegister = res.createCommand(Constants.PROMPT_REGISTRATION);

		promptRegister.setLabel("$register");
		res.add(promptRegister);

		res.setDefaultsFromPrevious();

		return res;
	}
}
