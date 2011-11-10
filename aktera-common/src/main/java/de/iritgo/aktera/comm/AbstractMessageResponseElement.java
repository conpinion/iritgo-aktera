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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class is the superclass for all ResponseElement implementations in this
 * package. It provides the attribute and nested element handling that all response elements
 * have in common, and checks the parameters to ensure they are serializable versions
 */
public class AbstractMessageResponseElement implements ResponseElement, Serializable
{
	private String name = null;

	private List nestedElements = new ArrayList();

	private Map attributes = null;

	public void removeAttribute(String key)
	{
		if (attributes != null)
		{
			synchronized (attributes)
			{
				attributes.remove(key);
			}
		}
	}

	/* Add a nested response element
	 */
	public void add(ResponseElement re)
	{
		if (re == null)
		{
			return;
		}

		if (nestedElements == null)
		{
			nestedElements = new ArrayList();
		}

		if (! (re instanceof AbstractMessageResponseElement))
		{
			throw new IllegalArgumentException("Nested element '" + re.getName() + "' was of type '"
							+ re.getClass().getName() + "'");
		}

		synchronized (nestedElements)
		{
			nestedElements.add(re);
		}
	}

	public void remove(ResponseElement re)
	{
		if (re == null)
		{
			return;
		}

		if (nestedElements != null)
		{
			synchronized (nestedElements)
			{
				nestedElements.remove(re);
			}
		}
	}

	public void setAttribute(String key, Object value)
	{
		if (key == null)
		{
			return;
		}

		if (attributes == null)
		{
			attributes = new HashMap();
		}

		if (value == null)
		{
			return;
		}

		if (! (value instanceof Serializable))
		{
			throw new IllegalArgumentException("Attribute '" + key + "' was not serializable. It was of type '"
							+ value.getClass().getName() + "'");
		}

		if (value instanceof ResponseElement)
		{
			if (! (value instanceof AbstractMessageResponseElement))
			{
				throw new IllegalArgumentException("Attribute '" + key + "' was a '" + value.getClass().getName() + "'");
			}
		}

		synchronized (attributes)
		{
			attributes.put(key, value);
		}
	}

	public Object getAttribute(String key)
	{
		Object returnValue = null;

		if (key != null)
		{
			if (attributes != null)
			{
				returnValue = attributes.get(key);
			}
		}

		return returnValue;
	}

	public Map getAttributes()
	{
		Map returnValue = null;

		if (attributes == null)
		{
			returnValue = new HashMap();
		}
		else
		{
			returnValue = attributes;
		}

		return returnValue;
	}

	public void setName(String newName)
	{
		name = newName;
	}

	public String getName()
	{
		return name;
	}

	public List getAll()
	{
		return nestedElements;
	}

	protected void copyFrom(ResponseElement re)
	{
		if (re == null)
		{
			return;
		}

		List nested = re.getAll();

		if (nested != null)
		{
			ResponseElement oneNested = null;

			for (Iterator in = nested.iterator(); in.hasNext();)
			{
				oneNested = (ResponseElement) in.next();

				if (oneNested instanceof Command)
				{
					add(new CommandMessage((Command) oneNested));
				}
				else if (oneNested instanceof Input)
				{
					add(new InputMessage((Input) oneNested));
				}
				else if (oneNested instanceof Output)
				{
					add(new OutputMessage((Output) oneNested));
				}
				else
				{
					throw new IllegalArgumentException("Nested element " + oneNested.getName() + " of element "
									+ getName() + " is not a valid type. It is " + oneNested.getClass().getName());
				}
			}
		}

		Map attribs = re.getAttributes();
		String oneAttribKey = null;
		Object oneAttribValue = null;

		for (Iterator ia = attribs.keySet().iterator(); ia.hasNext();)
		{
			oneAttribKey = (String) ia.next();
			oneAttribValue = attribs.get(oneAttribKey);

			if (oneAttribValue == null)
			{
				//Check for null, other wise into error case,
				//which throws a NullPointerExcepton on getClass()
				setAttribute(oneAttribKey, null);
			}

			//BUG: WAY too many expensive instanceof checks here!!! 
			//Can we implement a routine to kick back what type of
			//class oneAttribValue is?
			else if (oneAttribValue instanceof Command)
			{
				setAttribute(oneAttribKey, new CommandMessage((Command) oneAttribValue));
			}
			else if (oneAttribValue instanceof Input)
			{
				setAttribute(oneAttribKey, new InputMessage((Input) oneAttribValue));
			}
			else if (oneAttribValue instanceof Output)
			{
				setAttribute(oneAttribKey, new OutputMessage((Output) oneAttribValue));
			}
			else if (oneAttribValue instanceof Serializable)
			{
				setAttribute(oneAttribKey, oneAttribValue);
			}
			else
			{
				throw new IllegalArgumentException("Attribute '" + oneAttribKey + "' of element '" + getName()
								+ "' is not serializable. It is a '" + oneAttribValue.getClass().getName() + "'");
			}
		}
	}
}
