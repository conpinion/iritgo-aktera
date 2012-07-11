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
import de.iritgo.aktera.importer.ImportManager;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import de.iritgo.aktera.tools.FileTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.tools.SystemTools;
import de.iritgo.aktera.ui.form.FormTools;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.simplelife.string.StringTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;


/**
 * This component imports cvs or xml import files.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.import"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.import" id="aktera.import" activation="startup"
 *              logger="aktera"
 * @model.parameter name="file" required="false"
 * @model.parameter name="mode" required="true"
 */
public class Import extends SecurableStandardLogEnabledModel
{
	/** Import paramters. */
	protected class ImportParams
	{
		public String fileName;

		public String mode;

		public String handlerId;

		public String backModel;

		public String xslt;

		public String destination;

		public boolean bulkImport;
	}

	/**
	 * Execute the model.
	 *
	 * @param req
	 *            The model request.
	 * @throws ModelException
	 *             In case of a business failure.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ImportParams params = getImportParams(req);
		Properties properties = new Properties();

		if (StringTools.isNotTrimEmpty(params.destination))
		{
			properties.setProperty("destination", params.destination);
		}

		ModelResponse res = req.createResponse();

		Command cmdBack = res.createCommand(params.backModel);

		cmdBack.setName("cmdBack");
		res.add(cmdBack);

		if ("analyze".equals(params.mode))
		{
			if (analyze(req, res, params.fileName, params.handlerId, params.bulkImport, params.xslt, properties))
			{
				Properties props = new Properties();

				props.put("file", params.fileName);

				if (StringTools.isNotTrimEmpty(params.destination))
				{
					props.put("destination", params.destination);
				}

				props.put("backModel", params.backModel);
				props.put("bulkImport", params.bulkImport);
				props.put("handler", params.handlerId);
				props.put("xslt", params.xslt);
				props.put("importModel", getConfiguration().getAttribute("id", "none"));

				return ModelTools.callModel(req, "aktera.import.analyse.report", props);
			}
			else
			{
				Command cmdForce = res.createCommand(getConfiguration().getAttribute("id", "none"));

				cmdForce.setName("cmdForce");
				cmdForce.setParameter("file", params.fileName);

				if (StringTools.isNotTrimEmpty(params.destination))
				{
					cmdForce.setParameter("destination", params.destination);
				}

				cmdForce.setParameter("mode", "analyze");
				cmdForce.setParameter("bulkImport", params.bulkImport);
				cmdForce.setParameter("force", "Y");
				cmdForce.setParameter("backModel", params.backModel);
				cmdForce.setParameter("handler", params.handlerId);
				cmdForce.setParameter("xslt", params.xslt);
				res.add(cmdForce);

				res.setAttribute("forward", "aktera.import.analyse-report");

				return res;
			}
		}
		else if ("import".equals(params.mode))
		{
			if (perform(req, res, params.fileName, params.handlerId, params.xslt, params.bulkImport, properties))
			{
				Properties props = new Properties();

				props.put("backModel", params.backModel);

				if (StringTools.isNotTrimEmpty(params.destination))
				{
					props.put("destination", params.destination);
				}
				props.put ("bulkImport", params.bulkImport);

				return ModelTools.callModel(req, "aktera.import.report", props);
			}
			else
			{
				res.setAttribute("forward", "aktera.import.report");

				return res;
			}
		}
		else
		{
			throw new ModelException("[Import] Unknown import mode '" + params.mode + "'");
		}
	}

	/**
	 * Retrieve the parameters of this import.
	 *
	 * @param req
	 *            A model request.
	 * @return The import parameters.
	 */
	private ImportParams getImportParams(ModelRequest req) throws ModelException
	{
		ImportParams params = new ImportParams();

		params.fileName = StringTools.trim(req.getParameter("file"));

		if (StringTools.isTrimEmpty(params.fileName))
		{
			params.fileName = "import.data";
		}

		String bulkImport = StringTools.trim(req.getParameter("bulkImport"));

		if (StringTools.isTrimEmpty(bulkImport) || "off".equals(bulkImport) || "false".equals(bulkImport))
		{
			params.bulkImport = false;
		}
		else
		{
			params.bulkImport = true;
		}

		params.mode = StringTools.trim(req.getParameter("mode"));

		if (StringTools.isTrimEmpty(params.mode))
		{
			params.mode = "analyze";
		}

		params.destination = (String) req.getParameter("destination");

		params.handlerId = StringTools.trim(req.getParameterAsString("handler"));

		params.backModel = StringTools.trim(req.getParameterAsString("backModel"));

		if (StringTools.isTrimEmpty(params.backModel))
		{
			params.backModel = "aktera.import.define.edit";
		}

		params.xslt = StringTools.trim(req.getParameterAsString("xslt"));

		if (StringTools.isTrimEmpty(params.xslt))
		{
			FormularDescriptor form = FormTools.getFormularFromContext(req, "aktera.import.define.edit", - 1);

			if (form != null)
			{
				params.xslt = (String) form.getPersistents().getAttribute("xslt");
			}
		}

		return params;
	}

