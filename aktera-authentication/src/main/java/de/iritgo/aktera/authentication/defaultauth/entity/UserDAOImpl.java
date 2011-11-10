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

package de.iritgo.aktera.authentication.defaultauth.entity;


import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import de.iritgo.simplelife.data.Tuple2;
import de.iritgo.simplelife.math.NumberTools;


/**
 * User DAO implementation.
 */
@Transactional(readOnly = true)
public class UserDAOImpl extends HibernateDaoSupport implements UserDAO, UserDetailsService
{
	static public String ID = "keel.UserDAO";

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findUserByName(java.lang.String)
	 */
	public AkteraUser findUserByName(String name)
	{
		List<AkteraUser> res = getHibernateTemplate().find("from AkteraUser where name = ?", name);
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findUserById(java.lang.Integer)
	 */
	public AkteraUser findUserById(Integer id)
	{
		return (AkteraUser) getHibernateTemplate().get(AkteraUser.class, id);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findAllUsers()
	 */
	public List<AkteraUser> findAllUsers()
	{
		return getHibernateTemplate().find("from AkteraUser");
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#listUsersOverview()
	 */
	public List<Tuple2<Integer, String>> listUsersOverview()
	{
		return getHibernateTemplate().find("select new de.iritgo.simplelife.data.Tuple2 (uid, name ) from AkteraUser");
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#listGroupIdsOfUserId(java.lang.Integer)
	 */
	public List<Integer> listGroupIdsOfUserId(Integer userId)
	{
		return getHibernateTemplate().find("select groupId from AkteraGroupEntry where userId = ?", userId);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#updateUser(de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser)
	 */
	@Transactional(readOnly = false)
	public void updateUser(AkteraUser user)
	{
		getHibernateTemplate().update(user);
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
	{
		AkteraUser user = findUserByName(username);
		if (user == null)
		{
			throw new UsernameNotFoundException("User " + username + " not found");
		}
		return new UserDetailsImpl(user.getName(), user.getPassword());
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findAllGroups()
	 */
	public List<AkteraGroup> findAllGroups()
	{
		return getHibernateTemplate().find("from AkteraGroup");
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findGroupById(java.lang.Integer)
	 */
	public AkteraGroup findGroupById(Integer id)
	{
		List<AkteraGroup> res = getHibernateTemplate().find("from AkteraGroup where id = ?", id);
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findGroupByName(java.lang.String)
	 */
	public AkteraGroup findGroupByName(String name)
	{
		List<AkteraGroup> res = getHibernateTemplate().find("from AkteraGroup where name = ?", name);
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findGroupsByUser(de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser)
	 */
	public List<AkteraGroup> findGroupsByUser(AkteraUser user)
	{
		HibernateTemplate htl = getHibernateTemplate();
		List<AkteraGroup> groups = new LinkedList();
		List<AkteraGroupEntry> entries = htl.find("from AkteraGroupEntry where userId = ?", user.getId());

		for (AkteraGroupEntry entry : entries)
		{
			AkteraGroup group = (AkteraGroup) htl.get(AkteraGroup.class, entry.getGroupId());

			groups.add(group);
		}

		return groups;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findUsersByGroup(de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroup)
	 */
	public List<AkteraUser> findUsersByGroup(AkteraGroup group)
	{
		HibernateTemplate htl = getHibernateTemplate();
		List<AkteraUser> users = new LinkedList();
		List<AkteraGroupEntry> entries = htl.find("from AkteraGroupEntry where groupId = ?", group.getId());
		for (AkteraGroupEntry entry : entries)
		{
			AkteraUser user = (AkteraUser) htl.get(AkteraUser.class, entry.getUserId());

			users.add(user);
		}
		return users;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#createUser(de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser)
	 */
	@Transactional(readOnly = false)
	public void createUser(AkteraUser user)
	{
		getHibernateTemplate().saveOrUpdate(user);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#createGroupMember(de.iritgo.aktera.authentication.defaultauth.entity.GroupMembers)
	 */
	@Transactional(readOnly = false)
	public void createGroupMember(GroupMembers groupMembers)
	{
		getHibernateTemplate().saveOrUpdate(groupMembers);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#userHasRole(de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser, java.lang.String)
	 */
	public boolean userHasRole(AkteraUser user, String role)
	{
		return getHibernateTemplate().find("from GroupMembers where uid = ? and groupName = ?", new Object[]
		{
						user.getId(), role
		}).size() > 0;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#countNotSystemUsers()
	 */
	public long countNonSystemUsers()
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery("select count (*) from AkteraUser where"
								+ " name <> 'anonymous' and name <> 'admin' and name <> 'manager'");
				return query.uniqueResult();
			}
		});
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#createAkteraGroupEntry(de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroupEntry)
	 */
	@Transactional(readOnly = false)
	public void createAkteraGroupEntry(AkteraGroupEntry entry)
	{
		int maxPos = NumberTools.toInt(getHibernateTemplate().find(
						"select max(position) from AkteraGroupEntry where groupId = ?", entry.getGroupId()).get(0), 0);
		entry.setPosition(maxPos + 1);
		getHibernateTemplate().save(entry);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findAkteraGroupEntryById(java.lang.Integer)
	 */
	public AkteraGroupEntry findAkteraGroupEntryById(Integer id)
	{
		return (AkteraGroupEntry) getHibernateTemplate().get(AkteraGroupEntry.class, id);
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#findAkteraGroupEntryByUserIdAndGroupId(java.lang.Integer, java.lang.Integer)
	 */
	public AkteraGroupEntry findAkteraGroupEntryByUserIdAndGroupId(Integer userId, Integer groupId)
	{
		List<AkteraGroupEntry> res = getHibernateTemplate().find(
						"from AkteraGroupEntry where userId = ? and groupId = ?", new Object[]
						{
										userId, groupId
						});
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#deleteAkteraGroupEntry(de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroupEntry)
	 */
	@Transactional(readOnly = false)
	public void deleteAkteraGroupEntry(AkteraGroupEntry entry)
	{
		getHibernateTemplate().delete(entry);
		for (AkteraGroupEntry otherEntry : (List<AkteraGroupEntry>) getHibernateTemplate().find(
						"from AkteraGroupEntry where groupId = ?", entry.getGroupId()))
		{
			if (otherEntry.getPosition() > entry.getPosition())
			{
				otherEntry.setPosition(otherEntry.getPosition() - 1);
				getHibernateTemplate().update(otherEntry);
			}
		}

	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#deleteAkteraGroupEntriesOfUser(int)
	 */
	@Transactional(readOnly = false)
	public void deleteAkteraGroupEntriesByUserId(int userId)
	{
		for (AkteraGroupEntry entry : (List<AkteraGroupEntry>) getHibernateTemplate().find(
						"from AkteraGroupEntry where userId = ?", userId))
		{
			deleteAkteraGroupEntry(entry);
		}
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#moveDownAkteraGroupEntry(de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroupEntry)
	 */
	@Transactional(readOnly = false)
	public void moveDownAkteraGroupEntry(AkteraGroupEntry entry)
	{
		int maxPos = (Integer) getHibernateTemplate().find(
						"select max(position) from AkteraGroupEntry where groupId = ?", entry.getGroupId()).get(0);
		if (entry.getPosition() < maxPos)
		{
			AkteraGroupEntry nextEntry = (AkteraGroupEntry) getHibernateTemplate().find(
							"from AkteraGroupEntry where groupId = ? and position = ?", new Object[]
							{
											entry.getGroupId(), entry.getPosition() + 1
							}).get(0);
			nextEntry.setPosition(entry.getPosition());
			getHibernateTemplate().update(nextEntry);
			entry.setPosition(entry.getPosition() + 1);
			getHibernateTemplate().update(entry);
		}
	}

	/**
	 * @see de.iritgo.aktera.authentication.defaultauth.entity.UserDAO#moveUpAkteraGroupEntry(de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroupEntry)
	 */
	@Transactional(readOnly = false)
	public void moveUpAkteraGroupEntry(AkteraGroupEntry entry)
	{
		if (entry.getPosition() > 1)
		{
			AkteraGroupEntry prevEntry = (AkteraGroupEntry) getHibernateTemplate().find(
							"from AkteraGroupEntry where groupId = ? and position = ?", new Object[]
							{
											entry.getGroupId(), entry.getPosition() - 1
							}).get(0);
			prevEntry.setPosition(entry.getPosition());
			getHibernateTemplate().update(prevEntry);
			entry.setPosition(entry.getPosition() - 1);
			getHibernateTemplate().update(entry);
		}
	}
}
