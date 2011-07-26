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

package de.iritgo.aktera.core.config;


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.configuration.ConfigurationUtil;
import java.util.HashSet;
import java.util.Set;


/**
 * Custom class to merge configurations - the Avalon standard ConfigurationMerger
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class KeelConfigurationMerger
{
	//--- Do not allow this to be instantiated since it is a Singleton.
	private KeelConfigurationMerger ()
	{
	} // KeelConfigurationMerger

	/**
	 * Merge two configurations.
	 *
	 * @param layer Configuration to <i>layer</i> over the base
	 * @param base Configuration <i>layer</i> will be merged with
	 *
	 * @return Result of merge
	 *
	 * @exception ConfigurationException if unable to merge
	 */
	public static Configuration merge (final Configuration layer, final Configuration base)
		throws ConfigurationException
	{
		final DefaultConfiguration merged = new DefaultConfiguration (base.getName (), "Merged [layer: "
						+ layer.getLocation () + ", base: " + base.getLocation () + "]");

		copyAttributes (base, merged);
		copyAttributes (layer, merged);

		mergeChildren (layer, base, merged);

		merged.setValue (getValue (layer, base));
		merged.makeReadOnly ();

		return merged;
	}

	private static void mergeChildren (final Configuration layer, final Configuration base,
					final DefaultConfiguration merged) throws ConfigurationException
	{
		final Configuration[] lc = layer.getChildren ();
		final Configuration[] bc = base.getChildren ();
		final Set baseUsed = new HashSet ();

		for (int i = 0; i < lc.length; i++)
		{
			final Configuration mergeWith = getMergePartner (lc[i], layer, base);

			if (null == mergeWith)
			{
				merged.addChild (lc[i]);
			}
			else
			{
				merged.addChild (merge (lc[i], mergeWith));

				baseUsed.add (mergeWith);
			}
		}

		for (int i = 0; i < bc.length; i++)
		{
			if (! baseUsed.contains (bc[i]))
			{
				merged.addChild (bc[i]);
			}
		}
	}

	private static Configuration getMergePartner (final Configuration toMerge, final Configuration layer,
					final Configuration base) throws ConfigurationException
	{
		if (true)
		{
			String keyAttribute = toMerge.getAttribute ("merge-key", "id");

			if (toMerge.getName ().equals ("role"))
			{
				keyAttribute = "name";
			}
			else if (toMerge.getName ().equals ("component"))
			{
				keyAttribute = "shorthand";
			}

			final String keyvalue = toMerge.getAttribute (keyAttribute, null);

			if (keyvalue == null)
			{
				return null;
			}

			final Configuration[] layerKids = ConfigurationUtil.match (layer, toMerge.getName (), keyAttribute,
							keyvalue);

			final Configuration[] baseKids = ConfigurationUtil.match (base, toMerge.getName (), keyAttribute, keyvalue);

			if (baseKids.length == 0)
			{
				return null;
			}

			if (layerKids.length == 1 && baseKids.length == 1)
			{
				return baseKids[0];
			}
			else
			{
				throw new ConfigurationException ("Attempting to merge '" + toMerge.getName () + "', layer '"
								+ layer.getName () + "' with id '" + layer.getAttribute ("id", "null")
								+ "', there were " + baseKids.length + " base items to merge with, and "
								+ layerKids.length + " layer items to merge them with. Cannot merge");
			}
		}

		return null;
	}

	private static String getValue (final Configuration layer, final Configuration base)
	{
		String returnValue = null;

		try
		{
			returnValue = layer.getValue ();
		}
		catch (ConfigurationException e)
		{
			returnValue = base.getValue (null);
		}

		return returnValue;
	}

	private static void copyAttributes (final Configuration source, final DefaultConfiguration dest)
		throws ConfigurationException
	{
		final String[] names = source.getAttributeNames ();

		for (int i = 0; i < names.length; i++)
		{
			//if (!names[i].startsWith(Constants.MERGE_METADATA_PREFIX)) {
			dest.setAttribute (names[i], source.getAttribute (names[i]));

			//}
		}
	}
}
