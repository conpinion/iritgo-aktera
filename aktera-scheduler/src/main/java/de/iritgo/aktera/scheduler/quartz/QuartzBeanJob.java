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


import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.scheduler.Scheduleable;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Map;


/**
 * Quartz job that calls a spring bean.
 */
public class QuartzBeanJob implements Job
{
	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext jobContext) throws JobExecutionException
	{
		JobDetail jobDetail = jobContext.getJobDetail();

		String beanName = (String) jobDetail.getJobDataMap().get("bean");
		Map parameters = (Map) jobDetail.getJobDataMap().get("parameters");

		Scheduleable scheduleable = (Scheduleable) KeelContainer.defaultContainer().getSpringBean(beanName);

		scheduleable.schedule(parameters);
	}
}
