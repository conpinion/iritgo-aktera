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

package de.iritgo.aktera.address.ui;


import java.io.PrintWriter;
import java.util.*;
import javax.xml.xpath.*;
import org.apache.avalon.framework.context.*;
import org.apache.avalon.framework.service.ServiceException;
import org.w3c.dom.*;
import org.xml.sax.*;
import de.iritgo.aktera.address.AddressManager;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.importer.*;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.*;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.*;

public class AddressImportHandler implements ImportHandler
{
	private static final int BULK_IMPORT_SIZE = 500;

	@SuppressWarnings("serial")
	private class DataCollector extends ImportDataCollector
	{
		public String phoneNumberCategory;
	}

	private ThreadLocal<DataCollector> data = new ThreadLocal<DataCollector>()
	{
		@Override
		protected DataCollector initialValue()
		{
			return new DataCollector();
		}
	};

	public boolean analyze(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException, XPathExpressionException
	{
		boolean bulkImport = (Boolean) properties.get("bulkImport");

		AddressManager addressManager = (AddressManager) SpringTools.getBean(AddressManager.ID);
		String addressStoreId = (String) properties.getProperty("destination", addressManager.getDefaultAddressStore()
						.getName());

		AddressStore addressStore = null;

		addressStore = addressManager.getAddressStoreByName(addressStoreId);

		if (! addressStore.getEditable())
		{
			reporter.println(i18n.msg(req, "AkteraAddress", "addressImportHandlerStoreNotWriteable"));

			return false;
		}

		Integer ownerId = UserTools.getCurrentUserId(req);

		if (addressStore.isGlobalStore())
		{
			ownerId = null;
		}

		XPath xPath = XPathFactory.newInstance().newXPath();

		int numNewAddresses = 0;
		int numOldAddresses = 0;

		NodeList addressElems = (NodeList) xPath.evaluate("addresses/address", importElem, XPathConstants.NODESET);

		for (Node addressElem : new IterableNodeList(addressElems))
		{
			try
			{
				String addressCategory = addressElem.getAttributes().getNamedItem("category").getTextContent();
				NodeList addressNodes = addressElem.getChildNodes();
				int nodePos = 1;
				nodePos++;
				String firstName = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String lastName = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String company = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				nodePos++;
				nodePos++;
				nodePos++;
				nodePos++;
				nodePos++;
				nodePos++;
				nodePos++;
				nodePos++;
				String contactNumber = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				nodePos++;

				if (StringTools.isTrimEmpty(company) && StringTools.isTrimEmpty(lastName))
				{
					continue;
				}

				if (! bulkImport && ! StringTools.isTrimEmpty(contactNumber)
								&& addressStore.findAddressByOwnerAndContactNumber(ownerId, contactNumber).full())
				{
					++numOldAddresses;
				}
				else
				{
					if (! bulkImport)
					{
						Option<Address> address = addressStore.findAddressByOnwerAndFirstNameOrLastNameOrCompany(ownerId,
								firstName, lastName, company);

						if (address.full())
						{
							++numOldAddresses;
						}
						else
						{
							++numNewAddresses;
						}
					}
					else
					{
						++numNewAddresses;
					}
				}
			}
			catch (Exception x)
			{
				reporter.println(i18n.msg(req, "AkteraAddress", "addressImportHandlerAnalyzeError", x));
			}
		}

		if (numNewAddresses > 0)
		{
			reporter.println(i18n.msg(req, "AkteraAddress", "numAddressEntriesWillBeCreated", new Integer(
							numNewAddresses)));
		}

		if (numOldAddresses > 0)
		{
			reporter.println(i18n.msg(req, "AkteraAddress", "numAddressEntriesWillBeUpdated", new Integer(
							numOldAddresses)));
		}

		return true;
	}

	public boolean perform(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException, XPathExpressionException
	{
		boolean bulkImport = (Boolean) properties.get("bulkImport");

		if (bulkImport)
		{
			reporter.println(i18n.msg(req, "AkteraAddress", "addressImportBulkMode"));
		}

		AddressManager addressManager = (AddressManager) SpringTools.getBean(AddressManager.ID);
		String addressStoreId = (String) properties.getProperty("destination", addressManager.getDefaultAddressStore()
						.getName());

		AddressStore addressStore = null;

		addressStore = addressManager.getAddressStoreByName(addressStoreId);

		if (! addressStore.getEditable())
		{
			reporter.println(i18n.msg(req, "AkteraAddress", "addressImportHandlerStoreNotWriteable"));

			return false;
		}

		Integer ownerId = UserTools.getCurrentUserId(req);

		if (addressStore.isGlobalStore())
		{
			ownerId = null;
		}

		String category = StringTools.trim (addressStore.getCategory());

		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList addressElems = (NodeList) xPath.evaluate("addresses/address", importElem, XPathConstants.NODESET);

		Collection<Address> bulkAddresses = new ArrayList<Address> ();
		long startImportTime = System.currentTimeMillis();
		for (Node addressElem : new IterableNodeList(addressElems))
		{
			try
			{
				String addressCategory = addressElem.getAttributes().getNamedItem("category").getTextContent();
				NodeList addressNodes = addressElem.getChildNodes();
				int nodePos = 1;
				String salutation = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String firstName = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String lastName = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String company = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String division = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String position = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String street = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String postalCode = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String city = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String country = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String email = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String web = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String contactNumber = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				String companyNumber = addressNodes.item(nodePos++).getTextContent();
				nodePos++;
				Node phoneNumberElem = addressNodes.item(nodePos++);
				NodeList phoneNumberNodes = phoneNumberElem.getChildNodes();
				int phoneNumberPos = 1;
				String numberB = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberBM = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberBF = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberBDD = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberP = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberPM = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberPF = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				phoneNumberPos++;
				String numberVOIP = phoneNumberNodes.item(phoneNumberPos++).getTextContent();
				nodePos++;
				String remark = addressNodes.item(nodePos++).getTextContent();

				if (StringTools.isTrimEmpty(company) && StringTools.isTrimEmpty(lastName))
				{
					reporter.println(i18n.msg(req, "AkteraAddress", "noLastNameOrCompanyTagFound"));

					continue;
				}

				String name = "";

				if (! StringTools.isTrimEmpty(lastName))
				{
					name = name + lastName;
				}

				if (! StringTools.isTrimEmpty(firstName))
				{
					if (! StringTools.isTrimEmpty(lastName))
					{
						name = name + " , ";
					}

					name = name + firstName + " ";
				}

				if (! StringTools.isTrimEmpty(company))
				{
					if (! StringTools.isTrimEmpty(name))
					{
						name = name + " - ";
					}

					name = name + company;
				}

				Option<Address> address = new Empty();
				if (! bulkImport)
				{
					if (! StringTools.isTrimEmpty(contactNumber))
					{
						address = addressStore.findAddressByOwnerAndContactNumber(ownerId, contactNumber);
						if (address.full())
						{
							reporter.println(i18n.msg(req, "AkteraAddress", "updateAddressEntry", name));
						}
					}
					else
					{
						address = addressStore.findAddressByOnwerAndFirstNameOrLastNameOrCompany(ownerId, firstName,
								lastName, company);

						if (address.full())
						{
							reporter.println(i18n.msg(req, "AkteraAddress", "updateAddressEntry", name));
						}
						else
						{
							reporter.println(i18n.msg(req, "AkteraAddress", "newAddressEntry", name));
						}
					}
				}

				if (address.empty())
				{
					address = new Full(new Address());
				}

				address.get().setCategory(category);
				address.get().setFirstName(firstName);
				address.get().setLastName(lastName);
				address.get().setCompany(company);
				address.get().setSalutation(StringTools.trim(salutation));
				address.get().setDivision(StringTools.trim(division));
				address.get().setPosition(StringTools.trim(position));
				address.get().setStreet(StringTools.trim(street));
				address.get().setPostalCode(StringTools.trim(postalCode));
				address.get().setCity(StringTools.trim(city));
				address.get().setCountry(StringTools.trim(country));
				address.get().setEmail(StringTools.trim(email));
				address.get().setWeb(StringTools.trim(web));
				address.get().setContactNumber(StringTools.trim(contactNumber));
				address.get().setCompanyNumber(StringTools.trim(companyNumber));
//				address.get().setSourceSystemId(StringTools.trim(xPath.evaluate("sourceSystemId", addressElem)));
//				address.get().setSourceSystemClient(StringTools.trim(xPath.evaluate("sourceSystemClient", addressElem)));
				address.get().setSourceSystemId(StringTools.trim(addressCategory));
				address.get().setSourceSystemClient(StringTools.trim(addressCategory));
				address.get().setRemark(StringTools.trim(remark));

				address.get()
								.getPhoneNumberByCategory("B")
								.setNumber(StringTools.trim(numberB));
				address.get()
								.getPhoneNumberByCategory("BM")
								.setNumber(StringTools.trim(numberBM));
				address.get()
								.getPhoneNumberByCategory("BF")
								.setNumber(StringTools.trim(numberBF));
				address.get()
								.getPhoneNumberByCategory("BDD")
								.setNumber(StringTools.trim(numberBDD));
				address.get()
								.getPhoneNumberByCategory("P")
								.setNumber(StringTools.trim(numberP));
				address.get()
								.getPhoneNumberByCategory("PM")
								.setNumber(StringTools.trim(numberPM));
				address.get()
								.getPhoneNumberByCategory("PF")
								.setNumber(StringTools.trim(numberPF));
				address.get()
								.getPhoneNumberByCategory("VOIP")
								.setNumber(StringTools.trim(numberVOIP));
				if (! bulkImport)
				{
					if (address.get().getId() != null)
					{
						addressStore.updateAddress(address.get());
					}
					else
					{
						addressStore.createAddress(address.get(), ownerId);
					}
				}
				else
				{
					bulkAddresses.add (address.get ());
					if (bulkAddresses.size () == BULK_IMPORT_SIZE)
					{
						long startTime = System.currentTimeMillis();
						addressStore.bulkImport (bulkAddresses);
						reporter.println(i18n.msg(req, "AkteraAddress", "numAddressInSekAdded", new Integer(
								bulkAddresses.size ()), new Integer ((int) (System.currentTimeMillis()-startTime)/1000)));
						bulkAddresses.clear();
					}
				}
			}
			catch (Exception x)
			{
				reporter.println(i18n.msg(req, "AkteraAddress", "addressImportHandlerPerformError", x));
			}
		}
		if (bulkImport)
		{
			addressStore.bulkImport (bulkAddresses);
		}
		reporter.println(i18n.msg(req, "AkteraAddress", "numAddressInSekAdded", new Integer(
				addressElems.getLength()), new Integer ((int) (System.currentTimeMillis()-startImportTime)/1000)));

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
		if ("phoneNumber".equals(localName))
		{
			data.get().phoneNumberCategory = attributes.getValue("category");
		}
	}

	public void endElement(String uri, String localName, String name, PrintWriter reporter, Properties properties)
		throws SAXException
	{
		if ("address".equals(localName))
		{
			ModelRequest request = null;

			try
			{
				UserEnvironment userEnvironment = new FakeAdminUserEnvironment();
				Context context = new DefaultContext();
				((DefaultContext) context).put(UserEnvironment.CONTEXT_KEY, userEnvironment);
				request = (ModelRequest) KeelTools.getService(ModelRequest.ROLE, "default-request", context);
				createOrUpdateAddress(request, data.get(), reporter, properties);
				data.remove();
			}
			catch (ServiceException x)
			{
			}
			finally
			{
				KeelTools.releaseService(request);
			}
		}
	}

	public void elementContent(String uri, String localName, String name, String content, PrintWriter reporter,
					Properties properties) throws SAXException
	{
		if ("phoneNumber".equals(localName))
		{
			data.get().put("phoneNumber" + data.get().phoneNumberCategory, content);
		}
		else
		{
			data.get().put(localName, content);
		}
	}

	protected void createOrUpdateAddress(ModelRequest request, DataCollector addressData, PrintWriter reporter,
					Properties properties)
	{
		try
		{
			boolean bulkImport = (Boolean) properties.get("bulkImport");

			I18N i18n = (I18N) request.getSpringBean(I18N.ID);

			AddressManager addressManager = (AddressManager) SpringTools.getBean(AddressManager.ID);
			String addressStoreId = properties
							.getProperty("destination", properties.getProperty("addressStoreId", addressManager
											.getDefaultAddressStore().getName()));
			AddressStore addressStore = null;

			addressStore = addressManager.getAddressStoreByName(addressStoreId);

			if (! addressStore.getEditable())
			{
				reporter.println(i18n.msg(request, "AkteraAddress", "addressImportHandlerStoreNotWriteable"));

				return;
			}

			Integer ownerId = UserTools.getCurrentUserId(request);

			if (addressStore.isGlobalStore())
			{
				ownerId = null;
			}

			String company = StringTools.trim(addressData.get("company"));
			String lastName = StringTools.trim(addressData.get("lastName"));
			String firstName = StringTools.trim(addressData.get("firstName"));
			String contactNumber = StringTools.trim(addressData.get("contactNumber"));

			if (StringTools.isTrimEmpty(company) && StringTools.isTrimEmpty(lastName))
			{
				reporter.println(i18n.msg(request, "AkteraAddress", "noLastNameOrCompanyTagFound"));

				return;
			}

			String displayName = "";

			if (! StringTools.isTrimEmpty(lastName))
			{
				displayName = displayName + lastName;
			}

			if (! StringTools.isTrimEmpty(firstName))
			{
				if (! StringTools.isTrimEmpty(lastName))
				{
					displayName = displayName + ", ";
				}

				displayName = displayName + firstName + " ";
			}

			if (! StringTools.isTrimEmpty(company))
			{
				if (! StringTools.isTrimEmpty(displayName))
				{
					displayName = displayName + " ";
				}

				displayName = displayName + company;
			}

			Option<Address> address = new Empty();
			if (! bulkImport)
			{
				if (! StringTools.isTrimEmpty(contactNumber))
				{
					address = addressStore.findAddressByOwnerAndContactNumber(ownerId, contactNumber);

					if (address.full())
					{
						reporter.println(i18n.msg(request, "AkteraAddress", "updateAddressEntry", displayName));
					}
				}
				else
				{
					address = addressStore.findAddressByOnwerAndFirstNameOrLastNameOrCompany(ownerId, firstName, lastName,
							company);

					if (address.full())
					{
						reporter.println(i18n.msg(request, "AkteraAddress", "updateAddressEntry", displayName));
					}
					else
					{
						reporter.println(i18n.msg(request, "AkteraAddress", "newAddressEntry", displayName));
					}
				}
			}

			if (address.empty())
			{
				address = new Full(new Address());
			}

			address.get().setFirstName(firstName);
			address.get().setLastName(lastName);
			address.get().setCompany(company);
			address.get().setSalutation((String) addressData.get("salutation"));
			address.get().setDivision((String) addressData.get("division"));
			address.get().setPosition((String) addressData.get("position"));
			address.get().setStreet((String) addressData.get("street"));
			address.get().setPostalCode((String) addressData.get("postalCode"));
			address.get().setCity((String) addressData.get("city"));
			address.get().setCountry((String) addressData.get("country"));
			address.get().setEmail((String) addressData.get("email"));
			address.get().setWeb((String) addressData.get("web"));
			address.get().setContactNumber((String) addressData.get("contactNumber"));
			address.get().setCompanyNumber((String) addressData.get("companyNumber"));
			address.get().setSourceSystemId((String) addressData.get("sourceSystemId"));
			address.get().setSourceSystemClient((String) addressData.get("sourceSystemClient"));
			address.get().setRemark((String) addressData.get("remark"));

			address.get().getPhoneNumberByCategory("B").setNumber((String) addressData.get("phoneNumberB"));
			address.get().getPhoneNumberByCategory("BM").setNumber((String) addressData.get("phoneNumberBM"));
			address.get().getPhoneNumberByCategory("BF").setNumber((String) addressData.get("phoneNumberBF"));
			address.get().getPhoneNumberByCategory("BDD").setNumber((String) addressData.get("phoneNumberBDD"));
			address.get().getPhoneNumberByCategory("P").setNumber((String) addressData.get("phoneNumberP"));
			address.get().getPhoneNumberByCategory("PM").setNumber((String) addressData.get("phoneNumberPM"));
			address.get().getPhoneNumberByCategory("PF").setNumber((String) addressData.get("phoneNumberPF"));
			address.get().getPhoneNumberByCategory("VOIP").setNumber((String) addressData.get("phoneNumberVOIP"));

			if (address.get().getId() != null)
			{
				addressStore.updateAddress(address.get());
			}
			else
			{
				addressStore.createAddress(address.get(), ownerId);
			}
		}
		catch (Exception x)
		{
			reporter.println("[AddressImportHandler] Error: " + x);
		}
	}
}
