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

package de.iritgo.aktera.servers.direct;


import de.iritgo.aktera.comm.ModelRequestMessage;
import de.iritgo.aktera.comm.ModelResponseMessage;
import de.iritgo.aktera.context.KeelContextualizable;
import de.iritgo.aktera.core.container.Container;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.servers.KeelAbstractServer;
import de.iritgo.aktera.startup.StartupManager;
import org.apache.avalon.framework.context.ContextException;
import org.apache.commons.beanutils.MethodUtils;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * This class implements the server-side queue when Keel server is running
 * directly in the same container as the Keel client. This is typically
 * used in low-load/non-distributed environments, particularly from a
 * webapp context.
 *
 * This class is not used directly. Instead, clients like Keel's
 * Struts ModelAction class invoke this thread via the KeelStarter class.
 *
 * @author Michael Nash
 * @author Shash Chatterjee
 * Created on Nov 16, 2002
 */
public class KeelDirectServer extends KeelAbstractServer
{
	//	class MultiThreadedProcessor
	//		extends Thread
	//	{
	//		private KeelRequest request;
	//
	//		private LinkedBlockingQueue replyChannel;
	//
	//		public MultiThreadedProcessor (
	//			KeelRequest request,
	//			LinkedBlockingQueue replyChannel)
	//		{
	//			this.request = request;
	//			this.replyChannel = replyChannel;
	//		}
	//
	//		public void run ()
	//		{
	//			KeelResponse response = null;
	//
	//			try
	//			{
	//				response = execute (request);
	//			}
	//			catch (ModelException x)
	//			{
	//				logger.error ("Exception running model:", x);
	//				response = new ModelResponseMessage();
	//				response.addError ("Error Running Model", x);
	//			}
	//			catch (Exception x)
	//			{
	//				System.out.println ("[MultiThreadedProcessor] Exception: " + x);
	//			}
	//
	//			try
	//			{
	//				byte[] responseBytes = response.serialize ();
	//				replyChannel.put (responseBytes);
	//			}
	//			catch (InterruptedException e)
	//			{
	//				throw new RuntimeException("Error sending message to client thread", e);
	//			}
	//			catch (IOException e)
	//			{
	//				throw new RuntimeException("Error Serializing received message", e);
	//			}
	//			catch (Exception x)
	//			{
	//				System.out.println ("[MultiThreadedProcessor] Exception: " + x);
	//			}
	//		}
	//	}
	private static final String LOG_PREFIX = "[KeelDirectServer] ";

	public LinkedBlockingQueue requestChannel = null;

	private Throwable heldError = null;

	private boolean keepRunningKeelDirectServer = true;

	//	private int timeout = 60;
	private Container keelContainer = null;

	//	private ThreadPoolExecutor pool;

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		KeelRequest dsreq = new ModelRequestMessage();

		if (! isInitialized())
		{
			try
			{
				keelContainer = getContainer();
				getLogger().info("Started");

				//				String mto = System.getProperty ("model.timeout");
				//
				//				if (mto != null)
				//				{
				//					try
				//					{
				//						int newTimeOut = new Integer(mto).intValue ();
				//						timeout = newTimeOut;
				//					}
				//					catch (Exception e)
				//					{
				//						getLogger ().warn ("Unable to set model timeout to '" + mto + "'");
				//					}
				//				}

				//				int initPoolSize = 1;
				//				int minPoolSize = 1;
				//				int maxPoolSize = Integer.MAX_VALUE;
				//				int keepAliveTime = 60 * 1000;
				//
				//				try
				//				{
				//					minPoolSize = Integer.parseInt (
				//							System.getProperty ("keel.processor.threads.min"));
				//				}
				//				catch (NumberFormatException x)
				//				{
				//				}
				//
				//				try
				//				{
				//					maxPoolSize = Integer.parseInt (
				//							System.getProperty ("keel.processor.threads.max"));
				//				}
				//				catch (NumberFormatException x)
				//				{
				//				}
				//
				//				try
				//				{
				//					initPoolSize = Integer.parseInt (
				//							System.getProperty ("keel.processor.threads.init"));
				//				}
				//				catch (NumberFormatException x)
				//				{
				//				}

				//				pool = new ThreadPoolExecutor(
				//						minPoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				//						new SynchronousQueue());
			}
			catch (Exception ee)
			{
				getLogger().error("Startup error:", ee);
				heldError = ee;
				throw new RuntimeException(LOG_PREFIX + "Startup Error", ee);
			}
		}

