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
import de.iritgo.simplelife.constants.*;
import de.iritgo.simplelife.tools.*;


public interface AddressDAO
{
	static public String ID = "de.iritgo.aktera.address.AddressDAO";

	public Party getPartyById (Integer id);

	public Party getPartyByUserId (Integer userId);

	public AddressStore getAddressStoreById (Integer id);

	public Address getAddressById (Integer addressId);

	public Option<Address> findAddressById (Integer addressId);

	public Option<Address> findAddressByCategoryAndLastNameOrCompany (String category, String lastNameOrCompany);

	public List<Address> findAddressesByCategoryAndNameOrCompanyStartsWith (String category, String lastNameOrCompany);

	public List<Address> findAddressesOfOwnerByCategoryAndLastNameOrCompanyStartsWith (Integer ownerId,
					String category, String name);

	public Option<Address> findAddressByCategoryAndPhoneNumber (String category, String number);

	public Option<Address> findAddressOfOwnerByCategoryAndPhoneNumber (Integer ownerId, String category,
					PhoneNumber phoneNumber);

	public Option<Address> findAddressOfOwnerByCategoryAndPhoneNumber (Integer ownerId, String category, String number,
					String countryLocalPrefix, String localPrefix);

	public Option<Address> findAddressByCategoryAndPhoneNumber (String category, PhoneNumber phoneNumber);

	public List<PhoneNumber> findPhoneNumbersEndingWith (String number);

	public List<AddressStore> findAllAddressStores ();

	public List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith (Integer ownerId, String number);

	public Option<Address> findAddressByUserId (Integer userId);

	public Option<Address> findAddressByDn (Object addressDn);

	public Option<AddressStore> findAddressStoreByName (String storeName);

	public Option<AddressStore> findAddressStoreById (Integer id);

	public Option<Address> findAddressByPartyId (int partyId);

	public Option<Address> findAddressByOwnerAndCategoryAndContactNumber (Integer ownerId, String category,
					String contactNumber);

	public Option<Address> findAddressByOwnerAndCategoryAndFirstNameOrLastNameOrCompany (Integer ownerId,
					String category, String firstName, String lastName, String company);

	public List<PhoneNumber> findAllPhoneNumbersByAddressIdSortedByCategories (Integer addressId, String[] categories);

	public long countAddressesByOwenrAndCategoryAndSearch (Integer userId, boolean onlyOwner, String category,
					String search);

	public long countAddressesByCategory (String category);

	public List<Address> createAddressListing (Integer userId, String category, boolean onlyOwner, String search,
					String orderBy, SortOrder orderDir, int firstResult, int maxResults);

	public void createAddress (Address address);

	public void createParty (Party party);

	public void updateAddress (Address address);

	public void deleteAddressesByCategory (String category);

	public void deleteAllAddressesOfOnwerByCategory (String category, Integer userId);

	public void deleteAddressWithDn (Object addressDn);

	public int calculateMaxAddressStorePosition ();

	public void moveAddressStoreOnePositionUp (AddressStore addressStore);

	public void moveAddressStoreOnePositionDown (AddressStore addressStore);

	public void renumberAddressStorePositions (Integer firstPosition, Integer deltaPosition);

	public void resetDefaultStoreFlagOnAllAddressStores ();
}
