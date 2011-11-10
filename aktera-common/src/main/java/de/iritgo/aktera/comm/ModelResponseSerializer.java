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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * @author Michael Nash
 *
 * Return a DOM Element representing a ModelResponse, serialized to XML
 *
 * $Date: 2003/12/29 07:01:34 $ $Revision: 1.1 $
 */
public class ModelResponseSerializer
{
	private static int count = 0;

	//--- Do not allow this to be instantiated since it is a Singleton.
	private ModelResponseSerializer()
	{
	} // ModelResponseSerializer

	/**
	 * Serialize a ModelResponse in a DOM Document
	 * @param res The ModelResponse to be serialized
	 * @param modelName The name of the Model that generated this response (it's
	 * shorthand, not it's class name)
	 * @return Document The DOM Document containing the serialized response
	 * @throws ParserConfigurationException If the XML Parser configuration is
	 * incorrect
	 * @throws SAXException If an XML error occurrs during serialization
	 */
	public static Document serialize(KeelResponse res, String modelName)
		throws ParserConfigurationException, SAXException
	{
		//Obtain DOMImplementation
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(true);

		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		Document xmldoc = impl.createDocument(null, "model", null);

		Element root = xmldoc.getDocumentElement();

		root.setAttribute("name", modelName);

		Map attrs = res.getAttributes();

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

		/* Figure out what format we use to serialize. By default, response */
		/* elements have the name of the "name" attribute of the response */
		/* element itself. However, if we set "element-names" to "false" as */
		/* an attribute of this model, then the names "input", "output", */
		/* etc according to their type will be used instead. */
		/*  This makes formatting somewhat easier in some situations */
		/* Note that when using this with a sequence, the entire sequence is */
		/* affected */
		boolean useElementNames = true;
		String elementNames = (String) res.getAttribute("element-names");

		if (elementNames != null)
		{
			if (elementNames.equalsIgnoreCase("false") || elementNames.equalsIgnoreCase("no"))
			{
				useElementNames = false;
			}
		}

		Map errors = res.getErrors();

		if (errors.size() > 0)
		{
			Element errorsElement = xmldoc.createElement("errors");
			String oneErrorKey = null;

			for (Iterator ie = errors.keySet().iterator(); ie.hasNext();)
			{
				oneErrorKey = (String) ie.next();

				if (oneErrorKey == null)
				{
					oneErrorKey = "unknown";
				}

				Element oneError = xmldoc.createElement("error");

				oneError.setAttribute("key", oneErrorKey);

				Element errorType = xmldoc.createElement("error-type");

				errorType.appendChild(xmldoc.createTextNode(res.getErrorType(oneErrorKey)));
				oneError.appendChild(errorType);

				Element message = xmldoc.createElement("message");

				message.appendChild(xmldoc.createTextNode((String) errors.get(oneErrorKey)));
				oneError.appendChild(message);

				String stack = res.getStackTrace(oneErrorKey);

				if (stack != null)
				{
					Element stackElement = xmldoc.createElement("stacktrace");

					stackElement.appendChild(xmldoc.createTextNode(stack));
					oneError.appendChild(stackElement);
				}

				errorsElement.appendChild(oneError);
			}

			xmldoc.getDocumentElement().appendChild(errorsElement);
		}

		ResponseElement oneElement = null;

		for (Iterator reList = res.getAll(); reList.hasNext();)
		{
			oneElement = (ResponseElement) reList.next();

			if (oneElement != null)
			{
				serialize(oneElement, root, xmldoc, useElementNames);
			}
		}

		return xmldoc;
	}

	/**
	 * Return the serialized ModelResponse as a formatted String
	 *
	 * @param res The ModelResponse to be serialized
	 * @param modelName The name of the model called
	 * @return String The Response formatted as an XML String
	 * @throws ParserConfigurationException If the XML Parser configuration was bad
	 * @throws SAXException If there is an error during serialization
	 * @throws IOException If there is an error during formatting/serializing
	 */
	public static String serializeToString(KeelResponse res, String modelName)
		throws ParserConfigurationException, SAXException, IOException
	{
		Document xmldoc = serialize(res, modelName);

		// Serialize the document
		OutputFormat format = new OutputFormat();

		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);

