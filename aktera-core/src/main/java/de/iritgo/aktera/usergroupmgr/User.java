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


/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author schatterjee
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface User
{
	public class Property implements Comparable
	{
		private static int nextOrdinal = 0;

		public static final Property UID = new Property ("UID");

		public static final Property NAME = new Property ("NAME");

		public static final Property PASSWORD = new Property ("PASSWORD");

		public static final Property EMAIL = new Property ("EMAIL");

		protected final String name;

		private final int ordinal = nextOrdinal++;

		protected Property (String name)
		{
			this.name = name;
		}

		public String toString ()
		{
			return name;
		}

		public int compareTo (Object o)
		{
			return ordinal - ((Property) o).ordinal;
		}
	}

	public final static String ROLE = User.class.getName ();

	public Object get (Property property) throws UserMgrException;

	public boolean set (Property property, Object value) throws UserMgrException;

	public UserManager getUserManager () throws UserMgrException;

	public Group[] listGroups () throws UserMgrException;
}
