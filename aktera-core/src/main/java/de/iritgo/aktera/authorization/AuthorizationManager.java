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

package de.iritgo.aktera.authorization;


import de.iritgo.aktera.authentication.UserEnvironment;
import org.apache.avalon.framework.context.Context;


/**
 * An AuthorizationManager is a service that is able to determine if the
 * specified operation is allowed for the given user. How it determines this is
 * entirely up to the implementation of the security manager. It is possible
 * that a security manager implementation may "nest" other security managers -
 * e.g. call other security managers and make sure that they all agree that the
 * user has access to the given operation.
 *
 * @author Michael Nash
 */
public interface AuthorizationManager
{
	public static String ROLE = "de.iritgo.aktera.authorization.AuthorizationManager";

	public static String ID = "de.iritgo.aktera.authorization.AuthorizationManager";

	/**
	 * Is the specified operation allowed for the given
	 * principal?
	 */
	public boolean allowed(Operation o, UserEnvironment ue) throws AuthorizationException;

	/**
	 * If we are supplied with a context then we can determine the subject
	 * by getting the UserEnvironment from the context.
	 */
	public boolean allowed(Operation o, Context c) throws AuthorizationException;

	/**
	 * If we don't specify an operation, determine if the
	 * user is allowed to do *anything* with this
	 * service. This is used for securing the entire service - e.g. a Model
	 */
	public boolean allowed(Object service, UserEnvironment ue) throws AuthorizationException;

	/**
	 * Same as allowed(Principal), but supplied with a Context, we can determine the subject
	 * on our own by reading it from the UserEnvironment
	 */
	public boolean allowed(Object service, Context c) throws AuthorizationException;

	/**
	 * Describe method allowed() here.
	 * @param service
	 * @param id
	 * @param ue
	 *
	 * @return
	 * @throws AuthorizationException
	 */
	public boolean allowed(Object service, String serviceId, UserEnvironment ue) throws AuthorizationException;
}
