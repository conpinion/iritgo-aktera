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

package de.iritgo.aktera.ui.ng.formular;


import de.iritgo.aktera.authorization.Security;
import de.iritgo.aktera.hibernate.StandardDao;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.aktera.ui.form.DefaultFormularHandler;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.ValidationResult;
import de.iritgo.aktera.ui.ng.ModelRequestWrapper;
import de.iritgo.aktera.ui.ng.ModelResponseWrapper;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.LinkedList;
import java.util.List;


/**
 *
 */
public class Delete extends AbstractUIController
{
	/** Parameter to set if the edit model should be part of a formless edit. */
	public static final String SYSTEM_DELETE = "systemDelete";

	private Configuration configuration;

	/** True if the configuration was already read. */
	protected boolean configRead;

	/** Formular handler. */
	protected FormularHandler handler;

	/** The name of the id attribute. */
	protected String keyName = "id";

	/** Persistent configuration. */
	protected List<Configuration> persistentConfig;

	public Delete ()
	{
		security = Security.INSTANCE;
	}

	public void setConfiguration (Configuration configuration)
	{
		this.configuration = configuration;
	}

	public Configuration getConfiguration ()
	{
		return configuration;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIController#execute(de.iritgo.aktera.ui.UIRequest, de.iritgo.aktera.ui.UIResponse)
	 */
	public void execute (UIRequest request, UIResponse response) throws UIControllerException
	{
		try
		{
			ModelRequestWrapper wrappedRequest = new ModelRequestWrapper (request);
			ModelResponseWrapper wrappedResponse = new ModelResponseWrapper (response);

			readConfig ();

			String[] ids;

			if (request.getParameter ("_lpdeleteKeyName") != null)
			{
				keyName = StringTools.trim (request.getParameter ("_lpdeleteKeyName"));
			}

			if (request.getParameter (keyName) == null)
			{
				ids = new String[0];
			}
			else if (request.getParameter (keyName) instanceof String)
			{
				ids = new String[]
				{
					(String) request.getParameter (keyName)
				};
			}
			else if (request.getParameter (keyName) instanceof String[])
			{
				ids = (String[]) request.getParameter (keyName);
			}
			else
			{
				ids = new String[]
				{
					request.getParameter (keyName).toString ()
				};
			}

			PersistentFactory persistentManager = (PersistentFactory) wrappedRequest.getService (
							PersistentFactory.ROLE, wrappedRequest.getDomain ());

			for (int i = 0; i < ids.length; ++i)
			{
				Persistent persistent = null;
				Object bean = null;

				if (persistentConfig.size () != 0)
				{
					if (persistentConfig.get (0).getAttribute ("name", null) != null)
					{
						persistent = persistentManager.create (persistentConfig.get (0).getAttribute ("name"));
						persistent.setField (persistentConfig.get (0).getAttribute ("key"), NumberTools.toIntInstance (
										ids[i], - 1));
						persistent.find ();
					}
					else
					{
						StandardDao standardDao = (StandardDao) SpringTools.getBean (StandardDao.ID);

						bean = standardDao.get (persistentConfig.get (0).getAttribute ("entity"), NumberTools.toInt (
										ids[i], - 1));
					}
				}

				ValidationResult result = new ValidationResult ();

				if (persistent != null)
				{
					if (handler.canDeletePersistent (wrappedRequest, ids[i], persistent, NumberTools.toBool (request
									.getParameter (SYSTEM_DELETE), false), result))
					{
						handler.deletePersistent (wrappedRequest, wrappedResponse, ids[i], persistent, NumberTools
										.toBool (request.getParameter (SYSTEM_DELETE), false));
					}
					else
					{
						result.createResponseElements (wrappedResponse, null);
					}
				}
				else
				{
					if (handler.canDeletePersistent (wrappedRequest, ids[i], bean, NumberTools.toBool (request
									.getParameter (SYSTEM_DELETE), false), result))
					{
						handler.deletePersistent (wrappedRequest, wrappedResponse, ids[i], bean, NumberTools.toBool (
										request.getParameter (SYSTEM_DELETE), false));
					}
					else
					{
						result.createResponseElements (wrappedResponse, null);
					}
				}
			}
		}
		catch (ConfigurationException x)
		{
			logger.error (x.toString ());
			throw new UIControllerException (x);
		}
		catch (PersistenceException x)
		{
			logger.error (x.toString ());
			throw new UIControllerException (x);
		}
		catch (ModelException x)
		{
			logger.error (x.toString ());
			throw new UIControllerException (x);
		}
	}

	/**
	 * Return an identifying string.
	 *
	 * @return The instance id.
	 */
	public String getInstanceIdentifier ()
	{
		return getConfiguration ().getAttribute ("id", "aktera.delete");
	}

	/**
	 * Retrieve the model configuration.
	 */
	public void readConfig () throws ModelException, ConfigurationException
	{
		if (configRead)
		{
			return;
		}

		Configuration config = getConfiguration ();
		java.util.List configPath = getDerivationPath ();

		keyName = ModelTools.getConfigString (configPath, "keyName", "id");

		persistentConfig = ModelTools.getConfigChildren (configPath, "persistent");

		String handlerClassName = ModelTools.getConfigString (configPath, "handler", "class", null);

		if (handlerClassName != null)
		{
			try
			{
				handler = (FormularHandler) Class.forName (handlerClassName).newInstance ();
			}
			catch (ClassNotFoundException x)
			{
				throw new ModelException ("[aktera.delete] Unable to create handler " + handlerClassName + " (" + x
								+ ")");
			}
			catch (InstantiationException x)
			{
				throw new ModelException ("[aktera.delete] Unable to create handler " + handlerClassName + " (" + x
								+ ")");
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException ("[aktera.delete] Unable to create handler " + handlerClassName + " (" + x
								+ ")");
			}
		}
		else
		{
			String handlerBeanName = ModelTools.getConfigString (configPath, "handler", "bean", null);

			if (handlerBeanName != null)
			{
				handler = (FormularHandler) SpringTools.getBean (handlerBeanName);
			}
		}

		if (handler != null)
		{
			handler.setDefaultHandler (new DefaultFormularHandler ());
		}
		else
		{
			handler = new DefaultFormularHandler ();
		}

		configRead = true;
	}

	/**
	 * Describe method getDerivationPath() here.
	 *
	 * @return
	 * @throws ConfigurationException
	 */
	private List getDerivationPath () throws ConfigurationException
	{
		List path = new LinkedList ();
		Configuration config = configuration;

		while (config != null)
		{
			path.add (config);

			String extendsBeanName = config.getChild ("extends").getAttribute ("bean", null);

			if (extendsBeanName != null)
			{
				Delete extendsBean = (Delete) SpringTools.getBean (extendsBeanName);

				if (extendsBean == null)
				{
					throw new ConfigurationException ("Unable to find parent controller bean: " + extendsBeanName);
				}

				config = extendsBean.getConfiguration ();
			}
			else
			{
				config = null;
			}
		}

		return path;
	}
}
