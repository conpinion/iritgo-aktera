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
import de.iritgo.aktera.authorization.dao.AuthorizationDAO;
import de.iritgo.aktera.authorization.entity.ComponentSecurity;
import de.iritgo.aktera.authorization.entity.InstanceSecurity;
import de.iritgo.aktera.ui.UIController;
import org.apache.avalon.framework.context.Context;


/**
 * Implementation of the authorization manager.
 */
public class AuthorizationManagerImpl implements AuthorizationManager
{
	/** Authorization DAO */
	private AuthorizationDAO authorizationDAO;

	/**
	 * Set the authorization DAO.
	 *
	 * @param authorizationDAO
	 *            The authorization DAO
	 */
	public void setAuthorizationDAO (AuthorizationDAO authorizationDAO)
	{
		this.authorizationDAO = authorizationDAO;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(de.iritgo.aktera.authorization.Operation,
	 *      de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public boolean allowed (Operation o, UserEnvironment ue) throws AuthorizationException
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(de.iritgo.aktera.authorization.Operation,
	 *      org.apache.avalon.framework.context.Context)
	 */
	public boolean allowed (Operation o, Context c) throws AuthorizationException
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(java.lang.Object,
	 *      de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public boolean allowed (Object service, UserEnvironment ue) throws AuthorizationException
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(java.lang.Object,
	 *      org.apache.avalon.framework.context.Context)
	 */
	public boolean allowed (Object service, Context c) throws AuthorizationException
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(java.lang.Object, java.lang.String, de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public boolean allowed (Object service, String serviceId, UserEnvironment ue) throws AuthorizationException
	{
		if (service instanceof UIController)
		{
			if (ue.getGroups ().contains ("root"))
			{
				return true;
			}

			UIController controller = (UIController) service;

			switch (controller.getSecurity ())
			{
				case NONE:
					return true;

				case COMPONENT:
					return checkComponentSecurity (service, ue);

				case INSTANCE:
					return checkInstanceSecurity (service, serviceId, ue);

				case INVOKATION:
					return false;
			}

			return false;
		}

		return false;
	}

	/**
	 * Check a component security.
	 *
	 * @param service The service component to check
	 * @param ue The user's environment
	 * @return True if the user is authorized
	 * @throws AuthorizationException
	 */
	private boolean checkComponentSecurity (Object service, UserEnvironment ue) throws AuthorizationException
	{
		for (String group : ue.getGroups ())
		{
			ComponentSecurity security = authorizationDAO.findComponentSecurityById (service.getClass ().getName (),
							group);

			if (security != null)
			{
				if (security.getOperationsAllowed ().equals ("*"))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check a instance security.
	 *
	 * @param service The service component to check
	 * @param ue The user's environment
	 * @parm id The service id
	 * @return True if the user is authorized
	 * @throws AuthorizationException
	 */
	private boolean checkInstanceSecurity (Object service, String serviceId, UserEnvironment ue)
		throws AuthorizationException
	{
		for (String group : ue.getGroups ())
		{
			InstanceSecurity security = authorizationDAO.findInstanceSecurityById (service.getClass ().getName (),
							serviceId, group);

			if (security != null)
			{
				if (security.getOperationsAllowed ().equals ("*"))
				{
					return true;
				}
			}
		}

		return false;
	}
}
