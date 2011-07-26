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


import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;


/**
 * @author Schatterjee
 *
 * For classes that cannot extend AbstractKeelServiceable, this class
 * provides an alternate way to delegate service lookup.
 *
 * Classes using this delegate should implement the Avalon Serviceable interface,
 * and create a new KeelServiceableDelegate object in the service(ServiceManager parent) method
 */
public class KeelServiceableDelegate extends AbstractKeelServiceable
{
	/**
	 * @param container
	 * @param parent
	 * @param m_extManager
	 * @param m_context
	 */
	public KeelServiceableDelegate (ServiceManager parent) throws ServiceException
	{
		service (parent);
	}
}
