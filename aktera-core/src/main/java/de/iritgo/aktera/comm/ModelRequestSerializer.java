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

package de.iritgo.aktera.comm;


import de.iritgo.aktera.model.KeelRequest;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.Map;


/**
 * @author Michael Nash
 *
 * Take a ModelRequest object and return a DOM Document that can be used to
 * send the request via XML/WebServices.
 */
public class ModelRequestSerializer
{
	//--- Do not allow this to be instantiated since it is a Singleton.
	private ModelRequestSerializer()
	{
	} // ModelRequestSerializer

	public static Document serialize(KeelRequest req) throws ParserConfigurationException, SAXException
	{
		//Obtain DOMImplementation
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(true);

		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		Document xmldoc = impl.createDocument("model", "request", null);

		Element root = xmldoc.getDocumentElement();

		root.setAttribute("model", req.getModel());

		/* Handle the request attributes */
		Map attrs = req.getAttributes();

		if (attrs.size() > 0)
		{
			String oneAttrKey = null;
			Object oneAttrValue = null;

			for (Iterator ia = attrs.keySet().iterator(); ia.hasNext();)
			{
				oneAttrKey = (String) ia.next();
				oneAttrValue = attrs.get(oneAttrKey);

				if (oneAttrValue != null)
				{
					root.setAttribute(oneAttrKey, oneAttrValue.toString());
				}
			}
		}

		/* Handle parameters */
		Map params = req.getParameters();

		if (params.size() > 0)
		{
			Element paramsElement = xmldoc.createElement("parameters");
			String oneKey = null;

			for (Iterator i = params.keySet().iterator(); i.hasNext();)
			{
				oneKey = (String) i.next();

				Element param = xmldoc.createElement("parameter");

				param.setAttribute("name", oneKey);
				param.setAttribute("value", (String) params.get(oneKey));
				paramsElement.appendChild(param);
			}

			root.appendChild(paramsElement);
		}

		return xmldoc;
	}
}
