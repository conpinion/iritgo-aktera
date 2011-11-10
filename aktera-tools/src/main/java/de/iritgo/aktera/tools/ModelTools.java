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

package de.iritgo.aktera.tools;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.simplelife.collection.PairwiseIterator;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.math.Pair;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Model utility methods.
 */
public class ModelTools
{
	/**
	 * Create a new command based on the 'previous request'. All parameters to
	 * the previous model are copied to the new command.
	 *
	 * @param req
	 *            The current model request.
	 * @param res
	 *            The current model response.
	 * @return The new command.
	 */
	public static Command createPreviousModelCommand(ModelRequest req, ModelResponse res, String model)
		throws ModelException
	{
		return createPreviousModelCommand(req, res, model, null);
	}

	/**
	 * Create a new command based on the 'previous request'. All parameters to
	 * the previous model are copied to the new command.
	 *
	 * @param req
	 *            The current model request.
	 * @param res
	 *            The current model response.
	 * @return The new command.
	 */
	public static Command createPreviousModelCommand(ModelRequest req, ModelResponse res, String model,
					String[] ommitPrevParameters) throws ModelException
	{
		Command cmd = null;

		if (model != null)
		{
			cmd = res.createCommand(model);
		}
		else
		{
			cmd = res.createCommand(getPreviousModel(req));
		}

		Map prevReq = req.getPreviousRequest();

		if (prevReq == null || prevReq.isEmpty())
		{
			prevReq = req.getParameters();
		}

		for (Iterator i = prevReq.keySet().iterator(); i.hasNext();)
		{
			String key = (String) i.next();

			if (! "model".equals(key) && ! "bean".equals(key) && ! "orig-model".equals(key)
							&& ! "SEQUENCE_NAME".equals(key) && ! "SEQUENCE_NUMBER".equals(key))
			{
				Object param = prevReq.get(key);

				if (! (param instanceof String[]))
				{
					boolean ommit = false;

					if (ommitPrevParameters != null)
					{
						for (int j = 0; j < ommitPrevParameters.length; ++j)
						{
							if (ommitPrevParameters[j].startsWith("~"))
							{
								if (key.toLowerCase().indexOf(ommitPrevParameters[j].substring(1).toLowerCase()) != - 1)
								{
									ommit = true;

									continue;
								}
							}
							else if (ommitPrevParameters[j].toLowerCase().equals(key.toLowerCase()))
							{
								ommit = true;

								continue;
							}
						}
					}

					if (! ommit)
					{
						cmd.setParameter(key, prevReq.get(key));
					}
				}
			}
		}

		return cmd;
	}

	/**
	 * Create a new command based on the 'previous request'. All parameters to
	 * the previous model are copied to the new command.
	 *
	 * @param req
	 *            The current model request.
	 * @param res
	 *            The current model response.
	 * @return The new command.
	 */
	public static Command createPreviousModelCommand(ModelRequest req, ModelResponse res) throws ModelException
	{
		return createPreviousModelCommand(req, res, null);
	}

	/**
	 * Get the name of the 'previous request' model.
	 *
	 * @param req
	 *            The current model request.
	 * @return The model name.
	 */
	public static String getPreviousModel(ModelRequest req) throws ModelException
	{
		String model = null;

		model = (String) req.getParameter("SEQUENCE_NAME");

		if (model == null)
		{
			model = (String) req.getModel();
		}

		return model;
	}

	/**
	 * Create a new command and copy all request parameters into it.
	 *
	 * @param req
	 *            The current model request.
	 * @param res
	 *            The current model response.
	 * @param model
	 *            The model name.
	 * @return The new command.
	 */
	public static Command createModelWithParameters(ModelRequest req, ModelResponse res, String model)
		throws ModelException
	{
		Command cmd = res.createCommand(model);

		for (Iterator i = req.getParameters().keySet().iterator(); i.hasNext();)
		{
			String key = (String) i.next();

			if (! "model".equals(key) && ! "SEQUENCE_NAME".equals(key) && ! "SEQUENCE_NUMBER".equals(key))
			{
				cmd.setParameter(key, req.getParameters().get(key));
			}
		}

		return cmd;
	}

