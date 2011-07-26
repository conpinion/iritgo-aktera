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


public class BasicPermission extends AbstractPermission implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private transient boolean wildcard;

	private transient String path;

	private void init (String name)
	{
		if (name == null)
		{
			throw new NullPointerException ("name can't be null");
		}

		int len = name.length ();

		if (len == 0)
		{
			throw new IllegalArgumentException ("name can't be empty");
		}

		char last = name.charAt (len - 1);

		if (last == '*' && (len == 1 || name.charAt (len - 2) == '.'))
		{
			wildcard = true;
			if (len == 1)
			{
				path = "";
			}
			else
			{
				path = name.substring (0, len - 1);
			}
		}
		else
		{
			path = name;
		}
	}

	public BasicPermission (String name)
	{
		super (name);
		init (name);
	}

	@Override
	public boolean implies (AbstractPermission p)
	{
		if ((p == null) || (p.getClass () != getClass ()))
			return false;

		BasicPermission that = (BasicPermission) p;

		if (this.wildcard)
		{
			if (that.wildcard)
			{
				// one wildcard can imply another
				return that.path.startsWith (path);
			}
			else
			{
				// make sure ap.path is longer so a.b.* doesn't imply a.b
				return (that.path.length () > this.path.length ()) && that.path.startsWith (this.path);
			}
		}
		else
		{
			if (that.wildcard)
			{
				// a non-wildcard can't imply a wildcard
				return false;
			}
			else
			{
				return this.path.equals (that.path);
			}
		}
	}

	@Override
	public boolean equals (Object obj)
	{
		if (obj == this)
			return true;

		if ((obj == null) || (obj.getClass () != getClass ()))
			return false;

		BasicPermission bp = (BasicPermission) obj;

		return getCanonicalName ().equals (bp.getCanonicalName ());
	}

	@Override
	public int hashCode ()
	{
		return this.getCanonicalName ().hashCode ();
	}

	@Override
	public PermissionCollection newPermissionCollection ()
	{
		return new BasicPermissionCollection ();
	}

	private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject ();
		init (getCanonicalName ());
	}

	final String getCanonicalName ()
	{
		return getName ();
	}
}
