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


import de.iritgo.aktera.model.ResponseElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Top-level abstract class for common behaviour among all other default
 * response elements.
 */
public class AbstractResponseElement implements ResponseElement
{
	private String name = null;

	private List nestedElements = new ArrayList();

	private Map attributes = new LinkedHashMap();

	public void remove(ResponseElement element)
	{
		if (nestedElements != null)
		{
			nestedElements.remove(element);
		}
	}

	public void removeAttribute(String key)
	{
		if (attributes != null)
		{
			attributes.remove(key);
		}
	}

	/*
	 * Add a nested response element
	 */
	public void add(ResponseElement re)
	{
		if (re == null)
		{
			return;
		}
		assert re != this;

		if (nestedElements == null)
		{
			nestedElements = new ArrayList();
		}

		synchronized (nestedElements)
		{
			nestedElements.add(re);
		}
	}

	public void setAttribute(String key, Object value)
	{
		if (key == null)
		{
			return;
		}

		if (key != null)
		{
			synchronized (attributes)
			{
				attributes.put(key, value);
			}
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
			returnValue = new LinkedHashMap();
		}
		else
		{
			returnValue = attributes;
		}

		return returnValue;
	}

	public void setName(String newName)
	{
		//assert newName != null;
		//This failed to compile on my rig. Changing to old style for now. -
		// ACR
		if (newName == null)
		{
			throw new IllegalArgumentException("A ResponseElement name cannot be null.");
		}

		name = newName;
	}

	public String getName()
	{
		String returnValue = null;

		if (name == null)
		{
			returnValue = "none";
		}
		else
		{
			returnValue = name;
		}

		return returnValue;
	}

	public List getAll()
	{
		return nestedElements;
	}

	public String toString()
	{
		StringBuffer ret = new StringBuffer("\t<" + getClass().getName() + " name='" + name + "'>");

		if (attributes.size() > 0)
		{
			ret.append("\n\t<attributes>\n");

			for (Iterator a = attributes.keySet().iterator(); a.hasNext();)
			{
				String oneKey = (String) a.next();

				ret.append("\t\t<attribute key='" + oneKey + "' value='" + attributes.get(oneKey).toString()
								+ "' type='" + attributes.get(oneKey).getClass().getName() + "'/>\n");
			}

			ret.append("\t</attributes>\n");
		}

		if (nestedElements.size() > 0)
		{
			ret.append("\t<nested>\n");

			for (Iterator i = nestedElements.iterator(); i.hasNext();)
			{
				ResponseElement oneElement = (ResponseElement) i.next();

				ret.append("\t" + oneElement.toString());
			}

			ret.append("\t</nested>\n");
		}

		return ret.toString();
	}
}
