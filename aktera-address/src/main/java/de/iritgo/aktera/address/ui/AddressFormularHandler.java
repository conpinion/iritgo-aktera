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


import java.util.List;
import lombok.Setter;
import org.apache.avalon.framework.configuration.Configuration;
import javax.inject.Inject;
import de.iritgo.aktera.address.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.authentication.defaultauth.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.permissions.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.*;


public class AddressFormularHandler extends FormularHandler
{
	@Setter
	@Inject
	private AddressManager addressManager;

	@Setter
	@Inject
	private AddressDAO addressDAO;

	@Setter
	@Inject
	private UserDAO userDAO;

	@Setter
	@Inject
	private PermissionManager permissionManager;

	@Override
	public void loadPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, Integer id) throws ModelException, PersistenceException
	{
		AddressStore store = addressManager.getAddressStoreById(getAddressStoreId(request));
		persistents.putAttribute("addressStore", store.getDisplayedTitle());
		persistents.putAttribute("addressStoreId", store.getId());

		Option<Address> address = store.findAddressByDn(request.getParameter("id"));
		if (address.empty())
		{
			address = new Full(new Address());
		}
		persistents.put("address", address.get());

		if (address.get().getId() == null
						&& ! permissionManager.hasPermission(UserTools.getCurrentUserName(request),
										"de.iritgo.aktera.address.create", AddressStore.class.getName(), store.getId()))
		{
			throw new PermissionException();
		}

		boolean editable = true;
		if (! store.getEditable())
		{
			editable = false;
		}
		if (! permissionManager.hasPermission(UserTools.getCurrentUserName(request), "de.iritgo.aktera.address.edit",
						AddressStore.class.getName(), store.getId()))
		{
			editable = false;
		}
		formular.setReadOnly(! editable);
	}

	@Override
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
		Address address = (Address) persistents.get("address");
		if (StringTools.isTrimEmpty(address.getLastName()) && StringTools.isTrimEmpty(address.getCompany()))
		{
			FormTools.addError(response, result, "address.lastName", "AkteraAddress:oneOfLastNameOrCompanyMustBeFilled");
		}
	}

	@Override
	public int createPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		AddressStore store = addressManager.getAddressStoreById((Integer) persistents.getAttribute("addressStoreId"));
		if (! store.getEditable())
		{
			throw new PermissionException();
		}
		if (! permissionManager.hasPermission(UserTools.getCurrentUserName(request), "de.iritgo.aktera.address.create",
						AddressStore.class.getName(), store.getId()))
		{
			throw new PermissionException();
		}

		return (Integer) store.createAddress((Address) persistents.get("address"), UserTools.getCurrentUserId(request));
	}

	@Override
	public void updatePersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		AddressStore store = addressManager.getAddressStoreById((Integer) persistents.getAttribute("addressStoreId"));
		if (! store.getEditable())
		{
			throw new PermissionException();
		}
		if (! permissionManager.hasPermission(UserTools.getCurrentUserName(request), "de.iritgo.aktera.address.edit",
						AddressStore.class.getName(), store.getId()))
		{
			throw new PermissionException();
		}
		store.updateAddress((Address) persistents.get("address"));
	}

	@Override
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		AddressStore store = addressManager.getAddressStoreById(getAddressStoreId(request));
		if (! store.getEditable())
		{
			throw new PermissionException();
		}
		if (! permissionManager.hasPermission(UserTools.getCurrentUserName(request), "de.iritgo.aktera.address.delete",
						AddressStore.class.getName(), store.getId()))
		{
			throw new PermissionException();
		}

		if (store.getName().equals(AddressManager.DEFAULT_GLOBAL_ADDRESS_STORE_NAME) && ! systemDelete)
		{
			Address address = addressDAO.getAddressById(NumberTools.toInt(id, - 1));
			if (address.getPartyId() != null)
			{
				Party party = addressDAO.getPartyById(address.getPartyId());
				if (party != null && party.getUserId() != null)
				{
					AkteraUser user = userDAO.findUserById(party.getUserId());
					if (user != null)
					{
						response.addError("GLOBAL_addressAssignedToUserCannotBeDeleted",
										"$AkteraAddress:addressAssignedToUserCannotBeDeleted|" + address.getLastName());
						return;
					}
				}
			}
		}

		store.deleteAddressWithDn(id);
	}

	protected Integer getAddressStoreId(ModelRequest request)
	{
		int addressStoreId = NumberTools.toInt(request.getParameterAsInt("addressStoreId"), - 1);
		return addressStoreId != - 1 ? addressStoreId : addressManager.getDefaultAddressStore().getId();
	}
}
