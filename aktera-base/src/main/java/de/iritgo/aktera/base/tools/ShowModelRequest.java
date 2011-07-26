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
import java.util.Iterator;
import java.util.Map;


/**
 * This is an experminentational model to test things out.
 *
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.tools.show-model-request"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.tools.show-model-request" id="aktera.tools.show-model-request" logger="aktera"
 * @model.attribute name="forward" value="aktera.tools.show-model-request"
 */
public class ShowModelRequest extends StandardLogEnabledModel
{
	/**
	 * Execute the model.
	 *
	 * @param req The model request.
	 * @return The model response.
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		ModelResponse res = req.createResponse ();

		System.out.println ("\nModel Request:");

		System.out.println ("  Source: " + req.getSource ());

		Output source = res.createOutput ("source", req.getSource ());

		res.add (source);

		System.out.println ("  Headers:");

		Output headerList = res.createOutput ("headers");

		res.add (headerList);

		int num = 1;

		for (Iterator i = req.getHeaders ().entrySet ().iterator (); i.hasNext ();)
		{
			Map.Entry entry = (Map.Entry) i.next ();

			System.out.println ("   " + (String) entry.getKey () + ": " + req.getHeader ((String) entry.getKey ()));

			Output header = res.createOutput ("header" + num++, (String) entry.getKey ());

			header.setAttribute ("value", req.getHeader ((String) entry.getKey ()));
			headerList.add (header);
		}

		System.out.println ("  Parameters:");

		Output parameterList = res.createOutput ("parameters");

		res.add (parameterList);

		num = 1;

		for (Iterator i = req.getParameters ().entrySet ().iterator (); i.hasNext ();)
		{
			Map.Entry entry = (Map.Entry) i.next ();

			System.out.println ("   " + (String) entry.getKey () + " = " + req.getParameter ((String) entry.getKey ()));

			Output parameter = res.createOutput ("parameter" + num++, (String) entry.getKey ());

			parameter.setAttribute ("value", req.getParameter ((String) entry.getKey ()));
			parameterList.add (parameter);
		}

		System.out.println ("  Attributes:");

		Output attributeList = res.createOutput ("attributes");

		res.add (attributeList);

		num = 1;

		for (Iterator i = req.getAttributes ().entrySet ().iterator (); i.hasNext ();)
		{
			Map.Entry entry = (Map.Entry) i.next ();

			System.out.println ("   " + (String) entry.getKey () + " = " + req.getAttribute ((String) entry.getKey ()));

			Output attribute = res.createOutput ("attribute" + num++, (String) entry.getKey ());

			attribute.setAttribute ("value", req.getAttribute ((String) entry.getKey ()));
			attributeList.add (attribute);
		}

		return res;
	}
}
