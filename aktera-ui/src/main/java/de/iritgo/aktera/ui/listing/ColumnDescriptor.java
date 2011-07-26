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

package de.iritgo.aktera.ui.listing;


import de.iritgo.simplelife.constants.SortOrder;


/**
 * Used to describe a listing column.
 */
public class ColumnDescriptor
{
	/** Column name. */
	private String name;

	/** Column renamed name. */
	private String rename;

	/** Column persistent name. */
	private String persistent;

	/** Column persistent field name. */
	private String field;

	/** Renamed column name ('.' replaced by '_'). */
	private String as;

	/** Column label. */
	private String label;

	/** Ressource bundle for translations. */
	private String bundle;

	/** Column viewer. */
	private String viewer;

	/** Column width in percent. */
	private int width;

	/** Column sort order. */
	private SortOrder sort = SortOrder.NONE;

	/** Column visibility. */
	private boolean visible = true;

	/** True if a column is sortable */
	private boolean sortable = true;

	/** Column value */
	private String value;

	/**
	 * Create a new ColumnDescriptor.
	 *
	 * @param name The column title.
	 * @param bundle The ressource bundle.
	 * @param viewer The column viewer.
	 * @param with The column width.
	 */
	public ColumnDescriptor (String name, String bundle, String viewer, int width)
	{
		this.name = name;
		this.label = null;
		this.bundle = bundle;
		this.viewer = viewer;
		this.width = width;
		this.field = name.substring (name.indexOf ('.') + 1);
		this.persistent = name.substring (0, Math.max (name.indexOf ('.'), 0));
		this.as = name.replace ('.', '_');
	}

	/**
	 * Create a new ColumnDescriptor.
	 *
	 * @param name The column title.
	 * @param label The column title.
	 * @param bundle The ressource bundle.
	 * @param viewer The column viewer.
	 * @param with The column width.
	 */
	public ColumnDescriptor (String name, String label, String bundle, String viewer, int width)
	{
		this.name = name;
		this.label = label;
		this.bundle = bundle;
		this.viewer = viewer;
		this.width = width;
		this.field = name.substring (name.indexOf ('.') + 1);
		this.persistent = name.substring (0, Math.max (name.indexOf ('.'), 0));
		this.as = name.replace ('.', '_');
	}

	/**
	 * Get the column name.
	 *
	 * @return The column name.
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Get the renamed column name.
	 *
	 * @return The renamed column name.
	 */
	public String getRename ()
	{
		return rename;
	}

	/**
	 * Set the renamed column name.
	 *
	 * @param rename The renamed column name.
	 */
	public void setRename (String rename)
	{
		this.rename = rename;

		if (rename != null)
		{
			as = rename.replace ('.', '_');
		}
	}

	/**
	 * Get the column resource bundle.
	 *
	 * @return The column resource bundle.
	 */
	public String getBundle ()
	{
		return bundle;
	}

	/**
	 * Get the column viewer.
	 *
	 * @return The column viewer.
	 */
	public String getViewer ()
	{
		return viewer;
	}

	/**
	 * Get the column width.
	 *
	 * @return The column width.
	 */
	public int getWidth ()
	{
		return width;
	}

	/**
	 * Get the column persistent name.
	 *
	 * @return The persistent name.
	 */
	public String getPersistent ()
	{
		return persistent;
	}

	/**
	 * Get the column persistent field name.
	 *
	 * @return The persistent field name.
	 */
	public String getField ()
	{
		return field;
	}

	/**
	 * Get the renamed column name.
	 *
	 * @return The renamed column name.
	 */
	public String getAs ()
	{
		return as;
	}

	/**
	 * Get the sort order.
	 *
	 * @return The sort order.
	 */
	public SortOrder getSort ()
	{
		return sort;
	}

	/**
	 * Set the sort order.
	 *
	 * @param sort The new sort order.
	 */
	public void setSort (SortOrder sort)
	{
		this.sort = sort;
	}

	/**
	 * Get the column label.
	 *
	 * @return The column label.
	 */
	public String getLabel ()
	{
		return label != null ? label : name.substring (name.lastIndexOf ('.') + 1);
	}

	/**
	 * Check the visibility of a column.
	 *
	 * @return True for a visible column.
	 */
	public boolean isVisible ()
	{
		return visible;
	}

	/**
	 * Set the column visibility.
	 *
	 * @param visible True for a visible column.
	 */
	public void setVisible (boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * @param sortable The new sortable.
	 */
	public void setSortable (boolean sortable)
	{
		this.sortable = sortable;
	}

	/**
	 * @return The sortable.
	 */
	public boolean isSortable ()
	{
		return sortable;
	}

	/**
	 * Get the column value.
	 *
	 * @return The column value
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * Set the column value.
	 *
	 * @param value The column value
	 */
	public void setValue (String value)
	{
		this.value = value;
	}
}
