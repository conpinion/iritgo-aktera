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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Used to describe a page of entry fields groups.
 */
public class PageDescriptor
{
	/** The label key. */
	protected String label;

	/** The resource bundle to lookup. */
	protected String bundle;

	/** The page icon. */
	protected String icon;

	/** The inactive page icon. */
	protected String inactiveIcon;

	/** The list of all groups on this page. */
	protected List<GroupDescriptor> groups;

	/** Page visibility state. */
	protected boolean visible;

	/** Page position. */
	protected int position;

	/**
	 * Create a new PageDescriptor.
	 *
	 * @param label The resource key of the page label.
	 * @param bundle The resource bundle to lookup for the label.
	 */
	public PageDescriptor(String label, String bundle)
	{
		this(label, bundle, null);
	}

	/**
	 * Create a new PageDescriptor.
	 *
	 * @param label The resource key of the page label.
	 * @param bundle The resource bundle to lookup for the label.
	 * @param icon The page icon.
	 */
	public PageDescriptor(String label, String bundle, String icon)
	{
		this.label = label;
		this.bundle = bundle;
		this.icon = icon;
		groups = new LinkedList();
	}

	/**
	 * Add a group to the page.
	 *
	 * @param group The group to add.
	 */
	public void addGroup(GroupDescriptor group)
	{
		groups.add(group);
	}

	/**
	 * Return an iterator over all groups.
	 *
	 * @return A group iterator.
	 */
	public Iterator groupIterator()
	{
		return groups.iterator();
	}

	/**
	 * Get the group label.
	 *
	 * @return The group label.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Get the name of the resource bundle.
	 *
	 * @return The resource bundle name.
	 */
	public String getBundle()
	{
		return bundle;
	}

	/**
	 * Set the group's visibility state.
	 *
	 * @param visible If true, the group will be visible.
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * Check wether the group is visible or not.
	 *
	 * @return True if the group is visible.
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * Sort all groups and fields by comparing their relative position.
	 */
	public void sort()
	{
		Collections.sort(groups, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return ((GroupDescriptor) o1).getPosition() - ((GroupDescriptor) o2).getPosition();
			}
		});
	}

	/**
	 * Set the page position.
	 *
	 * @param position The new page position.
	 */
	public void setPosition(int position)
	{
		this.position = position;
	}

	/**
	 * Get the page position.
	 *
	 * @return The new page position.
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Set the page icon.
	 *
	 * @param icon The new page icon.
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	 * Get the page icon.
	 *
	 * @return The page icon.
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Check wether the page has an icon.
	 *
	 * @return True if the page has an icon.
	 */
	public boolean hasIcon()
	{
		return icon != null;
	}

	/**
	 * Set the inactive page icon.
	 *
	 * @param inactiveIcon The new inactive page icon.
	 */
	public void setInactiveIcon(String inactiveIcon)
	{
		this.inactiveIcon = inactiveIcon;
	}

	/**
	 * Get the inactive page icon.
	 *
	 * @return The inactive page icon.
	 */
	public String getInactiveIcon()
	{
		return inactiveIcon;
	}

	/**
	 * Check wether the page has an inactive icon.
	 *
	 * @return True if the page has an inactive icon.
	 */
	public boolean hasInactiveIcon()
	{
		return inactiveIcon != null;
	}

	/**
	 * Retrieve the list of groups.
	 *
	 * @return The group list.
	 */
	public List<GroupDescriptor> getGroups()
	{
		return groups;
	}
}
