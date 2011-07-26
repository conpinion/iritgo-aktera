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

package de.iritgo.aktera.clients;


import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import java.io.IOException;


/**
 * Interface to provide for pluggable implementations of Keel clients
 *
 * @version        $Revision: 1.1 $        $Date: 2003/12/29 06:59:15 $
 * @author Michael Nash
 * @author Shash Chatterjee
 * Created on May 24, 2003
 */
public interface KeelClient
{
	/**
	 * The key used to store the Keel-client class in the context
	 */
	public static final String CLIENT_CLASS = "keel.client.class";

	/**
	 * The key used to store the keel-client config string in the context
	 */
	public static final String CLIENT_CONFIG = "keel.client.config";

	/**
	 * The key used to store the keel config directory in the context
	 */
	public static final String CONFIG_DIR = "keel.config.dir";

	/**
	 * Run a business-process, using a request/response architecture
	 * @param req The request to pass to Keel
	 * @return Keel response
	 * @throws ModelException
	 * @throws IOException
	 */
	public KeelResponse execute (KeelRequest req) throws ModelException, IOException;

	/**
	 * Set the client id
	 * @param newId The ID to set
	 */
	public void setId (int newId);

	/**
	 * Retrieve the client id
	 * @return The client ID
	 */
	public int getId ();

	/**
	 * Start the client.
	 *
	 * @throws ModelException
	 * @throws Exception
	 */
	public void start () throws ModelException, Exception;

	/**
	 * Stop the client.
	 *
	 * @throws ModelException
	 * @throws Exception
	 */
	public void stop () throws ModelException, Exception;
}
