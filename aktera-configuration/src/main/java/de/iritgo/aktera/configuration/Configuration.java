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


import de.iritgo.aktera.core.container.KeelContainer;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 *
 */
public class Configuration implements org.apache.avalon.framework.configuration.Configuration
{
	/** Name of the configuration's root element */
	private String root;

	/** The Keel configuration */
	private org.apache.avalon.framework.configuration.Configuration keelConfig;

	/**
	 * Create a new Configuration.
	 */
	public Configuration ()
	{
	}

	/**
	 * Create a new Configuration from a Keel configuration.
	 *
	 * @param keelConfig The Keel configuration
	 */
	private Configuration (org.apache.avalon.framework.configuration.Configuration keelConfig)
	{
		this.keelConfig = keelConfig;
		this.root = keelConfig.getName ();
	}

	/**
	 * Get the name of the root configuration element.
	 *
	 * @return The root tag name
	 */
	public String getRoot ()
	{
		return root;
	}

	/**
	 * Set the name of the root configuration element.
	 *
	 * @param root The root tag name
	 */
	public void setRoot (String root)
	{
		this.root = root;
	}

	/**
	 * Get the Keel configuration.
	 *
	 * @return The keel configuration
	 */
	private org.apache.avalon.framework.configuration.Configuration getKeelConfig ()
	{
		if (keelConfig == null)
		{
			try
			{
				keelConfig = KeelContainer.defaultContainer ().getSystemConfig ().getChild (root);
			}
			catch (ConfigurationException x)
			{
				System.out.println ("[Configuration] Error: " + x);
			}
		}

		return keelConfig;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttribute(java.lang.String)
	 */
	public String getAttribute (String paramName) throws ConfigurationException
	{
		return getKeelConfig ().getAttribute (paramName);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttribute(java.lang.String, java.lang.String)
	 */
	public String getAttribute (String name, String defaultValue)
	{
		return getKeelConfig ().getAttribute (name, defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsBoolean(java.lang.String)
	 */
	public boolean getAttributeAsBoolean (String paramName) throws ConfigurationException
	{
		return getKeelConfig ().getAttributeAsBoolean (paramName);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsBoolean(java.lang.String, boolean)
	 */
	public boolean getAttributeAsBoolean (String name, boolean defaultValue)
	{
		return getKeelConfig ().getAttributeAsBoolean (name, defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsFloat(java.lang.String)
	 */
	public float getAttributeAsFloat (String paramName) throws ConfigurationException
	{
		return getKeelConfig ().getAttributeAsFloat (paramName);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsFloat(java.lang.String, float)
	 */
	public float getAttributeAsFloat (String name, float defaultValue)
	{
		return getKeelConfig ().getAttributeAsFloat (name, defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsInteger(java.lang.String)
	 */
	public int getAttributeAsInteger (String paramName) throws ConfigurationException
	{
		return getKeelConfig ().getAttributeAsInteger (paramName);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsInteger(java.lang.String, int)
	 */
	public int getAttributeAsInteger (String name, int defaultValue)
	{
		return getKeelConfig ().getAttributeAsInteger (name, defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsLong(java.lang.String)
	 */
	public long getAttributeAsLong (String name) throws ConfigurationException
	{
		return getKeelConfig ().getAttributeAsLong (name);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeAsLong(java.lang.String, long)
	 */
	public long getAttributeAsLong (String name, long defaultValue)
	{
		return getKeelConfig ().getAttributeAsLong (name, defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getAttributeNames()
	 */
	public String[] getAttributeNames ()
	{
		return getKeelConfig ().getAttributeNames ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getChild(java.lang.String)
	 */
	public Configuration getChild (String child)
	{
		return new Configuration (getKeelConfig ().getChild (child));
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getChild(java.lang.String, boolean)
	 */
	public Configuration getChild (String child, boolean createNew)
	{
		return new Configuration (getKeelConfig ().getChild (child, createNew));
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getChildren()
	 */
	public Configuration[] getChildren ()
	{
		org.apache.avalon.framework.configuration.Configuration[] keelConfigs = getKeelConfig ().getChildren ();
		Configuration[] configs = new Configuration[keelConfigs.length];

		for (int i = 0; i < configs.length; ++i)
		{
			configs[i] = new Configuration (keelConfigs[i]);
		}

		return configs;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getChildren(java.lang.String)
	 */
	public Configuration[] getChildren (String name)
	{
		org.apache.avalon.framework.configuration.Configuration[] keelConfigs = getKeelConfig ().getChildren (name);
		Configuration[] configs = new Configuration[keelConfigs.length];

		for (int i = 0; i < configs.length; ++i)
		{
			configs[i] = new Configuration (keelConfigs[i]);
		}

		return configs;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getLocation()
	 */
	public String getLocation ()
	{
		return getKeelConfig ().getLocation ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getName()
	 */
	public String getName ()
	{
		return getKeelConfig ().getName ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getNamespace()
	 */
	public String getNamespace () throws ConfigurationException
	{
		return getKeelConfig ().getNamespace ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValue()
	 */
	public String getValue () throws ConfigurationException
	{
		return getKeelConfig ().getValue ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValue(java.lang.String)
	 */
	public String getValue (String defaultValue)
	{
		return getKeelConfig ().getValue (defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsBoolean()
	 */
	public boolean getValueAsBoolean () throws ConfigurationException
	{
		return getKeelConfig ().getValueAsBoolean ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsBoolean(boolean)
	 */
	public boolean getValueAsBoolean (boolean defaultValue)
	{
		return getKeelConfig ().getValueAsBoolean (defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsFloat()
	 */
	public float getValueAsFloat () throws ConfigurationException
	{
		return getKeelConfig ().getValueAsFloat ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsFloat(float)
	 */
	public float getValueAsFloat (float defaultValue)
	{
		return getKeelConfig ().getValueAsFloat (defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsInteger()
	 */
	public int getValueAsInteger () throws ConfigurationException
	{
		return getKeelConfig ().getValueAsInteger ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsInteger(int)
	 */
	public int getValueAsInteger (int defaultValue)
	{
		return getKeelConfig ().getValueAsInteger (defaultValue);
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsLong()
	 */
	public long getValueAsLong () throws ConfigurationException
	{
		return getKeelConfig ().getValueAsLong ();
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configuration#getValueAsLong(long)
	 */
	public long getValueAsLong (long defaultValue)
	{
		return getKeelConfig ().getValueAsLong (defaultValue);
	}

	public double getAttributeAsDouble (String arg0) throws ConfigurationException
	{
		return 0;
	}

	public double getAttributeAsDouble (String arg0, double arg1)
	{
		return 0;
	}

	public double getValueAsDouble () throws ConfigurationException
	{
		return 0;
	}

	public double getValueAsDouble (double arg0)
	{
		return 0;
	}
}
