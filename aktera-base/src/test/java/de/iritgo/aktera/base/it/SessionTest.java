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
import de.iritgo.aktera.test.UserTestSteps;
import org.junit.Test;


public class SessionTest extends BrowserTestCase
{
	/**
	 * Try to access the about page.
	 */
	@Test
	public void accessAboutPage() throws Exception
	{
		openURL("model.do?model=aktera.about-seq");
		assertTextPresent("Applikationsinformationen");
	}

	/**
	 * Login as the admin user. Logout.
	 */
	@Test
	public void loginLogout()
	{
		openStartPage();
		assertTextPresent("Nicht angemeldet");
		UserTestSteps.loginAsAdmin(selenium);
		assertTextPresent("Angemeldet als");
		assertTextPresent("[admin]");
		UserTestSteps.logout(selenium);
		assertTextPresent("Nicht angemeldet");
	}
}
