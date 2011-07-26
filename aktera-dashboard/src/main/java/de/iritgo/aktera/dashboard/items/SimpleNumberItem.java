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

package de.iritgo.aktera.dashboard.items;


import de.iritgo.aktera.dashboard.DashboardItem;


public interface SimpleNumberItem extends DashboardItem
{
	/**
	 * Return the label for the board.
	 *
	 * @return The label
	 */
	public String getLabel ();

	/**
	 * Return the description of the board
	 *
	 * @return
	 */
	public String getDescription ();

	/**
	 * Return the measuring unit for the board.
	 *
	 * @return The measuring unit
	 */
	public String getMeasuringUnit ();

	/**
	 * The current value for the board
	 *
	 * @return The value
	 */
	public int getValue ();

	/**
	 * The max value
	 *
	 * @return The max value
	 */
	public int getMaxValue ();

	/**
	 * Return true if the given value is a green value.
	 *
	 * @param value The value
	 * @return True if a green value
	 */
	public boolean isGreenValue (int value);

	/**
	 * Return true if the given value is a yellow value.
	 *
	 * @param value The value
	 * @return True if a yellow value
	 */
	public boolean isYellowValue (int value);

	/**
	 * Return true if the given value is a red value.
	 *
	 * @param value The value
	 * @return True if a red value
	 */
	public boolean isRedValue (int value);
}
