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


import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Used to describe a persistent field.
 */
public class FieldDescriptor
{
	/** Specifies a readonly attribute field. */
	private boolean readOnly;

	/** Specifies a duty field. */
	private boolean duty;

	/** Specifies an unbound field. */
	private boolean unbound;

	/** Hide the field if no value is set. */
	private boolean hideIfNull;

	/** True if this field is a comment. */
	private boolean comment;

	/** If set, the formular will be submitted if an action was performed on this field. */
	private boolean submit;

	/** If set, the formular will tested by a custom validation class. */
	private boolean validationClass;

	/** Specifies a field with no label. */
	private boolean noLabel;

	/** Specifies a multi line field. */
	private boolean multi;

	/** Specifies that the field is omitted. */
	private boolean omitted;

	/** Specifies that the field is disabled. */
	private boolean disabled;

	/** Field name. */
	protected String name;

	/** Field label (if not specified the name is used). */
	protected String label;

	/** Field tool tip. */
	protected String toolTip;

	/** Ressource bundle for translations. */
	protected String bundle;

	/** Field editor. */
	protected String editor;

	/** Display size of the editor. */
	protected int size;

	/** Visible rows of the editor. */
	protected int rows;

	/** Field flags. */
	protected int flags;

	/** Custom validation class name. */
	protected String validationClassName;

	/** Additional field commands. */
	protected CommandDescriptor commands;

	/** Field parameters. */
	protected Map<String, Object> params;

	/** The list of all sub fields. */
	protected List<FieldDescriptor> subFields;

	/** If true, trim the field value before outputing (e.g. strip blanks or leading zeroes) */
	protected boolean trim;

	/** If true, the field can be selected (for example through a check box left to the field. */
	protected boolean selectable;

	/**
	 * Create a new FieldDescriptor.
	 *
	 * @param name The field name.
	 * @param bundle The ressource bundle.
	 * @param editor The field type.
	 * @param size Display size of the editor.
	 */
	public FieldDescriptor(String name, String bundle, String editor, int size)
	{
		this.name = name;
		this.bundle = bundle;
		this.editor = editor;
		this.size = size;
		this.commands = new CommandDescriptor();
		this.subFields = new LinkedList<FieldDescriptor>();
	}

	/**
	 * Create a new FieldDescriptor.
	 *
	 * @param name The field name.
	 * @param bundle The ressource bundle.
	 * @param editor The field type.
	 * @param size Display size of the editor.
	 */
	public FieldDescriptor(String name, String label, String bundle, String editor, int size)
	{
		this(name, bundle, editor, size);
		this.label = label;
	}

	/**
	 * Create a new FieldDescriptor.
	 *
	 * @param name The field name.
	 * @param bundle The ressource bundle.
	 * @param editor The field type.
	 * @param size Display size of the editor.
	 * @param className The class name of the validation class.
	 */
	public FieldDescriptor(String name, String bundle, String editor, int size, String validationClassName)
	{
		this(name, bundle, editor, size);

		this.validationClassName = validationClassName;
	}

	/**
	 * Get the field name.
	 *
	 * @return The field name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the ressource bundle name.
	 *
	 * @return The ressource bundle name
	 */
	public String getBundle()
	{
		return bundle;
	}

	/**
	 * Set the ressource bundle name.
	 *
	 * @param bundle The ressource bundle name
	 */
	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the editor.
	 *
	 * @return The editor id.
	 */
	public String getEditor()
	{
		return editor;
	}

	/**
	 * Get the editor size.
	 *
	 * @return The editor size.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Get the field flags.
	 *
	 * @return The field flags.
	 */
	public int getFlags()
	{
		return flags;
	}

	/**
	 * Get the number of editor rows.
	 *
	 * @return The number of editor rows.
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 * Set the number of editor rows.
	 *
	 * @param rows The new number of editor rows.
	 */
	public void setRows(int rows)
	{
		this.rows = rows;
	}

	/**
	 * Check for a read only field.
	 *
	 * @return True for a read only field.
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}

	/**
	 * Set a read only field.
	 *
	 * @param readOnly True for a read only field.
	 */
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Check for a duty field.
	 *
	 * @return True for a duty field.
	 */
	public boolean isDuty()
	{
		return duty;
	}

	/**
	 * Set a duty field.
	 *
	 * @param duty True for a duty field.
	 */
	public void setDuty(boolean duty)
	{
		this.duty = duty;
	}

	/**
	 * Check for a custom validation class
	 *
	 * @return True for a custom validation class field.
	 */
	public boolean hasValidationClass()
	{
		return (validationClassName) != null;
	}

	/**
	 * Set the unbound flag.
	 *
	 * @param True if this field is unbound.
	 */
	public void setUnbound(boolean unbound)
	{
		this.unbound = unbound;
	}

	/**
	 * Check for an unbound field.
	 *
	 * @return True for an unbound field.
	 */
	public boolean isUnbound()
	{
		return unbound;
	}

	/**
	 * Check for a bound field.
	 *
	 * @return True for a bound field.
	 */
	public boolean isBound()
	{
		return ! isUnbound();
	}

