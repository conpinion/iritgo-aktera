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

import javax.inject.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.*;

import org.hibernate.validator.constraints.*;
import org.springframework.beans.factory.annotation.*;

import de.iritgo.aktera.address.*;
import de.iritgo.aktera.address.entity.AddressStore.*;
import de.iritgo.simplelife.constants.*;
import de.iritgo.simplelife.math.*;
import de.iritgo.simplelife.tools.*;


@Configurable
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@SuppressWarnings("serial")
public class AddressDAOStore extends AddressStore
{
	/** Store category */
	@Length(min = 1, max = 255)
	@NotNull
	public String category;

	/** If true the address owner must match the searching user */
	@NotNull
	@Value("false")
	private Boolean checkOwner;

	@Inject
	transient private AddressDAO addressDAO;

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
		return addressDAO.findAddressByDn(addressDn);
	}

	@Override
	public Option<Address> findAddressByLastNameOrCompany(String name)
	{
		return addressDAO.findAddressByCategoryAndLastNameOrCompany(category, name);
	}

	@Override
	public List<Address> findAddressByNameStartsWith(String name)
	{
		return addressDAO.findAddressesByCategoryAndNameOrCompanyStartsWith(category, name);
	}

	@Override
	public List<Address> findAddressOfOwnerByNameStartsWith(String name, Integer ownerId)
	{
		if (checkOwner)
		{
			return addressDAO.findAddressesOfOwnerByCategoryAndLastNameOrCompanyStartsWith(ownerId, category, name);
		}
		else
		{
			return addressDAO.findAddressesByCategoryAndNameOrCompanyStartsWith(category, name);
		}
	}

	@Override
	public List<PhoneNumber> findAllPhoneNumbersByAddressDnSortedByCategoryList(String addressDn, String[] categories)
	{
		return addressDAO.findAllPhoneNumbersByAddressIdSortedByCategories(NumberTools.toInt(addressDn, - 1),
						categories);
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(PhoneNumber phoneNumber)
	{
		return addressDAO.findAddressByCategoryAndPhoneNumber(category, phoneNumber);
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(String number)
	{
		if (! checkOwner)
		{
			return addressDAO.findAddressByCategoryAndPhoneNumber(category, number);
		}

		return new Empty();
	}

	private Option<Address> findAddressOfOwnerByPhoneNumber(String number, Integer ownerId)
	{
		if (checkOwner)
		{
			return addressDAO.findAddressOfOwnerByCategoryAndPhoneNumber(ownerId, category, number);
		}
		else
		{
			return addressDAO.findAddressByCategoryAndPhoneNumber(category, number);
		}
	}

	@Override
	public Option<Address> findAddressOfOwnerByPhoneNumber(PhoneNumber number, Integer ownerId)
	{
		if (checkOwner)
		{
			return addressDAO.findAddressOfOwnerByCategoryAndPhoneNumber(ownerId, category, number);
		}
		else
		{
			return addressDAO.findAddressByCategoryAndPhoneNumber(category, number);
		}
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersEndingWith(String number)
	{
		if (! checkOwner)
		{
			return addressDAO.findPhoneNumbersEndingWith(number);
		}

		return new LinkedList<PhoneNumber>();
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith(String number, Integer ownerId)
	{
		if (checkOwner)
		{
			return addressDAO.findPhoneNumbersOfOwnerEndingWith(ownerId, number);
		}

		return new LinkedList<PhoneNumber>();
	}

	@Override
	public Option<Address> findAddressByOwnerAndContactNumber(Integer ownerId, String contactNumber)
	{
		if (ownerId != null && isGlobalStore())
		{
			return null;
		}
		return addressDAO.findAddressByOwnerAndCategoryAndContactNumber(ownerId, category, contactNumber);
	}

	@Override
	public Option<Address> findAddressByOnwerAndFirstNameOrLastNameOrCompany(Integer ownerId, String firstName,
					String lastName, String company)
	{
		if (ownerId != null && isGlobalStore())
		{
			return new Empty();
		}
		return addressDAO.findAddressByOwnerAndCategoryAndFirstNameOrLastNameOrCompany(ownerId, category, firstName,
						lastName, company);
	}

	@Override
	public List<Address> createAddressListing(Integer userId, String search, String orderBy, SortOrder orderDir,
					int firstResult, int maxResults)
	{
		return addressDAO.createAddressListing(userId, category, checkOwner, search, orderBy, orderDir, firstResult,
						maxResults);
	}

	@Override
	public void updateAddress(Address address)
	{
		if (editable)
		{
			addressDAO.updateAddress(address);
		}
	}

	@Override
	public Object createAddress(Address address, Integer ownerId)
	{
		if (editable)
		{
			address.setCategory(category);

			if (checkOwner)
			{
				address.setOwnerId(ownerId);
			}

			addressDAO.createAddress(address);
			return address.getId();
		}

		return null;
	}

	@Override
	public void deleteAddressWithDn(Object addressDn)
	{
		if (editable)
		{
			addressDAO.deleteAddressWithDn(addressDn);
		}
	}

	@Override
	public boolean isGlobalStore()
	{
		return ! checkOwner;
	}

	@Override
	public long countAddressesByOwnerAndSearch(Integer userId, String search)
	{
		return addressDAO.countAddressesByOwenrAndCategoryAndSearch(userId, checkOwner, category, search);
	}

	@Override
	public void deleteAllAddresses()
	{
		addressDAO.deleteAddressesByCategory(category);
	}

	@Override
	public boolean canBeDeleted()
	{
		return addressDAO.countAddressesByCategory(category) == 0;
	}

	@Override
	public void deleteAllAddressesOfOwner(Integer userId)
	{
		if (checkOwner)
		{
			addressDAO.deleteAllAddressesOfOnwerByCategory(category, userId);
		}
	}

	@Override
	public Option<Address> findAddressByPhoneNumber(String number, String countryPrefix, String localPrefix, String internationalPrefix, String nationalPrefix)
	{
		if (numberNormalization == NumberNormalizationType.REMOVE_PREFIX)
		{
			number = removePrefixesFromPhoneNumber(number, countryPrefix, localPrefix, nationalPrefix);

			logger.debug("Private DAO-Store address resolution with number: " + number);

			List<PhoneNumber> phoneNumbers = findPhoneNumbersEndingWith(number);

			for (PhoneNumber phoneNumber : phoneNumbers)
			{
				String internalNumber = phoneNumber.getInternalNumber();

				Option<Address> address = null;

				// Add like 0049
				if (internalNumber.equals(countryPrefix + number))
				{
					address = findAddressByPhoneNumber(phoneNumber);
				}

				// Add like 0231
				if (internalNumber.equals(nationalPrefix + localPrefix + number))
				{
					address = findAddressByPhoneNumber(phoneNumber);
				}

				// Add like 00 (Fallback for dummy prefix user)
				if (internalNumber.equals("00" + number))
				{
					address = findAddressByPhoneNumber(phoneNumber);
				}

				// Add like 0
				if (internalNumber.equals("0" + number))
				{
					address = findAddressByPhoneNumber(phoneNumber);
				}

				if (internalNumber.equals(number))
				{
					address = findAddressByPhoneNumber(phoneNumber);
				}

				if (address.full())
				{
					address.get().setAddressStore (this);

					return address;
				}
			}
		}
		else if (numberNormalization == NumberNormalizationType.ADD_INTERNATIONAL_COUNTRY_PREFIX)
		{
			String normNumber = normalizeNumber(number, countryPrefix, localPrefix, nationalPrefix);
			Option<Address> address = findAddressByPhoneNumber(number);
			if (address.full ())
			{
				address.get().setAddressStore (this);
				return address;
			}
			address = findAddressByPhoneNumber(normNumber);
			if (address.full ())
			{
				address.get().setAddressStore (this);
				return address;
			}
		}
		else if (numberNormalization == NumberNormalizationType.NO_NORMALIZATION)
		{
			Option<Address> address = findAddressByPhoneNumber(number);
			if (address.full ())
			{
				address.get().setAddressStore (this);
				return address;
			}
		}

		return new Empty ();
	}

	@Override
	public Option<Address> findAddressOfOwnerByPhoneNumber(Integer ownerId, String number, String countryPrefix, String localPrefix, String internationalPrefix, String nationalPrefix)
	{
		if (numberNormalization == NumberNormalizationType.REMOVE_PREFIX)
		{
			number = removePrefixesFromPhoneNumber(number, countryPrefix, localPrefix, nationalPrefix);

			logger.debug("Private DAO-Store address resolution with number: " + number);

			List<PhoneNumber> phoneNumbers = findPhoneNumbersOfOwnerEndingWith(number, ownerId);

			for (PhoneNumber phoneNumber : phoneNumbers)
			{
				String internalNumber = phoneNumber.getInternalNumber();

				Option<Address> address = null;

				// Add like 0049
				if (internalNumber.equals(countryPrefix + number))
				{
					address = findAddressOfOwnerByPhoneNumber(phoneNumber, ownerId);
				}

				// Add like 0231
				if (internalNumber.equals(nationalPrefix + localPrefix + number))
				{
					address = findAddressOfOwnerByPhoneNumber(phoneNumber, ownerId);
				}

				// Add like 00 (Fallback for dummy prefix user)
				if (internalNumber.equals("00" + number))
				{
					address = findAddressOfOwnerByPhoneNumber(phoneNumber, ownerId);
				}

				// Add like 0
				if (internalNumber.equals("0" + number))
				{
					address = findAddressOfOwnerByPhoneNumber(phoneNumber, ownerId);
				}

				if (internalNumber.equals(number))
				{
					address = findAddressOfOwnerByPhoneNumber(phoneNumber, ownerId);
				}

				if (address.full())
				{
					address.get().setAddressStore (this);

					return address;
				}
			}
		}
		else if (numberNormalization == NumberNormalizationType.ADD_INTERNATIONAL_COUNTRY_PREFIX)
		{
			String normNumber = normalizeNumber(number, countryPrefix, localPrefix, nationalPrefix);
			Option<Address> address = findAddressOfOwnerByPhoneNumber(number, ownerId);
			if (address.full ())
			{
				address.get().setAddressStore (this);
				return address;
			}
			address = findAddressOfOwnerByPhoneNumber(normNumber, ownerId);
			if (address.full ())
			{
				address.get().setAddressStore (this);
				return address;
			}
		}
		else if (numberNormalization == NumberNormalizationType.NO_NORMALIZATION)
		{
			Option<Address> address = findAddressOfOwnerByPhoneNumber(number, ownerId);
			if (address.full ())
			{
				address.get().setAddressStore (this);
				return address;
			}
		}

		return new Empty ();
	}

	public void bulkImport (Collection<Address> addresses)
	{
		addressDAO.bulkImport (addresses);
	}
}
