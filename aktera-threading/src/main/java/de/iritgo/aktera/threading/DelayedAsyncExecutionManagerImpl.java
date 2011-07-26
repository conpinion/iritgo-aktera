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

package de.iritgo.aktera.threading;


import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Value;


public class DelayedAsyncExecutionManagerImpl implements DelayedAsyncExecutionManager
{
	@Value("1")
	private int POOL_SIZE;

	private ScheduledExecutorService scheduler;

	public DelayedAsyncExecutionManagerImpl ()
	{
		scheduler = Executors.newScheduledThreadPool (POOL_SIZE);
	}

	public void addDelayedExecution (long delay, Runnable runnable)
	{
		scheduler.schedule (runnable, delay, MILLISECONDS);
	}
}
