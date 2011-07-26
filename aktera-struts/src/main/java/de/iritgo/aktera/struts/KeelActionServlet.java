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


import de.iritgo.aktera.clients.KeelClient;
import de.iritgo.aktera.license.LicenseInfo;
import de.iritgo.aktera.license.LicenseTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.struts.action.ActionServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class KeelActionServlet extends ActionServlet
{
	/** */
	private static final long serialVersionUID = 1L;

	public LicenseInfo licenseInfo;

	@Override
	public void init (ServletConfig config) throws ServletException
	{
		super.init (config);

		licenseInfo = new LicenseInfo (log);

		logmsg ("Startup");

		//		setSystemPropertyFromConfig (config, "keel.processor.threads.min");
		//		setSystemPropertyFromConfig (config, "keel.processor.threads.max");
		//		setSystemPropertyFromConfig (config, "keel.processor.threads.init");
		//		setSystemPropertyFromConfig (config, "keel.processor.threads.alive");
		StrutsClientConnector connector = createClientConnector ();

		try
		{
			connector.startClient ();
		}
		catch (Exception x)
		{
			logmsg ("Error creating clients", x);
		}
	}

	@Override
	public void destroy ()
	{
		logmsg ("Shutdown");

		try
		{
			logmsg ("Terminating Iritgo Server");

			Object iritgoEngine = System.getProperties ().get ("iritgo.engine");

			if (iritgoEngine != null)
			{
				Method shutdown = iritgoEngine.getClass ().getMethod ("shutdown", new Class[]
				{});

				shutdown.invoke (iritgoEngine, new Object[]
				{});
			}
			else
			{
				logmsg ("Error while shutting down Iritgo Server: Unable to find IritgoEngine");
			}
		}
		catch (Exception x)
		{
			logmsg ("Error while shutting down Iritgo Server", x);
		}

		StrutsClientConnector connector = createClientConnector ();

		try
		{
			connector.stopClient ();
		}
		catch (Exception x)
		{
			logmsg ("Error stopping clients", x);
		}
	}

	@Override
	protected void process (HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{
		if ("aktera.session.invalid-license".equals (request.getParameter ("model"))
						|| "aktera.session.store-license".equals (request.getParameter ("model")))
		{
			super.process (request, response);

			return;
		}

		if (! licenseInfo.isValid ())
		{
			loadLicense ();
		}

		if (licenseInfo.isValid ())
		{
			super.process (request, response);

			return;
		}

		response.sendRedirect ("model.do?model=aktera.session.invalid-license");
	}

	protected void loadLicense ()
	{
		LicenseTools.clear ();

		List<String> licensePaths = new LinkedList<String> ();

		licensePaths.add (System.getProperty ("user.home") + File.separator + "Iritgo-License");

		String licensePathParam = getServletConfig ().getServletContext ().getInitParameter ("iritgo.license.path");

		if (licensePathParam != null)
		{
			for (String licensePath : licensePathParam.split (","))
			{
				licensePaths.add (getServletConfig ().getServletContext ().getRealPath ("/")
								+ StringTools.trim (licensePath));
			}
		}

		for (String licensePath : licensePaths)
		{
			licenseInfo = LicenseTools.getLicenseInfo (log, licensePath);

			if (licenseInfo != null)
			{
				logmsg ("License was found in " + licensePath);

				break;
			}
		}

		if (licenseInfo == null)
		{
			licenseInfo = new LicenseInfo (log);
		}
	}

	protected static void setSystemPropertyFromConfig (ServletConfig config, String key)
	{
		String prop = config.getServletContext ().getInitParameter (key);

		if (prop != null)
		{
			System.setProperty (key, prop);
		}
	}

	private StrutsClientConnector createClientConnector ()
	{
		ServletContext context = getServletContext ();

		String clientClass = context.getInitParameter ("jms-client");

		String clientConfig = context.getInitParameter ("jms-config");

		if (clientConfig == null)
		{
			clientConfig = "rmi://localhost:1099/JndiServer";
		}

		String configPath = context.getInitParameter ("keel.config.dir");

		if (configPath == null)
		{
			configPath = System.getProperty (KeelClient.CONFIG_DIR);
		}

		if (! configPath.startsWith ("/") && ! configPath.startsWith (File.separator))
		{
			configPath = context.getRealPath (".") + File.separator + configPath;
		}

		try
		{
			configPath = new File (configPath).getCanonicalPath ();
		}
		catch (IOException x)
		{
			logmsg ("Error computig config path", x);
		}

		System.setProperty (KeelClient.CONFIG_DIR, configPath);

		Map<String, String> clientContext = new HashMap<String, String> ();

		clientContext.put (KeelClient.CONFIG_DIR, configPath);
		clientContext.put (KeelClient.CLIENT_CLASS, clientClass);
		clientContext.put (KeelClient.CLIENT_CONFIG, clientConfig);

		StrutsClientConnector connector = new StrutsClientConnector ();

		connector.setContext (clientContext);
		connector.setLogger (new SimpleLog ("KeelStarter"));
		connector.setInitParamsParsed (true);

		return connector;
	}

	private void logmsg (String msg)
	{
		System.out.println ("[KeelActionServlet] " + msg);
	}

	private void logmsg (String msg, Throwable x)
	{
		System.out.println ("[KeelActionServlet] " + msg + ": " + x.toString ());
	}
}
