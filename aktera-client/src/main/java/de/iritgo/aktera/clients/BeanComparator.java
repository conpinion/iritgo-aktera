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

package de.iritgo.aktera.clients;


import org.apache.commons.beanutils.DynaBean;
import java.text.Collator;
import java.util.Comparator;


/**
 * @author root
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BeanComparator implements Comparator
{
	private String valueName = null;

	/**
	 * Constructor for BeanComparator.
	 */
	public BeanComparator(String newValue)
	{
		super();
		valueName = newValue;
	}

	/**
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2)
	{
		int returnValue = - 1;

		/* Can't compare anything except dynabeans */
		if ((! (o1 instanceof DynaBean)) || (! (o2 instanceof DynaBean)))
		{
			throw new IllegalArgumentException("Objects are not dynabeans");
		}

		DynaBean db1 = (DynaBean) o1;
		DynaBean db2 = (DynaBean) o2;
		Object oneVal = db1.get(valueName);
		Object twoVal = db2.get(valueName);

		if ((oneVal == null) && (twoVal == null))
		{
			returnValue = 0;
		}

		if (oneVal == null)
		{
			returnValue = - 1;
		}

		if (twoVal == null)
		{
			returnValue = 1;
		}

		Collator c = Collator.getInstance();

		returnValue = c.compare(oneVal.toString(), twoVal.toString());

		return returnValue;
	}
}
