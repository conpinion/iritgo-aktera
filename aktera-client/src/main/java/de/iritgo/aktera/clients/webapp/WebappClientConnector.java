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

package de.iritgo.aktera.clients.webapp;


import de.iritgo.aktera.clients.ClientConnector;
import de.iritgo.aktera.clients.ClientException;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;


/**
 * Interface to bridge a particular webapp or servlet client
 * software-system to the client-side of Keel.
 *
 * @version $Revision: 1.2 $    $Date: 2004/03/06 18:52:32 $
 * @author Schatterjee
 * Created on May 24, 2003
 */
public interface WebappClientConnector extends ClientConnector
{
	/**
	 * Run the business model, using a request/response pair
	 * The description sounds curious since the method doesn't
	 * take a request as an argument!  This is because although
	 * most webapps use the HttpServletRequest/HttpServletResponse
	 * pair, some, particularly Cocoon, do not and use their
	 * own abstraction.  So, the method preferred here, is that
	 * the WebappClientConnector implementation will pass in the
	 * the appropriate request/response and other paramters in the
	 * constructor and use those during the execution of this method.
	 * See de.iritgo.aktera.struts.StrutsClientConnector and
	 * de.iritgo.aktera.clients.cocoon.CocoocClientConnector for examples.
	 *
	 * @return  The Keel response
	 * @throws ClientException
	 * @throws ModelException
	 */
	public KeelResponse execute() throws ClientException, ModelException;

	/**
	 * Determine a string that tells the client where to navigate next
	 * @param kres The Keel response returned by execute()
	 * @return
	 */
	public String getForward(KeelResponse kres);
}
