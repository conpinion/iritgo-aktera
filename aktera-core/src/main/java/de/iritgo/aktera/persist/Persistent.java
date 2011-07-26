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
import java.sql.Date;
import java.util.List;
import java.util.Map;


/**
 * A persistent object. Your application classes should *not* normally directly
 * implement this interface, but should instead request instances
 * from the current PersistentFactory. Only implementation classes for the persistence service implement this
 * interface directly. If you prefer to do "bean-like" objects as your persistent classes,
 * see AbstractPersistent instead.
 */
public interface Persistent
{
	/** Status codes */
	public static int CURRENT = 0;

	public static int MODIFIED = 1;

	public static int DELETED = 2;

	public static int NEW = 3;

	/** Operation codes */
	public static int ADD = 0;

	public static int UPDATE = 1;

	public static int DELETE = 2;

	public static int QUERY = 3;

	/**
	 * Return one of the valid status codes indicating the current state of the Persistent object. The valid codes
	 * are CURRENT, MODIFIED, DELETED and NEW
	 * TODO: More details
	 */
	public int getStatus ();

	/**
	 * Determine if the given operation is allowed for the current user, given
	 * one of the operation codes ADD, UPDATE, DELETE or QUERY
	 *
	 * @return True if the operation is permitted, false otherwise
	 */
	public boolean allowed (int operation) throws PersistenceException;

	/**
	 * Determine if a record with this key exists already - if
	 * not, add a new record. Note that this method uses just the key
	 * fields to determine if the record already exists.
	 */
	public void add () throws PersistenceException;

	/**
	 * Set all fields to empty value & clear the last result set
	 *
	 * @throws    PersistenceException If the fields cannot be cleared
	 */
	public void clear () throws PersistenceException;

	/**
	 * Delete a record from the target table. The current field values for the key
	 * of this object tell us which record to delete. This default version
	 * of delete also deletes associated detail records (e.g. performs a
	 * "cascading delete" of details.
	 *
	 * @throws PersistenceException If the delete fails or if the referential integrity
	 *          would be violated by the delete
	 */
	public void delete () throws PersistenceException;

	/**
	 * Delete this record, optionally deleting any associated detail
	 * records.
	 * WARNING: Deleting details is *recursive* - e.g. any details of those
	 * details will also be deleted. Use with caution on extremely large
	 * data sets - might be a better idea to use deleteAll on each appropriate
	 * table, which is non-recursive and uses a single SQL statement to delete
	 * groups of records.
	 *
	 * @param deleteDetails Delete associated detail records if true
	 * @throws PersistenceException If the delete fails or if the referential integrity
	 *          would be violated by the delete
	 */
	public void delete (boolean deleteDetails) throws PersistenceException;

	/**
	 * Delete all of the records specified by the current search criteria.
	 * IMPORTANT: deleteAll does *not* check for details, nor does it offer
	 * the option of deleting associated detail records. In order to do this,
	 * retrieve each record with query and use delete(true), not deleteAll().
	 * deleteAll() is more efficient for deleting a large number of records
	 * from a single table.
	 * @throws PersistenceException If the delete fails or if the referential integrity
	 *          would be violated by the delete
	 */
	public void deleteAll () throws PersistenceException;

	/**
	 * A lot like retrieve, but works with any fields, not just the key field. Does not
	 * throw an exception if the record is not found, just returns false.
	 * Finds only first record matching the criteria. The current fields in this object
	 * are populated with the data in the first (or only) record found after the call to find() if the
	 * find is successful.
	 *
	 * @return    boolean If the search resulted in a record
	 * @throws    PersistenceException If the search could not be completed
	 */
	public boolean find () throws PersistenceException;

	/**
	 * Get a single record from the database into this Persistent's fields
	 * Assumes that the key fields are set to the key of the object to be
	 * retrieved
	 *
	 * @throws    PersistenceException If the record could not be retrieved, or if the key fields were not set. If you
	 *           are not sure if the record exists, use find() instead.
	 * @see #find
	 */
	public void retrieve () throws PersistenceException;

