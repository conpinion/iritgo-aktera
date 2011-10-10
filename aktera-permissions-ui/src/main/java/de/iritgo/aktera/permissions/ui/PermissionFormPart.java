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

package de.iritgo.aktera.permissions.ui;


import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.aktera.ui.listing.RowData;


public abstract class PermissionFormPart
{
	public String getBundle ()
	{
		return "Aktera";
	}

	public abstract String[] getPermissionKeys ();

	/**
	 * Create an detail info string for the permission list.
	 *
	 * @param request The model request
	 * @param data Row data
	 * @return An detail info string
	 * @throws ModelException In case of an error
	 */
	public String createListInfo (ModelRequest request, RowData data) throws PersistenceException, ModelException
	{
		return null;
	}

	/**
	 * This method is called after the formular descriptor was created.
	 * You can override this method to change the formularattributes depending
	 * on the request parameters (e.g. enable or disable some field groups).
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 */
	public void adjustFormular (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
	}

	/**
	 * This method is called to validate the persistents after the formular
	 * data was transfered to the persistents.
	 *
	 * @param request The model request.
	 * @param response The model response.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 * @param create True if the persistents are about to be created.
	 * @param result The validation result.
	 */
	public void validatePersistents (ModelRequest request, ModelResponse response, FormularDescriptor formular,
					PersistentDescriptor persistents, boolean create, ValidationResult result)
		throws ModelException, PersistenceException
	{
	}
}
