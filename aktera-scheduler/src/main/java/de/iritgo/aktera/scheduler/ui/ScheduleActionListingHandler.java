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

package de.iritgo.aktera.scheduler.ui;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.listing.CellData;
import de.iritgo.aktera.ui.listing.ColumnDescriptor;
import de.iritgo.aktera.ui.listing.ListingColumnViewer;
import de.iritgo.aktera.ui.listing.ListingDescriptor;
import de.iritgo.aktera.ui.listing.ListingHandler;
import de.iritgo.aktera.ui.listing.RowData;
import java.sql.SQLException;


/**
 * Device function key listing handler.
 */
public class ScheduleActionListingHandler extends ListingHandler
{
	/** */
	private ScheduleActionFormPartManager scheduleActionFormPartManager;

	/**
	 * @param scheduleActionFormPartManager The new scheduleActionFormPartManager.
	 */
	public void setScheduleActionFormPartManager(ScheduleActionFormPartManager scheduleActionFormPartManager)
	{
		this.scheduleActionFormPartManager = scheduleActionFormPartManager;
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.ListingHandler#handleResult(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, de.iritgo.aktera.ui.listing.ListingDescriptor, de.iritgo.aktera.ui.listing.RowData, de.iritgo.aktera.ui.listing.ColumnDescriptor)
	 */
	@Override
	public CellData handleResult(ModelRequest request, ModelResponse res, ListingDescriptor listing, RowData data,
					ColumnDescriptor column) throws PersistenceException, ModelException, SQLException
	{
		String type = data.getString("action.type");
		ScheduleActionFormPartInfo partInfo = scheduleActionFormPartManager.getActionFormPart(type);
		String info = partInfo.getFormPart().createListInfo(request, data);

		if (info != null)
		{
			return new CellData(info, ListingColumnViewer.MESSAGE, partInfo.getBundle());
		}

		return new CellData(partInfo.getInfoKey(), ListingColumnViewer.MESSAGE, partInfo.getBundle());
	}
}
