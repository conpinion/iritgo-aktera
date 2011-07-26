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


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;


/**
 * @author Schatterjee
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Container
{
	public Object getService (String role) throws ServiceException;

	public Object getService (String role, String hint) throws ServiceException;

	public Object getService (String role, String hint, Context context) throws ServiceException;

	public void release (Object service);

	public void release (String role, Object service);

	public void dispose ();

	public Configuration getSystemConfig () throws ConfigurationException;

	/**
	 * Get a Spring bean from the Spring container.
	 *
	 * @param name The name of the Spring bean.
	 * @return The Spring bean.
	 * @throws BeansException If the bean cannot be retrieved.
	 */
	public Object getSpringBean (String name) throws BeansException;

	/**
	 * @return
	 */
	public ConfigurableBeanFactory getSpringBeanFactory ();

	public String[] getHibernateConfigLocations ();

	public void addShutdownHandler (Runnable shutdownHandler);

	public Logger getLogger (String category);

	public void setLogLevel (String logLevel);

	public void setLogLevel (String logLevel, String category);
}
