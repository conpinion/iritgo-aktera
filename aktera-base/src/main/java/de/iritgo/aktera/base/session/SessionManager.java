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

package de.iritgo.aktera.base.session;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.tools.ModelTools;
import org.apache.avalon.framework.activity.Initializable;


/**
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name=aktera.session.session-manager
 * @x-avalon.lifestyle type=singleton
 * @model.model
 *   name="aktera.session.session-manager"
 *   id="aktera.session.session-manager"
 *   logger="aktera"
 *   activation="startup"
 */
public class SessionManager extends StandardLogEnabledModel implements Initializable
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		String command = req.getParameterAsString ("command");

		if ("update".equals (command))
		{
		}
		else if ("login".equals (command))
		{
		}
		else if ("logout".equals (command))
		{
		}
		else
		{
			if ("destroyed".equals (req.getParameterAsString ("sessionEvent")))
			{
				ModelTools.callModel (req, "aktera.session.logout");
			}
		}

		return res;
	}

	/**
	 * Initialize the component.
	 */
	public void initialize ()
	{
		ModelRequest req = null;

		// 		try
		// 		{
		// 			req = ModelTools.createModelRequest ();

		// 			Scheduler scheduler = (Scheduler) req.getService (Scheduler.ROLE, "default");

		// 			ScheduledRequest scheduledRequest =
		// 				(ScheduledRequest) req.getService (ScheduledRequest.ROLE, "default");

		// 			PropertyUtils.setSimpleProperty (
		// 				scheduledRequest, "runClass", new SessionManager ());

		// 			ModelRequest request = (ModelRequest) req.getService (ModelRequest.ROLE, "default");

		// 			request.setModel ("aktera.session.session-manager");
		// 			request.setParameter ("command", "update");

		// 			scheduledRequest.setRepeatInterval (15);
		// 			scheduledRequest.setRepeatParam (-1);
		// 			scheduledRequest.setSimpleJob (true);

		// 			scheduledRequest.setRequest (request);

		// 			scheduler.schedule (scheduledRequest);
		// 		}
		// 		catch (Exception x)
		// 		{
		// 			log.error ("Can not schedule external ip address check", x);
		// 		}
		// 		finally
		// 		{
		// 			ModelTools.releaseModelRequest (req);
		// 		}
	}
}
