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

package de.iritgo.aktera.base.appointment;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.aktera.tools.ModelTools;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * This model creates a single output element named 'calendar' that contains information
 * about the days in a given time period.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.appointment.get-calendar"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.appointment.get-calendar" id="aktera.appointment.get-calendar" logger="aktera"
 * @model.parameter name="showMonth" required="false"
 * @model.parameter name="showYear" required="false"
 * @model.parameter name="dayModel" required="false"
 */
public class GetCalendar extends SecurableStandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		int showMonth = req.getParameterAsInt ("showMonth", - 1);
		int showYear = req.getParameterAsInt ("showYear", - 1);
		String dayModel = req.getParameterAsString ("dayModel", null);

		ModelResponse res = req.createResponse ();

		Output outCalendar = res.createOutput ("calendar");

		res.add (outCalendar);

		Calendar cal = new GregorianCalendar ();

		cal.setFirstDayOfWeek (Calendar.MONDAY);

		int currentDayOfYear = cal.get (Calendar.DAY_OF_YEAR);
		int currentMonth = cal.get (Calendar.MONTH);
		int currentYear = cal.get (Calendar.YEAR);

		outCalendar.setAttribute ("currentDate", cal.getTime ());

		Command cmdCurrentMonth = ModelTools.createPreviousModelCommand (req, res);

		cmdCurrentMonth.setParameter ("showMonth", new Integer (cal.get (Calendar.MONTH)));
		cmdCurrentMonth.setParameter ("showYear", new Integer (cal.get (Calendar.YEAR)));
		outCalendar.setAttribute ("cmdCurrentMonth", cmdCurrentMonth);

		if (showMonth != - 1)
		{
			cal.set (Calendar.MONTH, showMonth);
		}
		else
		{
			showMonth = currentMonth;
		}

		if (showYear != - 1)
		{
			cal.set (Calendar.YEAR, showYear);
		}

		outCalendar.setAttribute ("showDate", cal.getTime ());

		cal.add (Calendar.MONTH, - 1);

		Command cmdPrevMonth = ModelTools.createPreviousModelCommand (req, res);

		cmdPrevMonth.setParameter ("showMonth", new Integer (cal.get (Calendar.MONTH)));
		cmdPrevMonth.setParameter ("showYear", new Integer (cal.get (Calendar.YEAR)));
		outCalendar.setAttribute ("cmdPrevMonth", cmdPrevMonth);

		cal.add (Calendar.MONTH, 2);

		Command cmdNextMonth = ModelTools.createPreviousModelCommand (req, res);

		cmdNextMonth.setParameter ("showMonth", new Integer (cal.get (Calendar.MONTH)));
		cmdNextMonth.setParameter ("showYear", new Integer (cal.get (Calendar.YEAR)));
		outCalendar.setAttribute ("cmdNextMonth", cmdNextMonth);

		cal.add (Calendar.MONTH, - 1);
		cal.add (Calendar.YEAR, - 1);

		Command cmdPrevYear = ModelTools.createPreviousModelCommand (req, res);

		cmdPrevYear.setParameter ("showMonth", new Integer (cal.get (Calendar.MONTH)));
		cmdPrevYear.setParameter ("showYear", new Integer (cal.get (Calendar.YEAR)));
		outCalendar.setAttribute ("cmdPrevYear", cmdPrevYear);

		cal.add (Calendar.YEAR, 2);

		Command cmdNextYear = ModelTools.createPreviousModelCommand (req, res);

		cmdNextYear.setParameter ("showMonth", new Integer (cal.get (Calendar.MONTH)));
		cmdNextYear.setParameter ("showYear", new Integer (cal.get (Calendar.YEAR)));
		outCalendar.setAttribute ("cmdNextYear", cmdNextYear);

		cal.add (Calendar.YEAR, - 1);

		Output outWeeks = res.createOutput ("weeks");

		outCalendar.setAttribute ("weeks", outWeeks);

		cal.set (Calendar.WEEK_OF_MONTH, 1);
		cal.set (Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		for (int i = 0; i < 6; ++i)
		{
			Output outWeek = res.createOutput ("week");

			outWeeks.add (outWeek);
			outWeek.setAttribute ("num", new Integer (cal.get (Calendar.WEEK_OF_YEAR)));

			for (int j = 0; j < 7; ++j)
			{
				Output outDay = res.createOutput ("day");

				outWeek.add (outDay);

				outDay.setAttribute ("num", new Integer (cal.get (Calendar.DAY_OF_MONTH)));

				if (cal.get (Calendar.YEAR) == currentYear && cal.get (Calendar.DAY_OF_YEAR) == currentDayOfYear)
				{
					outDay.setAttribute ("isCurrent", Boolean.TRUE);
				}

				if (cal.get (Calendar.MONTH) == showMonth)
				{
					if (dayModel != null)
					{
						Command cmdDoDay = res.createCommand (dayModel);

						outDay.setAttribute ("cmdDoDay", cmdDoDay);
					}
				}
				else
				{
					outDay.setAttribute ("isSecondary", Boolean.TRUE);
				}

				cal.add (Calendar.DAY_OF_MONTH, 1);
			}
		}

		return res;
	}
}
