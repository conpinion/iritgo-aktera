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


/**
 * Groups are the equivilant of "roles" in J2EE terminology
 * Again, the actual implementation might contain many more
 * fields of information, but this is all we require.
 *
 * @persist.persistent
 *   id="usergroup"
 *   name="usergroup"
 *   securable="true"
 *   table="keelgroups"
 *   descrip="Security Groups"
 *   schema="keel"
 *   am-bypass-allowed="true"
 */
public class UserGroup
{
	private String groupName = null;

	private String descrip = null;

	/**
	 *
	 */
	public UserGroup()
	{
		super();
	}

	/**
	 *  @persist.field
	 *   name="descrip"
	 *   db-name="Descrip"
	 *   type="varchar"
	 *   descrip="Description"
	 *   length="132"
	 *   null-allowed="false"
	 * @return
	 */
	public String getDescrip()
	{
		return descrip;
	}

	/**
	 * @persist.field
	 *   name="groupname"
	 *   db-name="GroupName"
	 *   type="varchar"
	 *   descrip="Group Name"
	 *   length="80"
	 *   null-allowed="false"
	 *   primary-key="true"
	 * @return
	 */
	public String getGroupName()
	{
		return groupName;
	}

	/**
	 * @param string
	 */
	public void setDescrip(String string)
	{
		descrip = string;
	}

	/**
	 * @param string
	 */
	public void setGroupName(String string)
	{
		groupName = string;
	}
}
