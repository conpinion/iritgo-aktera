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


import de.iritgo.aktera.authentication.ConfigurationCallback;
import de.iritgo.aktera.authentication.LoggerCallback;
import de.iritgo.aktera.authentication.LoginCallbackHandler;
import de.iritgo.aktera.authentication.ServiceManagerCallback;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule; /* Java imports */
import java.util.Map;


/**
 * <p>
 * KeelLoginModule is a LoginModule that authenticates a given
 * username/password credential against a persistence datasource.
 *
 * @see javax.security.auth.spi.LoginModule
 * @author Paul Feuer and John Musser
 * @author Santanu Dutt - Converted to work with Keel
 * @author Michael Nash - removed dependance on Model API
 * @version 1.1
 */
public class KeelLoginModule implements LoginModule
{
	// initial state
	CallbackHandler callbackHandler;

	Subject subject;

	Map sharedState;

	Map options;

	private Logger log = null;

	// the authentication status
	boolean success = false;

	// configurable options
	boolean debug = true;

	String passwordSeq = "base64.hash,base64.encrypt";

	/**
	 * <p>
	 * Creates a login module that can authenticate against a persistent
	 * object.
	 */
	public KeelLoginModule()
	{
	}

	/**
	 * Initialize this <code>LoginModule</code>.
	 *
	 * <p>
	 *
	 * @param subject
	 *            the <code>Subject</code> to be authenticated.
	 *            <p>
	 *
	 * @param callbackHandler
	 *            a <code>CallbackHandler</code> for communicating with the
	 *            end user (prompting for usernames and passwords, for
	 *            example).
	 *            <p>
	 *
	 * @param sharedState
	 *            shared <code>LoginModule</code> state.
	 *            <p>
	 *
	 * @param options
	 *            options specified in the login <code>Configuration</code>
	 *            for this particular <code>LoginModule</code>.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
	{
		// save the initial state
		this.callbackHandler = callbackHandler;
		this.subject = subject;
		this.sharedState = sharedState;
		this.options = options;

		// initialize any configured options
		if (options.containsKey("debug"))
		{
			debug = "true".equalsIgnoreCase((String) options.get("debug"));
		}

		debug = false;

		if (debug)
		{
			System.out.println("\t\t[KeelLoginModule] initialize");
		}

		if (options.containsKey("password-seq"))
		{
			passwordSeq = (String) options.get("password-seq");
		}

		if (debug)
		{
			System.out.println("\t\t[KeelLoginModule] password-seq: " + passwordSeq);
		}
	}

	/**
	 * <p>
	 * Verify the user against the relevant persistent objects.
	 *
	 * @return true always, since this <code>LoginModule</code> should not be
	 *         ignored.
	 *
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 *                <p>
	 *
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform
	 *                the authentication.
	 */
	public boolean login() throws LoginException
	{
		if (debug)
		{
			System.out.println("\t\t[KeelLoginModule] login");
		}

		if (callbackHandler == null)
		{
			throw new LoginException("Error: no CallbackHandler available "
							+ "to garner authentication information from the user");
		}

		try
		{
			// Setup default callback handlers.
			Callback[] callbacks = new Callback[]
			{
							new NameCallback("Username: "), /* 0 */
							new PasswordCallback("Password: ", false), /* 1 */
							new ConfirmationCallback(ConfirmationCallback.INFORMATION,
											ConfirmationCallback.YES_NO_OPTION, ConfirmationCallback.NO),

							/* 2 */
							new TextInputCallback("Domain: "), /* 3 */
							new ConfigurationCallback(), /* 4 */
							new LoggerCallback(), /* 5 */
							new ServiceManagerCallback()
			}; /* 6 */

			callbackHandler.handle(callbacks);

			String loginName = ((NameCallback) callbacks[0]).getName();
			String password = new String(((PasswordCallback) callbacks[1]).getPassword());

			((PasswordCallback) callbacks[1]).clearPassword();

			String domain = ((TextInputCallback) callbacks[3]).getText();
			Configuration config = ((ConfigurationCallback) callbacks[4]).getConfiguration();

			log = ((LoggerCallback) callbacks[5]).getLog();

			ServiceManager sm = ((ServiceManagerCallback) callbacks[6]).getServiceManager();

			log.debug("Log assigned in KeelLoginModule");

			LoginHelper lb = new LoginHelper();

			Subject tmp = lb.attemptLogin(domain, loginName, password, passwordSeq, log, config, sm);

			//Add the uid to the sharedState Map to be used by subsequent
			// LoginModule implementations
			this.sharedState.put("subject", tmp);

			success = true;

			for (int i = 0; i < callbacks.length; i++)
			{
				callbacks[i] = null;
			}

			return (true);
		}
		catch (LoginException e)
		{
			throw e;
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
			throw new LoginException(ex.toString());
		}
	}

	/**
	 * Abstract method to commit the authentication process.
	 *
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * succeeded (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules succeeded).
	 *
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method),
	 * then this method associates a <code>KeelPrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If
	 * this LoginModule's own authentication attempted failed, then this method
	 * removes any state that was originally saved.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the commit fails
	 *
	 * @return true if this LoginModule's own login and commit attempts
	 *         succeeded, or false otherwise.
	 */
	public boolean commit() throws LoginException
	{
		boolean returnValue = false;

		if (debug)
		{
			System.out.println("\t\t[KeelLoginModule] commit");
		}

		if (success)
		{
			if (subject.isReadOnly())
			{
				throw new LoginException("Subject is Readonly");
			}

			if (callbackHandler instanceof LoginCallbackHandler)
			{
				((LoginCallbackHandler) callbackHandler).clearPassword();
			}

			try
			{
				Subject tmp = (Subject) sharedState.get("subject");

				subject.getPrincipals().addAll(tmp.getPrincipals());
				subject.getPublicCredentials().addAll(tmp.getPublicCredentials());
				subject.getPrivateCredentials().addAll(tmp.getPrivateCredentials());
			}
			catch (Exception ex)
			{
				throw new LoginException(ex.getMessage());
			}

			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * failed. (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules did not succeed).
	 *
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state
	 * that was originally saved.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the abort fails.
	 *
	 * @return false if this LoginModule's own login and/or commit attempts
	 *         failed, and true otherwise.
	 */
	public boolean abort() throws javax.security.auth.login.LoginException
	{
		if (debug)
		{
			System.out.println("\t\t[KeelLoginModule] abort");
		}

		// Clean out state
		success = false;

		if (callbackHandler instanceof LoginCallbackHandler)
		{
			((LoginCallbackHandler) callbackHandler).clearPassword();
		}

		logout();

		return (true);
	}

	/**
	 * Logout a user.
	 *
	 * <p>
	 * This method removes the Principals that were added by the <code>commit</code>
	 * method.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the logout fails.
	 *
	 * @return true in all cases since this <code>LoginModule</code> should
	 *         not be ignored.
	 */
	public boolean logout() throws javax.security.auth.login.LoginException
	{
		if (debug)
		{
			System.out.println("\t\t[KeelLoginModule] logout");
		}

		// Clean out state
		success = false;

		if (callbackHandler instanceof LoginCallbackHandler)
		{
			((LoginCallbackHandler) callbackHandler).clearPassword();
		}

		// remove the principals the login module added
		if (subject != null)
		{
			subject.getPrincipals().clear();
			subject.getPrivateCredentials().clear();
			subject.getPublicCredentials().clear();
		}

		return (true);
	}
}
