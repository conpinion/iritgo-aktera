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

package de.iritgo.aktera.ui.listing;


import java.util.*;
import org.apache.avalon.framework.configuration.*;
import de.iritgo.aktera.authorization.InstanceSecurable;
import de.iritgo.aktera.configuration.preferences.PreferencesManager;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.form.*;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.list"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.list" id="aktera.list" logger="aktera"
 */
public class List extends SecurableStandardLogEnabledModel implements InstanceSecurable
{
	/** True if the configuration was already read. */
	protected Boolean configRead = false;

	/** The id of this list. */
	protected String id;

	/** If true a read only list is generated. */
	protected boolean readOnly;

	/** If true an overview list is generated. */
	protected boolean overview;

	/** The command to call when an item is executed. */
	CommandInfo cmdView;

	/** The search command. */
	CommandInfo cmdSearch;

	/** The new item command. */
	CommandInfo cmdNew;

	/** The back command. */
	CommandInfo cmdBack;

	/** The item execute command. */
	CommandInfo cmdExecute;

	/** The list model. */
	protected String listModel;

	/** The query condition. */
	protected String condition;

	/** Listing handler. */
	protected ListingHandler handler;

	/** Persistent configuration. */
	protected java.util.List persistentConfig;

	/** Query configuration. */
	protected Configuration queryConfig;

	/** Command configuration. */
	protected java.util.List commandConfig;

	/** Item command configuration. */
	protected java.util.List itemCommandConfig;

	/** Command style. */
	protected String commandStyle;

	/** For struts clients: The forward attribute. */
	protected String forward;

	/** Listing title. */
	protected String title;

	/** The resource bundle used for the list title. */
	protected String titleBundle;

	/** Listing header. */
	protected String header;

	/** Listing icon. */
	protected String icon;

	/** Wether this listing should be embedded in other output elements. */
	protected boolean embedded;

	/** List id. */
	protected String listId;

	/** The name of the primary key attribute. */
	protected String keyName = "id";

	/** Listing descriptor class name. */
	protected String listingClassName;

	/** Listing descriptor model name. */
	protected String listingModelName;

	/** If true, only a single list item can be selected */
	protected boolean singleSelection;

	/** List items can only be selected if this is true */
	protected boolean selectable;

	/**
	 * Return an identifying string.
	 *
	 * @return The instance id.
	 */
	public String getInstanceIdentifier()
	{
		return getConfiguration().getAttribute("id", "aktera.list");
	}

	/**
	 * Execute the model.
	 *
	 * @param request The model request.
	 * @return The model response.
	 */
	public ModelResponse execute(ModelRequest request) throws ModelException
	{
		try
		{
			ModelResponse response = request.createResponse();

			response.addOutput("formAction", "model");

			readConfig(request);

			ListingDescriptor listing = createListingDescriptor(request);

			if (! StringTools.isEmpty(request.getParameterAsString("listId")))
			{
				listing.setId(request.getParameterAsString("listId"));
			}

			if (request.getParameter(listing.getId() + "Page") != null)
			{
				listing.setPage(NumberTools.toInt(request.getParameter(listing.getId() + "Page"), listing.getPage()));
			}

			listing.updateSort(request);

			if (! StringTools.isTrimEmpty(request.getParameterAsString(listId
							+ ListingDescriptor.SEARCH_CATEGORY_PARAMETER_SUFFIX)))
			{
				listing.setCategory(request.getParameterAsString(listId
								+ ListingDescriptor.SEARCH_CATEGORY_PARAMETER_SUFFIX));
			}

			Map<String, String> searchCategories = handler.createSearchCategories(request, listing);

			if (searchCategories != null)
			{
				listing.setCategories(searchCategories);
			}

			String currentSearchCategory = handler.getCurrentSearchCategory(request, listing);

			if (currentSearchCategory != null)
			{
				listing.setCategory(currentSearchCategory);
			}

			ListContext context = createListContext(request, listing);

			handler.adjustListing(request, listing, context);

			ListFiller filler = handler.createListing(request, listing, handler, context);

			ListTools.createListing(request, response, listing, handler, context, filler);

			response.get(listing.getId()).setAttribute("formAction", "model");

			if (forward != null)
			{
				response.setAttribute("forward", forward);
			}

			return response;
		}
		catch (ConfigurationException x)
		{
			throw new ModelException("[List] " + x);
		}
		catch (PersistenceException x)
		{
			throw new ModelException("[List] " + x);
		}
	}

