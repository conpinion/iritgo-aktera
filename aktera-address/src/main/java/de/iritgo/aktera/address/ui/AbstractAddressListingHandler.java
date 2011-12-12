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

package de.iritgo.aktera.address.ui;


import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import de.iritgo.aktera.address.AddressManager;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.permissions.PermissionManager;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.listing.*;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.data.Tuple2;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


public abstract class AbstractAddressListingHandler extends ListingHandler
{
	@Setter
	@Autowired
	protected AddressManager addressManager;

	@Setter
	@Autowired
	protected PermissionManager permissionManager;

	@Override
	public Map<String, String> createSearchCategories(ModelRequest request, ListingDescriptor listing)
		throws ModelException
	{
		Map<String, String> addressStores = new TreeMap<String, String>();
		addressStores.put("", "$opt-");
		try
		{
			String userName = getActualUserName(request);
			for (Tuple2<Integer, String> store : addressManager.listAddressStoresIdAndTitle())
			{
				if (permissionManager.hasPermission(userName, "de.iritgo.aktera.address.view",
								AddressStore.class.getName(), store.get1()))
				{
					addressStores.put(store.get1().toString(), store.get2());
				}
			}
		}
		catch (PersistenceException x)
		{
			throw new ModelException(x);
		}
		return addressStores;
	}

	@Override
	public String getCurrentSearchCategory(ModelRequest request, ListingDescriptor listing) throws ModelException
	{
		AddressStore addressStore = addressManager.getAddressStoreById(NumberTools.toInt(listing.getCategory(), - 1));
		if (addressStore.getId() == - 1)
		{
			addressStore = addressManager.getDefaultAddressStore();
		}
		return addressStore.getId().toString();
	}

	@Override
	public ListFiller createListing(ModelRequest request, ListingDescriptor listing, ListingHandler handler,
					ListContext context) throws ModelException, PersistenceException
	{
		AddressStore store = addressManager.getAddressStoreById(NumberTools.toInt(
						getCurrentSearchCategory(request, listing), - 1));

		final long addressCount = store.countAddressesByOwnerAndSearch(UserTools.getCurrentUserId(request),
						request.getParameterAsString(listing.getId() + "Search", ""));

		context.setFirstResult(Math.min(context.getFirstResult(), context.getResultsPerPage()
						* (int) (addressCount / context.getResultsPerPage())));

		final List<Address> addresses = store.createAddressListing(UserTools.getCurrentUserId(request),
						request.getParameterAsString(listing.getId() + "Search", ""), listing.getSortColumnName(),
						listing.getSortOrder(), context.getFirstResult(), context.getResultsPerPage());

		return new ListFiller()
		{
			Iterator<Address> i = addresses.iterator();

			Address address = null;

			@Override
			public long getTotalRowCount()
			{
				return addressCount;
			}

			@Override
			public int getRowCount()
			{
				return addresses.size();
			}

			@Override
			public boolean next()
			{
				boolean more = i.hasNext();

				if (more)
				{
					address = i.next();
				}

				return more;
			}

			@Override
			public Object getId()
			{
				return StringTools.trim(address.getAnyId());
			}

			@Override
			public Object getValue(String column)
			{
				try
				{
					return PropertyUtils.getNestedProperty(address, column);
				}
				catch (IllegalAccessException x)
				{
				}
				catch (InvocationTargetException x)
				{
				}
				catch (NoSuchMethodException x)
				{
				}

				return "";
			}
		};
	}
}
