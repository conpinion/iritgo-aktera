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


/**
 * Create form elements for money editing.
 */
public class MoneyTag extends SelectTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Currency strings. */
	static String[] currencies = new String[]
	{
					"EUR", "DEM", "USD"
	};

	/** Read only. */
	protected boolean readOnly;

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
	 * Render the tag.
	 *
	 * @exception JspException if a JSP exception has occurred.
	 */
	public int doEndTag () throws JspException
	{
		StringBuffer results = new StringBuffer ();

		createValueField (results);

		results.append ("&nbsp;");

		createCurrencyField (results);

		TagUtils.getInstance ().write (pageContext, results.toString ());

		return EVAL_PAGE;
	}

	/**
	 * Create the money value field.
	 *
	 * @param result The receiving string buffer.
	 */
	protected void createValueField (StringBuffer results) throws JspException
	{
		String value = getBeanProperty ("Value").toString ();

		results.append ("<input type=\"text\"");
		results.append (" name=\"");

		if (indexed)
		{
			prepareIndex (results, name);
		}

		results.append (property);
		results.append ("\"");

		if (accesskey != null)
		{
			results.append (" accesskey=\"");
			results.append (accesskey);
			results.append ("\"");
		}

		if (tabindex != null)
		{
			results.append (" tabindex=\"");
			results.append (tabindex);
			results.append ("\"");
		}

		results.append (" value=\"");
		results.append (value);
		results.append ("\"");

		results.append (prepareEventHandlers ());

		results.append (" style=\"text-align:right;}\"");

		//         results.append (prepareStyles());
		results.append ("</input>");
	}

	/**
	 * Create the mone currency field.
	 *
	 * @param result The receiving string buffer.
	 */
	protected void createCurrencyField (StringBuffer results) throws JspException
	{
		String currency = getBeanProperty ("Currency").toString ();

		createSelectTag (results, "Currency");

		// 		results.append ("<option value=\"\">---</option>\n");
		for (int i = 0; i < currencies.length; ++i)
		{
			results.append ("<option value=\"");
			results.append (currencies[i]);
			results.append ("\"");
			;

			if (currencies[i].equals (currency))
			{
				results.append (" selected=\"selected\"");
			}

			results.append (">");
			results.append (currencies[i]);
			results.append ("</option>\n");
		}

		results.append ("</select>\n");
	}
}
