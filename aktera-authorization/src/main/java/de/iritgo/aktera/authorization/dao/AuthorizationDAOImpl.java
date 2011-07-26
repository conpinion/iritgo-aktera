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
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import java.util.List;


/**
 * DAO implementation for authorization entities.
 */
public class AuthorizationDAOImpl extends HibernateDaoSupport implements AuthorizationDAO
{
	public ComponentSecurity findComponentSecurityById (String component, String group)
	{
		List<ComponentSecurity> res = getHibernateTemplate ().find (
						"from ComponentSecurity where component = ? and groupName = ?", new Object[]
						{
										component, group
						});

		return res.size () > 0 ? res.get (0) : null;
	}

	public InstanceSecurity findInstanceSecurityById (String component, String instance, String group)
	{
		List<InstanceSecurity> res = getHibernateTemplate ().find (
						"from InstanceSecurity where component = ? and instance = ? and groupName = ?", new Object[]
						{
										component, instance, group
						});

		return res.size () > 0 ? res.get (0) : null;
	}
}
