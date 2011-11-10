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

package de.iritgo.aktera.ui.ng;


import de.iritgo.aktera.authorization.AuthorizationManager;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SequenceBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser
{
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
	{
		if (element.hasAttribute("security"))
		{
			builder.addPropertyValue("security", element.getAttribute("security"));
		}

		builder.addPropertyReference("logger", "de.iritgo.aktera.logger.Logger");

		NodeList controllerNodes = element.getElementsByTagNameNS("http://aktera.iritgo.de/spring/ui", "controller");
		List<String> controllerIds = new LinkedList();
		Map<String, Properties> controllerParams = new HashMap();

		for (int i = 0; i < controllerNodes.getLength(); ++i)
		{
			Node childNode = controllerNodes.item(i);
			NamedNodeMap attributeNodes = childNode.getAttributes();
			String controllerId = attributeNodes.getNamedItem("bean").getNodeValue();
			controllerIds.add(controllerId);

			Properties props = null;
			NodeList controllerChilds = childNode.getChildNodes();
			if (controllerChilds != null)
			{
				for (int j = 0; j < controllerChilds.getLength(); ++j)
				{
					Node controllerChild = controllerChilds.item(j);
					if (controllerChild.getNodeType() == Node.ELEMENT_NODE
									&& controllerChild.getLocalName().equals("param"))
					{
						if (props == null)
						{
							props = new Properties();
						}
						NamedNodeMap controllerChildAttrs = controllerChild.getAttributes();
						props.put(controllerChildAttrs.getNamedItem("name").getNodeValue(), controllerChildAttrs
										.getNamedItem("value").getNodeValue());
					}
				}
			}
			if (props != null)
			{
				controllerParams.put(controllerId, props);
			}
		}

		builder.addPropertyValue("controllerIds", controllerIds);
		builder.addPropertyValue("controllerParams", controllerParams);
		builder.addPropertyReference("authorizationManager", AuthorizationManager.ID);
	}

	@Override
	protected Class getBeanClass(Element element)
	{
		return Sequence.class;
	}
}
