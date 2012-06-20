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

package de.iritgo.aktera.journal;


import de.iritgo.aktera.journal.entity.JournalData;
import de.iritgo.aktera.journal.entity.JournalEntry;
import de.iritgo.simplelife.bean.BeanTools;

import java.util.*;


public class JournalDataExtenderImpl implements JournalDataExtender, JournalExtender
{
	private JournalDAO journalDAO;

	public void setJournalDAO(JournalDAO journalDAO)
	{
		this.journalDAO = journalDAO;
	}

	@Override
	public void accept(JournalEntry journal, JournalStrategy strategy)
	{
	}

	@Override
	public void deletedJournalEntry(JournalEntry journal)
	{
		journalDAO.deleteJournalDataById (journal.getExtendedInfoId());
	}

	@Override
	public void deleteAllJournalEntries(List<JournalEntry> journalEntries)
	{
		journalDAO.deleteAllJournalDataByJournalEntries (journalEntries);
	}

	@Override
	public void newJournalEntry(JournalEntry journal)
	{
	}

	@Override
	public void addJournalEntryAttributes(Map<String, Object> entry)
	{
		JournalData journalData = journalDAO.getJournalDataById((Integer) entry.get("extendedInfoId"));
		BeanTools.copyBean2Map(journalData, entry, false);
	}

	@Override
	public void deleteAllJournalEntriesBefore(long periodInSeconds)
	{
	}
}
