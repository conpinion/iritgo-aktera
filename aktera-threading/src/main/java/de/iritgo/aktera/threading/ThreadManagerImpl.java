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


import lombok.Setter;


public class ThreadManagerImpl implements ThreadManager
{
	@Setter
	private DelayedAsyncExecutionManager delayedAsyncExecutionManager;

	/**
	 * @see de.iritgo.aktera.threading.ThreadManager#addDelayedAsyncExecution(int, java.lang.Runnable)
	 */
	public void addDelayedAsyncExecution (long delay, Runnable runnable)
	{
		delayedAsyncExecutionManager.addDelayedExecution (delay, runnable);
	}
}
