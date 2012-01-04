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


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.ui.form.CommandDescriptor;
import de.iritgo.aktera.ui.form.CommandInfo;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.QueryDescriptor;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.string.StringTools;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Used to describe a persistent listing.
 */
public class ListingDescriptor implements Serializable
{
	/** Information about an id column. */
	public static class IdColumnInfo
	{
		public String column;

		public String persistent;

		public String field;

		public IdColumnInfo(String column, String persistent, String field)
		{
			this.column = column;
			this.persistent = persistent;
			this.field = field;
		}
	}

	/** */
	private static final long serialVersionUID = 1L;

	/** 'View' command id. */
	public static String COMMAND_VIEW = "view";

	/** 'Back' command id. */
	public static String COMMAND_BACK = "back";

	/** 'New' command id. */
	public static String COMMAND_NEW = "new";

	/** 'Search' command id. */
	public static String COMMAND_SEARCH = "search";

	/** 'Execute' command id. */
	public static String COMMAND_EXECUTE = "execute";

	/** Search category parameter suffix */
	public static final String SEARCH_CATEGORY_PARAMETER_SUFFIX = "SearchCategory";

	/** List id. */
	private String id = "list";

	/** Formular title. */
	private String title;

	/** Formular icon. */
	private String icon;

	/** The header of the listing. */
	private String header;

	/** The resource bundle used for translations. */
	private String bundle;

	/** The resource bundle used for the list title. */
	private String titleBundle;

	/** Columns. */
	private List columns;

	/** Columns to sort. */
	private List sortColumns;

	/** Map to retrieve columns by key. */
	private Map columnsByKey;

	/** Currently selected category. */
	private String category;

	/** Id columns. */
	private List idColumns;

	/** Query sample. */
	private Persistent querySample;

	/** Persistents to query. */
	private PersistentDescriptor persistents;

	/** Commands. */
	private Map commands;

	/** The list commands. */
	private CommandDescriptor listCommands;

	/** The item commands. */
	private CommandDescriptor itemCommands;

	/** Condition that each line of the lising wust satisfy. */
	private String condition;

	/** Condition parameters. */
	private Object[] conditionParams;

	/** Command style. */
	private String commandStyle = "button";

	/** Wether this listing should be embedded in other output elements. */
	private boolean embedded;

	/** The name of the primary key attribute. */
	private String keyName = "id";

	/** The search form can include a combobox with 'category' entries. */
	private Map categories;

	/** The current page. */
	private int page = 1;

	/** The list model. */
	private String listModel;

	/** If true, only a single list item can be selected */
	private boolean singleSelection;

	/** List items can only be selected if this is true */
	private boolean selectable;

	/** A query descriptor */
	private QueryDescriptor queryDescriptor;

	private boolean ng;

	/**
	 * Create a new listing.
	 */
	public ListingDescriptor()
	{
		columns = new LinkedList();
		columnsByKey = new HashMap();
		commands = new HashMap();
		sortColumns = new LinkedList();
		idColumns = new LinkedList();
	}

	/**
	 * Set the list id.
	 *
	 * @param id The new id.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Get the list id.
	 *
	 * @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Get the list id.
	 *
	 * @param req The model request.
	 * @return The id.
	 */
	public String getId(ModelRequest req)
	{
		if (! StringTools.isEmpty(req.getParameterAsString("listId")))
		{
			return req.getParameterAsString("listId");
		}

		return id;
	}

	/**
	 * Set the list header.
	 *
	 * @param header The new header.
	 * @param bundle The header resources.
	 */
	public void setHeader(String header)
	{
		this.header = header;
	}

	/**
	 * Get the list header.
	 *
	 * @return The header.
	 */
	public String getHeader()
	{
		return header;
	}

	/**
	 * Get the title.
	 *
	 * @return The title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Set the title.
	 *
	 * @return title The new title.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Get the icon.
	 *
	 * @return The icon.
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Set the icon.
	 *
	 * @return icon The new icon.
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	 * Set the resource bundle.
	 *
	 * @param bundle The resource bundle.
	 */
	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the resource bundle.
	 *
	 * @return The resource bundle.
	 */
	public String getBundle()
	{
		return bundle;
	}

	/**
	 * Set the title resource bundle.
	 *
	 * @param titleBundle The title resource bundle.
	 */
	public void setTitleBundle(String titleBundle)
	{
		this.titleBundle = titleBundle;
	}

	/**
	 * Get the title resource bundle.
	 *
	 * @return The title resource bundle.
	 */
	public String getTitleBundle()
	{
		return titleBundle;
	}

