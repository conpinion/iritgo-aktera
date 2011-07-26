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


import de.iritgo.aktera.configuration.SystemConfigTools;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.tools.AppInfo;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.string.StringTools;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.LinkedList;


/**
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name="aktera.aktario.start-jnlp-client"
 * @x-avalon.lifestyle type=singleton
 * @model.model name="aktera.aktario.start-jnlp-client" id="aktera.aktario.start-jnlp-client" logger="aktera"
 * @model.attribute name="forward" value="aktera.aktario.download-jnlp"
 */
public class StartJnlpClient extends StandardLogEnabledModel
{
	/**
	 * @see de.iritgo.aktera.model.Model#execute(de.iritgo.aktera.model.ModelRequest)
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		File iritgoDir = locateIritgoDir ();

		if (iritgoDir == null)
		{
			log.error ("Unable to find Iritgo system directory");

			return res;
		}

		String url = SystemConfigTools.getWebStartUrl (req);

		if (StringTools.isEmpty (url))
		{
			url = SystemConfigTools.getWebAppUrl (req);
		}

		res.addOutput ("codebase", url + "aktario/");
		res.addOutput ("href", url + "model.do?model=aktera.aktario.start-jnlp-client");

		AppInfo.Info appInfo = AppInfo.getAppInfo (AppInfo.SYSTEM);

		res.addOutput ("title", appInfo.getName () + " Client");
		res.addOutput ("version", appInfo.getVersion ());
		res.addOutput ("versionLong", appInfo.getVersionLong ());
		res.addOutput ("vendor", appInfo.getVendor ());
		res.addOutput ("copyright", appInfo.getCopyright ());
		res.addOutput ("description", appInfo.getName () + " Client");
		res.addOutput ("iconUrl", url + "aktera/images/std/app-icon-64.gif");
		res.addOutput ("fileName", appInfo.getFileName () + ".jnlp");

		res.addOutput ("userName", UserTools.getCurrentUserName (req));
		res.addOutput ("server", req.getServerName ());

		FilenameFilter iritgoJarFileFilter = new FilenameFilter ()
		{
			public boolean accept (File dir, String name)
			{
				return name.startsWith ("iritgo-aktario-framework") && name.endsWith (".jar");
			}
		};

		FilenameFilter jarFileFilter = new FilenameFilter ()
		{
			public boolean accept (File dir, String name)
			{
				return name.endsWith (".jar");
			}
		};

		Output libraries = res.createOutput ("libraries");

		res.add (libraries);

		Output librariesLinux = res.createOutput ("librariesLinux");

		res.add (librariesLinux);

		Output librariesWin32 = res.createOutput ("librariesWin32");

		res.add (librariesWin32);

		StringBuffer plugins = new StringBuffer ();

		int i = 0;

		for (String fileName : new File (iritgoDir, "lib").list (jarFileFilter))
		{
			if (fileName.contains ("-linux-"))
			{
				librariesLinux.add (res.createOutput ("" + ++i, "lib/" + fileName));
			}
			else if (fileName.contains ("-win32-"))
			{
				librariesWin32.add (res.createOutput ("" + ++i, "lib/" + fileName));
			}
			else if (fileName.startsWith ("iritgo-aktario-framework-"))
			{
				Output aktarioFrameworkJar = res.createOutput ("aktarioFramework");

				aktarioFrameworkJar.setContent ("lib/" + fileName);
				res.add (aktarioFrameworkJar);
			}
			else
			{
				libraries.add (res.createOutput ("" + ++i, "lib/" + fileName));
			}
		}

		for (String fileName : new File (iritgoDir, "plugins").list (jarFileFilter))
		{
			libraries.add (res.createOutput ("" + ++i, "plugins/" + fileName));

			String pluginName = fileName.substring (0, fileName.lastIndexOf ('-'));

			StringTools.appendWithDelimiter (plugins, pluginName, ",");
		}

		res.addOutput ("plugins", plugins.toString ());

		return res;
	}

	/**
	 * Find the Iritgo directory.
	 *
	 * @return The Iritgo directory or null if none was found
	 */
	public File locateIritgoDir ()
	{
		FileFilter dirFilter = new FileFilter ()
		{
			public boolean accept (File file)
			{
				return file.isDirectory ();
			}
		};

		LinkedList<File> searchQueue = new LinkedList<File> ();

		searchQueue.add (new File (System.getProperty ("keel.config.dir"), "../.."));

		while (searchQueue.size () > 0)
		{
			File parent = (File) searchQueue.getFirst ();

			searchQueue.removeFirst ();

			for (File dir : parent.listFiles (dirFilter))
			{
				if ("aktario".equals (dir.getName ()))
				{
					return dir;
				}

				searchQueue.add (dir);
			}
		}

		return null;
	}
}
