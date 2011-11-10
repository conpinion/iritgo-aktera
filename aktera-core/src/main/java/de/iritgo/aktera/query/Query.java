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

package de.iritgo.aktera.query;


import java.util.List;
import java.util.SortedSet;


/**
 * This component provides a general-purpose interface to Queries against
 * persistent stores. Each implementation of the persistent service can provide
 * it's own implementation of Query - for example, the default implementation
 * might query databases via JDBC. The exact structure of the query is based
 * on configuration. Each query can be refined at runtime by specifying variable
 * criteria in one of two ways: By named criteria (using the setCriteria(String,
 * Object) method, or by passing the Query a list of ObjectKey objects, which
 * may be the result of the operation of a Criteria component. In either case,
 * the Query returns a set of Maps in the specified order.
 *  @author Michael Nash, Eliot Clingman
 */
public interface Query
{
	public static final String ROLE = "de.iritgo.aktera.query.Query";

	/**
	 * Provide named "criteria" to control the operation of the query
	 * @param criteriaCode Name of the criteria being specified
	 * @param criteriaValue Value of the criteria
	 * @throws QueryException If there is no such criteria, or the value is not
	 * allowed
	 */
	public void setCriteria(String criteriaCode, Object criteriaValue) throws QueryException;

	/**
	 * Using the output set of a Criteria implementation, get a set of
	 * results for those objects. This output could contain set of beans, table
	 * rows, flat files, any type of relevant info about the objects. Note The
	 * output List may, but is not neccasarily, in same order as the input set.
	 * The ordering is of course different if each object key results in multiple outputs.
	 * @return A List containing Maps, one for each query result. Each map
	 * is a name / value mapping for each result "row"
	 */
	public List getQueryResults(SortedSet objectKeys) throws QueryException;

	/**
	 * This is a quick query that does not depend on running the Criteria service
	 * before the query service.  There will therefore be no object keys available
	 * to pass in to the query service.  The query service is therefore at the
	 * mercy of criteria parameters (which has nothing to do with Criteria service
	 * by the way.)  Using the parameters, get a List of
	 * results. This output could contain List of beans, table
	 * rows, flat files, any type of relevant info.
	 * @return A List containing Maps, one for each query result. Each map
	 * is a name / value mapping for each result "row"
	 */
	public List getQueryResults() throws QueryException;
}
