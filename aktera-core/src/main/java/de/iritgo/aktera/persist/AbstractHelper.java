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

package de.iritgo.aktera.persist;


/**
 * Abstract helper is simply a handy superclass that implements
 * the Helper interface. Sub-classes can then just override the methods
 * they are interested in, rather than having to implement the entire
 * Helper interface themselves. See the Helper interface for details on what Helper objects are all about.
 */
public abstract class AbstractHelper implements Helper
{
	private Persistent myPersistent = null;

	protected Persistent getPersistent ()
	{
		return myPersistent;
	}

	public void setPersistent (Persistent current)
	{
		myPersistent = current;
	}

	public void beforeUpdate (Persistent current)
	{
		setPersistent (current);
	}

	public void afterUpdate (Persistent current)
	{
		setPersistent (current);
	}

	public void beforeAdd (Persistent current)
	{
		setPersistent (current);
	}

	public void afterAdd (Persistent current)
	{
		setPersistent (current);
	}

	public void beforeDelete (Persistent current)
	{
		setPersistent (current);
	}

	public void afterDelete (Persistent current)
	{
		setPersistent (current);
	}

	public void beforeClear (Persistent current)
	{
		setPersistent (current);
	}

	public void beforeQuery (Persistent current)
	{
		setPersistent (current);
	}

	public void afterQuery (Persistent current)
	{
		setPersistent (current);
	}

	public void beforeSetField (String fieldName, Object oldValue, Object newValue)
	{
	}

	public void afterSetField (String fieldName, Object oldValue, Object newValue)
	{
	}
}
