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

package de.iritgo.aktera.command;

import java.util.Properties;


public class CommandRequest
{
	private Properties params = new Properties();

	public void put(String paramName, String value)
	{
		params.put(paramName, value);
	}

	public String getString(String paramName) throws CommandParameterNotFoundException
	{
		Object value = params.get(paramName);
		if (value != null)
		{
			return (String) value;
		}
		throw new CommandParameterNotFoundException("Command parameter '" + paramName + "' not found");
	}

	public void put(String paramName, Integer value)
	{
		params.put(paramName, value);
	}

	public Integer getInteger(String paramName) throws CommandParameterNotFoundException
	{
		Object value = params.get(paramName);
		if (value != null)
		{
			return (Integer) value;
		}
		throw new CommandParameterNotFoundException("Command parameter '" + paramName + "' not found");
	}

	public void put(String paramName, Boolean value)
	{
		params.put(paramName, value);
	}

	public Boolean getBoolean(String paramName) throws CommandParameterNotFoundException
	{
		Object value = params.get(paramName);
		if (value != null)
		{
			return (Boolean) value;
		}
		throw new CommandParameterNotFoundException("Command parameter '" + paramName + "' not found");
	}
}
