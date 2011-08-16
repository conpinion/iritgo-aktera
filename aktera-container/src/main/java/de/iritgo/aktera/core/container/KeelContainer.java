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

package de.iritgo.aktera.core.container;


import de.iritgo.aktera.util.string.SuperString;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.fortress.util.ContextManagerConstants;
import org.apache.avalon.fortress.util.OverridableContext;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This is the top-level container for Keel Framework services. This is
 * instantiated by the KeelServer class.
 *
 * @version        $Revision: 1.2 $        $Date: 2006/10/02 00:15:52 $
 * @author Shash Chatterjee
 * Created on Oct 17, 2002
 */
public class KeelContainer extends DefaultContainer implements Container
{
	private static Container defaultContainer;

	private Configuration m_configuration;

	private KeelServiceableDelegate svcDelegate;

	private AbstractApplicationContext springApplicationContext;

	private String[] springFileConfigLocations;

	private String[] springClasspathConfigLocations;

	private String[] hibernateConfigLocations;

	private List<Runnable> shutdownHandlers = new LinkedList<Runnable> ();

	private LoggerManager loggerManager;

	private Configuration logConfig;

	public Object getService (String role, String hint, Context ctx) throws ServiceException
	{
		return svcDelegate.getService (role, hint, ctx);
	}

	public Object getService (String role, String hint) throws ServiceException
	{
		return svcDelegate.getService (role, hint);
	}

	public Object getService (String role) throws ServiceException
	{
		return svcDelegate.getService (role);
	}

	public Configuration getSysConfig ()
	{
		return m_configuration;
	}

	public void release (Object obj)
	{
		if (obj != null)
		{
			synchronized (svcDelegate)
			{
				svcDelegate.releaseService (obj);
			}
		}
	}

	/**
	 * @deprecated Use release(Object obj) instead
	 */
	public void release (String role, Object obj)
	{
		synchronized (svcDelegate)
		{
			svcDelegate.releaseService (obj);
		}
	}

