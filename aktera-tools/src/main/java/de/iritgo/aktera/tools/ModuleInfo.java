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

package de.iritgo.aktera.tools;


public class ModuleInfo
{
	/** Module id */
	private String id;

	/** Product id */
	private String productId;

	/** Module name */
	private String name;

	/** Module version */
	private String version;

	/** Module version (verbose) */
	private String versionLong;

	/** Module description */
	private String description;

	/** Copyright information */
	private String copyright;

	/** Module vendor */
	private String vendor;

	/** Module file name */
	private String fileName;

	public String getId ()
	{
		return id;
	}

	public void setId (String id)
	{
		this.id = id;
	}

	public String getProductId ()
	{
		return productId;
	}

	public void setProductId (String productId)
	{
		this.productId = productId;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public String getVersion ()
	{
		return version;
	}

	public void setVersion (String version)
	{
		this.version = version;
	}

	public String getVersionLong ()
	{
		return versionLong;
	}

	public void setVersionLong (String versionLong)
	{
		this.versionLong = versionLong;
	}

	public String getDescription ()
	{
		return description;
	}

	public void setDescription (String description)
	{
		this.description = description;
	}

	public String getCopyright ()
	{
		return copyright;
	}

	public void setCopyright (String copyright)
	{
		this.copyright = copyright;
	}

	public String getVendor ()
	{
		return vendor;
	}

	public void setVendor (String vendor)
	{
		this.vendor = vendor;
	}

	public String getFileName ()
	{
		return fileName;
	}

	public void setFileName (String fileName)
	{
		this.fileName = fileName;
	}
}
