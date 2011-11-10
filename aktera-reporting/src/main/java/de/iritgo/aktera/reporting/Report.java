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

package de.iritgo.aktera.reporting;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import java.io.ByteArrayOutputStream;


/**
 * This model is used to redirect to the user's personal page.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.report.report"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.report.report" id="aktera.report.report" logger="aktera"
 * @model.parameter name="report" required="true"
 * @model.parameter name="type" required="false"
 * @model.parameter name="page" required="false"
 * @model.attribute name="forward" value="aktera.reporting.report-result"
 */
public class Report extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 * @throws ModelException
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		Output outReport = res.createOutput("report");

		res.add(outReport);

		String backModel = req.getParameterAsString("backModel");

		if (! StringTools.isEmpty(backModel))
		{
			Command cmd = res.createCommand(backModel);

			cmd.setName("back");
			cmd.setLabel("back");
			res.add(cmd);
		}

		try
		{
			JasperPrint reportPrint = null;

			if (req.getParameter("page") != null)
			{
				reportPrint = (JasperPrint) UserTools.getUserEnvObject(req, "currentReport");
			}

			String reportFormat = req.getParameterAsString("format");

			if (reportPrint == null)
			{
				String reportName = req.getParameterAsString("report");

				reportPrint = ReportTools.createReport("keel-dbpool", reportName, reportFormat, req, getClass());

				UserTools.setUserEnvObject(req, "currentReport", reportPrint);
			}

			int page = NumberTools.toInt(req.getParameter("page"), 1);

			if ("pdf".equals(reportFormat))
			{
				res.addOutput("contentType", "application/pdf");
				res.addOutput("fileName", "Report.pdf");

				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				JRExporter exporter = new JRPdfExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.exportReport();
				outReport.setContent(buf.toByteArray());
			}
			else if ("csv".equals(reportFormat))
			{
				res.addOutput("contentType", "text/plain");
				res.addOutput("fileName", "Report.csv");

				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				JRExporter exporter = new JRCsvExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.exportReport();
				outReport.setContent(buf.toByteArray());
			}
			else if ("csv".equals(reportFormat))
			{
				res.addOutput("contentType", "text/xml");
				res.addOutput("fileName", "Report.xml");

				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				JRExporter exporter = new JRXmlExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.exportReport();
				outReport.setContent(buf.toByteArray());
			}
			else if ("xls".equals(reportFormat))
			{
				res.addOutput("contentType", "application/xls");
				res.addOutput("fileName", "Report.xls");

				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				JRExporter exporter = new JRXlsExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.exportReport();
				outReport.setContent(buf.toByteArray());
			}
			else
			{
				StringBuffer buf = new StringBuffer();
				JRExporter exporter = new JRHtmlExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, buf);
				exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
				exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
				exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
				exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean(true));
				exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer(page - 1));
				exporter.exportReport();
				outReport.setContent(buf.toString());

				createPageNavigationControls(req, res, page, reportPrint.getPages().size(), backModel);
			}
		}
		catch (JRException x)
		{
			log.error("Unable to create report", x);
		}

		return res;
	}

	/**
	 * Create the navigaton controls (page back, page next, ...).
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param page The current page.
	 * @param numPages The total number of pages in the report.
	 * @param backModel The model to call when going back from the report.
	 * @throws ModelException
	 */
	protected void createPageNavigationControls(ModelRequest req, ModelResponse res, int page, int numPages,
					String backModel) throws ModelException
	{
		int numPrevPages = 4;
		int numNextPages = 4;

		Output outPage = res.createOutput("page");

		outPage.setContent(new Integer(page));
		res.add(outPage);

		if (page > 1)
		{
			Command cmdPageStart = createPageCommand(req, res, "cmdPageStart", backModel);

			cmdPageStart.setParameter("page", "1");
			cmdPageStart.setLabel("$start");
			res.add(cmdPageStart);

			Command cmdPageBack = createPageCommand(req, res, "cmdPageBack", backModel);

			cmdPageBack.setParameter("page", String.valueOf(page - 1));
			cmdPageBack.setLabel("$back");
			res.add(cmdPageBack);

			Output outPrevPages = res.createOutput("prevPages");

			res.add(outPrevPages);

			int firstPrevPage = Math.max(1, page - numPrevPages);

			for (int i = page - 1; i >= firstPrevPage; --i)
			{
				Command cmdPage = createPageCommand(req, res, "cmdPage", backModel);

				cmdPage.setParameter("page", String.valueOf(page - i - 1 + firstPrevPage));
				cmdPage.setLabel(String.valueOf(page - i - 1 + firstPrevPage));
				outPrevPages.add(cmdPage);
			}
		}

		if (page < numPages)
		{
			Command cmdPageEnd = createPageCommand(req, res, "cmdPageEnd", backModel);

			cmdPageEnd.setParameter("page", String.valueOf(numPages));
			cmdPageEnd.setLabel("$end");
			res.add(cmdPageEnd);

			Command cmdPageNext = createPageCommand(req, res, "cmdPageNext", backModel);

			cmdPageNext.setParameter("page", String.valueOf(page + 1));
			cmdPageNext.setLabel("$next");
			res.add(cmdPageNext);

			Output outNextPages = res.createOutput("nextPages");

			res.add(outNextPages);

			int lastNextPage = Math.min(numPages, page + numNextPages);

			for (int i = page + 1; i <= lastNextPage; ++i)
			{
				Command cmdPage = createPageCommand(req, res, "cmdPage", backModel);

				cmdPage.setParameter("page", String.valueOf(i));
				cmdPage.setLabel(String.valueOf(i));
				outNextPages.add(cmdPage);
			}
		}
	}

	/**
	 * Create a page navigation command.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param name The command name.
	 * @param backModel The model to call when going back from the report.
	 * @return The new command.
	 */
	private Command createPageCommand(ModelRequest req, ModelResponse res, String name, String backModel)
		throws ModelException
	{
		Command cmd = res.createCommand("aktera.report.report");

		cmd.setParameter("backModel", backModel);
		cmd.setName(name);

		return cmd;
	}
}
