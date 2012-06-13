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


import java.sql.*;
import java.util.*;

import org.hibernate.*;
import org.springframework.dao.*;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.*;
import org.springframework.transaction.annotation.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.aktera.authentication.defaultauth.entity.*;
import de.iritgo.simplelife.constants.*;
import de.iritgo.simplelife.math.*;
import de.iritgo.simplelife.string.*;
import de.iritgo.simplelife.tools.*;


@Transactional(readOnly = true)
public class AddressDAOImpl extends HibernateDaoSupport implements AddressDAO
{
	private AddressNullStore nullAddressStore = new AddressNullStore();

	public AddressStore getNullAddressStore()
	{
		return nullAddressStore;
	}

	public Party getPartyById(Integer id)
	{
		return (Party) getHibernateTemplate().get(Party.class, id);
	}

	public Party getPartyByUserId(Integer userId)
	{
		List<Party> res = getHibernateTemplate().find("from Party where userId = ?", userId);
		if (res.size() > 0)
		{
			return res.get(0);
		}
		throw new DataRetrievalFailureException("Unable to find party of user with id " + userId);
	}

	public Option<Address> findAddressById(Integer addressId)
	{
		List<Address> res = getHibernateTemplate().find("from Address where id = ?", addressId);
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Option<Address> findAddressByCategoryAndLastNameOrCompany(final String category, String lastNameOrCompany)
	{
		String search = lastNameOrCompany.toLowerCase();
		List<Address> res = getHibernateTemplate()
						.find(
										"from Address where category = ? AND"
														+ " (internalLastname like ? OR (internalLastname = '' AND internalCompany like ?))"
														+ " AND id != 0 AND id != 1" + " ORDER BY internalLastname ASC",
										new Object[]
										{
														category, search, search
										});
		return res.size() > 0 ? new Full(res.get(0)) : new Empty<Address>();
	}

	public List<Address> findAddressesByCategoryAndNameOrCompanyStartsWith(String category, String lastNameOrCompany)
	{
		String search = lastNameOrCompany.toLowerCase() + "%";
		return getHibernateTemplate()
						.find(
										"from Address where category = ? AND"
														+ " (internalLastname like ? OR (internalLastname = '' AND internalCompany like ?))"
														+ " AND id != 0 AND id != 1" + " ORDER BY internalLastname ASC",
										new Object[]
										{
														category, search, search
										});
	}

	@Transactional(readOnly = false)
	public void deleteAddressesByCategory(final String category)
	{
		getHibernateTemplate()
						.bulkUpdate(
										"delete PhoneNumber p where ( select a.category from Address a where p.addressId = a.id and a.partyId is null ) = ?",
										category);
		getHibernateTemplate().bulkUpdate("delete Address a where a.partyId is null AND a.category = ?", category);
	}

	public List<Address> findAddressesOfOwnerByCategoryAndLastNameOrCompanyStartsWith(Integer ownerId, String category,
					String name)
	{
		String search = name.toLowerCase() + "%";
		return getHibernateTemplate()
						.find(
										"from Address where category = ? and"
														+ " ownerId = ? and"
														+ " (internalLastname like ? or (internalLastname = '' or internalCompany like ?))"
														+ " and id != 0 and id != 1" + " order by internalLastname asc",
										new Object[]
										{
														category, ownerId, search, search
										});
	}

	public Option<Address> findAddressByCategoryAndPhoneNumber(final String category, final String number)
	{
		List<Address> res = getHibernateTemplate().findByNamedQuery(
						"de.iritgo.aktera.address.FindAddressByCategoryAndPhoneNumber", category, number);
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Option<Address> findAddressOfOwnerByCategoryAndPhoneNumber(final Integer ownerId, final String category,
					final PhoneNumber phoneNumber)
	{
		List<Address> res = getHibernateTemplate().find("from Address where id = ? and category = ? and ownerId = ?",
						new Object[]
						{
										phoneNumber.getAddress().getId(), category, ownerId
						});
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Option<Address> findAddressOfOwnerByCategoryAndPhoneNumber(final Integer ownerId, final String category,
					String number)
	{
		List<PhoneNumber> phoneNumbers = findPhoneNumbers (number);
		for (PhoneNumber phoneNumber : phoneNumbers)
		{
			Option<Address> address = findAddressByCategoryAndPhoneNumber(category, phoneNumber);

			if (address.full() && (ownerId == null || address.get().getOwnerId().equals(ownerId)))
			{
				return address;
			}
		}
		return new Empty();
	}

	public Option<Address> findAddressByCategoryAndPhoneNumber(final String category, final PhoneNumber phoneNumber)
	{
		List<Address> res = getHibernateTemplate().find("from Address where id = ? and category = ?", new Object[]
		{
						phoneNumber.getAddressId(), category
		});
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	private List<PhoneNumber> findPhoneNumbers(String number)
	{
		List<PhoneNumber> phoneNumbers = getHibernateTemplate().find("from PhoneNumber where internalNumber like ?",
				number);
		return phoneNumbers;
	}

	public List<PhoneNumber> findPhoneNumbersEndingWith(final String number)
	{
		List<PhoneNumber> phoneNumbers = getHibernateTemplate().find("from PhoneNumber where internalNumber like ?",
						"%" + number);
		return phoneNumbers;
	}

	public List<PhoneNumber> findPhoneNumbersOfOwnerEndingWith(Integer ownerId, String number)
	{
		return getHibernateTemplate()
						.find(
										"select pn from PhoneNumber pn left join pn.address where pn.internalNumber like ? and pn.address.ownerId = ?",
										new Object[]
										{
														"%" + number, ownerId
										});
	}

	public Option<Address> findAddressByUserId(final Integer userId)
	{
		Party party = getPartyByUserId(userId);
		List<Address> res = getHibernateTemplate().find("from Address where partyId = ?", party.getId());
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Address getAddressById(final Integer addressId)
	{
		Address address = getHibernateTemplate().get(Address.class, addressId);
		if (address == null)
		{
			throw new DataRetrievalFailureException("Address with id " + addressId + " not found");
		}
		return address;
	}

	public Option<Address> findAddressByPartyId(final int partyId)
	{
		List<Address> res = getHibernateTemplate().find("from Address where partyId = ?", partyId);
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Option<Address> findAddressByDn(final Object addressDn)
	{
		Address address = getHibernateTemplate().get(Address.class, NumberTools.toInt(addressDn, - 1));
		if (address != null)
		{
			return new Full(address);
		}
		else
		{
			return new Empty();
		}
	}

	public List<PhoneNumber> findAllPhoneNumbersByAddressIdSortedByCategories(Integer addressId, String[] categories)
	{
		List<PhoneNumber> phoneNumbers = getHibernateTemplate().find("from PhoneNumber where addressId = ?", addressId);
		List<PhoneNumber> result = new LinkedList<PhoneNumber>();
		List<PhoneNumber> tmp = new LinkedList<PhoneNumber>(phoneNumbers);
		for (String category : categories)
		{
			for (PhoneNumber phoneNumber : phoneNumbers)
			{
				if (phoneNumber.getCategory().equals(category))
				{
					tmp.remove(phoneNumber);
					result.add(phoneNumber);
				}
			}
		}
		result.addAll(tmp);
		return result;
	}

	public Option<Address> findAddressByOwnerAndCategoryAndContactNumber(final Integer ownerId, final String category,
					final String contactNumber)
	{
		if (StringTools.isTrimEmpty(contactNumber))
		{
			return new Empty();
		}

		List<Address> res = (List<Address>) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session
								.createQuery("from Address a where a.category = :category and a.contactNumber = :contactNumber"
												+ (ownerId != null ? " and a.ownerId = :ownerId" : ""));

				query.setParameter("category", category);
				query.setParameter("contactNumber", contactNumber);

				if (ownerId != null)
				{
					query.setParameter("ownerId", ownerId);
				}

				return query.list();
			}
		});

		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Option<Address> findAddressByOwnerAndCategoryAndFirstNameOrLastNameOrCompany(final Integer ownerId,
					final String category, final String firstName, final String lastName, final String company)
	{
		List<Address> res = (List<Address>) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery("from Address a where a.category = :category"
								+ (ownerId != null ? " and a.ownerId = :ownerId" : "")
								+ (StringTools.isNotTrimEmpty(firstName) ? " and a.firstName = :firstName" : "")
								+ (StringTools.isNotTrimEmpty(lastName) ? " and a.lastName = :lastName" : "")
								+ (StringTools.isNotTrimEmpty(company) ? " and a.company = :company" : ""));
				query.setParameter("category", category);
				if (ownerId != null)
				{
					query.setParameter("ownerId", ownerId);
				}
				if (StringTools.isNotTrimEmpty(firstName))
				{
					query.setParameter("firstName", firstName);
				}
				if (StringTools.isNotTrimEmpty(lastName))
				{
					query.setParameter("lastName", lastName);
				}
				if (StringTools.isNotTrimEmpty(company))
				{
					query.setParameter("company", company);
				}
				return query.list();
			}
		});
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public List<AddressStore> findAllAddressStores()
	{
		return getHibernateTemplate().find("from AddressStore");
	}

	public Option<AddressStore> findAddressStoreByName(String name)
	{
		List<AddressStore> res = getHibernateTemplate().find("from AddressStore s where s.name = ?", name);
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public Option<AddressStore> findAddressStoreById(Integer id)
	{
		List<AddressStore> res = getHibernateTemplate().find("from AddressStore s where s.id = ?", id);
		return res.size() > 0 ? new Full(res.get(0)) : new Empty();
	}

	public AddressStore getAddressStoreById(Integer id)
	{
		AddressStore store = getHibernateTemplate().get(AddressStore.class, id);
		if (store == null)
		{
			store = nullAddressStore;
		}
		return store;
	}

	public List<Address> createAddressListing(final Integer userId, final String category, final boolean onlyOwner,
					final String search, final String orderBy, final SortOrder orderDir, final int firstResult,
					final int maxResults)
	{
		return (List<Address>) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				String s = "%" + StringTools.trim(search).toLowerCase() + "%";
				Query query = session.createQuery("select distinct address"
								+ " from Address address where"
								+ " (address.partyId is null or address.partyId not in ("
								+ Party.ANONYMOUS_ID
								+ (! userId.equals(AkteraUser.ADMIN_ID) ? "," + Party.ADMIN_ID + "," + Party.MANAGER_ID
												: "") + "))" + " and address.category = :category"
								+ (onlyOwner ? " and address.ownerId = :userId" : "")
								+ " and (address.internalCompany like :s1 or address.internalLastname like :s2"
								+ " or address.street like :s3 or address.city like :s4)"
								+ (orderBy != null ? " order by " + orderBy + " " + orderDir.hql() : ""));

				query.setParameter("category", category);

				if (onlyOwner)
				{
					query.setParameter("userId", userId);
				}

				query.setParameter("s1", s);
				query.setParameter("s2", s);
				query.setParameter("s3", s);
				query.setParameter("s4", s);

				if (maxResults > 0)
				{
					query.setMaxResults(maxResults);
					query.setFirstResult(firstResult);
				}

				return query.list();
			}
		});
	}

