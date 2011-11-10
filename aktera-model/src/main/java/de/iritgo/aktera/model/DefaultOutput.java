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


import de.iritgo.aktera.model.Output;


/**
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Output
 * @x-avalon.info name=default-output
 * @x-avalon.lifestyle type=transient
 *
 */
public class DefaultOutput extends AbstractResponseElement implements Output
{
	private Object content = null;

	public synchronized void setContent(Object newContent)
	{
		if (newContent == null)
		{
			content = new String();
		}
		else
		{
			assert newContent != this;
			content = newContent;
		}
	}

	public Object getContent()
	{
		if (content == null)
		{
			return new String();
		}

		return content;
	}

	public String toString()
	{
		StringBuffer ret = new StringBuffer(super.toString());

		if (content == null)
		{
			ret.append("<content null>\n");
		}
		else
		{
			ret.append("<content type='" + content.getClass().getName() + "'>\n");
			ret.append(content.toString() + "\n");
			ret.append("</content>");
		}

		ret.append("\t</" + getClass().getName() + ">\n");

		return ret.toString();
	}
}
