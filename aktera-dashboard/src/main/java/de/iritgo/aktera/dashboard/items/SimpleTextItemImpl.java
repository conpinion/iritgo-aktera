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

package de.iritgo.aktera.dashboard.items;


import de.iritgo.aktera.dashboard.GroupVisitor;


public class SimpleTextItemImpl extends AbstractDashboardItem implements SimpleTextItem
{
	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleTextItem#getMeasuringUnit()
	 */
	public String getMeasuringUnit()
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleTextItem#getText()
	 */
	public String getText()
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleTextItem#isGreenText(java.lang.String)
	 */
	public boolean isGreenText(String text)
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleTextItem#isRedText(java.lang.String)
	 */
	public boolean isRedText(String text)
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleTextItem#isYellowText(java.lang.String)
	 */
	public boolean isYellowText(String text)
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#generate(de.iritgo.aktera.dashboard.GroupVisitor)
	 */
	public void generate(GroupVisitor visitor)
	{
		visitor.generate(this);
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#update()
	 */
	public void update()
	{
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#getRenderFile()
	 */
	public String getRenderFile()
	{
		return "/aktera-dashboard/items/simple-text-item.jsp";
	}
}
