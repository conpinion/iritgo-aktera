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
import de.iritgo.aktera.model.StandardModel;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="de.iritgo.aktera.scheduler.ScheduleActionFormParts"
 * @x-avalon.lifestyle type="singleton"
 * @model.model
 *   name="de.iritgo.aktera.scheduler.ScheduleActionFormParts"
 *   id="de.iritgo.aktera.scheduler.ScheduleActionFormParts"
 *   logger="aktera"
 */
public class ScheduleActionFormParts extends StandardModel
{
	/** Model id */
	public final static String ID = "de.iritgo.aktera.scheduler.ScheduleActionFormParts";

	/**
	 * @see de.iritgo.aktera.model.Model#execute(de.iritgo.aktera.model.ModelRequest)
	 */
	public ModelResponse execute (ModelRequest request) throws ModelException
	{
		return null;
	}
}
