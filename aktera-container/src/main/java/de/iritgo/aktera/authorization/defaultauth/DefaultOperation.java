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

package de.iritgo.aktera.authorization.defaultauth;


import de.iritgo.aktera.authorization.InstanceSecurable;
import de.iritgo.aktera.authorization.Operation;
import org.apache.avalon.fortress.impl.factory.ProxyObjectFactory;
import java.lang.reflect.Proxy;
import java.util.Map;


/**
 * @author sdutt
 *
 * A basic Operation implementation, used to indicate a service and a valid
 * operation for that service.
 */
public class DefaultOperation implements Operation
{
	private String opCode = null;

	private Map params = null;

	private Object o = null;

	/**
	 * @see de.iritgo.aktera.authorization.Operation#setService(Object)
	 */
	public void setService (Object o)
	{
		assert o != null;
		this.o = o;
	}

	/**
	 * @see de.iritgo.aktera.authorization.Operation#setOperationCode(String)
	 */
	public void setOperationCode (String opCode)
	{
		assert opCode != null;
		this.opCode = opCode;
	}

	/**
	 * @see de.iritgo.aktera.authorization.Operation#setParameter(Map)
	 */
	public void setParameter (Map params)
	{
		this.params = params;
	}

	/**
	 * @see de.iritgo.aktera.authorization.Operation#getService()
	 */
	public Object getService ()
	{
		return this.o;
	}

	/**
	 * @see de.iritgo.aktera.authorization.Operation#getOperationCode()
	 */
	public String getOperationCode ()
	{
		return this.opCode;
	}

	/**
	 * @see de.iritgo.aktera.authorization.Operation#getParams()
	 */
	public Map getParams ()
	{
		return this.params;
	}

	public String toString ()
	{
		StringBuffer buf = new StringBuffer ("Component [" + getComponentName (o) + "] Op:[" + opCode + "]");

		if (o instanceof InstanceSecurable)
		{
			buf.append (" Instance:[" + ((InstanceSecurable) o).getInstanceIdentifier () + "]");
		}
		else
		{
			buf.append (" Service:[" + getService ().toString () + "]");
		}

		return buf.toString ();
	}

	private String getComponentName (Object component)
	{
		String returnValue = null;

		if (Proxy.isProxyClass (component.getClass ()))
		{
			Proxy proxy = (Proxy) component;
			Object o = ProxyObjectFactory.getObject (proxy);

			returnValue = o.getClass ().getName ();
		}
		else
		{
			returnValue = component.getClass ().getName ();
		}

		return returnValue;
	}
}
