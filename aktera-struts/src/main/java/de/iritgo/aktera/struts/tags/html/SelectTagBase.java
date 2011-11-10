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

package de.iritgo.aktera.struts.tags.html;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.util.RequestUtils;
import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;


/**
 * Base class for select tags.
 */
public class SelectTagBase extends BaseHandlerTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/** The name of the bean containing our underlying property. */
	protected String name = Constants.BEAN_KEY;

	/** The actual values we will match against, calculated in doStartTag(). */
	protected String[] match = null;

	/**
	 * The property name we are associated with.
	 */
	protected String property;

	/**
	 * How many available options should be displayed when this element
	 * is rendered?
	 */
	protected String size = null;

	/**
	 * The name of the servlet context attribute containing our message
	 * resources.
	 */
	protected String bundle = Globals.MESSAGES_KEY;

	/**
	 * The name of the attribute containing the Locale to be used for
	 * looking up internationalized messages.
	 */
	protected String locale = Globals.LOCALE_KEY;

	/**
	 * Get the bean name.
	 *
	 * @return The bean name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Set the bean name.
	 *
	 * @param name The new bean name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Return the property name.
	 *
	 * @return The property name.
	 */
	public String getProperty()
	{
		return this.property;
	}

	/**
	 * Set the property name.
	 *
	 * @param property The new property name.
	 */
	public void setProperty(String property)
	{
		this.property = property;
	}

	/**
	 * Get the size.
	 *
	 * @return The size.
	 */
	public String getSize()
	{
		return this.size;
	}

	/**
	 * Set the size.
	 *
	 * @param size The new size.
	 */
	public void setSize(String size)
	{
		this.size = size;
	}

	/**
	 * Get the bundle.
	 *
	 * @return The bundle.
	 */
	public String getBundle()
	{
		return this.bundle;
	}

	/**
	 * Set the bundle.
	 *
	 * @param bundle The new bundle.
	 */
	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the locale.
	 *
	 * @return The locale.
	 */
	public String getLocale()
	{
		return this.locale;
	}

	/**
	 * Set the locale.
	 *
	 * @param locale The new locale.
	 */
	public void setLocale(String locale)
	{
		this.locale = locale;
	}

	/**
	 * Crerate a select tag.
	 *
	 * @param suffix An optional element suffix.
	 * @param results StringBuffer which receives the tag code.
	 */
	protected void createSelectTag(StringBuffer results, String suffix) throws JspException
	{
		results.append("<select name=\"");

		if (this.indexed)
		{
			prepareIndex(results, name);
		}

		results.append(suffix == null ? property : property + suffix);
		results.append("\"");

		if (accesskey != null)
		{
			results.append(" accesskey=\"");
			results.append(accesskey);
			results.append("\"");
		}

		if (size != null)
		{
			results.append(" size=\"");
			results.append(size);
			results.append("\"");
		}

		if (tabindex != null)
		{
			results.append(" tabindex=\"");
			results.append(tabindex);
			results.append("\"");
		}

		results.append(prepareEventHandlers());
		results.append(prepareStyles());
		results.append(">");
	}

	/**
	 * Crerate a select tag.
	 *
	 * @param results StringBuffer which receives the tag code.
	 */
	protected void createSelectTag(StringBuffer results) throws JspException
	{
		createSelectTag(results, null);
	}

	/**
	 * Release any acquired resources.
	 */
	public void release()
	{
		super.release();
		bundle = Globals.MESSAGES_KEY;
		name = Constants.BEAN_KEY;
		locale = Globals.LOCALE_KEY;
		property = null;
		size = null;
	}

	/**
	 * Retrieve the property of our bean.
	 *
	 * @param suffix Optional property suffix.
	 * @return The bean property value.
	 */
	protected Object getNamedBeanProperty(String propertyName) throws JspException
	{
		Object bean = TagUtils.getInstance().lookup(pageContext, name, null);

		if (bean == null)
		{
			JspException e = new JspException(messages.getMessage("getter.bean", name));

			TagUtils.getInstance().saveException(pageContext, e);
			throw e;
		}

		try
		{
			return BeanUtils.getProperty(bean, propertyName);
		}
		catch (IllegalAccessException e)
		{
			TagUtils.getInstance().saveException(pageContext, e);
			throw new JspException(messages.getMessage("getter.access", propertyName, name));
		}
		catch (InvocationTargetException e)
		{
			Throwable t = e.getTargetException();

			TagUtils.getInstance().saveException(pageContext, t);
			throw new JspException(messages.getMessage("getter.result", propertyName, t.toString()));
		}
		catch (NoSuchMethodException e)
		{
			TagUtils.getInstance().saveException(pageContext, e);
			throw new JspException(messages.getMessage("getter.method", propertyName, name));
		}
	}

	/**
	 * Retrieve the property of our bean.
	 *
	 * @param suffix Optional property suffix.
	 * @return The bean property value.
	 */
	protected Object getBeanProperty(String suffix) throws JspException
	{
		return getNamedBeanProperty(suffix == null ? property : property + suffix);
	}

	/**
	 * Retrieve the property of our bean.
	 *
	 * @return The bean property value.
	 */
	protected Object getBeanProperty() throws JspException
	{
		return getBeanProperty(null);
	}
}
