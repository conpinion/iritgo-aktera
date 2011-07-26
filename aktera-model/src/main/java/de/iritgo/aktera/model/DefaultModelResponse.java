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


import java.io.*;
import java.util.*;
import de.iritgo.aktera.core.exception.*;
import de.iritgo.aktera.models.util.*;


/**
 * Default implementation of the ModelResponse interface.
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.ModelResponse
 * @x-avalon.info name=default-response
 * @x-avalon.lifestyle type=transient
 *
 * @version                $Revision: 1.1 $  $Date: 2005/05/23 00:57:26 $
 */
public class DefaultModelResponse implements ModelResponse
{
	/**
	 * The request that initiated this response. A reference
	 * is held here so that we can refer to elements
	 * only stored in the request, such as the previous request
	 * for setting defaults on inputs
	 */
	protected transient ModelRequest myRequest = null;

	private Map attributes = null;

	/**
	 * Map of the ResponseElement entries this response contains
	 */
	private Map elements = new LinkedHashMap ();

	private transient int nextName = 0;

	private Map errors = new LinkedHashMap ();

	private Map errorTypes = new LinkedHashMap ();

	private Map throwables = new LinkedHashMap ();

	private transient short errorDupCount = 1;

	public void removeAttribute (String key)
	{
		assert key != null;

		if (attributes != null)
		{
			synchronized (attributes)
			{
				attributes.remove (key);
			}
		}
	}

	public String getErrorType (String errorName)
	{
		return (String) errorTypes.get (errorName);
	}

	public void addOutput (String name, String content) throws ModelException
	{
		Output newOutput = createOutput (name);

		newOutput.setContent (content);
		add (newOutput);
	}

	public void addOutput (String content) throws ModelException
	{
		String assignedName = "output_" + nextName;

		nextName++;
		addOutput (assignedName, content);
	}

	public Output createOutput (String name) throws ModelException
	{
		String oName = name;

		if (oName == null)
		{
			oName = "output_" + nextName;
			nextName++;
		}

		Output newOutput = null;

		try
		{
			newOutput = new DefaultOutput ();
			newOutput.setName (oName);

			return newOutput;
		}
		catch (Exception ce)
		{
			throw new ModelException (ce);
		}
	}

	public Output createOutput (String name, String content) throws ModelException
	{
		Output newOutput = createOutput (name);

		newOutput.setContent (content);

		return newOutput;
	}

	public Input createInput (String name) throws ModelException
	{
		assert name != null;

		Input newInput = null;

		try
		{
			newInput = new DefaultInput ();
			newInput.setName (name);

			return newInput;
		}
		catch (Exception ce)
		{
			throw new ModelException (ce);
		}
	}

	public void addInput (String name, String label) throws ModelException
	{
		Input newInput = createInput (name);
		assert label != null;
		newInput.setLabel (label);
		add (newInput);
	}

	/**
	 * This will create a new Command object relative to the current sequence.
	 * This allows movement within a sequence relative to the current step.
	 * The number of steps specifies how many steps to go forward or backward. For
	 * example, to go back three steps, you would call this method as follows:
	 * createCommandRelativeSequence(-3);
	 *
	 *
	 */
	public Command createCommandRelativeSequence (int numberOfSteps) throws ModelException
	{
		Command newCommand = null;
		String seqName = null;
		String seqNumber = null;
		int nextSeqNumber = 0;

		seqName = (String) myRequest.getParameter (Sequence.SEQUENCE_NAME);
		seqNumber = (String) myRequest.getParameter (Sequence.SEQUENCE_NUMBER);

		if ((seqName == null) || (seqNumber == null))
		{
			throw new ModelException (
							"This model was instructed to go to the next in sequence, but the current sequence could not be determined.");
		}

		nextSeqNumber = Integer.parseInt (seqNumber);
		nextSeqNumber = nextSeqNumber + numberOfSteps;

		if (nextSeqNumber < 1)
		{
			nextSeqNumber = 1; //for safety
		}

		try
		{
			newCommand = new DefaultCommand ();
			newCommand.setName ("command_" + nextName);
			nextName++;
			newCommand.setModel (seqName);
			newCommand.setParameter ("seq", "" + nextSeqNumber);

			return newCommand;
		}
		catch (Exception ce)
		{
			throw new ModelException (ce);
		}
	}

	/**
	 * This will create a new Command object for the specified model.
	 *
	 */
	public Command createCommand (String model) throws ModelException
	{
		Command newCommand = null;

		try
		{
			newCommand = new DefaultCommand ();
			newCommand.setName ("command_" + nextName);
			nextName++;
			newCommand.setModel (model);

			return newCommand;
		}
		catch (Exception ce)
		{
			throw new ModelException (ce);
		}
	}

	public void addCommand (String model, String label) throws ModelException
	{
		Command newCommand = createCommand (model);

		newCommand.setLabel (label);
		add (newCommand);
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
	public void remove (String elementLocator)
	{
		synchronized (elements)
		{
			elements.remove (elementLocator);
		}
	}

	/**
	 * Return an iterator over all of the ResponseElements in this response
	 */
	public Iterator getAll ()
	{
		return elements.values ().iterator ();
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
				elements = new LinkedHashMap ();
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
		}
	}

