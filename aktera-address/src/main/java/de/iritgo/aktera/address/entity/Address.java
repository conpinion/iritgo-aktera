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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import org.apache.commons.collections.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import de.iritgo.aktera.*;
import de.iritgo.simplelife.string.*;


/**
 * Address domain object.
 *
 * @persist.persistent id="Address" name="Address" table="Address"
 *                     schema="aktera" securable="true" am-bypass-allowed="true"
 */
@PublicAPI
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQuery(name = "de.iritgo.aktera.address.FindAddressByCategoryAndPhoneNumber", query = "select distinct p.address from PhoneNumber p where p.address.category = ? and p.internalNumber = ?")
@SuppressWarnings("serial")
public class Address implements Serializable
{
	/** Address categories */
	public enum Category
	{
		B, BM, BF, BDD, P, PM, PF, VOIP;
	}

	/** Adress category 'global' */
	public static final String CATEGORY_GLOBAL = "G";

	/** Adress category 'private' */
	public static final String CATEGORY_PRIVATE = "P";

	/** Primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Foreign party key (can be null for unassigned addresses) */
	@Column(insertable = true, updatable = false)
	private Integer partyId;

	/** An alternate id (e.g. a LDAP DN) */
	private transient String alternateId;

	/** Address category */
	@Column(nullable = false, length = 80)
	private String category;

	/** Salutation */
	@Column(length = 16)
	private String salutation;

	/** Last name */
	@Column(length = 255)
	private String lastName;

	/** Internal last name in lower case */
	@Column(length = 255)
	private String internalLastname;

	/** First name */
	@Column(length = 80)
	private String firstName;

	/** Company */
	@Column(length = 255)
	private String company;

	/** Company in lower case */
	@Column(length = 255)
	private String internalCompany;

	/** Position */
	@Column(length = 80)
	private String position;

	/** Division */
	@Column(length = 80)
	private String division;

	/** City */
	@Column(length = 80)
	private String city;

	/** Postal code */
	@Column(length = 32)
	private String postalCode;

	/** Country */
	@Column(length = 80)
	private String country;

	/** Street */
	@Column(length = 80)
	private String street;

	/** Email address */
	@Column(length = 255)
	private String email;

	/** Web site url */
	@Column(length = 80)
	private String web;

	/** Phone number */
	@Column(length = 80)
	private String phone;

	/** Mobile number */
	@Column(length = 80)
	private String mobile;

	/** Foreign key of the owning object (e.g. a user for a private entry) */
	private Integer ownerId;

	/** Remarks */
	private String remark;

	/** Contact number */
	@Column(length = 80)
	private String contactNumber;

	/** Company number */
	@Column(length = 80)
	private String companyNumber;

	/** Source system id */
	@Column(length = 80)
	private String sourceSystemId;

	/** Source system client */
	@Column(length = 80)
	private String sourceSystemClient;

