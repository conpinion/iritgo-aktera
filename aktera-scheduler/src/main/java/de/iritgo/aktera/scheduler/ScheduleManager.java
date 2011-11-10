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

package de.iritgo.aktera.scheduler;


import de.iritgo.aktera.scheduler.entity.ScheduleAction;
import java.util.Date;
import java.util.List;


/**
 * Interface of the schedule manager.
 */
public interface ScheduleManager
{
	/** Bean id */
	public static final String ID = "de.iritgo.aktera.scheduler.ScheduleManager";

	/**
	 * Restart all scheduled jobs.
	 */
	public void restart();

	/**
	 * Execute a schedule.
	 *
	 * @param scheduleId The id of the schedule to execute.
	 */
	public void executeSchedule(Integer scheduleId);

	/**
	 * Execute a schedule action.
	 *
	 * @param action The schedule action to execute.
	 */
	public void executeAction(ScheduleAction action);

	/**
	 * Retrieve a list of all actions of the specified schedule.
	 *
	 * @param scheduleId The id of the schedule
	 * @return A list of schedule actions
	 */
	public List<ScheduleAction> getScheduleActions(Integer scheduleId);

	/**
	 * Check if the given date is a holiday.
	 *
	 * @param date The date to check
	 * @param country The country which holidays should be accounted for
	 * @param province The (optional) country province
	 * @return True if date is a holiday
	 */
	public boolean dateIsHoliday(Date date, String country, String province);
}