	public void remove (ResponseElement re)
	{
		if (elements != null)
		{
			synchronized (elements)
			{
				elements.remove (re.getName ());
			}
		}
	}

	public void addError (String errorName, String errorMessage)
	{
		assert errorName != null;

		ModelException ne = new ModelException ("Application Error");

		ne.fillInStackTrace ();

		/* Check to make sure there isn't already an identical throwable */
		/* registered for this error */
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

	public void addError (String errorName, String errorMessage, Throwable t)
	{
		String errorNameToUse = uniqueErrorName (errorName);

		if ((errorMessage == null) || errorMessage.equals (""))
		{
			errorMessage = "No Message Supplied";
		}

		String existing = (String) errors.get (errorNameToUse);

		/* If there was no entry under this key, or there was but */
		/* it's not the same as this one, add it */
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

			/* If there either was no existing throwable under this key */
			/* or there was, but it was not the same, go ahead and add */
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

	public void addError (String errorName, Throwable t)
	{
		addError (errorName, t.getMessage (), t);
	}

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
		synchronized (errors)
		{
			errors.clear ();
		}
	}

	public Map getErrors ()
	{
		return errors;
	}

	public Throwable getThrowable (String oneKey)
	{
		assert oneKey != null;

		return (Throwable) throwables.get (oneKey);
	}

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

	public void setRequest (ModelRequest req)
	{
		assert req != null;
		myRequest = req;
	}

	public void setDefaultsFromPrevious ()
	{
		Map params = myRequest.getPreviousRequest ();

		if (params == null)
		{
			return;
		}

		String oneParamName = null;
		ResponseElement oneElement = null;

		for (Iterator i = params.keySet ().iterator (); i.hasNext ();)
		{
			oneParamName = (String) i.next ();

			oneElement = (ResponseElement) elements.get (oneParamName);

			if (oneElement != null)
			{
				if (oneElement instanceof Input)
				{
					Input oneInput = (Input) oneElement;

					oneInput.setDefaultValue (params.get (oneParamName));
				}
			}
		}
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
		if (key != null)
		{
			if (attributes != null)
			{
				return attributes.get (key);
			}
		}

		return null;
	}

	public Map getAttributes ()
	{
		if (attributes == null)
		{
			return new HashMap ();
		}

		return attributes;
	}

	public void setAttribute (String key, Object value)
	{
		assert key != null;
		assert value != null;

		if (attributes == null)
		{
			attributes = new LinkedHashMap ();
		}

		if (key != null)
		{
			synchronized (attributes)
			{
				attributes.put (key, value);
			}
		}
	}

	public String toString ()
	{
		StringBuffer out = new StringBuffer ();

		out.append ("<DefaultModelResponse type='" + this.getClass ().getName () + "' model='" + myRequest.getModel ()
						+ "'>\n");

		ResponseElement oneElement = null;

		for (Iterator i = getAll (); i.hasNext ();)
		{
			oneElement = (ResponseElement) i.next ();
			out.append ("\t" + oneElement.toString ());
		}

		out.append ("\t<attributes>\n");

		String oneAttribName = null;

		for (Iterator j = getAttributes ().keySet ().iterator (); j.hasNext ();)
		{
			oneAttribName = (String) j.next ();
			out.append ("\t\t<attribute name='" + oneAttribName + "' ");

			Object o = getAttribute (oneAttribName);

			out.append (" type='" + o.getClass ().getName () + "' value='");
			out.append (o.toString () + "/>/n");
		}

		out.append ("\t</attributes>\n");

		out.append ("\n\t<errors>");

		for (Iterator ji = errors.keySet ().iterator (); ji.hasNext ();)
		{
			String oneKey = (String) ji.next ();

			out.append ("\t\t<error name='" + oneKey + "' message='" + (String) errors.get (ji) + "'/>\n");
		}

		out.append ("\t</errors>");

		out.append ("\n\t<error-types>");

		for (Iterator jit = errorTypes.keySet ().iterator (); jit.hasNext ();)
		{
			String oneKey = (String) jit.next ();

			out.append ("\t\t<error key='" + oneKey + "' type='" + (String) errorTypes.get (oneKey) + "'/>");
		}

		out.append ("\n\t</error-types>");

		out.append ("\n\t<throwables>");

		for (Iterator ti = throwables.keySet ().iterator (); ti.hasNext ();)
		{
			String oneKey = (String) ti.next ();

			out.append ("\t\t<throwable key='" + oneKey + "' throwable='" + throwables.get (oneKey).toString ()
							+ "'/>\n");
		}

		out.append ("\t</error-types>\n");

		out.append ("</DefaultModelResponse>\n");

		return out.toString ();
	}

	public byte[] serialize () throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		ObjectOutputStream oos = new ObjectOutputStream (baos);

		oos.writeObject (this);

		return baos.toByteArray ();
	}

	public KeelResponse deserialize (byte[] bytes) throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream (bytes);
		ObjectInputStream ois = new ObjectInputStream (bais);
		KeelResponse myObject;

		try
		{
			myObject = (ModelResponse) ois.readObject ();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException (e.getMessage ());
		}

		return myObject;
	}
}
