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

package de.iritgo.aktera.servers.socket;


import de.iritgo.aktera.core.classloader.URLClassLoaderPath;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * This class is the entry-point for the Keel Meta-Framework Socket Server.
 */
public class KeelServer
{
	private static final String DEFAULT_CLASSPATH = "java.class.path";

	private static final String PREFIX = "[KeelServer] ";

	static public void main(String[] args) throws Exception
	{
		KeelServer server = new KeelServer();

		server.run(args);
	}

	/**
	 * This instance method is synchronized so that the calling thread may invoke wait()
	 * @param args
	 */
	public synchronized void run(String[] args) throws Exception
	{
		System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");

		String configPath = System.getProperty("keel.config.dir");

		if (configPath == null)
		{
			System.err.println("Please specify keel.config.dir system property");
			System.exit(- 1);
		}

		URL[] myClasspathURLs = null;
		StringBuffer myClasspath = new StringBuffer();
		ClassLoader myClassLoader = this.getClass().getClassLoader();

		//We need a list of URLs.  If classloader is the right type, we
		// can acquire it's list.  Otherwise, just use the default
		// classpath provided when this program was started
		if (myClassLoader instanceof URLClassLoader)
		{
			myClasspathURLs = myClasspathURLs = ((URLClassLoader) myClassLoader).getURLs();

			for (int i = 0; i < myClasspathURLs.length; i++)
			{
				myClasspath.append(myClasspathURLs[i].getFile()).append(File.pathSeparatorChar);
			}
		}
		else
		{
			System.out.println(PREFIX + "Using default classpath [" + DEFAULT_CLASSPATH + "]");
			myClasspath.append(System.getProperty(DEFAULT_CLASSPATH));
			myClasspathURLs = new URLClassLoaderPath(myClasspath.toString()).getURLArray();
		}

		//Support 3rd party ClassLoaders like Forehead, use classpath used in call to main() 
		//before defaulting to system classpath
		if ((myClassLoader != ClassLoader.getSystemClassLoader()) && (myClassLoader instanceof URLClassLoader))
		{
			System.out.println("Using current loader");
		}
		else
		{
			Class c = null;

			// AOP- Load the weaving classloader if it is available in the classpath
			// The way it will be available is if the "svc-aop-aspectwerkz" module
			// was included during deployment.
			try
			{
				c = myClassLoader.loadClass("de.iritgo.aktera.core.classloader.KeelWeavingClassLoader");
				//System.setProperty("aspectwerkz.transform.verbose", "yes");
				//System.setProperty("aspectwerkz.transform.dump", "*");
				System.setProperty("aspectwerkz.definition.file", configPath + System.getProperty("file.separator")
								+ "keelaop.xml");
			}
			catch (ClassNotFoundException e)
			{
				//Ignore - not having this class is not necessarily an error
				//We will try and load the "normal"classloader next, if that fails then that is a
				//problem
			}

			// If the AOP classloader is not found, load the "normal" Keel classloader
			if (c == null)
			{
				try
				{
					c = myClassLoader.loadClass("de.iritgo.aktera.core.classloader.KeelURLClassLoader");
				}
				catch (ClassNotFoundException e1)
				{
					System.err.println(PREFIX + "Could not instantiate KeelURLClassLoader classloader");
					System.exit(- 1);
				}
			}

			// Now that we have the correct loader class, actually create an instance of the classlaoder
			Class[] parmTypes =
			{
							URL[].class, ClassLoader.class
			};
			Constructor constructor = c.getConstructor(parmTypes);
			Object[] constructorArgs =
			{
							myClasspathURLs, ClassLoader.getSystemClassLoader().getParent()
			};

			myClassLoader = (ClassLoader) constructor.newInstance(constructorArgs);
		}

		System.err.println(PREFIX + "Path:'" + myClasspath.toString() + "'");
		System.err.println(PREFIX + "Server name is:" + System.getProperty("keel.server.name"));
		System.err.println(PREFIX + "Loader name is: " + myClassLoader);
		System.err.println(PREFIX + "Loader parent is: " + myClassLoader.getParent());

		Class clazz = myClassLoader.loadClass("de.iritgo.aktera.servers.socket.KeelSocketServer");
		Thread myThread = (Thread) clazz.newInstance();

		myThread.setDaemon(true);

		Class[] parmTypes =
		{
			ClassLoader.class
		};
		Method m = clazz.getMethod("setContextClassLoader", parmTypes);
		Object[] parms =
		{
			myClassLoader
		};

		m.invoke(myThread, parms);

		//Need to execute server as its own thread so that it resolves classpath appropriately. 
		m = clazz.getMethod("start", (Class[]) null);
		m.invoke(myThread, (Object[]) null);

		wait();
	}
}
