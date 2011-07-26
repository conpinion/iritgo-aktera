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

package de.iritgo.aktera.templating;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;


public class VelocitySpringResourceLoaderImpl extends ResourceLoader implements VelocitySpringResourceLoader
{
	private ConcurrentHashMap<String, String> templates = new ConcurrentHashMap<String, String> ();

	public void addTemplate (String name, String template)
	{
		templates.put (name, template);
	}

	public void remoteTemplate (String name)
	{
		templates.remove (name);
	}

	@Override
	public long getLastModified (Resource resource)
	{
		return 0;
	}

	@Override
	public InputStream getResourceStream (String name) throws ResourceNotFoundException
	{
		try
		{
			String template = templates.get (name);
			if (template == null)
				throw new ResourceNotFoundException ("Resource " + name + " not found.");

			return new ByteArrayInputStream (template.getBytes ("UTF-8"));
		}
		catch (UnsupportedEncodingException x)
		{
			throw new ResourceNotFoundException ("Resource " + name
							+ " has unsupported encoding. We support only utf-8.");
		}
	}

	@Override
	public void init (ExtendedProperties props)
	{
	}

	@Override
	public boolean isSourceModified (Resource resource)
	{
		return false;
	}

}
