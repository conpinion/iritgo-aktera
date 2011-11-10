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


/**
 * A "Helper" is an object that is related to a particular persistent
 * object, containing methods and logic that are connected logically
 * to the persistent object itself. It also receives events whenever
 * the persistent object is updated, read, etc
 */
public interface Helper
{
	/**
	 * Called when the helper is first instantiated
	 */
	public void setPersistent(Persistent current);

	/**
	 * Called before the Add operation is performed
	 * @param current The Persistent object before the add operation. Fields such
	 * as auto-incremented keys will not yet have their values assigned.
	 */
	public void beforeAdd(Persistent current);

	/**
	 * Called after the Add operation is completed successfully
	 * @param current The Persistent object after the add operation. Fields
	 * such as auto-incremented keys will have their values assigned properly
	 */
	public void afterAdd(Persistent current);

	/**
	 * Called before the Delete operation is performed
	 * @param current The Persistent object about to be deleted
	 */
	public void beforeDelete(Persistent current);

	/**
	 * Called before the Update operation is performed
	 * @param current The Persistent object before the update
	 */
	public void beforeUpdate(Persistent current);

	/**
	 * Called after the Update operation is performed
	 * @param current The Persistent object that has just been updated
	 */
	public void afterUpdate(Persistent current);

	/**
	 * Called after the Delete operation is performed
	 * @param current The Persistent object which has just been deleted
	 */
	public void afterDelete(Persistent current);

	/**
	 * Called before the Clear operation is performed
	 * @param current The Persistent object before it is cleared
	 */
	public void beforeClear(Persistent current);

	/**
	 * Called before each access to set the field value of a Persistent
	 * @param fieldName The field name whose value is being altered
	 * @param oldValue The field value before the changed is applied
	 * @param oldValue The field value requested
	 */
	public void beforeSetField(String fieldName, Object oldValue, Object newValue);

	/**
	 * Called after a field is changed, assuming the change was allowed (e.g. by validations, type, etc)
	 * @param fieldName The field name that was changed
	 * @param oldValue The field value before the change
	 * @param newValue The updated field value
	 */
	public void afterSetField(String fieldName, Object oldValue, Object newValue);

	/**
	 * Called before the query operation is performed
	 * @param current The Persistent object before the query - contains the constraint
	 * values that will be used to perform the query itself
	 */
	public void beforeQuery(Persistent current);

	/**
	 * Called after a query operation
	 * @param current The Persistent object after the query - essentially identical to before
	 */
	public void afterQuery(Persistent current);
}
