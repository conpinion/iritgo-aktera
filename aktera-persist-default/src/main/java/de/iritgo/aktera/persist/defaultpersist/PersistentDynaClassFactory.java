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


import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.base.JDBCDatabaseType;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;
import java.util.Iterator;


/**
 * A dynabean factory that will create a dynabean that corresponds to a persistent definition.
 * @author Michael Nash
 */
public class PersistentDynaClassFactory
{
	/**
	 * Static singleton pattern
	 */
	private PersistentDynaClassFactory ()
	{
	}

	public static BasicDynaClass getInstance (Persistent pe)
	{
		try
		{
			PersistentMetaData pmd = pe.getMetaData ();
			int propCount = 0;
			DynaProperty[] props = new DynaProperty[pmd.getFieldNames ().size ()];
			String oneFieldName = null;
			int oneType;

			for (Iterator ef = pmd.getFieldNames ().iterator (); ef.hasNext ();)
			{
				oneFieldName = (String) ef.next ();
				oneType = JDBCDatabaseType.stringToType (pmd.getType (oneFieldName));

				String typeClass = "java.lang.Object";

				switch (oneType)
				{
					case java.sql.Types.ARRAY:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.BIGINT:
						typeClass = "java.math.BigInteger";

						break;

					case java.sql.Types.BINARY:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.BIT:
						typeClass = "java.lang.Integer";

						break;

					case java.sql.Types.BLOB:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.CHAR:
						typeClass = "java.lang.String";

						break;

					case java.sql.Types.CLOB:
						typeClass = "java.lang.String";

						break;

					case java.sql.Types.DATE:
						typeClass = "java.util.Date";

						break;

					case java.sql.Types.DECIMAL:
						typeClass = "java.lang.Double";

						break;

					case java.sql.Types.DISTINCT:
						typeClass = "java.lang.Integer";

						break;

					case java.sql.Types.DOUBLE:
						typeClass = "java.lang.Double";

						break;

					case java.sql.Types.FLOAT:
						typeClass = "java.lang.Float";

						break;

					case java.sql.Types.INTEGER:
						typeClass = "java.lang.Integer";

						break;

					case java.sql.Types.JAVA_OBJECT:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.LONGVARBINARY:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.LONGVARCHAR:
						typeClass = "java.lang.String";

						break;

					case java.sql.Types.NULL:
						typeClass = "java.lang.String";

						break;

					case java.sql.Types.NUMERIC:
						typeClass = "java.lang.Double";

						break;

					case java.sql.Types.OTHER:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.REAL:
						typeClass = "java.lang.Double";

						break;

					case java.sql.Types.REF:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.SMALLINT:
						typeClass = "java.lang.Integer";

						break;

					case java.sql.Types.STRUCT:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.TIME:
						typeClass = "java.util.Date";

						break;

					case java.sql.Types.TIMESTAMP:
						typeClass = "java.util.Date";

						break;

					case java.sql.Types.TINYINT:
						typeClass = "java.lang.Integer";

						break;

					case java.sql.Types.VARBINARY:
						typeClass = "java.lang.Object";

						break;

					case java.sql.Types.VARCHAR:
						typeClass = "java.lang.String";

						break;

					default:

						//--- quikdraw: This should probably throw an exception, but which one?
						break;
				}

				DynaProperty fieldProp = new DynaProperty (oneFieldName, Class.forName (typeClass));

				props[propCount++] = fieldProp;
			}

			return new BasicDynaClass (pe.getName (), Class
							.forName ("de.iritgo.aktera.persist.defaultpersist.PersistentDynaBean"), props);
		}
		catch (Exception e)
		{
			System.err.println ("Unable to get instance of dynaclass");
			e.printStackTrace (System.err);
			throw new IllegalArgumentException (e.getMessage ());
		}
	}
}
