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


import java.util.TreeMap;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.iritgo.aktera.address.AddressDAO;
import de.iritgo.aktera.address.entity.AddressStore;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.permissions.ui.PermissionFormPart;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.aktera.ui.listing.RowData;
import de.iritgo.simplelife.math.NumberTools;


@Component("de.iritgo.aktera.address.AddressPermissionFormPart")
public class AddressPermissionFormPart extends PermissionFormPart
{
	@Override
	public String[] getPermissionKeys ()
	{
		return new String[]
		{
						"de.iritgo.aktera.address.*", "de.iritgo.aktera.address.create",
						"de.iritgo.aktera.address.delete", "de.iritgo.aktera.address.edit",
						"de.iritgo.aktera.address.import", "de.iritgo.aktera.address.view"
		};
	}

	@Setter
	@Autowired
	private AddressDAO addressDAO;

	@Override
	public String createListInfo (ModelRequest request, RowData data) throws PersistenceException, ModelException
	{
		AddressStore store = addressDAO.getAddressStoreById (NumberTools.toInt (data.get ("objectId"), - 1));
		return "addressStorePermissionInfo|" + store.getDisplayedTitle ();
	}

	@Override
	public void adjustFormular (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		TreeMap stores = new TreeMap ();
		persistents.putAttributeValidValues ("permission.objectId", stores);
		stores.put ("", "$opt-");
		for (AddressStore store : addressDAO.findAllAddressStores ())
		{
			stores.put (store.getId ().toString (), store.getDisplayedTitle ());
		}
	}

	@Override
	public void validatePersistents (ModelRequest request, ModelResponse response, FormularDescriptor formular,
					PersistentDescriptor persistents, boolean create, ValidationResult result)
		throws ModelException, PersistenceException
	{
	}
}
