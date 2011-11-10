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

package de.iritgo.aktera.journal.ui;


import java.util.List;
import org.apache.avalon.framework.configuration.Configuration;
import de.iritgo.aktera.journal.JournalManager;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.ValidationResult;


/**
 * Formular handler for journal domain objects.
 */
public class JournalFormularHandler extends FormularHandler
{
	/** The address manager */
	private JournalManager journalManager;

	/**
	 * Set the journal manager.
	 *
	 * @param journalManager The journal manager
	 */
	public void setJournalManager(JournalManager journalManager)
	{
		this.journalManager = journalManager;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#loadPersistents(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor, java.lang.Integer)
	 */
	public void loadPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					Integer id) throws ModelException, PersistenceException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#validatePersistents(List, de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor, boolean, de.iritgo.aktera.ui.form.ValidationResult)
	 */
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#createPersistents(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor)
	 */
	public int createPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		return 0;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public void updatePersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					boolean modified) throws ModelException, PersistenceException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#deletePersistent(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.model.ModelResponse, java.lang.Object, de.iritgo.aktera.persist.Persistent, boolean)
	 */
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
	}
}
