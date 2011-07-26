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

package de.iritgo.aktera.base.session;


import de.iritgo.aktera.comm.BinaryWrapper;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import java.io.File;
import java.io.PrintWriter;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.session.store-license"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.session.store-license" id="aktera.session.store-license" logger="aktera"
 * @model.attribute name="forward" value="aktera.session.store-license"
 */
public class StoreLicense extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		if (req.getParameter ("license") != null)
		{
			try
			{
				String license = req.getParameterAsString ("license").replaceAll ("\r", "");

				if (! license.endsWith ("\n"))
				{
					license += "\n";
				}

				File file = new File (System.getProperty ("iritgo.license.path"));
				PrintWriter fileOut = new PrintWriter (file);

				fileOut.print (license);
				fileOut.close ();
			}
			catch (Exception x)
			{
				System.out.println ("[StoreLicense] " + x);
			}
		}
		else if (req.getParameter ("fileUpload1") != null)
		{
			try
			{
				BinaryWrapper data = (BinaryWrapper) req.getParameter ("fileUpload1");

				if (data != null)
				{
					File file = new File (System.getProperty ("iritgo.license.path"));

					data.write (file);
				}
			}
			catch (Exception x)
			{
				System.out.println ("[StoreLicense] " + x);
			}
		}

		Command cmd = res.createCommand ("aktera.tools.goto-start-model");

		cmd.setName ("cmdLogin");
		res.add (cmd);

		return res;
	}
}
