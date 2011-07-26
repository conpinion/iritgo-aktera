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

package de.iritgo.aktera.authorization.lifecycle;


import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.Securable;
import de.iritgo.aktera.core.container.KeelServiceable;
import de.iritgo.aktera.core.container.KeelServiceableDelegate;
import org.apache.avalon.fortress.util.ContextManagerConstants;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.lifecycle.AbstractCreator;


/**
 * The method description goes here
 *
 * @version $Revision: 1.1 $ $Date: 2006/05/23 15:59:16 $
 * @author Schatterjee
 * Created on Apr 24, 2003
 * */
public class AuthorizationLifecycleCreateExtension extends AbstractCreator implements KeelServiceable
{
	private ServiceManager m_servicemanager = null;

	private KeelServiceableDelegate m_delegate = null;

	public void create (Object component, Context context) throws Exception
	{
		if (component instanceof Securable)
		{
			// Determine the authmanager service hint to use
			String amHint = null;

			try
			{
				amHint = (String) context.get ("component.am");
			}
			catch (ContextException e)
			{
				//--- quikdraw: Why is this empty?
			}

			if ((null == amHint) || ("".equals (amHint) || "default".equals (amHint)))
			{
				// No specific auth. mgr., just use the default
				amHint = "authmanager";
				// log the fact
				getLogger ().debug ("no am attribute available, using standard authorization manager");
			}
			else
			{
				// A specified auth. mgr. - log the fact
				getLogger ().debug ("am attribute is " + amHint);
			}

			try
			{
				m_servicemanager = (ServiceManager) context.get (ContextManagerConstants.SERVICE_MANAGER);
			}
			catch (Exception e)
			{
				getLogger ().error ("Could not get Service Manager for life-cycle extensions");
				throw new Exception (e);
			}

			// Lookup the Authorization Manager from the Service Manager
			String serviceKey = AuthorizationManager.ROLE + "/" + amHint;
			AuthorizationManager myAuthMgr = null;

			try
			{
				myAuthMgr = (AuthorizationManager) m_servicemanager.lookup (serviceKey);
			}
			catch (Exception e)
			{
				getLogger ().error ("Could not get service " + serviceKey);
				throw new Exception (e);
			}

			if (myAuthMgr == null)
			{
				throw new Exception ("Got null service " + serviceKey);
			}

			// Now set the AuthorizationManager for the component
			((Securable) component).setAuthorizationManager (myAuthMgr);
		}

		super.create (component, context);
	}

	/**
	 * @param manager
	 * @throws ServiceException
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service (ServiceManager manager) throws ServiceException
	{
		m_servicemanager = manager;
		m_delegate = new KeelServiceableDelegate (manager);
	}

	/**
	 * Release all services retrieved so far
	 */
	public synchronized void releaseServices ()
	{
		m_delegate.releaseServices ();
	}
}
