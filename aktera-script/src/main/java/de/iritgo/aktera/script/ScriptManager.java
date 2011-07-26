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
import java.util.Collection;


/**
 * The script manager is used to execute code written in scriptable
 * languages.
 */
public interface ScriptManager
{
	/** Service id */
	public static final String ID = "de.iritgo.aktera.script.ScriptManager";

	/**
	 * Execute a script method.
	 *
	 * @param scriptName The name of the script to call
	 * @param methodName The name of the method to call
	 * @param args Optional script arguments
	 * @return Script return value (can be null if the script doesn't
	 * @throws ScriptNotFoundException If the specified script was not found
	 * @throws ScriptMethodNotFoundException If the script doesn't contain the
	 * specified method
	 * @throws ScriptLanguageNotFoundException If no compiler exists for the
	 * specified language
	 * @throws ScriptExecutionException If an error occurred during script
	 * execution
	 * @throws ScriptCompilerException If an error occurred during the script
	 * compilation
	 */
	Object execute (String scriptName, String methodName, Object... args)
		throws ScriptNotFoundException, ScriptMethodNotFoundException, ScriptLanguageNotFoundException,
		ScriptExecutionException, ScriptCompilerException;

	/**
	 * Directly execute the given script code. The compiled script class is
	 * stored in the local cache for later retrieval. If the script has changed
	 * during the next execution, you need to call invalidate(scriptName) before
	 * the next call to run().
	 *
	 * @param scriptCode The script code to execute
	 * @param scriptLanguage The script compiler to use
	 * @param scriptName The script name
	 * @param methodName The name of the script method to execute.
	 * @return The return value of the executed script method
	 * @throws ScriptMethodNotFoundException If the script doesn't contain the
	 * specified method
	 * @throws ScriptLanguageNotFoundException If no compiler exists for the
	 * specified language
	 * @throws ScriptExecutionException If an error occurred during script
	 * execution
	 * @throws ScriptCompilerException If an error occurred during the script
	 * compilation
	 */
	public Object run (String scriptCode, String scriptLanguage, String scriptName, String methodName, Object... args)
		throws ScriptMethodNotFoundException, ScriptLanguageNotFoundException, ScriptExecutionException,
		ScriptCompilerException;

	/**
	 * Retrieve a set of the names of all scripts known to all script providers.
	 *
	 * @return A list of script name, id and display names
	 */
	public Collection<KeyedValue2<String, Integer, String>> listScriptNames ();

	/**
	 * List the names of all scripts that implement a specific method.
	 *
	 * @param methodName The name of the implemented method.
	 * @return A list of script name, id and display names
	 */
	public Collection<KeyedValue2<String, Integer, String>> listScriptNamesByImplementedMethod (String methodName);

	/**
	 * Retrieve a list of the names of all registerd script compilers
	 *
	 * @return A list of compiler names
	 */
	public Collection<String> listCompilerNames ();

	/**
	 * Check the given script code.
	 *
	 * @param scriptCode The script code
	 * @param language The compiler to use
	 * @throws ScriptCompilerException If the script contains any errors
	 */
	public void check (String scriptCode, String language)
		throws ScriptCompilerException, ScriptLanguageNotFoundException;

	/**
	 * Mark the specified script as invalid. This instructs the all script providers
	 * to remove the script from any caches.
	 *
	 * @param scriptName The name of the script
	 */
	public void invalidate (String scriptName);

	/**
	 * Retrieve a script name by a script id.
	 *
	 * @param id The script id
	 * @return The script name or null if none was found
	 */
	public String findScriptNameById (Integer id);

	/**
	 * Check if a compiled script with the specified name exists.
	 *
	 * @param scriptName The name of the script to check
	 * @return True if a script with that name was already compiled
	 */
	public boolean hasCompiledScript (String scriptName);

	/**
	 * Retrieve a script display name by a script id.
	 *
	 * @param id The script id
	 * @return The display script name or null if none was found
	 */
	public String findScriptDisplayNameById (Integer switchId);
}
