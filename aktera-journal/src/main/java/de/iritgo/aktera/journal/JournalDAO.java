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
import de.iritgo.simplelife.constants.SortOrder;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


/**
 * Journal DAO.
 */
public interface JournalDAO
{
	static public String ID = "de.iritgo.aktera.address.JournalDAO";

	/**
	 * Create a journal entry
	 *
	 * @param journal The journal entry
	 */
	@Transactional(readOnly = false)
	public void create(JournalEntry journal);

	/**
	 * Delete a journal entry
	 *
	 * @param journal The journal entry
	 */
	@Transactional(readOnly = false)
	public void delete(JournalEntry cdr);

	/**
	 * Update a journal entry
	 *
	 * @param journal The journal
	 */
	@Transactional(readOnly = false)
	public void update(JournalEntry journal);

	/**
	 * Retrieve a journal entry by the given id
	 *
	 * @param id The id
	 * @return The journal entry
	 */
	public JournalEntry getById(Integer id);

	public List<JournalEntry> listJournalEntries(String search, Timestamp start, Timestamp end, Integer ownerId,
					String ownerType, String sortColumnName, SortOrder sortOrder, int firstResult, int resultsPerPage);

	public long countJournalEntries(String search, Timestamp start, Timestamp end, Integer ownerId, String ownerType);

	/**
	 * Create a journal data.
	 *
	 * @param The journal data object
	 */
	public void createJournalData(JournalData journalData);

	/**
	 * Retrieve a journal data object by the given id
	 *
	 * @param id The primary key id
	 * @return The journal data
	 */
	public JournalData getJournalDataById(Integer id);

	/**
	 * Delete a journal data object
	 *
	 * @param journalData
	 */
	public void deleteJournalData(JournalData journalData);

	/**
	 * Updat a journal data object
	 *
	 * @param journalData
	 */
	public void updateJournalData(JournalData journalData);

	/**
	 * Find a journal entry by a given tag
	 *
	 * @param tag The given trag
	 */
	public JournalEntry findEntryByTag(String uuid);

	/**
	 * List a all journal entrys by primary and secondary type
	 *
	 * @param search Search
	 * @param start Start
	 * @param end End
	 * @param ownerId ownerId
	 * @param ownerType ownerType
	 * @param sortColumnName column name
	 * @param sortOrder oder
	 * @param firstResult first result
	 * @param resultsPerPage results per page
	 * @param primaryType primare type
	 * @param secondaryType secondary type
	 * @return The list The list
	 */
	public List<JournalEntry> listJournalEntriesByPrimaryAndSecondaryType(String search, final Timestamp start,
					final Timestamp end, final Integer ownerId, String ownerType, String sortColumnName,
					SortOrder sortOrder, final int firstResult, final int resultsPerPage, final String primaryType,
					final String secondaryType);

	/**
	 * Count the journal entries by a given condition
	 *
	 * @param search Search
	 * @param start staret
	 * @param end end
	 * @param ownerId owner id
	 * @param ownerType owner type
	 * @param primaryType primary type
	 * @param secondaryType secnodary type
	 * @return
	 */
	public long countJournalEntriesByPrimaryAndSecondaryType(String search, final Timestamp start, final Timestamp end,
					final Integer ownerId, String ownerType, final String primaryType, final String secondaryType);

	/**
	 * Return a journal entry list by the given condition
	 *
	 * @param sortColumnName
	 * @param sortOrder
	 * @param firstResult
	 * @param resultsPerPage
	 * @param condition
	 * @param conditionMap
	 * @return
	 */
	public List<JournalEntry> listJournalEntriesByCondition(String sortColumnName, SortOrder sortOrder,
					final int firstResult, final int resultsPerPage, final String condition,
					final Map<String, Object> conditionMap);

	/**
	 * Count the journal entry list by the given condition
	 *
	 * @param firstResult
	 * @param resultsPerPage
	 * @param condition
	 * @param conditionMap
	 * @return
	 */
	public long countJournalEntriesByCondition(final String condition, final Map<String, Object> conditionMap);

	/**
	 * Return a list with all journal entries by the given owner id
	 *
	 * @param ownerId The owner id
	 * @return The journal entry list
	 */
	public List<JournalEntry> listJournalEntriesByOwnerId(Integer ownerId);

	/**
	 * Find a journal entry by a given misc
	 *
	 * @param tag The given misc
	 */
	public JournalEntry findEntryByMisc(String tag);

	/**
	 * Return a journal entry by the given type and id of the extendend info fields
	 *
	 * @param String the type
	 * @param Id The id
	 * @return The journal entry
	 */
	public JournalEntry getByExtendedInfoTypeAndExtendedInfoId(String type, Integer id);

	/**
	 * Delete all journal entries by the given owner id
	 *
	 * @param ownerId The owner id
	 */
	public void deleteAllJournalEntriesByOwnerId(Integer ownerId);

	/**
	 * Delete a journal data entry by the given id
	 *
	 * @param extendedInfoId The id
	 */
	public void deleteJournalDataById(Integer id);
}