	/**
	 * Find a set of records of all of the objects that match the current
	 * search critieria in the fields
	 * and retrieve the list of all records that match this criteria
	 * NOTE: Criteria in 'text' type colums is ignored (SQL Server limitation)
	 *
	 * @return    A List of new database objects containing the results
	 *            of the search
	 * @throws    PersistenceException If the search could not be completed
	 */
	public List<Persistent> query () throws PersistenceException;

	/**
	 * Search and retrieve in a particular order
	 *
	 * @param    sortKeyString A comma-delimited list of key fields to sort
	 *            the returned set by
	 * @return    A List of new database objects retrieved by the search
	 * @throws    PersistenceException If the search could not be completed
	 */
	public List<Persistent> query (String sortKeyString) throws PersistenceException;

	/**
	 * Set the given field to the specified value. Note that no validation is done at this point, other than possible
	 * type conversion if required. Use validateField or validateAll to apply appropriate validations and record
	 * the errors, if any.
	 * Note that field names in the Persistent object may or may not be the same as the database field in which
	 * the Persistent is stored. This is determined by the configuration file.
	 */
	public void setField (String fieldName, Object fieldValue) throws PersistenceException;

	/**
	 * Get the value of a field in this object
	 *
	 * @param    fieldname Name of the field to fetch
	 * @return    The value of the given field as a string - if the field is null,
	 *          an empty string is returned.
	 * @throws    PersistenceException If there is no such field or it's value cannot be accessed
	 */
	public Object getField (String fieldName) throws PersistenceException;

	/**
	 * Get the integer value of a field in this object
	 *
	 * @param   fieldName Name of a field in this object
	 * @return  int  The value of the field as a float
	 * @throws  PersistenceException if there is no such field or it's value cannot be
	 *          converted to an integer.
	 */
	public int getFieldInt (String fieldName) throws PersistenceException;

	/**
	 * Return the value of a field as a Date object
	 *
	 * @param    fieldName The field to be retrieved
	 * @return   The java.sql.Date object equivilant to this field's value
	 * @throws   PersistenceException If the field does not exist or it's value
	 *            is not a date or cannot be converted to a date
	 */
	public Date getFieldDate (String fieldName) throws PersistenceException;

	/**
	 * Boolean typesafe getField
	 *
	 * @param    fieldName The field to be retrieved
	 * @return   The boolean equivilant to this field's value
	 * @throws   PersistenceException If the field does not exist or it's value
	 *            is not a date or cannot be converted to a date
	 */
	public boolean getFieldBoolean (String fieldName) throws PersistenceException;

	/**
	 * Return the value of a field as a Strintg object
	 *
	 * @param    fieldName The field to be retrieved
	 * @return   The String object equivilant to this field's value
	 * @throws   PersistenceException If the field does not exist or it's value
	 *            is not a date or cannot be converted to a date
	 */
	public String getFieldString (String fieldName) throws PersistenceException;

	/**
	 * Update the database with the new info. Update affects only one record,
	 * and uses the current field values to write to the DBMS. Referential
	 * integrity is verified before the update is performed.
	 *
	 * @throws    PersistenceException if the update to the database fails due to
	 *             a database error
	 */
	public void update () throws PersistenceException;

	public void setTransaction (Transaction t) throws PersistenceException;

	public Helper getHelper () throws PersistenceException;

	/**
	 * Apply field validations to a single field, returning a validation message if there is a problem,
	 * or null if there's no problem
	 */
	public String validateField (String fieldName, Object fieldValue);

	/**
	 * Apply all field validations to all fields, returning a map where the key is the field
	 * name and the value is the error message for that field. If a field is not listed, there
	 * was no problem with it's validation
	 */
	public Map validateAll ();

	/**
	 * A Persistent is always configured from it's factory, never from the application directly
	 */
	void setMetaData (PersistentMetaData newMeta) throws PersistenceException;

	/**
	 * Return the PersistentMetaData object which describes this Persistent. This object contains all information
	 * about fieldnames, types, etc.
	 */
	public PersistentMetaData getMetaData () throws PersistenceException;

	/**
	 * Each persistent object has a name, assigned in the configuration file. This name may or may not bear any relationship
	 * to the table name in which the Persistent is stored.
	 */
	public String getName ();

