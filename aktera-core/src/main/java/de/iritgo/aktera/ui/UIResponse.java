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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import java.util.Iterator;
import java.util.Map;


public interface UIResponse
{
	/**
	 * Set the controller forward.
	 *
	 * @param forward The forward name
	 */
	public void setForward (String forward);

	/**
	 * Get the controller forward.
	 *
	 * @return The forward name
	 */
	public String getForward ();

	/**
	 * Get a response element.
	 *
	 * @param name The element name.
	 */
	public ResponseElement get (String name);

	/**
	 * Remove a response element.
	 *
	 * @param name The name of the element to remove
	 */
	public void remove (String name);

	/**
	 * Remove a response element.
	 *
	 * @param element The element to remove
	 */
	public void remove (ResponseElement element);

	/**
	 * Return an iterator over all of the ResponseElements in this response.
	 *
	 * @return An element iterator
	 */
	public Iterator getAll ();

	/**
	 * Get the map of reponse elements.
	 *
	 * @return The response elements
	 */
	public Map<String, ResponseElement> getElements ();

	/**
	 * Add a response element.
	 *
	 * @param element The element to add
	 */
	public void add (ResponseElement element);

	/**
	 * Create an output response element.
	 *
	 * @param name The element name
	 * @return The output response element
	 */
	public Output createOutput (String name);

	/**
	 * Create an output response element.
	 *
	 * @param name The element name
	 * @param content The element content
	 * @return The output response element
	 */
	public Output createOutput (String name, String content);

	/**
	 * Create an input response element.
	 *
	 * @param name The element name
	 * @return The input response element
	 */
	public Input createInput (String name);

	/**
	 * Create a command response element.
	 *
	 * @param name The element name
	 * @param bean The bean name
	 * @return The command response element
	 */
	public Command createCommand (String name, String bean);

	/**
	 * Create a command response element.
	 *
	 * @param name The element name
	 * @param bean The bean name
	 * @param label The command label
	 * @return The command response element
	 */
	public Command createCommand (String name, String bean, String label);

	/**
	 * Create and add an output response element.
	 *
	 * @param name The element name
	 */
	public Output addOutput (String name);

	/**
	 * Create and add an output response element.
	 *
	 * @param name The element name
	 * @param content The element content
	 */
	public Output addOutput (String name, String content);

	/**
	 * Create and add an input response element.
	 *
	 * @param name The element name
	 * @return The new input object
	 */
	public Input addInput (String name);

	/**
	 * Create and add an input response element.
	 *
	 * @param name The element name
	 * @param label The element label
	 * @return The new input object
	 */
	public Input addInput (String name, String label);

	/**
	 * Create and add a command response element.
	 *
	 * @param name The element name
	 * @param bean The bean name
	 */
	public Command addCommand (String name, String bean);

	/**
	 * Create and add a command response element.
	 *
	 * @param name The element name
	 * @param bean The bean name
	 * @param label The element label
	 */
	public Command addCommand (String name, String bean, String label);

	/**
	 * Create and add a command response element.
	 *
	 * @param name The element name
	 * @param model The model name
	 */
	public Command addCommandWithModel (String name, String model);

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
	public void addError (String errorName, String errorMessage);

	/**
	 * Just like addError above, but including an actual throwable with details about the error
	 * @param errorName A name for this error message, which may (or may not) correspond
	 * to the UI element from which the error originated.
	 * @param errorMessage A human-readable error message to be displayed to the user. Messages
	 * beginning with "$" will be taken to be internationalized keys to look up in the
	 * appropriate message bundle for this locale.
	 * @param t A Throwable that caused this error to be recorded.
	 */
	public void addError (String errorName, String errorMessage, Throwable t);

	/**
	 * Just like addError above, but including a throwable but no message. The
	 * getMessage() method of the throwable will be used to determine the message displayed.
	 * @param errorName A name for this error message, which may (or may not) correspond
	 * to the UI element from which the error originated.
	 * @param t A Throwable that caused this error to be recorded.
	 */
	public void addError (String errorName, Throwable t);

	/**
	 * Add a whole set of errors to the current error set. The map is assumed to contain
	 * an error name followed by an error message or a throwable.
	 */
	public void addErrors (Map errors);

	/**
	 * Return a map of error names and error messages. Use getStackTrace to get the
	 * corresponding stack trace of the throwable recorded with this error, if any.
	 * @return A map of error names and messages
	 */
	public Map getErrors ();

	/**
	 * Clear the current errors Map
	 */
	public void clearErrors ();

	/**
	 * If there is a stack trace related to the error, it can
	 * be accessed via this method. Each error has an error key - this
	 * same key is used here to request the corresponding
	 * stack trace, if any
	 * @param errorName The name of the error, as used when addError logged the error originally
	 * @returns A string containing the stacktrace recorded from the throwable for this error,
	 * if one was specified.
	 */
	public String getStackTrace (String errorName);

	/**
	 * Get the type of the specified error, where the type is the classname
	 * of the exception contained in the error. May be null if no exception
	 * was included in the error.
	 * @param errorName Name of the error to be checked
	 * @return The classname of the exception embedded in the given error.
	 */
	public String getErrorType (String errorName);

	/**
	 * Describe method getThrowable() here.
	 *
	 * @param oneKey
	 * @return
	 */
	public Throwable getThrowable (String oneKey);
}
