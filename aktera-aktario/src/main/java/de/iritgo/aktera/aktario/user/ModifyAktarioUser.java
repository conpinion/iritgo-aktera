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
import de.iritgo.aktario.core.user.*;
import de.iritgo.aktera.address.AddressDAO;
import de.iritgo.aktera.address.entity.Address;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.Option;


/**
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name=aktera.aktario.user.modify-aktario-user
 * @x-avalon.lifestyle type=singleton
 * @model.model name="aktera.aktario.user.modify-aktario-user" id="aktera.aktario.user.modify-aktario-user" logger="aktera"
 * @model.parameter name="id" required="true"
 */
public class ModifyAktarioUser extends StandardLogEnabledModel
{
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		try
		{
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

					AddressDAO addressDAO = (AddressDAO) SpringTools.getBean (AddressDAO.ID);
					Option<Address> address = addressDAO.findAddressByPartyId (party.getFieldInt ("partyId"));
					AktarioUser aktarioUser = aktarioUsers.getUserByName (user.getFieldString ("name"));
					if (address.full ())
					{
						if (StringTools.isTrimEmpty (address.get ().getFirstName ()))
						{
							aktarioUser.setFullName (address.get ().getLastName ());
						}
						else
						{
							aktarioUser.setFullName (address.get ().getFirstName () + " "
											+ address.get ().getLastName ());
						}
					}

					String password = (String) req.getParameter ("password");

					if ((password != null) && (! password.equals ("")))
					{
						aktarioUser.setPassword (password);
					}

					if (address.full ())
					{
						aktarioUser.setEmail (address.get ().getEmail ());
					}

					if (aktarioUser != null)
					{
						aktarioUserManager.modifyUser (aktarioUser);
					}
				}
			}
		}
		catch (PersistenceException x)
		{
			System.out.println ("ModifyAktarioUser: " + x);
		}

		return req.createResponse ();
	}
}
