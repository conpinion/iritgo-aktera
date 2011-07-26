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

package de.iritgo.aktera.permissions.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;


/**
 * @persist.persistent
 *   id="Permission"
 *   name="Permission"
 *   table="Permission"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
@NamedQueries(
{
				@NamedQuery(name = "de.iritgo.aktera.permissions.PermissionListing", query = ""
								+ "select p.id as id, p.permission as permission, p.negative as negative, p.objectType as objectType, p.objectId as objectId from Permission p"),
				@NamedQuery(name = "de.iritgo.aktera.permissions.UserPermissionListing", query = ""
								+ "select p.id as id, p.permission as permission, p.negative as negative, p.objectType as objectType, p.objectId as objectId from Permission p"
								+ " where p.principalId = :userId and p.principalType = 'U'"),
				@NamedQuery(name = "de.iritgo.aktera.permissions.GroupPermissionListing", query = ""
								+ "select p.id as id, p.permission as permission, p.negative as negative, p.objectType as objectType, p.objectId as objectId from Permission p"
								+ " where p.principalId = :groupId and p.principalType = 'G'")
})
public class Permission implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** The primrary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** The foreign principal id */
	@Column(nullable = false)
	private Integer principalId;

	/** The foreign principal type */
	@Column(nullable = false, length = 255)
	private String principalType;

	/** The foreign object key */
	private Integer objectId;

	/** The foreign object type */
	@Column(length = 255)
	private String objectType;

	/** The permission value */
	@Column(nullable = false, length = 255)
	private String permission;

	/** If true, this is a negative permission */
	private Boolean negative;

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
	public Integer getId ()
	{
		return id;
	}

	/**
	 * Set the primary key.
	 */
	public void setId (Integer id)
	{
		this.id = id;
	}

	/**
	 * Get the foreign principal id.
	 *
	 * @persist.field
	 *   name="principalId"
	 *   db-name="principalId"
	 *   type="integer"
	 *   null-allowed="false"
	 */
	public Integer getPrincipalId ()
	{
		return principalId;
	}

	/**
	 * Set the foreign principal id.
	 */
	public void setPrincipalId (Integer principalId)
	{
		this.principalId = principalId;
	}

	/**
	 * Get the foreign principal type.
	 *
	 * @persist.field
	 *   name="principalType"
	 *   db-name="principalType"
	 *   type="varchar"
	 *   length="255"
	 *   null-allowed="false"
	 */
	public String getPrincipalType ()
	{
		return principalType;
	}

	/**
	 * Set the foreign principal type.
	 */
	public void setPrincipalType (String principalType)
	{
		this.principalType = principalType;
	}

	/**
	 * Get the foreign object id.
	 *
	 * @persist.field
	 *   name="objectId"
	 *   db-name="objectId"
	 *   type="integer"
	 */
	public Integer getObjectId ()
	{
		return objectId;
	}

	/**
	 * Set the foreign object id.
	 *
	 * @param objectId The new object id.
	 */
	public void setObjectId (Integer objectId)
	{
		this.objectId = objectId;
	}

	/**
	 * Get the foreign object type.
	 *
	 * @persist.field
	 *   name="objectType"
	 *   db-name="objectType"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getObjectType ()
	{
		return objectType;
	}

	/**
	 * Set the foreign object type.
	 */
	public void setObjectType (String objectType)
	{
		this.objectType = objectType;
	}

	/**
	 * Get the permission value
	 *
	 * @persist.field
	 *   name="permission"
	 *   db-name="permission"
	 *   type="varchar"
	 *   length="255"
	 *   null-allowed="false"
	 */
	public String getPermission ()
	{
		return permission;
	}

	/**
	 * Set the permission value
	 */
	public void setPermission (String permission)
	{
		this.permission = permission;
	}

	/**
	 * Get the negative flag.
	 *
	 * @persist.field
	 *   name="negative"
	 *   db-name="negative"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getNegative ()
	{
		return negative;
	}

	/**
	 * Set the negative flag.
	 */
	public void setNegative (Boolean negative)
	{
		this.negative = negative;
	}
}
