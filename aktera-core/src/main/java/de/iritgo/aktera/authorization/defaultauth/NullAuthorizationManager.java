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

package de.iritgo.aktera.authorization.defaultauth;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.Operation;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;


/**
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.authorization.AuthorizationManager
 * @x-avalon.info name=nullauth
 * @x-avalon.lifestyle type=singleton
 *
 * @author Michael Nash
 */
public class NullAuthorizationManager implements AuthorizationManager, LogEnabled, ThreadSafe
{
	private Logger log = null;

	public NullAuthorizationManager()
	{
		super();
	}

	public void enableLogging(Logger newLog)
	{
		log = newLog;
	}

	/**
	 * Is the specified operation allowed for the given
	 * principal?
	 */
	public boolean allowed(Operation o, UserEnvironment ue)
	{
		return true;
	}

	/**
	 * If the instance of this SecurityManager is
	 * Contextualizable, then we can determine the principal
	 * by getting the UserEnvironment from the context.
	 */
	public boolean allowed(Operation o, Context c)
	{
		return true;
	}

	/**
	 * If we don't specify an operation, determine if the
	 * user is allowed to do *anything* with this
	 * service. This is used for securing the entire service - e.g. a Model First
	 * it checks whether the Group in which the user is allowed. If yes then it
	 * checks whther all users are allowed for this Group. If not then it checks
	 * whether the current user is allowed.
	 */
	public boolean allowed(Object service, UserEnvironment ue)
	{
		return true;
	}

	/**
	 * Same as allowed(Principal), but if the SecurityManager
	 * is Contextualizable, it can determine the principal(s)
	 * on it's own
	 */
	public boolean allowed(Object service, Context c)
	{
		return true;
	}

	/**
	 * @see de.iritgo.aktera.authorization.AuthorizationManager#allowed(java.lang.Object, java.lang.String, de.iritgo.aktera.authentication.UserEnvironment)
	 */
	public boolean allowed(Object service, String id, UserEnvironment ue) throws AuthorizationException
	{
		return true;
	}
}
