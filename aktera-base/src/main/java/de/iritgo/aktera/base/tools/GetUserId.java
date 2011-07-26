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

package de.iritgo.aktera.base.tools;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.model.ThreadedModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.getuserid"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.getuserid" id="aktera.tools.getuserid" logger="aktera"
 */
public class GetUserId extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		int uid = 0;

		try
		{
			Context ctx = req.getContext ();
			UserEnvironment ue = null;

			if (ctx != null)
			{
				try
				{
					ue = (UserEnvironment) ctx.get (UserEnvironment.CONTEXT_KEY);
					uid = ue.getUid ();
				}
				catch (ContextException ce)
				{
					log.debug ("Unable to acces user environment from context!");
				}
				catch (AuthorizationException x)
				{
					log.debug ("AuthorizationException!");
				}

				if (ue != null)
				{
					try
					{
						if (ue.getUid () == UserEnvironment.ANONYMOUS_UID)
						{
							log.debug ("BUG, this can't true");
						}
					}
					catch (AuthorizationException e)
					{
						log.debug ("Unable to acces user environment from context!");
						throw new ModelException ("Unable to acces user environment from context!", e);
					}
				}
				else
				{
					log.debug ("Unable to acces user environment from context!");
					throw new ModelException ("Unable to acces user environment from context!");
				}
			}

			PersistentFactory persistentManager = (PersistentFactory) req.getService (PersistentFactory.ROLE, req
							.getDomain ());

			Persistent keelUser = persistentManager.create ("keel.user");

			keelUser.setField ("uid", new Integer (uid));

			if (! keelUser.find ())
			{
				ThreadedModel.sleep (3000);
				res.addError ("notLoggedIn", "You are not logged in.");

				return res;
			}
		}
		catch (PersistenceException x)
		{
			log.error ("Database Error", x);
			throw new ModelException ("$databaseError", x);
		}
		catch (InterruptedException ie)
		{
		}

		res.setAttribute ("userId", new Integer (uid));

		return res;
	}
}
