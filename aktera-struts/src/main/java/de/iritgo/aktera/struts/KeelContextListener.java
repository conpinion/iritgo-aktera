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

package de.iritgo.aktera.struts;


import de.iritgo.aktera.clients.webapp.WebappRequest;
import de.iritgo.aktera.clients.webapp.WebappResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSessionEvent;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;


public class KeelContextListener implements ServletContextListener
{
	/**
	 * Custom WebappRequest.
	 */
	protected class SessionWebappRequest implements WebappRequest
	{
		ServletContextEvent event;

		Properties parameters = new Properties();

		public SessionWebappRequest(ServletContextEvent event)
		{
			this.event = event;
		}

		public void setParameter(String name, String value)
		{
			parameters.setProperty(name, value);
		}

		public final String getParameter(String name)
		{
			return parameters.getProperty(name);
		}

		public final String[] getParameterValues(String name)
		{
			Object[] v = parameters.values().toArray();
			String[] values = new String[v.length];

			for (int i = 0; i < v.length; ++i)
			{
				values[i] = v[i].toString();
			}

			return values;
		}

		public final Cookie[] getCookies()
		{
			return new Cookie[]
			{};
		}

		public final Enumeration<?> getParameterNames()
		{
			return parameters.propertyNames();
		}

		public final String getSessionId()
		{
			return "";
		}

		public final String getRemoteAddr()
		{
			return "";
		}

		public final void setAttribute(String name, Object value)
		{
		}

		public final String getRequestURL()
		{
			return "";
		}

		public final String getQueryString()
		{
			return "";
		}

		public final String getRealPath(String path)
		{
			return event.getServletContext().getRealPath(path);
		}

		public final Object getAttribute(String name)
		{
			return "";
		}

		public final String getInitParameter(String name)
		{
			return event.getServletContext().getInitParameter(name);
		}

		public final String getSource()
		{
			return "";
		}

		public final Locale getLocale()
		{
			return null;
		}

		public Enumeration getHeaderNames()
		{
			return null;
		}

		public String getHeader(String key)
		{
			return "";
		}

		public String getScheme()
		{
			return "";
		}

		public String getServerName()
		{
			return "";
		}

		public int getServerPort()
		{
			return 0;
		}

		public String getContextPath()
		{
			return "";
		}
	}

	/**
	 * Custom
	 */
	protected class SessionWebappResponse implements WebappResponse
	{
		ServletContextEvent event;

		public SessionWebappResponse(ServletContextEvent event)
		{
			this.event = event;
		}

		public void addCookie(Cookie c)
		{
		}
	}

	/** Logger. */
	private static Log log = LogFactory.getFactory().getInstance("de.iritgo.aktera.struts.KeelSessionListener");

	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent e)
	{
		String model = e.getServletContext().getInitParameter("keel.session.created.model");

		if (model != null)
		{
			SessionWebappRequest req = new SessionWebappRequest(e);

			req.setParameter("sessionEvent", "created");

			SessionWebappResponse res = new SessionWebappResponse(e);

			try
			{
				StrutsClientConnector client = new StrutsClientConnector();

				client.setLogger(log);
				client.execute(req, res, model);
			}
			catch (Exception x)
			{
				System.out.println("[KeelSessionListener] " + x);
				x.printStackTrace();
			}
		}
	}

	public void contextDestroyed(ServletContextEvent e)
	{
	}
}
