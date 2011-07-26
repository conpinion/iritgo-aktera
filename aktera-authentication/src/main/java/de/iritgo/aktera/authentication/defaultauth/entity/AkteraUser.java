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

package de.iritgo.aktera.authentication.defaultauth.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @persist.persistent id="user" name="user" table="keelusers" securable="true"
 *                     schema="keel" am-bypass-allowed="true"
 */
@Entity
@Table(name = "keelusers")
public class AkteraUser
{
	/** Name of the administrator user */
	public static final String USER_NAME_ADMINISTRATOR = "admin";

	/** Name of the manager user */
	public static final String USER_NAME_MANAGER = "manager";

	/** Id of the anonymous user */
	public static final Integer ANONYMOUS_ID = 0;

	/** Id of the administrator user */
	public static final Integer ADMIN_ID = 1;

	/** Id of the manager user */
	public static final Integer MANAGER_ID = 2;

	/** Primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uniqid")
	private Integer uid;

	/** The system name */
	@Column(name = "userName", nullable = false, length = 80)
	private String name;

	/** Email address */
	@Column(length = 132)
	private String email;

	/** Password */
	@Column(name = "passwd", nullable = false, length = 255)
	private String password;

	/** LDAP name */
	@Column(length = 120)
	private String ldapName;

	/**
	 * Get the primary key.
	 *
	 * @persist.field name="uid" db-name="uniqid" type="integer"
	 *                null-allowed="false" primary-key="true"
	 *                auto-increment="identity"
	 */
	public Integer getUid ()
	{
		return uid;
	}

	/**
	 * Get the primary key.
	 */
	public Integer getId ()
	{
		return uid;
	}

	/**
	 * Set the primary key.
	 */
	public void setUid (Integer id)
	{
		uid = id;
	}

	/**
	 * Set the primary key.
	 */
	public void setId (Integer id)
	{
		uid = id;
	}

	/**
	 * Set the user name.
	 *
	 * @persist.field name="name" db-name="userName" type="varchar" length="80"
	 *                null-allowed="false"
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the user name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Get the password.
	 *
	 * @persist.field name="password" db-name="passwd" type="varchar"
	 *                length="255"
	 */
	public String getPassword ()
	{
		return password;
	}

	/**
	 * Set the password.
	 */
	public void setPassword (String password)
	{
		this.password = password;
	}

	/**
	 * Get the user's email address.
	 *
	 * @persist.field name="email" db-name="email" type="varchar" length="132"
	 */
	public String getEmail ()
	{
		return email;
	}

	/**
	 * Set the user's email address.
	 */
	public void setEmail (String email)
	{
		this.email = email;
	}

	/**
	 * Set the ldap name.
	 *
	 * @persist.field name="ldapName" db-name="ldapName" type="varchar"
	 *                length="120"
	 */
	public String getLdapName ()
	{
		return ldapName;
	}

	/**
	 * Set the LDAP name.
	 */
	public void setLdapName (String ldapName)
	{
		this.ldapName = ldapName;
	}
}
