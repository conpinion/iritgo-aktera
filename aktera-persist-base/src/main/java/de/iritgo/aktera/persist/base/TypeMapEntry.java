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


/**
 * A TypeMapEntry represents one entry in the map of database types to java.sql.Type types  used by
 * a particular database type
 * @version                 $Revision: 1.1 $ $Date: 2004/03/27 16:02:18 $
 */
public class TypeMapEntry
{
	/**
	 * Name of the type used by the database
	 */
	private String typeName = null;

	/**
	 * The type as defined in java.sql.Types (these are the internal types, and used in the config file)
	 */
	private short dataType = 0;

	/**
	 * PRECISION int => maximum precision
	 */
	private int precision = 0;

	/**
	 * LITERAL_PREFIX String => prefix used to quote a literal
	 */
	private String literalPrefix = null;

	/**
	 * LITERAL_SUFFIX String => suffix used to quote a literal
	 */
	private String literalSuffix = null;

	/**
	 * Parameters used in creating the type (may be null)
	 */
	private String createParams = null;

	/**
	 * Can you use NULL for this type?
	 * <pre>
	 * <ul>
	 * <li>typeNoNulls - does not allow NULL values </li>
	 * <li>typeNullable - allows NULL values</li>
	 * <li>typeNullableUnknown - nullability unknown</li>
	 * </pre>
	 */
	private short nullable = 0;

	/**
	 * Is this type case sensitive?
	 */
	private boolean caseSensitive = false;

	/**
	 * Can you use "WHERE" based on this type?
	 * <pre>
	 * <ul>
	 * <li>typePredNone - No support</li>
	 * <li>typePredChar - Only supported with WHERE .. LIKE</li>
	 * <li>typePredBasic - Supported except for WHERE .. LIKE</li>
	 * <li>typeSearchable - Supported for all WHERE ..</li>
	 * </ul>
	 * </pre>
	 */
	private short searchable = 0;

	/**
	 * UNSIGNED_ATTRIBUTE boolean => is it unsigned?
	 */
	private boolean unsigned = true;

	/**
	 * FIXED_PREC_SCALE boolean => can it be a money value?
	 */
	private boolean fixedPrecisionScale = false;

	/**
	 * AUTO_INCREMENT boolean => can it be used for an
	 * auto-increment value?
	 */
	private boolean autoIncrement = false;

	/**
	 * LOCAL_TYPE_NAME String => localized version of type name
	 *  (may be null)
	 */
	private String localTypeName = null;

	/**
	 * MINIMUM_SCALE short => minimum scale supported
	 */
	private short minScale = 0;

	/**
	 * MAXIMUM_SCALE short => maximum scale supported
	 */
	private short maxScale = 0;

	/**
	 * NUM_PREC_RADIX int => usually 2 or 10
	 */
	private int numPrecRadix = 0;

	public void setTypeName(String newName)
	{
		typeName = newName;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setDataType(short newType)
	{
		dataType = newType;
	}

	public int getDataType()
	{
		return dataType;
	}

	public void setPrecision(int newPrecision)
	{
		precision = newPrecision;
	}

	public int getPrecision()
	{
		return precision;
	}

	public void setLiteralPrefix(String newPrefix)
	{
		literalPrefix = newPrefix;
	}

	public String getLiteralPrefix()
	{
		return literalPrefix;
	}

	public void setLiteralSuffix(String newSuffix)
	{
		literalSuffix = newSuffix;
	}

	public String getLiteralSuffix()
	{
		return literalSuffix;
	}

	public void setCreateParams(String newParams)
	{
		createParams = newParams;
	}

	public String getCreateParams()
	{
		return createParams;
	}

	public void setNullable(short newNullable)
	{
		nullable = newNullable;
	}

	public short getNullable()
	{
		return nullable;
	}

	public void setCaseSensitive(boolean newCase)
	{
		caseSensitive = newCase;
	}

	public boolean getCaseSensitive()
	{
		return caseSensitive;
	}

	public void setSearchable(short newSearchable)
	{
		searchable = newSearchable;
	}

	public short getSearchable()
	{
		return searchable;
	}

	public void setFixedPrecision(boolean newPrecision)
	{
		fixedPrecisionScale = newPrecision;
	}

	public boolean getFixedPrecision()
	{
		return fixedPrecisionScale;
	}

	public void setUnsigned(boolean newUnsigned)
	{
		unsigned = newUnsigned;
	}

	public boolean getUnsigned()
	{
		return unsigned;
	}

	public void setMaxScale(short newMax)
	{
		maxScale = newMax;
	}

	public short getMaxScale()
	{
		return maxScale;
	}

	public void setMinScale(short newMin)
	{
		minScale = newMin;
	}

	public short getMinScale()
	{
		return minScale;
	}

	public void setNumPrecRadix(int newPrec)
	{
		numPrecRadix = newPrec;
	}

	public int getNumPrecRadix()
	{
		return numPrecRadix;
	}

	public void setAutoIncrement(boolean newAuto)
	{
		autoIncrement = newAuto;
	}

	public boolean getAutoIncrement()
	{
		return autoIncrement;
	}

	public void setLocalTypeName(String newName)
	{
		localTypeName = newName;
	}

	public String getLocalTypeName()
	{
		return localTypeName;
	}
}
