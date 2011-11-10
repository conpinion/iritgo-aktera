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

package de.iritgo.aktera.struts.tags.bean;


import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;


/**
 *
 */
public class WriteTag extends org.apache.struts.taglib.bean.WriteTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/** If true, all newlines are converted to '<br>' tags */
	protected boolean convertNewlinesToBr;

	/**
	 * Get the convert newlines to br flag.
	 */
	public boolean getConvertNewlinesToBr()
	{
		return convertNewlinesToBr;
	}

	/**
	 * Set the convert newlines to br flag.
	 */
	public void setConvertNewlinesToBr(boolean convertNewlinesToBr)
	{
		this.convertNewlinesToBr = convertNewlinesToBr;
	}

	/**
	 * @see org.apache.struts.taglib.bean.WriteTag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException
	{
		if (ignore)
		{
			if (TagUtils.getInstance().lookup(pageContext, name, scope) == null)
			{
				return SKIP_BODY;
			}
		}

		Object value = TagUtils.getInstance().lookup(pageContext, name, property, scope);

		if (value == null)
		{
			return SKIP_BODY;
		}

		String output = formatValue(value);

		if (filter)
		{
			output = ResponseUtils.filter(output);
		}

		if (convertNewlinesToBr)
		{
			output = output.replaceAll("\n", "<br>");
		}

		TagUtils.getInstance().write(pageContext, output);

		return SKIP_BODY;
	}
}
