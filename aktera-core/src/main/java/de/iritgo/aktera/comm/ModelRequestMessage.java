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

package de.iritgo.aktera.comm;


import de.iritgo.aktera.model.KeelRequest;
/**
 * A "shell" model request that can be used to populate a "real" model request
 * on the server. This class implements the ModelRequest interface, but is
 * directly instantiated by clients that do not have access to the full Keel
 * server class set, then passed via a JMS message to a Keel JMS server, which
 * copies it into a 'real' model request, executes the request, and passes back
 * the response.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ModelRequestMessage implements KeelRequest, Serializable
{
	private Map params = null;

	private Map attributes = null;

	private String myModel = null;

	private Map headers = null;

	private String source = null;

	private Locale locale = null;

	private String scheme;

	private String serverName;

	private int serverPort;

	private String contextPath;

	private String requestUrl;

	private String queryString;

	private String myBean = null;

	public Object getParameter(String paramKey)
	{
		Object returnValue = null;

		if (params != null)
		{
			returnValue = params.get(paramKey);
		}

		return returnValue;
	}

	public String getModel()
	{
		return myModel;
	}

	public Map getParameters()
	{
		Map returnValue = null;

		if (params == null)
		{
			returnValue = new HashMap();
		}
		else
		{
			returnValue = params;
		}

		return returnValue;
	}

	/* public void configure(Configuration myConfig) {
	} */
	public void setParameter(String paramKey, Object param)
	{
		if (paramKey != null)
		{
			if (params == null)
			{
				params = new HashMap();
			}

			if (! (param instanceof Serializable))
			{
				throw new IllegalArgumentException("Parameter " + paramKey + " is not serializable");
			}

			params.put(paramKey, param);
		}
	}

	/**
	 * The name of the model and the state allow us to determine the
	 * actual Java class that is going to be invoked, and how we're
	 * going to communicate with it
	 */
	public void setModel(String modelName)
	{
		myModel = modelName;
	}

	public Object getAttribute(String key)
	{
		Object returnValue = null;

		if (key != null)
		{
			if (attributes != null)
			{
				returnValue = attributes.get(key);
			}
		}

		return returnValue;
	}

	public Map getAttributes()
	{
		Map returnValue = null;

		if (attributes == null)
		{
			returnValue = new HashMap();
		}
		else
		{
			returnValue = attributes;
		}

		return returnValue;
	}

	public void setAttribute(String key, Object value)
	{
		if (attributes == null)
		{
			attributes = new HashMap();
		}

		if (key != null)
		{
			synchronized (attributes)
			{
				attributes.put(key, value);
			}
		}
	}

	public byte[] serialize() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		oos.writeObject(this);

		return baos.toByteArray();
	}

	public KeelRequest deserialize(byte[] bytes) throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		ModelRequestMessage myObject;

		try
		{
			myObject = (ModelRequestMessage) ois.readObject();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException(e.getMessage());
		}

		return myObject;
	}

	public String getHeader(String key)
	{
		String returnValue = null;

		if (key != null)
		{
			if (headers != null)
			{
				returnValue = (String) headers.get(key);
			}
		}

		return returnValue;
	}

	public Map getHeaders()
	{
		Map returnValue = null;

		if (headers == null)
		{
			returnValue = new HashMap();
		}
		else
		{
			returnValue = headers;
		}

		return returnValue;
	}

	public void setHeader(String key, String value)
	{
		if (headers == null)
		{
			headers = new HashMap();
		}

		if (key != null)
		{
			synchronized (headers)
			{
				headers.put(key, value);
			}
		}
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getSource()
	{
		return source;
	}

	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public String getScheme()
	{
		return scheme;
	}

	public void setScheme(String scheme)
	{
		this.scheme = scheme;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public int getServerPort()
	{
		return serverPort;
	}

	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public String getContextPath()
	{
		return contextPath;
	}

	public void setContextPath(String contextPath)
	{
		this.contextPath = contextPath;
	}

	public String getRequestUrl()
	{
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl)
	{
		this.requestUrl = requestUrl;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	public String getBean()
	{
		return myBean;
	}

	public void setBean(String bean)
	{
		this.myBean = bean;
	}
}
