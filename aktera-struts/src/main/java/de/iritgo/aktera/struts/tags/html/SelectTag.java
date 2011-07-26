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
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;


/**
 * ReadOnly-able SelectTag.
 */
public class SelectTag extends org.apache.struts.taglib.html.SelectTag
{
	/** Read only. */
	protected boolean readOnly = false;

	/**
	 * Set the read only flag.
	 *
	 * @param readOnly Read only flag.
	 */
	public void setReadOnly (boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Get the read only flag.
	 *
	 * @retrun The read only flag.
	 */
	public boolean getReadOnly ()
	{
		return readOnly;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release ()
	{
		super.release ();

		readOnly = false;
	}

	/**
	 * Render the beginning of this select tag.
	 *
	 * Support for indexed property since Struts 1.1
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag () throws JspException
	{
		if (! readOnly)
		{
			return super.doStartTag ();
		}

		pageContext.setAttribute (Constants.SELECT_KEY, this);

		this.myCalculateMatchValues ();

		return EVAL_BODY_TAG;
	}

	/**
	 * Calculate the match values we will actually be using.
	 * @throws JspException
	 */
	private void myCalculateMatchValues () throws JspException
	{
		if (this.value != null)
		{
			this.match = new String[1];
			this.match[0] = this.value;
		}
		else
		{
			Object bean = TagUtils.getInstance ().lookup (pageContext, name, null);

			if (bean == null)
			{
				JspException x = new JspException (messages.getMessage ("getter.bean", name));

				TagUtils.getInstance ().saveException (pageContext, x);
				throw x;
			}

			try
			{
				this.match = BeanUtils.getArrayProperty (bean, property);

				if (this.match == null)
				{
					this.match = new String[0];
				}
			}
			catch (IllegalAccessException x)
			{
				TagUtils.getInstance ().saveException (pageContext, x);
				throw new JspException (messages.getMessage ("getter.access", property, name));
			}
			catch (InvocationTargetException x)
			{
				Throwable t = x.getTargetException ();

				TagUtils.getInstance ().saveException (pageContext, t);
				throw new JspException (messages.getMessage ("getter.result", property, t.toString ()));
			}
			catch (NoSuchMethodException x)
			{
				TagUtils.getInstance ().saveException (pageContext, x);
				throw new JspException (messages.getMessage ("getter.method", property, name));
			}
		}
	}

	/**
	 * Render the end of this form.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doEndTag () throws JspException
	{
		if (! readOnly)
		{
			return super.doEndTag ();
		}

		if (saveBody != null)
		{
			TagUtils.getInstance ().write (pageContext, saveBody);
		}

		pageContext.removeAttribute (Constants.SELECT_KEY);

		return EVAL_PAGE;
	}
}
