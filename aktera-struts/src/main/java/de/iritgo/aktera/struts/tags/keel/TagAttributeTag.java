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


import de.iritgo.aktera.struts.tags.BaseBodyTagSupport;
import org.apache.commons.beanutils.PropertyUtils;
import javax.servlet.jsp.JspException;


/**
 * Set an attribute of the parent tag.
 */
public class TagAttributeTag extends BaseBodyTagSupport
{
	/** */
	private static final long serialVersionUID = 1L;

	/** The attribute name. */
	private String name;

	/** The attribute value. Can also be specified as body content. */
	private String value;

	/**
	 * Set the attribute name.
	 *
	 * @param name The attribute name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Get the attribute name.
	 *
	 * @return The attribute name.
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the attribute value.
	 *
	 * @param value The attribute value.
	 */
	public void setValue (String value)
	{
		this.value = value;
	}

	/**
	 * Get the attribute value.
	 *
	 * @return The attribute value.
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release ()
	{
		name = null;
		value = null;
	}

	/**
	 * If the tag has a body, use it's content as the parameter value.
	 *
	 * @return SKIP_BODY.
	 */
	public int doAfterBody () throws JspException
	{
		try
		{
			if (value == null)
			{
				value = bodyContent.getString ();
			}

			bodyContent.clear ();

			return SKIP_BODY;
		}
		catch (Exception x)
		{
			throw new JspException (x);
		}
	}

	/**
	 * Execute the tag.
	 *
	 * @return EVAL_PAGE.
	 */
	public int doEndTag () throws JspException
	{
		if (name == null)
		{
			throw new JspException ("[TagAttributeTag] No attribute name specified");
		}

		try
		{
			PropertyUtils.setProperty (getParent (), name, value);

			return EVAL_PAGE;
		}
		catch (Exception x)
		{
			throw new JspException (x);
		}
	}
}
