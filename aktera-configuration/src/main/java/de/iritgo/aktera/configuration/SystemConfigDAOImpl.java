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


import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * System config hibernate DAO.
 */
@Transactional(readOnly = true)
public class SystemConfigDAOImpl extends HibernateDaoSupport implements SystemConfigDAO
{
	/**
	 * @see de.iritgo.aktera.base.configuration.SystemConfigDAO#findByCategoryAndName(java.lang.String, java.lang.String)
	 */
	public SystemConfig findByCategoryAndName (String category, String name)
	{
		List<SystemConfig> res = getHibernateTemplate ().find ("from SystemConfig where category = ? and name = ?",
						new Object[]
						{
										category, name
						});

		return res.size () > 0 ? res.get (0) : null;
	}

	/**
	 * @see de.iritgo.aktera.base.configuration.SystemConfigDAO#createOrUpdate(de.iritgo.aktera.base.configuration.SystemConfig)
	 */
	@Transactional(readOnly = false)
	public void createOrUpdate (SystemConfig configValue)
	{
		SystemConfig config = findByCategoryAndName (configValue.getCategory (), configValue.getName ());

		if (config != null)
		{
			config.setValue (configValue.getValue ());
			getHibernateTemplate ().update (config);
		}
		else
		{
			getHibernateTemplate ().save (configValue);
		}
	}

	/**
	 * @see de.iritgo.aktera.base.configuration.SystemConfigDAO#delete(de.iritgo.aktera.base.configuration.SystemConfig)
	 */
	@Transactional(readOnly = false)
	public void delete (SystemConfig configValue)
	{
		getHibernateTemplate ().delete (configValue);
	}

	/**
	 * @see de.iritgo.aktera.base.configuration.SystemConfigDAO#delete(de.iritgo.aktera.base.configuration.SystemConfig)
	 */
	@Transactional(readOnly = false)
	public void delete (String category, String name)
	{
		SystemConfig config = findByCategoryAndName (category, name);

		if (config != null)
		{
			getHibernateTemplate ().delete (config);
		}
	}
}
