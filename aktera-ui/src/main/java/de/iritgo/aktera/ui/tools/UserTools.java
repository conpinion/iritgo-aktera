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

package de.iritgo.aktera.ui.tools;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.configuration.preferences.Preferences;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.permissions.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import java.util.Iterator;


/**
 * User management utility methods.
 */
public class UserTools
{
	/** Aktera context data key. */
	public static final String AKTERA_CONTEXT_KEY = "Aktera";

	/**
	 * Get the current user id from the model request context.
	 *
	 * @param req The model request.
	 * @return The user id or null if no user exists in the current session.
	 */
	public static Integer getCurrentUserId (ModelRequest req)
	{
		try
		{
			Context context = req.getContext ();

			UserEnvironment userEnv = (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null && userEnv.getUid () != UserEnvironment.ANONYMOUS_UID)
			{
				return new Integer (userEnv.getUid ());
			}
			else
			{
				return null;
			}
		}
		catch (ContextException x)
		{
			return null;
		}
		catch (ModelException x)
		{
			return null;
		}
		catch (AuthorizationException x)
		{
			return null;
		}
	}

	/**
	 * Get the current user's system name.
	 *
	 * @param req The model request.
	 * @return The user's system name null if no user exists in the current
	 *         session.
	 */
	public static String getCurrentUserName (ModelRequest req)
	{
		try
		{
			Context context = req.getContext ();
			UserEnvironment userEnv = (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null && userEnv.getUid () != UserEnvironment.ANONYMOUS_UID)
			{
				return userEnv.getLoginName ();
			}
			else
			{
				return null;
			}
		}
		catch (AuthorizationException x)
		{
			return null;
		}
		catch (ContextException x)
		{
			return null;
		}
		catch (ModelException x)
		{
			return null;
		}
	}

	/**
	 * Get an object that is stored in the user environment.
	 *
	 * @param req The model request.
	 * @param key Key of the object to retrieve.
	 * @return The environment object or null.
	 */
	public static Object getUserEnvObject (ModelRequest req, String key)
	{
		try
		{
			Context context = req.getContext ();

			UserEnvironment userEnv = (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null)
			{
				return userEnv.getAttribute (key);
			}

			return null;
		}
		catch (ContextException x)
		{
			return null;
		}
		catch (ModelException x)
		{
			return null;
		}
	}

	/**
	 * Store an object in the user environment.
	 *
	 * @param req The model request.
	 * @param key Key of the object to store.
	 * @param val The object to store.
	 */
	public static void setUserEnvObject (ModelRequest req, String key, Object val)
	{
		try
		{
			Context context = req.getContext ();
			UserEnvironment userEnv = (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null)
			{
				userEnv.setAttribute (key, val);
			}
		}
		catch (ContextException x)
		{
		}
		catch (ModelException x)
		{
		}
	}

	/**
	 * Remove an object from the user environment.
	 *
	 * @param req The model request.
	 * @param key Key of the object to remove.
	 */
	public static void removeUserEnvObject (ModelRequest req, String key)
	{
		try
		{
			Context context = req.getContext ();
			UserEnvironment userEnv = (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null)
			{
				userEnv.removeAttribute (key);
			}
		}
		catch (ContextException x)
		{
		}
		catch (ModelException x)
		{
		}
	}

	/**
	 * Get an object that is stored in the request context.
	 *
	 * @param req The model request.
	 * @param key Key of the object to retrieve.
	 * @return The environment object or null.
	 */
	public static Object getContextObject (ModelRequest req, String key)
	{
		try
		{
			UserEnvironment ue = (UserEnvironment) req.getContext ().get (UserEnvironment.CONTEXT_KEY);

			return ue.getAttribute (key);
		}
		catch (ContextException x)
		{
		}
		catch (ModelException x)
		{
		}

		return null;
	}

	/**
	 * Store an object in the request context.
	 *
	 * @param req The model request.
	 * @param key Key of the object to store.
	 * @param val The object to store.
	 */
	public static void setContextObject (ModelRequest req, String key, Object val)
	{
		try
		{
			UserEnvironment ue = (UserEnvironment) req.getContext ().get (UserEnvironment.CONTEXT_KEY);

			ue.setAttribute (key, val);
		}
		catch (ContextException x)
		{
		}
		catch (ModelException x)
		{
		}
	}

