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


import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.scheduler.action.ScheduleActionHandler;
import de.iritgo.aktera.scheduler.entity.HolidayDAO;
import de.iritgo.aktera.scheduler.entity.Schedule;
import de.iritgo.aktera.scheduler.entity.ScheduleAction;
import de.iritgo.aktera.scheduler.entity.ScheduleDAO;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.quartz.SchedulerException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Schedule manager implementation.
 */
public class ScheduleManagerImpl implements ScheduleManager, StartupHandler
{
	/** When to schedule: Only on holidays */
	private static final int SCHEDULE_ONLY_ON_HOLIDAYS = 1;

	/** All day/night mode jobs are stored in this job grop */
	private static final String JOB_GROUP = "de.iritgo.aktera.scheduler.ScheduleManager";

	/** The scheduler to use. */
	private Scheduler scheduler;

	/** The schedule DAO */
	private ScheduleDAO scheduleDAO;

	/** The holiday DAO */
	private HolidayDAO holidayDAO;

	/** Our logger. */
	private Logger logger;

	/** Action handler configuration */
	private Configuration configuration;

	/** Action handler bean names */
	private Map<String, String> actionHandlers = new HashMap<String, String>();

	/** Action cache (List of actions by schedule id) */
	private Map<Integer, List<ScheduleAction>> scheduleActions = new HashMap<Integer, List<ScheduleAction>>();

	/**
	 * Set the scheduler.
	 */
	public void setScheduler(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	/**
	 * Set the schedule DAO.
	 */
	public void setScheduleDAO(ScheduleDAO scheduleDAO)
	{
		this.scheduleDAO = scheduleDAO;
	}

	/**
	 * Set the holiday DAO.
	 */
	public void setHolidayDAO(HolidayDAO holidayDAO)
	{
		this.holidayDAO = holidayDAO;
	}

	/**
	 * Set the logger.
	 */
	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * Set the Action form part configuration.
	 */
	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Manager initialization.
	 */
	public void initialize()
	{
		logger.info("Creating schedule action handlers");

		Configuration[] configs = configuration.getChildren("handler");

		for (int i = 0; i < configs.length; ++i)
		{
			Configuration config = configs[i];

			try
			{
				String id = config.getAttribute("id");

				actionHandlers.put(id, config.getAttribute("bean"));
			}
			catch (Exception x)
			{
				logger.error("Unable to create action handler '" + config.getAttribute("id", "?") + "'", x);
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup() throws StartupException
	{
		restart();
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ScheduleManager#restart()
	 */
	public synchronized void restart()
	{
		removeJobs();
		createJobs();
	}

	/**
	 * Remove all currently running jobs.
	 */
	private synchronized void removeJobs()
	{
		logger.info("Removing all currently running jobs");
		scheduler.removeAllJobsInGroup(JOB_GROUP);
		scheduleActions.clear();
	}

	/**
	 * Create all scheduled jobs.
	 */
	private synchronized void createJobs()
	{
		logger.info("Scheduling jobs");

		Collection<Schedule> schedules = scheduleDAO.findAllSchedules();
		String errorSchedule = "";

		try
		{
			for (Schedule schedule : schedules)
			{
				errorSchedule = schedule.getName();

				if (! schedule.getAllSeconds() && StringTools.isTrimEmpty(schedule.getSeconds()))
				{
					continue;
				}

				if (! schedule.getAllMinutes() && StringTools.isTrimEmpty(schedule.getMinutes()))
				{
					continue;
				}

				if (! schedule.getAllHours() && StringTools.isTrimEmpty(schedule.getHours()))
				{
					continue;
				}

				if (! schedule.getAllDays() && StringTools.isTrimEmpty(schedule.getDays())
								&& ! schedule.getAllDaysOfWeek() && StringTools.isTrimEmpty(schedule.getDaysOfWeek())
								&& StringTools.isTrimEmpty(schedule.getHolidaysCountry()))
				{
					continue;
				}

				Properties params = new Properties();

				params.put("scheduleId", schedule.getId());

				ScheduleOn times = new ScheduleOn();

				times.setSeconds(schedule.getAllSeconds() ? "*" : schedule.getSeconds());
				times.setMinutes(schedule.getAllMinutes() ? "*" : schedule.getMinutes());
				times.setHours(schedule.getAllHours() ? "*" : schedule.getHours());

				times.setMonths(schedule.getAllMonths() ? "*" : schedule.getMonths());

				if (schedule.getAllDays() || ! StringTools.isTrimEmpty(schedule.getDays()))
				{
					times.setDaysOfMonth(schedule.getAllDays() ? "*" : schedule.getDays());
				}
				else if (schedule.getAllDaysOfWeek() || ! StringTools.isTrimEmpty(schedule.getDaysOfWeek()))
				{
					times.setDaysOfWeek(schedule.getAllDaysOfWeek() ? "*" : schedule.getDaysOfWeek());
				}

				if (schedule.getHolidaysAllowance() != null && schedule.getHolidaysAllowance() != 0
								&& ! StringTools.isTrimEmpty(schedule.getHolidaysCountry()))
				{
					if (schedule.getHolidaysAllowance() == SCHEDULE_ONLY_ON_HOLIDAYS)
					{
						times.setDaysOfMonth("*");
					}

					params.put("holidaysAllowance", schedule.getHolidaysAllowance());
					params.put("holidaysCountry", schedule.getHolidaysCountry());

					if (! StringTools.isTrimEmpty(schedule.getHolidaysProvince()))
					{
						params.put("holidaysProvince", schedule.getHolidaysProvince());
					}
				}

				scheduler.scheduleBean("Job-" + schedule.getId() + ": " + schedule.getName(), JOB_GROUP,
								"de.iritgo.aktera.scheduler.ScheduleActionJob", params, times);

				logger.debug("Job '" + schedule.getName() + "' created");
			}
		}
		catch (Exception x)
		{
			logger.error("Can't set scheduler for service: " + errorSchedule);
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ScheduleManager#executeSchedule(java.lang.Integer)
	 */
	public void executeSchedule(Integer scheduleId)
	{
		List<ScheduleAction> actions = getScheduleActions(scheduleId);

		for (ScheduleAction action : actions)
		{
			executeAction(action);
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ScheduleManager#executeAction(de.iritgo.aktera.scheduler.entity.ScheduleAction)
	 */
	public void executeAction(ScheduleAction action)
	{
		ScheduleActionHandler handler = (ScheduleActionHandler) KeelContainer.defaultContainer().getSpringBean(
						actionHandlers.get(action.getType()));

		handler.execute(action);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ScheduleManager#getScheduleActions(java.lang.Integer)
	 */
	public synchronized List<ScheduleAction> getScheduleActions(Integer scheduleId)
	{
		List<ScheduleAction> actions = scheduleActions.get(scheduleId);

		if (actions != null)
		{
			return actions;
		}

		actions = scheduleDAO.findScheduleActionsByScheduleId(scheduleId);
		scheduleActions.put(scheduleId, actions);

		return actions;
	}

	/**
	 * @see de.iritgo.aktera.scheduler.ScheduleManager#dateIsHoliday(java.util.Date,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean dateIsHoliday(Date date, String country, String province)
	{
		return holidayDAO.dateIsHoliday(date, country, province);
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown() throws ShutdownException
	{
		try
		{
			scheduler.dispose();
		}
		catch (SchedulerException ignored)
		{
		}
	}
}
