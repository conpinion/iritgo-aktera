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


import java.io.Serializable;


/**
 * @persist.persistent
 *   id="Version"
 *   name="Version"
 *   table="Version"
 *   schema="aktera"
 *   descrip="Version"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
public class Version implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** */
	private String type;

	/** */
	private String name;

	/** */
	private String version;

	/**
	 * Return the type.
	 *
	 * @persist.field
	 *   name="type"
	 *   db-name="type"
	 *   type="varchar"
	 *   length="1"
	 *   null-allowed="false"
	 *   primary-key="true"
	 *   descrip="Type"
	 *
	 * @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Set the type.
	 *
	 * @param type The new type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Return the name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="255"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Name"
	 *
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the name.
	 *
	 * @param name The new name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Return the version.
	 *
	 * @persist.field
	 *   name="version"
	 *   db-name="version"
	 *   type="varchar"
	 *   length="16"
	 *   primary-key="false"
	 *   null-allowed="false"
	 *   descrip="Version"
	 *
	 * @return The version.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Set the version.
	 *
	 * @param version The new version.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}
}
