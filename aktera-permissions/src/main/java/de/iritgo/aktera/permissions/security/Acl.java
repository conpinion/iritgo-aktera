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
import java.util.Hashtable;
import java.util.Vector;


public class Acl extends Owner
{
	private Hashtable allowedUsersTable = new Hashtable(23);

	private Hashtable allowedGroupsTable = new Hashtable(23);

	private Hashtable deniedUsersTable = new Hashtable(23);

	private Hashtable deniedGroupsTable = new Hashtable(23);

	private String aclName = null;

	private Vector zeroSet = new Vector(1, 1);

	public Acl(Principal owner, String name)
	{
		super(owner);

		try
		{
			setName(owner, name);
		}
		catch (Exception e)
		{
		}
	}

	public void setName(Principal caller, String name) throws NotOwnerException
	{
		if (! isOwner(caller))
		{
			throw new NotOwnerException();
		}

		aclName = name;
	}

	public String getName()
	{
		return aclName;
	}

	public synchronized boolean addEntry(Principal caller, AclEntry entry) throws NotOwnerException
	{
		if (! isOwner(caller))
		{
			throw new NotOwnerException();
		}

		Hashtable aclTable = findTable(entry);
		Principal key = entry.getPrincipal();

		if (aclTable.get(key) != null)
		{
			return false;
		}

		aclTable.put(key, entry);

		return true;
	}

	public synchronized boolean removeEntry(Principal caller, AclEntry entry) throws NotOwnerException
	{
		if (! isOwner(caller))
		{
			throw new NotOwnerException();
		}

		Hashtable aclTable = findTable(entry);
		Object key = entry.getPrincipal();

		Object o = aclTable.remove(key);

		return (o != null);
	}

	public synchronized Enumeration getPermissions(Principal user)
	{
		Enumeration individualPositive;
		Enumeration individualNegative;
		Enumeration groupPositive;
		Enumeration groupNegative;

		groupPositive = subtract(getGroupPositive(user), getGroupNegative(user));
		groupNegative = subtract(getGroupNegative(user), getGroupPositive(user));
		individualPositive = subtract(getIndividualPositive(user), getIndividualNegative(user));
		individualNegative = subtract(getIndividualNegative(user), getIndividualPositive(user));

		Enumeration temp1 = subtract(groupPositive, individualNegative);
		Enumeration netPositive = union(individualPositive, temp1);

		individualPositive = subtract(getIndividualPositive(user), getIndividualNegative(user));
		individualNegative = subtract(getIndividualNegative(user), getIndividualPositive(user));

		temp1 = subtract(groupNegative, individualPositive);

		Enumeration netNegative = union(individualNegative, temp1);

		return subtract(netPositive, netNegative);
	}

	public boolean checkPermission(Principal principal, AbstractPermission permission)
	{
		Enumeration permSet = getPermissions(principal);

		while (permSet.hasMoreElements())
		{
			AbstractPermission p = (AbstractPermission) permSet.nextElement();

			if (p.equals(permission))
			{
				return true;
			}
		}

		return false;
	}

	public synchronized Enumeration entries()
	{
		return new AclEnumerator(this, allowedUsersTable, allowedGroupsTable, deniedUsersTable, deniedGroupsTable);
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		Enumeration entries = entries();

		while (entries.hasMoreElements())
		{
			AclEntry entry = (AclEntry) entries.nextElement();

			sb.append(entry.toString().trim());
			sb.append("\n");
		}

		return sb.toString();
	}

	private Hashtable findTable(AclEntry entry)
	{
		return findTable(entry.getPrincipal(), entry.isNegative());
	}

	private Hashtable findTable(Principal principal, boolean negative)
	{
		Hashtable aclTable = null;

		if (principal instanceof Group)
		{
			if (negative)
			{
				aclTable = deniedGroupsTable;
			}
			else
			{
				aclTable = allowedGroupsTable;
			}
		}
		else
		{
			if (negative)
			{
				aclTable = deniedUsersTable;
			}
			else
			{
				aclTable = allowedUsersTable;
			}
		}

		return aclTable;
	}

	private static Enumeration union(Enumeration e1, Enumeration e2)
	{
		Vector v = new Vector(20, 20);

		while (e1.hasMoreElements())
		{
			v.addElement(e1.nextElement());
		}

		while (e2.hasMoreElements())
		{
			Object o = e2.nextElement();

			if (! v.contains(o))
			{
				v.addElement(o);
			}
		}

		return v.elements();
	}

	private Enumeration subtract(Enumeration e1, Enumeration e2)
	{
		Vector v = new Vector(20, 20);

		while (e1.hasMoreElements())
		{
			v.addElement(e1.nextElement());
		}

		while (e2.hasMoreElements())
		{
			Object o = e2.nextElement();

			if (v.contains(o))
			{
				v.removeElement(o);
			}
		}

		return v.elements();
	}

	private Enumeration getGroupPositive(Principal user)
	{
		Enumeration groupPositive = zeroSet.elements();
		Enumeration e = allowedGroupsTable.keys();

		while (e.hasMoreElements())
		{
			Group g = (Group) e.nextElement();

			if (g.isMember(user))
			{
				AclEntry ae = (AclEntry) allowedGroupsTable.get(g);

				groupPositive = union(ae.permissions(), groupPositive);
			}
		}

		return groupPositive;
	}

	private Enumeration getGroupNegative(Principal user)
	{
		Enumeration groupNegative = zeroSet.elements();
		Enumeration e = deniedGroupsTable.keys();

		while (e.hasMoreElements())
		{
			Group g = (Group) e.nextElement();

			if (g.isMember(user))
			{
				AclEntry ae = (AclEntry) deniedGroupsTable.get(g);

				groupNegative = union(ae.permissions(), groupNegative);
			}
		}

		return groupNegative;
	}

	private Enumeration getIndividualPositive(Principal user)
	{
		Enumeration individualPositive = zeroSet.elements();
		AclEntry ae = (AclEntry) allowedUsersTable.get(user);

		if (ae != null)
		{
			individualPositive = ae.permissions();
		}

		return individualPositive;
	}

	private Enumeration getIndividualNegative(Principal user)
	{
		Enumeration individualNegative = zeroSet.elements();
		AclEntry ae = (AclEntry) deniedUsersTable.get(user);

		if (ae != null)
		{
			individualNegative = ae.permissions();
		}

		return individualNegative;
	}

	public AclEntry findAclEntry(Principal principal, boolean negative)
	{
		Hashtable aclTable = findTable(principal, negative);
		if (aclTable != null)
		{
			return (AclEntry) aclTable.get(principal);
		}
		return null;
	}
}
