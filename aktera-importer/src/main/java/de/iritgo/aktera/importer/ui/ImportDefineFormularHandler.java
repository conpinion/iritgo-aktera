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


import de.iritgo.aktera.comm.BinaryWrapper;
import de.iritgo.aktera.importer.ImportManager;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.tools.FileTools;
import de.iritgo.aktera.ui.form.FormTools;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.ValidationResult;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.configuration.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;


/**
 *
 */
public class ImportDefineFormularHandler extends FormularHandler
{
	/** */
	private ImportManager importManager;

	/**
	 * @param importManager The new importManager.
	 */
	public void setImportManager(ImportManager importManager)
	{
		this.importManager = importManager;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#adjustFormular(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor)
	 */
	@Override
	public void adjustFormular(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		super.adjustFormular(request, formular, persistents);

		TreeMap<String, String> csvImports = new TreeMap<String, String>();

		persistents.putAttributeValidValues("xslt", csvImports);
		csvImports.put("", "$opt-");

		for (String id : importManager.getCsvImportHandlerIds())
		{
			csvImports.put(importManager.getCsvImportHandlerXsl(id), "$import" + id);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
		persistents.putAttribute("fileUpload1", request.getParameter("fileUpload1"));

		if (request.getParameter("fileUpload1") == null && request.getParameter("fileUpload2") == null)
		{
			FormTools.addError(response, result, "fileUpload1", "Aktera:noImportFileGiven");
		}

		if (request.getParameter("fileUpload2") != null && StringTools.isTrimEmpty(persistents.getAttribute("xslt")))
		{
			FormTools.addError(response, result, "xslt", "Aktera:noCsvTypeSpecified");
		}

		if (request.getParameter("fileUpload1") != null)
		{
			BinaryWrapper data = (BinaryWrapper) request.getParameter("fileUpload1");

			if (data != null)
			{
				File outDir = FileTools.newAkteraFile("/var/tmp/iritgo");

				outDir.mkdirs();

				File outFile = FileTools.newAkteraFile("/var/tmp/iritgo/import.data");

				try
				{
					outFile.delete();
					outFile.createNewFile();
					data.write(outFile);
				}
				catch (IOException x)
				{
					System.out.println("[ImportDefineFormularHandler] Unable to store import file: " + x);
				}
			}
		}
		else if (request.getParameter("fileUpload2") != null)
		{
			BinaryWrapper data = (BinaryWrapper) request.getParameter("fileUpload2");

			if (data != null)
			{
				File outDir = FileTools.newAkteraFile("/var/tmp/iritgo");

				outDir.mkdirs();

				File outFile = FileTools.newAkteraFile("/var/tmp/iritgo/import.data");

				try
				{
					outFile.delete();
					outFile.createNewFile();
					data.write(outFile);
				}
				catch (IOException x)
				{
					System.out.println("[ImportDefineFormularHandler] Unable to store import file: " + x);
				}
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public int createPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		request.setParameter("filename", FileTools.newAkteraFile("var/tmp/iritgo/import.data"));

		return - 1;
	}
}
