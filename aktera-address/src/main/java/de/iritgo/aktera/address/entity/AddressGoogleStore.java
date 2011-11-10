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


import java.io.*;
import java.net.*;
import java.net.URL;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.*;
import org.springframework.beans.factory.annotation.*;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.*;
import com.google.gdata.data.contacts.*;
import com.google.gdata.util.*;
import de.iritgo.simplelife.constants.*;
import de.iritgo.simplelife.tools.*;


@Configurable
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@SuppressWarnings("serial")
public class AddressGoogleStore extends AddressStore
{
	private static Map<String, String> phoneNumberTypes = new HashMap();

	static
	{
		phoneNumberTypes.put("http://schemas.google.com/g/2005#work", "B");
		phoneNumberTypes.put("http://schemas.google.com/g/2005#mobile", "BM");
		phoneNumberTypes.put("http://schemas.google.com/g/2005#home", "P");
	}

	/** The server URL */
	@Length(min = 1, max = 255)
	@NotNull
	private String url;

	/** Authentication user name */
	@Length(min = 1, max = 10)
	@NotNull
	private String authUser;

	/** Authentication password */
	@Length(min = 1, max = 255)
	@NotNull
	private String authPassword;

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
		Option<Address> address = new Empty();
		try
		{
			ContactsService myService = new ContactsService("unknown");
			myService.setUserCredentials(authUser, authPassword);
			URL feedUrl = new URL(addressDn.toString());
			ContactEntry entry = myService.getEntry(feedUrl, ContactEntry.class);
			address = new Full(createAddressFromGoogleEntry(entry));
		}
		catch (AuthenticationException x)
		{
		}
		catch (MalformedURLException x)
		{
		}
		catch (IOException x)
		{
		}
		catch (ServiceException x)
		{
		}
		return address;
	}

	@Override
	public Option<Address> findAddressByLastNameOrCompany(String name)
	{
		return new Empty();
	}

	@Override
	public List<Address> findAddressByNameStartsWith(String name)
	{
		List<Address> list = new LinkedList<Address>();

		return list;
	}

	@Override
	public List<Address> findAddressOfOwnerByNameStartsWith(String name, Integer ownerId)
	{
		return findAddressByNameStartsWith(name);
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(PhoneNumber phoneNumber)
	{
		return new Empty();
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(String number)
	{
		return new Empty();
	}

	@Override
	public Option<Address> findAddressOfOwnerByPhoneNumber(PhoneNumber phoneNumber, Integer ownerId)
	{
		return new Empty();
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersEndingWith(String number)
	{
		List<PhoneNumber> list = new LinkedList<PhoneNumber>();

		return list;
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith(String number, Integer ownerId)
	{
		List<PhoneNumber> list = new LinkedList<PhoneNumber>();

		return list;
	}

	@Override
	public Option<Address> findAddressByOwnerAndContactNumber(Integer ownerId, String contactNumber)
	{
		return new Empty();
	}

	@Override
	public Option<Address> findAddressByOnwerAndFirstNameOrLastNameOrCompany(Integer ownerId, String firstName,
					String lastName, String company)
	{
		return new Empty();
	}

	@Override
	public List<PhoneNumber> findAllPhoneNumbersByAddressDnSortedByCategoryList(String addressDn, String[] categories)
	{
		return new LinkedList();
	}

	@Override
	public List<Address> createAddressListing(Integer userId, String search, String orderBy, SortOrder orderDir,
					int firstResult, int maxResults)
	{
		List<Address> res = new LinkedList<Address>();

		try
		{
			ContactsService myService = new ContactsService("unknown");

			myService.setUserCredentials(authUser, authPassword);

			URL feedUrl = new URL(url);
			Query myQuery = new Query(feedUrl);

			myQuery.setStartIndex(firstResult + 1);
			myQuery.setMaxResults(maxResults);

			ContactFeed resultFeed = myService.query(myQuery, ContactFeed.class);

			for (ContactEntry entry : resultFeed.getEntries())
			{
				Address address = createAddressFromGoogleEntry(entry);

				res.add(address);
			}
		}
		catch (MalformedURLException x)
		{
		}
		catch (IOException x)
		{
		}
		catch (ServiceException x)
		{
		}

		return res;
	}

	@Override
	public void updateAddress(Address address)
	{
	}

	@Override
	public Object createAddress(Address address, Integer ownerId)
	{
		return null;
	}

	@Override
	public void deleteAddressWithDn(Object addressDn)
	{
	}

	@Override
	public long countAddressesByOwnerAndSearch(Integer userId, String search)
	{
		long count = 0;

		try
		{
			ContactsService myService = new ContactsService("unknown");

			myService.setUserCredentials(authUser, authPassword);

			URL feedUrl = new URL(url);
			Query myQuery = new Query(feedUrl);
			ContactFeed resultFeed = myService.query(myQuery, ContactFeed.class);

			count = resultFeed.getTotalResults();
		}
		catch (MalformedURLException x)
		{
		}
		catch (IOException x)
		{
		}
		catch (ServiceException x)
		{
		}

		return count;
	}

	@Override
	public void deleteAllAddresses()
	{
	}

	@Override
	public boolean isGlobalStore()
	{
		return true;
	}

	private Address createAddressFromGoogleEntry(ContactEntry entry)
	{
		Address address = new Address();

		address.setAlternateId(entry.getId());
		address.setFirstName(entry.getName().getGivenName().getValue());
		address.setLastName(entry.getName().getFamilyName().getValue());

		if (entry.getOrganizations().size() > 0)
		{
			address.setCompany(entry.getOrganizations().get(0).getOrgName().getValue());
		}

		for (com.google.gdata.data.extensions.PhoneNumber pn : entry.getPhoneNumbers())
		{
			String category = phoneNumberTypes.get(pn.getRel());

			if (category != null)
			{
				PhoneNumber number = new PhoneNumber();

				address.addPhoneNumber(number);
				number.setCategory(category);
				number.setNumber(pn.getPhoneNumber());
			}
		}

		return address;
	}

	@Override
	public boolean canBeDeleted()
	{
		return true;
	}

	@Override
	public void deleteAllAddressesOfOwner(Integer userId)
	{
	}
}
