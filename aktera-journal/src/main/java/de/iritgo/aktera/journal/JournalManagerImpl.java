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


import java.sql.*;
import java.util.*;
import com.ibm.icu.util.GregorianCalendar;
import lombok.Setter;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.event.EventManager;
import de.iritgo.aktera.journal.entity.JournalEntry;
import de.iritgo.aktera.logger.Logger;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.scheduler.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.startup.*;
import de.iritgo.simplelife.bean.BeanTools;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.string.StringTools;


public class JournalManagerImpl implements JournalManager, StartupHandler
{
	@Setter
	private JournalDAO journalDAO;

	@Setter
	private Logger logger;

	@Setter
	private Map<String, JournalExtender> journalExtenders;

	@Setter
	private List<JournalExecute> journalExecuters;

	@Setter
	private EventManager eventManager;

	@Setter
	private SystemConfigManager systemConfigManager;

	@Setter
	private Scheduler scheduler;

	@Override
	public void startup() throws StartupException
	{
		if (systemConfigManager.getBool("phone", "journalCleanupEnabled"))
		{
			Time cleanupTime = systemConfigManager.getTime("phone", "journalCleanupTime");
			int cleanupInterval = systemConfigManager.getInt("phone", "journalCleanupPeriod");
			logger.info("Starting Journal cleanup job (at " + cleanupTime + ", delete entries older than "
							+ cleanupInterval + " seconds)");
			GregorianCalendar cleanupTimeCal = new GregorianCalendar();
			cleanupTimeCal.setTime(cleanupTime);
			Runnable cleanupTask = new Runnable()
			{
				@Override
				public void run()
				{
					journalCleanup();
				}
			};
			scheduler.scheduleRunnable(
							"de.iritgo.aktera.journal.JournalCleanup",
							"JournalManager",
							cleanupTask,
							new ScheduleOn().hour(cleanupTimeCal.get(Calendar.HOUR_OF_DAY))
											.minute(cleanupTimeCal.get(Calendar.MINUTE)).second(0));
		}
	}

	@Override
	public void shutdown() throws ShutdownException
	{
	}

	@Override
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

	@Override
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
		UserDAO userDAO = (UserDAO) SpringTools.getBean(UserDAO.ID);
		de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser userAktera = userDAO.findUserById(journalEntry
						.getOwnerId());

		Properties props = new Properties();

		props.setProperty("akteraUserName", userAktera.getName());
		CommandTools.performAsync("de.iritgo.aktera.journal.RefreshJournal", props);
	}

	@Override
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

	@Override
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

	@Override
	public long countJournalEntries(String search, Timestamp start, Timestamp end, Integer ownerId, String ownerType)
	{
		return journalDAO.countJournalEntries(search, start, end, ownerId, ownerType);
	}

	@Override
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

	@Override
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

	@Override
	public void deleteJournalEntry(int journalEntryId)
	{
		JournalEntry journalEntry = journalDAO.getById(journalEntryId);

		deleteJournalEntry(journalEntry);
	}

	@Override
	public long countJournalEntriesByCondition(String condition, Map<String, Object> conditionMap)
	{
		return journalDAO.countJournalEntriesByCondition(condition, conditionMap);
	}

	@Override
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

	@Override
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

	@Override
	public void deleteJournalAllEntries(Integer ownerId)
	{
		List<JournalEntry> journalEntries = journalDAO.listJournalEntriesByOwnerId(ownerId);

		for (JournalExtender extender : journalExtenders.values())
		{
			extender.deleteAllJournalEntries(journalEntries);
		}

		Properties eventProps = new Properties();
		eventProps.setProperty("ownerId", StringTools.trim(ownerId));

		eventManager.fire("iritgo.aktera.journal.remove-all-entries", eventProps);

		journalDAO.deleteAllJournalEntriesByOwnerId(ownerId);

		UserDAO userDAO = (UserDAO) SpringTools.getBean(UserDAO.ID);
		de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser userAktera = userDAO.findUserById(ownerId);

		Properties props = new Properties();
		props.setProperty("akteraUserName", userAktera.getName());
		CommandTools.performAsync("de.iritgo.aktera.journal.RefreshJournal", props);
	}

	@Override
	public void deleteAllJournalEntriesBefore(long periodInSeconds)
	{
		for (JournalExtender extender : journalExtenders.values())
		{
			extender.deleteAllJournalEntriesBefore(periodInSeconds);
		}

		journalDAO.deleteAllJournalDatasBefore(periodInSeconds);
		journalDAO.deleteAllJournalEntriesBefore(periodInSeconds);
	}

	@Override
	public void journalCleanup()
	{
		int cleanupInterval = systemConfigManager.getInt("phone", "journalCleanupPeriod");
		if (cleanupInterval > 0)
		{
			logger.info("Cleaning CDR data older than " + cleanupInterval + " seconds)");
			deleteAllJournalEntriesBefore(cleanupInterval);
		}
	}
}
