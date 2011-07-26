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
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * Group members indicates the mapping from a user
 * to a list of groups that that user belongs to. Exactly how
 * this is determined is left up to the implementation.
 * @persist.persistent
 *   id="groupmembers"
 *   name="groupmembers"
 *   securable="true"
 *   schema="keel"
 *   table="keelgroupmembers"
 *   descrip="Group Members"
 *   am-bypass-allowed="true"
 *   schema="keel"
 *
 * @author Michael Nash
 */
@Entity
@Table(name = "keelgroupmembers")
public class GroupMembers implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "uniqid")
	private Integer uid = null;

	@Column(nullable = false, length = 80)
	private String groupName = null;

	/**
	 *
	 */
	public GroupMembers ()
	{
		super ();
	}

	/**
	 * @persist.field
	 *   name="groupname"
	 *   db-name="GroupName"
	 *   descrip="Group Name"
	 *   type="varchar"
	 *   length="80"
	 *   null-allowed="false"
	 *   primary-key="true"
	 * @persist.lookup
	 *   name="group"
	 *   field="GroupName"
	 * @return
	 */
	public String getGroupName ()
	{
		return groupName;
	}

	/**
	 * @persist.field
	 *   name="uid"
	 *   db-name="UniqId"
	 *   type="integer"
	 *   descrip="User Id"
	 *   null-allowed="false"
	 *   primary-key="true"
	 * @return
	 */
	public Integer getUid ()
	{
		return uid;
	}

	/**
	 * @param string
	 */
	public void setGroupName (String string)
	{
		groupName = string;
	}

	/**
	 * @param integer
	 */
	public void setUid (Integer integer)
	{
		uid = integer;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals (Object object)
	{
		if (! (object instanceof GroupMembers))
		{
			return false;
		}

		GroupMembers rhs = (GroupMembers) object;

		if ((rhs.getGroupName ().equals (getGroupName ())) && (rhs.getUid ().equals (getUid ())))
		{
			return true;
		}

		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode ()
	{
		return new String (getGroupName () + getUid ().toString ()).hashCode ();
	}
}