	/**
	 * Any database operation might in fact result in more than one error - validations, for example, or referential
	 * integrity problems. This method allows the caller to retrieve all of the errors from the most recent
	 * operation. For example, if you call "add()" and it throws an exception, there may in fact have been more than
	 * one problem preventing the add from happening, e.g. multiple validation errors. An empty map is returned when
	 * the last operation was successful.
	 */
	public Map getErrors ();

	/**
	 * Return a Map of valid values and an associated
	 * description of each value for this field.
	 * @throws PersistenceException If there is no such field
	 */
	public Map getValidValues (String fieldName) throws PersistenceException;

	/**
	 * Just like find, but only retrieves the count, not the records themselves.
	 *
	 * @return    integer Count of the records matching the criteria
	 * @throws    PersistenceException If the search could not be completed
	 * @see #find
	 */
	public int count () throws PersistenceException;

	/**
	 * Set a general-purpose short-lived attribute for this
	 * persistent. The attribute is not (necessarily) stored
	 * in the database.
	 */
	public void setAttribute (String attribName, Object attribValue);

	/**
	 * Get a general-purpose attribute
	 * where the scope is the whole persistent
	 */
	public Object getAttribute (String attribName);

	public Map getAttributes ();

	/**
	 * Get a general-purpose attribute
	 * for a specific field.
	 * @throws PersistenceException If there is no such field
	 */
	public Object getAttribute (String fieldName, String attribName) throws PersistenceException;

	/**
	 * Specify the maximum number of records retrieved by
	 * the next query
	 */
	public void setMaxRecords (int newMax);

	/**
	 * Specifies the number of records that should be skipped over
	 * before any data from the <code>ResultSet</code>
	 * is retrieved in any subsequent
	 * query() call. Records will be skipped over (in the specified
	 * sort order) until the record counts is equal to or greater
	 * than the offset record. Specifying zero indicates that no
	 * records should be skipped over and the
	 * <code>ResultSet</code> immediately from the start.
	 *
	 * @param newMax The maximum number of records to retrieve.
	 * @throws PersistenceException If the max number is less than 0
	 *
	 */
	public void setOffsetRecord (int newOffset);

	/**
	 * Get exclusive read-write access to the underlying store for a persistent table
	 */
	public void lock () throws PersistenceException;

	/**
	 * Release the exclusive read-write access to the underlying store for the persistence table
	 */
	public void unlock () throws PersistenceException;

	/**
	 * Return the maximum value of the specified field
	 */
	public double max (String fieldName) throws PersistenceException;

	/**
	 * Return the minimum value of the specified field
	 */
	public double min (String fieldName) throws PersistenceException;

	/**
	 * Find the sum of the values in the specified field of records
	 * selected by the Persistent selection criteria.
	 *
	 * @param    String Persistent fieldName to average
	 * @return    double Sum of the records matching the criteria
	 * @throws    PersistenceException If the search could not be completed
	 */
	public double sum (String fieldName) throws PersistenceException;

	/**
	 * The PersistentMetaData object associated with this Persistent can supply
	 * a list of the "detail" names associated with this Persistent. Each of
	 * these names refers to another Persistent which has a detail relationship
	 * to this Persistent. For each such name, this method can be used to
	 * retrieve the set of related Persistents from the detail object.
	 * @param detailName The name of the detail relationship, as provided by the
	 * meta-data
	 * @return List A list of Persistents related to this one via the specified
	 * relationship
	 * @throws PersistenceException If a problem occurs retrieving the details
	 * or if there is no such relationship
	 */
	public List getDetails (String detailName) throws PersistenceException;

	/**
	 * Convenience method that returns a JavaBean containing the data from the
	 * current persistent. The bean class is specified in configuration, and associated
	 * with the persistent name. If there is no classname specified, a DynaBean will
	 * be returned instead.
	 * @return An object (a JavaBean) populated with the data from the current persistent
	 * @throws PersistenceException If there is a problem instantiating or populating the bean.
	 */
	public Object getBean () throws PersistenceException;

	/**
	 * Set the persistent fields from the fields of a bean.
	 *
	 * @param newBean A JavaBean with the data to populate the current persistent from.
	 * @throws PersistenceException
	 */
	public void setBean (Object newBean) throws PersistenceException;

	public void setBypassAuthorizationManager (AuthorizationManager bypassAm) throws PersistenceException;
}
