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


import de.iritgo.aktera.clients.webapp.WebappRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;


/**
 * A Model request for use with the <code>CallModel</code> tag.
 */
public class TagRequest implements WebappRequest
{
	/** Underlying http request. */
	private HttpServletRequest req;

	/** Model parameters. */
	private Hashtable<String, String> tagParameters;

	/**
	 * Create a new <code>TagRequest</code>.
	 */
	public TagRequest ()
	{
		this.req = null;
		tagParameters = new Hashtable<String, String> ();
	}

	/**
	 * Create a new <code>TagRequest</code>.
	 *
	 * @param req The http request.
	 */
	public TagRequest (HttpServletRequest req)
	{
		tagParameters = new Hashtable<String, String> ();
		setRequest (req);
	}

	/**
	 * Set the http request.
	 *
	 * @param req The http request.
	 */
	public void setRequest (HttpServletRequest req)
	{
		this.req = req;

		for (Enumeration<String> i = req.getParameterNames (); i.hasMoreElements ();)
		{
			String name = i.nextElement ();

			tagParameters.put (name, req.getParameter (name));
		}
	}

	/**
	 * Set a parameter.
	 *
	 * @param name The parameter name.
	 * @param value The parameter value.
	 */
	public void setParameter (String name, String value)
	{
		tagParameters.put (name, value);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getParameter(java.lang.String)
	 */
	public String getParameter (String name)
	{
		return (String) tagParameters.get (name);

		// 		return req.getParameter (name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getCookies()
	 */
	public Cookie[] getCookies ()
	{
		return req.getCookies ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getParameterNames()
	 */
	public Enumeration<String> getParameterNames ()
	{
		return tagParameters.keys ();

		// 		return req.getParameterNames ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getSessionId()
	 */
	public String getSessionId ()
	{
		return req.getSession (true).getId ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getRemoteAddr()
	 */
	public String getRemoteAddr ()
	{
		return req.getRemoteAddr ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute (String name, Object value)
	{
		if (name != null)
		{
			req.setAttribute (name, value);
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getRequestURL()
	 */
	public String getRequestURL ()
	{
		if (req.getRequestURL () != null)
		{
			return req.getRequestURL ().toString ();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getQueryString()
	 */
	public String getQueryString ()
	{
		return req.getQueryString ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath (String path)
	{
		return req.getSession (true).getServletContext ().getRealPath (path);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getAttribute(java.lang.String)
	 */
	public Object getAttribute (String name)
	{
		return req.getAttribute (name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getInitParameter(java.lang.String)
	 */
	public String getInitParameter (String name)
	{
		return req.getSession (true).getServletContext ().getInitParameter (name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues (String key)
	{
		return new String[]
		{
			(String) tagParameters.get (key)
		};
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getHeaderNames()
	 */
	public final Enumeration<String> getHeaderNames ()
	{
		return req.getHeaderNames ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getHeader(java.lang.String)
	 */
	public final String getHeader (String name)
	{
		return req.getHeader (name);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getSource()
	 */
	public final String getSource ()
	{
		return req.getRemoteAddr ();
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappRequest#getSource()
	 */
	public final Locale getLocale ()
	{
		return req.getLocale ();
	}

	public String getScheme ()
	{
		return req.getScheme ();
	}

	public String getServerName ()
	{
		return req.getServerName ();
	}

	public int getServerPort ()
	{
		return req.getServerPort ();
	}

	public String getContextPath ()
	{
		return req.getContextPath ();
	}
}
