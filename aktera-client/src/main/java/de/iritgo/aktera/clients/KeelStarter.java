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

package de.iritgo.aktera.clients;


import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.servers.direct.KeelDirectServer;
import de.iritgo.aktera.util.thread.ThreadUtil;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * This is a utility class that starts the Keel server-side
 * in its own thread using its own classloader to isolate
 * the server/client classpaths/classes.
 *
 * This class is used typically by Keel's webapp clients such as
 * Struts and Cocoon clients when the client/server needs
 * to run locally in the same JVM.
 *
 * @version $Revision: 1.2 $ $Date: 2006/03/08 12:21:33 $
 * @author Shash Chatterjee
 * Created on Nov 7, 2002
 */
public class KeelStarter
{
	private static String PREFIX = "[KeelStarter] ";

	private Thread myThread = null;

	// 	private Channel requestChannel = null;
	private LinkedBlockingQueue requestChannel = null;

	/**
	 * Create a KeelDirectServer and return to server/ui bridge
	 * @throws Exception
	 */
	protected void createKeelDirectServer () throws Exception
	{
		String configPath = System.getProperty ("keel.config.dir");

		//String contextPath = configPath + System.getProperty("file.separator") + "..";
		String libPath = configPath + System.getProperty ("file.separator") + ".."
						+ System.getProperty ("file.separator") + "lib";
		File libDir = new File (libPath);

		if (! libDir.isDirectory ())
		{
			throw new Exception ("Specified path '" + libDir.getCanonicalPath () + "' is not a directory");
		}

		libPath = libDir.getCanonicalPath () + System.getProperty ("file.separator");

		StringBuffer path = new StringBuffer ();
		String sep = System.getProperty ("path.separator");

		System.out.println (PREFIX + "Configuration dir: '" + configPath + "'");
		System.out.println (PREFIX + "Classpath dir: " + libPath);

		String[] dirList = libDir.list ();

		for (String oneFileName : dirList)
		{
			if (oneFileName.endsWith (".jar"))
			{
				path.append (libPath + oneFileName);
				path.append (sep);
			}
		}

		//First, get the class-loader that this thread should use, failing which
		// get the loader used to load current class
		ClassLoader origClassLoader = Thread.currentThread ().getContextClassLoader ();

		if (origClassLoader == null)
		{
			origClassLoader = this.getClass ().getClassLoader ();
		}

		//
		//		//Use a new URLClassLoader to allow loading from keel/lib.  We need to do this, since
		//		//we need to see if the AOP-aware class-loader is available in the Keel server lib directory.
		//		origClassLoader = new URLClassLoader(
		//				new URLClassLoaderPath(path.toString ()).getURLArray (), origClassLoader);
		//
		//		// Set the current thread's context loader, that is what the AOP-aware classloader
		//		// will use to look up it's own internal classes.
		//		Thread.currentThread ().setContextClassLoader (origClassLoader);

		// AOP- Load the weaving classloader if it is available in the classpath
		// The way it will be available is if the "svc-aop-aspectwerkz" module
		// was included during deployment.
		//		Class c = null;

		//		try
		//		{
		//			c = origClassLoader.loadClass ("de.iritgo.aktera.core.classloader.KeelWeavingClassLoader");
		//			//System.setProperty("aspectwerkz.transform.verbose", "yes");
		//			//System.setProperty("aspectwerkz.transform.dump", "*");
		//			System.setProperty (
		//				"aspectwerkz.definition.file",
		//				configPath + System.getProperty ("file.separator") + "svc-aop-aspectwerkz" +
		//				System.getProperty ("file.separator") + "keelaop.xml");
		//		}
		//		catch (ClassNotFoundException e)
		//		{
		//			//Ignore - not having this class is not necessarily an error
		//			//We will try and load the "normal" classloader next, if that fails then that is a
		//			//problem
		//		}

		//		// If the AOP classloader is not found, load the "normal" Keel classloader
		//		if (c == null)
		//		{
		//			try
		//			{
		//				c = origClassLoader.loadClass ("de.iritgo.aktera.core.classloader.KeelURLClassLoader");
		//			}
		//			catch (ClassNotFoundException e1)
		//			{
		//				System.err.println (
		//					PREFIX + "Could not instantiate KeelURLClassLoader classloader");
		//				System.exit (-1);
		//			}
		//		}
		//
		//		//Now that we have the correct class loader class, actually
		//		//instantiate the class-loader
		//		Class[] parmTypes = 
		//			{
		//				URL[].class,
		//				ClassLoader.class
		//			};
		//		Constructor constructor = c.getConstructor (parmTypes);
		//		Object[] constructorArgs =
		//			{
		//				new URLClassLoaderPath(path.toString ()).getURLArray (),
		//				origClassLoader
		//			};
		//		myClassLoader = (ClassLoader) constructor.newInstance (constructorArgs);
		ClassLoader myClassLoader = origClassLoader;

		// Use the class-loader we just instantiated ton start the actual Keel threads. 
		Thread keelDirectSeverThread = createAndStartKeelDirectServerThread (myClassLoader);
		Thread theThreadToWatch = ThreadUtil.findThread (Thread.currentThread (), "main", "StandardManager", "bdc",
						false);

		if (theThreadToWatch != null)
		{
			//ShowThreadInfo(loader, theThreadToWatch);
			createAndStartMonitorForKeelDirectServerThread (myClassLoader, theThreadToWatch, keelDirectSeverThread);

			//Added by Phil Brown to monitor a thread and tell the keelDirectServer to die when this thread dies
		}
	}

	/**
	 * @param theThreadToWatch
	 */

