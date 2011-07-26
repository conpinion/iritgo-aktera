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


import de.iritgo.aktera.core.exception.NestedException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;


/**
 * Used to report a problem at the persistence level
 *
 * @author Michael Nash
 */
public class PersistenceException extends NestedException
{
	private Map errorMap = null;

	/**
	 * Most PersistenceException will be created using one of the custom
	 * constructors. Basically, there are three arguments, each of which is
	 * optional, that always appear in the same order: 1: the string message
	 * for the exception being created 2: the nested exception (or nested
	 * throwable, actually) 3: the exception "key" - for internationalization
	 *
	 * Any of these can be left out or passed as null, as required in the
	 * situation.
	 */

	/**
	 * Default constructor
	 */
	public PersistenceException ()
	{
		super ();
	} /* PersistenceException() */

	/**
	 * Constructor with a single message, no key
	 *
	 * @param s
	 *            exception message
	 */
	public PersistenceException (String s)
	{
		super (s);
	} /* PersistenceException */

	/**
	 * Constructor with a single message, no key
	 *
	 * @param s
	 *            exception message
	 */
	public PersistenceException (String s, Map m)
	{
		super (s);
		addErrors (m);
	} /* PersistenceException */

	/**
	 * String message and error key
	 */
	public PersistenceException (String s, String newErrorKey)
	{
		super (s, newErrorKey);
	}

	/**
	 * Constructor with a message and a nested exception
	 *
	 * @param s
	 *            The exception message
	 * @param newNested
	 *            The nested item
	 */
	public PersistenceException (String message, Throwable newNested)
	{
		super (message, newNested);
	} /* PersistenceException(String, Throwable) */

	/**
	 * Constructor with a message and a nested exception
	 *
	 * @param s
	 *            The exception message
	 * @param newNested
	 *            The nested item
	 */
	public PersistenceException (String message, Throwable newNested, Map m)
	{
		super (message, newNested);
		addErrors (m);
	} /* PersistenceException(String, Throwable) */

	/**
	 * Constructor with a single message and a nested exception with error key
	 *
	 * @param s
	 *            The exception message
	 * @param newNested
	 *            The nested item
	 * @param errorKey
	 *            A string key to the messages bundle
	 */
	public PersistenceException (String message, Throwable newNested, String newErrorKey)
	{
		super (message, newNested, newErrorKey);
	} /* PersistenceException(String, Throwable, String) */

	public PersistenceException (String message, Throwable newNested, String newErrorKey, Map m)
	{
		super (message, newNested, newErrorKey);
		addErrors (m);
	}

	/**
	 * Constructor with no message and a nested exception
	 *
	 * @param newNested
	 *            The nested exception
	 */
	public PersistenceException (Throwable newNested)
	{
		super (newNested);
	} /* PersistenceException(Throwable) */

	public PersistenceException (Throwable newNested, Map m)
	{
		super (newNested);
		addErrors (m);
	}

	/**
	 * Constructor with no message and a nested exception, but with an error
	 * key
	 *
	 * @param newNested
	 *            The nested exception
	 */
	public PersistenceException (Throwable newNested, String newErrorKey)
	{
		super (newNested, newErrorKey);
	} /* PersistenceException(Throwable) */

	public PersistenceException (Throwable newNested, String newErrorKey, Map m)
	{
		super (newNested, newErrorKey);
		addErrors (m);
	} /* PersistenceException(Throwable) */

	/**
	 * Add a Map of errors as detail errors for this overall exception
	 */
	public void addErrors (Map m)
	{
		errorMap = m;
	}

	public Map getErrors ()
	{
		return errorMap;
	}

	/**
	 * Extend printStackTrace to handle the nested Error Map
	 *
	 * @param p
	 *            The PrintStream to write the exception messages into
	 */
	public void printStackTrace (PrintStream p)
	{
		super.printStackTrace (p);

		if (errorMap != null)
		{
			p.println ("Error Map:-------------");

			String oneKey = null;
			Object val = null;

			for (Iterator i = errorMap.keySet ().iterator (); i.hasNext ();)
			{
				oneKey = i.next ().toString ();
				p.print (oneKey + ":");
				val = errorMap.get (oneKey);

				if (val instanceof Throwable)
				{
					((Throwable) val).printStackTrace (p);
				}
				else
				{
					p.println (val.toString ());
				}
			}
		}
	}

	/**
	 * Extend printStackTrace to handle the nested Error Map
	 *
	 * @param p
	 *          The PrintWriter to write the exception messages into
	 */
	public void printStackTrace (PrintWriter p)
	{
		super.printStackTrace (p);

		if (errorMap != null)
		{
			p.println ("Error Map:-------------");

			String oneKey = null;
			Object val = null;

			for (Iterator i = errorMap.keySet ().iterator (); i.hasNext ();)
			{
				oneKey = i.next ().toString ();
				p.print (oneKey + ":");
				val = errorMap.get (oneKey);

				if (val instanceof Throwable)
				{
					((Throwable) val).printStackTrace (p);
				}
				else
				{
					p.println (val.toString ());
				}
			}
		}
	}
} /* PersistenceException */
