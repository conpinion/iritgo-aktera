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


import java.util.List;
import de.iritgo.simplelife.data.Tuple2;


/**
 * User DAO interface.
 */
public interface UserDAO
{
	/** Service id */
	static public String ID = "keel.UserDAO";

	/**
	 * Get a keel user by the id
	 *
	 * @param id The user id
	 * @return The user object or null if none was found
	 */
	public AkteraUser findUserById(Integer id);

	/**
	 * Get a keel user by the name
	 *
	 * @param name The name
	 * @return The user object
	 */
	public AkteraUser findUserByName(String name);

	/**
	 * Retrieve a list of all users.
	 *
	 * @return A list of all users
	 */
	public List<AkteraUser> findAllUsers();

	/**
	 * Retrieve a list of (user primary key, user name) pairs.
	 *
	 * @return A list of of (user primary key, user name) pairs
	 */
	public List<Tuple2<Integer, String>> listUsersOverview();

	/**
	 * Retrieve a list of id's of all groups to which the user, specified by
	 * by it's id, belongs.
	 *
	 * @param userId The user id
	 * @return A list of group ids
	 */
	public List<Integer> listGroupIdsOfUserId(Integer userId);

	/**
	 * Update a user.
	 *
	 * @param user The user to update
	 */
	public void updateUser(AkteraUser user);

	/**
	 * Retrieve a list of all groups.
	 *
	 * @return A list of all groups
	 */
	public List<AkteraGroup> findAllGroups();

	/**
	 * Get a group by it's id.
	 *
	 * @param id The group id
	 * @return The group object or null if none was found
	 */
	public AkteraGroup findGroupById(Integer id);

	/**
	 * Get a group by it's name.
	 *
	 * @param name The group name
	 * @return The group object or null if none was found
	 */
	public AkteraGroup findGroupByName(String name);

	/**
	 * Retrieve a list of all groups to which the specified user belongs.
	 *
	 * @return A list of all groups containing the specified user
	 */
	public List<AkteraGroup> findGroupsByUser(AkteraUser user);

	/**
	 * Retrieve a list of all user of the specified group.
	 *
	 * @return A list of all users in the specified group
	 */
	public List<AkteraUser> findUsersByGroup(AkteraGroup group);

	/**
	 * Store a given user in the db
	 *
	 * @param user The user
	 */
	public void createUser(AkteraUser user);

	/**
	 * Create a group member entry
	 *
	 * @param groupMember The group member entry
	 */
	public void createGroupMember(GroupMembers groupMember);

	/**
	 * Check if the user has the specified role.
	 *
	 * @param user The user to check
	 * @param role The role to check
	 * @return True if the user has this role
	 */
	public boolean userHasRole(AkteraUser user, String role);

	/**
	 * Count all users without accounting the system users.
	 *
	 * @return The number of not system users.
	 */
	public long countNonSystemUsers();

	/**
	 * Create a new aktera group entry. The position will be set to the
	 * current maximum position + 1 over all entries in the group.
	 *
	 * @param entry The entry to add
	 */
	public void createAkteraGroupEntry(AkteraGroupEntry entry);

	/**
	 * Find an aktera group entry by it's id.
	 *
	 * @param id The entry id
	 * @return The aktera group entry or null if none was found
	 */
	public AkteraGroupEntry findAkteraGroupEntryById(Integer id);

	/**
	 * Find an aktera group entry by it's user and group id.
	 *
	 * @param userId The user id
	 * @param groupId The group id
	 * @return The group entry or null if none was found
	 */
	public AkteraGroupEntry findAkteraGroupEntryByUserIdAndGroupId(Integer userId, Integer groupId);

	/**
	 * Delete the specified aktera group entry.
	 *
	 * @param entry The entry to delete
	 */
	public void deleteAkteraGroupEntry(AkteraGroupEntry entry);

	/**
	 * Delete all aktera groups entries of the specified user.
	 *
	 * @param userId The user id
	 */
	public void deleteAkteraGroupEntriesByUserId(int userId);

	/**
	 * Move an aktera group entry one position down.
	 *
	 * @param entry The entry to move
	 */
	public void moveDownAkteraGroupEntry(AkteraGroupEntry entry);

	/**
	 * Move an aktera group entry one position up.
	 *
	 * @param entry The entry to move
	 */
	public void moveUpAkteraGroupEntry(AkteraGroupEntry entry);
}
