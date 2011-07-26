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

package de.iritgo.aktera.clients.direct;


import de.iritgo.aktera.clients.KeelClient;
import de.iritgo.aktera.clients.KeelStarter;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import java.io.IOException;


/**
 * Special version of the KeelClient interface that "wraps" the entire KeelServer process
 * inside the same VM as the client, allowing an "all-in-one" deployment without
 * any JMS required.
 *
 * NOTE: If you use this client, there may be only one of them running within the
 * VM - e.g. if you deploy both Struts and Cocoon, you cannot have both of them use
 * the KeelClientDirect as the client, as AltRMI will fail on startup of the second
 * container (due to a "port already in use" problem when starting the Instrumentation
 * AltRMI port). If you're using multiple clients, you almost certainly want to be using
 * a JMS-enabled version of KeelClient, so that only one server task is used to service
 * the multiple clients.
 *
 * @author Michael Nash
 */
public class KeelClientDirect implements KeelClient
{
	/** Client id. */
	private int id = 0;

	/** Keel server starter. */
	private KeelStarter starter = null;

	/**
	 * @return The Keel starter.
	 * @throws ModelException
	 */
	public KeelStarter getStarter () throws ModelException
	{
		try
		{
			if (starter == null)
			{
				synchronized (this)
				{
					if (starter == null)
					{
						starter = new KeelStarter ();
						Thread.currentThread ().setName ("KeelClientDirect");
					}
				}
			}

			return starter;
		}
		catch (Exception x)
		{
			throw new ModelException ("Exception loading starter (" + x + ")");
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.KeelClient#execute(de.iritgo.aktera.model.KeelRequest)
	 */
	public KeelResponse execute (KeelRequest request) throws ModelException, IOException
	{
		try
		{
			KeelStarter starter = getStarter ();

			return starter.execute (request);
		}
		catch (Exception x)
		{
			x.printStackTrace (System.err);
			throw new ModelException (x);
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.KeelClient#setId(int)
	 */
	public void setId (int id)
	{
		this.id = id;
	}

	/**
	 * @see de.iritgo.aktera.clients.KeelClient#getId()
	 */
	public int getId ()
	{
		return id;
	}

	/**
	 * @see de.iritgo.aktera.clients.KeelClient#start()
	 */
	public void start () throws ModelException, Exception
	{
		getStarter ().start ();
	}

	/**
	 * @see de.iritgo.aktera.clients.KeelClient#stop()
	 */
	public void stop () throws ModelException, Exception
	{
		getStarter ().stop ();
	}
}
