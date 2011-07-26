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

package de.iritgo.aktera.struts;


import de.iritgo.aktera.core.exception.NestedException;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.DefaultCommand;
import de.iritgo.aktera.model.DefaultInput;
import de.iritgo.aktera.model.DefaultOutput;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.simplelife.string.StringTools;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class BeanResponse implements UIResponse
{
	/** The controller forward */
	private String forward;

	/** Response elements */
	private Map<String, ResponseElement> elements = new LinkedHashMap ();

	private Map errors = new LinkedHashMap ();

	private Map errorTypes = new LinkedHashMap ();

	private Map throwables = new LinkedHashMap ();

	private transient short errorDupCount = 1;

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#setForward(java.lang.String)
	 */
	public void setForward (String forward)
	{
		this.forward = forward;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getForward()
	 */
	public String getForward ()
	{
		return forward;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#get(java.lang.String)
	 */
	public ResponseElement get (String name)
	{
		return elements.get (name);
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#remove(java.lang.String)
	 */
	public void remove (String name)
	{
		synchronized (elements)
		{
			elements.remove (name);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#remove(de.iritgo.aktera.model.ResponseElement)
	 */
	public void remove (ResponseElement element)
	{
		remove (element.getName ());
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getAll()
	 */
	public Iterator<ResponseElement> getAll ()
	{
		return elements.values ().iterator ();
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getElements()
	 */
	public Map<String, ResponseElement> getElements ()
	{
		return elements;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#add(de.iritgo.aktera.model.ResponseElement)
	 */
	public void add (ResponseElement element)
	{
		assert element != null;
		assert StringTools.isNotTrimEmpty (element.getName ());

		synchronized (elements)
		{
			elements.put (element.getName (), element);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#createOutput(java.lang.String)
	 */
	public Output createOutput (String name)
	{
		assert StringTools.isNotTrimEmpty (name);

		Output output = new DefaultOutput ();

		output.setName (name);

		return output;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#createOutput(java.lang.String,
	 *      java.lang.String)
	 */
	public Output createOutput (String name, String content)
	{
		Output output = createOutput (name);

		output.setContent (content);

		return output;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#createInput(java.lang.String)
	 */
	public Input createInput (String name)
	{
		assert StringTools.isNotTrimEmpty (name);

		Input input = new DefaultInput ();

		input.setName (name);

		return input;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#createCommand(java.lang.String,
	 *      java.lang.String)
	 */
	public Command createCommand (String name, String bean)
	{
		assert StringTools.isNotTrimEmpty (name);
		Command command = new DefaultCommand ();
		command.setName (name);
		command.setBean (bean);
		command.setLabel (name);
		return command;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#createCommand(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Command createCommand (String name, String bean, String label)
	{
		assert StringTools.isNotTrimEmpty (name);

		Command command = new DefaultCommand ();

		command.setName (name);
		command.setBean (bean);
		command.setLabel (label);

		return command;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addOutput(java.lang.String)
	 */
	public Output addOutput (String name)
	{
		Output newOutput = createOutput (name);

		add (newOutput);

		return newOutput;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addOutput(java.lang.String,
	 *      java.lang.String)
	 */
	public Output addOutput (String name, String content)
	{
		Output newOutput = addOutput (name);

		newOutput.setContent (content);

		return newOutput;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addInput(java.lang.String)
	 */
	public Input addInput (String name)
	{
		return addInput (name, name);
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addInput(java.lang.String,
	 *      java.lang.String)
	 */
	public Input addInput (String name, String label)
	{
		Input newInput = createInput (name);
		assert label != null;
		newInput.setLabel (label);
		add (newInput);

		return newInput;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addCommand(java.lang.String,
	 *      java.lang.String)
	 */
	public Command addCommand (String name, String bean)
	{
		return addCommand (name, bean, name);
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addCommand(java.lang.String,
	 *      java.lang.String)
	 */
	public Command addCommand (String name, String bean, String label)
	{
		Command command = createCommand (name, bean);

		command.setLabel (label);
		add (command);

		return command;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addCommandWithModel(java.lang.String,
	 *      java.lang.String)
	 */
	public Command addCommandWithModel (String name, String model)
	{
		Command command = createCommand (name, null);

		command.setLabel (name);
		command.setModel (model);
		add (command);

		return command;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addError(java.lang.String, java.lang.String)
	 */
	public void addError (String errorName, String errorMessage)
	{
		assert errorName != null;

		ModelException ne = new ModelException ("Application Error");

		ne.fillInStackTrace ();

		Throwable existing = (Throwable) throwables.get (errorName);

		if ((existing != null) && (! existing.equals (ne)))
		{
			synchronized (throwables)
			{
				throwables.put (errorName, ne);
			}
		}

		addError (errorName, errorMessage, ne);
	}

	private String uniqueErrorName (String errorName)
	{
		String errorNameToUse = errorName;

		if (errors.containsKey (errorName))
		{
			errorNameToUse = errorNameToUse + errorDupCount;
			errorDupCount++;
		}

		return errorNameToUse;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addError(java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	public void addError (String errorName, String errorMessage, Throwable t)
	{
		String errorNameToUse = uniqueErrorName (errorName);

		if ((errorMessage == null) || errorMessage.equals (""))
		{
			errorMessage = "No Message Supplied";
		}

		String existing = (String) errors.get (errorNameToUse);

		if ((existing == null) || (existing.equals (errorMessage)))
		{
			synchronized (errors)
			{
				errors.put (errorNameToUse, errorMessage);
			}
		}

		if (t != null)
		{
			Throwable existingThrowable = (Throwable) throwables.get (errorNameToUse);

			if ((existingThrowable == null) || (! existingThrowable.equals (t)))
			{
				synchronized (throwables)
				{
					throwables.put (errorNameToUse, t);
				}
			}

			String errorType = t.getClass ().getName ();

			if (t instanceof NestedException)
			{
				errorType = ((NestedException) t).getNestedExceptionType ();
			}

			synchronized (errorTypes)
			{
				errorTypes.put (errorNameToUse, errorType);
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addError(java.lang.String,
	 *      java.lang.Throwable)
	 */
	public void addError (String errorName, Throwable t)
	{
		addError (errorName, t.getMessage (), t);
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#addErrors(java.util.Map)
	 */
	public void addErrors (Map newErrors)
	{
		if (newErrors == null)
		{
			return;
		}

		String oneKey = null;
		Object o = null;

		for (Iterator i = newErrors.keySet ().iterator (); i.hasNext ();)
		{
			oneKey = (String) i.next ();
			o = newErrors.get (oneKey);

			if (o instanceof Throwable)
			{
				Throwable t = (Throwable) o;

				addError (oneKey, t.getMessage (), t);
			}
			else if (o instanceof String)
			{
				addError (oneKey, (String) o);
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#clearErrors()
	 */
	public void clearErrors ()
	{
		synchronized (errors)
		{
			errors.clear ();
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getErrors()
	 */
	public Map getErrors ()
	{
		return errors;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getThrowable(java.lang.String)
	 */
	public Throwable getThrowable (String oneKey)
	{
		assert oneKey != null;

		return (Throwable) throwables.get (oneKey);
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getStackTrace(java.lang.String)
	 */
	public String getStackTrace (String errorName)
	{
		assert errorName != null;

		Throwable throwable = getThrowable (errorName);

		if (throwable != null)
		{
			StringWriter sw = new StringWriter ();
			PrintWriter pw = new PrintWriter (sw);

			throwable.printStackTrace (pw);

			return sw.toString ();
		}

		return null;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIResponse#getErrorType(java.lang.String)
	 */
	public String getErrorType (String errorName)
	{
		return (String) errorTypes.get (errorName);
	}
}
