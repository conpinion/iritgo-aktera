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


import de.iritgo.aktario.core.gui.IDisplay;
import de.iritgo.aktario.framework.base.action.FrameworkAction;
import de.iritgo.aktario.framework.base.action.FrameworkInputStream;
import de.iritgo.aktario.framework.base.action.FrameworkOutputStream;
import de.iritgo.aktario.framework.client.Client;
import de.iritgo.aktario.framework.dataobject.gui.QueryPane;
import java.io.IOException;


/**
 *
 */
public class DeleteAkteraObjectResponse extends FrameworkAction
{
	private String model;

	private String keelObjectUniqueId;

	private long dataObjectUniqueId;

	private String onScreenUniqueId;

	/**
	 *
	 */
	public DeleteAkteraObjectResponse ()
	{
		setTypeId ("DeleteAkteraObjectResponse");
	}

	/**
	 *
	 */
	public DeleteAkteraObjectResponse (String model, String keelObjectUniqueId, long dataObjectUniqueId,
					String onScreenUniqueId)
	{
		this ();
		this.keelObjectUniqueId = keelObjectUniqueId;
		this.model = model;
		this.dataObjectUniqueId = dataObjectUniqueId;
		this.onScreenUniqueId = onScreenUniqueId;
	}

	/**
	 * Read the attributes from the a stream.
	 *
	 * @param stream The stream to read from.
	 */
	public void readObject (FrameworkInputStream stream) throws IOException
	{
		model = stream.readUTF ();
		keelObjectUniqueId = stream.readUTF ();
		dataObjectUniqueId = stream.readLong ();
		onScreenUniqueId = stream.readUTF ();
	}

	/**
	 * Write the attributes to a stream.
	 *
	 * @param stream The stream to write to.
	 */
	public void writeObject (FrameworkOutputStream stream) throws IOException
	{
		stream.writeUTF (model);
		stream.writeUTF (keelObjectUniqueId);
		stream.writeLong (dataObjectUniqueId);
		stream.writeUTF (onScreenUniqueId);
	}

	/**
	 * Perform the action.
	 */
	public void perform ()
	{
		IDisplay display = (IDisplay) Client.instance ().getClientGUI ().getDesktopManager ().getDisplay (
						onScreenUniqueId);
		QueryPane queryPane = (QueryPane) display.getGUIPane ();

		queryPane.deleteListEntry (dataObjectUniqueId, model);
	}
}
