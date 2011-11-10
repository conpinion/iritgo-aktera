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


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


final class BasicPermissionCollection extends PermissionCollection implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Map perms;

	private boolean all_allowed;

	private Class permClass;

	public BasicPermissionCollection()
	{
		perms = new HashMap(11);
		all_allowed = false;
	}

	@Override
	public void add(AbstractPermission permission)
	{
		if (! (permission instanceof BasicPermission))
			throw new IllegalArgumentException("invalid permission: " + permission);
		if (isReadOnly())
			throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");

		BasicPermission bp = (BasicPermission) permission;

		if (perms.size() == 0)
		{
			permClass = bp.getClass();
		}
		else
		{
			if (bp.getClass() != permClass)
				throw new IllegalArgumentException("invalid permission: " + permission);
		}

		synchronized (this)
		{
			perms.put(bp.getCanonicalName(), permission);
		}

		if (! all_allowed)
		{
			if (bp.getCanonicalName().equals("*"))
				all_allowed = true;
		}
	}

	@Override
	public boolean implies(AbstractPermission permission)
	{
		if (! (permission instanceof BasicPermission))
			return false;

		BasicPermission bp = (BasicPermission) permission;

		if (bp.getClass() != permClass)
			return false;

		if (all_allowed)
			return true;

		String path = bp.getCanonicalName();

		AbstractPermission x;

		synchronized (this)
		{
			x = (AbstractPermission) perms.get(path);
		}

		if (x != null)
		{
			return x.implies(permission);
		}

		int last, offset;

		offset = path.length() - 1;

		while ((last = path.lastIndexOf(".", offset)) != - 1)
		{

			path = path.substring(0, last + 1) + "*";

			synchronized (this)
			{
				x = (AbstractPermission) perms.get(path);
			}

			if (x != null)
			{
				return x.implies(permission);
			}
			offset = last - 1;
		}

		return false;
	}

	@Override
	public Enumeration elements()
	{
		synchronized (this)
		{
			return Collections.enumeration(perms.values());
		}
	}

	private static final ObjectStreamField[] serialPersistentFields =
	{
					new ObjectStreamField("permissions", Hashtable.class),
					new ObjectStreamField("all_allowed", Boolean.TYPE),
					new ObjectStreamField("permClass", Class.class),
	};

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		Hashtable permissions = new Hashtable(perms.size() * 2);

		synchronized (this)
		{
			permissions.putAll(perms);
		}

		ObjectOutputStream.PutField pfields = out.putFields();
		pfields.put("all_allowed", all_allowed);
		pfields.put("permissions", permissions);
		pfields.put("permClass", permClass);
		out.writeFields();
	}

	/**
	 * readObject is called to restore the state of the
	 * BasicPermissionCollection from a stream.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ObjectInputStream.GetField gfields = in.readFields();

		Hashtable permissions = (Hashtable) gfields.get("permissions", null);
		perms = new HashMap(permissions.size() * 2);
		perms.putAll(permissions);

		all_allowed = gfields.get("all_allowed", false);

		permClass = (Class) gfields.get("permClass", null);

		if (permClass == null)
		{
			Enumeration e = permissions.elements();
			if (e.hasMoreElements())
			{
				AbstractPermission p = (AbstractPermission) e.nextElement();
				permClass = p.getClass();
			}
		}
	}
}
