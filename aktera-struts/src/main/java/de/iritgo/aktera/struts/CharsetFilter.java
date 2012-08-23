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


import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;


public class CharsetFilter implements Filter
{
	private String encoding;

	@Override
	public void init(FilterConfig config) throws ServletException
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
		throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		String userAgent = req.getHeader("User-Agent");
		if (request.getCharacterEncoding() == null)
		{
			if (userAgent != null && userAgent.indexOf("Aastra") != -1)
			{
				request.setCharacterEncoding("ISO-8859-1");
			}
			else
			{
				request.setCharacterEncoding("UTF-8");
			}
		}

		next.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
	}
}
