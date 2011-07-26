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


import de.iritgo.simplelife.math.NumberTools;
import org.apache.commons.io.FileUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import javax.servlet.jsp.JspException;


/**
 *
 */
public class ByteSizeTag extends org.apache.struts.taglib.html.BaseInputTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the bean containing our underlying property.
	 */
	protected String name = Constants.BEAN_KEY;

	/** Read only. */
	protected boolean readOnly = false;

	/**
	 * Get the bean name.
	 */
	public String getName ()
	{
		return this.name;
	}

	/**
	 * Set the bean name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

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

		name = null;
		readOnly = false;
	}

	/**
	 * Generate the required input tag.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag () throws JspException
	{
		long val = 0;

		if (value != null)
		{
			val = NumberTools.toLong (value.toString (), 0);
		}
		else
		{
			val = NumberTools.toLong (TagUtils.getInstance ().lookup (pageContext, name, property, null), 0);
		}

		StringBuffer results = new StringBuffer ();

		results.append (FileUtils.byteCountToDisplaySize (val));

		TagUtils.getInstance ().write (pageContext, results.toString ());

		return EVAL_BODY_BUFFERED;
	}
}
