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

package de.iritgo.aktera.aktario.gui;


import de.iritgo.aktario.core.command.Command;
import de.iritgo.aktario.framework.client.Client;


/**
 * Get the participant state dyn data object from the participant state gui pane.
 */
public class GetToolPanel extends Command
{
	/**
	 * Create a new startup command.
	 */
	public GetToolPanel ()
	{
		super ("GetToolPanel");
	}

	/**
	 * perform command.
	 */
	public Object performWithResult ()
	{
		return ((AkteraAktarioGUI) Client.instance ().getClientGUI ()).getToolPanel ();
	}
}
