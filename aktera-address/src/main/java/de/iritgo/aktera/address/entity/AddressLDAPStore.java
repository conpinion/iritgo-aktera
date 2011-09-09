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

package de.iritgo.aktera.address.entity;


import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Configurable;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.simplelife.tools.*;


@Configurable
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@SuppressWarnings("serial")
public class AddressLDAPStore extends AddressStore
{
	public enum SearchScope
	{
		LEVEL, SUBTREE
	}

	/** Server host */
	@Length(min = 1, max = 255)
	@NotNull
	private String host;

	/** Server port (default port 389 if 0 or null) */
	@Min(value = 0)
	@Max(value = 65535)
	private Integer port;

	/** Authentication DN */
	@Length(max = 255)
	private String authDn;

	/** Authentication password */
	@Length(max = 255)
	private String authPassword;

	/** Base DN for address searches */
	@Length(min = 1, max = 255)
	@NotNull
	private String baseDn;

	/** List query expression */
	@Length(max = 255)
	private String query;

	/** Search scope */
	@Enumerated(EnumType.STRING)
	@NotNull
	private SearchScope scope;

	/** Size of a result page (0 or null disables paging) */
	@Min(value = 0)
	private Integer pageSize;

	/** Maximum number of entries returned per search (0 or null means infinite) */
	@Min(value = 0)
	private Integer maxEntries;

	/** Attribute names */
	private String attributeNames;

	/** Search attributes */
	@Length(max = 255)
	private String searchAttributes;

	/** Set to true if you want to use a connection pool */
	transient private boolean useConnectionPool = true;

	/** Attributes that should be included into the search */
	transient private List<String> searchAttributesList;

	/** Attributes to search for phone numbers */
	transient private List<String> phoneNumberSearchAttributesList;

	/** Search scope */
	transient private int ldapScope;

	/** Attribute names */
	transient private Properties attributeNamesProperties;

	/** True if the capabilities of the ldap server were verified */
	transient private boolean capsChecked;

	/** True if the server supports paged results */
	transient private boolean capPagedResults;

	/** True if the server supports server side sorting */
	transient private boolean capSort;

	@Override
	public void init ()
	{
		ldapScope = "level".equals (scope) ? SearchControls.ONELEVEL_SCOPE : SearchControls.SUBTREE_SCOPE;
		attributeNamesProperties = new Properties ();

		try
		{
			attributeNamesProperties.load (new StringReader (StringTools.trim (attributeNames)));
		}
		catch (IOException x)
		{
			logger.error ("[LDAPAddressStore] Error while reading the attribute names", x);
		}

		searchAttributesList = new ArrayList<String> ();

		for (String name : StringTools.trim (searchAttributes).split (","))
		{
			if (StringTools.isNotTrimEmpty (name))
			{
				searchAttributesList.add (StringTools.trim (name));
			}
		}

		if (searchAttributes.isEmpty ())
		{
			searchAttributesList.add ("cn");
			searchAttributesList.add ("sn");
			searchAttributesList.add ("telephoneNumber");
			searchAttributesList.add ("telexNumber");
			searchAttributesList.add ("mobile");
		}

		phoneNumberSearchAttributesList = new ArrayList<String> ();
		String attributeName = getLdapAttributeName ("businessNumber", "telephoneNumber");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("businessFaxNumber", "facsimileTelephoneNumber");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("businessMobileNumber", "mobile");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("businessDirectDialNumber", "otherTelephone");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("privateNumber", "homePhone");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("privateMobileNumber", "otherMobile");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("privateFaxNumber", "otherFacsimileTelephoneNumber");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
		attributeName = getLdapAttributeName ("voipNumber", "ipPhone");
		if (StringTools.isNotTrimEmpty (attributeName))
		{
			phoneNumberSearchAttributesList.add (attributeName);
		}
	}

	@Override
	public void shutdown ()
	{
	}

