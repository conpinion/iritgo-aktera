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


import de.iritgo.aktera.authorization.InstanceSecurable;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.List;


/**
 * Generic delete model.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.delete"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.delete" id="aktera.delete" logger="aktera"
 * @model.parameter name="id" required="true"
 */
public class Delete extends SecurableStandardLogEnabledModel implements InstanceSecurable
{
	/** Parameter to set if the edit model should be part of a formless edit. */
	public static final String SYSTEM_DELETE = "systemDelete";

	/** True if the configuration was already read. */
	protected boolean configRead;

	/** Formular handler. */
	protected FormularHandler handler;

	/** The name of the id attribute. */
	protected String keyName = "id";

	/** Persistent configuration. */
	protected List<Configuration> persistentConfig;

	/**
	 * Return an identifying string.
	 *
	 * @return The instance id.
	 */
	public String getInstanceIdentifier()
	{
		return getConfiguration().getAttribute("id", "aktera.delete");
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

		try
		{
			readConfig(req);

			String[] ids;

			if (req.getParameter("_lpdeleteKeyName") != null)
			{
				keyName = StringTools.trim(req.getParameter("_lpdeleteKeyName"));
			}

			if (req.getParameter(keyName) == null)
			{
				ids = new String[0];
			}
			else if (req.getParameter(keyName) instanceof String)
			{
				ids = new String[]
				{
					(String) req.getParameter(keyName)
				};
			}
			else if (req.getParameter(keyName) instanceof String[])
			{
				ids = (String[]) req.getParameter(keyName);
			}
			else
			{
				ids = new String[]
				{
					req.getParameter(keyName).toString()
				};
			}

			PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE, req
							.getDomain());

			for (int i = 0; i < ids.length; ++i)
			{
				Persistent persistent = null;

				if (persistentConfig.size() != 0)
				{
					persistent = persistentManager.create(persistentConfig.get(0).getAttribute("name"));

					persistent.setField(persistentConfig.get(0).getAttribute("key"), NumberTools.toIntInstance(ids[i],
									- 1));

					persistent.find();
				}

				ValidationResult result = new ValidationResult();

				if (handler.canDeletePersistent(req, ids[i], persistent, NumberTools.toBool(req
								.getParameter(SYSTEM_DELETE), false), result))
				{
					handler.deletePersistent(req, res, ids[i], persistent, NumberTools.toBool(req
									.getParameter(SYSTEM_DELETE), false));
				}
				else
				{
					result.createResponseElements(res, null);
				}
			}
		}
		catch (ConfigurationException x)
		{
			log.error(x.toString());
			throw new ModelException(x);
		}
		catch (PersistenceException x)
		{
			log.error(x.toString());
			throw new ModelException(x);
		}

		return res;
	}

	/**
	 * Retrieve the model configuration.
	 *
	 * @param req The model configuration.
	 */
	public void readConfig(ModelRequest req) throws ModelException, ConfigurationException
	{
		if (configRead)
		{
			return;
		}

		Configuration config = getConfiguration();
		java.util.List configPath = ModelTools.getDerivationPath(req, this);

		keyName = ModelTools.getConfigString(configPath, "keyName", "id");

		persistentConfig = ModelTools.getConfigChildren(configPath, "persistent");

		String handlerClassName = ModelTools.getConfigString(configPath, "handler", "class", null);

		if (handlerClassName != null)
		{
			try
			{
				handler = (FormularHandler) Class.forName(handlerClassName).newInstance();
			}
			catch (ClassNotFoundException x)
			{
				throw new ModelException("[aktera.delete] Unable to create handler " + handlerClassName + " (" + x
								+ ")");
			}
			catch (InstantiationException x)
			{
				throw new ModelException("[aktera.delete] Unable to create handler " + handlerClassName + " (" + x
								+ ")");
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException("[aktera.delete] Unable to create handler " + handlerClassName + " (" + x
								+ ")");
			}
		}
		else
		{
			String handlerBeanName = ModelTools.getConfigString(configPath, "handler", "bean", null);

			if (handlerBeanName != null)
			{
				handler = (FormularHandler) SpringTools.getBean(handlerBeanName);
			}
		}

		if (handler != null)
		{
			handler.setDefaultHandler(new DefaultFormularHandler());
		}
		else
		{
			handler = new DefaultFormularHandler();
		}

		configRead = true;
	}
}
