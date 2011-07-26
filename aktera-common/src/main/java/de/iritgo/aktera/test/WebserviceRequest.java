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

package de.iritgo.aktera.test;


import java.util.HashMap;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;


public class WebserviceRequest
{

	public class ParameterType
	{
		public String value;

		public Class propertyInfo;

		public ParameterType (Class propertyInfo, String value)
		{
			this.propertyInfo = propertyInfo;
			this.value = value;
		}
	}

	private SoapObject request;

	private HashMap<String, ParameterType> parameters;

	private String username;

	private String password;

	private String server;

	private String webserviceUrl;

	public WebserviceRequest (SoapObject request, String username, String password, String server, String webserviceUrl)
	{
		parameters = new HashMap<String, ParameterType> ();
		this.request = request;
		this.username = username;
		this.password = password;
		this.server = server;
		this.webserviceUrl = webserviceUrl;
	}

	public WebserviceRequest withStringParam (String name, String value)
	{
		parameters.put (name, new ParameterType (PropertyInfo.STRING_CLASS, value));
		return this;
	}

	public WebserviceRequest and ()
	{
		return this;
	}

	public Object send ()
	{
		SEnvelope envelope = WebserviceTools.createEnvelope (request, username, password);
		for (String serviceName : parameters.keySet ())
		{
			ParameterType parameterType = parameters.get (serviceName);

			WebserviceTools.addRequestParameter (request, serviceName, parameterType.propertyInfo, parameterType.value);
		}
		return WebserviceTools.sendReturnObject (envelope, server, webserviceUrl, false);
	}
}
