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

/*
 * Copyright (c) 2002, The Keel Group, Ltd. All rights reserved.
 *
 * This software is made available under the terms of the license found
 * in the LICENSE file, included with this source code. The license can
 * also be found at:
 * http://www.keelframework.net/LICENSE
 */

package de.iritgo.aktera.struts;


import java.util.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.struts.taglib.*;
import org.apache.struts.taglib.html.*;
import de.iritgo.aktera.clients.*;
import de.iritgo.aktera.model.*;


/**
 * @author Michael Nash
 * @version $Revision: 1.8 $ $Date: 2004/02/23 21:26:58 $
 */
public class CommandTag extends SubmitTag
{
	// ----------------------------------------------------- Instance Variables

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the generated input field.
	 */
	protected String property = null;

	protected String name = null;

	/**
	 * The body content of this tag (if any).
	 */
	protected String text = null;

	/**
	 * The value of the button label.
	 */
	protected String value = null;

	protected String target = null;

	/**
	 * Name of the bean containing the command we're displaying
	 */
	protected String command = null;

	protected String link = null;

	private String commandName = null;

	private String commandModel = null;

	private String commandBean = null;

	private String commandLabel = null;

	// Added by Brian Rosenthal - 01/29/03
	private String commandImagePath = null;

	private String scriptMethod = null; //Variable and related code Added by Phil Brown - 7/11/03

	private StringBuffer modelWithParams = null;

	private Map cp = null;

	// --------------------------------------------------------- Public Methods
	public void setLink (String newLink)
	{
		link = newLink;
	}

	public String getLink ()
	{
		return link;
	}

	public void setName (String newName)
	{
		name = newName;
	}

	public String getName ()
	{
		return name;
	}

	// Added by Brian Rosenthal - 01/29/03
	public void setCommandImagePath (String pathToImage)
	{
		commandImagePath = pathToImage;
	}

	public String getCommandImagePath ()
	{
		return commandImagePath;
	}

	public String getTarget ()
	{
		return target;
	}

	public void setTarget (String newTarget)
	{
		target = newTarget;
	}

	public void setProperty (String newProperty)
	{
		property = newProperty;
	}

	public String getProperty ()
	{
		return property;
	}

