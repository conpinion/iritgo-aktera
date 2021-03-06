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


import java.io.Serializable;
import java.util.*;
import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Data;
import org.apache.avalon.framework.logger.Logger;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.*;
import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.Option;


@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "de.iritgo.aktera.address.AddressStoreList", query = "select s.id as id, s.name as name, s.type as type, s.position as position, s.defaultStore as defaultStore from AddressStore s where lower(s.name) like :name")
@SuppressWarnings("serial")
public abstract class AddressStore implements Serializable
{
	public enum NumberNormalizationType
	{
		NO_NORMALIZATION, ADD_INTERNATIONAL_COUNTRY_PREFIX, REMOVE_PREFIX
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	@Length(min = 1, max = 255)
	@NotNull
	protected String type;

	@Length(min = 1, max = 255)
	@NotNull
	protected String name;

	@Length(max = 255)
	protected String title;

	@Min(value = 1)
	@NotNull
	@Value ("1")
	protected Integer position;

	@NotNull
	@Value("false")
	protected Boolean systemStore;

	@NotNull
	@Value("false")
	protected Boolean defaultStore;

	@NotNull
	@Value("true")
	protected Boolean editable;

	@NotNull
	@Value("true")
	protected Boolean numberLookup;

	@NotNull
	@Value("false")
	protected Boolean emptySearchReturnsAllEntries;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Value("NO_NORMALIZATION")
	protected NumberNormalizationType numberNormalization;

	@Length(max = 255)
	protected String mainNumber;

	protected Integer internalNumberLength;

	@Inject
	@Qualifier("de.iritgo.aktera.logger.Logger")
	transient protected Logger logger;

	@Inject
	transient protected SystemConfigManager systemConfigManager;

	public AddressStore()
	{
	}

	public String getDisplayedTitle()
	{
		return StringTools.isNotTrimEmpty(title) ? title : name;
	}

	public abstract void init();

	public abstract void shutdown();

	public abstract Option<Address> findAddressByDn(Object addressDn);

	public abstract Option<Address> findAddressByLastNameOrCompany(String name);

	public abstract List<Address> findAddressByNameStartsWith(String name);

	public abstract List<Address> findAddressOfOwnerByNameStartsWith(String name, Integer ownerId);

	public abstract Option<Address> findAddressByPhoneNumber(PhoneNumber phoneNumber);

	public abstract Option<Address> findAddressByPhoneNumber(String number);

	public abstract Option<Address> findAddressOfOwnerByPhoneNumber(PhoneNumber phoneNumber, Integer ownerId);

	public abstract List<PhoneNumber> findPhoneNumbersEndingWith(String number);

	public abstract List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith(String number, Integer ownerId);

	public abstract Option<Address> findAddressByOwnerAndContactNumber(Integer ownerId, String contactNumber);

	public abstract Option<Address> findAddressByOnwerAndFirstNameOrLastNameOrCompany(Integer ownerId,
					String firstName, String lastName, String company);

	public abstract List<PhoneNumber> findAllPhoneNumbersByAddressDnSortedByCategoryList(String addressDn,
					String[] categories);

	public abstract List<Address> createAddressListing(Integer userId, String search, String orderBy,
					SortOrder orderDir, int firstResult, int maxResults);

	public abstract void updateAddress(Address address);

	public abstract Object createAddress(Address address, Integer ownerId);

	public abstract void deleteAddressWithDn(Object addressDn);

	public abstract void deleteAllAddresses();

	public abstract boolean isGlobalStore();

	public abstract long countAddressesByOwnerAndSearch(Integer userId, String search);

	public abstract boolean canBeDeleted();

	public abstract void deleteAllAddressesOfOwner(Integer userId);

	public abstract Option<Address> findAddressByPhoneNumber(String number,
			String internationalPrefix, String countryPrefix,
			String nationalPrefix, String localPrefix);

	public abstract Option<Address> findAddressOfOwnerByPhoneNumber(Integer ownerId,
			String number, String internationalPrefix, String countryPrefix,
			String nationalPrefix, String localPrefix);

	public abstract String getCategory ();

	protected String normalizeNumber (String number, String internationalPrefix, String countryPrefix,
			String nationalPrefix, String localPrefix)
	{
		if (numberNormalization == NumberNormalizationType.REMOVE_PREFIX)
		{
			return "*" + removePrefixesFromPhoneNumber(number, internationalPrefix, countryPrefix, localPrefix, nationalPrefix);
		}
		else if (numberNormalization == NumberNormalizationType.ADD_INTERNATIONAL_COUNTRY_PREFIX)
		{
			return addPrefixesFromPhoneNumber(number, internationalPrefix, countryPrefix, nationalPrefix, localPrefix);
		}
		// Do nothing with the number
		return number;
	}

	private String addPrefixesFromPhoneNumber(String number, String internationalPrefix, String countryPrefix, String nationalPrefix,
			String localPrefix)
	{
		// Starts with 00 -> we have a international number
		if (number.startsWith (internationalPrefix))
		{
			return number;
		}
		else if (number.startsWith ("+"))
		{
			number = number.substring(1);
			number = internationalPrefix + number;
			return number;
		}
		else if (number.startsWith(nationalPrefix)) // 0
		{
			number = number.substring(nationalPrefix.length());
			number = internationalPrefix + countryPrefix + number;
			return number;
		}
		// City- or firm-Number add compete prefix (e.g. 0049-231)
		return internationalPrefix + countryPrefix + localPrefix + number;
	}

	protected String removePrefixesFromPhoneNumber(String number, String internationPrefix, String countryPrefix,
			String nationalPrefix, String localPrefix)
	{
		// Remove like 0049
		if (number.startsWith(internationPrefix + countryPrefix))
		{
			number = number.substring((internationPrefix + countryPrefix).length());
		} // Remove like 0231
		else if (number.startsWith(nationalPrefix + localPrefix))
		{
			number = number.substring(nationalPrefix.length() + localPrefix.length());
		} // Remove like 231
		else if (number.startsWith(localPrefix) && (localPrefix.length() != number.length()))
		{
			number = number.substring(localPrefix.length());
		} // Remove like 0
		else if (number.startsWith(nationalPrefix))
		{
			number = number.substring(nationalPrefix.length());
		}

		return number;
	}

	public void bulkImport(Collection<Address> addresses)
	{
	}
}
