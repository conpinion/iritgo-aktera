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

package de.iritgo.aktera.authentication;


import de.iritgo.aktera.authorization.AuthorizationException;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @version $Revision: 1.1 $    $Date: 2005/07/23 21:55:17 $
 * @author Shash Chatterjee
 * Created on Jun 8, 2003
 */
public class DefaultUserEnvironment implements UserEnvironment, Serializable
{
	/** The JAAS Subject of the currently logged-in user */
	private static Subject anon = createAnonymousUser();

	private Subject subject = anon;

	private transient LoginContext lc = null;

	private Map attributes = new HashMap();

	/**
	 *
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getUid()
	 */
	public int getUid() throws AuthorizationException
	{
		Principal p = getOnePrincipal(UidPrincipal.class);

		int uid;

		try
		{
			uid = Integer.valueOf(p.getName()).intValue();
		}
		catch (NumberFormatException e)
		{
			throw new AuthorizationException("Uid=" + p.getName() + " cannot be converted to an int");
		}

		return uid;
	}

	/**
	 *
	 * @return
	 */
	private static Subject createAnonymousUser()
	{
		Subject tmp = new Subject();
		Set s = tmp.getPrincipals();

		s.add(new LoginPrincipal(ANONYMOUS_LOGINNAME));
		s.add(new GroupPrincipal(ANONYMOUS_GROUPNAME));
		s.add(new DomainPrincipal(ANONYMOUS_DOMAIN));
		s.add(new UidPrincipal(Integer.toString(ANONYMOUS_UID)));
		s.add(new UserDescripPrincipal(ANONYMOUS_USERDESC));

		return tmp;
	}

	/**
	 *
	 * @param clazz
	 * @return
	 * @throws AuthorizationException
	 */
	private Principal getOnePrincipal(Class clazz) throws AuthorizationException
	{
		Iterator principals = getPrincipals(clazz);
		Principal one = (Principal) principals.next();

		return one;
	}

	/**
	 *
	 * @param clazz
	 * @return
	 * @throws AuthorizationException
	 */
	private Iterator getPrincipals(Class clazz) throws AuthorizationException
	{
		if (subject == null)
		{
			throw new AuthorizationException("Cannot get principals, subject is null");
		}

		Set p = subject.getPrincipals(clazz);

		if (p.isEmpty())
		{
			throw new AuthorizationException("No principals of type " + clazz.getName());
		}

		return p.iterator();
	}

	/**
	 *
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getDomain()
	 */
	public String getDomain() throws AuthorizationException
	{
		return getOnePrincipal(DomainPrincipal.class).getName();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Subject getSubject() throws AuthorizationException
	{
		return this.subject;
	}

	/**
	 * Returns the myLoginName.
	 *
	 * @return String
	 */
	public String getLoginName() throws AuthorizationException
	{
		return getOnePrincipal(LoginPrincipal.class).getName();
	}

	/**
	 *
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getUserDescrip()
	 */
	public String getUserDescrip() throws AuthorizationException
	{
		return getOnePrincipal(UserDescripPrincipal.class).getName();
	}

	/**
	 *
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getGroups()
	 */
	public List<String> getGroups()
	{
		List<String> groups = new ArrayList();

		if (subject != null)
		{
			for (Iterator principals = subject.getPrincipals(GroupPrincipal.class).iterator(); principals.hasNext();)
			{
				groups.add(((GroupPrincipal) principals.next()).getName());
			}
		}

		return groups;
	}

	/**
	 * @see de.iritgo.aktera.authentication.UserEnvironment#reset()
	 */
	public void reset() throws AuthorizationException
	{
		subject = anon;
	}

	/**
	 * @see de.iritgo.aktera.authentication.UserEnvironment#setLoginContext(javax.security.auth.login.LoginContext)
	 */
	public void setLoginContext(LoginContext lc) throws AuthorizationException
	{
		this.lc = lc;
		subject = lc.getSubject();
	}

	/**
	 * @see de.iritgo.aktera.authentication.UserEnvironment#getLoginContext()
	 */
	public LoginContext getLoginContext() throws AuthorizationException
	{
		return lc;
	}

	public String toString()
	{
		try
		{
			return "UserEnvironment [uid=" + getUid() + ", LoginName=" + getLoginName() + ", UserDescrip="
							+ getUserDescrip() + "]";
		}
		catch (Exception e)
		{
			return e.getMessage();
		}
	}

	public void setAttribute(String attributeName, Object attributeContents)
	{
		if (attributeName != null)
		{
			attributes.put(attributeName, attributeContents);
		}
	}

	public Object getAttribute(String attributeName)
	{
		return attributes.get(attributeName);
	}

	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	public void removeAttribute(String attributeName)
	{
		attributes.remove(attributeName);
	}

	public void clearAttributes()
	{
		attributes.clear();
	}
}
