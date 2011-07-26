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

package de.iritgo.aktera.model;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * A component that validates a ModelRequest before the specified
 * Model actually executes.
 *
 * @author Michael Nash
 */
public interface ModelValidator
{
	/**
	 * Role string used to look up this component
	 */
	public final static String ROLE = "de.iritgo.aktera.model.ModelValidator";

	/**
	 * Validate the request parameters about to be passed to the specified
	 * model. This method is called *before* the model is executed.
	 * @param req The ModelRequest with the parameters to be validated
	 * @param theModel The model about to be executed
	 * @return Null if all validations are acceptable, or a Command
	 * indicating a re-direct to some other Model. This method can
	 * also return null if some validations fail, but if a redirect is
	 * not requested the errors are simply accumulated with the ModelRequest.
	 * @throws ConfigurationException In the event the configuration for this validator is incorrect
	 * @throws ModelException If there is something seriously wrong with the ModelRequest or the Model itself
	 */
	public Command validate (ModelRequest req, Model theModel) throws ConfigurationException, ModelException;
}
