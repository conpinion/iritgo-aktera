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


import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;


/**
 * A DatabaseType provides information and methods that allow persistent objects to talk to a specific kind
 * of database.
 * A database type contains all of the information and configuration specific to that type of database, e.g.
 * the type mappings for the database, any quirks in it's SQL syntax, etc. Each PersistentFactory instance uses
 * exactly one DatabaseType, and connects to exactly one database of that type.
 *
 *   @version          $Revision: 1.7 $ $Date: 2005/10/27 01:00:25 $
 */
public interface DatabaseType
{
	public static String ROLE = "de.iritgo.aktera.persist.DatabaseType";

	/**
	 * If this database does not support the concept of retrieving only a subset of the selected rows. Persistent
	 * implementations should still support returning only a subset, although it will not be as efficient.
	 */
	public final static int SUBSET_NOT_SUPPORTED = 0;

	/**
	 * Insert the subset syntax string after the name of the table in the SQL. E.g.
	 * <programlisting>
	 * SELECT ... FROM tableName subSetSyntax WHERE ...
	 * </programlisting>
	 */
	public final static int SUBSET_AFTER_TABLE = 1;

	/**
	 * Insert the subset syntax string after WHERE term
	 * <programlisting>
	 * SELECT ... FROM tableName WHERE ...
	 * AND subSetSyntax ORDER BY ...
	 * </programlisting>
	 */
	public final static int SUBSET_AFTER_WHERE = 2;

	/**
	 * Insert the subset syntax string after ORDER BY term
	 * <programlisting>
	 * SELECT ... FROM tableName
	 * WHERE ...  ORDER BY ... subSetSyntax
	 * </programlisting>
	 */
	public final static int SUBSET_AFTER_ORDER_BY = 3;

	/**
	 * Does this database type support commit, rollback?
	 */
	public boolean supportsTransactions ();

	/**
	 * Return the list of reserved words for this database type, if one is
	 * specified. These are words that cannot be used for field or
	 * table names
	 */
	public Set getReservedWords ();

	/**
	 * Does this type of database allow where clauses to include "text"-type fields?
	 */
	public boolean allowTextQueries ();

	public Set getWildCards ();

	public String getDistinctKeyWord ();

	public String getDateUpdateFormat ();

	public String getDateTimeUpdateFormat ();

	public String getTimeUpdateFormat ();

	public String getDateUpdateFunction ();

	public String getDateTimeUpdateFunction ();

	public String getTimeUpdateFunction ();

	/**
	 * Does a given field value denote a range?
	 *
	 * @param fieldValue
	 * @return The "range" string if the value starts with a range indicator, null if not
	 */
	public String denotesRange (String fieldValue);

	public String getDateSelectFormat ();

	public String getDateSelectFunction ();

	public String getDateTimeSelectFormat ();

	public String getDateTimeSelectFunction ();

	public String getTimeSelectFormat ();

	public String getTimeSelectFunction ();

	/**
	 * Return true if the specified type name (database type) requires
	 * quotes around it for this database type. Return false if the field
	 * value may be used in SQL without quotes
	 * @throws PersistenceException If the named type is not supported
	 */
	public boolean isQuotedType (String typeName) throws PersistenceException;

	/**
	 * Return true if the specified type is numeric, a non-string field
	 * and not a date/time field
	 * @throws PersistenceException If the named type is not supported
	 */
	public boolean isNumericType (String typeName) throws PersistenceException;

	/**
	 * Determine if this field type is a date/time type field.
	 * @param typeName  The type to check
	 * @return boolean True if this type is a date/time type
	 * @throws PersistenceException If there is no such type
	 */
	public boolean isDateType (String typeName) throws PersistenceException;

	/** The methods used to retreive BLOBs from a resultset vary by database.
	 */
	public byte[] getBlobResult (String fieldName, ResultSet rs, int index) throws PersistenceException;

	/** The methods used to retreive CLOBs from a resultset vary by database.
	 */
	public List getClobResult (String fieldName, ResultSet rs, int index) throws PersistenceException;

