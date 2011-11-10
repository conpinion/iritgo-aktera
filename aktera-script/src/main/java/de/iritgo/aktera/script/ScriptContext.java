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

package de.iritgo.aktera.script;


import de.iritgo.aktera.PublicAPI;
import de.iritgo.aktera.logger.Logger;


/**
 * All script context classes must implement this interface.
 * It provides basic services to all script methods.
 *
 * A script context object is passed to all script methods.
 * In most cases this will be a derived interface, that defines
 * methods that are meaningful to the called script method.
 */
@PublicAPI
public interface ScriptContext
{
	/**
	 * Get the logger service.
	 */
	public Logger getLogger();

	/**
	 * Set the logger service.
	 */
	public void setLogger(Logger logger);

	/**
	 * Retrieve a service.
	 *
	 * @param name The service name
	 * @return The service
	 */
	public Object getService(String name) throws IllegalArgumentException;
}
