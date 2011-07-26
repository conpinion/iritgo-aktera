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

package de.iritgo.aktera.permissions;


/**
 * Meta data about permissions. This is used for example to generate a list of
 * all permissions in a web formular.
 */
public class PermissionMetaData
{
	/** The permission id */
	private String id;

	public void setId (String id)
	{
		this.id = id;
	}

	public String getId ()
	{
		return id;
	}

	/** The permission display name */
	private String name;

	public void setName (String name)
	{
		this.name = name;
	}

	public String getName ()
	{
		return name;
	}

	/** Permission DAO */
	protected PermissionDAO permissionDAO;

	public void setPermissionDAO (PermissionDAO permissionDAO)
	{
		this.permissionDAO = permissionDAO;
	}

	/** Object type or null if global permission */
	String objectType;

	public void setObjectType (String objectType)
	{
		this.objectType = objectType;
	}

	public String getObjectType ()
	{
		return objectType;
	}
}
