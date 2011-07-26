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


import de.iritgo.aktera.model.Input;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;


public class InputMessage extends AbstractMessageResponseElement implements Input, Serializable
{
	private String label = null;

	private Map myValidValues = null;

	private Object defaultValue = null;

	public InputMessage ()
	{
	}

	public InputMessage (Input i)
	{
		super.copyFrom (i);
		setName (i.getName ());
		setDefaultValue (i.getDefaultValue ());
		setValidValues (i.getValidValues ());
		setLabel (i.getLabel ());
	}

	public String getLabel ()
	{
		return label;
	}

	public void setLabel (String newLabel)
	{
		label = newLabel;
	}

	public void setValidValues (Map newValues)
	{
		myValidValues = newValues;
	}

	public void setDefaultValue (Object newDefault)
	{
		defaultValue = newDefault;
	}

	public Object getDefaultValue ()
	{
		return defaultValue;
	}

	public Map getValidValues ()
	{
		Map returnValue = null;

		if (myValidValues == null)
		{
			returnValue = new TreeMap ();
		}
		else
		{
			returnValue = myValidValues;
		}

		return returnValue;
	}
}
