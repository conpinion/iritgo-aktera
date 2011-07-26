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
public class WeekDayCheckTag extends org.apache.struts.taglib.html.BaseInputTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the bean containing our underlying property.
	 */
	protected String name = Constants.BEAN_KEY;

	/**
	 * The name of the attribute containing the Locale to be used for
	 * looking up internationalized messages.
	 */
	protected String locale = Globals.LOCALE_KEY;

	/** Read only. */
	protected boolean readOnly = false;

	/**
	 * The name of the servlet context attribute containing our message
	 * resources.
	 */
	protected String bundle = Globals.MESSAGES_KEY;

	/**
	 * Get the bean name.
	 */
	public String getName ()
	{
		return this.name;
	}

	/**
	 * Set the bean name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Set the read only flag.
	 *
	 * @param readOnly Read only flag.
	 */
	public void setReadOnly (boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Get the read only flag.
	 *
	 * @retrun The read only flag.
	 */
	public boolean getReadOnly ()
	{
		return readOnly;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release ()
	{
		super.release ();

		name = null;
		readOnly = false;
	}

	/**
	 * Set the bundle.
	 *
	 * @param bundle The new bundle.
	 */
	public void setBundle (String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the locale.
	 *
	 * @return The locale.
	 */
	public String getLocale ()
	{
		return this.locale;
	}

	/**
	 * Set the locale.
	 *
	 * @param locale The new locale.
	 */
	public void setLocale (String locale)
	{
		this.locale = locale;
	}

	/**
	 * Generate the required input tag.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag () throws JspException
	{
		String weekDays = "";

		if (value != null)
		{
			weekDays = value.toString ();
		}
		else
		{
			Object val = TagUtils.getInstance ().lookup (pageContext, name, property, null);

			if (val == null)
			{
				weekDays = "";
			}
			else
			{
				weekDays = val.toString ();
			}
		}

		StringBuffer results = new StringBuffer ();

		results.append ("<table class=\"dipswitch\"><tr>");

		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "Mo") + "</th>");
		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "Di") + "</th>");
		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "Mi") + "</th>");
		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "Do") + "</th>");
		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "Fr") + "</th>");
		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "Sa") + "</th>");
		results.append ("<th>" + TagUtils.getInstance ().message (pageContext, bundle, locale, "So") + "</th>");

		results.append ("</tr><tr>");

		for (int i = 0; i < 7; ++i)
		{
			results.append ("<td><input type=\"checkbox\"");
			results.append (" name=\"");

			if (indexed)
			{
				prepareIndex (results, name);
			}

			results.append (property + i);
			results.append ("\"");

			if (weekDays.indexOf (String.valueOf (i)) != - 1)
			{
				results.append (" checked=\"checked\"");
			}

			results.append ("/></td>");
		}

		results.append ("</tr></table>\n");

		TagUtils.getInstance ().write (pageContext, results.toString ());

		return EVAL_BODY_TAG;
	}
}
