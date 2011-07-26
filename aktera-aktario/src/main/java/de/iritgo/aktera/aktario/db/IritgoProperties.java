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
 *   id="IritgoProperties"
 *   name="IritgoProperties"
 *   table="IritgoProperties"
 *   schema="aktera"
 *   descrip="IritgoProperties"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
public class IritgoProperties implements Serializable
{
	/** */
	private String name;

	/** */
	private String value;

	/**
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="80"
	 *   primary-key="true"
	 *   null-allowed="false"
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
	 *   name="value"
	 *   db-name="value"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 */
	public void setValue (String value)
	{
		this.value = value;
	}
}
