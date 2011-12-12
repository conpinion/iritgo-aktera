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


import de.iritgo.aktera.address.AddressManager;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.ui.*;
import de.iritgo.simplelife.tools.Option;


public class GetPhoneNumbersByStoreAndAddress extends AbstractUIController
{
	public void execute(UIRequest request, UIResponse response) throws UIControllerException
	{
		AddressManager addressManager = (AddressManager) SpringTools.getBean(AddressManager.ID);
		String addressId = request.getParameterAsString("addressId");
		Integer addressStoreId = request.getParameterAsInt("storeId");
		AddressStore store = addressManager.getAddressStoreById(addressStoreId);
		Option<Address> address = store.findAddressByDn(addressId);
		if (address.full())
		{
			for (PhoneNumber number : address.get().getPhoneNumbers())
			{
				Command cmd = new DefaultCommand();
				cmd.setModel("connect.pbx.base.my.call-number-seq");
				cmd.setName("phoneNumber" + number.getCategory());
				cmd.setLabel(number.getNumber());
				cmd.setParameter("number", number.getInternalNumber());
				response.add(cmd);
			}
		}
	}
}
