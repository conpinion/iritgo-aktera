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

package de.iritgo.aktera.scheduler.ui;


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class ScheduleActionFormPartManagerImpl implements ScheduleActionFormPartManager
{
	/** Action form part configuration */
	private Configuration configuration;

	/** Our logger. */
	private Logger logger;

	/** Action form parts */
	private Map<String, ScheduleActionFormPartInfo> actionFormParts = new HashMap<String, ScheduleActionFormPartInfo> ();

	/**
	 * Set the Action form part configuration.
	 */
	public void setConfiguration (Configuration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Set the logger.
	 */
	public void setLogger (Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * Manager initialization.
	 */
	public void initialize ()
	{
		logger.info ("Creating schedule action form parts");

		Configuration[] configs = configuration.getChildren ("part");

		for (int i = 0; i < configs.length; ++i)
		{
			Configuration config = configs[i];

			try
			{
				ScheduleActionFormPartInfo info = new ScheduleActionFormPartInfo ();

				info.setId (config.getAttribute ("id"));
				info.setFormPart ((ScheduleActionFomPart) Class.forName (config.getAttribute ("class")).newInstance ());
				info.setBundle (config.getAttribute ("bundle", "aktera-scheduler"));
				info.setInfoKey (config.getAttribute ("infoKey", "scheduleAction"));
				actionFormParts.put (info.getId (), info);
			}
			catch (Exception x)
			{
				logger.error ("Unable to create action form part '" + config.getAttribute ("id", "?") + "'", x);
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ui.ScheduleActionFormPartManager#getActionFormParts()
	 */
	public Collection<ScheduleActionFormPartInfo> getActionFormParts ()
	{
		return actionFormParts.values ();
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ui.ScheduleActionFormPartManager#getActionFormPart(java.lang.String)
	 */
	public ScheduleActionFormPartInfo getActionFormPart (String id)
	{
		return actionFormParts.get (id);
	}
}
