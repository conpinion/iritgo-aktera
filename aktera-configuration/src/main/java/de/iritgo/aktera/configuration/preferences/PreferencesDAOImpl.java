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


import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Transactional(readOnly = true)
public class PreferencesDAOImpl extends HibernateDaoSupport implements PreferencesDAO
{
	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesDAO#findPreferencesByUserId(java.lang.Integer)
	 */
	public Preferences findPreferencesByUserId(Integer userId)
	{
		HibernateTemplate htl = getHibernateTemplate();
		List<Preferences> res = htl.find("from Preferences where userId = ?", userId);

		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesDAO#findPreferencesConfig(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	public PreferencesConfig findPreferencesConfig(Integer userId, String category, String name)
	{
		HibernateTemplate htl = getHibernateTemplate();
		List<PreferencesConfig> res = htl.find("from PreferencesConfig where userId = ? and category = ? and name = ?",
						new Object[]
						{
										userId, category, name
						});

		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesDAO#updatePreferencesConfig(de.iritgo.aktera.configuration.preferences.PreferencesConfig)
	 */
	@Transactional(readOnly = false)
	public void updatePreferencesConfig(PreferencesConfig preferencesConfig)
	{
		getHibernateTemplate().update(preferencesConfig);
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesDAO#updatePreferencesConfig(java.lang.Integer, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Transactional(readOnly = false)
	public void updatePreferencesConfig(Integer userId, String category, String name, Object value)
	{
		PreferencesConfig pc = findPreferencesConfig(userId, category, name);

		if (pc != null)
		{
			pc.setValue(value.toString());
			updatePreferencesConfig(pc);
		}
	}

	@Transactional(readOnly = false)
	public void create(Preferences preferences)
	{
		getHibernateTemplate().saveOrUpdate(preferences);
	}
}
