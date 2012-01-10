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

package de.iritgo.aktera.ui.ng;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.struts.BeanResponse;
import de.iritgo.aktera.tools.KeelTools;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.tools.UserTools;


public class ModelRequestWrapper implements ModelRequest
{
	private UIRequest request;

	private Context context;

	private List<Object> services = new LinkedList();

	private Map attributes = new HashMap();

	public ModelRequestWrapper(UIRequest request)
	{
		this.request = request;
		this.context = new Context()
		{
			public Object get(Object key) throws ContextException
			{
				if (UserEnvironment.CONTEXT_KEY.equals(key))
				{
					return ModelRequestWrapper.this.request.getUserEnvironment();
				}

				return null;
			}
		};
	}

	public void release()
	{
		for (Object service : services)
		{
			KeelTools.releaseService(service);
		}
	}

	public void addError(String errorName, String errorMessage)
	{
	}

	public void copyFrom(KeelRequest newRequest) throws ModelException
	{
	}

	public ModelResponse createResponse() throws ModelException
	{
		return new ModelResponseWrapper(new BeanResponse());
	}

	public ModelResponse execute() throws ModelException
	{
		return null;
	}

	public Object getConfiguration()
	{
		return null;
	}

	public Context getContext() throws ModelException
	{
		return context;
	}

	public Object getDefaultService(String role) throws ModelException
	{
		return null;
	}

	public String getDomain()
	{
		return "default";
	}

	public Map getErrors()
	{
		return null;
	}

	public Object[] getParameterAsArray(String name)
	{
		return request.getParameterAsArray(name);
	}

	public Date getParameterAsDate(String name)
	{
		return request.getParameterAsDate(name);
	}

	public Date getParameterAsDate(String name, Date defaultValue)
	{
		return request.getParameterAsDate(name, defaultValue);
	}

	public double getParameterAsDouble(String name)
	{
		return request.getParameterAsDouble(name);
	}

	public double getParameterAsDouble(String name, double defaultValue)
	{
		return request.getParameterAsDouble(name, defaultValue);
	}

	public float getParameterAsFloat(String name)
	{
		return request.getParameterAsFloat(name);
	}

	public float getParameterAsFloat(String name, float defaultValue)
	{
		return request.getParameterAsFloat(name, defaultValue);
	}

	public int getParameterAsInt(String name)
	{
		return request.getParameterAsInt(name);
	}

	public int getParameterAsInt(String name, int defaultValue)
	{
		return request.getParameterAsInt(name, defaultValue);
	}

	public List getParameterAsList(String name)
	{
		return request.getParameterAsList(name);
	}

	public List getParameterAsList(String name, List defaultValue)
	{
		return request.getParameterAsList(name, defaultValue);
	}

	public long getParameterAsLong(String name)
	{
		return request.getParameterAsLong(name);
	}

	public long getParameterAsLong(String name, long defaultValue)
	{
		return request.getParameterAsLong(name, defaultValue);
	}

	public String getParameterAsString(String name)
	{
		return request.getParameterAsString(name);
	}

	public String getParameterAsString(String name, String defaultValue)
	{
		return request.getParameterAsString(name, defaultValue);
	}

	public Map getParams()
	{
		return request.getParameters();
	}

	public Map getPreviousRequest()
	{
		return null;
	}

	public Object getService(String role) throws ModelException
	{
		try
		{
			Object service = KeelTools.getService(role);

			services.add(service);

			return service;
		}
		catch (ServiceException x)
		{
			throw new ModelException(x);
		}
	}

	public Object getService(String role, String hint) throws ModelException
	{
		try
		{
			Object service = KeelTools.getService(role, hint);

			services.add(service);

			return service;
		}
		catch (ServiceException x)
		{
			throw new ModelException(x);
		}
	}

	public Object getService(String role, String hint, Context ctx) throws ModelException
	{
		try
		{
			Object service = KeelTools.getService(role, hint, ctx);

			services.add(service);

			return service;
		}
		catch (ServiceException x)
		{
			throw new ModelException(x);
		}
	}

	public Object getSpringBean(String name) throws ModelException
	{
		return SpringTools.getBean(name);
	}

	public int getUid()
	{
		return UserTools.getCurrentUserId(this);
	}

	public boolean hasParameter(String name)
	{
		return false;
	}

	public void setPreviousRequest(Map newPrevious)
	{
	}

	public KeelRequest deserialize(byte[] bytes) throws IOException
	{
		return null;
	}

	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public Map getAttributes()
	{
		return attributes;
	}

	public String getBean()
	{
		return null;
	}

	public String getContextPath()
	{
		return null;
	}

	public String getHeader(String headerKey)
	{
		return null;
	}

	public Map getHeaders()
	{
		return null;
	}

	public Locale getLocale()
	{
		return null;
	}

	public String getModel()
	{
		return request.getBean();
	}

	public Object getParameter(String paramKey)
	{
		return request.getParameter(paramKey);
	}

	public Map getParameters()
	{
		return request.getParameters();
	}

	public String getQueryString()
	{
		return null;
	}

	public String getRequestUrl()
	{
		return null;
	}

	public String getScheme()
	{
		return null;
	}

	public String getServerName()
	{
		return null;
	}

	public int getServerPort()
	{
		return 0;
	}

	public String getSource()
	{
		return null;
	}

	public byte[] serialize() throws IOException
	{
		return null;
	}

	public void setAttribute(String key, Object value)
	{
		attributes.put(key, value);
	}

	public void setBean(String bean)
	{
	}

	public void setContextPath(String contextPath)
	{
	}

	public void setHeader(String headerKey, String header)
	{
	}

	public void setLocale(Locale locale)
	{
	}

	public void setModel(String modelName)
	{
	}

	public void setParameter(String paramKey, Object param)
	{
	}

	public void setQueryString(String queryString)
	{
	}

	public void setRequestUrl(String requestUrl)
	{
	}

	public void setScheme(String scheme)
	{
	}

	public void setServerName(String serverName)
	{
	}

	public void setServerPort(int serverPort)
	{
	}

	public void setSource(String source)
	{
	}

	public void removeParameter(String name)
	{
	}
}
