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

package de.iritgo.aktera.ui.form;


import java.util.List;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.permissions.PermissionException;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.string.StringTools;


/**
 *
 */
public class FormularHandler
{
	/** The default handler. */
	private FormularHandler defaultHandler;

	/** Our logger. */
	protected Logger log = new NullLogger();

	/**
	 * Create a new formular handler.
	 */
	public FormularHandler()
	{
	}

	/**
	 * Copy construct a new formular handler.
	 *
	 * @param handler Another formular handler.
	 */
	public FormularHandler(FormularHandler handler)
	{
		defaultHandler = new DefaultFormularHandler();
	}

	/**
	 * Set the default handler.
	 */
	public void setDefaultHandler(FormularHandler defaultHandler)
	{
		this.defaultHandler = defaultHandler;
	}

	/**
	 * Get the default handler.
	 */
	public FormularHandler getDefaultHandler()
	{
		return defaultHandler;
	}

	/**
	 * This method returns the primary key of the base persistent to load.
	 *
	 * @param request A model request.
	 * @param keyName Alternate id name.
	 * @return The persistent id.
	 */
	public Object getPersistentId(ModelRequest request, String formName, String keyName)
	{
		if (! "Y".equals(request.getParameter("new")))
		{
			return defaultHandler.getPersistentId(request, formName, keyName);
		}
		else
		{
			return new Integer(- 1);
		}
	}

	/**
	 * This method is called straight after the creation of the formular descriptor.
	 * You can override this method to change the formular descriptor prior to
	 * any operations on the formular.
	 *
	 * @param request The model request.
	 * @param response The model response.
	 * @param formular The formular.
	 */
	public void prepareFormular(ModelRequest request, ModelResponse response, FormularDescriptor formular)
		throws ModelException, PersistenceException
	{
	}

	/**
	 * This method is called directly after the persistent objects are loaded.
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 */
	public void afterLoad(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
	}

	/**
	 * This method is called after the formular descriptor was created.
	 * You can override this method to change the formularattributes depending
	 * on the request parameters (e.g. enable or disable some field groups).
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 */
	public void adjustFormular(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
	}

