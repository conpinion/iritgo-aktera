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

package de.iritgo.aktera.scheduler.action;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.scheduler.ui.ScheduleActionFomPart;
import de.iritgo.aktera.ui.listing.RowData;


/**
 *
 */
public class LogScheduleActionFormPart extends ScheduleActionFomPart
{
	/**
	 * @see de.iritgo.aktera.scheduler.ui.ScheduleActionFomPart#createListInfo(ModelRequest, RowData)
	 */
	@Override
	public String createListInfo(ModelRequest request, RowData row) throws PersistenceException, ModelException
	{
		return "scheduleActionLogInfo|" + row.getString("action.stringParam1");
	}
}
