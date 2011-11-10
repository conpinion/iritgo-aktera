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


/**
 * Form part info.
 */
public class ScheduleActionFormPartInfo
{
	/** Part id */
	private String id;

	/** Part form handler */
	private ScheduleActionFomPart formPart;

	/** Bundle name for resource lookup */
	private String bundle;

	/** Info resource key */
	private String infoKey;

	/**
	 * Get the part id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Set the part id.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Set the form handler.
	 */
	public ScheduleActionFomPart getFormPart()
	{
		return formPart;
	}

	/**
	 * Get the form handler.
	 */
	public void setFormPart(ScheduleActionFomPart formPart)
	{
		this.formPart = formPart;
	}

	/**
	 * Get the reource bundle.
	 */
	public String getBundle()
	{
		return bundle;
	}

	/**
	 * Set the resource bundle.
	 */
	public void setBundle(String bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * Get the info resource key.
	 */
	public String getInfoKey()
	{
		return infoKey;
	}

	/**
	 * Set the info resource key.
	 */
	public void setInfoKey(String infoKey)
	{
		this.infoKey = infoKey;
	}
}