	/**
	 * Syntax used to limit the rows retrieved to an arbitrary subset of the
	 * actual rows selected. Not all database support this capability themselves, and it
	 * is very inefficient to do in those cases. Check the result from isSubSetSupported
	 * to see if your database supports this directly.
	 * </para>
	 *
	 * <para>For Oracle the syntax is:</para>
	 * <programlisting>ROWNUM >= %offset% AND ROWNUM <=%endrecord%</programlisting>
	 * <para>And for MySQL it is:</para>
	 * <programlisting>LIMIT %offset% , %maxrecord%</programlisting>
	 * <para>The String returned uses the %offset%, %endrecord% and %maxrecord% placeholders to
	 * indicate where the corresponding values should be substituted when creating the actual SQL
	 */
	public String getSubSetSyntax ();

	/**
	 * Where should the subset syntax be inserted when building the SQL statement for this particular
	 * database? See the various status codes at the top of this file for the valid return
	 * values
	 */
	public int getSubSetPosition ();

	/**
	 * Return false if the database does not support the ability to retrieve only a subset
	 * of the selected rows. getSubSetPosition will be SUBSET_NOT_SUPPORTED in this case
	 */
	public boolean isSubSetSupported ();

	/**
	 * Create the table defined by the PersistentMetaData we are supplied in the supplied data source. If the table already
	 * exists, report (in the log) on discrepancies between it's definition and the one in the PersistentMetaData, if any.
	 */
	public void createTable (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException;

	/**
	 * Create the indices defined by the PersistentMetaData we are supplied with. If any of the indices already exists, then log it.
	 * The 'duringTableCreation' flag denotes whether this method is part of the table creation process. If it is, then the xml attribute
	 * 'create-with-table' of the index is checked to see whether this index should be created. If the index creation is not occuring
	 * during table creation then the 'create-with-table' attribute is ignored.
	 */
	public void createIndices (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException;

	/**
	 * Drops the indices defined by the PersistentMetaData we are supplied with in the supplied data source.
	 */
	public void dropIndices (PersistentMetaData pmd, DataSourceComponent dataSource) throws PersistenceException;

	/**
	 * Return the string name of the database type used for this internal type (java.sql.Types)
	 */
	public String getDBType (String internalType) throws PersistenceException;

	/**
	 * A DatabaseType is handed a DataSourceComponent, which it uses to initialize it's default type mapping and other
	 * meta-data about the database directly from the JDBC driver. This step is *optional*, and a factory may choose
	 * to simply allow configuration to specify all type mappings supported by the database type. If this method is called,
	 * the type map it builds is overridden by any explicit mappings in the configuration file
	 */
	public void setDataSource (DataSourceComponent newSource) throws PersistenceException;

	/**
	 * Does this DatabaseType handle IDENTITY fields for auto-increment?
	 */
	public boolean isIdentitySupported ();

	/**
	 * Does this DatabaseType handle SEQUENCE fields for auto-increment?
	 */
	public boolean isSequenceSupported ();

	/**
	 * Retrieve the SQL syntax used for creating an identity column in this
	 * database. If Identity is supported, how can the identity field be created
	 * in the "CREATE TABLE" syntax? For instance: in DB2 this function would
	 * return "not null generated by default as identity" in HsqlDB this function
	 * would return "NOT NULL IDENTITY"    in MySQL  this function would return
	 * "INT NOT NULL AUTO_INCREMENT PRIMARY KEY" in Sybase this function would
	 * return "IDENTITY NOT NULL"
	 */
	public String getCreateIdentitySyntax () throws PersistenceException;

	/**
	 * If Sequence is supported, how can sequence fields be created
	 * For instance:
	 *    in DB2        this function would return "create sequence sequenceName"
	 *    in Oracle     this function would return "create sequence sequenceName"
	 *    in PostgresQL this function would return "create sequence sequenceName"
	 */
	public String getCreateSequenceSyntax (String sequenceName) throws PersistenceException;

	/**
	 * If Sequence is supported, how can sequence fields be dropped
	 * For instance:
	 *    in DB2        this function would return "drop sequence sequenceName restrict"
	 *    in Oracle     this function would return "drop sequence sequenceName"
	 *    in PostgresQL this function would return "drop sequence sequenceName"
	 */
	public String getDropSequenceSyntax (String sequenceName) throws PersistenceException;

	/**
	 * Retrieve the syntax used for inserting an identity column. If Identity is
	 * supported, how can the identity field be inserted in the "INSERT TABLE"
	 * syntax? For instance: in DB2 this function would return "default"    in
	 * HsqlDB this function would return "null"    in MySQL this function would
	 * return "null" in Sybase this function would return "IDENTITY NOT NULL"
	 */
	public String getInsertIdentitySyntax () throws PersistenceException;

	/**
	 * Retrieve the syntax used for retrieving an identity column. If Identity is
	 * supported, how can the identity field be retrieved? For instance:    in DB2
	 * this function would return "values IDENTITY_VAL_LOCAL()"    in HsqlDB this
	 * function would return "CALL IDENTITY()"    in MySQL  this function would
	 * return "SELECT LAST_INSERT_ID()"    in Sybase this function would return
	 * "select @@identity"
	 * @param pmd TODO
	 * @param idFieldName TODO
	 */
	public String getRetrieveIdentitySyntax (PersistentMetaData pmd, String idFieldName) throws PersistenceException;

	/**
	 * Return the SQL syntax used for retrieving a Sequence value, if supported.
	 * If Sequence is supported, how can the sequence field be retrieved? For
	 * instance:    in DB2 this function would return "values nextval for
	 * sequenceName"    in Oracle this function would return "select sequenceName.
	 * nextVal from dual" in PostgresQL this function would return "select nextval
	 * (sequenceName)"
	 */
	public String getRetrieveSequenceSyntax (String sequenceName) throws PersistenceException;

	/**
	 * Is primary key supported in alter statement?
	 */
	public boolean isAlterKeySupported ();

	/**
	 * Some DBs require that identity/auto_inc columns be specified as primary key
	 * in CREATE TABLE.  These fields need to be skipped in ALTER TABLE
	 */
	public boolean skipIdentityInAlterKey ();

	/**
	 * Get the complete database type of a field including all modifiers
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName Field name.
	 * @return The database type.
	 */
	public String getCompleteDBType (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Get the statement to rename a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 * @return The rename statement.
	 */
	public String getRenameFieldStatement (PersistentMetaData pmd, String oldFieldName, String newFieldName)
		throws PersistenceException;

	/**
	 * Rename a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 */
	public void renameField (PersistentMetaData pmd, String oldFieldName, String newFieldName)
		throws PersistenceException;

	/**
	 * Check wether a persistent table contains a specified field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The field name.
	 * @return True if the table contains the field.
	 */
	public boolean containsField (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Get the statement to rename a table.
	 *
	 * @param oldTableName Old table name.
	 * @param newTableName Old table name.
	 * @return The rename statement.
	 */
	public String getRenameTableStatement (String oldTableName, String newTableName) throws PersistenceException;

	/**
	 * Rename a table.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldTableName Old table name.
	 * @param newTableName Old table name.
	 */
	public void renameTable (PersistentMetaData pmd, String oldTableName, String newTableName)
		throws PersistenceException;

	/**
	 * Drop a table.
	 *
	 * @param pmd Persistent meta data.
	 */
	public void dropTable (PersistentMetaData pmd) throws PersistenceException;

	/**
	 * Get the statement to add a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return The add field statement.
	 */
	public String getAddFieldStatement (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Get the statement to update a field to it's default value.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return The update field statement.
	 */
	public String getUpdateFieldDefaultValueStatement (PersistentMetaData pmd, String fieldName)
		throws PersistenceException;

	/**
	 * Add a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 */
	public void addField (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Add fields.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldNames A string array of field names to add.
	 */
	public void addFields (PersistentMetaData pmd, String[] fieldNames) throws PersistenceException;

	/**
	 * Update the type of a table column.
	 *
	 * @param pmd Persistent meta data.
	 * @param oldFieldName Old field name.
	 * @param newFieldName New field name.
	 */
	public void updateTypeField (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Get the statement to delete a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to delete.
	 * @return The add field statement.
	 */
	public String getDeleteFieldStatement (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Delete a field.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to delete.
	 */
	public void deleteField (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Check for field existence.
	 *
	 * @param pmd Persistent meta data.
	 * @param fieldName The name of the field to add.
	 * @return True if the field exists.
	 */
	public boolean hasField (PersistentMetaData pmd, String fieldName) throws PersistenceException;

	/**
	 * Get the 'like' condition statement.
	 *
	 * @return The 'like' condition statement.
	 */
	public String getLikeStatement () throws PersistenceException;
}
