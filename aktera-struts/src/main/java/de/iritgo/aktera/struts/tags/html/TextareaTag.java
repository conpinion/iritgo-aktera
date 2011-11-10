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


import javax.servlet.jsp.JspException;


/**
 *
 */
public class TextareaTag extends org.apache.struts.taglib.html.TextareaTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Wrap. */
	protected boolean wrap = false;

	/**
	 * Set the wrap flag.
	 *
	 * @param wrap Wrap flag.
	 */
	public void setWrap(boolean wrap)
	{
		this.wrap = wrap;
	}

	/**
	 * Get the wrap flag.
	 *
	 * @retrun The wrap flag.
	 */
	public boolean getWrap()
	{
		return wrap;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		super.release();

		wrap = true;
	}

	/**
	 * Generate an HTML &lt;textarea&gt; tag.
	 * @throws JspException
	 * @since Struts 1.1
	 */
	protected String renderTextareaElement() throws JspException
	{
		StringBuffer results = new StringBuffer("<textarea");

		results.append(" name=\"");

		if (indexed)
		{
			prepareIndex(results, name);
		}

		results.append(property);
		results.append("\"");

		if (accesskey != null)
		{
			results.append(" accesskey=\"");
			results.append(accesskey);
			results.append("\"");
		}

		if (tabindex != null)
		{
			results.append(" tabindex=\"");
			results.append(tabindex);
			results.append("\"");
		}

		if (cols != null)
		{
			results.append(" cols=\"");
			results.append(cols);
			results.append("\"");
		}

		if (rows != null)
		{
			results.append(" rows=\"");
			results.append(rows);
			results.append("\"");
		}

		if (! wrap)
		{
			results.append(" wrap=\"off\"");
		}

		results.append(prepareEventHandlers());
		results.append(prepareStyles());
		results.append(">");

		results.append(this.renderData());

		results.append("</textarea>");

		return results.toString();
	}
}
