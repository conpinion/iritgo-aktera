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

package de.iritgo.aktera.clients.webapp;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
 * Wraps a HttpServletResponse in a WebappResponse
 *
 * @version        $Revision: 1.1 $        $Date: 2003/12/29 06:59:14 $
 * @author Schatterjee
 * Created on May 24, 2003
 */
public class DefaultWebappResponse implements WebappResponse
{
	private HttpServletResponse hres = null;

	private DefaultWebappResponse ()
	{
	}

	public DefaultWebappResponse (HttpServletResponse hres)
	{
		this.hres = hres;
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappResponse#addCookie(javax.servlet.http.Cookie)
	 */
	public void addCookie (Cookie c)
	{
		hres.addCookie (c);
	}
}
