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

package de.iritgo.aktera.webservices.it;


import static org.junit.Assert.assertEquals;
import org.junit.Test;
import de.iritgo.aktera.test.WebserviceRequest;
import de.iritgo.aktera.test.WebserviceTestCase;


public class EchoWebserviceTest extends WebserviceTestCase
{
	/**
	 * Try to access the echo webservice
	 */
	@Test
	public void callEchoWebservice() throws Exception
	{
		WebserviceRequest request = createWebserviceRequest("http://aktera.iritgo.de/webservices/webservices", "echo",
						"admin", "admin");
		Object result = request.withStringParam("name", "alice").withStringParam("message", "How art thou?").send();
		assertEquals("Hello alice! How art thou?", result.toString());

	}

}
