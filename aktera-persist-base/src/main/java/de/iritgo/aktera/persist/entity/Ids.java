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

package de.iritgo.aktera.persist.entity;


import java.io.Serializable;


public class Ids implements Serializable
{
	private String table_name = null;

	private Integer next_id = null;

	/**
	 * @return
	 */
	public Integer getNext_id ()
	{
		return next_id;
	}

	/**
	 * @param next_id
	 */
	public void setNext_id (Integer next_id)
	{
		this.next_id = next_id;
	}

	/**
	 * @return
	 */
	public String getTable_name ()
	{
		return table_name;
	}

	/**
	 * @param table_name
	 */
	public void setTable_name (String table_name)
	{
		this.table_name = table_name;
	}
}
