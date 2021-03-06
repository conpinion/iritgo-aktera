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
public interface Group
{
	public class Property implements Comparable
	{
		private static int nextOrdinal = 0;

		public static final Property GID = new Property("GID");

		public static final Property NAME = new Property("NAME");

		public static final Property DESCRIPTION = new Property("DESCRIPTION");

		protected final String name;

		private final int ordinal = nextOrdinal++;

		protected Property(String name)
		{
			this.name = name;
		}

		public String toString()
		{
			return name;
		}

		public int compareTo(Object o)
		{
			return ordinal - ((Property) o).ordinal;
		}
	}

	public final static String ROLE = Group.class.getName();

	public Object get(Property property) throws UserMgrException;

	public boolean set(Property property, Object value) throws UserMgrException;

	public GroupManager getGroupManager() throws UserMgrException;

	public User[] listUsers() throws UserMgrException;

	public boolean addUser(User user) throws UserMgrException;

	public boolean deleteUser(User user) throws UserMgrException;
}
