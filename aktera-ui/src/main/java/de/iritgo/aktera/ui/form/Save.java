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
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
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


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.save"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.save" id="aktera.save" logger="aktera"
 */
public class Save extends SecurableStandardLogEnabledModel implements InstanceSecurable
{
	/** Parameter to set if the formular storage should be ommited. */
	public static final String NO_FORM_STORE = "AKTERA_NO_FORM_STORE";

	/** Parameter to set if the save model should be part of a formless edit. */
	public static final String SYSTEM_EDIT = "AKTERA_SYSTEM_EDIT";

	/** True if the configuration was already read. */
	protected boolean configRead;

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

	/**
	 * Return an identifying string.
	 *
	 * @return The instance id.
	 */
	public String getInstanceIdentifier ()
	{
		return getConfiguration ().getAttribute ("id", "aktera.save");
	}

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		SaveFormContext context = new SaveFormContext ();

		context.setRequest (req);

		ModelResponse res = req.createResponse ();

		try
		{
			readConfig (req);
		}
		catch (ConfigurationException x)
		{
			throw new ModelException (x);
		}

		Object id = req.getParameter (keyName);

		if (StringTools.isTrimEmpty (id))
		{
			id = new Integer (- 1);
		}

		String persistentsId = FormTools.createContextKey (contextId, id);

		FormularDescriptor formular = (FormularDescriptor) UserTools.getContextObject (req, persistentsId);

		if (formular == null)
		{
			res.setAttribute ("forward", "aktera.formular.save-without-edit");

			return res;
		}

