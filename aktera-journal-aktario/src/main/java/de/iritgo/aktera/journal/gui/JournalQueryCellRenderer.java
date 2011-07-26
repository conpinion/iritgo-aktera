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


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.gui.GUIPane;
import de.iritgo.aktario.core.iobject.IObject;
import de.iritgo.aktario.core.resource.ResourceService;
import de.iritgo.aktario.framework.dataobject.DynDataObject;
import de.iritgo.aktario.framework.dataobject.gui.ExtensionTile;
import de.iritgo.aktera.journal.JournalClientManager;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.util.Properties;


/**
 *
 */
public class JournalQueryCellRenderer implements ExtensionTile
{
	/** Id of a primrary type renderer */
	public static final String PRIMRARY_TYPE_ID = "journal_primaryType";

	/** Id of a secondary type renderer */
	public static final String SECONDARY_TYPE_ID = "journal_secondaryType";

	/** Id of a secondary type text renderer */
	public static final String SECONDARY_TYPE_TEXT_ID = "journal_secondaryTypeText";

	/** Id of a message renderer */
	public static final String MESSAGE_ID = "journal_message";

	/** Id of a occurredAt renderer */
	public static final String OCCURREDAT_ID = "journal_occurredAt";

	/** The tile id */
	private String tileId;

	/** Our journal manager */
	private JournalClientManager manager;

	/** Our resource service */
	private ResourceService resources;

	/**
	 * @param tileId
	 */
	public JournalQueryCellRenderer (String tileId)
	{
		this.tileId = tileId;
		this.manager = (JournalClientManager) Engine.instance ().getManagerRegistry ().getManager (
						"de.iritgo.aktera.journal.JournalClientManager");
		resources = Engine.instance ().getResourceService ();
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#command(de.iritgo.aktario.core.gui.GUIPane,
	 *      de.iritgo.aktario.core.iobject.IObject, java.util.Properties)
	 */
	public void command (GUIPane guiPane, IObject iObject, Properties properties)
	{
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getConstraints()
	 */
	public Object getConstraints ()
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getLabel()
	 */
	public String getLabel ()
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getTile(de.iritgo.aktario.core.gui.GUIPane,
	 *      de.iritgo.aktario.core.iobject.IObject, java.util.Properties)
	 */
	@SuppressWarnings("serial")
	public JComponent getTile (GUIPane guiPane, final IObject iObject, Properties properties)
	{
		return new DefaultTableCellRenderer ()
		{
			public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column)
			{
				if (PRIMRARY_TYPE_ID.equals (tileId))
				{
					ImageIcon icon = manager.getJournalPrimaryTypeIcon (value.toString ());

					setIcon (icon);
					setVerticalAlignment (JLabel.TOP);
					table.getColumnModel ().getColumn (column).setMinWidth (icon.getIconWidth ());
					table.getColumnModel ().getColumn (column).setMaxWidth (icon.getIconWidth () + 4);

					return this;
				}
				else if (SECONDARY_TYPE_ID.equals (tileId))
				{
					ImageIcon icon = manager.getJournalSecondaryTypeIcon (value.toString ());

					setIcon (icon);
					setVerticalAlignment (JLabel.TOP);
					table.getColumnModel ().getColumn (column).setMinWidth (icon.getIconWidth ());
					table.getColumnModel ().getColumn (column).setMaxWidth (icon.getIconWidth () + 4);

					return this;
				}
				else if ("journal_id".equals (tileId))
				{
					DynDataObject rowData = ((DynDataObject) value);

					setText ("<html><b>" + rowData.getStringAttribute ("journal_rawData") + "</b><br/><font size=-2>"
									+ resources.getStringWithParams (rowData.getStringAttribute (MESSAGE_ID))
									+ "</font><br/><font size=-1>" + rowData.getStringAttribute (OCCURREDAT_ID)
									+ "</font>");
					setVerticalAlignment (JLabel.TOP);

					return this;
				}

				return this;
			}
		};
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#getTileId()
	 */
	public String getTileId ()
	{
		return tileId;
	}

	/**
	 * @see de.iritgo.aktario.framework.dataobject.gui.ExtensionTile#isDoubleClickCommand()
	 */
	public boolean isDoubleClickCommand ()
	{
		return false;
	}
}
