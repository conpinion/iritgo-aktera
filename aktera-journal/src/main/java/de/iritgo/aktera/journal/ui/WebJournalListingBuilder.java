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

package de.iritgo.aktera.journal.ui;


import de.iritgo.aktera.ui.listing.ListFiller;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class WebJournalListingBuilder
{
	/** The web journal entry builder */
	private Map<String, WebJournalListingEntryBuilder> entryBuilders;

	/** Set the web journal entry builder */
	public void setEntryBuilders(Map<String, WebJournalListingEntryBuilder> entryBuilders)
	{
		this.entryBuilders = entryBuilders;
	}

	public ListFiller createListFiller(final List<Map<String, Object>> journal, final long numEntries,
					final Locale locale)
	{
		return new ListFiller()
		{
			Iterator<Map<String, Object>> i = journal.iterator();

			Map<String, Object> entry = null;

			@Override
			public long getTotalRowCount()
			{
				return numEntries;
			}

			@Override
			public int getRowCount()
			{
				return journal.size();
			}

			@Override
			public boolean next()
			{
				boolean more = i.hasNext();

				if (more)
				{
					entry = i.next();
				}

				return more;
			}

			@Override
			public Object getId()
			{
				return entry.get("id").toString();
			}

			@Override
			public Object getValue(String column)
			{
				WebJournalListingEntryBuilder jeb = entryBuilders.get(entry.get("extendedInfoType"));
				Object value = null;

				if (jeb != null)
				{
					value = jeb.getValue(entry, column, jeb.getBundle(), locale);
				}

				if (value == null)
				{
					value = entryBuilders.get(WebJournalListingEntryBuilder.BASE).getValue(entry, column,
									jeb.getBundle(), locale);
				}

				return value;
			}
		};
	}
}
