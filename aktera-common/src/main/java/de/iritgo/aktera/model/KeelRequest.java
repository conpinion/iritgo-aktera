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

package de.iritgo.aktera.model;


import java.io.IOException;
import java.util.Locale;
import java.util.Map;


public interface KeelRequest
{
	public Object getParameter (String paramKey);

	public Map getParameters ();

	public void setParameter (String paramKey, Object param);

	/**
	 * The name of the model and the state allow us to determine the
	 * actual Java class that is going to be invoked, and how we're
	 * going to communicate with it
	 */
	public void setModel (String modelName);

	/**
	 * Return the name of the model currently being requested
	 */
	public String getModel ();

	/**
	 * Set the value of an attribute of this response
	 */
	public void setAttribute (String key, Object value);

	public Object getAttribute (String key);

	public Map getAttributes ();

	public byte[] serialize () throws IOException;

	public KeelRequest deserialize (byte[] bytes) throws IOException;

	/**
	 * Get all request headers (depends on the Keel client).
	 *
	 * @return The header map.
	 */
	public Map getHeaders ();

	/**
	 * Set a header value.
	 *
	 * @param headerKey The header key.
	 * @param header The header value.
	 */
	public void setHeader (String headerKey, String header);

	/**
	 * Get a request header (depends on the Keel client).
	 *
	 * @param headerKey The header key.
	 * @return The header value.
	 */
	public String getHeader (String headerKey);

	/**
	 * Set the request source.
	 *
	 * @param source The request source.
	 */
	public void setSource (String source);

	/**
	 * Get the request source.
	 *
	 * @return The request source.
	 */
	public String getSource ();

	/**
	 * Set the request locale.
	 *
	 * @param locale The request locale.
	 */
	public void setLocale (Locale locale);

	/**
	 * Get the request locale.
	 *
	 * @return The request locale.
	 */
	public Locale getLocale ();

	public String getScheme ();

	public void setScheme (String scheme);

	public String getServerName ();

	public void setServerName (String serverName);

	public int getServerPort ();

	public void setServerPort (int serverPort);

	public String getContextPath ();

	public void setContextPath (String contextPath);

	public String getRequestUrl ();

	public void setRequestUrl (String requestUrl);

	public String getQueryString ();

	public void setQueryString (String queryString);

	public void setBean (String bean);

	public String getBean ();
}
