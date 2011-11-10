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


import de.iritgo.aktera.clients.AbstractClientConnector;
import de.iritgo.aktera.clients.ClientException;
import de.iritgo.aktera.clients.KeelClient;
import de.iritgo.aktera.comm.ModelRequestMessage;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.servers.KeelAbstractServer;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * This abstract class provides all the features needed to go between a webapp's
 * HttpServletRequest/Response (or similar) request/response pairs and Keel's
 * request/response pair.
 *
 * @version $Revision: 1.2 $ $Date: 2004/01/28 17:05:02 $
 * @author Michael Nash
 * @author Schatterjee Created on May 3, 2003
 */
public abstract class AbstractWebappClientConnector extends AbstractClientConnector implements WebappClientConnector
{
	protected static final String PARAMETER_PARAM = "PARAMS_";

	protected static final String COMMAND_PARAM = "COMMAND_";

	protected static final int COMMAND_PARAM_LEN = COMMAND_PARAM.length();

	protected static final String MODEL_PARAM = "model";

	protected static final String BEAN_PARAM = "bean";

	protected static final String KEEL_MODEL_PARAM = "orig-model";

	protected static boolean initParamsParsed = false;

	protected static final String MODEL_PARAMS_PARAM = "MODEL_PARAMS_PARAM";

	/**
	 * This method is called after the webapp-specific request/response pair has
	 * been wrapped into a WebappRequest and WebappResponse pair.
	 */
	protected KeelResponse execute(WebappRequest wreq, WebappResponse wres, String defaultModelName)
		throws ClientException, ModelException
	{
		if (! initParamsParsed)
		{
			setContext(parseInitParameters(wreq));
			initParamsParsed = true;
		}

		KeelRequest kreq = makeKeelRequest(wreq, defaultModelName);

		KeelResponse kres = super.execute(kreq);

		if (kres == null)
		{
			throw new ClientException("Model response was null");
		}

		String modelName = kreq.getModel();

		kres.setAttribute("model", modelName);

		makeWebappResponse(kreq, kres, wres, wreq, modelName);

		return kres;
	}

	/**
	 * Convert a webapp-request to a Keel-request
	 *
	 * @param wreq
	 *            The supplied webapp request
	 * @param defaultModelName
	 *            The model-name to use if none in params-list
	 * @return The Keel request
	 * @throws ClientException
	 */
	protected KeelRequest makeKeelRequest(WebappRequest wreq, String defaultModelName) throws ClientException
	{
		KeelRequest kreq = createRequest();

		setRequestParameters(wreq, kreq);
		setRequestModel(wreq, kreq, defaultModelName);
		setRequestAttributes(wreq, kreq);
		setRequestCookies(wreq, kreq);
		setRequestLocale(wreq, kreq);

		return kreq;
	}

	/**
	 * Convert a Keel response to a webapp response
	 *
	 * @param kres
	 *            The Keel response
	 * @return The webapp response
	 */
	protected WebappResponse makeWebappResponse(KeelRequest kreq, KeelResponse kres, WebappResponse wres,
					WebappRequest wreq, String modelName) throws ClientException
	{
		setResponseCookies(kres, wres);

		return wres;
	}

