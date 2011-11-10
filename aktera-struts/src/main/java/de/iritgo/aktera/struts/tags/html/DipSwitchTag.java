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
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;


/**
 *
 */
public class DipSwitchTag extends org.apache.struts.taglib.html.BaseInputTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the bean containing our underlying property.
	 */
	protected String name = Constants.BEAN_KEY;

	/** Read only. */
	protected boolean readOnly = false;

	/** Numbering offset. */
	protected int numberingOffset = 0;

	/** Number of switches per row (0 means infinite). This attribute is also set by setCols ().*/
	protected int switchesPerRow = 0;

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
	 * Get the numbering offset.
	 */
	public int getNumberingOffset()
	{
		return this.numberingOffset;
	}

	/**
	 * Set the numbering offset.
	 */
	public void setNumberingOffset(int numberingOffset)
	{
		this.numberingOffset = numberingOffset;
	}

	/**
	 * Set the number of switches per row.
	 *
	 * @param cols The number of switches per row.
	 */
	public void setCols(String cols)
	{
		super.setCols(cols);

		try
		{
			switchesPerRow = Integer.parseInt(cols);
		}
		catch (NumberFormatException x)
		{
		}
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		super.release();

		name = null;
		readOnly = false;
		numberingOffset = 0;
		switchesPerRow = 0;
	}

	/**
	 * Generate the required input tag.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag() throws JspException
	{
		String states = "";

		if (value != null)
		{
			states = value.toString();
		}
		else
		{
			Object val = TagUtils.getInstance().lookup(pageContext, name, property, null);

			if (val == null)
			{
				states = "";
			}
			else
			{
				states = val.toString();
			}
		}

		String[] tmp = states.split("\\|");

		states = tmp[0];

		String disables = "";

		if (tmp.length > 1)
		{
			disables = tmp[1];
		}

		int row = 0;

		StringBuffer results = new StringBuffer();

		results.append("<table class=\"dipswitch\"><tr>");

		for (int i = 0; i < states.length() && (switchesPerRow == 0 || i < switchesPerRow); ++i)
		{
			results.append("<th>" + (numberingOffset + i) + "</th>");
		}

		if (states.length() == 0)
		{
			results.append("<th>&nbsp;</th>");
		}

		results.append("</tr><tr>");

		for (int i = 0; i < states.length(); ++i)
		{
			results.append("<td><input type=\"checkbox\"");
			results.append(" name=\"");

			if (indexed)
			{
				prepareIndex(results, name);
			}

			results.append(property + i);
			results.append("\"");

			if (states.charAt(i) == '1')
			{
				results.append(" checked=\"checked\"");
			}

			if (i < disables.length() && disables.charAt(i) == '1')
			{
				results.append(" disabled=\"disabled\"");
			}

			results.append("/></td>");

			if (switchesPerRow > 0 && (i + 1) < states.length() && (i + 1) % switchesPerRow == 0)
			{
				++row;

				results.append("</tr><tr>");

				for (int j = 0; j < switchesPerRow; ++j)
				{
					results.append("<th>");

					if (numberingOffset + row * switchesPerRow + j <= states.length())
					{
						results.append(String.valueOf(numberingOffset + row * switchesPerRow + j));
					}

					results.append("</th>");
				}

				results.append("</tr><tr>");
			}
		}

		if (states.length() == 0)
		{
			results.append("<td>&nbsp;</td>");
		}

		results.append("</tr></table>\n");

		TagUtils.getInstance().write(pageContext, results.toString());

		return EVAL_BODY_AGAIN;
	}
}
