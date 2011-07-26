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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Used to describe a group of entry fields.
 */
public class GroupDescriptor
{
	/** Group name */
	protected String name;

	public String getName ()
	{
		return name;
	}

	/** The label key */
	protected String label;

	public String getLabel ()
	{
		return label;
	}

	public void setLabel (String label)
	{
		this.label = label;
	}

	/** The resource bundle to lookup for labels */
	protected String bundle;

	public String getBundle ()
	{
		return bundle;
	}

	/** The label icon */
	protected String icon;

	public void setIcon (String icon)
	{
		this.icon = icon;
	}

	public String getIcon ()
	{
		return icon;
	}

	/** The list of all fields in this group */
	protected List<FieldDescriptor> fields;

	public List<FieldDescriptor> getFields ()
	{
		return fields;
	}

	/** Map to retrieve fields by key */
	protected Map<String, FieldDescriptor> fieldsByKey;

	/** Group visibility state */
	protected boolean visible = true;

	public void setVisible (boolean visible)
	{
		this.visible = visible;
	}

	public boolean isVisible ()
	{
		return visible;
	}

	/** Group title visibility state */
	protected boolean titleVisible = true;

	public void setTitleVisible (boolean titleVisible)
	{
		this.titleVisible = titleVisible;
	}

	public boolean isTitleVisible ()
	{
		return titleVisible;
	}

	/** Group position */
	protected int position;

	public void setPosition (int position)
	{
		this.position = position;
	}

	public int getPosition ()
	{
		return position;
	}

	/** The last field added */
	protected FieldDescriptor lastField;

	/**
	 * Create a new GroupDescriptor.
	 *
	 * @param name The group name
	 * @param label The resource key of the group label
	 * @param bundle The resource bundle to lookup for the label
	 */
	public GroupDescriptor (String name, String label, String bundle)
	{
		this.name = name;
		this.label = label;
		this.bundle = bundle;
		fields = new LinkedList<FieldDescriptor> ();
		fieldsByKey = new HashMap<String, FieldDescriptor> ();
		visible = true;
	}

	/**
	 * Create a new GroupDescriptor.
	 *
	 * @param name The group name
	 * @param label The resource key of the group label
	 * @param bundle The resource bundle to lookup for the label
	 */
	public GroupDescriptor (String name, String bundle)
	{
		this (name, name, bundle);
	}

	/**
	 * Add a field to the group.
	 *
	 * @param name The field name
	 * @param bundle The resource bundle
	 * @param editor The field type
	 * @param size Display size of the editor
	 */
	public GroupDescriptor addField (String name, String bundle, String editor, int size)
	{
		FieldDescriptor field = new FieldDescriptor (name, bundle, editor, size);

		fields.add (field);
		fieldsByKey.put (name, field);
		lastField = field;

		return this;
	}

	/**
	 * Add a field to the group.
	 *
	 * @param field The field descriptor
	 */
	public void addField (FieldDescriptor field)
	{
		fields.add (field);
		fieldsByKey.put (field.getName (), field);
	}

	/**
	 * Add a field to the group behind another field.
	 *
	 * @param field The field descriptor
	 * @param otherField The field behind the new one should be placed
	 */
	public void addFieldBehind (FieldDescriptor field, FieldDescriptor otherField)
	{
		int index = fields.indexOf (otherField);

		if (index != - 1)
		{
			fields.add (index + 1, field);
			fieldsByKey.put (field.getName (), field);
		}
	}

	/**
	 * Add a field to the group before another field.
	 *
	 * @param field The field descriptor
	 * @param otherField The field before the new one should be placed
	 */
	public void addFieldBefore (FieldDescriptor field, FieldDescriptor otherField)
	{
		int index = fields.indexOf (otherField);

		if (index != - 1)
		{
			fields.add (index, field);
			fieldsByKey.put (field.getName (), field);
		}
	}

	/**
	 * Return an iterator over all fields.
	 *
	 * @return A field iterator
	 */
	public Iterator<FieldDescriptor> fieldIterator ()
	{
		return fields.iterator ();
	}

	/**
	 * Check whether this group contains a given field.
	 *
	 * @param name The name of the field to check
	 * @return True if the group contains the field
	 */
	public boolean containsField (String name)
	{
		return fieldsByKey.containsKey (name);
	}

	/**
	 * Check whether this group contains a given field.
	 *
	 * @param name The name of the field to check
	 * @return True if the group contains the field
	 */
	public boolean hasField (String name)
	{
		return containsField (name);
	}

	/**
	 * Retrieve a field by key.
	 *
	 * @param key The field key
	 * @return The field
	 * @throws ModelException If the field wasn't found
	 */
	public FieldDescriptor getField (String key) throws ModelException
	{
		FieldDescriptor field = fieldsByKey.get (key);

		if (field == null)
		{
			throw new ModelException ("Unable to find field '" + key + "' in group '" + label + "'");
		}

		return field;
	}

	/**
	 * Remove a field by key.
	 *
	 * @param key The field key
	 * @throws ModelException If the field wasn't found
	 */
	public void removeField (String key) throws ModelException
	{
		removeField (getField (key));
	}

	/**
	 * Remove a field.
	 *
	 * @param field The field
	 * @throws ModelException If the field wasn't found
	 */
	public void removeField (FieldDescriptor field) throws ModelException
	{
		fieldsByKey.remove (field.getName ());
		fields.remove (field);
	}

	/**
	 * Get the map containing all fields.
	 *
	 * @return The field map
	 */
	public Map getFieldMap ()
	{
		return fieldsByKey;
	}

	/**
	 * Add a command to the last field.
	 *
	 * @param model The command model
	 * @param name The command name
	 * @param label The command label
	 * @param icon The command icon
	 */
	public GroupDescriptor withCommand (String model, String name, String label, String icon)
	{
		if (lastField != null)
		{
			lastField.getCommands ().add (model, name, label, icon);
		}

		return this;
	}

	/**
	 * Add a command parameter to the last added command.
	 *
	 * @param name Parameter name
	 * @param value Parameter value
	 */
	public GroupDescriptor withParameter (String name, Object value)
	{
		if (lastField != null)
		{
			lastField.getCommands ().withParameter (name, value);
		}

		return this;
	}

	/**
	 * Add a command attribute to the last added command.
	 *
	 * @param name Attribute name
	 * @param value Attribute value
	 */
	public GroupDescriptor withAttribute (String name, Object value)
	{
		if (lastField != null)
		{
			lastField.getCommands ().withAttribute (name, value);
		}

		return this;
	}
}
