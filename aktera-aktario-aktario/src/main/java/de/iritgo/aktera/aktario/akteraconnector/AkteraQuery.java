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

package de.iritgo.aktera.aktario.akteraconnector;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.iobject.IObjectList;
import de.iritgo.aktario.framework.appcontext.AppContext;
import de.iritgo.aktario.framework.dataobject.AbstractQuery;
import de.iritgo.aktario.framework.user.User;


/**
 *
 */
public class AkteraQuery extends AbstractQuery
{
	/**
	 * Create aktera query.
	 */
	public AkteraQuery()
	{
		super("AkteraQuery");

		addAttribute("model", "none");
		addAttribute("listName", "none");
		addAttribute("listSearchCategory", "");
		addAttribute("listSearchValues", "");

		User user = AppContext.instance().getUser();

		if (user != null)
		{
			setUserUniqueId(user.getUniqueId());
		}
	}

	/**
	 * Create aktera query.
	 */
	public AkteraQuery(String model, String listName)
	{
		this();
		setAttribute("model", model);
		setAttribute("listName", listName);
		setAttribute("dataObjectTypeId", model);
	}

	/**
	 * Create aktera query.
	 */
	public AkteraQuery(String model, String listName, String listSearchCategory)
	{
		this();
		setAttribute("model", model);
		setAttribute("listName", listName);
		setAttribute("dataObjectTypeId", model);
		setAttribute("listSearchCategory", listSearchCategory);
	}

	public String getListSearchCategory()
	{
		return getStringAttribute("listSearchCategory");
	}

	public String getListSearchValues()
	{
		return getStringAttribute("listSearchValues");
	}

	public void setListSearchValues(String listSearchValues)
	{
		setAttribute("listSearchValues", listSearchValues);
	}

	public void refresh()
	{
		IObjectList results = (IObjectList) getIObjectListResults();

		results.clearIObjectList();
		doQuery();
	}

	/**
	 * Do a aktera keel query.
	 */
	public void doQuery()
	{
		String model = getStringAttribute("model");
		String listName = getStringAttribute("listName");

		ConnectorServerManager connectorServerManager = (ConnectorServerManager) Engine.instance().getManagerRegistry()
						.getManager("ConnectorServerManager");

		connectorServerManager.doQuery(model, listName, getUserUniqueId(), this, getSearchCondition(),
						getListSearchCategory());
	}
}
