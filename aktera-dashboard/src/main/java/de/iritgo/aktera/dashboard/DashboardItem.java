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


import java.util.Locale;


public interface DashboardItem
{
	/**
	 * Return the unique id of the dashboard item
	 *
	 * @return The id
	 */
	public String getId();

	/**
	 * Return the label for the board.
	 *
	 * @return The label
	 */
	public String getLabel();

	/**
	 * Return the bundle for the item
	 *
	 * @return The bundle
	 */
	public String getBundle();

	/**
	 * Set the bundle
	 *
	 * @param bundle The bundle
	 */
	public void setBundle(String bundle);

	/**
	 * Return the description of the board
	 *
	 * @return Desc
	 */
	public String getDescription();

	/**
	 * generate site attributes visitor
	 *
	 * @param siteAttributeVisitor The site attribute visitor
	 */
	public void generate(GroupVisitor visitor);

	/**
	 * Return the render template/include file
	 *
	 * @return The render file
	 */
	public String getRenderFile();

	/**
	 * Update the item internal state
	 */
	public void update();

	/**
	 * Set the locale
	 *
	 * @param locale The locale
	 */
	public void setLocale(Locale locale);
}
