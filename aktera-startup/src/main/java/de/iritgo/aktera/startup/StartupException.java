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

package de.iritgo.aktera.startup;


/**
 * This exception is thrown if an error occurred during the execution
 * of a startup handler.
 */
public class StartupException extends Exception
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public StartupException()
	{
	}

	/**
	 * @param message
	 */
	public StartupException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public StartupException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StartupException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
