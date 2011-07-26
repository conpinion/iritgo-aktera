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


import org.apache.avalon.framework.configuration.ConfigurationException;
import de.iritgo.aktera.base.database.UpdateHelper;
import de.iritgo.aktera.base.server.SystemCheckService;
import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.ui.tools.UserTools;


/**
 * Goto the start model.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.goto-start-model"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.goto-start-model"
 *              id="aktera.tools.goto-start-model" logger="aktera"
 */
public class GotoStartModel extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		SystemConfigManager systemConfigManager = (SystemConfigManager) req.getSpringBean (SystemConfigManager.ID);

		ModelResponse res = req.createResponse ();

		if (! UpdateHelper.databaseExists (req))
		{
			return res.createCommand ("aktera.database.create-prompt").execute (req, res);
		}

		try
		{
			if (UpdateHelper.needUpdate (req))
			{
				return res.createCommand ("aktera.database.update-prompt").execute (req, res);
			}
		}
		catch (ConfigurationException x)
		{
			throw new ModelException ("ConfigurationException during needUpdate() check", x);
		}

		try
		{
			SystemCheckService systemCheckService = (SystemCheckService) SpringTools.getBean (SystemCheckService.ID);
			if (systemCheckService != null && ! systemCheckService.isSystemReady ())
			{
				return res.createCommand (systemCheckService.getSystemCheckModel ()).execute (req, res);
			}
		}
		catch (Exception ignore)
		{
			// No system check service configured.
		}

		String startModel = (String) systemConfigManager.get ("system", "startModel");

		if (startModel != null && UserTools.getCurrentUserId (req) == null)
		{
			return res.createCommand ("aktera.session.prompt-login").execute (req, res);
		}

		if (UserTools.currentUserIsInGroup (req, "admin"))
		{
			return res.createCommand ((String) systemConfigManager.get ("system", "startModelAdmin"))
							.execute (req, res);
		}
		else if (UserTools.currentUserIsInGroup (req, "manager"))
		{
			return res.createCommand ((String) systemConfigManager.get ("system", "startModelManager")).execute (req,
							res);
		}

		return res.createCommand (startModel).execute (req, res);
	}
}
