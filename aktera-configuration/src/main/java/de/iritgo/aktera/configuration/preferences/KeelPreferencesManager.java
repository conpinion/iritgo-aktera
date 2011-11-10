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


import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.tools.*;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.preferences.manager"
 * @x-avalon.lifestyle type="singleton"
 * @model.model
 *   name="aktera.preferences.manager"
 *   id="aktera.preferences.manager"
 *   logger="aktera"
 *   activation="startup"
 */
public class KeelPreferencesManager extends StandardLogEnabledModel implements Initializable
{
	/** Theme info */
	public static class ThemeInfo
	{
		public String id;

		public String name;

		public ThemeInfo(String id, String name)
		{
			this.id = id;
			this.name = name;
		}

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}
	}

	/** Available themes. */
	protected static Map themes = new HashMap();

	/**
	 * Initialize the component.
	 */
	public void initialize() throws ConfigurationException
	{
		Configuration[] themeConfigs = getConfiguration().getChildren("theme");

		for (int i = 0; i < themeConfigs.length; ++i)
		{
			Configuration themeConfig = themeConfigs[i];

			themes.put(themeConfig.getAttribute("id"), new ThemeInfo(themeConfig.getAttribute("id"), themeConfig
							.getAttribute("name")));
		}
	}

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		return res;
	}

	public static void createDefaultValues(Integer userId)
	{
		try
		{
			ModelRequest modelReq = ModelTools.createModelRequest();
			createDefaultValues(modelReq, userId);
		}
		catch (Exception x)
		{
			//TODO: get a logger
			x.printStackTrace();
		}
	}

	/**
	 * Create default preferences values for a user.
	 *
	 * @param req A model request.
	 * @param userId The user id.
	 */
	public static void createDefaultValues(ModelRequest req, Integer userId) throws ModelException
	{
		try
		{
			Model self = (Model) req.getService(Model.ROLE, "aktera.preferences.manager");

			PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE, req
							.getDomain());

			Configuration[] configs = self.getConfiguration().getChildren("config");

			for (int i = 0; i < configs.length; ++i)
			{
				Configuration config = configs[i];

				Persistent configEntry = persistentManager.create("aktera.PreferencesConfig");

				configEntry.setField("userId", userId);
				configEntry.setField("category", config.getAttribute("category"));
				configEntry.setField("name", config.getAttribute("name"));

				if (! configEntry.find())
				{
					configEntry.setField("type", config.getAttribute("type"));
					configEntry.setField("value", config.getAttribute("value", null));
					configEntry.setField("validValues", config.getAttribute("validValues", null));
					configEntry.add();
				}
			}
		}
		catch (PersistenceException x)
		{
			throw new ModelException("[PreferencesManager] Unable to create default values", x);
		}
		catch (ConfigurationException x)
		{
			throw new ModelException("[PreferencesManager] Unable to create default values", x);
		}
	}

	/**
	 * Create default preferences values for a user.
	 *
	 * @param req A model request.
	 * @param userId The user id.
	 */
	public static void createDefaultValue(ModelRequest req, Integer userId, String category, String name)
		throws ModelException
	{
		try
		{
			Model self = (Model) req.getService(Model.ROLE, "aktera.preferences.manager");

			PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE, req
							.getDomain());

			Configuration[] configs = self.getConfiguration().getChildren("config");

			for (int i = 0; i < configs.length; ++i)
			{
				Configuration config = configs[i];

				if (! config.getAttribute("category").equals(category) && ! config.getAttribute("name").equals(name))
				{
					continue;
				}

				Persistent configEntry = persistentManager.create("aktera.PreferencesConfig");

				configEntry.setField("userId", userId);
				configEntry.setField("category", config.getAttribute("category"));
				configEntry.setField("name", config.getAttribute("name"));

				if (! configEntry.find())
				{
					configEntry.setField("type", config.getAttribute("type"));
					configEntry.setField("value", config.getAttribute("value", null));
					configEntry.setField("validValues", config.getAttribute("validValues", null));
					configEntry.add();
				}
			}
		}
		catch (PersistenceException x)
		{
			throw new ModelException("[PreferencesManager] Unable to create default value", x);
		}
		catch (ConfigurationException x)
		{
			throw new ModelException("[PreferencesManager] Unable to create default value", x);
		}
	}

	/**
	 * Get an iterator over all themes.
	 *
	 * @return A theme iterator.
	 */
	public static Iterator themeIterator()
	{
		return themes.values().iterator();
	}
}