		try
		{
			boolean modified = false;

			handler.adjustFormular (req, formular, formular.getPersistents ());

			if (req.getParameter (NO_FORM_STORE) == null
							&& ! NumberTools.toBool (req.getParameter (SYSTEM_EDIT), false))
			{
				try
				{
					modified = FormTools.storeInput (req, res, formular, formular.getPersistents (), log);
				}
				catch (Exception x)
				{
					System.out.println ("[Save] Error while storing input: " + x);
					x.printStackTrace ();
				}
			}

			if (req.getParameter ("AKTERA_page") != null)
			{
				int page = NumberTools.toInt (req.getParameter ("AKTERA_page"), formular.getPage ());

				if (page >= 0)
				{
					formular.setPage (page);
				}
			}

			if (NumberTools.toBool (req.getParameter ("AKTERA_auto"), false))
			{
				CommandInfo cmdInfo = (CommandInfo) (cmdPage != null && cmdPage.getModel () != null ? cmdPage.clone ()
								: cmdEdit.clone ());
				Command cmd = cmdInfo.createCommand (req, res, context);

				for (Iterator i = req.getParameters ().keySet ().iterator (); i.hasNext ();)
				{
					String key = (String) i.next ();

					if (! "model".equals (key) && ! "SEQUENCE_NAME".equals (key) && ! "SEQUENCE_NUMBER".equals (key))
					{
						cmd.setParameter (key, req.getParameters ().get (key));
					}
				}

				cmd.setParameter (keyName, id);
				cmd.setParameter ("reedit", "Y");

				if (req.getParameter ("ajax") != null)
				{
					cmd.setParameter ("ajax", "Y");
				}

				return cmd.execute (req, res);
			}

			handler.preStorePersistents (req, formular, formular.getPersistents (), new Boolean (modified));

			if (req.getParameter (NO_FORM_STORE) == null && validate
							&& ! NumberTools.toBool (req.getParameter (SYSTEM_EDIT), false))
			{
				ValidationResult result = null;

				try
				{
					result = FormTools.validateInput (req, res, formular, formular.getPersistents ());
					handler.validatePersistents (persistentConfig, req, res, formular, formular.getPersistents (),
									NumberTools.toInt (id, - 1) == - 1, result);
				}
				catch (Exception x)
				{
					System.out.println ("[Save] Error while validating input: " + x);
					x.printStackTrace ();
				}

				if (result.hasErrors ())
				{
					formular.setPage (Math.max (formular.getPageWithField (result.getFirstErrorField (formular)
									.replaceAll ("_", ".")), 0));
				}

				result.createResponseElements (res, formular);

				if (result.hasErrors ())
				{
					CommandInfo cmdInfo = (CommandInfo) cmdEdit.clone ();
					Command cmd = cmdInfo.createCommand (req, res, context);

					cmd.setParameter (keyName, id);
					cmd.setParameter ("error", result.getFirstErrorField (formular));

					if (! NumberTools.toBool (req.getParameter (SYSTEM_EDIT), false))
					{
						return cmd.execute (req, res);
					}
					else
					{
						return res;
					}
				}
			}

			if (NumberTools.toInt (id, - 1) != - 1)
			{
				handler.updatePersistents (req, formular, formular.getPersistents (), persistentConfig, modified);

				if (! preserveContext)
				{
					UserTools.removeContextObject (req, persistentsId);
				}
			}
			else
			{
				id = new Integer (handler.createPersistents (req, formular, formular.getPersistents (),
								persistentConfig));

				if (NumberTools.toInt (id, - 1) != - 1)
				{
					if (! preserveContext)
					{
						UserTools.removeContextObject (req, FormTools.createContextKey (contextId, - 1));
					}
					else
					{
						UserTools.setContextObject (req, FormTools.createContextKey (contextId, id), formular);
					}
				}
			}

			if (! NumberTools.toBool (req.getParameter (SYSTEM_EDIT), false))
			{
				context.setSaveId (id);

				CommandInfo cmdInfo = (CommandInfo) cmdOk.clone ();
				Command cmd = cmdInfo.createCommand (req, res, context);

				ModelRequest newReq = (ModelRequest) req.getService (ModelRequest.ROLE);
				ModelResponse cmdRes = cmd.execute (newReq, res);

				if (res.get ("IRITGO_formMessages") != null)
				{
					cmdRes.add (res.get ("IRITGO_formMessages"));
				}

				return cmdRes;
			}
			else
			{
				return res;
			}
		}
		catch (ModelException x)
		{
			throw ModelTools.handleException (res, x, log);
		}
		catch (PersistenceException x)
		{
			throw ModelTools.handleException (res, x, log);
		}
	}

	/**
	 * Retrieve the model configuration.
	 *
	 * @param req The model configuration.
	 */
	public void readConfig (ModelRequest req) throws ModelException, ConfigurationException
	{
		if (configRead)
		{
			return;
		}

		java.util.List configPath = ModelTools.getDerivationPath (req, this);

		validate = ModelTools.getConfigBool (configPath, "validate", true);

		cmdOk = readCommandConfig (configPath, "command-ok", "ok", null, "ok");
		cmdEdit = readCommandConfig (configPath, "command-edit", "edit", null, "edit");
		cmdPage = readCommandConfig (configPath, "command-page", "page", null, "page");

		keyName = ModelTools.getConfigString (configPath, "keyName", "id");

		persistentConfig = ModelTools.getConfigChildren (configPath, "persistent");

		contextId = ModelTools.getConfigString (configPath, "context", "id", null);

		preserveContext = ModelTools.getConfigBool (configPath, "preserveContext", false);

		String handlerClassName = ModelTools.getConfigString (configPath, "handler", "class", null);

		if (handlerClassName != null)
		{
			try
			{
				handler = (FormularHandler) Class.forName (handlerClassName).newInstance ();
			}
			catch (ClassNotFoundException x)
			{
				throw new ModelException ("[aktera.save] Unable to create handler " + handlerClassName + " (" + x + ")");
			}
			catch (InstantiationException x)
			{
				throw new ModelException ("[aktera.save] Unable to create handler " + handlerClassName + " (" + x + ")");
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException ("[aktera.save] Unable to create handler " + handlerClassName + " (" + x + ")");
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
	 * Retrieve the configuration of a list command.
	 *
	 * @param configPath The configuration path.
	 * @param configName The name of the command config.
	 * @param name The command name.
	 * @param defaultLabel The default command label.
	 * @return The command info.
	 */
	public CommandInfo readCommandConfig (java.util.List configPath, String configName, String name,
					String defaultModel, String defaultLabel) throws ModelException, ConfigurationException
	{
		Configuration config = ModelTools.getConfig (configPath, configName);

		String model = config != null ? config.getAttribute ("model", defaultModel) : defaultModel;
		String label = config != null ? config.getAttribute ("label", defaultLabel) : defaultLabel;
		String bundle = config != null ? config.getAttribute ("bundle", "Aktera") : "Aktera";

		if (config != null)
		{
			CommandInfo cmd = new CommandInfo (model, name, label);

			cmd.setBundle (bundle);

			if (config != null)
			{
				Configuration[] params = config.getChildren ("parameter");

				for (int i = 0; i < params.length; ++i)
				{
					cmd.addParameter (params[i].getAttribute ("name"), params[i].getAttribute ("value"));
				}

				params = config.getChildren ("param");

				for (int i = 0; i < params.length; ++i)
				{
					cmd.addParameter (params[i].getAttribute ("name"), params[i].getAttribute ("value"));
				}
			}

			cmd.setIcon (config.getAttribute ("icon", null));

			return cmd;
		}

		return null;
	}
}
