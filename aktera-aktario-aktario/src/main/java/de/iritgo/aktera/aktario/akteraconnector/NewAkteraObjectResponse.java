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
import de.iritgo.aktario.framework.base.action.FrameworkAction;
import de.iritgo.aktario.framework.base.action.FrameworkInputStream;
import de.iritgo.aktario.framework.base.action.FrameworkOutputStream;
import de.iritgo.aktario.framework.client.command.ShowWindow;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.dataobject.DataObjectTools;
import de.iritgo.aktario.framework.dataobject.DynDataObject;
import java.io.IOException;
import java.util.Properties;


/**
 *
 */
public class NewAkteraObjectResponse extends FrameworkAction
{
	private String model;

	private long dataObjectUniqueId;

	private String jFrameId;

	private String queryPaneId;

	/**
	 *
	 */
	public NewAkteraObjectResponse()
	{
	}

	/**
	 *
	 */
	public NewAkteraObjectResponse(String model, long dataObjectUniqueId, String jFrameId, String queryPaneId)
	{
		this();
		this.dataObjectUniqueId = dataObjectUniqueId;
		this.model = model;
		this.jFrameId = jFrameId;
		this.queryPaneId = queryPaneId;
	}

	/**
	 * Read the attributes from the a stream.
	 *
	 * @param stream The stream to read from.
	 */
	public void readObject(FrameworkInputStream stream) throws IOException
	{
		model = stream.readUTF();
		dataObjectUniqueId = stream.readLong();
		jFrameId = stream.readUTF();
		queryPaneId = stream.readUTF();
	}

	/**
	 * Write the attributes to a stream.
	 *
	 * @param stream The stream to write to.
	 */
	public void writeObject(FrameworkOutputStream stream) throws IOException
	{
		stream.writeUTF(model);
		stream.writeLong(dataObjectUniqueId);
		stream.writeUTF(jFrameId);
		stream.writeUTF(queryPaneId);
	}

	/**
	 * Perform the action.
	 */
	public void perform()
	{
		Properties props = new Properties();

		props.put("closable", Boolean.FALSE);
		props.put("iconifiable", Boolean.FALSE);
		props.put("maximizable", Boolean.FALSE);
		props.put("maximized", Boolean.TRUE);
		props.put("titlebar", Boolean.FALSE);
		props.put("queryPaneId", queryPaneId);
		props.setProperty("toolbar", "yes");

		DynDataObject dataObject = (DynDataObject) DataObjectTools.createDynDataObject(model, dataObjectUniqueId);

		String onScreenUniqueId = "onScreenUniqueId-" + Engine.instance().getTransientIDGenerator().createId();

		CommandTools.performAsync(new ShowWindow("DataObjectGUIPane", onScreenUniqueId, jFrameId, dataObject), props);
	}
}
