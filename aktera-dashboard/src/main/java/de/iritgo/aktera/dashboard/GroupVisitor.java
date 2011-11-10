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


public abstract class GroupVisitor
{
	protected GroupVisitor parentVisitor;

	/**
	 * The base method to generate things for the simple number item.
	 *
	 * @param simpleNumberItem The simple number item
	 */
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
	}

	/**
	 * The base method to generate the simple text item
	 *
	 * @param simpleTextItemImpl The simple text item
	 */
	public void generate(SimpleTextItem simpleTextItem)
	{
	}

	/**
	 * Set the parent visitor
	 *
	 * @param visitor The parent visitor
	 */
	public void setParentVisitor(GroupVisitor visitor)
	{
		this.parentVisitor = visitor;
	}
}
