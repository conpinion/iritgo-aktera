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
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.simplelife.constants.SortOrder;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public interface JournalManager
{
	public static enum COMMANDS
	{
		EXECUTE;
	}

	public static String ID = "de.iritgo.aktera.journal.JournalManager";

	/**
	 * Add a journal entry to the journal system
	 *
	 * @param journal The journal
	 */
	public void addJournalEntry(JournalEntry journal);

	/**
	 * Delete a journal entry to the journal system
	 *
	 * @param journal The journal
	 */
	public void deleteJournalEntry(JournalEntry journal);

	/**
	 * Retrieve the journal entry and call on all extenders the visit method from the visitor
	 *
	 * @param id The journal id
	 * @param strategy The strategy
	 */
	public Map<String, Object> getJournalEntryById(Integer id);

	public List<Map<String, Object>> listJournalEntries(String parameterAsString, Timestamp start, Timestamp end,
					Integer ownerId, String ownerType, String sortColumnName, SortOrder sortOrder, int firstResult,
					int resultsPerPage);

	public long countJournalEntries(String search, Timestamp start, Timestamp end, Integer ownerId, String ownerType);

	public List<Map<String, Object>> listJournalEntriesByPrimaryAndSecondaryType(String search, Timestamp start,
					Timestamp end, Integer ownerId, String ownerType, String sortColumnName, SortOrder sortOrder,
					int firstResult, int resultsPerPage, String primaryType, String secondaryType);

	public long countJournalEntriesByPrimaryAndSecondaryType(String search, Timestamp start, Timestamp end,
					Integer ownerId, String ownerType, String primaryType, String secondaryType);

	public List<Map<String, Object>> listJournalEntriesByCondition(String sortColumnName, SortOrder sortOrder,
					final int firstResult, final int resultsPerPage, final String condition,
					final Map<String, Object> conditionMap);

	public long countJournalEntriesByCondition(final String condition, final Map<String, Object> conditionMap);

	/**
	 * Delete a journal entry to the journal system
	 *
	 * @param journal The journal id
	 */
	public void deleteJournalEntry(int journalEntryId);

	/**
	 * Execute a special execution method on the entry
	 *
	 * @param commandId The command id
	 * @param id The id
	 * @param preifx The prefix
	 */
	public void executeJournalEntry(String commandId, int id, String prefix, ModelRequest req);

	/**
	 * Delete all journal entries for the given owner id.
	 *
	 * @param ownerId The owner id
	 */
	public void deleteJournalAllEntries(Integer ownerId);

	/**
	 * Delete all journal entries that are older than periodInSeconds seconds
	 * (from the time this method is called)
	 * 
	 * @param periodInSeconds Delete all entries before this time period
	 */
	public void deleteAllJournalEntriesBefore(long periodInSeconds);

	/**
	 * Cleanup the jounal data. Which journal entries are deleted is spefied
	 * in the system configuration.
	 */
	public void journalCleanup();
}
