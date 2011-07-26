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

package de.iritgo.aktera.nexim;


import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import de.iritgo.nexim.IMServer;


public class NeximServerStarter implements StartupHandler
{
	/** */
	private IMServer imServer;

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup () throws StartupException
	{
		//		ApplicationContext appContext = new ClassPathXmlApplicationContext("nexim.spring.xml");
		imServer = (IMServer) SpringTools.getBean ("de.iritgo.nexim.IMServer");
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown () throws ShutdownException
	{
		imServer.shutdown ();
	}
}
