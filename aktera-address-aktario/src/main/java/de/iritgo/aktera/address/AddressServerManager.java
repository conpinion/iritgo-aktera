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

package de.iritgo.aktera.address;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.framework.base.IObjectCreatedEvent;
import de.iritgo.aktario.framework.base.IObjectCreatedListener;
import de.iritgo.aktario.framework.base.IObjectDeletedEvent;
import de.iritgo.aktario.framework.base.IObjectDeletedListener;
import de.iritgo.aktario.framework.base.IObjectModifiedEvent;
import de.iritgo.aktario.framework.base.IObjectModifiedListener;


/**
 *
 */
public class AddressServerManager extends AddressAktarioManager implements IObjectCreatedListener,
				IObjectModifiedListener, IObjectDeletedListener
{
	/**
	 * Create a new client manager.
	 */
	public AddressServerManager()
	{
		super("AddressServerManager");
	}

	/**
	 * Initialize the client manager.
	 */
	public void init()
	{
		Engine.instance().getEventRegistry().addListener("Plugin", this);
		Engine.instance().getEventRegistry().addListener("objectcreated", this);
		Engine.instance().getEventRegistry().addListener("objectmodified", this);
		Engine.instance().getEventRegistry().addListener("objectremoved", this);
	}

	/**
	 * Called when an iritgo object was created.
	 *
	 * @param event The creation event.
	 */
	public void iObjectCreatedEvent(IObjectCreatedEvent event)
	{
	}

	/**
	 * Called when an iritgo object was modified.
	 *
	 * @param event The modification event.
	 */
	public void iObjectModifiedEvent(IObjectModifiedEvent event)
	{
	}

	/**
	 * Called when an iritgo object was deleted.
	 *
	 * @param event The delete event.
	 */
	public void iObjectDeletedEvent(IObjectDeletedEvent event)
	{
	}

	/**
	 * Free all client manager resources.
	 */
	public void unload()
	{
	}
}
