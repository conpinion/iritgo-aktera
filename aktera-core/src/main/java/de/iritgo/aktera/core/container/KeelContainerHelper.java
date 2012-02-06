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

package de.iritgo.aktera.core.container;


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;


/**
 * @author Schatterjee
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class KeelContainerHelper
{
	public static final int CONFIG_TYPE_ROLE = 1;

	public static final int CONFIG_TYPE_SYSTEM = 2;

	public static final int CONFIG_TYPE_INSTRUMENTATION = 3;

	public static final int CONFIG_TYPE_LOG = 4;

	public static Configuration getConfiguration(Context context, final int type)
	{
		Configuration configuration = null;

		if (context == null)
		{
			configuration = new DefaultConfiguration("empty");
		}
		else
		{
			try
			{
				switch (type)
				{
					case CONFIG_TYPE_ROLE:
						configuration = (Configuration) context.get("keel.config.roles");

						break;

					case CONFIG_TYPE_SYSTEM:
						configuration = (Configuration) context.get("keel.config.system");

						break;

					case CONFIG_TYPE_INSTRUMENTATION:
						configuration = (Configuration) context.get("keel.config.instr");

						break;

					case CONFIG_TYPE_LOG:
						configuration = (Configuration) context.get("keel.config.log");

						break;

					default:
						configuration = new DefaultConfiguration("empty");
				}
			}
			catch (ContextException e)
			{
				throw new RuntimeException("Error geting configuration from context:" + e.getMessage());
			}
		}

		return configuration;
	}

	/**
	 * @param shortHand
	 * @return
	 */
	public static Class getClassForShortHand(String shortHand)
	{
		return null;
	}
}
