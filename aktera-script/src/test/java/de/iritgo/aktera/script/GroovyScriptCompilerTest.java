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


import de.iritgo.aktera.script.GroovyScriptCompiler;
import de.iritgo.aktera.script.ScriptCompiler;
import de.iritgo.aktera.script.ScriptCompilerException;
import org.apache.commons.beanutils.MethodUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;


/**
 * Test the GroovyScriptCompiler.
 */
public class GroovyScriptCompilerTest
{
	@Test
	public void compileGroovyScript ()
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException,
		ScriptCompilerException
	{
		String script = "public class Script { public String scriptMethod () { return \"ExpectedResult\"; }}";
		ScriptCompiler groovyCompiler = new GroovyScriptCompiler ();
		Class<?> scriptClass = groovyCompiler.compile (script);

		assertEquals ("Wrong script result", "ExpectedResult", MethodUtils.invokeExactMethod (scriptClass
						.newInstance (), "scriptMethod", new Object[]
		{}));
	}

	@Test
	public void compileBadGroovyScript ()
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		String script = "badGroovyScript !!$$$%%%&&";
		ScriptCompiler groovyCompiler = new GroovyScriptCompiler ();

		try
		{
			@SuppressWarnings("unused")
			Class<?> ignored = groovyCompiler.compile (script);

			fail ("No exception triggered");
		}
		catch (ScriptCompilerException x)
		{
			// Expected
		}
	}

	@Test
	public void checkBadGroovyScript ()
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		String script = "badGroovyScript !!$$$%%%&&";
		ScriptCompiler groovyCompiler = new GroovyScriptCompiler ();

		try
		{
			groovyCompiler.check (script);
			fail ("No exception triggered");
		}
		catch (ScriptCompilerException x)
		{
			// Expected
		}
	}
}
