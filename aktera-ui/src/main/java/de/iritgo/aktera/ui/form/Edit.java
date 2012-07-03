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
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.edit"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.edit" id="aktera.edit" logger="aktera"
 */
public class Edit extends SecurableStandardLogEnabledModel implements InstanceSecurable
{
	/** Parameter to set if the edit model should be part of a formless edit. */
	public static final String SYSTEM_EDIT = "AKTERA_SYSTEM_EDIT";

	/** Key under which the formular will be returned in the model response (system edit only). */
	public static final String FORM_KEY = "AKTERA_FORMULAR";

	/** True if the configuration was already read. */
	protected Boolean configRead;

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

	/** Formular descriptor model name. */
	protected String formularModelName;

	/** The name of the id attribute. */
	protected String keyName;

	/**
	 * Return an identifying string.
	 *
	 * @return The instance id.
	 */
	public String getInstanceIdentifier()
	{
		return getConfiguration().getAttribute("id", "aktera.edit");
	}

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		EditFormContext context = new EditFormContext();

		context.setRequest(req);

		ModelResponse res = req.createResponse();

		try
		{
			readConfig(req);

			Object id = handler.getPersistentId(req, contextId, keyName);

			if (StringTools.isTrimEmpty(id))
			{
				id = new Integer(- 1);
			}

			context.setId(id);

			String persistentsId = FormTools.createContextKey(contextId, id);

			FormularDescriptor formular = createFormular(req);

			if (req.getParameter("page") != null)
			{
				formular.setPage(NumberTools.toInt(req.getParameter("page"), formular.getPage()));
			}

			if (NumberTools.toBool(req.getParameter("AKTERA_auto"), false) && req.getParameter("AKTERA_page") != null)
			{
				formular.setPage(NumberTools.toInt(req.getParameter("AKTERA_page"), formular.getPage()));
			}

			handler.prepareFormular(req, res, formular);

			PersistentDescriptor persistents = formular.getPersistents();

			if (persistents == null
							|| (req.getParameter("error") == null && req.getParameter("reedit") == null && ! reeditAlways))
			{
				persistents = new PersistentDescriptor(persistentsId);
				formular.setPersistents(persistents);
				handler.loadPersistents(req, formular, persistents, persistentConfig, NumberTools
								.toIntInstance(id, - 1));
				handler.afterLoad(req, formular, persistents);
				FormTools.loadPersistents(formular, persistents);
			}
			else
			{
				handler.afterLoad(req, formular, persistents);
			}

			handler.adjustFormular(req, formular, persistents);

			if (NumberTools.toBool(req.getParameter(SYSTEM_EDIT), false))
			{
				Output out = res.createOutput(FORM_KEY);

				out.setContent(formular);
				res.add(out);
			}
			else
			{
				if (req.getParameter("ajax") != null)
				{
					res.setAttribute("forward", "aktera.formular.ajax");
				}
				else
				{
					res.setAttribute("forward", forward);
				}

				FormTools.createResponseElements(req, res, formular, persistents, formular.getCommands(), readOnly
								|| formular.getReadOnly(), context);
			}

			res.addOutput("formAction", "model");

			return res;
		}
		catch (ModelException x)
		{
			throw ModelTools.handleException(res, x, log);
		}
		catch (PersistenceException x)
		{
			throw ModelTools.handleException(res, x, log);
		}
		catch (ConfigurationException x)
		{
			throw ModelTools.handleException(res, x, log);
		}
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

