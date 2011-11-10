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


import de.iritgo.aktera.clients.ResponseElementDynaBean;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.struts.tags.BaseBodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.util.Iterator;


/**
 * This is a tag that directly executes a model.
 */
public class CallModelTag extends BaseBodyTagSupport
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Our client connector. */
	private TagClientConnector client;

	/** Our logger. */
	Log log;

	/** The model to call. */
	private String model;

	/** Model request. */
	private TagRequest request;

	/** The scope in which to store the model results. */
	private String scope;

	/**
	 * Create a new <code>CallModel</code> tag.
	 */
	public CallModelTag()
	{
		super();

		log = LogFactory.getFactory().getInstance("de.iritgo.aktera.struts.ModelAction");
		client = new TagClientConnector();
		client.setLogger(log);
	}

	/**
	 * Set the name of the model to call.
	 *
	 * @param model The model name.
	 */
	public void setModel(String model)
	{
		this.model = model;
	}

	/**
	 * Get the name of the model to call.
	 *
	 * @return The model name.
	 */
	public String getModel()
	{
		return model;
	}

	/**
	 * Set a model parameter.
	 *
	 * @param name The parameter name.
	 * @param value The parameter value.
	 */
	public void setParameter(String name, String value)
	{
		request.setParameter(name, value);
	}

	/**
	 * Set the name of the result scope.
	 *
	 * @param scope The scope name.
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	/**
	 * Get the name of the result scope.
	 *
	 * @return The scope name.
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		model = null;
		request = new TagRequest();
		scope = "request";
	}

	/**
	 * Execute the model.
	 *
	 * @return EVAL_PAGE.
	 */
	public int doEndTag() throws JspException
	{
		try
		{
			request.setRequest((HttpServletRequest) pageContext.getRequest());
			client.setRequest(request);
			client.setResponse((HttpServletResponse) pageContext.getResponse());
			client.setModel(model);

			KeelResponse response = client.execute();

			for (Iterator i = response.getAll(); i.hasNext();)
			{
				ResponseElement element = (ResponseElement) i.next();

				if (element instanceof Output)
				{
					ResponseElementDynaBean elementAsBean = new ResponseElementDynaBean(element);

					pageContext.setAttribute(element.getName(), elementAsBean, getScope(scope));
				}
			}

			return EVAL_PAGE;
		}
		catch (Exception x)
		{
			StackTraceElement ste = (x.getStackTrace())[0];

			throw new JspException(new StringBuffer().append("CallModelTag: ").append(x).append(" (Class: ").append(
							ste.getClassName()).append(" Method: ").append(ste.getMethodName()).append(" Line: ")
							.append(ste.getLineNumber()).append(")").toString());
		}
	}
}
