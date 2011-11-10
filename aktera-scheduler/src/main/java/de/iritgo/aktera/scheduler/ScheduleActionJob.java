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


import de.iritgo.aktera.scheduler.entity.Schedule;
import org.apache.avalon.framework.logger.Logger;
import java.util.Date;
import java.util.Map;


public class ScheduleActionJob implements Scheduleable
{
	/** Our logger. */
	private Logger logger;

	/** The schedule manager */
	private ScheduleManager scheduleManager;

	/**
	 * Set the logger.
	 */
	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * Set the schedule manager.
	 */
	public void setScheduleManager(ScheduleManager scheduleManager)
	{
		this.scheduleManager = scheduleManager;
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduleable#schedule(java.util.Map)
	 */
	public void schedule(Map parameters)
	{
		if (parameters.containsKey("holidaysAllowance"))
		{
			Schedule.HolidaysAllowance allowance = Schedule.HolidaysAllowance.values()[(Integer) parameters
							.get("holidaysAllowance")];
			boolean isHoliday = scheduleManager.dateIsHoliday(new Date(), (String) parameters.get("holidaysCountry"),
							(String) parameters.get("holidaysProvince"));

			if ((allowance.equals(Schedule.HolidaysAllowance.ONLY_HOLIDAYS) && ! isHoliday)
							|| (allowance.equals(Schedule.HolidaysAllowance.EXCLUDE_HOLIDAYS) && isHoliday))
			{
				return;
			}
		}

		scheduleManager.executeSchedule((Integer) parameters.get("scheduleId"));
	}
}
