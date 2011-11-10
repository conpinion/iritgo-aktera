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

/*
 * Copyright (c) 2002, The Keel Group, Ltd. All rights reserved.
 *
 * This software is made available under the terms of the license found
 * in the LICENSE file, included with this source code. The license can
 * also be found at:
 * http://www.keelframework.net/LICENSE
 */

package de.iritgo.aktera.struts;


import de.iritgo.aktera.clients.KeelClient;
import de.iritgo.aktera.comm.ModelRequestMessage;
import de.iritgo.aktera.core.exception.NestedException;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KeelWebappSupport
{
	private static List myClients = new ArrayList();

	private static Logger log = Logger.getLogger(KeelWebappSupport.class.getName());

	//--- This is a Singleton so don't let anyone instantiate us.
	private KeelWebappSupport()
	{
	} // KeelWebappSupport

	/**
	 * This method examines the current list of available KeelJmsClient obejcts, and returns
	 * the first one for use. If there are no clients, it attempts to
	 * create one or more. If the "jms-config" initial property has been specified
	 * in the web.xml file, it is used to configure these clients. If no such property
	 * was specified, default configuration (JMS via RMI to localhost on port 1099) is used instead,
	 * and only one client is configured.
	 * <P>
	 * The format of the jms-config is config|config|config, where each
	 * "config" is of the form protocol://host:port/jndiname, e.g. like rmi://localhost:1099/JndiServer
	 * Each "config" indicates a seperate client, and they are used in the order specified. If a
	 * connection goes bad to one client, the next is used and so forth until there
	 * are no more, at which point the first one is tried again.
	 */
	public static KeelClient getClient(HttpSession session) throws NestedException, ModelException, IOException
	{
		if (myClients.size() == 0)
		{
			/* There were no available clients - initialize */

			/* If the keel.config.dir is set in web.xml, override the */
			/* existing system property by that name with the custom */
			/* value. Only used if we're using a KeelDirect client! */
			String configPath = session.getServletContext().getInitParameter("keel.config.dir");

			if (configPath != null)
			{
				if (! configPath.equals(""))
				{
					System.setProperty("keel.config.dir", getWebappPath(session, configPath));
				}
			}

			String configSet = System.getProperty("keel.config.dir");

			if (configSet == null)
			{
				throw new NestedException("System property keel.config.dir must be set - or specified in web.xml");
			}

			String totalConfig = session.getServletContext().getInitParameter("jms-config");

			if (totalConfig == null)
			{
				/* Use default configuration */
				totalConfig = new String("rmi://localhost:1099/JndiServer");
			}

			StringTokenizer stk = new StringTokenizer(totalConfig, "|");

			while (stk.hasMoreTokens())
			{
				stk.nextToken();

				int clientId = myClients.size() + 1;

				/* Determine from configuration what client to use */
				String clientClass = session.getServletContext().getInitParameter("jms-client");

				if (clientClass == null)
				{
					clientClass = "de.iritgo.aktera.comm.openjms.clients.KeelJmsClientOpenJMS";
					log.info("No jms-client parameter supplied - using default OpenJMS client");
				}

				KeelClient oneClient = null;

				try
				{
					oneClient = (KeelClient) Class.forName(clientClass).newInstance();
				}
				catch (Exception e)
				{
					throw new NestedException(e);
				}

				oneClient.setId(clientId);
				log.info("Configuring Client " + clientId);

				myClients.add(oneClient);
			}
		}

		if (myClients.size() == 0)
		{
			throw new NestedException("No clients configured");
		}

		/* Return the first available client */
		return (KeelClient) myClients.get(0);
	}

	private static String getWebappPath(HttpSession session, String configPath) throws IOException
	{
		String sep = System.getProperty("file.separator");
		String newPath = configPath;

		if (! configPath.startsWith("/") && ! configPath.startsWith(sep))
		{
			String contextPath = session.getServletContext().getRealPath(".");

			newPath = contextPath + sep + configPath;
		}

		File configDir = new File(newPath);

		return configDir.getCanonicalPath();
	}

	public static boolean allowed(HttpSession session, String resource, String operation)
	{
		KeelRequest keelRequest = new ModelRequestMessage();

		keelRequest.setModel("security.authorization");
		keelRequest.setAttribute("sessionid", session.getId());
		keelRequest.setParameter("component", resource);

		if (operation != null)
		{
			keelRequest.setParameter("operation", operation);
		}

		try
		{
			KeelResponse response = getClient(session).execute(keelRequest);

			return ((Boolean) response.getAttribute("allowed")).booleanValue();
		}
		catch (ModelException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		catch (NestedException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		throw new RuntimeException("Unable to execute AuthorizationModel");
	}
}
