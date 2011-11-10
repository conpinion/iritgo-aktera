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


import de.iritgo.aktera.journal.entity.JournalEntry;
import de.iritgo.simplelife.bean.BeanTools;
import de.iritgo.simplelife.string.StringTools;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class BaseWebJournalListingEntryBuilder implements WebJournalListingEntryBuilder
{
	public Object getValue(Map<String, Object> entry, String column, String bundle, Locale locale)
	{
		if ("primaryType".equals(column))
		{
			return (entry.get(column) + "-journal-16").toLowerCase();
		}
		else if ("secondaryType".equals(column))
		{
			return StringTools.toLowerCase(entry.get("primaryType") + "-" + entry.get(column) + "-journal-16");
		}
		else if ("secondaryTypeText".equals(column))
		{
			return bundle
							+ ":"
							+ StringTools.toLowerCase("journal." + entry.get("primaryType") + "."
											+ entry.get("secondaryType"));
		}
		else if ("occurredAt".equals(column))
		{
			if (locale == null)
			{
				locale = Locale.GERMANY;
			}

			//TODO: Datedisplay format in the base settings
			return new java.text.SimpleDateFormat("dd.MM.yyyy (EE) HH:mm", locale).format(new Date(((Timestamp) entry
							.get("occurredAt")).getTime()));
		}

		return BeanTools.hasProperty(JournalEntry.class, column) ? entry.get(column) : null;
	}

	public String getBundle()
	{
		return "aktera-journal";
	}
}
