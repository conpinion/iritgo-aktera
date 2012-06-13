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


import java.util.List;
import de.iritgo.aktera.persist.PersistenceException;


/**
 * Permission manager service interface.
 */
public interface PermissionManager
{
	/** Permission check result. */
	public static final int PERM_DENIED = 0;

	/** Permission check result. */
	public static final int PERM_GRANTED = 1;

	/** Id of the default permission service. */
	public final String ID = "de.iritgo.aktera.permissions.PermissionManager";

	/**
	 * Get a collection of meta data about all defined permissions.
	 *
	 * @return A meta data collection
	 */
	public List<PermissionMetaData> getPermissionMetaData();

	/**
	 * Get a specific permission meta data by it's id.
	 *
	 * @param id The permission id
	 * @return The permission meta data
	 */
	public PermissionMetaData getMetaDataById(String id);

	/**
	 * Check if a given user has the specified permission granted.
	 *
	 * @return True if the specified permission is granted.
	 */
	public boolean hasPermission(String userName, String permission);

	/**
	 * Check if a given user has the specified permission granted.
	 *
	 * @return True if the specified permission is granted.
	 */
	public boolean hasPermission(Integer userId, String permission);

	/**
	 * Check if a given user has the specified permission granted.
	 *
	 * @param params Permission specific parameters.
	 * @return True if the specified permission is granted.
	 */
	public boolean hasPermission(String userName, String permission, String objectType, Integer objectId);

	/**
	 * Check if a given user has the specified permission granted.
	 *
	 * @param params Permission specific parameters.
	 * @return True if the specified permission is granted.
	 */
	public boolean hasPermission(Integer userId, String permission, String objectType, Integer objectId);

	/**
	 * Describe method hasPermissionOnUserOrGroup() here.
	 *
	 * @param userName
	 * @param permission
	 * @param userId
	 * @return
	 */
	public boolean hasPermissionOnUserOrGroup(String userName, String permission, Integer userId);

	/**
	 * Remove all cached permissions.
	 */
	public void clear();

	/**
	 * Delete all permissions for the specified principal.
	 *
	 * @param principalId The principal id.
	 * @param principalType The principal type.
	 */
	public void deleteAllPermissionsOfPrincipal(Integer principalId, String principalType) throws PersistenceException;

	/**
	 * Dump all users groups permissions
	 *
	 */
	public String dumpAll ();

}
