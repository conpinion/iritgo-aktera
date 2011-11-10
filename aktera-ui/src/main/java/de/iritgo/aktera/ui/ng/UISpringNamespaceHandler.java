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

package de.iritgo.aktera.ui.ng;


import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import de.iritgo.aktera.ui.ng.formular.DeleteBeanDefinitionParser;
import de.iritgo.aktera.ui.ng.formular.EditBeanDefinitionParser;
import de.iritgo.aktera.ui.ng.formular.FormularBeanDefinitionParser;
import de.iritgo.aktera.ui.ng.formular.FormularGroupBeanDefinitionParser;
import de.iritgo.aktera.ui.ng.formular.SaveBeanDefinitionParser;
import de.iritgo.aktera.ui.ng.listing.ListingBeanDefinitionParser;


public class UISpringNamespaceHandler extends NamespaceHandlerSupport
{
	public void init()
	{
		registerBeanDefinitionParser("listing", new ListingBeanDefinitionParser());
		registerBeanDefinitionParser("formular", new FormularBeanDefinitionParser());
		registerBeanDefinitionParser("formularGroup", new FormularGroupBeanDefinitionParser());
		registerBeanDefinitionParser("edit", new EditBeanDefinitionParser());
		registerBeanDefinitionParser("save", new SaveBeanDefinitionParser());
		registerBeanDefinitionParser("delete", new DeleteBeanDefinitionParser());
		registerBeanDefinitionParser("sequence", new SequenceBeanDefinitionParser());
	}
}
