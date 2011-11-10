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


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.apache.commons.beanutils.PropertyUtils;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


/**
 * This class encapsulates row data for listing handlers that was retrieved through
 * a sql result set.
 */
public class BeanRowData extends RowData
{
	/**
	 * The row data.
	 */
	protected Object data;

	/**
	 * Create a new BeanRowData.
	 *
	 * @param listing The listing descriptor.
	 * @param data The bean containing the row data.
	 */
	public BeanRowData(ListingDescriptor listing, Object data)
	{
		super(listing);
		this.data = data;
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.RowData#get(java.lang.String)
	 */
	@Override
	public Object get(String columnName) throws PersistenceException
	{
		try
		{
			return PropertyUtils.getProperty(data, columnName);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as string.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public String getString(String columnName) throws PersistenceException
	{
		try
		{
			return StringTools.trim(PropertyUtils.getProperty(data, columnName));
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as int.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public int getInt(String columnName) throws PersistenceException
	{
		try
		{
			return NumberTools.toInt(PropertyUtils.getProperty(data, columnName), 0);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as long.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public long getLong(String columnName) throws PersistenceException
	{
		try
		{
			return NumberTools.toLong(PropertyUtils.getProperty(data, columnName), 0);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as flaot.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public float getFloat(String columnName) throws PersistenceException
	{
		try
		{
			return NumberTools.toFloat(PropertyUtils.getProperty(data, columnName), 0);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as time.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public Time getTime(String columnName) throws PersistenceException
	{
		try
		{
			return (Time) PropertyUtils.getProperty(data, columnName);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as date.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public Date getDate(String columnName) throws PersistenceException
	{
		try
		{
			return (Date) PropertyUtils.getProperty(data, columnName);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get a column value as timestamp.
	 *
	 * @param columnName The name of the column (as specified by the column tag).
	 * @return The column value.
	 * @throws PersistenceException If an unknown column name was specified or
	 *   an error occurred during data retrieval.
	 */
	@Override
	public Timestamp getTimestamp(String columnName) throws PersistenceException
	{
		try
		{
			return (Timestamp) PropertyUtils.getProperty(data, columnName);
		}
		catch (Exception x)
		{
			throw new PersistenceException("[BeanRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}
}
