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


import de.iritgo.aktera.dashboard.DashboardItem;
import de.iritgo.simplelife.string.StringTools;


public interface JournalItem extends DashboardItem
{
	/**
	 * Return the label for the board.
	 *
	 * @return The label
	 */
	public String getLabel();

	/**
	 * Return the primary journal id key
	 *
	 * @return The id
	 */
	public String getJournalId();

	/**
	 * Return the description of the board
	 *
	 * @return
	 */
	public String getDescription();

	public boolean hasNext();

	public void next();

	/**
	 * Return the raw data field
	 *
	 * @return The raw data
	 */
	public String getRawData();

	/**
	 * The scondary type
	 *
	 * @return The secondary type
	 */
	public String getSecondaryType();

	/**
	 * Rerturn short message field
	 *
	 * @return The short message field
	 */
	public String getShortMessage();

	/**
	 * Return the message field
	 *
	 * @return The message field
	 */
	public String getMessage();

	/**
	 * OccurredAt
	 *
	 * @return Occurred at
	 */
	public String getOccurredAt();

	/**
	 * Return the misc field
	 *
	 * @return The misc field
	 */
	public String getMisc();

	public void init(String primaryType, Integer ownerId);
}
