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


import de.iritgo.aktera.script.Script;
import de.iritgo.aktera.script.ScriptCompiler;
import de.iritgo.aktera.script.ScriptCompilerException;
import de.iritgo.aktera.script.ScriptExecutionException;
import de.iritgo.aktera.script.ScriptLanguageNotFoundException;
import de.iritgo.aktera.script.ScriptManager;
import de.iritgo.aktera.script.ScriptManagerImpl;
import de.iritgo.aktera.script.ScriptMethodNotFoundException;
import de.iritgo.aktera.script.ScriptNotFoundException;
import de.iritgo.aktera.script.ScriptProvider;
import de.iritgo.simplelife.data.KeyedValue2;
import de.iritgo.simplelife.data.Tuple3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Test the ScriptManager.
 */
public class ScriptManagerTest
{
	private ScriptManager scriptManager = new ScriptManagerImpl ();

	@Before
	public void setUp () throws Exception
	{
		Map<String, ScriptCompiler> compilers = new HashMap<String, ScriptCompiler> ();

		compilers.put ("scriptLanguage", new ScriptCompiler ()
		{
			public Class<?> compile (String script)
			{
				try
				{
					return Class.forName (script);
				}
				catch (ClassNotFoundException x)
				{
					throw new RuntimeException (x);
				}
			}

			public void check (String script) throws ScriptCompilerException
			{
				try
				{
					Class.forName (script);
				}
				catch (ClassNotFoundException x)
				{
					throw new ScriptCompilerException (x);
				}
			}
		});
		((ScriptManagerImpl) scriptManager).setCompilers (compilers);

		List<ScriptProvider> providers = new LinkedList<ScriptProvider> ();

		providers.add (new ScriptProvider ()
		{
			public Script find (String scriptName) throws ScriptNotFoundException
			{
				if ("ScriptWithMissingLanguage".equals (scriptName))
				{
					return new Script ("ScriptWithMissingLanguage", "java.lang.String", "missingScriptLanguage");
				}
				else if ("Script".equals (scriptName))
				{
					return new Script ("Script", "java.lang.String", "scriptLanguage");
				}

				throw new ScriptNotFoundException ();
			}

			public List<KeyedValue2<String, Integer, String>> listScriptNames ()
			{
				List<KeyedValue2<String, Integer, String>> scriptNames = new LinkedList<KeyedValue2<String, Integer, String>> ();

				scriptNames.add (new KeyedValue2<String, Integer, String> ("ScriptWithMissingLanguage", 1,
								"ScriptWithMissingLanguage"));
				scriptNames.add (new KeyedValue2<String, Integer, String> ("Script", 2, "Script"));

				return scriptNames;
			}

			public void invalidate (String scriptName)
			{
			}

			public List<KeyedValue2<String, Integer, String>> listScriptNamesByImplementedMethod (String methodName)
			{
				List<KeyedValue2<String, Integer, String>> scriptNames = new LinkedList<KeyedValue2<String, Integer, String>> ();

				scriptNames.add (new KeyedValue2<String, Integer, String> ("Script", 2, "Script"));

				return scriptNames;
			}

			public String findScriptNameById (Integer id)
			{
				return "Script";
			}

			public String findScriptDisplayNameById (Integer id)
			{
				return "Script";
			}
		});
		providers.add (new ScriptProvider ()
		{
			private Script script = new Script ("ModifyableScript", "java.lang.String", "scriptLanguage");

			public Script find (String scriptName) throws ScriptNotFoundException
			{
				if ("ModifyableScript".equals (scriptName))
				{
					return script;
				}

				throw new ScriptNotFoundException ();
			}

			public List<KeyedValue2<String, Integer, String>> listScriptNames ()
			{
				List<KeyedValue2<String, Integer, String>> scriptNames = new LinkedList<KeyedValue2<String, Integer, String>> ();

				scriptNames.add (new KeyedValue2<String, Integer, String> ("ModifyableScript", 3, "ModifyableScript"));
				scriptNames.add (new KeyedValue2<String, Integer, String> ("Script", 4, "Script"));

				return scriptNames;
			}

			public void invalidate (String scriptName)
			{
				script = new Script ("ModifyableScript", "java.util.Date", "scriptLanguage");
			}

			public List<KeyedValue2<String, Integer, String>> listScriptNamesByImplementedMethod (String methodName)
			{
				List<KeyedValue2<String, Integer, String>> scriptNames = new LinkedList<KeyedValue2<String, Integer, String>> ();

				scriptNames.add (new KeyedValue2<String, Integer, String> ("ModifyableScript", 3, "ModifyableScript"));

				return scriptNames;
			}

			public String findScriptNameById (Integer id)
			{
				return null;
			}

			public String findScriptDisplayNameById (Integer id)
			{
				return null;
			}
		});
		((ScriptManagerImpl) scriptManager).setProviders (providers);
	}

