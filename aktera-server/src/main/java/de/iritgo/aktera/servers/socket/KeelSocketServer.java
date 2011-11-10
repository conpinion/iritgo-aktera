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

package de.iritgo.aktera.servers.socket;


import de.iritgo.aktera.comm.ModelResponseMessage;
import de.iritgo.aktera.context.KeelContextualizable;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.servers.KeelAbstractServer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * A KeelServer instance calls an instance of this class every time a request is received,
 * up to a certain maximum number of active threads. This class handles the request
 * in a seperate thread, and terminates once the response has been sent back to the client.
 *
 */
public class KeelSocketServer extends KeelAbstractServer
{
	class MultiThreadedProcessor extends Thread
	{
		private Socket socket;

		public MultiThreadedProcessor(Socket socket)
		{
			this.socket = socket;
		}

		public void run()
		{
			ObjectOutputStream writer = null;
			ObjectInputStream reader = null;
			KeelResponse response = null;

			try
			{
				reader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

				KeelRequest request = (KeelRequest) reader.readObject();

				try
				{
					response = execute(request);
				}
				catch (ModelException me)
				{
					System.err.println("Exception running model:");
					me.printStackTrace(System.err);
					response = new ModelResponseMessage();
					response.addError("Error Running Model", me);
				}

				writer = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				writer.writeObject(response);
				writer.flush();
			}
			catch (Exception e)
			{
				logger.error("Exception while processing request:", e);
				throw new RuntimeException(e);
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (Exception ignore)
					{
					}
				}

				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (Exception ignore)
					{
					}
				}

				if (socket != null)
				{
					try
					{
						socket.close();
					}
					catch (Exception ignore)
					{
					}
				}
			}
		}
	}

	private Configuration myConfig = null;

	//TODO: Either use these, or remove them.
	//  private Context namingContext = null;
	//	private int timeout = 30;
	//TODO: refactor to use JDK 1.4 NIO based sockets
	private ServerSocket serverSocket = null;

	public void setLogger(Logger newLog)
	{
		logger = newLog;
	}

	/**
	 * Need to configure, initialize, etc...but can't rely on the lifecycle interfaces
	 * used by typical components because the container is not yet available. This bootstrap
	 * method initializes the container and configures & initializes this server instance.
	 * @throws ModelException
	 */
	protected void bootstrap() throws ModelException
	{
		getContainer();

		try
		{
			myConfig = getContainer().getSystemConfig().getChild("socket-server");
		}
		catch (ModelException e)
		{
			throw new RuntimeException("Unable to configure " + this.getClass().getName() + ": " + e.getMessage());
		}
		catch (ConfigurationException e)
		{
			throw new RuntimeException("Unable to configure " + this.getClass().getName() + ": " + e.getMessage());
		}

		try
		{
			serverSocket = new ServerSocket(myConfig.getAttributeAsInteger("port"));
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to instantiate ServerSocket on configured port", e);
		}
	}

	public void run()
	{
		if (! this.isInitialized())
		{
			getLogger().info("Keel server starts.");

			try
			{
				bootstrap();
			}
			catch (ModelException me)
			{
				System.err.println("Exception initializing container:");
				me.printStackTrace(System.err);
				throw new RuntimeException(me.getMessage());
			}

			this.setInitialized(true);
		}

		try
		{
			socketServiceLoop();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error while running service loop", e);
		}
		finally
		{
			try
			{
				serverSocket.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Error closing server socket", e);
			}
		}
	}

	protected void socketServiceLoop()
	{
		ModelRequest req = null;
		ObjectInputStream reader = null;

		try
		{
			while (! serverSocket.isClosed())
			{
				try
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("Request " + getName() + " starts");
					}

					MultiThreadedProcessor processor = new MultiThreadedProcessor(serverSocket.accept());

					logger.debug("Client connection established.");
					processor.start();

					if (logger.isDebugEnabled())
					{
						logger.debug("Request " + getName() + " serviced");
					}
				}
				catch (Exception e)
				{
					logger.error("Error while processing request: " + e);
				}
				finally
				{
					if (reader != null)
					{
						try
						{
							reader.close();
						}
						catch (Exception ignore)
						{
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Exception while servicing request:", e);
			throw new RuntimeException(e);
		}
		finally
		{
			if (req != null)
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Releasing request " + req.toString());
				}

				myContainer.release(req);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#destroy()
	 */
	public void destroy()
	{
		if (serverSocket != null)
		{
			try
			{
				serverSocket.close();
			}
			catch (IOException ignore)
			{
			}
		}

		super.destroy();
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

		//Set the context to that provided in the original request
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
}
