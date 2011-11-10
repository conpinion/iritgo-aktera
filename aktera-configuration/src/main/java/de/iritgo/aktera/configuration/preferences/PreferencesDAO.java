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


public interface PreferencesDAO
{
	/** Name of the preferences manager */
	public static final String ID = "de.iritgo.aktera.configuration.PreferencesDAO";

	/**
	 * Retrieve the preferences object for the given user id
	 *
	 * @param userId The user id
	 * @return The preferences object
	 */
	public Preferences findPreferencesByUserId(Integer userId);

	/**
	 * Find a preferences config object by it's unique user id, category and name.
	 *
	 * @param userId The user id
	 * @param category The preferences category
	 * @param name The preferences name
	 * @return The preferences category object or null if none was found
	 */
	public PreferencesConfig findPreferencesConfig(Integer userId, String category, String name);

	/**
	 * Update a preferences config object
	 *
	 * @param preferencesConfig The preferences config object to update
	 */
	public void updatePreferencesConfig(PreferencesConfig preferencesConfig);

	/**
	 * Update a preferences config object
	 *
	 * @param userId The user id
	 * @param category The preferences category
	 * @param name The preferences name
	 * @param value The new preferences value
	 */
	public void updatePreferencesConfig(Integer userId, String category, String name, Object value);

	/**
	 * Store a given preferences object in the db
	 *
	 * @param preferences The preference
	 */
	public void create(Preferences preferences);
}
