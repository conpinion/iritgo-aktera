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


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class EventHandlerTree
{
	private String eventId;

	private List<StandardEventHandler> handlers = new LinkedList<StandardEventHandler> ();

	private Map<String, EventHandlerTree> children = new HashMap<String, EventHandlerTree> ();

	public EventHandlerTree (String eventId)
	{
		this.eventId = eventId;
	}

	public EventHandlerTree ()
	{
		this.eventId = "";
	}

	public void insert (String eventId, StandardEventHandler handler)
	{
		String eventIdHead = eventId.split ("\\.")[0];
		String eventIdCons = eventId.substring (Math.min (eventId.length (), eventIdHead.length () + 1));

		EventHandlerTree node = children.get (eventIdHead);

		if (node == null)
		{
			node = new EventHandlerTree (eventIdHead);
			children.put (eventIdHead, node);
		}

		if (eventIdCons.length () != 0)
		{
			node.insert (eventIdCons, handler);

			return;
		}

		node.handlers.add (handler);
	}

	public EventHandlerTree getChild (String eventId)
	{
		return children.get (eventId);
	}

	public List<StandardEventHandler> getHandlers ()
	{
		return handlers;
	}
}
