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

package de.iritgo.aktera.address.gui;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.gui.GUIPane;
import de.iritgo.aktario.core.gui.IBusyButton;
import de.iritgo.aktario.core.gui.IComboBoxIdItem;
import de.iritgo.aktario.core.gui.ITableColumn;
import de.iritgo.aktario.core.gui.SwingGUIPane;
import de.iritgo.aktario.core.iobject.IObject;
import de.iritgo.aktario.core.logger.Log;
import de.iritgo.aktario.core.resource.ResourceService;
import de.iritgo.aktera.address.AddressClientManager;
import de.iritgo.aktera.address.wsclient.Address;
import de.iritgo.aktera.address.wsclient.AddressClientService;
import de.iritgo.aktera.address.wsclient.AddressStore;
import de.iritgo.simplelife.math.IntRange;
import de.iritgo.simplelife.string.StringTools;
import org.swixml.SwingEngine;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;


@SuppressWarnings("serial")
public class AddressQueryPane extends SwingGUIPane
{
	private class AddressTableModel extends AbstractTableModel
	{
		public int getColumnCount ()
		{
			return columns.size ();
		}

		public int getRowCount ()
		{
			return addresses.size ();
		}

		@Override
		public String getColumnName (int column)
		{
			return columns.get (column).getTitle ().get ();
		}

		public Object getValueAt (int row, int column)
		{
			Address address = addresses.get (row);

			switch (column)
			{
				case 0:
					return address.getId ();

				case 1:
					return address.getLastName ();

				case 2:
					return address.getFirstName ();

				case 3:
					return address.getCompany ();

				default:
					return "";
			}
		}

		@Override
		public void setValueAt (Object value, int row, int column)
		{
		}
	}

