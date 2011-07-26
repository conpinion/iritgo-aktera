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

package de.iritgo.aktera.base.tools;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;


/**
 * Redirect to another URL.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.redirect"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.redirect" id="aktera.redirect" logger="aktera"
 */
public class Redirect extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param request The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest request) throws ModelException
	{
		ModelResponse response = request.createResponse ();

		response.setAttribute ("forward", "aktera.redirect");

		String scheme = request.getScheme ();
		String serverName = request.getServerName ();
		int port = request.getServerPort ();
		String baseUrl = scheme + "://" + serverName + (port != ("https".equals (scheme) ? 443 : 80) ? ":" + port : "");
		String contextPath = request.getContextPath ();

		String url = configuration.getChild ("url").getValue ("#");

		url = url.replaceAll ("\\#\\{scheme\\}", scheme);
		url = url.replaceAll ("\\#\\{server\\}", serverName);
		url = url.replaceAll ("\\#\\{port\\}", String.valueOf (port));
		url = url.replaceAll ("\\#\\{context\\}", contextPath);
		url = url.replaceAll ("\\#\\{baseUrl\\}", baseUrl);
		response.addOutput ("url", url);

		return response;
	}
}
