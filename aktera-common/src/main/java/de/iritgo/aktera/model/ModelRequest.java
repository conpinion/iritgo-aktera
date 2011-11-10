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


import java.util.*;
import org.apache.avalon.framework.context.Context;


public interface ModelRequest extends KeelRequest
{
	public final static String ROLE = "de.iritgo.aktera.model.ModelRequest";

	/**
	 * Actually execute the requested model object's logic, returning the ModelResponse it creates
	 */
	public ModelResponse execute() throws ModelException;

	/**
	 * Used by the model to generate the "empty" response object, which
	 * the model then populates with Inputs, Outputs and Commands
	 */
	public ModelResponse createResponse() throws ModelException;

	/**
	 * Get the Keel Context object indiciating the current context for this request. Usually this contains at least a UserEnvironment
	 * object
	 */
	public Context getContext() throws ModelException;

	/**
	 * This method is used by Model objects to get access to other
	 * services or services in the system. The current context is used to
	 * determine if a special "hint" should be used with the service selector in
	 * order to deliver a specific service - for example, when getting a
	 * PersistentFactory, the UserEnvironment in the current context is used to
	 * determine the currently selected database.
	 */
	public Object getDefaultService(String role) throws ModelException;

	/**
	 * This method is used by Model objects to get access to other
	 * services or services in the system. The current context is used to
	 * determine if a special "hint" should be used with the service selector in
	 * order to deliver a specific service - for example, when getting a
	 * PersistentFactory, the UserEnvironment in the current context is used to
	 * determine the currently selected database.
	 */
	public Object getService(String role) throws ModelException;

	/**
	 * This method is used by a model when it wants to access a particular
	 * service (often a factory for another service) and specify the hint
	 * itself. This basically means the Model has access to the same kind of
	 * access it would have if it were a Composable, but only through it's
	 * request object.
	 */
	public Object getService(String role, String hint) throws ModelException;

	/**
	 * This method is used by a model when it wants to access a particular
	 * service (often a factory for another service) and specify the hint
	 * itself. This basically means the Model has access to the same kind of
	 * access it would have if it were a Composable, but only through it's
	 * request object.
	 */
	public Object getService(String role, String hint, Context ctx) throws ModelException;

	/**
	 * Return the user identifier of the user who has submitted this
	 * request. Normally gets this information from the UserEnvironment
	 * object in the current context. If none, return 0 (anonymous user)
	 */
	public int getUid();

	public String getDomain();

	/**
	 * A request for a model may involve a jump to another model, or back to the same model in the
	 * event of an error (for the user to input new data, for example). In this case, a map of the
	 * parameters to the the 'previous'
	 * request is held here, so that the last value of various parameters supplied in the last request
	 * can be displayed (possibly along with an error message) for the user to try again
	 */
	public Map getPreviousRequest();

	public void setPreviousRequest(Map newPrevious);

	/**
	 * Get the configuration object for this Request's Model
	 * @return Return the Configuration object for this model (if any)
	 */
	public Object getConfiguration();

	/**
	 * Copy the values from another Keel Request into this one.
	 * @param newRequest The request to copy from
	 * @throws ModelException  If an error occurs during the copy
	 */
	public void copyFrom(KeelRequest newRequest) throws ModelException;

	/**
	 * Get the named parameter as an int. Return 0 if there is no such parameter.
	 * @param name Name of the required parameter
	 * @return An int representation of the paramter, 0 if no such parameter.
	 */
	public int getParameterAsInt(String name);

	/**
	 * Return the specified parameter as an integer, returning the supplied default
	 * value if no such parameter exists.
	 * @param name Name of the required parameter
	 * @param defaultValue The value returned if no such parameter exists
	 * @return The int value corresponding to the named parameter
	 */
	public int getParameterAsInt(String name, int defaultValue);

	/**
	 * Return the specified parameter as a long
	 * @param name Name of the required parameter
	 * @return The parameter value as a long, or 0 if no such parameter exists.
	 */
	public long getParameterAsLong(String name);