	/**
	 * Process the start of this tag.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag () throws JspException
	{
		StringBuffer results = new StringBuffer ();

		Object o = TagUtils.getInstance ().lookup (pageContext, name, property, null);

		if (o instanceof ResponseElementDynaBean)
		{
			ResponseElementDynaBean bean = (ResponseElementDynaBean) o;
			String type = (String) bean.get ("type");

			if (! type.equals ("command"))
			{
				throw new JspException ("Element '" + name + "/" + property + "' was not a command, it was a '" + type
								+ "'");
			}

			commandName = (String) bean.get ("name");
			commandModel = (String) bean.get ("model");
			commandBean = (String) bean.get ("bean");
			commandLabel = (String) bean.get ("label");
			cp = (Map) bean.get ("parameters");
		}
		else if (o instanceof Command)
		{
			Command c = (Command) o;

			commandName = c.getName ();
			commandModel = c.getModel ();
			commandBean = c.getBean ();
			commandLabel = c.getLabel ();
			cp = c.getParameters ();
		}
		else
		{
			throw new JspException ("Element '" + name + "/" + property
							+ "' was not a ResponseElementDynaBean or a Command, it was a '" + o.getClass ().getName ()
							+ "'");
		}

		setAltKey (null);
		setAlt (commandLabel);
		setTitleKey (null);
		setTitle (commandLabel);

		if (link == null)
		{
			results.append ("<input type=\"hidden\" name=\"PARAMS_");
			results.append (commandName);

			modelWithParams = new StringBuffer (commandBean != null ? commandBean : commandModel);

			String oneKey = null;
			Object oneValue = null;

			for (Iterator i = cp.keySet ().iterator (); i.hasNext ();)
			{
				oneKey = (String) i.next ();
				oneValue = cp.get (oneKey);

				if (oneValue != null)
				{
					modelWithParams.append ("&");
					modelWithParams.append (oneKey);
					modelWithParams.append ("=");
					modelWithParams.append (oneValue.toString ());
				}
			}

			results.append ("\" value=\"");
			results.append (commandBean != null ? "bean=" : "model=");
			results.append (modelWithParams);
			results.append ("\"");
			results.append (prepareEventHandlers ());
			results.append (prepareStyles ());
			results.append ("/>\n");

			TagUtils.getInstance ().write (pageContext, results.toString ());
			this.text = null;

			return (EVAL_BODY_BUFFERED);
		}
		else
		{
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest ();

			StringBuffer urlString = new StringBuffer (request.getContextPath ()
							+ (commandBean != null ? "/bean.do?" : "/model.do?"));

			results.append ("<a href=\"");

			String oneKey = null;
			Object oneValue = null;

			modelWithParams = new StringBuffer (commandBean != null ? commandBean : commandModel);

			for (Iterator i = cp.keySet ().iterator (); i.hasNext ();)
			{
				oneKey = (String) i.next ();
				oneValue = cp.get (oneKey);

				if (oneValue != null)
				{
					modelWithParams.append ("&");
					modelWithParams.append (oneKey);
					modelWithParams.append ("=");
					modelWithParams.append (oneValue.toString ());
				}
			}

			urlString.append (commandBean != null ? "bean=" : "model=");
			urlString.append (modelWithParams);

			HttpServletResponse response = (HttpServletResponse) pageContext.getResponse ();

			results.append (response.encodeURL (urlString.toString ()));
			results.append ("\"");

			if (target != null)
			{
				results.append (" target=\"" + target + "\"");
			}

			results.append (prepareEventHandlers ());
			results.append (prepareStyles ());
			results.append (">\n");

			TagUtils.getInstance ().write (pageContext, results.toString ());

			return (EVAL_BODY_INCLUDE);
		}
	}

	/**
	 * Save the associated label from the body content.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doAfterBody () throws JspException
	{
		/* if (bodyContent != null) {
		    String value = bodyContent.getString().trim();
		    if (value.length() > 0)
		        text = value;
		} */
		if (link != null)
		{
			return (EVAL_BODY_INCLUDE);
		}
		else
		{
			return (SKIP_BODY);
		}
	}

	/**
	 * Process the end of this tag.
	 * Indexed property since 1.1
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doEndTag () throws JspException
	{
		if (link == null)
		{
			// Acquire the label value we will be generating
			String label = value;

			if ((label == null) && (text != null))
			{
				label = text;
			}

			if ((label == null) || (label.length () < 1))
			{
				label = "Submit";
			}

			// Generate an HTML element
			StringBuffer results = new StringBuffer ();

			// Added by Brian Rosenthal - 01/29/03
			// If specified, make the button of type "image."
			if (commandImagePath != null)
			{
				results.append ("<input type=\"image\" src=\"" + commandImagePath + "\" border=\"0\" name=\"");
			}
			else
			{
				results.append ("<input type=\"submit\" name=\"");
			}

			results.append ("COMMAND_" + commandName);

			// since 1.1
			if (indexed)
			{
				prepareIndex (results, null);
			}

			results.append ("\"");

			if (accesskey != null)
			{
				results.append (" accesskey=\"");
				results.append (accesskey);
				results.append ("\"");
			}

			if (tabindex != null)
			{
				results.append (" tabindex=\"");
				results.append (tabindex);
				results.append ("\"");
			}

			if (scriptMethod != null)
			{
				results.append (" onclick=\"");
				results.append (scriptMethod);
				results.append ("('");
				results.append (modelWithParams);
				results.append ("');");
				results.append ("\"");
			}

			results.append (" value=\"");
			results.append (commandLabel);
			results.append ("\"");
			setAlt (commandLabel);
			results.append (prepareEventHandlers ());
			results.append (prepareStyles ());
			results.append (">");

			// Render this element to our writer
			TagUtils.getInstance ().write (pageContext, results.toString ());
		}
		else
		{
			TagUtils.getInstance ().write (pageContext, "</a>");
		}

		// Evaluate the remainder of this page
		return (EVAL_PAGE);
	}

	protected String message (String literal, String key) throws JspException
	{
		return literal;
	}

	/**
	 * Release any acquired resources.
	 */
	public void release ()
	{
		super.release ();
		name = null;
		property = null;
		text = null;
		value = null;
		link = null;
	}

	/**
	 * @return
	 */
	public String getScriptMethod ()
	{
		return scriptMethod;
	}

	/**
	 * @param string
	 */
	public void setScriptMethod (String string)
	{
		scriptMethod = string;
	}
}
