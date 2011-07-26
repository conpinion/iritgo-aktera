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


import de.iritgo.aktera.core.container.KeelContainer;


/**
 *
 */
public class LoggerImpl implements Logger
{
	/** The name of the logger category to use */
	private String category;

	/** The Keel logger */
	private org.apache.avalon.framework.logger.Logger keelLogger;

	public LoggerImpl ()
	{
	}

	/**
	 * Get the name of the logger category.
	 *
	 * @return The name of the logger category
	 */
	public String getCategory ()
	{
		return category;
	}

	/**
	 * Set the name of the logger category.
	 *
	 * @param category The name of the logger category
	 */
	public void setCategory (String category)
	{
		this.category = category;
	}

	/**
	 * Get the Keel configuration.
	 *
	 * @return The keel configuration
	 */
	private org.apache.avalon.framework.logger.Logger getKeelLogger ()
	{
		if (keelLogger == null)
		{
			keelLogger = KeelContainer.defaultContainer ().getLogger (category);
		}

		return keelLogger;
	}

	public void debug (String message)
	{
		getKeelLogger ().debug (message);
	}

	public void debug (String message, Throwable throwable)
	{
		getKeelLogger ().debug (message, throwable);
	}

	public void error (String message)
	{
		getKeelLogger ().error (message);
	}

	public void error (String message, Throwable throwable)
	{
		getKeelLogger ().error (message, throwable);
	}

	public void fatalError (String message)
	{
		getKeelLogger ().fatalError (message);
	}

	public void fatalError (String message, Throwable throwable)
	{
		getKeelLogger ().fatalError (message, throwable);
	}

	public org.apache.avalon.framework.logger.Logger getChildLogger (String name)
	{
		return getKeelLogger ().getChildLogger (name);
	}

	public void info (String message)
	{
		getKeelLogger ().info (message);
	}

	public void info (String message, Throwable throwable)
	{
		getKeelLogger ().info (message, throwable);
	}

	public boolean isDebugEnabled ()
	{
		return getKeelLogger ().isDebugEnabled ();
	}

	public boolean isErrorEnabled ()
	{
		return getKeelLogger ().isErrorEnabled ();
	}

	public boolean isFatalErrorEnabled ()
	{
		return getKeelLogger ().isFatalErrorEnabled ();
	}

	public boolean isInfoEnabled ()
	{
		return getKeelLogger ().isInfoEnabled ();
	}

	public boolean isWarnEnabled ()
	{
		return getKeelLogger ().isWarnEnabled ();
	}

	public void warn (String message)
	{
		getKeelLogger ().warn (message);
	}

	public void warn (String message, Throwable throwable)
	{
		getKeelLogger ().warn (message, throwable);
	}
}
