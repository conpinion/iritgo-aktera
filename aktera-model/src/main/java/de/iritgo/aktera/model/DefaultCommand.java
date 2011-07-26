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


import java.util.*;


/**
 * Give the user a navigation option
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Command
 * @x-avalon.info name=default-command
 * @x-avalon.lifestyle type=transient
 *
 */
public class DefaultCommand extends AbstractResponseElement implements Command
{
	private Map params = new HashMap ();

	private String myModel = null;

	private String myLabel = null;

	private int moveRelativeSteps = 0;

	private String myBean = null;

	/**
	 * Specify the name of the model this command will execute
	 * @param newModel The name (the logical configuration name) of the model that will
	 * be executed by this command.
	 */
	public void setModel (String newModel)
	{
		myModel = newModel;
	}

	public String getModel ()
	{
		return myModel;
	}

	public void setParameter (String param, Object value)
	{
		assert param != null;

		//         if (value != null) {
		//             if  ((!value.getClass().getName().startsWith("java.lang")) && (!value.getClass().getName().equals("java.util.Date"))) {
		//                 throw new IllegalArgumentException("Only basic types may be used as parameter values");
		//             }
		//         }
		params.put (param, value);
	}

	public Map getParameters ()
	{
		return params;
	}

	public ModelResponse execute (ModelRequest req, ModelResponse res) throws ModelException
	{
		return execute (req, res, false, false);
	}

	public ModelResponse execute (ModelRequest req, ModelResponse res, boolean includeParams,
					boolean includeResponseElements) throws ModelException
	{
		if (myModel == null && myBean == null)
		{
			throw new ModelException ("No model or bean set for this command");
		}

		ModelRequest newReq = (ModelRequest) req.getService (ModelRequest.ROLE, "default", req.getContext ());

		newReq.setModel (myModel != null ? myModel : myBean);
		newReq.setScheme (req.getScheme ());
		newReq.setServerName (req.getServerName ());
		newReq.setServerPort (req.getServerPort ());
		newReq.setContextPath (req.getContextPath ());
		newReq.setRequestUrl (req.getRequestUrl ());
		newReq.setQueryString (req.getQueryString ());
		newReq.setLocale (req.getLocale ());

		String oneAttribName = null;

		for (Iterator ip = req.getAttributes ().keySet ().iterator (); ip.hasNext ();)
		{
			oneAttribName = (String) ip.next ();
			newReq.setAttribute (oneAttribName, req.getAttribute (oneAttribName));
		}

		newReq.setPreviousRequest (req.getParameters ());

		String oneParam = null;

		for (Iterator i = params.keySet ().iterator (); i.hasNext ();)
		{
			oneParam = (String) i.next ();
			newReq.setParameter (oneParam, params.get (oneParam));
		}

		if (includeParams)
		{
			Map oldParams = req.getParameters ();
			String oldParamKey = null;

			for (Iterator ip = oldParams.keySet ().iterator (); ip.hasNext ();)
			{
				oldParamKey = (String) ip.next ();

				if (! params.containsKey (oldParamKey))
				{
					newReq.setParameter (oldParamKey, oldParams.get (oldParamKey));
				}
			}
		}

		ModelResponse newRes = newReq.execute ();

		newRes.addErrors (res.getErrors ());

		if (includeResponseElements)
		{
			ResponseElement oldElement = null;

			for (Iterator oe = res.getAll (); oe.hasNext ();)
			{
				oldElement = (ResponseElement) oe.next ();

				if (newRes.get (oldElement.getName ()) == null)
				{
					newRes.add (oldElement);
				}
			}
		}

		return newRes;
	}

	public void setLabel (String newLabel)
	{
		myLabel = newLabel;
	}

	public String getLabel ()
	{
		return myLabel;
	}

	public void setRelativeMovement (int numberOfSteps)
	{
		moveRelativeSteps = numberOfSteps;
	}

	public int getRelativeMovement ()
	{
		return moveRelativeSteps;
	}

	public String toString ()
	{
		StringBuffer ret = new StringBuffer (super.toString ());

		ret.append ("\t<label>" + getLabel () + "</label>");
		ret.append ("\t<model>" + getLabel () + "</model>");

		Map m = getParameters ();

		if (m.size () > 0)
		{
			ret.append ("\t<parameters>");

			for (Iterator i = m.keySet ().iterator (); i.hasNext ();)
			{
				String oneKey = (String) i.next ();

				ret.append ("\t\t<parameter key='" + oneKey + "' value='" + m.get (oneKey).toString () + "'/>\n");
			}

			ret.append ("\t</parameters>");
		}

		ret.append ("\t</" + getClass ().getName () + ">\n");

		return ret.toString ();
	}

	public String getBean ()
	{
		return myBean;
	}

	public void setBean (String bean)
	{
		myBean = bean;
	}
}
