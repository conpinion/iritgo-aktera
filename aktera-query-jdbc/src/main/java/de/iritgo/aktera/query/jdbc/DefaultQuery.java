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

package de.iritgo.aktera.query.jdbc;


import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.core.container.ServiceConfig;
import de.iritgo.aktera.finder.ObjectKey;
import de.iritgo.aktera.query.Query;
import de.iritgo.aktera.query.QueryException;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.ids.IdException;
import org.apache.avalon.excalibur.datasource.ids.IdGenerator;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Default  JDBC-based implementation of Query, using simple JDBC/SQL access to
 * a database to get query results.
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.query.Query
 * @x-avalon.info name=query-sql
 * @x-avalon.lifestyle type=singleton
 *
 * @author Michael Nash
 * @author Eliot Clingman
 * @author Ryan Thoma
 */
public class DefaultQuery extends AbstractKeelServiceable implements Query, Poolable, Recyclable, LogEnabled,
				Configurable, Initializable, Disposable
{
	private static Logger log = null;

	/**
	 * If the criteria specified contains the special criteria name "headers", we
	 * include a row header automatically.
	 */
	private final static String headerCriteriaName = "headers";

	protected ServiceConfig svcConfig = null;

	private Map criteriaInput = new HashMap ();

	private Configuration[] criteria = null;

	private Configuration[] resultFields = null;

	private String sql = null;

	private Pattern reParam = Pattern.compile ("%(\\w+)");

	/**
	 * The name of the data source we will access for this query. Defined with
	 * the <dbpool> configuration element
	 */
	private String dataSourceName = null;

	/**
	 * Data source service itself. Used for all connections from this query
	 */
	private DataSourceComponent dataSource = null;

	/**
	 * Name of this Query
	 */
	protected String myName = null;

	/**
	 * @see de.iritgo.aktera.query.Query#setCriteria(java.lang.String, java.
	 * lang. Object)
	 */
	public void setCriteria (String criteriaCode, Object criteriaValue)
	{
		criteriaInput.put (criteriaCode, criteriaValue);
	}

	/**
	 * Creates the tabular report rows.  In those cases where
	 * Criteria component has provided a collection of
	 * ObjectKeys of interest, joins to table queryJoin which caches objectKeys.
	 * Otherwise executes a generic SQL statement with simple parameter substitution
	 * Returns results as a List of Maps, not as a java.sql.ResultSet
	 * @see de.iritgo.aktera.query.Query#getQueryResults(java.util.SortedSet)
	 * @param objectKeys  The unique Keys of those objects for which tabular data is to be returned.
	 * @return the tabular data from sql data source as a List of Maps (each row is in a single map)
	 * @throws QueryException Thrown in the case of system failure.
	 */
	public List getQueryResults (SortedSet objectKeys) throws QueryException
	{
		// ===================================================
		// Declare method variables
		// ===================================================
		//String temp = null;
		List queryResult = null;
		int myQueryId = 0;
		Connection myConnection = null;

		// ===================================================
		// Obtain database connection
		// ===================================================
		try
		{
			myConnection = dataSource.getConnection ();
			log.debug ("[CONNECTION] instantiated: " + myConnection.toString ());
		}
		catch (SQLException e)
		{
			throw new QueryException (e, "Obtaining database connection failed");
		}

		//		 modified by aleks
		try
		{
			// ===================================================
			// We are building a tabular report which analyzes the objects
			// named in ObjectKeys.  So if ObjectKeys collection is not empty we
			// store ObjectKeys info the id cache table called queryJoin. myQueryId identifies the cache.
			// Warning: If ObjectKeys is empty the method polymorphism
			// getQueryResults with no parameter should be used instead
			// ===================================================
			if (objectKeys != null)
			{
				myQueryId = cacheObjectKeys (objectKeys, myQueryId, myConnection);
			} //end if
			else
			{
				log.warn ("\n\n\n getQueryResults(objectKeys) has been called"
								+ " with objectKeys == null, but getQueryResults() without parameters should be used");
			}

			// ===================================================
			// now run the query and get tabular report
			// ===================================================
			queryResult = runQuery (myConnection, myQueryId);

			// ===================================================
			// Eliminate the object Keys cache: no longer needed
			// ===================================================
			deCacheObjectKeys (myQueryId, myConnection);
		}
		catch (QueryException qe)
		{
			throw new QueryException (qe);
		}
		finally
		{
			try
			{
				myConnection.close ();
				myConnection = null;
			}
			catch (SQLException e1)
			{
				throw new QueryException (e1);
			}
		}

		// modified by aleks
		return queryResult;
	}

	/**
	 * Creates the tabular report rows, in the case where
	 * there are no object keys (because the criteria service wasn't used).
	 * Executes a generic SQL statement with simple parameter substitution
	 * Returns results as a List of Maps, not as a java.sql.ResultSet
	 * @see de.iritgo.aktera.query.Query#getQueryResults()
	 * @return the tabular data from sql data source as a List of Maps (each row is in a single map)
	 * @throws QueryException Thrown in the case of system failure.
	 */
	public List getQueryResults () throws QueryException
	{
		// ===================================================
		// Declare method variables
		// ===================================================
		//String temp = null;
		List queryResult = null;
		Connection myConnection = null;

		// ===================================================
		// Obtain database connection
		// ===================================================
		try
		{
			myConnection = dataSource.getConnection ();
			log.debug ("[CONNECTION2] instantiated: " + myConnection.toString ());
		}
		catch (SQLException e)
		{
			throw new QueryException (e, "Obtaining database connection failed");
		}

		// ===================================================
		// now run the query and get tabular report
		// There is no queryId, so we pass 0 as a dummy parameter
		// ===================================================
		//			 modified by aleks
		try
		{
			queryResult = runQuery (myConnection, 0);
		}
		catch (QueryException qe)
		{
			throw new QueryException (qe);
		}
		finally
		{
			try
			{
				myConnection.close ();
				myConnection = null;
			}
			catch (SQLException e1)
			{
				throw new QueryException (e1);
			}
		}

		// modified by aleks
		return queryResult;
	}

	private int cacheObjectKeys (SortedSet objectKeys, int myQueryId, Connection myConnection) throws QueryException
	{
		// modified by aleks
		Statement aStatement = null;

		// modified by aleks
		try
		{
			// ===================================================
			// Obtain unique cache id
			// ===================================================
			IdGenerator myIdGenerator = (IdGenerator) getService (IdGenerator.ROLE, svcConfig
							.getHint (IdGenerator.ROLE));

			try
			{
				myQueryId = myIdGenerator.getNextIntegerId ();
			}
			catch (IdException ie)
			{
				throw new QueryException ("Unable to get myQueryId. ");
			}

			// ===================================================
			// Add each ObjectKey to sql batch insert statement
			// ===================================================
			ObjectKey oneKey = null;
			String insertStatement = null;

			// modified by aleks
			aStatement = myConnection.createStatement ();

			// modified by aleks
			for (Iterator i = objectKeys.iterator (); i.hasNext ();)
			{
				ObjectKey oneObj = (ObjectKey) i.next ();

				if (oneObj != null)
				{
					oneKey = oneObj;
					log.info ("UniqueId of ObjectKey: " + oneKey.getUniqueId ().toString ());
					insertStatement = "INSERT INTO QueryJoin (ObjectId, QueryId, name, alias) VALUES ('"
									+ oneKey.getUniqueId ().toString () + "', " + myQueryId + ", '" + oneKey.getName ()
									+ "', '" + oneKey.getAlias () + "')";
					log.debug ("Running statement '" + insertStatement.toString () + "'");
					aStatement.addBatch (insertStatement);
				}
			} //end for loop

			aStatement.executeBatch ();

			//				int [] updateCounts = aStatement.executeBatch();
			//		 for (int i = 0; i < updateCounts.length; i++)
			//		 {
			//		 System.out.println("sresult of batch statement number " + i + ": " + updateCounts[i]);
			//		 }
		}
		catch (java.sql.BatchUpdateException be)
		{
			throw new QueryException (be, "Cacheing of objectKeys in SQL table failed");
		}
		catch (SQLException se)
		{
			throw new QueryException (se, "Cacheing of objectKeys in SQL table failed");
		}
		catch (ServiceException svce)
		{
			throw new QueryException (svce);
		}

		// modified by aleks
		finally
		{
			try
			{
				aStatement.close ();
				aStatement = null;
			}
			catch (SQLException se)
			{
				throw new QueryException (se);
			}
		}

		// modified by aleks
		return myQueryId;
	}

	private void deCacheObjectKeys (int myQueryId, Connection myConnection) throws QueryException
	{
		// ===================================================
		// Now that the tabular report rows are built, remove the objectKeys cache
		// from table queryJoin for the query. Specifically remove all relevant
		// rows from table queryJoin.
		// ===================================================
		log.debug ("SQL for join cleanup: " + "DELETE FROM queryJoin   " + "WHERE QueryId = '" + myQueryId + "'");

		// modified by aleks
		Statement cleanupStatement = null;

		// modified by aleks
		try
		{
			// modified by aleks
			cleanupStatement = myConnection.createStatement ();
			// modified by aleks
			cleanupStatement.execute ("DELETE FROM queryJoin   " + "WHERE QueryId = '" + myQueryId + "'");
		}
		catch (SQLException se)
		{
			throw new QueryException (se, "Failed to remove Cache of objectKeys in table queryJoin");
		}

		// modified by aleks
		finally
		{
			try
			{
				cleanupStatement.close ();
				cleanupStatement = null;
			}
			catch (SQLException se)
			{
				throw new QueryException (se);
			}
		}

		// modified by aleks
	}

	/**
	 * Bind parameters to actual values in the sql statement, run the
	 * sql statement, and create the queryResults as a List of Maps.
	 * @param myConnection The database connection
	 * @param myQueryId   The identifier of the cache of objectKeys
	 * @return the query results
	 * @throws QueryException if the service failed
	 */
	private List runQuery (Connection myConnection, int myQueryId) throws QueryException
	{
		// =================================================
		// Bind parameters of the sql statement to actual values.
		// =================================================
		SuperString myStatement = bindParameters (myQueryId);

		// =================================================
		// Execute massaged sql statement
		// =================================================
		log.debug ("Query statement after, join id,  param subsitution and append ORDER by clause:" + myStatement);

		List tempResult = executeQuery (myConnection, myStatement);

		// ===================================================
		// Return tabular report rows
		// ===================================================
		return tempResult;
	}

	/**
	 * Bind parameters to actual values in the sql statement
	 * @param myQueryId The identifier of the cache of objectKeys
	 * @return  The SQL statement with the actual values in lieu of parameters
	 * @throws QueryException Thrown if the Query service fails
	 */
	private SuperString bindParameters (int myQueryId) throws QueryException
	{
		// ===================================================
		// Now we complete the sql statement that is associated
		// with this tabular report type. The raw sql statement is provided in
		// a config file, but this must be modified before execution.
		// ===================================================
		SuperString myStatement = new SuperString (sql);

		log.debug ("Config Query statement before param substitution:" + myStatement);

		// ===================================================
		// Modify sql statement from the config.
		//  - If there is no "order by" clause in config file, then order by queryjoin.name
		//    so that queryResult ordering  will match ObjectKeys ordering.
		//  - Replace any paramters in sql statement with runtime values passed via criteria inputs
		//    order by clauses at run time.
		//  - If objectKeys is not null, substitute $queryid with myQueryId governing the join.
		// ===================================================
		log.debug ("Does Config Query have ORDER by clause, not if this index is -1:"
						+ (myStatement.toString ().toUpperCase ().indexOf ("ORDER BY ")));

		// modified by aleks
		if ((myStatement.toString ().toUpperCase ().indexOf ("ORDER BY ")) == - 1 && myQueryId > 0)
		{
			log.debug ("sindex not found, so append");
			myStatement.append ("\n ORDER BY $orderByClause ");
		}

		// modified by aleks
		try
		{
			String oneCriteriaCode = null;

			for (int i = 0; i < criteria.length; i++)
			{
				oneCriteriaCode = criteria[i].getAttribute ("name");

				// ==================================
				// Replace 0 or more occurancees of criteria code parameter with value
				// ==================================
				while (myStatement.toString ().indexOf ("$" + oneCriteriaCode) != - 1)
				{
					myStatement = new SuperString (myStatement.replace ("$" + oneCriteriaCode,
					// BUEROBYTE: Don't cast to String, convert to String!

									//  							(String) criteriaInput.get(oneCriteriaCode)));
									criteriaInput.get (oneCriteriaCode).toString ()));

					// BUEROBYTE
				}

				// BUEROBYTE: Replace '%'-paramters with a '?' for prepared statements.
				while (myStatement.toString ().indexOf ("%" + oneCriteriaCode) != - 1)
				{
					myStatement = new SuperString (myStatement.replace ("%" + oneCriteriaCode, "?"));
				}

				// BUEROBYTE
			}
		}
		catch (ConfigurationException ce)
		{
			throw new QueryException (ce);
		}

		myStatement = new SuperString (myStatement.replace ("$orderByClause", "queryJoin.name"));
		myStatement = new SuperString (myStatement.replace ("$queryid", (new Integer (myQueryId)).toString ()));

		//Note: SuperString replace does not change "this", but only return value.  Is that desirable?
		return myStatement;
	}

	/**
	 * Run sql query, returning the query result as a set of Maps, where each
	 * map represents one row of the result.
	 * @param myStatement The sql statement to be run
	 * @return Set queryResultRows containing Maps, each representing a row.
	 * @throws QueryException Thrown in the case of system failure.
	 */
	private List executeQuery (Connection myConnection, SuperString myStatement) throws QueryException
	{
		//Connection myConnection = null;

		// BUEROBYTE: Use a prepared statement.

		// 		Statement sqlStatement = null;
		PreparedStatement sqlStatement = null;

		// BUEROBYTE
		ResultSet myResultSet = null;
		List queryResultRows = new ArrayList ();

		try
		{
			// Populate the display header row with the description field in the result config.
			if (criteriaInput.containsKey (headerCriteriaName))
			{
				String oneDisplayHeaderName = null;
				String oneFieldName = null;
				SortedMap displayHeaderMap = new TreeMap ();

				for (int i = 0; i < resultFields.length; i++)
				{
					oneFieldName = resultFields[i].getAttribute ("name");
					oneDisplayHeaderName = resultFields[i].getAttribute ("descrip");
					displayHeaderMap.put (oneFieldName, oneDisplayHeaderName);
				}

				queryResultRows.add (displayHeaderMap);
			}

			// ================================
			// Execute the sql query
			// ================================
			//myConnection = dataSource.getConnection();
			log.debug ("Running statement '" + myStatement.toString () + "'");

			// BUEROBYTE: Use a prepared statement.

			// 			sqlStatement = myConnection.createStatement();

			// 			myResultSet =
			// 				sqlStatement.executeQuery(myStatement.toString());
			sqlStatement = myConnection.prepareStatement (myStatement.toString ());

			int paramIndex = 1;
			int pos = 0;
			Matcher paramMatcher = reParam.matcher (sql);

			while (paramMatcher.find (pos))
			{
				Object value = criteriaInput.get (paramMatcher.group (1));

				sqlStatement.setObject (paramIndex, value);
				++paramIndex;
				pos = paramMatcher.end () + 1;
			}

			myResultSet = sqlStatement.executeQuery ();

			// BUEROBYTE

			// ================================
			// Convert resultSet to a set of maps, by going through
			// the list of response elements in order, building
			// the persistent list with each result.
			// ================================
			int recordCount = 0;
			int retrieveCount = 0;

			while (myResultSet.next ())
			{
				recordCount++;
				retrieveCount++;

				Map resultMap = new LinkedHashMap ();
				String oneFieldName = null;

				for (int i = 0; i < resultFields.length; i++)
				{
					oneFieldName = resultFields[i].getAttribute ("name");
					resultMap.put (oneFieldName, myResultSet.getString (i + 1));
				}

				log.debug ("Processing new row into map, see size: " + queryResultRows.size ());
				queryResultRows.add (resultMap);
			}
		}
		catch (Exception de)
		{
			throw new QueryException (de);
		}
		finally
		{
			try
			{
				if (myResultSet != null)
				{
					myResultSet.close ();
					myResultSet = null;
				}

				if (sqlStatement != null)
				{
					sqlStatement.close ();
					sqlStatement = null;
				}

				//				if(myConnection!=null)
				//				{
				//					myConnection.close();
				//				}
			}
			catch (SQLException se)
			{
				throw new QueryException (se);
			}
		}

		return queryResultRows;
	}

	// ================================
	// Keel Container support methods
	//================================
	public void enableLogging (Logger newLog)
	{
		log = newLog;
	}

	public void initialize () throws QueryException
	{
		try
		{
			// Get a reference to a data source
			dataSource = (DataSourceComponent) getService (DataSourceComponent.ROLE, dataSourceName);
		}
		catch (ServiceException se)
		{
			throw new QueryException (se);
		}
	}

	public void configure (Configuration configuration) throws ConfigurationException
	{
		svcConfig = new ServiceConfig (configuration);

		// Obtain a reference to the configured DataSource
		dataSourceName = configuration.getChild ("dbpool").getValue ();

		sql = configuration.getChild ("sql").getValue ();

		criteria = configuration.getChildren ("criteria");

		/* We *must* have defined result field names */
		resultFields = configuration.getChildren ("result");

		if (resultFields.length == 0)
		{
			throw new ConfigurationException ("No result fields specified");
		}
	}

	public void recycle ()
	{
		releaseServices ();
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
	public void dispose ()
	{
		releaseServices ();
	}
}
