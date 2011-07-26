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


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.ModuleVersion;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.UpdateHandler;
import de.iritgo.aktera.scheduler.entity.Holiday;
import org.apache.avalon.framework.logger.Logger;
import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import java.io.InputStreamReader;
import java.sql.Connection;


/**
 *
 */
public class ModuleUpdateHandler extends UpdateHandler
{
	@Override
	public void updateDatabase (ModelRequest req, Logger logger, Connection connection, PersistentFactory pf,
					ModuleVersion currentVersion, ModuleVersion newVersion) throws Exception
	{
		if (currentVersion.between ("0.0.0", "2.1.2"))
		{
			// Create the Schedule table
			createTable ("schedule", "id int4 NOT NULL", "position int NOT NULL", "disabled boolean",
							"name varchar(80) NOT NULL", "description text", "allseconds boolean",
							"seconds varchar(80)", "allminutes boolean", "minutes varchar(80)", "allhours boolean",
							"hours varchar(80)", "alldays boolean", "days varchar(80)", "allmonths boolean",
							"months varchar(80)", "alldaysofweek boolean", "daysofweek varchar(80)");

			createAutoIncrement ("Schedule", 1);

			// Create the ScheduleAction table
			createTable ("scheduleaction", "id int4 NOT NULL", "position int NOT NULL", "disabled boolean",
							"scheduleId int4 NOT NULL", "type varchar(80) NOT NULL", "stringParam1 varchar(255)",
							"stringParam2 varchar(255)", "stringParam3 varchar(255)", "stringParam4 varchar(255)",
							"stringParam5 varchar(255)", "stringParam6 varchar(255)", "stringParam7 varchar(255)",
							"stringParam8 varchar(255)", "integerParam1 int", "integerParam2 int", "integerParam3 int",
							"integerParam4 int", "integerParam5 int", "integerParam6 int", "integerParam7 int",
							"integerParam8 int", "booleanParam1 boolean", "booleanParam2 boolean",
							"booleanParam3 boolean", "booleanParam4 boolean", "booleanParam5 boolean",
							"booleanParam6 boolean", "booleanParam7 boolean", "booleanParam8 boolean",
							"timeParam1 time", "timeParam2 time", "timeParam3 time", "timeParam4 time",
							"dateParam1 date", "dateParam2 date", "dateParam3 date", "dateParam4 date",
							"textParam1 text", "textParam2 text", "textParam3 text", "textParam4 text");

			createAutoIncrement ("ScheduleAction", 1);

			currentVersion.setVersion ("2.1.2");
		}

		if (currentVersion.between ("2.1.2", "2.1.3"))
		{
			// Completed holiday domain model
			renameColumn ("holiday", "holidayid", "id");
			addColumn ("holiday", "year", "int4");
			addColumn ("holiday", "country", "varchar(8)");
			addColumn ("holiday", "province", "varchar(8)");

			addColumn ("schedule", "holidaysallowance", "int4");
			addColumn ("schedule", "holidayscountry", "varchar(8)");
			addColumn ("schedule", "holidaysprovince", "varchar(8)");

			// Create the standard holidays
			Deserializer holidayReader = CsvIOFactory.createFactory (Holiday.class).createDeserializer ();

			holidayReader.open (new InputStreamReader (this.getClass ().getResourceAsStream ("holidays_de.csv")));

			while (holidayReader.hasNext ())
			{
				Holiday holiday = holidayReader.next ();

				update ("INSERT INTO holiday (id,name,country,province,day,month,year) values (?,?,?,?,?,?,?)",
								new Object[]
								{
												createKeelId ("Holiday"), holiday.getName (), holiday.getCountry (),
												holiday.getProvince (), holiday.getDay (), holiday.getMonth (),
												holiday.getYear ()
								});
			}

			currentVersion.setVersion ("2.1.3");
		}

		if (currentVersion.lessThan ("2.2.1"))
		{
			createPrimaryKeySequenceFromIdTable ("Holiday", "id");
			createPrimaryKeySequenceFromIdTable ("Schedule", "id");
			createPrimaryKeySequenceFromIdTable ("ScheduleAction", "id");

			currentVersion.setVersion ("2.2.1");
		}
	}
}
