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

package de.iritgo.aktera.usergroupmgr;


/**
 * @author schatterjee
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface GroupManager
{
	public final static String ROLE = GroupManager.class.getName ();

	public Group find (Group.Property property, Object value) throws UserMgrException;

	public Group[] list () throws UserMgrException;

	public Group add (Group group) throws UserMgrException;

	public boolean delete (Group group) throws UserMgrException;

	public boolean update (Group group) throws UserMgrException;

	public User[] listUsers (Group group) throws UserMgrException;

	public boolean addUser (Group group, User user) throws UserMgrException;

	public boolean deleteUser (Group group, User user) throws UserMgrException;

	public Group[] listGroups (User user) throws UserMgrException;

	public Group createGroup (String name, String description) throws UserMgrException;
}
