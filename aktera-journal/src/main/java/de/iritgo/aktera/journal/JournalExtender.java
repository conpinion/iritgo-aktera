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


import de.iritgo.aktera.journal.entity.JournalEntry;

import java.util.*;


public interface JournalExtender
{
	public void newJournalEntry(JournalEntry journal);

	public void deletedJournalEntry(JournalEntry journal);

	public void accept(JournalEntry journal, JournalStrategy strategy);

	public void addJournalEntryAttributes(Map<String, Object> entry);

	public void deleteAllJournalEntries(List<JournalEntry> journalEntries);

	/**
	 * Delete all journal entries that are older than periodInSeconds seconds
	 * (from the time this method is called).
	 * You ounly need to implement this method if you store custom journal data 
	 * in a location which differs from JournalEntry or JournalData.
	 * 
	 * @param periodInSeconds Delete all entries before this time period
	 */
	public void deleteAllJournalEntriesBefore(long periodInSeconds);
}
