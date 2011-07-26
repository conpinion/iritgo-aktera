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

package de.iritgo.aktera.address.wsclient;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktera.webservices.SEnvelope;
import de.iritgo.aktera.webservices.WebservicesClientManager;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/**
 * Client side facade for address services.
 */
public class AddressClientService extends BaseObject implements Manager
{
	/** Manager ID */
	public static String ID = "de.iritgo.aktera.address.wsclient.AddressClientService";

	/**
	 * @see de.iritgo.aktario.core.manager.Manager#init()
	 */
	public void init ()
	{
	}

	/**
	 * @see de.iritgo.aktario.core.manager.Manager#unload()
	 */
	public void unload ()
	{
	}

	/**
	 * Query the server for a list of all address stores.
	 *
	 * @return A list of all address stores
	 */
	public List<AddressStore> listAddressStores ()
	{
		WebservicesClientManager wcm = (WebservicesClientManager) Engine.instance ().getManager (
						WebservicesClientManager.ID);
		List<AddressStore> res = new LinkedList ();
		SoapObject request = wcm.createSoapRequest ("http://aktera.iritgo.de/webservices/address",
						"listAddressStoresRequest");
		SEnvelope envelope = wcm.createEnvelopeForCurrentUser (request);

		try
		{
			Vector<Object> results = wcm.sendToCurrentServer (envelope);

			for (Object o : results.toArray ())
			{
				SoapObject so = (SoapObject) o;
				AddressStore as = new AddressStore ();
				as.setName (wcm.getPropertyAsString (so, "name"));
				as.setTitle (wcm.getPropertyAsString (so, "title"));
				res.add (as);
			}
		}
		catch (Exception x)
		{
			System.out.println (x.toString ());
		}

		return res;
	}

	/**
	 * Query the server for a list of addresses.
	 *
	 * @param addressStoreName The address store which addresses should be
	 * looked up
	 * @param query A query string
	 * @param firstResult Index of the first address item
	 * @param maxResults Maximum number of returned address items
	 * @return A list of address objects
	 */
	public List<Address> listAddresses (String addressStoreName, String query, int firstResult, int maxResults)
	{
		WebservicesClientManager wcm = (WebservicesClientManager) Engine.instance ().getManager (
						WebservicesClientManager.ID);
		List<Address> res = new LinkedList ();
		SoapObject request = wcm.createSoapRequest ("http://aktera.iritgo.de/webservices/address",
						"listAddressesRequest");

		wcm.addRequestParameter (request, "addressStoreName", PropertyInfo.STRING_CLASS, addressStoreName);
		wcm.addRequestParameter (request, "query", PropertyInfo.STRING_CLASS, query);
		wcm.addRequestParameter (request, "firstResult", PropertyInfo.INTEGER_CLASS, firstResult);
		wcm.addRequestParameter (request, "maxResults", PropertyInfo.INTEGER_CLASS, maxResults);

		SEnvelope envelope = wcm.createEnvelopeForCurrentUser (request);

		try
		{
			Vector<Object> results = wcm.sendToCurrentServer (envelope);

			for (Object o : results.toArray ())
			{
				SoapObject so = (SoapObject) o;
				Address a = new Address ();

				a.setStoreId (addressStoreName);
				a.setId (wcm.getPropertyAsString (so, "id"));
				a.setFirstName (wcm.getPropertyAsString (so, "firstName"));
				a.setLastName (wcm.getPropertyAsString (so, "lastName"));
				a.setCompany (wcm.getPropertyAsString (so, "company"));
				a.setEmail (wcm.getPropertyAsString (so, "email"));
				a.setHomepage (wcm.getPropertyAsString (so, "homepage"));
				res.add (a);
			}
		}
		catch (Exception x)
		{
			System.out.println (x.toString ());
		}

		return res;
	}

