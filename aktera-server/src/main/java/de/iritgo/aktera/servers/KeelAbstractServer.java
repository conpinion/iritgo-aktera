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

package de.iritgo.aktera.servers;


import de.iritgo.aktera.core.container.Container;
import de.iritgo.aktera.core.container.ContainerException;
import de.iritgo.aktera.core.container.ContainerFactory;
import de.iritgo.aktera.core.container.ContainerFactoryLoader;
import de.iritgo.aktera.core.log.DefaultLoggerFactory;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import java.util.HashMap;
import java.util.Map;


/**
 * This is an abstract base class that provides the common
 * functionality of Keel server-side threads.
 *
 * @version        $Revision: 1.2 $        $Date: 2006/06/27 21:43:51 $
 * @author Shash Chatterjee
 * Created on Nov 17, 2002
 */
public abstract class KeelAbstractServer extends Thread //	implements EventListener
{
	private class ShutDownHook extends Thread
	{
		private KeelAbstractServer myServer = null;

		public ShutDownHook(KeelAbstractServer ks)
		{
			myServer = ks;
		}

		public void run()
		{
			if (myServer != null)
			{
				getLogger().info("[KeelAbstractServer.ShutDownHook] Shutdown requested");
				myServer.shutDown();
				myServer = null;
			}
		}
	}

	private static String PREFIX = new String("[KeelAbstractServer] ");

	protected static Container myContainer = null;

	private static Map contexts = new HashMap();

	private static ContainerFactory containerFactory = null;

	//	private static EventManager eventManager = null;
	protected Logger logger = null;

	private boolean initialized = false;

	/* How many times do we retry when waiting for an initial */
	/* context synchronization request. Zero means don't check the cluster at all */
	/* This is set by the "syncretries" property in the top-level */
	/* element of system.xconf */
	private int maxRetries = 5;

	private String[] args = null;

	public KeelAbstractServer()
	{
	}

	public KeelAbstractServer(String name)
	{
		super(name);
	}

	protected ComparableContext getContext(ModelRequest req)
	{
		String sessionId = (String) req.getAttribute("sessionid");

		if (sessionId == null)
		{
			getLogger().warn("Request contained no session id. Session will not be distributable.");

			// Can't share this session, no message to queue
			return new ComparableContext();
		}

		getLogger().debug("Request from session " + sessionId);

		ComparableContext c = (ComparableContext) contexts.get(sessionId);

		//		if (c == null)
		//		{
		//			/* We don't know about a context for this session - see if */
		//			/* anyone else does */
		//			c = getNewContext (sessionId);
		//
		//			if (getLogger ().isDebugEnabled ())
		//			{
		//				getLogger ()
		//					.debug (PREFIX + "New context assigned for " + sessionId + ":" + c.toString ());
		//			}
		//
		//			getLogger ().info ("Created new context for session " + sessionId);
		//		}
		if (getLogger().isDebugEnabled())
		{
			getLogger().debug(PREFIX + "Context passed to model was " + c.toString());
		}

		return new ComparableContext(c);
	}

	protected Container getContainer() throws ModelException
	{
		if (myContainer == null)
		{
			synchronized (this)
			{
				if (myContainer == null)
				{
					initialize();
				}
			}
		}

		return myContainer;
	}

	protected void initialize() throws ModelException
	{
		System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");

		//		ShutDownHook myShutDown = new ShutDownHook(this);
		//		Runtime.getRuntime ().addShutdownHook (myShutDown);
		try
		{
			containerFactory = new ContainerFactoryLoader().getContainerFactory();
		}
		catch (ContainerException e)
		{
			getLogger().error("Error getting container factory", e);
			throw new ModelException("Error getting container factory", e);
		}

		try
		{
			myContainer = containerFactory.createContainer();
		}
		catch (ContainerException e)
		{
			getLogger().error("Error creating container", e);
			throw new ModelException("Error creating container", e);
		}

		if (myContainer == null)
		{
			throw new ModelException("Factory returned null container");
		}

		try
		{
			maxRetries = myContainer.getSystemConfig().getAttributeAsInteger("syncretries", 0);
		}
		catch (ConfigurationException e)
		{
			throw new RuntimeException("Unable to configure " + this.getClass().getName() + ": " + e.getMessage());
		}

		if (maxRetries == 0)
		{
			getLogger()
							.info(
											PREFIX
															+ "Context synchronization disabled - use 'syncretries' attribute of top-level config to enable");
		}

		//		try
		//		{
		//			eventManager = (EventManager) myContainer.getService (EventManager.ROLE, "default");
		//			eventManager.addListener (this, "*");
		//		}
		//		catch (ServiceException se)
		//		{
		//			throw new ModelException("Error setting up Event Manager for context sharing", se);
		//		}
		setInitialized(true);
	}

