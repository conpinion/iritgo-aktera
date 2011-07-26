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

package de.iritgo.aktera.base.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;


/**
 * @persist.persistent
 *   id="Profile"
 *   name="Profile"
 *   table="Profile"
 *   schema="aktera"
 *   descrip="profile base informations"
 *   page-size="5"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class Profile implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** */
	@Id
	private Integer partyId;

	/** */
	@Column()
	private Timestamp lastLogin;

	/** */
	@Column()
	private Date birthDate;

	/** */
	@Column()
	private Boolean publishInformation;

	/**
	 * Return the party id.
	 *
	 * @persist.field
	 *   name="partyId"
	 *   db-name="partyId"
	 *   type="integer"
	 *   primary-key="true"
	 *   null-allowed="false"
	 *   descrip="Foreign party id"
	 *
	 * @return The party id.
	 */
	public Integer getPartyId ()
	{
		return partyId;
	}

	/**
	 * Set the party id.
	 *
	 * @param partyId The new party id.
	 */
	public void setPartyId (Integer partyId)
	{
		this.partyId = partyId;
	}

	/**
	 * Return the date and time of the last Login.
	 *
	 * @persist.field
	 *   name="lastLogin"
	 *   db-name="lastLogin"
	 *   type="timestamp"
	 *   descrip="Last login"
	 *
	 * @return The Last login date.
	 */
	public Timestamp getLastLogin ()
	{
		return lastLogin;
	}

	/**
	 * Set the the last login date and time.
	 *
	 * @param lastLogin The new last login date.
	 */
	public void setLastLogin (Timestamp lastLogin)
	{
		this.lastLogin = lastLogin;
	}

	/**
	 * Return the birth date.
	 *
	 * @persist.field
	 *   name="birthDate"
	 *   db-name="birthDate"
	 *   type="date"
	 *   descrip="Birth date"
	 *
	 * @return The birth date.
	 */
	public Date getBirthDate ()
	{
		return birthDate;
	}

	/**
	 * Set the birth date.
	 *
	 * @param birthDate The new birth date.
	 */
	public void setBirthDate (Date birthDate)
	{
		this.birthDate = birthDate;
	}

	/**
	 * Returns the publish personal state.
	 *
	 * @persist.field
	 *   name="publishInformation"
	 *   db-name="publishInformation"
	 *   type="boolean"
	 *   descrip="Publish information"
	 *
	 * @return True, if the personal information should be published.
	 */
	public Boolean getPublishInformation ()
	{
		return publishInformation;
	}

	/**
	 * Set the publish personal state.
	 *
	 * @param publishInformation The new state.
	 */
	public void setPublishInformation (Boolean publishInformation)
	{
		this.publishInformation = publishInformation;
	}
}
