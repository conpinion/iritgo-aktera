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

package de.iritgo.aktera.authorization;


import java.util.Map;


/**
 * @author Michael Nash
 *
 * An operation is a reference to some function of a Securable
 * service that is able to be secured. E.g. if a service can do three different
 * things (add, update, delete, for example), you can define an operation for
 * each of these, and secure them individually.
 */
public interface Operation
{
	/**
	 * What service does this operation refer to?
	 */
	public void setService(Object o);

	/**
	 * What operation are we referring to?
	 */
	public void setOperationCode(String opCode);

	/**
	 * What parameters (name/value) are involved in this
	 * operation?
	 */
	public void setParameter(Map params);

	public Object getService();

	public String getOperationCode();

	public Map getParams();
}
