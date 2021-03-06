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


import de.iritgo.aktera.persist.base.AbstractPersistentMetaData;


/**
 * This object contains the "definition" of the Persistent, while the Persistent
 * itself contains only the data.
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.persist.PersistentMetaData
 * @x-avalon.info name=default-metadata
 * @x-avalon.lifestyle type=transient
 *
 *
 * @version        $Revision: 1.9 $  $Date: 2004/02/05 22:21:31 $
 * @author        Michael Nash
 */
public class DefaultPersistentMetaData extends AbstractPersistentMetaData
{
	/* We use only the default capabilities from the abstract */
}
