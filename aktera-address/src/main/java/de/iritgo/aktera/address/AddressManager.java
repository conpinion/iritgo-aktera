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

package de.iritgo.aktera.address;


import java.util.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.simplelife.data.*;
import de.iritgo.simplelife.tools.Option;


public interface AddressManager
{
	public enum SalutationFormatMode
	{
		NORMAL,
		LETTER,
		FIRST_LASTNAME
	}

	public static String ID = "de.iritgo.aktera.address.AddressManager";

	public static String DEFAULT_GLOBAL_ADDRESS_STORE_NAME = "de.iritgo.aktera.address.AddressLocalGlobalStore";

	public static String DEFAULT_PRIVATE_ADDRESS_STORE_NAME = "de.iritgo.aktera.address.AddressLocalPrivateStore";

	public AddressStore getAddressStoreByName(String storeName);

	public AddressStore getAddressStoreById(Integer id);

	public AddressStore getDefaultAddressStore();

	public String formatAddressTemplate(Address address, String template, Locale locale);

	public Option<Address> findAddressByLastNameOrCompany(String name);

	public Option<Address> findAddressByPhoneNumber(String number);

	public Option<Address> findAddressByPhoneNumber(String number, String internationalPrefix, String countryPrefix,
					String nationalPrefix, String localPrefix);

	public Option<Address> findAddressOfOwnerByPhoneNumber(Integer ownerId, String number, String internationalPrefix,
					String countryPrefix, String nationalPrefix, String localPrefix);

	public Option<Address> findAddressByStoreAndDn(String storeName, Object addressDn);

	public String formatNameWithSalutation(Address address, Locale dstLocale, SalutationFormatMode mode);

	public List<AddressStoreType> getAddressStoreTypes();

	public AddressStoreType getAddressStoreType(String key) throws NoSuchAddressStoreTypeException;

	Properties convertAddressToProperties(Address address, Locale locale);

	public void initializeAddressStores();

	public boolean isAddressStoreEditable(String storeName);

	public boolean isAddressStoreGlobal(String storeName);

	public List<Tuple2<String, String>> listAddressStoresNameAndTitle();

	public List<Tuple2<Integer, String>> listAddressStoresIdAndTitle();

	public List<Tuple2<Integer, String>> listSystemAddressStoresIdAndTitle();

	public List<Tuple3<Integer, String, String>> listAddressStoresIdAndNameAndTitle();

	public void deleteAllAddressesOfOwner(Integer userId);
}
