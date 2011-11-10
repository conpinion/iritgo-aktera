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

package de.iritgo.aktera.core.classloader;


import java.net.URL;
import java.net.URLClassLoader;


/**
 * This is a custom class loader that behaves similarly to URLClassLoader
 * except for the fact that unless the class being loaded is a system class,
 * then the parent classloader is not used to resolve the class.  This
 * gurantees that even if the same class (but a different version) exists
 * in the parent loader's classpath, it will not affect the child loader
 * and the correct version of the class from the child loader's path will be
 * loaded.
 *
 * @version        $Revision: 1.1 $        $Date: 2003/12/30 01:29:35 $
 * @author Shash Chatterjee
 * Created on Nov 6, 2002
 */
public class KeelURLClassLoader extends URLClassLoader
{
	/**
	 * @see java.net.URLClassLoader#URLClassLoader(URL[])
	 */
	public KeelURLClassLoader(URL[] urls)
	{
		super(urls);
	}

	/**
	 * @see java.net.URLClassLoader#URLClassLoader(URL[], ClassLoader)
	 */
	public KeelURLClassLoader(URL[] urls, ClassLoader loader)
	{
		super(urls, loader);
	}

	/**
	 * A quick-and-dirty function to determine if the classname
	 * is a system class or not.
	 * @param name Name of the class, fully qualified with package name
	 * @return boolean True if the class is a system class, false otherwise
	 */
	protected boolean isSystemResource(String name)
	{
		//return true;  // Debug: Force loading all from parent class-loader
		return (name.startsWith("java.") || name.startsWith("java/") || name.startsWith("javax.")
						|| name.startsWith("javax/") || name.startsWith("sun.") || name.startsWith("sun/")
						|| name.startsWith("sunw.") || name.startsWith("sunw/") || name.startsWith("com.sun.")
						|| name.startsWith("com/sun/") || name.startsWith("org.xml.") || name.startsWith("org/xml/")
						|| name.startsWith("org/w3c/") || name.startsWith("org.w3c.")
						|| name.startsWith("EDU.oswego.cs.dl.util.concurrent.")
						|| name.startsWith("EDU/oswego/cs/dl/util/concurrent/") || name.startsWith("ru/nlmk/") || name
						.startsWith("ru.nlmk."));
	}

	/**
	 * This method overrides the default behavior,  If not a system class,
	 * then the parent class-loader is never consulted.
	 *
	 * @see java.lang.ClassLoader#loadClass(String, boolean)
	 */
	public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		Class c = null;

		//Check if the class is a system class or not
		if (isSystemResource(name))
		{
			try
			{
				// Load system classes from the parent class-loader
				if (getParent() != null)
				{
					c = getParent().loadClass(name);
				}
			}
			catch (ClassNotFoundException cnfe)
			{
				// Ignore....want to try locally
				//--- quikdraw: Why?
			}
		}

		// If not a system class, or parent didn't have it, try to load it locally
		if (c == null)
		{
			// First, check if the class has already been loaded
			c = findLoadedClass(name);

			if (c == null)
			{
				// If not found, then try to find class locally
				c = findClass(name);
			}
		}

		// Link the class if specified
		if (resolve)
		{
			resolveClass(c);
		}

		return c;
	}

	/**
	 * This method overrides the default behavior,  If not a system resource,
	 * then the parent class-loader is never consulted.
	 *
	 * @see java.lang.ClassLoader#getResource(String)
	 */
	public URL getResource(String name)
	{
		URL u = null;

		if (isSystemResource(name))
		{
			u = getParent().getResource(name);
		}

		if (u == null)
		{
			u = findResource(name);
		}

		return u;
	}
}
