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
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


/**
 * ResponseElementDynaBean
 *
 * A wrapper for a ResponseElement (Input, Output, Command) that allows it to be
 * accessed as a DynaBean.
 *
 * Created on Jul 25, 2002
 */
public class ResponseElementDynaBean extends MapExposingBasicDynaBean
{
	private ResponseElement myElement = null;

	/**
	 * Constructor for ResponseElementDynaBean.
	 * @param arg0
	 */
	public ResponseElementDynaBean(DynaClass arg0)
	{
		super(arg0);
	}

	public ResponseElementDynaBean(ResponseElement re)
	{
		super(ResponseElementDynaClassFactory.getInstance(re));
		setResponseElement(re);
	}

	public String toString()
	{
		if (myElement instanceof Output)
		{
			Output o = (Output) myElement;

			if (o.getContent() != null)
			{
				return o.getContent().toString();
			}
			else
			{
				return "";
			}
		}

		return super.toString();
	}

	public void setResponseElement(ResponseElement re)
	{
		myElement = re;

		try
		{
			List allNested = re.getAll();
			ArrayList newNested = new ArrayList();

			set("name", re.getName());

			if (allNested.size() > 0)
			{
				ResponseElement nestedElement = null;

				for (Iterator in = allNested.iterator(); in.hasNext();)
				{
					nestedElement = (ResponseElement) in.next();
					newNested.add(new ResponseElementDynaBean(nestedElement));
				}
			}

			set("nested", newNested);

			/**
			 * For attributes, we create a bean called "attributes" that has a getter and setter
			 * for every defined attribute, returning the attribute value
			 */
			Map attributes = re.getAttributes();

			if (attributes.size() > 0)
			{
				String oneAttribName = null;
				Object oneAttribValue = null;
				DynaProperty[] dattr = new DynaProperty[attributes.size()];
				int j = 0;

				for (Iterator io = attributes.keySet().iterator(); io.hasNext();)
				{
					oneAttribName = (String) io.next();
					oneAttribValue = attributes.get(oneAttribName);

					if (oneAttribValue == null)
					{
						dattr[j++] = new DynaProperty(oneAttribName, Class.forName("java.lang.String"));
					}
					else
					{
						if (oneAttribValue instanceof ResponseElement)
						{
							dattr[j++] = new DynaProperty(oneAttribName, Class
											.forName("de.iritgo.aktera.clients.ResponseElementDynaBean"));
						}
						else
						{
							dattr[j++] = new DynaProperty(oneAttribName, oneAttribValue.getClass());
						}
					}
				}

				DynaClass bdattr = new BasicDynaClass(re.getName() + "_attributes", Class
								.forName("de.iritgo.aktera.clients.MapExposingBasicDynaBean"), dattr);

				DynaBean oneAttrBean = bdattr.newInstance();

				for (Iterator io = attributes.keySet().iterator(); io.hasNext();)
				{
					oneAttribName = (String) io.next();
					oneAttribValue = attributes.get(oneAttribName);

					if (oneAttribValue instanceof ResponseElement)
					{
						oneAttrBean.set(oneAttribName, new ResponseElementDynaBean((ResponseElement) oneAttribValue));
					}
					else
					{
						oneAttrBean.set(oneAttribName, oneAttribValue);
					}
				}

				set("attributes", oneAttrBean);
			}
			else
			{
				DynaClass bdattr = new BasicDynaClass(re.getName() + "_attributes", Class
								.forName("de.iritgo.aktera.clients.MapExposingBasicDynaBean"), new DynaProperty[0]);
				BasicDynaBean oneAttrBean = (BasicDynaBean) bdattr.newInstance();

				set("attributes", oneAttrBean);
			}

			if (re instanceof Input)
			{
				set("type", "input");

				Input i = (Input) re;

				set("defaultValue", i.getDefaultValue());

				Map valids = i.getValidValues();

				if (valids.size() > 0)
				{
					BeanComparator bc = new BeanComparator("value");
					TreeSet options = new TreeSet(bc);
					DynaProperty[] dpopts = new DynaProperty[2];

					dpopts[0] = new DynaProperty("value", Class.forName("java.lang.String"));
					dpopts[1] = new DynaProperty("label", Class.forName("java.lang.String"));

					DynaClass bdopt = new BasicDynaClass("validValues", Class
									.forName("de.iritgo.aktera.clients.MapExposingBasicDynaBean"), dpopts);
					Object oneOpt = null;
					Object oneLabelObj = null;

					for (Iterator io = valids.keySet().iterator(); io.hasNext();)
					{
						oneOpt = io.next();

						DynaBean oneOptBean = bdopt.newInstance();

						oneOptBean.set("value", oneOpt.toString());
						oneLabelObj = valids.get(oneOpt.toString());

						if (oneLabelObj == null)
						{
							oneLabelObj = "(No Label)";
						}

						oneOptBean.set("label", oneLabelObj.toString());
						options.add(oneOptBean);
					}

					set("validValues", options);
				}

				set("label", i.getLabel());
			}

			if (re instanceof Output)
			{
				set("type", "output");

				Output o = (Output) re;
				Object content = o.getContent();

				set("content", content);
			}

			if (re instanceof Command)
			{
				set("type", "command");

				Command c = (Command) re;

				if (c.getModel() != null)
				{
					set("model", c.getModel());
				}

				if (c.getBean() != null)
				{
					set("bean", c.getBean());
				}

				set("label", c.getLabel());

				Map params = c.getParameters();

				set("parameters", params);
			}
		}
		catch (Exception e)
		{
			System.err.println("ResponseElementDynaBean:");
			e.printStackTrace(System.err);
			throw new IllegalArgumentException(
							"ResponseElementBean could not be constructed - see server log for details "
											+ e.getMessage());
		}
	}

	public ResponseElement getResponseElement()
	{
		return myElement;
	}
}
