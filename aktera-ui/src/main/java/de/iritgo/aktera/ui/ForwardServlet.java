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

package de.iritgo.aktera.ui;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


public class ForwardServlet extends HttpServlet
{
	/** */
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		String targetModel = getServletConfig().getInitParameter("targetModel");

		if (targetModel != null)
		{
			StringBuilder targetUri = new StringBuilder("/model.do?model=");

			targetUri.append(targetModel);

			for (Enumeration params = request.getParameterNames(); params.hasMoreElements();)
			{
				String paramName = (String) params.nextElement();
				String paramValue = request.getParameter(paramName);

				targetUri.append("&" + paramName + "=" + paramValue);
			}

			RequestDispatcher rd = request.getRequestDispatcher(targetUri.toString());

			rd.forward(request, response);

			return;
		}

		throw new ServletException("No target specified");
	}
}
