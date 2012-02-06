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

package de.iritgo.aktera.models.util;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;


/**
 * Display all available configuration information
 * NOTE: It is *extremely important* that this model be correctly
 * secured! If necessary, you can remove it's role definition and
 * configuration (from roles.xconf and system.xconf) to ensure it cannot
 * be accessed in a production system.
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name=displayconfig
 * @x-avalon.lifestyle type=singleton
 *
 * @version                $Revision: 1.1 $  $Date: 2003/12/29 07:09:30 $
 * @author                Michael Nash
 */
public class DisplayConfig extends SecurableStandardLogEnabledModel implements Contextualizable, ThreadSafe
{
	private Context context = null;

	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		try
		{
			Configuration sysConfig = (Configuration) context.get("keel.config.system");
			Output sysOutput = res.createOutput("system");

			addChildren(res, sysOutput, sysConfig);
			res.add(sysOutput);

			Configuration logConfig = (Configuration) context.get("keel.config.log");
			Output logOutput = res.createOutput("log");

			addChildren(res, logOutput, logConfig);
			res.add(logOutput);

			Configuration roleConfig = (Configuration) context.get("keel.config.roles");
			Output roleOutput = res.createOutput("role");

			addChildren(res, roleOutput, roleConfig);
			res.add(roleOutput);

			Configuration instrConfig = (Configuration) context.get("keel.config.instr");
			Output instrOutput = res.createOutput("instrument");

			addChildren(res, instrOutput, instrConfig);
			res.add(instrOutput);
		}
		catch (ConfigurationException ce)
		{
			throw new ModelException(ce);
		}
		catch (ContextException e)
		{
			throw new ModelException(e);
		}

		return res;
	}

	private void addChildren(ModelResponse res, Output parent, Configuration conf)
		throws ModelException, ConfigurationException
	{
		Configuration[] children = conf.getChildren();
		Configuration oneChild = null;

		for (int i = 0; i < children.length; i++)
		{
			oneChild = children[i];

			Output o = res.createOutput(oneChild.getName());

			parent.add(o);

			String[] attrs = oneChild.getAttributeNames();
			String oneAttrName = null;

			for (int j = 0; j < attrs.length; j++)
			{
				oneAttrName = attrs[j];
				o.setAttribute(oneAttrName, oneChild.getAttribute(oneAttrName));
				o.setContent(oneChild.getValue(null));
			}

			addChildren(res, o, oneChild);
		}
	}

	/**
	 * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
	 */
	public void contextualize(Context context) throws ContextException
	{
		this.context = context;
	}
}
