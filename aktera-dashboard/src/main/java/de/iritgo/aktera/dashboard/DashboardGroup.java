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

package de.iritgo.aktera.dashboard;


import java.util.List;
import java.util.Locale;


public interface DashboardGroup extends DashboardItem
{
	/**
	 * Return the title of the dashboard box
	 *
	 * @return The title
	 */
	public String getTitle ();

	/**
	 * Return the number of columns
	 *
	 * @return The number of columns
	 */
	public int getNumberOfColumns ();

	/**
	 * Return the dashboard items
	 *
	 * @return The list of all dashboard items
	 */
	public List<DashboardItem> getDashboardItems ();

	/**
	 * Return a new instance of the bean.
	 *
	 * @return The new instance
	 */
	public DashboardGroup newInstance ();
}
