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

package de.iritgo.aktera.ui.listing;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.struts.BeanAction;
import de.iritgo.aktera.struts.BeanRequest;
import de.iritgo.aktera.struts.BeanResponse;
import de.iritgo.aktera.ui.UIController;
import de.iritgo.aktera.ui.UIControllerException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.execute-listitem-command"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.execute-listitem-command" id="aktera.tools.execute-listitem-command" logger="aktera"
 */
public class ExecuteListItemCommand extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		String modelName = null;

		for (Iterator i = req.getParameters().entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			String key = (String) entry.getKey();

			if (! key.startsWith("_lp") && key.endsWith("ExecuteModel"))
			{
				modelName = (String) entry.getValue();

				if (modelName != null && ! modelName.equals("null"))
				{
					break;
				}
			}
		}

		String forward = null;

		ModelResponse res = req.createResponse();

		if (modelName != null && ! modelName.equals("null") && ! modelName.startsWith("_BEAN_."))
		{
			Model model = (Model) req.getService(Model.ROLE, modelName);

			ModelRequest mreq = (ModelRequest) req.getService(ModelRequest.ROLE, "default");

			mreq.copyFrom(req);

			for (Iterator i = req.getParameters().keySet().iterator(); i.hasNext();)
			{
				String key = (String) i.next();

				if (key.startsWith("_lep"))
				{
					String keyName = key.substring(4);

					if (req.getParameter(keyName) == null)
					{
						mreq.setParameter(keyName, req.getParameter(key));
					}
				}

				if (key.startsWith("_lp"))
				{
					String keyName = key.substring(3);

					if (req.getParameter(keyName) == null)
					{
						mreq.setParameter(keyName, req.getParameter(key));
					}
				}
			}

			res = model.execute(mreq);

			try
			{
				Configuration[] attributes = model.getConfiguration().getChildren("attribute");

				for (int i = 0; i < attributes.length; ++i)
				{
					if ("forward".equals(attributes[i].getAttribute("name", null)))
					{
						forward = attributes[i].getAttribute("value");
					}
				}
			}
			catch (ConfigurationException x)
			{
			}
		}
		else if (modelName != null && modelName.startsWith("_BEAN_."))
		{
			try
			{
				Map<String, Object> parameters = new HashMap();

				for (Iterator i = req.getParameters().keySet().iterator(); i.hasNext();)
				{
					String key = (String) i.next();

					if (key.startsWith("_lep"))
					{
						String keyName = key.substring(4);

						if (req.getParameter(keyName) == null)
						{
							parameters.put(keyName, req.getParameter(key));
						}
					}
					else if (key.startsWith("_lp"))
					{
						String keyName = key.substring(3);

						if (req.getParameter(keyName) == null)
						{
							parameters.put(keyName, req.getParameter(key));
						}
					}
					else
					{
						parameters.put(key, req.getParameter(key));
					}
				}

				BeanRequest uiRequest = new BeanRequest();

				uiRequest.setLocale(req.getLocale());
				uiRequest.setBean(modelName.replace("_BEAN_.", ""));
				uiRequest.setParameters(parameters);
				uiRequest.setUserEnvironment((UserEnvironment) req.getContext().get(UserEnvironment.CONTEXT_KEY));

				BeanResponse uiResponse = new BeanResponse();

				BeanAction.execute(uiRequest, uiResponse);

				if (! UIController.DEFAULT_FORWARD.equals(uiResponse.getForward()))
				{
					res.setAttribute("forward", uiResponse.getForward());
				}

				for (Map.Entry<String, ResponseElement> re : uiResponse.getElements().entrySet())
				{
					res.add(re.getValue());
				}
			}
			catch (ContextException x)
			{
				throw new ModelException(x);
			}
			catch (AuthorizationException x)
			{
				throw new ModelException(x);
			}
			catch (UIControllerException x)
			{
				throw new ModelException(x);
			}
		}

		if (forward == null)
		{
			forward = (String) res.getAttribute("forward");
		}

		if (forward != null)
		{
			res.setAttribute("forward", forward);

			return res;
		}
		else
		{
			Command cmd = res.createCommand((String) req.getParameter("_lm"));

			for (Iterator i = req.getParameters().keySet().iterator(); i.hasNext();)
			{
				String key = (String) i.next();

				if (key.startsWith("_lp"))
				{
					cmd.setParameter(key.substring(3), req.getParameter(key));
				}
			}

			return cmd.execute(req, res);
		}
	}
}
