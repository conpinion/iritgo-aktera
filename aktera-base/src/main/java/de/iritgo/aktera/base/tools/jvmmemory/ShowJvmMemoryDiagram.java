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

package de.iritgo.aktera.base.tools.jvmmemory;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.SecurableStandardLogEnabledModel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.show-jvm-diagram"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.show-jvm-diagram" id="aktera.tools.show-jvm-diagram" logger="aktera"
 * @model.attribute name="forward" value="aktera.tools.data-download"
 */
public class ShowJvmMemoryDiagram extends SecurableStandardLogEnabledModel
{
	private BufferedImage image;

	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse();

		try
		{
			AkteraJvmMemoryManager jvmMemoryManager = (AkteraJvmMemoryManager) req
							.getSpringBean(AkteraJvmMemoryManager.ID);

			BufferedImage image = new BufferedImage(1024 + 91, 768 + 92, BufferedImage.TYPE_INT_RGB);
			String name = req.getParameterAsString("name");

			jvmMemoryManager.generateGraph(name, image);

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			ImageIO.write(image, "jpg", os);

			Output out = res.createOutput("data");

			out.setContent(os.toByteArray());
			res.add(out);
			os.close();

			res.addOutput("contentType", "image/png");
			res.addOutput("diagram", "diagram.png");
		}
		catch (Exception x)
		{
		}

		return res;
	}
}
