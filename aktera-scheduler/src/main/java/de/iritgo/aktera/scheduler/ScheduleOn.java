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


import de.iritgo.simplelife.string.StringTools;


/**
 * Use this class to define at which times a job should be executed.
 */
public class ScheduleOn extends ScheduleTimes
{
	/** Seconds. */
	private StringBuilder seconds = new StringBuilder();

	/** Minutes. */
	private StringBuilder minutes = new StringBuilder();

	/** Hours. */
	private StringBuilder hours = new StringBuilder();

	/** Days of month. */
	private StringBuilder daysOfMonth = new StringBuilder();

	/** Months. */
	private StringBuilder months = new StringBuilder();

	/** Days of wheek. */
	private StringBuilder daysOfWeek = new StringBuilder();

	/** Years. */
	private StringBuilder years = new StringBuilder();

	/**
	 * Set the seconds on which to execute the job.
	 * Any previous second settings will be overwritten.
	 *
	 * @param seconds Cron like seconds: 0-59 and special chars , - * /
	 * @return This
	 */
	public ScheduleOn seconds(String seconds)
	{
		this.seconds = new StringBuilder(seconds);

		return this;
	}

	/**
	 * Add a second on which to execute the job.
	 * You can repeatedly call this method to specify multiple seconds.
	 *
	 * @param second The second (0-59)
	 * @return This
	 */
	public ScheduleOn second(int second)
	{
		if (0 <= second && second <= 59)
		{
			StringTools.appendWithDelimiter(seconds, String.valueOf(second), ",");
		}

		return this;
	}

	/**
	 * Set the minutes on which to execute the job.
	 * Any previous minute settings will be overwritten.
	 *
	 * @param minutes Cron like minutes: 0-59 and special chars , - * /
	 * @return This
	 */
	public ScheduleOn minutes(String minutes)
	{
		setMinutes(minutes);

		return this;
	}

	/**
	 * Add a minute on which to execute the job.
	 * You can repeatedly call this method to specify multiple minutes.
	 *
	 * @param minute The minute (0-59)
	 * @return This
	 */
	public ScheduleOn minute(int minute)
	{
		if (0 <= minute && minute <= 59)
		{
			StringTools.appendWithDelimiter(minutes, String.valueOf(minute), ",");
		}

		return this;
	}

	/**
	 * Set the hours on which to execute the job.
	 * Any previous hour settings will be overwritten.
	 *
	 * @param hours Cron like hours: 0-23 and special chars , - * /
	 * @return This
	 */
	public ScheduleOn hours(String hours)
	{
		setHours(hours);

		return this;
	}

	/**
	 * Add an hour on which to execute the job.
	 * You can repeatedly call this method to specify multiple hours.
	 *
	 * @param hour The hour (0-23)
	 * @return This
	 */
	public ScheduleOn hour(int hour)
	{
		if (0 <= hour && hour <= 23)
		{
			StringTools.appendWithDelimiter(hours, String.valueOf(hour), ",");
		}

		return this;
	}

	/**
	 * Set the days of month on which to execute the job.
	 * Any previous day settings will be overwritten.
	 *
	 * @param daysOfMonth Cron like days: 1-31 and special chars , - * ? / L W
	 * @return This
	 */
	public ScheduleOn daysOfMonth(String daysOfMonth)
	{
		setDaysOfMonth(daysOfMonth);

		return this;
	}

	/**
	 * Add a day of month on which to execute the job.
	 * You can repeatedly call this method to specify multiple days.
	 *
	 * @param dayOfMonth The day of month (1-31)
	 * @return This
	 */
	public ScheduleOn dayOfMonth(int dayOfMonth)
	{
		if (1 <= dayOfMonth && dayOfMonth <= 31)
		{
			StringTools.appendWithDelimiter(daysOfMonth, String.valueOf(dayOfMonth), ",");
			daysOfWeek = new StringBuilder("?");
		}

		return this;
	}

	/**
	 * Set the months on which to execute the job.
	 * Any previous month settings will be overwritten.
	 *
	 * @param months Cron like months: 1-12 or JAN-DEC
	 * @return This
	 */
	public ScheduleOn months(String months)
	{
		setMonths(months);

		return this;
	}

