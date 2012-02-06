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

package de.iritgo.aktera.model;


import org.apache.avalon.framework.configuration.Configuration;


/**
 * A 'Model' is the basic unit of application logic in Keel. Applications consist
 * of a number of models and their associated support objects.
 * A model is a very simple interface - only one method is required, and the
 * implementation of that method is left entirely up to the model object itself. Models
 * work closely in conjunction with ModelRequests, so see the detailed information
 * in the ModelRequest class as well.
 */
public interface Model
{
	public String ROLE = "de.iritgo.aktera.model.Model";

	public ModelResponse execute(ModelRequest request) throws ModelException;

	public Configuration getConfiguration();
}
