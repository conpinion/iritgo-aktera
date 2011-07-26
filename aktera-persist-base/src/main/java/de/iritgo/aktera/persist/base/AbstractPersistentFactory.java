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

package de.iritgo.aktera.persist.base;


import de.iritgo.aktera.authorization.AuthorizationManager;
import de.iritgo.aktera.context.KeelContextualizable;
import de.iritgo.aktera.core.config.KeelConfigurationUtil;
import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.persist.DatabaseType;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.Transaction;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * A PersistentFactory is a class that generates new Persistent objects,
 * Transactions, and Queries
 *
 * @version $Revision$ $Date$
 *
 */
public abstract class AbstractPersistentFactory extends AbstractKeelServiceable implements PersistentFactory,
				Configurable, Initializable, KeelContextualizable, LogEnabled, Poolable, Recyclable, Disposable
{
	protected static Logger log = null;

	/**
	 * The Keel context for getting user env.
	 */
	protected Context keelContext = null;

	/**
	 * The name of the data source we will access for all Persistent objects
	 * created with this factory. Defined with the <dbpool>configuration
	 * element
	 */
	protected String dataSourceName = null;

	/**
	 * The database type service we will use for all of our database-specific
	 * information defined with the <dbtype>element
	 */
	private String dbTypeName = null;

	/**
	 * Data source service itself. Used for all connections from Persistent
	 * objects created from this factory
	 */
	protected DataSourceComponent dataSource = null;

	/**
	 * DatabaseType service we use. Gives us specific information about the
	 * particular database
	 */
	protected DatabaseType databaseType = null;

	/**
	 * Name of this instance of the PersistentFactory
	 */
	protected String myName = null;

	/**
	 * A HashMap of the schema configurations we have available, for rapid
	 * access to the correct schema
	 */
	protected Map schemas = new HashMap ();

	protected Map schemaDescrips = new HashMap ();

	protected Map metas = new HashMap ();

	private boolean configured = false;

	private boolean initialized = false;

	private AuthorizationManager byPassAuthManager = null;

	private Configuration myConfig = null;

	/**
	 * The (optional) name of another persistent factory that should be used
	 * for all accesses to the keel standard security objects (e.g. user, group
	 * and groupmembers)
	 */
	private String security = null;

	protected AuthorizationManager getByPassAuthManager ()
	{
		return byPassAuthManager;
	}

	protected Configuration getConfiguration ()
	{
		return myConfig;
	}

	/**
	 * The only thing that changes when we recycle an instance of a
	 * PersistentFactory is the context, specifying the user who's actually
	 * using this instance of the factory. Leave everything else alone to
	 * re-use next time (until we're "disposed");
	 */
	public void recycle ()
	{
		keelContext = null;
	}

	public void dispose ()
	{
		releaseServices ();
	}

	public final Set getSchemas ()
	{
		return schemas.keySet ();
	}

	public final String getSchemaDescription (String schemaName)
	{
		return (String) schemaDescrips.get (schemaName);
	}

	/**
	 * Create a new persistent object with the specified name
	 */
	public abstract Persistent create (String name) throws PersistenceException;

	public Configuration getSchemaConfiguration (String schemaName)
	{
		assert schemaName != null;

		return (Configuration) schemas.get (schemaName);
	}

	/**
	 * Return a set of names, one for each persistent in the given schema
	 */
	public final Set getPersistents (String schemaName) throws PersistenceException
	{
		HashSet returnSet = new HashSet ();

		try
		{
			Configuration mySchema = (Configuration) schemas.get (schemaName);

			if (mySchema == null)
			{
				throw new PersistenceException ("No such schema '" + schemaName + "' defined for PersistentFactory '"
								+ getName () + "'");
			}

			Configuration[] eachTable = mySchema.getChildren ();

			for (int i = 0; i < eachTable.length; i++)
			{
				Configuration oneTable = eachTable[i];

				if (oneTable.getName ().equals ("persistent"))
				{
					returnSet.add (oneTable.getAttribute ("name"));
				}
			}

			return returnSet;
		}
		catch (ConfigurationException ce)
		{
			throw new PersistenceException (ce);
		}
	}

	protected final PersistentMetaData getMetaData (String persistentName)
		throws PersistenceException, ConfigurationException, ServiceException
	{
		if (metas == null)
		{
			metas = new HashMap ();
		}

		/* See if we have already configured the meta-data for this Persistent */
		PersistentMetaData pd = (PersistentMetaData) metas.get (persistentName);

		if (pd != null)
		{
			return pd;
		}

		/*
		 * Now fish through our own configuration and find the definition of
		 * this Persistent, pass it on
		 */

		/* to the PersistentMetaData to allow it to configure itself */
		StringTokenizer stk = new StringTokenizer (persistentName, ".");

		String firstPart = stk.nextToken ();

		SuperString.assertNotBlank (firstPart, "Schema name must be specified");

		String secondPart = null;

		if (stk.hasMoreTokens ())
		{
			secondPart = stk.nextToken ();
		}

		SuperString.assertNotBlank (secondPart, "Persistent name must be specified, only '" + persistentName
						+ "' found");

		Configuration mySchema = (Configuration) schemas.get (firstPart);

		if (mySchema == null)
		{
			throw new PersistenceException ("No such schema '" + firstPart + "' defined for PersistentFactory '"
							+ getName () + "'");
		}

		Configuration[] eachTable = mySchema.getChildren ();

		if (eachTable.length == 0)
		{
			throw new PersistenceException ("No persistent obejcts defined in schema '" + firstPart + "' for factory '"
							+ getName () + "'");
		}

		for (int i = 0; i < eachTable.length; i++)
		{
			Configuration oneTable = eachTable[i];

			if (oneTable.getName ().equals ("persistent"))
			{
				if (oneTable.getAttribute ("name").equals (secondPart))
				{
					try
					{
						pd = (PersistentMetaData) getService (PersistentMetaData.ROLE);

						pd.setFactory (this);
						pd.setSchemaName (firstPart);
						pd.setDataSource (dataSource);
						pd.setDatabaseType (databaseType);

						if (pd instanceof Serviceable)
						{
							((Serviceable) pd).service (getServiceManager ());
						}

						pd.configurePersistent (oneTable);
						metas.put (persistentName, pd);

						return pd;
					}
					catch (Exception e)
					{
						throw new PersistenceException (PersistentMetaData.ROLE
										+ "Error creating metadata for persistent " + persistentName, e);
					}
				}
			}
		}

		throw new PersistenceException ("No meta-data definition for persistent '" + secondPart + "' in factory '"
						+ getName () + "' in schema '" + firstPart + "'");
	}

	/**
	 * Create a new Persistent which participates in a transaction
	 */
	public final Persistent create (String name, Transaction trx) throws PersistenceException
	{
		Persistent newPersistent = create (name);

		newPersistent.setTransaction (trx);

		return newPersistent;
	}

	public final void configure (Configuration configuration) throws ConfigurationException
	{
		if (configured)
		{
			return;
		}

		myConfig = configuration;

		try
		{
			ConvertUtils.register (new SqlDateConverter (), Date.class);
		}
		catch (NoClassDefFoundError ne)
		{
			log.error ("No class def:", ne);
		}
		catch (Exception e)
		{
			log.error ("Exception registering date converter", e);
		}

		configured = true;

		/*
		 * If we have a "bypass-am" element, this is the configuration name of
		 * an AuthorizationManager instance to be used for access to persistent
		 * objects that allow bypass, if they are not given a specific bypass
		 * authorization manager of their own by the calling application.
		 */
		String byPassAmName = configuration.getChild ("bypass-am").getValue (null);

		if (byPassAmName != null)
		{
			try
			{
				byPassAuthManager = (AuthorizationManager) getService (AuthorizationManager.ROLE, byPassAmName);
			}
			catch (Exception e)
			{
				log.error ("Could not get service " + AuthorizationManager.ROLE + "/" + byPassAmName);
				throw new ConfigurationException (e.getMessage ());
			}
		}
		else
		{
			log.warn ("No default bypass authorization manager specified");
		}

		setName (configuration.getAttribute ("name"));

		setSecurity (configuration.getAttribute ("security", null));

		log.debug ("Configuring persistent factory '" + getName () + "' - security '" + getSecurity () + "'");

		// Obtain a reference to the configured DataSource
		dataSourceName = configuration.getChild ("dbpool").getValue ();
		dbTypeName = configuration.getChild ("dbtype").getValue ();

		Configuration[] check = configuration.getChildren ("schemas");

		if (check.length > 1)
		{
			throw new ConfigurationException ("Only one <schemas> element may appear within "
							+ "a persistent factory configuration element - possible configuration merge problem.");
		}

		/*
		 * Now we read all the "schema" information. This creates the meta-data
		 * for each of our persistent objects
		 */

		/*
		 * It is possible to defined a "cloned" schema, that is, a schema that
		 * should read the schema details from another default-persistent
		 * configuration and configure itself identically.
		 */
		Configuration allSchemas = configuration.getChild ("schemas");

		if (allSchemas == null)
		{
			throw new ConfigurationException ("No schemas defined for PersistentFactory '" + getName () + "'");
		}

		Configuration[] eachSchema = allSchemas.getChildren ();

		if (eachSchema.length == 0)
		{
			throw new ConfigurationException ("No schemas defined for PersistentFactory '" + getName () + "'");
		}

		Configuration oneSchema = null;

		for (int i = 0; i < eachSchema.length; i++)
		{
			oneSchema = eachSchema[i];

			String schemaName = oneSchema.getAttribute ("name");

			if (oneSchema.getChild ("clone", false) != null)
			{
				Configuration cloneConfig = oneSchema.getChild ("clone");

				/* This is a cloned schema, get the schema config element */
				/* from where we're told to clone from, not from here */
				if (oneSchema.getChildren ().length > 1)
				{
					throw new ConfigurationException (
									"A cloned schema should not have other entries, just the clone element. Found "
													+ KeelConfigurationUtil.list (oneSchema, "schema"));
				}

				String sourceFactory = cloneConfig.getAttribute ("factory");
				String sourceSchema = cloneConfig.getAttribute ("schema");

				Configuration origSchema;

				try
				{
					PersistentFactory source = (PersistentFactory) getService (PersistentFactory.ROLE, sourceFactory);

					origSchema = source.getSchemaConfiguration (sourceSchema);
				}
				catch (ServiceException e1)
				{
					throw new ConfigurationException ("Error getting top-level configuration from context", e1);
				}

				if (origSchema == null)
				{
					throw new ConfigurationException ("Schema " + schemaName + " in factory " + getName ()
									+ " specified it was a clone of schema " + sourceSchema + " in factory "
									+ sourceFactory + ", but that source was not found");
				}
			}

			schemas.put (schemaName, oneSchema);
			schemaDescrips.put (schemaName, oneSchema.getAttribute ("descrip", schemaName));
		}

		if (schemas.size () == 0)
		{
			throw new ConfigurationException ("No schemas specified for PersistentFactory '" + getName () + "'");
		}
	}

	public final PersistentFactory getFactory (String newName) throws PersistenceException
	{
		if (newName == null)
		{
			throw new PersistenceException ("No factory name specified");
		}

		try
		{
			return (PersistentFactory) getService (PersistentFactory.ROLE, newName, getKeelContext ());
		}
		catch (ServiceException e)
		{
			throw new PersistenceException (e);
		}
	}

	public void initialize () throws PersistenceException
	{
		if (initialized)
		{
			return;
		}

		// Get a reference to a data source
		try
		{
			dataSource = (DataSourceComponent) getService (DataSourceComponent.ROLE, dataSourceName);
		}
		catch (ServiceException e)
		{
			throw new PersistenceException (e);
		}

		try
		{
			databaseType = (DatabaseType) getService (DatabaseType.ROLE, dbTypeName);
		}
		catch (ServiceException e)
		{
			throw new PersistenceException (e);
		}

		// Get a reference to a database type
		databaseType.setDataSource (dataSource);
		initialized = true;
	}

	/**
	 * Begin a new transaction and pass the transaction back to the caller.
	 * Used when we want a number of database operations to be processed in a
	 * single transaction.
	 */
	public abstract Transaction begin () throws PersistenceException;

	protected final void setName (String newName)
	{
		myName = newName;
	}

	public final String getName ()
	{
		return myName;
	}

	public final void enableLogging (Logger newLog)
	{
		log = newLog;
	}

	public abstract void createTables (String schema) throws PersistenceException;

	protected final void createRecords (Configuration config, String persistentName, PersistentMetaData pmd)
		throws ConfigurationException
	{
		if (log.isDebugEnabled ())
		{
			log.debug ("Populating default data for '" + persistentName + "'");
		}

		Persistent p = null;

		try
		{
			p = create (persistentName);

			Persistent checkExisting = create (persistentName);
			boolean nokeys = true;

			Configuration[] children = config.getChildren ();

			// BUEROBYTE: Sort records by attribute 'id' (if present)
			Arrays.sort (children, new Comparator ()
			{
				public int compare (Object o1, Object o2)
				{
					Configuration c1 = (Configuration) o1;
					Configuration c2 = (Configuration) o2;

					int id1 = 0;
					int id2 = 0;

					try
					{
						id1 = c1.getAttributeAsInteger ("id");
					}
					catch (ConfigurationException x)
					{
					}

					try
					{
						id2 = c2.getAttributeAsInteger ("id");
					}
					catch (ConfigurationException x)
					{
					}

					return id1 - id2;
				}
			});

			// BUEROBYTE
			Configuration oneChild = null;

			for (int j = 0; j < children.length; j++)
			{
				oneChild = children[j];

				if (! oneChild.getName ().equals ("record"))
				{
					throw new ConfigurationException ("default-data element must contain only record elements");
				}

				p.clear ();
				checkExisting.clear ();

				String oneFieldName = null;

				for (Iterator names = pmd.getFieldNames ().iterator (); names.hasNext ();)
				{
					oneFieldName = (String) names.next ();

					// BUEROBYTE: Sort records by attribute 'id' (if present)
					if (! pmd.getKeyFieldNames ().contains (oneFieldName) && "id".equals (oneFieldName))
					{
						continue;
					}

					// BUEROBYTE
					String attribValue = null;

					if (pmd.getKeyFieldNames ().contains (oneFieldName))
					{
						if (pmd.isAutoIncremented (oneFieldName))
						{
							attribValue = oneChild.getAttribute (oneFieldName, null);
						}
						else
						{
							attribValue = oneChild.getAttribute (oneFieldName);
						}

						if (attribValue != null)
						{
							checkExisting.setField (oneFieldName, processedAttribValue (attribValue));
							nokeys = false;
						}
					}
					else
					{
						attribValue = oneChild.getAttribute (oneFieldName, null);
					}

					p.setField (oneFieldName, processedAttribValue (attribValue));
				}

				if (nokeys)
				{
					if (! p.find ())
					{
						if (log.isDebugEnabled ())
						{
							log.debug ("No existing record for " + p.toString () + ", adding");
						}

						p.add ();
					}
					else
					{
						if (log.isDebugEnabled ())
						{
							log.debug ("Existing record for " + p.toString ());
						}
					}
				}
				else if (! checkExisting.find ())
				{
					if (log.isDebugEnabled ())
					{
						log.debug ("No existing record with key " + checkExisting.toString () + "', adding");
					}

					p.add ();
				}
				else
				{
					if (log.isDebugEnabled ())
					{
						log.debug ("Existing record with key " + checkExisting.toString ());
					}
				}
			}
		}
		catch (PersistenceException pe)
		{
			String msg = "Persistence exception while creating default data for '" + persistentName + "'";

			if (p != null)
			{
				msg = msg + ", adding " + p.toString ();
			}

			throw new ConfigurationException (msg, pe);
		}
	}

	/**
	 * @param attribValue
	 * @return
	 */
	private String processedAttribValue (String attribValue) throws PersistenceException
	{
		String retValue = attribValue;
		String attrib = null;

		if (attribValue != null && attribValue.length () > 1 && attribValue.startsWith ("$")
						&& Character.isLetter (attribValue.charAt (1)))
		{
			//Get rid of the initial "$"
			attrib = attribValue.substring (1);

			//Separate the field and lookup portions
			StringTokenizer tok = new StringTokenizer (attrib, ":");

			if (tok.countTokens () != 2)
			{
				throw new PersistenceException ("Format error (need field:lookup) in indirect default-data value: "
								+ attrib);
			}

			String fieldStr = tok.nextToken ();
			String lookupStr = tok.nextToken ();

			//Extract the schema, table, field names
			tok = new StringTokenizer (fieldStr, ".");

			if (tok.countTokens () != 3)
			{
				throw new PersistenceException (
								"Format error (need $schema.table.field) in indirect default-data value: " + attrib);
			}

			String schema = tok.nextToken ();
			String table = tok.nextToken ();
			String field = tok.nextToken ();

			//Extract the value from the table
			Persistent p = create (schema + "." + table);

			tok = new StringTokenizer (lookupStr, "|");

			String f = null;
			String v = null;
			StringTokenizer t = null;

			for (int i = 0; i < tok.countTokens (); i++)
			{
				t = new StringTokenizer (tok.nextToken (), "=");
				f = t.nextToken ();
				v = t.nextToken ();
				p.setField (f, v);
			}

			if (p.find ())
			{
				retValue = p.getFieldString (field);
			}
			else
			{
				throw new PersistenceException ("Could not lookup indirect default-data value: " + retValue);
			}
		}

		return retValue;
	}

	public abstract void createTables () throws PersistenceException;

	public final void addInputs (ModelResponse res, Persistent p) throws PersistenceException, ModelException
	{
		PersistentMetaData pmd = p.getMetaData ();
		String oneFieldName = null;

		for (Iterator i = pmd.getFieldNames ().iterator (); i.hasNext ();)
		{
			oneFieldName = (String) i.next ();

			Input oneInput = res.createInput (oneFieldName);

			oneInput.setLabel (pmd.getDescription (oneFieldName));

			if (pmd.isMultiValued (oneFieldName))
			{
				oneInput.setValidValues (p.getValidValues (oneFieldName));
			}

			oneInput.setDefaultValue (p.getFieldString (oneFieldName));
			res.add (oneInput);
		}
	}

	public final void addOutputs (ModelResponse res, Persistent p) throws PersistenceException, ModelException
	{
		PersistentMetaData pmd = p.getMetaData ();
		String oneFieldName = null;

		for (Iterator i = pmd.getFieldNames ().iterator (); i.hasNext ();)
		{
			oneFieldName = (String) i.next ();

			Output oneOutput = res.createOutput (oneFieldName);

			oneOutput.setContent (p.getFieldString (oneFieldName));
			res.add (oneOutput);
		}
	}

	/**
	 * Add an entire set of Persistents to a model reponse as Outputs, not just
	 * one.
	 *
	 * @param res
	 *            The current model response being prepared for the UI
	 * @param p
	 *            A persistent that has been used in a "query" (the method, not
	 *            to be confused with the query service, which is something
	 *            else)
	 * @param outputName
	 *            The name of the top-level output inside which all the other
	 *            outputs will be nested.
	 * @throws PersistenceException
	 * @throws ModelException
	 *             @note THIS IS DUPLICATED IN
	 *             SVC-PERSIST-HIBERNATE/HibernatePersistentFactory
	 */
	public final void addQuery (final ModelResponse res, final Persistent p, final String outputName)
		throws PersistenceException, ModelException
	{
		final PersistentMetaData pmd = p.getMetaData ();
		final Set fieldNames = pmd.getFieldNames ();

		//TODO: Set this up in such a manner that the key fields are first.
		final String[] fields = (String[]) fieldNames.toArray (new String[fieldNames.size ()]);

		addQuery (res, p, outputName, fields);
	}

	/**
	 * Convenience method to add a List of nested Output ResponseElements to
	 * the specified ModelRepsonse. Similiar to addOutputs, for the individiual
	 * rows. Each row is added to the "outer" Output as row # (useful for
	 * logic:iterate). Rows are added to the Output Response Element specified
	 * by outputName.
	 *
	 * @param res
	 *            A ModelResponse object
	 * @param p
	 *            A persistent to be used to create Outputs
	 * @param outputName
	 *            Name of Output ResponseElement to add the List to.
	 * @param fieldNames
	 *            The fields to be added to the Output. Fields will be added in
	 *            the same order as in the list. As an added bonus, the records
	 *            will be sorted on the order of the fields in the list.
	 * @throws PersistenceException
	 *             If an exception occurs retrieving the fields
	 * @throws ModelException
	 *             If an exception occurs creating the outputs.
	 * @see #addOutputs(ModelReponse, Persistent)
	 */
	public final void addQuery (final ModelResponse res, final Persistent p, final String outputName,
					final String[] fieldNames) throws PersistenceException, ModelException
	{
		final StringBuffer fieldList = new StringBuffer (fieldNames.length * 32);

		//Best guess at an average length.
		for (int idx = 0; idx < fieldNames.length; idx++)
		{
			if (idx > 0)
			{
				fieldList.append (',');
			} //end if

			fieldList.append (fieldNames[idx]);
		} //end for

		final List pValues = p.query (fieldList.toString ());
		final Output oValues = res.createOutput (outputName);
		int idx = 0;

		for (final Iterator it = pValues.iterator (); it.hasNext (); idx++)
		{
			final Persistent pCurrent = (Persistent) it.next ();

			log.debug ("Processing Row: " + idx);

			final Output output = res.createOutput (Integer.toString (idx));

			//NOTE: Keel Clients don't know what a PersistentDynaBean is, so
			// can't
			//deserialize this Output
			//            output.setContent(pCurrent.getBean());
			addPersistentToOutput (fieldNames, pCurrent, output, res);
			oValues.add (output);
		}

		res.add (oValues);
	}

	/**
	 * Adds the requested values of the Persistent to the given Output.
	 *
	 * @param fields
	 *            The names of the Database fields being added
	 * @param p
	 *            The Peristent with the values being read
	 * @param o
	 *            The Output to add the values to
	 * @param response
	 *            The ModelResponse being used to generate the Outputs
	 * @throws ModelException
	 * @throws PersistenceException
	 *             @note THIS IS DUPLICATED IN
	 *             SVC-PERSIST-HIBERNATE/HibernatePersistentFactory
	 */
	protected final void addPersistentToOutput (final String[] fields, final Persistent p, final Output o,
					final ModelResponse response) throws ModelException, PersistenceException
	{
		for (int idx = 0; idx < fields.length; idx++)
		{
			final Object obj = p.getField (fields[idx]);
			final String value = obj == null ? "" : obj.toString ();

			if (log.isDebugEnabled ())
			{
				log.debug ("Field/'Value':" + fields[idx] + "/'" + value + '\'');
			}

			o.add (response.createOutput (fields[idx], value));
		}
	} //end addOutputList

	private void setSecurity (String newSec)
	{
		security = newSec;
	}

	public final String getSecurity ()
	{
		if (security == null)
		{
			return getName ();
		}

		return security;
	}

	public final String getInstanceIdentifier ()
	{
		return myName;
	}

	public abstract void createIndices () throws PersistenceException;

	public abstract void createIndices (String schema) throws PersistenceException;

	public abstract void dropIndices () throws PersistenceException;

	public abstract void dropIndices (String schema) throws PersistenceException;

	/**
	 * @see de.iritgo.aktera.context.KeelContextualizable#setKeelContext(org.apache.avalon.framework.context.Context)
	 */
	public final void setKeelContext (Context keelContext) throws ContextException
	{
		this.keelContext = keelContext;
	}

	/**
	 * @see de.iritgo.aktera.context.KeelContextualizable#getKeelContext()
	 */
	public final Context getKeelContext ()
	{
		return keelContext;
	}

	protected boolean isConfigured ()
	{
		return configured;
	}
}
