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
import org.apache.commons.beanutils.MethodUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Default ScriptManager implementation.
 */
public class ScriptManagerImpl implements ScriptManager
{
	/** This map stores the compiled scripts under their name */
	private Map<String, Class<?>> compiledScripts = new HashMap<String, Class<?>> ();

	/** All available script compilers, indexed by name */
	private Map<String, ScriptCompiler> compilers = new HashMap<String, ScriptCompiler> ();

	/** All available script providers, indexed by name */
	private List<ScriptProvider> providers = new LinkedList<ScriptProvider> ();

	/**
	 * Set the available script compilers.
	 */
	public void setCompilers (Map<String, ScriptCompiler> compilers)
	{
		this.compilers = compilers;
	}

	/**
	 * Set the available script providers.
	 */
	public void setProviders (List<ScriptProvider> providers)
	{
		this.providers = providers;
	}

	/**
	 * @throws ScriptCompilerException
	 * @throws ScriptLanguageNotFoundException
	 * @see de.iritgo.aktera.script.ScriptManager#execute(java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public Object execute (String scriptName, String methodName, Object... args)
		throws ScriptNotFoundException, ScriptMethodNotFoundException, ScriptLanguageNotFoundException,
		ScriptExecutionException, ScriptCompilerException
	{
		if (! isCompiled (scriptName))
		{
			Script script = find (scriptName);

			compile (script);
		}

		return executeCompiledScript (scriptName, methodName, args);
	}

	/**
	 * Execute a compiled script.
	 *
	 * @param scriptName The name of the script to execute
	 * @param methodName The name of the method to execute
	 * @param args Method arguments
	 * @return Script return value (can be null if the script doesn't
	 * @throws ScriptExecutionException If an error occurred during script
	 * execution
	 * @throws ScriptMethodNotFoundException If the script doesn't contain the
	 * specified method
	 */
	private Object executeCompiledScript (String scriptName, String methodName, Object... args)
		throws ScriptExecutionException, ScriptMethodNotFoundException
	{
		try
		{
			Class<?> scriptClass = compiledScripts.get (scriptName);
			Object scriptObject = scriptClass.newInstance ();

			return MethodUtils.invokeMethod (scriptObject, methodName, args);
		}
		catch (InstantiationException x)
		{
			throw new ScriptExecutionException ("Unable to instantiate script with name '" + scriptName + "'", x);
		}
		catch (IllegalAccessException x)
		{
			throw new ScriptMethodNotFoundException ("No such script method with name '" + methodName + "'", x);
		}
		catch (NoSuchMethodException x)
		{
			throw new ScriptMethodNotFoundException ("No such script method with name '" + methodName + "'", x);
		}
		catch (InvocationTargetException x)
		{
			throw new ScriptExecutionException ("Error during execution of script method '" + scriptName + "."
							+ methodName + "': " + x.getTargetException ().getMessage (), x.getTargetException ());
		}
	}

	/**
	 * @throws ScriptCompilerException
	 * @see de.iritgo.aktera.script.ScriptManager#compile(java.lang.String, java.lang.String)
	 */
	protected void compile (Script script) throws ScriptLanguageNotFoundException, ScriptCompilerException
	{
		if (! compilers.containsKey (script.getLanguage ()))
		{
			throw new ScriptLanguageNotFoundException ("No such script language '" + script.getLanguage () + "'");
		}

		compiledScripts.put (script.getName (), compilers.get (script.getLanguage ()).compile (script.getCode ()));
	}

	/**
	 * Retrieve the specified script from the first provider that contains this
	 * script.
	 *
	 * @param scriptName The name of the script to retrieve
	 * @return The script
	 * @throws ScriptNotFoundException If no provider contains this script
	 */
	protected Script find (String scriptName) throws ScriptNotFoundException
	{
		for (ScriptProvider provider : providers)
		{
			try
			{
				return provider.find (scriptName);
			}
			catch (ScriptNotFoundException ignored)
			{
			}
		}

		throw new ScriptNotFoundException ("No such script with name '" + scriptName + "'");
	}

	/**
	 * Check if a script with the specified name is already compiled.
	 *
	 * @param scriptName The name of the script
	 * @return True if the script is already compiled
	 */
	protected boolean isCompiled (String scriptName)
	{
		return compiledScripts.containsKey (scriptName);
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#hasCompiledScript(java.lang.String)
	 */
	public boolean hasCompiledScript (String scriptName)
	{
		return isCompiled (scriptName);
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#listScriptNames()
	 */
	public Collection<KeyedValue2<String, Integer, String>> listScriptNames ()
	{
		Set<KeyedValue2<String, Integer, String>> scriptNames = new HashSet<KeyedValue2<String, Integer, String>> ();

		for (ScriptProvider provider : providers)
		{
			scriptNames.addAll (provider.listScriptNames ());
		}

		return scriptNames;
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#listScriptNamesByImplementedMethod(java.lang.String)
	 */
	public Collection<KeyedValue2<String, Integer, String>> listScriptNamesByImplementedMethod (String methodName)
	{
		Set<KeyedValue2<String, Integer, String>> scriptNames = new HashSet<KeyedValue2<String, Integer, String>> ();

		for (ScriptProvider provider : providers)
		{
			scriptNames.addAll (provider.listScriptNamesByImplementedMethod (methodName));
		}

		return scriptNames;
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#listCompilerNames()
	 */
	public Collection<String> listCompilerNames ()
	{
		return compilers.keySet ();
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#check(java.lang.String, java.lang.String)
	 */
	public void check (String scriptCode, String language)
		throws ScriptCompilerException, ScriptLanguageNotFoundException
	{
		if (! compilers.containsKey (language))
		{
			throw new ScriptLanguageNotFoundException ("No script compiler with name '" + language + "' found");
		}

		compilers.get (language).check (scriptCode);
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#invalidate(java.lang.String)
	 */
	public void invalidate (String scriptName)
	{
		compiledScripts.remove (scriptName);

		for (ScriptProvider provider : providers)
		{
			provider.invalidate (scriptName);
		}
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#run(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public Object run (String scriptCode, String scriptLanguage, String scriptName, String methodName, Object... args)
		throws ScriptMethodNotFoundException, ScriptLanguageNotFoundException, ScriptExecutionException,
		ScriptCompilerException
	{
		if (! compiledScripts.containsKey (scriptName))
		{
			Script script = new Script (scriptName, scriptCode, scriptLanguage);

			compile (script);
		}

		return executeCompiledScript (scriptName, methodName, args);
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#findScriptNameById(java.lang.Integer)
	 */
	public String findScriptNameById (Integer id)
	{
		for (ScriptProvider provider : providers)
		{
			String name = provider.findScriptNameById (id);

			if (name != null)
			{
				return name;
			}
		}

		return null;
	}

	/**
	 * @see de.iritgo.aktera.script.ScriptManager#findScriptDisplayNameById(java.lang.Integer)
	 */
	public String findScriptDisplayNameById (Integer id)
	{
		for (ScriptProvider provider : providers)
		{
			String name = provider.findScriptDisplayNameById (id);

			if (name != null)
			{
				return name;
			}
		}

		return null;
	}
}
