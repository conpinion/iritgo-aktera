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
import org.apache.avalon.framework.configuration.*;
import org.springframework.beans.factory.annotation.*;
import de.iritgo.aktera.address.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.ui.form.*;


public class AddressStoreCreationFormularHandler extends FormularHandler
{
	@Setter
	@Autowired
	private AddressManager addressManager;

	@Override
	public void adjustFormular (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		Map types = new LinkedHashMap<String, String> ();
		for (AddressStoreType type : addressManager.getAddressStoreTypes ())
		{
			types.put (type.getKey (), type.getLabel ());
		}
		persistents.putValidValues ("type", types);
	}

	@Override
	public int createPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		String typeString = (String) persistents.getAttribute ("type");
		try
		{
			AddressStoreType type = addressManager.getAddressStoreType (typeString);
			AddressStore store = type.newAddressStore ();
			store.setType (typeString);
			persistents.put ("store", store);
		}
		catch (NoSuchAddressStoreTypeException x)
		{
			throw new ModelException ("Invalid address store type '" + typeString + "'");
		}
		catch (Exception x)
		{
			throw new ModelException ("Unable to create address of type '" + typeString + "': " + x.toString ());
		}
		return - 1;
	}
}
