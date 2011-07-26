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

package de.iritgo.aktera.configuration;


/**
 * System config DAO.
 */
public interface SystemConfigDAO
{
	/**
	 * Retrieve a config value by category and name.
	 *
	 * @param category The config category
	 * @param name The config name
	 * @return The config value or null if none was found
	 */
	public SystemConfig findByCategoryAndName (String category, String name);

	/**
	 * Create or update a config value.
	 *
	 * @param configValue The config value
	 */
	public void createOrUpdate (SystemConfig configValue);

	/**
	 * Delete a config value.
	 *
	 * @param configValue The config value to delete
	 */
	public void delete (SystemConfig configValue);

	/**
	 * Delete a config value.
	 *
	 * @param category The config category
	 * @param name The config name
	 */
	public void delete (String category, String name);
}
