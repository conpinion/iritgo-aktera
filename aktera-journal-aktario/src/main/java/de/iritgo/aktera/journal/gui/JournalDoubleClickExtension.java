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

package de.iritgo.aktera.journal.gui;


import de.iritgo.aktario.core.gui.GUIPane;
import de.iritgo.aktario.core.iobject.IObject;
import de.iritgo.aktario.framework.dataobject.gui.ExtensionTile;
import javax.swing.JComponent;
import java.util.Properties;


/**
 *
 */
public class JournalDoubleClickExtension implements ExtensionTile
{
	/** */
	private String tileId;

	/**
	 * @param tileId
	 */
	public JournalDoubleClickExtension (String tileId)
	{
		this.tileId = tileId;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getTile(de.iritgo.aktario.core.gui.GUIPane, de.iritgo.aktario.core.iobject.IObject, java.util.Properties)
	 */
	public JComponent getTile (GUIPane guiPane, IObject iObject, Properties properties)
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#command(de.iritgo.aktario.core.gui.GUIPane, de.iritgo.aktario.core.iobject.IObject, java.util.Properties)
	 */
	public void command (GUIPane guiPane, IObject iObject, Properties properties)
	{
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#isDoubleClickCommand()
	 */
	public boolean isDoubleClickCommand ()
	{
		return true;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getTileId()
	 */
	public String getTileId ()
	{
		return tileId;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getLabel()
	 */
	public String getLabel ()
	{
		return "";
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getConstraints()
	 */
	public Object getConstraints ()
	{
		return null;
	}
}
