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

package de.iritgo.aktera.base.module;


import de.iritgo.aktera.persist.ModuleVersion;


/**
 * This class is used to perform update steps from one version to another.
 * It's main purpose is to correctly track the version number updates.
 */
public class UpdateStep
{
	/** The start version. */
	protected String fromVersion;

	/** The target version. */
	protected String toVersion;

	/** The current module version. */
	protected ModuleVersion currentVersion;

	/**
	 * Create a new update step.
	 *
	 * @param currentVersion The current module version.
	 * @param fromVersion The start version.
	 * @param toVersion The target version.
	 */
	public UpdateStep(String fromVersion, String toVersion, ModuleVersion currentVersion)
	{
		this.fromVersion = fromVersion;
		this.toVersion = toVersion;
		this.currentVersion = currentVersion;
	}

	/**
	 * Perform the update step. Subclasses should override this method and perform
	 * the needed update actions.
	 */
	public void perform()
	{
	}

	/**
	 * This method is called by the update handler to perform the update step.
	 */
	public void performUpdate()
	{
		if (currentVersion.between(fromVersion, toVersion))
		{
			perform();
			currentVersion.setVersion(toVersion);
		}
	}
}
