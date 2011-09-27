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


import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.commons.collections.map.LRUMap;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;


public class PreferencesManagerImpl implements PreferencesManager
{
	private PreferencesDAO preferencesDAO;

	/** The size of the LRU map per user that caches preference values */
	private int lruSize = 97;

	/** A LRU map per user (keyed by user id) for preference value caching */
	private Map<Integer, Map<String, Object>> preferencesByUser = new HashMap ();

	public void setPreferencesDAO (PreferencesDAO preferencesDAO)
	{
		this.preferencesDAO = preferencesDAO;
	}

	public void setLruSize (int lruSize)
	{
		this.lruSize = lruSize;
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesManager#clearCache(java.lang.Integer)
	 */
	public void clearCache (Integer userId)
	{
		synchronized (preferencesByUser)
		{
			preferencesByUser.remove (userId);
		}
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesManager#createDefaultPrefrenceConfigsForUserId(java.lang.Integer)
	 */
	public void createDefaultPrefrenceConfigsForUserId (Integer userId)
	{
		KeelPreferencesManager.createDefaultValues (userId);
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesManager#get(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	public Object get (Integer userId, String category, String name)
	{
		Map<String, Object> prefs = null;

		synchronized (preferencesByUser)
		{
			prefs = preferencesByUser.get (userId);

			if (prefs == null)
			{
				prefs = new LRUMap (lruSize);
				preferencesByUser.put (userId, prefs);
			}
		}

		synchronized (prefs)
		{
			String key = category + "." + name;
			Object value = prefs.get (key);

			if (value == null)
			{
				PreferencesConfig pc = preferencesDAO.findPreferencesConfig (userId, category, name);

				if (pc != null)
				{
					if ("B".equals (pc.getType ()))
					{
						value = NumberTools.toBoolInstance (pc.getValue ());
					}
					else if ("I".equals (pc.getType ()))
					{
						value = NumberTools.toIntInstance (pc.getValue ());
					}
					else if ("T".equals (pc.getType ()))
					{
						value = Time.valueOf (pc.getValue ());
					}
					else
					{
						value = StringTools.trim (pc.getValue ());
					}

					prefs.put (key, value);
				}
			}

			return value;
		}
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesManager#getInt(java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public Integer getInt (Integer userId, String category, String name, Integer defaultValue)
	{
		Object value = get (userId, category, name);

		if (value != null && value instanceof Integer)
		{
			return (Integer) value;
		}

		return NumberTools.toIntInstance (value, defaultValue);
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesManager#getString(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getString (Integer userId, String category, String name, String defaultValue)
	{
		Object value = get (userId, category, name);

		if (value != null && value instanceof String)
		{
			return (String) value;
		}

		if (value != null)
		{
			return value.toString ();
		}

		return defaultValue;
	}

	/**
	 * @see de.iritgo.aktera.configuration.preferences.PreferencesManager#set(java.lang.Integer, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void set (Integer userId, String category, String name, Object value)
	{
		String key = category + "." + name;
		Map<String, Object> prefs = preferencesByUser.get (userId);

		if (prefs != null)
		{
			synchronized (prefs)
			{
				prefs.remove (key);
			}
		}

		preferencesDAO.updatePreferencesConfig (userId, category, name, value);
	}
}
