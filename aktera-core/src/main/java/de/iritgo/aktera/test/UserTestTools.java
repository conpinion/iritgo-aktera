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

package de.iritgo.aktera.test;


import java.util.Properties;


public class UserTestTools
{
	public static final String SYSUSER_NAME = "sysUser_name";

	public static final String SYSUSER_LDAPNAME = "sysUser_ldapName";

	public static final String DELETEPASSWORD = "deletePassword";

	public static final String PASSWORDNEW = "passwordNew";

	public static final String PASSWORDNEWREPEAT = "passwordNewRepeat";

	public static final String PINNEW = "pinNew";

	public static final String PINNEWREPEAT = "pinNewRepeat";

	public static final String ROLE = "role";

	public static final String ADDRESS_SALUTATION = "address_salutation";

	public static final String ADDRESS_FIRSTNAME = "address_firstName";

	public static final String ADDRESS_LASTNAME = "address_lastName";

	public static final String ADDRESS_COMPANY = "address_company";

	public static final String ADDRESS_DIVISION = "address_division";

	public static final String ADDRESS_STREET = "address_street";

	public static final String ADDRESS_POSTALCODE = "address_postalCode";

	public static final String ADDRESS_CITY = "address_city";

	public static final String ADDRESS_COUNTRY = "address_country";

	public static final String ADDRESS_EMAIL = "address_email";

	public static final String ADDRESS_WEB = "address_web";

	public static final String PREFERENCES_CANCHANGEPASSWORD = "preferences_canChangePassword";

	public static final String PREFERENCES_LANGUAGE = "preferences_language";

	public static final String PREFERENCES_THEME = "preferences_theme";

	public static final String GUI_TABLEROWSPERPAGE = "gui_tableRowsPerPage";

	public static final Integer GUIROWS = 15;

	public static void createUser(SuperSelenium selenium, Properties newUserProperties)
	{
		try
		{
			selenium.clickButton("user");
			selenium.clickButton("COMMAND_usersCmdNew");
			selenium.type(SYSUSER_NAME, newUserProperties.getProperty("sysUser_name", "")); // required
			selenium.type(SYSUSER_LDAPNAME, newUserProperties.getProperty("sysUser_ldapName", ""));

			if (newUserProperties.getProperty("deletePassword") == "true")
			{
				selenium.click(DELETEPASSWORD);
			}
			else
			{
				selenium.type(PASSWORDNEW, newUserProperties.getProperty("passwordNew", ""));
				selenium.type(PASSWORDNEWREPEAT, newUserProperties.getProperty("passwordNewRepeat", ""));
			}

			selenium.type(PINNEW, newUserProperties.getProperty("pinNew", ""));
			selenium.type(PINNEWREPEAT, newUserProperties.getProperty("pinNewRepeat", ""));
			selenium.select(ROLE, "label=" + newUserProperties.getProperty("role", "Benutzer"));
			selenium.select(ADDRESS_SALUTATION, "label=" + newUserProperties.getProperty("role", "---"));
			selenium.type(ADDRESS_FIRSTNAME, newUserProperties.getProperty("address_firstName", ""));
			selenium.type(ADDRESS_LASTNAME, newUserProperties.getProperty("address_lastName", "")); // required
			selenium.type(ADDRESS_COMPANY, newUserProperties.getProperty("address_company", ""));
			selenium.type(ADDRESS_DIVISION, newUserProperties.getProperty("address_division", ""));
			selenium.type(ADDRESS_STREET, newUserProperties.getProperty("address_street", ""));
			selenium.type(ADDRESS_POSTALCODE, newUserProperties.getProperty("address_postalCode", ""));
			selenium.type(ADDRESS_CITY, newUserProperties.getProperty("address_city", ""));
			selenium.select(ADDRESS_COUNTRY, "label=" + newUserProperties.getProperty("address_country", "---"));
			selenium.type(ADDRESS_EMAIL, newUserProperties.getProperty("address_email", "")); // required
			selenium.type(ADDRESS_WEB, newUserProperties.getProperty("address_web", ""));

			if (newUserProperties.getProperty("preferences_canChangePassword") == "true")
			{
				selenium.click(PREFERENCES_CANCHANGEPASSWORD);
			}

			selenium.select(PREFERENCES_LANGUAGE, "label="
							+ newUserProperties.getProperty("preferences_language", "Deutsch"));
			selenium.select(PREFERENCES_THEME, "label="
							+ newUserProperties.getProperty("preferences_theme", "Standard"));
			selenium.type(GUI_TABLEROWSPERPAGE, newUserProperties.getProperty("gui_tableRowsPerPage", String
							.valueOf(GUIROWS)));
			selenium.clickButton("COMMAND_save");
		}
		catch (com.thoughtworks.selenium.SeleniumException e)
		{
			selenium.captureScreenshot("exception.png");
			throw (e);
		}
	}

	public static void createUserAndLogin(SuperSelenium selenium, Properties newUserProperties)
	{
		createUser(selenium, newUserProperties);
		logout(selenium);
		login(selenium, newUserProperties.getProperty("sysUser_name"), newUserProperties.getProperty("passwordNew"));
	}

	public static void deleteUser(SuperSelenium selenium, String username)
	{
		selenium.clickButton("user");
		selenium.type("usersSearch", username);
		selenium.clickButton("COMMAND_usersCmdSearch");

		if (! (selenium.isTextPresent(username)))
		{
			selenium.captureScreenshot("assertfailure.png");
		}
		assert (selenium.isTextPresent(username));
		selenium.selectTable(username);
		selenium.select("usersExecuteModel", "label=Benutzer und Adresse löschen");
		selenium.clickButton("COMMAND_usersCmdExecute");
	}

	public static void login(SuperSelenium selenium, String username, String password)
	{
		selenium.openStartPage();
		selenium.enterText("loginName", username);
		selenium.enterText("password", password);
		selenium.clickButton("COMMAND_login");
	}

	public static void loginAsAdmin(SuperSelenium selenium)
	{
		selenium.openStartPage();
		login(selenium, "admin", "admin");
	}

	public static void logout(SuperSelenium selenium)
	{
		selenium.openStartPage();
		selenium.clickLinkWithText("Abmelden");
	}

	public static void logoutAndDeleteUser(SuperSelenium selenium, String userName)
	{
		logout(selenium);
		loginAsAdmin(selenium);
		deleteUser(selenium, userName);
	}
}
