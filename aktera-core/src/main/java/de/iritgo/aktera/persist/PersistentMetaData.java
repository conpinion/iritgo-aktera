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


import de.iritgo.aktera.authorization.AuthorizationManager;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.ids.IdGenerator;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.oro.text.regex.Pattern;
import java.util.Map;
import java.util.Set;


/**
 * This object contains the "definition" of a Persistent, all of the Meta-data that associates it with a particular
 * database table. Each persistent has a name, which is assigned arbitrarily
 * (but uniquely) within it's "schema". This name is used to refer to the
 * Persistent within Model objects. Each field in the persistent likewise has a
 * name, which may or may not be the same as the field name in the actual
 * database. This allows the field names to be altered for compatibility with a
 * particular database by changing only the configuration file, not the
 * application. The same is true of table names.
 *
 * @version        $Revision: 1.8 $  $Date: 2003/11/17 04:05:12 $
 * @author        Michael Nash
 */
public interface PersistentMetaData
{
	public static String ROLE = "de.iritgo.aktera.persist.PersistentMetaData";

	/**
	 * Get the (read-only) configured default value for the named field, if one was
	 * specified.
	 *
	 * @param fieldName Name of the field to retrieve the default for.
	 * @return String The default value for this field.
	 * @throws PersistenceException If there is no such field.
	 */
	public String getDefaultValue(String fieldName) throws PersistenceException;

	/**
	 * Return a set of relation names, where each relation is a "detail" relationship
	 * between this persistent and another.
	 */
	public Set getDetailNames();

	/**
	 * Get a specific Relation object which describes a relationship involving this persistent
	 */
	public Relation getRelation(String relationName);

	/**
	 * Get the set of all Relation names associated with this persistent.
	 */
	public Set getRelationNames();

	/**
	 * Determine if the specified field may contain null.
	 * @return True if the field may be null, false if it may not
	 * @throws PersistenceException If there is no such field in this persistent
	 */
	public boolean allowsNull(String fieldName) throws PersistenceException;

	/**
	 * Get a named "attribute" of this field from the setup meta-data for the
	 * Persistent. Each field may have a (read-only) set of attributes defined for
	 * it as part of the configuration for this Persistent object. Each attribute
	 * has a name and a value. This method allows retrieveal of the values.
	 *
	 * @param fieldName Name of the field to retrieve the attribute value for.
	 * @param attribName Name of the attribute to retrieve.
	 * @return Object The value of the Attribute - often a String, but not
	 * necessarily.
	 * @throws PersistenceException If there is no such field, or no such attribute.
	 */
	public Object getAttribute(String fieldName, String attribName) throws PersistenceException;

	/**
	 * Return the current object's character set
	 *
	 * @return A string representing this Persistent's character set.
	 */
	public String getCharset();

	/**
	 * Return a description of this Persistent.
	 *
	 * @return A string describing this Persistent
	 */
	public String getDescription();

	/**
	 * Return the description of a field
	 *
	 * @param    fieldName The name of the field
	 * @return    String The description of the field
	 * @throws    PersistenceException If there is no such field
	 */
	public String getDescription(String fieldName) throws PersistenceException;

	/**
	 * Get a set of all of the fields in this object.
	 *
	 * @return    A Set of all of the field names in this object
	 * @throws    PersistenceException If the list cannot be retrieved
	 */
	public Set getFieldNames();

	/**
	 * Get the name of the field in the actual database, that is, the name to be
	 * used in constructing SQL statement to refer to this field. This may or
	 * may not be the same as the internal name of the field
	 * @param fieldName The "internal" name for this field
	 * @return The actual name of the field as a column in the underlying
	 * database.
	 */
	public String getDatabaseName(String fieldName) throws PersistenceException;

	/**
	 * Get a list of all of the names of the key fields in this object
	 *
	 * @return    An Set of all of the names of the key fields
	 */
	public Set getKeyFieldNames();

	/**
	 * Determine if the specified field participates in the primary key for this
	 * Persistent.
	 * @param fieldName The name of the field
	 * @return boolean True if the field is one of the primary key fields, false if
	 * it is not.
	 */
	public boolean isKeyField(String fieldName);

	/**
	 * Return the maximum length of the specified field, if any maximum is configured. Return 0 if no maximum is configured.
	 * @throws PersistenceException If no such field exists
	 */
	public int getLength(String fieldName) throws PersistenceException;

	/**
	 * Return the precision of the specified field, if any precision is configured. Return 0 if none is specified
	 *
	 * @param fieldName The field requested
	 * @throws PersistenceException If the field does not exist in this Persistent
	 * @return The precision of the specified field
	 */
	public int getPrecision(String fieldName) throws PersistenceException;

	/**
	 * Get a field's lookup object - this is the name of another Persistent
	 * that can be used to look up valid values for this object.
	 * TODO: Allow this to be a query, not a persistent
	 *
	 * @param fieldName The name of the field
	 * @return The name of the lookup object, if any
	 * @throws PersistenceException If the specified field does not exist.
	 */
	public String getLookupObject(String fieldName) throws PersistenceException;

