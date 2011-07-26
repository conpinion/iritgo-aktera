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
 * The Keel List Header defines groups of lists, for use as valid
 * values in multi-valued fields. This table stores the name, friendly
 * name, and sort order (Y/N). If UseSortOrder = Y, then the Keel List
 * items (below) are ordered by the SortOrder column. If UseSortOrder
 * N, then the ItemName column is used to sort (alphabetically).
 *
 *
 * @persist.persistent
 *   id="KeelListHeader"
 *   descrip="Keel List Header"
 *   name="KeelListHeader"
 *   table="KeelListHeader"
 *   schema="keel"
 *   am-bypass-allowed="true"
 * @author Michael Nash (mnash@jglobalonline.com)
 */
public class KeelListHeader implements Serializable
{
	private String listName = null;

	private String friendlyListName = null;

	private Boolean useSortOrder = null;

	/**
	 *
	 */
	public KeelListHeader ()
	{
		super ();
	}

	/**
	 * @persist.field
	 *   db-name="FriendlyListName"
	 *   descrip="Friendly List Name"
	 *   length="150"
	 *   name="FriendlyListName"
	 *   null-allowed="true"
	 *   type="varchar"
	 * @return The "friendly" name of this list
	 */
	public String getFriendlyListName ()
	{
		return friendlyListName;
	}

	/**
	 * @persist.field
	 *   db-name="ListName"
	 *   descrip="List Name"
	 *   length="40"
	 *   name="ListName"
	 *   null-allowed="false"
	 *   primary-key="true"
	 *   type="varchar"
	 * @return
	 */
	public String getListName ()
	{
		return listName;
	}

	/**
	 * @persist.field
	 *   db-name="UseSortOrder"
	 *   descrip="Use Sort Order?"
	 *   length="1"
	 *   name="UseSortOrder"
	 *   null-allowed="false"
	 *   type="varchar"
	 * @persist.valid-value
	 *   value="N"
	 *   descrip="No"
	 * @persist.valid-value
	 *   value="Y"
	 *   descrip="Yes"
	 * @return
	 */
	public Boolean getUseSortOrder ()
	{
		return useSortOrder;
	}

	/**
	 * @param string
	 */
	public void setFriendlyListName (String string)
	{
		friendlyListName = string;
	}

	/**
	 * @param string
	 */
	public void setListName (String string)
	{
		listName = string;
	}

	/**
	 * @param boolean1
	 */
	public void setUseSortOrder (Boolean boolean1)
	{
		useSortOrder = boolean1;
	}
}
