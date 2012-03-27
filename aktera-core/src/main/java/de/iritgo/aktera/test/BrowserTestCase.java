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
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;


/**
 * Base class for Selenium based browser tests.
 */
public abstract class BrowserTestCase
{
	protected static String host;

	protected static String port;

	protected static String executable;

	protected static String context;

	protected static String waitForPageTimeout;

	protected static String executionDelay;

	protected static Random random = new Random();

	protected static String lowerCaseAlphaChars = "abcdefghijklmonpqrstuvwxyz";

	protected SuperSelenium selenium;

	/**
	 * Called before the test class is initialized. Retrieve the system test
	 * properties.
	 */
	@BeforeClass
	public static void beforeStartup()
	{
		host = System.getProperty("test.host", "localhost");
		port = System.getProperty("test.port", "8080");
		executable = System.getProperty("test.browser", "*firefox /opt/firefox3/firefox-bin");
		context = System.getProperty("test.context", "aktera");
		waitForPageTimeout = System.getProperty("test.waitForPageTimeout", "30000");
		executionDelay = System.getProperty("test.executionDelay", "0");
	}

	/**
	 * Create a new Selenium client for each test.
	 */
	@Before
	public void setUp()
	{
		selenium = new SuperSelenium("localhost", 4444, executable, "http://" + host + ":" + port + "/");
		selenium.setStartModel("model.do?model=aktera.tools.goto-start-model");
		selenium.setContext(context);
		selenium.setWaitForPageTimeout(Integer.valueOf(waitForPageTimeout));
		selenium.setSpeed(executionDelay);
		selenium.start();
	}

	/**
	 * Stop the Selenium client after running each test.
	 */
	@After
	public void tearDown()
	{
		selenium.stop();
	}

	public String randomName(int nameLength)
	{
		/* from http://www.daniweb.com/code/snippet851.html */
		char[] buf = new char[nameLength];

		for (int i = 0; i < buf.length; i++)
		{
			buf[i] = lowerCaseAlphaChars.charAt(random.nextInt(lowerCaseAlphaChars.length()));
		}

		return new String(buf);
	}

	public String randomNumber(int numberLength)
	{
		String number = new String();

		for (int i = 0; i < numberLength; i++)
		{
			number = number + String.valueOf(random.nextInt(9));
		}

		return number;
	}

	public String randomNumber(int numberLength, int offset)
	{
		String number = new String();

		for (int i = 0; i < numberLength; i++)
		{
			number = number + String.valueOf(random.nextInt(9));
		}

		return String.valueOf((Integer.valueOf(number) + offset));
	}

	public SuperSelenium getSelenium()
	{
		return selenium;
	}

	public void openURL(String url)
	{
		selenium.openURL(url);
	}

	public void addLocationStrategy(String strategyName, String functionDefinition)
	{
		selenium.addLocationStrategy(strategyName, functionDefinition);
	}

	public void addSelection(String locator, String optionLocator)
	{
		selenium.addSelection(locator, optionLocator);
	}

	public void allowNativeXpath(String allow)
	{
		selenium.allowNativeXpath(allow);
	}

	public void altKeyDown()
	{
		selenium.altKeyDown();
	}

	public void altKeyUp()
	{
		selenium.altKeyUp();
	}

	public void answerOnNextPrompt(String answer)
	{
		selenium.answerOnNextPrompt(answer);
	}

	public void assignId(String locator, String identifier)
	{
		selenium.assignId(locator, identifier);
	}

	public void attachFile(String fieldLocator, String fileLocator)
	{
		selenium.attachFile(fieldLocator, fileLocator);
	}

	public void captureEntirePageScreenshot(String filename)
	{
		selenium.captureEntirePageScreenshot(filename, null);
	}

	public void captureScreenshot(String filename)
	{
		selenium.captureScreenshot(filename);
	}

	public void check(String locator)
	{
		selenium.check(locator);
	}

	public void chooseCancelOnNextConfirmation()
	{
		selenium.chooseCancelOnNextConfirmation();
	}

	public void chooseOkOnNextConfirmation()
	{
		selenium.chooseOkOnNextConfirmation();
	}

	public void click(String locator)
	{
		selenium.click(locator);
	}

	public void clickAt(String locator, String coordString)
	{
		selenium.clickAt(locator, coordString);
	}

