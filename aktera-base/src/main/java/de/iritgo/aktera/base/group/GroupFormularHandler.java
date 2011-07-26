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


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.ui.form.FormTools;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.ValidationResult;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import java.util.List;


/**
 * Handler for the user group management formular.
 */
public class GroupFormularHandler extends FormularHandler
{
	/** */
	private PermissionManager permissionManager;

	/**
	 * @param permissionManager The new permissionManager.
	 */
	public void setPermissionManager (PermissionManager permissionManager)
	{
		this.permissionManager = permissionManager;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void validatePersistents (List<Configuration> persistentConfig, ModelRequest request,
					ModelResponse response, FormularDescriptor formular, PersistentDescriptor persistents,
					boolean create, ValidationResult result) throws ModelException, PersistenceException
	{
		PersistentFactory persistentManager = (PersistentFactory) request.getService (PersistentFactory.ROLE, request
						.getDomain ());

		Persistent group = persistents.getPersistent ("akteraGroup");

		if (! StringTools.isTrimEmpty (group.getField ("name")))
		{
			Persistent otherGroup = persistentManager.create ("aktera.AkteraGroup");

			otherGroup.setField ("name", group.getField ("name"));

			if (otherGroup.find () && ! otherGroup.getField ("id").equals (group.getField ("id")))
			{
				FormTools.addError (response, result, "akteraGroup_name", "Aktera:duplicateGroupName");
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public int createPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		int id = super.createPersistents (request, formular, persistents, persistentConfig);

		permissionManager.clear ();

		return id;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void updatePersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		PersistentFactory persistentManager = (PersistentFactory) request.getService (PersistentFactory.ROLE, request
						.getDomain ());

		Persistent group = persistents.getPersistent ("akteraGroup");

		Persistent oldGroup = persistentManager.create ("aktera.AkteraGroup");

		oldGroup.setField ("id", group.getField ("id"));
		oldGroup.retrieve ();

		super.updatePersistents (request, formular, persistents, persistentConfig, modified);

		if (modified)
		{
			permissionManager.clear ();
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void deletePersistent (ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		boolean protect = NumberTools.toBool (persistent.getField ("protect"), false);

		if (! systemDelete && protect)
		{
			return;
		}

		PersistentFactory persistentManager = (PersistentFactory) request.getService (PersistentFactory.ROLE, request
						.getDomain ());

		Persistent groupEntry = persistentManager.create ("aktera.AkteraGroupEntry");

		groupEntry.setField ("groupId", persistent.getField ("id"));
		groupEntry.deleteAll ();

		super.deletePersistent (request, response, id, persistent, systemDelete);

		permissionManager.deleteAllPermissionsOfPrincipal (persistent.getFieldInt ("id"), "G");
	}
}
