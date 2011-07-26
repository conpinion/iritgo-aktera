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

package de.iritgo.aktera.journal;


import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.simplelife.data.Tuple2;
import java.util.Map;


public interface JournalSearch
{
	/**
	 * Return the category id
	 *
	 * @return The category id
	 */
	public String getCategoryId ();

	/**
	 * Return the condition sql string for the listing query
	 *
	 * @param username The username
	 * @param search The search param
	 * @return The query
	 */
	public String getCondition (String search, AkteraUser user);

	/**
	 * Return the translated category label
	 *
	 * @param The user object
	 * @return The translated label for the category
	 */
	public String getCategoryLabel (AkteraUser user);

	/**
	 * Return a map with the key and objects for the condition
	 *
	 * @param search The search string
	 * @param user The user
	 * @return The contition map
	 */
	public Map<String, Object> getConditionMap (String search, AkteraUser user);
}