	@Override
	public Option<Address> findAddressByDn (Object addressDn)
	{
		LdapContext context = null;
		try
		{
			context = createLDAPContext ();
			SearchControls controls = new SearchControls ();
			controls.setSearchScope (ldapScope);
			Attributes attributes = context.getAttributes (addressDn.toString ());
			Address address = inflateAddress (attributes, addressDn);
			return new Full (address);
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}
		return new Empty ();
	}

	@Override
	public Option<Address> findAddressByLastNameOrCompany (String name)
	{
		LdapContext context = null;
		try
		{
			context = createLDAPContext ();
			SearchControls controls = new SearchControls ();
			controls.setSearchScope (ldapScope);
			NamingEnumeration<SearchResult> res = context.search (baseDn, "(sn=" + name + ")", controls);
			if (res.hasMore ())
			{
				SearchResult sr = res.next ();
				Attributes attributes = sr.getAttributes ();
				Address address = inflateAddress (attributes, sr.getNameInNamespace ());
				return new Full (address);
			}
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}
		return new Empty ();
	}

	@Override
	public List<Address> findAddressByNameStartsWith (String name)
	{
		List<Address> list = new LinkedList<Address> ();

		LdapContext context = null;

		try
		{
			context = createLDAPContext ();

			SearchControls controls = new SearchControls ();

			controls.setSearchScope (ldapScope);
			controls.setCountLimit (maxEntries);

			NamingEnumeration<SearchResult> res = context.search (baseDn, "(sn=" + name + "*)", controls);
			while (res.hasMore ())
			{
				SearchResult sr = res.next ();
				Attributes attributes = sr.getAttributes ();
				Address address = inflateAddress (attributes, sr.getNameInNamespace ());
				list.add (address);
			}
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}

		return list;
	}

	@Override
	public List<Address> findAddressOfOwnerByNameStartsWith (String name, Integer ownerId)
	{
		return findAddressByNameStartsWith (name);
	}

	@Override
	public Option<Address> findAddressByPhoneNumber (PhoneNumber phoneNumber)
	{
		LdapContext context = null;
		try
		{
			context = createLDAPContext ();
			SearchControls controls = new SearchControls ();
			controls.setSearchScope (ldapScope);
			String searchQuery = createQuery (phoneNumber.getInternalNumber (), phoneNumberSearchAttributesList);
			NamingEnumeration<SearchResult> res = context.search (baseDn, searchQuery, controls);
			if (res.hasMore ())
			{
				SearchResult sr = res.next ();
				Attributes attributes = sr.getAttributes ();
				Address address = inflateAddress (attributes, sr.getNameInNamespace ());
				return new Full (address);
			}
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}
		return new Empty ();
	}

	@Override
	public Option<Address> findAddressByPhoneNumber (String number)
	{
		LdapContext context = null;
		try
		{
			context = createLDAPContext ();
			SearchControls controls = new SearchControls ();
			controls.setSearchScope (ldapScope);
			String searchQuery = createQuery (number, phoneNumberSearchAttributesList);
			NamingEnumeration<SearchResult> res = context.search (baseDn, searchQuery, controls);
			if (res.hasMore ())
			{
				SearchResult sr = res.next ();
				Attributes attributes = sr.getAttributes ();
				Address address = inflateAddress (attributes, sr.getNameInNamespace ());
				return new Full (address);
			}
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}
		return new Empty ();
	}

