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

package de.iritgo.aktera.configuration.preferences;


import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;


/**
 * @persist.persistent
 *   id="PreferencesConfig"
 *   name="PreferencesConfig"
 *   table="PreferencesConfig"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class PreferencesConfig implements Serializable
{
	public static class PrimaryKey implements Serializable
	{
		/** */
		private static final long serialVersionUID = 1L;

		/** */
		@Column(nullable = false)
		public Integer userId;

		/** */
		@Column(nullable = false, length = 32)
		public String category;

		/** */
		@Column(nullable = false, length = 32)
		public String name;
	}

	private static final long serialVersionUID = 1L;

	/** */
	@EmbeddedId
	private PrimaryKey primaryKey = new PrimaryKey ();

	/** */
	@Column(nullable = false, length = 1)
	private String type;

	/** */
	private String value;

	/** */
	private String validValues;

	/**
	 * Return the user Id.
	 *
	 * @persist.field
	 *   name="userId"
	 *   db-name="userId"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Foreign user id"
	 *
	 * @return The user id.
	 */
	public Integer getUserId ()
	{
		return primaryKey.userId;
	}

	/**
	 * Set the user Id.
	 *
	 * @param userId The new user id.
	 */
	public void setUserId (Integer userId)
	{
		this.primaryKey.userId = userId;
	}

	/**
	 * Return the category.
	 *
	 * @persist.field
	 *   name="category"
	 *   db-name="category"
	 *   type="varchar"
	 *   length="16"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Category"
	 *
	 * @return The category.
	 */
	public String getCategory ()
	{
		return primaryKey.category;
	}

	/**
	 * Set the category.
	 *
	 * @param category The new category.
	 */
	public void setCategory (String category)
	{
		this.primaryKey.category = category;
	}

	/**
	 * Return the name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="32"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Name"
	 *
	 * @return The name.
	 */
	public String getName ()
	{
		return primaryKey.name;
	}

	/**
	 * Set the name.
	 *
	 * @param name The new name.
	 */
	public void setName (String name)
	{
		this.primaryKey.name = name;
	}

	/**
	 * Return the type.
	 *
	 * @persist.field
	 *   name="type"
	 *   db-name="type"
	 *   type="varchar"
	 *   length="1"
	 *   null-allowed="false"
	 *   descrip="Type"
	 *
	 * @return The type.
	 */
	public String getType ()
	{
		return type;
	}

	/**
	 * Set the type.
	 *
	 * @param type The new type.
	 */
	public void setType (String type)
	{
		this.type = type;
	}

	/**
	 * Return the value.
	 *
	 * @persist.field
	 *   name="value"
	 *   db-name="value"
	 *   type="text"
	 *   null-allowed="true"
	 *   descrip="Value"
	 *
	 * @return The value.
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * Set the value.
	 *
	 * @param value The new value.
	 */
	public void setValue (String value)
	{
		this.value = value;
	}

	/**
	 * Return the valid values.
	 *
	 * @persist.field
	 *   name="validValues"
	 *   db-name="validValues"
	 *   type="text"
	 *   null-allowed="true"
	 *   descrip="Valid values"
	 *
	 * @return The valid values.
	 */
	public String getValidValues ()
	{
		return validValues;
	}

	/**
	 * Set the valid value.
	 *
	 * @param validValues The new valid values.
	 */
	public void setValidValues (String validValues)
	{
		this.validValues = validValues;
	}
}
