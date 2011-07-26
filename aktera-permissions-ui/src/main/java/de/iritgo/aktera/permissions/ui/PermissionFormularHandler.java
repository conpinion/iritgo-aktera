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

package de.iritgo.aktera.permissions.ui;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.avalon.framework.configuration.Configuration;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.permissions.PermissionMetaData;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.GroupDescriptor;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.ValidationResult;


/**
 * Handler for permission formulars.
 */
public class PermissionFormularHandler extends FormularHandler
{
	/** The permission manager */
	private PermissionManager permissionManager;

	public void setPermissionManager (PermissionManager permissionManager)
	{
		this.permissionManager = permissionManager;
	}

	/** Formular handler for specific permission groups */
	private Map<String, PermissionFormPart> permissionFormParts;

	public void setPermissionFormParts (Map<String, PermissionFormPart> permissionFormParts)
	{
		this.permissionFormParts = permissionFormParts;
	}

	/**
	 * Create a new formular handler.
	 */
	public PermissionFormularHandler ()
	{
	}

	/**
	 * Copy construct a new formular handler.
	 *
	 * @param handler Another formular handler.
	 */
	public PermissionFormularHandler (PermissionFormularHandler handler)
	{
		super (handler);
	}

	@Override
	public void adjustFormular (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		TreeMap permissions = new TreeMap ();
		persistents.putAttributeValidValues ("permission.permission", permissions);
		permissions.put ("", "$opt-");

		for (PermissionMetaData pmd : permissionManager.getPermissionMetaData ())
		{
			permissions.put (pmd.getId (), "$" + pmd.getName ());
		}

		String permission = (String) persistents.getAttribute ("permission.permission");

		for (GroupDescriptor group : formular.getGroups ())
		{
			group.setVisible (group.getName ().equals ("permission") || group.getName ().equals (permission));
		}

		PermissionFormPart part = permissionFormParts.get (permission);
		if (part != null)
		{
			part.adjustFormular (request, formular, persistents);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void loadPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, Integer id) throws ModelException, PersistenceException
	{
		super.loadPersistents (request, formular, persistents, persistentConfig, id);

		Persistent permission = persistents.getPersistent ("permission");

		if (permission.getStatus () == Persistent.NEW)
		{
			permission.setField ("principalType", request.getParameterAsString ("principalType"));
			permission.setField ("principalId", request.getParameterAsString ("principalId"));
		}
	}

	@Override
	public void validatePersistents (List<Configuration> persistentConfig, ModelRequest request,
					ModelResponse response, FormularDescriptor formular, PersistentDescriptor persistents,
					boolean create, ValidationResult result) throws ModelException, PersistenceException
	{
		String permission = (String) persistents.getAttribute ("permission.permission");
		PermissionFormPart part = permissionFormParts.get (permission);
		if (part != null)
		{
			part.validatePersistents (request, response, formular, persistents, create, result);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void updatePersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		Persistent permission = persistents.getPersistent ("permission");
		String permissionType = (String) persistents.getAttribute ("permission.permission");
		permission.setField ("objectType", (permissionManager.getMetaDataById (permissionType).getObjectType ()));

		super.updatePersistents (request, formular, persistents, persistentConfig, modified);

		permissionManager.clear ();
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public int createPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		Persistent permission = persistents.getPersistent ("permission");
		String permissionType = (String) persistents.getAttribute ("permission.permission");
		permission.setField ("objectType", (permissionManager.getMetaDataById (permissionType).getObjectType ()));

		int res = super.createPersistents (request, formular, persistents, persistentConfig);

		permissionManager.clear ();

		return res;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void deletePersistent (ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		super.deletePersistent (request, response, id, persistent, systemDelete);

		permissionManager.clear ();
	}
}
