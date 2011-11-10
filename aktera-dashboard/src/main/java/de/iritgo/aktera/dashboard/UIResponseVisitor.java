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


import de.iritgo.aktera.dashboard.items.SimpleNumberItem;
import de.iritgo.aktera.dashboard.items.SimpleTextItem;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.ui.UIResponse;
import java.util.LinkedList;
import java.util.List;


public class UIResponseVisitor extends GroupVisitor
{
	protected UIResponse response;

	protected Output dashboardGroups;

	protected List<ResponseElement> currentGroupItems;

	public UIResponseVisitor()
	{
	}

	public UIResponseVisitor(UIResponse response)
	{
		this.response = response;
	}

	public UIResponse getResponse()
	{
		return response;
	}

	public Output getDashboardGroups()
	{
		return dashboardGroups;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.GroupVisitor#generate(de.iritgo.aktera.dashboard.items.SimpleNumberItem)
	 */
	@Override
	public void generate(SimpleNumberItem simpleNumberItem)
	{
	}

	/**
	 * The base method to generate the dashboard group item
	 *
	 * @param dashboardGroup The dashboard group
	 */
	public void generate(DashboardGroup dashboardGroup)
	{
		currentGroupItems = new LinkedList<ResponseElement>();

		if (response == null && parentVisitor != null)
		{
			response = ((UIResponseVisitor) parentVisitor).getResponse();
			dashboardGroups = ((UIResponseVisitor) parentVisitor).getDashboardGroups();
		}

		Output group = response.createOutput(dashboardGroup.getId());

		group.setContent(currentGroupItems);
		group.setAttribute("title", dashboardGroup.getTitle());
		group.setAttribute("renderInclude", dashboardGroup.getRenderFile());
		dashboardGroups.add(group);
	}

	/**
	 * @see de.iritgo.aktera.dashboard.GroupVisitor#generate(de.iritgo.aktera.dashboard.items.SimpleTextItem)
	 */
	public void generate(SimpleTextItem simpleTextItem)
	{
		Output item = response.createOutput(simpleTextItem.getId());

		item.setAttribute("label", simpleTextItem.getLabel());
		item.setAttribute("text", simpleTextItem.getText());
		item.setAttribute("measuringUnit", simpleTextItem.getMeasuringUnit());
		item.setAttribute("renderInclude", simpleTextItem.getRenderFile());
		currentGroupItems.add(item);
	}

	public void init(UIResponse response)
	{
		this.response = response;
		dashboardGroups = response.createOutput("dashboardGroups");
		response.add(dashboardGroups);
	}
}
