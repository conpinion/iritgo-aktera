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
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ExporterFilter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Untility methods for report generation.
 */
public class ReportTools
{
	/**
	 * Create a report.
	 *
	 * @param dataSourceName The name of the data source.
	 * @param reportName The name of the jasper report.
	 * @param format TODO
	 * @param req The model request.
	 * @param klass The class used to load the jasper report.
	 * @param parameters The report parameters.
	 */
	public static JasperPrint createReport (String dataSourceName, String reportName, String format, ModelRequest req,
					Class klass) throws ModelException
	{
		Map parameters = new HashMap ();

		for (Iterator i = req.getParameters ().keySet ().iterator (); i.hasNext ();)
		{
			String key = (String) i.next ();

			if (! "report".equals (key) && ! "format".equals (key) && ! "model".equals (key)
							&& ! "download".equals (key) && ! "backModel".equals (key) && ! "orig-model".equals (key))
			{
				parameters.put (key, req.getParameter (key));
			}
		}

		return createReport (dataSourceName, reportName, req, klass, parameters, format);
	}

	/**
	 * Create a report.
	 *
	 * @param dataSourceName The name of the data source.
	 * @param reportName The name of the jasper report.
	 * @param req The model request.
	 * @param klass The class used to load the jasper report.
	 * @param parameters The report parameters.
	 * @param format TODO
	 */
	public static JasperPrint createReport (String dataSourceName, String reportName, ModelRequest req, Class klass,
					Map parameters, String format) throws ModelException
	{
		try
		{
			DataSourceComponent dataSource = (DataSourceComponent) req.getService (DataSourceComponent.ROLE,
							dataSourceName);
			Connection connection = dataSource.getConnection ();

			InputStream reportStream = klass.getResourceAsStream (reportName);

			if (reportStream == null)
			{
				throw new ModelException ("Unable to find report file " + reportName + "!");
			}

			if ("csv".equals (format) || "xls".equals (format))
			{
				parameters.put (JRParameter.IS_IGNORE_PAGINATION, true);
			}

			return JasperFillManager.fillReport (reportStream, parameters, connection);
		}
		catch (SQLException x)
		{
			throw new ModelException ("Unable to create report", x);
		}
		catch (JRException x)
		{
			throw new ModelException ("Unable to create report", x);
		}
	}

	/**
	 * Create a report.
	 *
	 * @param dataSourceName The name of the data source.
	 * @param reportName The name of the jasper report.
	 * @param req The model request.
	 * @param klass The class used to load the jasper report.
	 * @param parameters The report parameters.
	 * @param backModel The model to call when the 'back'-button was clicked.
	 */
	public static void createReport (ModelRequest req, ModelResponse res, Class klass, String reportName,
					Map parameters, String format, String dataSourceName, String backModel) throws ModelException
	{
		createReport (req, res, klass, reportName, parameters, format, dataSourceName, "aktera.report.report",
						backModel);
	}

