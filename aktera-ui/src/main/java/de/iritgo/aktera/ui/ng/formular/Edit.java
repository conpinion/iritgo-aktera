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


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.*;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.struts.*;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.aktera.ui.form.CommandDescriptor;
import de.iritgo.aktera.ui.form.CommandInfo;
import de.iritgo.aktera.ui.form.DefaultFormularHandler;
import de.iritgo.aktera.ui.form.Delete;
import de.iritgo.aktera.ui.form.EditFormContext;
import de.iritgo.aktera.ui.form.FormTools;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.ng.ModelRequestWrapper;
import de.iritgo.aktera.ui.ng.ModelResponseWrapper;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import java.util.*;


public class Edit extends AbstractUIController
{
	/** Parameter to set if the edit model should be part of a formless edit. */
	public static final String SYSTEM_EDIT = "AKTERA_SYSTEM_EDIT";

	/** Key under which the formular will be returned in the model response (system edit only). */
	public static final String FORM_KEY = "AKTERA_FORMULAR";

	private Configuration configuration;

	/** True if the configuration was already read. */
	protected Boolean configRead = false;

	/** If true a read only formular is generated. */
	protected boolean readOnly;

	/** The save command. */
	CommandInfo cmdSave;

	/** The edit command. */
	CommandInfo cmdEdit;

	/** The cancel command. */
	CommandInfo cmdCancel;

	/** Persistent configuration. */
	protected List<Configuration> persistentConfig;

	/** Attributes configuration. */
	protected List<Configuration> attributeConfig;

	/** Name of the formular values in the session context. */
	protected String contextId;

	/** For struts clients: The forward attribute. */
	protected String forward;

	/** True if the formular values should always be persisted. */
	protected boolean reeditAlways;

	/** Command configuration. */
	protected List commandConfig;

	/** Formular title. */
	protected String title;

	/** Formular title bundle. */
	protected String titleBundle;

	/** Formular icon. */
	protected String icon;

	/** Formular handler. */
	protected FormularHandler handler;

	/** Formular descriptor class name. */
	protected String formularClassName;

	/** Formular descriptor bean name. */
	protected String formularBeanName;

	/** The name of the id attribute. */
	protected String keyName;

	public Edit()
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

			EditFormContext context = new EditFormContext();

			context.setRequest(wrappedRequest);

			readConfig();

			Object id = handler.getPersistentId(wrappedRequest, contextId, keyName);

			if (StringTools.isTrimEmpty(id) || request.hasParameter("new"))
			{
				id = new Integer(- 1);
			}

			context.setId(id);

			String persistentsId = FormTools.createContextKey(contextId, id);

			FormularDescriptor formular = createFormular(wrappedRequest);

			if (request.getParameter("page") != null)
			{
				formular.setPage(NumberTools.toInt(request.getParameter("page"), formular.getPage()));
			}

			if (NumberTools.toBool(request.getParameter("AKTERA_auto"), false)
							&& request.getParameter("AKTERA_page") != null)
			{
				formular.setPage(NumberTools.toInt(request.getParameter("AKTERA_page"), formular.getPage()));
			}

			handler.prepareFormular(wrappedRequest, wrappedResponse, formular);

			PersistentDescriptor persistents = formular.getPersistents();
			context.setPersistents(persistents);

			if (persistents == null
							|| (request.getParameter("error") == null && request.getParameter("reedit") == null && ! reeditAlways))
			{
				persistents = new PersistentDescriptor(persistentsId);
				context.setPersistents(persistents);

				for (Configuration configuration : attributeConfig)
				{
					String name = configuration.getAttribute("name");
					Object value = null;
					try
					{
						value = context.evalExpressionLanguageValue(configuration.getAttribute("value"));
						if (value != null)
						{
							persistents.putAttribute(name, value);
						}
					}
					catch (IllegalArgumentException x)
					{
						logger.error("Unable to retrieve value for attribute " + name);
					}
				}

				formular.setPersistents(persistents);

				handler.loadPersistents(wrappedRequest, formular, persistents, persistentConfig, NumberTools
								.toIntInstance(id, - 1));
				handler.afterLoad(wrappedRequest, formular, persistents);
				FormTools.loadPersistents(formular, persistents);
			}
			else
			{
				handler.afterLoad(wrappedRequest, formular, persistents);
			}

			handler.adjustFormular(wrappedRequest, formular, persistents);

