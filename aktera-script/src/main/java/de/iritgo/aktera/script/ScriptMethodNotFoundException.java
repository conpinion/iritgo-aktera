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

package de.iritgo.aktera.script;


/**
 * This exception is thrown if a not existing script method
 * was called.
 */
public class ScriptMethodNotFoundException extends Exception
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Initialize a ScriptNotFoundException.
	 */
	public ScriptMethodNotFoundException()
	{
	}

	/**
	 * Initialize a ScriptNotFoundException.
	 *
	 * @param message The exception message
	 */
	public ScriptMethodNotFoundException(String message)
	{
		super(message);
	}

	/**
	 * Initialize a ScriptNotFoundException.
	 *
	 * @param cause Original exception
	 */
	public ScriptMethodNotFoundException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Initialize a ScriptNotFoundException.
	 *
	 * @param message The exception message
	 * @param cause Original exception
	 */
	public ScriptMethodNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
