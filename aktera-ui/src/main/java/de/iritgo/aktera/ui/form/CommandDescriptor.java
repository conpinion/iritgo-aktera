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

package de.iritgo.aktera.ui.form;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Used to describe formular commands.
 */
public class CommandDescriptor
{
	/** Command style: Tool item. */
	public static final String STYLE_NORMAL = null;

	/** Command style: Tool item. */
	public static final String STYLE_TOOL = "tool";

	/** List of all commands. */
	private List<CommandInfo> commands;

	/** List of all commands. */
	private Map<String, CommandInfo> commandsByName;

	/** Last added command. */
	private CommandInfo lastCommand;

	/** Label for this group of commands. */
	private String label;

	/**
	 * Create a new CommandDescriptor.
	 */
	public CommandDescriptor()
	{
		commands = new LinkedList<CommandInfo>();
		commandsByName = new HashMap<String, CommandInfo>();
	}

	/**
	 * Create a new CommandDescriptor with a label.
	 *
	 * @param label The command group label.
	 */
	public CommandDescriptor(String label)
	{
		this();
		this.label = label;
	}

	/**
	 * Add a command.
	 *
	 * @param commandInfo The command info.
	 */
	public CommandDescriptor add(CommandInfo commandInfo)
	{
		lastCommand = commandInfo;
		commands.add(commandInfo);
		commandsByName.put(commandInfo.getName(), commandInfo);

		return this;
	}

	/**
	 * Add a command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 */
	public CommandDescriptor add(String model, String name, String label)
	{
		lastCommand = new CommandInfo(model, name, label, null);
		commands.add(lastCommand);
		commandsByName.put(name, lastCommand);

		return this;
	}

	/**
	 * Add a command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 * @param icon The command icon.
	 */
	public CommandDescriptor add(String model, String name, String label, String icon)
	{
		lastCommand = new CommandInfo(model, name, label, icon);
		commands.add(lastCommand);
		commandsByName.put(name, lastCommand);

		return this;
	}

	/**
	 * Add a command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 * @param bundle The command label.
	 * @param icon The command icon.
	 */
	public CommandDescriptor add(String model, String name, String label, String bundle, String icon)
	{
		lastCommand = new CommandInfo(model, name, label, icon);
		lastCommand.setBundle(bundle);
		commands.add(lastCommand);
		commandsByName.put(name, lastCommand);

		return this;
	}

	/**
	 * Add a command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 * @param icon The command icon.
	 */
	public CommandDescriptor add(String model, String name, String label, String bundle, String icon, String style)
	{
		lastCommand = new CommandInfo(model, name, label, icon, style);
		lastCommand.setBundle(bundle);
		commands.add(lastCommand);
		commandsByName.put(name, lastCommand);

		return this;
	}

	/**
	 * Get an iterator over all commands.
	 *
	 * @return A command iterator.
	 */
	public Iterator iterator()
	{
		return commands.iterator();
	}

	/**
	 * Add a command parameter to the last added command.
	 *
	 * @param name Parameter name.
	 * @param value Parameter value.
	 */
	public CommandDescriptor withParameter(String name, Object value)
	{
		if (lastCommand != null)
		{
			lastCommand.addParameter(name, value);
		}

		return this;
	}

	/**
	 * Add a command attribute to the last added command.
	 *
	 * @param name Attribute name.
	 * @param value Attribute value.
	 */
	public CommandDescriptor withAttribute(String name, Object value)
	{
		if (lastCommand != null)
		{
			lastCommand.addAttribute(name, value);
		}

		return this;
	}

	/**
	 * Set the command group label.
	 *
	 * @param label The new label.
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Get the command group label.
	 *
	 * @return The label.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Get the number of commands in this descriptor.
	 *
	 * @return The command count.
	 */
	public int commandCount()
	{
		return commands.size();
	}

	/**
	 * Set the command label resource bundle.
	 *
	 * @param bundle The new resource bundle.
	 */
	public CommandDescriptor setBundle(String bundle)
	{
		if (lastCommand != null)
		{
			lastCommand.setBundle(bundle);
		}

		return this;
	}

	/**
	 * Check wether the descriptor has visible buttons.
	 *
	 * @return True if the descriptor has visible buttons.
	 */
	public boolean hasVisibleButtons()
	{
		for (CommandInfo command : commands)
		{
			if (command.isVisible())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Check for a command with a given name.
	 *
	 * @param name The name of the command to retrieve
	 * @return True if the command was found
	 */
	public boolean hasCommand(String name)
	{
		return commandsByName.get(name) != null;
	}

	/**
	 * Get a command with a given name.
	 *
	 * @param name The name of the command to retrieve.
	 * @return The command or null if it wasn't found.
	 */
	public CommandInfo getCommand(String name)
	{
		return commandsByName.get(name);
	}

	/**
	 * Sort the commands by position.
	 */
	public void sortCommands()
	{
		Collections.sort(commands, new Comparator<CommandInfo>()
		{
			public int compare(CommandInfo command1, CommandInfo command2)
			{
				return command1.getPosition() - command2.getPosition();
			}
		});
	}
}
