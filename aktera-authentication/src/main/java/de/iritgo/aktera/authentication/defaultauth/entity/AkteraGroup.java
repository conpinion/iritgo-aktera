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
import de.iritgo.simplelife.string.StringTools;
import java.io.Serializable;


/**
 * User groups.
 *
 * @persist.persistent
 *   id="AkteraGroup"
 *   name="AkteraGroup"
 *   table="AkteraGroup"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class AkteraGroup implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Standard group of administrators */
	public static final String GROUP_NAME_ADMINISTRATOR = "administrator";

	/** Standard group of managers */
	public static final String GROUP_NAME_MANAGER = "manager";

	/** Standard group of users */
	public static final String GROUP_NAME_USER = "user";

	/** Group primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Group name */
	@Column(nullable = false, length = 255)
	private String name;

	/** True if the group cannot be deleted */
	private Boolean protect;

	/** Group display name */
	@Column(nullable = true, length = 255)
	private String title;

	/** True if the group should be visible in group lists */
	private Boolean visible;

	/**
	 * Get the group primary key.
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
	 * Set the group primary key.
	 */
	public void setId (Integer id)
	{
		this.id = id;
	}

	/**
	 * Get the group name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="255"
	 *   null-allowed="false"
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the group name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Get the protected flag.
	 *
	 * @persist.field
	 *   name="protect"
	 *   db-name="protect"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getProtect ()
	{
		return protect;
	}

	/**
	 * Set the protected flag.
	 */
	public void setProtect (Boolean protect)
	{
		this.protect = protect;
	}

	/**
	 * Get the group title.
	 *
	 * @persist.field
	 *   name="title"
	 *   db-name="title"
	 *   type="varchar"
	 *   length="255"
	 *   null-allowed="true"
	 */
	public String getTitle ()
	{
		return title;
	}

	/**
	 * Set the group title.
	 */
	public void setTitle (String title)
	{
		this.title = title;
	}

	/**
	 * True if the group should be visible in group lists.
	 *
	 * @persist.field
	 *   name="visible"
	 *   db-name="visible"
	 *   type="boolean"
	 *   default-value="true"
	 */
	public Boolean getVisible ()
	{
		return visible;
	}

	/**
	 * Set the visible flag.
	 */
	public void setVisible (Boolean visible)
	{
		this.visible = visible;
	}

	public String getDisplayName ()
	{
		return StringTools.isNotTrimEmpty (title) ? title : name;
	}
}
