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


import java.util.HashMap;
import java.util.Map;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.permissions.PermissionException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.el.ExpressionLanguageContext;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.string.StringTools;


public class ListContext extends ExpressionLanguageContext
{
	/** The listing descriptor */
	private ListingDescriptor listing;

	public void setListing(ListingDescriptor listing)
	{
		this.listing = listing;
	}

	public ListingDescriptor getListing()
	{
		return listing;
	}

	/** Number of result rows per page */
	public int resultsPerPage;

	public int getResultsPerPage()
	{
		return resultsPerPage;
	}

	public void setResultsPerPage(int resultsPerPage)
	{
		this.resultsPerPage = resultsPerPage;
	}

	/** Number of previous pages */
	public int numPrevPages;

	public int getNumPrevPages()
	{
		return numPrevPages;
	}

	public void setNumPrevPages(int numPrevPages)
	{
		this.numPrevPages = numPrevPages;
	}

	/** Number of following pages */
	public int numNextPages;

	public int getNumNextPages()
	{
		return numNextPages;
	}

	public void setNumNextPages(int numNextPages)
	{
		this.numNextPages = numNextPages;
	}

	/** The name of the list */
	public String listName;

	public String getListName()
	{
		return listName;
	}

	public void setListName(String listName)
	{
		this.listName = listName;
	}

	/** If true, only column headers are generated */
	public boolean describe;

	public boolean isDescribe()
	{
		return describe;
	}

	public void setDescribe(boolean describe)
	{
		this.describe = describe;
	}

	/** Current page number */
	private int page;

	public void setPage(int page)
	{
		this.page = page;
	}

	public int getPage()
	{
		return page;
	}

	/** Row offset */
	private int firstResult;

	public void setFirstResult(int firstResult)
	{
		this.firstResult = firstResult;
	}

	public int getFirstResult()
	{
		return firstResult;
	}

	/** Current row data */
	private Object it;

	public Object getIt()
	{
		return it;
	}

	public void setIt(Object it)
	{
		this.it = it;
	}

	/** Custom attributes */
	private Map<String, Object> attributes = new HashMap();

	public void setAttribute(String key, Object value)
	{
		attributes.put(key, value);
	}

	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	/** The name of this session's user */
	private String userName;

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Get the text for an "equals" search.
	 *
	 * @return The search text
	 */
	public String getSearchExact()
	{
		return StringTools.trim(request.getParameterAsString(listing.getId(request) + "Search")).toLowerCase();
	}

	/**
	 * Get the text for a "contains" search.
	 *
	 * @return The search text
	 */
	public String getSearch()
	{
		return "%" + getSearchExact() + "%";
	}

	/**
	 * Get the text for a "starts with" search.
	 *
	 * @return The search text
	 */
	public String getSearchStartsWith()
	{
		return "%" + getSearchExact();
	}

	/**
	 * Get the text for a "ends with" search.
	 *
	 * @return The search text
	 */
	public String getSearchEndsWith()
	{
		return getSearchExact() + "%";
	}

	/**
	 * Treat the current row data as an object array and return the
	 * index-th arrey element.
	 *
	 * @param index The array index
	 * @return The row data element
	 */
	public Object getIt(int index)
	{
		return ((Object[]) it)[index];
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("ListContext [firstResult=");
		builder.append(firstResult);
		builder.append(", listName=");
		builder.append(listName);
		builder.append(", listing=");
		builder.append(listing);
		builder.append(", page=");
		builder.append(page);
		builder.append(", resultsPerPage=");
		builder.append(resultsPerPage);
		builder.append("]");

		return builder.toString();
	}

	/**
	 * Get the list category
	 *
	 * @return The list category
	 */
	public String getCategory()
	{
		return StringTools.trim(listing.getCategory());
	}

	/**
	 * Retrieve the actual user id. This is either the current user, or the user provided
	 * by the request parameter "userId".
	 *
	 * @return The user id
	 * @throws ModelException In case of a general error
	 * @throws PersistenceException in case of a database error
	 * @throws PermissionException If the current user has no permission to upload files
	 * for another user
	 */
	public Integer getActualUserId(ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		if (! StringTools.isTrimEmpty(request.getParameter("userId")))
		{
			int userId = request.getParameterAsInt("userId", - 1);

			if (! UserTools.currentUserIsInGroup(request, "manager") && UserTools.getCurrentUserId(request) != userId)
			{
				throw new PermissionException("Permission denied to edit com device function keys of user " + userId);
			}

			return userId;
		}

		return UserTools.getCurrentUserId(request);
	}

	/**
	 * Get the constant boolean true value.
	 *
	 * @return True
	 */
	public Boolean getTRUE()
	{
		return true;
	}

	/**
	 * Get the constant boolean false value.
	 *
	 * @return False
	 */
	public Boolean getFALSE()
	{
		return false;
	}
}
