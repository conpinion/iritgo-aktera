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


/**
 * List handlers must return an instance of this class
 * that provides the actual data from that the listing is
 * created.
 */
public class ListFiller
{
	/**
	 * Return the total number of rows. If the underlying data access
	 * is unable to use a paged retrieval, use the default implementation
	 * which returns -1.
	 *
	 * @return The total row count
	 */
	public long getTotalRowCount()
	{
		return - 1;
	}

	/**
	 * Return the number of rows.
	 *
	 * @return The row count
	 */
	public int getRowCount()
	{
		return 0;
	}

	/**
	 * Go to the next result row.
	 *
	 * @return True if there was another row
	 */
	public boolean next()
	{
		return false;
	}

	/**
	 * Get the value at the current row and the specified
	 * column.
	 *
	 * @param column The column to retrieve
	 * @return The value
	 */
	public Object getValue(String column)
	{
		return null;
	}

	/**
	 * Get the id of the current row.
	 *
	 * @return The current row's id
	 */
	public Object getId()
	{
		return "0";
	}
}