	/**
	 * Perform a model request.
	 *
	 * @param req
	 *            The original model request.
	 * @param model
	 *            The name of the model to call.
	 * @param params
	 *            Model parameters.
	 */
	public static ModelResponse callModel(ModelRequest req, String model, Properties params) throws ModelException
	{
		ModelResponse res = req.createResponse();
		Command cmd = res.createCommand(model);

		if (params != null)
		{
			for (Iterator i = params.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry param = (Map.Entry) i.next();

				cmd.setParameter((String) param.getKey(), param.getValue());
			}
		}

		return cmd.execute(req, res);
	}

	/**
	 * Perform a model request.
	 *
	 * @param model
	 *            The name of the model to call.
	 * @param params
	 *            Model parameters.
	 */
	public static ModelResponse callModel(String model, Properties params) throws ModelException
	{
		ModelRequest request = null;
		try
		{
			request = createModelRequest();
			return callModel(request, model, params);
		}
		catch (ServiceException ignored)
		{
			return null;
		}
		finally
		{
			releaseModelRequest(request);
		}
	}

	/**
	 * Perform a model request. The request parameters of the original request
	 * are passed to the model. All generated response elements are stored in
	 * the supplied model response.
	 *
	 * @param req
	 *            The original model request.
	 * @param res
	 *            The model response to write to.
	 * @param model
	 *            The name of the model to call.
	 */
	public static void callModel(ModelRequest req, ModelResponse res, String model) throws ModelException
	{
		Command cmd = res.createCommand(model);
		ModelResponse newRes = cmd.execute(req, res, true, false);

		KeelTools.copyResponseElements(res, newRes);
	}

	/**
	 * Perform a model request.
	 *
	 * @param req
	 *            The original model request.
	 * @param model
	 *            The name of the model to call.
	 * @param params
	 *            Model parameters.
	 */
	public static ModelResponse callModel(ModelRequest req, String model, Object... params) throws ModelException
	{
		ModelResponse res = req.createResponse();
		Command cmd = res.createCommand(model);

		for (Pair<String, Object> param : new PairwiseIterator<Object, String, Object>(params))
		{
			cmd.setParameter(param.get1(), param.get2());
		}

		return cmd.execute(req, res);
	}

	/**
	 * Perform a model request.
	 *
	 * @param req
	 *            The original model request.
	 * @param model
	 *            The name of the model to call.
	 */
	public static ModelResponse callModel(ModelRequest req, String model) throws ModelException
	{
		return callModel(req, model, (Object[]) null);
	}

	/**
	 * Check for the existence of a model.
	 *
	 * @param req
	 *            The original model request.
	 * @param model
	 *            The name of the model to check.
	 * @return True if the model exists.
	 */
	public static boolean modelExists(ModelRequest req, String model)
	{
		try
		{
			req.getService(Model.ROLE, model);
		}
		catch (ModelException x)
		{
			return false;
		}

		return true;
	}

	/**
	 * To provide extendable model configurations, you can define a tag
	 * 'extends' which names the model to extend. This method creates a list of
	 * all extended configurations. Other methods use this path to find a
	 * configuration value in the model hierarchy.
	 *
	 * @param req
	 *            The model request.
	 * @param model
	 *            The model to process.
	 * @exception ModelException
	 *                If a specifed model wasn't found.
	 */
	public static List getDerivationPath(ModelRequest req, Model model) throws ModelException
	{
		List path = new LinkedList();
		Configuration config = model.getConfiguration();

		while (config != null)
		{
			path.add(config);

			String extendsModelName = config.getChild("extends").getValue(null);

			if (extendsModelName != null)
			{
				Model extendsModel = (Model) req.getService(Model.ROLE, extendsModelName);

				config = extendsModel.getConfiguration();
			}
			else
			{
				config = null;
			}
		}

		return path;
	}

