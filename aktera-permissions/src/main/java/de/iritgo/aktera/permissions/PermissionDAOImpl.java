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

package de.iritgo.aktera.permissions;


import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroup;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.permissions.entity.Permission;


@Transactional(readOnly = true)
public class PermissionDAOImpl extends HibernateDaoSupport implements PermissionDAO
{
	public List<Permission> findUserPermissions(AkteraUser user)
	{
		return getHibernateTemplate().find("from Permission where principalId = ? and principalType = 'U'",
						user.getId());
	}

	public List<Permission> findGroupPermissions(AkteraGroup group)
	{
		return getHibernateTemplate().find("from Permission where principalId = ? and principalType = 'G'",
						group.getId());
	}

	@Transactional(readOnly = false)
	public void deletePermissionWithObjectTypeAndObjectId(String objectType, Integer objectId)
	{
		getHibernateTemplate().bulkUpdate("delete from Permission where objectType = ? and objectId = ?", new Object[]
		{
						objectType, objectId
		});
	}

	@Transactional(readOnly = false)
	public void deleteAllPermissionsOfPrincipal(Integer principalId, String principalType)
	{
		getHibernateTemplate().bulkUpdate("delete from Permission where principalId = ? and principalType = ?",
						new Object[]
						{
										principalId, principalType
						});
	}
}
