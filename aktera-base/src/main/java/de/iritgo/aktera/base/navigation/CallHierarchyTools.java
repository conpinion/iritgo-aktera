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
import de.iritgo.aktera.ui.tools.UserTools;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Utility methods to manage the model call hierarchy (i.e. handling of
 * model navigation and 'back'-like-buttons.
 */
public class CallHierarchyTools
{
	/**
	 * Store a model request as the child of the last model call
	 * (specified by the request parameter 'cid' (call id)).
	 *
	 * @param req The model request.
	 * @return The uuid of the new request.
	 */
	public static UUID storeModelRequest (ModelRequest req)
	{
		UUID parentCid = null;

		try
		{
			parentCid = UUID.fromString (req.getParameterAsString ("cid"));
		}
		catch (IllegalArgumentException x)
		{
		}

		UUID newCid = UUID.randomUUID ();

		CallHierarchy ch = new CallHierarchy (newCid, req);

		Map callHierarchy = (Map) UserTools.getContextObject (req, "callHierarchy");

		if (callHierarchy == null)
		{
			callHierarchy = new HashMap ();
			UserTools.setContextObject (req, "callHierarchy", callHierarchy);
		}

		callHierarchy.put (newCid, ch);

		if (parentCid != null && callHierarchy.get (parentCid) != null)
		{
			CallHierarchy parentCh = (CallHierarchy) callHierarchy.get (parentCid);

			parentCh.add (ch);
		}

		return newCid;
	}

	/**
	 * Dump the call hierarchy tree.
	 */
	public static void dumpCallHierarchy (ModelRequest req)
	{
		//		CallHierarchy root = (CallHierarchy) UserTools.getContextObject (req, "callHierarchy");
		//		root.dump (System.out, 0);
	}
}
