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

package de.iritgo.aktera.aktario;


import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.logger.Log;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.framework.appcontext.ServerAppContext;
import de.iritgo.aktera.context.KeelContextualizable;
import de.iritgo.aktera.core.container.Container;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelRequest;
import org.apache.avalon.framework.context.DefaultContext;
import java.util.Iterator;
import java.util.Properties;


/**
 *
 */
public class AkteraAktarioServerManager extends BaseObject implements Manager
{
	/**
	 * Create a new client manager.
	 */
	public AkteraAktarioServerManager()
	{
		super("AkteraAktarioServerManager");
	}

	/**
	 * Initialize the client manager.
	 */
	public void init()
	{
	}

	/**
	 * Free all client manager resources.
	 */
	public void unload()
	{
	}

	/**
	 * Execute a model.
	 *
	 * @param props Model request properties.
	 */
	public void doModel(Properties props)
	{
		try
		{
			Container keelContainer = (Container) ServerAppContext.serverInstance().getObject("keel.container");

			Model model = (Model) keelContainer.getService(Model.ROLE, props.getProperty("model"));

			ModelRequest req = (ModelRequest) keelContainer.getService(ModelRequest.ROLE);
			DefaultContext context = new DefaultContext();

			((KeelContextualizable) req).setKeelContext(context);

			String attributeName = null;

			for (Iterator i = props.keySet().iterator(); i.hasNext();)
			{
				attributeName = (String) i.next();
				req.setParameter(attributeName, props.getProperty(attributeName));
			}

			model.execute(req);
		}
		catch (Exception x)
		{
			Log.logError("server", "AkteraAktarioServerManager.doModel", "Unable to execute model: " + x.toString());
			x.printStackTrace();
		}
	}
}
