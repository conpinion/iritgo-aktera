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


import de.iritgo.aktera.dashboard.GroupVisitor;
import de.iritgo.aktera.dashboard.items.AbstractDashboardItem;
import de.iritgo.aktera.journal.JournalManager;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.string.StringTools;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class JournalItemImpl extends AbstractDashboardItem implements JournalItem
{
	private String renderFile;

	private String primaryType;

	private Integer ownerId;

	private JournalManager journalManager;

	private List<Map<String, Object>> journalEntries;

	private Map<String, Object> currentEntry;

	private Iterator<Map<String, Object>> iterator;

	public void setJournalManager(JournalManager journalManager)
	{
		this.journalManager = journalManager;
	}

	public void init(String primaryType, Integer ownerId)
	{
		this.primaryType = primaryType;
		this.ownerId = ownerId;

		HashMap<String, Object> conds = new HashMap();

		conds.put("ownerId", ownerId);
		conds.put("primaryType", primaryType);
		journalEntries = journalManager.listJournalEntriesByCondition("occurredAt", SortOrder.DESCENDING, 0, 3,
						"ownerId = :ownerId AND primaryType = :primaryType", conds);
		iterator = journalEntries.iterator();
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	public void next()
	{
		currentEntry = iterator.next();
	}

	public String getJournalId()
	{
		return StringTools.trim(currentEntry.get("id"));
	}

	public String getRawData()
	{
		return StringTools.trim(currentEntry.get("rawData"));
	}

	public String getSecondaryType()
	{
		return StringTools.trim(currentEntry.get("secondaryType"));
	}

	public String getShortMessage()
	{
		return StringTools.trim(currentEntry.get("shortMessage"));
	}

	public String getMessage()
	{
		return StringTools.trim(currentEntry.get("message"));
	}

	public String getOccurredAt()
	{
		return StringTools.trim(currentEntry.get("occurredAt"));
	}

	public String getMisc()
	{
		return StringTools.trim(currentEntry.get("misc"));
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#update()
	 */
	public void update()
	{
	}

	public void setRenderFile(String file)
	{
		this.renderFile = file;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#getRenderFile()
	 */
	public String getRenderFile()
	{
		return renderFile;
	}

	public void generate(GroupVisitor visitor)
	{
		((JournalUIResponseVisitor) visitor).generate(this);
	}
}
