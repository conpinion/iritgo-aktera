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

package de.iritgo.aktera.util.i18n;


import java.io.Serializable;


/**
 * An adaptation of the ActionMessage class from Struts for use with
 * multiple frameworks. Allows a message key and a number of parameters
 * for replacement to be bundled together. The translation into a "presentation-ready" string is always done by the client (e.g. Struts, Cocoon, etc), not
 * on the server side of Keel.
 *
 * @author Craig R. McClanahan
 * @author David Winterfeldt
 * @author Michael Nash
 * @version $Revision: 1.1 $ $Date: 2003/12/29 07:01:32 $
 */
public class Message implements Serializable
{
	// ----------------------------------------------------- Instance Variables
	protected String resultString = null;

	/**
	 * The message key for this message.
	 */
	protected String key = null;

	/**
	 * The replacement values for this mesasge.
	 */
	protected Object[] values = null;

	/**
	 * The message bundle hint used to determine which bundle
	 * this message is found within
	 */
	protected String bundle = null;

	// ----------------------------------------------------------- Constructors

	/**
	 * Construct an action message with no replacement values.
	 *
	 * @param key Message key for this message
	 */
	public Message(String key)
	{
		this.key = key;
	}

	/**
	 * Construct an action message with the specified replacement values.
	 *
	 * @param key Message key for this message
	 * @param value0 First replacement value
	 */
	public Message(String key, Object value0)
	{
		this.key = key;
		values = new Object[]
		{
			value0
		};
	}

	/**
	 * Construct an action message with the specified replacement values.
	 *
	 * @param key Message key for this message
	 * @param value0 First replacement value
	 * @param value1 Second replacement value
	 */
	public Message(String key, Object value0, Object value1)
	{
		this.key = key;
		values = new Object[]
		{
						value0, value1
		};
	}

	/**
	 * Construct an action message with the specified replacement values.
	 *
	 * @param key Message key for this message
	 * @param value0 First replacement value
	 * @param value1 Second replacement value
	 * @param value2 Third replacement value
	 */
	public Message(String key, Object value0, Object value1, Object value2)
	{
		this.key = key;
		values = new Object[]
		{
						value0, value1, value2
		};
	}

	/**
	 * Construct an action message with the specified replacement values.
	 *
	 * @param key Message key for this message
	 * @param value0 First replacement value
	 * @param value1 Second replacement value
	 * @param value2 Third replacement value
	 * @param value3 Fourth replacement value
	 */
	public Message(String key, Object value0, Object value1, Object value2, Object value3)
	{
		this.key = key;
		values = new Object[]
		{
						value0, value1, value2, value3
		};
	}

	/**
	 * Construct an action message with the specified replacement values.
	 * This constructor is used for when we have more than 4 parameters
	 * to supply for replacement.
	 *
	 * @param key Message key for this message
	 * @param values Array of replacement values
	 */
	public Message(String key, Object[] values)
	{
		this.key = key;
		this.values = values;
	}

	public void setBundle(String newBundle)
	{
		bundle = newBundle;
	}

	public String getBundle()
	{
		return bundle;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Get the message key for this message.
	 */
	public String getKey()
	{
		return (key);
	}

	/**
	 * Get the replacement values for this message.
	 */
	public Object[] getValues()
	{
		return (values);
	}

	public void setValues(Object[] newValues)
	{
		values = newValues;
	}

	public synchronized void setResultString(String newString)
	{
		resultString = newString;
	}

	public String toString()
	{
		String returnValue = null;

		if (resultString != null)
		{
			returnValue = resultString;
		}
		else
		{
			returnValue = super.toString();
		}

		return returnValue;
	}
}
