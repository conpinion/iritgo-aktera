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

package de.iritgo.aktera.struts.tags.html;


import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;


/**
 *
 */
public class WeekdaysMultiSelectTag extends org.apache.struts.taglib.html.BaseInputTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Weekday strings. */
	static String[] weekdays = new String[]
	{
					"1.weekday.sun", "1.weekday.mon", "1.weekday.tue", "1.weekday.wed", "1.weekday.thu",
					"1.weekday.fri", "1.weekday.sat"
	};

	/**
	 * The name of the bean containing our underlying property.
	 */
	private String name = Constants.BEAN_KEY;

	/** Ressource bundle name */
	private String bundle;

	/**
	 * The name of the attribute containing the Locale to be used for
	 * looking up internationalized messages.
	 */
	protected String locale = Globals.LOCALE_KEY;

	/** Read only. */
	private boolean readOnly = false;

	/**
	 * Get the bean name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Set the bean name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the bundle name.
	 */
	public String getBundle()
	{
		return this.bundle;
	}

	/**
	 * Set the bundle name.
	 */
	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the locale.
	 *
	 * @return The locale.
	 */
	public String getLocale()
	{
		return this.locale;
	}

	/**
	 * Set the locale.
	 *
	 * @param locale The new locale.
	 */
	public void setLocale(String locale)
	{
		this.locale = locale;
	}

	/**
	 * Set the read only flag.
	 *
	 * @param readOnly Read only flag.
	 */
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Get the read only flag.
	 *
	 * @retrun The read only flag.
	 */
	public boolean getReadOnly()
	{
		return readOnly;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		super.release();

		name = null;
		readOnly = false;
		locale = Globals.LOCALE_KEY;
	}

	/**
	 * Generate the required input tag.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag() throws JspException
	{
		String selectedWeekdays = "";

		if (value != null)
		{
			selectedWeekdays = "," + value.toString() + ",";
		}
		else
		{
			Object val = TagUtils.getInstance().lookup(pageContext, name, property, null);

			if (val != null)
			{
				selectedWeekdays = "," + val.toString() + ",";
			}
		}

		StringBuffer results = new StringBuffer();

		results.append("<table><tr>\n");

		int weekday = 2;

		for (int i = 0; i < 2; ++i)
		{
			results.append("<td valign=\"top\"><select multiple=\"true\" size=\"3\" name=\"" + prepareName() + "\">\n");

			for (int j = 0; j < 3; ++j)
			{
				results.append("<option value=\"" + weekday + "\"");

				if (selectedWeekdays.indexOf("," + weekday + ",") != - 1)
				{
					results.append(" selected=\"true\"");
				}

				results.append(">");
				results.append(TagUtils.getInstance().message(pageContext, bundle, locale, weekdays[weekday - 1]));
				results.append("</option>");
				++weekday;
			}
		}

		results.append("<td valign=\"top\"><select multiple=\"true\" size=\"3\" name=\"" + prepareName() + "\">\n");

		for (int j = 0; j < 1; ++j)
		{
			if (weekday == 8)
			{
				weekday = 1;
			}

			results.append("<option value=\"" + weekday + "\"");

			if (selectedWeekdays.indexOf("," + weekday + ",") != - 1)
			{
				results.append(" selected=\"true\"");
			}

			results.append(">");
			results.append(TagUtils.getInstance().message(pageContext, bundle, locale, weekdays[weekday - 1]));
			results.append("</option>");
			++weekday;
		}

		results.append("</select></td>\n");
		results.append("</tr></table>\n");
		TagUtils.getInstance().write(pageContext, results.toString());

		return SKIP_BODY;
	}
}
