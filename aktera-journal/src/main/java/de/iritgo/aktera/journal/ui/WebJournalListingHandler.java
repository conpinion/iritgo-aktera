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

package de.iritgo.aktera.journal.ui;


import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.journal.JournalManager;
import de.iritgo.aktera.journal.JournalSearch;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.listing.ListContext;
import de.iritgo.aktera.ui.listing.ListFiller;
import de.iritgo.aktera.ui.listing.ListingDescriptor;
import de.iritgo.aktera.ui.listing.ListingHandler;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.string.StringTools;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Handler for journal listings.
 */
public class WebJournalListingHandler extends ListingHandler
{
	/** The user dao */
	private UserDAO userDAO;

	/** The journal manager */
	private JournalManager journalManager;

	private WebJournalListingBuilder webJournalListingBuilder;

	private Map<String, JournalSearch> journalSearches;

	/** Set the journal manager. */
	public void setJournalManager(JournalManager journalManager)
	{
		this.journalManager = journalManager;
	}

	/** Set the web journal listing builder. */
	public void setWebJournalListingBuilder(WebJournalListingBuilder webJournalListingBuilder)
	{
		this.webJournalListingBuilder = webJournalListingBuilder;
	}

	/** Set the user dao */
	public void setUserDAO(UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	/** Set the journal searches */
	public void setJournalSearches(List<JournalSearch> journalSearchesTmp)
	{
		journalSearches = new HashMap<String, JournalSearch>();

		for (JournalSearch search : journalSearchesTmp)
		{
			journalSearches.put(search.getCategoryId(), search);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.ListingHandler#createSearchCategories(de.iritgo.aktera.model.ModelRequest, ListingDescriptor)
	 */
	public Map<String, String> createSearchCategories(ModelRequest request, ListingDescriptor listing)
		throws ModelException
	{
		AkteraUser user = userDAO.findUserById(UserTools.getCurrentUserId(request));
		TreeMap searchTypes = new TreeMap();

		for (JournalSearch search : journalSearches.values())
		{
			searchTypes.put(search.getCategoryId(), search.getCategoryLabel(user));
		}

		return searchTypes;
	}

	public ListFiller createListing(ModelRequest request, ListingDescriptor listing, ListingHandler handler,
					ListContext context) throws ModelException, PersistenceException
	{
		AkteraUser user = userDAO.findUserById(UserTools.getCurrentUserId(request));

		String search = StringTools.trim((String) request.getParameter("listSearch"));
		String condition = "";
		Map<String, Object> conditionMap = null;

		if (! StringTools.isTrimEmpty(listing.getCategory()))
		{
			condition = journalSearches.get(listing.getCategory()).getCondition(search, user);
			conditionMap = journalSearches.get(listing.getCategory()).getConditionMap(search, user);

			long numEntries = journalManager.countJournalEntriesByCondition(condition, conditionMap);

			List<Map<String, Object>> journal = journalManager.listJournalEntriesByCondition(listing
							.getSortColumnName(), listing.getSortOrder(), context.getFirstResult(), context
							.getResultsPerPage(), condition, conditionMap);

			return webJournalListingBuilder.createListFiller(journal, numEntries, request.getLocale());
		}

		long numEntries = journalManager.countJournalEntries(request.getParameterAsString("listSearch", ""),
						new Timestamp(0), new Timestamp(new Date().getTime()), UserTools.getCurrentUserId(request), "");

		List<Map<String, Object>> journal = journalManager.listJournalEntries(request.getParameterAsString(
						"listSearch", ""), new Timestamp(0), new Timestamp(new Date().getTime()), UserTools
						.getCurrentUserId(request), "", listing.getSortColumnName(), listing.getSortOrder(), context
						.getFirstResult(), context.getResultsPerPage());

		return webJournalListingBuilder.createListFiller(journal, numEntries, request.getLocale());
	}
}
