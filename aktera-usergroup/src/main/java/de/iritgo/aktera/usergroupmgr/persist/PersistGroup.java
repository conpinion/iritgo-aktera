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
import de.iritgo.aktera.usergroupmgr.Group;
import de.iritgo.aktera.usergroupmgr.GroupManager;
import de.iritgo.aktera.usergroupmgr.User;
import de.iritgo.aktera.usergroupmgr.UserMgrException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;


/**
 * <Replace with description for PersistGroup>
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.usergroupmgr.Group
 * @x-avalon.info name=persist-group
 * @x-avalon.lifestyle type=transient
 *
 * @version        $Revision: 1.1 $        $Date: 2004/03/09 00:04:12 $
 * @author SChatterjee
 * @date Feb 8, 2004
 */
public class PersistGroup extends AbstractKeelServiceable implements Group, LogEnabled
{
	protected Logger log = null;

	protected String name = null;

	protected String description = null;

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#get(de.iritgo.aktera.usergroupmgr.Group.Property)
	 */
	public Object get(Property property) throws UserMgrException
	{
		String value = null;

		if (property == Property.NAME)
		{
			value = name;
		}
		else if (property == Property.DESCRIPTION)
		{
			value = description;
		}
		else
		{
			log.debug("Could not return value for property " + property);
		}

		return value;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#set(de.iritgo.aktera.usergroupmgr.Group.Property, java.lang.Object)
	 */
	public boolean set(Property property, Object value) throws UserMgrException
	{
		if (! (value instanceof String))
		{
			throw new UserMgrException("Value for property " + property + " must be a String object, but is a"
							+ value.getClass().getName());
		}

		boolean status = true;

		if (property == Property.NAME)
		{
			name = (String) value;
		}
		else if (property == Property.DESCRIPTION)
		{
			description = (String) value;
		}
		else
		{
			log.debug("Could not set value for property " + property);
			status = false;
		}

		return status;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#getGroupManager()
	 */
	public GroupManager getGroupManager() throws UserMgrException
	{
		GroupManager um;

		try
		{
			um = (GroupManager) getService(GroupManager.ROLE, "persist-group-manager");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException("Error getting group manager service", e);
		}

		return um;
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger logger)
	{
		log = logger;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#listUsers(de.iritgo.aktera.usergroupmgr.User)
	 */
	public User[] listUsers() throws UserMgrException
	{
		return getGroupManager().listUsers(this);
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#addUser(de.iritgo.aktera.usergroupmgr.User)
	 */
	public boolean addUser(User user) throws UserMgrException
	{
		return getGroupManager().addUser(this, user);
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#deleteUser(de.iritgo.aktera.usergroupmgr.User)
	 */
	public boolean deleteUser(User user) throws UserMgrException
	{
		return getGroupManager().deleteUser(this, user);
	}
}
