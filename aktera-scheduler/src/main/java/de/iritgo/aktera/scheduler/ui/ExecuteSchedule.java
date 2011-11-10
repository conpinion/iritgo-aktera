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


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.scheduler.ScheduleManager;
import de.iritgo.aktera.ui.listing.AbstractListCommandModel;
import de.iritgo.simplelife.math.NumberTools;


/**
 * This model moves schedules one position down.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="de.iritgo.aktera.scheduler.ExecuteSchedule"
 * @x-avalon.lifestyle type="singleton"
 * @model.model
 *   name="de.iritgo.aktera.scheduler.ExecuteSchedule"
 *   id="de.iritgo.aktera.scheduler.ExecuteSchedule"
 *   logger="aktera"
 */
public class ExecuteSchedule extends AbstractListCommandModel
{
	/**
	 * @see de.iritgo.aktera.ui.listing.AbstractListCommandModel#execute(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, java.lang.String)
	 */
	@Override
	protected void execute(ModelRequest request, ModelResponse response, String id) throws ModelException
	{
		ScheduleManager scheduleManager = (ScheduleManager) request.getSpringBean(ScheduleManager.ID);

		scheduleManager.executeSchedule(NumberTools.toIntInstance(id, - 1));
	}
}
