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
import de.iritgo.aktario.framework.base.DataObject;
import de.iritgo.aktario.framework.base.action.FrameworkInputStream;
import de.iritgo.aktario.framework.base.action.FrameworkOutputStream;
import de.iritgo.aktario.framework.base.action.FrameworkServerAction;
import java.io.IOException;


/**
 *
 */
public class DeleteAkteraObjectRequest extends FrameworkServerAction
{
	private String model;

	private String keelObjectUniqueId;

	private long dataObjectUniqueId;

	private String onScreenUniqueId;

	/**
	 * Standard constructor
	 */
	public DeleteAkteraObjectRequest()
	{
	}

	/**
	 * Standard constructor
	 */
	public DeleteAkteraObjectRequest(String model, DataObject dataObject, String onScreenUniqueId)
	{
		this.model = model;
		this.keelObjectUniqueId = dataObject.getStringAttribute("keelObjectId");
		this.dataObjectUniqueId = dataObject.getUniqueId();
		this.onScreenUniqueId = onScreenUniqueId;
	}

	/**
	 * Read the attributes from the given stream.
	 */
	public void readObject(FrameworkInputStream stream) throws IOException, ClassNotFoundException
	{
		model = stream.readUTF();
		keelObjectUniqueId = stream.readUTF();
		dataObjectUniqueId = stream.readLong();
		onScreenUniqueId = stream.readUTF();
	}

	/**
	 * Write the attributes to the given stream.
	 */
	public void writeObject(FrameworkOutputStream stream) throws IOException
	{
		stream.writeUTF(model);
		stream.writeUTF(keelObjectUniqueId);
		stream.writeLong(dataObjectUniqueId);
		stream.writeUTF(onScreenUniqueId);
	}

	/**
	 * Perform the action.
	 */
	public void perform()
	{
		ConnectorServerManager connectorServerManager = (ConnectorServerManager) Engine.instance().getManagerRegistry()
						.getManager("ConnectorServerManager");

		connectorServerManager.deleteKeelObject(model, keelObjectUniqueId, userUniqueId);

		ActionTools.sendToClient(userUniqueId, new DeleteAkteraObjectResponse(model, keelObjectUniqueId,
						dataObjectUniqueId, onScreenUniqueId));
	}
}
