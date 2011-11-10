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

package de.iritgo.aktera.core.container;


/**
 * <Replace with description for ContainerFactoryLoader>
 *
 * @version        $Revision: 1.1 $        $Date: 2003/12/29 06:56:23 $
 * @author Schatterjee
 * Created on Dec 22, 2003
 */
public class ContainerFactoryLoader
{
	public static String FACTORY_CLASS_PROPERTY = "keel.container.factory.default";

	public static String DEFAULT_FACTORY_CLASS = "de.iritgo.aktera.core.container.KeelContainerFactory";

	public ContainerFactory getContainerFactory(String className) throws ContainerException
	{
		ContainerFactory factory = null;

		if (className == null)
		{
			throw new ContainerException("Factory class name cannot be null");
		}

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if (loader == null)
		{
			loader = this.getClass().getClassLoader();
		}

		Class clazz;

		try
		{
			clazz = loader.loadClass(className);
			factory = (ContainerFactory) clazz.newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw new ContainerException("Could not find container factory class " + className, e);
		}
		catch (InstantiationException e)
		{
			throw new ContainerException("Could not instantiate container factory class " + className, e);
		}
		catch (IllegalAccessException e)
		{
			throw new ContainerException("Access violation instantiating container factory class " + className, e);
		}

		return factory;
	}

	public ContainerFactory getContainerFactory() throws ContainerException
	{
		String className = System.getProperty(FACTORY_CLASS_PROPERTY, DEFAULT_FACTORY_CLASS);

		return getContainerFactory(className);
	}
}