	/**
	 * @param request
	 * @param listing
	 * @return
	 * @throws ModelException
	 */
	private ListContext createListContext(ModelRequest request, ListingDescriptor listing) throws ModelException
	{
		PreferencesManager preferencesManager = (PreferencesManager) SpringTools.getBean(PreferencesManager.ID);
		String currentUserName = UserTools.getCurrentUserName(request);
		Integer currentUserId = UserTools.getCurrentUserId(request);

		ListContext context = new ListContext();

		context.setRequest(request);
		context.setListing(listing);
		context.setUserName(currentUserName);
		context.setUserEnvironment(UserTools.getUserEnvironment(request));

		java.util.List configPath = ModelTools.getDerivationPath(request, this);

		if (request.getParameter("recordsPerPage") != null)
		{
			int prefRows = preferencesManager.getInt(currentUserId, "gui", "tableRowsPerPage", 15);

			context.setResultsPerPage(NumberTools.toInt(request.getParameterAsString("recordsPerPage"), prefRows));
		}
		else
		{
			int prefRecordsPerPage = preferencesManager.getInt(currentUserId, "gui", "tableRowsPerPage", 15);

			context.setResultsPerPage(ModelTools.getConfigInt(configPath, "recordsPerPage", prefRecordsPerPage));
		}

		context.setNumPrevPages(ModelTools.getConfigInt(configPath, "numPrevPages", 4));
		context.setNumNextPages(ModelTools.getConfigInt(configPath, "numNextPages", 4));
		context.setListName(request.getParameterAsString("listId"));

		if (StringTools.isEmpty(context.getListName()))
		{
			context.setListName(ModelTools.getConfigString(configPath, "listId", "list"));
		}

		context.setDescribe(NumberTools.toBool(request.getParameterAsString("describe"), false));
		context.setPage(listing.getPage());
		context.setFirstResult((context.getPage() - 1) * context.getResultsPerPage());

		return context;
	}