	/**
	 * Get the embedded flag.
	 *
	 * @return The embedded flag.
	 */
	public boolean isEmbedded()
	{
		return embedded;
	}

	/**
	 * Set the embedded flag.
	 *
	 * @return embedded The new embedded flag.
	 */
	public void setEmbedded(boolean embedded)
	{
		this.embedded = embedded;
	}

	/**
	 * Get the single selection flag.
	 *
	 * @return True if this is a single selection list
	 */
	public boolean isSingleSelection()
	{
		return singleSelection;
	}

	/**
	 * Set the single selection flag.
	 *
	 * @param singleSelection True if this is a single selection list
	 */
	public void setSingleSelection(boolean singleSelection)
	{
		this.singleSelection = singleSelection;
	}

	/**
	 * Get the primary key attribute name.
	 *
	 * @return The primary key attribute name.
	 */
	public String getKeyName()
	{
		return keyName;
	}

	/**
	 * Set the primary key attribute name.
	 *
	 * @return keyName The new primary key attribute name.
	 */
	public void setKeyName(String keyName)
	{
		this.keyName = keyName;
	}

	/**
	 * Set the category.
	 *
	 * @param category The category.
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * Get the category.
	 *
	 * @return The category.
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * Unset all sort info.
	 */
	public void clearSort()
	{
		sortColumns.clear();

		for (Iterator i = columns.iterator(); i.hasNext();)
		{
			((ColumnDescriptor) i.next()).setSort(SortOrder.NONE);
		}
	}

	/**
	 * Set the column on which to sort the list items.
	 *
	 * @param sortColumn The column on which to sort.
	 */
	public void setSortColumn(String sortColumn)
	{
		ColumnDescriptor column = (ColumnDescriptor) columnsByKey.get(sortColumn);

		if (column != null)
		{
			column.setSort(SortOrder.ASCENDING);
			sortColumns.add(column);
		}
	}

	/**
	 * Set the column on which to sort the list items.
	 *
	 * @param sortColumn The column on which to sort.
	 * @param sortOrder The sort order for this column.
	 */
	public void setSortColumn(String sortColumn, SortOrder sortOrder)
	{
		ColumnDescriptor column = (ColumnDescriptor) columnsByKey.get(sortColumn);

		if (column != null)
		{
			column.setSort(sortOrder);
			sortColumns.add(column);
		}
	}

	/**
	 * Get the name of the first column on which to sort the list items.
	 *
	 * @return The name of the first column on which to sort.
	 */
	public String getSortColumnName()
	{
		if (sortColumns.size() > 0)
		{
			return ((ColumnDescriptor) sortColumns.get(0)).getName();
		}

		return null;
	}

	/**
	 * Get the first column on which to sort the list items.
	 *
	 * @return The first column on which to sort.
	 */
	public ColumnDescriptor getSortColumn()
	{
		if (sortColumns.size() > 0)
		{
			return (ColumnDescriptor) sortColumns.get(0);
		}

		return null;
	}

	/**
	 * Get the column's sort order.
	 *
	 * @return The column's sort order.
	 */
	public SortOrder getSortOrder()
	{
		if (sortColumns.size() > 0)
		{
			return ((ColumnDescriptor) sortColumns.get(0)).getSort();
		}

		return SortOrder.NONE;
	}

	/**
	 * Get an iterator over all sorting columns.
	 *
	 * @return A sorting column iterator.
	 */
	public Iterator sortColumnIterator()
	{
		return sortColumns.iterator();
	}

	/**
	 * Get the list of all sorting columns.
	 *
	 * @return The sorting columns.
	 */
	public List getSortColumns()
	{
		return sortColumns;
	}

	/**
	 * Set the column that contains the object ids.
	 *
	 * @param idColumn The column that contains the object ids.
	 */
	public void setIdColumn(String idColumn)
	{
		if (idColumn != null)
		{
			idColumns = new LinkedList();
			addIdColumn(idColumn);
		}
	}

	/**
	 * Add a column that contains the object ids.
	 *
	 * @param idColumn A column that contains the object ids.
	 */
	public void addIdColumn(String idColumn)
	{
		if (idColumn != null)
		{
			idColumns.add(new IdColumnInfo(idColumn, idColumn.substring(0, Math.max(idColumn.indexOf('.'), 0)),
							idColumn.substring(idColumn.lastIndexOf('.') + 1)));
		}
	}

	/**
	 * Get the column that contains the object ids.
	 *
	 * @return The column that contains the object ids.
	 */
	public String getIdColumn()
	{
		if (idColumns.size() > 0)
		{
			return ((IdColumnInfo) idColumns.get(0)).column;
		}

		return null;
	}

