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

package de.iritgo.aktera.base.tools.jvmmemory;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;


/**
 * This controller displays the current memory consumption of the JVM.
 */
public class ShowJvmMemory extends AbstractUIController
{
	/**
	 * @see de.iritgo.aktera.ui.UIController#execute(de.iritgo.aktera.ui.UIRequest, de.iritgo.aktera.ui.UIResponse)
	 */
	public void execute (UIRequest request, UIResponse response) throws UIControllerException
	{
		if (request.getParameter ("gc") != null)
		{
			System.gc ();
		}

		Command cmd = response.addCommand ("cmd5Minutes", "de.iritgo.aktera.base.ShowJvmMemoryHistory");

		cmd.setParameter ("startTime", "5");

		cmd = response.addCommand ("cmd30Minutes", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("startTime", "30");

		cmd = response.addCommand ("cmd60Minutes", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("startTime", "60");

		cmd = response.addCommand ("cmd120Minutes", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("startTime", "120");

		cmd = response.addCommand ("cmdDay", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("startTime", "1440");

		cmd = response.addCommand ("cmdWeek", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("startTime", "10080");

		cmd = response.addCommand ("cmdMonth", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("startTime", "302400");

		cmd = response.addCommand ("cmdYear", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("cmdYear", "3628800");

		cmd = response.addCommand ("cmdGC", "de.iritgo.aktera.base.ShowJvmMemoryHistory");
		cmd.setParameter ("gc", "Y");
	}
}
