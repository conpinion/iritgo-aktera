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
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;


/**
 * Create form elements for month editing.
 */
public class MonthSelectTag extends SelectTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Month strings. */
	static String[] months = new String[]
	{
					"1.month.00", "1.month.jan", "1.month.feb", "1.month.mar", "1.month.apr", "1.month.may",
					"1.month.jun", "1.month.jul", "1.month.aug", "1.month.sep", "1.month.oct", "1.month.nov",
					"1.month.dec"
	};

	/** Country Iso Shortcut values. */
	static String[] monthValues = new String[]
	{
					"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
	};

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
		String selectedValue = getBeanProperty().toString();

		StringBuffer results = new StringBuffer();

		if (! readOnly)
		{
			createSelectTag(results);

			for (int i = 0; i < months.length; ++i)
			{
				results.append("<option value=\"");
				results.append(monthValues[i]);
				results.append("\"");

				if (monthValues[i].equals(selectedValue))
				{
					results.append(" selected=\"selected\"");
				}

				results.append(">");
				results.append(TagUtils.getInstance().message(pageContext, bundle, locale, months[i]));
				results.append("</option>");
			}

			results.append("</select>\n");
		}
		else
		{
			for (int i = 0; i < months.length; ++i)
			{
				if (monthValues[i].equals(selectedValue))
				{
					results.append(TagUtils.getInstance().message(pageContext, bundle, locale, months[i]));
				}
			}
		}

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