		while (keepRunningKeelDirectServer)
		{
			KeelRequest request;

			try
			{
				//There's only one thread taking objects off the requestQueue, so this doesn't need to be synced
				byte[] requestBytes = (byte[]) requestChannel.take();

				LinkedBlockingQueue replyChannel = (LinkedBlockingQueue) requestChannel.take();

				if (keepRunningKeelDirectServer == false)
				{
					continue;
				}

				request = dsreq.deserialize(requestBytes);

				//				MultiThreadedProcessor processor =
				//					new MultiThreadedProcessor(request, replyChannel);

				//				pool.execute (processor);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException("Error receiving message from client thread", e);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Error deserializing received message", e);
			}

			if (heldError != null)
			{
				System.err.println("Container startup error - KeelDirectServer shuts down");

				return;
			}
		}

		System.err.println("[KeelDirectServer] Exiting");

		this.clearRequestChannel();

		try
		{
			this.getContainer().dispose();
		}
		catch (ModelException e)
		{
			e.printStackTrace();
		}

		//		pool.shutdownNow ();
		this.setInitialized(false);
		this.heldError = null;
	}

	/**
	 * Clears all data in the channels (up to 1000 messages).
	 * Has safegaurds for time outs and to prevent infinite loops
	 */
	private void clearRequestChannel()
	{
		int count = 0;

		while (requestChannel.peek() != null && count < 1000)
		{
			count++;

			try
			{
				requestChannel.poll(1000, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}

		this.requestChannel = null;
	}

	public void dispose()
	{
		try
		{
			this.keepRunningKeelDirectServer = false;

			KeelRequest message = new ModelRequestMessage();

			/**
			 * Put an empty message on the queue to causes the "infinite" loop to stop.
			 */
			byte[] requestBytes = message.serialize();

			requestChannel.put(requestBytes);

			LinkedBlockingQueue replyChannel = new LinkedBlockingQueue();

			requestChannel.put(replyChannel);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public LinkedBlockingQueue getRequestChannel()
	{
		return requestChannel;
	}

	/**
	 * @param channel
	 */
	public void setRequestChannel(LinkedBlockingQueue channel)
	{
		requestChannel = channel;
	}

	public KeelResponse execute(KeelRequest request) throws ModelException
	{
		if (getContainer() == null)
		{
			throw new ModelException("Initialization failed - container is null");
		}

		ModelRequest req = null;

		try
		{
			Object o = getContainer().getService(ModelRequest.ROLE);

			if (o == null)
			{
				getLogger().error("Service returned was null");
			}

			req = (ModelRequest) o;
		}
		catch (Exception se)
		{
			getLogger().error("Service Exception:", se);
			throw new ModelException("Service Exception getting request", se);
		}

		try
		{
			req.copyFrom(request);
		}
		catch (Exception ee)
		{
			getLogger().error("Error copying ModelRequestMessage to ModelRequest", ee);
		}

		// Set the context to that provided in the original request
		if (req instanceof KeelContextualizable)
		{
			try
			{
				((KeelContextualizable) req).setKeelContext(getContext(req));
			}
			catch (ContextException e)
			{
				throw new ModelException("Unable to set keel context on ModelRequest", e);
			}
		}

		getLogger().debug("Executing model " + req.getModel());

		/**
		 * We don't fail on a ModelException, we just stuff it into the
		 * model response and carry on, so that the client gets the
		 * exception
		 */
		ModelResponse res = null;
		ModelResponseMessage resMessage = new ModelResponseMessage();

		try
		{
			res = req.execute();
		}
		catch (ModelException me)
		{
			resMessage.addError("model", me);

			if (getLogger().isDebugEnabled())
			{
				getLogger().debug("Error from model execution:", me);
			}
		}

		try
		{
			if (res == null)
			{
				getLogger().error("Response from model was null");
			}
			else
			{
				resMessage.copyFrom(res);
			}
		}
		catch (Exception ce)
		{
			getLogger().error("Unable to copy response to message:", ce);
			resMessage.addError("badCopy", ce);
		}

		/**
		 * Stash any changes to the context
		 */
		saveContext(req);

		getContainer().release(req);

		return resMessage;
	}

	@Override
	public void shutDown()
	{
		try
		{
			StartupManager startupManager = (StartupManager) getContainer().getService(StartupManager.ROLE);

			startupManager.shutdown();
		}
		catch (Exception x)
		{
			getLogger().error("Unable to call the startup manager to shutdown all components", x);
		}

		super.shutDown();
		dispose();
	}
}
