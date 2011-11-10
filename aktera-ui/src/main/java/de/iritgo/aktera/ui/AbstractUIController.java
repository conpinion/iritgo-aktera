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

package de.iritgo.aktera.ui;


import org.apache.avalon.framework.logger.Logger;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.Security;
import de.iritgo.aktera.core.container.KeelContainer;


public abstract class AbstractUIController implements UIController
{
	/** Controller forward */
	protected String forward = "default";

	/** The resource bundle name */
	protected String bundle = "Aktera";

	/** The controller's security mode */
	protected Security security = Security.COMPONENT;

	/** Our logger */
	protected Logger logger;

	/**
	 * @see de.iritgo.aktera.ui.UIController#getForward()
	 */
	public String getForward()
	{
		return forward;
	}

	public void setForward(String forward)
	{
		this.forward = forward;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIController#getBundle()
	 */
	public String getBundle()
	{
		return bundle;
	}

	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIController#getSecurity()
	 */
	public Security getSecurity()
	{
		return security;
	}

	public void setSecurity(Security security)
	{
		this.security = security;
	}

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIController#redirect(java.lang.String, de.iritgo.aktera.ui.UIRequest, de.iritgo.aktera.ui.UIResponse)
	 */
	public void redirect(String bean, UIRequest request, UIResponse response) throws UIControllerException
	{
		UIController controller = (UIController) KeelContainer.defaultContainer().getSpringBean(bean);

		AuthorizationManager authorizationManager = (AuthorizationManager) KeelContainer.defaultContainer()
						.getSpringBean(AuthorizationManager.ID);

		try
		{
			if (! authorizationManager.allowed(controller, bean, request.getUserEnvironment()))
			{
				throw new SecurityException("Controller '" + bean + "' not authorized");
			}
		}
		catch (AuthorizationException x)
		{
			throw new SecurityException("Controller '" + bean + "' unable to check authorization", x);
		}

		controller.execute(request, response);

		if (response.getForward() == null)
		{
			response.setForward(controller.getForward());
		}
	}
}
