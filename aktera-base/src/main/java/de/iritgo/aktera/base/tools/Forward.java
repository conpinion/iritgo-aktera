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


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.forward"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.forward" id="aktera.forward" logger="aktera"
 * @model.attribute name="forward" value="aktera.index"
 */
public class Forward extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param request The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest request) throws ModelException
	{
		ModelResponse response = request.createResponse ();

		return response;
	}
}