	/**
	 * Retrieve a list of the phone numbers of the specified address.
	 *
	 * @param addressStoreName The address store which addresses should be
	 * looked up
	 * @param addressId The id of the address which phone numbers should be
	 * looked up
	 * @return A list of phone numbers
	 */
	public List<PhoneNumber> listPhoneNumbers (String addressStoreName, String addressId)
	{
		WebservicesClientManager wcm = (WebservicesClientManager) Engine.instance ().getManager (
						WebservicesClientManager.ID);
		List<PhoneNumber> res = new LinkedList ();
		SoapObject request = wcm.createSoapRequest ("http://aktera.iritgo.de/webservices/address",
						"listAddressPhoneNumbersRequest");

		wcm.addRequestParameter (request, "addressStoreName", PropertyInfo.STRING_CLASS, addressStoreName);
		wcm.addRequestParameter (request, "addressId", PropertyInfo.STRING_CLASS, addressId);

		SEnvelope envelope = wcm.createEnvelopeForCurrentUser (request);

		try
		{
			Vector<Object> results = wcm.sendToCurrentServer (envelope);

			for (Object o : results.toArray ())
			{
				SoapObject so = (SoapObject) o;
				PhoneNumber pn = new PhoneNumber ();

				pn.setCategory (wcm.getPropertyAsString (so, "category"));
				pn.setDisplayNumber (wcm.getPropertyAsString (so, "displayNumber"));
				pn.setCanonicalNumber (wcm.getPropertyAsString (so, "canonicalNumber"));
				res.add (pn);
			}
		}
		catch (Exception x)
		{
			System.out.println (x.toString ());
		}

		return res;
	}

	public String getDefaultAddressStoreName ()
	{
		WebservicesClientManager wcm = (WebservicesClientManager) Engine.instance ().getManager (
						WebservicesClientManager.ID);
		SoapObject request = wcm.createSoapRequest ("http://aktera.iritgo.de/webservices/address",
						"getDefaultAddressStoreNameRequest");
		SEnvelope envelope = wcm.createEnvelopeForCurrentUser (request);

		try
		{
			return wcm.sendToCurrentServerReturnString (envelope, "");
		}
		catch (Exception x)
		{
			System.out.println (x.toString ());
		}

		return "";
	}

	public Address getAddress (String addressStoreName, String addressId)
	{
		WebservicesClientManager wcm = (WebservicesClientManager) Engine.instance ().getManager (
						WebservicesClientManager.ID);
		SoapObject request = wcm.createSoapRequest ("http://aktera.iritgo.de/webservices/address", "getAddressRequest");

		wcm.addRequestParameter (request, "addressStoreName", PropertyInfo.STRING_CLASS, addressStoreName);
		wcm.addRequestParameter (request, "addressId", PropertyInfo.STRING_CLASS, addressId);

		SEnvelope envelope = wcm.createEnvelopeForCurrentUser (request);
		Address address = new Address ();

		try
		{
			SoapObject so = (SoapObject) wcm.sendToCurrentServerReturnObject (envelope);
			address.setId (wcm.getPropertyAsString (so, "id"));
			address.setSalutation (wcm.getPropertyAsString (so, "salutation"));
			address.setFirstName (wcm.getPropertyAsString (so, "firstName"));
			address.setLastName (wcm.getPropertyAsString (so, "lastName"));
			address.setCompany (wcm.getPropertyAsString (so, "company"));
			address.setDivision (wcm.getPropertyAsString (so, "division"));
			address.setPosition (wcm.getPropertyAsString (so, "position"));
			address.setStreet (wcm.getPropertyAsString (so, "street"));
			address.setPostalCode (wcm.getPropertyAsString (so, "postalCode"));
			address.setCity (wcm.getPropertyAsString (so, "city"));
			address.setCountry (wcm.getPropertyAsString (so, "country"));
			address.setEmail (wcm.getPropertyAsString (so, "email"));
			address.setHomepage (wcm.getPropertyAsString (so, "homepage"));
			address.setRemark (wcm.getPropertyAsString (so, "remark"));

			List<PhoneNumber> phoneNumbers = new LinkedList ();
			PhoneNumber pn = new PhoneNumber ();

			pn.setCategory (PhoneNumber.Category.B);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberB"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberB"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.BDD);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberBDD"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberBDD"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.BF);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberBF"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberBF"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.BM);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberBM"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberBM"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.BM);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberBM"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberBM"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.P);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberP"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberP"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.PF);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberPF"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberPF"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.PM);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberPM"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberPM"));
			phoneNumbers.add (pn);
			pn = new PhoneNumber ();
			pn.setCategory (PhoneNumber.Category.VOIP);
			pn.setDisplayNumber (wcm.getPropertyAsString (so, "phoneNumberVOIP"));
			pn.setCanonicalNumber (wcm.getPropertyAsString (so, "phoneNumberVOIP"));
			phoneNumbers.add (pn);
			address.setPhoneNumbers (phoneNumbers);
		}
		catch (Exception x)
		{
			System.out.println (x.toString ());
		}

		return address;
	}
}
