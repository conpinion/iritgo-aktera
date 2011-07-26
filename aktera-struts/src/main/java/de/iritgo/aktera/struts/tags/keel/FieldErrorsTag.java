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

package de.iritgo.aktera.struts.tags.keel;


import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.ErrorsTag;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;
import java.util.Iterator;


/**
 *
 */
public class FieldErrorsTag extends ErrorsTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Render the specified error messages if there are any.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag () throws JspException
	{
		ActionMessages errors = null;

		try
		{
			errors = TagUtils.getInstance ().getActionMessages (pageContext, name);
		}
		catch (JspException x)
		{
			TagUtils.getInstance ().saveException (pageContext, x);
			throw x;
		}

		if ((errors == null) || errors.isEmpty ())
		{
			return (EVAL_BODY_INCLUDE);
		}

		boolean headerPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "errors.header");
		boolean footerPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "errors.footer");
		boolean prefixPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "error.prefix");
		boolean suffixPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "error.suffix");

		StringBuffer results = new StringBuffer ();
		boolean headerDone = false;
		String message = null;
		Iterator reports = null;

		if (property == null)
		{
			reports = errors.get ();
		}
		else
		{
			reports = errors.get (property);
		}

		while (reports.hasNext ())
		{
			ActionMessage report = (ActionMessage) reports.next ();

			if (! headerDone)
			{
				if (headerPresent)
				{
					message = TagUtils.getInstance ().message (pageContext, bundle, locale, "errors.header");
					results.append (message);
					results.append ("<BR>");
				}

				headerDone = true;
			}

			if (prefixPresent)
			{
				results.append (TagUtils.getInstance ().message (pageContext, bundle, locale, "error.prefix"));
			}
			else
			{
				results.append ("<tr><td><font color=\"red\">");
			}

			message = TagUtils.getInstance ().message (pageContext, bundle, locale, report.getKey (),
							report.getValues ());

			if (message != null)
			{
				results.append (message);
				results.append ("<BR>");
			}
			else
			{
				results.append (report.getKey ());
				results.append ("<BR>");
			}

			if (suffixPresent)
			{
				results.append (TagUtils.getInstance ().message (pageContext, bundle, locale, "error.suffix"));
			}
			else
			{
				results.append ("</font></td></tr>");
			}
		}

		if (headerDone && footerPresent)
		{
			message = TagUtils.getInstance ().message (pageContext, bundle, locale, "errors.footer");
			results.append (message);
			results.append ("<BR>");
		}

		TagUtils.getInstance ().write (pageContext, results.toString ());

		return (EVAL_BODY_INCLUDE);
	}
}
