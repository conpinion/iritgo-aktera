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


import java.util.logging.Logger;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.ui.el.ExpressionLanguageContext;


public class EditFormContext extends ExpressionLanguageContext
{
	/** The persistent descriptor */
	private PersistentDescriptor persistents;

	public void setPersistents(PersistentDescriptor persistents)
	{
		this.persistents = persistents;
	}

	public PersistentDescriptor getPersistents()
	{
		return persistents;
	}

	public Persistent getPersistent(String name)
	{
		try
		{
			return persistents.getPersistent(name);
		}
		catch (ModelException x)
		{
			return null;
		}
	}

	public Object getAttribute(String name)
	{
		try
		{
			return persistents.getAttribute(name);
		}
		catch (ModelException x)
		{
			return null;
		}
	}

	/** The id of the edited object */
	private Object id;

	public void setId(Object id)
	{
		this.id = id;
	}

	public Object getId()
	{
		return id;
	}
}