	/**
	 * Retrieve the model configuration.
	 *
	 * @param req The model configuration.
	 */
	public void readConfig(ModelRequest req) throws ModelException, ConfigurationException
	{
		if (configRead)
		{
			return;
		}

		synchronized (configRead)
		{
			if (configRead)
			{
				return;
			}
			configRead = true;

			Configuration config = getConfiguration();
			java.util.List configPath = ModelTools.getDerivationPath(req, this);

			id = config.getAttribute("id");

			readOnly = ModelTools.getConfigBool(configPath, "readOnly", false);
			overview = ModelTools.getConfigBool(configPath, "overview", false);
			embedded = ModelTools.getConfigBool(configPath, "embedded", false);
			singleSelection = ModelTools.getConfigBool(configPath, "singleSelection", false);
			selectable = ModelTools.getConfigBool(configPath, "selectable", true);

			title = ModelTools.getConfigString(configPath, "title", null);
			titleBundle = ModelTools.getConfigString(configPath, "title", "bundle", null);
			header = ModelTools.getConfigString(configPath, "header", null);

			if (titleBundle == null)
			{
				titleBundle = ModelTools.getConfigString(configPath, "titleBundle", null);
			}

			icon = ModelTools.getConfigString(configPath, "icon", null);
			listId = ModelTools.getConfigString(configPath, "listId", "list");
			keyName = ModelTools.getConfigString(configPath, "keyName", "id");
			listModel = ModelTools.getConfigString(configPath, "listModel", null);

			cmdView = readCommandConfig(configPath, "command-view", "view", null, "edit");
			cmdSearch = readCommandConfig(configPath, "command-search", "search", null, "search");
			cmdBack = readCommandConfig(configPath, "command-back", "back", null, "back");
			cmdExecute = readCommandConfig(configPath, "command-execute", listId + "CmdExecute",
					"aktera.tools.execute-listitem-command", "execute");

			cmdNew = readCommandConfig(configPath, "command-new", "new", null, "new");

			if (cmdNew != null)
			{
				cmdNew.addParameter("new", "Y");
			}

			commandConfig = ModelTools.getConfigChildren(configPath, "command");

			itemCommandConfig = ModelTools.getConfigChildren(configPath, "item-command");

			condition = StringTools.trim(ModelTools.getConfigString(configPath, "condition", null));

			commandStyle = ModelTools.getConfigString(configPath, "commandStyle", "button").toLowerCase();

			persistentConfig = ModelTools.getConfigChildren(configPath, "persistent");

			queryConfig = ModelTools.getConfig(configPath, "query");

			Configuration forwardConfig = ModelTools.findConfig(configPath, "attribute", "name", "forward");

			forward = forwardConfig != null ? forwardConfig.getAttribute("value", "aktera.listing") : ModelTools
					.getConfigString(configPath, "forward", "aktera.listing");

			listingClassName = ModelTools.getConfigString(configPath, "listing", "class", null);

			listingModelName = ModelTools.getConfigString(configPath, "listing", "id", null);

			String handlerClassName = ModelTools.getConfigString(configPath, "handler", "class", null);

			if (handlerClassName != null)
			{
				try
				{
					handler = (ListingHandler) Class.forName(handlerClassName).newInstance();
				}
				catch (ClassNotFoundException x)
				{
					log.error("Unable to create handler " + handlerClassName + " (" + x + ")");
					throw new ModelException("[aktera.list] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
				catch (InstantiationException x)
				{
					log.error("Unable to create handler " + handlerClassName + " (" + x + ")");
					throw new ModelException("[aktera.list] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
				catch (IllegalAccessException x)
				{
					log.error("Unable to create handler " + handlerClassName + " (" + x + ")");
					throw new ModelException("[aktera.list] Unable to create handler " + handlerClassName + " (" + x + ")");
				}
			}
			else
			{
				String handlerBeanName = ModelTools.getConfigString(configPath, "handler", "bean", null);

				if (handlerBeanName != null)
				{
					handler = (ListingHandler) SpringTools.getBean(handlerBeanName);
				}
			}

			if (handler != null)
			{
				handler.setDefaultHandler(new DefaultListingHandler());
			}
			else
			{
				handler = new DefaultListingHandler();
			}
		}
	}

	/**
	 * Retrieve the configuration of a list command.
	 *
	 * @param configPath The configuration path.
	 * @param configName The name of the command config.
	 * @param name The command name.
	 * @param defaultLabel The default command label.
	 * @return The command info.
	 */
	public CommandInfo readCommandConfig(java.util.List configPath, String configName, String name,
					String defaultModel, String defaultLabel) throws ModelException, ConfigurationException
	{
		Configuration config = ModelTools.getConfig(configPath, configName);

		String model = config != null ? config.getAttribute("model", defaultModel) : defaultModel;
		String label = config != null ? config.getAttribute("label", defaultLabel) : defaultLabel;
		String bundle = config != null ? config.getAttribute("bundle", "Aktera") : "Aktera";

		name = config != null ? config.getAttribute("id", name) : name;

		boolean visible = config != null ? NumberTools.toBool(config.getAttribute("visible", "true"), true) : true;

		if (config != null)
		{
			CommandInfo cmd = new CommandInfo(model, name, label);

			cmd.setBundle(bundle);
			cmd.setVisible(visible);
			cmd.setPermission(config != null ? config.getAttribute("ifPermission", null) : null);

			if (config != null)
			{
				Configuration[] params = config.getChildren("param");

				for (int i = 0; i < params.length; ++i)
				{
					cmd.addParameter(params[i].getAttribute("name"), params[i].getAttribute("value"));
				}
			}

			return cmd;
		}

		return null;
	}

	/**
	 * Create the listing descriptor. This method stores the descriptor in the
	 * user's context. On a later request, the descriptor is taken directly from
	 * the user context.
	 *
	 * @param req The model request.
	 * @throws ModelException In case of an error.
	 * @throws ConfigurationException If a configuration value cannot be found.
	 * @throws PersistenceException If a persistent cannot be found.
	 */
	public ListingDescriptor createListingDescriptor(ModelRequest req)
		throws ModelException, ConfigurationException, PersistenceException
	{
		ListingDescriptor listing = (ListingDescriptor) UserTools.getContextObject(req, id);

		if (listing != null)
		{
			return listing;
		}

		if (listingModelName != null)
		{
			listing = Listing.createListingFromModel(req, listingModelName);
		}
		else if (listingClassName != null)
		{
			try
			{
				listing = (ListingDescriptor) Class.forName(listingClassName).newInstance();
			}
			catch (ClassNotFoundException x)
			{
				throw new ModelException("[aktera.list] Unable to create listing from class " + listingClassName + " ("
								+ x + ")");
			}
			catch (InstantiationException x)
			{
				throw new ModelException("[aktera.list] Unable to create listing from class " + listingClassName + " ("
								+ x + ")");
			}
			catch (IllegalAccessException x)
			{
				throw new ModelException("[aktera.list] Unable to create listing from class " + listingClassName + " ("
								+ x + ")");
			}
		}
		else
		{
			listing = Listing.createListingFromConfig(getConfiguration(), ModelTools.getDerivationPath(req, this));
		}

		listing.setCommandStyle(commandStyle);
		listing.setTitle(title);
		listing.setTitleBundle(titleBundle);

		if (header != null)
		{
			listing.setHeader(header);
		}

		listing.setIcon(icon);
		listing.setEmbedded(embedded);
		listing.setSingleSelection(singleSelection);
		listing.setSelectable(selectable);
		listing.setKeyName(keyName);
		listing.setId(listId);
		listing.setListModel(listModel);

		listing.setCommand(ListingDescriptor.COMMAND_VIEW, cmdView);
		listing.setCommand(ListingDescriptor.COMMAND_SEARCH, cmdSearch);
		listing.setCommand(ListingDescriptor.COMMAND_NEW, cmdNew);
		listing.setCommand(ListingDescriptor.COMMAND_BACK, cmdBack);
		listing.setCommand(ListingDescriptor.COMMAND_EXECUTE, cmdExecute);

		if (commandConfig.size() > 0)
		{
			CommandDescriptor cmdDescriptor = new CommandDescriptor();

			for (Iterator i = commandConfig.iterator(); i.hasNext();)
			{
				Configuration aCommandConfig = (Configuration) i.next();

				String permission = aCommandConfig.getAttribute("permission", null);

				if (permission != null && ! UserTools.currentUserHasPermission(req, permission))
				{
					continue;
				}

				String role = aCommandConfig.getAttribute("role", null);

				if (role != null && ! UserTools.currentUserIsInGroup(req, role))
				{
					continue;
				}

				CommandInfo cmdInfo = new CommandInfo(aCommandConfig.getAttribute("model"),
								aCommandConfig.getAttribute("id"), aCommandConfig.getAttribute("label"),
								aCommandConfig.getAttribute("icon", null));

				cmdInfo.setBundle(aCommandConfig.getAttribute("bundle", "Aktera"));

				Configuration[] params = aCommandConfig.getChildren("param");

				setParameters(req, cmdInfo, params);

				cmdDescriptor.add(cmdInfo);
			}

			listing.setListCommands(cmdDescriptor);
		}

		if (! overview && itemCommandConfig.size() > 0)
		{
			CommandDescriptor cmdDescriptor = new CommandDescriptor("selectedItems");

			for (Iterator i = itemCommandConfig.iterator(); i.hasNext();)
			{
				Configuration aItemCommandConfig = (Configuration) i.next();

				if (! NumberTools.toBool(aItemCommandConfig.getAttribute("visible", "true"), true))
				{
					continue;
				}

				if (cmdDescriptor.hasCommand(aItemCommandConfig.getAttribute("id")))
				{
					continue;
				}

				CommandInfo cmdInfo = new CommandInfo(aItemCommandConfig.getAttribute("model",
								aItemCommandConfig.getAttribute("bean", null)), aItemCommandConfig.getAttribute("id"),
								aItemCommandConfig.getAttribute("label"), aItemCommandConfig.getAttribute("icon", null));

				cmdInfo.setStyle(aItemCommandConfig.getAttribute("style", null));
				cmdInfo.setBundle(aItemCommandConfig.getAttribute("bundle", "Aktera"));

				if (aItemCommandConfig.getAttribute("bean", null) != null)
				{
					cmdInfo.setBean(true);
				}

				Configuration[] params = aItemCommandConfig.getChildren("param");

				setParameters(req, cmdInfo, params);

				cmdDescriptor.add(cmdInfo);
			}

			listing.setItemCommands(cmdDescriptor);
		}

		PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE,
						req.getDomain());
		PersistentDescriptor persistents = new PersistentDescriptor();

		if (persistentConfig.size() > 0)
		{
			Iterator persistentConfigIterator = persistentConfig.iterator();
			Configuration aPersistentConfig = (Configuration) persistentConfigIterator.next();

			Persistent persistent = persistentManager.create(aPersistentConfig.getAttribute("name"));

			persistents.put(aPersistentConfig.getAttribute("id"), persistent);

			for (; persistentConfigIterator.hasNext();)
			{
				aPersistentConfig = (Configuration) persistentConfigIterator.next();

				persistent = persistentManager.create(aPersistentConfig.getAttribute("name"));

				String join = aPersistentConfig.getAttribute("join", null);

				if (join != null)
				{
					persistents.put(aPersistentConfig.getAttribute("id"), persistent).join(join,
									aPersistentConfig.getAttribute("otherKey"),
									aPersistentConfig.getAttribute("myKey"),
									aPersistentConfig.getAttribute("condition", null));
				}
				else
				{
					persistents.put(aPersistentConfig.getAttribute("id"), persistent);
				}
			}
		}

		listing.setPersistents(persistents);

		if (queryConfig != null)
		{
			QueryDescriptor qd = new QueryDescriptor();

			listing.setQuery(qd);
			qd.setName(queryConfig.getAttribute("name", null));
			qd.setDaoName(queryConfig.getAttribute("dao", null));
			qd.setDaoMethodName(queryConfig.getAttribute("method", null));
			qd.setQuery(queryConfig.getChild("find").getValue(queryConfig.getChild("expression").getValue(null)));
			qd.setCountQuery(queryConfig.getChild("count").getValue(null));

			for (Configuration paramConfig : queryConfig.getChildren("param"))
			{
				String value = paramConfig.getAttribute("value", null);

				if (value == null)
				{
					value = paramConfig.getValue();
				}

				qd.setParam(paramConfig.getAttribute("name"), value);
			}
		}

		listing.setCondition(condition != null ? condition : "");

		UserTools.setContextObject(req, id, listing);

		return listing;
	}

	@SuppressWarnings("unused")
	private void setParameters(ModelRequest req, CommandInfo cmdInfo, Configuration[] params)
		throws ConfigurationException
	{
		for (int j = 0; j < params.length; ++j)
		{
			String value = params[j].getAttribute("value");

			cmdInfo.addParameter(params[j].getAttribute("name"), value);
		}
	}
}
