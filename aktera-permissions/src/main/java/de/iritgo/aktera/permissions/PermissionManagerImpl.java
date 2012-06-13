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


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.avalon.framework.logger.Logger;
import org.springframework.transaction.annotation.Transactional;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraGroup;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.permissions.entity.Permission;
import de.iritgo.aktera.permissions.security.Acl;
import de.iritgo.aktera.permissions.security.AclEntry;
import de.iritgo.aktera.permissions.security.Group;
import de.iritgo.aktera.permissions.security.NotOwnerException;
import de.iritgo.aktera.permissions.security.Principal;
import de.iritgo.aktera.permissions.security.SimplePermission;
import de.iritgo.simplelife.data.Tuple2;


/**
 * Permission manager service implementation.
 */
@Transactional(readOnly = true)
public class PermissionManagerImpl implements PermissionManager
{
	/** A list of all permission meta data */
	private List<PermissionMetaData> permissionMetaData = new LinkedList<PermissionMetaData>();

	/** A map from permission ids to permission meta data */
	private Map<String, PermissionMetaData> permissionMetaDataById = null;

	public void setPermissionMetaData(List<PermissionMetaData> permissionMetaData)
	{
		this.permissionMetaData = permissionMetaData;
	}

	public List<PermissionMetaData> getPermissionMetaData()
	{
		return permissionMetaData;
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#getMetaDataById(java.lang.String)
	 */
	public synchronized PermissionMetaData getMetaDataById(String id)
	{
		if (permissionMetaDataById == null)
		{
			permissionMetaDataById = new HashMap<String, PermissionMetaData>();
			for (PermissionMetaData pmd : permissionMetaData)
			{
				permissionMetaDataById.put(pmd.getId(), pmd);
			}
		}
		return permissionMetaDataById.get(id);
	}

	/** Our logger. */
	private Logger logger;

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/** The user DAO */
	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	/** The permissionDAO */
	private PermissionDAO permissionDAO;

	public void setPermissionDAO(PermissionDAO permissionDAO)
	{
		this.permissionDAO = permissionDAO;
	}

	/** Cached principals. */
	protected Map<String, Principal> principals = new HashMap<String, Principal>();

	/** Cached groups. */
	protected Map<String, Group> groups = new HashMap<String, Group>();

	/** ACL owner. */
	protected static final String ROOT_NAME = "root";

	/** ACL owner. */
	protected static Principal ROOT = new Principal(ROOT_NAME);

	/** Per domain object ACL */
	protected Map<Tuple2<String, Integer>, Acl> aclByDomainObject = new HashMap();

	/** Dummy object type for global permissions */
	protected static final String GLOBAL_OBJECT_TYPE = "*";

	/**
	 * Constructor for QuartzScheduler.
	 */
	public PermissionManagerImpl()
	{
		super();
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#hasPermission(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean hasPermission(String userName, String permission)
	{
		return hasPermission(userName, permission, GLOBAL_OBJECT_TYPE, null);
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#hasPermission(java.lang.Integer,
	 *      java.lang.String)
	 */
	public boolean hasPermission(Integer userId, String permission)
	{
		return hasPermission(userId, permission, GLOBAL_OBJECT_TYPE, null);
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#hasPermission(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public synchronized boolean hasPermission(String userName, String permission, String objectType, Integer objectId)
	{
		if ("admin".equals(userName))
		{
			return true;
		}

		loadAclForUser(userName);
		Principal principal = (Principal) principals.get(userName);
		if (principal == null)
		{
			return false;
		}

		Acl acl = aclByDomainObject.get(new Tuple2(objectType, objectId));
		if (acl != null && acl.checkPermission(principal, new SimplePermission(permission)))
		{
			return true;
		}

		return false;
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#hasPermission(java.lang.Integer,
	 *      java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public synchronized boolean hasPermission(Integer userId, String permission, String objectType, Integer objectId)
	{
		return hasPermission(userDAO.findUserById(userId).getName(), permission, objectType, objectId);
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#hasPermissionOnUserOrGroup(java.lang.String,
	 *      java.lang.String, java.lang.Integer)
	 */
	public synchronized boolean hasPermissionOnUserOrGroup(String userName, String permission, Integer userId)
	{
		if ("admin".equals(userName))
		{
			return true;
		}

		loadAclForUser(userName);
		Principal principal = (Principal) principals.get(userName);
		if (principal == null)
		{
			return false;
		}

		Acl acl = aclByDomainObject.get(new Tuple2(AkteraUser.class.getName(), userId));
		if (acl != null && acl.checkPermission(principal, new SimplePermission(permission)))
		{
			return true;
		}

		for (Integer groupId : userDAO.listGroupIdsOfUserId(userId.intValue()))
		{
			acl = aclByDomainObject.get(new Tuple2(AkteraGroup.class.getName(), groupId));
			if (acl != null && acl.checkPermission(principal, new SimplePermission(permission)))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Load all acl's for a given user.
	 *
	 * @param userName The user name which acl's should be loaded.
	 */
	protected synchronized void loadAclForUser(String userName)
	{
		Principal principal = (Principal) principals.get(userName);
		if (principal != null)
		{
			return;
		}

		AkteraUser akteraUser = userDAO.findUserByName(userName);
		if (akteraUser == null)
		{
			return;
		}

		principal = new Principal(userName);
		principals.put(userName, principal);

		try
		{
			for (AkteraGroup akteraGroup : userDAO.findGroupsByUser(akteraUser))
			{
				String groupName = akteraGroup.getName();
				Group group = (Group) groups.get(groupName);
				if (group == null)
				{
					group = new Group(groupName);
					groups.put(groupName, group);

					for (Permission permissionEntity : permissionDAO.findGroupPermissions(akteraGroup))
					{
						Tuple2 aclKey = new Tuple2(permissionEntity.getObjectType() != null ? permissionEntity
										.getObjectType() : GLOBAL_OBJECT_TYPE, permissionEntity.getObjectId());
						Acl acl = aclByDomainObject.get(aclKey);
						if (acl == null)
						{
							acl = new Acl(ROOT, ROOT_NAME);
							aclByDomainObject.put(aclKey, acl);
						}
						AclEntry aclEntry = acl.findAclEntry(group, permissionEntity.getNegative());
						if (aclEntry == null)
						{
							aclEntry = new AclEntry(group);
							if (permissionEntity.getNegative())
							{
								aclEntry.setNegativePermissions();
							}
							acl.addEntry(ROOT, aclEntry);
						}
						aclEntry.addPermission(new SimplePermission(permissionEntity.getPermission()));
					}
				}

				if (! group.isMember(principal))
				{
					group.addMember(principal);
				}
			}

			for (Permission permissionEntity : permissionDAO.findUserPermissions(akteraUser))
			{
				Tuple2 aclKey = new Tuple2(permissionEntity.getObjectType() != null ? permissionEntity.getObjectType()
								: GLOBAL_OBJECT_TYPE, permissionEntity.getObjectId());
				Acl acl = aclByDomainObject.get(aclKey);
				if (acl == null)
				{
					acl = new Acl(ROOT, ROOT_NAME);
					aclByDomainObject.put(aclKey, acl);
				}
				AclEntry aclEntry = acl.findAclEntry(principal, permissionEntity.getNegative());
				if (aclEntry == null)
				{
					aclEntry = new AclEntry(principal);
					if (permissionEntity.getNegative())
					{
						aclEntry.setNegativePermissions();
					}
					acl.addEntry(ROOT, aclEntry);
				}
				aclEntry.addPermission(new SimplePermission(permissionEntity.getPermission()));
			}
		}
		catch (NotOwnerException x)
		{
			logger.error("Unable to load permissions for user '" + userName + "': " + x);
		}
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#clear()
	 */
	public synchronized void clear()
	{
		principals = new HashMap<String, Principal>();
		groups = new HashMap<String, Group>();
		ROOT = new Principal("root");
		aclByDomainObject.clear();
	}

	/**
	 * @see de.iritgo.aktera.permissions.PermissionManager#deleteAllPermissionsOfPrincipal(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Transactional(readOnly = false)
	public void deleteAllPermissionsOfPrincipal(Integer principalId, String principalType)
	{
		permissionDAO.deleteAllPermissionsOfPrincipal(principalId, principalType);
		clear();
	}

	public String dumpAll ()
	{
		clear ();
		StringBuffer buffer = new StringBuffer ();
		for (AkteraUser user : userDAO.findAllUsers())
		{
			buffer.append ("\n\nPermissions for user: " + user.getName()+ "\n");
			for (AkteraGroup akteraGroup : userDAO.findGroupsByUser(user))
			{
				String groupName = akteraGroup.getName();
				buffer.append ("  Group: " + groupName + "\n");
				for (Permission permissionEntity : permissionDAO.findGroupPermissions(akteraGroup))
				{
					buffer.append ("  - " + permissionEntity.getPermission() + ":" + permissionEntity.getPrincipalType() + ":" + permissionEntity.getObjectType() + ">" + permissionEntity.getObjectId() + "\n");
				}
			}
		}
		return buffer.toString ();
	}
}
