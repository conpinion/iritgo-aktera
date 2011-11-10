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

package de.iritgo.aktera.ui.ng;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.DefaultCommand;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.ui.UIResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


public class ModelResponseWrapper implements ModelResponse
{
	private UIResponse response;

	private transient int nextName = 0;

	public ModelResponseWrapper(UIResponse response)
	{
		this.response = response;
	}

	public void add(ResponseElement re) throws ModelException
	{
		response.add(re);
	}

	public void addCommand(String model, String label) throws ModelException
	{
		response.addCommand(model, label);
	}

	public void addError(String errorName, String errorMessage)
	{
		response.addError(errorName, errorMessage);
	}

	public void addError(String errorName, String errorMessage, Throwable t)
	{
		response.addError(errorName, errorMessage, t);
	}

	public void addError(String errorName, Throwable t)
	{
		response.addError(errorName, t);
	}

	public void addErrors(Map errors)
	{
		response.addErrors(errors);
	}

	public void addInput(String name, String label) throws ModelException
	{
		response.addInput(name, label);
	}

	public void addOutput(String name, String content) throws ModelException
	{
		response.addOutput(name, content);
	}

	public void addOutput(String content) throws ModelException
	{
		String assignedName = "output_" + nextName;

		nextName++;
		addOutput(assignedName, content);
	}

	public void clearErrors()
	{
		response.clearErrors();
	}

	public Command createCommand(String model) throws ModelException
	{
		Command newCommand = null;

		newCommand = new DefaultCommand();
		newCommand.setName("command_" + nextName);
		nextName++;
		newCommand.setBean(model);

		return newCommand;
	}

	public Command createCommandRelativeSequence(int numberOfSteps) throws ModelException
	{
		return null;
	}

	public Input createInput(String name) throws ModelException
	{
		return response.createInput(name);
	}

	public Output createOutput(String name) throws ModelException
	{
		return response.createOutput(name);
	}

	public Output createOutput(String name, String content) throws ModelException
	{
		return response.createOutput(name, content);
	}

	public KeelResponse deserialize(byte[] bytes) throws IOException
	{
		return null;
	}

	public ResponseElement get(String elementLocator)
	{
		return response.get(elementLocator);
	}

	public Iterator getAll()
	{
		return response.getAll();
	}

	public Object getAttribute(String key)
	{
		return null;
	}

	public Map getAttributes()
	{
		return null;
	}

	public String getErrorType(String errorName)
	{
		return response.getErrorType(errorName);
	}

	public Map getErrors()
	{
		return response.getErrors();
	}

	public String getStackTrace(String errorName)
	{
		return response.getStackTrace(errorName);
	}

	public Throwable getThrowable(String oneKey)
	{
		return response.getThrowable(oneKey);
	}

	public void remove(ResponseElement re) throws ModelException
	{
		response.remove(re);
	}

	public void removeAttribute(String key)
	{
	}

	public byte[] serialize() throws IOException
	{
		return null;
	}

	public void setAttribute(String key, Object value)
	{
	}

	public void setDefaultsFromPrevious()
	{
	}

	public void setRequest(ModelRequest req)
	{
	}
}
