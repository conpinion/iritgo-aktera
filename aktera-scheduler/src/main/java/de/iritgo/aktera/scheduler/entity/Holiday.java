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


import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Holiday domain object.
 *
 * @persist.persistent
 *   id="Holiday"
 *   name="Holiday"
 *   table="Holiday"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
@CsvDataType
public class Holiday implements Serializable
{
	/** Serial version */
	private static final long serialVersionUID = 1L;

	/** The primray key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** The holiday name */
	@Column(nullable = false)
	@CsvField(pos = 1)
	private String name;

	/** The country to which the holiday applies */
	@Column(nullable = false)
	@CsvField(pos = 2)
	private String country;

	/** The (optional) province to which the holiday applies */
	@CsvField(pos = 3)
	private String province;

	/** The day of the holiday date */
	@Column(nullable = false)
	@CsvField(pos = 4)
	private Integer day;

	/** The month of the holiday date */
	@Column(nullable = false)
	@CsvField(pos = 5)
	private Integer month;

	/** The (optional) year of the holiday date */
	@CsvField(pos = 6)
	private Integer year;

	/** A description of the holiday */
	@CsvField(pos = 7)
	private String description;

	/**
	 * Return the primary key.
	 *
	 * @persist.field
	 *   name="id"
	 *   db-name="id"
	 *   type="integer"
	 *   primary-key="true"
	 *   auto-increment="identity"
	 *   null-allowed="false"
	 */
	public Integer getId ()
	{
		return id;
	}

	/**
	 * Set the holiday Id.
	 */
	public void setId (Integer id)
	{
		this.id = id;
	}

	/**
	 * Return the holiday name.
	 *
	 * @persist.field
	 *   name="name"
	 *   db-name="name"
	 *   type="varchar"
	 *   length="80"
	 *   null-allowed="false"
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the holiday name.
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Return the country.
	 *
	 * @persist.field
	 *   name="country"
	 *   db-name="country"
	 *   type="varchar"
	 *   length="8"
	 *   null-allowed="false"
	 */
	public String getCountry ()
	{
		return country;
	}

	/**
	 * Set the country.
	 */
	public void setCountry (String country)
	{
		this.country = country;
	}

	/**
	 * Return the province.
	 *
	 * @persist.field
	 *   name="province"
	 *   db-name="province"
	 *   type="varchar"
	 *   length="8"
	 */
	public String getProvince ()
	{
		return province;
	}

	/**
	 * Set the province.
	 */
	public void setProvince (String province)
	{
		this.province = province;
	}

	/**
	 * Return the day.
	 *
	 * @persist.field
	 *   name="day"
	 *   db-name="day"
	 *   type="integer"
	 *   null-allowed="false"
	 */
	public Integer getDay ()
	{
		return day;
	}

	/**
	 * Set the day.
	 */
	public void setDay (Integer day)
	{
		this.day = day;
	}

	/**
	 * Return the month.
	 *
	 * @persist.field
	 *   name="month"
	 *   db-name="month"
	 *   type="integer"
	 *   null-allowed="false"
	 */
	public Integer getMonth ()
	{
		return month;
	}

	/**
	 * Set the month.
	 */
	public void setMonth (Integer month)
	{
		this.month = month;
	}

	/**
	 * Return the year.
	 *
	 * @persist.field
	 *   name="year"
	 *   db-name="year"
	 *   type="integer"
	 */
	public Integer getYear ()
	{
		return year;
	}

	/**
	 * Set the year.
	 */
	public void setYear (Integer year)
	{
		this.year = year;
	}

	/**
	 * Return the description.
	 *
	 * @persist.field
	 *   name="description"
	 *   db-name="description"
	 *   type="text"
	 */
	public String getDescription ()
	{
		return description;
	}

	/**
	 * Set the description.
	 */
	public void setDescription (String description)
	{
		this.description = description;
	}

	/**
	 * Convert a holiday to a string.
	 *
	 * @return A string representation of a holiday.
	 */
	public String toString ()
	{
		return "Holiday@" + hashCode () + "[" + "name=" + name + ",country=" + country + ",province=" + province
						+ ",day=" + day + ",month=" + month + ",year=" + year + "]";
	}

	/**
	 * Retrieve the date of a holiday. In case of continues holidays (which
	 * have a null year) a date for the current year is returned.
	 */
	public Date toDate ()
	{
		Calendar cal = GregorianCalendar.getInstance ();

		cal.set (Calendar.MONTH, month);
		cal.set (Calendar.DAY_OF_MONTH, day);

		if (year != null)
		{
			cal.set (Calendar.YEAR, year);
		}

		return cal.getTime ();
	}
}
