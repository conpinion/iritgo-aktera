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
import javax.servlet.jsp.JspException;


/**
 *
 */
public class MessageTag extends org.apache.struts.taglib.bean.MessageTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Execute the tag.
	 *
	 * @return SKIP_BODY.
	 */
	@Override
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

		Object[] args = new Object[10];

		args[0] = arg0;
		args[1] = arg1;
		args[2] = arg2;
		args[3] = arg3;
		args[4] = arg4;
		args[5] = "";
		args[6] = "";
		args[7] = "";
		args[8] = "";
		args[9] = "";

		if (key.indexOf("|") != - 1)
		{
			String[] parts = key.split("\\|");
			key = parts[0];
			for (int i = 1; i < Math.min(parts.length, 10); ++i)
			{
				if (! "".equals(parts[i]))
				{
					if (parts[i].length() >= 2 && parts[i].startsWith("$") && Character.isLetter(parts[i].charAt(1)))
					{
						String val = parts[i].substring(1);
						String bundle = this.bundle;
						int colonPos = val.indexOf(':');
						if (colonPos != - 1)
						{
							bundle = val.substring(0, colonPos);
							val = val.substring(colonPos + 1);
						}
						args[i - 1] = TagUtils.getInstance().message(pageContext, bundle, this.localeKey, val);
					}
					else
					{
						args[i - 1] = parts[i];
					}
				}
				else
				{
					args[i - 1] = " ";
				}
			}
		}

		String bundle = this.bundle;
		if (key.startsWith("$"))
		{
			String[] parts = key.substring(1).split(":");
			if (parts.length > 1)
			{
				bundle = parts[0];
				key = parts[1];
			}
			else
			{
				key = parts[0];
			}
		}

		String message = TagUtils.getInstance().message(pageContext, bundle, this.localeKey, key, args);

		if (message == null)
		{
			JspException x = new JspException(messages.getMessage("message.message", "\"" + key + "\""));
			TagUtils.getInstance().saveException(pageContext, x);
			throw x;
		}

		TagUtils.getInstance().write(pageContext, message);

		return (SKIP_BODY);
	}
}
