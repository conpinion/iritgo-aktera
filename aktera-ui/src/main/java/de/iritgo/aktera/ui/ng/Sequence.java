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

package de.iritgo.aktera.ui.ng;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.Security;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.ui.UIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;


public class Sequence extends AbstractUIController
{
	private AuthorizationManager authorizationManager;

	public void setAuthorizationManager (AuthorizationManager authorizationManager)
	{
		this.authorizationManager = authorizationManager;
	}

	private List<String> controllerIds;

	public void setControllerIds (List<String> controllerIds)
	{
		this.controllerIds = controllerIds;
	}

	public Map<String, Properties> controllerParams = new HashMap ();

	public void setControllerParams (Map<String, Properties> controllerParams)
	{
		this.controllerParams = controllerParams;
	}

	public Sequence ()
	{
		security = Security.INSTANCE;
	}

	public void execute (UIRequest request, UIResponse response) throws UIControllerException
	{
		for (String controllerId : controllerIds)
		{
			UIController controller = (UIController) SpringTools.getBean (controllerId);

			try
			{
				if (! authorizationManager.allowed (controller, controllerId, request.getUserEnvironment ()))
				{
					throw new SecurityException ("Controller '" + controllerId + "' not authorized");
				}
			}
			catch (AuthorizationException x)
			{
				throw new SecurityException ("Controller '" + controllerId + "' not authorized", x);
			}

			Properties props = controllerParams.get (controllerId);
			if (props != null)
			{
				for (Entry<Object, Object> param : props.entrySet ())
				{
					request.getParameters ().put (param.getKey ().toString (), param.getValue ());
				}
			}

			controller.execute (request, response);
		}
	}
}
