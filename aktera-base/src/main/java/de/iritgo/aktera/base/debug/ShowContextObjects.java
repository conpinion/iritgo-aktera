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

package de.iritgo.aktera.base.debug;


import de.iritgo.aktera.authentication.DefaultUserEnvironment;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.servers.ComparableContext;
import de.iritgo.aktera.servers.KeelAbstractServer;
import de.iritgo.aktera.ui.AbstractUIController;
import de.iritgo.aktera.ui.UIControllerException;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.UIResponse;
import java.util.Map;


/**
 * This controller creates a list of all context objects.
 */
public class ShowContextObjects extends AbstractUIController
{
	/**
	 * @see de.iritgo.aktera.ui.UIController#execute(de.iritgo.aktera.ui.UIRequest, de.iritgo.aktera.ui.UIResponse)
	 */
	public void execute (UIRequest request, UIResponse response) throws UIControllerException
	{
		Output outContexts = response.createOutput ("contexts");

		response.add (outContexts);

		int contextNum = 0;

		for (Map.Entry<String, ComparableContext> contextEntry : KeelAbstractServer.getContexts ().entrySet ())
		{
			Output outContext = response.createOutput ("context-" + (contextNum++));

			outContexts.add (outContext);
			outContext.setContent (contextEntry.getKey ());

			ComparableContext context = contextEntry.getValue ();
			int entryNum = 0;

			while (context != null)
			{
				for (Map.Entry<Object, Object> dataEntry : context.getData ().entrySet ())
				{
					Output outEntry = response.createOutput ("entry-" + (entryNum++));

					outContext.add (outEntry);
					outEntry.setContent (dataEntry.getKey () + ":" + dataEntry.getValue ());

					if (dataEntry.getValue () instanceof DefaultUserEnvironment)
					{
						int attrNum = 0;

						for (Map.Entry<String, Object> attrEntry : ((DefaultUserEnvironment) dataEntry.getValue ())
										.getAttributes ().entrySet ())
						{
							Output outAttr = response.createOutput ("attr-" + (attrNum++));

							outEntry.add (outAttr);
							outAttr.setContent (attrEntry.getKey () + "=" + attrEntry.getValue ());
						}
					}
				}

				context = context.getParentContext ();
			}
		}
	}
}