	public void setArgs(String[] args)
	{
		this.args = args;
	}

	protected void saveContext(ModelRequest req) throws ModelException
	{
		String sessionId = (String) req.getAttribute("sessionid");

		if (sessionId == null)
		{
			getLogger().error(PREFIX + "No session id - can't save context");

			return;
		}

		// Get the old context, compare to the new. If nothing changed, do nothing.
		ComparableContext currentContext = (ComparableContext) contexts.get(sessionId);

		ComparableContext c = (ComparableContext) req.getContext();

		if (c == null)
		{
			throw new ModelException("No context was assigned to model!");
		}

		if (! (c.equals(currentContext)))
		{
			// If something
			// did change, send a message to the Event queue, letting other servers know.
			if (getLogger().isDebugEnabled())
			{
				getLogger().debug(PREFIX + "Context changed");
			}

			//			postContext (sessionId, c);
			synchronized (contexts)
			{
				contexts.put(sessionId, c);
			}
		}
		else
		{
			if (getLogger().isDebugEnabled())
			{
				getLogger().debug(
								PREFIX + "Context did not change: Before:" + currentContext.toString() + ", after: "
												+ c.toString());
			}
		}
	}

	//	private void postContext (String sessionId, ComparableContext c)
	//		throws ModelException
	//	{
	//		/* If max retries is zero, context sharing is disabled, so just */
	//		/* carry on */
	//		if (maxRetries == 0)
	//		{
	//			return;
	//		}
	//
	//		if (eventManager == null)
	//		{
	//			getLogger ().warn ("No event manager to post context change");
	//
	//			return;
	//		}
	//
	//		getLogger ().debug ("Posting event for context change for session " + sessionId);
	//
	//		Event e = null;
	//
	//		try
	//		{
	//			e = (Event) myContainer.getService (Event.ROLE, "default");
	//		}
	//		catch (ServiceException se)
	//		{
	//			throw new ModelException("Unable to create new event for context synchronization");
	//		}
	//
	//		/* Populate the event */
	//		e.setIdentifier ("context");
	//
	//		Map m = new HashMap();
	//		m.put ("context", c.getMap ());
	//		m.put ("session-id", sessionId);
	//		e.setDetails (m);
	//		e.setPostedBy (this);
	//
	//		try
	//		{
	//			eventManager.postEvent (e);
	//		}
	//		catch (NestedException ne)
	//		{
	//			getLogger ().error ("Unable to post context event", ne);
	//			throw new ModelException(ne);
	//		}
	//	}
	protected boolean isInitialized()
	{
		return initialized;
	}

	protected void setInitialized(boolean initialized)
	{
		this.initialized = initialized;
	}

	protected Logger getLogger()
	{
		if (logger == null)
		{
			logger = DefaultLoggerFactory.getInstance().getLoggerForCategory("keel.server");
		}

		return logger;
	}

	/**
	 * Receive event callbacks from the EventManager. Whenever a message is
	 * recieved indicating a change to context, we get an event. Part of the
	 * event gives us the new context and the session ID associated with it.
	 */

