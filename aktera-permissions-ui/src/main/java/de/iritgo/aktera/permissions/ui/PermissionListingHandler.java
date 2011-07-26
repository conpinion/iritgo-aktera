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

package de.iritgo.aktera.permissions.ui;


import java.sql.SQLException;
import java.util.Map;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.listing.CellData;
import de.iritgo.aktera.ui.listing.ColumnDescriptor;
import de.iritgo.aktera.ui.listing.ListingColumnViewer;
import de.iritgo.aktera.ui.listing.ListingDescriptor;
import de.iritgo.aktera.ui.listing.ListingHandler;
import de.iritgo.aktera.ui.listing.RowData;


/**
 * Handler for permission listings.
 */
public class PermissionListingHandler extends ListingHandler
{
	/** The permission manager */
	private PermissionManager permissionManager;

	public void setPermissionManager (PermissionManager permissionManager)
	{
		this.permissionManager = permissionManager;
	}

	/** Formular handler for specific permission groups */
	private Map<String, PermissionFormPart> permissionFormParts;

	public void setPermissionFormParts (Map<String, PermissionFormPart> permissionFormParts)
	{
		this.permissionFormParts = permissionFormParts;
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.ListingHandler#handleResult(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, de.iritgo.aktera.ui.listing.ListingDescriptor, de.iritgo.aktera.ui.listing.RowData, de.iritgo.aktera.ui.listing.ColumnDescriptor)
	 */
	@Override
	public CellData handleResult (ModelRequest request, ModelResponse response, ListingDescriptor listing,
					RowData data, ColumnDescriptor column) throws PersistenceException, ModelException, SQLException
	{
		String permissionType = data.getString ("permission");

		if ("permissionName".equals (column.getName ()))
		{
			String permissionName = permissionManager.getMetaDataById (permissionType).getName ();
			String[] nameParts = permissionName.split (":");
			String bundle = "Aktera";
			String name = nameParts[0];
			if (nameParts.length > 1)
			{
				bundle = nameParts[0];
				name = nameParts[1];
			}
			return new CellData (name, ListingColumnViewer.MESSAGE, bundle);
		}
		else if ("permissionInfo".equals (column.getName ()))
		{
			PermissionFormPart part = permissionFormParts.get (permissionType);
			if (part != null)
			{
				return new CellData (part.createListInfo (request, data), ListingColumnViewer.MESSAGE, part
								.getBundle ());
			}
		}
		return new CellData ();
	}
}
