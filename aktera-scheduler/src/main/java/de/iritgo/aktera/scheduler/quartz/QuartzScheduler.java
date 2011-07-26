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

package de.iritgo.aktera.scheduler.quartz;


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.scheduler.ScheduleOn;
import de.iritgo.aktera.scheduler.ScheduleRepeated;
import de.iritgo.aktera.scheduler.ScheduleTimes;
import de.iritgo.aktera.scheduler.Scheduler;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import java.util.Map;


/**
 * Implementation of the Scheduler service interface that uses Quartz to schedule the jobs.
 */
public class QuartzScheduler implements Scheduler
{
	/** Our quartz scheduler. */
	private org.quartz.Scheduler scheduler;

	/**
	 * Initialize and start the scheduler.
	 * @throws SchedulerException
	 */
	public void initialize () throws SchedulerException
	{
		if (scheduler == null)
		{
			SchedulerFactory schedulerFactory = new StdSchedulerFactory ();

			scheduler = schedulerFactory.getScheduler ();
			scheduler.start ();
		}
	}

	/**
	 * Stop the scheduler and free all resources.
	 * @throws SchedulerException
	 */
	public void dispose () throws SchedulerException
	{
		if (scheduler != null)
		{
			scheduler.shutdown ();
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#scheduleBean(java.lang.String, java.lang.String, java.util.Map, de.iritgo.aktera.scheduler.ScheduleTimes)
	 */
	@SuppressWarnings("unchecked")
	public void scheduleBean (String jobName, String beanName, Map parameters, ScheduleTimes times)
	{
		scheduleBean (jobName, org.quartz.Scheduler.DEFAULT_GROUP, beanName, parameters, times);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#scheduleBean(java.lang.String, java.lang.String, java.lang.String, java.util.Map, de.iritgo.aktera.scheduler.ScheduleTimes)
	 */
	@SuppressWarnings("unchecked")
	public void scheduleBean (String jobName, String groupName, String beanName, Map parameters, ScheduleTimes times)
	{
		try
		{
			JobDetail jobDetail = new JobDetail (jobName, groupName, QuartzBeanJob.class);
			JobDataMap jobData = new JobDataMap ();

			jobData.put ("bean", beanName);
			jobData.put ("parameters", parameters);
			jobDetail.setJobDataMap (jobData);

			Trigger trigger = createTrigger (jobName, groupName, times);

			synchronized (scheduler)
			{
				scheduler.scheduleJob (jobDetail, trigger);
			}
		}
		catch (Exception x)
		{
			System.err.println ("[QuartzScheduler] " + x.toString ());
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#scheduleModel(java.lang.String, de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.scheduler.ScheduleTimes)
	 */
	public void scheduleModel (String jobName, ModelRequest request, ScheduleTimes times)
	{
		scheduleModel (jobName, org.quartz.Scheduler.DEFAULT_GROUP, request, times);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#scheduleModel(java.lang.String, java.lang.String, de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.scheduler.ScheduleTimes)
	 */
	public void scheduleModel (String jobName, String groupName, ModelRequest request, ScheduleTimes times)
	{
		try
		{
			JobDetail jobDetail = new JobDetail (jobName, groupName, QuartzModelJob.class);
			JobDataMap jobData = new JobDataMap ();

			jobData.put ("request", request);
			jobDetail.setJobDataMap (jobData);

			Trigger trigger = createTrigger (jobName, groupName, times);

			synchronized (scheduler)
			{
				scheduler.scheduleJob (jobDetail, trigger);
			}
		}
		catch (Exception x)
		{
			System.err.println ("[QuartzScheduler] " + x.toString ());
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#scheduleRunable(java.lang.String, java.lang.Runnable, de.iritgo.aktera.scheduler.ScheduleTimes)
	 */
	public void scheduleRunnable (String jobName, Runnable runnable, ScheduleTimes times)
	{
		scheduleRunnable (jobName, org.quartz.Scheduler.DEFAULT_GROUP, runnable, times);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#scheduleRunnable(java.lang.String, java.lang.String, java.lang.Runnable, de.iritgo.aktera.scheduler.ScheduleTimes)
	 */
	public void scheduleRunnable (String jobName, String groupName, Runnable runnable, ScheduleTimes times)
	{
		try
		{
			JobDetail jobDetail = new JobDetail (jobName, groupName, QuartzRunnableJob.class);
			JobDataMap jobData = new JobDataMap ();

			jobData.put ("runnable", runnable);
			jobDetail.setJobDataMap (jobData);

			Trigger trigger = createTrigger (jobName, groupName, times);

			synchronized (scheduler)
			{
				scheduler.scheduleJob (jobDetail, trigger);
			}
		}
		catch (Exception x)
		{
			System.err.println ("[QuartzScheduler] " + x.toString ());
		}
	}

	/**
	 * Create a trigger for the specifed times and job parameters.
	 *
	 * @param jobName The job name
	 * @param groupName The group name
	 * @param times The schedule times
	 * @return A Quartz trigger
	 * @throws Exception
	 */
	private Trigger createTrigger (String jobName, String groupName, ScheduleTimes times) throws Exception
	{
		if (times instanceof ScheduleOn)
		{
			ScheduleOn on = (ScheduleOn) times;

			return new CronTrigger (jobName, groupName, jobName, groupName, on.createCronExpression ());
		}
		else if (times instanceof ScheduleRepeated)
		{
			ScheduleRepeated repeated = (ScheduleRepeated) times;

			return new SimpleTrigger (jobName, groupName, jobName, groupName, repeated.getFrom (),
							repeated.getUntil (), repeated.isIndefinitely () ? SimpleTrigger.REPEAT_INDEFINITELY
											: repeated.getCount (), repeated.getInterval ());
		}

		throw new Exception ("Unknown ScheduleTimes instance");
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#removeAllJobsInGroup(java.lang.String)
	 */
	public void removeAllJobsInGroup (String groupName)
	{
		try
		{
			for (String jobName : scheduler.getJobNames (groupName))
			{
				scheduler.deleteJob (jobName, groupName);
			}
		}
		catch (SchedulerException x)
		{
			System.err.println ("[QuartzScheduler] " + x.toString ());
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#getGroupNames()
	 */
	public String[] getGroupNames ()
	{
		try
		{
			return scheduler.getJobGroupNames ();
		}
		catch (SchedulerException x)
		{
			return new String[0];
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.Scheduler#getJobNames(java.lang.String)
	 */
	public String[] getJobNames (String groupName)
	{
		try
		{
			return scheduler.getJobNames (groupName);
		}
		catch (SchedulerException x)
		{
			return new String[0];
		}
	}
}
