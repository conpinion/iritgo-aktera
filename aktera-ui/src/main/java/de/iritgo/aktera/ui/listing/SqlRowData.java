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
import de.iritgo.simplelife.string.StringTools;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;


/**
 * This class encapsulates row data for listing handlers that was retrieved through
 * a sql result set.
 */
public class SqlRowData extends RowData
{
	/**
	 * The result set containing the row data.
	 */
	protected ResultSet resultSet;

	/**
	 * Create a new SqlRowData.
	 *
	 * @param listing The listing descriptor.
	 * @param resultSet The result set containing the row data.
	 */
	public SqlRowData(ListingDescriptor listing, ResultSet resultSet)
	{
		super(listing);
		this.resultSet = resultSet;
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.RowData#get(java.lang.String)
	 */
	@Override
	public Object get(String columnName) throws PersistenceException
	{
		try
		{
			return resultSet.getObject(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return StringTools.trim(resultSet.getObject(convertToSqlColumnName(columnName)));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return resultSet.getInt(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return resultSet.getLong(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return resultSet.getFloat(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return resultSet.getTime(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return resultSet.getDate(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
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
			return resultSet.getTimestamp(convertToSqlColumnName(columnName));
		}
		catch (SQLException x)
		{
			throw new PersistenceException("[SqlRowData] Error while fetching column '" + columnName + "': " + x);
		}
	}

	/**
	 * Get the sql column name for the specified column.
	 *
	 * @param columnName The column name as specified by the column tag.
	 * @return The sql column name.
	 * @throws PersistenceException If the column wasn't found.
	 */
	protected String convertToSqlColumnName(String columnName) throws PersistenceException
	{
		ColumnDescriptor column = listing.getColumn(columnName);

		if (column == null)
		{
			throw new PersistenceException("[SqlRowData] Unable to find column '" + columnName + "' in listing '"
							+ listing.getId() + " (" + listing.getTitle() + ")");
		}

		return column.getAs();
	}
}