	/** Phone numbers */
	@OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@Cascade(value = org.hibernate.annotations.CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<PhoneNumber> phoneNumbers = new HashSet<PhoneNumber>();

	/** The address store. Calculated at runtime */
	@Transient
	private AddressStore addressStore;

	/** The address store name. Calculated at runtime */
	@Transient
	private String addressStoreName;

	/**
	 * Create a default address.
	 */
	public Address()
	{
	}

	/**
	 * Create a new address.
	 *
	 * @param lastName The last name
	 */
	public Address(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Get the primary key.
	 *
	 * @persist.field name="id" db-name="id" type="integer" primary-key="true"
	 *                null-allowed="false" auto-increment="identity"
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * Set the primary key.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}

	/**
	 * Get the foreign party key.
	 *
	 * @persist.field name="partyId" db-name="partyId" type="integer"
	 */
	public Integer getPartyId()
	{
		return partyId;
	}

	/**
	 * Set the foreign party key.
	 */
	public void setPartyId(Integer partyId)
	{
		this.partyId = partyId;
	}

	/**
	 * Get the alternate id.
	 */
	public String getAlternateId()
	{
		return alternateId;
	}

	/**
	 * Set the alternate id.
	 */
	public void setAlternateId(String alteranteId)
	{
		this.alternateId = alteranteId;
	}

	/**
	 * Get the address id or (if this is null) it's alternate id.
	 *
	 * @return The address id or alternate id
	 */
	public Object getAnyId()
	{
		if (id != null)
		{
			return id;
		}

		return alternateId;
	}

	/**
	 * Get the address category.
	 *
	 * @persist.field name="category" db-name="category" type="varchar"
	 *                length="80" null-allowed="false" default-value="G"
	 * @persist.valid-value value="G" descrip="$global"
	 * @persist.valid-value value="P" descrip="$private"
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * Set the address category.
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * Get the salutation.
	 *
	 * @persist.field name="salutation" db-name="salutation" type="varchar"
	 *                length="16"
	 */
	public String getSalutation()
	{
		return salutation;
	}

	/**
	 * Set the salutation.
	 */
	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	/**
	 * Get the last name.
	 *
	 * @persist.field name="lastName" db-name="lastName" type="varchar"
	 *                length="255"
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Set the last name.
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
		this.internalLastname = StringTools.trim(lastName).toLowerCase();
	}

	/**
	 * Get the last internal last name.
	 *
	 * @persist.field name="internalLastname" db-name="internalLastname"
	 *                type="varchar" length="255"
	 */
	public String getInternalLastname()
	{
		return internalLastname;
	}

	/**
	 * Set the last internal last name.
	 */
	public void setInternalLastname(String internalLastname)
	{
		this.internalLastname = internalLastname;
	}

	/**
	 * Get the first name.
	 *
	 * @persist.field name="firstName" db-name="firstName" type="varchar"
	 *                length="80"
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Set the first name.
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Get the company.
	 *
	 * @persist.field name="company" db-name="company" type="varchar"
	 *                length="255"
	 */
	public String getCompany()
	{
		return company;
	}

	/**
	 * Set the company.
	 */
	public void setCompany(String company)
	{
		this.company = company;
		this.internalCompany = StringTools.trim(company).toLowerCase();
	}

	/**
	 * Get the internal company.
	 *
	 * @persist.field name="internalCompany" db-name="internalCompany"
	 *                type="varchar" length="255"
	 */
	public String getInternalCompany()
	{
		return internalCompany;
	}

	/**
	 * Set the internal company.
	 */
	public void setInternalCompany(String internalCompany)
	{
		this.internalCompany = internalCompany;
	}

	/**
	 * Get the position in the company.
	 *
	 * @persist.field name="position" db-name="position" type="varchar"
	 *                length="80"
	 */
	public String getPosition()
	{
		return position;
	}

	/**
	 * Set the position in the company.
	 */
	public void setPosition(String position)
	{
		this.position = position;
	}

	/**
	 * Get the division.
	 *
	 * @persist.field name="division" db-name="division" type="varchar"
	 *                length="80"
	 */
	public String getDivision()
	{
		return division;
	}

	/**
	 * Set the division.
	 */
	public void setDivision(String division)
	{
		this.division = division;
	}

	/**
	 * Get the street.
	 *
	 * @persist.field name="street" db-name="street" type="varchar" length="80"
	 */
	public String getStreet()
	{
		return street;
	}

	/**
	 * Set the street.
	 */
	public void setStreet(String street)
	{
		this.street = street;
	}

	/**
	 * Get the city
	 *
	 * @persist.field name="city" db-name="city" type="varchar" length="80"
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * Set the city.
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * Get the country
	 *
	 * @persist.field name="country" db-name="country" type="varchar"
	 *                length="80"
	 */
	public String getCountry()
	{
		return country;
	}

	/**
	 * Set the country.
	 */
	public void setCountry(String country)
	{
		this.country = country;
	}

	/**
	 * Get the postal code.
	 *
	 * @persist.field name="postalCode" db-name="postalCode" type="varchar"
	 *                length="32"
	 */
	public String getPostalCode()
	{
		return postalCode;
	}

	/**
	 * Set the postal code.
	 */
	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	/**
	 * Get the email address.
	 *
	 * @persist.field name="email" db-name="email" type="varchar" length="255"
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * Set the email address.
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * Get the web url.
	 *
	 * @persist.field name="web" db-name="web" type="varchar" length="80"
	 */
	public String getWeb()
	{
		return web;
	}

	/**
	 * Set the web url.
	 */
	public void setWeb(String web)
	{
		this.web = web;
	}

	/**
	 * Get the phone number.
	 *
	 * @persist.field name="phone" db-name="phone" type="varchar" length="80"
	 */
	public String getPhone()
	{
		return phone;
	}

	/**
	 * Set the phone number.
	 */
	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	/**
	 * Get the mobile number.
	 *
	 * @persist.field name="mobile" db-name="mobile" type="varchar" length="80"
	 */
	public String getMobile()
	{
		return mobile;
	}

	/**
	 * Set the mobile number.
	 */
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	/**
	 * Get the foreign owner key.
	 *
	 * @persist.field name="ownerId" db-name="ownerId" type="integer"
	 */
	public Integer getOwnerId()
	{
		return ownerId;
	}

	/**
	 * Set the foreign owner key.
	 */
	public void setOwnerId(Integer ownerId)
	{
		this.ownerId = ownerId;
	}

	/**
	 * Get the remark.
	 *
	 * @persist.field name="remark" db-name="remark" type="text"
	 */
	public String getRemark()
	{
		return remark;
	}

	/**
	 * Set the remark.
	 */
	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	/**
	 * Get the internal number.
	 *
	 * @persist.field name="contactNumber" db-name="contactNumber"
	 *                type="varchar" length="80"
	 */
	public String getContactNumber()
	{
		return contactNumber;
	}

	/**
	 * Set the contact number.
	 */
	public void setContactNumber(String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	/**
	 * Get the company number.
	 *
	 * @persist.field name="companyNumber" db-name="companyNumber"
	 *                type="varchar" length="80"
	 */
	public String getCompanyNumber()
	{
		return companyNumber;
	}

	/**
	 * Set the company number.
	 */
	public void setCompanyNumber(String companyNumber)
	{
		this.companyNumber = companyNumber;
	}

	/**
	 * Get the id of the source system. This is used for data imports to store a
	 * handle to the originating system.
	 *
	 * @persist.field name="sourceSystemId" db-name="sourceSystemId"
	 *                type="varchar" length="80"
	 */
	public String getSourceSystemId()
	{
		return sourceSystemId;
	}

	/**
	 * Set the data source system id.
	 */
	public void setSourceSystemId(String sourceSystemId)
	{
		this.sourceSystemId = sourceSystemId;
	}

	/**
	 * Get the client of the source system. This is used for data imports to
	 * store a handle to the originating system.
	 *
	 * @persist.field name="sourceSystemClient" db-name="sourceSystemClient"
	 *                type="varchar" length="80"
	 */
	public String getSourceSystemClient()
	{
		return sourceSystemClient;
	}

	/**
	 * Set the data source system client.
	 */
	public void setSourceSystemClient(String sourceSystemClient)
	{
		this.sourceSystemClient = sourceSystemClient;
	}

	/**
	 * Get the phone numbers.
	 *
	 * @return A set of phone numbers.
	 *
	 * @hibernate.set role="phoneNumbers" outer-join="true" cascade="all"
	 *                inverse="true"
	 * @hibernate.collection-key column="addressId"
	 * @hibernate.collection-one-to-many
	 *                                   class="de.iritgo.aktera.address.entity.PhoneNumber"
	 */
	public Set<PhoneNumber> getPhoneNumbers()
	{
		return phoneNumbers;
	}

	/**
	 * Set the phone numbers.
	 */
	public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}

	public Set<PhoneNumber> getPhoneNumbersDetached()
	{
		Set<PhoneNumber> res = new HashSet<PhoneNumber>();

		for (PhoneNumber number : phoneNumbers)
		{
			PhoneNumber copy = new PhoneNumber();

			copy.setId(number.getId());
			copy.setAddressId(number.getAddressId());
			copy.setCategory(number.getCategory());
			copy.setNumber(number.getNumber());
			copy.setInternalNumber(number.getInternalNumber());
			res.add(copy);
		}

		return res;
	}

	public void addPhoneNumber(PhoneNumber phoneNumber)
	{
		phoneNumber.setAddress(this);
		phoneNumbers.add(phoneNumber);
	}

	public PhoneNumber getPhoneNumberByCategory(final String category)
	{
		PhoneNumber number = (PhoneNumber) CollectionUtils.find(phoneNumbers, new Predicate()
		{
			public boolean evaluate(Object o)
			{
				return ((PhoneNumber) o).getCategory().equals(category);
			}
		});

		if (number == null)
		{
			number = new PhoneNumber();
			number.setCategory(category);
			number.setAddress(this);
			phoneNumbers.add(number);
		}

		return number;
	}

	public PhoneNumber getPhoneNumber(String category)
	{
		return getPhoneNumberByCategory(category);
	}

	public PhoneNumber getPhoneNumber(Category category)
	{
		return getPhoneNumberByCategory(category.toString());
	}

	public void setAddressStore(AddressStore addressStore)
	{
		this.addressStore = addressStore;
	}

	public AddressStore getAddressStore()
	{
		return addressStore;
	}

	@Override
	public String toString()
	{
		return getClass().getName() + " (id=" + id + ",partyId=" + partyId + ",lastName=" + lastName + ",company="
						+ company + ")";
	}
}