			if (NumberTools.toBool(request.getParameter(SYSTEM_EDIT), false))
			{
				Output out = response.createOutput(FORM_KEY);

				out.setContent(formular);
				response.add(out);
			}
			else
			{
				if (request.getParameter("ajax") != null)
				{
					response.setForward("aktera.formular.ajax");
				}
				else
				{
					response.setForward(forward);
				}

				FormTools.createResponseElements(wrappedRequest, wrappedResponse, formular, persistents, formular
								.getCommands(), readOnly || formular.getReadOnly(), context);
			}

			response.addOutput("formAction", "bean");
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
		return configuration.getAttribute("id", "aktera.edit");
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

			readOnly = ModelTools.getConfigBool(configPath, "readOnly", false);
			reeditAlways = ModelTools.getConfigBool(configPath, "reeditAlways", false);

			if (! readOnly)
			{
				cmdSave = readCommandConfig(configPath, "command-save", "save", null, "save", "S");

				if (cmdSave != null)
				{
					cmdSave.setVisible(ModelTools.getConfigBool(configPath, "command-save", "visible", true));

					if (cmdSave.getIcon() == null)
					{
						cmdSave.setIcon("tool-ok-16");
					}
				}
			}
			else
			{
				cmdEdit = readCommandConfig(configPath, "command-edit", "save", null, "edit", "S");

				if (cmdEdit != null)
				{
					if (cmdEdit.getIcon() == null)
					{
						cmdEdit.setIcon("tool-edit-16");
					}
				}
			}

			cmdCancel = readCommandConfig(configPath, "command-cancel", "cancel", null, "cancel", "E");

			if (cmdCancel != null)
			{
				cmdCancel.addAttribute("cancel", "Y");
				cmdCancel.addParameter("cancel", "Y");

				if (cmdCancel.getIcon() == null)
				{
					cmdCancel.setIcon("tool-cancel-16");
				}
			}

			formularClassName = ModelTools.getConfigString(configPath, "formular", "class", null);

			formularBeanName = ModelTools.getConfigString(configPath, "formular", "bean", null);

			persistentConfig = ModelTools.getConfigChildren(configPath, "persistent");

			attributeConfig = ModelTools.getConfigChildren(configPath, "attribute");

			contextId = ModelTools.getConfigString(configPath, "context", "id", null);

			keyName = ModelTools.getConfigString(configPath, "keyName", null);

			commandConfig = ModelTools.getConfigChildrenReverse(configPath, "command");

			Configuration forwardConfig = ModelTools.findConfig(configPath, "attribute", "name", "forward");

			forward = forwardConfig != null ? forwardConfig.getAttribute("value", "aktera.formular") : "aktera.formular";

			title = ModelTools.getConfigString(configPath, "title", null);
			titleBundle = ModelTools.getConfigString(configPath, "title", "bundle", null);

			if (titleBundle == null)
			{
				titleBundle = ModelTools.getConfigString(configPath, "titleBundle", null);
			}

			icon = ModelTools.getConfigString(configPath, "icon", null);

			String handlerClassName = ModelTools.getConfigString(configPath, "handler", "class", null);

