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


import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ResponseElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;


/**
 * @author Michael Nash
 *
 * Take a DOM Element representing a ModelResponse, serialized to XML, and
 * return a ModelResponse.
 */
public class ModelResponseDeserializer
{
	//--- Do not allow this to be instantiated since it is a Singleton.
	private ModelResponseDeserializer()
	{
	} // ModelResponseDeserializer

	public static KeelResponse deserialize(Element root)
		throws ParserConfigurationException, SAXException, ModelException
	{
		assert root != null;

		ModelResponseMessage res = new ModelResponseMessage();

		res.setAttribute("model", root.getAttribute("name"));

		/* Handle response attributes */
		NamedNodeMap attribs = root.getAttributes();

		for (int i = 0; i < attribs.getLength(); i++)
		{
			Node oneAttrib = attribs.item(i);

			res.setAttribute(oneAttrib.getLocalName(), oneAttrib.getNodeValue());
		}

		NodeList children = root.getChildNodes();
		Node oneChild = null;

		for (int i = 0; i < children.getLength(); i++)
		{
			oneChild = children.item(i);

			if (! getAttribute(oneChild, "element-type").equals(""))
			{
				ResponseElement child = handleChild(oneChild);

				if (child != null)
				{
					res.add(child);
				}
			}
		}

		return res;
	}

	private static ResponseElement handleChild(Node oneChild)
	{
		assert oneChild != null;

		if (oneChild.getNodeType() == Node.TEXT_NODE)
		{
			return null;
		}

		if (getAttribute(oneChild, "element-type").equals("input"))
		{
			InputMessage in = new InputMessage();

			in.setLabel(getAttribute(oneChild, "label"));
			in.setDefaultValue(getAttribute(oneChild, "default-value"));

			HashMap vvMap = new HashMap();
			Node inputChild = null;

			for (int i = 0; i < oneChild.getChildNodes().getLength(); i++)
			{
				inputChild = oneChild.getChildNodes().item(i);

				if (inputChild.getLocalName().equals("valid-values"))
				{
					Node vvChild = null;

					for (int j = 0; j < inputChild.getChildNodes().getLength(); j++)
					{
						vvChild = inputChild.getChildNodes().item(j);

						if (vvChild.getLocalName().equals("valid-value"))
						{
							vvMap.put(getAttribute(vvChild, "name"), getAttribute(vvChild, "value"));
						}
					}
				}
			}

			in.setValidValues(vvMap);
			handleAttributes(in, oneChild);
			handleNested(in, oneChild);

			return in;
		}

		if (getAttribute(oneChild, "element-type").equals("output"))
		{
			OutputMessage out = new OutputMessage();

			out.setName(oneChild.getLocalName());

			StringBuffer contents = new StringBuffer("");
			Node outputChild = null;

			for (int i = 0; i < oneChild.getChildNodes().getLength(); i++)
			{
				outputChild = oneChild.getChildNodes().item(i);

				if (outputChild.getNodeType() == Node.TEXT_NODE)
				{
					contents.append(outputChild.getNodeValue());
				}
			}

			out.setContent(contents);
			handleAttributes(out, oneChild);
			handleNested(out, oneChild);

			return out;
		}

		if (getAttribute(oneChild, "element-type").equals("command"))
		{
			CommandMessage cmd = new CommandMessage();

			cmd.setName(oneChild.getLocalName());
			cmd.setModel(getAttribute(oneChild, "model"));
			cmd.setBean(getAttribute(oneChild, "bean"));
			cmd.setLabel(getAttribute(oneChild, "label"));

			Node commandChild = null;

			for (int i = 0; i < oneChild.getChildNodes().getLength(); i++)
			{
				commandChild = oneChild.getChildNodes().item(i);

				if (commandChild.getLocalName().equals("parameter"))
				{
					cmd.setParameter(getAttribute(oneChild, "name"), getAttribute(oneChild, "value"));
				}
			}

			handleAttributes(cmd, oneChild);
			handleNested(cmd, oneChild);

			return cmd;
		}

		throw new IllegalArgumentException("Unknown element-type '" + getAttribute(oneChild, "element-type")
						+ "' in element '" + oneChild.toString() + "'");
	}

	/**
	 * Go through the nested elements of a node looking for any others with an "element-type"
	 * attribute - add them as nested elements of the current element
	 * @param n
	 * @param attribName
	 * @return
	 */
	private static void handleNested(ResponseElement re, Node oneChild)
	{
		Node oneSub = null;

		for (int i = 0; i < oneChild.getChildNodes().getLength(); i++)
		{
			oneSub = oneChild.getChildNodes().item(i);

			if (oneSub.getNodeType() != Node.TEXT_NODE)
			{
				if (oneSub.getLocalName().equals("attribute"))
				{
					re.setAttribute(getAttribute(oneChild, "name"), getAttribute(oneChild, "value"));
				}
			}
		}
	}

	/**
	 * Handle attributes of a ResponseElement
	 * @param re
	 * @param oneChild
	 */
	private static void handleAttributes(ResponseElement re, Node oneChild)
	{
		Node oneSub = null;

		for (int i = 0; i < oneChild.getChildNodes().getLength(); i++)
		{
			oneSub = oneChild.getChildNodes().item(i);

			if (! getAttribute(oneChild, "element-type").equals(""))
			{
				ResponseElement ne = handleChild(oneSub);

				re.add(ne);
			}
		}
	}

	/**
	 * Convenience method to fish through the attributes of a node for one with a
	 * particular name and return it's value.
	 * @param n
	 * @param attribName
	 * @return String
	 */
	private static String getAttribute(Node n, String attribName)
	{
		if (n == null)
		{
			return "";
		}

		NamedNodeMap paramAttrs = n.getAttributes();

		if (paramAttrs == null)
		{
			return "";
		}

		Node oneParamAttr = null;

		for (int k = 0; k < paramAttrs.getLength(); k++)
		{
			oneParamAttr = paramAttrs.item(k);

			if (oneParamAttr.getLocalName().equals(attribName))
			{
				return oneParamAttr.getNodeValue();
			}
		}

		return "";
	}
}
