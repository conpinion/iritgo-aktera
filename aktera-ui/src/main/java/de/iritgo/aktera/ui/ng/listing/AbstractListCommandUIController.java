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

package de.iritgo.aktera.ui.ng.listing;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.permissions.PermissionException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;


/**
 * This class can be used as a base for Models that are called as commands in
 * a listing view. It assumes that a parameter id is passed to it, that
 * contains the id or id's of the persistents to act upon.
 */

/**
 *
 */
public abstract class AbstractListCommandUIController extends AbstractUIController
{
	/** The name of the request parameter that contains the list item ids */
	private String idParameterName;

	/**
	 * Initialization.
	 */
	public AbstractListCommandUIController ()
	{
		this ("id");
	}

	/**
	 * Initialization.
	 */
	public AbstractListCommandUIController (String idParameterName)
	{
		this.idParameterName = idParameterName;
	}

	/**
	 * Set the id parameter name.
	 *
	 * @param idParameterName The new id parameter name
	 */
	public void setIdParameterName (String idParameterName)
	{
		this.idParameterName = idParameterName;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIController#execute(de.iritgo.aktera.ui.UIRequest, de.iritgo.aktera.ui.UIResponse)
	 */
	public void execute (UIRequest request, UIResponse response) throws UIControllerException
	{
		String[] ids;

		if (request.getParameter (idParameterName) == null)
		{
			ids = new String[0];
		}
		else if (request.getParameter (idParameterName) instanceof String)
		{
			ids = new String[]
			{
				(String) request.getParameter (idParameterName)
			};
		}
		else
		{
			ids = (String[]) request.getParameter (idParameterName);
		}

		execute (request, response, ids);
	}

	/**
	 * Override this method to perform an action on each passed id.
	 *
	 * @param request The model request
	 * @param ids The ids of the persistent to act upon
	 * @return The model response
	 * @throws ModelException
	 */
	protected void execute (UIRequest request, UIResponse response, String[] ids) throws UIControllerException
	{
		for (String id : ids)
		{
			execute (request, response, id);
		}
	}

	/**
	 * Override this method to perform an action on each passed id.
	 *
	 * @param request The model request
	 * @param response The model response
	 * @param id The id of the persistent to act upon
	 * @throws ModelException
	 */
	protected void execute (UIRequest request, UIResponse response, String id) throws UIControllerException
	{
	}

	/**
	 * Retrieve the actual user name. This is either the current user, or the user provided
	 * by the request parameter "userId".
	 *
	 * @return The user name
	 * @throws ModelException In case of a general error
	 * @throws PersistenceException in case of a database error
	 * @throws PermissionException If the current user has no permission to upload files
	 * for another user
	 */
	protected String getActualUserName (ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		PersistentFactory pf = (PersistentFactory) request.getService (PersistentFactory.ROLE, request.getDomain ());

		String userName = UserTools.getCurrentUserName (request);

		if (request.getParameter ("userId") != null)
		{
			int userId = NumberTools.toInt (request.getParameter ("userId"), UserTools.getCurrentUserId (request));

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
