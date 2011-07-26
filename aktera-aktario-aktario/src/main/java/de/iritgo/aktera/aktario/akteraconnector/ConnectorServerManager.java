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

package de.iritgo.aktera.aktario.akteraconnector;


import de.iritgo.aktario.core.Engine;
import de.iritgo.aktario.core.base.BaseObject;
import de.iritgo.aktario.core.iobject.IObject;
import de.iritgo.aktario.core.iobject.IObjectList;
import de.iritgo.aktario.core.iobject.IObjectProxy;
import de.iritgo.aktario.core.logger.Log;
import de.iritgo.aktario.core.manager.Manager;
import de.iritgo.aktario.core.plugin.PluginStateEvent;
import de.iritgo.aktario.framework.action.ActionTools;
import de.iritgo.aktario.framework.base.DataObject;
import de.iritgo.aktario.framework.base.FrameworkProxy;
import de.iritgo.aktario.framework.base.IObjectCreatedEvent;
import de.iritgo.aktario.framework.base.IObjectCreatedListener;
import de.iritgo.aktario.framework.base.IObjectDeletedEvent;
import de.iritgo.aktario.framework.base.IObjectDeletedListener;
import de.iritgo.aktario.framework.base.IObjectModifiedEvent;
import de.iritgo.aktario.framework.base.IObjectModifiedListener;
import de.iritgo.aktario.framework.base.action.EditIObjectAction;
import de.iritgo.aktario.framework.dataobject.AnnounceDynDataObjectResponse;
import de.iritgo.aktario.framework.dataobject.DynDataObject;
import de.iritgo.aktario.framework.dataobject.gui.CommandDescription;
import de.iritgo.aktario.framework.dataobject.gui.Controller;
import de.iritgo.aktario.framework.dataobject.gui.GUIControllerMissingEvent;
import de.iritgo.aktario.framework.dataobject.gui.GUIControllerMissingListener;
import de.iritgo.aktario.framework.dataobject.gui.GUIControllerRequest;
import de.iritgo.aktario.framework.dataobject.gui.GUIManager;
import de.iritgo.aktario.framework.dataobject.gui.WidgetDescription;
import de.iritgo.aktario.framework.user.User;
import de.iritgo.aktario.framework.user.UserEvent;
import de.iritgo.aktario.framework.user.UserListener;
import de.iritgo.aktera.authentication.AuthenticationManager;
import de.iritgo.aktera.authentication.DefaultUserEnvironment;
import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.context.KeelContextualizable;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.persist.Persistent;
import de.iritgo.aktera.persist.PersistentFactory;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 *
 */
