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


/**
 * Compilers for the various script languages must implement
 * this interface.
 */
public interface ScriptCompiler
{
	/**
	 * Compile the specified script
	 *
	 * @param script The script code
	 * @return The compiled script class
	 * @throws ScriptCompilerException In case of a compilation error
	 */
	public Class<?> compile (String script) throws ScriptCompilerException;

	/**
	 * Check the validity of the specified script.
	 *
	 * @param script The script code
	 * @throws ScriptCompilerException In case of a compilation error
	 */
	public void check (String script) throws ScriptCompilerException;
}