	/**
	 * Get the id persistent name.
	 *
	 * @return The id persistent name.
	 */
	public String getIdPersistent()
	{
		if (idColumns.size() > 0)
		{
			return ((IdColumnInfo) idColumns.get(0)).persistent;
		}

		return null;
	}

	/**
	 * Get the id persistent field name.
	 *
	 * @return The id persistent field name.
	 */
	public String getIdField()
	{
		if (idColumns.size() > 0)
		{
			return ((IdColumnInfo) idColumns.get(0)).field;
		}

		return null;
	}

	/**
	 * Get the list of all id columns.
	 *
	 * @return the list of all id columns.
	 */
	public List getIdColumns()
	{
		return idColumns;
	}

	/**
	 * Get the number of id columns.
	 *
	 * @return The number of id columns.
	 */
	public int getNumIdColumns()
	{
		return idColumns.size();
	}

	/**
	 * Set the query sample.
	 *
	 * @param querySample The query sample.
	 */
	public void setQuerySample(Persistent querySample)
	{
		this.querySample = querySample;
	}

	/**
	 * Get the query sample.
	 *
	 * @return The query sample.
	 */
	public Persistent getQuerySample()
	{
		return querySample;
	}

	/**
	 * Add a new column.
	 *
	 * @param name The column name.
	 * @param bundle The ressource bundle.
	 * @param viewer The field type.
	 * @param with The column width.
	 * @return The new column.
	 */
	public ColumnDescriptor addColumn(String name, String bundle, String viewer, int width)
	{
		ColumnDescriptor column = new ColumnDescriptor(name, bundle, viewer, width);

		columns.add(column);
		columnsByKey.put(name, column);

		return column;
	}

	/**
	 * Add a new column.
	 *
	 * @param name The column name.
	 * @param label The column label.
	 * @param bundle The ressource bundle.
	 * @param viewer The field type.
	 * @param with The column width.
	 * @return The new column.
	 */
	public ColumnDescriptor addColumn(String name, String label, String bundle, String viewer, int width)
	{
		ColumnDescriptor column = new ColumnDescriptor(name, label, bundle, viewer, width);

		columns.add(column);
		columnsByKey.put(name, column);

		return column;
	}

	/**
	 * Get an iterator over all columns.
	 *
	 * @return A column iterator.
	 */
	public Iterator columnIterator()
	{
		return columns.iterator();
	}

	/**
	 * Get the number of defined columns.
	 *
	 * @return The column count.
	 */
	public int getColumnCount()
	{
		return columns.size();
	}

	/**
	 * Get the number of visible columns.
	 *
	 * @return The visible column count.
	 */
	public int getVisibleColumnCount()
	{
		int count = 0;

		for (Iterator i = columns.iterator(); i.hasNext();)
		{
			if (((ColumnDescriptor) i.next()).isVisible())
			{
				++count;
			}
		}

		return count;
	}

	/**
	 * Set the list commands.
	 *
	 * These are commands that operate on the whole list, e.g. 'New'
	 * or 'Reload'.
	 *
	 * @param commands The list commands.
	 */
	public void setListCommands(CommandDescriptor commands)
	{
		this.listCommands = commands;

		for (Iterator i = commands.iterator(); i.hasNext();)
		{
			CommandInfo cmd = (CommandInfo) i.next();

			if (cmd.getBundle() == null)
			{
				cmd.setBundle(bundle);
			}
		}
	}

	/**
	 * Get the list commands.
	 *
	 * @return The list commands.
	 */
	public CommandDescriptor getListCommands()
	{
		return listCommands;
	}

	/**
	 * Set the item commands.
	 *
	 * These are commands that operate on a single list item, e.g. 'Delete'
	 * or 'Change state'.
	 *
	 * @param commands The item commands.
	 */
	public void setItemCommands(CommandDescriptor commands)
	{
		this.itemCommands = commands;
	}

	/**
	 * Get the item commands.
	 *
	 * @return The item commands.
	 */
	public CommandDescriptor getItemCommands()
	{
		return itemCommands;
	}

	/**
	 * Set the persistents to query.
	 *
	 * @param persistents The persistents.
	 */
	public void setPersistents(PersistentDescriptor persistents)
	{
		this.persistents = persistents;
	}

	/**
	 * Get the persistents to query.
	 *
	 * @return The persistents.
	 */
	public PersistentDescriptor getPersistents()
	{
		return persistents;
	}

	/**
	 * Set the list condition.
	 *
	 * @param condition The new condition.
	 * @param conditionParams The condition parameters.
	 */
	public void setCondition(String condition, Object[] conditionParams)
	{
		this.condition = condition;
		this.conditionParams = conditionParams;
	}

