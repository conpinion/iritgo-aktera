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

package de.iritgo.aktera.dashboard;


import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;


public class PromptDashboard extends AbstractUIController
{
	/** The dashboard manager */
	private DashboardManager dashboardManager;

	private UIResponseVisitor uiResponseVisitor;

	/**
	 * The dashboard manager
	 *
	 * @param dashboardManager The manager
	 */
	public void setDashboardManager(DashboardManager dashboardManager)
	{
		this.dashboardManager = dashboardManager;
	}

	public void setUiResponseVisitor(UIResponseVisitor uiResponseVisitor)
	{
		this.uiResponseVisitor = uiResponseVisitor;
	}

	/**
	 * Execute the model.
	 *
	 **/
	public void execute(UIRequest request, UIResponse response) throws UIControllerException
	{
		uiResponseVisitor.init(response);

		for (DashboardGroup dashboardGroup : dashboardManager.getDashboardGroups())
		{
			DashboardGroup freshDashboardGroup = dashboardGroup.newInstance();

			freshDashboardGroup.setLocale(request.getLocale());
			freshDashboardGroup.generate(uiResponseVisitor);
		}
	}
}
