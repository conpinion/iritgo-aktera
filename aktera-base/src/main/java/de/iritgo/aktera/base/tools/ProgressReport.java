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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.aktera.tools.FileTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.beanutils.MethodUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


/**
 * This model is used to present a progress report to the user. It displays the
 * contents of a progress report file an repeatedly refreshes this display.
 *
 * Configuration:
 *
 * - fileName: Path name of the report file
 * - cmd-ok: If no error was shown in the report, a command button with this
 *   model will be presented to the user
 * - cmd-error: If an error was shown in the report, a command button with this
 *   model will be presented to the user
 * - cmd-report: The report is repeatedly refreshed. Specify the report model
 *   (the concrete instance of this abstract model) with this setting
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.progress-report"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.progress-report" id="aktera.tools.progress-report" logger="aktera"
 */
public class ProgressReport extends SecurableStandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		String lastLine = "";

		ModelResponse res = req.createResponse();

		try
		{
			Output report = res.createOutput("report");

			res.add(report);

			String reportFileName = getConfiguration().getChild("fileName").getValue(null);

			if (! StringTools.isTrimEmpty(reportFileName))
			{
				StringBuilder reportBuf = new StringBuilder();
				File reportFile = FileTools.newAkteraFile(reportFileName);
				BufferedReader in = new BufferedReader(new FileReader(reportFile));
				String line = null;

				while ((line = in.readLine()) != null)
				{
					reportBuf.append(line + "\n");
					lastLine = line;
				}

				report.setContent(reportBuf.toString());
			}
			else
			{
				String beanName = getConfiguration().getChild("bean").getValue(null);
				String methodName = getConfiguration().getChild("method").getValue(null);

				if (! StringTools.isTrimEmpty(beanName) && ! StringTools.isTrimEmpty(methodName))
				{
					Object bean = req.getSpringBean(beanName);

					lastLine = (String) MethodUtils.invokeExactMethod(bean, methodName, null);
					report.setContent(lastLine);
				}
			}

			if (lastLine != null && lastLine.indexOf("Result: OK") != - 1)
			{
				Command cmd = res.createCommand(getConfiguration().getChild("cmd-ok").getValue());

				cmd.setName("cmdOk");
				res.add(cmd);
			}
			else if (lastLine != null && lastLine.indexOf("Result: ERROR") != - 1)
			{
				Command cmd = res.createCommand(getConfiguration().getChild("cmd-error").getValue());

				cmd.setName("cmdError");
				res.add(cmd);

				res.add(res.createOutput("error", "Y"));
			}
			else
			{
				Command cmd = res.createCommand(getConfiguration().getChild("cmd-report").getValue());

				cmd.setName("cmdReport");
				res.add(cmd);
			}

			res
							.setAttribute("forward", getConfiguration().getChild("forward").getValue(
											"aktera.tools.progress-report"));
		}
		catch (IOException x)
		{
			throw new ModelException("[ProgressReport] " + x);
		}
		catch (ConfigurationException x)
		{
			throw new ModelException("[ProgressReport] " + x);
		}
		catch (NoSuchMethodException x)
		{
		}
		catch (IllegalAccessException x)
		{
		}
		catch (InvocationTargetException x)
		{
		}

		return res;
	}
}
