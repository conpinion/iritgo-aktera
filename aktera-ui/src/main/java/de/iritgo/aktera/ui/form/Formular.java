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


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.simplelife.math.NumberTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.formular"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.formular" id="aktera.formular" logger="Aktera"
 */
public class Formular extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req
	 *            The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		try
		{
			Configuration config = getConfiguration();

			FormularDescriptor formular = new FormularDescriptor();

			formular.setBundle(config.getChild("bundle").getValue("Aktera"));

			formular.setIdField(config.getChild("key").getValue(null));
			formular.setLabelWidth(NumberTools.toInt(config.getChild("labelWidth").getValue("0"), 0));

			createGroups(config, formular);

			Configuration[] pagesConfig = config.getChildren("page");

			for (Configuration pageConfig : pagesConfig)
			{
				String pageBundle = pageConfig.getAttribute("bundle", formular.getBundle());

				PageDescriptor page = formular.addPage(pageConfig.getAttribute("name"), pageBundle);

				page.setPosition(positionStringToValue(pageConfig.getAttribute("pos", "C")));
				page.setIcon(pageConfig.getAttribute("icon", null));
				page.setInactiveIcon(pageConfig.getAttribute("inactiveIcon", null));

				createGroups(pageConfig, formular);
			}

			modifyGroups(config, formular);

			formular.sort();

			Output output = res.createOutput("formular");

			output.setContent(formular);
			res.add(output);
		}
		catch (ConfigurationException x)
		{
			throw new ModelException(x);
		}

		return res;
	}

	/**
	 * Create gGroup descriptors for each group child of the specified
	 * configuration node.
	 *
	 * @param config
	 *            The parent configuration.
	 * @param formular
	 *            The formular descriptor.
	 */
	private void createGroups(Configuration config, FormularDescriptor formular)
		throws ConfigurationException, ModelException
	{
		Configuration[] groupsConfig = config.getChildren("group");

		for (Configuration groupConfig : groupsConfig)
		{
			String id = groupConfig.getAttribute("id", null);
			String name = groupConfig.getAttribute("name", null);

			if (name != null)
			{
				if (id != null)
				{
					throw new ModelException("Both id and name specified for group '" + id + "'");
				}

				String groupBundle = groupConfig.getAttribute("bundle", formular.getBundle());

				GroupDescriptor group = formular.addGroup(name, groupBundle);

				group.setPosition(positionStringToValue(groupConfig.getAttribute("pos", "C")));
				group.setVisible(NumberTools.toBool(groupConfig.getAttribute("visible", "true"), true));
				group.setTitleVisible(NumberTools.toBool(groupConfig.getAttribute("titleVisible", "true"), true));
				group.setIcon(groupConfig.getAttribute("icon", null));
				group.setLabel(groupConfig.getAttribute("label", group.getLabel()));

				createFields(groupConfig, formular, group, null);
			}
		}
	}

	/**
	 * Create the fields of a group or multi field.
	 *
	 */
	private void createFields(Configuration parent, FormularDescriptor formular, GroupDescriptor parentGroup,
					FieldDescriptor parentField) throws ConfigurationException
	{
		Configuration[] children = parent.getChildren();

		for (Configuration childConfig : children)
		{
			String bundle = childConfig.getAttribute("bundle", parentGroup != null ? parentGroup.getBundle()
							: parentField.getBundle());

			if ("field".equals(childConfig.getName()))
			{
				FieldDescriptor field = new FieldDescriptor(childConfig.getAttribute("name"), bundle, childConfig
								.getAttribute("editor", ""), NumberTools
								.toInt(childConfig.getAttribute("size", "0"), 0));

				field.setLabel(childConfig.getAttribute("label", null));
				field.setToolTip(childConfig.getAttribute("tip", null));
				field.setRows(NumberTools.toInt(childConfig.getAttribute("rows", "6"), 6));
				field.setNoLabel(NumberTools.toBool(childConfig.getAttribute("nolabel", childConfig.getAttribute(
								"noLabel", "false")), false));
				field.setTrim(NumberTools.toBool(childConfig.getAttribute("trim", "false"), false));

				if (childConfig.getAttribute("unbound", null) != null)
				{
					field.setUnbound(childConfig.getAttributeAsBoolean("unbound", false));
				}

				field.setSelectable(childConfig.getAttributeAsBoolean("selectable", false));

				field.setValidationClassName(childConfig.getAttribute("validator", null));

				if (childConfig.getAttribute("readonly", null) != null)
				{
					field.setReadOnly(childConfig.getAttributeAsBoolean("readonly", false));
				}

				if (childConfig.getAttribute("duty", null) != null)
				{
					field.setDuty(childConfig.getAttributeAsBoolean("duty", false));
				}

				if (childConfig.getAttribute("submit", null) != null)
				{
					field.setSubmit(childConfig.getAttributeAsBoolean("submit", false));
				}

				if (childConfig.getAttribute("nullAllowed", null) != null)
				{
					field.setNullAllowed(childConfig.getAttributeAsBoolean("nullAllowed", true));
				}

				if (parentGroup != null)
				{
					parentGroup.addField(field);
				}
				else if (parentField != null)
				{
					parentField.addField(field);
				}

				createCommandsForField(childConfig, formular, field);
			}
			else if ("comment".equals(childConfig.getName()))
			{
				FieldDescriptor field = new FieldDescriptor(childConfig.getAttribute("label"), bundle, "", 0);

				field.setComment(true);

				if (parentGroup != null)
				{
					parentGroup.addField(field);
				}
				else if (parentField != null)
				{
					parentField.addField(field);
				}
			}
			else if ("buttons".equals(childConfig.getName()))
			{
				FieldDescriptor field = new FieldDescriptor("dummy", "Aktera", "", 0);

				field.setUnbound(true);

				field.setLabel("0.empty");

				if (parentGroup != null)
				{
					parentGroup.addField(field);
				}
				else if (parentField != null)
				{
					parentField.addField(field);
				}

				createCommandsForField(childConfig, formular, field);
			}
			else if ("multi".equals(childConfig.getName()) && parentField == null)
			{
				FieldDescriptor field = new FieldDescriptor(childConfig.getAttribute("label"), bundle, "", 0);

				field.setMulti(true);

				if (parentGroup != null)
				{
					parentGroup.addField(field);
					createFields(childConfig, formular, null, field);
				}
			}
		}
	}

	/**
	 * Modify group descriptors for each group child of the specified
	 * configuration node.
	 *
	 * @param config
	 *            The parent configuration.
	 * @param formular
	 *            The formular descriptor.
	 */
	private void modifyGroups(Configuration config, FormularDescriptor formular)
		throws ConfigurationException, ModelException
	{
		Configuration[] groupsConfig = config.getChildren("group");

		for (Configuration groupConfig : groupsConfig)
		{
			String id = groupConfig.getAttribute("id", null);
			String name = groupConfig.getAttribute("name", null);

			if (id != null)
			{
				if (name != null)
				{
					throw new ModelException("Both id and name specified for group '" + id + "'");
				}

				GroupDescriptor group = formular.getGroup(id);

				if (group == null)
				{
					throw new ModelException("Unable to find group '" + id + "'");
				}

				Configuration[] groupChildren = groupConfig.getChildren();

				for (Configuration childConfig : groupChildren)
				{
					if ("field".equals(childConfig.getName()))
					{
						String fieldId = childConfig.getAttribute("id", null);
						String fieldName = childConfig.getAttribute("id", null);

						if (id != null && name != null)
						{
							throw new ModelException("Both id and name specified for field '" + id + "'");
						}

						if (id != null)
						{
							FieldDescriptor field = group.getField(fieldId);

							if (field == null)
							{
								throw new ModelException("Unable to find field '" + fieldId + "' in group '" + id + "'");
							}

							createCommandsForField(childConfig, formular, field);
						}
					}
				}
			}
		}
	}

	/**
	 * Create commands for a field.
	 *
	 * @param config
	 *            The field configuration.
	 * @param formular
	 *            The formular descriptor.
	 * @param field
	 *            The field descriptor.
	 */
	private void createCommandsForField(Configuration config, FormularDescriptor formular, FieldDescriptor field)
		throws ConfigurationException
	{
		Configuration[] commandChildren = config.getChildren("command");

		for (Configuration commandConfig : commandChildren)
		{
			CommandInfo cmd = new CommandInfo(commandConfig.getAttribute("model", commandConfig.getAttribute("bean",
							null)), commandConfig.getAttribute("name"), commandConfig.getAttribute("label"),
							commandConfig.getAttribute("icon", null));

			if (commandConfig.getAttribute("bean", null) != null)
			{
				cmd.setBean(true);
			}

			if (commandConfig.getAttribute("confirm", null) != null)
			{
				cmd.setConfirm(commandConfig.getAttribute("confirm"));
			}

			CommandDescriptor command = field.getCommands().add(cmd);

			command.setBundle(commandConfig.getAttribute("bundle", field.getBundle()));

			Configuration[] parameterChildren = commandConfig.getChildren("parameter");

			for (Configuration parameterConfig : parameterChildren)
			{
				command.withParameter(parameterConfig.getAttribute("name"), parameterConfig.getAttribute("value"));
			}

			parameterChildren = commandConfig.getChildren("param");

			for (Configuration parameterConfig : parameterChildren)
			{
				command.withParameter(parameterConfig.getAttribute("name"), parameterConfig.getAttribute("value"));
			}

			Configuration[] attributeChildren = commandConfig.getChildren("attribute");

			for (Configuration attributeConfig : attributeChildren)
			{
				command.withParameter(attributeConfig.getAttribute("name"), attributeConfig.getAttribute("value"));
			}			
		}
	}

	/**
	 * Helper method to convert position strings to position integers.
	 *
	 * @param pos
	 *            The position string.
	 * @return The position value.
	 */
	protected int positionStringToValue(String pos)
	{
		int position = 0;

		if ("SS".equals(pos))
		{
			position = - 20;
		}
		else if ("S".equals(pos))
		{
			position = - 10;
		}
		else if ("T".equals(pos) || "L".equals(pos))
		{
			position = - 5;
		}
		else if ("M".equals(pos) || "C".equals(pos))
		{
			position = 0;
		}
		else if ("B".equals(pos) || "R".equals(pos))
		{
			position = 5;
		}
		else if ("E".equals(pos))
		{
			position = 10;
		}
		else if ("EE".equals(pos))
		{
			position = 20;
		}
		else
		{
			position = NumberTools.toInt(pos, 0);
		}

		return position;
	}
}
