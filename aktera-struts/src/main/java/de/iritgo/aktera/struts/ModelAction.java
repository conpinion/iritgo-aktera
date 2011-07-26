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


import de.iritgo.aktera.clients.webapp.WebappClientConnector;
import de.iritgo.aktera.model.KeelResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * An Action that provides the glue between Struts and Keel.
 *
 * @author Michael Nash
 * @author Shash Chatterjee
 * @version $Revision$ $Date$
 */
public class ModelAction extends Action
{
	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getFactory ().getInstance ("de.iritgo.aktera.struts.ModelAction");

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it), with
	 * provision for handling exceptions thrown by the business logic.
	 *
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @param form
	 *            The optional ActionForm bean for this request (if any)
	 * @param request
	 *            The non-HTTP request we are processing
	 * @param response
	 *            The non-HTTP response we are creating
	 *
	 * @exception Exception
	 *                if the application business logic throws an exception
	 */
	@Override
	public ActionForward execute (ActionMapping mapping, ActionForm form, HttpServletRequest request,
					HttpServletResponse response) throws Exception
	{
		String model = request.getParameter ("model");

		if (model != null && model.startsWith ("_BEAN_."))
		{
			response.sendRedirect (request.getRequestURL ().toString ().replace ("model.do", "bean.do") + "?"
							+ request.getQueryString ().replace ("model=", "bean=").replace ("_BEAN_.", ""));

			return null;
		}

		try
		{
			WebappClientConnector connector = new StrutsClientConnector (request, response, form, getServlet ());

			connector.setLogger (log);

			KeelResponse kres = connector.execute ();

			/* Now determine what the appropriate ActionForward to return is */
			/*
			 * If we have an output called "forward", we use this name as the
			 * forward. If not, we use the default
			 */
			return mapping.findForward (connector.getForward (kres));
		}
		catch (Exception ee)
		{
			if (ee instanceof SecurityException || ee instanceof PermissionException)
			{
				throw ee;
			}

			log.error ("Exception during model execution", ee);
			ee.printStackTrace ();
			throw new ServletException ("Exception during model execution", ee);
		}
	}
}