		synchronized (configRead)
		{
			if (configRead)
			{
				return;
			}
			configRead = true;

			java.util.List configPath = ModelTools.getDerivationPath(req, this);

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

			formularModelName = ModelTools.getConfigString(configPath, "formular", "id", null);

			persistentConfig = ModelTools.getConfigChildren(configPath, "persistent");

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
				handler.setLogger(log);
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

		String model = config != null ? config.getAttribute("model", defaultModel) : defaultModel;
		String label = config != null ? config.getAttribute("label", defaultLabel) : defaultLabel;
		String bundle = config != null ? config.getAttribute("bundle", "Aktera") : "Aktera";
		String pos = config != null ? config.getAttribute("pos", defaultPos) : defaultPos;

		if (config != null)
		{
			CommandInfo cmd = new CommandInfo(model, name, label);

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
	 * @param req The model request.
	 * @throws ModelException In case of an error.
	 * @throws ConfigurationException If a configuration value cannot be found.
	 * @throws PersistenceException If a persistent cannot be found.
	 */
	public FormularDescriptor createFormular(ModelRequest req)
		throws ModelException, ConfigurationException, PersistenceException
	{
		Object id = handler.getPersistentId(req, contextId, keyName);

		if (StringTools.isTrimEmpty(id))
		{
			id = new Integer(- 1);
		}

		String persistentsId = FormTools.createContextKey(contextId, id);

		FormularDescriptor formular = FormTools.getFormularFromContext(req, contextId, keyName);

		if (formular != null
						&& (formular.getId().equals(formularModelName) || formular.getId().equals(formularClassName)))
		{
			return formular;
		}

		FormularDescriptor oldFormular = formular;

		if (formularModelName != null)
		{
			try
			{
				Model formularModel = (Model) req.getService(Model.ROLE, formularModelName);

				Output formularOutput = (Output) formularModel.execute(req).get("formular");

				if (formularOutput != null)
				{
					formular = (FormularDescriptor) formularOutput.getContent();
				}
			}
			catch (Exception x)
			{
				throw new ModelException("[aktera.edit] Unable to create formular from model " + formularModelName
								+ " (" + x + ")");
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

		if (formularModelName != null)
		{
			formular.setId(formularModelName);
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

		UserTools.setContextObject(req, persistentsId, formular);

		return formular;
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The edit model to call.
	 * @return The formular descriptor.
	 */
	public static FormularDescriptor start(ModelRequest req, String model, Object id) throws ModelException
	{
		Properties props = new Properties();

		props.put(SYSTEM_EDIT, Boolean.TRUE);

		if (id != null)
		{
			props.put("id", id);
		}

		ModelResponse res = ModelTools.callModel(req, model, props);

		return (FormularDescriptor) ModelTools.getOutputContent(res, FORM_KEY);
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The edit model to call.
	 * @return The formular descriptor.
	 */
	public static FormularDescriptor start(ModelRequest req, String model) throws ModelException
	{
		return start(req, model, null);
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The edit model to call.
	 * @param id The id of the persistent to reedit.
	 * @return The formular descriptor.
	 */
	public static FormularDescriptor restart(ModelRequest req, String model, Object id) throws ModelException
	{
		Properties props = new Properties();

		props.put(SYSTEM_EDIT, Boolean.TRUE);
		props.put("reedit", Boolean.TRUE);

		if (id != null)
		{
			props.put("id", id);
		}

		ModelResponse res = ModelTools.callModel(req, model, props);

		return (FormularDescriptor) ModelTools.getOutputContent(res, FORM_KEY);
	}

	/**
	 * Call this method to init a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The edit model to call.
	 * @return The formular descriptor.
	 */
	public static FormularDescriptor restart(ModelRequest req, String model) throws ModelException
	{
		return restart(req, model, null);
	}

	/**
	 * Call this method to finish a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The save model to call.
	 * @param id The id of the persistent to save.
	 * @return The formular descriptor.
	 */
	public static ModelResponse finish(ModelRequest req, String model, Object id) throws ModelException
	{
		Properties props = new Properties();

		props.put(SYSTEM_EDIT, Boolean.TRUE);

		if (id != null)
		{
			props.put("id", id);
		}

		return ModelTools.callModel(req, model, props);
	}

	/**
	 * Call this method to finish a system internal edit process.
	 *
	 * @param req A model request.
	 * @param model The save model to call.
	 * @return The formular descriptor.
	 */
	public static ModelResponse finish(ModelRequest req, String model) throws ModelException
	{
		return finish(req, model, null);
	}

	/**
	 * Call this method to perform a system internal delete process.
	 *
	 * @param req A model request.
	 * @param model The delete model to call.
	 * @param id The id of the persistent to delete.
	 * @return The formular descriptor.
	 */
	public static ModelResponse delete(ModelRequest req, String model, Object id) throws ModelException
	{
		Properties props = new Properties();

		props.put(Delete.SYSTEM_DELETE, Boolean.TRUE);
		props.put("id", id);

		return ModelTools.callModel(req, model, props);
	}
}
