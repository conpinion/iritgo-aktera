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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This is an utility class that allows the conversion of a typical
 * classpath specification to an URL array as needed by the URLClassLoader
 * class.  During the conversion process, this class checks to see that the
 * each service of the classpath is present and accessible.
 *
 * @version        $Revision: 1.1 $        $Date: 2003/12/30 01:29:35 $
 * @author Shash Chatterjee
 * Created on Nov 5, 2002
 */
public class URLClassLoaderPath
{
	/**
	 * The specified path to convert
	 */
	protected String m_path = null;

	/**
	 * Cosntruct the helper object         * @param path The classpath string, formatted
	 * as is normally expected for the java.class.path property
	 * or the CLASSPATH environment variable.
	 */
	public URLClassLoaderPath(String path)
	{
		m_path = path;
	}

	/**
	 * Set the path string to be converted         * @param path The classpath string, formatted
	 * as is normally expected for the java.class.path property
	 * or the CLASSPATH environment variable.
	 */
	public void setPath(String path)
	{
		m_path = path;
	}

	/**
	 * COnvert the string so that each individual JAR or directory
	 * in the classpath is returned as a URL object
	 * @return URL[] The array of JAR/directory resources
	 * @throws Exception
	 */
	public URL[] getURLArray() throws Exception
	{
		// Each path service is stored here after processing
		Vector urlVec = new Vector(0);

		// Either set the path using the constructor or
		// use the setPath method.
		if (m_path == null)
		{
			throw new Exception("Path has not been set");
		}

		//Split up the supplied path using the system-specific 
		// path separator
		StringTokenizer tok = new StringTokenizer(m_path, System.getProperty("path.separator"));

		//Iterate through each path service and store it in the Vecotr
		// of URLs
		while (tok.hasMoreTokens())
		{
			// Make sure the file exists and is accessible
			String fileName = tok.nextToken();
			File file = new File(fileName);

			if (file.exists() && file.canRead())
			{
				// File exists and is accessible
				try
				{
					//Get the FQN of the file 
					String canonicalPath = file.getCanonicalPath();

					//We need to make an URL out of this, replace the
					//system-dependent separators with a "/"
					canonicalPath = canonicalPath.replace('\\', '/');

					// If the file is a directory, then tag on the
					// trailing "/" since that is what the
					// standard URLClassLoader uses to designate a directory
					if (file.isDirectory())
					{
						canonicalPath += "/";
					}

					// Convert the path service into an URL resource
					URL url = new URL("file", null, canonicalPath);

					//Add the resource to the vector of URLs
					urlVec.add(url);
				}
				catch (IOException e)
				{
					throw new Exception("Cannot create URL for " + fileName, e);
				}
			}
			else
			{
				//File does not exist and is not accessible -- throw exception
				throw new Exception("Cannot find or read " + fileName);
			}
		}

		// Processed all services, return in array format
		return (URL[]) urlVec.toArray(new URL[0]);
	}
}