	/**
	 * This method is called to load the persistents before the formular
	 * is created.
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 * @param persistentConfig TODO
	 * @param id The base id of the persistents to load.
	 */
	public void loadPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, Integer id) throws ModelException, PersistenceException
	{
		defaultHandler.loadPersistents(request, formular, persistents, persistentConfig, id);
	}

	/**
	 * This method is called before the persistents are stored (update or create) to
	 * the persistent store.
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 */
	public void preStorePersistents(ModelRequest request, FormularDescriptor formular,
					PersistentDescriptor persistents, boolean modified) throws ModelException, PersistenceException
	{
		defaultHandler.preStorePersistents(request, formular, persistents, modified);
	}

	/**
	 * This method is called to update the persistents after the formular
	 * data was validated.
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 * @param persistentConfig TODO
	 */
	public void updatePersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		defaultHandler.updatePersistents(request, formular, persistents, persistentConfig, modified);
	}

	/**
	 * This method is called to create the persistents after the formular
	 * data was validated.
	 *
	 * @param request The model request.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 * @param persistentConfig TODO
	 * @return Return The primary key of the new base persistent.
	 */
	public int createPersistents(ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		return defaultHandler.createPersistents(request, formular, persistents, persistentConfig);
	}

	/**
	 * This method is called to validate the persistents after the formular
	 * data was transfered to the persistents.
	 * @param persistentConfig TODO
	 * @param request The model request.
	 * @param response The model response.
	 * @param formular The formular.
	 * @param persistents The persistent objects.
	 * @param create True if the persistents are about to be created.
	 * @param result The validation result.
	 */
	public void validatePersistents(List<Configuration> persistentConfig, ModelRequest request, ModelResponse response,
					FormularDescriptor formular, PersistentDescriptor persistents, boolean create,
					ValidationResult result) throws ModelException, PersistenceException
	{
		defaultHandler.validatePersistents(persistentConfig, request, response, formular, persistents, create, result);
	}

	/**
	 * This method is called to delete the persistents.
	 *
	 * @param request The model request.
	 * @param response The model response.
	 * @param id TODO
	 * @param persistent The persistent object.
	 * @param systemDelete True if this is an internal system delete.
	 */
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		defaultHandler.deletePersistent(request, response, id, persistent, systemDelete);
	}

	/**
	 * This method is called to delete the persistents.
	 *
	 * @param request The model request.
	 * @param response The model response.
	 * @param id TODO
	 * @param entity The persistent entity.
	 * @param systemDelete True if this is an internal system delete.
	 */
	public void deletePersistent(ModelRequest request, ModelResponse response, Object id, Object entity,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		defaultHandler.deletePersistent(request, response, id, entity, systemDelete);
	}

	/**
	 * This method is called to check wether we are allowed to delete a persistent.
	 *
	 * @param request The model request.
	 * @param id The id of the domain object to delete
	 * @param persistent The persistent object.
	 * @param systemDelete True if this is an internal system delete.
	 * @param result True if the domain object was deleted
	 */
	public boolean canDeletePersistent(ModelRequest request, Object id, Persistent persistent, boolean systemDelete,
					ValidationResult result) throws ModelException, PersistenceException
	{
		return defaultHandler.canDeletePersistent(request, id, persistent, systemDelete, result);
	}

	/**
	 * This method is called to check wether we are allowed to delete a persistent.
	 *
	 * @param request The model request.
	 * @param id The id of the domain object to delete
	 * @param entity The persistent entity.
	 * @param systemDelete True if this is an internal system delete.
	 * @param result True if the domain object was deleted
	 */
	public boolean canDeletePersistent(ModelRequest request, Object id, Object entity, boolean systemDelete,
					ValidationResult result) throws ModelException, PersistenceException
	{
		return defaultHandler.canDeletePersistent(request, id, entity, systemDelete, result);
	}

	/**
	 * Add an error.
	 *
	 * @param response The model response.
	 * @param result The validation result.
	 * @param field The error field.
	 * @param message The error message.
	 */
	protected void addError(ModelResponse response, ValidationResult result, String field, String message)
	{
		FormTools.addError(response, result, field, message);
	}

	/**
	 * Add an error.
	 *
	 * @param response The model response.
	 * @param result The validation result.
	 * @param field The error field.
	 * @param message The error message.
	 * @param bundle The error message's resource bundle.
	 */
	protected void addError(ModelResponse response, ValidationResult result, String field, String message, String bundle)
	{
		FormTools.addError(response, result, field, bundle + ":" + message);
	}

	/**
	 * Set the logger.
	 *
	 * @param log The new logger.
	 */
	public void setLogger(Logger log)
	{
		this.log = log != null ? log : new NullLogger();
	}

	/**
	 * Get the logger.
	 *
	 * @return The logger.
	 */
	public Logger getLogger()
	{
		return log;
	}

	/**
	 * Retrieve the actual user id. This is either the current user, or the user provided
	 * by the request parameter "userId".
	 *
	 * @return The user id
	 * @throws ModelException In case of a general error
	 * @throws PersistenceException in case of a database error
	 * @throws PermissionException If the current user has no permission to upload files
	 * for another user
	 */
	protected Integer getActualUserId(ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		if (! StringTools.isTrimEmpty(request.getParameter("userId")))
		{
			int userId = request.getParameterAsInt("userId", - 1);

			if (! UserTools.currentUserIsInGroup(request, "manager") && UserTools.getCurrentUserId(request) != userId)
			{
				throw new PermissionException("Permission denied to edit com device function keys of user " + userId);
			}

			return userId;
		}

		return UserTools.getCurrentUserId(request);
	}

	/**
	 * Retrieve the actual user name. This is either the current user, or the user provided
	 * by the request parameter "userId".
	 *
	 * @return The user name
	 * @throws ModelException In case of a general error
	 * @throws PersistenceException in case of a database error
	 * @throws PermissionException If the current user has no permission to upload files
	 * for another user
	 */
	protected String getActualUserName(ModelRequest request)
		throws PersistenceException, PermissionException, ModelException
	{
		PersistentFactory pf = (PersistentFactory) request.getService(PersistentFactory.ROLE, request.getDomain());

		String userName = UserTools.getCurrentUserName(request);

		if (! StringTools.isTrimEmpty(request.getParameter("userId")))
		{
			int userId = request.getParameterAsInt("userId", - 1);

			if (! UserTools.currentUserIsInGroup(request, "manager") && UserTools.getCurrentUserId(request) != userId)
			{
				throw new PermissionException("Permission denied to edit com device function keys of user " + userId);
			}

			try
			{
				Persistent user = pf.create("keel.user");

				user.setField("uid", userId);
				user.find();
				userName = user.getFieldString("name");
			}
			catch (PersistenceException x)
			{
			}
		}

		return userName;
	}
}
