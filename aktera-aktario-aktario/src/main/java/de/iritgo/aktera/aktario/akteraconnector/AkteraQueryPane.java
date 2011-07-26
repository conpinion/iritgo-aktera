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
import de.iritgo.aktario.core.gui.GUIPane;
import de.iritgo.aktario.core.gui.IButton;
import de.iritgo.aktario.core.gui.ITableSorter;
import de.iritgo.aktario.core.gui.SwingGUIPane;
import de.iritgo.aktario.core.iobject.IObject;
import de.iritgo.aktario.core.iobject.IObjectList;
import de.iritgo.aktario.core.iobject.IObjectTableModelSorted;
import de.iritgo.aktario.core.logger.Log;
import de.iritgo.aktario.framework.base.DataObject;
import de.iritgo.aktario.framework.dataobject.DynDataObject;
import de.iritgo.aktario.framework.dataobject.IObjectRegisteredEventListener;
import de.iritgo.aktera.aktario.AkteraAktarioPlugin;
import org.swixml.SwingEngine;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Properties;


/**
 * This gui pane displays a list of all users and lets the administrator
 * add, edit, and delete users.
 */
@SuppressWarnings("serial")
public class AkteraQueryPane extends SwingGUIPane implements TableModelListener
{
	/** The table of all users. */
	public JTable akteraQueryTable;

	/** Model for the user table. */
	private IObjectTableModelSorted akteraQueryTableModel;

	/** The edit button. */
	public IButton btnEdit;

	/** The delete button. */
	public IButton btnDelete;

	/** ScrollPane containing the akteraQuery state table. */
	public JScrollPane akteraQueryScrollPane;

	private ITableSorter tableSorter;

	private boolean allreadyInit = false;

	/**
	 * Close the display
	 */
	public Action okAction = new AbstractAction ()
	{
		public void actionPerformed (ActionEvent e)
		{
			display.close ();
		}
	};

	/**
	 * Create a new UserListGUIPane.
	 */
	public AkteraQueryPane ()
	{
		super ("AkteraQueryPane");
	}

	/**
	 * Initialize the gui. Subclasses should override this method to create a
	 * custom gui.
	 */
	public void initGUI ()
	{
	}

	public void initOnLoadGUI (final DynDataObject sampleObject)
	{
		try
		{
			if (allreadyInit)
			{
				return;
			}

			allreadyInit = true;

			SwingEngine swingEngine = new SwingEngine (this);

			swingEngine.setClassLoader (AkteraAktarioPlugin.class.getClassLoader ());

			JPanel panel = (JPanel) swingEngine.render (getClass ().getResource ("/swixml/AkteraQueryPane.xml"));

			content.add (panel, createConstraints (0, 0, 1, 1, GridBagConstraints.BOTH, 100, 100, null));

			akteraQueryTableModel = new IObjectTableModelSorted ()
			{
				public int getColumnCount ()
				{
					return sampleObject.getNumAttributes ();
				}

				public String getColumnName (int col)
				{
					String text = Engine.instance ().getResourceService ().getStringWithoutException (
									(String) new LinkedList (sampleObject.getAttributes ().keySet ()).get (col));

					return text;
				}

				public boolean isCellEditable (int row, int col)
				{
					return false;
				}

				public Object getValueAt (int row, int col)
				{
					IObjectList list = ((AkteraQuery) getIObject ()).getIObjectListResults ();

					DataObject akteraQuery = (DataObject) list.get (row);

					Object object = new LinkedList (akteraQuery.getAttributes ().values ()).get (col);

					return object;
				}
			};

			akteraQueryTable.setShowGrid (true);

			akteraQueryTable.setCellSelectionEnabled (false);

			akteraQueryTable.setRowSelectionAllowed (true);

			akteraQueryTable.setSelectionMode (0);

			akteraQueryTable.setRowHeight (Math.max (akteraQueryTable.getRowHeight () + 4, 24 + 4));

			akteraQueryScrollPane.getColumnHeader ().setVisible (true);

			tableSorter = akteraQueryTableModel.getTableSorter ();
			akteraQueryTable.setModel (tableSorter);
			akteraQueryTableModel.addTableModelListener (this);

			akteraQueryTable.addMouseListener (new MouseAdapter ()
			{
				public void mouseClicked (MouseEvent e)
				{
					int col = akteraQueryTable.columnAtPoint (e.getPoint ());
					int row = tableSorter.getRealRow (akteraQueryTable.getSelectedRow ());

					if ((col < 0) || (row < 0))
					{
						return;
					}

					IObjectList list = ((AkteraQuery) getIObject ()).getIObjectListResults ();

					DataObject akteraQuery = (DataObject) list.get (row);

					String attributeName = (String) new LinkedList (akteraQuery.getAttributes ().keySet ()).get (col);

					if (e.getClickCount () == 2)
					{
						Properties props = new Properties ();

						return;
					}
				}
			});

			akteraQueryTable.getSelectionModel ().addListSelectionListener (new ListSelectionListener ()
			{
				public void valueChanged (ListSelectionEvent e)
				{
				}
			});
		}
		catch (Exception x)
		{
			Log.logError ("client", "AkteraQuery.initGUI", x.toString ());
			x.printStackTrace ();
		}
	}

	/**
	 * Load the data object into the gui.
	 */
	public void loadFromObject (IObject iobject)
	{
		final AkteraQuery akteraQuery = (AkteraQuery) iobject;

		final String iObjectTypeId = akteraQuery.getStringAttribute ("listName");
		DynDataObject sampleObject = null;

		try
		{
			sampleObject = (DynDataObject) Engine.instance ().getIObjectFactory ().newInstance (iObjectTypeId);
			initOnLoadGUI (sampleObject);

			akteraQueryTableModel.update (akteraQuery.getIObjectListResults ());
		}
		catch (Exception x)
		{
			x.printStackTrace ();

			Engine.instance ().getEventRegistry ().addListener ("iobjectregistered",
							new IObjectRegisteredEventListener ()
							{
								public void iObjectRegisteredEvent (IObject iObject)
								{
									try
									{
										initOnLoadGUI ((DynDataObject) Engine.instance ().getIObjectFactory ()
														.newInstance (iObjectTypeId));
									}
									catch (Exception x)
									{
									}

									akteraQueryTableModel.update (akteraQuery.getIObjectListResults ());
								}
							});
		}
	}

	public void tableChanged (TableModelEvent e)
	{
		tableSorter.reallocateIndexesUpdate ();
		tableSorter.sortByColumn (0);
	}

	/**
	 * Store the gui values to the data object.
	 */
	public void storeToObject (IObject iobject)
	{
	}

	/**
	 * Return a clone of this gui pane.
	 *
	 * @return The gui pane clone.
	 */
	public GUIPane cloneGUIPane ()
	{
		return new AkteraQueryPane ();
	}

	public DynDataObject getIObjectAtRow (int row)
	{
		IObjectList list = ((AkteraQuery) getIObject ()).getIObjectListResults ();

		DynDataObject dynDataObject = (DynDataObject) list.get (tableSorter.getRealRow (row));

		return dynDataObject;
	}
}
