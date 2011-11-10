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

package de.iritgo.aktera.base.menu;


import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;


public class SelectMenuItemController extends AbstractUIController
{
	/**
	 * Describe method execute() here.
	 *
	 * @param request
	 * @param response
	 * @throws UIControllerException
	 */
	public void execute(UIRequest request, UIResponse response) throws UIControllerException
	{
		if (request.getParameter("menu") != null)
		{
			request.getUserEnvironment().setAttribute("aktera.currentMenu", request.getParameterAsString("menu"));
		}

		if (request.getParameter("menu") != null)
		{
			request.getUserEnvironment().setAttribute("aktera.currentMenuItem",
							request.getParameterAsString("menuItem"));
		}
		else
		{
			request.getUserEnvironment().setAttribute("aktera.currentMenuItem", request.getParameterAsString("item"));
		}

		if (request.getParameter("targetBean") != null)
		{
			redirect(request.getParameterAsString("targetBean"), request, response);
		}
	}
}
