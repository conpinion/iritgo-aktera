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
import java.util.Enumeration;
import java.util.Locale;


/**
 * This abstracts a client-request for business-logic execution
 * This does not provide all the inforation that a typical
 * Http-request might have, instead providing only the bare minimum
 * required by the current set of client-connectors.
 *
 * @version        $Revision$        $Date$
 * @author Schatterjee
 * Created on May 24, 2003
 */
public interface WebappRequest
{
	/**
	 * Set an attribute
	 * @param key Atrribute key
	 * @param value Attribute value
	 */
	void setAttribute (String key, Object value);

	/**
	 * Get configuration parameters
	 * @param key Paramater key
	 * @return Parameter value
	 */
	String getInitParameter (String key);

	/**
	 * Get names of all the paramters in the request
	 * @return enumaeration of parameter names
	 */
	Enumeration getParameterNames ();

	/**
	 * Get the value of a parameter
	 * @param key Parameter key
	 * @return Parameter value
	 */
	String getParameter (String key);

	/**
	 * Get the values of a parameter
	 * @param key Parameter key
	 * @return Parameter value
	 */
	String[] getParameterValues (String key);

	/**
	 * Get the value of a particular attribute
	 * @param key Attribute key
	 * @return Attribute value
	 */
	Object getAttribute (String key);

	/**
	 * Get all the HTTP cookies in the session
	 * @return cookies
	 */
	Cookie[] getCookies ();

	/**
	 * Get the request URL
	 * @return request URL
	 */
	String getRequestURL ();

	/**
	 * Get query string
	 * @return query strin
	 */
	String getQueryString ();

	/**
	 * Get session ID set by browser
	 * @return session ID
	 */
	String getSessionId ();

	/**
	 * Get IP address of browser
	 * @return IP address
	 */
	String getRemoteAddr ();

	/**
	 * Get fully qualified path
	 * @param path Path relative to webapp/WEB-INF
	 * @return fully qualified path
	 */
	String getRealPath (String path);

	/**
	 */
	Enumeration getHeaderNames ();

	/**
	 */
	String getHeader (String name);

	String getSource ();

	Locale getLocale ();

	String getScheme ();

	String getServerName ();

	int getServerPort ();

	String getContextPath ();
}
