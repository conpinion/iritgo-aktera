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


import java.util.Map;


/**
 * Give the user a navigation option. This allows the model to provide
 * navigation options to the UI. Note that implementations of this interface
 * should be serializable
 *
 *  @version      $Revision: 1.1 $  $Date: 2003/12/29 07:01:33 $
 */
public interface Command extends ResponseElement
{
	public void setParameter(String paramName, Object paramValue);

	public void setModel(String model);

	/**
	 * Return the name of the model specified for this command, in the form it would appear
	 * in the configuration file
	 */
	public String getModel();

	/**
	 * Return a map of any parameter specified for this command
	 */
	public Map getParameters();

	/**
	 * Convenience method, equivilant to execute(req, res, false, false)
	 */
	public ModelResponse execute(ModelRequest req, ModelResponse res) throws ModelException;

	/**
	 * Execute the model specified by this command and return the model
	 * response.
	 * @param req The previous request. The model being will have this request available via
	 * it's getPreviousRequest method
	 * @param res The current response. The new response will always include any errors stored in this response,
	 * but will not include any response elements already generated unless includeResponseElements is true
	 * @param includeParams Include request parameters from the old request when building the new request. Normally,
	 * a command has it's parameters set via "setParameter", just like a request, and these are the only parameters
	 * used in the new request. If this flag is true, however, any parameters that exist in the current request
	 * and that have not been specified via setParameter are populated from the current request.
	 * @param includeResponseElements Include any response elements from the current response (res) in the newly
	 * generated response. If an element in the new response has the same name as an element in the current response,
	 * use the new one only.
	 */
	public ModelResponse execute(ModelRequest req, ModelResponse res, boolean includeParams,
					boolean includeResponseElements) throws ModelException;

	public void setLabel(String newLabel);

	public String getLabel();

	public void setRelativeMovement(int numberOfSteps);

	public int getRelativeMovement();

	public void setBean(String bean);

	public String getBean();
}
