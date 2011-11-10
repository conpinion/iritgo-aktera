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


import de.iritgo.aktera.model.Input;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 * @avalon.component @avalon.service type=de.iritgo.aktera.model.Input
 * @x-avalon.info name=default-input @x-avalon.lifestyle type=transient
 *
 */
public class DefaultInput extends AbstractResponseElement implements Input
{
	private String label = null;

	private Map myValidValues = null;

	private Object defaultValue = null;

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String newLabel)
	{
		assert newLabel != null;
		label = newLabel;
	}

	public void setValidValues(Map newValues)
	{
		assert newValues != null;
		myValidValues = new LinkedHashMap(newValues);
	}

	public void setDefaultValue(Object newDefault)
	{
		assert newDefault != this;
		defaultValue = newDefault;
	}

	public Object getDefaultValue()
	{
		if (defaultValue == null)
		{
			return "";
		}

		return defaultValue;
	}

	public Map getValidValues()
	{
		Map returnValue = null;

		if (myValidValues == null)
		{
			returnValue = new LinkedHashMap();
		}
		else
		{
			returnValue = myValidValues;
		}

		return returnValue;
	}

	public String toString()
	{
		StringBuffer ret = new StringBuffer(super.toString());

		ret.append("\t<label>" + getLabel() + "</label>");

		Map m = getValidValues();

		if (m.size() > 0)
		{
			ret.append("\t<valid-values>");

			for (Iterator i = m.keySet().iterator(); i.hasNext();)
			{
				String oneKey = (String) i.next();

				ret.append("\t\t<valid-value key='" + oneKey + "' value='" + m.get(oneKey).toString() + "'/>\n");
			}

			ret.append("\t</valid-values>");
		}

		ret.append("\tdefault-value>" + getDefaultValue().toString() + "</default-value>\n");
		ret.append("\t</" + getClass().getName() + ">\n");

		return ret.toString();
	}
}