		XMLSerializer serializer;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		serializer = new XMLSerializer(bos, format);

		serializer.serialize(xmldoc);

		return new String(bos.toString());
	}

	/**
	 * Internal method to serialize each ResponseElement
	 * @param re The ResponseElement to serialize
	 * @param parent The parent Element
	 * @param xmldoc The top-level document
	 * @param elementNames Use element names (or generic names) for each sub-
	 * element?
	 * @throws SAXException If an XML error occurrs during serialization
	 */
	private static void serialize(ResponseElement re, Element parent, Document xmldoc, boolean elementNames)
		throws SAXException
	{
		if (re == null)
		{
			throw new IllegalArgumentException("ResponseElement may not be null: Parent " + parent.toString());
		}

		//AttributesImpl attr = new AttributesImpl();
		if (re instanceof Input)
		{
			Input i = (Input) re;

			Element in = null;

			if (elementNames)
			{
				in = createValidNameElement(xmldoc, i.getName());
				in.setAttribute("element-type", "input");
			}
			else
			{
				in = xmldoc.createElement("input");
				in.setAttribute("name", i.getName());
			}

			in.setAttribute("label", i.getLabel());
			parent.appendChild(in);
			parent.appendChild(xmldoc.createTextNode("\n"));

			if (i.getDefaultValue() != null)
			{
				Element def = xmldoc.createElement("default-value");

				in.appendChild(def);
				def.appendChild(xmldoc.createTextNode(i.getDefaultValue().toString()));
			}

			Map vv = i.getValidValues();

			if ((vv != null) && (vv.size() > 0))
			{
				Element vvElement = xmldoc.createElement("valid-values");

				in.appendChild(vvElement);

				String oneValKey = null;
				Object oneValObj = null;

				for (Iterator iv = vv.keySet().iterator(); iv.hasNext();)
				{
					oneValKey = (String) iv.next();

					Element oneVV = xmldoc.createElement("valid-value");

					vvElement.appendChild(oneVV);
					oneVV.setAttribute("value", oneValKey);

					oneValObj = vv.get(oneValKey);

					if (oneValObj != null)
					{
						oneVV.setAttribute("label", oneValObj.toString());
					}
				}
			}

			handleNested(re, in, xmldoc, elementNames);
			handleAttributes(re, in, xmldoc, elementNames);
		}
		else if (re instanceof Output)
		{
			Output o = (Output) re;

			Element out = null;

			if (elementNames)
			{
				out = createValidNameElement(xmldoc, o.getName());
				out.setAttribute("element-type", "output");
			}
			else
			{
				out = xmldoc.createElement("output");
				out.setAttribute("name", o.getName());
			}

			if (o.getContent() != null)
			{
				serializeContents(xmldoc, out, o.getContent());
			}

			parent.appendChild(out);

			handleNested(re, out, xmldoc, elementNames);
			handleAttributes(re, out, xmldoc, elementNames);
			parent.appendChild(xmldoc.createTextNode("\n"));
		}
		else if (re instanceof Command)
		{
			Command c = (Command) re;

			Element comm = xmldoc.createElement("command");

			comm.setAttribute("name", c.getName());
			parent.appendChild(comm);
			//parent.appendChild(xmldoc.createTextNode("\n"));
			comm.setAttribute("label", c.getLabel());
			comm.setAttribute("model", c.getModel());

			Map params = c.getParameters();
			String oneKey = null;
			Object oneValue = null;

			for (Iterator ip = params.keySet().iterator(); ip.hasNext();)
			{
				oneKey = (String) ip.next();
				oneValue = params.get(oneKey);

				if (oneValue != null)
				{
					Element cp = xmldoc.createElement("parameter");

					comm.appendChild(cp);
					cp.setAttribute("name", oneKey);
					cp.setAttribute("value", oneValue.toString());
				}
			}

			handleNested(re, comm, xmldoc, elementNames);
			handleAttributes(re, comm, xmldoc, elementNames);
			comm.appendChild(xmldoc.createTextNode("\n"));
		}
	}

	private static void handleAttributes(ResponseElement re, Element e, Document xmldoc, boolean useElementNames)
		throws SAXException
	{
		Map attrs = re.getAttributes();
		String oneKey = null;
		Object oneAttr = null;

		if (e == null)
		{
			throw new IllegalArgumentException("Element may not be null");
		}

		if (re == null)
		{
			throw new IllegalArgumentException("ResponseElement may not be null for element " + e.toString());
		}

		Element oneAttrib = null;

		for (Iterator ai = attrs.keySet().iterator(); ai.hasNext();)
		{
			oneKey = (String) ai.next();

			if (oneKey != null)
			{
				oneAttr = attrs.get(oneKey);

				if (oneAttr != null)
				{
					if (oneAttr instanceof ResponseElement)
					{
						if (oneAttrib == null)
						{
							oneAttrib = xmldoc.createElement("attribute");
							e.appendChild(oneAttrib);
						}

						oneAttrib.setAttribute("name", oneKey);

						serialize((ResponseElement) oneAttr, oneAttrib, xmldoc, useElementNames);
					}
					else
					{
						e.setAttribute(oneKey, oneAttr.toString());
						e.appendChild(xmldoc.createTextNode("\n"));
					}
				}
			}
		}
	}

	/**
	 * Handle nested ResponseElements, creating the appropriate nested tree of XML
	 * elements
	 * @param re The ResponseElement to serialize
	 * @param e The element of which this new element will be a child
	 * @param xmldoc The top-level document
	 * @param useElementNames Use the name of each ResponseElement or generic names?
	 * @throws SAXException If an XML error occurs during serialization
	 */
	private static void handleNested(ResponseElement re, Element e, Document xmldoc, boolean useElementNames)
		throws SAXException
	{
		if (re == null)
		{
			throw new IllegalArgumentException("ReponseElement may not be null");
		}

		if (e == null)
		{
			throw new IllegalArgumentException("Element may not be null");
		}

		ResponseElement oneNested = null;

		for (Iterator i = re.getAll().iterator(); i.hasNext();)
		{
			oneNested = (ResponseElement) i.next();

			if (oneNested != null)
			{
				serialize(oneNested, e, xmldoc, useElementNames);
				e.appendChild(xmldoc.createTextNode("\n"));
			}
		}
	}

	/**
	 * Special wrapper around createElement to ensure we don't try to create an
	 * element with an invalid name. If so, use a temporary name and warn.
	 * @param xmldoc
	 * @param name
	 * @return Element
	 */
	private static Element createValidNameElement(Document xmldoc, String name)
	{
		Element returnValue = null;

		try
		{
			returnValue = xmldoc.createElement(name);
		}
		catch (DOMException de)
		{
			/* probably an invalid name for the element */
			count++;
			returnValue = xmldoc.createElement("invalidElementName" + count);
		}

		return returnValue;
	}

	private static void serializeContents(Document xmldoc, Element parent, Object c)
	{
		if (c == null)
		{
			return;
		}

		if (c instanceof Map)
		{
			Map m = (Map) c;
			String oneKey = null;
			Object oneValue = null;

			for (Iterator i = m.keySet().iterator(); i.hasNext();)
			{
				oneKey = (String) i.next();
				oneValue = m.get(oneKey);

				Element entry = createValidNameElement(xmldoc, oneKey);

				parent.appendChild(entry);
				serializeContents(xmldoc, entry, oneValue);
			}
		}
		else if (c instanceof Collection)
		{
			Collection coll = (Collection) c;
			Object oneElement = null;

			for (Iterator i = coll.iterator(); i.hasNext();)
			{
				oneElement = i.next();

				Element member = xmldoc.createElement("member");

				parent.appendChild(member);
				serializeContents(xmldoc, member, oneElement);
			}
		}
		else
		{
			String contentString = c.toString();

			if (! contentString.equals(""))
			{
				parent.appendChild(xmldoc.createTextNode(contentString));
			}
		}
	}
}
