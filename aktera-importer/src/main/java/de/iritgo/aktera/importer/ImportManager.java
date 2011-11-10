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

package de.iritgo.aktera.importer;


import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


/**
 * Import manager.
 */
public interface ImportManager
{
	/**
	 * Information about an import handler.
	 */
	public static class ImportHandlerConfig
	{
		private String id;

		private String root;

		private ImportHandler handler;

		public ImportHandlerConfig(String id, String root, ImportHandler handler)
		{
			this.id = id;
			this.root = root;
			this.handler = handler;
		}

		public String getId()
		{
			return id;
		}

		public String getRoot()
		{
			return root;
		}

		public ImportHandler getHandler()
		{
			return handler;
		}

		public String toString()
		{
			return super.toString() + "[" + id + "," + handler.getClass().getName() + "]";
		}
	}

	/**
	 * Information about an csv import handler.
	 */
	public static class CsvImportHandlerConfig
	{
		private String id;

		private String xsl;

		public CsvImportHandlerConfig(String id, String xsl)
		{
			this.id = id;
			this.xsl = xsl;
		}

		public String getId()
		{
			return id;
		}

		public String getXsl()
		{
			return xsl;
		}

		public String toString()
		{
			return super.toString() + "[" + id + "," + xsl + "]";
		}
	}

	/** Service id */
	public static final String ID = "de.iritgo.aktera.importer.ImportManager";

	/**
	 * Retrieve a list of all import handler configurations.
	 *
	 * @return A list of ImportHandlerConfig objects
	 */
	public List<ImportHandlerConfig> getImportHandlerConfigs();

	/**
	 * Retrieve a collection of all CSV import handler ids.
	 *
	 * @return A list of all CSV import handler ids
	 */
	public Collection<String> getCsvImportHandlerIds();

	/**
	 * Get the XSL id of the specified CSV import handler.
	 *
	 * @param id The id of the CSV import handler
	 * @return The id of the XSL
	 */
	public String getCsvImportHandlerXsl(String id);

	/**
	 * Validate the given XML file.
	 *
	 * @param importFile The XML import file to validate
	 * @return True if the import file is valid
	 */
	public boolean validateXmlFile(File importFile);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param reporter A report result writer
	 */
	public void importXmlFile(File importFile, PrintWriter reporter);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param reporter A report result writer
	 * @param properties Additional import properties
	 */
	public void importXmlFile(File importFile, PrintWriter reporter, Properties properties);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param importHandler The import handler to use (can be null)
	 * @param reporter A report result writer
	 */
	public void importXmlFile(File importFile, String importHandler, PrintWriter reporter);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param importHandler The import handler to use (can be null)
	 * @param reporter A report result writer
	 * @param properties Additional import properties
	 */
	public void importXmlFile(File importFile, String importHandler, PrintWriter reporter, Properties properties);

	/**
	 * Analyse an import with all registered handlers.
	 */
	public boolean analyzeImport(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException;

	/**
	 * Analyse an import with a specified handler.
	 */
	public boolean analyzeImport(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					String handlerId, Properties properties) throws ModelException;

	/**
	 * Import an import with all registered handlers.
	 */
	public boolean performImport(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException;

	/**
	 * Import an import with a specified handler.
	 */
	public boolean performImport(ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					String handlerId, Properties properties) throws ModelException;
}
