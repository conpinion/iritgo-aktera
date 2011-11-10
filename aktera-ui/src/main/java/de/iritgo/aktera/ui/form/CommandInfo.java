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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.struts.BeanRequest;
import de.iritgo.aktera.ui.el.ExpressionLanguageContext;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Command list elements.
 */
public class CommandInfo implements Cloneable
{
	/** Command model. */
	protected String model;

	/** Command name. */
	protected String name;

	/** Command label. */
	protected String label;

	/** Command label resource bundle. */
	protected String bundle = "Aktera";

	/** Command icon. */
	protected String icon;

	/** Parameters. */
	protected Map<String, Object> parameters;

	/** Attributes. */
	protected Map<String, Object> attributes;

	/** Command style. */
	protected String style = CommandDescriptor.STYLE_NORMAL;

	/** Is this command visible ? */
	protected boolean visible = true;

	/** Position of this command. */
	protected int position;

	/** If true, the model is a bean */
	protected boolean bean;

	/** Permission that must be met for a command to be available */
	protected String permission;

	/**
	 * Create an empty Command.
	 */
	protected CommandInfo()
	{
	}

	/**
	 * Create a new Command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 * @param icon The command icon.
	 * @param style The command style.
	 */
	public CommandInfo(String model, String name, String label, String icon, String style)
	{
		this.model = model;
		this.name = name;
		this.label = label;
		this.icon = icon;
		this.style = style;
		parameters = new HashMap<String, Object>();
		attributes = new HashMap<String, Object>();
	}

	/**
	 * Create a new Command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 * @param icon The command icon.
	 */
	public CommandInfo(String model, String name, String label, String icon)
	{
		this(model, name, label, icon, null);
	}

	/**
	 * Create a new Command.
	 *
	 * @param model The command model.
	 * @param name The command name.
	 * @param label The command label.
	 */
	public CommandInfo(String model, String name, String label)
	{
		this(model, name, label, null);
	}

	/**
	 * Create a new Command.
	 *
	 * @param model The command model.
	 * @param label The command label.
	 */
	public CommandInfo(String model, String label)
	{
		this(model, null, label, null);
	}

	/**
	 * Set the command model.
	 *
	 * @return The new command model.
	 */
	public void setModel(String model)
	{
		this.model = model;
	}

	/**
	 * Get the command model.
	 *
	 * @return The command model.
	 */
	public String getModel()
	{
		return model;
	}

	/**
	 * Get the command name.
	 *
	 * @return The command name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the command label.
	 *
	 * @return The command label.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Get the command label resource bundle.
	 *
	 * @return The resource bundle.
	 */
	public String getBundle()
	{
		return bundle;
	}

	/**
	 * Set the command label resource bundle.
	 *
	 * @param bundle The new resource bundle.
	 */
	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the command icon.
	 *
	 * @return The command icon.
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Set the command icon.
	 *
	 * @param icon The new command icon.
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	 * Set the visible state.
	 *
	 * @param visible True for a visible command.
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * Get the visible state.
	 *
	 * @return True for a visible command.
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * Add a command parameter.
	 *
	 * @param name Parameter name.
	 * @param value Parameter value.
	 * @return This command info.
	 */
	public CommandInfo addParameter(String name, Object value)
	{
		parameters.put(name, value);

		return this;
	}

	/**
	 * Get the command parameters.
	 *
	 * @return The command parameters.
	 */
	public Map getParameters()
	{
		return parameters;
	}

	/**
	 * Get an iterator over all parameters.
	 *
	 * @return A parameter iterator (map entries).
	 */
	public Iterator parameterIterator()
	{
		return parameters.entrySet().iterator();
	}

	/**
	 * Add a command attribute.
	 *
	 * @param name Attribute name.
	 * @param value Attribute value.
	 */
	public void addAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	/**
	 * Get an iterator over all attributes.
	 *
	 * @return An attribute iterator (map entries).
	 */
	public Iterator attributeIterator()
	{
		return attributes.entrySet().iterator();
	}

	/**
	 * Get the command style.
	 *
	 * @return The command style.
	 */
	public String getStyle()
	{
		return style;
	}

	/**
	 * Get the command style.
	 *
	 * @return The command style.
	 */
	public void setStyle(String style)
	{
		this.style = style;
	}

	/**
	 * Create a Keel command from this command descriptor.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param context The expression language context
	 * @return The keel command.
	 */
	public Command createCommand(ModelRequest req, ModelResponse res, ExpressionLanguageContext context)
		throws ModelException
	{
		return createCommand(req, res, "", context);
	}

	/**
	 * Create a Keel command from this command descriptor.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param context The expression language context
	 * @return The keel command.
	 */
	public Command createCancelCommand(ModelRequest req, ModelResponse res, ExpressionLanguageContext context)
		throws ModelException
	{
		Command command = res.createCommand("aktera.cancel");

		command.setName(name);

		command.setLabel(StringTools.trim(label));

		if (! StringTools.isTrimEmpty(icon))
		{
			command.setAttribute("icon", icon);
		}

		if (! StringTools.isTrimEmpty(bundle))
		{
			command.setAttribute("bundle", bundle);
		}

		command.setAttribute("style", StringTools.isTrimEmpty(style) ? "" : style);

		if (! visible)
		{
			command.setAttribute("hide", "Y");
		}

		for (Map.Entry<String, Object> parameter : parameters.entrySet())
		{
			Object value = parameter.getValue();

			if (value instanceof String)
			{
				value = context.evalExpressionLanguageValue((String) value);
			}

			command.setParameter("_cp" + parameter.getKey(), value);
		}

		command.setParameter("_cmodel", model);

		for (Map.Entry<String, Object> attribute : attributes.entrySet())
		{
			command.setAttribute(attribute.getKey(), attribute.getValue());
		}

		return command;
	}

