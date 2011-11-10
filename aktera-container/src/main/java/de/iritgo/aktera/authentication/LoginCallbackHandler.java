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


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException; /* Java imports */
import java.io.IOException;


/**
 * <p>
 * LoginCallbackHandler has constructor that takes
 * a username, password, domain and boolean for login remember so its handle() method does
 * not have to prompt the user for input.
 * Useful for server-side applications.
 *
 * @author Paul Feuer and John Musser
 * @author  Santanu Dutt - Converted to work with Keel
 * @version 1.0
 */
public class LoginCallbackHandler implements CallbackHandler
{
	private String username;

	char[] password;

	private String domain;

	private boolean remember;

	private Configuration conf;

	private Logger log;

	private ServiceManager serviceManager;

	/**
	 * <p>Default constructor
	 */
	public LoginCallbackHandler()
	{
	}

	/**
	 * <p>Creates a callback handler with the give username
	 * and password.
	 */
	public LoginCallbackHandler(String user, String pass, String domain, boolean remember, Configuration newConf,
					Logger newLog, ServiceManager newManager)
	{
		log = newLog;
		log.debug("LoginCallbackHandler initialized");
		this.username = user;
		this.password = pass.toCharArray();
		this.domain = domain;
		this.remember = remember;
		this.serviceManager = newManager;
		conf = newConf;
	}

	/**
	 * Handles the specified set of Callbacks. Uses the
	 * username and password that were supplied to our
	 * constructor to popluate the Callbacks.
	 *
	 * This class supports NameCallback, PasswordCallback, ConfirmationCallback
	 * and TextInputCallback.
	 *
	 * @param   callbacks the callbacks to handle
	 * @throws  IOException if an input or output error occurs.
	 * @throws  UnsupportedCallbackException if the callback is not an
	 * instance of NameCallback, PasswordCallback, ConfirmationCallback
	 * or TextInputCallback.
	 */
	public void handle(Callback[] callbacks) throws java.io.IOException, UnsupportedCallbackException
	{
		for (int i = 0; i < callbacks.length; i++)
		{
			if (callbacks[i] instanceof NameCallback)
			{
				((NameCallback) callbacks[i]).setName(username);
			}
			else if (callbacks[i] instanceof PasswordCallback)
			{
				((PasswordCallback) callbacks[i]).setPassword(password);
			}
			else if (callbacks[i] instanceof ConfirmationCallback)
			{
				if (this.remember)
				{
					((ConfirmationCallback) callbacks[i]).setSelectedIndex(ConfirmationCallback.YES);
				}
				else
				{
					((ConfirmationCallback) callbacks[i]).setSelectedIndex(ConfirmationCallback.YES);
				}
			}
			else if (callbacks[i] instanceof TextInputCallback)
			{
				((TextInputCallback) callbacks[i]).setText(domain);
			}
			else if (callbacks[i] instanceof ConfigurationCallback)
			{
				((ConfigurationCallback) callbacks[i]).setConfiguration(conf);
			}
			else if (callbacks[i] instanceof LoggerCallback)
			{
				((LoggerCallback) callbacks[i]).setLog(log);
			}
			else if (callbacks[i] instanceof ServiceManagerCallback)
			{
				((ServiceManagerCallback) callbacks[i]).setServiceManager(serviceManager);
			}
			else
			{
				throw new UnsupportedCallbackException(callbacks[i], "Callback class '"
								+ callbacks[i].getClass().getName() + "' not supported");
			}
		}
	}

	/**
	 * Clears out password state.
	 */
	public void clearPassword()
	{
		if (password != null)
		{
			for (int i = 0; i < password.length; i++)
			{
				password[i] = ' ';
			}

			password = null;
		}
	}
}
