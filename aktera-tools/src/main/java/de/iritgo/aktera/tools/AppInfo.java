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

package de.iritgo.aktera.tools;


import de.iritgo.aktera.license.LicenseTools;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import org.apache.avalon.framework.configuration.Configuration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.app-info"
 * @x-avalon.lifestyle type="singleton"
 * @model.model
 *   name="aktera.app-info"
 *   id="aktera.app-info"
 *   logger="aktera"
 */
public class AppInfo extends StandardLogEnabledModel implements StartupHandler
{
	/** Application info */
	public static class Info
	{
		private String id;

		private String productId;

		private String name;

		private String nameLong;

		private String version;

		private String versionLong;

		private String description;

		private String copyright;

		private String vendor;

		private String fileName;

		public Info (String id, String productId, String name, String nameLong, String version, String versionLong,
						String description, String copyright, String vendor, String fileName)
		{
			this.id = id;
			this.productId = productId;
			this.name = name;
			this.nameLong = nameLong;
			this.version = version;
			this.versionLong = versionLong;
			this.description = description;
			this.copyright = copyright;
			this.vendor = vendor;
			this.fileName = fileName;
		}

		public String getId ()
		{
			return id;
		}

		public String getProductId ()
		{
			return productId;
		}

		public String getName ()
		{
			return name;
		}

		public String getNameLong ()
		{
			return nameLong;
		}

		public String getVersion ()
		{
			return version;
		}

		public String getVersionLong ()
		{
			return versionLong;
		}

		public String getDescription ()
		{
			return description;
		}

		public String getCopyright ()
		{
			return copyright;
		}

		public String getVendor ()
		{
			return vendor;
		}

		public String getFileName ()
		{
			return fileName;
		}
	}

	/** System app info id. */
	public static final String SYSTEM = "system";

	/** Applications by id. */
	protected static Map appById = new TreeMap ();

	/** Application configuration */
	private Configuration configuration;

	public void setConfiguration (Configuration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Get an application info by id.
	 *
	 * @param id The application info to retrieve.
	 * @return The application info.
	 */
	public static Info getAppInfo (String id)
	{
		Info info = (Info) appById.get (id);

		if (info == null)
		{
			return new Info ("unknown", "unknown", "unknown", "unknown", "0", "0", "unknown", "unknown", "unknown",
							"unknown");
		}

		return info;
	}

	public void startup () throws StartupException
	{
		Configuration[] configs = configuration.getChildren ("app");

		for (int i = 0; i < configs.length; ++i)
		{
			Configuration config = configs[i];

			try
			{
				appById.put (config.getAttribute ("id"), new Info (config.getAttribute ("id"), config.getChild (
								"productId").getValue (""), config.getChild ("name").getValue (), config.getChild (
								"nameLong").getValue (config.getChild ("name").getValue ()), config
								.getChild ("version").getValue (), config.getChild ("versionLong").getValue (""),
								config.getChild ("description").getValue (""), config.getChild ("copyright").getValue (
												""), config.getChild ("vendor").getValue (""), config.getChild (
												"fileName").getValue ("")));
			}
			catch (Exception x)
			{
				System.out.println ("[AppInfo] " + x);
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.model.Model#execute(de.iritgo.aktera.model.ModelRequest)
	 */
	public ModelResponse execute (ModelRequest request) throws ModelException
	{
		ModelResponse res = request.createResponse ();

		Output outAppList = res.createOutput ("apps");

		res.add (outAppList);

		for (Iterator<Info> i = appById.values ().iterator (); i.hasNext ();)
		{
			Info appInfo = i.next ();

			if (SYSTEM.equals (appInfo.getId ()))
			{
				continue;
			}

			if (LicenseTools.getLicenseInfo ().appAllowed (appInfo.getId ()))
			{
				Output outApp = res.createOutput ("app_" + appInfo.getId ());

				outAppList.add (outApp);
				outApp.setAttribute ("name", appInfo.getNameLong ());
				outApp.setAttribute ("version", appInfo.getVersion ());
				outApp.setAttribute ("description", appInfo.getDescription ());
				outApp.setAttribute ("copyright", appInfo.getCopyright ().replaceAll ("\\\\n", "<br />"));
			}
		}

		return res;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown () throws ShutdownException
	{
	}
}