	public void clickButton(String locator)
	{
		selenium.clickButton(locator);
	}

	public void clickLinkWithText(String text)
	{
		selenium.clickLinkWithText(text);
	}

	public void close()
	{
		selenium.close();
	}

	public void contextMenu(String locator)
	{
		selenium.contextMenu(locator);
	}

	public void contextMenuAt(String locator, String coordString)
	{
		selenium.contextMenuAt(locator, coordString);
	}

	public void controlKeyDown()
	{
		selenium.controlKeyDown();
	}

	public void controlKeyUp()
	{
		selenium.controlKeyUp();
	}

	public void createCookie(String nameValuePair, String optionsString)
	{
		selenium.createCookie(nameValuePair, optionsString);
	}

	public void deleteAllVisibleCookies()
	{
		selenium.deleteAllVisibleCookies();
	}

	public void deleteCookie(String name, String optionsString)
	{
		selenium.deleteCookie(name, optionsString);
	}

	public void doubleClick(String locator)
	{
		selenium.doubleClick(locator);
	}

	public void doubleClickAt(String locator, String coordString)
	{
		selenium.doubleClickAt(locator, coordString);
	}

	public void dragAndDrop(String locator, String movementsString)
	{
		selenium.dragAndDrop(locator, movementsString);
	}

	public void dragAndDropToObject(String locatorOfObjectToBeDragged, String locatorOfDragDestinationObject)
	{
		selenium.dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
	}

	public void dragdrop(String locator, String movementsString)
	{
		selenium.dragdrop(locator, movementsString);
	}

	public void enterText(String locator, String value)
	{
		selenium.enterText(locator, value);
	}

	public boolean equals(Object obj)
	{
		return selenium.equals(obj);
	}

	public void fireEvent(String locator, String eventName)
	{
		selenium.fireEvent(locator, eventName);
	}

	public void focus(String locator)
	{
		selenium.focus(locator);
	}

	public String getAlert()
	{
		return selenium.getAlert();
	}

	public String[] getAllButtons()
	{
		return selenium.getAllButtons();
	}

	public String[] getAllFields()
	{
		return selenium.getAllFields();
	}

	public String[] getAllLinks()
	{
		return selenium.getAllLinks();
	}

	public String[] getAllWindowIds()
	{
		return selenium.getAllWindowIds();
	}

	public String[] getAllWindowNames()
	{
		return selenium.getAllWindowNames();
	}

	public String[] getAllWindowTitles()
	{
		return selenium.getAllWindowTitles();
	}

	public String getAttribute(String attributeLocator)
	{
		return selenium.getAttribute(attributeLocator);
	}

	public String[] getAttributeFromAllWindows(String attributeName)
	{
		return selenium.getAttributeFromAllWindows(attributeName);
	}

	public String getBodyText()
	{
		return selenium.getBodyText();
	}

	public String getConfirmation()
	{
		return selenium.getConfirmation();
	}

	public String getCookie()
	{
		return selenium.getCookie();
	}

	public String getCookieByName(String name)
	{
		return selenium.getCookieByName(name);
	}

	public Number getCursorPosition(String locator)
	{
		return selenium.getCursorPosition(locator);
	}

	public Number getElementHeight(String locator)
	{
		return selenium.getElementHeight(locator);
	}

	public Number getElementIndex(String locator)
	{
		return selenium.getElementIndex(locator);
	}

	public Number getElementPositionLeft(String locator)
	{
		return selenium.getElementPositionLeft(locator);
	}

	public Number getElementPositionTop(String locator)
	{
		return selenium.getElementPositionTop(locator);
	}

	public Number getElementWidth(String locator)
	{
		return selenium.getElementWidth(locator);
	}

	public String getEval(String script)
	{
		return selenium.getEval(script);
	}

	public String getExpression(String expression)
	{
		return selenium.getExpression(expression);
	}

	public String getHtmlSource()
	{
		return selenium.getHtmlSource();
	}

	public String getLocation()
	{
		return selenium.getLocation();
	}

	public Number getMouseSpeed()
	{
		return selenium.getMouseSpeed();
	}

	public String getPrompt()
	{
		return selenium.getPrompt();
	}

	public String getSelectedId(String selectLocator)
	{
		return selenium.getSelectedId(selectLocator);
	}

