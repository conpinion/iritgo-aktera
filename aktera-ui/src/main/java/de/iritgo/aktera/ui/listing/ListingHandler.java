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


import java.sql.SQLException;
import java.util.Map;
import de.iritgo.aktera.authentication.defaultauth.entity.UserDAO;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.string.StringTools;


/**
 * Base class for all listing handlers.
 */
public class ListingHandler
{
	/** The default handler */
	private ListingHandler defaultHandler;

	/**
	 * Set the default handler.
	 *
	 * @param defaultHandler The new default handler
	 */
	public void setDefaultHandler(ListingHandler defaultHandler)
	{
		this.defaultHandler = defaultHandler;
	}

	/**
	 * This method is called before the listing is actually created.
	 *
	 * @param req The model request
	 * @param listing The listing
	 * @param context The list context
	 * @throws ModelException In case of an error
	 */
	public void adjustListing(ModelRequest req, ListingDescriptor listing, ListContext context) throws ModelException
	{
	}

	/**
	 * Override this method if you want to provide a list of search categories.
	 *
	 * @param request The model request
	 * @param listing The listing
	 * @return A map of (value, label) entries
	 * @throws ModelException In case of an error
	 */
	public Map<String, String> createSearchCategories(ModelRequest request, ListingDescriptor listing)
		throws ModelException
	{
		return null;
	}

	/**
	 * Override this method to provide the currently selected search category
	 *
	 * @param request The model request
	 * @param listing The listing descriptor
	 * @return The currently selected search category
	 * @throws ModelException In case of an error
	 */
	public String getCurrentSearchCategory(ModelRequest request, ListingDescriptor listing) throws ModelException
	{
		Object category = request.getParameter(listing.getId() + "SearchCategory");

		return category != null ? category.toString() : null;
	}

	/**
	 * Actually create the listing response.
	 *
	 * @param request The model request
	 * @param listing The listing
	 * @param handler The listing handler
	 * @param context The list context
	 * @return A listing filler
	 * @throws ModelException In case of an error
	 * @throws PersistenceException In case of a database error
	 */
	public ListFiller createListing(ModelRequest request, ListingDescriptor listing, ListingHandler handler,
					ListContext context) throws ModelException, PersistenceException
	{
		return defaultHandler.createListing(request, listing, handler, context);
	}

	/**
	 * Handle one item of the query result.
	 *
	 * @param req The model request
	 * @param res The model response
	 * @param listing The listing
	 * @param item The cell item
	 * @param set The result set
	 * @throws ModelException In case of an error
	 * @throws PersistenceException In case of a database error
	 * @throws SQLException In case of a database error
	 */
	public CellData handleResult(ModelRequest req, ModelResponse res, ListingDescriptor listing,
					@SuppressWarnings("unused") RowData data, @SuppressWarnings("unused") ColumnDescriptor column)
		throws PersistenceException, ModelException, SQLException
	{
		return new CellData();
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
	protected Integer getActualUserId(ModelRequest request)
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
	 * Retrieve the actual user id. This is either the current user, or the user provided
	 * by the request parameter "userId".
	 *
	 * @return The user id
	 * @throws ModelException In case of a general error
	 * @throws PersistenceException in case of a database error
	 * @throws PermissionException If the current user has no permission to upload files
	 * for another user
	 */
	protected String getActualUserName(ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		if (! StringTools.isTrimEmpty(request.getParameter("userId")))
		{
			int userId = request.getParameterAsInt("userId", - 1);
			if (! UserTools.currentUserIsInGroup(request, "manager") && UserTools.getCurrentUserId(request) != userId)
			{
				throw new PermissionException("Permission denied to edit com device function keys of user " + userId);
			}
			UserDAO userDAO = (UserDAO) SpringTools.getBean(UserDAO.ID);
			return userDAO.findUserById(userId).getName();
		}
		return UserTools.getCurrentUserName(request);
	}
}
