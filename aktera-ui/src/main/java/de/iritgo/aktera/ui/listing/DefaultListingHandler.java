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


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.PersistenceException;


/**
 *
 */
public class DefaultListingHandler extends ListingHandler
{
	/**
	 * Create a default listing handler.
	 */
	public DefaultListingHandler ()
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.ListingHandler#adjustListing(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.listing.ListingDescriptor, ListContext)
	 */
	@Override
	public void adjustListing (ModelRequest req, ListingDescriptor listing, ListContext context) throws ModelException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.listing.ListingHandler#createListing(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.listing.ListingDescriptor, de.iritgo.aktera.ui.listing.ListingHandler, ListContext)
	 */
	@Override
	public ListFiller createListing (ModelRequest request, ListingDescriptor listing, ListingHandler handler,
					ListContext context) throws ModelException, PersistenceException
	{
		return null;
	}
}
