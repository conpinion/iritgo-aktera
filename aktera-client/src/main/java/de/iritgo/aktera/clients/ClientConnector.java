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
import org.apache.commons.logging.Log;
import java.util.Map;


/**
 * Interface to bridge a particular client software-system to the client-side of Keel.
 *
 * @version        $Revision: 1.2 $        $Date: 2004/03/06 16:18:41 $
 * @author Schatterjee
 * Created on May 24, 2003
 */
public interface ClientConnector
{
	/**
	 * Run the business-model using a request/response pair
	 * @param req The request to pass to Keel
	 * @return The Keel response
	 * @throws ClientException
	 * @throws ModelException
	 */
	public KeelResponse execute(KeelRequest req) throws ClientException, ModelException;

	/**
	 * Set the logger
	 * @param log the logger to use
	 */
	public void setLogger(Log log);

	/**
	 * Context to retrieve various configuration items from
	 * @see de.iritgo.aktera.clients.KeelClient for items that can be set
	 * @param clientContext Contains all the configuration items
	 */
	public void setContext(Map clientContext);
}
