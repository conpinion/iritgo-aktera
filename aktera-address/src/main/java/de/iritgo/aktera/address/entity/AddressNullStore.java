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


package de.iritgo.aktera.address.entity;

import java.util.*;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.tools.*;


@SuppressWarnings("serial")
public class AddressNullStore extends AddressStore
{
	public AddressNullStore()
	{
		id = -1;
		type = "de.iritgo.aktera.address.entity.AddressNullStore";
		name = "de.iritgo.aktera.address.entity.AddressNullStore";
		title = "$AkteraAddress:nullStore";
		position = 0;
		systemStore = true;
		defaultStore = false;
		editable = false;
		numberLookup = false;
		emptySearchReturnsAllEntries = false;
	}

	@Override
	public void init()
	{
	}

	@Override
	public void shutdown()
	{
	}

	@Override
	public Option<Address> findAddressByDn(Object addressDn)
	{
		return Option.Empty();
	}

	@Override
	public Option<Address> findAddressByLastNameOrCompany(String name)
	{
		return Option.Empty();
	}

	@Override
	public List<Address> findAddressByNameStartsWith(String name)
	{
		return new LinkedList<Address>();
	}

	@Override
	public List<Address> findAddressOfOwnerByNameStartsWith(String name, Integer ownerId)
	{
		return new LinkedList<Address>();
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(PhoneNumber phoneNumber)
	{
		return Option.Empty();
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(String number)
	{
		return Option.Empty();
	}

	@Override
	public Option<Address> findAddressOfOwnerByPhoneNumber(PhoneNumber phoneNumber, Integer ownerId)
	{
		return Option.Empty();
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersEndingWith(String number)
	{
		return new LinkedList<PhoneNumber>();
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith(String number, Integer ownerId)
	{
		return new LinkedList<PhoneNumber>();
	}

	@Override
	public Option<Address> findAddressByOwnerAndContactNumber(Integer ownerId, String contactNumber)
	{
		return Option.Empty();
	}

	@Override
	public Option<Address> findAddressByOnwerAndFirstNameOrLastNameOrCompany(Integer ownerId, String firstName,
					String lastName, String company)
	{
		return Option.Empty();
	}

	@Override
	public List<PhoneNumber> findAllPhoneNumbersByAddressDnSortedByCategoryList(String addressDn, String[] categories)
	{
		return new LinkedList<PhoneNumber>();
	}

	@Override
	public List<Address> createAddressListing(Integer userId, String search, String orderBy, SortOrder orderDir,
					int firstResult, int maxResults)
	{
		return new LinkedList<Address>();
	}

	@Override
	public void updateAddress(Address address)
	{
	}

	@Override
	public Object createAddress(Address address, Integer ownerId)
	{
		return new Address();
	}

	@Override
	public void deleteAddressWithDn(Object addressDn)
	{
	}

	@Override
	public void deleteAllAddresses()
	{
	}

	@Override
	public boolean isGlobalStore()
	{
		return false;
	}

	@Override
	public long countAddressesByOwnerAndSearch(Integer userId, String search)
	{
		return 0;
	}

	@Override
	public boolean canBeDeleted()
	{
		return false;
	}

	@Override
	public void deleteAllAddressesOfOwner(Integer userId)
	{
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(String number, String countryPrefix, String localPrefix, String internationalPrefix, String nationalPrefix)
	{
		return new Empty ();
	}

	@Override
	public Option<Address> findAddressOfOwnerByPhoneNumber(Integer ownerId, String number, String countryPrefix, String localPrefix, String internationalPrefix, String nationalPrefix)
	{
		return new Empty ();
	}

}