	@Transactional(readOnly = false)
	public void updateAddress(Address address)
	{
		getHibernateTemplate().update(address);
	}

	@Transactional(readOnly = false)
	public void createAddress(Address address)
	{
		getHibernateTemplate().saveOrUpdate(address);
	}

	@Transactional(readOnly = false)
	public void deleteAddressWithDn(Object addressDn)
	{
		final Option<Address> address = findAddressByDn(addressDn);
		if (address.full())
		{
			getHibernateTemplate().delete(address.get());
		}
	}

	public long countAddressesByOwenrAndCategoryAndSearch(final Integer userId, final boolean onlyOwner,
					final String category, final String search)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				String s = "%" + StringTools.trim(search).toLowerCase() + "%";
				Query query = session.createQuery("select count (address)"
								+ " from Address address where"
								+ " (address.partyId is null or address.partyId not in ("
								+ Party.ANONYMOUS_ID
								+ (! userId.equals(AkteraUser.ADMIN_ID) ? "," + Party.ADMIN_ID + "," + Party.MANAGER_ID
												: "") + "))" + " and address.category = :category"
								+ (onlyOwner ? " and address.ownerId = :userId" : "")
								+ " and (address.internalCompany like :s1 or address.internalLastname like :s2"
								+ " or address.street like :s3 or address.city like :s4)");

