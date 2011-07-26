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

package de.iritgo.aktera.servers;


import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.commons.collections.SequencedHashMap;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;


/**
 * @author Michael Nash
 *
 * A version of Context that implements an equals method, and has
 * the ability to have it's contents extracted into a SequencedHashMap,
 * for serialization purposes (when synchronizing Contexts in a cluster).
 */
public class ComparableContext extends DefaultContext
{
	/**
	 * Constructor for ComparableContext.
	 */
	public ComparableContext ()
	{
		super ();
	}

	/**
	 * Constructor for ComparableContext.
	 * @param contextData
	 * @param parent
	 */
	public ComparableContext (Map contextData, Context parent)
	{
		super (contextData, parent);
	}

	/**
	 * Constructor for ComparableContext.
	 * @param contextData
	 */
	public ComparableContext (Map contextData)
	{
		super (contextData);
	}

	/**
	 * Constructor for ComparableContext.
	 * @param parent
	 */
	public ComparableContext (Context parent)
	{
		super (parent);
	}

	public void put (final Object key, final Object value) throws IllegalStateException
	{
		if (! (key instanceof Serializable))
		{
			throw new IllegalStateException ("Context key is not serializable");
		}

		if (! (value instanceof Serializable) && (value != null))
		{
			throw new IllegalStateException ("Context value is not serializable");
		}

		super.put (key, value);
	}

	public boolean equals (Object o)
	{
		if (o == null)
		{
			return false;
		}

		if (! (o instanceof Context))
		{
			return false;
		}

		Map m = this.getContextData ();
		Context oc = (Context) o;
		Object oneKey = null;
		Object oneValue = null;

		for (Iterator i = m.keySet ().iterator (); i.hasNext ();)
		{
			oneKey = i.next ();
			oneValue = m.get (oneKey);

			try
			{
				if (! oc.get (oneKey).equals (oneValue))
				{
					return false;
				}
			}
			catch (ContextException ce)
			{
				return false;
			}
		}

		return true;
	}

	/*
	 * Needs to be implemented for this class to be defined correctly .
	 * @see java.lang.Object#hashCode()
	        public int hashCode()
	        {
	                return super.hashCode();
	        }
	 */
	public String toString ()
	{
		StringBuffer res = new StringBuffer ("{ComparableContext:");
		Map contextData = getContextData ();
		int count = 0;

		for (Iterator i = contextData.keySet ().iterator (); i.hasNext ();)
		{
			Object key = i.next ();

			count++;

			Object value = contextData.get (key);

			res.append (key.toString () + "=" + value.toString ());
			res.append (",");
		}

		if (count == 0)
		{
			res.append ("(no items)");
		}

		res.append ("}");

		return res.toString ();
	}

	public SequencedHashMap getMap ()
	{
		SequencedHashMap newMap = new SequencedHashMap ();
		Map contextData = getContextData ();

		for (Iterator i = contextData.keySet ().iterator (); i.hasNext ();)
		{
			Object key = i.next ();
			Object value = contextData.get (key);

			newMap.put (key, value);
		}

		return newMap;
	}

	public void putMap (SequencedHashMap sm)
	{
		for (Iterator i = sm.keySet ().iterator (); i.hasNext ();)
		{
			Object key = i.next ();
			Object value = sm.get (key);

			put (key, value);
		}
	}

	public Map<Object, Object> getData ()
	{
		return getContextData ();
	}

	public ComparableContext getParentContext ()
	{
		if (getParent () != null && getParent () instanceof ComparableContext)
		{
			return (ComparableContext) getParent ();
		}
		else
		{
			return null;
		}
	}
}
