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

package de.iritgo.aktera.base.tools;


import de.iritgo.aktera.comm.BinaryWrapper;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.download"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.download" id="aktera.download" logger="aktera"
 * @model.attribute name="forward" value="aktera.tools.data-download"
 */
public class Download extends SecurableStandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		try
		{
			Output out = res.createOutput("data");

			Configuration fileConfig = getConfiguration().getChild("file", false);

			if (fileConfig != null)
			{
				BinaryWrapper data = new BinaryWrapper(fileConfig.getAttribute("name"),
								fileConfig.getAttribute("type"), fileConfig.getAttribute("path"), 1024 * 1024, null);

				out.setContent(data);
			}

			res.add(out);
		}
		catch (ConfigurationException x)
		{
		}

		return res;
	}
}
