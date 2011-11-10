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


import com.thoughtworks.selenium.DefaultSelenium;
import de.iritgo.simplelife.string.StringTools;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.Properties;


public class SuperSelenium extends DefaultSelenium
{
	private String startModel;

	private String context;

	private int waitForPageTimeout = 30000;

	public SuperSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL)
	{
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public void setWaitForPageTimeout(int waitForPageTimeout)
	{
		this.waitForPageTimeout = waitForPageTimeout;
	}

	public void setStartModel(String startModel)
	{
		this.startModel = startModel;
	}

	public void assertElementPresent(String locator)
	{
		assertTrue("Element not found: " + locator, isElementPresent(locator));
	}

	public void assertTextNotPresent(String text)
	{
		assertTrue("Text found: \"" + text + "\"", ! isTextPresent(text));
	}

	public void assertTextPresent(String text)
	{
		assertTrue("Text not found: \"" + text + "\"", isTextPresent(text));
	}

	public void clickButton(String locator)
	{
		click(locator);
		waitForPageToLoad();
	}

	/**
	 * Perform a click on a link containing the given text.
	 *
	 * @param text
	 *            The link text
	 */
	public void clickLinkContainingText(String text)
	{
		click("link=" + text);
		waitForPageToLoad();
	}

	/**
	 * Perform a click on the link mathing the given text.
	 *
	 * @param text
	 *            The link text
	 */
	public void clickLinkWithText(String text)
	{
		click("link=" + text);
		waitForPageToLoad();
	}

	public void clickTable(String text)
	{
		click("//td[contains(text(),'" + text + "')]");
		waitForPageToLoad();
	}

	public void enterText(String locator, String value)
	{
		type(locator, value);
	}

	public void selectTable(String text)
	{
		check("xpath=/descendant-or-self::node()/child::input[parent::node()/parent::node()/child::td[contains(child::text(),'"
						+ text + "')]]");
	}

	/**
	 * Wait for the current page to load.
	 */
	public void waitForPageToLoad()
	{
		waitForPageToLoad(String.valueOf(waitForPageTimeout));
	}

	public void openStartPage()
	{
		openURL(startModel);
	}

	public void selectReload(String locator, String value)
	{
		select(locator, value);
		waitForPageToLoad();
	}

	protected void openURL(String url)
	{
		open("/" + context + "/" + url);
	}

	public void openController(String controller)
	{
		openURL("model.do?model=" + controller);
	}

	public void openController(String controller, Properties params)
	{
		StringBuilder url = new StringBuilder("model.do?model=" + controller);

		for (Map.Entry param : params.entrySet())
		{
			StringTools.appendWithDelimiter(url, param.getKey() + "=" + param.getValue(), "&");
		}

		openURL("model.do?model=" + url.toString());
	}
}
