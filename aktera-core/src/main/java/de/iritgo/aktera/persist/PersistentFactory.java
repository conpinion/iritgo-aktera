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

package de.iritgo.aktera.persist;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelResponse;
import org.apache.avalon.framework.configuration.Configuration;
import java.util.Set;


/**
 * A PersistentFactory is a class that generates new Persistent
 * objects, Transactions, and Queries.
 */
public interface PersistentFactory
{
	public static String ROLE = "de.iritgo.aktera.persist.PersistentFactory";

	/**
	 * Create a new persistent object with the specified name. The name must be
	 * of the form "schema.persistent".
	 */
	public Persistent create(String name) throws PersistenceException;

	/**
	 * Create a new Persistent which participates in a transaction.
	 */
	public Persistent create(String name, Transaction trx) throws PersistenceException;

	/**
	 * Begin a new transaction and pass the transaction back to the caller. Used when we want a number of database
	 * operations to be processed in a single transaction.
	 */
	public Transaction begin() throws PersistenceException;

	/**
	 * A different factory is used to talk to each different database that the application requires. Each factory has
	 * a unique name.
	 */
	public String getName();

	/**
	 * Initialize the specified schema, creating all tables as required. Logs any differences between existing
	 * tables and specified tables.
	 */
	public void createTables(String schemaName) throws PersistenceException;

	/**
	 * Initialize all schemas defined in this persistent factory, creating all tables as required.
	 * Logs any differences between existing tables and specified tables.
	 */
	public void createTables() throws PersistenceException;

	/**
	 * Return a set of Schema names this factory knows about.
	 */
	public Set getSchemas();

	/**
	 * Get the human-readable description of the specified schema
	 * @param schemaName The name of the schema
	 * @return String The human-readable description of this schema
	 */
	public String getSchemaDescription(String schemaName);

	/**
	 * Get the Configuration object for the specified schema, if this
	 * schema is defined in this PersistentFactory
	 * @param schemaName The name of the schema to retrieve configuration for
	 * @return A Configuration for the named schema, or null if no such schema exists
	 * @throws PersistenceException If a parameter error is made (e.g. null schema name), or an error
	 * occurrs retrieving the specified schema's configuration
	 */
	public Configuration getSchemaConfiguration(String schemaName);

	/**
	 * Return a list of the names of all persistent object in the specified
	 * schema.
	 */
	public Set getPersistents(String schemaName) throws PersistenceException;

	/**
	 * Convenience method to add an Input ResponseElement to the specified
	 * ModelResponse for every field in the supplied Persistent. Each Input will
	 * have a default value the same as the current value of the field in the
	 * Persistent, and the Input will have valid values if the field has a list
	 * of supplied valid values.
	 *
	 * @param res A ModelResponse object
	 * @param p A persistent to be used to create Inputs
	 * @throws PersistenceException If a problem occurs retrieving the fields
	 * @throws ModelException If a problem occurs creating the Inputs
	 */
	public void addInputs(ModelResponse res, Persistent p) throws PersistenceException, ModelException;

	/**
	 * Convenience method to add an Output ResponseElement to the specified
	 * ModelResponse for every field in the supplied Persistent. Each output
	 * will be named according to the field name from the persistent, and the
	 * content of the output will be the value of the field.
	 *
	 * @param res A ModelResponse object
	 * @param p A persistent to be used to create Outputs
	 * @throws PersistenceException If an exception occurs retrieving the fields
	 * @throws ModelException If an exception occurs creating the outputs.
	 */
	public void addOutputs(ModelResponse res, Persistent p) throws PersistenceException, ModelException;

	/**
	 * Convenience method to add a List of nested Output ResponseElements to the
	 * specified ModelRepsonse.  Similiar to addOutputs, for the individiual rows.
	 * Each row is added to the "outer" Output as row # (useful for logic:iterate).
	 * Rows are added to the Output Response Element specified by outputName.
	 *
	 * @param res A ModelResponse object
	 * @param p A persistent to be used to create Outputs
	 * @param outputName Name of Output ResponseElement to add the List to.
	 * @throws PersistenceException If an exception occurs retrieving the fields
	 * @throws ModelException If an exception occurs creating the outputs.
	 * @see #addOutputs(ModelReponse, Persistent)
	 */
	public void addQuery(final ModelResponse res, final Persistent p, final String outputName)
		throws PersistenceException, ModelException;

	/**
	 * Convenience method to add a List of nested Output ResponseElements to the
	 * specified ModelRepsonse.  Similiar to addOutputs, for the individiual rows.
	 * Each row is added to the "outer" Output as row # (useful for logic:iterate).
	 * Rows are added to the Output Response Element specified by outputName.
	 *
	 * @param res A ModelResponse object
	 * @param p A persistent to be used to create Outputs
	 * @param outputName Name of Output ResponseElement to add the List to.
	 * @param fieldNames The fields to be added to the Output.  Fields will be added in the same order as in the list.
	 * @throws PersistenceException If an exception occurs retrieving the fields
	 * @throws ModelException If an exception occurs creating the outputs.
	 * @see #addOutputs(ModelReponse, Persistent)
	 */
	public void addQuery(final ModelResponse res, final Persistent p, final String outputName, final String[] fieldNames)
		throws PersistenceException, ModelException;

	/**
	 * Return the name of an another persistent factory where we find
	 * the definitions of the Keel security persistent objects. If we get null
	 * we find them here.
	 */
	public String getSecurity();

	/**
	 * A convenience method which gets another factory (other than the current
	 * one).
	 * @param factoryName The name of the factory being requested
	 * @return A new PersistentFactory
	 * @throws PersistenceException If the factory cannot be initialized, or
	 * there is no such factory configured
	 */
	public PersistentFactory getFactory(String factoryName) throws PersistenceException;
}
