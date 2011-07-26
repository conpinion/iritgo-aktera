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

package de.iritgo.aktera.ui;


import de.iritgo.aktera.authentication.UserEnvironment;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public interface UIRequest
{
	/**
	 * Get the controller bean name.
	 *
	 * @return bean The bean name
	 */
	public String getBean ();

	/**
	 * Get the user environment.
	 *
	 * @return The user environment
	 */
	public UserEnvironment getUserEnvironment ();

	/**
	 * Get the request locale.
	 *
	 * @return The request locale
	 */
	public Locale getLocale ();

	/**
	 * Set a parameter.
	 *
	 * @param name The parameter name
	 * @param value The parameter value
	 */
	public void setParameter (String name, Object value);

	/**
	 * Return the value of the specified parameter.
	 *
	 * @param name Name of the required parameter
	 * @return The parameter value
	 */
	public Object getParameter (String name);

	/**
	 * Check the existence of the specified parameter.
	 *
	 * @param name The name of the parameter to check
	 * @return True if the parameter exists
	 */
	public boolean hasParameter (String name);

	/**
	 * Return the values of a multi-valued parameter as an array of objects.
	 *
	 * @param name Name of the required parameter
	 * @return The parameter value as an array of objects
	 */
	public Object[] getParameterAsArray (String name);

	/**
	 * Return the values of a multi-valued parameter as an array of objects.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The array value to return if no such parameter exists
	 * @return The parameter value as an array of objects
	 */
	public Object[] getParameterAsArray (String name, Object[] defaultValue);

	/**
	 * Return the value of the specified parameter as a date
	 *
	 * @param name Name of the required parameter
	 * @return A date value for the specified parameter, or null if there is no such parameter
	 */
	public Date getParameterAsDate (String name);

	/**
	 * Return the specified parameter's value as a date, or the default value if no such
	 * parameter exists.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The date value to return if no such parameter exists
	 * @return The specified parameter as a date, or the default
	 */
	public Date getParameterAsDate (String name, Date defaultValue);

	/**
	 * Return the specified parameter's value as a double.
	 *
	 * @param name Name of the required parameter
	 * @return The specified value as a double, or 0 if there is no such parameter
	 */
	public double getParameterAsDouble (String name);

	/**
	 * Return the specified parameter's value as a double.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The double value to return if no such parameter exists
	 * @return The specified value as a double, or 0 if there is no such parameter
	 */
	public double getParameterAsDouble (String name, double defaultValue);

	/**
	 * Return the value of a parameter as a float.
	 *
	 * @param name Name of the required parameter
	 * @return The specified parameter as a float, or 0 if there is no such parameter
	 */
	public float getParameterAsFloat (String name);

	/**
	 * Return the specified parameter's value as a float.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The float value to return if no such parameter exists
	 * @return The specified parameter's value as a float, or the default value if no such
	 * parameter exists
	 */
	public float getParameterAsFloat (String name, float defaultValue);

	/**
	 * Get the named parameter as an int. Return 0 if there is no such parameter.
	 *
	 * @param name Name of the required parameter
	 * @return An int representation of the paramter, 0 if no such parameter
	 */
	public int getParameterAsInt (String name);

	/**
	 * Return the specified parameter as an integer, returning the supplied default
	 * value if no such parameter exists.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The value returned if no such parameter exists
	 * @return The int value corresponding to the named parameter
	 */
	public int getParameterAsInt (String name, int defaultValue);

	/**
	 * Return the values of a parameter which has more than one value.
	 *
	 * @param name Name of the required parameter
	 * @return The specified parameter as a list of values
	 */
	public List getParameterAsList (String name);

	/**
	 * Return the values of a parameter which has more than one value.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The list returned if no such parameter exists
	 * @return The specified parameter as a list of values, or the default list supplied
	 * if no such parameter exists
	 */
	public List getParameterAsList (String name, List defaultValue);

	/**
	 * Return the specified parameter as a long.
	 *
	 * @param name Name of the required parameter
	 * @return The parameter value as a long, or 0 if no such parameter exists.
	 */
	public long getParameterAsLong (String name);

	/**
	 * Return the specified parameter as a long, or the default value if there
	 * is no such parameter.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The value to be returned if no such parameter exists
	 * @return The value of the specified parameter as a long, or the default
	 * value if no such parameter exists.
	 */
	public long getParameterAsLong (String name, long defaultValue);

	/**
	 * Return the specified parameter's value as a string.
	 *
	 * @param name Name of the required parameter
	 * @return The parameter value as a string, or an empty string if there is no such parameter
	 */
	public String getParameterAsString (String name);

	/**
	 * Return the specified parameter's value as a string.
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue The value to be returned if no such parameter exists
	 * @return The value of the specified parameter as a long, or the default
	 * value if no such parameter exists
	 */
	public String getParameterAsString (String name, String defaultValue);

	/**
	 * Return a map of all parameters.
	 *
	 * @return The parameter map
	 */
	public Map<String, Object> getParameters ();
}
