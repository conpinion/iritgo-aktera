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


import java.lang.reflect.InvocationTargetException;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.apache.commons.beanutils.MethodUtils;
import org.codehaus.groovy.control.CompilationFailedException;


public class GroovyScriptCompiler implements ScriptCompiler
{
	public static class CompiledGroovyScript extends CompiledScript
	{
		private String scriptName;

		private Class scriptClass;

		public CompiledGroovyScript (String scriptName, Class scriptClass)
		{
			this.scriptName = scriptName;
			this.scriptClass = scriptClass;
		}

		@Override
		public Object execute (String methodName, Object... args) throws ScriptMethodNotFoundException, ScriptExecutionException
		{
			try
			{
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
	}

	public CompiledScript compile (String scriptName, String scriptCode) throws ScriptCompilerException
	{
		try
		{
			GroovyClassLoader gcl = new GroovyClassLoader ();
			return new CompiledGroovyScript (scriptName, gcl.parseClass (scriptCode));
		}
		catch (CompilationFailedException x)
		{
			throw new ScriptCompilerException ("Unable to compile script", x);
		}
	}

	public void check (String scriptCode) throws ScriptCompilerException
	{
		try
		{
			GroovyShell gs = new GroovyShell ();
			gs.parse (scriptCode);
		}
		catch (CompilationFailedException x)
		{
			throw new ScriptCompilerException ("Unable to compile script", x);
		}
	}
}
