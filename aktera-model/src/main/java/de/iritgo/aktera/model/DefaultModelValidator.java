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

package de.iritgo.aktera.model;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelValidator;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import java.util.Iterator;


/**
 * The DefaultModelValidator is used if no other "validator" hint
 * is supplied in the model's configuration. It reads the model's
 * configuration, and understands the following configuration (Xdoclet
 * tags for these are also recognized):
 * <br>
 * <ul>
 * <li>parameter: The parameter element with the usual "name" attribute
 * (and the optional "value" attribute)
 * will now also accept the following attributes:</li>
 *     <ul>
 * <li>required: True or false. Value for this parameter must be supplied.
 * False is assumed if the attribute is absent.</li>
 * <li>type: A valid Java type (e.g. "java.lang.Double"). The parameter's
 * value must be capable of being converted to this type without error
 * in order for the validation to pass. Any type accepted if this
 * attribute is absent.</li>
 * <li>default: A default value to be supplied for this parameter if
 * there is no parameter of this name already in the request (or if
 * the current value is null). No default if this attribute is absent.</li>
 * <li>pattern: A regular expression that this field's value must match,
 * see String.match() in the J2SE JavaDoc for details of the types
 * of regular expressions accepted here. No match attempted if
 * this attribute is absent.</li>
 *     </ul>
 * <li>validation-error: This element indicates a model that should be
 * executed *instead* of the specified model in the event of a
 * validation error. Allows a "Save" model to "jump" back to the "prompt"
 * model, for instance. It accepts the following two attributes</li>
 * <ul>
 * <li>model: The id of the model or sequence to jump to in the event
 * of a validation error. Required.</li>
 * <li>params: Include the parameters to the current request as parameters
 * to the validation-error model - e.g. "pass on" parameters. True or false,
 * false assumed if attribute is absent.</li>
 * </ul>
 * </ul>
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.ModelValidator
 * @x-avalon.info name=default-validator
 * @x-avalon.lifestyle type=singleton
 *
 * @author Michael Nash
 */
public class DefaultModelValidator implements ModelValidator, LogEnabled
{
	private static Logger log = null;

	private static boolean convertersRegistered = false;

	/**
	 * Examine the Model's configuration and see if there are any "parameter"
	 * elements, which may provide certain validation information about each
	 * parameter. If there are any such elements, validate the named
	 * parameters, adding to a store of errors that are later passed on to the
	 * model.
	 *
	 * @param req The ModelRequest containing the parameters to be validated
	 * @param newModel The model to be executed.
	 * @throws ConfigurationException
	 *             If parameter configuration exists, but it incorrectly
	 *             specified.
	 * @throws ModelException If there is an exception accessing the parameters to be validated
	 * @return A command, ready to be executed to return to the appropriate
	 *         model for the user to try again, or null of no such
	 *         validation-error model is specified. If null is returned, then
	 *         any validation errors are stored as a Map in a request
	 *         attribute, where they may be accessed by the Model as needed.
	 */
	public Command validate (ModelRequest req, Model newModel) throws ConfigurationException, ModelException
	{
		Configuration conf = newModel.getConfiguration ();

		/* If there's no config, we can't possibly have any parameter config */
		if (conf == null)
		{
			return null;
		}

		if (! convertersRegistered)
		{
			registerConverters ();
		}

		String name = null;
		Object value = null;
		String defaultValue = null;
		String pattern = null;
		String type = null;
		boolean required = false;

		Configuration[] paramConfigs = conf.getChildren ("parameter");

		for (int i = 0; i < paramConfigs.length; i++)
		{
			Configuration oneParamConfig = paramConfigs[i];

			name = oneParamConfig.getAttribute ("name");
			required = oneParamConfig.getAttributeAsBoolean ("required", false);
			value = req.getParameter (name);

			if (required && (value == null))
			{
				req.addError (name, "'" + name + "' is required");
			}

			defaultValue = oneParamConfig.getAttribute ("default", null);
			type = oneParamConfig.getAttribute ("type", null);

			if ((value == null) && (defaultValue != null))
			{
				req.setParameter (name, defaultValue);
			}

			if ((value != null) && (type != null))
			{
				/* verify the value can be converted to the specified type */
				if (! type.equals ("java.lang.String"))
				{
					Class clazz = null;

					try
					{
						clazz = Class.forName (type);
					}
					catch (ClassNotFoundException ce)
					{
						throw new ConfigurationException ("No class found for '" + type
										+ "', specified as type for parameter '" + name + "'");
					}

					if (type.equalsIgnoreCase ("java.util.Date"))
					{
						try
						{
							new SuperString (value.toString ()).toDate ();
						}
						catch (Exception e)
						{
							log.error ("Value '" + value + " cannot be converted to a date", e);
							req.addError (name, "Value '" + value + "' cannot be converted to a date");
						}
					}
					else
					{
						Converter c = ConvertUtils.lookup (clazz);

						if (c == null)
						{
							log.error ("No converter found for class '" + type + "'");
							throw new IllegalArgumentException ("No converter found for class '" + type + "'");
						}

						try
						{
							c.convert (clazz, value);
						}
						catch (ConversionException ce)
						{
							req.addError (name, "Value '" + value + "' cannot be converted to a '" + type + "'");
						}
					}
				}
			}

			pattern = oneParamConfig.getAttribute ("pattern", null);

			if ((value != null) && (pattern != null))
			{
				if (! value.toString ().matches (pattern))
				{
					req.addError (name, "Value '" + value + "' does not match validation pattern '" + pattern + "'");
				}
			}
		}

		if (conf.getChild ("validation-error", false) != null)
		{
			Configuration validationErrorConfig = conf.getChild ("validation-error");
			Command redirect = req.createResponse ().createCommand (validationErrorConfig.getAttribute ("model"));

			if (validationErrorConfig.getAttributeAsBoolean ("params", true))
			{
				String oneParamName = null;

				for (Iterator i = req.getParameters ().keySet ().iterator (); i.hasNext ();)
				{
					oneParamName = (String) i.next ();
					redirect.setParameter (oneParamName, req.getParameter (oneParamName));

					return redirect;
				}
			}
		}

		return null;
	}

	/**
	 * Receive our logger from the container
	 * @param newLog The logger passed to this component by the container on initialization
	 */
	public void enableLogging (Logger newLog)
	{
		log = newLog;
	}

	/**
	 * The reason we re-register converters (as a number of defaults are
	 * already registered) is that the default registrations set things up
	 * such that a conversion error returns a "default" value, (e.g. zero).
	 * We want to actually throw a ConversionException if there's a problem
	 * instead of just returning a default value.
	 * Support for additional types might require additional converters be
	 * registered here.
	 */
	private void registerConverters ()
	{
		ConvertUtils.deregister ();
		ConvertUtils.register (new LongConverter (), java.lang.Long.class);
		ConvertUtils.register (new IntegerConverter (), java.lang.Integer.class);
		ConvertUtils.register (new FloatConverter (), java.lang.Float.class);
		ConvertUtils.register (new DoubleConverter (), java.lang.Double.class);

		convertersRegistered = true;
	}
}
