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

/*
 * Copyright (c) 2002, The Keel Group, Ltd. All rights reserved.
 *
 * This software is made available under the terms of the license found
 * in the LICENSE file, included with this source code. The license can
 * also be found at:
 * http://www.keelframework.net/LICENSE
 */

package de.iritgo.aktera.struts;


import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.ErrorsTag;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;
import java.util.Iterator;


/**
 * Fixed version of Errors Tag. The original tag would not render messages that
 * were *not* internationalized, forcing internationalization on apps that don't need/want/require it
 *
 * Custom tag that renders error messages if an appropriate request attribute
 * has been created.  The tag looks for a request attribute with a reserved
 * key, and assumes that it is either a String, a String array, containing
 * message keys to be looked up in the application's MessageResources, or
 * an object of type <code>org.apache.struts.action.ActionErrors</code>.
 * <p>
 * The following optional message keys will be utilized if corresponding
 * messages exist for them in the application resources:
 * <ul>
 * <li><b>errors.header</b> - If present, the corresponding message will be
 *     rendered prior to the individual list of error messages.
 * <li><b>errors.footer</b> - If present, the corresponding message will be
 *     rendered following the individual list of error messages.
 * <li><b>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @author Michael Nash
 * @version $Revision: 1.8 $ $Date: 2004/02/27 19:27:29 $
 */
public class KeelErrorsTag extends ErrorsTag
{
	// ------------------------------------------------------- Public Methods

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Render the specified error messages if there are any.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag () throws JspException
	{
		// Were any error messages specified?
		ActionMessages errors = null;

		try
		{
			errors = TagUtils.getInstance ().getActionMessages (pageContext, name);
		}
		catch (JspException e)
		{
			TagUtils.getInstance ().saveException (pageContext, e);
			throw e;
		}

		if ((errors == null) || errors.isEmpty ())
		{
			return (EVAL_BODY_INCLUDE);
		}

		// Check for presence of header and footer message keys
		boolean headerPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "errors.header");
		boolean footerPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "errors.footer");
		boolean prefixPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "error.prefix");
		boolean suffixPresent = TagUtils.getInstance ().present (pageContext, bundle, locale, "error.suffix");

		// Render the error messages appropriately
		StringBuffer results = new StringBuffer ();
		boolean headerDone = false;
		boolean oneErrorOnly = false;
		String message = null;
		Iterator<ActionMessage> reports = null;

		if (property == null)
		{
			reports = errors.get ();
		}
		else
		{
			oneErrorOnly = true;
			reports = errors.get (property);
		}

		while (reports.hasNext ())
		{
			ActionMessage report = (ActionMessage) reports.next ();

			if ((! headerDone) && (! oneErrorOnly))
			{
				if (headerPresent)
				{
					message = TagUtils.getInstance ().message (pageContext, bundle, locale, "errors.header");
					results.append (message);
					results.append ("<BR>");
				}

				headerDone = true;
			}

			if ((prefixPresent) && (! oneErrorOnly))
			{
				results.append (TagUtils.getInstance ().message (pageContext, bundle, locale, "error.prefix"));
			}
			else
			{
				if (! oneErrorOnly)
				{
					results.append ("<tr><td><font color=\"red\">");
				}
			}

			if (report.getKey ().startsWith ("$"))
			{
				message = TagUtils.getInstance ().message (pageContext, bundle, locale, report.getKey (),
								report.getValues ());
			}
			else
			{
				message = report.getKey ();
			}

			if (message != null)
			{
				results.append (message);
			}

			if (! oneErrorOnly)
			{
				results.append ("<BR>");
			}

			if ((suffixPresent) && (! oneErrorOnly))
			{
				results.append (TagUtils.getInstance ().message (pageContext, bundle, locale, "error.suffix"));
			}
			else
			{
				if (! oneErrorOnly)
				{
					results.append ("</font></td></tr>");
				}
			}
		}

		if ((headerDone) && (footerPresent) && (! oneErrorOnly))
		{
			message = TagUtils.getInstance ().message (pageContext, bundle, locale, "errors.footer");
			results.append (message);
			results.append ("<BR>");
		}

		// Print the results to our output writer
		TagUtils.getInstance ().write (pageContext, results.toString ());

		// Continue processing this page
		return (EVAL_BODY_INCLUDE);
	}
}
