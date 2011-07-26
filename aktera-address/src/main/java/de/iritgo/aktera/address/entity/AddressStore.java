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
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;
import org.apache.avalon.framework.logger.*;
import org.hibernate.validator.constraints.*;
import org.springframework.beans.factory.annotation.*;
import de.iritgo.aktera.configuration.*;
import de.iritgo.simplelife.constants.*;
import de.iritgo.simplelife.string.*;
import de.iritgo.simplelife.tools.*;


@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "de.iritgo.aktera.address.AddressStoreList", query = "select s.id as id, s.name as name, s.type as type, s.position as position, s.defaultStore as defaultStore from AddressStore s where lower(s.name) like :name")
@SuppressWarnings("serial")
public abstract class AddressStore implements Serializable
{
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

	@Autowired
	@Qualifier("de.iritgo.aktera.logger.Logger")
	transient protected Logger logger;

	@Autowired
	transient protected SystemConfigManager systemConfigManager;

	public String getDisplayedTitle ()
	{
		return StringTools.isNotTrimEmpty (title) ? title : name;
	}

	public abstract void init ();

	public abstract void shutdown ();

	public abstract Option<Address> findAddressByDn (Object addressDn);

	public abstract Option<Address> findAddressByLastNameOrCompany (String name);

	public abstract List<Address> findAddressByNameStartsWith (String name);

	public abstract List<Address> findAddressOfOwnerByNameStartsWith (String name, Integer ownerId);

	public abstract Option<Address> findAddressByPhoneNumber (PhoneNumber phoneNumber);

	public abstract Option<Address> findAddressByPhoneNumber (String number);

	public abstract Option<Address> findAddressOfOwnerByPhoneNumber (PhoneNumber phoneNumber, Integer ownerId);

	public abstract List<PhoneNumber> findPhoneNumbersEndingWith (String number);

	public abstract List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith (String number, Integer ownerId);

	public abstract Option<Address> findAddressByOwnerAndContactNumber (Integer ownerId, String contactNumber);

	public abstract Option<Address> findAddressByOnwerAndFirstNameOrLastNameOrCompany (Integer ownerId,
					String firstName, String lastName, String company);

	public abstract List<PhoneNumber> findAllPhoneNumbersByAddressDnSortedByCategoryList (String addressDn,
					String[] categories);

	public abstract List<Address> createAddressListing (Integer userId, String search, String orderBy,
					SortOrder orderDir, int firstResult, int maxResults);

	public abstract void updateAddress (Address address);

	public abstract Object createAddress (Address address, Integer ownerId);

	public abstract void deleteAddressWithDn (Object addressDn);

	public abstract void deleteAllAddresses ();

	public abstract boolean isGlobalStore ();

	public abstract long countAddressesByOwnerAndSearch (Integer userId, String search);

	public abstract boolean canBeDeleted ();

	public abstract void deleteAllAddressesOfOwner (Integer userId);
}
