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
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.video-list"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.video-list" id="aktera.tools.video-list" logger="aktera"
 */
public class VideoList extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param request The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest request) throws ModelException
	{
		ModelResponse response = request.createResponse();

		String downloadDir = getConfiguration().getChild("directory").getValue(null);
		String alternativeForward = getConfiguration().getChild("forward").getValue(null);
		final String filetype = getConfiguration().getChild("filetype").getValue(null) == null ? ".flv"
						: getConfiguration().getChild("filetype").getValue(null);

		if (downloadDir == null)
		{
			log.error("No download directory specified");
			throw new ModelException("No download directory specified");
		}

		String urlBase = getConfiguration().getChild("url").getValue(null);

		if (urlBase == null)
		{
			log.error("No URL base specified");
			throw new ModelException("No URL base specified");
		}

		Output player = response.createOutput("player");

		player.setAttribute("playerUrl", getConfiguration().getChild("playerUrl").getValue(null));
		player.setAttribute("urlBase", urlBase);
		player.setAttribute("server", request.getServerName());

		response.add(player);

		Output outList = response.createOutput("list");

		response.add(outList);

		List<File> files = new LinkedList(FileUtils.listFiles(FileTools.newAkteraFile(downloadDir), new IOFileFilter()
		{
			public boolean accept(File file)
			{
				return file.getName().endsWith(filetype);
			}

			public boolean accept(File arg0, String arg1)
			{
				return false;
			}
		}, null));

		Collections.sort(files, new Comparator<File>()
		{
			public int compare(File o1, File o2)
			{
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});

		for (File file : files)
		{
			Output outFile = response.createOutput("file" + file.hashCode());

			try
			{
				outFile.setContent(file.getName());
				outFile.setAttribute("extension", FilenameUtils.getExtension(file.getName()));
				outFile.setAttribute("url", urlBase + "/" + file.getName());
				outFile.setAttribute("filename", file.getName());

				String videoNameFile = FilenameUtils.removeExtension(file.getName()) + ".name";
				String descriptionFile = FilenameUtils.removeExtension(file.getName()) + ".description";

				outFile.setAttribute("videoName", FileUtils
								.readFileToString(new File(downloadDir + "/" + videoNameFile)));
				outFile.setAttribute("description", FileUtils.readFileToString(new File(downloadDir + "/"
								+ descriptionFile)));
			}
			catch (IOException x)
			{
			}

			outList.add(outFile);
		}

		if (alternativeForward == null)
		{
			response.setAttribute("forward", "aktera.tools.video-list");
		}
		else
		{
			response.setAttribute("forward", alternativeForward);
		}

		return response;
	}
}
