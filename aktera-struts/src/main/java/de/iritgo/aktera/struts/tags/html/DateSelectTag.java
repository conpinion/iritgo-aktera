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


import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Create form elements for date editing.
 */
public class DateSelectTag extends SelectTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** String formatter. */
	protected static DecimalFormat timeFormat = new DecimalFormat("00");

	/** String formatter. */
	protected static DecimalFormat yearFormat = new DecimalFormat("0000");

	/** Read only. */
	protected boolean readOnly = false;

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
	 * Render the tag.
	 *
	 * @exception JspException if a JSP exception has occurred.
	 */
	public int doEndTag() throws JspException
	{
		String day = getBeanProperty("Day").toString();
		String month = getBeanProperty("Month").toString();
		String year = getBeanProperty("Year").toString();

		StringBuffer results = new StringBuffer();

		createSelectTag(results, "Day");
		results.append("<option value=\"-1\">---</option>");

		for (int i = 1; i <= 31; ++i)
		{
			String value = String.valueOf(i);

			results.append("<option value=\"");
			results.append(value);
			results.append("\"");

			if (value.equals(day))
			{
				results.append(" selected=\"selected\"");
			}

			results.append(">");
			results.append(timeFormat.format(i, new StringBuffer(), new FieldPosition(0)).toString());
			results.append("</option>");
		}

		results.append("</select>\n");

		results.append("<b>.</b>\n");

		createSelectTag(results, "Month");
		results.append("<option value=\"-1\">---</option>");

		for (int i = 1; i <= 12; ++i)
		{
			String value = String.valueOf(i);

			results.append("<option value=\"");
			results.append(value);
			results.append("\"");

			if (value.equals(month))
			{
				results.append(" selected=\"selected\"");
			}

			results.append(">");
			results.append(timeFormat.format(i, new StringBuffer(), new FieldPosition(0)).toString());
			results.append("</option>");
		}

		results.append("</select>\n");

		results.append("<b>.</b>\n");

		createSelectTag(results, "Year");
		results.append("<option value=\"-1\">---</option>");

		Calendar cal = GregorianCalendar.getInstance();

		for (int i = cal.get(Calendar.YEAR) + 15; i >= cal.get(Calendar.YEAR) - 15; --i)
		{
			String value = String.valueOf(i);

			results.append("<option value=\"");
			results.append(value);
			results.append("\"");

			if (value.equals(year))
			{
				results.append(" selected=\"selected\"");
			}

			results.append(">");
			results.append(yearFormat.format(i, new StringBuffer(), new FieldPosition(0)).toString());
			results.append("</option>");
		}

		results.append("</select>\n");

		TagUtils.getInstance().write(pageContext, results.toString());

		return EVAL_PAGE;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		super.release();

		readOnly = false;
	}
}