	/**
	 * Remove an object from the request context.
	 *
	 * @param req The model request.
	 * @param key Key of the object to remove.
	 */
	public static void removeContextObject (ModelRequest req, String key)
	{
		try
		{
			UserEnvironment ue = (UserEnvironment) req.getContext ().get (UserEnvironment.CONTEXT_KEY);

			ue.removeAttribute (key);
		}
		catch (ContextException x)
		{
		}
		catch (ModelException x)
		{
		}
	}

	/**
	 * Get the user preferences.
	 *
	 * @param req The model request.
	 * @return The user preferences or null.
	 */
	public static Preferences getUserPreferences (ModelRequest req)
	{
		return (Preferences) getUserEnvObject (req, "sessionPreferences");
	}

	/**
	 * Check if the user belongs to a specified group.
	 *
	 * @param req The model request.
	 * @param groupName The name of the group to check.
	 * @return True if the user belongs to this group.
	 */
	public static boolean currentUserIsInGroup (ModelRequest req, String groupName)
	{
		try
		{
			Context context = req.getContext ();

			UserEnvironment userEnv = (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);

			if (userEnv != null)
			{
				for (Iterator i = userEnv.getGroups ().iterator (); i.hasNext ();)
				{
					String aGroup = (String) i.next ();

					if (aGroup.equals (groupName))
					{
						return true;
					}
				}
			}

			return false;
		}
		catch (ContextException x)
		{
			return false;
		}
		catch (ModelException x)
		{
			return false;
		}
		catch (AuthorizationException x)
		{
			return false;
		}
	}

	/**
	 * Describe method getGeneratedPassword() here.
	 *
	 * @param npw
	 * @param pwl
	 * @return
	 */
	public static String getGeneratedPassword (int npw, int pwl)
	{
		PasswordGenerator passwordGenerator = new PasswordGenerator ();

		String password = "secret";

		try
		{
			password = passwordGenerator.generate (npw, pwl);
		}
		catch (Exception x)
		{
			System.out.println ("UserTools.getGeneratedPassword error: " + x);
		}

		return password;
	}

	public static String getGeneratedPassword ()
	{
		PasswordGenerator passwordGenerator = new PasswordGenerator ();

		return passwordGenerator.generate (10, 10);
	}

	public static boolean currentUserHasPermission (ModelRequest req, String permission) throws ModelException
	{
		PermissionManager pm = (PermissionManager) req.getSpringBean (PermissionManager.ID);

		return pm.hasPermission (getCurrentUserName (req), permission);
	}

	public static UserEnvironment getUserEnvironment (ModelRequest req)
	{
		try
		{
			Context context = req.getContext ();

			return (UserEnvironment) context.get (UserEnvironment.CONTEXT_KEY);
		}
		catch (ContextException x)
		{
			return null;
		}
		catch (ModelException x)
		{
			return null;
		}
	}

	public static Integer getActualRequestUserId (ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		if (! StringTools.isTrimEmpty (request.getParameter ("userId")))
		{
			int userId = request.getParameterAsInt ("userId", - 1);

			if (! UserTools.currentUserIsInGroup (request, "manager") && UserTools.getCurrentUserId (request) != userId)
			{
				throw new PermissionException ("Permission denied to edit com device function keys of user " + userId);
			}

			return userId;
		}

		return UserTools.getCurrentUserId (request);
	}

	public static String getActualRequestUserName (ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		PersistentFactory pf = (PersistentFactory) request.getService (PersistentFactory.ROLE, request.getDomain ());
		String userName = UserTools.getCurrentUserName (request);
		if (! StringTools.isTrimEmpty (request.getParameter ("userId")))
		{
			int userId = request.getParameterAsInt ("userId", - 1);
			if (! UserTools.currentUserIsInGroup (request, "manager") && UserTools.getCurrentUserId (request) != userId)
			{
				throw new PermissionException ("Permission denied to edit com device function keys of user " + userId);
			}

			try
			{
				Persistent user = pf.create ("keel.user");
				user.setField ("uid", userId);
				user.find ();
				userName = user.getFieldString ("name");
			}
			catch (PersistenceException x)
			{
			}
		}
		return userName;
	}
}
