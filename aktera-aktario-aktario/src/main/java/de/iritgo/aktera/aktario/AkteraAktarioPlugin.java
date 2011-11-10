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

package de.iritgo.aktera.aktario;


import de.iritgo.aktario.framework.base.FrameworkPlugin;
import de.iritgo.aktario.framework.dataobject.gui.DataObjectGUIPane;
import de.iritgo.aktario.framework.dataobject.gui.QueryPane;
import de.iritgo.aktera.aktario.akteraconnector.AkteraQuery;
import de.iritgo.aktera.aktario.akteraconnector.AkteraQueryPane;
import de.iritgo.aktera.aktario.akteraconnector.CancelAkteraObjectCommand;
import de.iritgo.aktera.aktario.akteraconnector.ConnectorServerManager;
import de.iritgo.aktera.aktario.akteraconnector.DeleteAkteraObjectCommand;
import de.iritgo.aktera.aktario.akteraconnector.DeleteAkteraObjectRequest;
import de.iritgo.aktera.aktario.akteraconnector.DeleteAkteraObjectResponse;
import de.iritgo.aktera.aktario.akteraconnector.EditAkteraObjectCommand;
import de.iritgo.aktera.aktario.akteraconnector.EditAkteraObjectRequest;
import de.iritgo.aktera.aktario.akteraconnector.EditAkteraObjectResponse;
import de.iritgo.aktera.aktario.akteraconnector.GetPersistent;
import de.iritgo.aktera.aktario.akteraconnector.NewAkteraObjectCommand;
import de.iritgo.aktera.aktario.akteraconnector.NewAkteraObjectRequest;
import de.iritgo.aktera.aktario.akteraconnector.NewAkteraObjectResponse;
import de.iritgo.aktera.aktario.akteraconnector.SaveAkteraObjectCommand;
import de.iritgo.aktera.aktario.akteraconnector.SaveAkteraObjectRequest;
import de.iritgo.aktera.aktario.akteraconnector.SaveAkteraObjectResponse;
import de.iritgo.aktera.aktario.gui.GetToolPanel;


/**
 *
 */
public class AkteraAktarioPlugin extends FrameworkPlugin
{
	@Override
	protected void registerDataObjects()
	{
		registerDataObject(new AkteraQuery());
	}

	@Override
	protected void registerGUIPanes()
	{
		registerGUIPane(new AkteraQueryPane());
		registerGUIPane(new DataObjectGUIPane());
		registerGUIPane(new QueryPane());
	}

	@Override
	protected void registerActions()
	{
		registerAction(new AkteraAktarioKeelCommandRequest());
		registerAction(new EditAkteraObjectRequest());
		registerAction(new EditAkteraObjectResponse());

		registerAction(new NewAkteraObjectRequest());
		registerAction(new NewAkteraObjectResponse());

		registerAction(new SaveAkteraObjectRequest());
		registerAction(new SaveAkteraObjectResponse());

		registerAction(new DeleteAkteraObjectRequest());
		registerAction(new DeleteAkteraObjectResponse());
	}

	@Override
	protected void registerCommands()
	{
		registerCommand(FrameworkPlugin.CLIENT, new Startup());
		registerCommand(FrameworkPlugin.CLIENT, new AkteraAktarioKeelCommand());
		registerCommand(FrameworkPlugin.SERVER, new AkteraAktarioKeelServerCommand());
		registerCommand(FrameworkPlugin.CLIENT, new GetToolPanel());
		registerCommand(FrameworkPlugin.CLIENT, new NewAkteraObjectCommand());
		registerCommand(FrameworkPlugin.CLIENT, new EditAkteraObjectCommand());
		registerCommand(FrameworkPlugin.CLIENT, new SaveAkteraObjectCommand());
		registerCommand(FrameworkPlugin.CLIENT, new DeleteAkteraObjectCommand());
		registerCommand(FrameworkPlugin.CLIENT, new CancelAkteraObjectCommand());
		registerCommand(FrameworkPlugin.CLIENT, new TestShowBuddyListCommand());

		registerCommand(FrameworkPlugin.SERVER, new GetPersistent());
		registerCommand(FrameworkPlugin.SERVER, new Authenticate());
		registerCommand(FrameworkPlugin.SERVER, new LoginAllowed());
	}

	@Override
	protected void registerManagers()
	{
		registerManager(FrameworkPlugin.CLIENT, new AkteraAktarioClientManager());

		if (getMode() == FrameworkPlugin.SERVER)
		{
			registerManager(FrameworkPlugin.SERVER, new AkteraAktarioServerManager());
			registerManager(FrameworkPlugin.SERVER, new ConnectorServerManager());
		}
	}
}
