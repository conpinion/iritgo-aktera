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


import de.iritgo.aktario.framework.IritgoEngine;
import de.iritgo.aktario.framework.IritgoServer;
import de.iritgo.aktario.framework.appcontext.ServerAppContext;
import de.iritgo.aktera.core.container.KeelContainer;
import de.iritgo.aktera.startup.AbstractStartupHandler;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.tools.AppInfo;
import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;


/**
 *
 */
public class StartIritgoServer extends AbstractStartupHandler
{
	/**
	 * @see de.iritgo.aktera.startup.AbstractStartupHandler#startup()
	 */
	@Override
	public void startup()
	{
		logger.info("Attempting to start the Iritgo server...");

		AppInfo.Info appInfo = AppInfo.getAppInfo(AppInfo.SYSTEM);

		ServerAppContext.serverInstance().put("keel.container", KeelContainer.defaultContainer());

		final File systemDir = locateAktarioDir();

		if (systemDir != null)
		{
			logger.debug("Using Aktario system dir " + systemDir.getAbsolutePath());

			System.setProperty("iritgo.system.dir", systemDir.getAbsolutePath());
			System.setProperty("iritgo.app.version.long", appInfo.getVersionLong());
			System.setProperty("iritgo.app.id", appInfo.getProductId());
			System.setProperty("iritgo.debug.level", "20");

			new Thread(new Runnable()
			{
				public void run()
				{
					IritgoServer.main(new String[]
					{});
				}
			}).start();
		}
		else
		{
			logger.error("Unable to find Aktario system dir");
		}

		while (! IritgoServer.isUpAndRunning())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}
		}

		logger.info("Successfully started the Iritgo server");
	}

	/**
	 * Find the aktario directory.
	 */
	public File locateAktarioDir()
	{
		FileFilter dirFilter = new FileFilter()
		{
			public boolean accept(File file)
			{
				return file.isDirectory();
			}
		};

		LinkedList searchQueue = new LinkedList();

		searchQueue.add(new File(System.getProperty("keel.config.dir"), "../../../.."));

		while (searchQueue.size() > 0)
		{
			File parent = (File) searchQueue.getFirst();

			searchQueue.removeFirst();

			File[] dirs = parent.listFiles(dirFilter);

			for (int i = 0; i < dirs.length; ++i)
			{
				if ("aktario".equals(dirs[i].getName()))
				{
					return dirs[i];
				}

				searchQueue.add(dirs[i]);
			}
		}

		return null;
	}

	/**
	 * @see de.iritgo.aktera.startup.AbstractStartupHandler#shutdown()
	 */
	@Override
	public void shutdown() throws ShutdownException
	{
		IritgoEngine.instance().shutdown();
	}
}
