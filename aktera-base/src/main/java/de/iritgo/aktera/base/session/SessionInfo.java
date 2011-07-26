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

package de.iritgo.aktera.base.session;


import de.iritgo.aktera.base.tools.OutputAdapter;


/**
 *
 */
public class SessionInfo extends OutputAdapter
{
	/** The system name of the current user. */
	private String loginName;

	/**
	 * Create a new <code>SessionInfo</code>.
	 */
	public SessionInfo ()
	{
		super ("sessionInfo");
	}

	/**
	 * Create a new <code>SessionInfo</code>.
	 *
	 * @param name The name of this output element.
	 */
	public SessionInfo (String name)
	{
		super (name);
	}

	/**
	 * Get the user's login name.
	 *
	 * @return The user's login name.
	 */
	public String getLoginName ()
	{
		return "# " + loginName + " #";
	}

	/**
	 * Set the user's login name.
	 *
	 * @param loginName The new login name.
	 */
	public void setLoginName (String loginName)
	{
		this.loginName = loginName;
	}
}
