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

package de.iritgo.aktera.script;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * Script domain objects.
 *
 * @persist.persistent
 *   id="Script"
 *   name="Script"
 *   table="Script"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class Script implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** The primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** The script name */
	@Column(nullable = false, length = 255)
	private String name;

	/** The script display name */
	@Column(nullable = false, length = 255)
	private String displayName;

	/** The script code */
	private String code;

	/** The script language */
	@Column(nullable = false, length = 32)
	private String language;

	/** A description of the script */
	private String description;

	/** The script author */
	@Column(length = 255)
	private String author;

	/** Copyright information */
	@Column(length = 255)
	private String copyright;

	/** Version information */
	@Column(length = 80)
	private String version;

	/**
	 * Initialize a new script.
	 */
	public Script()
	{
	}

	/**
	 * Initialize a new script.
	 *
	 * @param name The script name
	 * @param code String code
	 * @param language The script language
	 */
	public Script(String name, String code, String language)
	{
		this.name = name;
		this.language = language;
		this.code = code;
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
	 * Get the script name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="255"
	 *   null-allowed="false"
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the script name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the script display name.
	 *
	 * @persist.field
	 *   name="displayName"
	 *   db-name="displayName"
	 *   type="varchar"
	 *   length="255"
	 *   null-allowed="false"
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Set the script display name.
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * Get the script code.
	 *
	 * @persist.field
	 *   name="code"
	 *   db-name="code"
	 *   type="text"
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * Set the script code.
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * Get the script language.
	 *
	 * @persist.field
	 *   name="language"
	 *   db-name="language"
	 *   type="varchar"
	 *   length="32"
	 *   null-allowed="false"
	 *   default-value="groovy"
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * Set the script language.
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	/**
	 * Get the description.
	 *
	 * @persist.field
	 *   name="description"
	 *   db-name="description"
	 *   type="text"
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Set the description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Get the script author.
	 *
	 * @persist.field
	 *   name="author"
	 *   db-name="author"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getAuthor()
	{
		return author;
	}

	/**
	 * Set the script name.
	 */
	public void setAuthor(String author)
	{
		this.author = author;
	}

	/**
	 * Get the copyright information.
	 *
	 * @persist.field
	 *   name="copyright"
	 *   db-name="copyright"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getCopyright()
	{
		return copyright;
	}

	/**
	 * Set the copyright information.
	 */
	public void setCopyright(String copyright)
	{
		this.copyright = copyright;
	}

	/**
	 * Get the version information.
	 *
	 * @persist.field
	 *   name="version"
	 *   db-name="version"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Set the version information.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}
}
