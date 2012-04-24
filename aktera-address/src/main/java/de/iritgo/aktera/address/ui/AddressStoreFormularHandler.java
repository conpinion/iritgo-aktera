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

package de.iritgo.aktera.address.ui;


import java.util.*;
import lombok.Setter;
import org.apache.avalon.framework.configuration.Configuration;
import javax.inject.Inject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import de.iritgo.aktera.address.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.Option;


@Component("de.iritgo.aktera.address.AddressStoreFormularHandler")
public class AddressStoreFormularHandler extends FormularHandler
{
	@Setter
	@Inject
	private AddressDAO addressDAO;

	@Setter
	@Inject
	private AddressManager addressManager;

	@Override
	public void afterLoad(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		AddressStore store = (AddressStore) persistents.get("store");
		for (AddressStoreType type : addressManager.getAddressStoreTypes())
		{
			String addressStoreClassName = type.getClassName();
			formular.getGroup(addressStoreClassName).setVisible(
							addressStoreClassName.equals(store.getClass().getName()));
		}
		if (store instanceof AddressLDAPStore)
		{
			AddressLDAPStore ldapStore = (AddressLDAPStore) store;
			ldapStore.setAuthPassword(StringTools.decode(ldapStore.getAuthPassword()));
		}
	}

	@Override
	public void adjustFormular(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		Map scopes = new HashMap();
		persistents.putValidValues("store.scope", scopes);
		for (AddressLDAPStore.SearchScope scope : AddressLDAPStore.SearchScope.values())
		{
			scopes.put(scope.name(), "$ldapScope" + scope.name());
		}
	}

	@Override
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
		super.validatePersistents(persistentConfig, request, response, formular, persistents, create, result);
		AddressStore store = (AddressStore) persistents.get("store");
		Option<AddressStore> otherStore = addressDAO.findAddressStoreByName(store.getName());
		if (otherStore.full() && ! otherStore.get().getId().equals(store.getId()))
		{
			FormTools.addError(response, result, "store.name", "AkteraAddress:duplicateAddressStoreName");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void preStorePersistents(ModelRequest request, FormularDescriptor formular,
					PersistentDescriptor persistents, boolean modified) throws ModelException, PersistenceException
	{
		AddressStore store = (AddressStore) persistents.get("store");
		if (store instanceof AddressLDAPStore)
		{
			AddressLDAPStore ldapStore = (AddressLDAPStore) store;
			ldapStore.setAuthPassword(StringTools.encode(ldapStore.getAuthPassword()));
		}
	}

	@Override
	@Transactional(readOnly = false)
	public int createPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		AddressStore store = (AddressStore) persistents.get("store");
		if (store.getDefaultStore())
		{
			addressDAO.resetDefaultStoreFlagOnAllAddressStores();
		}
		store.setPosition(addressDAO.calculateMaxAddressStorePosition() + 1);
		int id = super.createPersistents(request, formular, persistents, persistentConfig);
		addressManager.initializeAddressStores();
		return id;
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		AddressStore store = (AddressStore) persistents.get("store");
		if (store.getDefaultStore())
		{
			addressDAO.resetDefaultStoreFlagOnAllAddressStores();
		}
		super.updatePersistents(request, formular, persistents, persistentConfig, modified);
		addressManager.initializeAddressStores();
	}

	@Override
	public boolean canDeletePersistent(ModelRequest request, Object id, Object entity, boolean systemDelete,
					ValidationResult result) throws ModelException, PersistenceException
	{
		AddressStore store = (AddressStore) entity;
		if (systemDelete || (store.canBeDeleted() && ! store.getDefaultStore()))
		{
			return true;
		}
		result.addError("store", "AkteraAddress:notEmptyAddressStoreCannotBeDeleted");
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Object entity,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		AddressStore store = (AddressStore) entity;
		addressDAO.renumberAddressStorePositions(store.getPosition() + 1, - 1);
		super.deletePersistent(request, response, id, entity, systemDelete);
		addressManager.initializeAddressStores();
	}
}
