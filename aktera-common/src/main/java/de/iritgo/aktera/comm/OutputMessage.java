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

package de.iritgo.aktera.comm;


import de.iritgo.aktera.model.Output;
import java.io.Serializable;


/* Request input from the user */
public class OutputMessage extends AbstractMessageResponseElement implements Output, Serializable
{
	private Object content = null;

	public OutputMessage ()
	{
	}

	public OutputMessage (Output o)
	{
		super.copyFrom (o);
		setName (o.getName ());
		setContent (o.getContent ());
	}

	public synchronized void setContent (Object newContent)
	{
		//--- If the user supplied content is null, clear the internal content and return.
		if (newContent == null)
		{
			content = null;

			return;
		}

		//--- If the user supplied content is not serializable, throw an exception.
		if (! (newContent instanceof Serializable))
		{
			throw new IllegalArgumentException ("Content for Output '" + getName ()
							+ "' is not serializable. It is of type '" + newContent.getClass ().getName () + "'");
		}

		//--- Otherwise, save the new content.
		content = newContent;
	}

	public Object getContent ()
	{
		return content;
	}
}
