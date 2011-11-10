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

package de.iritgo.aktera.context;


import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;


/**
 * This interface is used to set and retrieve the Keel session context
 * used for authenticating Securable components.
 *
 * Any class expecting that expects to use Securable components,
 * amd that acquires the Securable components from a ModelRequest,
 * must implement this interface.
 *
 * @version        $Revision: 1.2 $        $Date: 2003/07/03 15:49:32 $
 * @author Schatterjee
 * Created on May 29, 2003
 */
public interface KeelContextualizable
{
	public void setKeelContext(Context Context) throws ContextException;

	public Context getKeelContext();
}
