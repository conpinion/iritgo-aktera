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

package de.iritgo.aktera.persist.base;


import de.iritgo.aktera.persist.Relation;
import java.util.Set;
import java.util.TreeSet;


/**
 * A relationship between two persistents.
 * This can take the form of a master/detail relationship, a lookup relationship
 * (where one or more fields are "looked up" from another persistent),
 * or a generalized relation, enforced via referential integrity or not.
 *
 * @author Michael Nash
 *  @version $Revision: 1.1 $ $Date: 2004/03/27 16:02:19 $
 */
public class DefaultRelation implements Relation
{
	private int relationType = - 1;

	private String name = null;

	private String fromPersistent = null;

	private String toPersistent = null;

	private Set fromFields = new TreeSet ();

	private Set toFields = new TreeSet ();

	private Set lookedUpFields = new TreeSet ();

	/**
	 * Constructor for Relation.
	 */
	public DefaultRelation (int newType, String newName, String newFrom, String newTo)
	{
		super ();

		relationType = newType;
		name = newName;
		toPersistent = newTo;
		fromPersistent = newFrom;
	}

	public String getFromPersistent ()
	{
		return fromPersistent;
	}

	public String getToPersistent ()
	{
		return toPersistent;
	}

	public int getType ()
	{
		return relationType;
	}

	public void addFromField (String newField)
	{
		fromFields.add (newField);
	}

	public void addToField (String newField)
	{
		toFields.add (newField);
	}

	public void addLookedUpField (String newField)
	{
		lookedUpFields.add (newField);
	}

	public Set getFromFields ()
	{
		return fromFields;
	}

	public Set getToFields ()
	{
		return toFields;
	}

	public Set getLookedUpFields ()
	{
		return lookedUpFields;
	}
}
