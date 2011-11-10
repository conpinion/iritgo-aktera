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
import java.io.Serializable;


/**
 * @persist.persistent
 *   id="AkteraGroupEntry"
 *   name="AkteraGroupEntry"
 *   table="AkteraGroupEntry"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class AkteraGroupEntry implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Group entry primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** The group foreign key */
	@Column(nullable = false)
	private Integer groupId;

	/** The user foreign key */
	@Column(nullable = false)
	private Integer userId;

	/** Position of this entry in group entry list */
	@Column(nullable = false)
	private Integer position;

	/**
	 * Get the group entry primary key.
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
	 * Set the group entry primary key.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}

	/**
	 * Get the group foreign key.
	 *
	 * @persist.field
	 *   name="groupId"
	 *   db-name="groupId"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public Integer getGroupId()
	{
		return groupId;
	}

	/**
	 * Set the group foreign key.
	 */
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
	}

	/**
	 * Get the user foreign key.
	 *
	 * @persist.field
	 *   name="userId"
	 *   db-name="userId"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public Integer getUserId()
	{
		return userId;
	}

	/**
	 * Set the user foreign key.
	 */
	public void setUserId(Integer userId)
	{
		this.userId = userId;
	}

	/**
	 * Get the list position.
	 *
	 * @persist.field
	 *   name="position"
	 *   db-name="position"
	 *   type="integer"
	 *   null-allowed="false"
	 *   default-value="1"
	 */
	public Integer getPosition()
	{
		return position;
	}

	/**
	 * Set the list position.
	 */
	public void setPosition(Integer position)
	{
		this.position = position;
	}
}