	public String[] getSelectedIds(String selectLocator)
	{
		return selenium.getSelectedIds(selectLocator);
	}

	public String getSelectedIndex(String selectLocator)
	{
		return selenium.getSelectedIndex(selectLocator);
	}

	public String[] getSelectedIndexes(String selectLocator)
	{
		return selenium.getSelectedIndexes(selectLocator);
	}

	public String getSelectedLabel(String selectLocator)
	{
		return selenium.getSelectedLabel(selectLocator);
	}

	public String[] getSelectedLabels(String selectLocator)
	{
		return selenium.getSelectedLabels(selectLocator);
	}

	public String getSelectedValue(String selectLocator)
	{
		return selenium.getSelectedValue(selectLocator);
	}

	public String[] getSelectedValues(String selectLocator)
	{
		return selenium.getSelectedValues(selectLocator);
	}

	public String[] getSelectOptions(String selectLocator)
	{
		return selenium.getSelectOptions(selectLocator);
	}

	public String getSpeed()
	{
		return selenium.getSpeed();
	}

	public String getTable(String tableCellAddress)
	{
		return selenium.getTable(tableCellAddress);
	}

	public String getText(String locator)
	{
		return selenium.getText(locator);
	}

	public String getTitle()
	{
		return selenium.getTitle();
	}

	public String getValue(String locator)
	{
		return selenium.getValue(locator);
	}

	public boolean getWhetherThisFrameMatchFrameExpression(String currentFrameString, String target)
	{
		return selenium.getWhetherThisFrameMatchFrameExpression(currentFrameString, target);
	}

	public boolean getWhetherThisWindowMatchWindowExpression(String currentWindowString, String target)
	{
		return selenium.getWhetherThisWindowMatchWindowExpression(currentWindowString, target);
	}

	public Number getXpathCount(String xpath)
	{
		return selenium.getXpathCount(xpath);
	}

	public void goBack()
	{
		selenium.goBack();
	}

	public int hashCode()
	{
		return selenium.hashCode();
	}

	public void highlight(String locator)
	{
		selenium.highlight(locator);
	}

	public void ignoreAttributesWithoutValue(String ignore)
	{
		selenium.ignoreAttributesWithoutValue(ignore);
	}

	public boolean isAlertPresent()
	{
		return selenium.isAlertPresent();
	}

	public boolean isChecked(String locator)
	{
		return selenium.isChecked(locator);
	}

	public boolean isConfirmationPresent()
	{
		return selenium.isConfirmationPresent();
	}

	public boolean isCookiePresent(String name)
	{
		return selenium.isCookiePresent(name);
	}

	public boolean isEditable(String locator)
	{
		return selenium.isEditable(locator);
	}

	public boolean isElementPresent(String locator)
	{
		return selenium.isElementPresent(locator);
	}

	public boolean isOrdered(String locator1, String locator2)
	{
		return selenium.isOrdered(locator1, locator2);
	}

	public boolean isPromptPresent()
	{
		return selenium.isPromptPresent();
	}

	public boolean isSomethingSelected(String selectLocator)
	{
		return selenium.isSomethingSelected(selectLocator);
	}

	public boolean isTextPresent(String pattern)
	{
		return selenium.isTextPresent(pattern);
	}

	public boolean isVisible(String locator)
	{
		return selenium.isVisible(locator);
	}

	public void keyDown(String locator, String keySequence)
	{
		selenium.keyDown(locator, keySequence);
	}

	public void keyDownNative(String keycode)
	{
		selenium.keyDownNative(keycode);
	}

	public void keyPress(String locator, String keySequence)
	{
		selenium.keyPress(locator, keySequence);
	}

	public void keyPressNative(String keycode)
	{
		selenium.keyPressNative(keycode);
	}

	public void keyUp(String locator, String keySequence)
	{
		selenium.keyUp(locator, keySequence);
	}

	public void keyUpNative(String keycode)
	{
		selenium.keyUpNative(keycode);
	}

	public void metaKeyDown()
	{
		selenium.metaKeyDown();
	}

	public void metaKeyUp()
	{
		selenium.metaKeyUp();
	}

	public void mouseDown(String locator)
	{
		selenium.mouseDown(locator);
	}

	public void mouseDownAt(String locator, String coordString)
	{
		selenium.mouseDownAt(locator, coordString);
	}

