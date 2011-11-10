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

package de.iritgo.aktera.address.it;


import de.iritgo.aktera.test.BrowserTestCase;
import de.iritgo.aktera.test.UserTools;
import org.junit.Test;
import java.util.Properties;


public class AddressCrudTest extends BrowserTestCase
{
	private static int NAMELENGTH = 10;

	private static int NUMBERLENGTH = 12;

	@Test
	public void addressCrud()
	{
		UserTools.loginAsAdmin(selenium);

		Properties newEntry = new Properties();

		newEntry.setProperty("address_salutation", "Herr");
		newEntry.setProperty("address_firstName", randomName(NAMELENGTH));
		newEntry.setProperty("address_lastName", randomName(NAMELENGTH));
		newEntry.setProperty("address_company", randomName(NAMELENGTH));
		newEntry.setProperty("address_division", randomName(NAMELENGTH));
		newEntry.setProperty("address_position", randomName(NAMELENGTH));
		newEntry.setProperty("address_street", randomName(NAMELENGTH));
		newEntry.setProperty("address_postalCode", randomName(NAMELENGTH));
		newEntry.setProperty("address_city", randomName(NAMELENGTH));
		newEntry.setProperty("address_country", "Deutschland");
		newEntry.setProperty("address_email", randomName(NAMELENGTH));
		newEntry.setProperty("address_web", randomName(NAMELENGTH));
		newEntry.setProperty("address_contactNumber", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_companyNumber", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_phoneNumber_B__number", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_phoneNumber_BM__number", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_phoneNumber_BF__number", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_phoneNumber_BDD__number", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_phoneNumber_P__number", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_phoneNumber_PM__number", randomNumber(NUMBERLENGTH));
		newEntry.setProperty("address_remark", "Test-Eintrag");

		createAddress(newEntry);

		updateAddress(newEntry.getProperty("address_lastName"), "address_lastName", newEntry.getProperty(
						"address_lastName").toUpperCase());
		newEntry.setProperty("address_lastName", newEntry.getProperty("address_lastName").toUpperCase());

		deleteAddress(newEntry.getProperty("address_lastName"));

		UserTools.logout(selenium);
	}

	protected void createAddress(Properties addressEntry)
	{
		selenium.clickButton("address");
		selenium.clickButton("COMMAND_listCmdNew");
		selenium.select("address_salutation", "label=" + addressEntry.getProperty("address_salutation", ""));
		selenium.type("address_firstName", addressEntry.getProperty("address_firstName", ""));
		selenium.type("address_lastName", addressEntry.getProperty("address_lastName", ""));
		selenium.type("address_company", addressEntry.getProperty("address_company", ""));
		selenium.type("address_division", addressEntry.getProperty("address_division", ""));
		selenium.type("address_position", addressEntry.getProperty("address_position", ""));
		selenium.type("address_street", addressEntry.getProperty("address_street", ""));
		selenium.type("address_postalCode", addressEntry.getProperty("address_postalCode", ""));
		selenium.type("address_city", addressEntry.getProperty("address_city", ""));
		selenium.select("address_country", "label=" + addressEntry.getProperty("address_country", "---"));
		selenium.type("address_email", addressEntry.getProperty("address_email", ""));
		selenium.type("address_web", addressEntry.getProperty("address_web", ""));
		selenium.type("address_contactNumber", addressEntry.getProperty("address_contactNumber", ""));
		selenium.type("address_companyNumber", addressEntry.getProperty("address_companyNumber", ""));
		selenium.type("address_phoneNumber_B__number", addressEntry.getProperty("address_phoneNumber_B__number", ""));
		selenium.type("address_phoneNumber_BM__number", addressEntry.getProperty("address_phoneNumber_BM__number", ""));
		selenium.type("address_phoneNumber_BF__number", addressEntry.getProperty("address_phoneNumber_BF__number", ""));
		selenium.type("address_phoneNumber_BDD__number", addressEntry
						.getProperty("address_phoneNumber_BDD__number", ""));
		selenium.type("address_phoneNumber_P__number", addressEntry.getProperty("address_phoneNumber_P__number", ""));
		selenium.type("address_phoneNumber_PM__number", addressEntry.getProperty("address_phoneNumber_PM__number", ""));
		selenium.type("address_remark", addressEntry.getProperty("address_remark", ""));
		selenium.clickButton("COMMAND_save");
	}

	protected void updateAddress(String lastName, String updateProperty, String newValue)
	{
		selenium.clickButton("address");
		selenium.type("listSearch", lastName);
		selenium.clickButton("COMMAND_listCmdSearch");
		selenium.selectTable(lastName);
		selenium.select("listExecuteModel", "label=Bearbeiten");
		selenium.clickButton("COMMAND_listCmdExecute");
		selenium.type(updateProperty, newValue);
		selenium.clickButton("COMMAND_save");
	}

	protected void deleteAddress(String lastName)
	{
		selenium.clickButton("address");
		selenium.type("listSearch", lastName);
		selenium.clickButton("COMMAND_listCmdSearch");
		selenium.selectTable(lastName);
		selenium.select("listExecuteModel", "label=Löschen");
		selenium.clickButton("COMMAND_listCmdExecute");
	}
}
