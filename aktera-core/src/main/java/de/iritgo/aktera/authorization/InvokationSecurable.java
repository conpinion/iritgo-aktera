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


import java.util.Map;


/**
 * An InvokationSecurable component builds on the concept
 * of an InstanceSecurable. In addition to the particular
 * instance of the component being able to be secured
 * independantly, each operation requested may or may not
 * be able to authorized depending on the current value
 * of any of a number of properties that the InvokationSecurable
 * currently posseses. These properties can be compared
 * (via authorization rules) to the current parameters
 * of the requested operation, for example). This can be
 * used, for example, to allow access to a particular
 * persistent only when the value of a certain field
 * contains the uid of the currently logged-in user (e.g.
 * only the "owner" of the record can modify it).
 *
 * @author Michael Nash
 */
public interface InvokationSecurable extends InstanceSecurable
{
	/**
	 * Return a map of properties representing the
	 * current state of this InvokationSecurable object.
	 * The authorization manager then usese these properties
	 * to determine if the current operation is
	 * permitted
	 */
	public Map getAuthorizationProperties();
}