	/**
	 * Set the list condition.
	 *
	 * @param condition The new condition.
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	/**
	 * Get the list condition.
	 *
	 * @return The condition.
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Get the list condition parameters.
	 *
	 * @return The condition parameters.
	 */
	public Object[] getConditionParams()
	{
		return conditionParams;
	}

	/**
	 * Set a command.
	 *
	 * @param id The command id.
	 * @param command The command descriptor.
	 */
	public void setCommand(String id, CommandInfo command)
	{
		if (command != null)
		{
			commands.put(id, command);
		}
	}

	/**
	 * Retrieve a command.
	 *
	 * @param id The command id.
	 * @return The command descriptor.
	 */
	public CommandInfo getCommand(String id)
	{
		return (CommandInfo) commands.get(id);
	}

	/**
	 * Set the command style.
	 *
	 * @param commandStyle The new command style.
	 */
	public void setCommandStyle(String commandStyle)
	{
		this.commandStyle = commandStyle;
	}

	/**
	 * Get the command style.
	 *
	 * @return The command style.
	 */
	public String getCommandStyle()
	{
		return commandStyle;
	}

	/**
	 * Check if the listing contains any normal item commands.
	 *
	 * @return True if there are normal item commands.
	 */
	public boolean hasNormalItemCommands()
	{
		if (getItemCommands() == null)
		{
			return false;
		}

		for (Iterator i = getItemCommands().iterator(); i.hasNext();)
		{
			CommandInfo descriptor = (CommandInfo) i.next();

			if (CommandDescriptor.STYLE_NORMAL == descriptor.getStyle())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if the listing contains any tool item commands.
	 *
	 * @return True if there are normal item commands.
	 */
	public boolean hasToolItemCommands()
	{
		if (getItemCommands() == null)
		{
			return false;
		}

		for (Iterator i = getItemCommands().iterator(); i.hasNext();)
		{
			CommandInfo descriptor = (CommandInfo) i.next();

			if (CommandDescriptor.STYLE_TOOL == descriptor.getStyle())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Set the categories.
	 *
	 * @param categories The new categories.
	 */
	public void setCategories(Map categories)
	{
		this.categories = categories;
	}

	/**
	 * Get the categories.
	 *
	 * @return The categories.
	 */
	public Map getCategories()
	{
		return categories;
	}

	/**
	 * Set the current page.
	 *
	 * @param page The new current page.
	 */
	public void setPage(int page)
	{
		this.page = page;
	}

	/**
	 * Get the current page.
	 *
	 * @return The current page.
	 */
	public int getPage()
	{
		return page;
	}

	/**
	 * Get the list model.
	 *
	 * @return The list mode.
	 */
	public String getListModel()
	{
		return listModel;
	}

	/**
	 * Set the list model.
	 *
	 * @return listModel The new list model.
	 */
	public void setListModel(String listModel)
	{
		this.listModel = listModel;
	}

	/**
	 * Update the listing sort.
	 *
	 * @param req The model request.
	 */
	public void updateSort(ModelRequest req)
	{
		updateSort((String) req.getParameter(getId() + "Sort"));
	}

	/**
	 * Update the listing sort.
	 *
	 * @param sort The new sort column.
	 */
	public void updateSort(String sort)
	{
		if (sort != null)
		{
			SortOrder sortOrder = SortOrder.ASCENDING;

			if (sort.equals(getSortColumnName()))
			{
				sortOrder = getSortColumn().getSort() == SortOrder.ASCENDING ? SortOrder.DESCENDING
								: SortOrder.ASCENDING;
			}

			clearSort();
			setSortColumn(sort, sortOrder);
			setPage(1);
		}
	}

	/**
	 * Retrieve a column by name.
	 *
	 * @param name The name of the column to retrieve.
	 * @return The column or null if none was found.
	 */
	public ColumnDescriptor getColumn(String name)
	{
		return (ColumnDescriptor) columnsByKey.get(name);
	}

	/**
	 * @param queryDescriptor The new queryDescriptor.
	 */
	public void setQuery(QueryDescriptor queryDescriptor)
	{
		this.queryDescriptor = queryDescriptor;
	}

	/**
	 * @return The queryDescriptor.
	 */
	public QueryDescriptor getQuery()
	{
		return queryDescriptor;
	}

	/**
	 * Get the columns.
	 *
	 * @return The columns
	 */
	public List<ColumnDescriptor> getColumns()
	{
		return columns;
	}

	public boolean isNg()
	{
		return ng;
	}

	public void setNg(boolean ng)
	{
		this.ng = ng;
	}

	public boolean isSelectable()
	{
		return selectable;
	}

	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}
}