	/**
	 * Return the specified parameter as a long, or the default value if there
	 * is no such parameter.
	 * @param name Name of the required parameter
	 * @param defaultValue The value to be returned if no such parameter exists
	 * @return The value of the specified parameter as a long, or the default
	 * value if no such parameter exists.
	 */
	public long getParameterAsLong(String name, long defaultValue);

	/**
	 * Return the value of a parameter as a float
	 * @param name Name of the required parameter
	 * @return The specified parameter as a float, or 0 if there is no such parameter
	 */
	public float getParameterAsFloat(String name);

	/**
	 * Return the specified parameter's value as a float
	 * @param name Name of the required parameter
	 * @param defaultValue
	 * @return The specified parameter's value as a float, or the default value if no such
	 * parameter exists
	 */
	public float getParameterAsFloat(String name, float defaultValue);

	/**
	 * Return the specified parameter's value as a double
	 * @param name Name of the required parameter
	 * @return The specified value as a double, or 0 if there is no such parameter
	 */
	public double getParameterAsDouble(String name);

	/**
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue
	 * @return
	 */
	public double getParameterAsDouble(String name, double defaultValue);

	/**
	 * Return the specified parameter's value as a string
	 * @param name Name of the required parameter
	 * @return The parameter value as a string, or an empty string if there is no such parameter
	 */
	public String getParameterAsString(String name);

	/**
	 *
	 * @param name Name of the required parameter
	 * @param defaultValue
	 * @return
	 */
	public String getParameterAsString(String name, String defaultValue);

	/**
	 * Return the values of a multi-valued parameter as an array of objects.
	 * @param name Name of the required parameter
	 * @return The parameter value as an array of objects.
	 */
	public Object[] getParameterAsArray(String name);

	/**
	 * Return the value of the specified parameter as a date
	 * @param name Name of the required parameter
	 * @return A date value for the specified parameter, or null if there is no such parameter
	 */
	public Date getParameterAsDate(String name);

	/**
	 * Return the specified parameter's value as a date, or the default value if no such
	 * parameter exists.
	 * @param name Name of the required parameter
	 * @param defaultValue The date value to return if no such parameter exists
	 * @return The specified parameter as a date, or the default
	 */
	public Date getParameterAsDate(String name, Date defaultValue);

	/**
	 * Return the values of a parameter which has more than one value
	 * @param name Name of the required parameter
	 * @return The specified parameter as a list of values
	 */
	public List getParameterAsList(String name);

	/**
	 * Return the values of a parameter which has more than one value
	 * @param name Name of the required parameter
	 * @param defaultValue The list returned if no such parameter exists.
	 * @return The specified parameter as a list of values, or the default list supplied
	 * if no such parameter exists.
	 */
	public List getParameterAsList(String name, List defaultValue);

	/**
	 * Return a map of error names and messages produced by validation
	 * of the parameters before entering this model. This allows the model
	 * to deal with these errors as it sees fit - note: such errors
	 * are not automatically added the the response, the model must
	 * do that if it is required (res.addErrors(req.addErrors()) for example.
	 * @return A map mapping names to messages for validation errors, or an
	 * empty map if no such errors have occurred.
	 */
	public Map getErrors();

	/**
	 * Add an error with a specific name. This name may be used to associate the error
	 * with a certain UI element (such as an Input), so the user can see immediately
	 * what was wrong when the error is displayed.This method is generally
	 * only used by ModelValidator components *before* Model execution begins.
	 *
	 * @param errorName A name for this error message, which may (or may not) correspond
	 * to the UI element from which the error originated.
	 * @param errorMessage A human-readable error message to be displayed to the user. Messages
	 * beginning with "$" will be taken to be internationalized keys to look up in the
	 * appropriate message bundle for this locale.
	 */
	public void addError(String errorName, String errorMessage);

	/**
	 * Get a Spring bean from the Spring container.
	 *
	 * @param name The name of the Spring bean.
	 * @return The Spring bean.
	 * @throws ModelException If the bean cannot be retrieved.
	 */
	public Object getSpringBean(String name) throws ModelException;

	/**
	 * Get the request parameters.
	 *
	 * @return The request parameters
	 */
	public Map getParams();

	public boolean hasParameter(String name);
}
