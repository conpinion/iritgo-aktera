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

package de.iritgo.aktera.scheduler.ui;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.scheduler.entity.Schedule;
import de.iritgo.aktera.scheduler.entity.ScheduleDAO;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import org.apache.avalon.framework.configuration.Configuration;
import java.util.List;


/**
 * Formular handler for the Schedule form.
 */
public class ScheduleFormularHandler extends FormularHandler
{
	/** */
	private ScheduleDAO scheduleDAO;

	/**
	 * @param scheduleDAO The new scheduleDAO.
	 */
	public void setScheduleDAO (ScheduleDAO scheduleDAO)
	{
		this.scheduleDAO = scheduleDAO;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#adjustFormular(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor)
	 */
	@Override
	public void adjustFormular (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		persistents.putAttribute ("schedule.holidaysProvinceCountry", persistents.getPersistent ("schedule")
						.getFieldString ("holidaysCountry"));
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#createPersistents(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor, List)
	 */
	@Override
	public int createPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		synchronized (Schedule.class)
		{
			persistents.getPersistent ("schedule").setField ("position", scheduleDAO.maxSchedulePosition () + 1);

			return super.createPersistents (request, formular, persistents, persistentConfig);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#deletePersistent(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, Object, de.iritgo.aktera.persist.Persistent, boolean)
	 */
	@Override
	public void deletePersistent (ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		synchronized (Schedule.class)
		{
			Schedule schedule = scheduleDAO.findScheduleById (persistent.getFieldInt ("id"));

			scheduleDAO.deleteAllActionsOfSchedule (schedule);
			scheduleDAO.moveScheduleToEnd (schedule);
			super.deletePersistent (request, response, id, persistent, systemDelete);
		}
	}
}
