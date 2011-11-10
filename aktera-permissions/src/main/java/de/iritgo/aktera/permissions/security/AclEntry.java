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


public class AclEntry
{
	private Principal user = null;

	private Vector permissionSet = new Vector(10, 10);

	private boolean negative = false;

	public AclEntry(Principal user)
	{
		this.user = user;
	}

	public AclEntry()
	{
	}

	public boolean setPrincipal(Principal user)
	{
		if (this.user != null)
		{
			return false;
		}

		this.user = user;

		return true;
	}

	public void setNegativePermissions()
	{
		negative = true;
	}

	public boolean isNegative()
	{
		return negative;
	}

	public boolean addPermission(AbstractPermission permission)
	{
		if (permissionSet.contains(permission))
		{
			return false;
		}

		permissionSet.addElement(permission);

		return true;
	}

	public boolean removePermission(AbstractPermission permission)
	{
		return permissionSet.removeElement(permission);
	}

	public boolean checkPermission(AbstractPermission permission)
	{
		return permissionSet.contains(permission);
	}

	public Enumeration permissions()
	{
		return permissionSet.elements();
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		if (negative)
		{
			s.append("-");
		}
		else
		{
			s.append("+");
		}

		if (user instanceof Group)
		{
			s.append("Group.");
		}
		else
		{
			s.append("User.");
		}

		s.append(user + "=");

		Enumeration e = permissions();

		while (e.hasMoreElements())
		{
			AbstractPermission p = (AbstractPermission) e.nextElement();

			s.append(p);

			if (e.hasMoreElements())
			{
				s.append(",");
			}
		}

		return new String(s);
	}

	@Override
	public synchronized Object clone()
	{
		AclEntry cloned;

		cloned = new AclEntry(user);
		cloned.permissionSet = (Vector) permissionSet.clone();
		cloned.negative = negative;

		return cloned;
	}

	public Principal getPrincipal()
	{
		return user;
	}
}
