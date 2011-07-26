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

package de.iritgo.aktera.clients;


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;
import java.util.List;
import java.util.Map;


/**
 * @author root
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ResponseElementDynaClassFactory
{
	/**
	 * Static singleton pattern
	 */
	private ResponseElementDynaClassFactory ()
	{
	}

	public static BasicDynaClass getInstance (ResponseElement re)
	{
		try
		{
			int propCount = 0;

			/** Account for the name and type */
			int totalPropCount = 2;

			List allNested = re.getAll ();

			totalPropCount = totalPropCount + 1; //allNested.size();

			// Map attributes = re.getAttributes();
			totalPropCount = totalPropCount + 1; //attributes.size();

			if (re instanceof Input)
			{
				totalPropCount = totalPropCount + 3;

				Input i = (Input) re;
				Map valids = i.getValidValues ();

				if (valids.size () == 0)
				{
					totalPropCount = totalPropCount - 1;
				}
			}

			if (re instanceof Output)
			{
				totalPropCount = totalPropCount + 1;
			}

			if (re instanceof Command)
			{
				totalPropCount = totalPropCount + 4;
			}

			DynaProperty[] props = new DynaProperty[totalPropCount];
			DynaProperty nameProp = new DynaProperty ("name", Class.forName ("java.lang.String"));

			props[propCount++] = nameProp;

			DynaProperty typeProp = new DynaProperty ("type", Class.forName ("java.lang.String"));

			props[propCount++] = typeProp;

			DynaProperty nested = new DynaProperty ("nested", allNested.getClass ());

			props[propCount++] = nested;

			/*if (allNested.size() > 0) {
			    ResponseElement nestedElement = null;
			    for (Iterator in = allNested.iterator(); in.hasNext();) {
			        nestedElement = (ResponseElement) in.next();
			        DynaProperty nestedProp =
			            new DynaProperty(
			                nestedElement.getName(),
			                Class.forName(
			                    "de.iritgo.aktera.clients.ResponseElementDynaBean"));
			        props[propCount++] = nestedProp;
			    }
			} */
			DynaProperty attribProp = new DynaProperty ("attributes", Class
							.forName ("org.apache.commons.beanutils.BasicDynaBean"));

			props[propCount++] = attribProp;

			/*   if (attributes.size() > 0) {
			       Object attribVal = null;
			       String oneKey = null;
			       for (Iterator in = attributes.keySet().iterator();
			           in.hasNext();
			           ) {
			           oneKey = (String) in.next();
			           attribVal = attributes.get(oneKey);
			           DynaProperty nestedProp =
			               new DynaProperty(oneKey, attribVal.getClass());
			           props[propCount++] = nestedProp;
			       }
			   } */

			/**
			 * If the element is an Input, create properties appropriate to an Input
			 */
			if (re instanceof Input)
			{
				Input i = (Input) re;
				Object defValue = i.getDefaultValue ();
				DynaProperty defProp = null;

				if (defValue != null)
				{
					defProp = new DynaProperty ("defaultValue", i.getDefaultValue ().getClass ());
				}
				else
				{
					defProp = new DynaProperty ("defaultValue", Class.forName ("java.lang.String"));
				}

				props[propCount++] = defProp;

				/**
				 * We only have an attribute of "validValues" if the set of valid values
				 * for this input is not empty. The actual validValues property gets
				 * populated by a HashSet of dynabeans, where each bean has attributes
				 * name and label
				 */
				Map valids = i.getValidValues ();

				if (valids.size () > 0)
				{
					DynaProperty validsProp = new DynaProperty ("validValues", Class.forName ("java.util.TreeSet"));

					props[propCount++] = validsProp;
				}

				DynaProperty labelProp = new DynaProperty ("label", Class.forName ("java.lang.String"));

				props[propCount++] = labelProp;
			}

			if (re instanceof Output)
			{
				Output o = (Output) re;
				Object content = o.getContent ();
				DynaProperty contentProp = null;

				if (content != null)
				{
					contentProp = new DynaProperty ("content", content.getClass ());
				}
				else
				{
					contentProp = new DynaProperty ("content", Class.forName ("java.lang.String"));
				}

				props[propCount++] = contentProp;
			}

			if (re instanceof Command)
			{
				Command c = (Command) re;

				DynaProperty modelProp = new DynaProperty ("model", Class.forName ("java.lang.String"));

				props[propCount++] = modelProp;

				DynaProperty beanProp = new DynaProperty ("bean", Class.forName ("java.lang.String"));

				props[propCount++] = beanProp;

				DynaProperty labelProp = new DynaProperty ("label", Class.forName ("java.lang.String"));

				props[propCount++] = labelProp;

				Map params = c.getParameters ();
				DynaProperty paramsProp = new DynaProperty ("parameters", params.getClass ());

				props[propCount++] = paramsProp;
			}

			return new BasicDynaClass (re.getName (), Class
							.forName ("de.iritgo.aktera.clients.ResponseElementDynaBean"), props);
		}
		catch (Exception e)
		{
			System.err.println ("ResponseElementDynaClassFactory: Unable to get instance of dynaclass:");
			e.printStackTrace (System.err);
			throw new IllegalArgumentException (e.getMessage ());
		}
	}
}
