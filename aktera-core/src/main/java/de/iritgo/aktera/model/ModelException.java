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


/**
 * ModelException.java
 *
 * The exception returned by a Request to a model object. It is a
 * "chained" exception, and typically contains another exception that
 * shows the actual low-level problem.
 */
import de.iritgo.aktera.core.exception.NestedException;


/**
 * @version  $Revision: 1.1 $  $Date: 2003/12/29 07:01:33 $
 * @author: Michael Nash
 */
public class ModelException extends NestedException
{
	/**
	 * Most ModelException will be created using one of the custom
	 * constructors. Basically, there are three arguments, each
	 * of which is optional, that always appear in the same order:
	 *
	 * 1: the string message for the exception being created
	 * 2: the nested exception (or nested throwable, actually)
	 * 3: the exception "key" - for internationalization
	 *
	 * Any of these can be left out or passed as null, as required in the
	 * situation.
	 */

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public ModelException()
	{
		super();
	} /* ModelException() */

	/**
	 * Constructor with a single message, no key
	 *
	 * @param   s exception message
	 */
	public ModelException(String s)
	{
		super(s);
	} /*  ModelException */

	/**
	 * String message and error key
	 */
	public ModelException(String s, String newErrorKey)
	{
		super(s, newErrorKey);
	}

	/**
	 * Constructor with a message and a nested exception
	 *
	 * @param   s The exception message
	 * @param   newNested The nested item
	 */
	public ModelException(String message, Throwable newNested)
	{
		super(message, newNested);
	} /* ModelException(String, Throwable) */

	/**
	 * Constructor with a single message and a nested exception with error key
	 *
	 * @param   s The exception message
	 * @param   newNested The nested item
	 * @param   errorKey A string key to the messages bundle
	 */
	public ModelException(String message, Throwable newNested, String newErrorKey)
	{
		super(message, newNested, newErrorKey);
	} /* ModelException(String, Throwable, String) */

	/**
	 * Constructor with no message and a nested exception
	 *
	 * @param   newNested The nested exception
	 */
	public ModelException(Throwable newNested)
	{
		super(newNested);
	} /* ModelException(Throwable) */

	/**
	 * Constructor with no message and a nested exception, but with an error key
	 *
	 * @param   newNested The nested exception
	 */
	public ModelException(Throwable newNested, String newErrorKey)
	{
		super(newNested, newErrorKey);
	} /* ModelException(Throwable) */
} /* ModelException */
