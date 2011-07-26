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


import de.iritgo.aktera.model.ModelRequest;
import org.quartz.SchedulerException;
import java.util.Map;


/**
 * Scheduler service interface.
 */
public interface Scheduler
{
	/** Id of the default scheduler service. */
	public final String ID = "de.iritgo.aktera.scheduler.Scheduler";

	/**
	 * Scheduled execution of beans.
	 *
	 * @param jobName The job name
	 * @param beanName The bean to execute
	 * @param parameters Parameters to pass to the beans execute method
	 * @param times When to execute the job
	 */
	@SuppressWarnings("unchecked")
	public void scheduleBean (String jobName, String beanName, Map parameters, ScheduleTimes times);

	/**
	 * Scheduled execution of beans.
	 *
	 * @param jobName The job name
	 * @param groupName The job group
	 * @param beanName The bean to execute
	 * @param parameters Parameters to pass to the beans execute method
	 * @param times When to execute the job
	 */
	@SuppressWarnings("unchecked")
	public void scheduleBean (String jobName, String groupName, String beanName, Map parameters, ScheduleTimes times);

	/**
	 * Scheduled execution of Keel models.
	 *
	 * @param jobName The job name
	 * @param request The model request to execute
	 * @param times When to execute the job
	 */
	public void scheduleModel (String jobName, ModelRequest request, ScheduleTimes times);

	/**
	 * Scheduled execution of Keel models.
	 *
	 * @param jobName The job name
	 * @param groupName The job group
	 * @param request The model request to execute
	 * @param times When to execute the job
	 */
	public void scheduleModel (String jobName, String groupName, ModelRequest request, ScheduleTimes times);

	/**
	 * Scheduled execution of runnables.
	 *
	 * @param jobName The job name
	 * @param runnable The runable to execute
	 * @param times When to execute the job
	 */
	@SuppressWarnings("unchecked")
	public void scheduleRunnable (String jobName, Runnable runnable, ScheduleTimes times);

	/**
	 * Scheduled execution of runnables.
	 *
	 * @param jobName The job name
	 * @param groupName The job group
	 * @param runnable The runable to execute
	 * @param times When to execute the job
	 */
	@SuppressWarnings("unchecked")
	public void scheduleRunnable (String jobName, String groupName, Runnable runnable, ScheduleTimes times);

	/**
	 * Remove all jobs in the specified group.
	 *
	 * @param groupName The group name
	 */
	public void removeAllJobsInGroup (String groupName);

	/**
	 * Get a list of all job group names.
	 */
	public String[] getGroupNames ();

	/**
	 * Get a list of all job names in a given group.
	 *
	 * @param groupName The group name
	 */
	public String[] getJobNames (String groupName);

	/**
	 * Initialize and start the scheduler.
	 * @throws SchedulerException
	 */
	public void initialize () throws SchedulerException;

	/**
	 * Stop the scheduler and free all resources.
	 * @throws SchedulerException
	 */
	public void dispose () throws SchedulerException;
}
