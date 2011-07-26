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

package de.iritgo.aktera.journal;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.simplelife.math.NumberTools;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.journal.delete-journal-entry"
 * @x-avalon.lifestyle type="singleton"
 * @model.model
 *   name="aktera.journal.delete-journal-entry"
 *   id="aktera.journal.delete-journal-entry"
 *   logger="aktera"
 */
public class DeleteJournalEntry extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		JournalManager journalManager = (JournalManager) req.getSpringBean (JournalManager.ID);

		String[] ids;

		if (req.getParameter ("id") == null)
		{
			ids = new String[0];
		}
		else if (req.getParameter ("id") instanceof String)
		{
			ids = new String[]
			{
				(String) req.getParameter ("id")
			};
		}
		else
		{
			ids = (String[]) req.getParameter ("id");
		}

		for (int i = 0; i < ids.length; ++i)
		{
			journalManager.deleteJournalEntry (NumberTools.toInt (ids[i], - 1));
		}

		return req.createResponse ();
	}
}