	/**
	 * Check the 'hide if null' state.
	 *
	 * @return True if the field should be hidden when no value is set.
	 */
	public boolean isHideIfNull()
	{
		return hideIfNull;
	}

	/**
	 * Check wether this field is a description.
	 *
	 * @return True for a description field.
	 */
	public boolean isComment()
	{
		return comment;
	}

	/**
	 * Check wether this field is a hidden field.
	 *
	 * @return True for a hidden field.
	 */
	public boolean isHidden()
	{
		return "hidden".equals(editor);
	}

	/**
	 * Set this field to be hidden.
	 */
	public void hide()
	{
		editor = "hidden";
	}

	/**
	 * Check wether this is a submit field.
	 *
	 * @return True for a submit field.
	 */
	public boolean isSubmit()
	{
		return submit;
	}

	/**
	 * Determine wether this is a submit field or not.
	 */
	public void setSubmit(boolean submit)
	{
		this.submit = submit;
	}

	/**
	 * Get the validator for this field.
	 *
	 * @return The field validator.
	 */
	public Validation getValidationClass()
	{
		try
		{
			return (Validation) Class.forName(validationClassName).newInstance();
		}
		catch (Exception x)
		{
		}

		return null;
	}

	/**
	 * Set the class name of the field validator.
	 *
	 * @param validationClassName The name of the field validator.
	 */
	public void setValidationClassName(String validationClassName)
	{
		this.validationClassName = validationClassName;
	}

	/**
	 * Get the field resource label.
	 *
	 * @return The field resource label.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Set the field resource label.
	 *
	 * @param label The new field resource label.
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Get the field commands.
	 *
	 * @return The field commands.
	 */
	public CommandDescriptor getCommands()
	{
		return commands;
	}

	/**
	 * Get a command with a given name.
	 *
	 * @param name The name of the command to retrieve.
	 * @return The command or null if it wasn't found.
	 */
	public CommandInfo getCommand(String name)
	{
		return commands.getCommand(name);
	}

	/**
	 * Set a parameter.
	 *
	 * @param name The parameter name.
	 * @param value The parameter value.
	 */
	public void putParameter(String name, Object value)
	{
		params.put(name, value);
	}

	/**
	 * Get a parameter.
	 *
	 * @param name The parameter name.
	 * @return The parameter value.
	 */
	public Object getParameter(String name)
	{
		return params.get(name);
	}

	/**
	 * Set this field to have no label.
	 *
	 * @param noLabel If true, this field has no label.
	 */
	public void setNoLabel(boolean noLabel)
	{
		this.noLabel = noLabel;
	}

	/**
	 * Check if this field has no label.
	 *
	 * @return True if this field has no label.
	 */
	public boolean isNoLabel()
	{
		return noLabel;
	}

	/**
	 * Check if this field is a multi field.
	 *
	 * @return True if this field is a multi field.
	 */
	public boolean isMulti()
	{
		return multi;
	}

	/**
	 * Specify wether the field should be omitted
	 *
	 * @param omitted If true, this field is omitted.
	 */
	public void setOmitted(boolean omitted)
	{
		this.omitted = omitted;
	}

	/**
	 * Specify wether the field should be omitted
	 *
	 * @param omitted If true, this field is omitted.
	 */
	public void setVisible(boolean visible)
	{
		setOmitted(! visible);
	}

	/**
	 * Specify that the field should be omitted
	 */
	public void setOmitted()
	{
		setOmitted(true);
	}

	/**
	 * Check if the field is omitted.
	 *
	 * @return True If this field is omitted.
	 */
	public boolean isOmitted()
	{
		return omitted;
	}

	/**
	 * Add a sub field to the field.
	 *
	 * @param field The field descriptor.
	 */
	public void addField(FieldDescriptor field)
	{
		subFields.add(field);
	}

	/**
	 * Get the tool tip.
	 *
	 * @return The the toop tip.
	 */
	public String getToolTip()
	{
		return toolTip;
	}

	/**
	 * Set the tool tip.
	 *
	 * @param toolTip The new tool tip.
	 */
	public void setToolTip(String toolTip)
	{
		this.toolTip = toolTip;
	}

	/**
	 * Get the trim flag.
	 */
	public boolean isTrim()
	{
		return trim;
	}

	/**
	 * Set the trim flag.
	 */
	public void setTrim(boolean trim)
	{
		this.trim = trim;
	}

	/**
	 * Set the selectable flag.
	 *
	 * @param selectabel If true, the field can be selected.
	 */
	public boolean isSelectable()
	{
		return selectable;
	}

	/**
	 * Set the selectable flag.
	 *
	 * @param selectabel If true, the field can be selected.
	 */
	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}

	/**
	 * @param comment
	 */
	public void setComment(boolean comment)
	{
		this.comment = comment;

		if (comment)
		{
			this.unbound = true;
			this.editor = "desc";
		}
	}

	/**
	 * @param multi
	 */
	public void setMulti(boolean multi)
	{
		this.multi = multi;
	}

	/**
	 * Specify if the field should be disabled
	 */
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	/**
	 * Check if the field is disabled.
	 *
	 * @return True If this field is disabled.
	 */
	public boolean isDisabled()
	{
		return disabled;
	}
}
