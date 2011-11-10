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

package de.iritgo.aktera.ui.form;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.simplelife.string.StringTools;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Used to describe sets of persistent or bean objects used to retrieve
 * and store formular data.
 */
public class PersistentDescriptor implements Serializable
{
	/**
	 * Join relations between persistents.
	 */
	public static class JoinInfo
	{
		/** The name of the related persistent */
		private String persistent;

		/** The name of the foreign key attribute */
		private String key;

		/** The name of the our key attribute */
		private String myKey;

		/** Additional join condition */
		private String condition;

		/**
		 * Create a mew JoinInfo.
		 *
		 * @param persistent The name of the related persistent
		 * @param key The name of the foreign key attribute
		 * @param myKey The name of the our key attribute
		 * @param condition Additional join condition
		 */
		public JoinInfo(String persistent, String key, String myKey, String condition)
		{
			this.persistent = persistent;
			this.key = key;
			this.myKey = myKey;
			this.condition = condition;
		}

		/**
		 * Create a mew JoinInfo.
		 *
		 * @param persistent The name of the related persistent
		 * @param key The name of the foreign key attribute
		 * @param myKey The name of the our key attribute
		 */
		public JoinInfo(String persistent, String key, String myKey)
		{
			this(persistent, key, myKey, null);
		}

		/**
		 * Get the name of the related persistent.
		 *
		 * @return The persistent name
		 */
		public String getPersistent()
		{
			return persistent;
		}

		/**
		 * Get the name of the foreign key attribute.
		 *
		 * @return The foreign key attribute
		 */
		public String getKey()
		{
			return key;
		}

		/**
		 * Get the name of the our key attribute.
		 *
		 * @return Our key attribute
		 */
		public String getMyKey()
		{
			return myKey;
		}

		/**
		 * Get the additional join condition.
		 *
		 * @return The additional join condition
		 */
		public String getCondition()
		{
			return condition;
		}
	}

	/** */
	private static final long serialVersionUID = 1L;

	/** PersistentDescriptor id */
	private String id;

	/** The persistent objects by name */
	private Map<String, Persistent> persistentMap;

	/** The persistent keys */
	private List<String> keys;

	/** Additional attributes */
	private Map<String, Object> attributes;

	/** Join information */
	private Map<String, JoinInfo> joins;

	/** Key of the last added persistent */
	private String lastKey;

	/**
	 * Create a new PersistentDescriptor.
	 *
	 * @param id The descriptor id
	 */
	public PersistentDescriptor(String id)
	{
		this.id = id;
		persistentMap = new HashMap<String, Persistent>();
		keys = new LinkedList<String>();
		attributes = new HashMap<String, Object>();
		joins = new HashMap<String, JoinInfo>();
	}

	/**
	 * Create a new PersistentDescriptor.
	 */
	public PersistentDescriptor()
	{
		this(null);
	}

	/**
	 * Get the descriptor id.
	 *
	 * @return The descriptor id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Put a persistent object into the descriptor.
	 *
	 * @param key The key of the persistent
	 * @param persistent The persistent object
	 * @return This descriptor
	 */
	public PersistentDescriptor put(String key, Persistent persistent)
	{
		lastKey = key;
		persistentMap.put(key, persistent);
		keys.add(key);

		return this;
	}

	/**
	 * Put a bean object to this descriptor.
	 *
	 * @param key The key of the bean
	 * @param bean The bean object
	 * @return This descriptor
	 */
	public PersistentDescriptor put(String key, Object bean)
	{
		attributes.put(key, bean);

		return this;
	}

	/**
	 * Put an attribute to the descriptor.
	 *
	 * @param key The attribute key
	 * @param value The attribute value
	 */
	public void putAttribute(String key, Object value)
	{
		attributes.put(key.replace('.', '_'), value);
	}

	/**
	 * Depending on the specified persistent (which may be null), either
	 * set a persistent attribute or an additional attribute.
	 *
	 * @param persistent The persistent (may be null)
	 * @param key The attribute key
	 * @param value The attribute value
	 */
	public void putAttribute(Persistent persistent, String persistentName, String key, Object value)
		throws PersistenceException
	{
		if (persistent != null)
		{
			persistent.setField(key, value);
		}
		else
		{
			putAttribute(! StringTools.isTrimEmpty(persistentName) ? persistentName + "_" + key : key, value);
		}
	}

	/**
	 * Put the valid values of an attribute into this descriptor.
	 *
	 * @param key The attribute key
	 * @param validValues The valid values
	 */
	public void putAttributeValidValues(String key, Map validValues)
	{
		putAttribute(key + "ValidValues", validValues);
	}

	/**
	 * Put the valid values of an attribute into this descriptor.
	 *
	 * @param key The attribute key
	 * @param validValues The valid values
	 */
	public void putValidValues(String key, Map validValues)
	{
		putAttributeValidValues(key, validValues);
	}

	/**
	 * Get a persistent or a bean by name.
	 *
	 * @param name The name of the persistent or bean
	 * @return The persistent or bean
	 * @throws ModelException If the no persistent or bean was found
	 */
	public Object get(String name) throws ModelException
	{
		Persistent persistent = persistentMap.get(name);

		if (persistent != null)
		{
			return persistent;
		}

		Object bean = attributes.get(name);

		if (bean != null)
		{
			return bean;
		}

		throw new ModelException("Unable to find persistent or bean '" + name + "' in persistent descriptor '" + id
						+ "'");
	}

