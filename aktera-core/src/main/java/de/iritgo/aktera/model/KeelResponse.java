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


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


public interface KeelResponse
{
	/**
	 * Return a specific element
	 */
	public ResponseElement get(String elementLocator);

	public void remove(ResponseElement re) throws ModelException;

	/**
	 * Return an iterator over all of the ResponseElements in this response
	 */
	public Iterator getAll();

	/**
	 * The methods from here down are used *only* by the Model object
	 * itself, and should not be called from external objects
	 */

	/**
	 * The Model (only) uses the method below to add new response elements to
	 * this response.
	 */
	public void add(ResponseElement re) throws ModelException;

	/**
	 * Convenience add method for creating a simple string output with
	 * a specific name
	 */
	public void addOutput(String name, String content) throws ModelException;

	/**
	 * Convenience add method for creating a simple string output with a
	 * name assigned by the ModelResponse
	 */
	public void addOutput(String content) throws ModelException;

	/**
	 * Convenience add method for creating a simple input.
	 * If you need
	 * to specify additional information about the input, such as valid
	 * values, use createInput, populate the object, then use add.
	 */
	public void addInput(String name, String label) throws ModelException;

	/**
	 * Convenience add method for creating a simple command
	 * If you need
	 * to specify additional information about the command, such as parameters
	 * use createCommand, populate the object, then use add.
	 */
	public void addCommand(String model, String label) throws ModelException;

	/**
	 * Create an Output with the specified name
	 */
	public Output createOutput(String name) throws ModelException;

	/**
	 * Create an output with the specified name and content
	 */
	public Output createOutput(String name, String content) throws ModelException;

	/**
	 * Create an Input with the specified name
	 */
	public Input createInput(String name) throws ModelException;

	/**
	 * Create a Command with the specified name
	 */
	public Command createCommand(String model) throws ModelException;

	public Command createCommandRelativeSequence(int numberOfSteps) throws ModelException;

	/**
	 * Add an error with a specific name. This name may be used to associate the error
	 * with a certain UI element (such as an Input), so the user can see immediately
	 * what was wrong when the error is displayed.
	 *
	 * @param errorName A name for this error message, which may (or may not) correspond
	 * to the UI element from which the error originated.
	 * @param errorMessage A human-readable error message to be displayed to the user. Messages
	 * beginning with "$" will be taken to be internationalized keys to look up in the
	 * appropriate message bundle for this locale.
	 */
	public void addError(String errorName, String errorMessage);

	/**
	 * Just like addError above, but including an actual throwable with details about the error
	 * @param errorName A name for this error message, which may (or may not) correspond
	 * to the UI element from which the error originated.
	 * @param errorMessage A human-readable error message to be displayed to the user. Messages
	 * beginning with "$" will be taken to be internationalized keys to look up in the
	 * appropriate message bundle for this locale.
	 * @param t A Throwable that caused this error to be recorded.
	 */
	public void addError(String errorName, String errorMessage, Throwable t);

	/**
	 * Just like addError above, but including a throwable but no message. The
	 * getMessage() method of the throwable will be used to determine the message displayed.
	 * @param errorName A name for this error message, which may (or may not) correspond
	 * to the UI element from which the error originated.
	 * @param t A Throwable that caused this error to be recorded.
	 */
	public void addError(String errorName, Throwable t);

	/**
	 * Add a whole set of errors to the current error set. The map is assumed to contain
	 * an error name followed by an error message or a throwable.
	 */
	public void addErrors(Map errors);

	/**
	 * Return a map of error names and error messages. Use getStackTrace to get the
	 * corresponding stack trace of the throwable recorded with this error, if any.
	 * @return A map of error names and messages
	 */
	public Map getErrors();

	/**
	 * Clear the current errors Map
	 */
	public void clearErrors();

	/**
	 * If there is a stack trace related to the error, it can
	 * be accessed via this method. Each error has an error key - this
	 * same key is used here to request the corresponding
	 * stack trace, if any
	 * @param errorName The name of the error, as used when addError logged the error originally
	 * @returns A string containing the stacktrace recorded from the throwable for this error,
	 * if one was specified.
	 */
	public String getStackTrace(String errorName);

	/**
	 * Get the type of the specified error, where the type is the classname
	 * of the exception contained in the error. May be null if no exception
	 * was included in the error.
	 * @param errorName Name of the error to be checked
	 * @return The classname of the exception embedded in the given error.
	 */
	public String getErrorType(String errorName);

	/**
	 * Used *only* by the ModelRequest to associate the
	 * originating request with the response.
	 */
	void setRequest(ModelRequest req);

	/**
	 * This method is used to facilitate a "repeat request"
	 * where the default values of Inputs are set from the
	 * values entered in the previous request by the user.
	 * This method sets the default value for each input
	 * found in the current request to the value of the
	 * parameter with the same name from the *previous*
	 * ModelRequest.
	 */
	public void setDefaultsFromPrevious();

	/**
	 * Set the value of an attribute of this response
	 */
	public void setAttribute(String key, Object value);

	public void removeAttribute(String key);

	public Object getAttribute(String key);

	public Map getAttributes();

	public byte[] serialize() throws IOException;

	public KeelResponse deserialize(byte[] bytes) throws IOException;

	public Throwable getThrowable(String oneKey);
}
