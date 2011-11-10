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

package de.iritgo.aktera.base.tools;


import de.iritgo.aktera.license.LicenseInfo;
import de.iritgo.aktera.license.LicenseTools;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import java.util.Date;


/**
 * Return the current license. At the moment only the company name.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.get-license-company-name"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.get-license-company-name" id="aktera.tools.get-license-company-name" logger="aktera"
 */
public class GetLicenseCompanyName extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of a business failure.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		LicenseInfo license = LicenseTools.getLicenseInfo();

		Output licenseComplanyName = res.createOutput("licenseCompanyName");

		if (license != null)
		{
			licenseComplanyName.setContent(license.getCompany());
		}
		else
		{
			licenseComplanyName.setContent("");
		}

		res.add(licenseComplanyName);

		return res;
	}
}