	@Override
	public Option<Address> findAddressOfOwnerByPhoneNumber (PhoneNumber phoneNumber, Integer ownerId)
	{
		return new Empty ();
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersEndingWith (String number)
	{
		List<PhoneNumber> list = new LinkedList<PhoneNumber> ();
		LdapContext context = null;
		try
		{
			context = createLDAPContext ();
			SearchControls controls = new SearchControls ();
			controls.setSearchScope (ldapScope);
			String searchQuery = createQuery ("*" + number, phoneNumberSearchAttributesList);
			NamingEnumeration<SearchResult> res = context.search (baseDn, searchQuery, controls);
			if (res.hasMore ())
			{
				SearchResult sr = res.next ();
				Address address = new Address ();
				inflatePhoneNumbers (sr.getAttributes (), address);
				for (PhoneNumber pn : address.getPhoneNumbers ())
				{
					if (pn.getNumber ().endsWith (number))
					{
						list.add (pn);
					}
				}
			}
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}

		return list;
	}

	@Override
	public List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith (String number, Integer ownerId)
	{
		List<PhoneNumber> list = new LinkedList<PhoneNumber> ();

		if (ownerId != null)
		{
			return list;
		}

		LdapContext context = null;
		try
		{
			context = createLDAPContext ();
			SearchControls controls = new SearchControls ();
			controls.setSearchScope (ldapScope);
			String searchQuery = createQuery ("*" + number, phoneNumberSearchAttributesList);
			NamingEnumeration<SearchResult> res = context.search (baseDn, searchQuery, controls);
			if (res.hasMore ())
			{
				SearchResult sr = res.next ();
				Address address = new Address ();
				inflatePhoneNumbers (sr.getAttributes (), address);
				for (PhoneNumber pn : address.getPhoneNumbers ())
				{
					if (pn.getNumber ().endsWith (number))
					{
						list.add (pn);
					}
				}
			}
		}
		catch (NamingException x)
		{
			logger.error ("LDAP Error: " + x.toString ());
		}
		finally
		{
			closeLDAPContext (context);
		}

		return list;
	}

	@Override
	public Option<Address> findAddressByOwnerAndContactNumber (Integer ownerId, String contactNumber)
	{
		return new Empty ();
	}

	@Override
	public Option<Address> findAddressByOnwerAndFirstNameOrLastNameOrCompany (Integer ownerId, String firstName,
					String lastName, String company)
	{
		return new Empty ();
	}

	@Override
	public List<PhoneNumber> findAllPhoneNumbersByAddressDnSortedByCategoryList (String addressDn, String[] categories)
	{
		Option<Address> address = findAddressByDn (addressDn);
		List<PhoneNumber> result = new LinkedList<PhoneNumber> ();
		for (String category : categories)
		{
			result.add (address.get ().getPhoneNumberByCategory (category));
		}
		return result;
	}

	@Override
	public List<Address> createAddressListing (Integer userId, String search, String orderBy, SortOrder orderDir,
					int firstResult, int maxResults)
	{
		List<Address> res = new LinkedList<Address> ();

		if (! emptySearchReturnsAllEntries && StringTools.isTrimEmpty (search))
		{
			return res;
		}

		long count = 0;
		byte[] cookie = null;
		LdapContext context = null;

		try
		{
			context = createLDAPContext ();

			ArrayList<Control> requestControls = new ArrayList<Control> (2);

			if (pageSize > 0 && capPagedResults)
			{
				requestControls.add (new PagedResultsControl (pageSize, Control.CRITICAL));
			}

			SortControl sortControl = null;

			if (orderBy != null && capSort)
			{
				sortControl = new SortControl (new SortKey[]
				{
					new SortKey ("sn", orderDir == SortOrder.ASCENDING, null)
				}, Control.CRITICAL);
				requestControls.add (sortControl);
			}

			context.setRequestControls (requestControls.toArray (new Control[requestControls.size ()]));

			String searchQuery = createQuery (StringTools.isNotTrimEmpty (search) ? "*" + search + "*" : "*",
							searchAttributesList);

			SearchControls controls = new SearchControls ();

			controls.setSearchScope (ldapScope);
			controls.setCountLimit (maxEntries);

			boolean moreResults = true;

			while (moreResults)
			{
				NamingEnumeration<SearchResult> i = context.search (baseDn, searchQuery, controls);

				if (! i.hasMore () || count >= maxEntries)
				{
					i.close ();
					moreResults = false;

					break;
				}

				while (i.hasMore ())
				{
					++count;

					if (count > firstResult + maxResults)
					{
						moreResults = false;

						break;
					}

					SearchResult sr = i.next ();
					Attributes attributes = sr.getAttributes ();

					if (count > firstResult)
					{
						Address address = inflateAddress (attributes, sr.getNameInNamespace ());
						res.add (address);
					}
				}

				requestControls = new ArrayList<Control> (2);

				if (pageSize > 0 && capPagedResults)
				{
					Control[] c = context.getResponseControls ();

					if (c != null)
					{
						for (int j = 0; j < c.length; j++)
						{
							if (c[j] instanceof PagedResultsResponseControl)
							{
								PagedResultsResponseControl prrc = (PagedResultsResponseControl) c[j];

								cookie = prrc.getCookie ();
							}
						}
					}

					if (cookie != null)
					{
						requestControls.add (new PagedResultsControl (pageSize, cookie, Control.CRITICAL));
					}
					else
					{
						moreResults = false;
					}
				}
				else
				{
					moreResults = false;
				}

				if (sortControl != null)
				{
					requestControls.add (sortControl);
				}

				context.setRequestControls (requestControls.toArray (new Control[requestControls.size ()]));
			}
		}
		catch (Exception x)
		{
			System.out.println (x);
		}
		finally
		{
			closeLDAPContext (context);
		}

		return res;
	}

	@Override
	public void updateAddress (Address address)
	{
	}

	@Override
	public Object createAddress (Address address, Integer ownerId)
	{
		return null;
	}

	@Override
	public void deleteAddressWithDn (Object addressDn)
	{
	}

	@Override
	public long countAddressesByOwnerAndSearch (Integer userId, String search)
	{
		if (! emptySearchReturnsAllEntries && StringTools.isTrimEmpty (search))
		{
			return 0;
		}

		long count = 0;
		byte[] cookie = null;
		LdapContext context = null;

		try
		{
			context = createLDAPContext ();

			ArrayList<Control> requestControls = new ArrayList<Control> (2);

			if (pageSize > 0 && capPagedResults)
			{
				requestControls.add (new PagedResultsControl (pageSize, Control.CRITICAL));
			}

			context.setRequestControls (requestControls.toArray (new Control[requestControls.size ()]));

			SearchControls controls = new SearchControls ();

			controls.setSearchScope (ldapScope);
			controls.setCountLimit (maxEntries);
			controls.setReturningAttributes (new String[]
			{});
			controls.setReturningObjFlag (false);

			String searchQuery = createQuery (StringTools.isNotTrimEmpty (search) ? "*" + search + "*" : "*",
							searchAttributesList);

			boolean moreResults = true;

			while (moreResults)
			{
				NamingEnumeration<SearchResult> i = context.search (baseDn, searchQuery, controls);

				if (! i.hasMore () || count >= maxEntries)
				{
					moreResults = false;

					break;
				}

				while (i.hasMore ())
				{
					++count;

					if (count >= maxEntries)
					{
						break;
					}

					i.next ();
				}

				if (pageSize > 0 && capPagedResults)
				{
					Control[] c = context.getResponseControls ();

					if (c != null)
					{
						for (int j = 0; j < c.length; j++)
						{
							if (c[j] instanceof PagedResultsResponseControl)
							{
								PagedResultsResponseControl prrc = (PagedResultsResponseControl) c[j];

								cookie = prrc.getCookie ();
							}
						}
					}

					if (cookie != null)
					{
						context.setRequestControls (new Control[]
						{
							new PagedResultsControl (pageSize, cookie, Control.CRITICAL)
						});
					}
					else
					{
						moreResults = false;
					}
				}
			}
		}
		catch (Exception x)
		{
			System.out.println (x);
		}
		finally
		{
			closeLDAPContext (context);
		}

		return count;
	}

	@Override
	public void deleteAllAddresses ()
	{
	}

	@Override
	public boolean isGlobalStore ()
	{
		return true;
	}

	/**
	 * Get the LDAP attribute name of an Aktera attribute name.
	 *
	 * @param attributeName The Aktera attribute name
	 * @return The LDAP attribute name
	 */
	private String getLdapAttributeName (String attributeName, String defaultLdapAttributeName)
	{
		String ldapAttributeName = attributeNamesProperties.getProperty (attributeName);
		if (ldapAttributeName != null)
		{
			return ldapAttributeName;
		}
		if (defaultLdapAttributeName != null)
		{
			return defaultLdapAttributeName;
		}
		return "";
	}

	/**
	 * Verify the capabilities of the LDAP server.
	 *
	 * @param context The LDAP context
	 * @throws NamingException In case of an LDAP error
	 */
	private void checkCapabilities (LdapContext context) throws NamingException
	{
		SearchControls ctl = new SearchControls ();

		ctl.setReturningAttributes (new String[]
		{
			"supportedControl"
		});
		ctl.setSearchScope (SearchControls.OBJECT_SCOPE);

		NamingEnumeration results = context.search ("", "(objectClass=*)", ctl);

		while (results.hasMore ())
		{
			SearchResult entry = (SearchResult) results.next ();
			NamingEnumeration attrs = entry.getAttributes ().getAll ();

			while (attrs.hasMore ())
			{
				Attribute attr = (Attribute) attrs.next ();
				NamingEnumeration vals = attr.getAll ();

				while (vals.hasMore ())
				{
					String value = (String) vals.next ();

					if (value.equals (PagedResultsControl.OID))
					{
						capPagedResults = true;
					}
					else if (value.equals (SortControl.OID))
					{
						capSort = true;
					}
				}
			}
		}

		capsChecked = true;
	}

	private Address inflateAddress (Attributes attributes, Object addressDn)
	{
		Address address = new Address ();
		address.setAlternateId (addressDn.toString ());
		address.setLastName (getLDAPAttributeAsString (attributes, "lastName", "sn"));
		address.setFirstName (getLDAPAttributeAsString (attributes, "firstName", "givenName"));
		address.setCompany (getLDAPAttributeAsString (attributes, "company", "o"));
		address.setStreet (getLDAPAttributeAsString (attributes, "street", "street"));
		address.setSalutation (getLDAPAttributeAsString (attributes, "salutation", null));
		address.setPosition (getLDAPAttributeAsString (attributes, "position", "title"));
		address.setDivision (getLDAPAttributeAsString (attributes, "division", "ou"));
		address.setCity (getLDAPAttributeAsString (attributes, "city", "l"));
		address.setPostalCode (getLDAPAttributeAsString (attributes, "postalCode", "postalCode"));
		address.setCountry (getLDAPAttributeAsString (attributes, "country", "destinationIndicator"));
		address.setEmail (getLDAPAttributeAsString (attributes, "email", "mail"));
		address.setWeb (getLDAPAttributeAsString (attributes, "web", "seeAlso"));
		address.setRemark (getLDAPAttributeAsString (attributes, "remark", "description"));

		inflatePhoneNumbers (attributes, address);

		return address;
	}

	private void inflatePhoneNumbers (Attributes attributes, Address address)
	{
		PhoneNumber number = new PhoneNumber ();
		number.setCategory ("B");
		number.setNumber (getLDAPAttributeAsString (attributes, "businessNumber", "telephoneNumber"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("BF");
		number.setNumber (getLDAPAttributeAsString (attributes, "businessFaxNumber", "facsimileTelephoneNumber"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("BM");
		number.setNumber (getLDAPAttributeAsString (attributes, "businessMobileNumber", "mobile"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("BDD");
		number.setNumber (getLDAPAttributeAsString (attributes, "businessDirectDialNumber", "otherTelephone"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("P");
		number.setNumber (getLDAPAttributeAsString (attributes, "privateNumber", "homePhone"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("PM");
		number.setNumber (getLDAPAttributeAsString (attributes, "privateMobileNumber", "otherMobile"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("PF");
		number.setNumber (getLDAPAttributeAsString (attributes, "privateFaxNumber", "otherFacsimileTelephoneNumber"));
		address.addPhoneNumber (number);
		number = new PhoneNumber ();
		number.setCategory ("VOIP");
		number.setNumber (getLDAPAttributeAsString (attributes, "voipNumber", "ipPhone"));
		address.addPhoneNumber (number);
	}

	private LdapContext createLDAPContext () throws NamingException
	{
		Hashtable env = new Hashtable ();
		env.put ("java.naming.ldap.version", "3");
		env.put (Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		int ldapPort = NumberTools.toInt (port, 0);
		env.put (Context.PROVIDER_URL, "ldap://" + host + ":" + (ldapPort != 0 ? ldapPort : 389) + "/");
		env.put ("com.sun.jndi.ldap.connect.timeout", "5000");
		env.put ("com.sun.jndi.ldap.connect.pool", String.valueOf (useConnectionPool));

		if (StringTools.isNotTrimEmpty (authDn) && StringTools.isNotTrimEmpty (authPassword))
		{
			env.put (Context.SECURITY_AUTHENTICATION, "simple");
			env.put (Context.SECURITY_PRINCIPAL, authDn);
			env.put (Context.SECURITY_CREDENTIALS, authPassword);
		}

		LdapContext ldapContext = new InitialLdapContext (env, null);

		if (! capsChecked)
		{
			checkCapabilities (ldapContext);
		}

		return ldapContext;
	}

	private void closeLDAPContext (LdapContext ldapContext)
	{
		if (ldapContext != null)
		{
			try
			{
				ldapContext.close ();
			}
			catch (NamingException x)
			{
				logger.error ("LDAP Error: " + x.toString ());
			}
		}
	}

	/**
	 * Safely retrieve a LDAP attribute from an attribute set.
	 *
	 * @param attributes The LDAP attributes
	 * @param attributeName TODO
	 * @param ldapAttributeName The name of the attribute to retrieve
	 * @return The attribute value or an empty string if the attribute doesn't
	 *         exist
	 */
	private String getLDAPAttributeAsString (Attributes attributes, String attributeName, String ldapAttributeName)
	{
		String realAttributeName = getLdapAttributeName (attributeName, ldapAttributeName);
		Attribute attr = attributes.get (realAttributeName);
		if (attr != null)
		{
			try
			{
				return StringTools.trim (attr.get ());
			}
			catch (NamingException ignored)
			{
			}
		}
		return "";
	}

	/**
	 * Create the LDAP query expression.
	 *
	 * @param search The search text
	 * @return The search query
	 */
	private String createQuery (String search, List<String> attributes)
	{
		StringBuilder searchQuery = new StringBuilder ();
		if (! attributes.isEmpty () && StringTools.isNotTrimEmpty (search))
		{
			searchQuery.append ("(&");
		}
		searchQuery.append ("(" + (StringTools.isNotTrimEmpty (query) ? query : "objectClass=person") + ")");
		if (! attributes.isEmpty () && StringTools.isNotTrimEmpty (search))
		{
			if (attributes.size () > 1)
			{
				searchQuery.append ("(|");
			}

			for (String name : attributes)
			{
				searchQuery.append ("(" + name + "=" + search + ")");
			}

			if (attributes.size () > 1)
			{
				searchQuery.append (")");
			}

			searchQuery.append (")");
		}

		return searchQuery.toString ();
	}

	@Override
	public boolean canBeDeleted ()
	{
		return true;
	}

	@Override
	public void deleteAllAddressesOfOwner (Integer userId)
	{
	}
}
