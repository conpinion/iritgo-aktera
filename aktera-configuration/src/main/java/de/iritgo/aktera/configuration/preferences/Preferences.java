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

package de.iritgo.aktera.configuration.preferences;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * User preferences.
 *
 * @persist.persistent
 *   id="Preferences"
 *   name="Preferences"
 *   table="Preferences"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class Preferences implements Serializable
{
	/** Primary key equals user key */
	@Id
	private Integer userId;

	/** True if the preferences cannot be chaned */
	private Boolean protect;

	/** Permissions */
	@Column(length = 16)
	private String security;

	/** Interface language */
	@Column(length = 8)
	private String language;

	/** Power user flag */
	private Boolean powerUser;

	/** True if the user is allowed to chaned his password */
	private Boolean canChangePassword;

	/** Interface theme */
	@Column(length = 32)
	private String theme;

	/** Pin number */
	@Column(length = 16)
	private String pin;

	/**
	 * Get the user key.
	 *
	 * @persist.field
	 *   name="userId"
	 *   db-name="userId"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 */
	public Integer getUserId()
	{
		return userId;
	}

	/**
	 * Set the user key.
	 */
	public void setUserId(Integer userId)
	{
		this.userId = userId;
	}

	/**
	 * Get the protect flag.
	 *
	 * @persist.field
	 *   name="protect"
	 *   db-name="protect"
	 *   type="boolean"
	 *   descrip="Protect"
	 */
	public Boolean getProtect()
	{
		return protect;
	}

	/**
	 * Set the protect flag.
	 */
	public void setProtect(Boolean protect)
	{
		this.protect = protect;
	}

	/**
	 * Get the security flags.
	 *
	 * @persist.field
	 *   name="security"
	 *   db-name="security"
	 *   type="varchar"
	 *   length="16"
	 */
	public String getSecurity()
	{
		return security;
	}

	/**
	 * Set the security flags.
	 */
	public void setSecurity(String security)
	{
		this.security = security;
	}

	/**
	 * Get the interface language.
	 *
	 * @persist.field
	 *   name="language"
	 *   db-name="language"
	 *   type="varchar"
	 *   length="8"
	 *   default-value="de"
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * Set the interface language.
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	/**
	 * Get the power user flag.
	 *
	 * @persist.field
	 *   name="powerUser"
	 *   db-name="powerUser"
	 *   type="boolean"
	 *   default-value="false"
	 */
	public Boolean getPowerUser()
	{
		return powerUser;
	}

	/**
	 * Set the power user flag.
	 */
	public void setPowerUser(Boolean powerUser)
	{
		this.powerUser = powerUser;
	}

	/**
	 * Get the can change password flag.
	 *
	 * @persist.field
	 *   name="canChangePassword"
	 *   db-name="canChangePassword"
	 *   type="boolean"
	 *   default-value="true"
	 */
	public Boolean getCanChangePassword()
	{
		return canChangePassword;
	}

	/**
	 * Set the can change password flag.
	 */
	public void setCanChangePassword(Boolean canChangePassword)
	{
		this.canChangePassword = canChangePassword;
	}

	/**
	 * Get the interface theme.
	 *
	 * @persist.field
	 *   name="theme"
	 *   db-name="theme"
	 *   type="varchar"
	 *   length="32"
	 *   default-value="iritgong"
	 */
	public String getTheme()
	{
		return theme;
	}

	/**
	 * Set the interface theme.
	 */
	public void setTheme(String theme)
	{
		this.theme = theme;
	}

	/**
	 * Get the pin number.
	 *
	 * @persist.field
	 *   name="pin"
	 *   db-name="pin"
	 *   type="varchar"
	 *   length="16"
	 */
	public String getPin()
	{
		return pin;
	}

	/**
	 * Set the pin number.
	 */
	public void setPin(String pin)
	{
		this.pin = pin;
	}
}
