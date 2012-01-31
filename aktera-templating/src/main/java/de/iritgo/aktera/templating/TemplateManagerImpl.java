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

package de.iritgo.aktera.templating;


import java.io.StringWriter;
import java.util.Map;
import lombok.Setter;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import javax.inject.Inject;


public class TemplateManagerImpl implements TemplateManager
{
	@Setter
	@Inject
	private VelocityEngine velocityEngine;

	@Setter
	@Inject
	private VelocitySpringResourceLoader velocitySpringResourceLoader;

	/**
	 * @see de.iritgo.aktera.templating.TemplateManager#renderTemplate(java.lang.String, java.lang.String, org.apache.velocity.context.Context)
	 */
	public String renderTemplate(String templateName, String template, Context context)
	{
		velocitySpringResourceLoader.addTemplate(templateName, template);
		Template velocityTemplate = velocityEngine.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		velocityTemplate.merge(context, writer);
		return writer.toString();
	}
}
