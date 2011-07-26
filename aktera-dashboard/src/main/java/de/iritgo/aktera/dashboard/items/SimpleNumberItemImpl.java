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


public class SimpleNumberItemImpl extends AbstractDashboardItem implements SimpleNumberItem
{
	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleNumberItem#getMaxValue()
	 */
	public int getMaxValue ()
	{
		return 0;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleNumberItem#getMeasuringUnit()
	 */
	public String getMeasuringUnit ()
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleNumberItem#getValue()
	 */
	public int getValue ()
	{
		return 0;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleNumberItem#isGreenValue(int)
	 */
	public boolean isGreenValue (int value)
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleNumberItem#isRedValue(int)
	 */
	public boolean isRedValue (int value)
	{
		return false;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.items.SimpleNumberItem#isYellowValue(int)
	 */
	public boolean isYellowValue (int value)
	{
		return false;
	}

	public void generate (GroupVisitor boxAttributeVisitor)
	{
		boxAttributeVisitor.generate (this);
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#update()
	 */
	public void update ()
	{
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#getRenderFile()
	 */
	public String getRenderFile ()
	{
		return "/aktera/dashboard/items/simple-number-item.jsp";
	}
}
