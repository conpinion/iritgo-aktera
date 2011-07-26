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

package de.iritgo.aktera.tools;


import de.iritgo.simplelife.process.NullStreamHandler;
import de.iritgo.simplelife.string.StringTools;
import java.io.File;
import java.io.IOException;


/**
 * Useful system methods.
 */
public final class SystemTools
{
	/**
	 * Helper method to wait for a process.
	 *
	 * @param proc The process to wait for.
	 */
	public static int waitForProcess (Process proc)
	{
		int res = 0;

		try
		{
			res = proc.waitFor ();
		}
		catch (InterruptedException x)
		{
		}

		return res;
	}

	/**
	 * Start a process and wait for it's termination.
	 * The command is searched relative to the AKTERA_FS_ROOT-
	 *
	 * @param cmd The command path
	 * @param args The command arguments
	 * @throws IOException
	 */
	public static Process startAkteraProcess (String cmd, String args) throws IOException
	{
		File cmdFile = FileTools.newAkteraFile (cmd);
		Process proc = Runtime.getRuntime ().exec (
						cmdFile.getAbsolutePath () + (! StringTools.isTrimEmpty (args) ? " " + args : ""));

		return proc;
	}

	/**
	 * Start a process and wait for it's termination.
	 * The command is searched relative to the AKTERA_FS_ROOT-
	 *
	 * @param cmd The command path
	 * @param args The command arguments
	 * @throws IOException
	 */
	public static int startAndWaitAkteraProcess (String cmd, String args) throws IOException
	{
		Process proc = startAkteraProcess (cmd, args);

		new NullStreamHandler (proc.getInputStream ());
		new NullStreamHandler (proc.getErrorStream ());

		return waitForProcess (proc);
	}

	/**
	 * Start a process and wait for it's termination.
	 * The command is searched relative to the AKTERA_FS_ROOT-
	 *
	 * @param cmd The command path
	 * @param args The command arguments
	 * @param dir The working directory
	 * @throws IOException
	 */
	public static Process startAkteraProcess (String cmd, String args, String dir) throws IOException
	{
		File cmdFile = FileTools.newAkteraFile (cmd);
		File dirFile = FileTools.newAkteraFile (dir);
		Process proc = Runtime.getRuntime ().exec (
						cmdFile.getAbsolutePath () + (! StringTools.isTrimEmpty (args) ? " " + args : ""), null,
						dirFile);

		return proc;
	}

	/**
	 * Start a process and wait for it's termination.
	 * The command is searched relative to the AKTERA_FS_ROOT-
	 *
	 * @param cmd The command path
	 * @param args The command arguments
	 * @param dir The working directory
	 * @throws IOException
	 */
	public static int startAndWaitAkteraProcess (String cmd, String args, String dir) throws IOException
	{
		Process proc = startAkteraProcess (cmd, args, dir);

		new NullStreamHandler (proc.getInputStream ());
		new NullStreamHandler (proc.getErrorStream ());

		return waitForProcess (proc);
	}

	/**
	 * Try to set a network interface to the given ip address and mas.
	 *
	 * @param interfaceName The interface name
	 * @param ipAddress The ip address
	 * @param mask The network mask
	 * @return True if successfull set
	 */
	public static boolean setSystemIPAddress (String interfaceName, String ipAddress, String mask)
	{
		try
		{
			startAndWaitAkteraProcess ("/usr/bin/sudo", "/sbin/ifconfig " + interfaceName + " " + ipAddress
							+ " netmask " + mask + " up", "");
		}
		catch (IOException x)
		{
			return false;
		}

		return true;
	}

	public static boolean isDeveloperDeployment ()
	{
		return "developer".equals (System.getProperty ("de.iritgo.aktera.deployment"));
	}
}
