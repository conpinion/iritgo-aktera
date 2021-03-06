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
import de.iritgo.aktera.scheduler.entity.ScheduleAction;
import de.iritgo.aktera.scheduler.entity.ScheduleDAO;
import de.iritgo.aktera.ui.listing.AbstractListCommandModel;
import de.iritgo.simplelife.math.NumberTools;


/**
 * This model moves schedule actions one position down.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="de.iritgo.aktera.scheduler.MoveScheduleActionDown"
 * @x-avalon.lifestyle type="singleton"
 * @model.model
 *   name="de.iritgo.aktera.scheduler.MoveScheduleActionDown"
 *   id="de.iritgo.aktera.scheduler.MoveScheduleActionDown"
 *   logger="aktera"
 */
public class MoveScheduleActionDown extends AbstractListCommandModel
{
	/**
	 *
	 */
	public MoveScheduleActionDown()
	{
		super("actionId");
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.AbstractListCommandModel#execute(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, java.lang.String)
	 */
	@Override
	protected void execute(ModelRequest request, ModelResponse response, String id) throws ModelException
	{
		moveScheduleActionDown(request, id);
	}

	/**
	 * Move the schedule action.
	 *
	 * @param request The model request
	 * @param id The schedule id
	 * @return True if the schedule action has moved
	 * @throws ModelException
	 */
	protected boolean moveScheduleActionDown(ModelRequest request, String id) throws ModelException
	{
		ScheduleDAO scheduleDAO = (ScheduleDAO) request.getSpringBean(ScheduleDAO.ID);
		ScheduleAction action = scheduleDAO.findScheduleActionById(NumberTools.toIntInstance(id));

		if (action != null)
		{
			return scheduleDAO.moveScheduleActionDown(action);
		}

		return false;
	}
}
