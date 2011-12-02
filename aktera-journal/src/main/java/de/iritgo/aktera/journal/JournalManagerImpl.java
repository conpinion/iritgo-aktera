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


import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.event.EventManager;
import de.iritgo.aktera.journal.entity.JournalEntry;
import de.iritgo.aktera.logger.Logger;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import de.iritgo.simplelife.bean.BeanTools;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.math.*;
import de.iritgo.simplelife.string.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Default implementation of the journal manager.
 */
public class JournalManagerImpl implements JournalManager, StartupHandler
{
	/** The journal dao */
	private JournalDAO journalDAO;

	private Logger logger;

	/** Journal extenders list */
	private Map<String, JournalExtender> journalExtenders;

	/** Journal executers */
	private List<JournalExecute> journalExecuters;

	/** The event manager */
	private EventManager eventManager;

	/** Set the journal dao */
	public void setJournalDAO(JournalDAO journalDAO)
	{
		this.journalDAO = journalDAO;
	}

	/** Set the event manager */
	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

	/** Set a list with journal extenders */
	public void setJournalExtenders(Map<String, JournalExtender> journalExtenders)
	{
		this.journalExtenders = journalExtenders;
	}

	/** Set journal executers */
	public void setJournalExecuters(List<JournalExecute> journalExecuters)
	{
		this.journalExecuters = journalExecuters;
	}

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup() throws StartupException
	{
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#addJournalEntry(de.iritgo.aktera.journal.entity.JournalEntry)
	 */
	public void addJournalEntry(JournalEntry journalEntry)
	{
		if (journalEntry.getExtendedInfoType() != null)
		{
			JournalExtender je = journalExtenders.get(journalEntry.getExtendedInfoType());

			if (je != null)
			{
				je.newJournalEntry(journalEntry);
			}
		}

		journalDAO.create(journalEntry);

		Properties eventProps = new Properties();

		eventProps.put("journalEntry", journalEntry);
		eventManager.fire("iritgo.aktera.journal.new-entry", eventProps);

		refreshAktarioJournalList(journalEntry);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#deleteJournalEntry(de.iritgo.aktera.journal.entity.JournalEntry)
	 */
	public void deleteJournalEntry(JournalEntry journalEntry)
	{
		if (journalEntry == null)
		{
			logger.error("Can't delete journal entry. The journal entry object is null.");
			Thread.dumpStack();

			return;
		}

		if (journalEntry.getExtendedInfoType() != null)
		{
			JournalExtender je = journalExtenders.get(journalEntry.getExtendedInfoType());

			if (je != null)
			{
				je.deletedJournalEntry(journalEntry);
			}
		}

		journalDAO.delete(journalEntry);

		Properties eventProps = new Properties();

		eventProps.put("journalEntry", journalEntry);
		eventManager.fire("iritgo.aktera.journal.removed-entry", eventProps);

		refreshAktarioJournalList(journalEntry);
	}

	private void refreshAktarioJournalList(JournalEntry journalEntry)
	{
		//Aktera-Aktario
		UserDAO userDAO = (UserDAO) SpringTools.getBean(UserDAO.ID);
		de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser userAktera = userDAO.findUserById(journalEntry
						.getOwnerId());

		Properties props = new Properties();

		props.setProperty("akteraUserName", userAktera.getName());
		CommandTools.performAsync("de.iritgo.aktera.journal.RefreshJournal", props);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#getJournalEntryById(java.lang.Integer, de.iritgo.aktera.journal.JournalStrategy)
	 */
	public Map<String, Object> getJournalEntryById(Integer id)
	{
		Map<String, Object> entry = new HashMap<String, Object>();

		JournalEntry journalEntry = journalDAO.getById(id);

		addJournalEntryAttributes(journalEntry, entry);

		if (journalEntry.getExtendedInfoType() != null)
		{
			JournalExtender je = journalExtenders.get(journalEntry.getExtendedInfoType());

			if (je != null)
			{
				je.addJournalEntryAttributes(entry);
			}
		}

		return entry;
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#listJournalEntries(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, de.iritgo.simplelife.constants.SortOrder, int, int)
	 */
	public List<Map<String, Object>> listJournalEntries(String search, Timestamp start, Timestamp end, Integer ownerId,
					String ownerType, String sortColumnName, SortOrder sortOrder, int firstResult, int resultsPerPage)
	{
		List<Map<String, Object>> entries = new LinkedList<Map<String, Object>>();

		for (JournalEntry journalEntry : journalDAO.listJournalEntries(search, start, end, ownerId, ownerType,
						sortColumnName, sortOrder, firstResult, resultsPerPage))
		{
			Map<String, Object> entry = new HashMap<String, Object>();

			addJournalEntryAttributes(journalEntry, entry);

			if (journalEntry.getExtendedInfoType() != null)
			{
				JournalExtender je = journalExtenders.get(journalEntry.getExtendedInfoType());

				if (je != null)
				{
					je.addJournalEntryAttributes(entry);
				}
			}

			entries.add(entry);
		}

		return entries;
	}

	public long countJournalEntries(String search, Timestamp start, Timestamp end, Integer ownerId, String ownerType)
	{
		return journalDAO.countJournalEntries(search, start, end, ownerId, ownerType);
	}

	public List<Map<String, Object>> listJournalEntriesByPrimaryAndSecondaryType(String search, Timestamp start,
					Timestamp end, Integer ownerId, String ownerType, String sortColumnName, SortOrder sortOrder,
					int firstResult, int resultsPerPage, String primaryType, String secondaryType)
	{
		List<Map<String, Object>> entries = new LinkedList<Map<String, Object>>();

		for (JournalEntry journalEntry : journalDAO.listJournalEntriesByPrimaryAndSecondaryType(search, start, end,
						ownerId, ownerType, sortColumnName, sortOrder, firstResult, resultsPerPage, primaryType,
						secondaryType))
		{
			Map<String, Object> entry = new HashMap<String, Object>();

			addJournalEntryAttributes(journalEntry, entry);

			if (journalEntry.getExtendedInfoType() != null)
			{
				JournalExtender je = journalExtenders.get(journalEntry.getExtendedInfoType());

				if (je != null)
				{
					je.addJournalEntryAttributes(entry);
				}
			}

			entries.add(entry);
		}

		return entries;
	}

	public long countJournalEntriesByPrimaryAndSecondaryType(String search, Timestamp start, Timestamp end,
					Integer ownerId, String ownerType, String primaryType, String secondaryType)
	{
		return journalDAO.countJournalEntriesByPrimaryAndSecondaryType(search, start, end, ownerId, ownerType,
						primaryType, secondaryType);
	}

	private void addJournalEntryAttributes(JournalEntry journalEntry, Map<String, Object> entry)
	{
		BeanTools.copyBean2Map(journalEntry, entry, true);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#deleteJournalEntry(int)
	 */
	public void deleteJournalEntry(int journalEntryId)
	{
		JournalEntry journalEntry = journalDAO.getById(journalEntryId);

		deleteJournalEntry(journalEntry);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#countJournalEntriesByCondition(int, int, java.lang.String, java.util.Map)
	 */
	public long countJournalEntriesByCondition(String condition, Map<String, Object> conditionMap)
	{
		return journalDAO.countJournalEntriesByCondition(condition, conditionMap);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#listJournalEntriesByCondition(java.lang.String, de.iritgo.simplelife.constants.SortOrder, int, int, java.lang.String, java.util.Map)
	 */
	public List<Map<String, Object>> listJournalEntriesByCondition(String sortColumnName, SortOrder sortOrder,
					int firstResult, int resultsPerPage, String condition, Map<String, Object> conditionMap)
	{
		List<Map<String, Object>> entries = new LinkedList<Map<String, Object>>();

		for (JournalEntry journalEntry : journalDAO.listJournalEntriesByCondition(sortColumnName, sortOrder,
						firstResult, resultsPerPage, condition, conditionMap))
		{
			Map<String, Object> entry = new HashMap<String, Object>();

			addJournalEntryAttributes(journalEntry, entry);

			if (journalEntry.getExtendedInfoType() != null)
			{
				JournalExtender je = journalExtenders.get(journalEntry.getExtendedInfoType());

				if (je != null)
				{
					je.addJournalEntryAttributes(entry);
				}
			}

			entries.add(entry);
		}

		return entries;
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#executeJournalEntry(int)
	 */
	public void executeJournalEntry(String commandId, int id, String prefix, ModelRequest req)
	{
		Map<String, Object> entry = getJournalEntryById(id);
		String primaryType = (String) entry.get("primaryType");
		String secondaryType = (String) entry.get("secondaryType");

		for (JournalExecute execute : journalExecuters)
		{
			execute.execute(commandId, primaryType, secondaryType, prefix, entry, req);
		}
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown() throws ShutdownException
	{
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalManager#deleteJournalAllEntries(java.lang.Integer)
	 */
	public void deleteJournalAllEntries(Integer ownerId)
	{
		List<JournalEntry> journalEntries = journalDAO.listJournalEntriesByOwnerId(ownerId);

		for (JournalExtender extender : journalExtenders.values())
		{
			extender.deleteAllJournalEntries (journalEntries);
		}

		Properties eventProps = new Properties();
		eventProps.setProperty ("ownerId", StringTools.trim(ownerId));

		eventManager.fire("iritgo.aktera.journal.remove-all-entries", eventProps);

		journalDAO.deleteAllJournalEntriesByOwnerId (ownerId);

		UserDAO userDAO = (UserDAO) SpringTools.getBean(UserDAO.ID);
		de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser userAktera =
				userDAO.findUserById (ownerId);

		Properties props = new Properties ();
		props.setProperty("akteraUserName", userAktera.getName());
		CommandTools.performAsync("de.iritgo.aktera.journal.RefreshJournal", props);
	}
}
