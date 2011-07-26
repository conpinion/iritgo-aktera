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
import java.util.NoSuchElementException;


final class AclEnumerator implements Enumeration
{
	Acl acl;

	Enumeration u1;

	Enumeration u2;

	Enumeration g1;

	Enumeration g2;

	AclEnumerator (Acl acl, Hashtable u1, Hashtable g1, Hashtable u2, Hashtable g2)
	{
		this.acl = acl;
		this.u1 = u1.elements ();
		this.u2 = u2.elements ();
		this.g1 = g1.elements ();
		this.g2 = g2.elements ();
	}

	public boolean hasMoreElements ()
	{
		return (u1.hasMoreElements () || u2.hasMoreElements () || g1.hasMoreElements () || g2.hasMoreElements ());
	}

	public Object nextElement ()
	{
		synchronized (acl)
		{
			if (u1.hasMoreElements ())
			{
				return u1.nextElement ();
			}

			if (u2.hasMoreElements ())
			{
				return u2.nextElement ();
			}

			if (g1.hasMoreElements ())
			{
				return g1.nextElement ();
			}

			if (g2.hasMoreElements ())
			{
				return g2.nextElement ();
			}
		}

		throw new NoSuchElementException ("Acl Enumerator");
	}
}
