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

package de.iritgo.aktera.core.log;


import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.logging.Log;


public class CommonsLoggingWrapperForAvalonLogging implements Log
{
	private Logger log;

	public CommonsLoggingWrapperForAvalonLogging (Logger log)
	{
		this.log = log;
	}

	public void debug (Object message)
	{
		log.debug (String.valueOf (message));
	}

	public void debug (Object message, Throwable t)
	{
		log.debug (String.valueOf (message), t);
	}

	public void error (Object message)
	{
		log.error (String.valueOf (message));
	}

	public void error (Object message, Throwable t)
	{
		log.error (String.valueOf (message), t);
	}

	public void fatal (Object message)
	{
		log.fatalError (String.valueOf (message));
	}

	public void fatal (Object message, Throwable t)
	{
		log.fatalError (String.valueOf (message), t);
	}

	public void info (Object message)
	{
		log.info (String.valueOf (message));
	}

	public void info (Object message, Throwable t)
	{
		log.info (String.valueOf (message), t);
	}

	public boolean isDebugEnabled ()
	{
		return log.isDebugEnabled ();
	}

	public boolean isErrorEnabled ()
	{
		return false;
	}

	public boolean isFatalEnabled ()
	{
		return false;
	}

	public boolean isInfoEnabled ()
	{
		return false;
	}

	public boolean isTraceEnabled ()
	{
		return false;
	}

	public boolean isWarnEnabled ()
	{
		return false;
	}

	public void trace (Object message)
	{
	}

	public void trace (Object message, Throwable t)
	{
	}

	public void warn (Object message)
	{
	}

	public void warn (Object message, Throwable t)
	{
	}
}