				query.setParameter("category", category);

				if (onlyOwner)
				{
					query.setParameter("userId", userId);
				}

				query.setParameter("s1", s);
				query.setParameter("s2", s);
				query.setParameter("s3", s);
				query.setParameter("s4", s);

				return query.uniqueResult();
			}
		});
	}

	public long countAddressesByCategory(final String category)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery("select count (address)"
								+ " from Address address where address.category = :category");
				query.setParameter("category", category);
				return query.uniqueResult();
			}
		});
	}

	@Transactional(readOnly = false)
	public void moveAddressStoreOnePositionUp(AddressStore addressStore)
	{
		HibernateTemplate htl = getHibernateTemplate();

		if (addressStore.getPosition() > 1)
		{
			List<AddressStore> prev = htl.find("from AddressStore where position = ?", new Object[]
			{
				addressStore.getPosition() - 1
			});

			if (prev.size() > 0)
			{
				prev.get(0).setPosition(addressStore.getPosition());
				htl.update(prev.get(0));
			}

			addressStore.setPosition(addressStore.getPosition() - 1);
			htl.update(addressStore);
		}
	}

	@Transactional(readOnly = false)
	public void moveAddressStoreOnePositionDown(AddressStore addressStore)
	{
		HibernateTemplate htl = getHibernateTemplate();

		Integer maxPosition = (Integer) htl.find("select max(position) from AddressStore").get(0);

		if (addressStore.getPosition() < maxPosition)
		{
			List<AddressStore> next = htl.find("from AddressStore where position = ?", new Object[]
			{
				addressStore.getPosition() + 1
			});

			if (next.size() > 0)
			{
				next.get(0).setPosition(addressStore.getPosition());
				htl.update(next.get(0));
			}

			addressStore.setPosition(addressStore.getPosition() + 1);
			htl.update(addressStore);
		}
	}

	public int calculateMaxAddressStorePosition()
	{
		List res = getHibernateTemplate().find("select max(position) from AddressStore");
		return res.get(0) != null ? (Integer) res.get(0) : 0;
	}

	public void renumberAddressStorePositions(Integer firstPosition, Integer deltaPosition)
	{
		HibernateTemplate htl = getHibernateTemplate();
		htl.bulkUpdate("update AddressStore set position = position + ? where position >= ?", new Object[]
		{
						deltaPosition, firstPosition
		});
	}

	@Transactional(readOnly = false)
	public void createParty(Party party)
	{
		getHibernateTemplate().saveOrUpdate(party);
	}

	@Transactional(readOnly = false)
	public void deleteAllAddressesOfOnwerByCategory(String category, Integer userId)
	{
		getHibernateTemplate()
						.bulkUpdate(
										"delete PhoneNumber p where ( select a.category from Address a where p.addressId = a.id and a.partyId is null and a.ownerId = ? ) = ?",
										userId, category);

		getHibernateTemplate().bulkUpdate(
						"delete Address a where a.partyId is null AND a.ownerId = ? and a.category = ?", userId,
						category);
	}

	@Transactional(readOnly = false)
	public void deleteAddress(Address address)
	{
		getHibernateTemplate().delete(address);
	}

	@Transactional(readOnly = false)
	public void resetDefaultStoreFlagOnAllAddressStores()
	{
		getHibernateTemplate().bulkUpdate("update AddressStore set defaultStore = false");
	}
}
