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


import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentMetaData;
import de.iritgo.aktera.persist.base.JDBCDatabaseType;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaClass;
import java.util.Iterator;


/**
 * @author root
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PersistentDynaBean extends BasicDynaBean
{
	/**
	 * Constructor for PersistentDynaBean.
	 * @param arg0
	 */
	public PersistentDynaBean (DynaClass arg0)
	{
		super (arg0);
	}

	/**
	 * Preferred Constructor - build the PersistentDynaBean directly from a Persistent
	 * @param pe The persistent to build this bean from
	 */
	public PersistentDynaBean (Persistent pe)
	{
		super (PersistentDynaClassFactory.getInstance (pe));
	}

	public void setPersistent (Persistent pe) throws PersistenceException
	{
		PersistentMetaData pmd = pe.getMetaData ();
		String oneFieldName = null;
		int oneType;

		for (Iterator ef = pmd.getFieldNames ().iterator (); ef.hasNext ();)
		{
			oneFieldName = (String) ef.next ();
			oneType = JDBCDatabaseType.stringToType (pmd.getType (oneFieldName));

			switch (oneType)
			{
				case java.sql.Types.ARRAY:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.BIGINT:
					set (oneFieldName, new Integer (pe.getFieldInt (oneFieldName)));

					break;

				case java.sql.Types.BINARY:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.BIT:
					set (oneFieldName, new Integer (pe.getFieldInt (oneFieldName)));

					break;

				case java.sql.Types.BLOB:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.CHAR:
					set (oneFieldName, pe.getFieldString (oneFieldName));

					break;

				case java.sql.Types.CLOB:
					set (oneFieldName, pe.getFieldString (oneFieldName));

					break;

				case java.sql.Types.DATE:
					set (oneFieldName, pe.getFieldDate (oneFieldName));

					break;

				case java.sql.Types.DECIMAL:
					set (oneFieldName, new Double (pe.getFieldString (oneFieldName)));

					break;

				case java.sql.Types.DISTINCT:
					set (oneFieldName, new Integer (pe.getFieldInt (oneFieldName)));

					break;

				case java.sql.Types.DOUBLE:
					set (oneFieldName, new Double (pe.getFieldString (oneFieldName)));

					break;

				case java.sql.Types.FLOAT:
					set (oneFieldName, new Float (pe.getFieldString (oneFieldName)));

					break;

				case java.sql.Types.INTEGER:
					set (oneFieldName, new Integer (pe.getFieldInt (oneFieldName)));

					break;

				case java.sql.Types.JAVA_OBJECT:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.LONGVARBINARY:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.LONGVARCHAR:
					set (oneFieldName, pe.getFieldString (oneFieldName));

					break;

				case java.sql.Types.NULL:
					set (oneFieldName, pe.getFieldString (oneFieldName));

					break;

				case java.sql.Types.NUMERIC:
					set (oneFieldName, new Double (pe.getFieldString (oneFieldName)));

					break;

				case java.sql.Types.OTHER:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.REAL:
					set (oneFieldName, new Double (pe.getFieldString (oneFieldName)));

					break;

				case java.sql.Types.REF:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.SMALLINT:
					set (oneFieldName, new Integer (pe.getFieldInt (oneFieldName)));

					break;

				case java.sql.Types.STRUCT:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.TIME:
					set (oneFieldName, pe.getFieldDate (oneFieldName));

					break;

				case java.sql.Types.TIMESTAMP:
					set (oneFieldName, pe.getFieldDate (oneFieldName));

					break;

				case java.sql.Types.TINYINT:
					set (oneFieldName, new Integer (pe.getFieldInt (oneFieldName)));

					break;

				case java.sql.Types.VARBINARY:
					set (oneFieldName, pe.getField (oneFieldName));

					break;

				case java.sql.Types.VARCHAR:
					set (oneFieldName, pe.getFieldString (oneFieldName));

					break;

				default:
					throw new PersistenceException ("Invalid JDBC Data Type.");
			}
		}
	}

	public String toString ()
	{
		final StringBuffer sb = new StringBuffer (values.size () * 16 + 32);

		sb.append (getClass ().getName () + ":" + dynaClass.getClass ().getName ());

		if (values.size () > 0)
		{
			sb.append ("\nKey/Values;\n");

			for (final Iterator it = values.keySet ().iterator (); it.hasNext ();)
			{
				final Object key = it.next ();

				sb.append ("" + key + '/' + values.get (key) + '\n');
			} //end for
		}
		else
		{
			sb.append ("\nEmpty Bean.");
		}

		return (sb.toString ());
	}
}
