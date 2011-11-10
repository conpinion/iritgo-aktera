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

package de.iritgo.aktera.base.navigation;


import de.iritgo.aktera.model.ModelRequest;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


/**
 * Instances of this class are used to describe and store the call-hierarchy
 * (i.e. the history of model requests) of the application.
 */
public class CallHierarchy
{
	/** The unique call id. */
	private UUID cid;

	/** The model request. */
	private ModelRequest req;

	/** The parent call hierarchy item. */
	private CallHierarchy parent;

	/** The child calls. */
	private List children;

	/**
	 * Create a new CallHierarchy instance.
	 *
	 * @param cid The uuid of the call.
	 * @param req The model request.
	 */
	public CallHierarchy(UUID cid, ModelRequest req)
	{
		children = new LinkedList();
		this.cid = cid;
		this.req = req;
	}

	/**
	 * Add a call hierarchy item to this one.
	 *
	 * @param child The child to add.
	 */
	public void add(CallHierarchy child)
	{
		child.setParent(this);
		children.add(child);
	}

	/**
	 * Set the parent call hierarchy item.
	 *
	 * @param parent The new parent.
	 */
	public void setParent(CallHierarchy parent)
	{
		this.parent = parent;
	}

	/**
	 * Get the parent call hierarchy item.
	 *
	 * @return The parent.
	 */
	public CallHierarchy getParent()
	{
		return parent;
	}

	/**
	 * Get the cid.
	 *
	 * @return The cid.
	 */
	public UUID getCid()
	{
		return cid;
	}

	/**
	 * Get the model request.
	 *
	 * @return The model request.
	 */
	public ModelRequest getModelRequest()
	{
		return req;
	}

	/**
	 * Dump the call hierarchy.
	 *
	 * @param out The print writer to dump to.
	 * @param indent The indentation level.
	 */
	public void dump(PrintWriter out, int indent)
	{
		for (int i = 0; i < indent; ++i)
		{
			out.print('\t');
		}

		out.println(cid + " " + req.getModel());

		for (Iterator i = children.iterator(); i.hasNext();)
		{
			CallHierarchy ch = (CallHierarchy) i.next();

			ch.dump(out, indent + 1);
		}
	}
}
