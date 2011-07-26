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


import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;


/**
 * Compiler for groovy scripts.
 */
public class GroovyScriptCompiler implements ScriptCompiler
{
	/**
	 * @see de.iritgo.aktera.base.configuration.ScriptCompiler#compile(java.lang.String)
	 */
	public Class<?> compile (String script) throws ScriptCompilerException
	{
		try
		{
			GroovyClassLoader gcl = new GroovyClassLoader ();

			return gcl.parseClass (script);
		}
		catch (CompilationFailedException x)
		{
			throw new ScriptCompilerException ("Unable to compile script", x);
		}
	}

	/**
	 * @see de.iritgo.aktera.base.configuration.ScriptCompiler#check(java.lang.String)
	 */
	public void check (String script) throws ScriptCompilerException
	{
		try
		{
			GroovyShell gs = new GroovyShell ();

			gs.parse (script);
		}
		catch (CompilationFailedException x)
		{
			throw new ScriptCompilerException ("Unable to compile script", x);
		}
	}
}
