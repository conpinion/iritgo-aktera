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

package de.iritgo.aktera.base.group;


import de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroupEntry;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.aktera.ui.ng.listing.AbstractListCommandUIController;
import de.iritgo.simplelife.math.NumberTools;


/**
 * Remove an aktera group entry.
 */
public class RemoveAkteraGroupEntry extends AbstractListCommandUIController
{
	private UserDAO userDAO;

	public void setUserDAO (UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	private PermissionManager permissionManager;

	public void setPermissionManager (PermissionManager permissionManager)
	{
		this.permissionManager = permissionManager;
	}

	public RemoveAkteraGroupEntry ()
	{
		super ("entryId");
	}

	@Override
	protected void execute (UIRequest request, UIResponse response, String[] ids) throws UIControllerException
	{
		for (String id : ids)
		{
			AkteraGroupEntry entry = userDAO.findAkteraGroupEntryById (NumberTools.toInt (id, - 1));
			userDAO.deleteAkteraGroupEntry (entry);
		}
		permissionManager.clear ();
	}
}
