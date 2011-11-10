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

package de.iritgo.aktera.importer;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Iterator;


/**
 * XML utility methods.
 */
public class IterableNodeList implements Iterator<Node>, Iterable<Node>
{
	private NodeList list;

	private int i = 0;

	public IterableNodeList(NodeList list)
	{
		this.list = list;
	}

	public boolean hasNext()
	{
		return i < list.getLength();
	}

	public Node next()
	{
		return list.item(i++);
	}

	public void remove()
	{
	}

	public Iterator<Node> iterator()
	{
		return this;
	}
}
