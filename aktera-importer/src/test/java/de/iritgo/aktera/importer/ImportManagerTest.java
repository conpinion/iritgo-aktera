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

package de.iritgo.aktera.importer;


import static org.junit.Assert.*;
import java.io.*;
import java.util.*;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.importer.ImportManager.ImportHandlerConfig;
import de.iritgo.aktera.logger.NullLoggerImpl;
import de.iritgo.aktera.model.*;
import de.iritgo.simplelife.io.NullPrintWriter;


/**
 * Test the ImportManager.
 */
public class ImportManagerTest
{
	private class TestImportHandler implements ImportHandler
	{
		private String id;

		public TestImportHandler(String id)
		{
			this.id = id;
		}

		public boolean analyze(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
						Properties properties) throws ModelException, XPathExpressionException
		{
			return false;
		}

		public void endRootElement(String uri, String localName, String name, PrintWriter reporter,
						Properties properties) throws SAXException
		{
			protocol.append("^" + id + ":" + localName);
		}

		public void endElement(String uri, String localName, String name, PrintWriter reporter, Properties properties)
			throws SAXException
		{
			protocol.append("-" + id + ":" + localName);
		}

		public boolean perform(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
						Properties properties) throws ModelException, XPathExpressionException
		{
			return false;
		}

		public void startRootElement(String uri, String localName, String name, Attributes attributes,
						PrintWriter reporter, Properties properties)
		{
			protocol.append("$" + id + ":" + localName);
		}

		public void startElement(String uri, String localName, String name, Attributes attributes,
						PrintWriter reporter, Properties properties)
		{
			protocol.append("+" + id + ":" + localName);
		}

		public void elementContent(String uri, String localName, String name, String content, PrintWriter reporter,
						Properties properties) throws SAXException
		{
			protocol.append("*" + id + ":" + content);
		}
	}

	private ImportManager importManager;

	private File xmlFile = null;

	private StringBuilder protocol = new StringBuilder();

	private TestImportHandler aHandler;

	private TestImportHandler bHandler;

	@Before
	public void before() throws IOException
	{
		importManager = new ImportManagerImpl();
		((ImportManagerImpl) importManager).setLogger(new NullLoggerImpl ());

		List<ImportHandlerConfig> ihcs = new LinkedList<ImportHandlerConfig>();

		aHandler = new TestImportHandler("a");
		bHandler = new TestImportHandler("b");
		ihcs.add(new ImportHandlerConfig("a", "as", aHandler));
		ihcs.add(new ImportHandlerConfig("b", "bs", bHandler));
		((ImportManagerImpl) importManager).setImportHandlerConfigs(ihcs);

		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
						+ "<import><as><a atr=\"1\"><aa>x</aa>\n</a></as><bs><b atr=\"2\"><bb/></b></bs></import>";

		xmlFile = File.createTempFile("Aktera", ".xml");
		FileUtils.writeStringToFile(xmlFile, xml);
	}

	@After
	public void after()
	{
		xmlFile.delete();
	}

	@Test
	public void validateNotExistingXmlFile()
	{
		assertFalse(importManager.validateXmlFile(new File("this-file-does-not-exist.xml")));
	}

	@Test
	public void validateInvalidXmlFile()
	{
		assertFalse(importManager.validateXmlFile(new File("/etc/passwd")));
	}

	@Test
	public void validateValidXmlFile()
	{
		assertTrue(importManager.validateXmlFile(xmlFile));
	}

	@Test
	public void importXmlFile()
	{
		importManager.importXmlFile(xmlFile, new NullPrintWriter());
		assertEquals("Wrong handler call sequence", "$a:as+a:a+a:aa*a:x-a:aa-a:a^a:as$b:bs+b:b+b:bb-b:bb-b:b^b:bs",
						protocol.toString());
	}

	@Test
	public void importXmlFileOnlyATags()
	{
		importManager.importXmlFile(xmlFile, "a", new NullPrintWriter());
		assertEquals("Wrong handler call sequence", "$a:as+a:a+a:aa*a:x-a:aa-a:a^a:as", protocol.toString());
	}
}
