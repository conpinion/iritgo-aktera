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

package de.iritgo.aktera.tools;


import de.iritgo.aktera.authentication.DefaultUserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import java.util.LinkedList;
import java.util.List;


/**
 *
 */
public class FakeAdminUserEnvironment extends DefaultUserEnvironment
{
	/** */
	private static final long serialVersionUID = 1L;

	/** */
	private LinkedList<String> groupList;

	/**
	 * @see de.iritgo.aktera.authentication.DefaultUserEnvironment#getGroups()
	 */
	@Override
	public List<String> getGroups ()
	{
		if (groupList == null)
		{
			groupList = new LinkedList<String> ();
			groupList.add (new String ("root"));
		}

		return groupList;
	}

	/**
	 * @see de.iritgo.aktera.authentication.DefaultUserEnvironment#getUid()
	 */
	@Override
	public int getUid () throws AuthorizationException
	{
		return 1;
	}

	/**
	 * @see de.iritgo.aktera.authentication.DefaultUserEnvironment#getLoginName()
	 */
	@Override
	public String getLoginName () throws AuthorizationException
	{
		return "admin";
	}

	/**
	 * @see de.iritgo.aktera.authentication.DefaultUserEnvironment#getUserDescrip()
	 */
	@Override
	public String getUserDescrip () throws AuthorizationException
	{
		return "Administrator";
	}
}
