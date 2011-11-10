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

package de.iritgo.aktera.struts.tags.bean;


import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;
import java.util.regex.PatternSyntaxException;


/**
 * This creates a form label.
 */
public class LabelTag extends org.apache.struts.taglib.bean.MessageTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Execute the tag.
	 *
	 * @return SKIP_BODY.
	 */
	public int doStartTag() throws JspException
	{
		String key = this.key;

		if (key == null)
		{
			Object value = TagUtils.getInstance().lookup(pageContext, name, property, scope);

			if (value != null && ! (value instanceof String))
			{
				JspException x = new JspException(messages.getMessage("message.property", key));

				TagUtils.getInstance().saveException(pageContext, x);
				throw x;
			}

			key = (String) value;
		}

		Object[] args = new Object[5];

		args[0] = arg0;
		args[1] = arg1;
		args[2] = arg2;
		args[3] = arg3;
		args[4] = arg4;

		String message = TagUtils.getInstance().message(pageContext, this.bundle, this.localeKey, key, args);

		if (message == null)
		{
			JspException x = new JspException(messages.getMessage("message.message", "\"" + key + "\""));

			TagUtils.getInstance().saveException(pageContext, x);
			throw x;
		}

		try
		{
			if (! message.matches("^(\\s|&nbsp;)*$"))
			{
				message = message + ":";
			}
		}
		catch (PatternSyntaxException x)
		{
		}

		TagUtils.getInstance().write(pageContext, message);

		return (SKIP_BODY);
	}
}