			if (handlerClassName != null)
			{
				try
				{
					handler = (FormularHandler) Class.forName(handlerClassName).newInstance();
				}
				catch (ClassNotFoundException x)
				{
					throw new ModelException("[aktera.edit] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
				catch (InstantiationException x)
				{
					throw new ModelException("[aktera.edit] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
				catch (IllegalAccessException x)
				{
					throw new ModelException("[aktera.edit] Unable to create handler " + handlerClassName + " (" + x + ")");
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

			if (handler != null)
			{
				handler.setLogger(logger);
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
					String defaultModel, String defaultLabel, String defaultPos)
		throws ModelException, ConfigurationException
	{
		Configuration config = ModelTools.getConfig(configPath, configName);

		String model = config != null ? config.getAttribute("model", config.getAttribute("bean", defaultModel))
						: defaultModel;
		String label = config != null ? config.getAttribute("label", defaultLabel) : defaultLabel;
		String bundle = config != null ? config.getAttribute("bundle", "Aktera") : "Aktera";
		String pos = config != null ? config.getAttribute("pos", defaultPos) : defaultPos;

		if (config != null)
		{
			CommandInfo cmd = new CommandInfo(model, name, label);

			if (config.getAttribute("bean", null) != null)
			{
				cmd.setBean(true);
			}

			cmd.setBundle(bundle);
			cmd.setPosition(pos);

			if (config != null)
			{
				Configuration[] params = config.getChildren("param");

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
	 * Create the formular descriptor. This method stores the descriptor in the user's
	 * context. On a later request, the descriptor is taken directly from the user
	 * context.
	 *
	 * @param request The model request.
	 * @throws ModelException In case of an error.
	 * @throws ConfigurationException If a configuration value cannot be found.
	 * @throws PersistenceException If a persistent cannot be found.
	 */
	public FormularDescriptor createFormular(ModelRequest request)
		throws ModelException, ConfigurationException, PersistenceException
	{
		Object id = handler.getPersistentId(request, contextId, keyName);

		if (StringTools.isTrimEmpty(id))
		{
			id = new Integer(- 1);
		}

		String persistentsId = FormTools.createContextKey(contextId, id);

		FormularDescriptor formular = FormTools.getFormularFromContext(request, contextId, keyName);

		if (formular != null
						&& (formular.getId().equals(formularBeanName) || formular.getId().equals(formularClassName)))
		{
			return formular;
		}

		FormularDescriptor oldFormular = formular;

		if (formularBeanName != null)
		{
			try
			{
				formular = ((Formular) SpringTools.getBean(formularBeanName)).createFormularDescriptor();
			}
			catch (Exception x)
			{
				throw new ModelException("[aktera.edit] Unable to create formular from bean " + formularBeanName + " ("
								+ x + ")");
			}
		}
		else if (formularClassName != null)
		{
			try
			{
				formular = (FormularDescriptor) Class.forName(formularClassName).newInstance();
			}
			catch (ClassNotFoundException x)
			{
				throw new ModelException("[aktera.edit] Unable to create formular from class " + formularClassName
								+ " (" + x + ")");
			}
			catch (InstantiationException x)
			{
				throw new ModelException("[aktera.edit] Unable to create formular from class " + formularClassName
								+ " (" + x + ")");
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException("[aktera.edit] Unable to create formular from class " + formularClassName
								+ " (" + x + ")");
			}
		}

		if (formular == null)
		{
			throw new ModelException("[aktera.edit] No formular defined");
		}

		if (formularBeanName != null)
		{
			formular.setId(formularBeanName);
		}
		else if (formularClassName != null)
		{
			formular.setId(formularClassName);
		}

		formular.setTitle(title);
		formular.setTitleBundle(titleBundle);
		formular.setIcon(icon);

		if (oldFormular != null)
		{
			formular.setPersistents(oldFormular.getPersistents());
		}

		CommandDescriptor commands = formular.getCommands();

		if (! readOnly && cmdSave != null && cmdSave.getModel() != null)
		{
			commands.add((CommandInfo) cmdSave.clone());
		}
		else
		{
			if (cmdEdit != null && cmdEdit.getModel() != null)
			{
				CommandInfo cmd = (CommandInfo) cmdEdit.clone();

				cmd.addParameter("id", id);
				commands.add(cmd);
			}
		}

		if (commandConfig.size() > 0)
		{
			for (Iterator i = commandConfig.iterator(); i.hasNext();)
			{
				Configuration aCommandConfig = (Configuration) i.next();

				CommandInfo cmdInfo = new CommandInfo(aCommandConfig.getAttribute("model"), aCommandConfig
								.getAttribute("id"), aCommandConfig.getAttribute("label"), aCommandConfig.getAttribute(
								"icon", null));

				cmdInfo.setBundle(aCommandConfig.getAttribute("bundle", "Aktera"));

				commands.add(cmdInfo);
			}
		}

		if (cmdCancel != null && cmdCancel.getModel() != null)
		{
			commands.add((CommandInfo) cmdCancel.clone());
		}

		commands.sortCommands();

		UserTools.setContextObject(request, persistentsId, formular);

		return formular;
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param controller The edit controller to call.
	 * @return The formular descriptor.
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 * @throws ModelException
	 * @throws ContextException
	 */
	public static FormularDescriptor start(ModelRequest req, String controller, Object id)
		throws AuthorizationException, UIControllerException, ContextException, ModelException
	{
		Map<String, Object> params = new HashMap();
		params.put(SYSTEM_EDIT, Boolean.TRUE);
		if (id != null)
		{
			params.put("id", id);
		}
		BeanRequest uiRequest = new BeanRequest();
		uiRequest.setLocale(req.getLocale());
		uiRequest.setBean(controller);
		uiRequest.setParameters(params);
		uiRequest.setUserEnvironment((UserEnvironment) req.getContext().get(UserEnvironment.CONTEXT_KEY));
		BeanResponse uiResponse = new BeanResponse();
		BeanAction.execute(uiRequest, uiResponse);
		return (FormularDescriptor) ((Output) uiResponse.get(FORM_KEY)).getContent();
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param controller The edit controller to call.
	 * @return The formular descriptor.
	 * @throws ContextException
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 */
	public static FormularDescriptor start(ModelRequest req, String controller)
		throws ModelException, AuthorizationException, UIControllerException, ContextException
	{
		return start(req, controller, null);
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param controller The edit controller to call.
	 * @param id The id of the persistent to reedit.
	 * @return The formular descriptor.
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 * @throws ContextException
	 */
	public static FormularDescriptor restart(ModelRequest req, String controller, Object id)
		throws ModelException, AuthorizationException, UIControllerException, ContextException
	{
		Map<String, Object> params = new HashMap();
		params.put(SYSTEM_EDIT, Boolean.TRUE);
		params.put("reedit", Boolean.TRUE);
		if (id != null)
		{
			params.put("id", id);
		}
		BeanRequest uiRequest = new BeanRequest();
		uiRequest.setLocale(req.getLocale());
		uiRequest.setBean(controller);
		uiRequest.setParameters(params);
		uiRequest.setUserEnvironment((UserEnvironment) req.getContext().get(UserEnvironment.CONTEXT_KEY));
		BeanResponse uiResponse = new BeanResponse();
		BeanAction.execute(uiRequest, uiResponse);
		return (FormularDescriptor) uiResponse.get(FORM_KEY);
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The edit model to call.
	 * @return The formular descriptor.
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 * @throws ContextException
	 */
	public static FormularDescriptor restart(ModelRequest req, String model)
		throws ModelException, AuthorizationException, UIControllerException, ContextException
	{
		return restart(req, model, null);
	}

	/**
	 * Call this method to finish a system internal edit process.
	 *
	 * @param req A model request.
	 * @param controller The save model to call.
	 * @param id The id of the persistent to save.
	 * @throws ContextException
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 */
	public static void finish(ModelRequest req, String controller, Object id)
		throws ModelException, ContextException, AuthorizationException, UIControllerException
	{
		Map<String, Object> params = new HashMap();
		params.put(SYSTEM_EDIT, Boolean.TRUE);
		if (id != null)
		{
			params.put("id", id);
		}
		BeanRequest uiRequest = new BeanRequest();
		uiRequest.setLocale(req.getLocale());
		uiRequest.setBean(controller);
		uiRequest.setParameters(params);
		uiRequest.setUserEnvironment((UserEnvironment) req.getContext().get(UserEnvironment.CONTEXT_KEY));
		BeanResponse uiResponse = new BeanResponse();
		BeanAction.execute(uiRequest, uiResponse);
	}

	/**
	 * Call this method to finish a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The save model to call.
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 * @throws ContextException
	 */
	public static void finish(ModelRequest req, String model)
		throws ModelException, ContextException, AuthorizationException, UIControllerException
	{
		finish(req, model, null);
	}

	/**
	 * Call this method to perform a system internal delete process.
	 *
	 * @param req A model request.
	 * @param controller The delete model to call.
	 * @param id The id of the persistent to delete.
	 * @throws UIControllerException
	 * @throws AuthorizationException
	 * @throws ContextException
	 */
	public static void delete(ModelRequest req, String controller, Object id)
		throws ModelException, AuthorizationException, UIControllerException, ContextException
	{
		Map<String, Object> params = new HashMap();
		params.put(Delete.SYSTEM_DELETE, Boolean.TRUE);
		params.put("id", id);
		BeanRequest uiRequest = new BeanRequest();
		uiRequest.setLocale(req.getLocale());
		uiRequest.setBean(controller);
		uiRequest.setParameters(params);
		uiRequest.setUserEnvironment((UserEnvironment) req.getContext().get(UserEnvironment.CONTEXT_KEY));
		BeanResponse uiResponse = new BeanResponse();
		BeanAction.execute(uiRequest, uiResponse);
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
				Edit extendsBean = (Edit) SpringTools.getBean(extendsBeanName);

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
