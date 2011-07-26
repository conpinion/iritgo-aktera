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

package de.iritgo.aktera.persist.defaultpersist;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.Transaction;
import de.iritgo.aktera.persist.base.AbstractPersistentFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * A PersistentFactory is a class that generates new Persistent
 * objects, Transactions, and Queries
 *
 *  @version                 $Revision$ $Date$
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.PersistentFactory
 * @x-avalon.info name=default-persistent
 * @x-avalon.lifestyle type=singleton
 *
 */
public class DefaultPersistentFactory extends AbstractPersistentFactory
{
	public DefaultPersistentFactory ()
	{
		super ();
	}

	/**
	 * Create a new persistent object with the specified name
	 */
	public Persistent create (String name) throws PersistenceException
	{
		if (! isConfigured ())
		{
			throw new PersistenceException ("Factory not configured!");
		}

		DefaultPersistent newPersistent = null;

		if (name.indexOf (".") < 0)
		{
			throw new PersistenceException ("Name of Persistent must be of the form 'schema.name'" + ":'" + name + "'");
		}

		try
		{
			PersistentMetaData pmd = getMetaData (name);

			/**
			 * If this persistent is not instantiated using a custom class...
			 */
			if (pmd.getClassName () == null)
			{
				if (pmd.isRowSecurable ())
				{
					newPersistent = new RowSecurablePersistent ();
				}
				else
				{
					newPersistent = new DefaultPersistent ();
				}

				/* We pass our persistent's the same log as the factory... */
				newPersistent.setLogger (log);

				/* And a reference back to their factory, so they can */
				/* access getService, etc */
				newPersistent.setFactory (this);
				newPersistent.setMetaData (pmd);
				newPersistent.setAuthorizationManager (pmd.getAuthManager ());

				/* If auth manager bypass is allowed at all for this persistent, */
				/* hand it the default bypass manager */
				if (pmd.isAuthManagerBypassAllowed ())
				{
					newPersistent.setBypassAuthorizationManager (getByPassAuthManager ());
				}

				return newPersistent;
			}
			else
			{
				Class c = Class.forName (pmd.getClassName ());
				Object o = c.newInstance ();

				/* If the class we instantiated extends Persistent... */
				if (o instanceof Persistent)
				{
					Persistent p = (Persistent) c.newInstance ();

					p.setMetaData (pmd);

					return p;
				}
				else
				{
					/* If it does not, then "wrap" it in a persistent */
					DefaultPersistent np = null;

					if (pmd.isRowSecurable ())
					{
						np = new RowSecurablePersistent ();
					}
					else
					{
						np = new DefaultPersistent ();
					}

					/* We pass our persistent's the same log as the factory... */
					np.setLogger (log);

					/* And a reference back to their factory, so they can */
					/* access getService, etc */
					np.setFactory (this);
					np.setMetaData (pmd);
					np.setAuthorizationManager (pmd.getAuthManager ());

					/* If auth manager bypass is allowed at all for this persistent, */
					/* hand it the default bypass manager */
					if (pmd.isAuthManagerBypassAllowed ())
					{
						np.setBypassAuthorizationManager (getByPassAuthManager ());
					}

					np.setBean (o);

					return np;
				}
			}
		}
		catch (Exception ce)
		{
			throw new PersistenceException (ce);
		}
	}

	/**
	 * Begin a new transaction and pass the transaction back to the caller. Used when we want a number of database
	 * operations to be processed in a single transaction.
	 */
	public Transaction begin () throws PersistenceException
	{
		DefaultTransaction newTrx = new DefaultTransaction ();

		newTrx.setDataSource (dataSource);
		newTrx.setSupportsTransactions (databaseType.supportsTransactions ());
		newTrx.begin ();

		return newTrx;
	}

