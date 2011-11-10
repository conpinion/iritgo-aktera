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


import de.iritgo.aktera.authorization.InvokationSecurable;
import de.iritgo.aktera.persist.AbstractHelper;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * Invokationsecurity is used to compare the current "state"
 * of the component with a series of "rules" that must be
 * complied with
 *
 * @persist.persistent
 *   id="invokationsecurity"
 *   schema="component-security"
 *   name="invokationsecurity"
 *   table="InvokationSecurity"
 *   descrip="Component Invokation-Level Operation Security"
 *   securable="true"
 *   am-bypass-allowed="true"
 *   helper="de.iritgo.aktera.authorization.entity.InvokationSecurity"
 *
 * @author Michael Nash, Santanu Dutt
 */
public class InvokationSecurity extends AbstractHelper implements Serializable
{
	public static class PrimaryKey implements Serializable
	{
		/** */
		private static final long serialVersionUID = 1L;

		@Column(nullable = false, length = 132)
		private String component;

		@Column(nullable = false, length = 60)
		private String instance;

		@Column(nullable = false, length = 80)
		private String groupName;

		@Column(nullable = false, length = 2)
		private String comparator;

		@Column(nullable = false, length = 80)
		private String property;

		@Column(nullable = false, length = 80)
		private String value;
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
	 *   name="instance"
	 *   db-name="Instance"
	 *   type="varchar"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Instance Id"
	 *   length="60"
	 */
	public String getInstance()
	{
		return primaryKey.instance;
	}

	/**
	 * Describe method setInstance() here.
	 *
	 * @param string
	 */
	public void setInstance(String string)
	{
		primaryKey.instance = string;
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
	 * @param newName
	 */
	public void setGroupName(String newName)
	{
		primaryKey.groupName = newName;
	}

	/**
	 * @persist.field
	 *   name="comparator"
	 *   db-name="Comparator"
	 *   type="varchar"
	 *   length="2"
	 *   null-allowed="false"
	 *   primary-key="true"
	 *   descrip="Comparison Operation"
	 */
	public String getComparator()
	{
		return primaryKey.comparator;
	}

	/**
	 * Describe method setComparator() here.
	 *
	 * @param string
	 */
	public void setComparator(String string)
	{
		primaryKey.comparator = string;
	}

	/**
	 * @persist.field
	 *   name="property"
	 *   db-name="Property"
	 *   type="varchar"
	 *   length="80"
	 *   null-allowed="false"
	 *   descrip="Property Name"
	 *   primary-key="true"
	 * @return
	 */
	public String getProperty()
	{
		return primaryKey.property;
	}

	/**
	 * Describe method setProperty() here.
	 *
	 * @param string
	 */
	public void setProperty(String string)
	{
		primaryKey.property = string;
	}

	/**
	 * @persist.field
	 *   name="value"
	 *   db-name="Value"
	 *   type="varchar"
	 *   length="80"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Property Value"
	 * @return
	 */
	public String getValue()
	{
		return primaryKey.value;
	}

	/**
	 * Describe method setValue() here.
	 *
	 * @param string
	 */
	public void setValue(String string)
	{
		primaryKey.value = string;
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
	 * @param s
	 */
	public void setOperationsAllowed(String s)
	{
		operationsAllowed = s;
	}

	/**
	 * Validate that the class being referenced is indeed an InstanceSecurable
	 */
	public void beforeAdd(Persistent current)
	{
		validate(current);
	}

	public void beforeUpdate(Persistent current)
	{
		validate(current);
	}

	private void validate(Persistent current)
	{
		try
		{
			if (current.getField("operationsallowed").equals(""))
			{
				current.setField("operationsallowed", "*");
			}

			String comp = current.getFieldString("component");

			try
			{
				Object o = Class.forName(comp).newInstance();

				if (! (o instanceof InvokationSecurable))
				{
					throw new IllegalArgumentException(
									"Component '"
													+ comp
													+ "' is not an InvokationSecurable, and cannot be secured as an InvokationSecurable."
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
		if (! (object instanceof InvokationSecurity))
		{
			return false;
		}

		InvokationSecurity rhs = (InvokationSecurity) object;

		if ((rhs.getOperationsAllowed().equals(getOperationsAllowed())) && (rhs.getComponent().equals(getComponent()))
						&& (rhs.getGroupName().equals(getGroupName())) && (rhs.getComparator().equals(getComparator())))
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
		return new String(getComponent() + getOperationsAllowed() + getGroupName() + getComparator()).hashCode();
	}
}
