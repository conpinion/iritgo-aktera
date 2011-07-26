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

package de.iritgo.aktera.struts.tags;


import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;


/**
 *
 */
public class BaseTagSupport extends TagSupport
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor calls <code>release()</code> to initialize the tag attributes to their
	 * default values.
	 */
	public BaseTagSupport ()
	{
		super ();
		release ();
	}

	/**
	 * Logger method.
	 *
	 * @param category The log category.
	 * @param source The log source.
	 * @param message The log message.
	 */
	protected void log (String category, String source, String message)
	{
		pageContext.getServletContext ().log ("[" + category + "] " + source + " - " + message);
	}

	/**
	 * Get a scope constant by name.
	 *
	 * @param scope The scope name.
	 * @return The corresponding scope constant.
	 */
	public int getScope (String scope)
	{
		if ("page".equals (scope))
		{
			return PageContext.PAGE_SCOPE;
		}
		else if ("request".equals (scope))
		{
			return PageContext.REQUEST_SCOPE;
		}
		else if ("session".equals (scope))
		{
			return PageContext.SESSION_SCOPE;
		}
		else if ("application".equals (scope))
		{
			return PageContext.APPLICATION_SCOPE;
		}
		else
		{
			return PageContext.PAGE_SCOPE;
		}
	}
}
