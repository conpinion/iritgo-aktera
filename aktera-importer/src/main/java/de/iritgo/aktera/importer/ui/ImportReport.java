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

package de.iritgo.aktera.importer.ui;


import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.aktera.tools.FileTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.import.report"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.import.report" id="aktera.import.report" logger="aktera"
 */
public class ImportReport extends SecurableStandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		I18N i18n = (I18N) req.getSpringBean(I18N.ID);

		ModelResponse res = req.createResponse();

		String backModel = req.getParameterAsString("backModel");

		res.setAttribute("forward", "aktera.import.import-report");

		Output report = res.createOutput("report");

		res.add(report);

		String lastLine = null;

		try
		{
			StringBuffer reportBuf = new StringBuffer();
			File reportFile = FileTools.newAkteraFile("/var/tmp/iritgo/import-report.txt");
			BufferedReader in = new BufferedReader(new FileReader(reportFile));
			String line = null;

			while ((line = in.readLine()) != null)
			{
				reportBuf.append(line + "\n");
				lastLine = line;
			}

			report.setContent(reportBuf.toString());
		}
		catch (IOException x)
		{
		}

		if (i18n.msg(req, "Aktera", "reportFileResult", "OK").equals(lastLine))
		{
			Command cmdBack = res.createCommand(backModel);

			cmdBack.setName("cmdBack");
			res.add(cmdBack);
		}
		else if (i18n.msg(req, "Aktera", "reportFileResult", "ERROR").equals(lastLine))
		{
			Command cmdBack = res.createCommand(backModel);

			cmdBack.setName("cmdBack");
			res.add(cmdBack);

			res.add(res.createOutput("error", "Y"));
		}
		else
		{
			Command cmdReport = res.createCommand("aktera.import.report");

			cmdReport.setName("cmdReport");
			cmdReport.setParameter("backModel", backModel);
			res.add(cmdReport);
		}

		return res;
	}
}
