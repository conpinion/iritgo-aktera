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

package de.iritgo.aktera.persist;


import de.iritgo.aktera.core.container.KeelContainer;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.service.ServiceException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * This data source encapsulates the default data source component of the keel
 * container.
 */
public class KeelDataSource implements DataSource
{
	/** A log writer */
	private PrintWriter logWriter = new PrintWriter(System.out);

	/** Login timeout */
	private int loginTimeout;

	/** The Keel data source */
	private DataSourceComponent dataSourceComponent;

	/**
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException
	{
		return getKeelConnection();
	}

	/**
	 * @see javax.sql.DataSource#getConnection(java.lang.String,
	 *      java.lang.String)
	 */
	public Connection getConnection(String username, String password) throws SQLException
	{
		return getKeelConnection();
	}

	/**
	 * Get a connection from the default Keel data source component.
	 *
	 * @return A SQL connection to the default data source.
	 * @throws SQLException
	 */
	private Connection getKeelConnection() throws SQLException
	{
		if (dataSourceComponent == null)
		{
			try
			{
				dataSourceComponent = (DataSourceComponent) KeelContainer.defaultContainer().getService(
								DataSourceComponent.ROLE, "keel-dbpool");
			}
			catch (ServiceException x)
			{
				throw new SQLException("Unable to retrieve the Keel data source component");
			}
		}

		return dataSourceComponent.getConnection();
	}

	/**
	 * Free all allocated resources.
	 */
	public void dispose()
	{
		KeelContainer.defaultContainer().release(dataSourceComponent);
		dataSourceComponent = null;
	}

	/**
	 * @see javax.sql.DataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException
	{
		return logWriter;
	}

	/**
	 * @see javax.sql.DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException
	{
		return loginTimeout;
	}

	/**
	 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter out) throws SQLException
	{
		this.logWriter = out;
	}

	/**
	 * @see javax.sql.DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int seconds) throws SQLException
	{
		this.loginTimeout = seconds;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		return null;
	}
}
