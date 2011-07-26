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

package de.iritgo.aktera.core.container;


import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.Securable;
import de.iritgo.aktera.context.KeelContextualizable;
import org.apache.avalon.fortress.Container;
import org.apache.avalon.fortress.impl.AbstractContainer;
import org.apache.avalon.fortress.impl.handler.ComponentHandler;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.WrapperServiceSelector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @version $Revision: 1.3 $ $Date: 2006/10/02 00:15:52 $
 * @author Santanu Dutt
 * @author Shash Chatterjee Created on Jun 8, 2003
 */
public class KeelServiceManager implements ServiceManager
{
	private final static class Lookup
	{
		String m_role;

		String m_hint;
	}

	private final Container m_container;

	private final Map m_used;

	private final ServiceManager m_parent;

	public KeelServiceManager (final Container container, final ServiceManager parent)
	{
		if (null == container)
		{
			throw new NullPointerException ("impl");
		}

		m_parent = parent;
		m_container = container;
		m_used = Collections.synchronizedMap (new HashMap ());
	}

	/**
	 * The logic for obtaining a service from a ServiceManager. First we try to
	 * get it from this ServiceManager, then we try to get it from the parent
	 * manager. If it exists, we will lastly try the JNDI context.
	 */
	public Object lookup (String role) throws ServiceException
	{
		Object o = lookupFManager (role);

		if (o instanceof Securable)
		{
			throw new ServiceException (role, "Securable components cannot be obtained here");
		}
		else
		{
			return o;
		}
	}

	/**
	 * The logic for obtaining a service from a ServiceManager. First we try to
	 * get it from this ServiceManager, then we try to get it from the parent
	 * manager. If it exists, we will lastly try the JNDI context.
	 */
	public Object lookup (String role, Context c) throws ServiceException
	{
		if (c == null)
		{
			throw new ServiceException (role, "Unable to pass null context");
		}

		Object o = lookupFManager (role);

		if ((o instanceof KeelContextualizable))
		{
			try
			{
				((KeelContextualizable) o).setKeelContext (c);
			}
			catch (ContextException e)
			{
				throw new ServiceException (role, e.getMessage ());
			}
		}

		if (o instanceof Securable)
		{
			AuthorizationManager am = ((Securable) o).getAuthorizationManager ();

			if (am == null)
			{
				throw new ServiceException (role,
								"Authorization Manager was not setup properly, this is a container setup problem");
			}

			try
			{
				if (! am.allowed (o, c))
				{
					throw new SecurityException ("Service '" + role + "' Not Authorized");
				}
			}
			catch (AuthorizationException e)
			{
				throw new ServiceException (role, "Error authorizing service " + role + " -  " + e.toString ());
			}
		}

		return o;
	}

	public Object lookupFManager (final String role) throws ServiceException
	{
		final Lookup lookup = parseRole (role);

		if (! m_container.has (lookup.m_role, lookup.m_hint))
		{
			return m_parent.lookup (role);
		}

		final Object result = m_container.get (lookup.m_role, lookup.m_hint);

		if (result instanceof ServiceSelector)
		{
			return result;
		}

		if (result instanceof ComponentSelector)
		{
			return new WrapperServiceSelector (lookup.m_role, (ComponentSelector) result);
		}

		if (! (result instanceof ComponentHandler))
		{
			final String message = "Invalid entry in component manager";

			throw new ServiceException (role, message);
		}

		try
		{
			final ComponentHandler handler = (ComponentHandler) result;
			final Object component = handler.get ();

			m_used.put (new ComponentKeelKey (component), handler);

			return component;
		}
		catch (final ServiceException ce)
		{
			throw ce; // rethrow
		}
		catch (final Exception e)
		{
			final String message = "Could not return a reference to the Component";

			throw new ServiceException (role, message, e);
		}
	}

	public boolean hasService (final String role)
	{
		final Lookup lookup = parseRole (role);

		if (m_container.has (lookup.m_role, lookup.m_hint))
		{
			return true;
		}
		else
		{
			return null != m_parent ? m_parent.hasService (role) : false;
		}
	}

	public void release (final Object component)
	{
		try
		{
			final ComponentHandler handler = (ComponentHandler) m_used.remove (new ComponentKeelKey (component));

			if (null == handler)
			{
				if (null == m_parent)
				{
					/*
					 * This is a purplexing problem. SOmetimes the m_used hash
					 * returns null for the component--usually a ThreadSafe
					 * component. When there is no handler and no parent, that
					 * is an error condition--but if the component is usually
					 * ThreadSafe, the impact is essentially nill.
					 */

					// Pete: This occurs when objects are released more often
					// than
					// when they are aquired
					// Pete: It also happens when a release of a
					// ComponentSelector occurs
				}
				else
				{
					m_parent.release (component);
				}
			}
			else
			{
				handler.put (component);
			}
		}
		catch (Exception x)
		{
			System.out.println ("ERROR [KeelServiceManager]: " + x);
		}
	}

	private Lookup parseRole (final String role)
	{
		final Lookup lookup = new Lookup ();

		lookup.m_role = role;
		lookup.m_hint = AbstractContainer.DEFAULT_ENTRY;

		if (role.endsWith ("Selector"))
		{
			lookup.m_role = role.substring (0, role.length () - "Selector".length ());
			lookup.m_hint = AbstractContainer.SELECTOR_ENTRY;
		}

		final int index = role.lastIndexOf ("/");

		// needs to be further than the first character
		if (index > 0)
		{
			lookup.m_role = role.substring (0, index);
			lookup.m_hint = role.substring (index + 1);
		}

		return lookup;
	}
}
