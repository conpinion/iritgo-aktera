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

package de.iritgo.aktera.dashboard.items;


import de.iritgo.aktera.dashboard.DashboardItem;
import de.iritgo.aktera.i18n.I18N;
import java.util.Locale;


public abstract class AbstractDashboardItem implements DashboardItem
{
	/** The label */
	private String label;

	private String id;

	/** The i18n service */
	protected I18N i18n;

	/** The locale */
	protected Locale locale;

	protected String bundle;

	protected String description;

	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Set the i18n service
	 *
	 * @param i18n The i18n service
	 */
	public void setI18n(I18N i18n)
	{
		this.i18n = i18n;
	}

	/**
	 * @see de.iritgo.aktera.dashboard.DashboardItem#getId()
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Set the id
	 *
	 * @param id The id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Return the label for the board.
	 *
	 * @return The label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Set the label
	 *
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Return the bundle for the board.
	 *
	 * @return The bundle
	 */
	public String getBundle()
	{
		return bundle;
	}

	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Set the description
	 *
	 * @param description The description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Return the description of the board
	 *
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}
}