	//  private void ShowThreadInfo(ClassLoader loader, Thread theThreadToWatch) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	//    //ThreadUtil.showInfo();
	//    Class ThreadUtilClazz = loader.loadClass("de.iritgo.aktera.util.thread.ThreadUtil");  
	//    Class[] parmTypes1 = {Thread.class};
	//    Method m = ThreadUtilClazz.getMethod("showInfo", parmTypes1);
	//    Object[] parmArray1 = {theThreadToWatch};
	//    m.invoke(Thread.currentThread(),parmArray1);  
	//  }

	/**
	 * This method is intended to monitor on of the threads in the current context,
	 * so that it can tell the keelDirectServer to die when the current context dies.
	 * @param loader
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void createAndStartMonitorForKeelDirectServerThread (ClassLoader loader, Thread threadToWatch,
					Thread KeelDirectServerThread)
		throws ClassNotFoundException, InstantiationException, SecurityException, NoSuchMethodException,
		IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		//new MonitorForKeelDirectServer(Thread.currentThread(), keelDirectServer);
		//Begin code fore MonitorForKeelDirectServer
		Class monitorForKeelDirectServerClazz = loader
						.loadClass ("de.iritgo.aktera.clients.direct.MonitorForKeelDirectServer");
		Thread monitorForKeelDirectServer = (Thread) monitorForKeelDirectServerClazz.newInstance ();
		String currentThreadName = monitorForKeelDirectServer.getName ();

		monitorForKeelDirectServer.setName ("MonitorForKeelDirectServer (" + currentThreadName + ")");

		//monitorForKeelDirectServer.setThreadToWatch(threadToWatch);
		Class[] parmTypesForMon1 =
		{
			Thread.class
		};
		Method m = monitorForKeelDirectServerClazz.getMethod ("setThreadToWatch", parmTypesForMon1);
		Object[] parmForMon1 =
		{
			threadToWatch
		};

		m.invoke (monitorForKeelDirectServer, parmForMon1);

		//monitorForKeelDirectServer.setKeelDirectServer(myThread);
		Class[] parmTypesForMon2 =
		{
			Thread.class
		};

		m = monitorForKeelDirectServerClazz.getMethod ("setKeelDirectServer", parmTypesForMon2);

		Object[] parmForMon2 =
		{
			KeelDirectServerThread
		};

		m.invoke (monitorForKeelDirectServer, parmForMon2);

		//monitorForKeelDirectServer.setKeelDirectServerClass(monitorForKeelDirectServerClazz);
		Class keelDirectServerClazz = loader.loadClass ("de.iritgo.aktera.servers.direct.KeelDirectServer");
		Class[] parmTypesForMon3 =
		{
			Class.class
		};

		m = monitorForKeelDirectServerClazz.getMethod ("setKeelDirectServerClass", parmTypesForMon3);

		Object[] parmForMon3 =
		{
			keelDirectServerClazz
		};

		m.invoke (monitorForKeelDirectServer, parmForMon3);

		//Thread.yield();  //Explicitly give another thread a chance to run.
		monitorForKeelDirectServer.start ();

		//Start the thread that will shut down the keel direct server when this thread dies.
	}

	/**
	 *
	 * @param loader
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Thread createAndStartKeelDirectServerThread (ClassLoader loader)
		throws ClassNotFoundException, InstantiationException, SecurityException, NoSuchMethodException,
		IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Class keelDirectServerClazz = loader.loadClass ("de.iritgo.aktera.servers.direct.KeelDirectServer");

		myThread = (Thread) keelDirectServerClazz.newInstance ();
		myThread.setName ("KeelDirectServer");

		keelDirectServerClazz.getMethod ("setContextClassLoader", new Class[]
		{
			ClassLoader.class
		}).invoke (myThread, new Object[]
		{
			loader
		});

		requestChannel = getRequestChannel ();
		keelDirectServerClazz.getMethod ("setRequestChannel", new Class[]
		{
			LinkedBlockingQueue.class
		}).invoke (myThread, new Object[]
		{
			requestChannel
		});

		keelDirectServerClazz.getMethod ("start", (Class[]) null).invoke (myThread, (Object[]) null);

		return myThread;
	}

	public KeelResponse execute (KeelRequest message) throws Exception
	{
		assert message != null;

		if (myThread == null)
		{
			//Insure that each KeelStarter can hold only one KeelDirectServer
			synchronized (this)
			{
				if (myThread == null)
				{
					createKeelDirectServer ();
				}
			}
		}

		//		// 		Channel replyChannel = new LinkedQueue();
		//		LinkedBlockingQueue replyChannel = new LinkedBlockingQueue();
		//
		//		byte[] requestBytes = message.serialize ();
		//
		//		//Each request must be immediately followed by its replyChannel.
		//		//So, the two put operations must be made atomic by synching against 
		//		//something common to all request threads for this server.
		//		synchronized (this)
		//		{
		//			requestChannel.put (requestBytes);
		//			requestChannel.put (replyChannel);
		//		}
		//
		//		byte[] responseBytes = (byte[]) replyChannel.take ();
		//		KeelResponse dsmr = new ModelResponseMessage();
		//		KeelResponse res = dsmr.deserialize (responseBytes);
		//
		//		if (res == null)
		//		{
		//			System.err.println (PREFIX + "WARNING: Deserialized model response is null");
		//		}
		//
		//		return res;
		return ((KeelDirectServer) myThread).execute (message);
	}

	// 	protected Channel getRequestChannel() {
	protected LinkedBlockingQueue getRequestChannel ()
	{
		// 		LinkedQueue toServer = new LinkedQueue();
		LinkedBlockingQueue toServer = new LinkedBlockingQueue ();

		return toServer;
	}

	public void start () throws Exception
	{
		createKeelDirectServer ();
	}

	public void stop ()
	{
		((KeelDirectServer) myThread).shutDown ();
	}
}
