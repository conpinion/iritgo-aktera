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

package de.iritgo.aktera.aktario.db;


import java.io.Serializable;


/**
 * @persist.persistent
 *   id="AktarioUserPreferences"
 *   name="AktarioUserPreferences"
 *   table="AktarioUserPreferences"
 *   schema="aktera"
 *   descrip="AktarioUserPreferences"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
public class AktarioUserPreferences implements Serializable
{
	/** */
	private Long id;

	/** */
	private String colorScheme;

	/** */
	private Integer alwaysDrawWindowContents;

	/** */
	private String language;

	/**
	 * @persist.field
	 *   name="id"
	 *   db-name="id"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public Long getId ()
	{
		return id;
	}

	/**
	 */
	public void setId (Long id)
	{
		this.id = id;
	}

	/**
	 * @persist.field
	 *   name="colorScheme"
	 *   db-name="colorScheme"
	 *   type="varchar"
	 *   length="64"
	 *   default-value="com.jgoodies.looks.plastic.theme.KDE"
	 */
	public String getColorScheme ()
	{
		return colorScheme;
	}

	/**
	 */
	public void setColorScheme (String colorScheme)
	{
		this.colorScheme = colorScheme;
	}

	/**
	 * @persist.field
	 *   name="alwaysDrawWindowContents"
	 *   db-name="alwaysDrawWindowContents"
	 *   type="integer"
	 *   default-value="1"
	 */
	public Integer getAlwaysDrawWindowContents ()
	{
		return alwaysDrawWindowContents;
	}

	/**
	 */
	public void setAlwaysDrawWindowContents (Integer alwaysDrawWindowContents)
	{
		this.alwaysDrawWindowContents = alwaysDrawWindowContents;
	}

	/**
	 * @persist.field
	 *   name="language"
	 *   db-name="language"
	 *   type="varchar"
	 *   length="16"
	 *   default-value="de"
	 */
	public String getLanguage ()
	{
		return language;
	}

	/**
	 */
	public void setLanguage (String language)
	{
		this.language = language;
	}
}