	/**
	 * Check if there are any persistent objects stored in this descriptor.
	 *
	 * @return True if there are persistent objects
	 */
	public boolean hasPersistents()
	{
		return ! persistentMap.isEmpty();
	}

	/**
	 * CHeck for the existence of the specified persistent.
	 *
	 * @param name The name of the persistent
	 * @return True if the persistent exists
	 */
	public boolean hasPersistent(String name) throws ModelException
	{
		return persistentMap.containsKey(name);
	}

	/**
	 * Get a persistent by name.
	 *
	 * @param name The name of the persistent
	 * @return The persistent or bean
	 * @throws ModelException If the no persistent was found
	 */
	public Persistent getPersistent(String name) throws ModelException
	{
		Persistent persistent = persistentMap.get(name);

		if (persistent != null)
		{
			return persistent;
		}

		throw new ModelException("Unable to find persistent '" + name + "' in persistent descriptor '" + id + "'");
	}

	/**
	 * Get a persistent meta data by name.
	 *
	 * @param name The name of the persistent
	 * @return The persistent meta data or null if it wasn't found
	 */
	public PersistentMetaData getMetaData(String name)
	{
		try
		{
			Persistent persistent = persistentMap.get(name);

			if (persistent != null)
			{
				return persistent.getMetaData();
			}
		}
		catch (PersistenceException ignored)
		{
		}

		return null;
	}

	/**
	 * Get an attribute from this descriptor.
	 *
	 * @param key The attribute key
	 * @return The attribute value
	 * @throws ModelException If the persistent wasn't found
	 */
	public Object getAttribute(String key) throws ModelException
	{
		return attributes.get(key.replace('.', '_'));
	}

	/**
	 * Get all attributes.
	 *
	 * @return The attributes.
	 */
	public Map getAttributes()
	{
		return attributes;
	}

	/**
	 * Remove an attribute from this descriptor.
	 *
	 * @param key The attribute key
	 */
	public void removeAttribute(String key)
	{
		attributes.remove(key.replace('.', '_'));
	}

	/**
	 * Check for the presence of an attribute.
	 *
	 * @param key The attribute key
	 * @return True if the descriptor contains the attribute
	 */
	public boolean containsAttribute(String key)
	{
		return attributes.get(key.replace('.', '_')) != null;
	}

	/**
	 * Check for the presence of an attribute.
	 *
	 * @param key The attribute key
	 * @return True if the descriptor contains the attribute
	 */
	public boolean hasAttribute(String key)
	{
		return containsAttribute(key);
	}

	/**
	 * Get an iterator over all persistent keys.
	 *
	 * @return A persistent key iterator.
	 */
	public Iterator keyIterator()
	{
		return keys.iterator();
	}

	/**
	 * Add a join information.
	 *
	 * @param persistent The name of the related persistent
	 * @param key The name of the foreign key attribute
	 * @param myKey The name of the our key attribute
	 */
	public PersistentDescriptor join(String persistent, String key, String myKey)
	{
		if (lastKey != null)
		{
			joins.put(lastKey, new JoinInfo(persistent, key, myKey));
		}

		return this;
	}

	/**
	 * Add a join information.
	 *
	 * @param persistent The name of the related persistent
	 * @param key The name of the foreign key attribute
	 * @param myKey The name of the our key attribute
	 * @param condition Additional join condition
	 */
	public PersistentDescriptor join(String persistent, String key, String myKey, String condition)
	{
		if (lastKey != null)
		{
			joins.put(lastKey, new JoinInfo(persistent, key, myKey, condition));
		}

		return this;
	}

	/**
	 * Set a join information.
	 *
	 * @param id The id of the persistent to which the join info should be added
	 * @param persistent The name of the related persistent
	 * @param key The name of the foreign key attribute
	 * @param myKey The name of the our key attribute
	 * @param condition Additional join condition
	 */
	public void setJoin(String id, String persistent, String key, String myKey, String condition)
	{
		joins.put(id, new JoinInfo(persistent, key, myKey, condition));
	}

	/**
	 * Get the join info of a persistent.
	 *
	 * @param key The persistent key
	 * @return The join info
	 */
	public JoinInfo getJoin(String key)
	{
		return (JoinInfo) joins.get(key);
	}

	/**
	 * Mark an attribute as selected/deselected.
	 *
	 * @param attributenName The attribute name
	 * @param selected True if the attribute is selected
	 */
	public void setAttributeSelected(String attributeName, boolean selected)
	{
		if (selected)
		{
			putAttribute(attributeName + "_Selected", true);
		}
		else
		{
			removeAttribute(attributeName);
		}
	}

	/**
	 * Check if an attribute is selected.
	 *
	 * @param attributenName The attribute name
	 * @return True if the attribute is selected
	 * @throws ModelException
	 * @throws ModelException If the persistent wasn't found
	 */
	public boolean isAttributeSelected(String attributeName) throws ModelException
	{
		return getAttribute(attributeName + "_Selected") != null;
	}
}