	public void mouseMove(String locator)
	{
		selenium.mouseMove(locator);
	}

	public void mouseMoveAt(String locator, String coordString)
	{
		selenium.mouseMoveAt(locator, coordString);
	}

	public void mouseOut(String locator)
	{
		selenium.mouseOut(locator);
	}

	public void mouseOver(String locator)
	{
		selenium.mouseOver(locator);
	}

	public void mouseUp(String locator)
	{
		selenium.mouseUp(locator);
	}

	public void mouseUpAt(String locator, String coordString)
	{
		selenium.mouseUpAt(locator, coordString);
	}

	public void openStartPage()
	{
		selenium.openStartPage();
	}

	public void openWindow(String url, String windowID)
	{
		selenium.openWindow(url, windowID);
	}

	public void refresh()
	{
		selenium.refresh();
	}

	public void removeAllSelections(String locator)
	{
		selenium.removeAllSelections(locator);
	}

	public void removeSelection(String locator, String optionLocator)
	{
		selenium.removeSelection(locator, optionLocator);
	}

	public void runScript(String script)
	{
		selenium.runScript(script);
	}

	public void select(String selectLocator, String optionLocator)
	{
		selenium.select(selectLocator, optionLocator);
	}

	public void selectFrame(String locator)
	{
		selenium.selectFrame(locator);
	}

	public void selectWindow(String windowID)
	{
		selenium.selectWindow(windowID);
	}

	public void setBrowserLogLevel(String logLevel)
	{
		selenium.setBrowserLogLevel(logLevel);
	}

	public void setContext(String context)
	{
		selenium.setContext(context);
	}

	public void setCursorPosition(String locator, String position)
	{
		selenium.setCursorPosition(locator, position);
	}

	public void setMouseSpeed(String pixels)
	{
		selenium.setMouseSpeed(pixels);
	}

	public void setSpeed(String value)
	{
		selenium.setSpeed(value);
	}

	public void setTimeout(String timeout)
	{
		selenium.setTimeout(timeout);
	}

	public void shiftKeyDown()
	{
		selenium.shiftKeyDown();
	}

	public void shiftKeyUp()
	{
		selenium.shiftKeyUp();
	}

	public void shutDownSeleniumServer()
	{
		selenium.shutDownSeleniumServer();
	}

	public void start()
	{
		selenium.start();
	}

	public void stop()
	{
		selenium.stop();
	}

	public void submit(String formLocator)
	{
		selenium.submit(formLocator);
	}

	public String toString()
	{
		return selenium.toString();
	}

	public void type(String locator, String value)
	{
		selenium.type(locator, value);
	}

	public void typeKeys(String locator, String value)
	{
		selenium.typeKeys(locator, value);
	}

	public void uncheck(String locator)
	{
		selenium.uncheck(locator);
	}

	public void waitForCondition(String script, String timeout)
	{
		selenium.waitForCondition(script, timeout);
	}

	public void waitForFrameToLoad(String frameAddress, String timeout)
	{
		selenium.waitForFrameToLoad(frameAddress, timeout);
	}

	public void waitForPageToLoad(String timeout)
	{
		selenium.waitForPageToLoad(timeout);
	}

	public void waitForPopUp(String windowID, String timeout)
	{
		selenium.waitForPopUp(windowID, timeout);
	}

	public void windowFocus()
	{
		selenium.windowFocus();
	}

	public void windowMaximize()
	{
		selenium.windowMaximize();
	}

	public void assertElementPresent(String locator)
	{
		selenium.assertElementPresent(locator);
	}

	public void assertTextNotPresent(String text)
	{
		selenium.assertTextNotPresent(text);
	}

	public void assertTextPresent(String text)
	{
		selenium.assertTextPresent(text);
	}

	public void clickLinkContainingText(String text)
	{
		selenium.clickLinkContainingText(text);
	}

	public void clickTable(String text)
	{
		selenium.clickTable(text);
	}

	public void selectTable(String text)
	{
		selenium.selectTable(text);
	}

	public void waitForPageToLoad()
	{
		selenium.waitForPageToLoad();
	}

	public void openController(String controller, Properties params)
	{
		selenium.openController(controller, params);
	}

	public void openController(String controller)
	{
		selenium.openController(controller);
	}

	public void selectReload(String locator, String value)
	{
		selenium.selectReload(locator, value);
	}
}
