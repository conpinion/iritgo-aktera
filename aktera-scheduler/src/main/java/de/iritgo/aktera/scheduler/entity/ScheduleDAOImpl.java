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


import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;


/**
 * DAO implementation for schedule domain objects.
 */
@Transactional(readOnly = true)
public class ScheduleDAOImpl extends HibernateDaoSupport implements ScheduleDAO
{
	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#findScheduleById(java.lang.Integer)
	 */
	public Schedule findScheduleById (Integer id)
	{
		return (Schedule) getHibernateTemplate ().get (Schedule.class, id);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#findAllSchedules()
	 */
	public Collection<Schedule> findAllSchedules ()
	{
		return getHibernateTemplate ().find ("from Schedule order by position");
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#findScheduleActionByType(java.lang.String)
	 */
	public Collection<ScheduleAction> findScheduleActionByType (String type)
	{
		return getHibernateTemplate ().find ("from ScheduleAction where type = ?", type);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#update(de.iritgo.aktera.scheduler.entity.ScheduleAction)
	 */
	@Transactional(readOnly = false)
	public void update (ScheduleAction action)
	{
		getHibernateTemplate ().update (action);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleUp(de.iritgo.aktera.scheduler.entity.Schedule)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleUp (Schedule schedule)
	{
		synchronized (Schedule.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();

			if (schedule.getPosition () > 1)
			{
				List<Schedule> prevSchedules = htl.find ("from Schedule where position = ?",
								schedule.getPosition () - 1);

				if (prevSchedules.size () > 0)
				{
					prevSchedules.get (0).setPosition (schedule.getPosition ());
					htl.update (prevSchedules.get (0));
				}

				schedule.setPosition (schedule.getPosition () - 1);
				htl.update (schedule);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleDown(de.iritgo.aktera.scheduler.entity.Schedule)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleDown (Schedule schedule)
	{
		synchronized (Schedule.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();
			Integer maxPosition = (Integer) htl.find ("select max(position) from Schedule").get (0);

			if (schedule.getPosition () < maxPosition)
			{
				List<Schedule> nextSchedules = htl.find ("from Schedule where position = ?",
								schedule.getPosition () + 1);

				if (nextSchedules.size () > 0)
				{
					nextSchedules.get (0).setPosition (schedule.getPosition ());
					htl.update (nextSchedules.get (0));
				}

				schedule.setPosition (schedule.getPosition () + 1);
				htl.update (schedule);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleToFront(de.iritgo.aktera.scheduler.entity.Schedule)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleToFront (Schedule schedule)
	{
		synchronized (Schedule.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();
			List<Schedule> prevSchedules = htl.find ("from Schedule where position < ?", schedule.getPosition ());

			for (Schedule prevSchedule : prevSchedules)
			{
				prevSchedule.setPosition (prevSchedule.getPosition () + 1);
				htl.update (prevSchedule);
			}

			if (schedule.getPosition () != 1)
			{
				schedule.setPosition (1);
				htl.update (schedule);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleToEnd(de.iritgo.aktera.scheduler.entity.Schedule)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleToEnd (Schedule schedule)
	{
		synchronized (Schedule.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();
			Integer maxPosition = (Integer) htl.find ("select max(position) from Schedule").get (0);
			List<Schedule> nextSchedules = htl.find ("from Schedule where position > ?", schedule.getPosition ());

			for (Schedule nextSchedule : nextSchedules)
			{
				nextSchedule.setPosition (nextSchedule.getPosition () - 1);
				htl.update (nextSchedule);
			}

			if (schedule.getPosition () != maxPosition)
			{
				schedule.setPosition (maxPosition);
				htl.update (schedule);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#maxSchedulePosition()
	 */
	public int maxSchedulePosition ()
	{
		Integer maxPosition = (Integer) getHibernateTemplate ().find ("select max(position) from Schedule").get (0);

		return maxPosition != null ? maxPosition.intValue () : 0;
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#findScheduleActionById(java.lang.Integer)
	 */
	public ScheduleAction findScheduleActionById (Integer id)
	{
		return (ScheduleAction) getHibernateTemplate ().get (ScheduleAction.class, id);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#findScheduleActionsByScheduleId(java.lang.Integer)
	 */
	public List<ScheduleAction> findScheduleActionsByScheduleId (Integer scheduleId)
	{
		return getHibernateTemplate ().find ("from ScheduleAction where scheduleId = ? order by position asc",
						scheduleId);
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleActionUp(de.iritgo.aktera.scheduler.entity.ScheduleAction)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleActionUp (ScheduleAction action)
	{
		synchronized (ScheduleAction.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();

			if (action.getPosition () > 1)
			{
				List<ScheduleAction> prevActions = htl.find (
								"from ScheduleAction where scheduleId = ? and position = ?", new Object[]
								{
												action.getScheduleId (), action.getPosition () - 1
								});

				if (prevActions.size () > 0)
				{
					prevActions.get (0).setPosition (action.getPosition ());
					htl.update (prevActions.get (0));
				}

				action.setPosition (action.getPosition () - 1);
				htl.update (action);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleActionDown(de.iritgo.aktera.scheduler.entity.ScheduleAction)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleActionDown (ScheduleAction action)
	{
		synchronized (ScheduleAction.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();
			Integer maxPosition = (Integer) htl.find ("select max(position) from ScheduleAction where scheduleId = ?",
							action.getScheduleId ()).get (0);

			if (action.getPosition () < maxPosition)
			{
				List<ScheduleAction> nextActions = htl.find (
								"from ScheduleAction where scheduleId = ? and position = ?", new Object[]
								{
												action.getScheduleId (), action.getPosition () + 1
								});

				if (nextActions.size () > 0)
				{
					nextActions.get (0).setPosition (action.getPosition ());
					htl.update (nextActions.get (0));
				}

				action.setPosition (action.getPosition () + 1);
				htl.update (action);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleActionToFront(de.iritgo.aktera.scheduler.entity.ScheduleAction)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleActionToFront (ScheduleAction action)
	{
		synchronized (ScheduleAction.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();
			List<ScheduleAction> prevActions = htl.find ("from ScheduleAction where scheduleId = ? and position < ?",
							new Object[]
							{
											action.getScheduleId (), action.getPosition ()
							});

			for (ScheduleAction prevAction : prevActions)
			{
				prevAction.setPosition (prevAction.getPosition () + 1);
				htl.update (prevAction);
			}

			if (action.getPosition () != 1)
			{
				action.setPosition (1);
				htl.update (action);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#moveScheduleActionToEnd(de.iritgo.aktera.scheduler.entity.ScheduleAction)
	 */
	@Transactional(readOnly = false)
	public boolean moveScheduleActionToEnd (ScheduleAction action)
	{
		synchronized (ScheduleAction.class)
		{
			HibernateTemplate htl = getHibernateTemplate ();
			Integer maxPosition = (Integer) htl.find ("select max(position) from ScheduleAction where scheduleId = ?",
							action.getScheduleId ()).get (0);
			List<ScheduleAction> nextActions = htl.find ("from ScheduleAction where scheduleId = ? and position > ?",
							new Object[]
							{
											action.getScheduleId (), action.getPosition ()
							});

			for (ScheduleAction nextAction : nextActions)
			{
				nextAction.setPosition (nextAction.getPosition () - 1);
				htl.update (nextAction);
			}

			if (action.getPosition () != maxPosition)
			{
				action.setPosition (maxPosition);
				htl.update (action);

				return true;
			}

			return false;
		}
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#maxScheduleActionPosition(java.lang.Integer)
	 */
	public int maxScheduleActionPosition (Integer scheduleId)
	{
		Integer maxPosition = (Integer) getHibernateTemplate ().find (
						"select max(position) from ScheduleAction where scheduleId = ?", scheduleId).get (0);

		return maxPosition != null ? maxPosition.intValue () : 0;
	}

	/**
	 * @see de.iritgo.aktera.scheduler.entity.ScheduleDAO#deleteAllActionsOfSchedule(de.iritgo.aktera.scheduler.entity.Schedule)
	 */
	@Transactional(readOnly = false)
	public void deleteAllActionsOfSchedule (Schedule schedule)
	{
		getHibernateTemplate ().bulkUpdate ("delete from ScheduleAction where scheduleId = ?", schedule.getId ());
	}
}
