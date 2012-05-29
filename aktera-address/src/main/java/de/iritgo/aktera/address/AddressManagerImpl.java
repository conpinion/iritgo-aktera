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

package de.iritgo.aktera.address;


import java.util.*;
import lombok.*;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.collections.*;
import org.springframework.cache.annotation.Cacheable;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.authentication.defaultauth.entity.*;
import de.iritgo.aktera.event.Event;
import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.startup.*;
import de.iritgo.simplelife.data.*;
import de.iritgo.simplelife.process.Procedure2;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.*;


public class AddressManagerImpl implements AddressManager, StartupHandler
{
	@Setter
	private List<AddressStore> addressStores = new LinkedList();

	@Setter
	private AddressDAO addressDAO;

	@Setter
	private Logger logger;

	@Setter
	private I18N i18n;

	@Setter
	private UserDAO userDAO;

	@Setter
	@Getter
	private List<AddressStoreType> addressStoreTypes;

	public AddressStore getAddressStoreByName(final String storeName)
	{
		AddressStore store = (AddressStore) CollectionUtils.find(addressStores, new Predicate()
		{
			public boolean evaluate(Object o)
			{
				return ((AddressStore) o).getName().equals(storeName);
			}
		});

		if (store != null)
		{
			return store;
		}

		return addressDAO.getNullAddressStore();
	}

	public AddressStore getAddressStoreById(final Integer id)
	{
		AddressStore store = (AddressStore) CollectionUtils.find(addressStores, new Predicate()
		{
			public boolean evaluate(Object o)
			{
				return ((AddressStore) o).getId().equals(id);
			}
		});
		if (store == null)
		{
			return addressDAO.getNullAddressStore();
		}
		return store;
	}

	public AddressStore getDefaultAddressStore()
	{
		AddressStore store = (AddressStore) CollectionUtils.find(addressStores, new Predicate()
		{
			public boolean evaluate(Object o)
			{
				return ((AddressStore) o).getDefaultStore();
			}
		});
		if (store == null)
		{
			store = addressDAO.getNullAddressStore();
		}
		return store;
	}

	public Option<Address> findAddressByStoreAndDn(String storeName, Object addressDn)
	{
		AddressStore store = getAddressStoreByName(storeName);
		Option<Address> address = store.findAddressByDn(addressDn);
		if (address.full())
		{
			address.get().setAddressStore(store);
		}
		return address;
	}

	public Option<Address> findAddressByLastNameOrCompany(String name)
	{
		for (AddressStore store : addressStores)
		{
			Option<Address> result = store.findAddressByLastNameOrCompany(name);
			if (result.full())
			{
				result.get().setAddressStore(store);
				return result;
			}
		}
		return new Empty();
	}

	@Cacheable("addressResolution")
	public Option<Address> findAddressByPhoneNumber(String phoneNumber)
	{
		for (AddressStore store : addressStores)
		{
			if (! store.getNumberLookup())
			{
				continue;
			}

			Option<Address> result = store.findAddressByPhoneNumber(phoneNumber);
			if (result.full())
			{
				result.get().setAddressStore(store);
				return result;
			}
		}
		return new Empty();
	}

	@Cacheable("addressResolution")
	public Option<Address> findAddressByPhoneNumber(String number, String countryPrefix, String localPrefix,
					String internationalPrefix, String nationalPrefix)
	{
		for (AddressStore store : addressStores)
		{
			if (! store.getNumberLookup())
			{
				continue;
			}

			Option<Address> address = store.findAddressByPhoneNumber (number, countryPrefix, localPrefix,
					internationalPrefix, nationalPrefix);

			if (address.full())
			{
				return address;
			}
		}

		return new Empty();
	}

	@Cacheable("addressResolution")
	public Option<Address> findAddressOfOwnerByPhoneNumber(Integer ownerId, String number, String countryPrefix,
					String localPrefix, String internationalPrefix, String nationalPrefix)
	{

		for (AddressStore store : addressStores)
		{
			if (! store.getNumberLookup())
			{
				continue;
			}

			Option<Address> address = store.findAddressOfOwnerByPhoneNumber(ownerId, number, countryPrefix,
					localPrefix, internationalPrefix, nationalPrefix);
			if (address.full())
			{
				return address;
			}
		}

		return new Empty();
	}

	public String formatAddressTemplate(Address address, String template, Locale locale)
	{
		return StringTools.replaceTemplate(template, convertAddressToProperties(address, locale), true);
	}

