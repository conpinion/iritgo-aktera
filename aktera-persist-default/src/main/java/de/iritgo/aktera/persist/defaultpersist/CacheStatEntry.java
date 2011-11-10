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


/**
 * @version                 $Revision: 1.2 $ $Date: 2003/07/04 15:37:06 $
 */
public class CacheStatEntry
{
	private String dbobjName = null;

	private String dbName = null;

	private int readCount = 0;

	private int cacheHits = 0;

	private int cacheMisses = 0;

	/** Creates new CacheStatEntry */
	public CacheStatEntry(String newName, String newDBName)
	{
		dbobjName = newName;
		dbName = newDBName;
	}

	public int getReadCount()
	{
		return readCount;
	}

	public int getCacheHits()
	{
		return cacheHits;
	}

	public int getCacheMisses()
	{
		return cacheMisses;
	}

	public String getDBName()
	{
		return dbName;
	}

	public String getDBObjName()
	{
		return dbobjName;
	}

	public void incCounts(boolean cached)
	{
		if (cached)
		{
			cacheHits++;
		}
		else
		{
			cacheMisses++;
		}

		readCount++;
	}
}
