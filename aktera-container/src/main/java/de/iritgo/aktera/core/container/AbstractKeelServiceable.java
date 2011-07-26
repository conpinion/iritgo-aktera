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


import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Michael Nash
 * @author Shash Chatterjee
 *
 * A helper class to consolidate obtaining services in the Keel environment.
 * All Keel classes that would normally implement Avalon's Serviceable interface
 * should extend this class instead.  For classes that already extend other classes,
 * there is the KeelServiceableDelegate class that can be used to delegate service lookup.
 * *
 */
public abstract class AbstractKeelServiceable implements KeelServiceable
{
	/**
	 * The parent service manager
	 */
	private KeelServiceManager serviceManager = null;

	/**
	 * The list of components looked up via this class
	 */
	private List components = new ArrayList ();

	/**
	 * Sets the parent service manager to lookup services from
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service (final ServiceManager parent) throws ServiceException
	{
		if (parent instanceof KeelServiceManager)
		{
			serviceManager = (KeelServiceManager) parent;
		}
		else
		{
			throw new ServiceException ("servicemanager",
							"Supplied service manager is not an instance of a KeelService Manager, instead it is a "
											+ parent.getClass ().getName ());
		}
	}

	/**
	 * Return the parent service manager
	 * @return ServiceManager
	 */
	public ServiceManager getServiceManager ()
	{
		return serviceManager;
	}

	/**
	 * Lookup a service
	 * @param role - The role for the service
	 * @param hint - Service shorthand, for a particular impl.
	 * @return Object - Cast to the correct service interface
	 * @throws ServiceException
	 */
	public Object getService (String role, String hint) throws ServiceException
	{
		String svcHint = hint;

		//Make sure the default hint is specified correctly
		if (hint == null || "".equals (hint) || "default".equals (hint))
		{
			svcHint = "*";
		}

		/*
		 * Lookup the service
		 */
		Object o = serviceManager.lookup (role + "/" + svcHint);

		// Keep the service in the list for later release
		addService (o);

		return o;
	}

	/**
	 * Lookup service and set security context
	 * @param role - The role for the service
	 * @param hint - Service shorthand, for a particular impl.
	 * @param c - Security context
	 * @return Object - Cast to the correct service interface
	 * @throws ServiceException
	 */
	public Object getService (String role, String hint, Context c) throws ServiceException
	{
		String svcHint = hint;

		//Make sure the default hint is specified correctly
		if (hint == null || "".equals (hint) || "default".equals (hint))
		{
			svcHint = "*";
		}

		/*
		 * Lookup the service
		 */
		Object o = serviceManager.lookup (role + "/" + svcHint, c);

		// Keep the service in the list for later release
		addService (o);

		return o;
	}

	/**
	 * Lookup default service
	 * @param role - The role for the service
	 * @return Object - Cast to the correct service interface
	 * @throws ServiceException
	 */
	public Object getService (String role) throws ServiceException
	{
		return getService (role, "*");
	}

	/**
	 * Lookup default service and set security context
	 * @param role - The role for the service
	 * @param c - Security context
	 * @return Object - Cast to the correct service interface
	 * @throws ServiceException
	 */
	public Object getService (String role, Context c) throws ServiceException
	{
		return getService (role, "*", c);
	}

	/**
	 * Lookup service selector
	 * @param role - The role for the service
	 * @return Object - Cast to the correct service interface
	 * @throws ServiceException
	 */
	public Object getServiceSelector (String role) throws ServiceException
	{
		return getService (role, "$");
	}

	/**
	 * Add a service to the list of retrieved services
	 */
	private synchronized void addService (Object o)
	{
		components.add (o);
	}

	/**
	 * Delete a service from the list of retrieved services
	 */
	private synchronized void deleteService (Object o)
	{
		for (int i = 0; i < components.size (); ++i)
		{
			if (components.get (i) == o || components.get (i).equals (o))
			{
				// 				releaseChild (components.get (i));
				components.remove (i);

				return;
			}
		}
	}

	/**
	 * Release all services retrieved so far
	 */
	public synchronized void releaseServices ()
	{
		for (Iterator i = components.iterator (); i.hasNext ();)
		{
			Object serviceable = (Object) i.next ();

			releaseChild (serviceable);

			serviceManager.release (serviceable);
		}

		components.clear ();
	}

	/**
	 * @param serviceable
	 */
	private void releaseChild (Object serviceable)
	{
		Proxy proxy = (Proxy) serviceable;

		try
		{
			proxy.getInvocationHandler (proxy).invoke (proxy, KeelServiceable.class.getMethod ("releaseServices"),
							new Object[]
							{});
		}
		catch (Throwable x)
		{
			if (x instanceof InvocationTargetException)
			{
				// 					System.out.println ("[AbstractKeelServiceable] " + ((InvocationTargetException) x).getTargetException ());
			}
			else
			{
				// 					System.out.println ("[AbstractKeelServiceable] " + x);
			}
		}
	}

	/**
	 * Release a particular service
	 * @param o Service to release
	 */
	public void releaseService (Object o)
	{
		serviceManager.release (o);
		deleteService (o);
	}
}
