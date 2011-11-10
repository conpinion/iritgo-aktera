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
 * Create form elements for salutation editing.
 */
public class SalutationSelectTag extends SelectTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Salutation strings. */
	static String[] salutations = new String[]
	{
					"opt-", "mr", "mrs", "firm"
	};

	/** Salutation values. */
	static String[] salutationValues = new String[]
	{
					"", "mr", "mrs", "firm"
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

			for (int i = 0; i < salutations.length; ++i)
			{
				results.append("<option value=\"");
				results.append(salutationValues[i]);
				results.append("\"");

				if (salutationValues[i].equals(selectedValue))
				{
					results.append(" selected=\"selected\"");
				}

				results.append(">");
				results.append(TagUtils.getInstance().message(pageContext, bundle, locale, salutations[i]));
				results.append("</option>");
			}

			results.append("</select>\n");
		}
		else
		{
			for (int i = 0; i < salutations.length; ++i)
			{
				if (salutationValues[i].equals(selectedValue))
				{
					results.append(TagUtils.getInstance().message(pageContext, bundle, locale, salutations[i]));
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
