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


import de.iritgo.aktera.core.container.KeelServiceable;
import de.iritgo.aktera.core.container.KeelServiceableDelegate;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.lifecycle.AbstractAccessor;


/**
 * @author Michael Nash
 * @author Santanu Dutt
 */
public class AuthorizationLifecycleAccessExtension extends AbstractAccessor implements KeelServiceable
{
	private KeelServiceableDelegate m_delegate = null;

	/**
	 * Called when the given service is being accessed (via lookup() or
	 * select()).
	 *
	 * If the service is Securable then it checks whether the service is also
	 * Servicable. Every Securable service has to be Serviceable to allow the
	 * service to obtain the AuthorizationManger service.
	 *
	 * @param component a Service instance
	 * @param context a Context instance
	 * @exception Exception if an error occurs
	 */
	public void access (Object component, Context context) throws Exception
	{
		super.access (component, context);
	}

	/**
	 * @param manager
	 * @throws ServiceException
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service (ServiceManager manager) throws ServiceException
	{
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