	//	public void receiveEvent (Event e)
	//	{
	//		if (getLogger ().isDebugEnabled ())
	//		{
	//			getLogger ().debug (PREFIX + "Received " + e.getIdentifier () + " event");
	//		}
	//
	//		if (e.getIdentifier ().equals ("context"))
	//		{
	//			/* If max retries is zero, context sharing is disabled, */
	//			/* so something nasty is going on if we receive a context */
	//			/* notification */
	//			if (maxRetries == 0)
	//			{
	//				getLogger ()
	//					.error (
	//					"Received a context-sharing request - but" +
	//					" context-sharing is disabled for this server instance. Cluster may be configured incorrectly.");
	//			}
	//
	//			Map m = e.getDetails ();
	//			SequencedHashMap seqMap = (SequencedHashMap) m.get ("context");
	//			ComparableContext cNew = new ComparableContext();
	//			cNew.putMap (seqMap);
	//
	//			String sessionId = (String) m.get ("session-id");
	//			getLogger ()
	//				.debug (
	//				"Got new context for session " + sessionId + " from event:" + cNew.toString ());
	//
	//			if (getLogger ().isDebugEnabled ())
	//			{
	//				getLogger ()
	//					.debug (
	//					PREFIX + "Remote context synchronize received for " + sessionId + ":" +
	//					cNew.toString ());
	//			}
	//
	//			synchronized (contexts)
	//			{
	//				contexts.put (sessionId, cNew);
	//			}
	//		}
	//		else
	//		{
	//			if (getLogger ().isDebugEnabled ())
	//			{
	//				getLogger ().debug ("Got a '" + e.getIdentifier () + "' event");
	//			}
	//
	//			if (e.getIdentifier ().equals ("context-request"))
	//			{
	//				/* If max retries is zero, context sharing is disabled, */
	//				/* so something nasty is going on if we receive a context */
	//				/* notification */
	//				if (maxRetries == 0)
	//				{
	//					getLogger ()
	//						.error (
	//						"Received a context-sharing request - but" +
	//						" context-sharing is disabled for this server instance. Cluster may be configured incorrectly.");
	//				}
	//
	//				Map m = e.getDetails ();
	//				String sessionId = (String) m.get ("session-id");
	//				ComparableContext requestedContext = (ComparableContext) contexts.get (sessionId);
	//
	//				if (requestedContext != null)
	//				{
	//					try
	//					{
	//						postContext (sessionId, requestedContext);
	//					}
	//					catch (ModelException me)
	//					{
	//						getLogger ()
	//							.error ("Unable to respond to request for context " + sessionId, me);
	//					} /* catch */} /* if requestedContext not null */} /* if it was a context-request */}
	//	}

	//	private ComparableContext getNewContext (String sessionId)
	//	{
	//		/* If retries max is zero, we don't bother checking the cluster */
	//		/* This is suitable for single-server systems */
	//		if (maxRetries == 0)
	//		{
	//			return new ComparableContext();
	//		}
	//
	//		if (getLogger ().isDebugEnabled ())
	//		{
	//			getLogger ().debug (PREFIX + "New session - checking cluster for existing context");
	//		}
	//
	//		Event e = null;
	//
	//		try
	//		{
	//			e = (Event) myContainer.getService (Event.ROLE, "default");
	//		}
	//		catch (ServiceException se)
	//		{
	//			getLogger ().error ("Unable to create new event for context synchronization", se);
	//
	//			return new ComparableContext();
	//		}
	//
	//		/* Populate the event */
	//		e.setIdentifier ("context-request");
	//
	//		Map m = new HashMap();
	//		m.put ("session-id", sessionId);
	//		e.setDetails (m);
	//		e.setPostedBy (this);
	//
	//		if (eventManager == null)
	//		{
	//			getLogger ().warn ("No event manager to share contexts");
	//
	//			return new ComparableContext();
	//		}
	//
	//		try
	//		{
	//			eventManager.postEvent (e);
	//		}
	//		catch (NestedException ne)
	//		{
	//			getLogger ().error ("Unable to post context event", ne);
	//
	//			return new ComparableContext();
	//		}
	//
	//		int count = 0;
	//
	//		while (count < maxRetries)
	//		{
	//			try
	//			{
	//				yield ();
	//				sleep (1000); /* TODO: Make configurable */
	//				count++;
	//
	//				/* Have another look in the context map to see if the session is there now */
	//				ComparableContext newContext = (ComparableContext) contexts.get (sessionId);
	//
	//				if (newContext != null)
	//				{
	//					getLogger ().debug ("Got new event from another server");
	//
	//					return newContext;
	//				}
	//			}
	//			catch (InterruptedException ie)
	//			{
	//				getLogger ().warn ("Interrupted", ie);
	//			}
	//		}
	//
	//		getLogger ().debug (
	//			"Never did get a context from the cluster - assigned new empty context");
	//
	//		return new ComparableContext();
	//	}
	public void shutDown()
	{
		if (containerFactory != null)
		{
			try
			{
				containerFactory.disposeContainer();
			}
			catch (ContainerException x)
			{
				getLogger().error("[KeelAbstractServer] " + x);
			}
		}

		getLogger().info("[KeelAbstractServer] Shutdown");

		//		Runtime.getRuntime ().halt (0);
	}

	public static Map<String, ComparableContext> getContexts()
	{
		return contexts;
	}

	public static void removeContext(String id)
	{
		contexts.remove(id);
	}
}