	/**
	 * Create a Keel command from this command descriptor.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param paramPrefix Prefix for parameter names.
	 * @param context The expression language context
	 * @return The keel command.
	 */
	public Command createCommand(ModelRequest req, ModelResponse res, String paramPrefix,
					ExpressionLanguageContext context) throws ModelException
	{
		String myModel = StringTools.trim(context.evalExpressionLanguageValue(model));

		Command command = res.createCommand(myModel);

		if (bean)
		{
			command.setBean(myModel);
			command.setModel(null);
		}

		command.setName(name);

		command.setLabel(StringTools.trim(label));

		if (! StringTools.isTrimEmpty(icon))
		{
			command.setAttribute("icon", icon);
		}

		if (! StringTools.isTrimEmpty(bundle))
		{
			command.setAttribute("bundle", bundle);
		}

		command.setAttribute("style", StringTools.isTrimEmpty(style) ? "" : style);

		if (! visible)
		{
			command.setAttribute("hide", "Y");
		}

		setParameters(req, command, paramPrefix, context);

		for (Map.Entry<String, Object> attribute : attributes.entrySet())
		{
			command.setAttribute(attribute.getKey(), attribute.getValue());
		}

		return command;
	}

	/**
	 * Set the command parameter.
	 *
	 * @param req The model request.
	 * @param command The command.
	 * @param paramPrefix Prefix for parameter names.
	 * @param context The expression language context
	 */
	public void setParameters(ModelRequest req, Command command, String paramPrefix, ExpressionLanguageContext context)
	{
		for (Map.Entry<String, Object> parameter : parameters.entrySet())
		{
			Object value = parameter.getValue();

			if (value instanceof String)
			{
				value = context.evalExpressionLanguageValue((String) value);
			}

			if (value != null)
			{
				command.setParameter(paramPrefix + parameter.getKey(), value);
			}
		}
	}

	/**
	 * Describe method setParameters() here.
	 *
	 * @param request
	 * @param context
	 */
	public void setParameters(BeanRequest request, ExpressionLanguageContext context)
	{
		for (Map.Entry<String, Object> parameter : ((Map<String, Object>) getParameters()).entrySet())
		{
			Object value = parameter.getValue();

			if (value instanceof String)
			{
				value = context.evalExpressionLanguageValue((String) value);
			}

			if (value != null)
			{
				request.setParameter(parameter.getKey(), value);
			}
		}
	}

	/**
	 * Clone a command info.
	 *
	 * @return The clone.
	 */
	public Object clone()
	{
		CommandInfo clone = new CommandInfo();

		clone.model = model;
		clone.name = name;
		clone.label = label;
		clone.icon = icon;
		clone.style = style;
		clone.visible = visible;
		clone.position = position;
		clone.parameters = new HashMap();
		clone.bean = bean;

		for (Map.Entry<String, Object> param : parameters.entrySet())
		{
			clone.parameters.put(param.getKey(), param.getValue());
		}

		clone.attributes = new HashMap();

		for (Map.Entry<String, Object> attribute : attributes.entrySet())
		{
			clone.attributes.put(attribute.getKey(), attribute.getValue());
		}

		return clone;
	}

	/**
	 * Get the command position.
	 *
	 * @return The command position.
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Set the command position.
	 *
	 * @param position The new command position.
	 */
	public void setPosition(int position)
	{
		this.position = position;
	}

	/**
	 * Set the command position.
	 *
	 * @param position The new command position.
	 */
	public void setPosition(String position)
	{
		this.position = 0;

		if ("SS".equals(position))
		{
			this.position = - 100000;
		}
		else if ("S".equals(position))
		{
			this.position = - 10000;
		}
		else if ("L".equals(position))
		{
			this.position = - 1000;
		}
		else if ("C".equals(position))
		{
			this.position = 0;
		}
		else if ("R".equals(position))
		{
			this.position = 1000;
		}
		else if ("E".equals(position))
		{
			this.position = 10000;
		}
		else if ("EE".equals(position))
		{
			this.position = 100000;
		}
		else
		{
			this.position = NumberTools.toInt(position, 0);
		}
	}

	/**
	 * Set the controller bean that should be executed by this command
	 *
	 * @param bean The controller bean name
	 */
	public void setBean(boolean bean)
	{
		this.bean = bean;
	}

	/**
	 * Get the controller bean that should be executed by this command
	 *
	 * @return bean The controller bean name
	 */
	public boolean isBean()
	{
		return bean;
	}

	/**
	 * Set the command permission
	 *
	 * @param permission The new permission
	 */
	public void setPermission(String permission)
	{
		this.permission = permission;
	}

	/**
	 * Get the command permission
	 *
	 * @return The permission
	 */
	public String getPermission()
	{
		return permission;
	}

	/**
	 * Check if the command permissions are fulfilled.
	 *
	 * @param request A model request
	 * @return True if the permissions are met
	 */
	public boolean checkPermission(ModelRequest request)
	{
		if (permission == null)
		{
			return true;
		}

		PermissionManager permissionManager = (PermissionManager) SpringTools.getBean(PermissionManager.ID);
		boolean hasPermission = false;

		for (String p : permission.split("\\|"))
		{
			if (permissionManager.hasPermission(UserTools.getCurrentUserName(request), p))
			{
				hasPermission = true;

				break;
			}
		}

		return hasPermission;
	}
}
