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

package de.iritgo.aktera.persist;


import de.iritgo.simplelife.math.NumberTools;


/**
 *
 */
public class ModuleVersion implements Cloneable
{
	/** Major version number. */
	protected int major;

	/** Minor version number. */
	protected int minor;

	/** Patch level. */
	protected int level;

	/**
	 * Create a new module version.
	 */
	public ModuleVersion ()
	{
		major = 0;
		minor = 0;
		level = 0;
	}

	/**
	 * Create a new module version from a version string.
	 *
	 * @param version The version string.
	 */
	public ModuleVersion (String version)
	{
		setVersion (version, true);
	}

	/**
	 * Clone a version.
	 *
	 * @return The clone.
	 */
	public Object clone () throws CloneNotSupportedException
	{
		return super.clone ();
	}

	/**
	 * Set the version.
	 *
	 * @param version The new version.
	 */
	public void setVersion (String version, boolean internal)
	{
		setVersion (version);
	}

	/**
	 * Set the version.
	 *
	 * @param version The new version.
	 */
	public void setVersion (String version)
	{
		String[] parts = version.split ("\\.");

		if (parts.length >= 1)
		{
			major = NumberTools.toInt (parts[0], 0);
		}

		if (parts.length >= 2)
		{
			minor = NumberTools.toInt (parts[1], 0);
		}

		if (parts.length >= 3)
		{
			level = NumberTools.toInt (parts[2], 0);
		}
	}

	/**
	 * Get the major version number.
	 *
	 * @return The major version number.
	 */
	public int getMajor ()
	{
		return major;
	}

	/**
	 * Get the minor version number.
	 *
	 * @return The minor version number.
	 */
	public int getMinor ()
	{
		return minor;
	}

	/**
	 * Get the patch level.
	 *
	 * @return The patch level.
	 */
	public int getLevel ()
	{
		return level;
	}

	/**
	 * Create a string representation.
	 *
	 * @return A version string.
	 */
	public String toString ()
	{
		return major + "." + minor + "." + level;
	}

	/**
	 * Check wether this version lies between version1 and version2,
	 * i.e. version1 <= thisVersion < version2.
	 *
	 * @param version1 Lower version to check.
	 * @param version2 Higher version to check.
	 * @return True if this version lies between version1 and version2.
	 */
	public boolean between (ModuleVersion version1, ModuleVersion version2)
	{
		return greaterOrEqual (version1) && lessThan (version2);
	}

	/**
	 * Check wether this version lies between version1 and version2,
	 * i.e. version1 <= thisVersion < version2.
	 *
	 * @param version1 Lower version to check.
	 * @param version2 Higher version to check.
	 * @return True if this version lies between version1 and version2.
	 */
	public boolean between (String version1, String version2)
	{
		return between (new ModuleVersion (version1), new ModuleVersion (version2));
	}

	/**
	 * Check wether this version is greater or equal to another version.
	 *
	 * @param version Lower version to check.
	 * @return True if this version is greater or equal to another version.
	 */
	public boolean greaterOrEqual (ModuleVersion version)
	{
		return this.major > version.major || (this.major == version.major && this.minor > version.minor)
						|| (this.major == version.major && this.minor == version.minor && this.level >= version.level);
	}

	/**
	 * Check wether this version is greater or equal to another version.
	 *
	 * @param version Lower version to check.
	 * @return True if this version is greater or equal to another version.
	 */
	public boolean greaterOrEqual (String version)
	{
		return greaterOrEqual (new ModuleVersion (version));
	}

	/**
	 * Check wether this version is greater or equal to another version.
	 *
	 * @param version Lower version to check.
	 * @return True if this version is greater or equal to another version.
	 */
	public boolean greater (ModuleVersion version)
	{
		return this.major > version.major || (this.major == version.major && this.minor > version.minor)
						|| (this.major == version.major && this.minor == version.minor && this.level > version.level);
	}

	/**
	 * Check wether this version is greater or equal to another version.
	 *
	 * @param version Lower version to check.
	 * @return True if this version is greater or equal to another version.
	 */
	public boolean greater (String version)
	{
		return greater (new ModuleVersion (version));
	}

	/**
	 * Check wether this version is less than another version.
	 *
	 * @param version Higher version to check.
	 * @return True if this version is less than another version.
	 */
	public boolean lessThan (ModuleVersion version)
	{
		return this.major < version.major || (this.major == version.major && this.minor < version.minor)
						|| (this.major == version.major && this.minor == version.minor && this.level < version.level);
	}

	/**
	 * Check wether this version is less than another version.
	 *
	 * @param version Higher version to check.
	 * @return True if this version is less than another version.
	 */
	public boolean lessThan (String version)
	{
		return lessThan (new ModuleVersion (version));
	}
}
