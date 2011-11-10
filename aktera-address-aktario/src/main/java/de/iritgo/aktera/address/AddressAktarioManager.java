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
import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.core.plugin.PluginEventListener;
import de.iritgo.aktario.core.plugin.PluginStateEvent;
import de.iritgo.aktario.framework.dataobject.DynDataObject;


/**
 *
 */
public class AddressAktarioManager extends BaseObject implements Manager, PluginEventListener
{
	protected DynDataObject address;

	/**
	 * Create a new client manager.
	 */
	public AddressAktarioManager(String instance)
	{
		super(instance);

		address = new DynDataObject("Address");
		address.addAttribute("lastName", "");
		address.addAttribute("firstName", "");
		address.addAttribute("street", "");
		address.addAttribute("company", "");
		address.addAttribute("phonenumber", "");
		address.addAttribute("email", "");
	}

	public void init()
	{
		Engine.instance().getEventRegistry().addListener("Plugin", this);
	}

	public void pluginEvent(PluginStateEvent event)
	{
	}

	public void unload()
	{
	}
}
