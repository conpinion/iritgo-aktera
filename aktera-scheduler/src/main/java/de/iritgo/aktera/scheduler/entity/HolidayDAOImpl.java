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


import de.iritgo.simplelife.string.StringTools;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * DAO implementation for schedule domain objects.
 */
@Transactional(readOnly = true)
public class HolidayDAOImpl extends HibernateDaoSupport implements HolidayDAO
{
	/**
	 * @see de.iritgo.aktera.scheduler.entity.HolidayDAO#dateIsHoliday(java.util.Date, java.lang.String, java.lang.String)
	 */
	public boolean dateIsHoliday (Date date, String country, String province)
	{
		Calendar cal = GregorianCalendar.getInstance ();

		cal.setTime (date);

		String query = "select count(h) from Holiday h" + " where h.country = ?"
						+ " and ((h.province is null or h.province = '')"
						+ (province != null ? " or h.province = ?)" : " and '' = ?)") + " and day = ?"
						+ " and month = ?" + " and (year is null or year = ?)";

		long count = (Long) getHibernateTemplate ().find (
						query,
						new Object[]
						{
										country, StringTools.trim (province), cal.get (Calendar.DAY_OF_MONTH),
										cal.get (Calendar.MONTH) + 1, cal.get (Calendar.YEAR)
						}).get (0);

		return count != 0;
	}
}
