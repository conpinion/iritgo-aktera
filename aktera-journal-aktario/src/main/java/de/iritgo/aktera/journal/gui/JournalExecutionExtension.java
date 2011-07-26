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
import de.iritgo.aktario.framework.appcontext.AppContext;
import de.iritgo.aktario.framework.base.DataObject;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.dataobject.gui.ExtensionTile;
import de.iritgo.aktario.framework.dataobject.gui.QueryPane;
import de.iritgo.simplelife.string.StringTools;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Properties;


/**
 * Add a attribute to the participantstate object.
 *
 * @version $Id: AddressCallExtension.java,v 1.23 2006/10/05 15:00:42 grappendorf Exp $
 */
public class JournalExecutionExtension implements ExtensionTile
{
	public class ExecutionAction extends AbstractAction
	{
		private long journalId;

		public ExecutionAction (long journalId)
		{
			super ();
			this.journalId = journalId;
		}

		public void actionPerformed (ActionEvent e)
		{
			Properties props = new Properties ();

			props.setProperty ("model", "aktera.journal.execute-journal-entry");
			props.setProperty ("name", AppContext.instance ().getUser ().getName ());
			props.setProperty ("id", "" + journalId);

			CommandTools.performAsync ("AkteraAktarioKeelCommand", props);
		}
	}

	public class DeleteAction extends AbstractAction
	{
		private long journalId;

		public DeleteAction (long journalId)
		{
			super ();
			this.journalId = journalId;
		}

		public void actionPerformed (ActionEvent e)
		{
			Properties props = new Properties ();

			props.setProperty ("model", "aktera.journal.delete-journal-entry");
			props.setProperty ("name", AppContext.instance ().getUser ().getName ());
			props.setProperty ("id", "" + journalId);

			CommandTools.performAsync ("AkteraAktarioKeelCommand", props);
		}
	}

	public class DeleteAllAction extends AbstractAction
	{
		private long journalId;

		public DeleteAllAction (long journalId)
		{
			super ();
			this.journalId = journalId;
		}

		public void actionPerformed (ActionEvent e)
		{
			Properties props = new Properties ();

			props.setProperty ("model", "de.telcat.iptell.base.journal.JournalDeleteAll");
			props.setProperty ("name", AppContext.instance ().getUser ().getName ());
			props.setProperty ("id", "" + journalId);

			CommandTools.performAsync ("AkteraAktarioKeelCommand", props);
		}
	}

	private ImageIcon executeIcon;

	private ImageIcon deleteIcon;

	private DefaultTableCellRenderer defaultTableCellRenderer;

	public JournalExecutionExtension ()
	{
		executeIcon = new ImageIcon (JournalExecutionExtension.class.getResource ("/resources/tool-accept-16.png"));
		deleteIcon = new ImageIcon (JournalExecutionExtension.class.getResource ("/resources/tool-delete-16.gif"));
	}

	public String getTileId ()
	{
		return "journal.execution";
	}

	public void command (GUIPane guiPane, IObject iObject, Properties properties)
	{
		QueryPane queryPane = (QueryPane) guiPane;
		DataObject dataObject = (DataObject) iObject;

		if (dataObject == null)
		{
			return;
		}

		Point point = (Point) properties.get ("mousePosition");
		JTable table = (JTable) properties.get ("table");

		JPopupMenu popupMenu = new JPopupMenu ();
		long id = dataObject.getUniqueId ();
		String rawData = StringTools.trim (dataObject.getStringAttribute ("journal_rawData"));

		String executeString = Engine.instance ().getResourceService ().getStringWithoutException ("journalExecute");
		JMenuItem call = new JMenuItem ("<html><span style=\"width:8em\"><b>" + executeString + "</b></span> "
						+ rawData + "</html>", (Icon) executeIcon);

		call.addActionListener (new ExecutionAction (id));
		popupMenu.add (call);

		String deleteString = Engine.instance ().getResourceService ().getStringWithoutException ("journalDelete");
		JMenuItem delete = new JMenuItem ("<html><span style=\"width:8em\"><b>" + deleteString + "</b></span> "
						+ rawData + "</html>", (Icon) deleteIcon);

		delete.addActionListener (new DeleteAction (id));
		popupMenu.add (delete);

		JSeparator sep = new JSeparator ();

		popupMenu.add (sep);

		String deleteAllString = Engine.instance ().getResourceService ()
						.getStringWithoutException ("journalDeleteAll");
		JMenuItem deleteAll = new JMenuItem ("<html><span style=\"width:8em\"><b>" + deleteAllString + "</b></span>"
						+ "</html>", (Icon) deleteIcon);

		deleteAll.addActionListener (new DeleteAllAction (id));
		popupMenu.add (deleteAll);

		popupMenu.show (table, (int) point.getX (), (int) point.getY ());
	}

	public boolean isDoubleClickCommand ()
	{
		return false;
	}

	public JComponent getTile (GUIPane guiPane, IObject iObject, Properties properties)
	{
		defaultTableCellRenderer = new DefaultTableCellRenderer ()
		{
			public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column)
			{
				setIcon (executeIcon);

				table.getColumnModel ().getColumn (column).setMinWidth (executeIcon.getIconWidth ());
				table.getColumnModel ().getColumn (column).setMaxWidth (executeIcon.getIconWidth () + 4);

				return this;
			}
		};

		return defaultTableCellRenderer;
	}

	public String getLabel ()
	{
		return "0.empty";
	}

	public Object getConstraints ()
	{
		return null;
	}
}
