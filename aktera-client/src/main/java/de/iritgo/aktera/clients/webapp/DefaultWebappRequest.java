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

package de.iritgo.aktera.clients.webapp;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;


/**
 * Wraps a HttpServletRequest in a WebappRequest
 *
 * @version        $Revision$        $Date$
 * @author Schatterjee
 * Created on May 24, 2003
 */
public class DefaultWebappRequest implements WebappRequest
{
	private HttpServletRequest hreq = null;

	private DefaultWebappRequest()
	{
	}

	public DefaultWebappRequest(HttpServletRequest hreq)
	{
		this.hreq = hreq;
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getParameter(java.lang.String)
	 */
	public final String getParameter(String name)
	{
		return hreq.getParameter(name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getParameterValues(java.lang.String)
	 */
	public final String[] getParameterValues(String name)
	{
		return hreq.getParameterValues(name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getCookies()
	 */
	public final Cookie[] getCookies()
	{
		return hreq.getCookies();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getParameterNames()
	 */
	public final Enumeration getParameterNames()
	{
		return hreq.getParameterNames();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getSessionId()
	 */
	public final String getSessionId()
	{
		return hreq.getSession(true).getId();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getRemoteAddr()
	 */
	public final String getRemoteAddr()
	{
		return hreq.getRemoteAddr();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	public final void setAttribute(String name, Object value)
	{
		if (name != null)
		{
			hreq.setAttribute(name, value);
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getRequestURL()
	 */
	public final String getRequestURL()
	{
		if (hreq.getRequestURL() != null)
		{
			return hreq.getRequestURL().toString();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getQueryString()
	 */
	public final String getQueryString()
	{
		return hreq.getQueryString();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getRealPath(java.lang.String)
	 */
	public final String getRealPath(String path)
	{
		return hreq.getSession(true).getServletContext().getRealPath(path);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getAttribute(java.lang.String)
	 */
	public final Object getAttribute(String name)
	{
		return hreq.getAttribute(name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getInitParameter(java.lang.String)
	 */
	public final String getInitParameter(String name)
	{
		return hreq.getSession(true).getServletContext().getInitParameter(name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getHeaderNames()
	 */
	public final Enumeration getHeaderNames()
	{
		return hreq.getHeaderNames();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getHeader(java.lang.String)
	 */
	public final String getHeader(String name)
	{
		return hreq.getHeader(name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getSource()
	 */
	public final String getSource()
	{
		return hreq.getRemoteAddr();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getSource()
	 */
	public final Locale getLocale()
	{
		return hreq.getLocale();
	}

	public String getScheme()
	{
		return hreq.getScheme();
	}

	public String getServerName()
	{
		return hreq.getServerName();
	}

	public int getServerPort()
	{
		return hreq.getServerPort();
	}

	public String getContextPath()
	{
		return hreq.getContextPath();
	}
}
