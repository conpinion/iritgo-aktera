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

package de.iritgo.aktera.configuration.preferences;


/**
 * The preference manager lets you retrieve and update preference values of
 * the users.
 */
public interface PreferencesManager
{
	public static final String ID = "de.iritgo.aktera.configuration.PreferencesManager";

	/**
	 * Remove all cached values of the specified user.
	 *
	 * @param userId The id of the user
	 */
	public void clearCache(Integer userId);

	/**
	 * Create the default prefrence configs for the given user id
	 *
	 * @param userId The user id
	 */
	public void createDefaultPrefrenceConfigsForUserId(Integer userId);

	/**
	 * Retrieve a preference value.
	 *
	 * @param userId The id of the user
	 * @param category The preference category
	 * @param name The preference name
	 * @return The preference value or null if none was found
	 */
	public Object get(Integer userId, String category, String name);

	/**
	 * Retrieve a preference value, eventually converting it to an int.
	 *
	 * @param userId The id of the user
	 * @param category The preference category
	 * @param name The preference name
	 * @param defaultValue The default value that is returned if the preference
	 *   value wasn't found
	 * @return The preference value
	 */
	public Integer getInt(Integer userId, String category, String name, Integer defaultValue);

	/**
	 * Retrieve a preference value, eventually converting it to a string.
	 *
	 * @param userId The id of the user
	 * @param category The preference category
	 * @param name The preference name
	 * @param defaultValue The default value that is returned if the preference
	 *   value wasn't found
	 * @return The preference value
	 */
	public String getString(Integer userId, String category, String name, String defaultValue);

	/**
	 * Set a preference value.
	 *
	 * @param userId The id of the user
	 * @param category The preference category
	 * @param name The preference name
	 * @param value The new preference value
	 */
	public void set(Integer userId, String category, String name, Object value);
}
