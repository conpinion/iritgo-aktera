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


import de.iritgo.aktera.model.ModelRequest;
import org.apache.avalon.framework.logger.Logger;
import java.util.Properties;


/**
 * The event manager handles the registration of events and their
 * dispatching to event handlers when they are fired.
 */
public interface EventManager
{
	/** Service id */
	public static final String ID = "de.iritgo.aktera.event.EventManager";

	/**
	 * Fire an event.
	 *
	 * @param eventId The event id.
	 */
	public void fire (String eventId);

	/**
	 * Fire an event.
	 *
	 * @param eventId The event id.
	 * @param properties Additional event properties.
	 */
	public void fire (String eventId, Properties properties);

	/**
	 * Fire an event.
	 *
	 * @param eventId The event id.
	 * @param req A model request.
	 */
	public void fire (String eventId, ModelRequest req);

	/**
	 * Fire an event.
	 *
	 * @param eventId The event id.
	 * @param req A model request.
	 * @param log A logger.
	 */
	public void fire (String eventId, ModelRequest req, Logger log);

	/**
	 * Fire an event.
	 *
	 * @param eventId The event id.
	 * @param req A model request.
	 * @param properties Additional event properties.
	 */
	public void fire (String eventId, ModelRequest req, Properties properties);

	/**
	 * Fire an event.
	 *
	 * @param eventId The event id.
	 * @param req A model request.
	 * @param log A logger.
	 * @param properties Additional event properties.
	 */
	public void fire (String eventId, ModelRequest req, Logger log, Properties properties);
}
