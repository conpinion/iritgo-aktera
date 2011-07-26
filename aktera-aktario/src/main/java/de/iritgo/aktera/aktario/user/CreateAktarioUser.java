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

package de.iritgo.aktera.aktario.user;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.user.AktarioUser;
import de.iritgo.aktario.core.user.AktarioUserManager;
import de.iritgo.aktario.core.user.AktarioUserRegistry;
import de.iritgo.aktario.framework.server.Server;
import de.iritgo.aktario.framework.user.UserRegistry;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.simplelife.math.NumberTools;


/**
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name=aktera.aktario.user.create-aktario-user
 * @x-avalon.lifestyle type=singleton
 * @model.model name="aktera.aktario.user.create-aktario-user" id="aktera.aktario.user.create-aktario-user" logger="aktera"
 */
public class CreateAktarioUser extends StandardLogEnabledModel
{
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		try
		{
			UserRegistry iritgoUsers = Server.instance ().getUserRegistry ();
			AktarioUserRegistry aktarioUsers = ((AktarioUserManager) Engine.instance ().getManager (
							"AktarioUserManager")).getUserRegistry ();
			AktarioUserManager aktarioUserManager = (AktarioUserManager) Engine.instance ().getManager (
							"AktarioUserManager");

			int userId = NumberTools.toInt (req.getParameter ("userId"), - 1);

			if (userId != - 1)
			{
				PersistentFactory persistentManager = (PersistentFactory) req.getService (PersistentFactory.ROLE, req
								.getDomain ());

				Persistent user = persistentManager.create ("keel.user");

				user.setField ("uid", new Integer (userId));

				if (user.find ())
				{
					Persistent party = persistentManager.create ("aktera.Party");

					party.setField ("userId", new Integer (userId));
					party.find ();

					Persistent address = persistentManager.create ("aktera.Address");

					address.setField ("partyId", party.getField ("partyId"));
					address.find ();

					AktarioUser aktarioUser = new AktarioUser ();

					if ((address.getFieldString ("firstName") == null)
									|| address.getFieldString ("firstName").equals (""))
					{
						aktarioUser.setFullName (address.getFieldString ("lastName"));
					}
					else
					{
						aktarioUser.setFullName (address.getFieldString ("firstName") + " "
										+ address.getFieldString ("lastName"));
					}

					aktarioUser.setName (user.getFieldString ("name"));

					String password = (String) req.getParameter ("password");

					if ((password != null) && (! password.equals ("")))
					{
						aktarioUser.setPassword (password);
					}

					aktarioUser.setEmail (address.getFieldString ("email"));

					if (aktarioUser != null)
					{
						aktarioUserManager.addUser (aktarioUser);
					}
				}
			}
		}
		catch (PersistenceException x)
		{
			System.out.println ("CreateAktarioUser: " + x);
		}

		return req.createResponse ();
	}
}
