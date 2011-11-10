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


import de.iritgo.simplelife.string.StringTools;
import javax.swing.AbstractAction;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;


public class WebPageAction extends AbstractAction
{
	/** */
	private static final long serialVersionUID = 1L;

	private String homepage;

	public WebPageAction(String homepage)
	{
		super();
		this.homepage = homepage;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (StringTools.isTrimEmpty(homepage))
		{
			return;
		}

		try
		{
			Desktop.getDesktop().browse(new URI(homepage));
		}
		catch (Exception x)
		{
			System.err.println("Error starting web client: " + x);
		}
	}
}