	protected void setRequestCookies(WebappRequest wreq, KeelRequest kreq) throws ClientException
	{
		Cookie[] cookies = wreq.getCookies();
		Map cmap = null;

		if (cookies != null)
		{
			if (cookies.length > 0)
			{
				cmap = new HashMap(cookies.length);

				for (int i = 0; i < cookies.length; i++)
				{
					Cookie c = cookies[i];

					try
					{
						String decoded = URLDecoder.decode(c.getValue(), "UTF-8");

						cmap.put(c.getName(), decoded);
						log.debug("Cookie '" + c.getName() + "', value '" + decoded + "'");
					}
					catch (UnsupportedEncodingException e)
					{
						throw new ClientException(e);
					}
				}

				kreq.setAttribute("cookies", cmap);
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("No cookies");
				}
			}
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("Null cookies");
			}
		}
	}

	protected void setRequestParameters(WebappRequest wreq, KeelRequest kreq) throws ClientException
	{
		String oneParamName = null;

		for (Enumeration e = wreq.getParameterNames(); e.hasMoreElements();)
		{
			oneParamName = (String) e.nextElement();

			/* If the URL has a trailing "&" on it, we'll get a blank param */
			/* name. Don't crash - just don't use that one */
			if (! oneParamName.equals(""))
			{
				oneParamName = preProcessParamName(oneParamName);

				if (oneParamName.startsWith(COMMAND_PARAM))
				{
					processCommandParam(wreq, kreq, oneParamName.substring(COMMAND_PARAM_LEN));
				} /* if "COMMAND_" */
				else if (oneParamName.equals(MODEL_PARAMS_PARAM))
				{
					processCommandParam(wreq, kreq, wreq.getParameter(MODEL_PARAMS_PARAM));
				}

				if ((! oneParamName.startsWith(COMMAND_PARAM)) && (! oneParamName.startsWith(PARAMETER_PARAM))
								&& (! oneParamName.equals(MODEL_PARAMS_PARAM)))
				{
					processNonCommandParam(wreq, kreq, oneParamName);
				}
			} /* if the parameter name is not blank */
		}
	}

	protected void setRequestAttributes(WebappRequest wreq, KeelRequest kreq)
	{
		/*
		 * A couple of "special-purpose" request attributes are used to send
		 * servlet-specific information back to the server
		 */
		kreq.setAttribute("sessionid", wreq.getSessionId());
		kreq.setAttribute("IPAddress", wreq.getRemoteAddr());
	}

	protected void setRequestModel(WebappRequest wreq, KeelRequest kreq, String defaultModelName)
		throws ClientException
	{
		String modelName = (String) kreq.getParameter(KEEL_MODEL_PARAM);

		if ((modelName == null) || (modelName.trim().equals("")))
		{
			modelName = defaultModelName;
		}

		if ((modelName != null) && (! modelName.trim().equals("")))
		{
			kreq.setModel(modelName);

			return;
		}

		String beanName = (String) kreq.getParameter(BEAN_PARAM);

		if ((beanName == null) || (beanName.trim().equals("")))
		{
			beanName = defaultModelName;
		}

		if ((beanName != null) && (! beanName.trim().equals("")))
		{
			kreq.setBean(beanName);

			return;
		}

		throw new ClientException("No model/bean specified in request: '" + wreq.getRequestURL() + "?"
						+ wreq.getQueryString() + "'");
	}

	protected void setResponseCookies(KeelResponse kres, WebappResponse wres) throws ClientException
	{
		/* Set any "outbound" cookies */
		Object setCookies = kres.getAttribute("cookies");

		if (setCookies == null)
		{
			log.debug("No cookies returned");
		}

		if (setCookies instanceof Map)
		{
			log.debug("Returned cookies");

			Map returnCookies = (Map) setCookies;
			String oneName = null;
			Object oneValue = null;
			String oneValueString = null;

			for (Iterator ic = returnCookies.keySet().iterator(); ic.hasNext();)
			{
				oneName = (String) ic.next();
				oneValue = returnCookies.get(oneName);

				if (oneValue != null)
				{
					try
					{
						oneValueString = URLEncoder.encode(oneValue.toString(), "UTF-8");
					}
					catch (UnsupportedEncodingException e)
					{
						throw new ClientException("Error setting cookies", e);
					}

					Cookie c = new Cookie(oneName, oneValueString);

					c.setMaxAge(2592000);
					c.setPath("/");
					wres.addCookie(c);
					log.debug("Stored cookie '" + oneName + "', value '" + oneValueString + "'");
				}
			}
		}
	}

	protected void processNonCommandParam(WebappRequest wreq, KeelRequest kreq, String oneParamName)
	{
		final String[] values = wreq.getParameterValues(oneParamName);

		if (values.length <= 1)
		{
			kreq.setParameter(oneParamName, wreq.getParameter(oneParamName));
		}
		else
		{
			kreq.setParameter(oneParamName, values);
		}

		if (log.isDebugEnabled())
		{
			log.debug("Regular form parameter '" + oneParamName + "', '" + wreq.getParameter(oneParamName) + "'");
		}
	}

	protected void processCommandParam(WebappRequest wreq, KeelRequest kreq, String commandName) throws ClientException
	{
		if (commandName == null || "".equals(commandName))
		{
			return;
		}

		if (log.isDebugEnabled())
		{
			log.debug("Command:'" + commandName + "'");
		}

		String associatedParams = wreq.getParameter(PARAMETER_PARAM + commandName);

		if (associatedParams == null)
		{
			throw new ClientException("Command '" + commandName + "' was found, but no parameters under name '"
							+ PARAMETER_PARAM + commandName + "' was found");
		}

		StringTokenizer stk1 = new StringTokenizer(associatedParams, "&");
		String onePair = null;
		String oneCommandParamName = null;
		String oneCommandParamValue = null;

		while (stk1.hasMoreTokens())
		{
			onePair = stk1.nextToken();

			StringTokenizer stk2 = new StringTokenizer(onePair, "=");

			oneCommandParamName = stk2.nextToken();

			if (! stk2.hasMoreTokens())
			{
				oneCommandParamValue = "";
			}
			else
			{
				oneCommandParamValue = stk2.nextToken();
			}

			if (oneCommandParamName.equals(MODEL_PARAM))
			{
				if (log.isDebugEnabled())
				{
					log.debug("Model from command:'" + oneCommandParamValue + "'");
				}

				kreq.setParameter(KEEL_MODEL_PARAM, oneCommandParamValue);
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("Parameter '" + oneCommandParamName + "', '" + oneCommandParamValue + "'");
				}

				kreq.setParameter(oneCommandParamName, oneCommandParamValue);
			} /* else */
		} /* while more parameters */
	}

	protected String preProcessParamName(String oneParamName)
	{
		return oneParamName;
	}

	protected KeelRequest createRequest()
	{
		KeelRequest kreq = new ModelRequestMessage();

		return kreq;
	}

	protected Map parseInitParameters(WebappRequest wreq) throws ClientException
	{
		assert wreq != null;

		HashMap clientContext = new HashMap();

		/* There were no available clients - initialize */

		/* If the keel.config.dir is set in web.xml, override the */
		/* existing system property by that name with the custom */
		/* value. Only used if we're using a KeelDirect client! */
		String configPath = wreq.getInitParameter(KeelClient.CONFIG_DIR);

		if (configPath != null)
		{
			if (! configPath.equals(""))
			{
				System.setProperty(KeelClient.CONFIG_DIR, getWebappPath(wreq, configPath));
			}
		}
		else
		{
			configPath = System.getProperty(KeelClient.CONFIG_DIR);
		}

		/* Determine from configuration what client to use */
		String clientClass = wreq.getInitParameter("jms-client");

		if (clientClass == null)
		{
			clientClass = "de.iritgo.aktera.comm.openjms.clients.KeelJmsClientOpenJMS";
			log.info("No jms-client parameter supplied - using default OpenJMS client");
		}

		String clientConfig = wreq.getInitParameter("jms-config");

		if (clientConfig == null)
		{
			/* Use default configuration */
			clientConfig = new String("rmi://localhost:1099/JndiServer");
		}

		clientContext.put(KeelClient.CONFIG_DIR, configPath);
		clientContext.put(KeelClient.CLIENT_CLASS, clientClass);
		clientContext.put(KeelClient.CLIENT_CONFIG, clientConfig);

		return clientContext;
	}

	protected String getWebappPath(WebappRequest request, String configPath) throws ClientException
	{
		String sep = System.getProperty("file.separator");
		String newPath = configPath;

		if (! configPath.startsWith("/") && ! configPath.startsWith(sep))
		{
			String contextPath = request.getRealPath(".");

			newPath = contextPath + sep + configPath;
		}

		File configDir = new File(newPath);

		try
		{
			return configDir.getCanonicalPath();
		}
		catch (IOException e)
		{
			throw new ClientException("Error getting configuration dir", e);
		}
	}

	public void setInitParamsParsed(boolean initParamsParsed)
	{
		AbstractWebappClientConnector.initParamsParsed = initParamsParsed;
	}

	public void removeSessionContext(String id)
	{
		KeelAbstractServer.removeContext(id);
	}

	/***
	 */
	protected void setRequestLocale(WebappRequest wreq, KeelRequest kreq)
	{
		kreq.setLocale(wreq.getLocale());
	}
}
