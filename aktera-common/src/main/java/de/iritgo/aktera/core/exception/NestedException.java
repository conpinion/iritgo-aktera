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

package de.iritgo.aktera.core.exception;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Base exception that allows nesting of other exceptions.
 * Most NestedExceptions will be created using one of the custom
 * constructors. Basically, there are three arguments, each
 * of which is optional, that always appear in the same order:
 *
 * 1: the string message for the exception being created
 * 2: the nested exception (or nested throwable, actually)
 * 3: the exception "key" - for internationalization
 *
 * Any of these can be left out or passed as null, as required in the
 * situation.
 *
 * @author: Michael Nash
 */
public class NestedException extends Exception
{
	private String errorKey = null;

	private StringBuffer addedStacktrace = new StringBuffer ();

	private StringBuffer addedMessages = new StringBuffer ();

	private String nestedExceptionType = getClass ().getName ();

	/**
	 * Default constructor
	 */
	public NestedException ()
	{
		super ();
	}

	/**
	 * Constructor with a single message, no key
	 *
	 * @param   s exception message
	 */
	public NestedException (String s)
	{
		super (s);
	}

	/**
	 * String message and error key
	 */
	public NestedException (String s, String newErrorKey)
	{
		super (s);
		errorKey = newErrorKey;
	}

	/**
	 * Constructor with a message and a nested exception
	 *
	 * @param   s The exception message
	 * @param   newNested The nested item
	 */
	public NestedException (String message, Throwable newNested)
	{
		super (message);
		setNested (newNested);
	}

	/**
	 * Constructor with a single message and a nested exception with error key
	 *
	 * @param   s The exception message
	 * @param   newNested The nested item
	 * @param   errorKey A string key to the messages bundle
	 */
	public NestedException (String message, Throwable newNested, String newErrorKey)
	{
		super (message);
		setNested (newNested);
		errorKey = newErrorKey;
	}

	/**
	 * Constructor with no message and a nested exception
	 *
	 * @param   newNested The nested exception
	 */
	public NestedException (Throwable newNested)
	{
		super ();
		setNested (newNested);
	}

	/**
	 * Constructor with no message and a nested exception, but with an error key
	 *
	 * @param   newNested The nested exception
	 */
	public NestedException (Throwable newNested, String newErrorKey)
	{
		super ();
		setNested (newNested);
		errorKey = newErrorKey;
	}

	public void addToMessage (String s)
	{
		addedMessages.append (", " + s);
	}

	private void setNested (Throwable newNested)
	{
		addToStack (getStackTraceAsString (newNested));
		addToMessage (newNested.getMessage ());
		nestedExceptionType = newNested.getClass ().getName ();
	}

	public String getNestedExceptionType ()
	{
		return nestedExceptionType;
	}

	/**
	 * Extend getMessage to return the nested message
	 *
	 * @return The extended message.
	 */
	public String getMessage ()
	{
		return super.getMessage () + addedMessages.toString ();
	}

	/**
	 * Return the error key if one was supplied
	 */
	public String getErrorKey ()
	{
		return errorKey;
	}

	/**
	 * Extend printStackTrace to handle the nested exception correctly.
	 */
	public void printStackTrace ()
	{
		printStackTrace (System.err);
	}

	/**
	 * Extend printStackTrace to handle the nested Exception
	 *
	 * @param   p The PrintStream to write the exception messages into
	 */
	public void printStackTrace (PrintStream p)
	{
		super.printStackTrace (p);
		p.println (addedStacktrace.toString ());
	}

	/**
	 * Extend printStackTrace to handle the nested Exception
	 *
	 * @param   p The PrintWriter to write the exception messages into
	 */
	public void printStackTrace (PrintWriter p)
	{
		super.printStackTrace (p);
		p.println (addedStacktrace.toString ());
	}

	/**
	 * Add a specified string to the stack trace of this exception.
	 *
	 * @param s The string to add: presumably the string representation of another stack trace
	 */
	public void addToStack (String s)
	{
		addedStacktrace.append ("\n-----------------------------------------\n" + s + "\n");
	}

	public String getStackTraceAsString ()
	{
		return getStackTraceAsString (this);
	}

	private String getStackTraceAsString (Throwable t)
	{
		StringWriter stw = new StringWriter ();
		PrintWriter pw = new PrintWriter (stw);

		t.printStackTrace (pw);

		return stw.getBuffer ().toString ();
	}
}