	/**
	 * Get a boolean configuration value.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param defaultValue
	 *            Default value if no configuration was found.
	 * @return The configuration value.
	 */
	public static boolean getConfigBool(List configPath, String name, boolean defaultValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			String value = config.getChild(name).getValue(null);

			if (value != null)
			{
				return NumberTools.toBool(value, defaultValue);
			}
		}

		return defaultValue;
	}

	/**
	 * Get a boolean configuration attribute.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param property
	 *            Name of the configuration property.
	 * @param defaultValue
	 *            Default value if no configuration was found.
	 * @return The configuration value.
	 */
	public static boolean getConfigBool(List configPath, String name, String property, boolean defaultValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			String value = config.getChild(name).getAttribute(property, null);

			if (value != null)
			{
				return NumberTools.toBool(value, defaultValue);
			}
		}

		return defaultValue;
	}

	/**
	 * Get an integer configuration value.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param defaultValue
	 *            Default value if no configuration was found.
	 * @return The configuration value.
	 */
	public static int getConfigInt(List configPath, String name, int defaultValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			String value = config.getChild(name).getValue(null);

			if (value != null)
			{
				return NumberTools.toInt(value, defaultValue);
			}
		}

		return defaultValue;
	}

	/**
	 * Get an integer configuration attribute.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param property
	 *            Name of the configuration property.
	 * @param defaultValue
	 *            Default value if no configuration was found.
	 * @return The configuration value.
	 */
	public static int getConfigInt(List configPath, String name, String property, int defaultValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			String value = config.getChild(name).getAttribute(property, null);

			if (value != null)
			{
				return NumberTools.toInt(value, defaultValue);
			}
		}

		return defaultValue;
	}

	/**
	 * Get a string configuration value.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param defaultValue
	 *            Default value if no configuration was found.
	 * @return The configuration value.
	 */
	public static String getConfigString(List configPath, String name, String defaultValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			String value = config.getChild(name).getValue(null);

			if (value != null)
			{
				return value;
			}
		}

		return defaultValue;
	}

	/**
	 * Get a string configuration attribute.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param property
	 *            Name of the configuration property.
	 * @param defaultValue
	 *            Default value if no configuration was found.
	 * @return The configuration value.
	 */
	public static String getConfigString(List configPath, String name, String property, String defaultValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			String value = config.getChild(name).getAttribute(property, null);

			if (value != null)
			{
				return value;
			}
		}

		return defaultValue;
	}

	/**
	 * Get a list of configuration children. This list contains all children
	 * with the specified names in the configuration hierarchy.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration children.
	 * @return The configuration children.
	 */
	public static List<Configuration> getConfigChildren(List<Configuration> configPath, String name)
	{
		List<Configuration> children = new LinkedList<Configuration>();

		for (Configuration config : configPath)
		{
			Configuration[] childrenConfig = config.getChildren(name);

			if (childrenConfig != null)
			{
				for (int j = 0; j < childrenConfig.length; ++j)
				{
					children.add(childrenConfig[j]);
				}
			}
		}

		return children;
	}

	/**
	 * Get a list of configuration children in reverse order. This list contains
	 * all children with the specified names in the configuration hierarchy.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration children.
	 * @return The configuration children.
	 */
	public static List getConfigChildrenReverse(List configPath, String name)
	{
		List children = new LinkedList();

		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			Configuration[] childrenConfig = config.getChildren(name);

			if (childrenConfig != null)
			{
				for (int j = 0; j < childrenConfig.length; ++j)
				{
					children.add(childrenConfig[j]);
				}
			}
		}

		Collections.reverse(children);

		return children;
	}

	/**
	 * Get a configuration object.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @return The configuration object.
	 */
	public static Configuration getConfig(List configPath, String name)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();
			Configuration value = config.getChild(name, false);

			if (value != null)
			{
				return value;
			}
		}

		return null;
	}

	/**
	 * Find a configuration element with the specified name and attribute value.
	 *
	 * @param configPath
	 *            The list of model configurations.
	 * @param name
	 *            Name of the configuration element.
	 * @param property
	 *            Name of the configuration propery.
	 * @param propertyValue
	 *            Value of the configuration propery.
	 * @return The configuration value.
	 */
	public static Configuration findConfig(List configPath, String name, String property, String propertyValue)
	{
		for (Iterator i = configPath.iterator(); i.hasNext();)
		{
			Configuration config = (Configuration) i.next();

			Configuration[] children = config.getChildren(name);

			for (int j = 0; j < children.length; ++j)
			{
				if (propertyValue.equals(children[j].getAttribute(property, null)))
				{
					return children[j];
				}
			}
		}

		return null;
	}

	/**
	 * Standard exception handling.
	 */
	public static ModelException handleException(ModelResponse res, Exception x, Logger log)
	{
		StringWriter sw = new StringWriter();

		x.printStackTrace(new PrintWriter(sw));
		res.addError("GLOBAL_exceptionStackTrace", sw.toString());

		if (log != null)
		{
			log.debug(x.toString());
			log.debug(sw.toString());

			System.out.println(x.toString());
			System.out.println(sw.toString());
		}

		return new ModelException(x.toString());
	}

	/**
	 * Get the content of an output element.
	 *
	 * @param res
	 *            A model response.
	 * @param key
	 *            The key of the output element.
	 * @return The output content.
	 */
	public static Object getOutputContent(ModelResponse res, String key)
	{
		return ((Output) res.get(key)).getContent();
	}

	/**
	 * Merge two model responses.
	 *
	 * @param base
	 *            The first model response.
	 * @param layer
	 *            The model response to merge.
	 * @retur A merged model response.
	 */
	public static ModelResponse mergeResponse(ModelResponse base, ModelResponse layer) throws ModelException
	{
		if (layer == null)
		{
			return base;
		}

		if (base == null)
		{
			return layer;
		}

		for (Iterator i = layer.getAll(); i.hasNext();)
		{
			ResponseElement newElement = (ResponseElement) i.next();

			base.add(newElement);
		}

		for (Iterator j = layer.getAttributes().keySet().iterator(); j.hasNext();)
		{
			String oneAttribName = (String) j.next();

			base.setAttribute(oneAttribName, layer.getAttribute(oneAttribName));
		}

		Map errors = new HashMap(layer.getErrors());

		for (Iterator k = errors.keySet().iterator(); k.hasNext();)
		{
			String oneErrorName = (String) k.next();
			Throwable throwable = layer.getThrowable(oneErrorName);
			String oneMessage = (String) errors.get(oneErrorName);

			if (throwable != null)
			{
				base.addError(oneErrorName, oneMessage, throwable);
			}
			else
			{
				base.addError(oneErrorName, oneMessage);
			}
		}

		return base;
	}

	/**
	 * Create a model request.
	 *
	 * @return The model request.
	 */
	public static ModelRequest createModelRequest() throws ServiceException
	{
		return (ModelRequest) KeelTools.getService(ModelRequest.ROLE, "default", new DefaultContext());
	}

	/**
	 * Release a model request.
	 *
	 * @param modelRequest
	 */
	public static void releaseModelRequest(ModelRequest modelRequest)
	{
		if (modelRequest != null)
		{
			KeelTools.releaseService(modelRequest);
		}
	}

	/**
	 * Load a server resource.
	 */
	public static String loadServerResourceAsString(String moduleName, String name, Logger log)
	{
		StringBuilder sb = new StringBuilder();

		try
		{
			String fileName = System.getProperty("keel.config.dir") + "/../resources/app-" + moduleName + "/" + name;

			BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
			String line = null;

			while ((line = in.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		}
		catch (IOException x)
		{
			log.error("[I18NImpl] " + x);
		}

		return sb.toString();
	}
}
