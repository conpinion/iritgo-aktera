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


import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;


/**
 * Event manager implementation.
 */
public class EventManagerImpl implements EventManager, StartupHandler
{
	/** The logger. */
	private Logger logger;

	public void setLogger (Logger logger)
	{
		this.logger = logger;
	}

	/** Service configuration. */
	private Configuration configuration;

	public void setConfiguration (Configuration configuration)
	{
		this.configuration = configuration;
	}

	/** Events */
	private List<Event> events;

	public void setEvents (List<Event> events)
	{
		this.events = events;
	}

	/** Handlers */
	private List<StandardEventHandler> handlers;

	public void setHandlers (List<StandardEventHandler> handlers)
	{
		this.handlers = handlers;
	}

	/** Event definitions. */
	protected Map<String, Event> eventDefs = new HashMap<String, Event> ();

	/** Event handlers. */
	protected EventHandlerTree eventHandlers = new EventHandlerTree ();

	/**
	 * @see org.apache.avalon.framework.configuration.Initializable#initialize()
	 */
	public void initialize () throws Exception
	{
	}

	/**
	 * @see de.iritgo.aktera.event.EventManager#fire(java.lang.String)
	 */
	public void fire (String eventId)
	{
		fire (eventId, (ModelRequest) null);
	}

	/**
	 * @see de.iritgo.aktera.event.EventManager#fire(java.lang.String, java.util.Properties)
	 */
	public void fire (String eventId, Properties properties)
	{
		fire (eventId, (ModelRequest) null, properties);
	}

	/**
	 * @see de.iritgo.aktera.event.EventManager#fire(String, ModelRequest)
	 */
	public void fire (String eventId, ModelRequest req)
	{
		fire (eventId, req, logger, null);
	}

	/**
	 * @see de.iritgo.aktera.event.EventManager#fire(String, ModelRequest)
	 */
	public void fire (String eventId, ModelRequest req, Logger logger)
	{
		fire (eventId, req, logger, null);
	}

	/**
	 * @see de.iritgo.aktera.event.EventManager#fire(String,ModelRequest, Hashtable)
	 */
	public void fire (String eventId, ModelRequest req, Properties properties)
	{
		fire (eventId, req, logger, properties);
	}

	/**
	 * @see de.iritgo.aktera.event.EventManager#fire(String,ModelRequest, Hashtable)
	 */
	public void fire (String eventId, ModelRequest req, Logger logger, Properties properties)
	{
		Event eventProto = eventDefs.get (eventId);

		if (eventProto == null)
		{
			logger.error ("Unable to find event '" + eventId + "'");

			return;
		}

		Event event = eventProto.clone ();

		event.setRequest (req);

		if (logger == null || logger instanceof NullLogger)
		{
			event.setLogger (logger);
		}
		else
		{
			event.setLogger (logger);
		}

		event.setProperties (properties);

		EventHandlerTree node = eventHandlers;

		for (String eventIdPart : eventId.split ("\\."))
		{
			node = node.getChild (eventIdPart);

			if (node == null)
			{
				break;
			}

			if (node.getHandlers ().size () != 0)
			{
				for (StandardEventHandler handler : node.getHandlers ())
				{
					handler.handle (event);
				}
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup () throws StartupException
	{
		for (Event event : events)
		{
			eventDefs.put (event.getEventId (), event);
		}

		for (Configuration config : configuration.getChildren ("event"))
		{
			try
			{
				String id = config.getAttribute ("id");

				eventDefs.put (id, new Event (id));
				logger.debug ("Added event '" + id + "'");
			}
			catch (ConfigurationException x)
			{
				logger.error ("Unable to add event", x);
			}
		}

		for (StandardEventHandler handler : handlers)
		{
			if (handler.getLogger () == null)
			{
				handler.setLogger (logger);
			}
			eventHandlers.insert (handler.getEvent (), handler);
		}

		for (Configuration config : configuration.getChildren ("handler"))
		{
			try
			{
				String eventId = config.getAttribute ("event");

				eventHandlers.insert (eventId, new DelegatingEventHandler (config, logger));
				logger.debug ("Added event handler for event '" + eventId + "'");
			}
			catch (ConfigurationException x)
			{
				logger.error ("Unable to add event handler", x);
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown () throws ShutdownException
	{
	}
}
