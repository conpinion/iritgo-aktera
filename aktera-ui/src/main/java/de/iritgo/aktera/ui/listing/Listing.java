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


import de.iritgo.aktera.model.Model;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.math.NumberTools;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="aktera.listing"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="aktera.listing" id="aktera.listing" logger="aktera"
 */
public class Listing extends StandardLogEnabledModel
{
	/**
	 * Execute the model. Create a listing descriptor and return it in
	 * an output element named "listing".
	 *
	 * @param req
	 * The model request
	 * @return The model response
	 */
	public ModelResponse execute (ModelRequest req) throws ModelException
	{
		try
		{
			Configuration config = getConfiguration ();
			java.util.List configPath = ModelTools.getDerivationPath (req, this);

			ModelResponse res = req.createResponse ();
			ListingDescriptor listing = createListingFromConfig (config, configPath);
			Output output = res.createOutput ("listing");

			output.setContent (listing);
			res.add (output);

			return res;
		}
		catch (ConfigurationException x)
		{
			throw new ModelException (x);
		}
	}

	/**
	 * Create a listing descriptor from a listing model.
	 *
	 * @param req
	 * The model request
	 * @param modelName
	 * The model name
	 * @return The listing descriptor
	 * @throws ModelException
	 * in case of a failure
	 */
	public static ListingDescriptor createListingFromModel (ModelRequest req, String modelName) throws ModelException
	{
		try
		{
			Model listingModel = (Model) req.getService (Model.ROLE, modelName);
			Output listingOutput = (Output) listingModel.execute (req).get ("listing");

			if (listingOutput != null)
			{
				return (ListingDescriptor) listingOutput.getContent ();
			}

			throw new ModelException ("[Listing] Unable to find listing model '" + modelName + "'");
		}
		catch (Exception x)
		{
			throw new ModelException ("Listing: Unable to create listing from model " + modelName + " (" + x + ")");
		}
	}

	/**
	 * Create a listing descriptor from a configuration.
	 *
	 * @param configPath
	 * TODO
	 * @param modelName
	 * The model name.
	 *
	 * @return The listing descriptor.
	 * @throws ModelException
	 * in case of a failure.
	 * @throws ConfigurationException
	 */
	public static ListingDescriptor createListingFromConfig (Configuration config, java.util.List configPath)
		throws ModelException, ConfigurationException
	{
		ListingDescriptor listing = new ListingDescriptor ();

		listing.setBundle (ModelTools.getConfigString (configPath, "bundle", "Aktera"));
		listing.setHeader (ModelTools.getConfigString (configPath, "header", null));

		java.util.List<Configuration> keyConfigs = ModelTools.getConfigChildren (configPath, "key");

		for (Configuration keyConfig : keyConfigs)
		{
			listing.addIdColumn (keyConfig.getValue (keyConfig.getAttribute ("value", keyConfig.getAttribute ("name",
							keyConfig.getAttribute ("column", null)))));
		}

		java.util.List<Configuration> columnsConfigs = ModelTools.getConfigChildren (configPath, "column");

		for (Configuration columnConfig : columnsConfigs)
		{
			String columnBundle = columnConfig.getAttribute ("bundle", listing.getBundle ());
			ColumnDescriptor column = listing.addColumn (columnConfig.getAttribute ("name"), columnConfig.getAttribute (
							"label", null), columnBundle, columnConfig.getAttribute ("viewer", "none"), columnConfig
							.getAttributeAsInteger ("width", 0));

			column.setRename (columnConfig.getAttribute ("rename", null));
			column.setVisible (NumberTools.toBool (columnConfig.getAttribute ("visible", "true"), true));
			column.setSortable (NumberTools.toBool (columnConfig.getAttribute ("sortable", "true"), true));
			column.setValue (columnConfig.getAttribute ("value", null));
		}

		java.util.List<Configuration> sortConfigs = ModelTools.getConfigChildren (configPath, "sort");

		for (Configuration sortConfig : sortConfigs)
		{
			SortOrder sortOrder = SortOrder.ASCENDING;

			if ("desc".equals (sortConfig.getAttribute ("order", null)))
			{
				sortOrder = SortOrder.DESCENDING;
			}

			String sortColumn = sortConfig.getAttribute ("column", sortConfig.getAttribute ("name", null));

			if (sortColumn == null)
			{
				sortColumn = sortConfig.getValue (sortConfig.getAttribute ("name", null));
			}

			if (listing.getColumn (sortColumn) == null)
			{
				throw new ModelException ("Unknown column '" + sortColumn + "' in sort tag for listing '"
								+ config.getAttribute ("id") + "'");
			}

			listing.setSortColumn (sortColumn, sortOrder);
		}

		return listing;
	}
}
