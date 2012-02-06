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


import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.stereotype.Component;
import de.iritgo.aktera.util.system.AnnotationUtils;


@Component("de.iritgo.aktera.command.CommandManager")
public class CommandManagerImpl implements CommandManager
{
	@Inject
	private List<Command> commands;

	private Map<String, Command> commandsByKey = new HashMap();

	@PostConstruct
	public void init()
	{
		for (Command command : commands)
		{
			String key = command.getKey();
			if (key == null)
			{
				key = (String) AnnotationUtils.getClassAnnotationAttribute(command, Component.class, "value");
			}
			if (key == null)
			{
				key = command.getClass().getName();
			}
			commandsByKey.put(key, command);
		}
	}

	@Override
	public CommandResponse execute(String key, CommandRequest request) throws CommandNotFoundException
	{
		Command command = commandsByKey.get(key);
		if (command != null)
		{
			return command.execute(request);
		}
		throw new CommandNotFoundException("Command '" + key + "' not found");
	}

	@Override
	public CommandResponse executeIfAvailable(String key, CommandRequest request)
	{
		try
		{
			return execute(key, request);
		}
		catch (CommandNotFoundException ignored)
		{
			return new CommandResponse();
		}
	}
}
