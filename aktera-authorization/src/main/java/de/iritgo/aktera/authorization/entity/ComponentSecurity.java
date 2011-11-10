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

package de.iritgo.aktera.authorization.entity;


import de.iritgo.aktera.authorization.InstanceSecurable;
import de.iritgo.aktera.authorization.InvokationSecurable;
import de.iritgo.aktera.authorization.Securable;
import de.iritgo.aktera.persist.AbstractHelper;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * @persist.persistent
 *   id="componentsecurity"
 *   schema="component-security"
 *   name="componentsecurity"
 *   table="ComponentSecurity"
 *   descrip="Component Security"
 *   securable="true"
 *   am-bypass-allowed="true"
 *   helper="de.iritgo.aktera.authorization.entity.ComponentSecurity"
 */
@Entity
public class ComponentSecurity extends AbstractHelper implements Serializable
{
	public static class PrimaryKey implements Serializable
	{
		/** */
		private static final long serialVersionUID = 1L;

		@Column(nullable = false, length = 132)
		private String component;

		@Column(nullable = false, length = 80)
		private String groupName;
	}

	/** */
	private static final long serialVersionUID = 1L;

	/** */
	@EmbeddedId
	private PrimaryKey primaryKey = new PrimaryKey();

	@Column(nullable = false, length = 30)
	private String operationsAllowed;

	/**
	 * @persist.field
	 *   name="component"
	 *   db-name="Component"
	 *   type="varchar"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Component Name"
	 *   length="132"
	 */
	public String getComponent()
	{
		return primaryKey.component;
	}

	/**
	 * Describe method setComponent() here.
	 *
	 * @param string
	 */
	public void setComponent(String string)
	{
		primaryKey.component = string;
	}

	/**
	 * @persist.field
	 *   name="groupname"
	 *   db-name="GroupName"
	 *   type="varchar"
	 *   length="80"
	 *   null-allowed="false"
	 *   descrip="Group Name"
	 *   primary-key="true"
	 */
	public String getGroupName()
	{
		return primaryKey.groupName;
	}

	/**
	 * Describe method setGroupName() here.
	 *
	 * @param string
	 */
	public void setGroupName(String string)
	{
		primaryKey.groupName = string;
	}

	/**
	 * @persist.field
	 *   name="operationsallowed"
	 *   db-name="OperationsAllowed"
	 *   type="varchar"
	 *   null-allowed="false"
	 *   descrip="Operations Allowed"
	 *   length="30"
	 */
	public String getOperationsAllowed()
	{
		return operationsAllowed;
	}

	/**
	 * Describe method setOperationsAllowed() here.
	 *
	 * @param b
	 */
	public void setOperationsAllowed(String b)
	{
		operationsAllowed = b;
	}

	/**
	 * @see de.iritgo.aktera.persist.AbstractHelper#beforeAdd(de.iritgo.aktera.persist.Persistent)
	 */
	public void beforeAdd(Persistent current)
	{
		validate(current);
	}

	/**
	 * @see de.iritgo.aktera.persist.AbstractHelper#beforeUpdate(de.iritgo.aktera.persist.Persistent)
	 */
	public void beforeUpdate(Persistent current)
	{
		validate(current);
	}

	/**
	 * Describe method validate() here.
	 *
	 * @param current
	 */
	private void validate(Persistent current)
	{
		try
		{
			String comp = current.getFieldString("component");

			try
			{
				Object o = Class.forName(comp).newInstance();

				if (! (o instanceof Securable))
				{
					throw new IllegalArgumentException("Component '" + comp + " is not Securable:" + current.toString());
				}

				if (o instanceof InstanceSecurable)
				{
					throw new IllegalArgumentException(
									"Component '"
													+ comp
													+ "' is an InstanceSecurable, and cannot be secured as a Securable. Put entries for this comoponent in InstanceSecurable instead."
													+ current.toString());
				}

				if (o instanceof InvokationSecurable)
				{
					throw new IllegalArgumentException(
									"Component '"
													+ comp
													+ "' is an InvokationSecurable, and cannot be secured as an InstanceSecurable. Put entries for this component in InvokationSecurable instead."
													+ current.toString());
				}
			}
			catch (ClassNotFoundException ce)
			{
				throw new IllegalArgumentException("No such class as '" + comp + "'");
			}
			catch (IllegalAccessException ie)
			{
				throw new IllegalArgumentException("Unable to access class '" + comp + "':" + ie.getMessage());
			}
			catch (InstantiationException iae)
			{
				throw new IllegalArgumentException("Instantiation exception accessing class '" + comp + "':"
								+ iae.getMessage());
			}
		}
		catch (PersistenceException pe)
		{
			throw new IllegalArgumentException(pe.getMessage());
		}
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object)
	{
		if (! (object instanceof ComponentSecurity))
		{
			return false;
		}

		ComponentSecurity rhs = (ComponentSecurity) object;

		if ((rhs.getGroupName().equals(getGroupName())) && (rhs.getComponent().equals(getComponent())))
		{
			return true;
		}

		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return new String(getGroupName() + getComponent()).hashCode();
	}
}
