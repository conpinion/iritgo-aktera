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

package de.iritgo.aktera.base.logger;


import org.apache.log.ContextMap;
import org.apache.log.LogEvent;
import org.apache.log.Logger;
import org.apache.log.format.PatternFormatter;
import org.apache.log.util.StackIntrospector;


public class ExtendedPatternFormatter extends PatternFormatter
{
	private static final int TYPE_METHOD = MAX_TYPE + 1;

	private static final int TYPE_THREAD = MAX_TYPE + 2;

	private static final String TYPE_METHOD_STR = "method";

	private static final String TYPE_THREAD_STR = "thread";

	/**
	 * Creation of a new extended pattern formatter.
	 *
	 * @param format the format string
	 */
	public ExtendedPatternFormatter (final String format)
	{
		super (format);
	}

	/**
	 * Retrieve the type-id for a particular string.
	 *
	 * @param type the string
	 * @return the type-id
	 */
	protected int getTypeIdFor (final String type)
	{
		if (type.equalsIgnoreCase (TYPE_METHOD_STR))
		{
			return TYPE_METHOD;
		}
		else if (type.equalsIgnoreCase (TYPE_THREAD_STR))
		{
			return TYPE_THREAD;
		}
		else
		{
			return super.getTypeIdFor (type);
		}
	}

	/**
	 * Formats a single pattern run (can be extended in subclasses).
	 *
	 * @param event the log event
	 * @param  run the pattern run to format.
	 * @return the formatted result.
	 */
	protected String formatPatternRun (final LogEvent event, final PatternRun run)
	{
		switch (run.m_type)
		{
			case TYPE_METHOD:
				return getMethod (event);

			case TYPE_THREAD:
				return getThread (event);

			default:
				return super.formatPatternRun (event, run);
		}
	}

	/**
	 * Utility method to format category.
	 *
	 * @param event the event
	 * @return the formatted string
	 */
	private String getMethod (final LogEvent event)
	{
		final ContextMap map = event.getContextMap ();

		if (null != map)
		{
			final Object object = map.get ("method");

			if (null != object)
			{
				return object.toString ();
			}
		}

		Class<?> clazz = StackIntrospector.getCallerClass (de.iritgo.aktera.logger.Logger.class, - 1);

		if (clazz == null)
		{
			clazz = StackIntrospector.getCallerClass (Logger.class, 0);
		}

		if (null == clazz)
		{
			return "UnknownMethod";
		}

		final String result = StackIntrospector.getCallerMethod (clazz);

		if (null == result)
		{
			return "UnknownMethod";
		}

		return result;
	}

	/**
	 * Utility thread to format category.
	 *
	 * @param event the even
	 * @return the formatted string
	 */
	private String getThread (final LogEvent event)
	{
		final ContextMap map = event.getContextMap ();

		if (null != map)
		{
			final Object object = map.get ("thread");

			if (null != object)
			{
				return object.toString ();
			}
		}

		return Thread.currentThread ().getName ();
	}
}
