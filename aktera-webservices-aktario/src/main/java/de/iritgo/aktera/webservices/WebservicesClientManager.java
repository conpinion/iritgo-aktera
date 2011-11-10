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

package de.iritgo.aktera.webservices;


import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.framework.appcontext.AppContext;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.Vector;


public class WebservicesClientManager extends BaseObject implements Manager
{
	public static final String ID = "de.iritgo.aktera.webservices.WebservicesClientManager";

	private final String ANY_TYPE = "anyType{}";

	public WebservicesClientManager()
	{
		super(ID);
	}

	public void init()
	{
	}

	public void unload()
	{
	}

	public SoapObject createSoapRequest(String namespace, String name)
	{
		return new SoapObject(namespace, name);
	}

	public SEnvelope createEnvelope(SoapObject request, String userName, String password)
	{
		SEnvelope envelope = new SEnvelope(SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.addWsseHeader(StringTools.trim(userName), StringTools.trim(password));

		return envelope;
	}

	public SEnvelope createEnvelopeForCurrentUser(SoapObject request)
	{
		return createEnvelope(request, AppContext.instance().getUserName(), AppContext.instance().getUserPassword());
	}

	public void addRequestParameter(SoapObject request, String name, Class type, Object value)
	{
		PropertyInfo param = new PropertyInfo();

		param.name = name;
		param.namespace = request.getNamespace();
		param.type = type;
		request.addProperty(param, value);
	}

	public Vector<Object> send(SEnvelope envelope, String server, String url, boolean secure)
		throws IOException, XmlPullParserException
	{
		HttpClientTransport transport = new HttpClientTransport((secure ? "https" : "http") + "://" + server + url);
		HttpClient client = new DefaultHttpClient();

		new HTTPSHackUtil().httpClientAllowAllSSL(client);
		transport.setHttpClient(client);
		transport.call(null, envelope);

		return envelope.getResponseVector();
	}

	public Object sendReturnObject(SEnvelope envelope, String server, String url, boolean secure)
		throws IOException, XmlPullParserException
	{
		HttpClientTransport transport = new HttpClientTransport((secure ? "https" : "http") + "://" + server + url);
		HttpClient client = new DefaultHttpClient();

		new HTTPSHackUtil().httpClientAllowAllSSL(client);
		transport.setHttpClient(client);
		transport.call(null, envelope);

		return envelope.getResponse();
	}

	public Vector<Object> sendToCurrentServer(SEnvelope envelope) throws IOException, XmlPullParserException
	{
		return send(envelope, AppContext.instance().getServerIP(),
						"/" + AppContext.instance().getAppId() + "/services", true);
	}

	public Object sendToCurrentServerReturnObject(SEnvelope envelope) throws IOException, XmlPullParserException
	{
		return sendReturnObject(envelope, AppContext.instance().getServerIP(), "/" + AppContext.instance().getAppId()
						+ "/services", true);
	}

	public Integer sendToCurrentServerReturnInteger(SEnvelope envelope, Integer defaultValue)
		throws IOException, XmlPullParserException
	{
		return NumberTools.toIntInstance(sendToCurrentServerReturnObject(envelope), defaultValue);
	}

	public String sendToCurrentServerReturnString(SEnvelope envelope, String defaultValue)
		throws IOException, XmlPullParserException
	{
		return StringTools.trim(sendToCurrentServerReturnObject(envelope), defaultValue);
	}

	public String getPropertyAsString(SoapObject object, String name)
	{
		try
		{
			String val = object.getProperty(name).toString();

			return ANY_TYPE.equals(val) ? "" : val.toString();
		}
		catch (Exception ignored)
		{
			return "";
		}
	}

	public Integer getPropertyAsInteger(SoapObject object, String name)
	{
		return NumberTools.toIntInstance(object.getProperty(name));
	}

	public String getPropertyAsStringToLowerCase(SoapObject object, String name)
	{
		return StringTools.trim(getPropertyAsString(object, name)).toLowerCase();
	}
}
