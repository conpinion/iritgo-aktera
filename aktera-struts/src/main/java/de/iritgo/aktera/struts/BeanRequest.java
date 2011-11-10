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


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 *
 */
public class BeanRequest implements UIRequest
{
	/** */
	private String bean;

	/** The user environment */
	private UserEnvironment userEnvironment;

	/** The request locale */
	private Locale locale;

	/** */
	private Map<String, Object> parameters = new HashMap();

	/**
	 * @see de.iritgo.aktera.ui.UIRequest#setBean(java.lang.String)
	 */
	public void setBean(String bean)
	{
		this.bean = bean;
	}

	/**
	 * Get the controller bean name.
	 *
	 * @return bean The bean name
	 */
	public String getBean()
	{
		return bean;
	}

	/**
	 * Set the user environment.
	 *
	 * @param userEnvironment The user environment
	 */
	public void setUserEnvironment(UserEnvironment userEnvironment)
	{
		this.userEnvironment = userEnvironment;
	}

	/**
	 * Get the user environment.
	 *
	 * @return The user environment
	 */
	public UserEnvironment getUserEnvironment()
	{
		return userEnvironment;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIRequest#getLocale()
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * Set the request locale.
	 *
	 * @param locale The new request locale
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Set the parameter map.
	 *
	 * @param parameters The new parameters
	 */
	public void setParameters(Map<String, Object> parameters)
	{
		this.parameters = parameters;
	}

	/**
	 * Get the request parameters.
	 *
	 * @return The request parameters
	 */
	public Map<String, Object> getParameters()
	{
		return parameters;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIRequest#setParameter(java.lang.String, java.lang.Object)
	 */
	public void setParameter(String name, Object value)
	{
		if (name != null)
		{
			if (value == null)
			{
				value = new HashMap();
			}

			parameters.put(name, value);
		}
	}

	public Object getParameter(String name)
	{
		return parameters.get(name);
	}

	public boolean hasParameter(String name)
	{
		return getParameter(name) != null;
	}

	public Object[] getParameterAsArray(String name)
	{
		assert name != null;

		final Object val = getParameter(name);

		if (val == null)
		{
			return (null);
		}

		final Class valClass = val.getClass();
		final Class valType = valClass.getComponentType();

		if (valType == null)
		{
			Object[] retVal = (Object[]) Array.newInstance(val.getClass(), 1);

			retVal[0] = val;

			return (retVal);
		}
		else
		{
			Object[] retVal = (Object[]) Array.newInstance(valType, Array.getLength(val));

			for (int i = 0; i < Array.getLength(val); i++)
			{
				retVal[i] = Array.get(val, i);
			}

			return retVal;
		}
	}

	public Object[] getParameterAsArray(String name, Object[] defaultValue)
	{
		assert name != null;

		Object val = getParameter(name);

		if (val == null)
		{
			return defaultValue;
		}

		return getParameterAsArray(name);
	}

	public Date getParameterAsDate(String name)
	{
		assert name != null;

		return new SuperString(getParameterAsString(name)).toDate();
	}

	public Date getParameterAsDate(String name, Date defaultValue)
	{
		assert name != null;

		Object val = getParameter(name);

		if (val == null)
		{
			return defaultValue;
		}

		return getParameterAsDate(name);
	}

	public double getParameterAsDouble(String name)
	{
		assert name != null;

		Converter c = ConvertUtils.lookup(java.lang.Double.class);

		return ((Double) c.convert(java.lang.Double.class, getParameterAsString(name))).doubleValue();
	}

	public double getParameterAsDouble(String name, double defaultValue)
	{
		assert name != null;

		if (getParameter(name) == null)
		{
			return defaultValue;
		}

		return getParameterAsDouble(name);
	}

	public float getParameterAsFloat(String name)
	{
		assert name != null;

		Converter c = ConvertUtils.lookup(java.lang.Float.class);

		return ((Float) c.convert(java.lang.Float.class, getParameterAsString(name))).floatValue();
	}

	public float getParameterAsFloat(String name, float defaultValue)
	{
		assert name != null;

		if (getParameter(name) == null)
		{
			return defaultValue;
		}

		return getParameterAsFloat(name);
	}

	public int getParameterAsInt(String name)
	{
		assert name != null;

		Converter c = ConvertUtils.lookup(java.lang.Integer.class);

		return ((Integer) c.convert(java.lang.Integer.class, getParameterAsString(name))).intValue();
	}

	public int getParameterAsInt(String name, int defaultValue)
	{
		assert name != null;

		Object val = getParameter(name);

		if (val == null || "".equals(val))
		{
			return defaultValue;
		}

		return getParameterAsInt(name);
	}

	public List getParameterAsList(String name)
	{
		assert name != null;

		Object[] arr = getParameterAsArray(name);
		ArrayList l = new ArrayList();

		for (int i = 0; i < arr.length; i++)
		{
			l.add(arr[i]);
		}

		return l;
	}

	public List getParameterAsList(String name, List defaultValue)
	{
		assert name != null;

		Object val = getParameter(name);

		if (val == null)
		{
			return defaultValue;
		}

		return getParameterAsList(name);
	}

	public long getParameterAsLong(String name)
	{
		assert name != null;

		Converter c = ConvertUtils.lookup(java.lang.Long.class);

		return ((Long) c.convert(java.lang.Long.class, getParameterAsString(name))).longValue();
	}

	public long getParameterAsLong(String name, long defaultValue)
	{
		assert name != null;

		if (getParameter(name) == null)
		{
			return defaultValue;
		}

		return getParameterAsLong(name);
	}

	public String getParameterAsString(String name)
	{
		assert name != null;

		Object val = getParameter(name);

		if (val == null)
		{
			return "";
		}

		if (val instanceof String[])
		{
			val = ((String[]) val)[0];
		}

		return val.toString();
	}

	public String getParameterAsString(String name, String defaultValue)
	{
		assert name != null;

		Object val = getParameter(name);

		if (val == null)
		{
			return defaultValue;
		}

		return val.toString();
	}
}
