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

package de.iritgo.aktera.journal;


import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.framework.client.Client;
import de.iritgo.aktario.framework.dataobject.gui.ExtensionTile;
import de.iritgo.aktera.journal.gui.JournalDoubleClickExtension;
import de.iritgo.aktera.journal.gui.JournalExecutionExtension;
import de.iritgo.aktera.journal.gui.JournalQueryCellRenderer;


/**
 *
 */
public class JournalClientManager extends BaseObject implements Manager
{
	/** */
	private Map<String, ImageIcon> journalPrimaryTypeIcons = new HashMap ();

	/** */
	private ImageIcon defaultJournalPrimaryTypeIcon;

	/** */
	private Map<String, ImageIcon> journalSecondaryTypeIcons = new HashMap ();

	/** */
	private ImageIcon defaultJournalSecondaryTypeIcon;

	/**
	 * Create a new client manager.
	 */
	public JournalClientManager ()
	{
		super ("de.iritgo.aktera.journal.JournalClientManager");
	}

	public void init ()
	{
		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.COLUMN, new JournalQueryCellRenderer (JournalQueryCellRenderer.PRIMRARY_TYPE_ID));

		Client.instance ().getGUIExtensionManager ()
						.registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal", ExtensionTile.COLUMN,
										new JournalQueryCellRenderer (JournalQueryCellRenderer.SECONDARY_TYPE_ID));

		//		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
		//						ExtensionTile.COLUMN,
		//						new JournalQueryCellRenderer (JournalQueryCellRenderer.SECONDARY_TYPE_TEXT_ID));
		//
		//		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
		//						ExtensionTile.COLUMN, new JournalQueryCellRenderer (JournalQueryCellRenderer.MESSAGE_ID));
		//
		//		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
		//						ExtensionTile.COLUMN, new JournalQueryCellRenderer (JournalQueryCellRenderer.OCCURREDAT_ID));
		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.COLUMN, new JournalQueryCellRenderer ("journal_id"));

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.COLUMN, new JournalExecutionExtension ());

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.LIST_COMMAND, new JournalDoubleClickExtension ("journal_occurredAt"));

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.LIST_COMMAND, new JournalDoubleClickExtension ("journal_primaryType"));

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.LIST_COMMAND, new JournalDoubleClickExtension ("journal_secondaryType"));

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.LIST_COMMAND, new JournalDoubleClickExtension ("journal_secondaryTypeText"));

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.LIST_COMMAND, new JournalDoubleClickExtension ("journal_message"));

		Client.instance ().getGUIExtensionManager ().registerExtension ("de.iritgo.aktera.journal.EmbeddedJournal",
						ExtensionTile.LIST_COMMAND, new JournalExecutionExtension ());

		defaultJournalPrimaryTypeIcon = new ImageIcon (JournalClientManager.class
						.getResource ("/resources/journal-16.png"));
		defaultJournalSecondaryTypeIcon = new ImageIcon (JournalClientManager.class
						.getResource ("/resources/journal-16.png"));
	}

	/**
	 * Free all client manager resources.
	 */
	public void unload ()
	{
	}

	/**
	 * Set a primary type journal icon.
	 *
	 * @param primaryType
	 *            The primary type string
	 * @param icon
	 *            The image icon
	 */
	public void setJournalPrimaryTypeIcon (String primaryType, ImageIcon icon)
	{
		journalPrimaryTypeIcons.put (primaryType, icon);
	}

	/**
	 * Get a primary type journal icon.
	 *
	 * @param primaryType
	 *            The primary type string
	 * @return The image icon or the default icon if none was found for the
	 *         specified type
	 */
	public ImageIcon getJournalPrimaryTypeIcon (String primaryType)
	{
		ImageIcon icon = journalPrimaryTypeIcons.get (primaryType);

		if (icon != null)
		{
			return icon;
		}

		return defaultJournalPrimaryTypeIcon;
	}

	/**
	 * Set a secondary type journal icon.
	 *
	 * @param secondaryType
	 *            The secondary type string
	 * @param icon
	 *            The image icon
	 */
	public void setJournalSecondaryTypeIcon (String secondaryType, ImageIcon icon)
	{
		journalSecondaryTypeIcons.put (secondaryType, icon);
	}

	/**
	 * Get a secondary type journal icon.
	 *
	 * @param secondaryType
	 *            The secondary type string
	 * @return The image icon or the default icon if none was found for the
	 *         specified type
	 */
	public ImageIcon getJournalSecondaryTypeIcon (String secondaryType)
	{
		ImageIcon icon = journalSecondaryTypeIcons.get (secondaryType);

		if (icon != null)
		{
			return icon;
		}

		return defaultJournalSecondaryTypeIcon;
	}
}
