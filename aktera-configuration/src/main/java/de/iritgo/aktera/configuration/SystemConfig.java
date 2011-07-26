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

package de.iritgo.aktera.configuration;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * System configuration.
 *
 * @persist.persistent
 *   id="SystemConfig"
 *   name="SystemConfig"
 *   table="SystemConfig"
 *   schema="aktera"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class SystemConfig implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Configuration value category */
	@Column(unique = true, nullable = false, length = 16)
	private String category;

	/** Configuration value name */
	@Column(unique = true, nullable = false, length = 255)
	private String name;

	/** Configuration value type */
	@Column(nullable = false, length = 1)
	private String type;

	/** Configuration value */
	private String value;

	/** Valid values for multi values */
	private String validValues;

	/**
	 * Initialize a new system config.
	 */
	public SystemConfig ()
	{
	}

	/**
	 * Initialize a new system config.
	 *
	 * @param category The config category
	 * @param name The config name
	 * @param type The config type
	 * @param value The config value
	 */
	public SystemConfig (String category, String name, String type, String value)
	{
		this.category = category;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	/**
	 * Get the primary key.
	 *
	 * @persist.field
	 *   name="id"
	 *   db-name="id"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   auto-increment="identity"
	 */
	public Integer getId ()
	{
		return id;
	}

	/**
	 * Set the primary key.
	 */
	public void setId (Integer id)
	{
		this.id = id;
	}

	/**
	 * Get the value category.
	 *
	 * @persist.field
	 *   name="category"
	 *   db-name="category"
	 *   type="varchar"
	 *   length="16"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public String getCategory ()
	{
		return category;
	}

	/**
	 * Set the category.
	 */
	public void setCategory (String category)
	{
		this.category = category;
	}

	/**
	 * Get the value name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="255"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Get the value type.
	 *
	 * @persist.field
	 *   name="type"
	 *   db-name="type"
	 *   type="varchar"
	 *   length="1"
	 *   null-allowed="false"
	 */
	public String getType ()
	{
		return type;
	}

	/**
	 * Set the value type.
	 */
	public void setType (String type)
	{
		this.type = type;
	}

	/**
	 * Get the configuration value.
	 *
	 * @persist.field
	 *   name="value"
	 *   db-name="value"
	 *   type="text"
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * Set the configuration value.
	 */
	public void setValue (String value)
	{
		this.value = value;
	}

	/**
	 * Get the valid values.
	 *
	 * @persist.field
	 *   name="validValues"
	 *   db-name="validValues"
	 *   type="text"
	 */
	public String getValidValues ()
	{
		return validValues;
	}

	/**
	 * Set the valid value.
	 */
	public void setValidValues (String validValues)
	{
		this.validValues = validValues;
	}
}