	/**
	 * Retrieve the import node.
	 *
	 * @param doc
	 *            The xml document.
	 * @return The import node (null if none was found).
	 */
	protected Node getImportNode(Document doc)
	{
		try
		{
			XPath xPath = XPathFactory.newInstance().newXPath();

			Node node = (Node) xPath.evaluate("/import", doc, XPathConstants.NODE);

			if (node != null)
			{
				return node;
			}

			return (Node) xPath.evaluate("//import", doc, XPathConstants.NODE);
		}
		catch (Exception x)
		{
		}

		return null;
	}

	/**
	 * Ensure that the import file is in XML format. If not, we assume it to be
	 * a CSV file which is then converted to XML with the specified XSLT script.
	 *
	 * @param request
	 *            A model request
	 * @param fileName
	 *            Name of the import file
	 * @param xslt
	 *            XSLT script used to convert to xml
	 * @throws ModelException
	 */
	protected void convertToXml(ModelRequest request, String fileName, String xslt)
		throws IOException, javax.xml.transform.TransformerConfigurationException,
		javax.xml.transform.TransformerException, ModelException
	{
		ImportManager im = (ImportManager) request.getSpringBean(ImportManager.ID);

		if (im.validateXmlFile(new File(fileName)))
		{
			return;
		}

		if (StringTools.isTrimEmpty(xslt))
		{
			return;
		}

		File orgFile = new File(fileName);
		File tmpFile = new File(fileName + ".tmp");

		SystemTools.startAndWaitAkteraProcess("/usr/bin/dos2unix", orgFile.getCanonicalPath());

		SystemTools.startAndWaitAkteraProcess("/usr/bin/csv2xml", "-r import -d ; -i " + orgFile.getCanonicalPath()
						+ " -o " + tmpFile.getCanonicalPath());

		String xslFileName = System.getProperty("keel.config.dir") + "/../resources/csv2xml/" + xslt + ".xsl";

		SystemTools.startAndWaitAkteraProcess("/usr/bin/xsltproc", xslFileName +  
						" -o " + orgFile.getCanonicalPath() + " " + tmpFile.getCanonicalPath());
		
		tmpFile.delete();
	}

