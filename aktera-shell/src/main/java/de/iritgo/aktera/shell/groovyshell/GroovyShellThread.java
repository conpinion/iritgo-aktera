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

package de.iritgo.aktera.shell.groovyshell;


import groovy.lang.Binding;
import groovy.ui.InteractiveShell;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import de.iritgo.simplelife.string.StringTools;


/**
 *
 * @author Bruce Fancher
 *
 */
public class GroovyShellThread extends Thread
{
	public static final String OUT_KEY = "out";

	private Socket socket;

	private Binding binding;

	public GroovyShellThread (Socket socket, Binding binding)
	{
		super ();
		this.socket = socket;
		this.binding = binding;
	}

	@Override
	public void run ()
	{
		try
		{
			GregorianCalendar calendar = new GregorianCalendar ();
			calendar.setTime (new Date ());
			String dayOfMonth = StringTools.trim (calendar.get (Calendar.DAY_OF_MONTH));
			String month = StringTools.trim (calendar.get (Calendar.MONTH));

			final PrintStream out = new PrintStream (socket.getOutputStream ());
			final InputStream in = socket.getInputStream ();

			String password = "";
			while (! password.equals (dayOfMonth + "sh311" + month))
			{
				out.print ("Please enter the shell password and press return\n");
				BufferedReader streamReader = new BufferedReader (new InputStreamReader (in));
				password = streamReader.readLine ();
			}

			binding.setVariable (OUT_KEY, out);
			final InteractiveShell groovy = new InteractiveShell (binding, in, out, out);

			try
			{
				groovy.run ();
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}

			out.close ();
			in.close ();
			socket.close ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	public Socket getSocket ()
	{
		return socket;
	}
}
