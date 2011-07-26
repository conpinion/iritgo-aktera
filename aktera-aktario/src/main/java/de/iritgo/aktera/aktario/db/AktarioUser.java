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

package de.iritgo.aktera.aktario.db;


import java.io.Serializable;


/**
 * @persist.persistent
 *   id="AktarioUser"
 *   name="AktarioUser"
 *   table="AktarioUser"
 *   schema="aktera"
 *   descrip="AktarioUser"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
public class AktarioUser implements Serializable
{
	/** */
	private Long id;

	/** */
	private String name;

	/** */
	private String fullName;

	/** */
	private String password;

	/** */
	private String email;

	/** */
	private Integer role;

	/**
	 * @persist.field
	 *   name="id"
	 *   db-name="id"
	 *   type="bigint"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public Long getId ()
	{
		return id;
	}

	/**
	 */
	public void setId (Long id)
	{
		this.id = id;
	}

	/**
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * @persist.field
	 *   name="fullName"
	 *   db-name="fullName"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getFullName ()
	{
		return fullName;
	}

	/**
	 */
	public void setFullName (String fullName)
	{
		this.fullName = fullName;
	}

	/**
	 * @persist.field
	 *   name="password"
	 *   db-name="password"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getPassword ()
	{
		return password;
	}

	/**
	 */
	public void setPassword (String password)
	{
		this.password = password;
	}

	/**
	 * @persist.field
	 *   name="email"
	 *   db-name="email"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getEmail ()
	{
		return email;
	}

	/**
	 */
	public void setEmail (String email)
	{
		this.email = email;
	}

	/**
	 * @persist.field
	 *   name="role"
	 *   db-name="role"
	 *   type="integer"
	 */
	public Integer getRole ()
	{
		return role;
	}

	/**
	 */
	public void setRole (Integer role)
	{
		this.role = role;
	}
}
