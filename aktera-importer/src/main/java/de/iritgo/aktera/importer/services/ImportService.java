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

package de.iritgo.aktera.importer.services;


import de.iritgo.aktera.PublicAPI;
import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;


/**
 * Import service.
 */
@PublicAPI
public interface ImportService
{
	/** Service id */
	public static final String ID = "de.iritgo.aktera.importer.ImportService";

	/**
	 * Validate the given XML file.
	 *
	 * @param importFile
	 *            The XML import file to validate
	 * @return True if the import file is valid
	 */
	public boolean validateXmlFile (File importFile);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param reporter A report result writer
	 */
	public void importXmlFile (File importFile, PrintWriter reporter);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param reporter A report result writer
	 * @param properties Additional import properties
	 */
	public void importXmlFile (File importFile, PrintWriter reporter, Properties properties);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param importHandler The import handler to use (can be null)
	 * @param reporter A report result writer
	 */
	public void importXmlFile (File importFile, String importHandler, PrintWriter reporter);

	/**
	 * Perform an XML import.
	 *
	 * @param importFile The XML file to import
	 * @param importHandler The import handler to use (can be null)
	 * @param reporter A report result writer
	 * @param properties Additional import properties
	 */
	public void importXmlFile (File importFile, String importHandler, PrintWriter reporter, Properties properties);
}
