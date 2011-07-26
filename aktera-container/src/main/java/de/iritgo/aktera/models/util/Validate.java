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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * General-purpose "validate" model. Configuration can specify a
 * validator for this Model (or just use the default validator),
 * and it will perform validation, then pass on all of it's
 * request parameters to the model specified in it's configuration
 * via the "model" element.
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name=validate
 * @x-avalon.lifestyle type=singleton
 *
 * @version                $Revision: 1.1 $  $Date: 2004/03/07 19:38:56 $
 * @author                Michael Nash
 */
public class Validate extends SecurableStandardLogEnabledModel
{
	/**
	 * Simply execute the model specified in configuration - all
	 * validation is done by the time this method begins.
	 *
	 * @param req The usual ModelRequest
	 * @return a ModelResponse from the specified model
	 * @throws ModelException If the model is not configured correctly
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		try
		{
			Command c = res.createCommand (getConfiguration ().getChild ("model").getValue ());

			res.addErrors (req.getErrors ());

			return c.execute (req, res, true, true);
		}
		catch (ConfigurationException ce)
		{
			throw new ModelException (ce);
		}
	}
}