	@Test
	public void callMissingScript ()
		throws ScriptLanguageNotFoundException, ScriptCompilerException, ScriptExecutionException,
		ScriptMethodNotFoundException
	{
		try
		{
			@SuppressWarnings("unused")
			Object ignored = scriptManager.execute ("ScriptThatDoesntExist", "scriptMethod");

			fail ("No exception triggered");
		}
		catch (ScriptNotFoundException ignored)
		{
			// Expected
		}
	}

	@Test
	public void callScriptWithMissingMethod ()
		throws ScriptNotFoundException, ScriptLanguageNotFoundException, ScriptCompilerException,
		ScriptExecutionException
	{
		try
		{
			@SuppressWarnings("unused")
			Object dummy = scriptManager.execute ("Script", "missingScriptMethod");

			fail ("No exception thrown");
		}
		catch (ScriptMethodNotFoundException ignored)
		{
			// Expected
		}
	}

	@Test
	public void callcriptWithMissingLanguage ()
		throws ScriptCompilerException, ScriptNotFoundException, ScriptMethodNotFoundException,
		ScriptExecutionException
	{
		try
		{
			@SuppressWarnings("unused")
			Object ignored = scriptManager.execute ("ScriptWithMissingLanguage", "scriptMethod");

			fail ("No exception triggered");
		}
		catch (ScriptLanguageNotFoundException ignored)
		{
			// Expected
		}
	}

	@Test
	public void callScript ()
		throws ScriptNotFoundException, ScriptMethodNotFoundException, ScriptLanguageNotFoundException,
		ScriptCompilerException, ScriptExecutionException
	{
		Object res = scriptManager.execute ("Script", "getClass");

		assertEquals ("Script returned wrong value", String.class, res);
	}

	@Test
	public void listScriptNames ()
	{
		Set<KeyedValue2<String, Integer, String>> expected = new HashSet<KeyedValue2<String, Integer, String>> ();

		expected.add (new KeyedValue2<String, Integer, String> ("ScriptWithMissingLanguage", 1,
						"ScriptWithMissingLanguage"));
		expected.add (new KeyedValue2<String, Integer, String> ("Script", 2, "Script"));
		expected.add (new KeyedValue2<String, Integer, String> ("ModifyableScript", 3, "ModifyableScript"));
		assertEquals ("Wrong script list", expected, scriptManager.listScriptNames ());
	}

	@Test
	public void listScriptNamesByImplementedMethod ()
	{
		Set<KeyedValue2<String, Integer, String>> expected = new HashSet<KeyedValue2<String, Integer, String>> ();

		expected.add (new KeyedValue2<String, Integer, String> ("Script", 2, "Script"));
		expected.add (new KeyedValue2<String, Integer, String> ("ModifyableScript", 3, "ModifyableScript"));
		assertEquals ("Wrong script list", expected, scriptManager.listScriptNamesByImplementedMethod ("scriptMethod"));
	}

	@Test
	public void scriptModification ()
		throws ScriptNotFoundException, ScriptMethodNotFoundException, ScriptLanguageNotFoundException,
		ScriptCompilerException, ScriptExecutionException
	{
		Object res = scriptManager.execute ("ModifyableScript", "getClass");

		assertEquals ("Unexpected original script result", String.class, res);
		scriptManager.invalidate ("ModifyableScript");
		res = scriptManager.execute ("ModifyableScript", "getClass");
		assertEquals ("Script change unseen by the script manager", Date.class, res);
	}

	@Test
	public void scriptCompilerList ()
	{
		Set<String> expected = new HashSet<String> ();

		expected.add ("scriptLanguage");
		assertEquals ("Wrong compiler list", expected, scriptManager.listCompilerNames ());
	}

	@Test
	public void correctScriptCheck () throws ScriptLanguageNotFoundException
	{
		try
		{
			scriptManager.check ("java.lang.String", "scriptLanguage");
		}
		catch (ScriptCompilerException x)
		{
			fail ("Exception thrown for correct script");
		}
	}

	@Test
	public void incorrectScriptCheck () throws ScriptLanguageNotFoundException
	{
		try
		{
			scriptManager.check ("not.existing.Class", "scriptLanguage");
			fail ("No excpetion thrown for incorrect script");
		}
		catch (ScriptCompilerException x)
		{
			// Expected
		}
	}

	@Test
	public void runScriptCode ()
		throws ScriptMethodNotFoundException, ScriptLanguageNotFoundException, ScriptCompilerException,
		ScriptExecutionException
	{
		Object res = scriptManager.run ("java.lang.String", "scriptLanguage", "Script", "getClass");

		assertEquals ("Script returned wrong value", String.class, res);
	}

	@Test
	public void findScriptNameById ()
	{
		assertEquals ("Wrong script name", "Script", scriptManager.findScriptNameById (2));
	}
}
