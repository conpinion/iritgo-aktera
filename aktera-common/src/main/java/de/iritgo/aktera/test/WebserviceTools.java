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


import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.Vector;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


public class WebserviceTools
{

	private static String context;

	private static String host;

	private static String port;

	public static String getContext ()
	{
		return context;
	}

	public static String getHost ()
	{
		return host;
	}

	public static String getPort ()
	{
		return port;
	}

	public static String getWebserviceURL ()
	{
		return "/" + getContext () + "/services";
	}

	public static void setContext (String context)
	{
		WebserviceTools.context = context;
	}

	public static void setHost (String host)
	{
		WebserviceTools.host = host;
	}

	public static void setPort (String port)
	{
		WebserviceTools.port = port;
	}

	public static SoapObject createSoapRequest (String namespace, String name)
	{
		return new SoapObject (namespace, name);
	}

	public static SEnvelope createEnvelope (SoapObject request, String userName, String password)
	{
		SEnvelope envelope = new SEnvelope (SoapEnvelope.VER11);
		envelope.setOutputSoapObject (request);
		envelope.addWsseHeader (StringTools.trim (userName), StringTools.trim (password));
		return envelope;
	}

	public static void addRequestParameter (SoapObject request, String name, Class type, Object value)
	{
		PropertyInfo param = new PropertyInfo ();
		param.name = name;
		param.namespace = request.getNamespace ();
		param.type = type;
		request.addProperty (param, value);
	}

	public static Vector<Object> send (SEnvelope envelope, String server, String url, boolean secure)
		throws IOException, XmlPullParserException
	{
		HttpClientTransport transport = new HttpClientTransport ((secure ? "https" : "http") + "://" + server + url);
		HttpClient client = new DefaultHttpClient ();
		new HTTPSHackUtil ().httpClientAllowAllSSL (client);
		transport.setHttpClient (client);
		transport.call (null, envelope);
		return envelope.getResponseVector ();
	}

	public static Object sendReturnObject (SEnvelope envelope, String server, String url, boolean secure)
	{
		HttpClientTransport transport = new HttpClientTransport ((secure ? "https" : "http") + "://" + server + url);
		HttpClient client = new DefaultHttpClient ();
		new HTTPSHackUtil ().httpClientAllowAllSSL (client);
		transport.setHttpClient (client);
		try
		{
			transport.call (null, envelope);
			return envelope.getResponse ();
		}
		catch (Exception x)
		{
			fail ("Unable to call Webservice at " + transport.toString () + ": " + x);
			return null;
		}
	}

	private static final String ANY_TYPE = "anyType{}";

	public static String getPropertyAsString (SoapObject object, String name)
	{
		try
		{
			String val = object.getProperty (name).toString ();
			return ANY_TYPE.equals (val) ? "" : val.toString ();
		}
		catch (Exception ignored)
		{
			return "";
		}
	}

	public static Integer getPropertyAsInteger (SoapObject object, String name)
	{
		return NumberTools.toIntInstance (object.getProperty (name));
	}

	public static String getPropertyAsStringToLowerCase (SoapObject object, String name)
	{
		return StringTools.trim (getPropertyAsString (object, name)).toLowerCase ();
	}

	public static WebserviceRequest createWebserviceRequest (String serviceBaseURL, String serviceName,
					String username, String password)
	{
		SoapObject request = WebserviceTools.createSoapRequest (serviceBaseURL, serviceName);
		return new WebserviceRequest (request, username, password, getHost () + ":" + getPort (), getWebserviceURL ());
	}

}
