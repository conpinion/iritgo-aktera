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

package de.iritgo.aktera.ui.form;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;


/**
 *
 */
public class ObjectExistsValidation implements Validation
{
	public ObjectExistsValidation()
	{
	}

	public boolean checkValidation(ModelRequest req, PersistentDescriptor persistentDescriptor,
					FieldDescriptor fieldDescriptor, String value)
	{
		try
		{
			if (value.equals(""))
			{
				return false;
			}

			PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE, req
							.getDomain());

			String name = fieldDescriptor.getName();
			String fieldName = name.substring(name.lastIndexOf('.') + 1);
			String persistentName = name.substring(0, Math.max(name.indexOf('.'), 0));
			String inputName = name.replace('.', '_');

			Persistent persistent = persistentDescriptor.getPersistent(persistentName);
			Persistent search = persistentManager.create(persistent.getName());

			search.setField(fieldName, value);

			if (search.find())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (PersistenceException x)
		{
			return false;
		}
		catch (ModelException x)
		{
			return false;
		}
	}

	public String getErrorTextId()
	{
		return "objectExistsValidation";
	}
}