	/**
	 * Added by Santanu Dutt
	 * Get a field to use in lookup object
	 *
	 * @param fieldName The field name
	 * @return The name of the lookup field
	 * @throws PersistenceException If the specified field does not exist.
	 */
	public String getLookupField(String fieldName) throws PersistenceException;

	/**
	 * Added by Santanu Dutt
	 * Get a display field to use in lookup object
	 *
	 * @param fieldName
	 * @throws PersistenceException If the specified field does not exist.
	 */
	public String getLookupFieldDisplay(String fieldName) throws PersistenceException;

	/**
	 * Get the name of this object
	 *
	 * @return    String The Persistent object name. Often (but NOT always) the same as the table name in the database
	 */
	public String getName();

	/**
	 * The name of the actual table (if a relational database) that stores this
	 * Persistent.
	 */
	public String getTableName();

	/**
	 * Return the name of the Java type of a field - this is the "internal" type of this field, not the type
	 * for the database
	 *
	 * @param    fieldName The name of the field
	 * @return    String: The type of the field
	 * @throws    PersistenceException If there is no such field in this object
	 */
	public String getType(String fieldName) throws PersistenceException;

	/**
	 * Retrieve the database storage type of the specified field.
	 *
	 * @param fieldName Field name
	 * @return String The database-specific type name
	 * @throws PersistenceException If there is no such field
	 */
	public String getDBType(String fieldName) throws PersistenceException;

	/**
	 * Return an Set of the names of each of the indicies defined on this table
	 * on this table
	 */
	public Set getIndicies();

	/**
	 * Return the set of field names indexed by the named index.
	 * @throws PersistenceException If there is no index with the given name
	 */
	public Set getIndexedFields(String indexName) throws PersistenceException;

	/**
	 * This method will return a boolean true if the field is defined in the Persistent
	 * false otherwise.
	 *
	 * @return boolean
	 * @param fieldName Name of the field to be tested for
	 */
	public boolean hasField(String fieldName);

	/**
	 * Method called to determine if a particular field is multi-valued,
	 * that is does it have a set of specific values and descriptions
	 *
	 * @param    fieldName Name of the field
	 * @return    boolean True if the field is multi-valued, false if not
	 * @throws    PersistenceException If there is no such field
	 */
	public boolean isMultiValued(String fieldName) throws PersistenceException;

	/**
	 * Is a given field readOnly - these fields are not offered for entry
	 * when a form is produced by the crud model.
	 *
	 * @param fieldName
	 * @return True of the field is "read only", false if it is not
	 * @throws PersistenceException Ff there is no such field
	 */
	public boolean isReadOnly(String fieldName) throws PersistenceException;

	/**
	 * Is a given field 'hidden' - these fields are not shown
	 * when a list is produced by the generic crud model.
	 * This means that only users with update permission to the record can see the
	 * value of the specified field.
	 *
	 * @param fieldName The name of the field to check
	 * @return True if the field is 'hidden', false if it is not
	 * @throws PersistenceException If there is no such field.
	 */
	public boolean isHidden(String fieldName) throws PersistenceException;

	/**
	 * Return the Schema name for this Persistent. This is the part of the name
	 * before the period in the full name.
	 *
	 */
	public String getSchemaName();

	/**
	 * Specify the schema name for this Persistent.
	 */
	void setSchemaName(String newName) throws PersistenceException;

	/**
	 * This is configuration for the underlying Persistent
	 */
	void configurePersistent(Configuration newConfig) throws PersistenceException;

	/**
	 * Specify the DatabaseType implementation used for this Persistent.
	 * @param newType The DatabaseType implementation used for this Persistent.
	 */
	public void setDatabaseType(DatabaseType newType);

	/**
	 * Return the IdGenerator instance configured for a particular field, if any.
	 * @param fieldName The field name.
	 * @return IdGenerator The IdGenerator class used to create new sequential id's
	 * for this field, if any. Null if this field is not auto-incremented.
	 * @throws PersistenceException If there is no such field name.
	 */
	public IdGenerator getIdGenerator(String fieldName) throws PersistenceException;

	/**
	 * Return the name of the DatabaseType implementation configured for this
	 * Persistent.
	 * @return DatabaseType A name of a DatabaseType implementation.
	 */
	public DatabaseType getDatabaseType();

	/**
	 * Return true if we are collecting Cache statistics for this Persistent.
	 * @return boolean True if statistics are being collected, false if not.
	 */
	public boolean collectStatistics();

	/**
	 * Get the size of the cache (in elements) used for this Persistent. Zero
	 * indicates no caching is performed.
	 * @return int Size of the cache in number of elements.
	 */
	public int getCacheSize();

	/**
	 * Get the name of the class defined as a "Helper" for this Persistent, if any.
	 * @return String The full classname of the Helper class. Null if no helper is
	 * specified.
	 */
	public String getHelperClassName();

