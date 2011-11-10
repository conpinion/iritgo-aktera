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

package de.iritgo.aktera.ui.el;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.beanutils.PropertyUtils;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.ui.UIController;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.ng.ModelRequestWrapper;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


public class ExpressionLanguageContext
{
	/** The model requets */
	protected ModelRequest request;

	/** The user environment */
	private UserEnvironment userEnvironment;

	public void setUserEnvironment(UserEnvironment userEnvironment)
	{
		this.userEnvironment = userEnvironment;
	}

	/**
	 * Initialize a new ExpressionLanguageContext.
	 *
	 * @param request A ui request
	 */
	public ExpressionLanguageContext(UIRequest request)
	{
		this.request = new ModelRequestWrapper(request);
	}

	/**
	 * Initialize a new ExpressionLanguageContext.
	 *
	 * @param request A model request
	 */
	public ExpressionLanguageContext(ModelRequest request)
	{
		this.request = request;
	}

	/**
	 * Initialize a new ExpressionLanguageContext.
	 */
	public ExpressionLanguageContext()
	{
	}

	/**
	 * Set the ui request.
	 *
	 * @param request The model request
	 */
	public void setRequest(UIRequest request)
	{
		this.request = new ModelRequestWrapper(request);
	}

	/**
	 * Set the model request.
	 *
	 * @param request The model request
	 */
	public void setRequest(ModelRequest request)
	{
		this.request = request;
	}

	/**
	 * Get the model request.
	 *
	 * @return The model request
	 */
	public ModelRequest getRequest()
	{
		return request;
	}

	/**
	 * Get the request parameters.
	 *
	 * @return The request parameters
	 */
	public Map getParams()
	{
		return request != null ? request.getParameters() : null;
	}

	/**
	 * Get a request parameter.
	 *
	 * @param name The request parameter name
	 * @return The parameter value
	 */
	public String getParam(String name)
	{
		return request != null ? StringTools.trim(request.getParameter(name)) : "";
	}

	/**
	 * Get a request parameter as an integer.
	 *
	 * @param name The request parameter name
	 * @return The parameter value as an integer
	 */
	public Integer getParamAsInt(String name)
	{
		return request != null ? NumberTools.toIntInstance(request.getParameter(name), - 1) : - 1;
	}

	/**
	 * Get a request parameter as a long.
	 *
	 * @param name The request parameter name
	 * @return The parameter value as a long
	 */
	public Long getParamAsLong(String name)
	{
		return request != null ? NumberTools.toLongInstance(request.getParameter(name), - 1) : - 1;
	}

	/**
	 * See {@link ExpressionLanguageContext#evalExpressionLanguageValue(Object, ModelRequest, String)}.
	 */
	public Object evalExpressionLanguageValue(String expression) throws IllegalArgumentException
	{
		return evalExpressionLanguageValue(this, request, expression);
	}

	/**
	 * Parse an expression language string and return the corresponding value.
	 * If the supplied argument is not an expression language string, the string
	 * itself is returned.If the expression connot be resolved succesfully, an
	 * InvalidArgumentException is thrown.
	 *
	 * The following expressions are supported:
	 *
	 * <ul>
	 * <li>#{variable} Retrieve a variable from the context map</li>
	 * <li>#{variable.property} Retrieve a property (through calling the getter)
	 * of a context variable</li>
	 * <li>#{variable.property(index)} Retrieve a property (through calling a
	 * getter which returns an array or a list) of a context variable</li>
	 * <li>#{variable.property(key)} Retrieve a property (through calling a
	 * getter which accepts a string parameter) of a context variable</li>
	 * <li>#{variable.property1.property2...} Retrieve a property (through
	 * calling a chain of getters as specified above) of a context variable</li>
	 * </ul>
	 *
	 * @param expression
	 *            The expression language string
	 * @param context
	 *            POJO containing the accessible variables
	 * @param properties
	 *            Backward compatibility; Additional variable properties. TODO
	 *            Should be replaced (see code)
	 * @return The expression language value
	 * @throws InvalidArgumentException
	 *             in case of an error
	 */
	public static Object evalExpressionLanguageValue(Object context, ModelRequest request, String expression)
		throws IllegalArgumentException
	{
		if (expression.startsWith("#{") && expression.endsWith("}"))
		{
			expression = expression.substring(2, expression.length() - 1);

			try
			{
				return PropertyUtils.getNestedProperty(context, expression);
			}
			catch (IllegalAccessException x)
			{
			}
			catch (InvocationTargetException x)
			{
			}
			catch (NoSuchMethodException x)
			{
			}

			throw new IllegalArgumentException("Error in expression '" + expression + "'. Unable"
							+ " to retrieve variable or it's property");
		}

		// Backward compatibility: #paramName
		// TODO Replace this with ${params(name)}
		if (expression.startsWith("#"))
		{
			if (request == null)
			{
				throw new IllegalArgumentException("Error in expression '" + expression + "'. No"
								+ " model request found in context");
			}

			return request.getParameterAsString(expression.substring(1));
		}

		return expression;
	}

	/**
	 * See {@link ExpressionLanguageContext#evalExpressionLanguageValue(Object, ModelRequest, String)}.
	 */
	public static Properties evalExpressionLanguageValue(Object context, ModelRequest request, Properties expressions)
	{
		Properties newProperties = new Properties();

		for (Entry<Object, Object> expression : expressions.entrySet())
		{
			Object val = evalExpressionLanguageValue(context, request, expression.getValue().toString());

			if (val != null)
			{
				newProperties.put(expression.getKey(), val);
			}
		}

		return newProperties;
	}
}
