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


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import de.iritgo.aktera.hibernate.StandardDao;
import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.persist.PersistenceException;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.simplelife.math.NumberTools;


/**
 *
 */
public class DefaultFormularHandler extends FormularHandler
{
	/**
	 * Create a new formular handler.
	 */
	public DefaultFormularHandler ()
	{
		super ();
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public Object getPersistentId (ModelRequest request, String formName, String keyName)
	{
		return FormTools.getPersistentId (request, formName, keyName);
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void prepareFormular (ModelRequest request, ModelResponse response, FormularDescriptor formular)
		throws ModelException, PersistenceException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void adjustFormular (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void loadPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, Integer id) throws ModelException, PersistenceException
	{
		PersistentFactory persistentManager = (PersistentFactory) request.getService (PersistentFactory.ROLE, request
						.getDomain ());

		try
		{
			if (persistentConfig.size () > 0)
			{
				Iterator persistentConfigIterator = persistentConfig.iterator ();
				Configuration aPersistentConfig = (Configuration) persistentConfigIterator.next ();

				if (aPersistentConfig.getAttribute ("name", null) != null)
				{
					Persistent persistent = persistentManager.create (aPersistentConfig.getAttribute ("name"));

					if (id.intValue () != - 1)
					{
						persistent.setField (aPersistentConfig.getAttribute ("key"), id);
						persistent.find ();
					}

					persistents.put (aPersistentConfig.getAttribute ("id"), persistent);

					for (; persistentConfigIterator.hasNext ();)
					{
						aPersistentConfig = (Configuration) persistentConfigIterator.next ();

						if (aPersistentConfig.getAttribute ("join", null) != null)
						{
							persistent = persistentManager.create (aPersistentConfig.getAttribute ("name"));
							persistents.put (aPersistentConfig.getAttribute ("id"), persistent);

							if (id.intValue () != - 1)
							{
								persistent.setField (aPersistentConfig.getAttribute ("myKey"), persistents
												.getPersistent (aPersistentConfig.getAttribute ("join")).getField (
																aPersistentConfig.getAttribute ("otherKey")));

								persistent.find ();
							}
						}
					}
				}
				else
				{
					StandardDao standardDao = (StandardDao) SpringTools.getBean (StandardDao.ID);
					String entityName = aPersistentConfig.getAttribute ("entity");

					if (id.intValue () != - 1)
					{
						Object bean = standardDao.get (entityName, id);

						persistents.put (aPersistentConfig.getAttribute ("id"), bean);
					}
					else
					{
						try
						{
							Object bean = standardDao.newEntity (entityName);

							try
							{
								MethodUtils.invokeMethod (bean, "init", new Object[0]);
							}
							catch (Exception ignored)
							{
							}

							persistents.put (aPersistentConfig.getAttribute ("id"), bean);
						}
						catch (InstantiationException x)
						{
							log.error ("Unable to create entity: " + entityName, x);
						}
						catch (IllegalAccessException x)
						{
							log.error ("Unable to create entity: " + entityName, x);
						}
					}

					for (; persistentConfigIterator.hasNext ();)
					{
						aPersistentConfig = (Configuration) persistentConfigIterator.next ();

						if (aPersistentConfig.getAttribute ("join", null) != null)
						{
							entityName = aPersistentConfig.getAttribute ("entity");

							if (id.intValue () != - 1)
							{
								try
								{
									Object joinBean = persistents.get (aPersistentConfig.getAttribute ("join"));
									Serializable foreignKey = (Serializable) PropertyUtils.getSimpleProperty (joinBean,
													aPersistentConfig.getAttribute ("key"));
									Object bean = standardDao.get (entityName, foreignKey);

									persistents.put (aPersistentConfig.getAttribute ("id"), bean);
								}
								catch (IllegalAccessException x)
								{
									log.error ("Unable to load entity: " + entityName, x);
								}
								catch (InvocationTargetException x)
								{
									log.error ("Unable to load entity: " + entityName, x);
								}
								catch (NoSuchMethodException x)
								{
									log.error ("Unable to load entity: " + entityName, x);
								}
							}
							else
							{
								try
								{
									Object bean = standardDao.newEntity (entityName);

									try
									{
										MethodUtils.invokeMethod (bean, "init", new Object[0]);
									}
									catch (Exception ignored)
									{
									}

									persistents.put (aPersistentConfig.getAttribute ("id"), bean);
								}
								catch (InstantiationException x)
								{
									log.error ("Unable to create entity: " + entityName, x);
								}
								catch (IllegalAccessException x)
								{
									log.error ("Unable to create entity: " + entityName, x);
								}
							}
						}
					}
				}
			}
		}
		catch (ConfigurationException x)
		{
			throw new ModelException (x);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void preStorePersistents (ModelRequest request, FormularDescriptor formular,
					PersistentDescriptor persistents, boolean modified) throws ModelException, PersistenceException
	{
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void updatePersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig, boolean modified) throws ModelException, PersistenceException
	{
		try
		{
			int id = NumberTools.toInt (request.getParameter ("id"), - 1);

			if (id != - 1 && persistentConfig.size () > 0)
			{
				for (Iterator i = persistentConfig.iterator (); i.hasNext ();)
				{
					Configuration aPersistentConfig = (Configuration) i.next ();

					if (persistents.hasPersistent (aPersistentConfig.getAttribute ("id")))
					{
						persistents.getPersistent (aPersistentConfig.getAttribute ("id")).update ();
					}
					else
					{
						StandardDao standardDao = (StandardDao) SpringTools.getBean (StandardDao.ID);
						Object bean = persistents.get (aPersistentConfig.getAttribute ("id"));

						standardDao.update (bean);
					}
				}
			}
		}
		catch (ConfigurationException x)
		{
			throw new ModelException (x);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public int createPersistents (ModelRequest request, FormularDescriptor formular, PersistentDescriptor persistents,
					List<Configuration> persistentConfig) throws ModelException, PersistenceException
	{
		try
		{
			int id = NumberTools.toInt (request.getParameter ("id"), - 1);

			if (id == - 1 && persistentConfig.size () > 0)
			{
				Iterator persistentConfigIterator = persistentConfig.iterator ();
				Configuration aPersistentConfig = (Configuration) persistentConfigIterator.next ();

				if (persistents.hasPersistent (aPersistentConfig.getAttribute ("id")))
				{
					Persistent persistent = persistents.getPersistent (aPersistentConfig.getAttribute ("id"));

					persistent.add ();
					id = persistent.getFieldInt (aPersistentConfig.getAttribute ("key"));
				}
				else
				{
					StandardDao standardDao = (StandardDao) SpringTools.getBean (StandardDao.ID);
					Object bean = persistents.get (aPersistentConfig.getAttribute ("id"));

					standardDao.create (bean);

					try
					{
						id = (Integer) PropertyUtils.getSimpleProperty (bean, "id");
					}
					catch (IllegalAccessException x)
					{
						log.error ("Unable to create entity: " + aPersistentConfig.getAttribute ("id"));
					}
					catch (InvocationTargetException x)
					{
						log.error ("Unable to create entity: " + aPersistentConfig.getAttribute ("id"));
					}
					catch (NoSuchMethodException x)
					{
						log.error ("Unable to create entity: " + aPersistentConfig.getAttribute ("id"));
					}
				}
			}

			return id;
		}
		catch (ConfigurationException x)
		{
			throw new ModelException (x);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void validatePersistents (List<Configuration> persistentConfig, ModelRequest request,
					ModelResponse response, FormularDescriptor formular, PersistentDescriptor persistents,
					boolean create, ValidationResult result) throws ModelException, PersistenceException
	{
		I18N i18n = (I18N) SpringTools.getBean (I18N.ID);
		javax.validation.ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory ();
		javax.validation.Validator validator = factory.getValidator ();
		try
		{
			if (persistentConfig.size () > 0)
			{
				for (Iterator i = persistentConfig.iterator (); i.hasNext ();)
				{
					Configuration aPersistentConfig = (Configuration) i.next ();
					String id = aPersistentConfig.getAttribute ("id");
					if (! persistents.hasPersistent (id))
					{
						Object bean = persistents.get (aPersistentConfig.getAttribute ("id"));
						Set<ConstraintViolation<Object>> constraintViolations = validator.validate (bean);
						for (javax.validation.ConstraintViolation violation : constraintViolations)
						{
							String fieldName = violation.getPropertyPath ().toString ();
							FieldDescriptor field = formular.getField (id + "." + fieldName);
							result.addError (id + "_" + fieldName.replace ('.', '_'), i18n.msg (request, field
											.getBundle (), field.getLabel () != null ? field.getLabel () : fieldName)
											+ " " + violation.getMessage ());
						}
					}
				}
			}
		}
		catch (ConfigurationException x)
		{
			throw new ModelException (x);
		}
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public void deletePersistent (ModelRequest request, ModelResponse response, Object id, Persistent persistent,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		persistent.delete ();
	}

	@Override
	public void deletePersistent (ModelRequest request, ModelResponse response, Object id, Object entity,
					boolean systemDelete) throws ModelException, PersistenceException
	{
		StandardDao standardDao = (StandardDao) SpringTools.getBean (StandardDao.ID);

		standardDao.delete (entity);
	}

	/**
	 * @see de.iritgo.aktera.ui.form.FormularHandler
	 */
	@Override
	public boolean canDeletePersistent (ModelRequest request, Object id, Persistent persistent, boolean systemDelete,
					ValidationResult result) throws ModelException, PersistenceException
	{
		return true;
	}

	@Override
	public boolean canDeletePersistent (ModelRequest request, Object id, Object entity, boolean systemDelete,
					ValidationResult result) throws ModelException, PersistenceException
	{
		return true;
	}
}
