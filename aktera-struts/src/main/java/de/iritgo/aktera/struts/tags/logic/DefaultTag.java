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

package de.iritgo.aktera.struts.tags.logic;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * This tag is used to implement switch/case like structures.
 */
public class DefaultTag extends TagSupport
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release ()
	{
		super.release ();
	}

	/**
	 * Execute the tag.
	 */
	public int doStartTag () throws JspException
	{
		try
		{
			SwitchTag switchTag = (SwitchTag) findAncestorWithClass (this, SwitchTag.class);

			if (switchTag == null)
			{
				throw new JspException ("[CaseTag] Can only be used inside a SwitchTag tag");
			}

			if (! switchTag.isCaseApplied ())
			{
				return EVAL_BODY_INCLUDE;
			}
			else
			{
				return SKIP_BODY;
			}
		}
		catch (Exception x)
		{
			throw new JspException (x);
		}
	}
}
