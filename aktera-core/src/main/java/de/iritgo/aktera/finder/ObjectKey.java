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

package de.iritgo.aktera.finder;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author @author eliotc <a href="mailto:eliotc@auditIntegrity.com"
 * >eliotc@auditIntegrity.com</a>
 * @version 1.0
 */
public class ObjectKey implements Comparable
{
	// ===========================
	// Standard Attributes
	// ===========================
	private Object uniqueId;

	private String name;

	private String alias;

	// ===========================
	// Additional Attributes
	// ===========================
	private Map additionalAttributes = new HashMap ();

	// ===========================
	// Constructors
	// ===========================	
	/**
	 * The default constructor
	 */
	public ObjectKey ()
	{
	}

	/**
	 * The constructor with all standard attributes
	 * @param aUniqueId
	 * @param aName
	 * @param aAlias
	 */
	public ObjectKey (Object aUniqueId, String aName, String aAlias)
	{
		this.uniqueId = aUniqueId;
		this.name = aName;
		this.alias = aAlias;
	}

	public void setAttribute (String attribName, Object attribValue)
	{
		additionalAttributes.put (attribName, attribValue);
	}

	public Object getAttribute (String attribName)
	{
		return additionalAttributes.get (attribName);
	}

	public Set getAttributeNames ()
	{
		return additionalAttributes.keySet ();
	}

	// ===========================
	// Getters and setters
	// ===========================
	public String getAlias ()
	{
		return alias;
	}

	public Object getUniqueId ()
	{
		return uniqueId;
	}

	public String getName ()
	{
		return name;
	}

	public void setAlias (String alias)
	{
		this.alias = alias;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public void setUniqueId (Object uniqueId)
	{
		this.uniqueId = uniqueId;
	}

	// ===================================
	// Over-write of generic object methods
	// ===================================

	/**
	 * This method implements the comparable interface.  Object ordering
	 * is based exclusively on the struct id ordering.
	 */
	public int compareTo (Object o)
	{
		ObjectKey k = (ObjectKey) o;

		return (this.name).compareTo (k.getName ());
	}

	/**
	 * This method overwrites the default equals method. Of course
	 * to objects are equal iff they have the same unique id.
	 */
	public boolean equals (Object o)
	{
		boolean returnValue = false;

		if (o instanceof ObjectKey)
		{
			ObjectKey oKey = (ObjectKey) o;

			returnValue = oKey.getUniqueId ().equals (this.uniqueId);
		}

		return returnValue;
	}

	/*
	 * Needs to be implemented for this class to be defined correctly .
	 * @see java.lang.Object#hashCode()
	        public int hashCode()
	        {
	                return super.hashCode();
	        }
	 */
}
