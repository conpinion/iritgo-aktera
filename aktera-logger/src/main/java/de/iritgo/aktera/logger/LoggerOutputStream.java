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


import de.iritgo.aktera.logger.Logger.Level;
import java.io.IOException;
import java.io.OutputStream;


/**
 * An output stream that writes to a logger service.
 */
public class LoggerOutputStream extends OutputStream
{
	/** The logger to write to */
	private Logger logger;

	/** The log level to use */
	private Level level;

	/** Used to collect output lines */
	private StringBuilder buf = new StringBuilder ();

	/**
	 * Initialize a new LoggerOutputStream.
	 *
	 * @param logger The logger to write to
	 * @param level The log level to use
	 */
	public LoggerOutputStream (Logger logger, Level level)
	{
		this.logger = logger;
		this.level = level;
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write (int b) throws IOException
	{
		if ((int) '\n' != b)
		{
			buf.append ((char) b);
		}
		else
		{
			switch (level)
			{
				case DEBUG:
					logger.debug (buf.toString ());

					break;

				case INFO:
					logger.info (buf.toString ());

					break;

				case WARN:
					logger.warn (buf.toString ());

					break;

				case ERROR:
					logger.error (buf.toString ());

					break;

				case FATAL_ERROR:
					logger.fatalError (buf.toString ());

					break;
			}

			buf = new StringBuilder ();
		}
	}
}
