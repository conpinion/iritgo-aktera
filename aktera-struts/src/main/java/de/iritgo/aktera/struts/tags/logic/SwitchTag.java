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


/**
 * This tag is used to implement switch/case like structures.
 */
public class SwitchTag extends org.apache.struts.taglib.logic.CompareTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** True if a case option was applied. */
	protected boolean caseApplied;

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release ()
	{
		super.release ();

		caseApplied = false;
	}

	/**
	 * The switch tag always includes it's body.
	 * The condition is check in the CaseTag children.
	 */
	protected boolean condition () throws JspException
	{
		return true;
	}

	/**
	 * Called by the CaseTag children to check the condition against
	 * their value.
	 */
	public boolean checkCondition () throws JspException
	{
		boolean check = condition (0, 0);

		caseApplied |= check;

		return check;
	}

	/**
	 * Check if a case option was applied.
	 *
	 * @return True if a case option was applied.
	 */
	public boolean isCaseApplied ()
	{
		return caseApplied;
	}

	/**
	 * Execute the tag.
	 */
	public int doStartTag () throws JspException
	{
		caseApplied = false;

		return super.doStartTag ();
	}
}
