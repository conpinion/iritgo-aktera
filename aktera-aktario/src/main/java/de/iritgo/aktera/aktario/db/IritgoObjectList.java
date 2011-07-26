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
 *   id="IritgoObjectList"
 *   name="IritgoObjectList"
 *   table="IritgoObjectList"
 *   schema="aktera"
 *   descrip="IritgoObjectList"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
public class IritgoObjectList implements Serializable
{
	/** */
	private String type;

	/** */
	private Long id;

	/** */
	private String attribute;

	/** */
	private String elemType;

	/** */
	private Long elemId;

	/**
	 * @persist.field
	 *   name="id"
	 *   db-name="id"
	 *   type="bigint"
	 *   null-allowed="false"
	 *   primary-key="true"
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
	 *   name="type"
	 *   db-name="type"
	 *   type="varchar"
	 *   null-allowed="false"
	 *   primary-key="true"
	 *   length="64"
	 */
	public String getType ()
	{
		return type;
	}

	/**
	 */
	public void setType (String type)
	{
		this.type = type;
	}

	/**
	 * @persist.field
	 *   name="attribute"
	 *   db-name="attribute"
	 *   type="varchar"
	 *   length="64"
	 *   null-allowed="false"
	 *   primary-key="true"
	 */
	public String getAttribute ()
	{
		return attribute;
	}

	/**
	 */
	public void setAttribute (String attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * @persist.field
	 *   name="elemType"
	 *   db-name="elemType"
	 *   type="varchar"
	 *   length="64"
	 *   null-allowed="false"
	 *   primary-key="true"
	 */
	public String getElemType ()
	{
		return elemType;
	}

	/**
	 */
	public void setElemType (String elemType)
	{
		this.elemType = elemType;
	}

	/**
	 * @persist.field
	 *   name="elemId"
	 *   db-name="elemId"
	 *   type="bigint"
	 *   null-allowed="false"
	 *   primary-key="true"
	 */
	public Long getElemId ()
	{
		return elemId;
	}

	/**
	 */
	public void setElemId (Long elemId)
	{
		this.elemId = elemId;
	}
}
