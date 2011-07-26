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


import de.iritgo.aktera.authentication.DefaultUserEnvironment;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.model.ModelRequest;
import org.apache.avalon.framework.context.DefaultContext;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.LinkedList;
import java.util.List;


/**
 * Quartz job that calls a Keel model.
 */
public class QuartzModelJob implements Job
{
	/**
	 * A user environment that contains the root group.
	 * Used to run the scheduled models with root rights.
	 */
	@SuppressWarnings("serial")
	class RootUserEnvironment extends DefaultUserEnvironment
	{
		private List<String> groups;

		public RootUserEnvironment ()
		{
			groups = new LinkedList<String> ();
			groups.add (new String ("root"));
		}

		public List<String> getGroups ()
		{
			return groups;
		}
	}

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@SuppressWarnings("unchecked")
	public void execute (JobExecutionContext jobContext) throws JobExecutionException
	{
		JobDetail jobDetail = jobContext.getJobDetail ();

		try
		{
			ModelRequest request = (ModelRequest) jobDetail.getJobDataMap ().get ("request");
			DefaultContext requestContext = ((DefaultContext) request.getContext ());

			requestContext.put (UserEnvironment.CONTEXT_KEY, new RootUserEnvironment ());
			request.execute ();
			KeelContainer.defaultContainer ().release (request);
		}
		catch (Exception x)
		{
			System.err.println ("[QuartzModelJob] Error executing job '" + jobDetail.getName () + "': " + x);
		}
	}
}
