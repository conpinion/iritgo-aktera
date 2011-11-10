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


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


public class NullConfiguration implements Configuration
{
	public String getAttribute(String name) throws ConfigurationException
	{
		throw new ConfigurationException("No attribute named \"" + name
						+ "\" is associated with the null configuration");
	}

	public String getAttribute(String name, String defaultValue)
	{
		return defaultValue;
	}

	public boolean getAttributeAsBoolean(String name) throws ConfigurationException
	{
		throw new ConfigurationException("No attribute named \"" + name
						+ "\" is associated with the null configuration");
	}

	public boolean getAttributeAsBoolean(String name, boolean defaultValue)
	{
		return defaultValue;
	}

	public double getAttributeAsDouble(String name) throws ConfigurationException
	{
		throw new ConfigurationException("No attribute named \"" + name
						+ "\" is associated with the null configuration");
	}

	public double getAttributeAsDouble(String name, double defaultValue)
	{
		return defaultValue;
	}

	public float getAttributeAsFloat(String name) throws ConfigurationException
	{
		throw new ConfigurationException("No attribute named \"" + name
						+ "\" is associated with the null configuration");
	}

	public float getAttributeAsFloat(String name, float defaultValue)
	{
		return defaultValue;
	}

	public int getAttributeAsInteger(String name) throws ConfigurationException
	{
		throw new ConfigurationException("No attribute named \"" + name
						+ "\" is associated with the null configuration");
	}

	public int getAttributeAsInteger(String name, int defaultValue)
	{
		return defaultValue;
	}

	public long getAttributeAsLong(String name) throws ConfigurationException
	{
		throw new ConfigurationException("No attribute named \"" + name
						+ "\" is associated with the null configuration");
	}

	public long getAttributeAsLong(String name, long defaultValue)
	{
		return defaultValue;
	}

	public String[] getAttributeNames()
	{
		return new String[0];
	}

	public Configuration getChild(String name)
	{
		return this;
	}

	public Configuration getChild(String name, boolean createNew)
	{
		return createNew ? this : null;
	}

	public Configuration[] getChildren()
	{
		return new Configuration[0];
	}

	public Configuration[] getChildren(String name)
	{
		return new Configuration[0];
	}

	public String getLocation()
	{
		return null;
	}

	public String getName()
	{
		return "<null>";
	}

	public String getNamespace() throws ConfigurationException
	{
		return "";
	}

	public String getValue() throws ConfigurationException
	{
		throw new ConfigurationException("No value is associated with the null configuration");
	}

	public String getValue(String defaultValue)
	{
		return defaultValue;
	}

	public boolean getValueAsBoolean() throws ConfigurationException
	{
		throw new ConfigurationException("No value is associated with the null configuration");
	}

	public boolean getValueAsBoolean(boolean defaultValue)
	{
		return defaultValue;
	}

	public double getValueAsDouble() throws ConfigurationException
	{
		throw new ConfigurationException("No value is associated with the null configuration");
	}

	public double getValueAsDouble(double defaultValue)
	{
		return defaultValue;
	}

	public float getValueAsFloat() throws ConfigurationException
	{
		throw new ConfigurationException("No value is associated with the null configuration");
	}

	public float getValueAsFloat(float defaultValue)
	{
		return defaultValue;
	}

	public int getValueAsInteger() throws ConfigurationException
	{
		throw new ConfigurationException("No value is associated with the null configuration");
	}

	public int getValueAsInteger(int defaultValue)
	{
		return defaultValue;
	}

	public long getValueAsLong() throws ConfigurationException
	{
		throw new ConfigurationException("No value is associated with the null configuration");
	}

	public long getValueAsLong(long defaultValue)
	{
		return defaultValue;
	}
}