	/**
	 * Determine if a specified field is automatically incremented in value as
	 * records are added.
	 *
	 * @param fieldName The name of the field.
	 * @return boolean True if a form of auto-incrementing is used on this field,
	 * False if it is not auto-incremented.
	 * @throws PersistenceException If there is no such field.
	 */
	public boolean isAutoIncremented(String fieldName) throws PersistenceException;

	/**
	 * Get the name of this field as it is stored in the database (the column
	 * name), actually used in SQL.
	 * @throws PersistenceException If there is no such field
	 */
	public String getDBFieldName(String fieldName) throws PersistenceException;

	/**
	 * Return the valid expression pattern (pre-compiled) for the named field, if any.
	 * @throws PersistenceException If there is no such field
	 */
	public Pattern getPattern(String fieldName) throws PersistenceException;

	/**
	 * Checks to make sure that the fieldName is valid, and then checks to make
	 * sure that a listValidValues name has been specified for this object (it
	 * is not null).
	 *
	 * @param fieldName
	 * @return boolean
	 * @throws PersistenceException If there is no such field
	 */
	public boolean hasListValidValues(String fieldName) throws PersistenceException;

	/**
	 * Returns a map of Valid values (id/description pairs). This method
	 * returns values if the field found by the given field name has been defined
	 * as having list-valid-values, as in this example:
	 *
	 *      <field db-name="SOME_FIELD_NAME"
	 *                         descrip="Just a field"
	 *                         name="SOME_FIELD_NAME" null-allowed="false" type="integer">
	 *                         <list-valid-values name="SOME_LIST_NAME" />
	 *            </field>
	 *
	 * List Valid Values are read from two core Keel tables, KeelListHeader and
	 * KeelListFooter.
	 *
	 * @param fieldName
	 * @return Map
	 * @throws PersistenceException
	 */
	public Map getListValidValues(String fieldName) throws PersistenceException;

	/**
	 * Determine if the specified field is stored in an encrypted format.
	 *
	 * @param fieldName Field name to check
	 * @return boolean True if the field is specified as encrypted, false otherwise
	 * @throws PersistenceException If there is no such field.
	 */
	public boolean isEncrypted(String fieldName) throws PersistenceException;

	/**
	 * The Persistent factory (only) hands this PersistentMetaData it's data source component, which it
	 * passes on as needed to the Persistent objects themselves. Only visible
	 * within the package.
	 */
	void setDataSource(DataSourceComponent newSource) throws PersistenceException;

	/**
	 * Return the DataSourceComponent used for this Persistent. Only visible within
	 * the package.
	 * @return DataSourceComponent The DataSourceComponent used for this Persistent.
	 */
	DataSourceComponent getDataSource();

	/**
	 * If the Persistent has been defined as having a specific "page size" (for
	 * display in listings), return the specified size.
	 * @return int The page size configured for this Persistent.
	 */
	public int getPageSize();

	/**
	 * Return any special additional attributes configured
	 * for the specified field.
	 * @param fieldName The name of the field to retrieve the Attribute map for.
	 * @return A Map of attribute names and values for this field.
	 */
	public Map getAttributes(String fieldName) throws PersistenceException;

	/**
	 * Return a map of declared valid values configured for the named
	 * field. A field may have a "static" set of valid values, specified
	 * in configuration, or it may dynamically figure them out. Applications
	 * should call getValidValues from the Persistent object, not this method.
	 */
	Map getStaticValidValues(String fieldName) throws PersistenceException;

	/**
	 * A particular persistent may be implemented by a specified class, in which
	 * case an instance of this class is created when a "create" on this persistent
	 * is called
	 */
	String getClassName();

	/**
	 * Specify the PersistentFactory used to create instances of this Persistent.
	 * @param myFactory The PersistentFactory used to create these Persistent
	 * objects.
	 */
	void setFactory(PersistentFactory myFactory);

	/**
	 * Return the current PersistentFactory
	 * @return PersistentFactory
	 */
	public PersistentFactory getFactory();

	/**
	 *  Determine   if   the Persistent objects specified by this Meta-data are
	 * Securable. This is required because the individual Persistent objects are
	 * not components themselves.
	 *
	 * Added by Santanu Dutt for Securable
	 * Persistent objects
	 */
	public boolean isSecurable();

	public boolean isRowSecurable();

	/**
	 * If securable, then each peristent can have it's own authorization
	 * manager
	 * @return
	 */
	public AuthorizationManager getAuthManager();

	/**
	 * Some securable persistents that are also used by the AuthorizationManager
	 * to check authoriztion, need to be able to bypass auth. to avoid
	 * looping of the auth. function.  The bypassing is done by using the
	 * setBypassAuthorizationManager(am) in the Persistent interface,
	 * But only certain persistents, by using the am-bypass-allowed attr. in
	 * configuration, are allowed to have auth. bypassed.
	 */
	public boolean isAuthManagerBypassAllowed();

	/**
	 * @param oneFieldName
	 * @return
	 * @throws PersistenceException
	 */
	public Object getAutoIncremented(String oneFieldName) throws PersistenceException;
}