	/**
	 * Create a report.
	 *
	 * @param dataSourceName The name of the data source.
	 * @param reportName The name of the jasper report.
	 * @param req The model request.
	 * @param klass The class used to load the jasper report.
	 * @param parameters The report parameters.
	 * @param backModel The model to call when the 'back'-button was clicked.
	 */
	public static void createReport (ModelRequest req, ModelResponse res, Class klass, String reportName,
					Map parameters, String format, String dataSourceName, String reportModel, String backModel)
		throws ModelException
	{
		Output outReport = res.createOutput ("report");

		res.add (outReport);

		if (! StringTools.isEmpty (backModel))
		{
			Command cmd = res.createCommand (backModel);

			cmd.setName ("back");
			cmd.setLabel ("back");
			res.add (cmd);
		}

		try
		{
			JasperPrint reportPrint = null;

			if (req.getParameter ("page") != null)
			{
				reportPrint = (JasperPrint) UserTools.getUserEnvObject (req, "currentReport");
			}

			if (reportPrint == null)
			{
				reportPrint = ReportTools.createReport (dataSourceName, reportName, req, klass, parameters, format);

				UserTools.setUserEnvObject (req, "currentReport", reportPrint);
			}

			int page = NumberTools.toInt (req.getParameter ("page"), 1);

			ExporterFilter noLayoutFilter = new ExporterFilter ()
			{
				public boolean isToExport (JRPrintElement element)
				{
					return true;
				}
			};

			if ("pdf".equals (format))
			{
				res.addOutput ("contentType", "application/pdf");
				res.addOutput ("fileName", "Report.pdf");

				ByteArrayOutputStream buf = new ByteArrayOutputStream ();
				JRExporter exporter = new JRPdfExporter ();

				exporter.setParameter (JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter (JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.exportReport ();
				outReport.setContent (buf.toByteArray ());
			}
			else if ("csv".equals (format))
			{
				res.addOutput ("contentType", "text/plain");
				res.addOutput ("fileName", "Report.csv");

				ByteArrayOutputStream buf = new ByteArrayOutputStream ();
				JRExporter exporter = new JasperCSVExporter ();

				exporter.setParameter (JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter (JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.setParameter (JRExporterParameter.IGNORE_PAGE_MARGINS, true);
				exporter.setParameter (JRExporterParameter.FILTER, noLayoutFilter);
				exporter.exportReport ();
				outReport.setContent (buf.toByteArray ());
			}
			else if ("xml".equals (format))
			{
				res.addOutput ("contentType", "text/xml");
				res.addOutput ("fileName", "Report.xml");

				ByteArrayOutputStream buf = new ByteArrayOutputStream ();
				JRExporter exporter = new JRXmlExporter ();

				exporter.setParameter (JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter (JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.setParameter (JRExporterParameter.IGNORE_PAGE_MARGINS, true);
				exporter.setParameter (JRExporterParameter.FILTER, noLayoutFilter);
				exporter.exportReport ();
				outReport.setContent (buf.toByteArray ());
			}
			else if ("xls".equals (format))
			{
				res.addOutput ("contentType", "application/xls");
				res.addOutput ("fileName", "Report.xls");

				ByteArrayOutputStream buf = new ByteArrayOutputStream ();
				JRExporter exporter = new JRXlsExporter ();

				exporter.setParameter (JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter (JRExporterParameter.OUTPUT_STREAM, buf);
				exporter.setParameter (JRExporterParameter.IGNORE_PAGE_MARGINS, true);
				exporter.setParameter (JRExporterParameter.FILTER, noLayoutFilter);
				exporter.exportReport ();
				outReport.setContent (buf.toByteArray ());
			}
			else
			{
				StringBuffer buf = new StringBuffer ();
				JRExporter exporter = new JRHtmlExporter ();

				exporter.setParameter (JRExporterParameter.JASPER_PRINT, reportPrint);
				exporter.setParameter (JRExporterParameter.OUTPUT_STRING_BUFFER, buf);
				exporter.setParameter (JRHtmlExporterParameter.HTML_HEADER, "");
				exporter.setParameter (JRHtmlExporterParameter.HTML_FOOTER, "");
				exporter.setParameter (JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
				exporter.setParameter (JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean (true));
				exporter.setParameter (JRExporterParameter.PAGE_INDEX, new Integer (page - 1));
				exporter.exportReport ();
				outReport.setContent (buf.toString ());

				createPageNavigationControls (req, res, page, reportPrint.getPages ().size (), reportModel, backModel);
			}
		}
		catch (JRException x)
		{
			outReport.setAttribute ("empty", "true");
		}
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
	private static void createPageNavigationControls (ModelRequest req, ModelResponse res, int page, int numPages,
					String reportModel, String backModel) throws ModelException
	{
		int numPrevPages = 4;
		int numNextPages = 4;

		Output outPage = res.createOutput ("page");

		outPage.setContent (new Integer (page));
		res.add (outPage);

		if (page > 1)
		{
			Command cmdPageStart = createPageCommand (req, res, "cmdPageStart", reportModel, backModel);

			cmdPageStart.setParameter ("page", "1");
			res.add (cmdPageStart);

			Command cmdPageBack = createPageCommand (req, res, "cmdPageBack", reportModel, backModel);

			cmdPageBack.setParameter ("page", String.valueOf (page - 1));
			res.add (cmdPageBack);

			Output outPrevPages = res.createOutput ("prevPages");

			res.add (outPrevPages);

			int firstPrevPage = Math.max (1, page - numPrevPages);

			for (int i = page - 1; i >= firstPrevPage; --i)
			{
				Command cmdPage = createPageCommand (req, res, "cmdPage", reportModel, backModel);

				cmdPage.setParameter ("page", String.valueOf (page - i - 1 + firstPrevPage));
				cmdPage.setLabel (String.valueOf (page - i - 1 + firstPrevPage));
				outPrevPages.add (cmdPage);
			}
		}

		if (page < numPages)
		{
			Command cmdPageEnd = createPageCommand (req, res, "cmdPageEnd", reportModel, backModel);

			cmdPageEnd.setParameter ("page", String.valueOf (numPages));
			res.add (cmdPageEnd);

			Command cmdPageNext = createPageCommand (req, res, "cmdPageNext", reportModel, backModel);

			cmdPageNext.setParameter ("page", String.valueOf (page + 1));
			res.add (cmdPageNext);

			Output outNextPages = res.createOutput ("nextPages");

			res.add (outNextPages);

			int lastNextPage = Math.min (numPages, page + numNextPages);

			for (int i = page + 1; i <= lastNextPage; ++i)
			{
				Command cmdPage = createPageCommand (req, res, "cmdPage", reportModel, backModel);

				cmdPage.setParameter ("page", String.valueOf (i));
				cmdPage.setLabel (String.valueOf (i));
				outNextPages.add (cmdPage);
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
	private static Command createPageCommand (ModelRequest req, ModelResponse res, String name, String reportModel,
					String backModel) throws ModelException
	{
		Command cmd = res.createCommand (reportModel);

		cmd.setParameter ("backModel", backModel);
		cmd.setName (name);

		return cmd;
	}
}
