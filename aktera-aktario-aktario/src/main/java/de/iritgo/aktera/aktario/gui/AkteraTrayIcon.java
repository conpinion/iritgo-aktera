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

package de.iritgo.aktera.aktario.gui;


import javax.swing.JPopupMenu;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 *
 */
public class AkteraTrayIcon extends TrayIcon
{
	/** */
	protected JPopupMenu jPopupMenu;

	/**
	 * @param image
	 */
	public AkteraTrayIcon (Image image)
	{
		super (image);
		init ();
	}

	/**
	 * @param image
	 * @param tooltip
	 */
	public AkteraTrayIcon (Image image, String tooltip)
	{
		super (image, tooltip);
		init ();
	}

	/**
	 * @param image
	 * @param tooltip
	 * @param popup
	 */
	public AkteraTrayIcon (Image image, String tooltip, PopupMenu popup)
	{
		super (image, tooltip, popup);
		init ();
	}

	/**
	 * @param image
	 * @param tooltip
	 * @param popup
	 */
	public AkteraTrayIcon (Image image, String tooltip, JPopupMenu popup)
	{
		super (image, tooltip);
		setJPopupMenu (popup);
		init ();
	}

	/**
	 * @return
	 */
	public JPopupMenu getJPopupMenu ()
	{
		return jPopupMenu;
	}

	/**
	 * @param popupMenu
	 */
	public void setJPopupMenu (JPopupMenu popupMenu)
	{
		jPopupMenu = popupMenu;
	}

	protected void init ()
	{
		addMouseListener (new MouseAdapter ()
		{
			@Override
			public void mousePressed (MouseEvent e)
			{
				if (e.isPopupTrigger () && jPopupMenu != null)
				{
					jPopupMenu.setLocation (e.getX (), e.getY ());
					jPopupMenu.setInvoker (jPopupMenu);
					jPopupMenu.setVisible (true);
				}
			}

			@Override
			public void mouseReleased (MouseEvent e)
			{
				if (e.isPopupTrigger () && jPopupMenu != null)
				{
					jPopupMenu.setLocation (e.getX (), e.getY ());
					jPopupMenu.setInvoker (jPopupMenu);
					jPopupMenu.setVisible (true);
				}
			}
		});
	}
}
