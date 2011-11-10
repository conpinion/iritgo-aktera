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

package de.iritgo.aktera.clients.direct;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author pjbrown
 *
 * This class periodically calls thread.isAlive();
 * When it determines that thread is no longer alive,
 *  then it tells the keelDirectServer to exit it's thread,
 * and then this thread exits itself.
 * If thread.isAlive() is true, then this thread sleeps before awakening
 * and trying again.
 * @param thread
 * @param keelDirectServer
 **/
public class MonitorForKeelDirectServer extends Thread
{
	Thread threadToWatch = null;

	Thread keelDirectServerInstance = null;

	Class keelDirectServerClazz = null;

	/**
	 * This thread periodically calls thread.isAlive();
	 * When it determines that thread is no longer alive,
	 * then it tells the keelDirectServer to exit it's thread,
	 * and then this thread exits itself.
	 * If thread.isAlive() is true, then this thread sleeps before awakening
	 * and trying again.
	 * @param thread
	 * @param keelDirectServer
	 */
	public MonitorForKeelDirectServer()
	{
		super(null, null, "MonitorForKeelDirectServer");
	}

	/**
	 * Cause this thread to sleep until theThreadToWatch.isAlive() returns false... at which point this method
	 * calls stopRuningKeelDirectServer() to tell the keelDirectServer thread to die, and then this thread dies itself.
	 * Do not start this thread until after you have started threadToWatch.
	 */
	public void run()
	{
		try
		{
			threadToWatch.getThreadGroup().getParent().list();
			sleep(5000);

			while (threadToWatch.isAlive())
			{
				sleep(5000); //Sleep for five seconds
			}

			print(" ");
			print("Thread died...");
			print(" ");
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}

		// The code below is equivalent to calling KeelDirectServer.dispose();   
		try
		{
			Method m;

			m = keelDirectServerClazz.getMethod("dispose", (Class[]) null);
			//Object[] parms = {};
			print("***> calling " + m.getName() + "   to tell the keel direct sever to stop.");
			m.invoke(keelDirectServerInstance, (Object[]) null);
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		keelDirectServerInstance = null; //Clear for Garbage Collection
		threadToWatch = null; //Clear for Garbage Collection
		keelDirectServerClazz = null; //Clear for GC

		return; //Allow this thread to die too.
	}

	/**
	 * @param thread
	 */
	public void setThreadToWatch(Thread thread)
	{
		threadToWatch = thread;

		if (thread != null)
		{
			print("***>Thread to watch set to " + thread.getName());
		}
		else
		{
			print("***>Thread to watch set to null ");
		}
	}

	/**
	 * @param keelDirectServer
	 */
	public void setKeelDirectServer(Thread keelDirectServer)
	{
		keelDirectServerInstance = keelDirectServer;

		if (keelDirectServer != null)
		{
			print("***>keelDirectServer set to " + keelDirectServer.getName());
		}
		else
		{
			print("***>keelDirectServer set to null ");
		}
	}

	/**
	 *
	 * @param theKeelDirectServerClazz
	 */
	public void setKeelDirectServerClass(Class theKeelDirectServerClazz)
	{
		keelDirectServerClazz = theKeelDirectServerClazz;

		if (theKeelDirectServerClazz != null)
		{
			print("***>theKeelDirectServerClazz set to " + theKeelDirectServerClazz.getName());
		}
		else
		{
			print("***>theKeelDirectServerClazz set to null ");
		}
	}

	/**
	 * Causes text to appear on the std error.
	 * @param string
	 */
	public final void print(String string)
	{
		System.err.println(this.getClass().getName() + ":" + string);
	}
}