	public Properties convertAddressToProperties(Address address, Locale locale)
	{
		Properties displayProperties = new Properties();

		displayProperties.setProperty("city", address.getCity() != null ? address.getCity() : "");
		displayProperties.setProperty("company", address.getCompany() != null ? address.getCompany() : "");
		displayProperties.setProperty("companyWithComma", ! StringTools.isTrimEmpty(address.getCompany()) ? ", "
						+ address.getCompany() : "");
		displayProperties.setProperty("companyNumber", address.getCompanyNumber() != null ? address.getCompanyNumber()
						: "");
		displayProperties.setProperty("contactNumber", address.getContactNumber() != null ? address.getContactNumber()
						: "");
		displayProperties.setProperty("country", address.getCountry() != null ? address.getCountry() : "");
		displayProperties.setProperty("division", address.getDivision() != null ? address.getDivision() : "");
		displayProperties.setProperty("email", address.getEmail() != null ? address.getEmail() : "");
		displayProperties.setProperty("firstName", address.getFirstName() != null ? address.getFirstName() : "");
		displayProperties.setProperty("firstNameFirstChar", ! StringTools.isTrimEmpty(address.getFirstName()) ? address
						.getFirstName().substring(0, 1) : "");
		displayProperties.setProperty("firstNameAbbr", ! StringTools.isTrimEmpty(address.getFirstName()) ? address
						.getFirstName().substring(0, 1)
						+ ". " : "");
		displayProperties.setProperty("lastName", address.getLastName() != null ? address.getLastName() : "");
		displayProperties.setProperty("position", address.getPosition() != null ? address.getPosition() : "");
		displayProperties.setProperty("postalCode", address.getPostalCode() != null ? address.getPostalCode() : "");
		displayProperties.setProperty("remark", address.getRemark() != null ? address.getRemark() : "");

		if (! StringTools.isTrimEmpty(address.getSalutation()) && locale != null)
		{
			String salutation = i18n.msg(locale, "Aktera", address.getSalutation());

			displayProperties.setProperty("salutation", salutation);
		}
		else
		{
			displayProperties.setProperty("salutation", "");
		}

		displayProperties.setProperty("street", address.getStreet() != null ? address.getStreet() : "");
		displayProperties.setProperty("web", address.getWeb() != null ? address.getWeb() : "");

		return displayProperties;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup() throws StartupException
	{
		initializeAddressStores();
	}

	public void initializeAddressStores()
	{
		for (AddressStore store : addressStores)
		{
			store.shutdown();
		}
		addressStores.clear();
		for (AddressStore addressStore : addressDAO.findAllAddressStores())
		{
			try
			{
				addressStore.init();
				addressStores.add(addressStore);
			}
			catch (Exception x)
			{
				logger.error("Unable to create address store", x);
			}
		}
	}

	public List<Tuple2<String, String>> listAddressStoresNameAndTitle()
	{
		List<Tuple2<String, String>> res = new LinkedList();
		for (AddressStore store : addressStores)
		{
			res.add(new Tuple2<String, String>(store.getName(), store.getDisplayedTitle()));
		}
		return res;
	}

	public List<Tuple2<Integer, String>> listAddressStoresIdAndTitle()
	{
		List<Tuple2<Integer, String>> res = new LinkedList();
		for (AddressStore store : addressStores)
		{
			res.add(new Tuple2<Integer, String>(store.getId(), store.getDisplayedTitle()));
		}
		return res;
	}

	public List<Tuple2<Integer, String>> listSystemAddressStoresIdAndTitle()
	{
		List<Tuple2<Integer, String>> res = new LinkedList();
		for (AddressStore store : addressStores)
		{
			if (store.getSystemStore())
			{
				res.add(new Tuple2<Integer, String>(store.getId(), store.getDisplayedTitle()));
			}
		}
		return res;
	}

	public List<Tuple3<Integer, String, String>> listAddressStoresIdAndNameAndTitle()
	{
		List<Tuple3<Integer, String, String>> res = new LinkedList();
		for (AddressStore store : addressStores)
		{
			res.add(new Tuple3<Integer, String, String>(store.getId(), store.getName(), store.getDisplayedTitle()));
		}
		return res;
	}

	public boolean isAddressStoreEditable(String storeName)
	{
		return getAddressStoreByName(storeName).getEditable();
	}

	public boolean isAddressStoreGlobal(String storeName)
	{
		return getAddressStoreByName(storeName).isGlobalStore();
	}

	public void shutdown() throws ShutdownException
	{
	}

	private String removePrefixesFromPhoneNumber(String number, String countryPrefix, String localPrefix,
					String nationalPrefix)
	{
		// Remove like 0049
		if (number.startsWith(countryPrefix))
		{
			number = number.substring(countryPrefix.length());
		} // Remove like 0231
		else if (number.startsWith(nationalPrefix + localPrefix))
		{
			number = number.substring(nationalPrefix.length() + localPrefix.length());
		} // Remove like 231
		else if (number.startsWith(localPrefix) && (localPrefix.length() != number.length()))
		{
			number = number.substring(localPrefix.length());
		} // Remove like 0
		else if (number.startsWith(nationalPrefix))
		{
			number = number.substring(nationalPrefix.length());
		}

		return number;
	}

	public String formatNameWithSalutation(Address address, Locale locale, SalutationFormatMode mode)
	{
		String salutation = null;
		String firstName = null;
		String lastName = null;

		if (! StringTools.isTrimEmpty(address.getSalutation()) && locale != null)
		{
			salutation = i18n.msg(locale, "Aktera", address.getSalutation());
		}
		if (! StringTools.isTrimEmpty(address.getFirstName()))
		{
			firstName = address.getFirstName();
		}
		if (! StringTools.isTrimEmpty(address.getLastName()))
		{
			lastName = address.getLastName();
		}

		switch (mode)
		{
			case NORMAL:
			{
				String formarted = salutation != null ? salutation + " " : "";
				formarted += firstName != null ? firstName + " " : "";
				formarted += lastName != null ? lastName : "";
				return formarted;
			}
			case LETTER:
			{
				// TODO: "Sehr geehrter Herr"
				String formarted = salutation != null ? salutation + " " : "";
				formarted += firstName != null ? firstName + " " : "";
				formarted += lastName != null ? lastName : "";
				return formarted;
			}
			case FIRST_LASTNAME:
			{
				String formarted = salutation != null ? salutation + " " : "";
				formarted += lastName != null ? lastName : "";
				formarted += firstName != null ? ", " + firstName : "";
				return formarted;
			}
		}
		return null;
	}

	public AddressStoreType getAddressStoreType(String key) throws NoSuchAddressStoreTypeException
	{
		for (AddressStoreType type : addressStoreTypes)
		{
			if (type.getKey().equals(key))
			{
				return type;
			}
		}
		throw new NoSuchAddressStoreTypeException();
	}

	public void deleteAllAddressesOfOwner(Integer userId)
	{
		for (AddressStore store : addressStores)
		{
			store.deleteAllAddressesOfOwner(userId);
		}
	}

	public void onUserCreated(Event event)
	{
		Procedure2 createPhoneNumberForAddress = new Procedure2()
		{
			public void execute(Object address, Object category)
			{
				Address theAddress = ((Option<Address>) address).get();
				String theCategory = (String) category;
				PhoneNumber number = new PhoneNumber();
				number.setCategory(theCategory);
				theAddress.addPhoneNumber(number);
			}
		};
		AkteraUser user = userDAO.findUserById((Integer) event.getProperties().get("id"));
		Party party = addressDAO.getPartyByUserId(user.getId());
		Option<Address> address = addressDAO.findAddressByPartyId(party.getId());
		if (address.full())
		{
			createPhoneNumberForAddress.execute(address, "B");
			createPhoneNumberForAddress.execute(address, "BM");
			createPhoneNumberForAddress.execute(address, "BDD");
			createPhoneNumberForAddress.execute(address, "BF");
			createPhoneNumberForAddress.execute(address, "P");
			createPhoneNumberForAddress.execute(address, "PM");
			createPhoneNumberForAddress.execute(address, "PF");
			createPhoneNumberForAddress.execute(address, "VOIP");
			addressDAO.updateAddress(address.get());
		}
	}

	public void onUserRenamed(Event event)
	{
		Integer userId = (Integer) event.getProperties().get("id");
		String newFirstName = event.getProperties().getProperty("newFirstName");
		String newLastName = event.getProperties().getProperty("newLastName");
		String newEmail = event.getProperties().getProperty("newEmail");

		Option<Address> address = addressDAO.findAddressByUserId(userId);

		if (address.full())
		{
			if (StringTools.isNotTrimEmpty(newFirstName))
			{
				address.get().setFirstName(newFirstName);
			}

			if (StringTools.isNotTrimEmpty(newLastName))
			{
				address.get().setLastName(newLastName);
			}

			if (StringTools.isNotTrimEmpty(newEmail))
			{
				address.get().setEmail(newEmail);
			}

			addressDAO.updateAddress(address.get());
		}
	}

	public void onUserDeleted(Event event)
	{
		deleteAllAddressesOfOwner((Integer) event.getProperties().get("id"));
	}
}
