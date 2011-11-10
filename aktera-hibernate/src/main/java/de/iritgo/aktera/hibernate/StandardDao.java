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

package de.iritgo.aktera.hibernate;


import de.iritgo.simplelife.constants.SortOrder;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;


public interface StandardDao
{
	public static final String ID = "de.iritgo.aktera.hibernate.StandardDao";

	public long countByNamedQuery(final String query, final Properties params);

	public long countByNamedFindQuery(final String query, final Properties params);

	public long countByQuery(final String query, final Properties params);

	public long countByFindQuery(final String query, final Properties params);

	public List findByNamedQuery(final String query, final Properties params, final int firstResult,
					final int maxResults, final String orderBy, final SortOrder orderDir);

	public List findByQuery(final String query, final Properties params, final int firstResult, final int maxResults,
					final String orderBy, SortOrder orderDir);

	public Object get(String entityName, Serializable id);

	public Object newEntity(String entityName) throws InstantiationException, IllegalAccessException;

	public void update(Object entity);

	public void create(Object entity);

	public void delete(Object entity);
}
