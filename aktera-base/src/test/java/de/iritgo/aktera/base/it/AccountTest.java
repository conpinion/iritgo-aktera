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

package de.iritgo.aktera.base.it;


import de.iritgo.aktera.test.BrowserTestCase;
import de.iritgo.aktera.test.UserTestTools;
import org.junit.Test;


public class AccountTest extends BrowserTestCase
{
	/**
	 * Entering a new password with an improper repeated password should
	 * generate an error message.
	 */
	@Test
	public void newPasswordMatchValidation()
	{
		UserTestTools.loginAsAdmin(selenium);

		clickLinkWithText("Einstellungen");
		enterText("passwordNew", "abc");
		enterText("passwordNewRepeat", "xyz");
		clickButton("COMMAND_save");

		assertTextPresent("Die Passwörter stimmen nicht überein");
		assertElementPresent("//tr[@class='error']");

		UserTestTools.logout(selenium);
	}

	/**
	 * Change the admin's password and try to login with the new password.
	 */
	@Test
	public void passwordChange() throws Exception
	{
		UserTestTools.loginAsAdmin(selenium);

		clickLinkWithText("Einstellungen");
		enterText("passwordNew", "abc");
		enterText("passwordNewRepeat", "abc");
		clickButton("COMMAND_save");

		UserTestTools.logout(selenium);

		UserTestTools.loginAsAdmin(selenium);

		assertTextPresent("Das von Ihnen eingegebene Passwort ist falsch!");

		UserTestTools.login(selenium, "admin", "abc");

		assertTextPresent("Angemeldet als");
		assertTextPresent("[admin]");

		clickLinkWithText("Einstellungen");
		enterText("passwordNew", "admin");
		enterText("passwordNewRepeat", "admin");
		clickButton("COMMAND_save");

		UserTestTools.logout(selenium);
	}

	/**
	 * Create a new user and a new group containing the new user. Try to login
	 * as the new user.
	 */
	@Test
	public void userAndGroupCreation()
	{
		UserTestTools.loginAsAdmin(selenium);

		clickLinkWithText("Benutzerverwaltung");

		clickButton("COMMAND_userListCmdNew");
		enterText("sysUser_name", "newUser");
		enterText("passwordNew", "newPassword");
		enterText("passwordNewRepeat", "newPassword");
		enterText("address_lastName", "newUser");
		enterText("address_email", "newUser");
		clickButton("COMMAND_save");
		assertTextPresent("newUser");

		clickLinkWithText("Gruppen");
		clickButton("COMMAND_groupListCmdNew");
		enterText("akteraGroup_name", "newGroup");
		clickButton("COMMAND_save");
		assertTextPresent("newGroup");

		clickTable("newGroup");
		clickLinkWithText("Gruppenmitglieder");
		clickButton("COMMAND_groupMemberListCmdNew");
		clickTable("newUser");
		assertTextPresent("newUser");

		UserTestTools.logout(selenium);

		UserTestTools.login(selenium, "newUser", "newPassword");

		assertTextPresent("[newUser]");

		UserTestTools.logout(selenium);

		UserTestTools.loginAsAdmin(selenium);

		clickLinkWithText("Benutzerverwaltung");

		clickLinkWithText("Gruppen");
		selectTable("newGroup");
		select("groupListExecuteModel", "label=Löschen");
		clickButton("COMMAND_groupListCmdExecute");
		assertTextNotPresent("newGroup");

		clickLinkWithText("Benutzer");
		selectTable("newUser");
		select("userListExecuteModel", "label=Benutzer und Adresse löschen");
		clickButton("COMMAND_userListCmdExecute");
		assertTextNotPresent("newUser");

		UserTestTools.logout(selenium);
	}
}
