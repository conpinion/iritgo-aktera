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

package de.iritgo.aktera.aktario.user;


import de.iritgo.aktera.aktario.db.IritgoUser;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * The keel user dao
 */
@Transactional(readOnly = true)
public class IritgoUserDAOImpl extends HibernateDaoSupport implements IritgoUserDAO
{
	/**
	 * @see de.iritgo.aktera.aktario.user.IritgoUserDAO#findIritgoUserByName(java.lang.String)
	 */
	public IritgoUser findIritgoUserByName(String name)
	{
		List<IritgoUser> res = getHibernateTemplate().find("from IritgoUser where name = ?", name);

		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * @see de.iritgo.aktera.aktario.user.IritgoUserDAO#updateIritgoUser(de.iritgo.aktera.aktario.db.IritgoUser)
	 */
	@Transactional(readOnly = false)
	public void updateIritgoUser(IritgoUser iritgoUser)
	{
		getHibernateTemplate().update(iritgoUser);
	}
}