	private class AddressTableCellRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected,
						boolean hasFocus, int rowIndex, int colIndex)
		{
			super.getTableCellRendererComponent (table, value, isSelected, hasFocus, rowIndex, colIndex);

			switch (colIndex)
			{
				default:
					setIcon (columns.get (colIndex).getCellIcon ());
					setText (value.toString ());

					break;
			}

			return this;
		}

		@Override
		public void validate ()
		{
		}

		@Override
		public void revalidate ()
		{
		}

		@Override
		protected void firePropertyChange (String propertyName, Object oldValue, Object newValue)
		{
		}

		@Override
		public void firePropertyChange (String propertyName, boolean oldValue, boolean newValue)
		{
		}
	}

	private class AddressTableMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked (MouseEvent e)
		{
			int col = addressTable.columnAtPoint (e.getPoint ());
			int row = addressTable.getSelectedRow ();

			if ((col < 0) || (row < 0))
			{
				return;
			}

			col = addressTable.getColumnModel ().getColumn (col).getModelIndex ();

			String addressId = (String) addressTable.getValueAt (addressTable.getSelectedRow (), 0);
			Address address = null;

			for (Address searchAddress : addresses)
			{
				if (searchAddress.getId ().equals (addressId))
				{
					address = searchAddress;
				}
			}

			if (e.getClickCount () == 2)
			{
				AddressViewDialog dialog = new AddressViewDialog ();

				dialog.show (address.getStoreId (), address.getId ());
			}
			else
			{
				columns.get (col).onCellClicked (address, addressTable, e);
			}
		}
	}

	private List<ITableColumn> columns;

	public JComboBox addressStore;

	public JTextField searchText;

	public JTable addressTable;

	private List<Address> addresses = new LinkedList ();

	public IBusyButton search;

	public Action doSearch = new AbstractAction ()
	{
		public void actionPerformed (ActionEvent e)
		{
			if (IComboBoxIdItem.getSelectedId (addressStore) != null)
			{
				new Thread ()
				{
					public void run ()
					{
						search (IComboBoxIdItem.getSelectedId (addressStore).toString (), searchText.getText ());
					}
				}.start ();
			}
		}
	};

	public AddressQueryPane ()
	{
		super ("AddressQueryGuiPane");
	}

	@Override
	public void initGUI ()
	{
		try
		{
			final ResourceService resources = Engine.instance ().getResourceService ();
			SwingEngine swingEngine = new SwingEngine (this);

			AddressClientManager acm = (AddressClientManager) Engine.instance ().getManager (AddressClientManager.ID);

			columns = acm.getAddressTableColumns ();

			JPanel panel = (JPanel) swingEngine.render (getClass ().getResource ("/swixml/AddressQueryPane.xml"));

			content.add (panel, createConstraints (0, 0, 1, 1, GridBagConstraints.BOTH, 100, 100, null));

			search.idle ();

			AddressTableCellRenderer renderer = new AddressTableCellRenderer ();
			AddressTableModel model = new AddressTableModel ();

			addressTable.setModel (model);
			addressTable.getColumnModel ().getColumn (0).setMinWidth (0);
			addressTable.getColumnModel ().getColumn (0).setMaxWidth (0);

			for (int i = 1; i < model.getColumnCount (); ++i)
			{
				addressTable.getColumnModel ().getColumn (i).setCellRenderer (renderer);
			}

			addressTable.addMouseListener (new AddressTableMouseListener ());

			for (int i : new IntRange (0, columns.size () - 1))
			{
				ITableColumn c = columns.get (i);

				if (StringTools.isTrimEmpty (c.getTitle ().get ()))
				{
					if (c.getCellIcon () != null)
					{
						addressTable.getColumnModel ().getColumn (i).setMinWidth (c.getCellIcon ().getIconWidth () + 4);
						addressTable.getColumnModel ().getColumn (i).setMaxWidth (c.getCellIcon ().getIconWidth () + 4);
					}
					else
					{
						addressTable.getColumnModel ().getColumn (i).setMinWidth (4);
						addressTable.getColumnModel ().getColumn (i).setMaxWidth (4);
					}
				}
			}

			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel> ();

			addressTable.setRowSorter (sorter);
			sorter.setModel (model);

			addressStore.addActionListener (new ActionListener ()
			{
				public void actionPerformed (ActionEvent e)
				{
					search (IComboBoxIdItem.getSelectedId (addressStore).toString (), searchText.getText ());
				}
			});

			searchText.grabFocus ();

			new Thread ()
			{
				@Override
				public void run ()
				{
					search.busy ();

					AddressClientService acs = (AddressClientService) Engine.instance ().getManager (
									AddressClientService.ID);

					for (final AddressStore ads : acs.listAddressStores ())
					{
						SwingUtilities.invokeLater (new Runnable ()
						{
							public void run ()
							{
								addressStore.addItem (new IComboBoxIdItem (ads.getName (), resources
												.getStringWithoutException (ads.getTitle ())));
							}
						});
					}

					final String defaultAddressStoreId = acs.getDefaultAddressStoreName ();

					SwingUtilities.invokeLater (new Runnable ()
					{
						public void run ()
						{
							IComboBoxIdItem.selectItemWithId (addressStore, defaultAddressStoreId);
						}
					});
					search (defaultAddressStoreId, "");
					search.idle ();
				}
			}.start ();
		}
		catch (Exception x)
		{
			Log.logError ("plugin", "AddressQueryPane.initGUI", x.toString ());
		}
	}

	private void search (String addressStoreName, String searchText)
	{
		search.busy ();
		AddressClientService acs = (AddressClientService) Engine.instance ().getManager (AddressClientService.ID);
		addresses = acs.listAddresses (addressStoreName, searchText, 0, 100);
		SwingUtilities.invokeLater (new Runnable ()
		{
			public void run ()
			{
				((AddressTableModel) addressTable.getModel ()).fireTableDataChanged ();
			}
		});
		search.idle ();
	}

	@Override
	public void loadFromObject (IObject iObject)
	{
	}

	@Override
	public void storeToObject (IObject iObject)
	{
	}

	@Override
	public GUIPane cloneGUIPane ()
	{
		return new AddressQueryPane ();
	}
}
