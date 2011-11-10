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
import javax.persistence.*;
import javax.persistence.Entity;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.*;
import de.iritgo.aktera.*;
import de.iritgo.aktera.configuration.*;
import de.iritgo.aktera.spring.*;
import de.iritgo.simplelife.string.*;


/**
 * @persist.persistent id="PhoneNumber" name="PhoneNumber" table="PhoneNumber"
 *                     schema="aktera" securable="true" am-bypass-allowed="true"
 */
@PublicAPI
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("serial")
public class PhoneNumber implements Serializable
{
	/** Empty phone number */
	public static PhoneNumber EMPTY = new PhoneNumber();

	/** The primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Foreign address key */
	@Column(insertable = false, updatable = false)
	private Integer addressId;

	/** Our address */
	@ManyToOne
	@JoinColumn(name = "addressId", nullable = false)
	@OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	private Address address;

	/** Phone number category (e.g. 'BN' for business number) */
	@Column(nullable = false, length = 32)
	private String category;

	/** Human readable phone number (as entered by the user) */
	@Column(length = 80)
	@Value("")
	private String number;

	/** Normalized phone number (used internally by the system) */
	@Column(length = 80)
	@Value("")
	private String internalNumber;

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
	 * Get the foreign address key.
	 *
	 * @persist.field name="addressId" db-name="addressId" type="integer"
	 *                null-allowed="false"
	 */
	public Integer getAddressId()
	{
		return addressId;
	}

	/**
	 * Set the foreign address key.
	 */
	public void setAddressId(Integer addressId)
	{
		this.addressId = addressId;
	}

	/**
	 * Get the address.
	 */
	public Address getAddress()
	{
		return address;
	}

	/**
	 * Set the address.
	 */
	public void setAddress(Address address)
	{
		this.address = address;
	}

	/**
	 * Get the phone number category.
	 *
	 * @persist.field name="category" db-name="category" type="varchar"
	 *                length="32" null-allowed="false" default-value="B"
	 * @persist.valid-value value="B" descrip="$phoneBusiness"
	 * @persist.valid-value value="BM" descrip="$phoneBusinessMobile"
	 * @persist.valid-value value="BF" descrip="$phoneBusinessFax"
	 * @persist.valid-value value="BDD" descrip="$phoneBusinessDirectDial"
	 * @persist.valid-value value="P" descrip="$phonePrivate"
	 * @persist.valid-value value="PM" descrip="$phonePrivateMobile"
	 * @persist.valid-value value="PF" descrip="$phonePrivateFax"
	 * @persist.valid-value value="VOIP" descrip="$phoneVoip"
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * Set the phone number category.
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * Get the human readable phone number.
	 *
	 * @persist.field name="number" db-name="number" type="varchar" length="80"
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * Set the human readable phone number.
	 */
	public void setNumber(String number)
	{
		SystemConfigManager systemConfigManager = (SystemConfigManager) SpringTools.getBean(SystemConfigManager.ID);

		this.number = number;
		this.internalNumber = StringTools.trim(number).replaceAll("[^0-9+]+", "");

		if (this.internalNumber.startsWith("+", 0))
		{
			this.internalNumber = systemConfigManager.getString("phone", "internationalPrefix")
							+ this.internalNumber.replaceAll("[^0-9]+", "");
		}
		else
		{
			this.internalNumber = StringTools.trim(number).replaceAll("[^0-9]+", "");
		}
	}

	/**
	 * Get the normalized phone number.
	 *
	 * @persist.field name="internalNumber" db-name="internalNumber"
	 *                type="varchar" length="80"
	 */
	public String getInternalNumber()
	{
		return internalNumber;
	}

	/**
	 * Set the normalized phone number.
	 */
	public void setInternalNumber(String internalNumber)
	{
		this.internalNumber = internalNumber;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getClass().getName() + " (id=" + id + ",addressId=" + address.getId() + ",category=" + category
						+ ",number=" + number + ",internalNumber=" + internalNumber + ")";
	}
}
