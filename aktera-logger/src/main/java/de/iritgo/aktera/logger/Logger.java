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

package de.iritgo.aktera.logger;


/**
 * Logger service.
 */
public interface Logger extends org.apache.avalon.framework.logger.Logger
{
	/**
	 * Log level to use.
	 */
	public enum Level
	{
		DEBUG, INFO, WARN, ERROR, FATAL_ERROR;
	}

	/**
	 * Log a debug message.
	 *
	 * @param message the message
	 */
	public void debug (String message);

	/**
	 * Log a debug message.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public void debug (String message, Throwable throwable);

	/**
	 * Determine if messages of priority "debug" will be logged.
	 *
	 * @return true if "debug" messages will be logged
	 */
	public boolean isDebugEnabled ();

	/**
	 * Log a info message.
	 *
	 * @param message the message
	 */
	public void info (String message);

	/**
	 * Log a info message.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public void info (String message, Throwable throwable);

	/**
	 * Determine if messages of priority "info" will be logged.
	 *
	 * @return true if "info" messages will be logged
	 */
	public boolean isInfoEnabled ();

	/**
	 * Log a warn message.
	 *
	 * @param message the message
	 */
	public void warn (String message);

	/**
	 * Log a warn message.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public void warn (String message, Throwable throwable);

	/**
	 * Determine if messages of priority "warn" will be logged.
	 *
	 * @return true if "warn" messages will be logged
	 */
	public boolean isWarnEnabled ();

	/**
	 * Log a error message.
	 *
	 * @param message the message
	 */
	public void error (String message);

	/**
	 * Log a error message.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public void error (String message, Throwable throwable);

	/**
	 * Determine if messages of priority "error" will be logged.
	 *
	 * @return true if "error" messages will be logged
	 */
	public boolean isErrorEnabled ();

	/**
	 * Log a fatalError message.
	 *
	 * @param message the message
	 */
	public void fatalError (String message);

	/**
	 * Log a fatalError message.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public void fatalError (String message, Throwable throwable);

	/**
	 * Determine if messages of priority "fatalError" will be logged.
	 *
	 * @return true if "fatalError" messages will be logged
	 */
	public boolean isFatalErrorEnabled ();
}
