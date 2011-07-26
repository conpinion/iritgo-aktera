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


import de.iritgo.aktera.tools.FileTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import java.io.File;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;


/**
 * Default ScriptManager implementation.
 */
public class SystemConfigManagerImpl implements SystemConfigManager
{
	/** An empty file used to mark a reboot. */
	private static File rebootMarker = FileTools.newAkteraFile ("/var/tmp/iritgo/reboot");

	/** All loaded configuration values */
	private Map<String, Object> configValues = new HashMap ();

	/** System config DAO */
	private SystemConfigDAO systemConfigDAO;

	/** XML config values */
	private Configuration xmlConfiguration;

	/**
	 * Set the system config DAO.
	 */
	public void setSystemConfigDAO (SystemConfigDAO systemConfigDAO)
	{
		this.systemConfigDAO = systemConfigDAO;
	}

	/**
	 * Set the XML config values.
	 */
	public void setXmlConfiguration (Configuration xmlConfiguration)
	{
		this.xmlConfiguration = xmlConfiguration;
	}

	/**
	 * Get a configuration value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @param cache True if the retrieved value should be cached.
	 * @return The configuration value.
	 */
	private Object get (String category, String name, boolean cache)
	{
		Object value = null;

		if (cache)
		{
			value = configValues.get (category + ":" + name);
		}

		if (value == null)
		{
			SystemConfig config = null;

			try
			{
				config = systemConfigDAO.findByCategoryAndName (category, name);
			}
			catch (InvalidDataAccessResourceUsageException ignored)
			{
			}

			if (config != null && (config.getValue () != null))
			{
				if ("B".equals (config.getType ()))
				{
					value = new Boolean (config.getValue ());
				}
				else if ("I".equals (config.getType ()))
				{
					value = new Integer (config.getValue ());
				}
				else if ("T".equals (config.getType ()))
				{
					value = Time.valueOf (config.getValue ());
				}
				else
				{
					value = config.getValue ();
				}
			}

			if (value == null)
			{
				Configuration[] configs = xmlConfiguration.getChildren ("config");

				for (int i = 0; i < configs.length; ++i)
				{
					if (category.equals (configs[i].getAttribute ("category", null))
									&& name.equals (configs[i].getAttribute ("name", null)))
					{
						String type = configs[i].getAttribute ("type", "S");
						String sValue = configs[i].getAttribute ("value", "");

						if ("B".equals (type))
						{
							value = new Boolean (sValue);
						}
						else if ("I".equals (type))
						{
							value = new Integer (sValue);
						}
						else if ("T".equals (type))
						{
							value = Time.valueOf (sValue);
						}
						else
						{
							value = sValue;
						}
					}
				}
			}
		}

		if (cache && value != null)
		{
			configValues.put (category + ":" + name, value);
		}

		return value;
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#get(java.lang.String, java.lang.String)
	 */

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#get(java.lang.String, java.lang.String)
	 */
	public Object get (String category, String name)
	{
		return get (category, name, true);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getNoCache(java.lang.String, java.lang.String)
	 */
	public Object getNoCache (String category, String name)
	{
		return get (category, name, false);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getBool(java.lang.String, java.lang.String)
	 */
	public boolean getBool (String category, String name)
	{
		return getBoolean (category, name).booleanValue ();
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getBoolean(java.lang.String, java.lang.String)
	 */
	public Boolean getBoolean (String category, String name)
	{
		Object value = get (category, name);

		return NumberTools.toBoolInstance (value, false);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getInt(java.lang.String, java.lang.String)
	 */
	public int getInt (String category, String name)
	{
		return getInteger (category, name).intValue ();
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getInt(java.lang.String, java.lang.String, int)
	 */
	public int getInt (String category, String name, int defaultValue)
	{
		try
		{
			return getInt (category, name);
		}
		catch (NumberFormatException x)
		{
			return defaultValue;
		}
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getInteger(java.lang.String, java.lang.String)
	 */
	public Integer getInteger (String category, String name)
	{
		Object value = get (category, name);

		return NumberTools.toIntInstance (value, 0);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getString(java.lang.String, java.lang.String)
	 */
	public String getString (String category, String name)
	{
		Object value = get (category, name);

		return StringTools.trim (value);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getTime(java.lang.String, java.lang.String)
	 */
	public Time getTime (String category, String name)
	{
		try
		{
			Object value = get (category, name);

			if (value instanceof Time)
			{
				return (Time) value;
			}

			return Time.valueOf (value.toString ());
		}
		catch (Exception x)
		{
			return Time.valueOf ("00:00:00");
		}
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#invalidate(java.lang.String, java.lang.String)
	 */
	public void invalidate (String category, String name)
	{
		configValues.remove (category + ":" + name);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#set(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void set (String category, String name, String type, Object value)
	{
		systemConfigDAO.createOrUpdate (new SystemConfig (category, name, type, StringTools.trim (value)));
		configValues.remove (category + ":" + name);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#getConfigState()
	 */
	public String getConfigState ()
	{
		return getString ("system", "configChanged");
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configChangedSoft()
	 */
	public void configChangedSoft ()
	{
		if (isRebootFlagSet ())
		{
			configChangedReboot ();

			return;
		}

		if (CHANGE_HARD.equals (getConfigState ()) || CHANGE_REBOOT.equals (getConfigState ()))
		{
			return;
		}

		set ("system", "configChanged", "S", SystemConfigManager.CHANGE_SOFT);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configChangedHard()
	 */
	public void configChangedHard ()
	{
		if (isRebootFlagSet ())
		{
			configChangedReboot ();

			return;
		}

		if (CHANGE_REBOOT.equals (getConfigState ()))
		{
			return;
		}

		set ("system", "configChanged", "S", SystemConfigManager.CHANGE_HARD);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configChangedReboot()
	 */
	public void configChangedReboot ()
	{
		clearRebootFlag ();
		set ("system", "configChanged", "S", SystemConfigManager.CHANGE_REBOOT);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configActivated()
	 */
	public void configActivated ()
	{
		set ("system", "configChanged", "S", SystemConfigManager.CHANGE_NO);
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#setRebootFlag()
	 */
	public void setRebootFlag ()
	{
		try
		{
			rebootMarker.createNewFile ();
		}
		catch (Exception x)
		{
		}
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#clearRebootFlag()
	 */
	public void clearRebootFlag ()
	{
		try
		{
			rebootMarker.delete ();
		}
		catch (Exception x)
		{
		}
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#isRebootFlagSet()
	 */
	public boolean isRebootFlagSet ()
	{
		return rebootMarker.exists ();
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configIsUnchanged()
	 */
	public boolean configIsUnchanged ()
	{
		return CHANGE_NO.equals (getConfigState ());
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configNeedsReload()
	 */
	public boolean configNeedsReload ()
	{
		return CHANGE_SOFT.equals (getConfigState ());
	}

	/**
	 * Check wether the config needs a restart.
	 *
	 * @return True if the config needs a restart.
	 */
	public boolean configNeedsRestart ()
	{
		return CHANGE_HARD.equals (getConfigState ());
	}

	/**
	 * @see de.iritgo.aktera.configuration.SystemConfigManager#configNeedsReboot()
	 */
	public boolean configNeedsReboot ()
	{
		return CHANGE_REBOOT.equals (getConfigState ());
	}
}
