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

package de.iritgo.aktera.usergroupmgr.persist;


import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.usergroupmgr.Group;
import de.iritgo.aktera.usergroupmgr.Group.Property;
import de.iritgo.aktera.usergroupmgr.GroupManager;
import de.iritgo.aktera.usergroupmgr.User;
import de.iritgo.aktera.usergroupmgr.UserManager;
import de.iritgo.aktera.usergroupmgr.UserMgrException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <Replace with description for PersistGroupManager>
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.usergroupmgr.GroupManager
 * @x-avalon.info name=persist-group-manager
 * @x-avalon.lifestyle type=singleton
 *
 * @version        $Revision: 1.2 $        $Date: 2004/03/09 05:17:10 $
 * @author SChatterjee
 * @date Feb 8, 2004
 */
public class PersistGroupManager extends AbstractKeelServiceable implements GroupManager, LogEnabled
{
	private static final String FLD_DESCRIPTION = "descrip";

	private static final String FLD_UID = "uid";

	private static final String FLD_GROUPNAME = "groupname";

	protected Logger log = null;

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#find(de.iritgo.aktera.usergroupmgr.Group.Property, java.lang.Object)
	 */
	public Group find(Property property, Object value) throws UserMgrException
	{
		Group group = null;

		try
		{
			Persistent g = getGroupPersistent();

			if (property == Group.Property.NAME)
			{
				g.setField(FLD_GROUPNAME, value);
			}
			else if (property == Group.Property.DESCRIPTION)
			{
				g.setField(FLD_DESCRIPTION, value);
			}
			else
			{
				log.warn("Don't know how to find using property " + property);
			}

			if (g.find())
			{
				group = createGroupFromPersistent(g);
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while finding user", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Internal error getting user service while finding user", e);
		}

		return group;
	}

	/**
	 * @return
	 */
	private Persistent getGroupPersistent() throws UserMgrException
	{
		PersistentFactory pf = null;
		Persistent myGroup = null;

		try
		{
			pf = (PersistentFactory) getService(PersistentFactory.ROLE);
			myGroup = pf.create("keel.usergroup");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException(e);
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException(e);
		}

		return myGroup;
	}

