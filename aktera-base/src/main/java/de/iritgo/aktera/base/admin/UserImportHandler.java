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

package de.iritgo.aktera.base.admin;


import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.importer.ImportHandler;
import de.iritgo.aktera.importer.IterableNodeList;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.ui.ng.formular.Edit;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.simplelife.string.StringTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.PrintWriter;
import java.util.Properties;


/**
 *
 */
public class UserImportHandler implements ImportHandler
{
	public boolean analyze(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException, XPathExpressionException
	{
		PersistentFactory persistentManager = null;

		try
		{
			persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE, req.getDomain());
		}
		catch (ModelException x)
		{
			reporter.println("[UserImportHandler] Error: " + x);

			return false;
		}

		XPath xPath = XPathFactory.newInstance().newXPath();

		int numUsers = 0;

		NodeList userElems = (NodeList) xPath.evaluate("users/user", importElem, XPathConstants.NODESET);

		for (Node userElem : new IterableNodeList(userElems))
		{
			try
			{
				String systemName = StringTools.trim(xPath.evaluate("systemName", userElem));

				if (StringTools.isTrimEmpty(systemName))
				{
					continue;
				}

				Persistent user = persistentManager.create("keel.user");

				user.setField("name", systemName);

				if (user.find())
				{
					continue;
				}

				++numUsers;
			}
			catch (PersistenceException x)
			{
				reporter.println("[UserImportHandler] Error: " + x);
			}
		}

		if (numUsers > 0)
		{
			reporter.println(i18n.msg(req, "Aktera", "numUsersWillBeCreated", new Integer(numUsers)));
		}

		return true;
	}

	public boolean perform(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException, XPathExpressionException
	{
		PersistentFactory persistentManager = null;

		try
		{
			persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE, req.getDomain());
		}
		catch (ModelException x)
		{
			reporter.println("[UserImportHandler] Error: " + x);

			return false;
		}

		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList userElems = (NodeList) xPath.evaluate("users/user", importElem, XPathConstants.NODESET);

		for (Node userElem : new IterableNodeList(userElems))
		{
			try
			{
				String systemName = StringTools.trim(xPath.evaluate("systemName", userElem));

				if (StringTools.isTrimEmpty(systemName))
				{
					reporter.println("User import error: No <systemName> tag specified");

					continue;
				}

				Persistent user = persistentManager.create("keel.user");

				user.setField("name", systemName);

				if (user.find())
				{
					continue;
				}

				String password = StringTools.trim(xPath.evaluate("password", userElem));

				if (StringTools.isTrimEmpty(password))
				{
					reporter.println("User import error: No <password> tag specified for user '" + systemName + "'");

					continue;
				}

				String lastName = StringTools.trim(xPath.evaluate("lastName", userElem));

				if (StringTools.isTrimEmpty(lastName))
				{
					reporter.println("User import error: No <lastName> tag specified for user '" + systemName + "'");

					continue;
				}

				String email = StringTools.trim(xPath.evaluate("email", userElem));

				if (StringTools.isTrimEmpty(email))
				{
					reporter.println("User import error: No <email> tag specified for user '" + systemName + "'");

					continue;
				}

				FormularDescriptor formular = Edit.start(req, "aktera.admin.user.edit");

				user = formular.getPersistents().getPersistent("sysUser");
				user.setField("name", systemName);
				formular.getPersistents().putAttribute("passwordNew", password);
				formular.getPersistents().putAttribute("passwordNewRepeat", password);

				String pin = StringTools.trim(xPath.evaluate("pin", userElem));

				if (! StringTools.isTrimEmpty(pin))
				{
					formular.getPersistents().putAttribute("pinNew", pin);
					formular.getPersistents().putAttribute("pinNewRepeat", pin);
				}

				Persistent preferences = formular.getPersistents().getPersistent("preferences");

				preferences.setField("canChangePassword", Boolean.TRUE);

				String company = StringTools.trim(xPath.evaluate("company", userElem));
				String firstName = StringTools.trim(xPath.evaluate("firstName", userElem));

				Persistent address = formular.getPersistents().getPersistent("address");

				address.setField("firstName", firstName);
				address.setField("lastName", lastName);
				address.setField("company", company);
				address.setField("salutation", StringTools.trim(xPath.evaluate("salutation", userElem)));
				address.setField("division", StringTools.trim(xPath.evaluate("division", userElem)));
				address.setField("position", StringTools.trim(xPath.evaluate("position", userElem)));
				address.setField("street", StringTools.trim(xPath.evaluate("street", userElem)));
				address.setField("postalCode", StringTools.trim(xPath.evaluate("postalCode", userElem)));
				address.setField("city", StringTools.trim(xPath.evaluate("city", userElem)));
				address.setField("country", StringTools.trim(xPath.evaluate("country", userElem)));
				address.setField("email", StringTools.trim(xPath.evaluate("email", userElem)));
				address.setField("web", StringTools.trim(xPath.evaluate("web", userElem)));
				address.setField("remark", StringTools.trim(xPath.evaluate("remark", userElem)));

				Edit.finish(req, "aktera.admin.user.save");

				reporter.println("New user: " + systemName);
			}
			catch (Exception x)
			{
				reporter.println("[UserImportHandler] Error: " + x);
			}
		}

		return true;
	}

	public void startRootElement(String uri, String localName, String name, Attributes attributes,
					PrintWriter reporter, Properties properties)
	{
	}

	public void endRootElement(String uri, String localName, String name, PrintWriter reporter, Properties properties)
		throws SAXException
	{
	}

	public void startElement(String uri, String localName, String name, Attributes attributes, PrintWriter reporter,
					Properties properties)
	{
	}

	public void endElement(String uri, String localName, String name, PrintWriter reporter, Properties properties)
		throws SAXException
	{
	}

	public void elementContent(String uri, String localName, String name, String content, PrintWriter reporter,
					Properties properties) throws SAXException
	{
	}
}
