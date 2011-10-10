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


import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import org.apache.avalon.excalibur.pool.*;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.context.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.framework.service.*;
import org.apache.commons.beanutils.*;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.context.KeelContextualizable;
import de.iritgo.aktera.core.container.*;
import de.iritgo.aktera.models.util.SequenceContext;
import de.iritgo.aktera.util.string.SuperString;
import de.iritgo.simplelife.string.StringTools;


/**
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.ModelRequest
 * @x-avalon.info name=default-request
 * @x-avalon.lifestyle type=transient
 *
 */
public class DefaultModelRequest implements LogEnabled, KeelServiceable, ModelRequest, Configurable,
				KeelContextualizable, Poolable, Recyclable
{
	private Logger log = null;

	private ModelValidator myDefaultValidator = null;

	protected KeelServiceableDelegate svcDelegate = null;

	private Map params = null;

	private boolean configured = false;

	private Map attributes = null;

	private Context keelContext = null;

	private String myModel = null;

	private Configuration myConfig = null;

	private Map previousRequest = null;

	private Map validationErrors = new HashMap ();

	private Map headers = null;

	private String source = null;

	private Locale locale = null;

	private String scheme;

	private String serverName;

	private int serverPort;

	private String contextPath;

	private String requestUrl;

	private String queryString;

	private String myBean;

	public DefaultModelRequest ()
	{
		super ();
	}

	public String getModel ()
	{
		return myModel;
	}

	/**
	 * Model requests can be "recycled", that is, cleared and used again for a
	 * new request.
	 */
	public void recycle ()
	{
		params = null;
		configured = false;
		attributes = null;
		myConfig = null;
		previousRequest = null;
		myModel = null;
		validationErrors = new HashMap ();
		headers = null;
		source = null;
		locale = null;

		if (log.isDebugEnabled ())
		{
			log.debug ("Request " + toString () + " recycled");
		}
	}

	/**
	 * Retrieve the named parameter as a "raw" object - e.g. attempt no type
	 * conversion.
	 *
	 * @param paramKey The name of the requested parameter
	 * @return The value of the specified parameter, or null if there is no
	 *         parameter with that name.
	 */
	public Object getParameter (String paramKey)
	{
		Object returnValue = null;

		if (params != null)
		{
			returnValue = params.get (paramKey);
		}

		return returnValue;
	}

	/**
	 * Retrieve the entire mapping of parameters and values
	 *
	 * @return A Map of parameter names and their values
	 */
	public Map getParameters ()
	{
		Map returnValue = null;

		if (params == null)
		{
			returnValue = new HashMap ();
		}
		else
		{
			returnValue = params;
		}

		return returnValue;
	}

	public void setParameter (String paramKey, Object param)
	{
		if (paramKey != null)
		{
			if (params == null)
			{
				params = new HashMap ();
			}

			params.put (paramKey, param);
		}
	}

	/**
	 * The name of the model and the state allow us to determine the actual Java
	 * class that is going to be invoked, and how we're going to communicate
	 * with it
	 */
	public void setModel (String modelName)
	{
		myModel = modelName;
	}

	public ModelResponse execute () throws ModelException
	{
		/* Create the appropriate model */
		assert myModel != null;

		if (! configured)
		{
			throw new ModelException ("Model request not configured");
		}

		Model newModel = null;

		try
		{
			newModel = (Model) getService (Model.ROLE, myModel, getContext ());

			Command redirect = validate (newModel);
			ModelResponse res = null;

			try
			{
				if (redirect == null)
				{
					res = newModel.execute (this);
				}
				else
				{
					res = redirect.execute (this, createResponse ());
				}
			}
			catch (Exception ee)
			{
				/* Any error during the actual execute is stored */
				if (res == null)
				{
					res = createResponse ();
				}

				res.addError ("Exception during model execution", ee);
			}

			if (res == null)
			{
				res = createResponse ();
			}

			return afterExecute (res, newModel.getConfiguration ());
		}
		catch (Exception ce)
		{
			throw new ModelException ("Could not run model '" + myModel + "' for role '" + Model.ROLE + "'", ce);
		}
		finally
		{
			svcDelegate.releaseServices ();
		}
	}

	/*
	 * Used by the Model (only) to create the empty response
	 */
	public ModelResponse createResponse () throws ModelException
	{
		ModelResponse newResponse = new DefaultModelResponse ();

		newResponse.setRequest (this);

		return newResponse;
	}

	public Context getContext ()
	{
		return getKeelContext ();
	}

	public int getUid ()
	{
		int returnValue = UserEnvironment.ANONYMOUS_UID;

		try
		{
			Context c = getContext ();

			UserEnvironment ue = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);

			if (ue != null)
			{
				returnValue = ue.getUid ();
			}
		}
		catch (ContextException ce)
		{
			log.error ("No user information in context");
		}
		catch (AuthorizationException e)
		{
			log.warn ("Authroization error");
		}

		return returnValue;
	}

	public String getDomain ()
	{
		String returnValue = UserEnvironment.ANONYMOUS_DOMAIN;

		try
		{
			Context c = getContext ();

			if (c == null)
			{
				throw new IllegalArgumentException ("No context");
			}

			UserEnvironment ue = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);

			if (ue != null)
			{
				returnValue = ue.getDomain ();
			}
		}
		catch (ContextException ignored)
		{
		}
		catch (AuthorizationException ignored)
		{
		}

		return returnValue;
	}

	public Map getPreviousRequest ()
	{
		return previousRequest;
	}

	public void setPreviousRequest (Map newPrevious)
	{
		previousRequest = newPrevious;
	}

	public void configure (Configuration conf) throws ConfigurationException
	{
		if (conf == null)
		{
			throw new ConfigurationException ("Null configuration not allowed");
		}

		configured = true;
		myConfig = conf;
	}

	public Object getConfiguration ()
	{
		return myConfig;
	}

	protected ModelResponse afterExecute (ModelResponse res, Configuration modelConfig) throws ModelException
	{
		try
		{
			Map currentAttributes = res.getAttributes ();

			Configuration[] children = modelConfig.getChildren ();

			// if (children.length == 0) {
			// log.warn("No configuration for model '" + myModel + "'");
			// }
			int addedAttribCount = 0;

			for (int i = 0; i < children.length; i++)
			{
				Configuration attrib = children[i];

				if (attrib.getName ().equals ("attribute"))
				{
					String attName = attrib.getAttribute ("name");
					String attVal = attrib.getAttribute ("value");

					if (! currentAttributes.containsKey (attName))
					{
						res.setAttribute (attName, attVal);
						addedAttribCount++;
					}
					else
					{
						log.debug ("Attribute '" + attName + "' for model '" + myModel
										+ "' was already supplied by the model");
					}
				}
			}

			log.debug ("Added " + addedAttribCount + " configuration attributes for model '" + myModel + "'");
		}
		catch (ConfigurationException ce)
		{
			throw new ModelException (ce);
		}

		// New logic to redirect back to the "sequence" if we were running one
		try
		{
			SequenceContext scon = (SequenceContext) getKeelContext ().get (SequenceContext.CONTEXT_KEY);

			if (scon != null)
			{
				log.debug ("SequenceContext is not null. Current Model = " + getModel () + ", model in sequence = "
								+ scon.getSequenceName () + ".");

				/* Make sure we didn't just come from the same sequence */
				if (! getModel ().equals (scon.getSequenceName ()))
				{
					scon.setCurrentResponse (res);
					// runSeq.setParameter( "seq", new
					// Integer(scon.getSeq()).toString());
					// expirimenting with the seq problems. When we transition,
					// we should be setting the seq back....
					log.debug ("Model name was not the same. Transitioning to new model or first step of new sequence....");

					// The code below was commented out by ACR. Not sure how
					// this was useful, as a user can never "break out" of a
					// sequence....
					// Command runSeq =
					// res.createCommand(scon.getSequenceModel());
					// runSeq.setParameter( "seq", "1");
					// return runSeq.execute(this, res);
				}
			}
		}
		catch (ContextException ce)
		{
			/* Do nothing, no sequence */
			// --- quikdraw: Why? If ContextException is general it seems there
			// should be a more specific exception here to identify NoSequence.
		}

		return res;
	}

	public Object getAttribute (String key)
	{
		Object returnValue = null;

		if (key != null)
		{
			if (attributes != null)
			{
				returnValue = attributes.get (key);
			}
		}

		return returnValue;
	}

	public Map getAttributes ()
	{
		Map returnValue = null;

		if (attributes == null)
		{
			returnValue = new HashMap ();
		}
		else
		{
			returnValue = attributes;
		}

		return returnValue;
	}

	public void setAttribute (String key, Object value)
	{
		if (attributes == null)
		{
			attributes = new HashMap ();
		}

		if (key != null)
		{
			synchronized (attributes)
			{
				attributes.put (key, value);
			}
		}
	}

	public Set getOperations ()
	{
		return null;
	}

	/**
	 * This method first checks if the current model is securable. If it is then
	 * it uses the AuthorizationManager class to check for authorization. If
	 * authorized then it passes on the model, otherwise throws
	 * SecurityException.
	 */
	public Object getService (String role, String hint) throws ModelException
	{
		Object o = null;

		try
		{
			o = svcDelegate.getService (role, hint, getContext ());
		}
		catch (ServiceException e)
		{
			throw new ModelException (e);
		}

		return o;
	}

	public byte[] serialize () throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		ObjectOutputStream oos = new ObjectOutputStream (baos);

		oos.writeObject (this);

		return baos.toByteArray ();
	}

	public KeelRequest deserialize (byte[] bytes) throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream (bytes);
		ObjectInputStream ois = new ObjectInputStream (bais);
		KeelRequest myObject;

		try
		{
			myObject = (KeelRequest) ois.readObject ();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException (e.getMessage ());
		}

		return myObject;
	}

	public void copyFrom (KeelRequest newRequest) throws ModelException
	{
		setModel (newRequest.getModel ());

		String oneParamName = null;

		for (Iterator i = newRequest.getParameters ().keySet ().iterator (); i.hasNext ();)
		{
			oneParamName = (String) i.next ();
			setParameter (oneParamName, newRequest.getParameter (oneParamName));
		}

		String oneAttributeName = null;

		for (Iterator ia = newRequest.getAttributes ().keySet ().iterator (); ia.hasNext ();)
		{
			oneAttributeName = (String) ia.next ();
			setAttribute (oneAttributeName, newRequest.getAttribute (oneAttributeName));
		}

		setSource (newRequest.getSource ());
		setLocale (newRequest.getLocale ());
		setScheme (newRequest.getScheme ());
		setServerName (newRequest.getServerName ());
		setServerPort (newRequest.getServerPort ());
		setContextPath (newRequest.getContextPath ());
		setRequestUrl (newRequest.getRequestUrl ());
		setQueryString (newRequest.getQueryString ());

		String oneHeaderName = null;

		for (Iterator ia = newRequest.getHeaders ().keySet ().iterator (); ia.hasNext ();)
		{
			oneHeaderName = (String) ia.next ();
			setHeader (oneHeaderName, newRequest.getHeader (oneHeaderName));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache
	 * .avalon.framework.logger.Logger)
	 */
	public void enableLogging (Logger logger)
	{
		log = logger;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.iritgo.aktera.model.ModelRequest#getDefaultService(java.lang.String)
	 */
	public Object getDefaultService (String role) throws ModelException
	{
		try
		{
			return svcDelegate.getService (role);
		}
		catch (ServiceException e)
		{
			throw new ModelException (e);
		}
	}

	/**
	 * @see de.iritgo.aktera.model.ModelRequest#getService(java.lang.String)
	 */
	public Object getService (String role) throws ModelException
	{
		return getService (role, "default");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.iritgo.aktera.model.ModelRequest#getService(java.lang.String,
	 * java.lang.String, org.apache.avalon.framework.context.Context)
	 */
	public Object getService (String role, String hint, Context ctx) throws ModelException
	{
		try
		{
			return svcDelegate.getService (role, hint, ctx);
		}
		catch (ServiceException e)
		{
			throw new ModelException (e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon
	 * .framework.service.ServiceManager)
	 */
	public void service (ServiceManager manager) throws ServiceException
	{
		svcDelegate = new KeelServiceableDelegate (manager);
	}

	/**
	 * @see de.iritgo.aktera.context.KeelContextualizable#getKeelContext()
	 */
	public Context getKeelContext ()
	{
		return keelContext;
	}

	/**
	 * @see de.iritgo.aktera.context.KeelContextualizable#setKeelContext(org.apache.avalon.framework.context.Context)
	 */
	public void setKeelContext (Context keelContext) throws ContextException
	{
		this.keelContext = keelContext;
	}

	/** *** Type-specific access to request parameters ***** */
	public int getParameterAsInt (String name)
	{
		assert name != null;

		Converter c = ConvertUtils.lookup (java.lang.Integer.class);

		return ((Integer) c.convert (java.lang.Integer.class, getParameterAsString (name))).intValue ();
	}

	public int getParameterAsInt (String name, int defaultValue)
	{
		assert name != null;

		Object val = getParameter (name);

		if (val == null || "".equals (val))
		{
			return defaultValue;
		}

		return getParameterAsInt (name);
	}

	public String getParameterAsString (String name)
	{
		assert name != null;

		Object val = getParameter (name);

		if (val == null)
		{
			return "";
		}

		if (val instanceof String[])
		{
			val = ((String[]) val)[0];
		}

		return val.toString ();
	}

	public String getParameterAsString (String name, String defaultValue)
	{
		assert name != null;

		Object val = getParameter (name);

		if (val == null)
		{
			return defaultValue;
		}

		return val.toString ();
	}

	public Object[] getParameterAsArray (String name)
	{
		final Object val = getParameter (name);

		if (val == null)
		{
			// No parameter by this name?
			return (null);
		} // else

		final Class valClass = val.getClass ();
		final Class valType = valClass.getComponentType ();

		if (valType == null)
		{
			// Val Class is not an Array
			Object[] retVal = (Object[]) Array.newInstance (val.getClass (), 1);

			retVal[0] = val;

			return (retVal);
		}
		else
		{
			Object[] retVal = (Object[]) Array.newInstance (valType, Array.getLength (val));

			for (int i = 0; i < Array.getLength (val); i++)
			{
				retVal[i] = Array.get (val, i);
			} // end for

			return retVal;
		} // end else
	} // end getParameterAsArray

	public Date getParameterAsDate (String name)
	{
		assert name != null;

		return new SuperString (getParameterAsString (name)).toDate ();
	}

	public Date getParameterAsDate (String name, Date defaultValue)
	{
		assert name != null;

		Object val = getParameter (name);

		if (val == null)
		{
			return defaultValue;
		}

		return getParameterAsDate (name);
	}

	public List getParameterAsList (String name)
	{
		Object[] arr = getParameterAsArray (name);
		ArrayList l = new ArrayList ();

		for (int i = 0; i < arr.length; i++)
		{
			l.add (arr[i]);
		}

		return l;
	}

	public List getParameterAsList (String name, List defaultValue)
	{
		Object val = getParameter (name);

		if (val == null)
		{
			return defaultValue;
		}

		return getParameterAsList (name);
	}

	public long getParameterAsLong (String name, long defaultValue)
	{
		if (getParameter (name) == null)
		{
			return defaultValue;
		}

		return getParameterAsLong (name);
	}

	public Map getErrors ()
	{
		return validationErrors;
	}

	public double getParameterAsDouble (String name)
	{
		Converter c = ConvertUtils.lookup (java.lang.Double.class);

		return ((Double) c.convert (java.lang.Double.class, getParameterAsString (name))).doubleValue ();
	}

	public float getParameterAsFloat (String name)
	{
		Converter c = ConvertUtils.lookup (java.lang.Float.class);

		return ((Float) c.convert (java.lang.Float.class, getParameterAsString (name))).floatValue ();
	}

	public float getParameterAsFloat (String name, float defaultValue)
	{
		if (getParameter (name) == null)
		{
			return defaultValue;
		}

		return getParameterAsFloat (name);
	}

	public double getParameterAsDouble (String name, double defaultValue)
	{
		if (getParameter (name) == null)
		{
			return defaultValue;
		}

		return getParameterAsDouble (name);
	}

	public long getParameterAsLong (String name)
	{
		Converter c = ConvertUtils.lookup (java.lang.Long.class);

		return ((Long) c.convert (java.lang.Long.class, getParameterAsString (name))).longValue ();
	}

	/**
	 * Add a validation error to the collection of errors in this request. This
	 * method called by a ModelValidator component, not by the Model itself
	 * (normally)
	 */
	public void addError (String name, String message)
	{
		validationErrors.put (name, message);
	}

	private Command validate (Model newModel) throws ModelException, ConfigurationException
	{
		String useValidator = newModel.getConfiguration ().getChild ("validator").getValue (null);

		if (useValidator == null)
		{
			if (myDefaultValidator == null)
			{
				myDefaultValidator = (ModelValidator) getService (ModelValidator.ROLE, "*");
			}

			return myDefaultValidator.validate (this, newModel);
		}

		ModelValidator mv = (ModelValidator) getService (ModelValidator.ROLE, useValidator);

		return mv.validate (this, newModel);
	}

	/**
	 * Release all services retrieved so far
	 */
	public synchronized void releaseServices ()
	{
		svcDelegate.releaseServices ();
	}

	public String getHeader (String key)
	{
		String returnValue = null;

		if (key != null)
		{
			if (headers != null)
			{
				returnValue = (String) headers.get (key);
			}
		}

		return returnValue;
	}

	public Map getHeaders ()
	{
		Map returnValue = null;

		if (headers == null)
		{
			returnValue = new HashMap ();
		}
		else
		{
			returnValue = headers;
		}

		return returnValue;
	}

	public void setHeader (String key, String value)
	{
		if (headers == null)
		{
			headers = new HashMap ();
		}

		if (key != null)
		{
			synchronized (headers)
			{
				headers.put (key, value);
			}
		}
	}

	public void setSource (String source)
	{
		this.source = source;
	}

	public String getSource ()
	{
		return source;
	}

	public void setLocale (Locale locale)
	{
		this.locale = locale;
	}

	public Locale getLocale ()
	{
		return locale;
	}

	/**
	 * @see de.iritgo.aktera.model.ModelRequest#getSpringBean(java.lang.String)
	 */
	public Object getSpringBean (String name) throws ModelException
	{
		try
		{
			return KeelContainer.defaultContainer ().getSpringBean (name);
		}
		catch (Exception x)
		{
			throw new ModelException ("Unable to retrieve spring bean '" + name, x);
		}
	}

	public String getScheme ()
	{
		return scheme;
	}

	public void setScheme (String scheme)
	{
		this.scheme = scheme;
	}

	public String getServerName ()
	{
		return serverName;
	}

	public void setServerName (String serverName)
	{
		this.serverName = serverName;
	}

	public int getServerPort ()
	{
		return serverPort;
	}

	public void setServerPort (int serverPort)
	{
		this.serverPort = serverPort;
	}

	public String getContextPath ()
	{
		return contextPath;
	}

	public void setContextPath (String contextPath)
	{
		this.contextPath = contextPath;
	}

	public String getRequestUrl ()
	{
		return requestUrl;
	}

	public void setRequestUrl (String requestUrl)
	{
		this.requestUrl = requestUrl;
	}

	public String getQueryString ()
	{
		return queryString;
	}

	public void setQueryString (String queryString)
	{
		this.queryString = queryString;
	}

	/**
	 * @see de.iritgo.aktera.model.ModelRequest#getParams()
	 */
	public Map getParams ()
	{
		return params;
	}

	public boolean hasParameter (String name)
	{
		assert name != null;

		return getParameter (name) != null;
	}

	public String getBean ()
	{
		return myBean;
	}

	public void setBean (String bean)
	{
		this.myBean = bean;
	}
}
