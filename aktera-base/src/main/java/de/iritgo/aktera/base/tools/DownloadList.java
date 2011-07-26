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


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.tools.FileTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.download-list"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.download-list" id="aktera.tools.download-list" logger="aktera"
 */
public class DownloadList extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param request The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest request) throws ModelException
	{
		ModelResponse response = request.createResponse ();

		String downloadDir = getConfiguration ().getChild ("directory").getValue (null);

		if (downloadDir == null)
		{
			log.error ("No download directory specified");
			throw new ModelException ("No download directory specified");
		}

		String urlBase = getConfiguration ().getChild ("url").getValue (null);

		if (urlBase == null)
		{
			log.error ("No URL base specified");
			throw new ModelException ("No URL base specified");
		}

		Output outList = response.createOutput ("list");

		response.add (outList);

		List<File> files = new LinkedList (FileUtils.listFiles (FileTools.newAkteraFile (downloadDir),
						TrueFileFilter.INSTANCE, null));

		Collections.sort (files, new Comparator<File> ()
		{
			public int compare (File o1, File o2)
			{
				return o1.getName ().compareToIgnoreCase (o2.getName ());
			}
		});

		for (File file : files)
		{
			Output outFile = response.createOutput ("file" + file.hashCode ());

			outFile.setContent (file.getName ());
			outFile.setAttribute ("extension", FilenameUtils.getExtension (file.getName ()));
			outFile.setAttribute ("url", urlBase + "/" + file.getName ());
			outList.add (outFile);
		}

		response.setAttribute ("forward", "aktera.tools.download-list");

		return response;
	}
}