	/**
	 * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
	 */
	public void contextualize (Context context) throws ContextException
	{
		//The context passed here is read-only....
		//Wrap it in a writable context we can make read-only later
		super.contextualize (new OverridableContext (context));

		if (defaultContainer == null)
		{
			defaultContainer = this;
		}

		springFileConfigLocations = (String[]) context.get ("keel.config.spring.file");
		springClasspathConfigLocations = (String[]) context.get ("keel.config.spring.classpath");
		hibernateConfigLocations = (String[]) context.get ("keel.config.hibernate");
		loggerManager = (LoggerManager) context.get ("keel.loggerManager");
		logConfig = (Configuration) context.get ("keel.config.log");
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure (Configuration config) throws ConfigurationException
	{
		m_configuration = null;

		Configuration newConfig = handleConditionalConfigs (config);

		try
		{
			super.configure (newConfig);
			m_configuration = newConfig;
		}
		catch (ConfigurationException ce)
		{
			System.err.println ("Configuration Exception:");
			ce.printStackTrace (System.err);
			throw ce;
		}

		System.err.println ("[KeelContainer] Starting Spring container");

		try
		{
			FileSystemXmlApplicationContext fsxac = (FileSystemXmlApplicationContext) (new FileSystemXmlApplicationContext (
							springFileConfigLocations));

			springApplicationContext = new ClassPathXmlApplicationContext (springClasspathConfigLocations, fsxac);
		}
		catch (Exception x)
		{
			System.err.println ("[KeelContainer] Unable to create Spring container: " + x);
		}
	}

	/**
	 * This method "prunes" top-level configuration entries (e.g. entries
	 * immediately below the "keel" top element in the *-system.xconf file(s).
	 * It first reads all the children of an element called "modules". Then
	 * it examines each top-level element and looks for an attribute named "if"
	 * If this attribute exists, it's value must exist in the list of names
	 * in the module list, or that element is "pruned" from the tree.
	 */
	private Configuration handleConditionalConfigs (Configuration orig) throws ConfigurationException
	{
		DefaultConfiguration newConfig = new DefaultConfiguration (orig.getName (), orig.getLocation (), orig
						.getNamespace (), "");

		newConfig.addAllAttributes (orig);

		Configuration[] children = orig.getChildren ("module");
		Configuration oneChild = null;
		Set moduleList = new HashSet ();

		for (int i = 0; i < children.length; i++)
		{
			oneChild = children[i];
			moduleList.add (oneChild.getValue ());
		}

		if (moduleList.size () == 0)
		{
			System.err.println ("WARNING: There were no modules listed in configuration");
		}

		Configuration[] allChildren = orig.getChildren ();
		Configuration aChild = null;

		for (int j = 0; j < allChildren.length; j++)
		{
			aChild = allChildren[j];

			String condition = aChild.getAttribute ("if", null);

			if (condition == null)
			{
				newConfig.addChild (aChild);
			}
			else
			{
				if (moduleList.contains (condition))
				{
					newConfig.addChild (aChild);
				}
				else
				{
					System.err.println ("[KeelContainer] Configuration " + aChild.getName () + " skipped");
				} /* else */
			} /* else */
		} /* for */
		return newConfig;
	}

	/**
	 * Root ServiceManager.  The Container may choose to have it's
	 * ServiceManager delegate to the root manager, or it may choose to be
	 * entirely self contained.
	 */
	public void service (final ServiceManager parent) throws ServiceException
	{
		super.service (parent);

		//m_metaManager = new KeelMetaInfoManager();
		m_serviceManager = new KeelServiceManager (this, parent);

		if (m_context instanceof DefaultContext)
		{
			DefaultContext context = (DefaultContext) m_context;

			context.put (ContextManagerConstants.SERVICE_MANAGER, m_serviceManager);
			context.makeReadOnly ();
		}

		svcDelegate = new KeelServiceableDelegate (m_serviceManager);
	}

	public void dispose ()
	{
		for (Runnable shutdownHandler : shutdownHandlers)
		{
			try
			{
				shutdownHandler.run ();
			}
			catch (Exception x)
			{
				System.err.println ("[KeelContainer] Shutdown handler error: " + x);
			}
		}

		if (springApplicationContext != null)
		{
			System.err.println ("[KeelContainer] Destroying Spring container");

			springApplicationContext.destroy ();
		}

		try
		{
			// Release all serveis acquired for service manager
			svcDelegate.releaseServices ();
			// Release all components acquired from container
			super.dispose ();
		}
		catch (NullPointerException npe)
		{
			npe.printStackTrace (System.err);
		}
		catch (Throwable t)
		{
			t.printStackTrace (System.err);
		}
	}

	private void dumpConfigInfo (Configuration config) throws ConfigurationException
	{
		System.err.println ("-----------------------------------");
		System.err.println ("Configuration information dump:");

		ClassLoader loader = this.getClass ().getClassLoader ();

		System.err.println ("Known services in classloader " + loader.toString () + ":");

		int serviceCount = 0;

		try
		{
			Enumeration enumeration = null;

			if (loader instanceof URLClassLoader)
			{
				URLClassLoader ul = (URLClassLoader) loader;

				enumeration = cleanUrlList (ul.findResources ("services.list"));
			}
			else
			{
				enumeration = cleanUrlList (loader.getResources ("services.list"));
			}

			while (enumeration.hasMoreElements ())
			{
				serviceCount++;

				URL serviceList = (URL) enumeration.nextElement ();

				processServiceList (serviceList, loader);
			}

			if (serviceCount == 0)
			{
				System.err
								.println ("WARNING! There were no services listed, or no services.list resources found in the classpath");
				System.err
								.println ("This indicates a bad build process, unless you are using roles.xconf exclusively,");
				System.err.println ("Or a problem with the classloader.getResources() in this implementation.");

				if (loader instanceof URLClassLoader)
				{
					URLClassLoader ul = (URLClassLoader) loader;

					try
					{
						Enumeration e2 = ul.findResources ("services.list");

						if (e2.hasMoreElements ())
						{
							System.err
											.println ("NOTE: findResources found services.list entries - so this is probably a classloader issue");
						}
					}
					catch (Exception e)
					{
						e.printStackTrace (System.err);
					}
				}
			}

			//String serviceName = (String) enumeration.nextElement();
			//System.err.println("Service name: '" + serviceName + "'");
			Configuration[] allConfigs = config.getChildren ();

			System.err.println ("There are " + allConfigs.length + " elements to verify");

			for (int i = 0; i < allConfigs.length; i++)
			{
				Configuration oneConfig = allConfigs[i];

				System.err.println ("Element '" + oneConfig.getName () + "'");
			}
		}
		catch (Exception ee)
		{
			ee.printStackTrace (System.err);
			throw new ConfigurationException ("Unable to verify configuration");
		}
	}

	private void metaFailed () throws ConfigurationException
	{
		System.err
						.println ("This probably means the meta collection process did not complete successfully on this application");
		System.err.println ("Try doing an 'ant clean' (on this application) and a new assemble-deploy");
		throw new ConfigurationException ("Can't verify configuration");
	}

	private void processServiceList (URL serviceList, ClassLoader loader) throws Exception
	{
		System.err.println ("Resource '" + serviceList.toString () + "' contains these services:");

		InputStream serviceListStream = serviceList.openStream ();
		BufferedReader reader = new BufferedReader (new InputStreamReader (serviceListStream));

		List entryList = new ArrayList ();
		String oneEntry = reader.readLine ();

		while (oneEntry != null)
		{
			entryList.add (oneEntry);

			oneEntry = reader.readLine ();
		}

		reader.close ();
		serviceListStream.close ();

		for (Iterator i = entryList.iterator (); i.hasNext ();)
		{
			processOneServiceEntry ((String) i.next (), serviceList, loader);
		}
	}

	private void processOneServiceEntry (String oneEntry, URL serviceList, ClassLoader loader) throws Exception
	{
		System.err.println ("\tRole:" + oneEntry);

		/* For this entry, there should be a META-INF/services/xxx" */
		/* file which lists the implementations of this service in this jar */
		StringTokenizer stk = new StringTokenizer (serviceList.toString (), "!");
		String jarFile = stk.nextToken ();

		//System.err.println("Jar file is " + jarFile);
		URL theRightServiceEntry = null;
		Enumeration eachServicesEntry = null;

		if (loader instanceof URLClassLoader)
		{
			URLClassLoader ul = (URLClassLoader) loader;

			eachServicesEntry = ul.findResources ("META-INF/services/" + oneEntry);
		}
		else
		{
			eachServicesEntry = cleanUrlList (loader.getResources ("META-INF/services/" + oneEntry));
		}

		while (eachServicesEntry.hasMoreElements ())
		{
			URL thisServiceEntry = (URL) eachServicesEntry.nextElement ();
			StringTokenizer stk2 = new StringTokenizer (thisServiceEntry.toString (), "!");
			String thisJar = stk2.nextToken ();

			if (thisJar.equals (jarFile))
			{
				/* We've found the right one */
				theRightServiceEntry = thisServiceEntry;
			}
		}

		if (theRightServiceEntry == null)
		{
			/* Check roles.xconf */
			System.err.println ("WARNING: No such file as /META-INF/services/" + oneEntry + " in " + jarFile
							+ ". This component's role implementations must be defined in roles.xconf");
		}
		else
		{
			processImplementationList (theRightServiceEntry, jarFile);
		}
	}

	private void processImplementationList (URL theRightServiceEntry, String jarFile) throws Exception
	{
		/* Now read that list */
		InputStream serviceEntryStream = theRightServiceEntry.openStream ();
		BufferedReader reader2 = new BufferedReader (new InputStreamReader (serviceEntryStream));
		String oneImplClass = reader2.readLine ();

		while (oneImplClass != null)
		{
			System.err.println ("\t\t\tClass:" + oneImplClass);

			/* Now verify that the meta-info for that class can be located (e.g. the .meta file resource) */
			String metaName = SuperString.replace (oneImplClass, ".", "/") + ".meta";
			URL metaUrl = this.getClass ().getClassLoader ().getResource (metaName);

			if (metaUrl == null)
			{
				System.err.println ("FATAL: No such file as " + metaName + " in jar file " + jarFile);
				metaFailed ();
			}

			//BufferedReader metaReader =
			//	new BufferedReader(new InputStreamReader(metaUrl.openStream()));
			/* Now read the meta file */
			Properties p = new Properties ();
			InputStream metaUrlStream = metaUrl.openStream ();

			p.load (metaUrlStream);
			metaUrlStream.close ();
			System.err.print ("\t\t\tProperties list for " + metaName + " [");

			for (Iterator i = p.keySet ().iterator (); i.hasNext ();)
			{
				String key = (String) i.next ();

				System.err.print (key + "=" + p.getProperty (key) + ",");
			}

			System.err.println ("]");

			/* Now verify that that class can be found */
			this.getClass ().getClassLoader ().loadClass (oneImplClass);

			/* Keep a list of all mappings between short names/ids and classes */
			oneImplClass = reader2.readLine ();
		}

		reader2.close ();
		serviceEntryStream.close ();
	}

	public Enumeration cleanUrlList (Enumeration originalURLs)
	{
		Vector v = new Vector ();
		int origSize = 0;
		int valids = 0;
		ClassLoader l = getClass ().getClassLoader ();

		if (l instanceof URLClassLoader)
		{
			URLClassLoader ul = (URLClassLoader) l;
			URL[] validUrls = ul.getURLs ();

			if (validUrls.length == 0)
			{
				return originalURLs;
			}

			while (originalURLs.hasMoreElements ())
			{
				origSize++;

				URL oneURL = (URL) originalURLs.nextElement ();
				StringTokenizer stk = new StringTokenizer (oneURL.getFile (), "!");
				String checkUrl = stk.nextToken ();

				for (int i = 0; i < validUrls.length; i++)
				{
					if (validUrls[i].toString ().equals (checkUrl))
					{
						v.addElement (oneURL);
						valids++;
					}
				}
			}
		}
		else
		{
			throw new IllegalArgumentException ("Using a " + l.getClass ().getName ());
		}

		return v.elements ();
	}

	/**
	 * @see de.iritgo.aktera.core.container.Container#getSystemConfig()
	 */
	public Configuration getSystemConfig () throws ConfigurationException
	{
		return m_configuration;
	}

	/**
	 * Get the default container. This will be the first initialized container.
	 * Since we use only exactly one container, we can be shure to always get
	 * the same container from this method.
	 */
	public static Container defaultContainer ()
	{
		return defaultContainer;
	}

	/**
	 * Get a Spring bean from the Spring container.
	 *
	 * @param name The name of the Spring bean.
	 * @return The Spring bean.
	 * @throws BeansException If the bean cannot be retrieved.
	 */
	public Object getSpringBean (String name) throws BeansException
	{
		if (springApplicationContext != null)
		{
			return springApplicationContext.getBean (name);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return
	 */
	public ConfigurableBeanFactory getSpringBeanFactory ()
	{
		return springApplicationContext.getBeanFactory ();
	}

	public String[] getHibernateConfigLocations ()
	{
		return hibernateConfigLocations;
	}

	public void addShutdownHandler (Runnable shutdownHandler)
	{
		shutdownHandlers.add (shutdownHandler);
	}

	public Logger getLogger (String category)
	{
		return loggerManager.getLoggerForCategory (category);
	}

	public void setLogLevel (String logLevel)
	{
		for (Configuration categoryConfigs : logConfig.getChild ("categories").getChildren ("category"))
		{
			setLogLevel (logLevel, categoryConfigs.getAttribute ("name", ""));
		}
	}

	public void setLogLevel (String logLevel, String category)
	{
		if (StringTools.isTrimEmpty (category) || category.startsWith ("system") || category.startsWith ("keel")
						|| category.startsWith ("console"))
		{
			return;
		}

		Logger logger = loggerManager.getLoggerForCategory (category);

		if (logger != null && logger.getClass ().equals (LogKitLogger.class))
		{
			for (Field field : LogKitLogger.class.getDeclaredFields ())
			{
				if ("m_logger".equals (field.getName ()))
				{
					try
					{
						field.setAccessible (true);

						Object apacheLogger = field.get (logger);

						if (apacheLogger != null && apacheLogger.getClass ().equals (org.apache.log.Logger.class))
						{
							((org.apache.log.Logger) apacheLogger).setPriority (org.apache.log.Priority
											.getPriorityForName (logLevel));
						}
					}
					catch (IllegalArgumentException x)
					{
					}
					catch (IllegalAccessException x)
					{
					}
				}
			}
		}
	}
}
