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

package de.iritgo.aktera.tools;


import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.core.container.KeelServiceable;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ResponseElement;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceException;
import java.lang.reflect.Proxy;
import java.util.Iterator;


/**
 * Use the keel tools to get a request call and release the request.
 */
public final class KeelTools
{
	/**
	 * Retrieve a Keel service.
	 *
	 * @param role
	 * @return
	 * @throws ServiceException
	 */
	public static Object getService (String role) throws ServiceException
	{
		return getService (role, "default");
	}

	/**
	 * Retrieve a Keel service.
	 *
	 * @param role
	 * @param hint
	 * @return
	 * @throws ServiceException
	 */
	public static Object getService (String role, String hint) throws ServiceException
	{
		return KeelContainer.defaultContainer ().getService (role, hint);
	}

	/**
	 * Retrieve a Keel service.
	 *
	 * @param role
	 * @param hint
	 * @return
	 * @throws ServiceException
	 */
	public static Object getService (String role, String hint, Context context) throws ServiceException
	{
		return KeelContainer.defaultContainer ().getService (role, hint, context);
	}

	/**
	 * Release a Keel service.
	 *
	 * @param service
	 */
	public static void releaseService (Object service)
	{
		if (service == null)
		{
			return;
		}

		KeelContainer.defaultContainer ().release (service);

		try
		{
			Proxy proxy = (Proxy) service;

			Proxy.getInvocationHandler (proxy).invoke (proxy, KeelServiceable.class.getMethod ("releaseServices"),
							new Object[]
							{});
		}
		catch (Throwable x)
		{
			System.out.println ("[KeelTools] Error while releasing service '" + service + "': " + x);
		}
	}

	/**
	 * Copy all response elements from one response to another.
	 *
	 * @param to The destination response
	 * @param from The source response
	 */
	public static void copyResponseElements (KeelResponse to, KeelResponse from)
	{
		if (from == null || to == null)
		{
			return;
		}

		try
		{
			for (Iterator<ResponseElement> i = from.getAll (); i.hasNext ();)
			{
				to.add (i.next ());
			}
		}
		catch (ModelException x)
		{
		}
	}
}
