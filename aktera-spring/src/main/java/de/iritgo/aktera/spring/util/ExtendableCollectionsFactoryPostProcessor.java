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

package de.iritgo.aktera.spring.util;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import java.util.List;
import java.util.Map;


/**
 * This factory post processor concatenates collection beans. This is best described
 * by an example:
 *
 * <util:list id="some.config">
 *   <value>A</value>
 * </util:list>
 *
 * <util:list id="some.config.ext">
 *   <value>B</value>
 * </util:list>
 *
 * The post processor copies all elements of the config.ext bean to the config
 * bean, so the config list reads '[A, B]'.
 *
 * The same applies to util:map beans.
 */
public class ExtendableCollectionsFactoryPostProcessor implements BeanFactoryPostProcessor
{
	public void postProcessBeanFactory (ConfigurableListableBeanFactory factory) throws BeansException
	{
		for (String name : factory.getBeanNamesForType (List.class))
		{
			int lastDotIndex = name.lastIndexOf ('-');

			if (lastDotIndex != - 1)
			{
				try
				{
					Object mainList = factory.getBean (name.substring (0, lastDotIndex));

					if (mainList instanceof List)
					{
						List extList = (List) factory.getBean (name);

						((List) mainList).addAll (extList);
					}
				}
				catch (BeansException ignored)
				{
				}
			}
		}

		for (String name : factory.getBeanNamesForType (Map.class))
		{
			int lastDotIndex = name.lastIndexOf ('-');

			if (lastDotIndex != - 1)
			{
				try
				{
					Object mainMap = factory.getBean (name.substring (0, lastDotIndex));

					if (mainMap instanceof Map)
					{
						Map extMap = (Map) factory.getBean (name);

						((Map) mainMap).putAll (extMap);
					}
				}
				catch (BeansException ignored)
				{
				}
			}
		}
	}
}
