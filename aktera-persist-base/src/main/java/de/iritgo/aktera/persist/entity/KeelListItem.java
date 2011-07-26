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

package de.iritgo.aktera.persist.entity;


import java.io.Serializable;


/**
 * This table stores individual valid values, grouped by the ListName
 * as defined in the Keel List Header table. The ItemId will be used as
 * the valid value key, and the ItemName will be used as the valid value
 * description.
 *
 * @persist.persistent
 *   id="KeelListItem"
 *   descrip="Keel List Items"
 *   name="KeelListItem"
 *   table="KeelListItem"
 *   schema="keel"
 *   am-bypass-allowed="true"
 */
public class KeelListItem implements Serializable
{
	private Integer itemId = null;

	private String listName = null;

	private String itemCode = null;

	private String itemName = null;

	private Integer sortOrder = null;

	/**
	 *
	 */
	public KeelListItem ()
	{
		super ();
	}

	/**
	 * @persist.field
	 *   db-name="ItemCode"
	 *   descrip="Item Code"
	 *   length="25"
	 *   name="ItemCode"
	 *   null-allowed="true"
	 *   type="varchar"
	 * @return
	 */
	public String getItemCode ()
	{
		return itemCode;
	}

	/**
	 * @persist.field
	 *   db-name="ItemId"
	 *   descrip="List Item ID"
	 *   read-only="true"
	 *   name="ItemId"
	 *   null-allowed="false"
	 *   auto-increment="identity"
	 *   primary-key="true"
	 *   type="integer"
	 * @return
	 */
	public Integer getItemId ()
	{
		return itemId;
	}

	/**
	 * @persist.field
	 *   db-name="ItemName"
	 *   descrip="Item Name"
	 *   length="250"
	 *   name="ItemName"
	 *   null-allowed="false"
	 *   type="varchar"
	 * @return
	 */
	public String getItemName ()
	{
		return itemName;
	}

	/**
	 *  @persist.field
	 *   db-name="ListName"
	 *   descrip="List Name"
	 *   length="40"
	 *   name="ListName"
	 *   null-allowed="false"
	 *   type="varchar"
	 * @persist.lookup
	 *   name="KeelListHeader"
	 *   field="ListName"
	 * @return
	 */
	public String getListName ()
	{
		return listName;
	}

	/**
	 * @persist.field
	 *   db-name="SortOrder"
	 *   descrip="Sort Order"
	 *   name="SortOrder"
	 *   null-allowed="true"
	 *   type="integer"
	 * @return
	 */
	public Integer getSortOrder ()
	{
		return sortOrder;
	}

	/**
	 * @param string
	 */
	public void setItemCode (String string)
	{
		itemCode = string;
	}

	/**
	 * @param integer
	 */
	public void setItemId (Integer integer)
	{
		itemId = integer;
	}

	/**
	 * @param string
	 */
	public void setItemName (String string)
	{
		itemName = string;
	}

	/**
	 * @param string
	 */
	public void setListName (String string)
	{
		listName = string;
	}

	/**
	 * @param integer
	 */
	public void setSortOrder (Integer integer)
	{
		sortOrder = integer;
	}
}
