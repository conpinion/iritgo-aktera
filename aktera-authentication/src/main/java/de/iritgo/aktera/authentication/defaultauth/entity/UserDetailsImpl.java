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

package de.iritgo.aktera.authentication.defaultauth.entity;


import java.util.Collection;
import java.util.LinkedList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import de.iritgo.simplelife.string.StringTools;


/**
 *
 */
public class UserDetailsImpl implements UserDetails
{
	/** */
	private static final long serialVersionUID = 1L;

	/** */
	private String userName;

	/** */
	private String password;

	/**
	 * @param userName
	 * @param password
	 */
	public UserDetailsImpl(String userName, String password)
	{
		this.userName = userName;
		this.password = StringTools.trim(password).toLowerCase();
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
	 */
	public Collection<GrantedAuthority> getAuthorities()
	{
		return new LinkedList();
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#getPassword()
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#getUsername()
	 */
	public String getUsername()
	{
		return userName;
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired()
	{
		return true;
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked()
	{
		return true;
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	/**
	 * @see org.springframework.security.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled()
	{
		return true;
	}
}
