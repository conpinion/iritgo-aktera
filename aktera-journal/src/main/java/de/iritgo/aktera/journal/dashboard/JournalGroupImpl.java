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

package de.iritgo.aktera.journal.dashboard;


import de.iritgo.aktera.dashboard.DashboardGroup;
import de.iritgo.aktera.dashboard.DashboardItem;
import de.iritgo.aktera.dashboard.GroupVisitor;
import de.iritgo.aktera.dashboard.groups.AbstractGroupImpl;


public class JournalGroupImpl extends AbstractGroupImpl implements JournalGroup
{
	private int numberOfColumns;

	private String primaryType;

	private Integer ownerId;

	public void setPrimaryType (String primaryType)
	{
		this.primaryType = primaryType;
	}

	public void setOwnerId (Integer ownerId)
	{
		this.ownerId = ownerId;
	}

	public int getNumberOfColumns ()
	{
		return numberOfColumns;
	}

	public void setNumberOfColumns (int numberOfColumns)
	{
		this.numberOfColumns = numberOfColumns;
	}

	public String getDescription ()
	{
		return "desctiption";
	}

	public void update ()
	{
	}

	public void generate (GroupVisitor visitor)
	{
		customVisitor.setParentVisitor (visitor);
		customVisitor.generate ((DashboardGroup) this);

		for (DashboardItem dashboardItem : dashboardItems)
		{
			if (dashboardItem instanceof JournalItem)
			{
				((JournalItem) dashboardItem).init (primaryType, ownerId);
			}

			dashboardItem.generate (customVisitor);
		}
	}
}
