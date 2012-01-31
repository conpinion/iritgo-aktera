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


import org.python.util.PythonInterpreter;
import de.iritgo.simplelife.string.StringTools;


public class PythonScriptCompiler implements ScriptCompiler
{
	public static class CompiledPythonScript extends CompiledScript
	{
		private String scriptName;

		private String script;

		public CompiledPythonScript(String scriptName, String script)
		{
			this.scriptName = scriptName;
			this.script = script;
		}

		@Override
		public Object execute(String methodName, Object... args)
			throws ScriptMethodNotFoundException, ScriptExecutionException
		{
			PythonInterpreter interpreter = new PythonInterpreter();
			interpreter.eval(script);
			interpreter.eval(methodName + "ReturnValue = " + methodName + "(" + StringTools.concatWithDelimiter(args, ",") +
							")");
			return interpreter.get(methodName + "ReturnValue");
		}
	}

	public CompiledScript compile(String scriptName, String scriptCode) throws ScriptCompilerException
	{
		return new CompiledPythonScript(scriptName, scriptCode);
	}

	public void check(String scriptCode) throws ScriptCompilerException
	{
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.eval(scriptCode);
	}
}
