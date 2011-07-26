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

package de.iritgo.aktera.authorization;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.InstanceSecurable;
import de.iritgo.aktera.authorization.InvokationSecurable;
import de.iritgo.aktera.authorization.Operation;
import de.iritgo.aktera.authorization.defaultauth.DefaultOperation;
import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.core.container.KeelServiceable;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import org.apache.avalon.fortress.impl.factory.ProxyObjectFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.thread.ThreadSafe;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * This class is an implementation of the AuthorizationManager to authorize
 * services based on database security tables. This class is a keel service
 * based on the AuthorizationManager role. Each service which is Securable
 * obtains this service from the KeelContainer in the setAuthorizationManager()
 * lifecycle method..
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.authorization.AuthorizationManager
 * @x-avalon.info name=authmanager
 * @x-avalon.lifestyle type=singleton
 *
 * @author Santanu Dutt
 *
 * TODO: Use cache service to cache permissions, rather then working them out
 * from the database. Have some means (Restartable?) to clear cache at runtime,
 * forcing new info to be re-read. Maybe this needs to be an AuthorizationManager
 * API change, to have a "clearCache()" method
 *
 */
public class KeelAuthorizationManager extends AbstractKeelServiceable implements AuthorizationManager, LogEnabled,
				ThreadSafe, Configurable
{
	private Logger log = null;

	private boolean rootBypass = false;

	private AuthorizationManager bypassAm = null;

	public void enableLogging (Logger newLog)
	{
		log = newLog;
	}

	/**
	 * Is the specified operation allowed for the given
	 * principal? First check all the groups, then check
	 * for specific user permission if none of the groups this
	 * user belongs to are permitted this operation.
	 */
	public boolean allowed (Operation o, UserEnvironment ue) throws AuthorizationException
	{
		if (rootBypass)
		{
			if (ue.getGroups ().contains ("root"))
			{
				return true;
			}
		}

		if (log.isDebugEnabled ())
		{
			log.debug ("Checking access for operation " + o.toString () + " for user " + ue.toString () + " component "
							+ o.getService ().toString ());
		}

		if (o.getService () instanceof InvokationSecurable)
		{
			return checkInvokationSecurable (o, ue);
		}
		else if (o.getService () instanceof InstanceSecurable)
		{
			return checkInstanceSecurable (o, ue);
		}
		else
		{
			return checkSecurable (o, ue);
		}
	}

	/**
	 * Check permissions for a "regular" Securable component (e.g. not one
	 * which is Invokation or Instance securable)
	 *
	 * @param o
	 * @param ue
	 * @return
	 * @throws AuthorizationException
	 */
	private boolean checkSecurable (Operation o, UserEnvironment ue) throws AuthorizationException
	{
		PersistentFactory pf = null;
		String operationCode = o.getOperationCode ();

		if ((operationCode == null) || (operationCode.trim ().equals ("")))
		{
			operationCode = "*";
		}

		if (ue.getGroups ().size () == 0)
		{
			throw new AuthorizationException ("User '" + ue.getLoginName () + "' is not a member of any groups");
		}

		try
		{
			pf = getPersistentFactory ();

			Persistent serviceSecurity = pf.create ("component-security.componentsecurity");

			serviceSecurity.setBypassAuthorizationManager (bypassAm);

			/* Iterate through all the groups that this principal is a member of */
			String oneGroup = null;

			for (Iterator j = ue.getGroups ().iterator (); j.hasNext ();)
			{
				oneGroup = (String) j.next ();

				serviceSecurity.clear ();
				serviceSecurity.setField ("component", getComponentName (o.getService ()));
				serviceSecurity.setField ("groupname", oneGroup);

				if (log.isDebugEnabled ())
				{
					log.debug ("Looking for " + serviceSecurity.toString ());
				}

				if (serviceSecurity.find ())
				{
					log.debug ("Found componentsecurity record, checking operation " + operationCode);

					if (operationCode.equals ("*"))
					{
						return true;
					}

					if (serviceSecurity.getFieldString ("operationsallowed").indexOf (operationCode) > 0)
					{
						return true;
					}

					if (serviceSecurity.getFieldString ("operationsallowed").equals ("*"))
					{
						return true;
					}
				}
			}
		}
		catch (PersistenceException pe)
		{
			log.error ("Database error checking authorization", pe);
			throw new AuthorizationException (pe);
		}
		catch (AuthorizationException ae)
		{
			log.error ("Authorization error while checking authorization", ae);
			throw new AuthorizationException (ae);
		}
		finally
		{
			releaseService (pf);

			try
			{
				Proxy proxy = (Proxy) pf;

				proxy.getInvocationHandler (proxy).invoke (proxy, KeelServiceable.class.getMethod ("releaseServices"),
								new Object[]
								{});
			}
			catch (Throwable x)
			{
			}
		}

		if (log.isDebugEnabled ())
		{
			log.debug ("No authorization found of any type for " + o.toString () + ", denied access to "
							+ ue.toString ());
		}

		return false;
	}

	/**
	 * If we don't specify an operation, determine if the
	 * user is allowed to do *anything* with this
	 * component. This is used for securing the entire
	 * component - e.g. a Model
	 * First it checks whether the Groups of which the user is a member
	 * are allowed access. If none are, then it checks for specific
	 * user access.
	 *
	 * @return True if the component is allowed at all, false if it is denied
	 */
	public boolean allowed (Object component, UserEnvironment ue) throws AuthorizationException
	{
		Operation o = new DefaultOperation ();

		o.setOperationCode ("*");
		o.setService (component);

		return allowed (o, ue);
	}

	/**
	 * Same as allowed(Principal), but if the SecurityManager
	 * is Contextualizable, it can determine the principal(s)
	 * on it's own
	 * @return True if the access is granted, false if it is denied
	 * @throws AuthorizationException If an error occurs trying to determine if the access
	 * is granted or not.
	 */
	public boolean allowed (Object service, Context c) throws AuthorizationException
	{
		try
		{
			UserEnvironment ue = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);

			if (rootBypass)
			{
				// Group "root" is an exception: granted auth in all cases
				if (ue.getGroups ().contains ("root"))
				{
					return true;
				}
			}

			return allowed (service, ue);
		}
		catch (AuthorizationException ae)
		{
			log.error ("Authorization exception while checking authorization", ae);
		}
		catch (ContextException e)
		{
			log.error ("Context exception while checking authorization: " + e.getMessage ());
		}

		return false;
	}

	/**
	 * Obtains the PersistentFactory to be used when checking authorization
	 * @return A persistent factory initialized with a default context
	 * @throws AuthorizationException if it is not possible to access the factory
	 */
	private PersistentFactory getPersistentFactory () throws AuthorizationException
	{
		return getPersistentFactory (new DefaultContext ());
	}

	/**
	 * Check for Invokation-level authorization if all other authorization has been
	 * granted.
	 * @param o The Operation requested
	 * @param ue The UserEnvironment describing the currently logged-in user
	 * @return True if authorization is granted, or if this object is not securable
	 * at the invokation level
	 * @throws AuthorizationException If an error occurs during verification of authorization
	 */
	private boolean checkInvokationSecurable (Operation o, UserEnvironment ue) throws AuthorizationException
	{
		if (log.isDebugEnabled ())
		{
			log.debug ("Checking invokation security for operation " + o.toString () + " for user " + ue.getUid ()
							+ " to component " + o.getService ().toString ());
		}

		String operationCode = o.getOperationCode ();

		if ((operationCode == null) || (operationCode.trim ().equals ("")))
		{
			operationCode = "*";
		}

		PersistentFactory pf = null;

		/* There must be a rule */
		int totalRules = 0;

		/* And we must match all applicable rules */
		int rulesMatched = 0;

		try
		{
			pf = getPersistentFactory ();

			String oneGroup = null;

			for (Iterator j = ue.getGroups ().iterator (); j.hasNext ();)
			{
				oneGroup = (String) j.next ();

				Persistent ruleList = pf.create ("component-security.invokationsecurity");

				ruleList.setBypassAuthorizationManager (bypassAm);
				ruleList.setField ("component", getComponentName (o.getService ()));
				ruleList.setField ("instance", ((InstanceSecurable) o.getService ()).getInstanceIdentifier ());
				ruleList.setField ("groupname", oneGroup);

				ArrayList rules = new ArrayList ();

				for (Iterator i = ruleList.query ().iterator (); i.hasNext ();)
				{
					rules.add (i.next ());
				}

				totalRules = totalRules + rules.size ();

				if (rules.size () != 0)
				{
					Map props = ((InvokationSecurable) o.getService ()).getAuthorizationProperties ();
					String onePropName = null;
					Persistent oneRule = null;

					for (Iterator ir = rules.iterator (); ir.hasNext ();)
					{
						oneRule = (Persistent) ir.next ();
						onePropName = oneRule.getFieldString ("property");

						if (compare (oneRule, props.get (onePropName), ue))
						{
							if ((operationCode.equals ("*"))
											|| (oneRule.getFieldString ("operationsallowed").equals ("*"))
											|| (oneRule.getFieldString ("operationsallowed").indexOf (operationCode) >= 0))
							{
								log.debug ("Rule " + oneRule.toString () + " succeeds, authorization granted so far");
								rulesMatched++;
							}
						}
					}
				}
			}
		}
		catch (PersistenceException pe)
		{
			throw new AuthorizationException (pe);
		}
		finally
		{
			releaseService (pf);

			try
			{
				Proxy proxy = (Proxy) pf;

				proxy.getInvocationHandler (proxy).invoke (proxy, KeelServiceable.class.getMethod ("releaseServices"),
								new Object[]
								{});
			}
			catch (Throwable x)
			{
			}
		}

		if (totalRules == 0)
		{
			log.debug ("No InvokationSecurity rules found to try - authorization denied");

			return false;
		}

		if (totalRules == rulesMatched)
		{
			log.debug ("All rules tested succeeded - authorization granted");

			return true;
		}

		if (log.isDebugEnabled ())
		{
			log.debug ("There were " + totalRules + " rules tested, but only " + rulesMatched + ", auth denied");
		}

		return false;
	}

	/* Check the specified rule against the property provided by
	 * the InvokationSecurity
	 */
	private boolean compare (Persistent rule, Object propValue, UserEnvironment ue)
		throws PersistenceException, AuthorizationException
	{
		String comparator = rule.getFieldString ("comparator").trim ();

		/* Comparator "ok" means all requests are allowed */
		if (comparator.equals ("ok"))
		{
			return true;
		}

		/* Strict equality, no null allowed */
		if (comparator.equals ("eq") || comparator.equals ("="))
		{
			if (propValue == null)
			{
				/* Can't be null */
				return false;
			}

			if (propValue.toString ().equals (expandValue (rule.getFieldString ("value"), ue)))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (comparator.equals ("en"))
		{ /* Equal or null */

			if (propValue == null)
			{
				return true;
			}

			if (propValue.toString ().equals (expandValue (rule.getFieldString ("value"), ue)))
			{
				return true;
			}

			return false;
		}
		else if (comparator.equals ("in"))
		{ /* contained in */

			String valueString = expandValue (rule.getFieldString ("value").trim (), ue);
			StringTokenizer stk = new StringTokenizer (valueString, " ");

			while (stk.hasMoreTokens ())
			{
				String oneToken = stk.nextToken ();

				if (propValue.equals (oneToken))
				{
					return true;
				}
			}

			return false;
		}
		else
		{
			throw new AuthorizationException ("Unknown comparator '" + comparator + "'");
		}
	}

	/************************* Lifecycle methods **************************/

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure (Configuration configuration) throws ConfigurationException
	{
		// Obtain a reference to the configured DataSource
		String bypassAmName = configuration.getChild ("bypass-am").getValue ("*");

		try
		{
			bypassAm = (AuthorizationManager) getService (AuthorizationManager.ROLE, bypassAmName);
		}
		catch (ServiceException e)
		{
			throw new ConfigurationException ("Cannot obtain bypass auth. manager", e);
		}

		rootBypass = configuration.getChild ("root-bypass").getValueAsBoolean (false);
	}

	/********* Convenience methods below here ******************/

	/**
	 * If the instance of this SecurityManager is
	 * Contextualizable, then we can determine the principal(s)
	 * by getting the UserEnvironment from the context. This is essentially
	 * a convenience method for allowed(Operation, UserEnvironment)
	 *
	 * @param o The Operation object that describes the requested operation
	 * @param c The Context we use to determine who the user is making the request
	 * @return True if the operation is allowed, false if not.
	 * @throws AuthorizationException If an error occurs while trying to determine authorization
	 */
	public boolean allowed (Operation o, Context c) throws AuthorizationException
	{
		boolean returnValue = false;
		assert c != null;

		if (log.isDebugEnabled ())
		{
			log.debug ("Checking auth for operation " + o.toString () + " for user in context for "
							+ o.getService ().toString ());
		}

		try
		{
			UserEnvironment ue = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);

			returnValue = allowed (o, ue);
		}
		catch (AuthorizationException ae)
		{
			log.error ("Authorization exception while checking authorization", ae);
		}
		catch (ContextException e)
		{
			throw new AuthorizationException ("Context exception - no login", e);
		}
		catch (NullPointerException ne)
		{
			throw new AuthorizationException ("Null pointer exception retrieving from context", ne);
		}

		return returnValue;
	}

	/******************** Private methods **********************/

	/**
	 * Obtains the PersistentFactory initialized with a specific context
	 */
	private PersistentFactory getPersistentFactory (Context c) throws AuthorizationException
	{
		PersistentFactory myFactory = null;

		try
		{
			PersistentFactory tmpFactory = (PersistentFactory) getService (PersistentFactory.ROLE, "default", c);

			myFactory = (PersistentFactory) getService (PersistentFactory.ROLE, tmpFactory.getSecurity (), c);
		}
		catch (ServiceException se)
		{
			throw new AuthorizationException ("An error occured while trying to fetch the PersistentFactory.", se);
		}

		return myFactory;
	}

	/**
	 * Translate a value beginning with "$" to some specific value based on the
	 * current user.
	 * @param value
	 * @return Either the same value unchanged (if it does not begin with a "$" or is
	 * not a recognized special tag value), or the translated value.
	 */
	private String expandValue (String value, UserEnvironment ue) throws AuthorizationException
	{
		if (! value.startsWith ("$"))
		{
			return value;
		}

		if (value.equals ("$uid"))
		{
			return "" + ue.getUid ();
		}
		else if (value.equals ("$login"))
		{
			return ue.getLoginName ();
		}
		else if (value.equals ("$groups"))
		{
			StringBuffer groups = new StringBuffer ();
			boolean needSpace = false;

			for (Iterator i = ue.getGroups ().iterator (); i.hasNext ();)
			{
				if (needSpace)
				{
					groups.append (" ");
				}

				groups.append ((String) i.next ());

				return groups.toString ();
			}
		}

		return value;
	}

	/**
	 * Utility method to get the component name corresponding to a given object
	 * @param component
	 * @return
	 */
	private String getComponentName (Object component)
	{
		String returnValue = null;

		if (Proxy.isProxyClass (component.getClass ()))
		{
			Proxy proxy = (Proxy) component;
			Object o = ProxyObjectFactory.getObject (proxy);

			returnValue = o.getClass ().getName ();
		}
		else
		{
			returnValue = component.getClass ().getName ();
		}

		return returnValue;
	}

	private boolean checkInstanceSecurable (Operation o, UserEnvironment ue) throws AuthorizationException
	{
		if (checkInstanceSecurableInstance (o, ue, ((InstanceSecurable) o.getService ()).getInstanceIdentifier ()))
		{
			return true;
		}

		if (checkInstanceSecurableInstance (o, ue, "*"))
		{
			return true;
		}

		return false;
	}

	/**
	 * Check permissions for a InstanceSecurable component (e.g. not one
	 * which is Invokation or Instance securable)
	 *
	 * @param o
	 * @param ue
	 * @return
	 * @throws AuthorizationException
	 */
	private boolean checkInstanceSecurableInstance (Operation o, UserEnvironment ue, String instanceName)
		throws AuthorizationException
	{
		String operationCode = o.getOperationCode ();

		if ((operationCode == null) || (operationCode.trim ().equals ("")))
		{
			operationCode = "*";
		}

		PersistentFactory pf = null;

		if (ue.getGroups ().size () == 0)
		{
			throw new AuthorizationException ("User '" + ue.getLoginName () + "' is not a member of any groups");
		}

		try
		{
			pf = getPersistentFactory ();

			Persistent serviceSecurity = pf.create ("component-security.instancesecurity");

			serviceSecurity.setBypassAuthorizationManager (bypassAm);

			/* Iterate through all the groups that this principal is a member of */
			String oneGroup = null;

			for (Iterator j = ue.getGroups ().iterator (); j.hasNext ();)
			{
				oneGroup = (String) j.next ();

				serviceSecurity.clear ();
				serviceSecurity.setField ("component", getComponentName (o.getService ()));
				serviceSecurity.setField ("groupname", oneGroup);
				serviceSecurity.setField ("instance", instanceName);

				if (log.isDebugEnabled ())
				{
					log.debug ("Looking for " + serviceSecurity.toString ());
				}

				if (serviceSecurity.find ())
				{
					if (operationCode.equals ("*"))
					{
						return true;
					}

					if (serviceSecurity.getFieldString ("allowedoperations").indexOf (operationCode) > 0)
					{
						return true;
					}

					if (serviceSecurity.getFieldString ("allowedoperations").equals ("*"))
					{
						return true;
					}
				}
			}
		}
		catch (PersistenceException pe)
		{
			log.error ("Database error checking authorization", pe);
			throw new AuthorizationException (pe);
		}
		catch (AuthorizationException ae)
		{
			log.error ("Authorization error while checking authorization", ae);
			throw new AuthorizationException (ae);
		}
		finally
		{
			releaseService (pf);

			try
			{
				Proxy proxy = (Proxy) pf;

				proxy.getInvocationHandler (proxy).invoke (proxy, KeelServiceable.class.getMethod ("releaseServices"),
								new Object[]
								{});
			}
			catch (Throwable x)
			{
			}
		}

		if (log.isDebugEnabled ())
		{
			log.debug ("No authorization found of any type for " + o.toString () + ", denied access to "
							+ ue.toString ());
		}

		return false;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(java.lang.Object, java.lang.String, de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public boolean allowed (Object service, String id, UserEnvironment ue) throws AuthorizationException
	{
		return allowed (service, ue);
	}
}
