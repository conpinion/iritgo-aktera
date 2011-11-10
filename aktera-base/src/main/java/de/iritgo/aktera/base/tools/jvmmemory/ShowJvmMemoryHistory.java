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


import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import de.iritgo.simplelife.math.NumberTools;


/**
 * This controller displays the current memory consumption of the JVM.
 */
public class ShowJvmMemoryHistory extends AbstractUIController
{
	/** The JVM memory manager */
	private AkteraJvmMemoryManager jvmMemoryManager;

	/**
	 * Set the JVM memory manager.
	 *
	 * @param jvmMemoryManager The JVM memory manager
	 */
	public void setJvmMemoryManager(AkteraJvmMemoryManager jvmMemoryManager)
	{
		this.jvmMemoryManager = jvmMemoryManager;
	}

	/**
	 * @see de.iritgo.aktera.ui.UIController#execute(de.iritgo.aktera.ui.UIRequest, de.iritgo.aktera.ui.UIResponse)
	 */
	public void execute(UIRequest request, UIResponse response) throws UIControllerException
	{
		jvmMemoryManager.setStartTime(NumberTools.toInt(request.getParameter("startTime"), 10));
	}
}
