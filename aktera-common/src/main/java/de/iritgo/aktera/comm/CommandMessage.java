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
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Give the user a navigation option
 */
public class CommandMessage extends AbstractMessageResponseElement implements Command, Serializable
{
	private Map params = new HashMap();

	private String myModel = null;

	private String myLabel = null;

	private int moveRelativeSteps = 0;

	private String myBean = null;

	public CommandMessage()
	{
	}

	public CommandMessage(Command c)
	{
		super.copyFrom(c);
		setName(c.getName());
		setModel(c.getModel());
		setBean(c.getBean());

		String oneParamKey = null;
		Map myParams = c.getParameters();

		for (Iterator i = myParams.keySet().iterator(); i.hasNext();)
		{
			oneParamKey = (String) i.next();
			setParameter(oneParamKey, myParams.get(oneParamKey));
		}

		setLabel(c.getLabel());
	}

	public void setModel(String newModel)
	{
		myModel = newModel;
	}

	public String getModel()
	{
		return myModel;
	}

	public void setParameter(String param, Object value)
	{
		if (value == null)
		{
			return;
		}

		if (! (value instanceof Serializable))
		{
			throw new IllegalArgumentException("Parameter " + param + " not serializable");
		}

		//         if ((!value.getClass().getName().startsWith("java.lang"))
		//             && (!value.getClass().getName().equals("java.util.Date"))) {
		//             throw new IllegalArgumentException(
		//                 "Only basic types may be used as parameter values. Value  of type '"
		//                     + value.getClass().getName()
		//                     + "' for parameter '"
		//                     + param
		//                     + "' is not allowed");
		//         }
		params.put(param, value);
	}

	public Map getParameters()
	{
		return params;
	}

	public ModelResponse execute(ModelRequest req, ModelResponse res) throws ModelException
	{
		return execute(req, res, false, false);
	}

	public ModelResponse execute(ModelRequest req, ModelResponse res, boolean includeParams,
					boolean includeResponseElements) throws ModelException
	{
		throw new ModelException("Not implemented");
	}

	public void setLabel(String newLabel)
	{
		myLabel = newLabel;
	}

	public String getLabel()
	{
		return myLabel;
	}

	public void setRelativeMovement(int numberOfSteps)
	{
		moveRelativeSteps = numberOfSteps;
	}

	public int getRelativeMovement()
	{
		return moveRelativeSteps;
	}

	public String getBean()
	{
		return myBean;
	}

	public void setBean(String bean)
	{
		myBean = bean;
	}
}
