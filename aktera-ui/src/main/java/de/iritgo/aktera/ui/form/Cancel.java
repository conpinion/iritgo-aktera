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

package de.iritgo.aktera.ui.form;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.tools.ModelTools;
import java.util.Map;
import java.util.Properties;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.cancel"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.cancel" id="aktera.cancel" logger="aktera"
 */
public class Cancel extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		Properties props = new Properties ();

		for (Object entry : req.getParameters ().entrySet ())
		{
			String key = (String) ((Map.Entry) entry).getKey ();

			if (key.startsWith ("_cp") && ! "_cmodel".equals (key))
			{
				props.put (key.substring (3), ((Map.Entry) entry).getValue ());
			}
		}

		ModelRequest newReq = (ModelRequest) req.getService (ModelRequest.ROLE);

		return ModelTools.callModel (newReq, req.getParameterAsString ("_cmodel"), props);
	}
}
