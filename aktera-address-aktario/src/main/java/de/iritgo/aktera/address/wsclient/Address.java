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


import java.util.List;
import de.iritgo.simplelife.collection.CollectionTools;
import de.iritgo.simplelife.math.Predicate;
import de.iritgo.simplelife.tools.Option;


public class Address
{
	private String storeId;

	private String id;

	private String salutation;

	private String firstName;

	private String lastName;

	private String company;

	private String division;

	private String position;

	private String postalCode;

	private String street;

	private String city;

	private String country;

	private String email;

	private String homepage;

	private String contactNumber;

	private String companyNumber;

	private String remark;

	private List<PhoneNumber> phoneNumbers;

	public void setStoreId(String storeId)
	{
		this.storeId = storeId;
	}

	public String getStoreId()
	{
		return storeId;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	public String getSalutation()
	{
		return salutation;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}

	public void setDivision(String division)
	{
		this.division = division;
	}

	public String getDivision()
	{
		return division;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getStreet()
	{
		return street;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCity()
	{
		return city;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getCountry()
	{
		return country;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getHomepage()
	{
		return homepage;
	}

	public void setHomepage(String homepage)
	{
		this.homepage = homepage;
	}

	public void setContactNumber(String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	public String getContactNumber()
	{
		return contactNumber;
	}

	public void setCompanyNumber(String companyNumber)
	{
		this.companyNumber = companyNumber;
	}

	public String getCompanyNumber()
	{
		return companyNumber;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public String getRemark()
	{
		return remark;
	}

	@Override
	public String toString()
	{
		return super.toString() + "[id=" + id + ",lastName=" + lastName + ",firstName=" + firstName + ",company="
						+ company + "]";
	}

	public List<PhoneNumber> getPhoneNumbers()
	{
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}

	public PhoneNumber getPhoneNumberWithCategory(final String category)
	{
		Option<PhoneNumber> phoneNumber = CollectionTools.find(phoneNumbers, new Predicate<PhoneNumber>()
		{
			public Boolean eval(PhoneNumber pn)
			{
				return category.equals(pn.getCategory());
			}
		});

		return phoneNumber.full() ? phoneNumber.get() : new PhoneNumber(category);
	}

	public PhoneNumber getPhoneNumberWithCategory(PhoneNumber.Category category)
	{
		return getPhoneNumberWithCategory(category.toString());
	}
}
