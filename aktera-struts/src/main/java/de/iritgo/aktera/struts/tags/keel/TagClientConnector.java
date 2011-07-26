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


import de.iritgo.aktera.clients.ClientException;
import de.iritgo.aktera.clients.webapp.AbstractWebappClientConnector;
import de.iritgo.aktera.clients.webapp.DefaultWebappRequest;
import de.iritgo.aktera.clients.webapp.DefaultWebappResponse;
import de.iritgo.aktera.clients.webapp.WebappRequest;
import de.iritgo.aktera.clients.webapp.WebappResponse;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A webapp client connector for tags.
 */
public class TagClientConnector extends AbstractWebappClientConnector
{
	/** The http request the tag was called with. */
	private WebappRequest req;

	/** The http response. */
	private WebappResponse res;

	/** The name of the model to call. */
	private String model;

	/**
	 * Create a new <code>TagClientConnector</code>
	 */
	public TagClientConnector ()
	{
		req = null;
		res = null;
		model = null;
	}

	/**
	 * Create a new <code>TagClientConnector</code>
	 *
	 * @param req The http request the tag was called with.
	 * @param res The http response.
	 * @param model The name of the model to call.
	 */
	public TagClientConnector (HttpServletRequest req, HttpServletResponse res, String model)
	{
		this.req = new DefaultWebappRequest (req);
		this.res = new DefaultWebappResponse (res);
		this.model = model;
	}

	/**
	 * Set the http request.
	 *
	 * @param req The http request.
	 */
	public void setRequest (HttpServletRequest req)
	{
		this.req = new DefaultWebappRequest (req);
	}

	/**
	 * Set the http response.
	 *
	 * @param req The http response.
	 */
	public void setResponse (HttpServletResponse res)
	{
		this.res = new DefaultWebappResponse (res);
	}

	/**
	 * Set the webapp request.
	 *
	 * @param req The webapp request.
	 */
	public void setRequest (WebappRequest req)
	{
		this.req = req;
	}

	/**
	 * Set the webapp response.
	 *
	 * @param res The webapp response.
	 */
	public void setResponse (WebappResponse res)
	{
		this.res = res;
	}

	/**
	 * Set the name of the model to call.
	 *
	 * @param model The model name.
	 */
	public void setModel (String model)
	{
		this.model = model;
	}

	/**
	 * Execute the model.
	 *
	 * @return The model response.
	 */
	public KeelResponse execute () throws ClientException, ModelException
	{
		return super.execute (req, res, model);
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappClientConnector#getForward()
	 */
	public String getForward (KeelResponse kres)
	{
		return null;
	}

	/**
	 * Convert a webapp-request to a Keel-request.
	 * This overriddeb method <i>always</i> sets the model name to the
	 * provided default model name, since this is the model we want to
	 * execute with the callModel tag.
	 *
	 * @param wreq The supplied webapp request
	 * @param defaultModelName The model-name to use if none in params-list
	 * @return The Keel request
	 * @throws ClientException
	 */
	protected KeelRequest makeKeelRequest (WebappRequest wreq, String defaultModelName) throws ClientException
	{
		KeelRequest kreq = super.makeKeelRequest (wreq, defaultModelName);

		kreq.setModel (defaultModelName);

		return kreq;
	}
}
