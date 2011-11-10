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

package de.iritgo.aktera.model;


import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.Securable;
import org.apache.avalon.framework.service.ServiceException;


/**
 * @author Michael Nash
 *
 * Simple base class to facilitate creation of Securable models
 */
public abstract class SecurableStandardLogEnabledModel extends StandardLogEnabledModel implements Securable
{
	private AuthorizationManager am = null;

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authorization.Securable#setAuthorizationManager(de.iritgo.aktera.authorization.AuthorizationManager)
	 */
	public void setAuthorizationManager(AuthorizationManager newAm) throws ServiceException
	{
		am = newAm;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authorization.Securable#getAuthorizationManager()
	 */
	public AuthorizationManager getAuthorizationManager()
	{
		return am;
	}
}
