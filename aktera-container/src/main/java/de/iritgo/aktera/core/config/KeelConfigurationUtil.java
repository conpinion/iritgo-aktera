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

package de.iritgo.aktera.core.config;


import org.apache.avalon.framework.configuration.Configuration;


/**
 * @author root
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class KeelConfigurationUtil
{
	//--- Do not allow this to be instantiated since it is a Singleton.
	private KeelConfigurationUtil ()
	{
		super ();
	}

	/**
	 * Returns a simple string representation of the the supplied configuration.
	 * @param config a configuration
	 * @return a simplified text representation of a configuration suitable
	 *     for debugging
	 */
	public static String list (Configuration config, String comment)
	{
		final StringBuffer buffer = new StringBuffer ();

		buffer.append ("\n<!-- " + comment + "-->\n\n");
		list (buffer, "  ", config);
		buffer.append ("\n");

		return buffer.toString ();
	}

	private static void list (StringBuffer buffer, String lead, Configuration config)
	{
		buffer.append ("\n" + lead + "<" + config.getName ());

		String[] names = config.getAttributeNames ();

		if (names.length > 0)
		{
			for (int i = 0; i < names.length; i++)
			{
				buffer.append (" " + names[i] + "=\"" + config.getAttribute (names[i], "???") + "\"");
			}
		}

		Configuration[] children = config.getChildren ();

		if (children.length > 0)
		{
			buffer.append (">");

			for (int j = 0; j < children.length; j++)
			{
				list (buffer, lead + "  ", children[j]);
			}

			buffer.append ("\n" + lead + "</" + config.getName () + ">");
		}
		else
		{
			if (config.getValue (null) != null)
			{
				buffer.append (">" + config.getValue ("???") + "</" + config.getName () + ">");
			}
			else
			{
				buffer.append ("/>");
			}
		}
	}
}
