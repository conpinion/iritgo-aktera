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
import de.iritgo.aktera.core.container.AbstractKeelServiceable;
import de.iritgo.aktera.persist.DatabaseType;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.Relation;
import de.iritgo.aktera.util.string.SuperString;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.ids.IdGenerator;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * This object contains the "definition" of the Persistent, while the Persistent
 * itself contains only the data.
 *
 * @version        $Revision: 1.2 $  $Date: 2006/10/11 05:47:28 $
 * @author        Michael Nash
 */
public abstract class AbstractPersistentMetaData extends AbstractKeelServiceable implements Configurable,
				PersistentMetaData, LogEnabled
{
	private class PersistentField
	{
		private Pattern maskPattern;

		private PatternCompiler myCompiler = new Perl5Compiler();

		private String fieldName;

		private String dbFieldName = null;

		/** java.sql.Types type */
		private short fieldType = - 1;

		/** Database type */
		private String databaseFieldType = null;

		/**
		 * Is a null value allowed for this field.
		 */
		private boolean allowNull = true;

		/**
		 * The user friendly description of this field.
		 */
		private String Description = null;

		/**
		 * The size of this field.  ex: 80 for VARCHAR(80)
		 */
		private int fieldSize = 0;

		/**
		 * Is this a virtual field: Ie, does the system provide all values
		 * for this field.
		 */
		private boolean isVirtual = false;

		/* regular expression "mask" for this field, if any */
		private Pattern mask = null;

		/**
		 * User Defined Attributes for each field.
		 */
		private Map attributes = new HashMap();

		/**
		 * Is this a multivalued field
		 */
		private boolean isMultiValued = false;

		/**
		 * Does this field not appear in standard DBMaint listings?
		 */
		private boolean isHidden = false;

		/**
		 * Is this field readonly?
		 */
		private boolean isReadOnly = false;

		/**
		 * String for lookup object classname
		 */
		private String lookupObject = null;

		private String lookupField = null;

		//Added by Santanu Dutt
		private String lookupFieldDisplay = null;

		/**
		 * Is this a key field?
		 */
		private boolean isKey = false;

		/**
		 * The Log associated with PersistentFields
		 */
		private transient Logger logger = null;

		/**
		 * set if the field is autoincremented
		 */
		private String autoIncremented = "";

		/**
		 * Set this value to true if you want a particular string field encrypted
		 * (Not implemented yet)
		 */
		private boolean encrypted = false;

		private String defaultValue = null;

		private boolean currentDateTimeDefault = false;

		private boolean currentTimeDefault = false;

		private boolean currentDateDefault = false;

		/**
		 * Set this value to true if you want a particular string field hashed
		 * instead of stored in plaintext.
		 * (Not implamented yet)
		 */
		private boolean hashed = false;

		/** Field precision, if applicable to this field */
		private int precision = 0;

		/** Static valid values, supplied in the schema.xml */
		private Map staticValidValues = null;

		/** the listName, a key to look up a collection of listitems from
		 * the KeelListHeader, KeelListItem tables. **/
		private String listNameForValidValues = null;

		public void setLogger(Logger newLog)
		{
			logger = newLog;
		}

		/**
		 * Constructor: A PersistentField is initialized knowing it's name, type, size,
		 * description and whether or not it can accept null or empty values
		 *
		 * @param        myName Field name of this field
		 * @param        mType Internal type of the field - a Java type
		 * @param        dType Database type of the field - a Java type
		 * @param        mySize Size of the field in characters
		 * @param        myAllowNull True if null is allowed, false if not
		 * @param        myDescrip Description (title) of the field
		 * @throws  PersistenceException
		 */
		public void populate(String myName, String myDBFieldName, short myType, String dType, int mySize,
						int newPrecision, boolean myAllowNull, String myDescrip) throws PersistenceException
		{
			if (logger == null)
			{
				throw new PersistenceException("Log not set");
			}

			// 			if (myDBFieldName.length() > 18) {
			// 				logger.warn(
			// 					"Field name '"
			// 						+ myName
			// 						+ "' is over 18 characters - may not be portable");
			// 			}
			assert myName != null;

			fieldName = myName;
			dbFieldName = myDBFieldName;

			databaseFieldType = dType;
			fieldType = myType;
			allowNull = myAllowNull;
			Description = myDescrip;
			fieldSize = mySize;
			precision = newPrecision;
		} /* PersistentField(String, String, int, boolean, String) */

		/**
		 * Does this field allow nulls?
		 *
		 * @return        boolean True if the field allows null, else false if it does not
		 */
		public boolean allowsNull()
		{
			return allowNull;
		} /* allowsNull() */

		/**
		 * Return the description of this field
		 *
		 * @return        String Description of the field
		 */
		public String getDescription()
		{
			return Description;
		} /* getDescription() */

		/**
		 * Return the length of the field in characters
		 *
		 * @return
		 */
		public int getLength()
		{
			return fieldSize;
		} /* getLength() */

		/**
		 * Method getListNameForValidValues.
		 * Returns the ListName, a key used to lookup a collection
		 * of valid values from the KeelListHeader, KeelListItem tables.
		 *
		 * @return String
		 */
		public String getListNameForValidValues()
		{
			return listNameForValidValues;
		}

		/**
		 * Return the length of this field as an integer
		 *
		 * @return        int The length of this field in characters
		 */
		public int getLengthInt()
		{
			return fieldSize;
		} /* getLengthInt() */

		/**
		 * Return the value for the lookupObject for this field
		 *
		 * @return
		 */
		public String getLookupObject()
		{
			return lookupObject;
		} /* getLookupObject() */

		/**
		 * Return the name of the field
		 *
		 * @return        String The name of this field
		 */
		public String getName()
		{
			return fieldName;
		} /* getName() */

		/**
		 * Return the precision of this field as an integer
		 *
		 * @return        int The precision of this field
		 */
		public int getPrecision()
		{
			return precision;
		} /* getPrecision() */

		/**
		 * Return the database type of the field as specified with a
		 * string in the Persistent itself
		 *
		 * @return        The type of this field
		 */
		public short getType()
		{
			return fieldType;
		} /* getTypeString() */

		public String getDBType()
		{
			return databaseFieldType;
		}

		public String getDBName()
		{
			return dbFieldName;
		}

		/**
		 * Is this field a key field?
		 *
		 * @return
		 */
		public boolean isKey()
		{
			return isKey;
		} /* isKey() */

		/**
		 * Is this field multi-valued?
		 *
		 * @return        boolean True if the field is multi-valued, else false
		 */
		public boolean isMultiValued()
		{
			return isMultiValued;
		} /* isMultiValued() */

		/**
		 * Return the field's read-only
		 *
		 * @return True if the field is readonly, else false if it is not
		 */
		public boolean isReadOnly()
		{
			return isReadOnly;
		} /* isReadOnly() */

		/**
		 * Return the field's hidden status
		 *
		 * @return True if the field is hidden, else false if it is not
		 */
		public boolean isHidden()
		{
			return isHidden;
		} /* isHidden() */

		/**
		 * Return the field's hashed status
		 *
		 * @todo This is not completely implemented yet.
		 * @return True if the field is secret, else false if it is not
		 */
		public boolean isHashed()
		{
			return hashed;
		} /* isHashed() */

		/**
		 * Return the field's hashed status
		 *
		 * @todo This is not completely implemented yet.
		 * @return True if the field is secret, else false if it is not
		 */
		public boolean isEncrypted()
		{
			return encrypted;
		} /* isEncrypted() */

		/**
		 * Is this field a virtual field? E.g. not stored in the database
		 *
		 * @return        boolean True if the field is virtual, else false
		 */
		public boolean isVirtual()
		{
			return isVirtual;
		} /* isVirtual() */

		/**
		 * Set this field as a key field (or not)
		 *
		 * @param newKey Is this field a key?
		 */
		public void setKey(boolean newKey)
		{
			isKey = newKey;
		} /* setKey(boolean) */

		public void setListNameForValidValues(String newListNameForValidValues)
		{
			listNameForValidValues = newListNameForValidValues;
		}

		/**
		 * Set the value for the "lookup object" for this field. This is
		 * a database object name which can be used to look up valid
		 * values for this field by the user. This is used by the standard
		 * maintenance forms to create a "Lookup" link alongside the
		 * field if this value is set
		 *
		 * @param objectName
		 */
		public synchronized void setLookupObject(String objectName)
		{
			lookupObject = objectName;
		} /* setLookupObject(String) */

		public synchronized void setLookupField(String fieldName)
		{
			lookupField = fieldName;
		}

		public String getLookupField()
		{
			return lookupField;
		}

		/**
		 * Added by Santanu Dutt
		 * This is to set the display field to be used for multi valued fields
		 */
		public synchronized void setLookupFieldDisplay(String displayName)
		{
			lookupFieldDisplay = displayName;
		}

		public String getLookupFieldDisplay()
		{
			return lookupFieldDisplay;
		}

		/**
		 * Set this field to be "multi-valued". A multi-valued field has
		 * a specific set of valid values, often from another database
		 * object. Any multi-valued field may be used in a call to the
		 * getValues method, which will return a hashtable of the valid
		 * values for the field and descriptions for those values.
		 *
		 * @param        newMulti True if the field is multi-valued, false if it is not
		 */
		public synchronized void setMultiValued(boolean newMulti)
		{
			isMultiValued = newMulti;
		} /* setMultiValued(boolean) */

		/**
		 * Set the field's hashed status.  Only works if the field is a string
		 * data type (in the future CLOB should be ok too)
		 *
		 * @param newValue
		 * @thows Exception
		 */
		public synchronized void setHashed(boolean newValue) throws Exception
		{
			String curType = JDBCDatabaseType.typeToString(getType());

			if (curType.equals("text") || curType.equals("varchar") || curType.equals("char"))
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Setting field " + fieldName + " to hashed status");
				}

				encrypted = true;
			}
			else
			{
				throw new Exception(fieldName + " Field needs to be a string data type field");
			}
		} /* setHashed(boolean) */

		/**
		 * Set the field's encrypted status
		 * @todo This is not completely implemented yet.
		 *
		 * @param newValue
		 * @throws Exception if the field type is not a string data type
		 */
		public synchronized void setEncrypted(boolean newValue) throws Exception
		{
			String curType = JDBCDatabaseType.typeToString(getType());

			if (curType.equals("text") || curType.equals("varchar") || curType.equals("text"))
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Setting field " + fieldName + " to encrypted status");
				}

				encrypted = true;
			}
			else
			{
				throw new Exception("Field needs to be a string data type field");
			}
		} /* setEncrypted(boolean) */

		/**
		 * Set the field as a read-only field. Read only fields are still used
		 * against the database, but are not offered for updating when the
		 * automatic database maintenance servlet creates a form on the screen. Note
		 * this is different from the setAutoIncremented method below, which
		 * means this field will not participate in any add or update statement
		 * to the database.
		 */
		public synchronized void setReadOnly()
		{
			isReadOnly = true;
		} /* setReadOnly() */

		/**
		 * Set the field as an autoincremented field, which
		 * means this field will not participate in any add or update statement
		 * to the database.
		 *
		 * @param newAutoIncremented
		 */
		public synchronized void setAutoIncremented(String newAutoIncremented)
		{
			autoIncremented = newAutoIncremented;
		} /* setAutoIncremented(boolean) */

		/**
		 * Is this field an auto-incremented field?
		 *
		 * @return
		 */
		public boolean isAutoIncremented()
		{
			boolean returnValue = false;

			if (! autoIncremented.equals(""))
			{
				returnValue = true;
			}

			return returnValue;
		} /* isAutoIncremented() */

		public String getAutoIncremented()
		{
			return autoIncremented;
		}

		/**
		 * Set the field as a 'hidden' field. Hidden fields are not shown
		 * in listings of data from this database object, and are only available
		 * to users with update, add or delete permissions
		 */
		public synchronized void setHidden()
		{
			isHidden = true;
		} /* setHidden() */

		/**
		 * Set this field as a virtual field. A virtual field is part of the object
		 * but not stored in the database table.
		 *
		 * @param        newVirtual True to make this object virtual, false if it is not
		 */
		public synchronized void setVirtual(boolean newVirtual)
		{
			isVirtual = newVirtual;
		} /* setVirtual(boolean) */

		/**
		 * Set a regular expression "mask" for this field that specifies it's
		 * valid values.  The mask should already be compiled by the regular
		 * expression compiler
		 */
		public void setMask(Pattern newMask)
		{
			mask = newMask;
		}

		/**
		 * Get the compiled regular expression for this field.
		 */
		public Pattern getMask()
		{
			return mask;
		}

		public synchronized void setAttribute(String attribName, Object attribValue)
		{
			if (attributes == null)
			{
				attributes = new HashMap();
			}

			attributes.put(attribName, attribValue);
		}

		public Object getAttribute(String attribName)
		{
			return attributes.get(attribName);
		}

		public Map getAttributes()
		{
			return attributes;
		}

		private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
		{
			stream.defaultReadObject();
		}

		public void enableLogging(Logger newLogger)
		{
			logger = newLogger;
		}

		public void setPattern(String patternMask)
		{
			try
			{
				maskPattern = myCompiler.compile(patternMask);
			}
			catch (MalformedPatternException me)
			{
				throw new IllegalArgumentException(me.getMessage());
			}
		}

		public Pattern getPattern()
		{
			return maskPattern;
		}

		public void addValidValue(String value, String descrip)
		{
			if (staticValidValues == null)
			{
				staticValidValues = new HashMap();
			}

			staticValidValues.put(value, descrip);
		}

		public Map getValidValues()
		{
			return staticValidValues;
		}

		public String getDefaultValue()
		{
			return defaultValue;
		}

		public void setDefaultValue(String newDefault)
		{
			defaultValue = newDefault;
		}

		public boolean isDefaultCurrentDateTime()
		{
			return currentDateTimeDefault;
		}

		public boolean isDefaultCurrentDate()
		{
			return currentDateDefault;
		}

		public boolean isDefaultCurrentTime()
		{
			return currentTimeDefault;
		}

		public void setDefaultCurrentDateTime(boolean newDef)
		{
			currentDateTimeDefault = newDef;
		}

		public void setDefaultCurrentDate(boolean newDef)
		{
			currentDateDefault = newDef;
		}

		public void setDefaultCurrentTime(boolean newDef)
		{
			currentTimeDefault = newDef;
		}
	} /* PersistentField */

	/**
	 * Bean class that describes a database index.
	 */
	class Index
	{
		/**
		 * The name of the index.
		 */
		private String indexName = null;

		/**
		 * A comma delimited list of fieldNames that belong in the Index.
		 */
		private String fieldNames = null;

		/**
		 * Specifies whether or not the index is supposed to have unique entries
		 * or not.  If specified unique, if a duplicate value is entered in the
		 * index, an exception will be thrown when the Persistent is written to the
		 * database.
		 */
		private boolean createWithTable = false;

		private boolean unique = false;

		private String myTableName = null;

		/**
		 * All in one constructor
		 * @param theIndexName The unique name of this index
		 * @param theTableName The name of the table this index is attached to
		 * @param theFieldNames A comma delimited list of fields in the table
		 * @param isItUnique Set it to true if you wish for the index to not have
		 *          duplicate entries
		 */
		public Index(String theIndexName, String theTableName, String theFieldNames, boolean isItUnique)
		{
			indexName = theIndexName;
			myTableName = theTableName;
			fieldNames = theFieldNames;
			unique = isItUnique;
		}

		public String getTableName()
		{
			return myTableName;
		}

		public String getIndexName()
		{
			return indexName;
		}

		public void setIndexName(String newIndexName)
		{
			indexName = newIndexName;
		}

		/**
		 * Return the comma-delimited list of field names in this index.
		 * NOTE: These are *internal* field names, not the names used in the database
		 * itself.
		 * @return A string containing a comma-delimited list of field names in this index
		 */
		public String getFieldNames()
		{
			return fieldNames;
		}

		/**
		 * Is this a unique index?
		 * @return
		 */
		public boolean isUnique()
		{
			return unique;
		}

		/**
		 * Specify a list of field names (*internal* field names) in this index.
		 * @param newFieldNames
		 */
		public void setFieldNames(String newFieldNames)
		{
			fieldNames = newFieldNames;
		}

		public void setUnique(boolean newUnique)
		{
			unique = newUnique;
		}

		/**
		 *  Sets the boolean that determines whether index is
		 * automatically created when table is created.
		 */
		public void setCreateWithTable(boolean createWithTable)
		{
			this.createWithTable = createWithTable;
		}

		public boolean createWithTable()
		{
			return createWithTable;
		}
	}

	private static Logger log = null;

	private static final String[] validFieldAttribs =
	{
					"name", "db-name", "type", "length", "precision", "null-allowed", "descrip", "read-only",
					"auto-increment", "primary-key", "multi-valued", "index", "default-value"
	};

	private DatabaseType myDatabaseType = null;

	private DataSourceComponent myDataSource = null;

	private boolean collectStatistics = false;

	private int cacheSize = 0;

	private String helperClassName = null;

	private String implClassName = null;

	private String id = null;

	private PersistentFactory myFactory = null;

	private Map relations = new HashMap();

	private boolean rowSecurable = false;

	private Configuration myConf = null;

	/**
	 * A persistent may have a "page-size" attribute
	 * set for it, in which case this is used as a display
	 * hint when displaying lists of this persistent
	 */
	private int pageSize = 0;

	private Set indicies = new HashSet();

	/** A map of all of the PersistentField objects in this Persistent, in the order they were defined
	 * The key to the map is the field name, as it appears in the configuration file
	 */
	private Map allFields = new TreeMap();

	/**
	 * The set of key field names, again in the order they were defined
	 */
	private Set keys = new TreeSet();

	private String objectDescription = "";

	/**
	 * The list of all indicies used by this Persistent.
	 */
	private Map indexList = new HashMap();

	/**
	 * Persistent name
	 */
	private String name = null;

	/**
	 * Table name
	 */
	private String tableName = null;

	/**
	 * A default characterset to filter on.
	 */
	private String charSet = "ISO-8859-1";

	private String myName = null;

	/** What schema does this Persistent belong to? */
	private String mySchema = null;

	//Added by Santanu Dutt for Securable Persistent object
	private boolean securable = false;

	private String defaultAmHint = null;

	protected AuthorizationManager authMgr = null;

	/**
	 * Is bypassing of authorization allowed?
	 */
	private boolean authMgrBypassAllowed = false;

	protected Configuration getConfiguration()
	{
		return myConf;
	}

	/**
	 * Private utility function to check reserved words against our database type
	 */
	private final void checkReservedWord(String word) throws PersistenceException
	{
		/* TODO: Put this in a map for faster lookup, rather than iterating every time */
		Iterator allReserved = myDatabaseType.getReservedWords().iterator();
		String oneReserved = null;

		while (allReserved.hasNext())
		{
			oneReserved = (String) allReserved.next();

			if (oneReserved.equalsIgnoreCase(word))
			{
				throw new PersistenceException(
								"You cannot have a field or table name of '"
												+ word
												+ "'.  It is a reserved word for this database type.  Check database documentation for a full list.");
			}
		}
	}

	/**
	 * Add a field with more details: This version allows the user to specify a
	 * precision, for fields that use both a size and precision.
	 *
	 * @param    fieldName Name of the field
	 * @param    fieldType Type of the field - this is the java.sql.Types type,
	 *          mapping in PersistentField to a specific database data type.
	 * @param    fieldSize Size of the field
	 * @param   fieldPrecision The precision of the field
	 * @param    allowNull Does this field allow nulls?
	 * @param   descriptionKey A key in the local language files for the
	 *          description of this field.
	 * @param    fieldDescription A longer description of this field
	 *            (user-understandable hopefully!)
	 * @throws  PersistenceException
	 */
	private synchronized void addField(String fieldName, String dbFieldName, short fieldType, int fieldSize,
					int fieldPrecision, boolean allowNull, String fieldDescription) throws PersistenceException
	{
		assert fieldName != null;

		checkReservedWord(fieldName);

		PersistentField p = new PersistentField();

		p.setLogger(log);
		p.populate(fieldName, dbFieldName, fieldType, getDatabaseType().getDBType(
						JDBCDatabaseType.typeToString(fieldType)), fieldSize, fieldPrecision, allowNull,
						fieldDescription);
		allFields.put(fieldName, p);
	} /* addField(String, String, int, int, boolean, String) */

	public final String getDefaultValue(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getField(fieldName);

		if (oneField.isDefaultCurrentDateTime())
		{
			return new Timestamp(System.currentTimeMillis()).toString();
		}

		if (oneField.isDefaultCurrentDate())
		{
			return new Date(System.currentTimeMillis()).toString();
		}

		if (oneField.isDefaultCurrentTime())
		{
			return new Time(System.currentTimeMillis()).toString();
		}

		return oneField.getDefaultValue();
	}

	/**
	 *
	 *
	 * @param fieldName
	 * @throws PersistenceException
	 * @return
	 */
	public final boolean allowsNull(String fieldName) throws PersistenceException
	{
		return getField(fieldName).allowsNull();
	} /* allowsNull(String) */

	/**
	 *
	 *
	 * @param fieldName
	 * @throws PersistenceException
	 * @return
	 */
	public final Object getAttribute(String fieldName, String attribName) throws PersistenceException
	{
		return getField(fieldName).getAttribute(attribName);
	} /* allowsNull(String) */

	public final Map getAttributes(String fieldName) throws PersistenceException
	{
		return getField(fieldName).getAttributes();
	}

	private PersistentField getField(String fieldName) throws PersistenceException
	{
		PersistentField oneField = (PersistentField) allFields.get(fieldName);

		if (oneField == null)
		{
			throw new PersistenceException("(" + getName() + ") Field " + fieldName
							+ " is not defined as a field in persistent '" + getSchemaName() + "." + getName() + "'");
		}

		return oneField;
	}

	/**
	 * Add an index to the table.
	 *
	 * @param indexName the name to give the index in the table
	 * @param fieldNames A comma delimited list of all fields in the index.
	 * @param isUnique - True if this field is a unique index.
	 * @throws IllegalArgumentException of fieldName is null or doesn't exist
	 *      or if indexName is null
	 */
	private Index addIndex(String indexName, String fieldNames, boolean isUnique) throws IllegalArgumentException
	{
		//
		//                Begin Argument Validation
		//
		if (indexName == null)
		{
			throw new IllegalArgumentException("DBOBJ_Add_Index_IllegalArgument1");
		}

		if (fieldNames == null)
		{
			throw new IllegalArgumentException("DBOBJ_Add_Index_IllegalArgument2");
		}

		//
		//Iterate through all the field names and make sure that they exist
		//
		String tempString = null;

		//Check to make sure that these fields really do exist
		//in the table they're being called for.
		StringTokenizer stk = new StringTokenizer(fieldNames, ",");

		while (stk.hasMoreTokens())
		{
			tempString = stk.nextToken();

			//
			//Will throw a PersistenceException if the field doesn't exist.
			//
			try
			{
				this.getPersistentField(tempString);
			}
			catch (PersistenceException e)
			{
				throw new IllegalArgumentException("Persistent.addIndex(): " + tempString
								+ " doesn't exist as a valid field");
			}
		}

		//
		//                End Argument Validation
		//
		Index retval = new Index(indexName, getTableName(), fieldNames, isUnique);

		indexList.put(indexName, retval);

		return retval;
	} /* addIndex(String, String, boolean) */

	/**
	 * Add a new field to the list of fields that are part of this
	 * object's key.
	 *
	 * @param    keyFieldName The name of the field to add as part of the key
	 * @throws    PersistenceException if the field name is not valid or the field
	 *             allows nulls
	 */
	private synchronized void addKey(String keyFieldName) throws PersistenceException
	{
		PersistentField oneField = (PersistentField) allFields.get(keyFieldName);

		if (oneField == null)
		{
			throw new PersistenceException("(" + getName() + ") Field " + keyFieldName
							+ " is not defined as a field in this Persistent - cannot add " + "to key for persistent "
							+ getName() + " in schema " + getSchemaName());
		}

		if (oneField.allowsNull())
		{
			throw new PersistenceException("(" + getName() + ") Field " + keyFieldName
							+ " allows null - not suitable for inclusion " + "in key for persistent " + getName()
							+ " in schema " + getSchemaName());
		}

		if (oneField.isVirtual())
		{
			throw new PersistenceException("(" + getName() + ") Field " + keyFieldName
							+ " is a virtual field - not suitable for " + "inclusion in key for persistent "
							+ getName() + " in schema " + getSchemaName());
		}

		oneField.setKey(true);
		keys.add(keyFieldName);
	} /* addKey(String) */

	/**
	 * return the current object's character set
	 *
	 * @return
	 */
	public final String getCharset()
	{
		return charSet;
	} /* getCharset() */

	/**
	 * Set a characterset for a particular field.
	 * See the Keel filter service for more information on Filters and implementing Filters for your own
	 * characterset.
	 *
	 * Sets the characterset expected for a this Persistent.  Default
	 * is &quot;ISO-8859-1&quot;
	 *
	 * @param charSet The name of the characterset that you will filter against.
	 * For Western-Latin character sets use &quot;ISO-8859-1&quot; as the parameter.
	 * @see de.iritgo.aktera.filter.Filter;
	 */
	synchronized void setCharset(String newCharSet) throws PersistenceException
	{
		charSet = newCharSet;
	} /* setCharset(String) */

	/**
	 * Return a description of this database object. If no explicit description has
	 * been set, return the class name.
	 *
	 * @return A string describing this database object
	 */
	public final String getDescription()
	{
		return objectDescription;
	} /* getDescription() */

	/**
	 * Return the long description of a field, if available
	 *
	 * @param    fieldName The name of the field
	 * @return    String: The long description of the field (user-readable)
	 * @throws    PersistenceException If there is no such field
	 */
	public final String getDescription(String fieldName) throws PersistenceException
	{
		PersistentField oneField = (PersistentField) allFields.get(fieldName);

		if (oneField == null)
		{
			throw new PersistenceException("No such field '" + fieldName + "'" + " in object '" + getName() + "'");
		}

		return oneField.getDescription();
	} /* getDescription(String) */

	/**
	 *
	 *
	 * @param fieldName
	 * @throws PersistenceException
	 * @return
	 */
	private PersistentField getPersistentField(String fieldName) throws PersistenceException
	{
		SuperString.assertNotBlank(fieldName, "Field name may not be blank");

		if (allFields.size() == 0)
		{
			throw new PersistenceException("Object " + getSchemaName() + "." + getName() + "'");
		}

		PersistentField oneField = (PersistentField) allFields.get(fieldName);

		if (oneField == null)
		{
			throw new PersistenceException("No such field as '" + fieldName + "' in object '" + getSchemaName() + "."
							+ getName() + "'");
		}

		return oneField;
	} /* getPersistentField(String) */

	/**
	 * Get a list of all of the fields in this object
	 *
	e     * @return    A Vector of all of the fieldNames in this object
	 * @throws    PersistenceException If the list cannot be retrieved
	 */
	public final Set getFieldNames()
	{
		return allFields.keySet();
	} /* getFieldList() */

	/**
	 * Get a Set of all of the names of the key fields in this object
	 *
	 * @return    A Set of names of the key fields
	 */
	public final Set getKeyFieldNames()
	{
		return keys;
	} /* getKeyFieldNames() */

	/**
	 * Return the length of a field
	 *
	 * @param    fieldName The name of the field
	 * @return    String: The length of the field
	 * @throws    PersistenceException If there is no such field in this object
	 */
	public final int getLength(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.getLength();
	} /* getLength(String) */

	/**
	 *
	 *
	 * @param fieldName
	 * @throws PersistenceException
	 * @return
	 */
	public final int getPrecision(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.getPrecision();
	} /* getPrecision(String) */

	/**
	 * Get a field's lookup object - this is the name of another database
	 * object that can be used to look up valid values for this object. The lookup
	 * object for a field is set in configuration file, and is used
	 * by the crud model to provide automatic lookup links for fields.
	 *
	 * @param fieldName
	 * @return
	 * @throws PersistenceException If the specified field does not exist.
	 */
	public final String getLookupObject(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.getLookupObject();
	} /* getLookupObject(String) */

	/**
	 * Added by Santanu Dutt
	 * Get a field in lookup object - this is used as keys in validValues
	 *
	 * @param fieldName
	 * @return
	 * @throws PersistenceException If the specified field does not exist.
	 */
	public final String getLookupField(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.getLookupField();
	} /* getLookupField(String) */

	/**
	 * Added by Santanu Dutt
	 * Get a field in lookup object for display - this is used as desc in validValues
	 *
	 * @param fieldName
	 * @return
	 * @throws PersistenceException If the specified field does not exist.
	 */
	public final String getLookupFieldDisplay(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.getLookupFieldDisplay();
	} /* getLookupFieldDisplay(String) */

	/**
	 * Get the name of this object
	 *
	 * @return  String The persistent's name
	 */
	public final String getName()
	{
		return myName;
	}

	/**
	 * Return the Table Name of the current Persistent object.
	 *
	 * @return String: Table name of this Persistent object
	 */
	public final String getTableName()
	{
		return tableName;
	} /* getTableName() */

	/**
	 * Get the type used to store the given field in the database
	 */
	public final String getDBType(String fieldName) throws PersistenceException
	{
		return getDatabaseType().getDBType(getType(fieldName));
	}

	/**
	 * Return the type of a field - this method returns the internal java.sql.Types type
	 *
	 * @param    fieldName The name of the field
	 * @return    String: The type of the field
	 * @throws    PersistenceException If there is no such field in this object
	 */
	public final String getType(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return JDBCDatabaseType.typeToString(oneField.getType());
	} /* getType(String) */

	public final Set getIndicies()
	{
		return indicies;
	}

	public final Set getIndexedFields(String indexName) throws PersistenceException
	{
		HashSet retval = new HashSet();
		Index index = (Index) indexList.get(indexName);
		StringTokenizer st = new StringTokenizer(index.getFieldNames(), ",");

		while (st.hasMoreTokens())
		{
			retval.add(st.nextToken());
		}

		return retval;
	}

	/**
	 * This method will return a boolean true if the field is defined in the persistent,
	 * false otherwise.
	 * @return boolean
	 * @param fieldName java.lang.String
	 */
	public final boolean hasField(String fieldName)
	{
		assert fieldName != null;

		if (allFields.containsKey(fieldName))
		{
			return true;
		}

		return false;
	} /* hasField(String) */

	/**
	 * Method called to determine if a particular field is multi-valued,
	 * that is does it have a set of specific values and descriptions
	 *
	 * @param    fieldName Name of the field
	 * @return    boolean True if the field is multi-valued, false if not
	 * @throws    PersistenceException If there is no such field
	 */
	public final boolean isMultiValued(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.isMultiValued();
	} /* isMultiValued(String) */

	/**
	 * Is a given field readOnly - these fields are not offered for entry
	 * when a form is produced by CRUD
	 *
	 * @param fieldName
	 * @return True of the field is "read only", false if it is not
	 * @throws PersistenceException If there is no such field
	 */
	public final boolean isReadOnly(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		if (oneField == null)
		{
			throw new PersistenceException("No such field '" + fieldName + "'" + "' in object '" + getName() + "'");
		}

		return oneField.isReadOnly();
	} /* isReadOnly(String) */

	/**
	 * Is a given field 'hidden' - these fields are not shown
	 * when a list is produced by the CRUD model.
	 * This means that only users with update permission to the record can see the
	 * value of the specified field.
	 *
	 * @see setHidden(String)
	 * @param fieldName The name of the field to check
	 * @return True if the field is 'hidden', false if it is not
	 * @throws PersistenceException If there is no such field.
	 */
	public final boolean isHidden(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		return oneField.isHidden();
	} /* isSecret(String) */

	/**
	 * Set the name of this object - this name is used to identify the db object
	 * with a more human-readable description
	 *
	 * @param    theName New name for this object
	 */
	private final synchronized void setName(String theName)
	{
		myName = theName;
	} /* setName(String) */

	/**
	 * Set a field as read-only - these fields are not offered for update
	 * when a form is produced by the generic database maintenance servlet
	 * (DBMaint). It does not otherwise affect the use of the field.
	 * <p>Typical uses are for serial-numbered fields and timestamps</p>
	 *
	 * @param fieldName The name of the field to be reserved as normally read-only.
	 * @throws PersistenceException If there is no such field.
	 */
	private final synchronized void setReadOnly(String fieldName) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		oneField.setReadOnly();
	} /* setReadOnly(String) */

	/**
	 * Set a field as read-only - this field will not be included in any insert
	 * or update operations to the table
	 * <p>Typical uses are for serial-numbered fields and timestamps</p>
	 *
	 * @param fieldName The name of the field to be reserved as normally read-only.
	 * @throws PersistenceException If there is no such field.
	 */
	private synchronized void setAutoIncremented(String fieldName, String inc) throws PersistenceException
	{
		PersistentField oneField = getPersistentField(fieldName);

		oneField.setAutoIncremented(inc);
	} /* setAutoIncremented(String) */

	/**
	 * Our factory object passes us the schema name *
	 * @param schemaName
	 */
	public final synchronized void setSchemaName(String schemaName)
	{
		SuperString.assertNotBlank(schemaName, "Schemaa name may not be null or blank");
		mySchema = schemaName;
	} /* setSchema(String) */

	public final void enableLogging(Logger newLog)
	{
		log = newLog;
	}

	/**
	 * We get handed by our factory a Configuration object which contains all of the necessary information
	 * to create the meta-data information.
	 */
	public final void configurePersistent(Configuration myConfig) throws PersistenceException
	{
		try
		{
			if (myConfig == null)
			{
				throw new PersistenceException("Configuration may not be null here");
			}

			/* top-level items should be a "persistent" element */
			if (! myConfig.getName().equals("persistent"))
			{
				throw new PersistenceException("Configuration must be from 'persistent' element, not '"
								+ myConfig.getName() + "'. Check config file format.");
			}

			setName(myConfig.getAttribute("name"));

			id = myConfig.getAttribute("id", "");

			authMgrBypassAllowed = myConfig.getAttributeAsBoolean("am-bypass-allowed", false);

			helperClassName = myConfig.getAttribute("helper", null);
			implClassName = myConfig.getAttribute("class", null);

			pageSize = myConfig.getAttributeAsInteger("page-size", 0);
			name = SuperString.notNull(myConfig.getAttribute("name"));

			if (name.equals(""))
			{
				throw new PersistenceException("persistent element must have a name attribute");
			}

			tableName = SuperString.notNull(myConfig.getAttribute("table"));

			if (tableName.equals(""))
			{
				throw new PersistenceException("persistent '" + name + "' must have a table attribute");
			}

			objectDescription = myConfig.getAttribute("descrip", getName());

			//added by Santanu Dutt for Securable persistent Objects
			if (myConfig.getAttributeAsBoolean("securable", false))
			{
				setSecurable(true);
			}

			if (myConfig.getAttributeAsBoolean("row-securable", false))
			{
				setSecurable(true);
				rowSecurable = true;
			}

			int fieldCount = 0;
			Configuration[] children = myConfig.getChildren();
			Configuration oneChild = null;

			for (int i = 0; i < children.length; i++)
			{
				oneChild = children[i];

				if (oneChild.getName().equals("field"))
				{
					fieldCount++;
					configureField(oneChild);
				}
				else if (oneChild.getName().equals("detail"))
				{
					/* Configure a detail relation from this persistent to another */
					DefaultRelation r = new DefaultRelation(Relation.DETAIL, oneChild.getAttribute("name"), getName(),
									oneChild.getAttribute("persistent"));
					String fromString = oneChild.getAttribute("fromFields");
					String toString = oneChild.getAttribute("toFields");
					StringTokenizer stk = new StringTokenizer(fromString, ",");

					while (stk.hasMoreTokens())
					{
						r.addFromField(stk.nextToken().trim());
					}

					stk = new StringTokenizer(toString, ",");

					while (stk.hasMoreTokens())
					{
						r.addToField(stk.nextToken().trim());
					}

					relations.put(oneChild.getAttribute("name"), r);
				}
				else if (oneChild.getName().equals("relation"))
				{
					DefaultRelation r = new DefaultRelation(Relation.OTHER, oneChild.getAttribute("name"), getName(),
									oneChild.getAttribute("persistent"));
					String fromString = oneChild.getAttribute("fromFields");
					String toString = oneChild.getAttribute("toFields");
					StringTokenizer stk = new StringTokenizer(fromString, ",");

					while (stk.hasMoreTokens())
					{
						r.addFromField(stk.nextToken().trim());
					}

					stk = new StringTokenizer(toString, ",");

					while (stk.hasMoreTokens())
					{
						r.addToField(stk.nextToken().trim());
					}

					relations.put(oneChild.getAttribute("name"), r);
				}
				else if (oneChild.getName().equals("default-data"))
				{
					/* Do nothing - we don't deal with default-data here, it's only read */
					/* at the time the table is created */
				}
				else if (oneChild.getName().equals("index"))
				{
					/* records fields to be indexed. */
					String indexName = oneChild.getAttribute("name");
					boolean isUnique = new Boolean(oneChild.getAttribute("is-unique")).booleanValue();
					boolean createWithTable = new Boolean(oneChild.getAttribute("create-with-table")).booleanValue();
					Configuration[] indexedFields = oneChild.getChildren();
					String fieldNames = "";

					for (int j = 0; j < indexedFields.length; j++)
					{
						String columnName = indexedFields[j].getAttribute("name");

						fieldNames += columnName;

						if (j + 1 != indexedFields.length)
						{
							fieldNames += ",";
						}
					}

					Index index = addIndex(indexName, fieldNames, isUnique);

					indicies.add(index);
					index.setCreateWithTable(createWithTable);
				}
				else
				{
					log.warn("Unknown child of 'persistent' element '" + name + "', '" + oneChild.getName()
									+ "' was ignored");
				}
			}

			if (fieldCount == 0)
			{
				throw new ConfigurationException("No fields in persistent '" + name + "' for schema '"
								+ getSchemaName() + "', id '" + id
								+ "', and persistent does not use 'use-factory' option.");
			}

			if (isSecurable())
			{
				String amHint = myConfig.getAttribute("am", defaultAmHint);

				try
				{
					authMgr = (AuthorizationManager) getService(AuthorizationManager.ROLE, amHint);
				}
				catch (Exception e)
				{
					log.error("Could not get service " + AuthorizationManager.ROLE + "/" + amHint);
					throw new PersistenceException(e);
				}
			}
		}
		catch (ConfigurationException ce)
		{
			throw new PersistenceException(ce);
		}
	}

	//Added by Santanu Dutt for Securable Persistent object
	private void setSecurable(boolean s)
	{
		this.securable = s;
	}

	//Added by Santanu Dutt for Securable Persistent object
	public final boolean isSecurable()
	{
		if (isRowSecurable())
		{
			return true;
		}

		return this.securable;
	}

	private void checkAttribName(String name) throws ConfigurationException
	{
		for (int i = 0; i < validFieldAttribs.length; i++)
		{
			if (name.equals(validFieldAttribs[i]))
			{
				return;
			}
		}

		throw new ConfigurationException("Attribute '" + name + "' is not a valid "
						+ "attribute for a field definition");
	}

	private void configureField(Configuration config) throws PersistenceException, ConfigurationException
	{
		Set reservedWords = getDatabaseType().getReservedWords();

		String[] usedNames = config.getAttributeNames();

		for (int i = 0; i < usedNames.length; i++)
		{
			checkAttribName(usedNames[i]);
		}

		String fieldName = config.getAttribute("name");

		if (fieldName.equals(""))
		{
			throw new PersistenceException("Field element must have a name attribute");
		}

		String dbFieldName = config.getAttribute("db-name", "");

		/* If we don't specify a db-name, then the name is used as both the internal name and the database name */
		if (dbFieldName.equals(""))
		{
			dbFieldName = fieldName;
		}

		if (reservedWords.contains(dbFieldName))
		{
			throw new PersistenceException("Field '" + fieldName + "' cannot use '" + dbFieldName
							+ "' as a database column/field name, as it is a reserved word in this type of database");
		}

		String fieldType = config.getAttribute("type");

		if (fieldType.equals(""))
		{
			throw new PersistenceException("Field '" + fieldName + "' must have a valid type specified");
		}

		/* Check for a valid type */
		short theType;

		try
		{
			theType = JDBCDatabaseType.stringToType(fieldType);
		}
		catch (IllegalArgumentException pe)
		{
			throw new PersistenceException("Unable to use type '" + fieldType + "' for field '" + fieldName
							+ "' in persistent '" + getName() + "'", pe);
		}

		addField(fieldName, dbFieldName, theType, config.getAttributeAsInteger("length", 0), config
						.getAttributeAsInteger("precision", 0), config.getAttributeAsBoolean("null-allowed", true),
						config.getAttribute("descrip", fieldName));

		if (config.getAttributeAsBoolean("read-only", false))
		{
			setReadOnly(fieldName);
		}

		String inc = config.getAttribute("auto-increment", "");

		setAutoIncremented(fieldName, inc);

		if (config.getAttributeAsBoolean("primary-key", false))
		{
			addKey(fieldName);
		}

		if (config.getAttributeAsBoolean("multi-valued", false))
		{
			PersistentField oneField = (PersistentField) allFields.get(fieldName);

			oneField.setMultiValued(true);
		}

		/* Now look for children, which specify valid values etc */
		Configuration[] children = config.getChildren();
		Configuration oneChild = null;

		for (int i = 0; i < children.length; i++)
		{
			oneChild = children[i];

			if (oneChild.getName().equals("valid-values"))
			{
				PersistentField oneField = (PersistentField) allFields.get(fieldName);

				oneField.setMultiValued(true);

				Configuration[] vv = oneChild.getChildren();
				Configuration oneVv = null;

				for (int j = 0; j < vv.length; j++)
				{
					oneVv = vv[j];

					if (oneVv.getName().equals("valid-value"))
					{
						oneField.addValidValue(oneVv.getAttribute("value"), oneVv.getAttribute("descrip"));
					}
					else
					{
						log.error("Unknown child of 'valid-values' element" + oneVv.getName());
					}
				}
			}
			else if (oneChild.getName().equals("lookup"))
			{
				PersistentField oneField = (PersistentField) allFields.get(fieldName);

				oneField.setLookupObject(oneChild.getAttribute("name"));
				oneField.setLookupField(oneChild.getAttribute("field"));

				// Added by Santanu Dutt
				// This is to set the display field to be used for multi valued fields if one is specified
				// If not then the lookup field itself is used
				boolean displayPresent = false;
				String[] attrs = oneChild.getAttributeNames();

				for (int j = 0; j < attrs.length; j++)
				{
					if ("display".equals(attrs[j]))
					{
						displayPresent = true;
					}
				}

				if (displayPresent)
				{
					oneField.setLookupFieldDisplay(oneChild.getAttribute("display"));
				}
				else
				{
					oneField.setLookupFieldDisplay(oneChild.getAttribute("field"));
				}
			}
			else if (oneChild.getName().equals("list-valid-values"))
			{
				PersistentField oneField = (PersistentField) allFields.get(fieldName);

				oneField.setListNameForValidValues(oneChild.getAttribute("name"));
			}
			else if (oneChild.getName().equals("default-value"))
			{
				boolean useDateTime = oneChild.getAttributeAsBoolean("current-datetime", false);
				boolean useDate = oneChild.getAttributeAsBoolean("current-date", false);
				boolean useTime = oneChild.getAttributeAsBoolean("current-time", false);
				PersistentField oneField = (PersistentField) allFields.get(fieldName);

				if (useDateTime || useDate || useTime)
				{
					if (useDateTime)
					{
						oneField.setDefaultCurrentDateTime(true);
					}
					else if (useDate)
					{
						oneField.setDefaultCurrentDate(true);
					}
					else if (useTime)
					{
						oneField.setDefaultCurrentTime(true);
					}
				}
				else
				{
					String defValue = oneChild.getValue();

					if ((defValue == null) || (defValue.equals("")))
					{
						throw new ConfigurationException("No default supplied for field '" + fieldName
										+ "' in persistent '" + getName() + "' - must supply either attribute or value");
					}

					oneField.setDefaultValue(oneChild.getValue());
				}
			}
			else if (oneChild.getName().equals("multi-valued"))
			{
				PersistentField oneField = (PersistentField) allFields.get(fieldName);

				oneField.setMultiValued(true);
			}
			else if (oneChild.getName().equals("attribute"))
			{
				PersistentField oneField = (PersistentField) allFields.get(fieldName);

				oneField.setAttribute(oneChild.getAttribute("name"), oneChild.getAttribute("value"));
			}
			else
			{
				throw new ConfigurationException("Unknown child of 'field' element '" + name + "', '"
								+ oneChild.getName() + "' in persistent '" + getName() + "'");
			}
		}
	}

	public final Map getStaticValidValues(String fieldName) throws PersistenceException
	{
		PersistentField oneField = (PersistentField) allFields.get(fieldName);

		if (oneField == null)
		{
			throw new PersistenceException("No such field '" + fieldName + "'" + " in object '" + getName() + "'");
		}

		Map returnMap = oneField.getValidValues();

		if (returnMap == null)
		{
			return new HashMap();
		}

		return returnMap;
	}

	/**
	 * @see de.iritgo.aktera.persist.PersistentMetaData#hasListValidValues(String)
	 */
	public final boolean hasListValidValues(String fieldName) throws PersistenceException
	{
		PersistentField oneField = (PersistentField) allFields.get(fieldName);
		boolean returnValue = false;

		if (oneField == null)
		{
			throw new PersistenceException("No such field '" + fieldName + "'" + " in object '" + getName() + "'");
		}

		String listName = oneField.getListNameForValidValues();

		if (listName != null)
		{
			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * @see de.iritgo.aktera.persist.PersistentMetaData#getListValidValues(String)
	 */
	public final Map getListValidValues(String fieldName) throws PersistenceException
	{
		PersistentField oneField = (PersistentField) allFields.get(fieldName);

		if (oneField == null)
		{
			throw new PersistenceException("No such field '" + fieldName + "'" + " in object '" + getName() + "'");
		}

		String listName = oneField.getListNameForValidValues();

		if (listName == null)
		{
			throw new PersistenceException("There is no listNameForValidValues " + "specified for field	 '" + fieldName
							+ "' in object '" + getName() + "'.");
		}

		Persistent listHeader = getFactory().create("keel.KeelListHeader");

		listHeader.setField("ListName", listName);

		if (! listHeader.find())
		{
			throw new PersistenceException("There is no list name of '" + listName
							+ "' defined in KeelListHeader, as specified for field	 '" + fieldName + "' in object '"
							+ getName() + "'.");
		}

		boolean useSortOrder = false;

		if ("Y".equalsIgnoreCase(listHeader.getFieldString("UseSortOrder")))
		{
			useSortOrder = true;
		}

		Persistent lookupObj = getFactory().create("keel.KeelListItem");

		lookupObj.setField("ListName", listName);

		List listItems = null;

		if (useSortOrder)
		{
			listItems = lookupObj.query("SortOrder");
		}
		else
		{
			listItems = lookupObj.query("ItemName");
		}

		Map vv = new LinkedHashMap();

		for (Iterator i = listItems.iterator(); i.hasNext();)
		{
			Persistent oneListItem = (Persistent) i.next();

			vv.put(oneListItem.getFieldString("ItemCode"), oneListItem.getFieldString("ItemName"));
		}

		return vv;
	}

	public final Set getDetailFieldsFrom(String relationName) throws PersistenceException
	{
		return null;
	}

	public final Set getDetailFieldsTo(String relationName) throws PersistenceException
	{
		return null;
	}

	public final String getSchemaName()
	{
		return mySchema;
	}

	public final void setDatabaseType(DatabaseType newType)
	{
		myDatabaseType = newType;
	}

	public final DatabaseType getDatabaseType()
	{
		return myDatabaseType;
	}

	//    public Set cs(String indexName) {
	//        return null;
	//    }

	//TODO: Is this method a dup of getDBName?
	public final String getDatabaseName(String fieldName) throws PersistenceException
	{
		return getPersistentField(fieldName).getDBName();
	}

	public final IdGenerator getIdGenerator(String fieldName) throws PersistenceException
	{
		IdGenerator generator = null;
		PersistentField oneField = getPersistentField(fieldName);
		String hint = oneField.getAutoIncremented();

		if (hint.equals("identity"))
		{
			return null;
		}

		if (hint.equals("table"))
		{
			/* Use the table name as the hint */
			hint = getTableName();
		}

		try
		{
			generator = (IdGenerator) getService(IdGenerator.ROLE, hint);

			return generator;
		}
		catch (ServiceException ce)
		{
			throw new PersistenceException("Unable to create id generator service for field '" + fieldName
							+ "' in persistent '" + getSchemaName() + "." + getName() + "' using hint '" + hint
							+ "' - have you configured an IdGenerator called '" + hint + "'?", ce);
		}
	}

	public final boolean collectStatistics()
	{
		return collectStatistics;
	}

	public final int getCacheSize()
	{
		return cacheSize;
	}

	public final String getHelperClassName()
	{
		return helperClassName;
	}

	public final boolean isKeyField(String fieldName)
	{
		boolean returnValue = false;

		if (keys.contains(fieldName))
		{
			returnValue = true;
		}

		return returnValue;
	}

	public final boolean isAutoIncremented(String fieldName) throws PersistenceException
	{
		return getPersistentField(fieldName).isAutoIncremented();
	}

	public final String getDBFieldName(String fieldName) throws PersistenceException
	{
		return getPersistentField(fieldName).getDBName();
	}

	public final Pattern getPattern(String fieldName) throws PersistenceException
	{
		return getPersistentField(fieldName).getPattern();
	}

	public final boolean isEncrypted(String fieldName) throws PersistenceException
	{
		return getPersistentField(fieldName).isEncrypted();
	}

	public final void setDataSource(DataSourceComponent newDataSource)
	{
		myDataSource = newDataSource;
	}

	public final DataSourceComponent getDataSource()
	{
		return myDataSource;
	}

	public final int getPageSize()
	{
		return pageSize;
	}

	public final String getClassName()
	{
		return implClassName;
	}

	public final void setFactory(PersistentFactory newFactory)
	{
		myFactory = newFactory;
	}

	public final PersistentFactory getFactory()
	{
		return myFactory;
	}

	public final Set getRelationNames()
	{
		return relations.keySet();
	}

	public final Relation getRelation(String relName)
	{
		return (Relation) relations.get(relName);
	}

	public final Set getDetailNames()
	{
		HashSet subSet = new HashSet();
		Relation oneRelation = null;
		String oneName = null;

		for (Iterator i = relations.keySet().iterator(); i.hasNext();)
		{
			oneName = (String) i.next();
			oneRelation = (Relation) relations.get(oneName);

			if (oneRelation.getType() == Relation.DETAIL)
			{
				subSet.add(oneName);
			}
		}

		return subSet;
	}

	/**
	 * @see de.iritgo.aktera.persist.PersistentMetaData#isAuthManagerBypassAllowed()
	 */
	public final boolean isAuthManagerBypassAllowed()
	{
		return authMgrBypassAllowed;
	}

	public final AuthorizationManager getAuthManager()
	{
		return authMgr;
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public final void configure(Configuration configuration) throws ConfigurationException
	{
		myConf = configuration;

		defaultAmHint = configuration.getChild("default-am", true).getValue("*");
	}

	/**
	 * @see de.iritgo.aktera.persist.PersistentMetaData#isRowSecurable()
	 */
	public final boolean isRowSecurable()
	{
		return rowSecurable;
	}

	/**
	 * @see de.iritgo.aktera.persist.PersistentMetaData#getAutoIncremented(java.lang.String)
	 */
	public Object getAutoIncremented(String oneFieldName) throws PersistenceException
	{
		return getPersistentField(oneFieldName).getAutoIncremented();
	}
}
