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

package de.iritgo.aktera.address;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import de.iritgo.aktario.core.gui.IAction;
import de.iritgo.aktario.core.gui.IMenuItem;
import de.iritgo.aktario.core.gui.IMenuItemProvider;
import de.iritgo.aktario.core.gui.ITableColumn;
import de.iritgo.aktario.core.io.i18n.I18NString;
import de.iritgo.aktario.core.plugin.PluginStateEvent;
import de.iritgo.aktario.framework.IritgoEngine;
import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktario.framework.dataobject.DataObjectTools;
import de.iritgo.aktera.address.gui.AddressActionColumn;
import de.iritgo.aktera.address.gui.AddressViewColumn;
import de.iritgo.aktera.address.gui.EMailAction;
import de.iritgo.aktera.address.gui.WebPageAction;
import de.iritgo.aktera.address.wsclient.Address;
import de.iritgo.simplelife.string.StringTools;


/**
 * This manager provides address client services.
 */
public class AddressClientManager extends AddressAktarioManager
{
	public static final String ID = "de.iritgo.aktera.address.AddressClientManager";

	private ImageIcon emailIcon = new ImageIcon (AddressClientManager.class.getResource ("/resources/email-16.png"));

	private ImageIcon webIcon = new ImageIcon (AddressClientManager.class.getResource ("/resources/world-16.png"));

	private I18NString emailLabel = new I18NString ("email");

	private I18NString homepageLabel = new I18NString ("homepage");

	/**
	 * Extension point for adding columns that are to be displayed in the
	 * address table.
	 */
	private List<ITableColumn> addressTableColumns = new ArrayList ();

	/** Extension point for adding items to the address action popup menu */
	private List<IMenuItemProvider> addressTableActionMenuItemProviders = new ArrayList ();

	public AddressClientManager ()
	{
		super (ID);
	}

	public List<ITableColumn> getAddressTableColumns ()
	{
		return addressTableColumns;
	}

	public void addAddressTableColumn (ITableColumn column)
	{
		addressTableColumns.add (column);
	}

	public List<IMenuItemProvider> getAddressTableActionMenuItemProviders ()
	{
		return addressTableActionMenuItemProviders;
	}

	public void addAddressTableActionMenuItemProvider (int index, IMenuItemProvider provider)
	{
		addressTableActionMenuItemProviders.add (index, provider);
	}

	public void addAddressTableActionMenuItemProvider (IMenuItemProvider provider)
	{
		addressTableActionMenuItemProviders.add (provider);
	}

	/** Extension point for adding actions to a phone number field */
	private List<IAction> phoneNumberFieldActions = new ArrayList ();

	public List<IAction> getPhoneNumberFieldActions ()
	{
		return phoneNumberFieldActions;
	}

	public void addPhoneNumberFieldAction (IAction action)
	{
		phoneNumberFieldActions.add (action);
	}

	/**
	 * @see de.iritgo.aktera.address.AddressAktarioManager#init()
	 */
	@Override
	public void init ()
	{
		if (! IritgoEngine.instance ().isServer ())
		{
			Properties props = new Properties ();

			props.setProperty ("command", "aktera-client.showEmbeddedAddress");
			props.setProperty ("name", "showEmbeddedAddress");
			CommandTools.performSimple ("aktario-xmlrpc.AddXmlRpcCommand", props);

			addAddressTableColumn (new ITableColumn ().withTitle ("id"));
			addAddressTableColumn (new ITableColumn ().withTitle ("name"));
			addAddressTableColumn (new ITableColumn ().withTitle ("firstName"));
			addAddressTableColumn (new ITableColumn ().withTitle ("company"));
			addAddressTableColumn (new AddressViewColumn ());
			addAddressTableColumn (new AddressActionColumn ());

			addAddressTableActionMenuItemProvider (new IMenuItemProvider ()
			{
				@Override
				public IMenuItem create (Object... params)
				{
					Address address = (Address) params[0];
					IMenuItem menuItem = new IMenuItem ("<html><span style=\"width:8em\"><b>"
									+ (StringTools.isTrimEmpty (address.getEmail ()) ? "-" : address.getEmail ())
									+ "</b></span> (" + emailLabel.get () + ")</html>", (Icon) emailIcon);

					menuItem.addActionListener (new EMailAction (address.getEmail ()));

					return menuItem;
				}
			});
			addAddressTableActionMenuItemProvider (new IMenuItemProvider ()
			{
				@Override
				public IMenuItem create (Object... params)
				{
					Address address = (Address) params[0];
					IMenuItem menuItem = new IMenuItem ("<html><span style=\"width:8em\"><b>"
									+ (StringTools.isTrimEmpty (address.getHomepage ()) ? "-" : address.getHomepage ())
									+ "</b></span> (" + homepageLabel.get () + ")</html>", (Icon) webIcon);

					menuItem.addActionListener (new WebPageAction (address.getHomepage ()));

					return menuItem;
				}
			});
		}
	}

	/**
	 * @see de.iritgo.aktera.address.AddressAktarioManager#pluginEvent(de.iritgo.aktario.core.plugin.PluginStateEvent)
	 */
	@Override
	public void pluginEvent (PluginStateEvent event)
	{
		if (event.allPluginsInitialized ())
		{
			DataObjectTools.registerOnStartupDynDataObject (address);
		}
	}
}
