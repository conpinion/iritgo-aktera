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

package de.iritgo.aktera.dashboard.groups;


import de.iritgo.aktera.dashboard.DashboardGroup;
import de.iritgo.aktera.dashboard.DashboardItem;
import de.iritgo.aktera.dashboard.GroupVisitor;
import de.iritgo.aktera.i18n.I18N;
import de.iritgo.simplelife.string.StringTools;
import java.util.List;
import java.util.Locale;


public abstract class AbstractGroupImpl implements AbstractGroup
{
	/** Dashboard items */
	protected List<DashboardItem> dashboardItems;

	protected GroupVisitor customVisitor;

	protected String renderFile;

	private String bundle;

	protected String title;

	protected String description;

	protected String id;

	/** The i18n service */
	protected I18N i18n;

	/** The locale */
	protected Locale locale;

	/**
	 * Set the locale
	 *
	 * @param locale The Locale
	 */
	public void setLocale (Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Set the i18n service
	 *
	 * @param i18n The i18n service
	 */
	public void setI18n (I18N i18n)
	{
		this.i18n = i18n;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#getId()
	 */
	public String getId ()
	{
		return id;
	}

	/**
	 * Set the id
	 *
	 * @param id The id
	 */
	public void setId (String id)
	{
		this.id = id;
	}

	public DashboardGroup newInstance ()
	{
		//Spring magic  <lookup-method name="newInstance"......
		return null;
	}

	/**
	 * Set dashbaord items
	 *
	 * @param dashboardItems The dashboard items
	 */
	public void setDashboardItems (List<DashboardItem> dashboardItems)
	{
		this.dashboardItems = dashboardItems;
	}

	/**
	 * Set the custom visitor
	 *
	 * @param customVisitor The custom visitor
	 */
	public void setCustomVisitor (GroupVisitor customVisitor)
	{
		this.customVisitor = customVisitor;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#generate(de.iritgo.aktera.dashboard.GroupVisitor)
	 */
	public void generate (GroupVisitor visitor)
	{
		if (customVisitor == null)
		{
			visitor.generate ((DashboardGroup) this);
		}
		else
		{
			customVisitor.setParentVisitor (visitor);
			customVisitor.generate ((DashboardGroup) this);
		}

		if (dashboardItems != null)
		{
			for (DashboardItem dashboardItem : dashboardItems)
			{
				dashboardItem.setLocale (locale);

				if (customVisitor == null)
				{
					dashboardItem.generate (visitor);
				}
				else
				{
					dashboardItem.generate (customVisitor);
				}
			}
		}
	}

	/**
	 * Set the title of the group
	 *
	 * @param title The title
	 */
	public void setTitle (String title)
	{
		this.title = title;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardGroup#getTitle()
	 */
	public String getTitle ()
	{
		return i18n.msg (locale, getBundle (), title);
	}

	public String getBundle ()
	{
		return bundle;
	}

	public void setBundle (String bundle)
	{
		this.bundle = bundle;
	}

	public List<DashboardItem> getDashboardItems ()
	{
		return dashboardItems;
	}

	public String getLabel ()
	{
		return null;
	}

	public void setRenderFile (String renderFile)
	{
		this.renderFile = renderFile;
	}

	public String getRenderFile ()
	{
		if (StringTools.isTrimEmpty (renderFile))
		{
			return "/aktera-dashboard/group/group.jsp";
		}

		return renderFile;
	}

	public void setDescription (String description)
	{
		this.description = description;
	}

	public String getDescription ()
	{
		return null;
	}
}
