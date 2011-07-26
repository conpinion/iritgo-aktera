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
import de.iritgo.aktera.usergroupmgr.UserManager;
import de.iritgo.aktera.usergroupmgr.UserMgrException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;


/**
 * This is the description for <code>PersistUser</code>
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.usergroupmgr.User
 * @x-avalon.info name=persist-user
 * @x-avalon.lifestyle type=transient
 *
 * @author Shash Chatterjee
 * @date Feb 4, 2004
 * @version $Revision: 1.1 $ $Date: 2004/03/09 00:04:12 $
 */
public class PersistUser extends AbstractKeelServiceable implements User, LogEnabled
{
	protected Logger log = null;

	protected String name = null;

	protected String uid = null;

	protected String email = null;

	protected String password = null;

	/**
	 * @see de.iritgo.aktera.usergroupmgr.User#get(de.iritgo.aktera.usergroupmgr.User.Property)
	 */
	public Object get (Property property) throws UserMgrException
	{
		String value = null;

		if (property == Property.UID)
		{
			value = uid;
		}
		else if (property == Property.NAME)
		{
			value = name;
		}
		else if (property == Property.PASSWORD)
		{
			value = password;
		}
		else if (property == Property.EMAIL)
		{
			value = email;
		}
		else
		{
			log.debug ("Could not return value for property " + property);
		}

		return value;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.User#set(de.iritgo.aktera.usergroupmgr.User.Property, java.lang.Object)
	 */
	public boolean set (Property property, Object value) throws UserMgrException
	{
		if ((value != null) && ! (value instanceof String))
		{
			throw new UserMgrException ("Value for property " + property + " must be a String object, but is a"
							+ value.getClass ().getName ());
		}

		boolean status = true;

		if (value != null)
		{
			if (property == Property.UID)
			{
				uid = (String) value;
			}
			else if (property == Property.NAME)
			{
				name = (String) value;
			}
			else if (property == Property.PASSWORD)
			{
				password = (String) value;
			}
			else if (property == Property.EMAIL)
			{
				email = (String) value;
			}
			else
			{
				log.debug ("Could not set value for property " + property);
				status = false;
			}
		}

		return status;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.User#getUserManager()
	 */
	public UserManager getUserManager () throws UserMgrException
	{
		UserManager um;

		try
		{
			um = (UserManager) getService (UserManager.ROLE, "persist-user-manager");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException ("Error getting user manager service", e);
		}

		return um;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.Group#getGroupManager()
	 */
	protected GroupManager getGroupManager () throws UserMgrException
	{
		GroupManager um;

		try
		{
			um = (GroupManager) getService (GroupManager.ROLE, "persist-group-manager");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException ("Error getting group manager service", e);
		}

		return um;
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging (Logger logger)
	{
		log = logger;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.User#listGroups()
	 */
	public Group[] listGroups () throws UserMgrException
	{
		return getGroupManager ().listGroups (this);
	}
}
