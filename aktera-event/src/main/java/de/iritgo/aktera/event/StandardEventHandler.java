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

package de.iritgo.aktera.event;


import org.apache.avalon.framework.logger.Logger;


/**
 * For event handlers using a catch all handle method.
 */
public interface StandardEventHandler extends EventHandler
{
	/**
	 * Event handler method.
	 *
	 * @param event The event
	 */
	public void handle (Event event);

	/**
	 * Set the id of the handled event.
	 *
	 * @param event The new event id
	 */
	public void setEvent (String event);

	/**
	 * Get the id of the handled event.
	 *
	 * @return The event id
	 */
	public String getEvent ();

	/**
	 * Set the logger.
	 *
	 * @param logger The logger
	 */
	public void setLogger (Logger logger);

	/**
	 * Get the logger.
	 *
	 * @return The logger
	 */
	public Logger getLogger ();
}