	public void createTables (String schema) throws PersistenceException
	{
		boolean securityBypass = false;

		UserEnvironment ue = null;

		if (keelContext != null)
		{
			try
			{
				ue = (UserEnvironment) keelContext.get (UserEnvironment.CONTEXT_KEY);
			}
			catch (ContextException e)
			{
				// Thgis can happen...but we handle this velow with the bykk check for ue
			}
		}

		if (ue == null)
		{
			securityBypass = true;

			/* if we have no context, we will be unable */
			/* to insert the default data when */
			/* creating tables */
			TempUserEnvironment te = new TempUserEnvironment ();

			te.setUid (0);
			te.setLoginName ("root");

			ArrayList gl = new ArrayList ();

			gl.add ("root");
			te.setGroups (gl);

			DefaultContext dc = new DefaultContext ();

			dc.put (UserEnvironment.CONTEXT_KEY, te);
			keelContext = dc;
		}

		try
		{
			/* Get the appropriate schema object */
			Configuration oneSchema = (Configuration) schemas.get (schema);

			if (oneSchema == null)
			{
				throw new PersistenceException ("No such schema '" + schema + "' defined in Persistent Factory '"
								+ getName () + "'");
			}

			Configuration[] eachPersistent = oneSchema.getChildren ();

			for (int i = 0; i < eachPersistent.length; i++)
			{
				Configuration onePersistent = eachPersistent[i];
				String persistentName = onePersistent.getAttribute ("name");
				PersistentMetaData pmd = getMetaData (schema + "." + persistentName);

				databaseType.createTable (pmd, dataSource);

				/* Now look for default data */
				Configuration[] children = eachPersistent[i].getChildren ();
				Configuration oneChild = null;

				for (int j = 0; j < children.length; j++)
				{
					oneChild = children[j];

					if (oneChild.getName ().equals ("default-data"))
					{
						/* Create the default data for each record */
						createRecords (oneChild, schema + "." + persistentName, pmd);
					}
				}
			}
		}
		catch (Exception ce)
		{
			throw new PersistenceException (ce);
		}
		finally
		{
			if (securityBypass)
			{
				keelContext = null;
			}
		}
	}

	public void createTables () throws PersistenceException
	{
		for (Iterator i = schemas.keySet ().iterator (); i.hasNext ();)
		{
			String oneSchema = (String) i.next ();

			createTables (oneSchema);
		}
	}

	public void createIndices () throws PersistenceException
	{
		for (Iterator i = schemas.keySet ().iterator (); i.hasNext ();)
		{
			String oneSchema = (String) i.next ();

			createIndices (oneSchema);
		}
	}

	public void createIndices (String schema) throws PersistenceException
	{
		try
		{
			/* Get the appropriate schema object */
			Configuration oneSchema = (Configuration) schemas.get (schema);

			if (oneSchema == null)
			{
				throw new PersistenceException ("No such schema '" + schema + "' defined in Persistent Factory '"
								+ getName () + "'");
			}

			Configuration[] eachPersistent = oneSchema.getChildren ();

			for (int i = 0; i < eachPersistent.length; i++)
			{
				Configuration onePersistent = eachPersistent[i];
				String persistentName = onePersistent.getAttribute ("name");
				PersistentMetaData pmd = getMetaData (schema + "." + persistentName);

				databaseType.createIndices (pmd, dataSource);
			}
		}
		catch (Exception ce)
		{
			throw new PersistenceException (ce);
		}
	}

	public void dropIndices () throws PersistenceException
	{
		for (Iterator i = schemas.keySet ().iterator (); i.hasNext ();)
		{
			String oneSchema = (String) i.next ();

			dropIndices (oneSchema);
		}
	}

	public void dropIndices (String schema) throws PersistenceException
	{
		try
		{
			/* Get the appropriate schema object */
			Configuration oneSchema = (Configuration) schemas.get (schema);

			if (oneSchema == null)
			{
				throw new PersistenceException ("No such schema '" + schema + "' defined in Persistent Factory '"
								+ getName () + "'");
			}

			Configuration[] eachPersistent = oneSchema.getChildren ();

			for (int i = 0; i < eachPersistent.length; i++)
			{
				Configuration onePersistent = eachPersistent[i];
				String persistentName = onePersistent.getAttribute ("name");
				PersistentMetaData pmd = getMetaData (schema + "." + persistentName);

				databaseType.dropIndices (pmd, dataSource);
			}
		}
		catch (Exception ce)
		{
			throw new PersistenceException (ce);
		}
	}
}