public class ConnectorServerManager extends BaseObject implements Manager, IObjectCreatedListener,
				IObjectModifiedListener, IObjectDeletedListener, GUIControllerMissingListener, UserListener
{
	private Map keelIritgoAuthMap;

	/**
	 * Create a new client manager.
	 */
	public ConnectorServerManager ()
	{
		super ("ConnectorServerManager");
		keelIritgoAuthMap = new HashMap ();
	}

	/**
	 * Initialize the client manager.
	 */
	public void init ()
	{
		Engine.instance ().getEventRegistry ().addListener ("Plugin", this);
		Engine.instance ().getEventRegistry ().addListener ("akteraobjectcreated", this);
		Engine.instance ().getEventRegistry ().addListener ("akteraobjectmodified", this);
		Engine.instance ().getEventRegistry ().addListener ("akteraobjectremoved", this);
		Engine.instance ().getEventRegistry ().addListener ("guimanager", this);
		Engine.instance ().getEventRegistry ().addListener ("User", this);
	}

	/**
	 * Called when an aktera object was created.
	 *
	 * @param event The creation event.
	 */
	public void iObjectCreatedEvent (IObjectCreatedEvent event)
	{
	}

	/**
	 * Called when an aktera object was modified.
	 *
	 * @param event The modification event.
	 */
	public void iObjectModifiedEvent (IObjectModifiedEvent event)
	{
	}

	/**
	 * Called when an aktera object was deleted.
	 *
	 * @param event The delete event.
	 */
	public void iObjectDeletedEvent (IObjectDeletedEvent event)
	{
	}

	public void pluginEvent (PluginStateEvent event)
	{
	}

	/**
	 * This method is called when user has logged in.
	 *
	 * Loggin/Logoff the user in the keel-framework.
	 *
	 * @param event The user event.
	 */
	public void userEvent (UserEvent event)
	{
		if ((event != null) && (event.isLoggedIn ()))
		{
			User user = event.getUser ();

			ModelRequest req = null;

			try
			{
				req = ModelTools.createModelRequest ();

				AuthenticationManager authMgr = (AuthenticationManager) req.getService (AuthenticationManager.ROLE,
								"default");

				authMgr.setUsername (user.getName ());
				authMgr.setPassword (event.getPlainPassword ());
				authMgr.setDomain ("default");

				UserEnvironment userEnvironment = new DefaultUserEnvironment ();
				Context context = new DefaultContext ();

				((DefaultContext) context).put (UserEnvironment.CONTEXT_KEY, userEnvironment);
				authMgr.login (userEnvironment);
				keelIritgoAuthMap.put (new Long (user.getUniqueId ()), context);
			}
			catch (Exception x)
			{
				x.printStackTrace ();
			}
			finally
			{
				ModelTools.releaseModelRequest (req);
			}
		}

		if ((event != null) && (event.isLoggedOut ()))
		{
			User user = event.getUser ();

			ModelRequest req = null;

			try
			{
				req = ModelTools.createModelRequest ();

				AuthenticationManager authMgr = (AuthenticationManager) req
								.getService (AuthenticationManager.ROLE, "*");

				authMgr.setUsername (user.getName ());
				authMgr.setPassword ("");
				authMgr.setDomain ("default");

				Context c = (Context) keelIritgoAuthMap.get (new Long (user.getUniqueId ()));
				UserEnvironment userEnv = (UserEnvironment) c.get (UserEnvironment.CONTEXT_KEY);

				authMgr.logout (userEnv);
				keelIritgoAuthMap.remove (user.getUniqueId ());
			}
			catch (Exception x)
			{
			}
			finally
			{
				ModelTools.releaseModelRequest (req);
			}
		}
	}

	/**
	 * Create a new iritgo object from a keel object.
	 *
	 * @param model The keel model query
	 */
	public long newKeelObject (String model, long userUniqueId)
	{
		KeelResponse keelForm = getKeelObject (model, "-1", userUniqueId);

		DynDataObject dataObject = null;

		dataObject = createDataObjectAndRegister (keelForm, model, "-1", true);

		try
		{
			AnnounceDynDataObjectResponse announceDynDataObjectResponse = new AnnounceDynDataObjectResponse (
							(DynDataObject) Engine.instance ().getIObjectFactory ().newInstance (model));

			ActionTools.sendToClient (userUniqueId, announceDynDataObjectResponse);
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.doQuery", "DynDataObject can not created: " + model + " Error: "
							+ x, Log.FATAL);
		}

		return dataObject.getUniqueId ();
	}

	/**
	 * Excecute the query for this aktario query.
	 *
	 * @param model The keel model query
	 * @param keelUniqueId The id from the keel object.
	 * @param userUniqueId The user unique id.
	 */
	public void editKeelObject (String model, String keelObjectId, long userUniqueId)
	{
		KeelResponse keelForm = getKeelObject (model, keelObjectId, userUniqueId);

		createDataObjectAndRegister (keelForm, model, keelObjectId, false);

		try
		{
			AnnounceDynDataObjectResponse announceDynDataObjectResponse = new AnnounceDynDataObjectResponse (
							(DynDataObject) Engine.instance ().getIObjectFactory ().newInstance (model));

			ActionTools.sendToClient (userUniqueId, announceDynDataObjectResponse);
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.doQuery", "DynDataObject can not created: " + model + " Error: "
							+ x, Log.FATAL);
		}
	}

	/**
	 * Save a data object in keel and returns errors.
	 *
	 * @param model The keel model query
	 * @param keelUniqueId The id from the keel object.
	 * @param DataObject The data object to save.
	 * @param userUniqueId The user unique id.
	 */
	public void saveKeelObject (String model, String keelObjectId, DataObject dataObject, long userUniqueId)
	{
		ModelRequest req = null;

		try
		{
			req = ModelTools.createModelRequest ();

			Model saveModel = (Model) req.getService (Model.ROLE, model, (Context) keelIritgoAuthMap.get (new Long (
							userUniqueId)));

			((KeelContextualizable) req).setKeelContext ((Context) keelIritgoAuthMap.get (new Long (userUniqueId)));

			req.setParameter ("id", keelObjectId);

			DataObject systemObject = (DataObject) Engine.instance ().getBaseRegistry ().get (
							dataObject.getUniqueId (), dataObject.getTypeId ());

			for (Iterator i = dataObject.getAttributes ().keySet ().iterator (); i.hasNext ();)
			{
				String attributeName = (String) i.next ();

				systemObject.setAttribute (attributeName, dataObject.getAttribute (attributeName));
				req.setParameter (attributeName, dataObject.getAttribute (attributeName));
			}

			req.setParameter ("id", dataObject.getStringAttribute ("keelObjectId"));

			saveModel.execute (req);
			ActionTools.sendServerBroadcast (new EditIObjectAction (EditIObjectAction.OK, systemObject));
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.saveKeelObject", "Keel error, unknown model (" + model
							+ ")? Error: " + x, Log.FATAL);
		}
		finally
		{
			ModelTools.releaseModelRequest (req);
		}
	}

	/**
	 * Delete a Iritgo/Keel object.
	 *
	 * @param model The keel model query
	 * @param keelUniqueId The id from the keel object.
	 * @param userUniqueId The user unique id.
	 */
	public void deleteKeelObject (String model, String keelObjectId, long userUniqueId)
	{
		ModelRequest req = null;

		try
		{
			req = ModelTools.createModelRequest ();

			Model deleteModel = (Model) req.getService (Model.ROLE, model, (Context) keelIritgoAuthMap.get (new Long (
							userUniqueId)));

			((KeelContextualizable) req).setKeelContext ((Context) keelIritgoAuthMap.get (new Long (userUniqueId)));

			req.setParameter ("id", keelObjectId);

			deleteModel.execute (req);
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.deleteKeelObject", "Keel error, unknown model? Error: " + x,
							Log.FATAL);
			x.printStackTrace ();
		}
		finally
		{
			ModelTools.releaseModelRequest (req);
		}
	}

	/**
	 * Excecute the query for this aktario query.
	 *
	 * @param model The keel model query
	 * @param listName The list name of the query
	 * @param userUniqueId The user uniqueid from the owner of the aktera query
	 * @param results The iobjectlist result object
	 */
	public void doQuery (String model, String listName, long userUniqueId, AkteraQuery akteraQuery,
					String searchCondition, String listSearchCategory)
	{
		KeelResponse result = getKeelResultList (model, listName, userUniqueId, searchCondition, listSearchCategory);

		// Listing aufrufen.
		if (result == null)
		{
			return;
		}

		ResponseElement resultList = result.get ("list");
		IObjectList results = (IObjectList) akteraQuery.getIObjectListResults ();

		if (resultList == null)
		{
			Log.logError ("ConnectorServerManager", "No result list for model: " + model);
		}

		registerListElement (resultList, model);

		List rows = resultList.getAll ();
		DynDataObject listObject = null;

		for (Iterator i = rows.iterator (); i.hasNext ();)
		{
			ResponseElement row = (ResponseElement) i.next ();

			listObject = createQueryDataObjectAndRegister (row, row.getAll ().iterator (), resultList, model,
							akteraQuery);

			if (listObject != null)
			{
				// System.out.println (listObject.dump ());
				results.add (listObject);
			}
		}

		Input attribute = (Input) result.get ("listSearchCategory");

		if (attribute != null)
		{
			StringBuffer validValues = new StringBuffer (StringTools.trim (attribute.getDefaultValue ()) + "|");

			for (Iterator k = attribute.getValidValues ().entrySet ().iterator (); k.hasNext ();)
			{
				Map.Entry value = (Map.Entry) k.next ();

				validValues.append (value.getKey () + "|" + value.getValue ());

				if (k.hasNext ())
				{
					validValues.append ("|");
				}
			}

			akteraQuery.setListSearchValues (validValues.toString ());
		}

		try
		{
			AnnounceDynDataObjectResponse announceDynDataObjectResponse = new AnnounceDynDataObjectResponse (
							(DynDataObject) Engine.instance ().getIObjectFactory ().newInstance (model));

			ActionTools.sendToClient (userUniqueId, announceDynDataObjectResponse);
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.doQuery", "DynDataObject can not created: " + listName
							+ " Error: " + x, Log.FATAL);
		}
	}

	public void registerListElement (ResponseElement resultList, String model)
	{
		if (Engine.instance ().getIObjectFactory ().contains (model))
		{
			return;
		}

		DynDataObject listObject = new DynDataObject (model);

		for (Iterator i = ((ResponseElement) resultList.getAttribute ("header")).getAll ().iterator (); i.hasNext ();)
		{
			ResponseElement field = (ResponseElement) i.next ();
			String attributeName = (String) ((Output) field).getContent ();

			listObject.setAttribute (attributeName, "");
		}

		listObject.setAttribute ("keelObjectId", "");
		listObject.setAttribute ("userUniqueId", "");

		Engine.instance ().getIObjectFactory ().register (listObject);
	}

	/**
	 * Execute a keel model with properties.
	 *
	 * @param Properties properties
	 * @param long userUniqueId
	 * @return
	 */
	@SuppressWarnings("serial")
	public KeelResponse executeModel (Properties properties, long userUniqueId)
	{
		ModelRequest req = null;

		try
		{
			Model model = null;

			req = ModelTools.createModelRequest ();

			Context context = (Context) keelIritgoAuthMap.get (new Long (userUniqueId));

			if (context != null)
			{
				model = (Model) req.getService (Model.ROLE, properties.getProperty ("model"), context);
			}
			else
			{
				UserEnvironment userEnvironment = new DefaultUserEnvironment ()
				{
					private LinkedList<String> fakeList;

					public List<String> getGroups ()
					{
						if (fakeList == null)
						{
							fakeList = new LinkedList<String> ();
							fakeList.add (new String ("root"));
						}

						return fakeList;
					}
				};

				context = new DefaultContext ();
				((DefaultContext) context).put (UserEnvironment.CONTEXT_KEY, userEnvironment);

				model = (Model) req.getService (Model.ROLE, properties.getProperty ("model"), context);
			}

			((KeelContextualizable) req).setKeelContext (context);

			String attributeName = null;

			for (Iterator i = properties.keySet ().iterator (); i.hasNext ();)
			{
				attributeName = (String) i.next ();

				req.setParameter (attributeName, properties.get (attributeName));
			}

			KeelResponse res = model.execute (req);

			return res;
		}
		catch (Exception x)
		{
			x.printStackTrace ();

			Log.logError ("server", "ConnectorServerManager.executeModel", "Unable to execute model: " + x.toString ());
		}
		finally
		{
			ModelTools.releaseModelRequest (req);
		}

		return null;
	}

	/**
	 * Return a keel response from the given model.
	 *
	 * @param String The model name.
	 * @param String keelObjectId
	 * @param long User unique id.
	 * @return KeelResponse object
	 */
	private KeelResponse getKeelObject (String model, String keelObjectId, long userUniqueId)
	{
		KeelResponse res = null;

		ModelRequest req = null;

		try
		{
			req = ModelTools.createModelRequest ();

			Model editModel = (Model) req.getService (Model.ROLE, model, (Context) keelIritgoAuthMap.get (new Long (
							userUniqueId)));

			((KeelContextualizable) req).setKeelContext ((Context) keelIritgoAuthMap.get (new Long (userUniqueId)));

			req.setParameter ("id", keelObjectId);

			res = editModel.execute (req);
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.getKeelObject", "Keel error, unknown model? Error: " + x,
							Log.FATAL);

			return null;
		}
		finally
		{
			ModelTools.releaseModelRequest (req);
		}

		return res;
	}

	/**
	 * Return a keel response from the given model.
	 *
	 * @param String The model name.
	 * @param String The name from the List.
	 * @param long User unique id.
	 * @return KeelResponse object
	 */
	private KeelResponse getKeelResultList (String model, String listName, long userUniqueId, String searchCondition,
					String listSearchCategory)
	{
		KeelResponse res = null;
		ModelRequest req = null;

		try
		{
			req = ModelTools.createModelRequest ();

			Model listingModel = (Model) req.getService (Model.ROLE, model, (Context) keelIritgoAuthMap.get (new Long (
							userUniqueId)));

			((KeelContextualizable) req).setKeelContext ((Context) keelIritgoAuthMap.get (new Long (userUniqueId)));

			req.setParameter ("listId", "list");
			req.setParameter ("recordsPerPage", new Integer (100));
			req.setParameter ("listSearch", searchCondition);
			req.setParameter ("aktario", "true");

			if (! listSearchCategory.equals (""))
			{
				req.setParameter ("listSearchCategory", listSearchCategory);
				req.setParameter (listName + "SearchCategory", listSearchCategory);
			}

			res = listingModel.execute (req);
		}
		catch (Exception x)
		{
			Log.log ("system", "ConnectorServerManager.doQuery", "Keel error, unknown model (" + model + ")? Error: "
							+ x, Log.FATAL);

			return null;
		}
		finally
		{
			ModelTools.releaseModelRequest (req);
		}

		return res;
	}

	/**
	 * Create a dyn data object and register it in all registrys
	 *
	 * @param KeelResponse
	 * @param String The model name
	 * @param String Keel object id
	 * @param boolean Flag for new Objects or transfer the keel object id direct
	 *        to the iritgo object.
	 * @return DynDataObject The generated object.
	 */
	private DynDataObject createDataObjectAndRegister (KeelResponse keelForm, String model, String keelObjectId,
					boolean createNewObject)
	{
		DynDataObject dataObject = null;

		boolean isObjectNotRegistered = false;

		if (Engine.instance ().getIObjectFactory ().contains (model))
		{
			try
			{
				dataObject = (DynDataObject) Engine.instance ().getIObjectFactory ().newInstance (model);
			}
			catch (Exception x)
			{
				Log.log ("system", "ConnectorServerManager.createDataObjectAndRegister",
								"DynDataObject can not created: " + model + " Error: " + x, Log.FATAL);
			}
		}
		else
		{
			dataObject = new DynDataObject (model);
			isObjectNotRegistered = true;
		}

		if (createNewObject)
		{
			dataObject.setUniqueId (Engine.instance ().getPersistentIDGenerator ().createId ());
		}
		else
		{
			dataObject.setUniqueId (new Long (keelObjectId));
		}

		ResponseElement groups = keelForm.get ("groups");

		for (Iterator i = groups.getAll ().iterator (); i.hasNext ();)
		{
			ResponseElement group = (ResponseElement) i.next ();

			for (Iterator j = group.getAll ().iterator (); j.hasNext ();)
			{
				Input attribute = (Input) j.next ();

				String attributeName = attribute.getName ();
				String attributeContent = (String) attribute.getDefaultValue ().toString ();

				dataObject.setAttribute (attributeName, attributeContent);

				if ("combo".equals (attribute.getAttribute ("editor")))
				{
					StringBuffer validValues = new StringBuffer ();

					for (Iterator k = attribute.getValidValues ().entrySet ().iterator (); k.hasNext ();)
					{
						Map.Entry value = (Map.Entry) k.next ();

						validValues.append (value.getKey () + "|" + value.getValue ());

						if (k.hasNext ())
						{
							validValues.append ("|");
						}
					}

					dataObject.setAttribute (attributeName + "ValidValues", validValues.toString ());
				}
			}
		}

		if (createNewObject)
		{
			dataObject.setAttribute ("keelObjectId", "-1");
		}
		else
		{
			dataObject.setAttribute ("keelObjectId", keelObjectId);
		}

		if (isObjectNotRegistered)
		{
			Engine.instance ().getIObjectFactory ().register ((IObject) dataObject);
		}

		IObjectProxy proxy = (IObjectProxy) new FrameworkProxy ();

		proxy.setSampleRealObject ((IObject) dataObject);

		Engine.instance ().getBaseRegistry ().add ((BaseObject) dataObject);
		Engine.instance ().getProxyRegistry ().addProxy (proxy, dataObject.getTypeId ());

		return dataObject;
	}

	/**
	 * Fills the Aktera query result list.
	 *
	 * @param KeelResponse
	 * @param Iterator Columns
	 * @param ResponseElement The result list from the keel model execution.
	 * @param String The list name.
	 * @return DynDataObject The generated object.
	 */
	private DynDataObject createQueryDataObjectAndRegister (ResponseElement row, Iterator col,
					ResponseElement resultList, String listName, AkteraQuery akteraQuery)
	{
		DynDataObject listObject = null;

		try
		{
			listObject = (DynDataObject) Engine.instance ().getIObjectFactory ().newInstance (listName);
		}
		catch (Exception x)
		{
			return null;
		}

		if (! col.hasNext ())
		{
			return null;
		}

		if (row.getAttribute ("empty") != null)
		{
			return null;
		}

		String stringId = (String) row.getAttribute ("id");

		listObject.setUniqueId (new Long (stringId));

		int colCounter = 0;

		while (col.hasNext ())
		{
			ResponseElement colResp = (ResponseElement) col.next ();

			String attributeName = (String) ((Output) ((ResponseElement) resultList.getAttribute ("header")).getAll ()
							.get (colCounter)).getContent ();

			String content = (String) (((Output) colResp).getContent ()).toString ();

			listObject.setAttribute (attributeName, content);
			++colCounter;
		}

		listObject.setAttribute ("keelObjectId", stringId);
		listObject.setAttribute ("userUniqueId", "" + akteraQuery.getUserUniqueId ());

		IObjectProxy proxy = (IObjectProxy) new FrameworkProxy ();

		proxy.setSampleRealObject ((IObject) listObject);

		Engine.instance ().getBaseRegistry ().add ((BaseObject) listObject);
		Engine.instance ().getProxyRegistry ().addProxy (proxy, listObject.getTypeId ());

		ActionTools.sendServerBroadcast (new EditIObjectAction (EditIObjectAction.OK, listObject));

		return listObject;
	}

	/**
	 * Generate for a object type a gui representation in aktera look.
	 *
	 * @param GUIControllerMissingEvent The event.
	 */
	public void guiControllerMissingEvent (GUIControllerMissingEvent event)
	{
		String controllerTypeId = event.getControllerTypeId ();
		GUIManager guiManager = event.getGUIManager ();
		int displayType = event.getDisplayType ();
		long userUniqueId = event.getUserUniqueId ();

		if (displayType == GUIControllerRequest.QUERY)
		{
			KeelResponse result = getKeelResultList (controllerTypeId, "list", userUniqueId, "", "");

			if (result == null)
			{
				return;
			}

			ResponseElement resultList = result.get ("list");

			Controller controller = createController (controllerTypeId);

			WidgetDescription wdGroup = createWidget (controller.getUniqueId (), controllerTypeId, "group", "group",
							false, true);

			controller.addWidgetDescription (wdGroup);

			for (Iterator i = ((ResponseElement) resultList.getAttribute ("header")).getAll ().iterator (); i
							.hasNext ();)
			{
				ResponseElement field = (ResponseElement) i.next ();
				String attributeName = (String) ((Output) field).getContent ();
				String fieldLabel = (String) ((Output) field).getAttribute ("label");
				boolean hide = ((Output) field).getAttribute ("hide") != null ? true : false;

				wdGroup.addWidgetDescription (createWidget (controller.getUniqueId (), fieldLabel, attributeName,
								attributeName, false, hide));
			}

			Input attribute = (Input) result.get ("listSearchCategory");

			if (attribute != null)
			{
				controller.addWidgetDescription (createWidget (controller.getUniqueId (), "listSearchCategory",
								"listSearchCategory", "listSearchCategory", false, false));
			}

			// Wird noch ben�tigt um zu �perpr�fen ob �berhaupt die Neuanlage
			// erlaubt ist.
			// Command cmdNew = (Command) resultList.getAttribute ("cmdNew");
			Command cmdEdit = (Command) resultList.getAttribute ("cmdEdit");

			if (cmdEdit != null)
			{
				controller.addCommandDescription (createCommand (controller.getUniqueId (), "NewAkteraObjectCommand",
								cmdEdit.getModel (), "new", "new", "des", true, true));

				controller.addCommandDescription (createCommand (controller.getUniqueId (), "EditAkteraObjectCommand",
								cmdEdit.getModel (), "edit", "edit", "des", true, true));
			}

			ResponseElement itemCommands = (ResponseElement) resultList.getAttribute ("itemCommands");

			if (itemCommands != null)
			{
				for (Iterator i = itemCommands.getAll ().iterator (); i.hasNext ();)
				{
					Command command = (Command) i.next ();

					if (command.getName ().equals ("delete"))
					{
						controller.addCommandDescription (createCommand (controller.getUniqueId (),
										"DeleteAkteraObjectCommand", command.getModel (), "delete", "delete", "des",
										true, true));
					}
				}
			}

			controller.addCommandDescription (createCommand (controller.getUniqueId (), "CancelAkteraObjectCommand",
							"cancel", "cancel", "cancel", "des", true, true));

			guiManager.addController (controllerTypeId, controller);
		}

		if (displayType == GUIControllerRequest.DATAOBJECT)
		{
			KeelResponse keelForm = getKeelObject (controllerTypeId, "0", userUniqueId);

			Controller controller = createController (controllerTypeId);

			ResponseElement groups = keelForm.get ("groups");

			for (Iterator i = groups.getAll ().iterator (); i.hasNext ();)
			{
				ResponseElement group = (ResponseElement) i.next ();

				WidgetDescription wdGroup = createWidget (controller.getUniqueId (), (String) ((Output) group)
								.getContent (), "group", "group", false, true);

				controller.addWidgetDescription (wdGroup);

				for (Iterator j = group.getAll ().iterator (); j.hasNext ();)
				{
					Input field = (Input) j.next ();
					String fieldName = field.getName ();
					String fieldLabel = field.getLabel ();
					boolean duty = field.getAttribute ("duty") == null ? false : true;

					WidgetDescription fieldWidget = createWidget (controller.getUniqueId (), fieldLabel, fieldName,
									(String) ((Input) field).getAttribute ("editor"), duty, true);

					wdGroup.addWidgetDescription (fieldWidget);
				}
			}

			Command cmdSave = (Command) keelForm.get ("save");

			if (cmdSave != null)
			{
				controller.addCommandDescription (createCommand (controller.getUniqueId (), "SaveAkteraObjectCommand",
								cmdSave.getModel (), "save", "save", "des", true, true));
			}

			controller.addCommandDescription (createCommand (controller.getUniqueId (), "CancelAkteraObjectCommand",
							"cancel", "cancel", "cancel", "des", true, true));

			guiManager.addController (controllerTypeId, controller);
		}
	}

	/**
	 * Execute a keel model with properties.
	 *
	 * @param Properties properties
	 * @param long userUniqueId
	 */
	public void getPersistentAttributes (Properties properties, long userUniqueId)
	{
		ModelRequest req = null;

		try
		{
			Model model = null;

			req = ModelTools.createModelRequest ();

			Context context = (Context) keelIritgoAuthMap.get (new Long (userUniqueId));

			PersistentFactory persistentManager = null;

			if (context != null)
			{
				persistentManager = (PersistentFactory) req.getService (PersistentFactory.ROLE, "", context);
			}
			else
			{
				UserEnvironment userEnvironment = new DefaultUserEnvironment ()
				{
					private LinkedList<String> fakeList;

					public List<String> getGroups ()
					{
						if (fakeList == null)
						{
							fakeList = new LinkedList<String> ();
							fakeList.add (new String ("root"));
						}

						return fakeList;
					}
				};

				context = new DefaultContext ();
				((DefaultContext) context).put (UserEnvironment.CONTEXT_KEY, userEnvironment);

				persistentManager = (PersistentFactory) req.getService (PersistentFactory.ROLE, "", context);
			}

			((KeelContextualizable) req).setKeelContext (context);

			Persistent persistent = persistentManager.create (properties.getProperty ("persistent"));

			if (properties.getProperty ("persistentFilter").equals ("true"))
			{
				persistent.setField (properties.getProperty ("persistentFilterAttributeName"), properties
								.get ("persistentFilterAttribute"));
			}

			if (! persistent.find ())
			{
				System.out.println ("No Object found:" + persistent);

				return;
			}

			for (Iterator i = persistent.getMetaData ().getFieldNames ().iterator (); i.hasNext ();)
			{
				String attribute = (String) i.next ();
				Object object = persistent.getField (attribute);

				if (object != null)
				{
					properties.put (attribute, object);
				}
			}
		}
		catch (Exception x)
		{
			x.printStackTrace ();

			Log.logError ("server", "ConnectorServerManager.executeModel", "Unable to execute model: " + x.toString ());
		}
		finally
		{
			ModelTools.releaseModelRequest (req);
		}
	}

	/**
	 * Register a dataobject. Create a base registry and a proxy registry entry.
	 *
	 * @param DataObject dataobject.
	 */
	private void registerObject (DataObject dataObject)
	{
		dataObject.setUniqueId (Engine.instance ().getPersistentIDGenerator ().createId ());

		IObjectProxy proxy = (IObjectProxy) new FrameworkProxy ();

		proxy.setSampleRealObject ((IObject) dataObject);
		Engine.instance ().getProxyRegistry ().addProxy (proxy, dataObject.getTypeId ());
		Engine.instance ().getBaseRegistry ().add ((BaseObject) dataObject);
	}

	/**
	 * Create a gui controller. He managed the display.
	 *
	 * @param String controllerTypeId
	 * @return Controller
	 */
	private Controller createController (String controllerTypeId)
	{
		Controller controller = new Controller ();
		long controllerUniqueId = Engine.instance ().getPersistentIDGenerator ().createId ();

		controller.setControllerTypeId (controllerTypeId);
		registerObject (controller);

		return controller;
	}

	/**
	 * Create a one widget description.
	 *
	 * @param long controllerUniqueId
	 * @param label labelId
	 * @param widget widgetId
	 * @param rendere rendererId
	 * @return widget description
	 */
	private WidgetDescription createWidget (long controllerUniqueId, String labelId, String widgetId,
					String rendererId, boolean duty, boolean hide)
	{
		WidgetDescription wd = new WidgetDescription ();

		wd.setControllerUniqueId (controllerUniqueId);
		wd.setLabelId (labelId);
		wd.setWidgetId (widgetId);
		wd.setRendererId (rendererId);
		wd.setMandatoryField (duty);
		wd.setVisible (! hide);
		registerObject (wd);

		return wd;
	}

	private CommandDescription createCommand (long controllerUniqueId, String commandId, String value, String iconId,
					String labelId, String description, boolean visible, boolean enabled)
	{
		CommandDescription cd = new CommandDescription ();

		cd.setControllerUniqueId (controllerUniqueId);
		cd.setCommandId (commandId);
		cd.setValue (value);
		cd.setTextId (labelId);
		cd.setIconId (iconId);
		cd.setVisible (visible);
		cd.setEnabled (enabled);
		registerObject (cd);

		return cd;
	}

	/**
	 * Free all manager resources.
	 */
	public void unload ()
	{
	}
}
