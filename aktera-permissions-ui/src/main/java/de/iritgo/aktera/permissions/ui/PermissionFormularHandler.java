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


import java.util.*;
import javax.annotation.PostConstruct;
import lombok.Setter;
import org.apache.avalon.framework.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.permissions.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.simplelife.string.StringTools;


@Component("de.iritgo.aktera.permissions.PermissionFormularHandler")
public class PermissionFormularHandler extends FormularHandler
{
	@Setter
	@Autowired
	private PermissionManager permissionManager;

	@Setter
	@Autowired
	private List<PermissionFormPart> permissionFormParts;

	private Map<String, PermissionFormPart> permissionFormPartsByKey = new HashMap();

	public PermissionFormularHandler()
	{
	}

	public PermissionFormularHandler(PermissionFormularHandler handler)
	{
		super(handler);
	}

	@PostConstruct
	public void init()
	{
		for (PermissionFormPart part : permissionFormParts)
		{
			for (String key : part.getPermissionKeys())
			{
				permissionFormPartsByKey.put(key, part);
			}
		}
	}

	@Override
	public void adjustFormular(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		TreeMap permissions = new TreeMap();
		persistents.putAttributeValidValues("permission.permission", permissions);
		permissions.put("", "$opt-");

		for (PermissionMetaData pmd : permissionManager.getPermissionMetaData())
		{
			permissions.put(pmd.getId(), "$" + pmd.getName());
		}

		String permission = (String) persistents.getAttribute("permission.permission");

		for (GroupDescriptor group : formular.getGroups())
		{
			group.setVisible(group.getName().equals("permission") || group.getName().equals(permission));
		}

		PermissionFormPart part = permissionFormPartsByKey.get(permission);
		if (part != null)
		{
			part.adjustFormular(request, formular, persistents);
		}
	}

	@Override
	public void loadPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, Integer id) throws ModelException, PersistenceException
	{
		super.loadPersistents(request, formular, persistents, persistentConfig, id);

		Persistent permission = persistents.getPersistent("permission");

		if (permission.getStatus() == Persistent.NEW)
		{
			permission.setField("principalType", request.getParameterAsString("principalType"));
			permission.setField("principalId", request.getParameterAsString("principalId"));
		}
	}

	@Override
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
		String permission = (String) persistents.getAttribute("permission.permission");
		PermissionFormPart part = permissionFormPartsByKey.get(permission);
		if (part != null)
		{
			part.validatePersistents(request, response, formular, persistents, create, result);
		}
	}

	@Override
	public void updatePersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		Persistent permission = persistents.getPersistent("permission");
		String permissionType = (String) persistents.getAttribute("permission.permission");
		if (StringTools.isTrimEmpty(permission.getField("objectType")))
		{
			permission.setField("objectType", (permissionManager.getMetaDataById(permissionType).getObjectType()));
		}

		super.updatePersistents(request, formular, persistents, persistentConfig, modified);

		permissionManager.clear();
	}

	@Override
	public int createPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		Persistent permission = persistents.getPersistent("permission");
		String permissionType = (String) persistents.getAttribute("permission.permission");
		if (StringTools.isTrimEmpty(permission.getField("objectType")))
		{
			permission.setField("objectType", (permissionManager.getMetaDataById(permissionType).getObjectType()));
		}

		int res = super.createPersistents(request, formular, persistents, persistentConfig);

		permissionManager.clear();

		return res;
	}

	@Override
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		super.deletePersistent(request, response, id, persistent, systemDelete);

		permissionManager.clear();
	}
}
