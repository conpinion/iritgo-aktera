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

package de.iritgo.aktera.permissions.security;


import java.util.Enumeration;
import java.util.Vector;


public class Group extends Principal
{
	private Vector groupMembers = new Vector (50, 100);

	private String group;

	public Group (String groupName)
	{
		super (groupName);
		this.group = groupName;
	}

	public boolean addMember (Principal user)
	{
		if (groupMembers.contains (user))
		{
			return false;
		}

		if (group.equals (user.toString ()))
		{
			throw new IllegalArgumentException ();
		}

		groupMembers.addElement (user);

		return true;
	}

	public boolean removeMember (Principal user)
	{
		return groupMembers.removeElement (user);
	}

	public Enumeration members ()
	{
		return groupMembers.elements ();
	}

	@Override
	public boolean equals (Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj instanceof Group == false)
		{
			return false;
		}

		Group another = (Group) obj;

		return group.equals (another.toString ());
	}

	public boolean equals (Group another)
	{
		return equals ((Object) another);
	}

	@Override
	public String toString ()
	{
		return group;
	}

	@Override
	public int hashCode ()
	{
		return group.hashCode ();
	}

	public boolean isMember (Principal member)
	{
		if (groupMembers.contains (member))
		{
			return true;
		}
		else
		{
			Vector alreadySeen = new Vector (10);

			return isMemberRecurse (member, alreadySeen);
		}
	}

	@Override
	public String getName ()
	{
		return group;
	}

	boolean isMemberRecurse (Principal member, Vector alreadySeen)
	{
		Enumeration e = members ();

		while (e.hasMoreElements ())
		{
			boolean mem = false;
			Principal p = (Principal) e.nextElement ();

			if (p.equals (member))
			{
				return true;
			}
			else if (p instanceof Group)
			{
				Group g = (Group) p;

				alreadySeen.addElement (this);

				if (! alreadySeen.contains (g))
				{
					mem = g.isMemberRecurse (member, alreadySeen);
				}
			}

			if (mem)
			{
				return mem;
			}
		}

		return false;
	}
}
