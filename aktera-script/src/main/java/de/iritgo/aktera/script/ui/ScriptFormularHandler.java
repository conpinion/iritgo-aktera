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

package de.iritgo.aktera.script.ui;


import de.iritgo.aktera.event.EventManager;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.script.ScriptCompilerException;
import de.iritgo.aktera.script.ScriptManager;
import de.iritgo.aktera.ui.form.FormTools;
import de.iritgo.aktera.ui.form.FormularDescriptor;
import de.iritgo.aktera.ui.form.FormularHandler;
import de.iritgo.aktera.ui.form.PersistentDescriptor;
import de.iritgo.aktera.ui.form.ValidationResult;
import org.apache.avalon.framework.configuration.Configuration;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;


/**
 *
 */
public class ScriptFormularHandler extends FormularHandler
{
	/** The script manager */
	private ScriptManager scriptManager;

	/** The script manager */
	private EventManager em;

	/**
	 * @param scriptManager The new scriptManager.
	 */
	public void setScriptManager(ScriptManager scriptManager)
	{
		this.scriptManager = scriptManager;
	}

	/**
	 * Set the event manager
	 *
	 * @param em The event manager
	 */
	public void setEventManager(EventManager em)
	{
		this.em = em;
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler#loadPersistents(de.iritgo.aktera.model.ModelRequest, de.iritgo.aktera.ui.form.FormularDescriptor, de.iritgo.aktera.ui.form.PersistentDescriptor, java.util.List, java.lang.Integer)
	 */
	@Override
	public void loadPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, Integer id) throws ModelException, PersistenceException
	{
		super.loadPersistents(request, formular, persistents, persistentConfig, id);
		persistents.putAttribute("oldName", persistents.getPersistent("script").getFieldString("name"));
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public void adjustFormular(final ModelRequest request, final FormularDescriptor formular,
					final PersistentDescriptor persistents) throws ModelException, PersistenceException
	{
		TreeMap<String, String> languages = new TreeMap<String, String>();

		persistents.putAttributeValidValues("script.language", languages);

		for (String language : scriptManager.listCompilerNames())
		{
			languages.put(language, "$" + language);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
		Persistent script = persistents.getPersistent("script");

		try
		{
			scriptManager.check(script.getFieldString("code"), script.getFieldString("language"));
		}
		catch (ScriptCompilerException x)
		{
			FormTools.addError(response, result, "script.code", "#\n" + x.getMessage());
			FormTools.addError(response, result, "script.aaa", "aktera-script:scriptCodeContainsErrors");
		}
		catch (Exception ignored)
		{
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public void updatePersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		super.updatePersistents(request, formular, persistents, persistentConfig, modified);
		scriptManager.invalidate(persistents.getPersistent("script").getFieldString("name"));

		String oldScriptName = (String) persistents.getAttribute("oldName");
		String newScriptName = (String) persistents.getPersistent("script").getFieldString("name");

		if (newScriptName.equals(oldScriptName))
		{
			return;
		}

		Properties eventProps = new Properties();

		eventProps.put("oldName", oldScriptName);
		eventProps.put("newName", newScriptName);
		em.fire("aktera.script.script-rename", eventProps);
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		super.deletePersistent(request, response, id, persistent, systemDelete);
		scriptManager.invalidate(persistent.getFieldString("name"));
	}
}
