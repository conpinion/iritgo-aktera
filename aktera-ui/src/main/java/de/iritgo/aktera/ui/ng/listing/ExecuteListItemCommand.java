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

package de.iritgo.aktera.ui.ng.listing;


import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.authorization.Security;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.struts.BeanAction;
import de.iritgo.aktera.struts.BeanRequest;
import de.iritgo.aktera.struts.BeanResponse;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ExecuteListItemCommand extends AbstractUIController
{
	public ExecuteListItemCommand ()
	{
		security = Security.NONE;
	}

	public void execute (UIRequest request, UIResponse response) throws UIControllerException
	{
		String beanName = null;

		for (Iterator i = request.getParameters ().entrySet ().iterator (); i.hasNext ();)
		{
			Map.Entry entry = (Map.Entry) i.next ();
			String key = (String) entry.getKey ();

			if (! key.startsWith ("_lp") && key.endsWith ("ExecuteModel"))
			{
				beanName = (String) entry.getValue ();

				if (beanName != null && ! beanName.equals ("null"))
				{
					break;
				}
			}
		}

		if (beanName != null && beanName.startsWith ("_BEAN_."))
		{
			try
			{
				Map<String, Object> parameters = new HashMap ();

				for (Iterator i = request.getParameters ().keySet ().iterator (); i.hasNext ();)
				{
					String key = (String) i.next ();

					if (key.startsWith ("_lep"))
					{
						String keyName = key.substring (4);

						if (request.getParameter (keyName) == null)
						{
							parameters.put (keyName, request.getParameter (key));
						}
					}
					else if (key.startsWith ("_lp"))
					{
						String keyName = key.substring (3);

						if (request.getParameter (keyName) == null)
						{
							parameters.put (keyName, request.getParameter (key));
						}
					}
					else
					{
						parameters.put (key, request.getParameter (key));
					}
				}

				BeanRequest uiRequest = new BeanRequest ();

				uiRequest.setLocale (request.getLocale ());
				uiRequest.setBean (beanName.replace ("_BEAN_.", ""));
				uiRequest.setParameters (parameters);
				uiRequest.setUserEnvironment (request.getUserEnvironment ());

				BeanResponse uiResponse = new BeanResponse ();

				BeanAction.execute (uiRequest, uiResponse);

				if (! UIController.DEFAULT_FORWARD.equals (uiResponse.getForward ()))
				{
					response.setForward (uiResponse.getForward ());
				}

				for (Map.Entry<String, ResponseElement> re : uiResponse.getElements ().entrySet ())
				{
					response.add (re.getValue ());
				}
			}
			catch (AuthorizationException x)
			{
				throw new UIControllerException (x);
			}
			catch (UIControllerException x)
			{
				throw new UIControllerException (x);
			}
		}

		if (response.getForward () == null)
		{
			String listingBeanName = (String) request.getParameter ("_lm");
			BeanRequest uiRequest = new BeanRequest ();

			uiRequest.setLocale (request.getLocale ());
			uiRequest.setBean (listingBeanName.replace ("_BEAN_.", ""));

			Map<String, Object> parameters = new HashMap ();

			for (Iterator i = request.getParameters ().keySet ().iterator (); i.hasNext ();)
			{
				String key = (String) i.next ();

				if (key.startsWith ("_lp"))
				{
					parameters.put (key.substring (3), request.getParameter (key));
				}
			}

			uiRequest.setParameters (parameters);
			uiRequest.setUserEnvironment (request.getUserEnvironment ());

			try
			{
				BeanAction.execute (uiRequest, response);
			}
			catch (AuthorizationException x)
			{
				throw new UIControllerException (x);
			}
		}
	}
}
