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
public class NullLoggerImpl implements Logger
{
	public NullLoggerImpl()
	{
	}

	public org.apache.avalon.framework.logger.Logger getChildLogger(String name)
	{
		return null;
	}

	public void debug(String message)
	{
	}

	public void debug(String message, Throwable throwable)
	{
	}

	public void error(String message)
	{
	}

	public void error(String message, Throwable throwable)
	{
	}

	public void fatalError(String message)
	{
	}

	public void fatalError(String message, Throwable throwable)
	{
	}

	public void info(String message)
	{
	}

	public void info(String message, Throwable throwable)
	{
	}

	public boolean isDebugEnabled()
	{
		return false;
	}

	public boolean isErrorEnabled()
	{
		return false;
	}

	public boolean isFatalErrorEnabled()
	{
		return false;
	}

	public boolean isInfoEnabled()
	{
		return false;
	}

	public boolean isWarnEnabled()
	{
		return false;
	}

	public void warn(String message)
	{
	}

	public void warn(String message, Throwable throwable)
	{
	}
}
