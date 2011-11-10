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

package de.iritgo.aktera.authorization.dao;


import de.iritgo.aktera.authorization.entity.ComponentSecurity;
import de.iritgo.aktera.authorization.entity.InstanceSecurity;


/**
 * DAO interface for authorization entities.
 */
public interface AuthorizationDAO
{
	/**
	 * Find a component security by it's id, which consists of the component
	 * name and the principal group name.
	 *
	 * @param component The component name
	 * @param group The group name
	 * @return The ComponentSecurity or null if none was found
	 */
	public ComponentSecurity findComponentSecurityById(String component, String group);

	/**
	 * Find a instance security by it's id, which consists of the component
	 * name, instance name and the principal group name.
	 *
	 * @param component The component name
	 * @param group The group name
	 * @return The ComponentSecurity or null if none was found
	 */
	public InstanceSecurity findInstanceSecurityById(String component, String instance, String group);
}
