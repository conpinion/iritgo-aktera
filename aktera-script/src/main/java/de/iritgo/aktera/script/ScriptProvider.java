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

package de.iritgo.aktera.script;


import de.iritgo.simplelife.data.KeyedValue2;
import java.util.List;


/**
 * Each script provider must implement this interface.
 */
public interface ScriptProvider
{
	/**
	 * Find a script by it's name.
	 *
	 * @param scriptName The script name
	 * @return The script
	 * @throws ScriptNotFoundException If the script was not found
	 */
	public Script find(String scriptName) throws ScriptNotFoundException;

	/**
	 * List the names of all scripts known to this provider.
	 *
	 * @return A list of script id, name, display names, keyed by name
	 */
	public List<KeyedValue2<String, Integer, String>> listScriptNames();

	/**
	 * List the names of all scripts that implement a specific method.
	 *
	 * @param methodName The name of the implemented method
	 * @return A list of script id, name, display names, keyed by name
	 */
	public List<KeyedValue2<String, Integer, String>> listScriptNamesByImplementedMethod(String methodName);

	/**
	 * Mark the specified script as invalid. This instructs the script provider
	 * to remove the script from any caches.
	 *
	 * @param scriptName The name of the script
	 */
	public void invalidate(String scriptName);

	/**
	 * Retrieve a script name by a script id.
	 *
	 * @param id The script id
	 * @return The script name or null if none was found
	 */
	public String findScriptNameById(Integer id);

	/**
	 * Retrieve a script display name by a script id.
	 *
	 * @param id The script id
	 * @return The script name or null if none was found
	 */
	public String findScriptDisplayNameById(Integer id);
}
