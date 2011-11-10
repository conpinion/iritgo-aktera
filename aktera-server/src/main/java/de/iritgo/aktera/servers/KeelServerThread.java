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

package de.iritgo.aktera.servers;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;


/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class KeelServerThread extends Thread
{
	private ModelRequest myRequest = null;

	private ModelResponse myResponse = null;

	private ModelException myException = null;

	public void setRequest(ModelRequest req)
	{
		myRequest = req;
	}

	public void run()
	{
		try
		{
			myResponse = myRequest.execute();
		}
		catch (ModelException me)
		{
			myException = me;
		}
	}

	public ModelResponse getResponse() throws ModelException
	{
		if (myException != null)
		{
			throw myException;
		}

		return myResponse;
	}
}
