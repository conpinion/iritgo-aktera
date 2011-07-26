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


import de.iritgo.aktera.persist.PersistenceException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


/**
 * This class encapsulates row data for listing handlers.
 */
public abstract class RowData
{
	/**
	 * The listing descriptor for which this data object was created.
	 */
	protected ListingDescriptor listing;

	/**
	 * Create a new RowData.
	 *
	 * @param listing The listing descriptor.
	 */
	public RowData (ListingDescriptor listing)
	{
		this.listing = listing;
	}

	/**
	 * Get a column value.
	 *
	 * @param columnName The name of the column (as specified by the column tag)
	 * @return The column value object
	 */
	public Object get (String columnName) throws PersistenceException
	{
		return null;
	}

	/**
	 * Get a column value as string.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public String getString (String columnName) throws PersistenceException
	{
		return null;
	}

	/**
	 * Get a column value as int.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public int getInt (String columnName) throws PersistenceException
	{
		return 0;
	}

	/**
	 * Get a column value as long.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public long getLong (String columnName) throws PersistenceException
	{
		return 0;
	}

	/**
	 * Get a column value as float.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public float getFloat (String columnName) throws PersistenceException
	{
		return 0;
	}

	/**
	 * Get a column value as time.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public Time getTime (String columnName) throws PersistenceException
	{
		return null;
	}

	/**
	 * Get a column value as date.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public Date getDate (String columnName) throws PersistenceException
	{
		return null;
	}

	/**
	 * Get a column value as timestamp.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	public Timestamp getTimestamp (String columnName) throws PersistenceException
	{
		return null;
	}
}
