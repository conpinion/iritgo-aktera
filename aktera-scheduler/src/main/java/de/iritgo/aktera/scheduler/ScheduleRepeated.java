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


import java.util.Date;


/**
 * Use this class to define at which times a job should be executed.
 */
public class ScheduleRepeated extends ScheduleTimes
{
	/** Repeat indefinitely. */
	public static final int INDEFINITELY = - 1;

	/** Date on which to execute the job for the first time. */
	private Date from = new Date();

	/** Date on which to execute the job for the last time. */
	private Date until;

	/** Number of times the job should be executed repeatedly. */
	private int count = INDEFINITELY;

	/** Number of milliseconds between repeated job executions. */
	private int interval = 1000;

	/**
	 * Set the first schedule date.
	 *
	 * @param from The first schedule date
	 * @return This
	 */
	public ScheduleRepeated from(Date from)
	{
		this.from = from;

		return this;
	}

	/**
	 * Set the last schedule date.
	 *
	 * @param last The last schedule date
	 * @return This
	 */
	public ScheduleRepeated until(Date until)
	{
		this.until = until;

		return this;
	}

	/**
	 * Set the repetition count.
	 *
	 * @param repeat The repetition count
	 * @return This
	 */
	public ScheduleRepeated repeat(int repeat)
	{
		this.count = repeat;

		return this;
	}

	/**
	 * Set the repetition interval in seconds.
	 *
	 * @param interval The repetition interval
	 * @return This
	 */
	public ScheduleRepeated interval(int interval)
	{
		this.interval = 1000 * interval;

		return this;
	}

	/**
	 * Get the first schedule date.
	 *
	 * @return The first schedule date
	 */
	public Date getFrom()
	{
		return from;
	}

	/**
	 * Set the first schedule date.
	 *
	 * @param from The first schedule date
	 * @return This
	 */
	public void setFrom(Date from)
	{
		this.from = from;
	}

	/**
	 * Get the last schedule date.
	 *
	 * @return The last schedule date
	 */
	public Date getUntil()
	{
		return until;
	}

	/**
	 * Set the last schedule date.
	 *
	 * @param last The last schedule date
	 * @return This
	 */
	public void setUntil(Date until)
	{
		this.until = until;
	}

	/**
	 * Get the repeat count.
	 *
	 * @return The repeat count
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Check if the repeat count is indefinitely.
	 *
	 * @return If the repeat count is indefinitely
	 */
	public boolean isIndefinitely()
	{
		return count == INDEFINITELY;
	}

	/**
	 * Set the repetition count.
	 *
	 * @param repeat The repetition count
	 * @return This
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

	/**
	 * Get the repeat interval.
	 *
	 * @return The repeat interval
	 */
	public int getInterval()
	{
		return interval;
	}

	/**
	 * Set the repetition interval in milliseconds.
	 *
	 * @param interval The repetition interval
	 * @return This
	 */
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
}
