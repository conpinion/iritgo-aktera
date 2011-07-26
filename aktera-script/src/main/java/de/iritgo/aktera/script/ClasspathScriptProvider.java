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

package de.iritgo.aktera.script;


import de.iritgo.simplelife.data.KeyedValue2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;


/**
 * A ScriptProvider that loads scripts from the classpath.
 */
public class ClasspathScriptProvider implements ScriptProvider
{
	/**
	 * @see de.iritgo.aktera.base.configuration.ScriptProvider#find(java.lang.String)
	 */
	public Script find (String scriptName) throws ScriptNotFoundException
	{
		try
		{
			InputStream stream = getClass ().getResourceAsStream ("/" + scriptName.replace ('.', '/'));

			if (stream == null)
			{
				throw new ScriptNotFoundException ("Script with name '" + scriptName + "' not found");
			}

			String code = IOUtils.toString (stream);
			Script script = new Script (scriptName, code, FilenameUtils.getExtension (scriptName));

			return script;
		}
		catch (IOException x)
		{
			throw new ScriptNotFoundException ("Script with name '" + scriptName + "' not found");
		}
	}

	/**
	 * @see de.iritgo.aktera.base.configuration.ScriptProvider#listScriptNames()
	 */
	public List<KeyedValue2<String, Integer, String>> listScriptNames ()
	{
		return new LinkedList<KeyedValue2<String, Integer, String>> ();
	}

	/**
	 * Currently this is a very simple way to find all scrits implementing
	 * a specific method. We just do a full text search with the pattern
	 * '%' + methodName + '%'.
	 *
	 * @see de.iritgo.aktera.base.configuration.ScriptProvider#listScriptNamesByImplementedMethod(java.lang.String)
	 */
	public List<KeyedValue2<String, Integer, String>> listScriptNamesByImplementedMethod (String methodName)
	{
		return new LinkedList<KeyedValue2<String, Integer, String>> ();
	}

	/**
	 * @see de.iritgo.aktera.base.configuration.ScriptProvider#invalidate(java.lang.String)
	 */
	public void invalidate (String scriptName)
	{
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptProvider#findScriptNameById(java.lang.Integer)
	 */
	public String findScriptNameById (Integer id)
	{
		return null;
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptProvider#findScriptDisplayNameById(java.lang.Integer)
	 */
	public String findScriptDisplayNameById (Integer id)
	{
		return null;
	}
}
