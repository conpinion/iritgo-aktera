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

package de.iritgo.aktera.event;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.beanutils.MethodUtils;
import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.tools.KeelTools;


/**
 *
 */
public class DelegatingEventHandler implements StandardEventHandler, Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Our logger. */
	private Logger logger;

	public void setLogger (Logger logger)
	{
		this.logger = logger;
	}

	public Logger getLogger ()
	{
		return logger;
	}

	/** Event id */
	private String event;

	public void setEvent (String event)
	{
		this.event = event;
	}

	public String getEvent ()
	{
		return event;
	}

	/** Class name of the event handler. */
	private String klass;

	public void setClass (String klass)
	{
		this.klass = klass;
	}

	/** Name of the method to call. */
	private String method;

	public void setMethod (String method)
	{
		this.method = method;
	}

	/** Name of a bean to call. */
	private String bean;

	public void setBean (String bean)
	{
		this.bean = bean;
	}

	/** Name of a model to call. */
	private String model;

	public void setModel (String model)
	{
		this.model = model;
	}

	/**
	 * Initialize the event handler.
	 */
	public DelegatingEventHandler ()
	{
	}

	/**
	 * Initialize the event handler.
	 *
	 * @param config Configuration to use
	 * @param log Logger to use
	 */
	public DelegatingEventHandler (Configuration config, Logger log)
	{
		this.logger = log;
		klass = config.getAttribute ("class", null);
		method = config.getAttribute ("method", null);
		bean = config.getAttribute ("bean", null);
		model = config.getAttribute ("model", null);
	}

	/**
	 * Initialize the event handler.
	 *
	 * @param className Event handler class name
	 */
	public DelegatingEventHandler (String className)
	{
		this.klass = className;
	}

	/**
	 * Initialize the event handler.
	 *
	 * @param klass Event handler class name
	 * @param method Event handler method name
	 */
	public DelegatingEventHandler (String klass, String method)
	{
		this.klass = klass;
		this.method = method;
	}

	/**
	 * @see de.iritgo.aktera.event.StandardEventHandler#handle(de.iritgo.aktera.event.Event)
	 */
	public void handle (Event event)
	{
		try
		{
			if (klass != null)
			{
				Object handler = Class.forName (klass).newInstance ();

				if (handler != null)
				{
					if (method != null)
					{
						MethodUtils.invokeMethod (handler, method, event);
					}
					else
					{
						((StandardEventHandler) handler).handle (event);
					}
				}
				else
				{
					logger.error ("Unable to create event handler class '" + klass + "'");
				}
			}
			else if (bean != null)
			{
				Object theBean = KeelContainer.defaultContainer ().getSpringBean (bean);

				if (theBean != null)
				{
					if (method != null)
					{
						MethodUtils.invokeMethod (theBean, method, event);
					}
					else
					{
						((StandardEventHandler) theBean).handle (event);
					}
				}
				else
				{
					logger.error ("Unable to find event handler bean '" + bean + "'");
				}
			}
			else if (model != null)
			{
				try
				{
					Object handler = KeelTools.getService (model);

					if (method != null)
					{
						MethodUtils.invokeMethod (handler, method, event);
					}
					else
					{
						((StandardEventHandler) handler).handle (event);
					}
				}
				catch (ServiceException x)
				{
					logger.error ("Unable to find event handler model '" + model + "'");
				}
			}
			else
			{
				logger.error ("No class or bean event handler specifed");
			}
		}
		catch (InstantiationException x)
		{
			if (logger != null)
			{
				logger.error ("Unable to call event handler", x);
			}
		}
		catch (IllegalAccessException x)
		{
			if (logger != null)
			{
				logger.error ("Unable to call event handler", x);
			}
		}
		catch (ClassNotFoundException x)
		{
			if (logger != null)
			{
				logger.error ("Unable to call event handler", x);
			}
		}
		catch (NoSuchMethodException x)
		{
			if (logger != null)
			{
				logger.error ("Unable to call event handler", x);
			}
		}
		catch (InvocationTargetException x)
		{
			if (logger != null)
			{
				logger.error ("Unable to call event handler", x.getTargetException ());
			}
		}
	}
}
