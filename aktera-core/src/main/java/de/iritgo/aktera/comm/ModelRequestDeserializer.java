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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Michael Nash
 *
 * Take a DOM Document representing a Model request, and turn it into an actual
 * ModelRequestMessage, ready for execution.
 */
public class ModelRequestDeserializer
{
	//--- Do not allow this to be instantiated since it is a Singleton.
	private ModelRequestDeserializer()
	{
	} // ModelRequestDeserializer

	public static KeelRequest deserialize(Element root)
	{
		KeelRequest req = new ModelRequestMessage();

		req.setModel(root.getAttribute("model"));

		/* Handle the request attributes */
		NamedNodeMap attrs = root.getAttributes();
		Node oneAttrNode = null;

		for (int i = 0; i < attrs.getLength(); i++)
		{
			oneAttrNode = attrs.item(i);
			req.setAttribute(oneAttrNode.getLocalName(), oneAttrNode.getNodeValue());
		}

		NodeList children = root.getChildNodes();
		Node oneChild = null;

		for (int i = 0; i < children.getLength(); i++)
		{
			oneChild = children.item(i);

			if (oneChild != null)
			{
				String name = oneChild.getLocalName();

				if (name == null)
				{
					name = "";
				}

				if (name.equals("parameters"))
				{
					/* Now for each "parameter" sub-node */
					Node oneParamNode = null;
					NodeList parameterNodes = oneChild.getChildNodes();

					for (int j = 0; j < parameterNodes.getLength(); j++)
					{
						oneParamNode = parameterNodes.item(j);

						if (oneParamNode != null)
						{
							String pnodeName = oneParamNode.getLocalName();

							if (pnodeName == null)
							{
								pnodeName = "";
							}

							if (pnodeName.equals("parameter"))
							{
								String paramName = null;
								String paramValue = null;
								NamedNodeMap paramAttrs = oneParamNode.getAttributes();
								Node oneParamAttr = null;

								for (int k = 0; k < paramAttrs.getLength(); k++)
								{
									oneParamAttr = paramAttrs.item(k);

									if (oneParamAttr.getLocalName().equals("name"))
									{
										paramName = oneParamAttr.getNodeValue();
									}

									if (oneParamAttr.getLocalName().equals("value"))
									{
										paramValue = oneParamAttr.getNodeValue();
									}
								}

								req.setParameter(paramName, paramValue);
							}
						}
					} /* if the child is "parameter" */
				} /* for each child of "parameters" */
			} /* if the child is "parameters" */
		}

		return req;
	}
}