	/**
	 * @param g
	 * @return
	 */
	private Group createGroupFromPersistent(Persistent g)
		throws ServiceException, UserMgrException, PersistenceException
	{
		Group group = (Group) getService(Group.ROLE, "persist-group");

		group.set(Group.Property.NAME, g.getFieldString(FLD_GROUPNAME));
		group.set(Group.Property.DESCRIPTION, g.getFieldString(FLD_DESCRIPTION));

		return group;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#list()
	 */
	public Group[] list() throws UserMgrException
	{
		ArrayList<Group> groupList = new ArrayList<Group>(0);

		try
		{
			Persistent g = getGroupPersistent();

			//g.setField(FLD_GID", group.get(Group.Property.GID));
			List<Persistent> groups = g.query();

			groupList = new ArrayList<Group>(groups.size());

			Iterator<Persistent> i = groups.iterator();

			while (i.hasNext())
			{
				g = (Persistent) i.next();

				Group nextGroup = createGroupFromPersistent(g);

				groupList.add(nextGroup);
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while adding group", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Internal error getting user service while adding group", e);
		}

		Group[] type =
		{};

		return (Group[]) groupList.toArray(type);
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#add(de.iritgo.aktera.usergroupmgr.Group)
	 */
	public Group add(Group group) throws UserMgrException
	{
		Group newGroup = group;

		try
		{
			Persistent g = getGroupPersistent();

			//g.setField(FLD_GID, group.get(Group.Property.GID));
			g.setField(FLD_GROUPNAME, group.get(Group.Property.NAME));
			g.setField(FLD_DESCRIPTION, group.get(Group.Property.DESCRIPTION));
			g.add();
			newGroup = createGroupFromPersistent(g);
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while adding group", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Internal error getting user service while adding group", e);
		}

		return newGroup;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#delete(de.iritgo.aktera.usergroupmgr.Group)
	 */
	public boolean delete(Group group) throws UserMgrException
	{
		try
		{
			Persistent g = getGroupPersistent();

			g.setField(FLD_GROUPNAME, group.get(Group.Property.NAME));
			g.setField(FLD_DESCRIPTION, group.get(Group.Property.DESCRIPTION));

			if (g.find())
			{
				g.delete();
			}
			else
			{
				throw new UserMgrException("Cannot delete, group not found");
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while deleting group", e);
		}

		return true;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#update(de.iritgo.aktera.usergroupmgr.Group)
	 */
	public boolean update(Group group) throws UserMgrException
	{
		try
		{
			Persistent g = getGroupPersistent();

			g.setField(FLD_GROUPNAME, group.get(Group.Property.NAME));

			if (g.find())
			{
				g.setField(FLD_DESCRIPTION, group.get(Group.Property.DESCRIPTION));
				g.update();
			}
			else
			{
				throw new UserMgrException("Cannot update, group not found");
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while updating group", e);
		}

		return true;
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger logger)
	{
		log = logger;
	}

	/**
	 * @return
	 */
	private Persistent getGroupMembersPersistent() throws UserMgrException
	{
		PersistentFactory pf = null;
		Persistent myGroupMembers = null;

		try
		{
			pf = (PersistentFactory) getService(PersistentFactory.ROLE);
			myGroupMembers = pf.create("keel.groupmembers");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException(e);
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException(e);
		}

		return myGroupMembers;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#listUsers(de.iritgo.aktera.usergroupmgr.Group, de.iritgo.aktera.usergroupmgr.User)
	 */
	public User[] listUsers(Group group) throws UserMgrException
	{
		ArrayList<User> userList = new ArrayList<User>(0);

		try
		{
			Persistent gm = getGroupMembersPersistent();

			gm.setField(FLD_GROUPNAME, group.get(Group.Property.NAME));

			List<Persistent> groupmembers = gm.query();

			userList = new ArrayList<User>(groupmembers.size());

			Iterator<Persistent> i = groupmembers.iterator();

			while (i.hasNext())
			{
				gm = (Persistent) i.next();

				User nextUser = getUserManager().find(User.Property.UID, gm.getField(FLD_UID));

				userList.add(nextUser);
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while listing users", e);
		}

		User[] type =
		{};

		return (User[]) userList.toArray(type);
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#addUser(de.iritgo.aktera.usergroupmgr.Group, de.iritgo.aktera.usergroupmgr.User)
	 */
	public boolean addUser(Group group, User user) throws UserMgrException
	{
		Persistent gm = getGroupMembersPersistent();

		try
		{
			gm.setField(FLD_GROUPNAME, group.get(Group.Property.NAME));
			gm.setField(FLD_UID, user.get(User.Property.UID));
			gm.add();
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error adding user to group", e);
		}

		return true;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#deleteUser(de.iritgo.aktera.usergroupmgr.Group, de.iritgo.aktera.usergroupmgr.User)
	 */
	public boolean deleteUser(Group group, User user) throws UserMgrException
	{
		Persistent gm = getGroupMembersPersistent();

		try
		{
			gm.setField(FLD_GROUPNAME, group.get(Group.Property.NAME));
			gm.setField(FLD_UID, user.get(User.Property.UID));
			gm.delete();
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error deleting user from group", e);
		}

		return true;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#listGroups(de.iritgo.aktera.usergroupmgr.User)
	 */
	public Group[] listGroups(User user) throws UserMgrException
	{
		ArrayList<Group> groupList = new ArrayList<Group>(0);

		try
		{
			Persistent gm = getGroupMembersPersistent();

			gm.setField(FLD_UID, user.get(User.Property.UID));

			List<Persistent> groupmembers = gm.query();

			Persistent g = getGroupPersistent();

			groupList = new ArrayList<Group>(groupmembers.size());

			Iterator<Persistent> i = groupmembers.iterator();

			while (i.hasNext())
			{
				gm = (Persistent) i.next();
				g.clear();
				g.setField(FLD_GROUPNAME, gm.getField(FLD_GROUPNAME));

				if (g.find())
				{
					Group nextGroup = createGroupFromPersistent(g);

					groupList.add(nextGroup);
				}
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException("Error from underlying persistence engine while listing groups", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Internal error getting user service while listing groups", e);
		}

		Group[] type =
		{};

		return (Group[]) groupList.toArray(type);
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.User#getUserManager()
	 */
	public UserManager getUserManager() throws UserMgrException
	{
		UserManager um;

		try
		{
			um = (UserManager) getService(UserManager.ROLE, "persist-user-manager");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Error getting user manager service", e);
		}

		return um;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.GroupManager#createGroup(java.lang.String, java.lang.String)
	 */
	public Group createGroup(String name, String description) throws UserMgrException
	{
		Group group;

		try
		{
			group = (Group) getService(Group.ROLE, "persist-group");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Error getting group service from container", e);
		}

		group.set(Group.Property.NAME, name);
		group.set(Group.Property.DESCRIPTION, description);

		return group;
	}
}
