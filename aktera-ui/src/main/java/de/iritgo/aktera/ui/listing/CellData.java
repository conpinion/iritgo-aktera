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

package de.iritgo.aktera.ui.listing;


import lombok.Data;


/**
 * This class encapsulates the data to be displayed in a table cell.
 */
@Data
public class CellData
{
	protected Object value;

	protected ListingColumnViewer viewer;

	protected String bundle;

	public CellData ()
	{
		this.value = "";
		this.viewer = ListingColumnViewer.TEXT;
		this.bundle = "Aktera";
	}

	public CellData (Object value)
	{
		this.value = value;
	}

	public CellData (Object value, ListingColumnViewer viewer)
	{
		this.value = value;
		this.viewer = viewer;
	}

	public CellData (Object value, ListingColumnViewer viewer, String bundle)
	{
		this.value = value;
		this.viewer = viewer;
		this.bundle = bundle;
	}
}
