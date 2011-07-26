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

package de.iritgo.aktera.comm;


import de.iritgo.aktera.core.exception.NestedException;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ModelResponseMessage implements ModelResponse, Serializable
{
	private static String PREFIX = "[ModelResponseMessage] ";

	private Map attributes = null;

	private transient short errorDupCount = 1;

	/**
	 * Map of the ResponseElement entries this response contains
	 */
	private Map elements = new HashMap ();

	private List elementList = new ArrayList ();

	private Map errors = new HashMap ();

	private Map errorTypes = new HashMap ();

	private Map stackTraces = new HashMap ();

	private transient ModelResponse originalRes = null;

	public void removeAttribute (String key)
	{
		if (attributes != null)
		{
			synchronized (attributes)
			{
				attributes.remove (key);
			}
		}
	}

	public void addOutput (String name, String content) throws ModelException
	{
		Output newOutput = createOutput (name);

		newOutput.setContent (content);
		add (newOutput);
	}

	private void notImplemented ()
	{
		throw new IllegalArgumentException ("Not implemented in ModelResponseMessage");
	}

	public void addOutput (String content) throws ModelException
	{
		notImplemented ();
	}

	public Output createOutput (String name) throws ModelException
	{
		notImplemented ();

		return null;
	}

	public Output createOutput (String name, String content) throws ModelException
	{
		notImplemented ();

		return null;
	}

	public Input createInput (String name) throws ModelException
	{
		notImplemented ();

		return null;
	}

	public void addInput (String name, String label) throws ModelException
	{
		notImplemented ();
	}

	public Command createCommand (String model) throws ModelException
	{
		notImplemented ();

		return null;
	}

	public Command createCommandRelativeSequence (int numberOfSteps) throws ModelException
	{
		notImplemented ();

		return null;
	}

	public void addCommand (String model, String label) throws ModelException
	{
		notImplemented ();
	}

	/**
	 * Return a specific element
	 */
	public ResponseElement get (String elementLocator)
	{
		return (ResponseElement) elements.get (elementLocator);
	}

	/**
	 * Remove a specific element
	 */
	public void remove (ResponseElement re)
	{
		if (elements != null)
		{
			synchronized (elements)
			{
				elements.remove (re);
			}
		}
	}

	/**
	 * Return an iterator over all of the ResponseElements in this response
	 */
	public Iterator getAll ()
	{
		return elementList.iterator ();
	}

	/**
	 * The Model (only) uses the method below to add new response elements to
	 * this response.
	 */
	public void add (ResponseElement re) throws ModelException
	{
		if (re != null)
		{
			if (elements == null)
			{
				elements = new HashMap ();
			}

			if (! (re instanceof AbstractMessageResponseElement))
			{
				throw new ModelException ("Cannot add a '" + re.getClass ().getName () + "' here");
			}

			String name = re.getName ();

			if (name == null)
			{
				int count = elements.size () + 1;

				name = "" + count;
			}

			synchronized (elements)
			{
				elements.put (name, re);
			}

			elementList.add (re);
		}
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

	public void addError (String errorName, String errorMessage)
	{
		ModelException me = new ModelException ("Application Error");

		me.fillInStackTrace ();

		addError (errorName, me);
	}

	public void addError (String errorName, String errorMessage, Throwable t)
	{
		String errorNameToUse = uniqueErrorName (errorName);

		synchronized (errors)
		{
			errors.put (errorNameToUse, errorMessage);
		}

		StringWriter stw = new StringWriter ();
		PrintWriter pw = new PrintWriter (stw);

		t.printStackTrace (pw);

		synchronized (stackTraces)
		{
			stackTraces.put (errorNameToUse, stw.getBuffer ().toString ());
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

	public void addError (String errorName, Throwable t)
	{
		addError (errorName, t.getMessage (), t);
	}

	/**
	 * Add an entire Map of errors at once
	 *
	 * @deprecated This method doesn't support sending the stack trace with the
	 *             error, which is usually very helpful. Use addError instead
	 *             for each error.
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

	//Added by Santanu Dutt to clear the current errors
	public void clearErrors ()
	{
		errors.clear ();
		stackTraces.clear ();
	}

	public Map getErrors ()
	{
		return errors;
	}

	public String getErrorType (String errorName)
	{
		return (String) errorTypes.get (errorName);
	}

	public String getStackTrace (String errorName)
	{
		return (String) stackTraces.get (errorName);
	}

	public void setRequest (ModelRequest req)
	{
		if (req != null)
		{
			if (! (req instanceof Serializable))
			{
				throw new IllegalArgumentException ("Request is not serializable - it is " + req.getClass ().getName ());
			}
		}
	}

	public void setDefaultsFromPrevious ()
	{
		notImplemented ();
	}

	public void addThrowables (Map m)
	{
		if (m == null)
		{
			return;
		}

		String oneKey = null;

		for (Iterator i = m.keySet ().iterator (); i.hasNext ();)
		{
			oneKey = (String) i.next ();

			Throwable t = (Throwable) m.get (oneKey);

			addError (oneKey, t.getMessage (), t);
		}
	}

	public Object getAttribute (String key)
	{
		Object returnValue = null;

		if (key != null)
		{
			if (attributes != null)
			{
				returnValue = attributes.get (key);
			}
		}

		return returnValue;
	}

	public Map getAttributes ()
	{
		Map returnValue = null;

		if (attributes == null)
		{
			returnValue = new HashMap ();
		}
		else
		{
			returnValue = attributes;
		}

		return returnValue;
	}

	public void setAttribute (String key, Object value)
	{
		if (attributes == null)
		{
			attributes = new HashMap ();
		}

		if (value == null)
		{
			return;
		}

		if (! (value instanceof Serializable))
		{
			throw new IllegalArgumentException ("Attribute '" + key + "' not serializable");
		}

		if (key != null)
		{
			synchronized (attributes)
			{
				attributes.put (key, value);
			}
		}
	}

	public void copyFrom (ModelResponse res) throws ModelException
	{
		assert res != null;
		originalRes = res;

		ResponseElement oneElement = null;

		for (Iterator ie = res.getAll (); ie.hasNext ();)
		{
			oneElement = (ResponseElement) ie.next ();

			if (oneElement instanceof Command)
			{
				add (new CommandMessage ((Command) oneElement));
			}
			else if (oneElement instanceof Input)
			{
				add (new InputMessage ((Input) oneElement));
			}
			else if (oneElement instanceof Output)
			{
				add (new OutputMessage ((Output) oneElement));
			}
			else
			{
				throw new ModelException ("Element " + oneElement.getName () + " is of an unknown type: "
								+ oneElement.getClass ().getName () + "'");
			}
		}

		/* Attributes must be either ResponseElements, or basic Java types */
		String oneAttribName = null;
		Object oneAttribValue = null;

		for (Iterator ia = res.getAttributes ().keySet ().iterator (); ia.hasNext ();)
		{
			oneAttribName = (String) ia.next ();
			oneAttribValue = res.getAttribute (oneAttribName);

			if (oneAttribValue instanceof Command)
			{
				setAttribute (oneAttribName, new CommandMessage ((Command) oneAttribValue));
			}
			else if (oneAttribValue instanceof Input)
			{
				setAttribute (oneAttribName, new InputMessage ((Input) oneAttribValue));
			}
			else if (oneAttribValue instanceof Output)
			{
				setAttribute (oneAttribName, new OutputMessage ((Output) oneAttribValue));
			}
			else if (oneAttribValue.getClass ().getName ().startsWith ("java.lang"))
			{
				setAttribute (oneAttribName, oneAttribValue);
			}
			else if (oneAttribValue.getClass ().getName ().startsWith ("java.util"))
			{
				setAttribute (oneAttribName, oneAttribValue);
			}
			else
			{
				throw new ModelException ("Attribute '" + oneAttribName + "' of Class '"
								+ oneAttribValue.getClass ().getName ()
								+ "' is not allowed as a ModelResponse attribute");
			}
		}

		Map errs = res.getErrors ();
		String oneErrorName = null;

		for (Iterator i = errs.keySet ().iterator (); i.hasNext ();)
		{
			oneErrorName = (String) i.next ();
			addError (oneErrorName, (String) errs.get (oneErrorName), res.getThrowable (oneErrorName));
		}
	}

	public byte[] serialize () throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		ObjectOutputStream oos = new ObjectOutputStream (baos);

		try
		{
			oos.writeObject (this);
		}
		catch (NotSerializableException ne)
		{
			System.err.println (PREFIX + "Error serializing this ModelResponseMessage (" + this.getClass ().getName ()
							+ "):");
			System.err.println (this.toString ());
			ne.printStackTrace (System.err);
			throw new IOException ("Model Response could not be serialized");
		}

		return baos.toByteArray ();
	}

	public KeelResponse deserialize (byte[] bytes) throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream (bytes);
		ObjectInputStream ois = new ObjectInputStream (bais);
		KeelResponse myObject = null;

		try
		{
			myObject = (ModelResponseMessage) ois.readObject ();
		}
		catch (ClassNotFoundException e)
		{
			System.err.println (PREFIX + "Class not found while de-serializing response:");
			e.printStackTrace (System.err);

			throw new IOException (e.getMessage ());
		}

		return myObject;
	}

	/**
	 * We don't store the actual Throwable in a ModelRequestMessage, only
	 * the stacktrace.
	 * @param oneKey The error message key of the error for which the corresponding Throwable
	 * was requested.
	 * @return Nothing. This method always throws an exception.
	 * @throws IllegalArgumentException We don't store the throwables.
	 */
	public Throwable getThrowable (String oneKey)
	{
		throw new IllegalArgumentException (
						"Cannot retrieve throwables from a ModeResponseMessage: Use getStackTrace(String) instead");
	}

	/**
	 * toString for a ModelRequestMessage just passes the request
	 * on to the underlying ModelRequest.
	 * @return A string representation of this class
	 */
	public String toString ()
	{
		return originalRes.toString ();
	}
}
