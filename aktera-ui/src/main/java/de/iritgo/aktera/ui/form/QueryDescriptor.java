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

package de.iritgo.aktera.ui.form;


import java.util.Properties;


/**
 * An instance of class QueryDescriptor describes a database query.
 */
public class QueryDescriptor
{
	/** The query name, in case of predefined queries */
	private String name;

	/** The count query name, in case of predefined queries */
	private String countName;

	/** The query string, in case of implicit queries */
	private String query;

	/** The count query string, in case of implicit queries */
	private String countQuery;

	/** Query parameters */
	private Properties params = new Properties ();

	/** A query can also call a DAO method */
	private String daoName;

	/** A query can also call a DAO method */
	private String daoMethodName;

	/**
	 * Get the query name.
	 *
	 * @return The query name
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * Set the query name.
	 *
	 * @param name The query name
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Get the count query name.
	 *
	 * @return The count query name
	 */
	public String getCountName ()
	{
		return countName;
	}

	/**
	 * Set the count query name.
	 *
	 * @param countName The count query name
	 */
	public void setCountName (String countName)
	{
		this.countName = countName;
	}

	/**
	 * Get the query string.
	 *
	 * @return The query string
	 */
	public String getQuery ()
	{
		return query;
	}

	/**
	 * Set the query string.
	 *
	 * @param query The query string
	 */
	public void setQuery (String query)
	{
		this.query = query;
	}

	/**
	 * Get the count query string.
	 *
	 * @return The count query string
	 */
	public String getCountQuery ()
	{
		return countQuery;
	}

	/**
	 * Set the count query string.
	 *
	 * @param countQuery The count query string
	 */
	public void setCountQuery (String countQuery)
	{
		this.countQuery = countQuery;
	}

	/**
	 * Set a query parameter.
	 *
	 * @param key The parameter key
	 * @param value The parameter value
	 */
	public void setParam (String key, String value)
	{
		params.put (key, value);
	}

	/**
	 * Get a parameter value
	 *
	 * @param key The parameter key
	 * @return The parameter value or null if none was found
	 */
	public String getParam (String key)
	{
		return params.getProperty (key);
	}

	/**
	 * Get the query parameters
	 *
	 * @return The query parameters
	 */
	public Properties getParams ()
	{
		return params;
	}

	/**
	 * Get the DAO name.
	 *
	 * @return The DAO name
	 */
	public String getDaoName ()
	{
		return daoName;
	}

	/**
	 * Set the DAO name.
	 *
	 * @param daoName The DAO name
	 */
	public void setDaoName (String daoName)
	{
		this.daoName = daoName;
	}

	/**
	 * Get the DAO method name.
	 *
	 * @return The DAO method name
	 */
	public String getDaoMethodName ()
	{
		return daoMethodName;
	}

	/**
	 * Set the DAO method name.
	 *
	 * @param daoMethodName The DAO method name
	 */
	public void setDaoMethodName (String daoMethodName)
	{
		this.daoMethodName = daoMethodName;
	}
}