	/**
	 * Add a month on which to execute the job.
	 * You can repeatedly call this method to specify multiple months.
	 *
	 * @param month The month (1-12)
	 * @return This
	 */
	public ScheduleOn month(int month)
	{
		if (1 <= month && month <= 12)
		{
			StringTools.appendWithDelimiter(months, String.valueOf(month), ",");
		}

		return this;
	}

	/**
	 * Set the days of week on which to execute the job.
	 * Any previous day settings will be overwritten.
	 *
	 * @param daysOfWeek Cron like days: 1-7 and special chars , - * ? / L #
	 * @return This
	 */
	public ScheduleOn daysOfWeek(String daysOfWeek)
	{
		setDaysOfWeek(daysOfWeek);

		return this;
	}

	/**
	 * Add a day of week on which to execute the job.
	 * You can repeatedly call this method to specify multiple days.
	 *
	 * @param dayOfWeek The day of week (1-7)
	 * @return This
	 */
	public ScheduleOn dayOfWeek(int dayOfWeek)
	{
		if (1 <= dayOfWeek && dayOfWeek <= 7)
		{
			StringTools.appendWithDelimiter(daysOfWeek, String.valueOf(dayOfWeek), ",");
			daysOfMonth = new StringBuilder("?");
		}

		return this;
	}

	/**
	 * Set the years on which to execute the job.
	 * Any previous year settings will be overwritten.
	 *
	 * @param months Cron like years: null or empty or 1970-2099 and special chars , - * /
	 * @return This
	 */
	public ScheduleOn years(String years)
	{
		setYears(years);

		return this;
	}

	/**
	 * Add a year on which to execute the job.
	 * You can repeatedly call this method to specify multiple years.
	 *
	 * @param year The year (1970-2099)
	 * @return This
	 */
	public ScheduleOn year(int year)
	{
		if (1970 <= year && year <= 2099)
		{
			StringTools.appendWithDelimiter(years, String.valueOf(year), ",");
		}

		return this;
	}

	/**
	 * Create the cron like expression from the specifed schedule times.
	 *
	 * @return The cron like schedule expression.
	 */
	public String createCronExpression()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(! StringTools.isTrimEmpty(seconds) ? seconds : "*");
		sb.append(" ");
		sb.append(! StringTools.isTrimEmpty(minutes) ? minutes : "*");
		sb.append(" ");
		sb.append(! StringTools.isTrimEmpty(hours) ? hours : "*");
		sb.append(" ");

		if (! StringTools.isTrimEmpty(daysOfMonth))
		{
			sb.append(daysOfMonth);
		}
		else
		{
			sb.append(StringTools.isTrimEmpty(daysOfWeek) ? "*" : "?");
		}

		sb.append(" ");
		sb.append(! StringTools.isTrimEmpty(months) ? months : "*");
		sb.append(" ");

		if (! StringTools.isTrimEmpty(daysOfWeek) && StringTools.isTrimEmpty(daysOfMonth))
		{
			sb.append(daysOfWeek);
		}
		else
		{
			sb.append("?");
		}

		if (! StringTools.isTrimEmpty(years))
		{
			sb.append(" ");
			sb.append(years);
		}

		return sb.toString();
	}

	/**
	 * @see #seconds(String)
	 */
	public void setSeconds(String seconds)
	{
		this.seconds = new StringBuilder(StringTools.trim(seconds));
	}

	/**
	 * @see #minutes(String)
	 */
	public void setMinutes(String minutes)
	{
		this.minutes = new StringBuilder(StringTools.trim(minutes));
	}

	/**
	 * @see #hours(String)
	 */
	public void setHours(String hours)
	{
		this.hours = new StringBuilder(StringTools.trim(hours));
	}

	/**
	 * @see #daysOfMonth(String)
	 */
	public void setDaysOfMonth(String daysOfMonth)
	{
		this.daysOfMonth = new StringBuilder(StringTools.trim(daysOfMonth));
	}

	/**
	 * @see #months(String)
	 */
	public void setMonths(String months)
	{
		this.months = new StringBuilder(StringTools.trim(months));
	}

	/**
	 * @see #daysOfWeek(String)
	 */
	public void setDaysOfWeek(String daysOfWeek)
	{
		this.daysOfWeek = new StringBuilder(StringTools.trim(daysOfWeek));
	}

	/**
	 * @see #years(String)
	 */
	public void setYears(String years)
	{
		this.years = new StringBuilder(StringTools.trim(years));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "ScheduleOn@" + hashCode() + "[" + createCronExpression() + "]";
	}
}
