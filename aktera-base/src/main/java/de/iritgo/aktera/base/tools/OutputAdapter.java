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

package de.iritgo.aktera.base.tools;


import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class OutputAdapter implements Output
{
	/** The name of this output element. */
	private String name;

	/** Attributes. */
	private Map attributes;

	/** Child elements. */
	private List elements;

	/**
	 * Create a new <code>OutputAdapter</code>.
	 *
	 * @param name The name of this output element.
	 */
	public OutputAdapter (String name)
	{
		this.name = name;
		attributes = new HashMap ();
		elements = new LinkedList ();
	}

	public void setContent (Object newContent)
	{
	}

	public Object getContent ()
	{
		return toString ();
	}

	public void add (ResponseElement re)
	{
	}

	public void remove (ResponseElement re)
	{
	}

	public void setAttribute (String key, Object value)
	{
	}

	public Object getAttribute (String key)
	{
		return null;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public List getAll ()
	{
		return elements;
	}

	public Map getAttributes ()
	{
		return attributes;
	}

	public void removeAttribute (String key)
	{
	}
}
