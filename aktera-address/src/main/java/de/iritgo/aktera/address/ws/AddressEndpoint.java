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

package de.iritgo.aktera.address.ws;


import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.ws.server.endpoint.annotation.*;
import de.iritgo.aktera.address.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.address.entity.Address;
import de.iritgo.aktera.authentication.*;
import de.iritgo.aktera.authentication.defaultauth.entity.*;
import de.iritgo.aktera.configuration.preferences.*;
import de.iritgo.aktera.permissions.*;
import de.iritgo.aktera.webservices.address.*;
import de.iritgo.simplelife.constants.*;
import de.iritgo.simplelife.data.*;
import de.iritgo.simplelife.string.*;
import de.iritgo.simplelife.tools.*;


@Endpoint
public class AddressEndpoint
{
	@Setter
	@Autowired
	private SecurityContext securityContext;

	@Setter
	@Autowired
	private AddressManager addressManager;

	@Setter
	@Autowired
	private PreferencesManager preferencesManager;

	@Setter
	@Autowired
	private PermissionManager permissionManager;

	@PayloadRoot(localPart = "listAddressStoresRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public ListAddressStoresResponse listAddressStores (@SuppressWarnings("unused") ListAddressStoresRequest request)
		throws Exception
	{
		AkteraUser user = securityContext.getUser ();
		ListAddressStoresResponse response = new ListAddressStoresResponse ();
		for (Tuple3<Integer, String, String> store : addressManager.listAddressStoresIdAndNameAndTitle ())
		{
			if (permissionManager.hasPermission (user.getName (), "de.iritgo.aktera.address.view", AddressStore.class
							.getName (), store.get1 ()))
			{
				ListAddressStoresResponse.AddressStore as = new ListAddressStoresResponse.AddressStore ();
				as.setId (store.get1 ());
				as.setName (store.get2 ());
				as.setTitle (store.get3 ());
				response.getAddressStore ().add (as);
			}
		}
		return response;
	}

	@PayloadRoot(localPart = "getDefaultAddressStoreNameRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public GetDefaultAddressStoreNameResponse getDefaultAddressStoreName (
					@SuppressWarnings("unused") GetDefaultAddressStoreNameRequest request) throws Exception
	{
		GetDefaultAddressStoreNameResponse response = new GetDefaultAddressStoreNameResponse ();
		AkteraUser user = securityContext.getUser ();
		AddressStore store = null;
		try
		{
			store = addressManager.getAddressStoreById (preferencesManager.getInt (user.getId (), "address",
							"defaultAddressStore", - 1));
		}
		catch (AddressStoreNotFoundException x)
		{
			store = addressManager.getDefaultAddressStore ();
		}
		response.setName (store.getName ());
		return response;
	}

	@PayloadRoot(localPart = "getDefaultAddressStoreIdRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public GetDefaultAddressStoreIdResponse getDefaultAddressStoreId (
					@SuppressWarnings("unused") GetDefaultAddressStoreIdRequest request) throws Exception
	{
		GetDefaultAddressStoreIdResponse response = new GetDefaultAddressStoreIdResponse ();
		AkteraUser user = securityContext.getUser ();
		AddressStore store = null;
		try
		{
			store = addressManager.getAddressStoreById (preferencesManager.getInt (user.getId (), "address",
							"defaultAddressStore", - 1));
		}
		catch (AddressStoreNotFoundException x)
		{
			store = addressManager.getDefaultAddressStore ();
		}
		response.setId (store.getId ());
		return response;
	}

	@PayloadRoot(localPart = "countAddressesRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public CountAddressesResponse countAddresses (CountAddressesRequest request) throws Exception
	{
		AkteraUser user = securityContext.getUser ();
		AddressStore store = addressManager.getAddressStoreByName (request.getAddressStoreName ());
		CountAddressesResponse response = new CountAddressesResponse ();
		response.setCount ((int) store.countAddressesByOwnerAndSearch (user.getId (), request.getQuery ()));
		return response;
	}

	@PayloadRoot(localPart = "listAddressesRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public ListAddressesResponse listAddresses (ListAddressesRequest request) throws Exception
	{
		AkteraUser user = securityContext.getUser ();
		int firstResult = request.getFirstResult () != null ? request.getFirstResult ().intValue () : 0;
		int maxResults = request.getMaxResults () != null ? request.getMaxResults ().intValue () : - 1;
		SortOrder orderDir = request.getOrderDir () != null ? SortOrder.byId (request.getOrderDir ())
						: SortOrder.ASCENDING;
		String orderBy = request.getOrderBy () != null ? request.getOrderBy () : "lastName";
		AddressStore store = addressManager.getAddressStoreByName (request.getAddressStoreName ());
		ListAddressesResponse response = new ListAddressesResponse ();
		for (Address address : store.createAddressListing (user.getId (), request.getQuery (), orderBy, orderDir,
						firstResult, maxResults))
		{
			ListAddressesResponse.Address addressElement = new ListAddressesResponse.Address ();
			addressElement.setId (address.getAnyId ().toString ());
			addressElement.setFirstName (address.getFirstName ());
			addressElement.setLastName (address.getLastName ());
			addressElement.setCompany (address.getCompany ());
			addressElement.setEmail (address.getEmail ());
			addressElement.setHomepage (address.getWeb ());
			response.getAddress ().add (addressElement);
		}
		return response;
	}

	@PayloadRoot(localPart = "listAddressPhoneNumbersRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public ListAddressPhoneNumbersResponse listAddressPhoneNumbers (ListAddressPhoneNumbersRequest request)
		throws Exception
	{
		ListAddressPhoneNumbersResponse response = new ListAddressPhoneNumbersResponse ();
		AddressStore store = addressManager.getAddressStoreByName (request.getAddressStoreName ());
		Option<Address> address = store.findAddressByDn (request.getAddressId ());
		if (address.full ())
		{
			for (PhoneNumber number : address.get ().getPhoneNumbers ())
			{
				ListAddressPhoneNumbersResponse.PhoneNumber numberElement = new ListAddressPhoneNumbersResponse.PhoneNumber ();
				numberElement.setCategory (number.getCategory ());
				numberElement.setDisplayNumber (number.getNumber ());
				numberElement.setCanonicalNumber (number.getInternalNumber ());
				response.getPhoneNumber ().add (numberElement);
			}
		}
		return response;
	}

	@PayloadRoot(localPart = "getAddressRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public GetAddressResponse getAddress (GetAddressRequest request) throws Exception
	{
		String addressStoreName = StringTools.isNotTrimEmpty (request.getAddressStoreName ()) ? request
						.getAddressStoreName () : addressManager.getDefaultAddressStore ().getName ();
		String addressId = StringTools.trim (request.getAddressId ());
		AddressStore store = addressManager.getAddressStoreByName (addressStoreName);
		GetAddressResponse response = new GetAddressResponse ();
		Option<Address> address = store.findAddressByDn (addressId);
		if (address.full ())
		{
			de.iritgo.aktera.webservices.address.Address addressElement = new de.iritgo.aktera.webservices.address.Address ();
			addressElement.setSalutation (address.get ().getSalutation ());
			addressElement.setFirstName (address.get ().getFirstName ());
			addressElement.setLastName (address.get ().getLastName ());
			addressElement.setCompany (address.get ().getCompany ());
			addressElement.setDivision (address.get ().getDivision ());
			addressElement.setStreet (address.get ().getStreet ());
			addressElement.setPostalCode (address.get ().getPostalCode ());
			addressElement.setCity (address.get ().getCity ());
			addressElement.setEmail (address.get ().getEmail ());
			addressElement.setHomepage (address.get ().getWeb ());
			addressElement.setContactNumber (address.get ().getContactNumber ());
			addressElement.setCompanyNumber (address.get ().getCompanyNumber ());
			addressElement.setPhoneNumberB (address.get ().getPhoneNumberByCategory ("B").getNumber ());
			addressElement.setPhoneNumberBDD (address.get ().getPhoneNumberByCategory ("BDD").getNumber ());
			addressElement.setPhoneNumberBF (address.get ().getPhoneNumberByCategory ("BF").getNumber ());
			addressElement.setPhoneNumberBM (address.get ().getPhoneNumberByCategory ("BM").getNumber ());
			addressElement.setPhoneNumberP (address.get ().getPhoneNumberByCategory ("P").getNumber ());
			addressElement.setPhoneNumberPF (address.get ().getPhoneNumberByCategory ("PF").getNumber ());
			addressElement.setPhoneNumberPM (address.get ().getPhoneNumberByCategory ("PM").getNumber ());
			addressElement.setPhoneNumberVOIP (address.get ().getPhoneNumberByCategory ("VOIP").getNumber ());
			addressElement.setRemark (address.get ().getRemark ());
			response.setAddress (addressElement);
		}
		return response;
	}

	@PayloadRoot(localPart = "findAddressByPhoneNumberRequest", namespace = "http://aktera.iritgo.de/webservices/address")
	public FindAddressByPhoneNumberResponse findAddressByPhoneNumber (FindAddressByPhoneNumberRequest request)
		throws Exception
	{
		FindAddressByPhoneNumberResponse response = new FindAddressByPhoneNumberResponse ();
		Option<Address> address = addressManager.findAddressByPhoneNumber (request.getPhoneNumber ());
		if (address.full ())
		{
			de.iritgo.aktera.webservices.address.Address addressElement = new de.iritgo.aktera.webservices.address.Address ();
			addressElement.setSalutation (address.get ().getSalutation ());
			addressElement.setFirstName (address.get ().getFirstName ());
			addressElement.setLastName (address.get ().getLastName ());
			addressElement.setCompany (address.get ().getCompany ());
			addressElement.setDivision (address.get ().getDivision ());
			addressElement.setStreet (address.get ().getStreet ());
			addressElement.setPostalCode (address.get ().getPostalCode ());
			addressElement.setCity (address.get ().getCity ());
			addressElement.setEmail (address.get ().getEmail ());
			addressElement.setHomepage (address.get ().getWeb ());
			addressElement.setContactNumber (address.get ().getContactNumber ());
			addressElement.setCompanyNumber (address.get ().getCompanyNumber ());
			addressElement.setPhoneNumberB (address.get ().getPhoneNumberByCategory ("B").getNumber ());
			addressElement.setPhoneNumberBDD (address.get ().getPhoneNumberByCategory ("BDD").getNumber ());
			addressElement.setPhoneNumberBF (address.get ().getPhoneNumberByCategory ("BF").getNumber ());
			addressElement.setPhoneNumberBM (address.get ().getPhoneNumberByCategory ("BM").getNumber ());
			addressElement.setPhoneNumberP (address.get ().getPhoneNumberByCategory ("P").getNumber ());
			addressElement.setPhoneNumberPF (address.get ().getPhoneNumberByCategory ("PF").getNumber ());
			addressElement.setPhoneNumberPM (address.get ().getPhoneNumberByCategory ("PM").getNumber ());
			addressElement.setPhoneNumberVOIP (address.get ().getPhoneNumberByCategory ("VOIP").getNumber ());
			addressElement.setRemark (address.get ().getRemark ());
			response.setAddress (addressElement);
		}
		return response;
	}
}
