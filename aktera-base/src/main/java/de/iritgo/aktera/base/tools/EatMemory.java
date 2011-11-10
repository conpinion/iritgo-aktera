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
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.simplelife.math.NumberTools;
import java.util.LinkedList;
import java.util.List;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.eat-memory"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.eat-memory" id="aktera.tools.eat-memory" logger="aktera"
 * @model.attribute name="forward" value="aktera.empty"
 */
public class EatMemory extends SecurableStandardLogEnabledModel
{
	private static List consumedMemory = new LinkedList();

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		int size = NumberTools.toInt(req.getParameter("size"), 10 * 1024 * 1024);

		consumedMemory.add(new byte[size]);

		return res;
	}
}
