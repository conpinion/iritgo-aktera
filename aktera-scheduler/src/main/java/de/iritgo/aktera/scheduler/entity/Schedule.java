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

package de.iritgo.aktera.scheduler.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * Domain object that defines a schedule.
 *
 * @persist.persistent
 *   id="Schedule"
 *   name="Schedule"
 *   table="Schedule"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class Schedule implements Serializable
{
	/**
	 * How to account for holidays:
	 *
	 * ALL_DAYS - The schedule is executed on holidays and on normal days
	 * ONLY_HOLIDAYS - The schedule is executed only on holidays
	 * EXCLUDE_HOLIDAYS - The schedule is executed on all days except on holidays
	 */
	public enum HolidaysAllowance
	{
		ALL_DAYS, ONLY_HOLIDAYS, EXCLUDE_HOLIDAYS;
	}

	/** */
	private static final long serialVersionUID = 1L;

	/** The primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Used for positional ordering of schedules */
	@Column(nullable = false)
	private Integer position;

	/** If true, the schedule is disabled */
	private Boolean disabled;

	/** Schedule name */
	@Column(nullable = false, length = 80)
	private String name;

	/** A user definable description */
	private String description;

	/** If true, the schedule runs on all seconds */
	private Boolean allSeconds;

	/** Comma separated list of seconds */
	@Column(length = 80)
	private String seconds;

	/** If true, the schedule runs on all minutes */
	private Boolean allMinutes;

	/** Comma separated list of minutes */
	@Column(length = 80)
	private String minutes;

	/** If true, the schedule runs on all hours */
	private Boolean allHours;

	/** Comma separated list of hours */
	@Column(length = 80)
	private String hours;

	/** If true, the schedule runs on all days */
	private Boolean allDays;

	/** Comma separated list of days */
	@Column(length = 80)
	private String days;

	/** If true, the schedule runs on all months */
	private Boolean allMonths;

	/** Comma separated list of months */
	@Column(length = 80)
	private String months;

	/** If true, the schedule runs on all days of week */
	private Boolean allDaysOfWeek;

	/** Comma separated list of days of week */
	@Column(length = 80)
	private String daysOfWeek;

	/** How to account for holidays */
	private Integer holidaysAllowance;

	/** The country which holidays should be accounted for */
	@Column(length = 8)
	private String holidaysCountry;

	/** The province which holidays should be accounted for */
	@Column(length = 8)
	private String holidaysProvince;

	/**
	 * Get the primary key.
	 *
	 * @persist.field
	 *   name="id"
	 *   db-name="id"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   auto-increment="identity"
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * Set the primary key.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}

	/**
	 * Get the position.
	 *
	 * @persist.field
	 *   name="position"
	 *   db-name="position"
	 *   type="integer"
	 *   null-allowed="false"
	 *   default-value="1"
	 */
	public Integer getPosition()
	{
		return position;
	}

	/**
	 * Set the position.
	 */
	public void setPosition(Integer position)
	{
		this.position = position;
	}

	/**
	 * Get the disabled flag.
	 *
	 * @persist.field
	 *   name="disabled"
	 *   db-name="disabled"
	 *   type="boolean"
	 */
	public Boolean getDisabled()
	{
		return disabled;
	}

	/**
	 * Set the disabled flag.
	 */
	public void setDisabled(Boolean disabled)
	{
		this.disabled = disabled;
	}

	/**
	 * Get the schedule name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="80"
	 *   null-allowed="false"
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the schedule name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the description.
	 *
	 * @persist.field
	 *   name="description"
	 *   db-name="description"
	 *   type="text"
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Set the description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Check if the schedule should run on all seconds.
	 *
	 * @persist.field
	 *   name="allSeconds"
	 *   db-name="allSeconds"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getAllSeconds()
	{
		return allSeconds;
	}

	/**
	 * Define whether the schedule should run on all seconds or not.
	 */
	public void setAllSeconds(Boolean allSeconds)
	{
		this.allSeconds = allSeconds;
	}

	/**
	 * Get the schedules seconds.
	 *
	 * @persist.field
	 *   name="seconds"
	 *   db-name="seconds"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getSeconds()
	{
		return seconds;
	}

	/**
	 * Set the scheduled seconds.
	 */
	public void setSeconds(String seconds)
	{
		this.seconds = seconds;
	}

	/**
	 * Check if the schedule should run on all minutes.
	 *
	 * @persist.field
	 *   name="allMinutes"
	 *   db-name="allMinutes"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getAllMinutes()
	{
		return allMinutes;
	}

	/**
	 * Define whether the schedule should run on all minutes or not.
	 */
	public void setAllMinutes(Boolean allMinutes)
	{
		this.allMinutes = allMinutes;
	}

	/**
	 * Get the schedules minutes.
	 *
	 * @persist.field
	 *   name="minutes"
	 *   db-name="minutes"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getMinutes()
	{
		return minutes;
	}

	/**
	 * Set the scheduled minutes.
	 */
	public void setMinutes(String minutes)
	{
		this.minutes = minutes;
	}

	/**
	 * Check if the schedule should run on all hours.
	 *
	 * @persist.field
	 *   name="allHours"
	 *   db-name="allHours"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getAllHours()
	{
		return allHours;
	}

	/**
	 * Define whether the schedule should run on all hours or not.
	 */
	public void setAllHours(Boolean allHours)
	{
		this.allHours = allHours;
	}

	/**
	 * Get the schedules hours.
	 *
	 * @persist.field
	 *   name="hours"
	 *   db-name="hours"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getHours()
	{
		return hours;
	}

	/**
	 * Set the scheduled hours.
	 */
	public void setHours(String hours)
	{
		this.hours = hours;
	}

	/**
	 * Check if the schedule should run on all days.
	 *
	 * @persist.field
	 *   name="allDays"
	 *   db-name="allDays"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getAllDays()
	{
		return allDays;
	}

	/**
	 * Define whether the schedule should run on all days or not.
	 */
	public void setAllDays(Boolean allDays)
	{
		this.allDays = allDays;
	}

	/**
	 * Get the schedules days.
	 *
	 * @persist.field
	 *   name="days"
	 *   db-name="days"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getDays()
	{
		return days;
	}

	/**
	 * Set the scheduled days.
	 */
	public void setDays(String days)
	{
		this.days = days;
	}

	/**
	 * Check if the schedule should run on all months.
	 *
	 * @persist.field
	 *   name="allMonths"
	 *   db-name="allMonths"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getAllMonths()
	{
		return allMonths;
	}

	/**
	 * Define whether the schedule should run on all months or not.
	 */
	public void setAllMonths(Boolean allMonths)
	{
		this.allMonths = allMonths;
	}

	/**
	 * Get the schedules months.
	 *
	 * @persist.field
	 *   name="months"
	 *   db-name="months"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getMonths()
	{
		return months;
	}

	/**
	 * Set the scheduled months.
	 */
	public void setMonths(String months)
	{
		this.months = months;
	}

	/**
	 * Check if the schedule should run on all days of week.
	 *
	 * @persist.field
	 *   name="allDaysOfWeek"
	 *   db-name="allDaysOfWeek"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getAllDaysOfWeek()
	{
		return allDaysOfWeek;
	}

	/**
	 * Define whether the schedule should run on all days of week or not.
	 */
	public void setAllDaysOfWeek(Boolean allDaysOfWeek)
	{
		this.allDaysOfWeek = allDaysOfWeek;
	}

	/**
	 * Get the schedules days of week.
	 *
	 * @persist.field
	 *   name="daysOfWeek"
	 *   db-name="daysOfWeek"
	 *   type="varchar"
	 *   length="80"
	 */
	public String getDaysOfWeek()
	{
		return daysOfWeek;
	}

	/**
	 * Set the scheduled days of week.
	 */
	public void setDaysOfWeek(String daysOfWeek)
	{
		this.daysOfWeek = daysOfWeek;
	}

	/**
	 * Get the holiday allowance.
	 *
	 * @persist.field
	 *   name="holidaysAllowance"
	 *   db-name="holidaysAllowance"
	 *   type="integer"
	 *   default-value="0"
	 * @persist.valid-value value="0" descrip="$holidaysAllowanceAllDays"
	 * @persist.valid-value value="1" descrip="$holidaysAllowanceOnlyOnHolidays"
	 * @persist.valid-value value="2" descrip="$holidaysAllowanceExceptOnHolidays"
	 */
	public Integer getHolidaysAllowance()
	{
		return holidaysAllowance;
	}

	/**
	 * Set the holidays allowance.
	 */
	public void setHolidaysAllowance(Integer holidaysAllowance)
	{
		this.holidaysAllowance = holidaysAllowance;
	}

	/**
	 * Get the scheduled holidays country.
	 *
	 * @persist.field
	 *   name="holidaysCountry"
	 *   db-name="holidaysCountry"
	 *   type="varchar"
	 *   length="8"
	 */
	public String getHolidaysCountry()
	{
		return holidaysCountry;
	}

	/**
	 * Set the scheduled holidays country.
	 */
	public void setHolidaysCountry(String holidaysCountry)
	{
		this.holidaysCountry = holidaysCountry;
	}

	/**
	 * Get the scheduled holidays province.
	 *
	 * @persist.field
	 *   name="holidaysProvince"
	 *   db-name="holidaysProvince"
	 *   type="varchar"
	 *   length="8"
	 */
	public String getHolidaysProvince()
	{
		return holidaysProvince;
	}

	/**
	 * Set the scheduled holidays province.
	 */
	public void setHolidaysProvince(String holidaysProvince)
	{
		this.holidaysProvince = holidaysProvince;
	}
}
