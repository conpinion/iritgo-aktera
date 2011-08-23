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


import org.mozilla.javascript.*;


public class JavaScriptScriptCompiler implements ScriptCompiler
{
	public static class CompiledJavaScriptScript extends CompiledScript
	{
		private String scriptName;

		private org.mozilla.javascript.Script script;

		public CompiledJavaScriptScript (String scriptName, org.mozilla.javascript.Script script)
		{
			this.scriptName = scriptName;
			this.script = script;
		}

		@Override
		public Object execute (String methodName, Object... args)
			throws ScriptMethodNotFoundException, ScriptExecutionException
		{
			Context ctx = Context.enter ();
			Scriptable scope = new ImporterTopLevel (ctx);
			script.exec (ctx, scope);
			Function function = (Function) scope.get (methodName, scope);
			return function.call (ctx, scope, scope, args);
		}
	}

	public CompiledScript compile (String scriptName, String scriptCode) throws ScriptCompilerException
	{
		Context ctx = Context.enter ();
		return new CompiledJavaScriptScript (scriptName, ctx.compileString (scriptCode, scriptName, 1, null));
	}

	public void check (String scriptCode) throws ScriptCompilerException
	{
		Context ctx = Context.enter ();
		Scriptable scope = new ImporterTopLevel (ctx);
		try
		{
			ctx.evaluateString (scope, scriptCode, "", 1, null);
		}
		catch (Exception x)
		{
			throw new ScriptCompilerException (x);
		}
	}
}
