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
import java.io.Serializable;
import java.util.Properties;


public class Event implements Serializable, Cloneable
{
	/** Serial version id. */
	private static final long serialVersionUID = 1L;

	/** Event id. */
	private String eventId;

	public String getEventId()
	{
		return eventId;
	}

	public void setEventId(String eventId)
	{
		this.eventId = eventId;
	}

	/** Additional event properties. */
	private Properties properties;

	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	public Properties getProperties()
	{
		return properties;
	}

	/** A model request. */
	private ModelRequest request;

	public void setRequest(ModelRequest request)
	{
		this.request = request;
	}

	public ModelRequest getRequest()
	{
		return request;
	}

	/** A logger. */
	private Logger logger;

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	public Logger getLogger()
	{
		return logger;
	}

	/**
	 * Create a new event.
	 *
	 * @param eventId The event id.
	 */
	public Event(String eventId)
	{
		this.eventId = eventId;
	}

	/**
	 * Clone this event.
	 *
	 * @see java.lang.Object#clone()
	 */
	public Event clone()
	{
		try
		{
			return (Event) super.clone();
		}
		catch (CloneNotSupportedException x)
		{
			return null;
		}
	}
}
