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


import java.util.Vector;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.kdom.Element;


public class SEnvelope extends SoapSerializationEnvelope
{
	public SEnvelope (int version)
	{
		super (version);
	}

	public Vector<Object> getResponseVector () throws SoapFault
	{
		if (bodyIn instanceof SoapFault)
		{
			throw (SoapFault) bodyIn;
		}
		KvmSerializable ks = (KvmSerializable) bodyIn;
		Vector<Object> v = new Vector<Object> (ks.getPropertyCount ());
		for (int i = 0; i < ks.getPropertyCount (); ++i)
		{
			v.add (ks.getProperty (i));
		}
		return v;
	}

	public void addHeader (Element header)
	{
		if (headerOut != null)
		{
			Element[] newHeaderOut = new Element[headerOut.length + 1];
			System.arraycopy (headerOut, 0, newHeaderOut, 0, newHeaderOut.length);
			newHeaderOut[headerOut.length] = header;
		}
		else
		{
			headerOut = new Element[1];
			headerOut[0] = header;
		}
	}

	public void addWsseHeader (String userName, String userPassword)
	{
		Element usernameTag = new Element ();
		usernameTag.setName ("wsse:Username");
		usernameTag.addChild (Element.TEXT, userName);

		Element passwordTag = new Element ();
		passwordTag.setName ("wsse:Password");
		passwordTag.addChild (Element.TEXT, userPassword);

		Element tokenTag = new Element ();
		tokenTag.setName ("wsse:UsernameToken");
		tokenTag.addChild (Element.ELEMENT, usernameTag);
		tokenTag.addChild (Element.ELEMENT, passwordTag);

		Element securityHeader = new Element ();
		securityHeader.setName ("wsse:Security");
		securityHeader.setPrefix ("wsse",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
		securityHeader.addChild (Element.ELEMENT, tokenTag);

		addHeader (securityHeader);
	}
}
