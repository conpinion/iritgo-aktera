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


import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.RequestUtils;
import javax.servlet.http.HttpServletResponse;


/**
 * This creates a HTML form Tag.
 */
public class FormTag extends org.apache.struts.taglib.html.FormTag
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Form name. */
	protected String formName;

	/**
	 * Set the form name.
	 *
	 * @param formName The new form name.
	 */
	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	/**
	 * Get the form name.
	 *
	 * @return The form name.
	 */
	public String getFormName()
	{
		return formName;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		super.release();

		formName = null;
	}

	/**
	 * Generates the opening <code>&lt;form&gt;</code> element with appropriate
	 * attributes.
	 * @since Struts 1.1
	 */
	protected String renderFormStartElement()
	{
		HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();

		StringBuffer results = new StringBuffer("<form");

		results.append(" name=\"");

		if (beanName != null && ! ("none".equals(beanName) && formName != null))
		{
			results.append(beanName);
		}
		else if (formName != null)
		{
			results.append(formName);
		}

		results.append("\"");
		results.append(" method=\"");
		results.append(method == null ? "post" : method);
		results.append("\" action=\"");
		results.append(response.encodeURL(TagUtils.getInstance().getActionMappingURL(this.action, this.pageContext)));

		results.append("\"");

		if (styleClass != null)
		{
			results.append(" class=\"");
			results.append(styleClass);
			results.append("\"");
		}

		if (enctype != null)
		{
			results.append(" enctype=\"");
			results.append(enctype);
			results.append("\"");
		}

		if (onreset != null)
		{
			results.append(" onreset=\"");
			results.append(onreset);
			results.append("\"");
		}

		if (onsubmit != null)
		{
			results.append(" onsubmit=\"");
			results.append(onsubmit);
			results.append("\"");
		}

		if (style != null)
		{
			results.append(" style=\"");
			results.append(style);
			results.append("\"");
		}

		if (styleId != null)
		{
			results.append(" id=\"");
			results.append(styleId);
			results.append("\"");
		}

		if (target != null)
		{
			results.append(" target=\"");
			results.append(target);
			results.append("\"");
		}

		results.append(">");

		return results.toString();
	}

	/**
	 * Generates javascript to set the initial focus to the form element given in the
	 * tag's "focus" attribute.
	 * @since Struts 1.1
	 */
	protected String renderFocusJavascript()
	{
		if (this.focus == null || "".equals(this.focus))
		{
			return "";
		}

		StringBuffer results = new StringBuffer();

		results.append(lineEnd);
		results.append("<script type=\"text/javascript\"");

		if (! TagUtils.getInstance().isXhtml(this.pageContext))
		{
			results.append(" language=\"JavaScript\"");
		}

		results.append(">");
		results.append(lineEnd);

		if (! TagUtils.getInstance().isXhtml(this.pageContext))
		{
			results.append("  <!--");
			results.append(lineEnd);
		}

		StringBuffer focusControl = new StringBuffer("document.forms[\"");

		if (beanName != null && ! ("none".equals(beanName) && formName != null))
		{
			focusControl.append(beanName);
		}
		else if (formName != null)
		{
			focusControl.append(formName);
		}

		focusControl.append("\"].elements[\"");
		focusControl.append(this.focus);
		focusControl.append("\"]");

		results.append("  var focusControl = ");
		results.append(focusControl.toString());
		results.append(";");
		results.append(lineEnd);
		results.append(lineEnd);

		results.append("  if (focusControl && focusControl.type != \"hidden\") {");
		results.append(lineEnd);

		String index = "";

		if (this.focusIndex != null)
		{
			StringBuffer sb = new StringBuffer("[");

			sb.append(this.focusIndex);
			sb.append("]");
			index = sb.toString();
		}

		results.append("     focusControl");
		results.append(index);
		results.append(".focus();");
		results.append(lineEnd);

		results.append("  }");
		results.append(lineEnd);

		if (! TagUtils.getInstance().isXhtml(this.pageContext))
		{
			results.append("  // -->");
			results.append(lineEnd);
		}

		results.append("</script>");
		results.append(lineEnd);

		return results.toString();
	}
}
