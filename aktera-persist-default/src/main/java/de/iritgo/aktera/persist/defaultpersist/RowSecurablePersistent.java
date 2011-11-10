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


import de.iritgo.aktera.authorization.InvokationSecurable;
import java.util.Map;


/**
 * Simple extension to DefaultPersistent to allow for row-level security
 * via the InvokationSecurable
 */
public class RowSecurablePersistent extends DefaultPersistent implements InvokationSecurable
{
	/**
	 * Return all of the fields in this persistent object as the properties
	 * used to determine authorization for this persistent. This allows
	 * any field (or fields) to be used to determine if the user is or is not
	 * allowed to access the record.
	 *
	 * @see de.iritgo.aktera.authorization.InvokationSecurable#getAuthorizationProperties()
	 */
	public Map getAuthorizationProperties()
	{
		return fieldData;
	}
}
