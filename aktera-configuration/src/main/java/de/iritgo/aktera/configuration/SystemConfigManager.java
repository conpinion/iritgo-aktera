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


import java.sql.Time;


/**
 *
 */
public interface SystemConfigManager
{
	/** Service id */
	public static final String ID = "de.iritgo.aktera.configuration.SystemConfigManager";

	/** No configuration change. */
	public static final String CHANGE_NO = "no";

	/** Configuration change needs soft reload. */
	public static final String CHANGE_SOFT = "soft";

	/** Configuration change needs hard reload. */
	public static final String CHANGE_HARD = "hard";

	/** A reboot is needed. */
	public static final String CHANGE_REBOOT = "reboot";

	/**
	 * Set a configuration value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @param value The configuration value.
	 */
	public void set (String category, String name, String type, Object value);

	/**
	 * Remove a configuration value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 */
	public void invalidate (String category, String name);

	/**
	 * Get a configuration value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public Object getNoCache (String category, String name);

	/**
	 * Get a configuration value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public Object get (String category, String name);

	/**
	 * Get a configuration string value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public String getString (String category, String name);

	/**
	 * Get a configuration boolean value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public Boolean getBoolean (String category, String name);

	/**
	 * Get a configuration boolean value.
	 *
	 * @param persistent The name of the persistent that contains the config values.
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public boolean getBool (String category, String name);

	/**
	 * Get a configuration integer value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public Integer getInteger (String category, String name);

	/**
	 * Get a configuration integer value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public int getInt (String category, String name);

	/**
	 * Get a configuration integer value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @param defaultValue The default value.
	 * @return The configuration value.
	 */
	public int getInt (String category, String name, int defaultValue);

	/**
	 * Get a configuration time value.
	 *
	 * @param category The category of the configuration value.
	 * @param name The name of the configuration value.
	 * @return The configuration value.
	 */
	public Time getTime (String category, String name);

	/**
	 * Check the configuration change status.
	 *
	 * @return The configuration change status.
	 */
	public String getConfigState ();

	/**
	 * Mark the config as beeing changed.
	 */
	public void configChangedSoft ();

	/**
	 * Mark the config as beeing changed.
	 */
	public void configChangedHard ();

	/**
	 * Mark the config to need a reboot.
	 */
	public void configChangedReboot ();

	/**
	 * Mark the config as beeing activated.
	 */
	public void configActivated ();

	/**
	 * Set the reboot flag.
	 */
	public void setRebootFlag ();

	/**
	 * Unset the reboot flag.
	 */
	public void clearRebootFlag ();

	/**
	 * Check the reboot flag.
	 *
	 * @return True if the reboot flag is set.
	 */
	public boolean isRebootFlagSet ();

	/**
	 * Check wether the config is up to date.
	 *
	 * @return True if the config is up to date.
	 */
	public boolean configIsUnchanged ();

	/**
	 * Check wether the config needs a reload.
	 *
	 * @return True if the config needs a reload.
	 */
	public boolean configNeedsReload ();

	/**
	 * Check wether the config needs a restart.
	 *
	 * @return True if the config needs a restart.
	 */
	public boolean configNeedsRestart ();

	/**
	 * Check wether the config needs a reboot.
	 *
	 * @return True if the config needs a reboot.
	 */
	public boolean configNeedsReboot ();
}
