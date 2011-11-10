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

package de.iritgo.aktera.tools;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.query.Query;
import de.iritgo.aktera.query.QueryException;
import de.iritgo.simplelife.math.NumberTools;
import java.util.List;
import java.util.Map;


/**
 * Query utility methods.
 */
public class QueryTools
{
	/**
	 * Helper method to execute count queries (select count (*) from ...).
	 *
	 * @param req A model request.
	 * @param queryId The id of the query to execute.
	 * @param params Query parameters.
	 * @return The count result.
	 * @throws ModelException in case of a general failure.
	 * @throws QueryException in case of a query failure.
	 */
	public static int executeCountQuery(ModelRequest req, String queryId, Object... params)
		throws ModelException, QueryException
	{
		Query query = (Query) req.getService(Query.ROLE, queryId);

		for (int i = 0; i + 1 < params.length;)
		{
			query.setCriteria(params[i].toString(), params[i + 1]);
			i += 2;
		}

		List res = query.getQueryResults();

		if (res.size() > 0)
		{
			return NumberTools.toInt(((Map) res.get(0)).get("count"), - 1);
		}

		return 0;
	}
}
