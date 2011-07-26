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

package de.iritgo.aktera.aktario.user;


import de.iritgo.aktario.framework.command.CommandTools;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.ui.listing.ListContext;
import de.iritgo.aktera.ui.listing.ListFiller;
import de.iritgo.aktera.ui.listing.ListingDescriptor;
import de.iritgo.aktera.ui.listing.ListingHandler;
import de.iritgo.simplelife.constants.SortOrder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 *
 */
public class ParticipantListingHandler extends ListingHandler
{
	/**
	 * @see de.iritgo.aktera.ui.listing.ListingHandler#createListing(de.iritgo.aktera.model.ModelRequest,
	 *      de.iritgo.aktera.ui.listing.ListingDescriptor,
	 *      de.iritgo.aktera.ui.listing.ListingHandler, ListContext)
	 */
	@Override
	public ListFiller createListing (ModelRequest request, final ListingDescriptor listing, ListingHandler handler,
					ListContext context) throws ModelException, PersistenceException
	{
		Properties props = new Properties ();
		List aktarioUsers = (List) CommandTools.performSimple ("aktario-participant.GetParticipantList", props);

		Collections.sort (aktarioUsers, new Comparator ()
		{
			public int compare (Object o1, Object o2)
			{
				return ((String) ((Map) o1).get ("aktarioUserName")).compareTo ((String) ((Map) o2)
								.get ("aktarioUserName"));
			}
		});

		final List users = aktarioUsers;

		Collections.sort (users, new Comparator ()
		{
			public int compare (Object o1, Object o2)
			{
				SortOrder sort = listing.getSortOrder ();
				String column = listing.getSortColumnName ();

				column = column.substring (column.indexOf ('.') + 1);

				Map m1 = sort == SortOrder.ASCENDING ? (Map) o1 : (Map) o2;
				Map m2 = sort == SortOrder.ASCENDING ? (Map) o2 : (Map) o1;

				if ("online".equals (column))
				{
					return ((Integer) m1.get ("onlineUser")).intValue ()
									- ((Integer) m2.get ("onlineUser")).intValue ();
				}
				else if ("name".equals (column))
				{
					return ((String) m1.get ("aktarioUserName")).compareTo ((String) m2.get ("aktarioUserName"));
				}

				return 0;
			}
		});

		return new ListFiller ()
		{
			Iterator i = users.iterator ();

			Map current = null;

			public int getRowCount ()
			{
				return users.size ();
			}

			public boolean next ()
			{
				boolean more = i.hasNext ();

				if (more)
				{
					current = (Map) i.next ();
				}

				return more;
			}

			public Object getValue (String column)
			{
				if ("online".equals (column))
				{
					switch ((Integer) current.get ("availableState"))
					{
						case - 1:
							return "user-offline-24";

						case 0:
							return "user-online-24";

						case 1:
							return "user-online-donotdisturb-24";

						case 2:
							return "user-online-sleep-24";

						case 3:
							return "user-online-unavailable-24";

						case 4:
							return "user-online-away-24";

						case 5:
							return "user-online-homeoffice-24";

						case 6:
							return "user-online-appointment-24";

						case 7:
							return "user-online-ill-24";

						case 8:
							return "user-online-holiday-24";

						case 9:
							return "user-online-forward-24";
					}
				}
				else if ("name".equals (column))
				{
					return current.get ("aktarioUserName");
				}

				return null;
			}
		};
	}
}
