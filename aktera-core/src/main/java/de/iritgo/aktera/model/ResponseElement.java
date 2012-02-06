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

package de.iritgo.aktera.model;


import java.util.List;
import java.util.Map;


/**
 * A ResponseElement is one item that is returned from invoking a state on a
 * particular model. It may contain other nested elements.
 */
public interface ResponseElement
{
	/* Add a nested response element
	 */
	public void add(ResponseElement re);

	public void remove(ResponseElement re);

	/**
	 * Set the value of an attribute of this element
	 */
	public void setAttribute(String key, Object value);

	public Object getAttribute(String key);

	public String getName();

	public void setName(String newName);

	public List getAll();

	public Map getAttributes();

	public void removeAttribute(String key);
}
