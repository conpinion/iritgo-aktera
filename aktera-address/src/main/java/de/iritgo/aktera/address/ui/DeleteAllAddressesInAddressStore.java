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


import lombok.Setter;
import javax.inject.Inject;
import de.iritgo.aktera.address.AddressManager;
import de.iritgo.aktera.address.entity.AddressStore;
import de.iritgo.aktera.ui.*;
import de.iritgo.aktera.ui.ng.listing.AbstractListCommandUIController;
import de.iritgo.simplelife.math.NumberTools;


public class DeleteAllAddressesInAddressStore extends AbstractListCommandUIController
{
	@Setter
	@Inject
	private AddressManager addressManager;

	@Override
	protected void execute(UIRequest request, UIResponse response, String id) throws UIControllerException
	{
		AddressStore store = addressManager.getAddressStoreById(NumberTools.toInt(id, - 1));
		store.deleteAllAddresses();
	}
}
