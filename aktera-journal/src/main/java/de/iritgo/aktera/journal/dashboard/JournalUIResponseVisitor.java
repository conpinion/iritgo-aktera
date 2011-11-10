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

package de.iritgo.aktera.journal.dashboard;


import de.iritgo.aktera.dashboard.UIResponseVisitor;
import de.iritgo.aktera.dashboard.items.SimpleNumberItem;
import de.iritgo.aktera.dashboard.items.SimpleTextItem;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.ui.UIResponse;
import java.util.LinkedList;
import java.util.List;


public class JournalUIResponseVisitor extends UIResponseVisitor
{
	public JournalUIResponseVisitor()
	{
	}

	/**
	 * @see de.iritgo.aktera.dashboard.GroupVisitor#generate(de.iritgo.aktera.dashboard.items.SimpleTextItem)
	 */
	public void generate(JournalItem journalItem)
	{
		while (journalItem.hasNext())
		{
			journalItem.next();

			Output item = response.createOutput(journalItem.getJournalId());

			item.setAttribute("imageUrl", journalItem.getRawData());
			item.setAttribute("typeOfNews", journalItem.getSecondaryType());
			item.setAttribute("headerLine", journalItem.getShortMessage());
			item.setAttribute("message", journalItem.getMessage());
			item.setAttribute("date", journalItem.getOccurredAt());
			item.setAttribute("producer", journalItem.getMisc());
			item.setAttribute("renderInclude", journalItem.getRenderFile());
			currentGroupItems.add(item);
		}
	}
}
