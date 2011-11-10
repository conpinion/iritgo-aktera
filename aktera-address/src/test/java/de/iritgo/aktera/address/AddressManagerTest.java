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


import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.*;
import de.iritgo.aktera.address.entity.*;
import de.iritgo.simplelife.tools.*;


@Ignore
public class AddressManagerTest
{
	private AddressManager addressManager;

	private AddressStore addressStore1;

	private AddressStore addressStore2;

	@Before
	public void setUp() throws Exception
	{
		addressManager = new AddressManagerImpl();

		List<AddressStore> stores = new LinkedList<AddressStore>();

		addressStore1 = createMock(AddressStore.class);
		addressStore2 = createMock(AddressStore.class);
		stores.add(addressStore1);
		stores.add(addressStore2);
		((AddressManagerImpl) addressManager).setAddressStores(stores);
	}

	@Test
	public void findAddressByName()
	{
		Address expected = new Address("Alice");

		expect(addressStore1.findAddressByLastNameOrCompany("Alice")).andReturn(new Empty());
		expect(addressStore2.findAddressByLastNameOrCompany("Alice")).andReturn(new Full(expected));
		expect(addressStore2.getName()).andReturn("addressStore2");
		replay(addressStore1, addressStore2);
		assertEquals(expected, addressManager.findAddressByLastNameOrCompany("Alice"));
		verify(addressStore1, addressStore2);
	}
}
