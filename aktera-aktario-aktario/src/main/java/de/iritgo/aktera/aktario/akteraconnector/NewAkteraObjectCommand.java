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

package de.iritgo.aktera.aktario.akteraconnector;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.framework.action.ActionTools;
import de.iritgo.aktario.framework.client.command.ShowOtherFrame;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.dataobject.gui.DataObjectCommand;
import de.iritgo.aktario.framework.dataobject.gui.QueryPane;
import java.awt.Rectangle;
import java.util.Properties;


/**
 *
 */
public class NewAkteraObjectCommand extends DataObjectCommand
{
	/**
	 * Create a new startup command.
	 */
	public NewAkteraObjectCommand ()
	{
		super ("NewAkteraObjectCommand");
		properties = new Properties ();
	}

	/**
	 *
	 */
	public void perform ()
	{
		Properties props = new Properties ();

		props.put ("closable", Boolean.FALSE);
		props.put ("iconifiable", Boolean.FALSE);
		props.put ("maximizable", Boolean.FALSE);
		props.put ("maximized", Boolean.TRUE);
		props.put ("titlebar", Boolean.FALSE);

		String jFrameId = "jFrameId-" + Engine.instance ().getTransientIDGenerator ().createId ();
		String queryPaneId = ((QueryPane) swingGUIPane).getOnScreenUniqueId ();

		Rectangle bounds = new Rectangle (400, 300, 800, 600);

		CommandTools.performSimple (new ShowOtherFrame (null, value, jFrameId, bounds));

		NewAkteraObjectRequest newAkteraObjectRequest = new NewAkteraObjectRequest (value, jFrameId, queryPaneId);

		ActionTools.sendToServer (newAkteraObjectRequest);
	}
}
