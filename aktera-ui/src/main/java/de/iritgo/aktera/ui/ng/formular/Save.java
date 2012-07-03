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
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.struts.BeanRequest;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.aktera.ui.form.CommandInfo;
import de.iritgo.aktera.ui.form.DefaultFormularHandler;
import de.iritgo.aktera.ui.form.FormTools;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.SaveFormContext;
import de.iritgo.aktera.ui.form.ValidationResult;
import de.iritgo.aktera.ui.ng.ModelRequestWrapper;
import de.iritgo.aktera.ui.ng.ModelResponseWrapper;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Save extends AbstractUIController
{
	/** Parameter to set if the formular storage should be ommited. */
	public static final String NO_FORM_STORE = "AKTERA_NO_FORM_STORE";

	/** Parameter to set if the save model should be part of a formless edit. */
	public static final String SYSTEM_EDIT = "AKTERA_SYSTEM_EDIT";

	private Configuration configuration;

	/** True if the configuration was already read. */
	protected Boolean configRead = false;

	/** The edit command. */
	CommandInfo cmdEdit;

	/** The page switch command. */
	CommandInfo cmdPage;

	/** The ok command. */
	CommandInfo cmdOk;

	/** Formular handler. */
	protected FormularHandler handler;

	/** Persistent configuration. */
	protected List persistentConfig;

	/** Name of the formular values in the session context. */
	protected String contextId;

	/** If true no validation is performed. */
	protected boolean validate;

	/** The name of the id attribute. */
	protected String keyName;

	/** If true, the formular context object is not deleted after the save. */
	protected boolean preserveContext;

	public Save()
	{
		security = Security.INSTANCE;
	}

	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void execute(UIRequest request, UIResponse response) throws UIControllerException
	{
		try
		{
			ModelRequestWrapper wrappedRequest = new ModelRequestWrapper(request);
			ModelResponseWrapper wrappedResponse = new ModelResponseWrapper(response);

			readConfig();

			Object id = request.getParameter(keyName);

			if (StringTools.isTrimEmpty(id))
			{
				id = new Integer(- 1);
			}

			String persistentsId = FormTools.createContextKey(contextId, id);

			FormularDescriptor formular = (FormularDescriptor) UserTools
							.getContextObject(wrappedRequest, persistentsId);

			if (formular == null)
			{
				response.setForward("aktera.formular.save-without-edit");

				return;
			}

			SaveFormContext context = new SaveFormContext();

			context.setRequest(wrappedRequest);
			context.setPersistents(formular.getPersistents());

			boolean modified = false;

			handler.adjustFormular(wrappedRequest, formular, formular.getPersistents());

			if (request.getParameter(NO_FORM_STORE) == null
							&& ! NumberTools.toBool(request.getParameter(SYSTEM_EDIT), false))
			{
				try
				{
					modified = FormTools.storeInput(wrappedRequest, wrappedResponse, formular, formular
									.getPersistents(), logger);
				}
				catch (Exception x)
				{
					System.out.println("[Save] Error while storing input: " + x);
					x.printStackTrace();
				}
			}

			if (request.getParameter("AKTERA_page") != null)
			{
				int page = NumberTools.toInt(request.getParameter("AKTERA_page"), formular.getPage());

				if (page >= 0)
				{
					formular.setPage(page);
				}
			}

			if (NumberTools.toBool(request.getParameter("AKTERA_auto"), false))
			{
				String bean = cmdPage != null && cmdPage.getModel() != null ? cmdPage.getModel() : cmdEdit.getModel();
				BeanRequest newRequest = new BeanRequest();
				newRequest.setBean(bean);
				newRequest.setLocale(request.getLocale());
				newRequest.setUserEnvironment(request.getUserEnvironment());
				for (Iterator i = request.getParameters().keySet().iterator(); i.hasNext();)
				{
					String key = (String) i.next();

					if (! "model".equals(key) && ! "SEQUENCE_NAME".equals(key) && ! "SEQUENCE_NUMBER".equals(key))
					{
						newRequest.setParameter(key, request.getParameters().get(key));
					}
				}
				newRequest.setParameter(keyName, id);
				newRequest.setParameter("reedit", "Y");
				if (request.getParameter("ajax") != null)
				{
					newRequest.setParameter("ajax", "Y");
				}
				redirect(bean, newRequest, response);
				return;
			}

			handler.preStorePersistents(wrappedRequest, formular, formular.getPersistents(), new Boolean(modified));

			if (request.getParameter(NO_FORM_STORE) == null && validate
							&& ! NumberTools.toBool(request.getParameter(SYSTEM_EDIT), false))
			{
				ValidationResult result = null;

				try
				{
					result = FormTools.validateInput(wrappedRequest, wrappedResponse, formular, formular
									.getPersistents());
					handler.validatePersistents(persistentConfig, wrappedRequest, wrappedResponse, formular, formular
									.getPersistents(), NumberTools.toInt(id, - 1) == - 1, result);
				}
				catch (Exception x)
				{
					System.out.println("[Save] Error while validating input: " + x);
					x.printStackTrace();
				}

				if (result.hasErrors())
				{
					formular.setPage(Math.max(formular.getPageWithField(result.getFirstErrorField(formular).replaceAll(
									"_", ".")), 0));
				}

				result.createResponseElements(wrappedResponse, formular);

				if (result.hasErrors())
				{
					if (! NumberTools.toBool(request.getParameter(SYSTEM_EDIT), false))
					{
						BeanRequest newRequest = new BeanRequest();
						newRequest.setBean(cmdEdit.getModel());
						newRequest.setLocale(request.getLocale());
						newRequest.setUserEnvironment(request.getUserEnvironment());
						newRequest.setParameter(keyName, id);
						newRequest.setParameter("error", result.getFirstErrorField(formular));
						redirect(cmdEdit.getModel(), newRequest, response);
						return;
					}
					else
					{
						return;
					}
				}
			}

			if (NumberTools.toInt(id, - 1) != - 1)
			{
				try
				{
					handler.updatePersistents(wrappedRequest, formular, formular.getPersistents(), persistentConfig,
									modified);
				}
				catch (Exception x)
				{
					logger.error("Unable to update persistents: " + x);
					throw new ModelException(x);
				}

				if (! preserveContext)
				{
					UserTools.removeContextObject(wrappedRequest, persistentsId);
				}
			}
			else
			{
				id = new Integer(handler.createPersistents(wrappedRequest, formular, formular.getPersistents(),
								persistentConfig));

				if (NumberTools.toInt(id, - 1) != - 1)
				{
					if (! preserveContext)
					{
						UserTools.removeContextObject(wrappedRequest, FormTools.createContextKey(contextId, - 1));
					}
					else
					{
						UserTools.setContextObject(wrappedRequest, FormTools.createContextKey(contextId, id), formular);
					}
				}
			}

			if (! NumberTools.toBool(request.getParameter(SYSTEM_EDIT), false))
			{
				context.setSaveId(id);
				BeanRequest newRequest = new BeanRequest();
				cmdOk.setParameters(newRequest, context);
				newRequest.setBean(cmdOk.getModel());
				newRequest.setLocale(request.getLocale());
				newRequest.setUserEnvironment(request.getUserEnvironment());
				redirect(cmdOk.getModel(), newRequest, response);
				return;
			}
			else
			{
				return;
			}
		}
		catch (ModelException x)
		{
			throw new UIControllerException(x);
		}
		catch (PersistenceException x)
		{
			throw new UIControllerException(x);
		}
		catch (ConfigurationException x)
		{
			throw new UIControllerException(x);
		}
	}

	/**
	 * Return an identifying string.
	 *
	 * @return The instance id.
	 */
	public String getInstanceIdentifier()
	{
		return getConfiguration().getAttribute("id", "aktera.save");
	}

	/**
	 * Retrieve the model configuration.
	 */
	public void readConfig() throws ModelException, ConfigurationException
	{
		if (configRead)
		{
			return;
		}

		synchronized (configRead)
		{
			if (configRead)
			{
				return;
			}
			configRead = true;

			java.util.List configPath = getDerivationPath();

			validate = ModelTools.getConfigBool(configPath, "validate", true);

			cmdOk = readCommandConfig(configPath, "command-ok", "ok", null, "ok");
			cmdEdit = readCommandConfig(configPath, "command-edit", "edit", null, "edit");
			cmdPage = readCommandConfig(configPath, "command-page", "page", null, "page");

			keyName = ModelTools.getConfigString(configPath, "keyName", "id");

			persistentConfig = ModelTools.getConfigChildren(configPath, "persistent");

			contextId = ModelTools.getConfigString(configPath, "context", "id", null);

			preserveContext = ModelTools.getConfigBool(configPath, "preserveContext", false);

			String handlerClassName = ModelTools.getConfigString(configPath, "handler", "class", null);

			if (handlerClassName != null)
			{
				try
				{
					handler = (FormularHandler) Class.forName(handlerClassName).newInstance();
				}
				catch (ClassNotFoundException x)
				{
					throw new ModelException("[aktera.save] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
				catch (InstantiationException x)
				{
					throw new ModelException("[aktera.save] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
				catch (IllegalAccessException x)
				{
					throw new ModelException("[aktera.save] Unable to create handler " + handlerClassName + " (" + x + ")");
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
		}
	}

	/**
	 * Retrieve the configuration of a list command.
	 *
	 * @param configPath The configuration path.
	 * @param configName The name of the command config.
	 * @param name The command name.
	 * @param defaultLabel The default command label.
	 * @return The command info.
	 */
	public CommandInfo readCommandConfig(java.util.List configPath, String configName, String name,
					String defaultModel, String defaultLabel) throws ModelException, ConfigurationException
	{
		Configuration config = ModelTools.getConfig(configPath, configName);

		String model = config != null ? config.getAttribute("model", config.getAttribute("bean", defaultModel))
						: defaultModel;
		String label = config != null ? config.getAttribute("label", defaultLabel) : defaultLabel;
		String bundle = config != null ? config.getAttribute("bundle", "Aktera") : "Aktera";

		if (config != null)
		{
			CommandInfo cmd = new CommandInfo(model, name, label);

			if (config.getAttribute("bean", null) != null)
			{
				cmd.setBean(true);
			}

			cmd.setBundle(bundle);

			if (config != null)
			{
				Configuration[] params = config.getChildren("parameter");

				for (int i = 0; i < params.length; ++i)
				{
					cmd.addParameter(params[i].getAttribute("name"), params[i].getAttribute("value"));
				}

				params = config.getChildren("param");

				for (int i = 0; i < params.length; ++i)
				{
					cmd.addParameter(params[i].getAttribute("name"), params[i].getAttribute("value"));
				}
			}

			cmd.setIcon(config.getAttribute("icon", null));

			return cmd;
		}

		return null;
	}

	/**
	 * Describe method getDerivationPath() here.
	 *
	 * @return
	 * @throws ConfigurationException
	 */
	private List getDerivationPath() throws ConfigurationException
	{
		List path = new LinkedList();
		Configuration config = configuration;

		while (config != null)
		{
			path.add(config);

			String extendsBeanName = config.getChild("extends").getAttribute("bean", null);

			if (extendsBeanName != null)
			{
				Save extendsBean = (Save) SpringTools.getBean(extendsBeanName);

				if (extendsBean == null)
				{
					throw new ConfigurationException("Unable to find parent controller bean: " + extendsBeanName);
				}

				config = extendsBean.getConfiguration();
			}
			else
			{
				config = null;
			}
		}

		return path;
	}
}
