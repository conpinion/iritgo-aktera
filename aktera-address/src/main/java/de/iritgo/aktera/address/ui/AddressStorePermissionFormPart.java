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
import lombok.*;
import de.iritgo.aktera.address.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.permissions.ui.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.aktera.ui.listing.*;
import de.iritgo.simplelife.math.*;


public class AddressStorePermissionFormPart extends PermissionFormPart
{
	@Setter
	private AddressDAO addressDAO;

	@Override
	public String createListInfo (ModelRequest request, RowData data) throws PersistenceException, ModelException
	{
		AddressStore store = addressDAO.getAddressStoreById (NumberTools.toInt (data.get ("objectId"), - 1));
		return "addressStorePermissionInfo|" + (store != null ? store.getDisplayedTitle () : "---");
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
