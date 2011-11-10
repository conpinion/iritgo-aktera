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


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;


/**
 * Domain object that defines a schedule.
 *
 * @persist.persistent
 *   id="ScheduleAction"
 *   name="ScheduleAction"
 *   table="ScheduleAction"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class ScheduleAction implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** The primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Used for positional ordering of schedules */
	@Column(nullable = false)
	private Integer position;

	/** If true, the action is disabled */
	private Boolean disabled;

	/** Foreign schedule key */
	@Column(nullable = false)
	private Integer scheduleId;

	/** Action type */
	@Column(nullable = false, length = 80)
	private String type;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam1;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam2;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam3;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam4;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam5;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam6;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam7;

	/** An action string parameter */
	@Column(length = 255)
	private String stringParam8;

	/** An action integer parameter */
	private Integer integerParam1;

	/** An action integer parameter */
	private Integer integerParam2;

	/** An action integer parameter */
	private Integer integerParam3;

	/** An action integer parameter */
	private Integer integerParam4;

	/** An action integer parameter */
	private Integer integerParam5;

	/** An action integer parameter */
	private Integer integerParam6;

	/** An action integer parameter */
	private Integer integerParam7;

	/** An action integer parameter */
	private Integer integerParam8;

	/** An action boolean parameter */
	private Boolean booleanParam1;

	/** An action boolean parameter */
	private Boolean booleanParam2;

	/** An action boolean parameter */
	private Boolean booleanParam3;

	/** An action boolean parameter */
	private Boolean booleanParam4;

	/** An action boolean parameter */
	private Boolean booleanParam5;

	/** An action boolean parameter */
	private Boolean booleanParam6;

	/** An action boolean parameter */
	private Boolean booleanParam7;

	/** An action boolean parameter */
	private Boolean booleanParam8;

	/** An action time parameter */
	private Time timeParam1;

	/** An action time parameter */
	private Time timeParam2;

	/** An action time parameter */
	private Time timeParam3;

	/** An action time parameter */
	private Time timeParam4;

	/** An action date parameter */
	private Date dateParam1;

	/** An action date parameter */
	private Date dateParam2;

	/** An action date parameter */
	private Date dateParam3;

	/** An action date parameter */
	private Date dateParam4;

	/** An action text parameter */
	private String textParam1;

	/** An action text parameter */
	private String textParam2;

	/** An action text parameter */
	private String textParam3;

	/** An action text parameter */
	private String textParam4;

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
	 * Get the schedule id.
	 *
	 * @persist.field
	 *   name="scheduleId"
	 *   db-name="scheduleId"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public Integer getScheduleId()
	{
		return scheduleId;
	}

	/**
	 * Set the schedule id.
	 */
	public void setScheduleId(Integer scheduleId)
	{
		this.scheduleId = scheduleId;
	}

	/**
	 * Get the action type.
	 *
	 * @persist.field
	 *   name="type"
	 *   db-name="type"
	 *   type="varchar"
	 *   length="80"
	 *   null-allowed="false"
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Set the action type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Get the string parameter 1.
	 *
	 * @persist.field
	 *   name="stringParam1"
	 *   db-name="stringParam1"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam1()
	{
		return stringParam1;
	}

	/**
	 * Set the string parameter 1.
	 */
	public void setStringParam1(String stringParam1)
	{
		this.stringParam1 = stringParam1;
	}

	/**
	 * Get the string parameter 2.
	 *
	 * @persist.field
	 *   name="stringParam2"
	 *   db-name="stringParam2"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam2()
	{
		return stringParam2;
	}

	/**
	 * Set the string parameter 2.
	 */
	public void setStringParam2(String stringParam2)
	{
		this.stringParam2 = stringParam2;
	}

	/**
	 * Get the string parameter 3.
	 *
	 * @persist.field
	 *   name="stringParam3"
	 *   db-name="stringParam3"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam3()
	{
		return stringParam3;
	}

	/**
	 * Set the string parameter 3.
	 */
	public void setStringParam3(String stringParam3)
	{
		this.stringParam3 = stringParam3;
	}

	/**
	 * Get the string parameter 4.
	 *
	 * @persist.field
	 *   name="stringParam4"
	 *   db-name="stringParam4"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam4()
	{
		return stringParam4;
	}

	/**
	 * Set the string parameter 4.
	 */
	public void setStringParam4(String stringParam4)
	{
		this.stringParam4 = stringParam4;
	}

	/**
	 * Get the string parameter 5.
	 *
	 * @persist.field
	 *   name="stringParam5"
	 *   db-name="stringParam5"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam5()
	{
		return stringParam5;
	}

	/**
	 * Set the string parameter 5.
	 */
	public void setStringParam5(String stringParam5)
	{
		this.stringParam5 = stringParam5;
	}

	/**
	 * Get the string parameter 6.
	 *
	 * @persist.field
	 *   name="stringParam6"
	 *   db-name="stringParam6"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam6()
	{
		return stringParam6;
	}

	/**
	 * Set the string parameter 6.
	 */
	public void setStringParam6(String stringParam6)
	{
		this.stringParam6 = stringParam6;
	}

	/**
	 * Get the string parameter 7.
	 *
	 * @persist.field
	 *   name="stringParam7"
	 *   db-name="stringParam7"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam7()
	{
		return stringParam7;
	}

	/**
	 * Set the string parameter 7.
	 */
	public void setStringParam7(String stringParam7)
	{
		this.stringParam7 = stringParam7;
	}

	/**
	 * Get the string parameter 8.
	 *
	 * @persist.field
	 *   name="stringParam8"
	 *   db-name="stringParam8"
	 *   type="varchar"
	 *   length="255"
	 */
	public String getStringParam8()
	{
		return stringParam8;
	}

	/**
	 * Set the string parameter 8.
	 */
	public void setStringParam8(String stringParam8)
	{
		this.stringParam8 = stringParam8;
	}

	/**
	 * Get the integer parameter 1.
	 *
	 * @persist.field
	 *   name="integerParam1"
	 *   db-name="integerParam1"
	 *   type="integer"
	 */
	public Integer getIntegerParam1()
	{
		return integerParam1;
	}

	/**
	 * Set the integer parameter 1.
	 */
	public void setIntegerParam1(Integer integerParam1)
	{
		this.integerParam1 = integerParam1;
	}

	/**
	 * Get the integer parameter 2.
	 *
	 * @persist.field
	 *   name="integerParam2"
	 *   db-name="integerParam2"
	 *   type="integer"
	 */
	public Integer getIntegerParam2()
	{
		return integerParam2;
	}

	/**
	 * Set the integer parameter 2.
	 */
	public void setIntegerParam2(Integer integerParam2)
	{
		this.integerParam2 = integerParam2;
	}

	/**
	 * Get the integer parameter 3.
	 *
	 * @persist.field
	 *   name="integerParam3"
	 *   db-name="integerParam3"
	 *   type="integer"
	 */
	public Integer getIntegerParam3()
	{
		return integerParam3;
	}

	/**
	 * Set the integer parameter 3.
	 */
	public void setIntegerParam3(Integer integerParam3)
	{
		this.integerParam3 = integerParam3;
	}

	/**
	 * Get the integer parameter 4.
	 *
	 * @persist.field
	 *   name="integerParam4"
	 *   db-name="integerParam4"
	 *   type="integer"
	 */
	public Integer getIntegerParam4()
	{
		return integerParam4;
	}

	/**
	 * Set the integer parameter 4.
	 */
	public void setIntegerParam4(Integer integerParam4)
	{
		this.integerParam4 = integerParam4;
	}

	/**
	 * Get the integer parameter 5.
	 *
	 * @persist.field
	 *   name="integerParam5"
	 *   db-name="integerParam5"
	 *   type="integer"
	 */
	public Integer getIntegerParam5()
	{
		return integerParam5;
	}

	/**
	 * Set the integer parameter 5.
	 */
	public void setIntegerParam5(Integer integerParam5)
	{
		this.integerParam5 = integerParam5;
	}

	/**
	 * Get the integer parameter 6.
	 *
	 * @persist.field
	 *   name="integerParam6"
	 *   db-name="integerParam6"
	 *   type="integer"
	 */
	public Integer getIntegerParam6()
	{
		return integerParam6;
	}

	/**
	 * Set the integer parameter 6.
	 */
	public void setIntegerParam6(Integer integerParam6)
	{
		this.integerParam6 = integerParam6;
	}

	/**
	 * Get the integer parameter 7.
	 *
	 * @persist.field
	 *   name="integerParam7"
	 *   db-name="integerParam7"
	 *   type="integer"
	 */
	public Integer getIntegerParam7()
	{
		return integerParam7;
	}

	/**
	 * Set the integer parameter 7.
	 */
	public void setIntegerParam7(Integer integerParam7)
	{
		this.integerParam7 = integerParam7;
	}

	/**
	 * Get the integer parameter 8.
	 *
	 * @persist.field
	 *   name="integerParam8"
	 *   db-name="integerParam8"
	 *   type="integer"
	 */
	public Integer getIntegerParam8()
	{
		return integerParam8;
	}

	/**
	 * Set the integer parameter 8.
	 */
	public void setIntegerParam8(Integer integerParam8)
	{
		this.integerParam8 = integerParam8;
	}

	/**
	 * Return the boolean parameter 1.
	 *
	 * @persist.field
	 *   name="booleanParam1"
	 *   db-name="booleanParam1"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam1()
	{
		return booleanParam1;
	}

	/**
	 * Set the boolean parameter 1.
	 */
	public void setBooleanParam1(Boolean booleanParam1)
	{
		this.booleanParam1 = booleanParam1;
	}

	/**
	 * Return the boolean parameter 2.
	 *
	 * @persist.field
	 *   name="booleanParam2"
	 *   db-name="booleanParam2"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam2()
	{
		return booleanParam2;
	}

	/**
	 * Set the boolean parameter 2.
	 */
	public void setBooleanParam2(Boolean booleanParam2)
	{
		this.booleanParam2 = booleanParam2;
	}

	/**
	 * Return the boolean parameter 3.
	 *
	 * @persist.field
	 *   name="booleanParam3"
	 *   db-name="booleanParam3"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam3()
	{
		return booleanParam3;
	}

	/**
	 * Set the boolean parameter 3.
	 */
	public void setBooleanParam3(Boolean booleanParam3)
	{
		this.booleanParam3 = booleanParam3;
	}

	/**
	 * Return the boolean parameter 4.
	 *
	 * @persist.field
	 *   name="booleanParam4"
	 *   db-name="booleanParam4"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam4()
	{
		return booleanParam4;
	}

	/**
	 * Set the boolean parameter 4.
	 */
	public void setBooleanParam4(Boolean booleanParam4)
	{
		this.booleanParam4 = booleanParam4;
	}

	/**
	 * Return the boolean parameter 5.
	 *
	 * @persist.field
	 *   name="booleanParam5"
	 *   db-name="booleanParam5"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam5()
	{
		return booleanParam5;
	}

	/**
	 * Set the boolean parameter 5.
	 */
	public void setBooleanParam5(Boolean booleanParam5)
	{
		this.booleanParam5 = booleanParam5;
	}

	/**
	 * Return the boolean parameter 6.
	 *
	 * @persist.field
	 *   name="booleanParam6"
	 *   db-name="booleanParam6"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam6()
	{
		return booleanParam6;
	}

	/**
	 * Set the boolean parameter 6.
	 */
	public void setBooleanParam6(Boolean booleanParam6)
	{
		this.booleanParam6 = booleanParam6;
	}

	/**
	 * Return the boolean parameter 7.
	 *
	 * @persist.field
	 *   name="booleanParam7"
	 *   db-name="booleanParam7"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam7()
	{
		return booleanParam7;
	}

	/**
	 * Set the boolean parameter 7.
	 */
	public void setBooleanParam7(Boolean booleanParam7)
	{
		this.booleanParam7 = booleanParam7;
	}

	/**
	 * Return the boolean parameter 8.
	 *
	 * @persist.field
	 *   name="booleanParam8"
	 *   db-name="booleanParam8"
	 *   type="boolean"
	 */
	public Boolean getBooleanParam8()
	{
		return booleanParam8;
	}

	/**
	 * Set the boolean parameter 8.
	 */
	public void setBooleanParam8(Boolean booleanParam8)
	{
		this.booleanParam8 = booleanParam8;
	}

	/**
	 * Get the time parameter 1.
	 *
	 * @persist.field
	 *   name="timeParam1"
	 *   db-name="timeParam1"
	 *   type="time"
	 */
	public Time getTimeParam1()
	{
		return timeParam1;
	}

	/**
	 * Set the time parameter 1.
	 */
	public void setTimeParam1(Time timeParam1)
	{
		this.timeParam1 = timeParam1;
	}

	/**
	 * Get the time parameter 2.
	 *
	 * @persist.field
	 *   name="timeParam2"
	 *   db-name="timeParam2"
	 *   type="time"
	 */
	public Time getTimeParam2()
	{
		return timeParam2;
	}

	/**
	 * Set the time parameter 2.
	 */
	public void setTimeParam2(Time timeParam2)
	{
		this.timeParam2 = timeParam2;
	}

	/**
	 * Get the time parameter 3.
	 *
	 * @persist.field
	 *   name="timeParam3"
	 *   db-name="timeParam3"
	 *   type="time"
	 */
	public Time getTimeParam3()
	{
		return timeParam3;
	}

	/**
	 * Set the time parameter 3.
	 */
	public void setTimeParam3(Time timeParam3)
	{
		this.timeParam3 = timeParam3;
	}

	/**
	 * Get the time parameter 4.
	 *
	 * @persist.field
	 *   name="timeParam4"
	 *   db-name="timeParam4"
	 *   type="time"
	 */
	public Time getTimeParam4()
	{
		return timeParam4;
	}

	/**
	 * Set the time parameter 4.
	 */
	public void setTimeParam4(Time timeParam4)
	{
		this.timeParam4 = timeParam4;
	}

	/**
	 * Return the date parameter 1.
	 *
	 * @persist.field
	 *   name="dateParam1"
	 *   db-name="dateParam1"
	 *   type="date"
	 */
	public Date getDateParam1()
	{
		return dateParam1;
	}

	/**
	 * Set the date parameter 1.
	 */
	public void setDateParam1(Date dateParam1)
	{
		this.dateParam1 = dateParam1;
	}

	/**
	 * Return the date parameter 2.
	 *
	 * @persist.field
	 *   name="dateParam2"
	 *   db-name="dateParam2"
	 *   type="date"
	 */
	public Date getDateParam2()
	{
		return dateParam2;
	}

	/**
	 * Set the date parameter 2.
	 */
	public void setDateParam2(Date dateParam2)
	{
		this.dateParam2 = dateParam2;
	}

	/**
	 * Return the date parameter 3.
	 *
	 * @persist.field
	 *   name="dateParam3"
	 *   db-name="dateParam3"
	 *   type="date"
	 */
	public Date getDateParam3()
	{
		return dateParam3;
	}

	/**
	 * Set the date parameter 3.
	 */
	public void setDateParam3(Date dateParam3)
	{
		this.dateParam3 = dateParam3;
	}

	/**
	 * Return the date parameter 4.
	 *
	 * @persist.field
	 *   name="dateParam4"
	 *   db-name="dateParam4"
	 *   type="date"
	 */
	public Date getDateParam4()
	{
		return dateParam4;
	}

	/**
	 * Set the date parameter 4.
	 */
	public void setDateParam4(Date dateParam4)
	{
		this.dateParam4 = dateParam4;
	}

	/**
	 * Get the text parameter 1.
	 *
	 * @persist.field
	 *   name="textParam1"
	 *   db-name="textParam1"
	 *   type="text"
	 */
	public String getTextParam1()
	{
		return textParam1;
	}

	/**
	 * Set the text parameter 1.
	 */
	public void setTextParam1(String textParam1)
	{
		this.textParam1 = textParam1;
	}

	/**
	 * Get the text parameter 2.
	 *
	 * @persist.field
	 *   name="textParam2"
	 *   db-name="textParam2"
	 *   type="text"
	 */
	public String getTextParam2()
	{
		return textParam2;
	}

	/**
	 * Set the text parameter 2.
	 */
	public void setTextParam2(String textParam2)
	{
		this.textParam2 = textParam2;
	}

	/**
	 * Get the text parameter 3.
	 *
	 * @persist.field
	 *   name="textParam3"
	 *   db-name="textParam3"
	 *   type="text"
	 */
	public String getTextParam3()
	{
		return textParam3;
	}

	/**
	 * Set the text parameter 3.
	 */
	public void setTextParam3(String textParam3)
	{
		this.textParam3 = textParam3;
	}

	/**
	 * Get the text parameter 4.
	 *
	 * @persist.field
	 *   name="textParam4"
	 *   db-name="textParam4"
	 *   type="text"
	 */
	public String getTextParam4()
	{
		return textParam4;
	}

	/**
	 * Set the text parameter 4.
	 */
	public void setTextParam4(String textParam4)
	{
		this.textParam4 = textParam4;
	}
}
