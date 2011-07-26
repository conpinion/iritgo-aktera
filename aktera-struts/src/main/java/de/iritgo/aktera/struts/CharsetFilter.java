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

package de.iritgo.aktera.struts;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class CharsetFilter implements Filter
{
	private String encoding;

	public void init (FilterConfig config) throws ServletException
	{
		encoding = config.getInitParameter ("requestEncoding");

		if (encoding == null)
		{
			encoding = "UTF-8";
		}
	}

	public void doFilter (ServletRequest request, ServletResponse response, FilterChain next)
		throws IOException, ServletException
	{
		// Respect the client-specified character encoding
		// (see HTTP specification section 3.4.1)
		if (null == request.getCharacterEncoding ())
		{
			request.setCharacterEncoding (encoding);
		}

		next.doFilter (request, response);
	}

	public void destroy ()
	{
	}
}
