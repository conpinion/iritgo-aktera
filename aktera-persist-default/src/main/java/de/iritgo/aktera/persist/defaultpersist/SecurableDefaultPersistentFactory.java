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

package de.iritgo.aktera.persist.defaultpersist;


import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.authorization.InstanceSecurable;
import org.apache.avalon.framework.service.ServiceException;


/**
 * @author Michael Nash
 *
 * A very simple extension to DefaultPersistentFactory that allows instance-level
 * security. Use this service instead of DefaultPersistentFactory if you want to
 * secure entire factories independantly. If you want to secure *all* persistent
 * factories at once, DefaultPersistentFactory can still be used.
 *
 * NOTE: If you secure a persistent factory, make sure you use a
 * auth-manager for it (using the "am=" attribute) that is something
 * other than a AuthorizationManager.  AuthorizationManager uses
 * PersistentFactory internally, and that results in infinite auth. loops.
 *
 * @version                 $Revision: 1.4 $ $Date: 2003/11/23 03:28:22 $
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.PersistentFactory
 * @x-avalon.info name=securable-default-persistent
 * @x-avalon.lifestyle type=singleton
 *
 */
public class SecurableDefaultPersistentFactory extends DefaultPersistentFactory implements InstanceSecurable
{
	private AuthorizationManager authMgr = null;

	//    public String getInstanceIdentifier() {
	//        return myName;
	//    }

	/**
	 * @see de.iritgo.aktera.authorization.Securable#setAuthorizationManager(de.iritgo.aktera.authorization.AuthorizationManager)
	 */
	public void setAuthorizationManager(AuthorizationManager am) throws ServiceException
	{
		this.authMgr = am;
	}

	/**
	 * @see de.iritgo.aktera.authorization.Securable#getAuthorizationManager()
	 */
	public AuthorizationManager getAuthorizationManager()
	{
		return authMgr;
	}
}
