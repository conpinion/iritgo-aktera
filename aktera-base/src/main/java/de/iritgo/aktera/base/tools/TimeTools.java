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

package de.iritgo.aktera.base.tools;


/**
 * Useful time methods.
 */
public final class TimeTools
{
	/**
	 * Convert a minute span to milli seconds.
	 *
	 * @param minutes The time span in minutes.
	 * @return The time span in milli seconds.
	 */
	public static long minutesToMillis (int minutes)
	{
		return ((long) minutes) * 60L * 1000L;
	}

	/**
	 * Convert a day span to milli seconds.
	 *
	 * @param days The time span in days.
	 * @return The time span in milli seconds.
	 */
	public static long daysToMillis (int days)
	{
		return ((long) days) * 24L * 60L * 60L * 1000L;
	}

	/**
	 * Convert a month span to milli seconds.
	 *
	 * @param months The time span in days.
	 * @return The time span in milli seconds.
	 */
	public static long monthsToMillis (int months)
	{
		return daysToMillis (months * 31);
	}

	/**
	 * Convert a week span to milli seconds.
	 *
	 * @param weeks The time span in weeks.
	 * @return The time span in milli seconds.
	 */
	public static long weeksToMillis (int weeks)
	{
		return daysToMillis (weeks * 7);
	}
}
