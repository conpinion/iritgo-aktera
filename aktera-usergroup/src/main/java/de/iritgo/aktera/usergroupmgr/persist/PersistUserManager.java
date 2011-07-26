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
import de.iritgo.aktera.usergroupmgr.User;
import de.iritgo.aktera.usergroupmgr.User.Property;
import de.iritgo.aktera.usergroupmgr.UserManager;
import de.iritgo.aktera.usergroupmgr.UserMgrException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This is the description for <code>PersistUserManager</code>
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.usergroupmgr.UserManager
 * @x-avalon.info name=persist-user-manager
 * @x-avalon.lifestyle type=singleton
 *
 * @author Shash Chatterjee @date Feb 4, 2004
 * @version $Revision: 1.1 $ $Date: 2004/03/09 00:04:12 $
 */
public class PersistUserManager extends AbstractKeelServiceable implements UserManager, LogEnabled
{
	private static final String FLD_EMAIL = "email";

	private static final String FLD_PASSWORD = "password";

	private static final String FLD_NAME = "name";

	private static final String FLD_UID = "uid";

	protected Logger log = null;

	/**
	 * @see de.iritgo.aktera.usergroupmgr.UserManager#find(de.iritgo.aktera.usergroupmgr.User.Property,
	 *      java.lang.Object)
	 */
	public User find (Property property, Object value) throws UserMgrException
	{
		User user = null;

		try
		{
			Persistent u = getUserPersistent ();

			if (property == User.Property.UID)
			{
				u.setField (FLD_UID, value);
			}
			else if (property == User.Property.NAME)
			{
				u.setField (FLD_NAME, value);
			}
			else if (property == User.Property.PASSWORD)
			{
				u.setField (FLD_PASSWORD, value);
			}
			else if (property == User.Property.EMAIL)
			{
				u.setField (FLD_EMAIL, value);
			}
			else
			{
				log.warn ("Don't know how to find using property " + property);
			}

			if (u.find ())
			{
				user = createUserFromPersistent (u);
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException ("Error from underlying persistence engine while finding user", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException ("Internal error getting user service while finding user", e);
		}

		return user;
	}

	/**
	 * @param u
	 * @return
	 * @throws ServiceException
	 * @throws UserMgrException
	 * @throws PersistenceException
	 */
	private User createUserFromPersistent (Persistent u)
		throws ServiceException, UserMgrException, PersistenceException
	{
		User user = (User) getService (User.ROLE, "persist-user");

		user.set (User.Property.UID, u.getFieldString (FLD_UID));
		user.set (User.Property.NAME, u.getFieldString (FLD_NAME));
		user.set (User.Property.PASSWORD, u.getFieldString (FLD_PASSWORD));
		user.set (User.Property.EMAIL, u.getFieldString (FLD_EMAIL));

		return user;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.UserManager#list()
	 */
	public User[] list () throws UserMgrException
	{
		ArrayList<User> userList = new ArrayList<User> (0);

		try
		{
			Persistent u = getUserPersistent ();

			//u.setField(FLD_UID, user.get(User.Property.UID));
			List<Persistent> users = u.query ();

			userList = new ArrayList<User> (users.size ());

			Iterator<Persistent> i = users.iterator ();

			while (i.hasNext ())
			{
				u = (Persistent) i.next ();

				User nextUser = createUserFromPersistent (u);

				userList.add (nextUser);
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException ("Error from underlying persistence engine while adding user", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException ("Internal error getting user service while adding user", e);
		}

		User[] type =
		{};

		return (User[]) userList.toArray (type);
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.UserManager#add(de.iritgo.aktera.usergroupmgr.User)
	 */
	public User add (User user) throws UserMgrException
	{
		User newUser = user;

		try
		{
			Persistent u = getUserPersistent ();

			//u.setField(FLD_UID, user.get(User.Property.UID));
			u.setField (FLD_NAME, user.get (User.Property.NAME));
			u.setField (FLD_PASSWORD, user.get (User.Property.PASSWORD));
			u.setField (FLD_EMAIL, user.get (User.Property.EMAIL));
			u.add ();
			u.find ();
			newUser = createUserFromPersistent (u);
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException ("Error from underlying persistence engine while adding user", e);
		}
		catch (ServiceException e)
		{
			throw new UserMgrException ("Internal error getting user service while adding user", e);
		}

		return newUser;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.UserManager#delete(de.iritgo.aktera.usergroupmgr.User)
	 */
	public boolean delete (User user) throws UserMgrException
	{
		try
		{
			Persistent u = getUserPersistent ();

			u.setField (FLD_NAME, user.get (User.Property.NAME));
			u.setField (FLD_PASSWORD, user.get (User.Property.PASSWORD));
			u.setField (FLD_EMAIL, user.get (User.Property.EMAIL));

			if (u.find ())
			{
				u.delete ();
			}
			else
			{
				throw new UserMgrException ("Cannot delete, user not found");
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException ("Error from underlying persistence engine while deleting user", e);
		}

		return true;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.UserManager#update(de.iritgo.aktera.usergroupmgr.User)
	 */
	public boolean update (User user) throws UserMgrException
	{
		try
		{
			Persistent u = getUserPersistent ();
			String uidString = (String) user.get (User.Property.UID);

			if ((uidString == null) || uidString.trim ().equals (""))
			{
				throw new UserMgrException ("Cannot update, no uid specified");
			}

			u.setField (FLD_UID, uidString);

			if (u.find ())
			{
				u.setField (FLD_NAME, user.get (User.Property.NAME));
				u.setField (FLD_PASSWORD, user.get (User.Property.PASSWORD));
				u.setField (FLD_EMAIL, user.get (User.Property.EMAIL));
				u.update ();
			}
			else
			{
				throw new UserMgrException ("Cannot update, user not found");
			}
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException ("Error from underlying persistence engine while updating user", e);
		}

		return true;
	}

	protected Persistent getUserPersistent () throws UserMgrException
	{
		PersistentFactory pf = null;
		Persistent myUser = null;

		try
		{
			pf = (PersistentFactory) getService (PersistentFactory.ROLE);
			myUser = pf.create ("keel.user");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException (e);
		}
		catch (PersistenceException e)
		{
			throw new UserMgrException (e);
		}

		return myUser;
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging (Logger logger)
	{
		log = logger;
	}

	/**
	 * @see de.iritgo.aktera.usergroupmgr.UserManager#createUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public User createUser (String name, String password, String email) throws UserMgrException
	{
		User user;

		try
		{
			user = (User) getService (User.ROLE, "persist-user");
		}
		catch (ServiceException e)
		{
			throw new UserMgrException ("Error getting user service from container", e);
		}

		user.set (User.Property.NAME, name);
		user.set (User.Property.PASSWORD, password);
		user.set (User.Property.EMAIL, email);

		return user;
	}
}
