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


import de.iritgo.aktera.struts.tags.BaseBodyTagSupport;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.RequestUtils;
import javax.servlet.jsp.JspException;
import java.util.Iterator;


/**
 *
 */
public class HasErrorTag extends BaseBodyTagSupport
{
	/**
	 * The request attribute key for our error messages (if any).
	 */
	protected String name = Globals.ERROR_KEY;

	/**
	 * The name of the property for which error messages should be returned,
	 * or <code>null</code> to return all errors.
	 */
	protected String property = null;

	/**
	 * Get the request attribute key for our error messages.
	 *
	 * @return The request attribute key.
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the request attribute key for our error messages.
	 *
	 * @param name The new request attribute key.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Get the name of the property for which error messages should be returned.
	 *
	 * @return The name of the property.
	 */
	public String getProperty ()
	{
		return property;
	}

	/**
	 * Set the name of the property for which error messages should be returned.
	 *
	 * @param property The new name of the property.
	 */
	public void setProperty (String property)
	{
		this.property = property;
	}

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

		if ((errors == null) || errors.isEmpty () || property == null)
		{
			return SKIP_BODY;
		}

		Iterator reports = errors.get (property);

		if (reports.hasNext ())
		{
			return EVAL_BODY_INCLUDE;
		}
		else
		{
			return SKIP_BODY;
		}
	}
}