	/**
	 * Analyze a given XML import file. This method generates output elements
	 * describing the contents of the import file.
	 *
	 * @param req
	 *            The model request.
	 * @param res
	 *            The model response.
	 * @param fileName
	 *            The XML import file.
	 * @param handlerId
	 *            The id of the handler to execute (can be null to execute all
	 *            handlers).
	 * @param xslt
	 *            XSLT script used to convert to xml.
	 * @return False if the import wasn't started because of an existing lock
	 *         file.
	 */
	protected boolean analyze(final ModelRequest req, ModelResponse res, final String fileName, final String handlerId,
					final boolean bulkImport, String xslt, final Properties properties) throws ModelException
	{
		final I18N i18n = (I18N) req.getSpringBean(I18N.ID);
		final ImportManager im = (ImportManager) req.getSpringBean(ImportManager.ID);

		FileTools.newAkteraFile("/var/tmp/iritgo").mkdirs();

		final File lockFile = FileTools.newAkteraFile("/var/tmp/iritgo/import.lck");

		if ("Y".equals(req.getParameter("force")))
		{
			lockFile.delete();
		}

		if (lockFile.exists())
		{
			return false;
		}

		try
		{
			lockFile.createNewFile();

			File reportFile = FileTools.newAkteraFile("/var/tmp/iritgo/import-report.txt");

			reportFile.delete();
			reportFile.createNewFile();

			final PrintWriter reporter = new PrintWriter(new FileOutputStream(reportFile), true);

			convertToXml(req, fileName, xslt);

			if (im.validateXmlFile(new File(fileName)))
			{
				new Thread()
				{
					public void run()
					{
						boolean ok = true;

						try
						{
							File file = new File(fileName);
							Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
											"file://" + file.getAbsolutePath());

							if (doc.getChildNodes().getLength() > 0)
							{
								XPath xPath = XPathFactory.newInstance().newXPath();
								Node importElem = (Node) xPath.evaluate("import", doc, XPathConstants.NODE);

								if (importElem != null)
								{
									reporter.println(i18n.msg(req, "Aktera", "startingImportAnalysis"));
									ok = im.analyzeImport(req, doc, importElem, reporter, i18n, handlerId, bulkImport, properties);
								}
								else
								{
									reporter.println(i18n
													.msg(req, "AkteraImporter", "importErrorNoImportRootNodeFound"));
								}
							}
							else
							{
								reporter.println(i18n.msg(req, "AkteraImporter", "importErrorNoImportRootNodeFound"));
							}
						}
						catch (ParserConfigurationException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (SAXException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (IOException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (XPathExpressionException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (ModelException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}

						reporter.println(i18n.msg(req, "Aktera", "finishedImportAnalysis"));
						reporter.println();
						reporter.println(i18n.msg(req, "Aktera", "reportFileResult", (ok ? "OK" : "ERROR")));

						reporter.close();
						lockFile.delete();
					}
				}.start();
			}
			else
			{
				reporter.println(i18n.msg(req, "Aktera", "importFileDoesntContainImportElement"));
				reporter.println();
				reporter.println(i18n.msg(req, "Aktera", "reportFileResult", "ERROR"));
				reporter.close();
				lockFile.delete();
			}
		}
		catch (Exception x)
		{
			try
			{
				File reportFile = FileTools.newAkteraFile("/var/tmp/iritgo/import-report.txt");
				PrintWriter out = new PrintWriter(reportFile);

				out.println(i18n.msg(req, "Aktera", "importError", x.toString()));
				out.println();
				out.println(i18n.msg(req, "Aktera", "reportFileResult", "ERROR"));
				out.close();
				lockFile.delete();
			}
			catch (IOException xx)
			{
			}
		}

		return true;
	}

	/**
	 * Import a given XML import file. This method generates output elements
	 * describing the contents of the import file.
	 *
	 * @param req
	 *            The model request.
	 * @param res
	 *            The model response.
	 * @param fileName
	 *            The XML import file.
	 * @param handlerId
	 *            The id of the handler to execute (can be null to execute all
	 *            handlers).
	 * @param xslt
	 *            XSLT script used to convert to xml.
	 * @param destination
	 *            TODO
	 * @return False if the import wasn't started because of an existing lock
	 *         file.
	 */
	protected boolean perform(final ModelRequest req, ModelResponse res, final String fileName, final String handlerId,
					String xslt, final boolean bulkImport, final Properties properties) throws ModelException
	{
		final I18N i18n = (I18N) req.getSpringBean(I18N.ID);
		final ImportManager im = (ImportManager) req.getSpringBean(ImportManager.ID);

		FileTools.newAkteraFile("/var/tmp/iritgo").mkdirs();

		final File lockFile = FileTools.newAkteraFile("/var/tmp/iritgo/import.lck");

		if (lockFile.exists())
		{
			return true;
		}

		try
		{
			lockFile.createNewFile();

			File reportFile = FileTools.newAkteraFile("/var/tmp/iritgo/import-report.txt");

			reportFile.delete();
			reportFile.createNewFile();

			final PrintWriter reporter = new PrintWriter(new FileOutputStream(reportFile), true);

			convertToXml(req, fileName, xslt);

			if (im.validateXmlFile(new File(fileName)))
			{
				new Thread()
				{
					public void run()
					{
						boolean ok = true;
						File file = null;

						try
						{
							file = new File(fileName);

							Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
											"file://" + file.getAbsolutePath());

							XPath xPath = XPathFactory.newInstance().newXPath();

							Node importElem = (Node) xPath.evaluate("import", doc, XPathConstants.NODE);

							reporter.println(i18n.msg(req, "Aktera", "startingImport"));

							ok = im.performImport(req, doc, importElem, reporter, i18n, handlerId, bulkImport, properties);
						}
						catch (ParserConfigurationException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (SAXException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (IOException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (XPathExpressionException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						catch (ModelException x)
						{
							reporter.println(i18n.msg(req, "Aktera", "importError", x.toString()));
						}
						finally
						{
							lockFile.delete();
							file.delete();
						}

						reporter.println(i18n.msg(req, "Aktera", "finishedImport"));
						reporter.println();
						reporter.println(i18n.msg(req, "Aktera", "reportFileResult", (ok ? "OK" : "ERROR")));

						reporter.close();
					}
				}.start();
			}
			else
			{
				reporter.println(i18n.msg(req, "Aktera", "importFileDoesntContainImportElement"));
				reporter.println();
				reporter.println(i18n.msg(req, "Aktera", "reportFileResult", "ERROR"));
				reporter.close();
				lockFile.delete();
			}
		}
		catch (Exception x)
		{
			try
			{
				lockFile.delete();

				File reportFile = FileTools.newAkteraFile("/var/tmp/iritgo/import-report.txt");
				PrintWriter out = new PrintWriter(reportFile);

				out.println(i18n.msg(req, "Aktera", "importError", x.toString()));
				out.println();
				out.println(i18n.msg(req, "Aktera", "reportFileResult", "ERROR"));
				out.close();
			}
			catch (IOException xx)
			{
			}
		}

		return true;
	}
}
