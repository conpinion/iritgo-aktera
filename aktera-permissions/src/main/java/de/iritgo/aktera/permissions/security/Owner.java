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


public class Owner
{
	private Group ownerGroup;

	public Owner(Principal owner)
	{
		ownerGroup = new Group("AclOwners");
		ownerGroup.addMember(owner);
	}

	public synchronized boolean addOwner(Principal caller, Principal owner) throws NotOwnerException
	{
		if (! isOwner(caller))
		{
			throw new NotOwnerException();
		}

		ownerGroup.addMember(owner);

		return false;
	}

	public synchronized boolean deleteOwner(Principal caller, Principal owner)
		throws NotOwnerException, LastOwnerException
	{
		if (! isOwner(caller))
		{
			throw new NotOwnerException();
		}

		Enumeration e = ownerGroup.members();

		@SuppressWarnings("unused")
		Object ignored = e.nextElement();

		if (e.hasMoreElements())
		{
			return ownerGroup.removeMember(owner);
		}
		else
		{
			throw new LastOwnerException();
		}
	}

	public synchronized boolean isOwner(Principal owner)
	{
		return ownerGroup.isMember(owner);
	}
}
