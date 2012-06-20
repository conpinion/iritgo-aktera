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
import org.hibernate.*;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import de.iritgo.aktera.journal.entity.*;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.string.StringTools;


@Transactional(readOnly = true)
public class JournalDAOImpl extends HibernateDaoSupport implements JournalDAO
{
	/**
	 * @see de.iritgo.aktera.journal.JournalDAO#create(de.iritgo.aktera.journal.entity.JournalEntry)
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(JournalEntry journal)
	{
		getHibernateTemplate().save(journal);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalDAO#delete(de.iritgo.aktera.journal.entity.JournalEntry)
	 */
	@Transactional(readOnly = false)
	@Override
	public void delete(JournalEntry journal)
	{
		getHibernateTemplate().delete(journal);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalDAO#getById(java.lang.Integer)
	 */
	@Override
	public JournalEntry getById(Integer id)
	{
		return (JournalEntry) getHibernateTemplate().get(JournalEntry.class, id);
	}

	/**
	 * @see de.iritgo.aktera.journal.JournalDAO#update(de.iritgo.aktera.journal.entity.JournalEntry)
	 */
	@Transactional(readOnly = false)
	@Override
	public void update(JournalEntry journal)
	{
		getHibernateTemplate().update(journal);
	}

	@Override
	public List<JournalEntry> listJournalEntries(final String search, final Timestamp start, final Timestamp end,
					final Integer ownerId, String ownerType, String sortColumnName, SortOrder sortOrder,
					final int firstResult, final int resultsPerPage)
	{
		return (List<JournalEntry>) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.getNamedQuery("de.iritgo.aktera.journal.JournalEntryList");
				query.setParameter("ownerId", ownerId);
				query.setParameter("start", start);
				query.setParameter("end", end);
				query.setParameter("search", "%" + StringTools.toLowerCase(search) + "%");
				query.setMaxResults(resultsPerPage);
				query.setFirstResult(firstResult);
				return query.list();
			}
		});
	}

	@Override
	public long countJournalEntries(final String search, final Timestamp start, final Timestamp end,
					final Integer ownerId, String ownerType)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.getNamedQuery("de.iritgo.aktera.journal.JournalEntryCount");
				query.setParameter("ownerId", ownerId);
				query.setParameter("start", start);
				query.setParameter("end", end);
				query.setParameter("search", "%" + StringTools.toLowerCase(search) + "%");
				return query.uniqueResult();
			}
		});
	}

	@Override
	public List<JournalEntry> listJournalEntriesByPrimaryAndSecondaryType(String search, final Timestamp start,
					final Timestamp end, final Integer ownerId, String ownerType, String sortColumnName,
					SortOrder sortOrder, final int firstResult, final int resultsPerPage, final String primaryType,
					final String secondaryType)
	{
		return (List<JournalEntry>) getHibernateTemplate().execute(new HibernateCallback()
		{
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session
								.createQuery("from JournalEntry where occurredAt between :start and :end and ownerId = :ownerId and "
												+ primaryType + " and " + secondaryType + " order by occurredAt desc");
				query.setParameter("ownerId", ownerId);
				query.setParameter("start", start);
				query.setParameter("end", end);
				query.setMaxResults(resultsPerPage);
				query.setFirstResult(firstResult);
				return query.list();
			}
		});
	}

	@Override
	public long countJournalEntriesByPrimaryAndSecondaryType(String search, final Timestamp start, final Timestamp end,
					final Integer ownerId, String ownerType, final String primaryType, final String secondaryType)
	{
		return getHibernateTemplate().find(
						"from JournalEntry where occurredAt between ? and ? and ownerId = ? and  " + primaryType
										+ " and " + secondaryType, new Object[]
						{
										start, end, ownerId
						}).size();
	}

	@Override
	public void createJournalData(JournalData journalData)
	{
		getHibernateTemplate().save(journalData);
	}

	@Override
	public void deleteJournalData(JournalData journalData)
	{
		getHibernateTemplate().delete(journalData);
	}

	@Override
	public JournalData getJournalDataById(Integer id)
	{
		return (JournalData) getHibernateTemplate().get(JournalData.class, id);
	}

	@Override
	public void updateJournalData(JournalData journalData)
	{
		getHibernateTemplate().update(journalData);
	}

	@Override
	public JournalEntry findEntryByTag(String tag)
	{
		List<JournalEntry> res = getHibernateTemplate().find("from JournalEntry where tags = ?", tag);
		return res.size() > 0 ? res.get(0) : null;
	}

	@Override
	public List<JournalEntry> listJournalEntriesByCondition(String sortColumnName, SortOrder sortOrder,
					final int firstResult, final int resultsPerPage, final String condition,
					final Map<String, Object> conditionMap)
	{
		return (List<JournalEntry>) getHibernateTemplate().execute(new HibernateCallback()
		{
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery("from JournalEntry where " + condition + " order by occurredAt desc");
				for (String key : conditionMap.keySet())
				{
					query.setParameter(key, conditionMap.get(key));
				}
				query.setMaxResults(resultsPerPage);
				query.setFirstResult(firstResult);
				return query.list();
			}
		});
	}

	@Override
	public long countJournalEntriesByCondition(final String condition, final Map<String, Object> conditionMap)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery("select count(*) from JournalEntry entry where " + condition);
				for (String key : conditionMap.keySet())
				{
					query.setParameter(key, conditionMap.get(key));
				}
				return query.uniqueResult();
			}
		});
	}

	@Override
	public List<JournalEntry> listJournalEntriesByOwnerId(Integer ownerId)
	{
		return getHibernateTemplate().find("from JournalEntry where ownerId = ?", ownerId);
	}

	@Override
	public JournalEntry findEntryByMisc(String tag)
	{
		List<JournalEntry> res = getHibernateTemplate().find("from JournalEntry where misc = ?", tag);
		return res.size() > 0 ? res.get(0) : null;
	}

	@Override
	public JournalEntry getByExtendedInfoTypeAndExtendedInfoId(String type, Integer id)
	{
		List<JournalEntry> res = getHibernateTemplate().find(
						"from JournalEntry where extendedInfoType = ? AND extendedInfoId = ? ", new Object[]
						{
										type, id
						});
		return res.size() > 0 ? res.get(0) : null;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllJournalEntriesByOwnerId(Integer ownerId)
	{
		getHibernateTemplate().bulkUpdate("delete from JournalEntry where ownerId = ?", ownerId);
	}

	@Transactional(readOnly = false)
	@Override
	public void deleteJournalDataById(Integer id)
	{
		getHibernateTemplate().bulkUpdate("delete from JournalData where id = ?", id);
	}

	@Override
	public void deleteAllJournalDataByJournalEntries(
			List<JournalEntry> journalEntries) 
	{
		for (JournalEntry entry : journalEntries)
		{
			if (entry.getExtendedInfoId() != null)
			{
				deleteJournalDataById(entry.getExtendedInfoId());
			}
		}
	}
	
	@Transactional(readOnly = false)
	@Override
	public void deleteAllJournalEntriesBefore(final long periodInSeconds)
	{
		Time time= new Time(System.currentTimeMillis() - periodInSeconds * 1000);
		getHibernateTemplate().bulkUpdate("delete from JournalEntry where occurredAt < ?",
						time);		
	}	

	@Transactional(readOnly = false)
	@Override
	public void deleteAllJournalDatasBefore(final long periodInSeconds)
	{
		Time time= new Time(System.currentTimeMillis() - periodInSeconds * 1000);
		getHibernateTemplate().bulkUpdate("delete from JournalData where occurredAt < ?",
						time);
	}	
}
