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


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.session.get-session-info"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.session.get-session-info" id="aktera.session.get-session-info" logger="aktera"
 */
public class GetSessionInfo extends StandardLogEnabledModel
{
	/** Supported languages. */
	protected static Map languages;

	static
	{
		languages = new HashMap();
		languages.put("de", Locale.GERMAN);
		languages.put("en", Locale.ENGLISH);
	}

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		Context ctx = req.getContext();

		if (ctx != null)
		{
			UserEnvironment userEnv = null;

			try
			{
				userEnv = (UserEnvironment) ctx.get(UserEnvironment.CONTEXT_KEY);
			}
			catch (ContextException x)
			{
			}

			try
			{
				if (userEnv != null && userEnv.getUid() != UserEnvironment.ANONYMOUS_UID)
				{
					if (userEnv.getAttribute("sessionInfoLoaded") == null
									|| "N".equals(userEnv.getAttribute("sessionInfoLoaded")))
					{
						try
						{
							PersistentFactory persistentManager = (PersistentFactory) req.getService(
											PersistentFactory.ROLE, req.getDomain());

							Persistent preferences = persistentManager.create("aktera.Preferences");

							preferences.setField("userId", new Integer(userEnv.getUid()));
							preferences.find();
							userEnv.setAttribute("sessionPreferences", preferences.getBean());

							Persistent party = persistentManager.create("aktera.Party");

							party.setField("userId", new Integer(userEnv.getUid()));

							if (party.find())
							{
								Persistent address = persistentManager.create("aktera.Address");

								address.setField("partyId", party.getField("partyId"));

								if (address.find())
								{
									String firstName = address.getFieldString("firstName");
									String lastName = address.getFieldString("lastName");
									String displayName = (firstName != null ? firstName + " " : "") + lastName;

									userEnv.setAttribute("sessionDisplayName", displayName);
									userEnv.setAttribute("sessionFirstName", firstName != null ? firstName : "");
									userEnv.setAttribute("sessionLastName", lastName != null ? lastName : "");

									Locale locale = (Locale) languages.get(preferences.getFieldString("language"));

									userEnv.setAttribute("sessionLanguage", locale != null ? locale : Locale.GERMAN);
									userEnv.setAttribute(I18N.USER_CONTEXT_LOCALE_KEY, locale != null ? locale
													: Locale.GERMAN);
									userEnv.setAttribute("sessionInfoLoaded", "Y");
								}
							}
						}
						catch (PersistenceException x)
						{
							throw new ModelException(x);
						}
					}

					res.addOutput("sessionDisplayName", (String) userEnv.getAttribute("sessionDisplayName"));
					res.addOutput("sessionFirstName", (String) userEnv.getAttribute("sessionFirstName"));
					res.addOutput("sessionLastName", (String) userEnv.getAttribute("sessionLastName"));
					res.addOutput("sessionLoginName", (String) userEnv.getLoginName());

					Output lang = res.createOutput("sessionLanguage");

					lang.setContent(userEnv.getAttribute("sessionLanguage"));
					res.add(lang);
				}
				else
				{
					Output lang = res.createOutput("sessionLanguage");

					lang.setContent(Locale.GERMAN);
					res.add(lang);
				}
			}
			catch (AuthorizationException x)
			{
				throw new ModelException(x);
			}
		}

		return res;
	}
}
