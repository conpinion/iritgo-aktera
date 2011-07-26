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


import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;


/**
 * DAO for schedule domain objects.
 */
public interface ScheduleDAO
{
	/** Bean id */
	public static final String ID = "de.iritgo.aktera.scheduler.ScheduleDAO";

	/**
	 * Retrieve a schedule by it's id.
	 *
	 * @param id The primary key of the schedule
	 * @return The schedule object or null if none was found
	 */
	public Schedule findScheduleById (Integer id);

	/**
	 * Retrieve all schedules.
	 *
	 * @return A collection of all schedules
	 */
	public Collection<Schedule> findAllSchedules ();

	/**
	 * Move a schedule one position up in the list of all schedules.
	 *
	 * @param schedule The schedule to move
	 * @return True if the schedule was moved up
	 */
	public boolean moveScheduleUp (Schedule schedule);

	/**
	 * Move a schedule one position down in the list of all schedules.
	 *
	 * @param schedule The schedule to move
	 * @return True if the schedule was moved up
	 */
	public boolean moveScheduleDown (Schedule schedule);

	/**
	 * Move a schedule to the first position in the list of all schedules.
	 *
	 * @param schedule The schedule to move
	 * @return True if the schedule was moved
	 */
	public boolean moveScheduleToFront (Schedule schedule);

	/**
	 * Move a schedule to the last position in the list of all schedules.
	 *
	 * @param schedule The schedule to move
	 * @return True if the schedule was moved
	 */
	public boolean moveScheduleToEnd (Schedule schedule);

	/**
	 * Retrieve the position of the last schedule.
	 *
	 * @return The position of the last schedule
	 */
	public int maxSchedulePosition ();

	/**
	 * Retrieve a schedule action by it's id.
	 *
	 * @param id The primary key of the schedule action
	 * @return The schedule action object or null if none was found
	 */
	public ScheduleAction findScheduleActionById (Integer id);

	/**
	 * Retrieve a list of all actions of a specific schedule.
	 *
	 * @param scheduleId The primary key of the schedule
	 * @return A list of the schedule'a actions
	 */
	public List<ScheduleAction> findScheduleActionsByScheduleId (Integer scheduleId);

	/**
	 * Move a schedule action one position up in the list of all schedules.
	 *
	 * @param schedule The schedule action to move
	 * @return True if the schedule action was moved up
	 */
	public boolean moveScheduleActionUp (ScheduleAction action);

	/**
	 * Move a schedule action one position down in the list of all schedules.
	 *
	 * @param schedule The schedule action to move
	 * @return True if the schedule action was moved up
	 */
	public boolean moveScheduleActionDown (ScheduleAction action);

	/**
	 * Move a schedule action to the first position in the list of all schedules.
	 *
	 * @param schedule The schedule action to move
	 * @return True if the schedule action was moved
	 */
	public boolean moveScheduleActionToFront (ScheduleAction action);

	/**
	 * Move a schedule action to the last position in the list of all schedules.
	 *
	 * @param schedule The schedule action to move
	 * @return True if the schedule action was moved
	 */
	public boolean moveScheduleActionToEnd (ScheduleAction action);

	/**
	 * Retrieve the position of the last action of a spceified schedule.
	 *
	 * @param scheduleId Primary key of the schedule
	 * @return The position of the last schedule action
	 */
	public int maxScheduleActionPosition (Integer scheduleId);

	/**
	 * Delete all actions of a specific scheulde.
	 *
	 * @param schedule The schedule which actions should be deleted.
	 */
	public void deleteAllActionsOfSchedule (Schedule schedule);

	/**
	 * Retrieve all schedule actions by the given action type
	 *
	 * @param type The type
	 * @return The schedule actions
	 */
	public Collection<ScheduleAction> findScheduleActionByType (String type);

	/**
	 * Update the given schedule action
	 *
	 * @param scheduleAction The schedule action
	 */
	@Transactional(readOnly = false)
	public void update (ScheduleAction action);
}
