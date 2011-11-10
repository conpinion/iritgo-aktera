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

package de.iritgo.aktera.ui.ng.formular;


import de.iritgo.aktera.authorization.Security;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.struts.BeanRequest;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import java.util.Map;


public class Cancel extends AbstractUIController
{
	public Cancel()
	{
		security = Security.NONE;
	}

	public void execute(UIRequest request, UIResponse response) throws UIControllerException
	{
		UIController controller = (UIController) SpringTools.getBean(request.getParameterAsString("_cmodel"));
		BeanRequest newRequest = new BeanRequest();

		newRequest.setBean(request.getParameterAsString("_cmodel"));
		newRequest.setLocale(request.getLocale());
		newRequest.setUserEnvironment(request.getUserEnvironment());

		for (Object entry : request.getParameters().entrySet())
		{
			String key = (String) ((Map.Entry) entry).getKey();

			if (key.startsWith("_cp") && ! "_cmodel".equals(key))
			{
				newRequest.setParameter(key.substring(3), ((Map.Entry) entry).getValue());
			}
		}

		controller.execute(newRequest, response);
	}
}
