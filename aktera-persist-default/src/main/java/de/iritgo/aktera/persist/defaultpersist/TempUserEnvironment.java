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

/*
 * Created on Nov 16, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package de.iritgo.aktera.persist.defaultpersist;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
class TempUserEnvironment implements UserEnvironment
{
	private String loginName = "";

	private List<String> groups = null;

	private int uid = 0;

	private Map attribs = new HashMap ();

	/**
	 *
	 */
	public TempUserEnvironment ()
	{
		super ();

		// TODO Auto-generated constructor stub
	}

	public String toString ()
	{
		StringBuffer groupString = new StringBuffer ();

		for (Iterator i = groups.iterator (); i.hasNext ();)
		{
			groupString.append (",");
			groupString.append ((String) i.next ());
		}

		return "[TempUserEnvironment] " + loginName + ", uid=" + uid + ", groups " + groupString.toString ();
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#setLoginContext(javax.security.auth.login.LoginContext)
	 */
	public void setLoginContext (LoginContext lc) throws AuthorizationException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getLoginContext()
	 */
	public LoginContext getLoginContext () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getSubject()
	 */
	public Subject getSubject () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getLoginName()
	 */
	public String getLoginName () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return loginName;
	}

	public void setLoginName (String newName)
	{
		loginName = newName;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getDomain()
	 */
	public String getDomain () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getUserDescrip()
	 */
	public String getUserDescrip () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getUid()
	 */
	public int getUid () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return uid;
	}

	public void setUid (int newUid)
	{
		uid = newUid;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getGroups()
	 */
	public List<String> getGroups () throws AuthorizationException
	{
		// TODO Auto-generated method stub
		return groups;
	}

	public void setGroups (List newGroups)
	{
		groups = newGroups;
	}

	/* (non-Javadoc)
	 * @see de.iritgo.aktera.authentication.UserEnvironment#reset()
	 */
	public void reset () throws AuthorizationException
	{
		// TODO Auto-generated method stub
	}

	public void setAttribute (String name, Object attrib)
	{
		attribs.put (name, attrib);
	}

	public Object getAttribute (String name)
	{
		return attribs.get (name);
	}

	public void removeAttribute (String attributeName)
	{
		attribs.remove (attributeName);
	}

	public void clearAttributes ()
	{
		attribs.clear ();
	}
}
