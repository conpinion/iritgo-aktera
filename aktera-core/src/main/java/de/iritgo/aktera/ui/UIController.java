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

package de.iritgo.aktera.ui;


import de.iritgo.aktera.authorization.Security;


public interface UIController
{
	/** Default forward */
	public static String DEFAULT_FORWARD = "default";

	/**
	 * Execute the controller.
	 *
	 * @param request The ui request
	 * @param response The ui response
	 * @throws UIControllerException In case of an error
	 */
	public void execute (UIRequest request, UIResponse response) throws UIControllerException;

	/**
	 * Get the ressource bundle name.
	 *
	 * @return The ressource bundle name
	 */
	public String getBundle ();

	/**
	 * Get the name of the controller forward.
	 *
	 * @return The forward name
	 */
	public String getForward ();

	/**
	 * Redirect to another controller.
	 *
	 * @param bean The id of the controller bean
	 * @param request The controller request
	 * @param response The controller response
	 * @throws UIControllerException In case of an error
	 */
	public void redirect (String bean, UIRequest request, UIResponse response) throws UIControllerException;

	/**
	 * Retrieve the controller's security mode.
	 *
	 * @return The security mode
	 */
	public Security getSecurity ();
}
