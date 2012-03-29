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

package de.iritgo.aktera.script.it;


import de.iritgo.aktera.test.BrowserTestCase;
import de.iritgo.aktera.test.UserTestSteps;
import org.junit.Test;
import java.util.Properties;


public class ScriptCrudTest extends BrowserTestCase
{
	@Test
	public void createUpdateAndDeleteScript()
	{
		try
		{
			UserTestSteps.loginAsAdmin(selenium);

			// create script
			String scriptName = "pseudoscript" + String.valueOf(randomNumber(4));
			Properties myScript = new Properties();

			myScript.setProperty("script_name", scriptName);
			myScript.setProperty("script_displayName", scriptName);
			myScript.setProperty("script_author", "selenium");
			myScript.setProperty("script_version", "0.1");
			myScript.setProperty("script_description", "I am a pseudo script for test purposes.");
			myScript.setProperty("script_code", "// hic sunt leones");
			createScript(myScript);

			// update script
			updateScript(scriptName, "script_version", "0.11");

			// delete script
			deleteScript(scriptName);

			UserTestSteps.logout(selenium);
		}
		catch (com.thoughtworks.selenium.SeleniumException e)
		{
			selenium.captureScreenshot("exception-" + this + ".png");
			throw (e);
		}
	}

	protected void createScript(Properties scriptProperties)
	{
		selenium.openController("de.iritgo.aktera.script.ListScripts");
		selenium.clickButton("COMMAND_listCmdNew");
		selenium.type("script_name", scriptProperties.getProperty("script_name", "")); // required
		selenium.type("script_displayName", scriptProperties.getProperty("script_displayName", "")); // required
		selenium.type("script_author", scriptProperties.getProperty("script_author", ""));
		selenium.type("script_version", scriptProperties.getProperty("script_version", ""));
		selenium.type("script_description", scriptProperties.getProperty("script_description", ""));
		selenium.type("script_code", scriptProperties.getProperty("script_code", ""));
		selenium.clickButton("COMMAND_save");
	}

	protected void deleteScript(String scriptName)
	{
		selenium.openController("de.iritgo.aktera.script.ListScripts");
		selenium.type("listSearch", scriptName);
		selenium.clickButton("COMMAND_listCmdSearch");
		selenium.selectTable(scriptName);
		selenium.select("listExecuteModel", "label=Löschen");
		selenium.clickButton("COMMAND_listCmdExecute");
	}

	protected void updateScript(String scriptName, String updateProperty, String updateValue)
	{
		selenium.openController("de.iritgo.aktera.script.ListScripts");
		selenium.type("listSearch", scriptName);
		selenium.clickButton("COMMAND_listCmdSearch");
		selenium.selectTable(scriptName);
		selenium.select("listExecuteModel", "label=Bearbeiten");
		selenium.clickButton("COMMAND_listCmdExecute");
		selenium.type(updateProperty, updateValue);
		selenium.clickButton("COMMAND_save");
	}
}
