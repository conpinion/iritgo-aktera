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


import de.iritgo.aktario.framework.base.FrameworkPlugin;
import de.iritgo.aktera.address.command.ShowAddressViewDialog;
import de.iritgo.aktera.address.gui.AddressQueryPane;
import de.iritgo.aktera.address.wsclient.AddressClientService;


/**
 *
 */
public class AkteraAddressPlugin extends FrameworkPlugin
{
	/**
	 * @see de.iritgo.aktario.core.plugin.Plugin#registerGUIPanes()
	 */
	@Override
	protected void registerGUIPanes ()
	{
		registerGUIPane (new AddressQueryPane ());
	}

	/**
	 * @see de.iritgo.aktario.core.plugin.Plugin#registerClientManagers()
	 */
	@Override
	protected void registerClientManagers ()
	{
		registerManager (new AddressClientManager ());
		registerManager (new AddressClientService ());
	}

	/**
	 * @see de.iritgo.aktario.core.plugin.Plugin#registerServerManagers()
	 */
	@Override
	protected void registerServerManagers ()
	{
		registerManager (new AddressServerManager ());
	}

	/**
	 * @see de.iritgo.aktario.framework.base.FrameworkPlugin#registerConsoleCommands()
	 */
	@Override
	protected void registerConsoleCommands ()
	{
	}

	/**
	 * @see de.iritgo.aktario.core.plugin.Plugin#registerCommands()
	 */
	@Override
	protected void registerClientCommands ()
	{
		registerCommand (new ShowAddressViewDialog ());
	}
}
