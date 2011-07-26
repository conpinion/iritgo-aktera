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

package de.iritgo.aktera.scheduler.module;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.CreateHandler;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.scheduler.entity.Holiday;
import org.apache.avalon.framework.logger.Logger;
import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Database creation.
 */
public class ModuleCreateHandler extends CreateHandler
{
	/**
	 * @see de.iritgo.aktera.persist.CreateHandler#createTables(ModelRequest, de.iritgo.aktera.persist.PersistentFactory, java.sql.Connection, Logger)
	 */
	@Override
	public void createTables (ModelRequest request, PersistentFactory persistentFactory, Connection connection,
					Logger logger) throws ModelException, PersistenceException, SQLException
	{
		createTable ("Holiday", "country varchar(8) not null", "day int4 not null", "description text",
						"id serial primary key", "month int4 not null", "name varchar(80) not null",
						"province varchar(8)", "year int4");

		createTable ("Schedule", "allDays boolean", "allDaysOfWeek boolean", "allHours boolean", "allMinutes boolean",
						"allMonths boolean", "allSeconds boolean", "days varchar(80)", "daysOfWeek varchar(80)",
						"description text", "disabled boolean", "holidaysAllowance int4", "holidaysCountry varchar(8)",
						"holidaysProvince varchar(8)", "hours varchar(80)", "id serial primary key",
						"minutes varchar(80)", "months varchar(80)", "name varchar(80) not null",
						"position int4 not null", "seconds varchar(80)");

		createTable ("ScheduleAction", "booleanParam1 boolean", "booleanParam2 boolean", "booleanParam3 boolean",
						"booleanParam4 boolean", "booleanParam5 boolean", "booleanParam6 boolean",
						"booleanParam7 boolean", "booleanParam8 boolean", "dateParam1 date", "dateParam2 date",
						"dateParam3 date", "dateParam4 date", "disabled boolean", "id serial primary key",
						"integerParam1 int4", "integerParam2 int4", "integerParam3 int4", "integerParam4 int4",
						"integerParam5 int4", "integerParam6 int4", "integerParam7 int4", "integerParam8 int4",
						"position int4 not null", "scheduleId int4 not null", "stringParam1 varchar(255)",
						"stringParam2 varchar(255)", "stringParam3 varchar(255)", "stringParam4 varchar(255)",
						"stringParam5 varchar(255)", "stringParam6 varchar(255)", "stringParam7 varchar(255)",
						"stringParam8 varchar(255)", "textParam1 text", "textParam2 text", "textParam3 text",
						"textParam4 text", "timeParam1 time", "timeParam2 time", "timeParam3 time", "timeParam4 time",
						"type varchar(80) not null");
	}

	/**
	 * @see de.iritgo.aktera.base.services.persist.CreateHandler#createData(de.iritgo.aktera.persist.PersistentFactory, java.sql.Connection, Logger, ModelRequest)
	 */
	@Override
	public void createData (PersistentFactory persistentFactory, Connection connection, Logger logger,
					ModelRequest request) throws ModelException, PersistenceException, SQLException
	{
		// Create the standard holidays
		Deserializer holidayReader = CsvIOFactory.createFactory (Holiday.class).createDeserializer ();

		holidayReader.open (new InputStreamReader (this.getClass ().getResourceAsStream ("holidays_de.csv")));

		while (holidayReader.hasNext ())
		{
			Holiday holiday = holidayReader.next ();

			if (holiday.getYear () != null)
			{
				update ("INSERT INTO holiday (name, country, province, day, month, year) values (?, ?, ?, ?, ?, ?)",
								new Object[]
								{
												holiday.getName (), holiday.getCountry (), holiday.getProvince (),
												holiday.getDay (), holiday.getMonth (), holiday.getYear ()
								});
			}
			else
			{
				update ("INSERT INTO holiday (name, country, province, day, month) values (?, ?, ?, ?, ?)",
								new Object[]
								{
												holiday.getName (), holiday.getCountry (), holiday.getProvince (),
												holiday.getDay (), holiday.getMonth ()
								});
			}
		}
	}
}
