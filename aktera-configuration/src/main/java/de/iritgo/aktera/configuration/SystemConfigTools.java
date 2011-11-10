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

package de.iritgo.aktera.configuration;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.simplelife.string.StringTools;


/**
 *
 */
public class SystemConfigTools
{
	/**
	 * Get the URL to the web application accessed by the given request.
	 *
	 * @param request A model request
	 * @return The web application URL
	 */
	public static String getWebAppUrl(ModelRequest request) throws ModelException
	{
		boolean localRequest = false;
		StringBuilder sb = new StringBuilder();

		try
		{
			if (request == null)
			{
				request = ModelTools.createModelRequest();
				localRequest = true;
			}

			SystemConfigManager systemConfigManager = (SystemConfigManager) request
							.getSpringBean(SystemConfigManager.ID);

			String url = systemConfigManager.getString("tb2", "webAppUrl");

			if (! StringTools.isTrimEmpty(url))
			{
				return url;
			}

			sb.append(request.getScheme());
			sb.append("://");
			sb.append(request.getServerName());

			if (request.getServerPort() != ("https".equals(request.getScheme()) ? 443 : 80))
			{
				sb.append(":");
				sb.append(request.getServerPort());
			}

			sb.append(request.getContextPath());
			sb.append("/");
		}
		catch (Exception x)
		{
			throw new ModelException(x);
		}
		finally
		{
			if (localRequest)
			{
				ModelTools.releaseModelRequest(request);
			}
		}

		return sb.toString();
	}

	/**
	 * Get the URL to the jnlp-service accessed by the given request.
	 *
	 * @param request A model request
	 * @return The web start URL
	 */
	public static String getWebStartUrl(ModelRequest request) throws ModelException
	{
		SystemConfigManager systemConfigManager = (SystemConfigManager) request.getSpringBean(SystemConfigManager.ID);

		String url = systemConfigManager.getString("tb2", "webStartUrl");

		if (! StringTools.isTrimEmpty(url))
		{
			return url;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());

		if (request.getServerPort() != ("https".equals(request.getScheme()) ? 443 : 80))
		{
			sb.append(":");
			sb.append(request.getServerPort());
		}

		sb.append(request.getContextPath());
		sb.append("/");

		return sb.toString();
	}
}
