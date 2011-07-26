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


import de.iritgo.aktario.core.gui.ITableColumn;
import de.iritgo.aktera.address.AddressClientManager;
import de.iritgo.aktera.address.wsclient.Address;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import java.awt.event.MouseEvent;


public class AddressViewColumn extends ITableColumn
{
	public AddressViewColumn ()
	{
		setCellIcon (new ImageIcon (AddressClientManager.class.getResource ("/resources/find-12.png")));
	}

	@Override
	public void onCellClicked (Object item, JTable addressTable, MouseEvent e)
	{
		AddressViewDialog dialog = new AddressViewDialog ();
		Address address = (Address) item;

		dialog.show (address.getStoreId (), address.getId ());
	}
}
