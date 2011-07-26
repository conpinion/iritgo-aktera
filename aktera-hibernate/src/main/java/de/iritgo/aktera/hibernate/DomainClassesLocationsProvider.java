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

package de.iritgo.aktera.hibernate;


import de.iritgo.aktera.core.container.KeelContainer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;


/**
 * This class is used to provide Spring's LocalSessionFactoryBean with
 * Hibernate mapping locations. These locations are stored in the Keel
 * container.
 */
public class DomainClassesLocationsProvider extends ArrayList<String>
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new HibernateMappingProvider.
	 */
	public DomainClassesLocationsProvider ()
	{
		ClassLoader cl = Thread.currentThread ().getContextClassLoader ();
		Enumeration<URL> urls;

		try
		{
			urls = cl.getResources ("META-INF/persistence.xml");

			while (urls.hasMoreElements ())
			{
				URL url = urls.nextElement ();
				String jarPath = url.toString ();
				int index = jarPath.lastIndexOf ("!/META-INF/persistence.xml");

				if (index != - 1)
				{
					jarPath = jarPath.substring (4, index);

					JarInputStream jar = new JarInputStream (new URL (jarPath).openStream ());
					JarEntry entry = null;

					while ((entry = jar.getNextJarEntry ()) != null)
					{
						String entryName = entry.getName ();

						if (entryName.endsWith (".class"))
						{
							ClassFile cf = new ClassFile (new DataInputStream (new BufferedInputStream (jar)));
							AnnotationsAttribute ai = (AnnotationsAttribute) cf
											.getAttribute (AnnotationsAttribute.visibleTag);

							if (ai != null
											&& (ai.getAnnotation ("javax.persistence.Entity") != null || ai
															.getAnnotation ("org.hibernate.annotations.GenericGenerators") != null))
							{
								add (cf.getName ());
							}
						}

						jar.closeEntry ();
					}

					jar.close ();
				}
			}
		}
		catch (IOException x)
		{
		}
	}
}
