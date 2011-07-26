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

package de.iritgo.aktera.journal.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * Address domain object.
 *
 * @persist.persistent id="JournalEntry" name="JournalEntry"
 *                     table="JournalEntry" schema="aktera" securable="true"
 *                     am-bypass-allowed="true"
 */
@Entity
@NamedQueries(
{
				@NamedQuery(name = "de.iritgo.aktera.journal.JournalEntryList", query = "select entry from JournalEntry entry where entry.occurredAt between :start and :end and entry.ownerId = :ownerId and lower(searchableText) like :search order by entry.occurredAt desc"),
				@NamedQuery(name = "de.iritgo.aktera.journal.JournalEntryCount", query = "select count(entry) from JournalEntry entry where entry.occurredAt between :start and :end and entry.ownerId = :ownerId and lower(searchableText) like :search")
})
public class JournalEntry implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** The timestamp for the journal entry */
	@Column(nullable = false)
	private Timestamp occurredAt;

	/** The producerId - from where came the entry system|user|... */
	private Integer producerId;

	/** The producerType */
	@Column(length = 255)
	private String producerType;

	/** The ownerId */
	private Integer ownerId;

	/** The ownerType */
	@Column(length = 255)
	private String ownerType;

	/** The ownerGroupId */
	private Integer ownerGroupId;

	/** The ownerGroupType */
	@Column(length = 255)
	private String ownerGroupType;

	/** A foreign key to extend the information in the journal */
	private Integer extendedInfoId;

	/** */
	@Column(length = 255)
	private String extendedInfoType;

	/** The journal message */
	@Column(length = 255)
	private String message;

	/** The journal short message */
	@Column(length = 255)
	private String shortMessage;

	/** The tags for searching and/or individual use */
	@Column(length = 255)
	private String tags;

	/** The primary type of the journal entry e.g. call-list */
	@Column(length = 255)
	private String primaryType;

	/** The secondary type of the journal entry e.g anwered */
	@Column(length = 255)
	private String secondaryType;

	/** The searchable free text */
	@Column(length = 255)
	private String searchableText;

	/** The any text store (misc) */
	@Column(length = 255)
	private String misc;

	/** The raw data (e.g. the pure callerid) */
	@Column(length = 255)
	private String rawData;

	/** The new flag */
	private Boolean newFlag;

	/**
	 * Create a default journal.
	 */
	public JournalEntry ()
	{
	}

	/**
	 * Get the primary key.
	 */
	public Integer getId ()
	{
		return id;
	}

	/**
	 * Set the primary key.
	 */
	public void setId (Integer id)
	{
		this.id = id;
	}

	/**
	 * Return the the timestamp for the journal entry
	 *
	 * @return The timestamp
	 */
	public Timestamp getOccurredAt ()
	{
		return occurredAt;
	}

	/**
	 * Set the the timestamp for the journal entry
	 *
	 * @param Timestamp
	 *            The timestamp
	 */
	public void setOccurredAt (Timestamp occurredAt)
	{
		this.occurredAt = occurredAt;
	}

	/**
	 * Return the producerId
	 *
	 * @return The producerId
	 */
	public Integer getProducerId ()
	{
		return producerId;
	}

	/**
	 * Set the producerId
	 *
	 * @param producerId
	 *            The producerId
	 */
	public void setProducerId (Integer producerId)
	{
		this.producerId = producerId;
	}

	/**
	 * Return the ownerid
	 *
	 * @return The ownerId
	 */
	public Integer getOwnerId ()
	{
		return ownerId;
	}

	/**
	 * Set the owner id
	 *
	 * @param owner
	 *            id The owner id
	 */
	public void setOwnerId (Integer ownerId)
	{
		this.ownerId = ownerId;
	}

	/**
	 * Return the owner group id
	 *
	 * @return The ownerGroupId
	 */
	public Integer getOwnerGroupId ()
	{
		return ownerGroupId;
	}

	/**
	 * Set the owner group id
	 *
	 * @param ownerGroupId
	 *            The owner group id
	 */
	public void setOwnerGroupId (Integer ownerGroupId)
	{
		this.ownerGroupId = ownerGroupId;
	}

	/**
	 * Get the primary type of the journal entry
	 *
	 * @return Th primary type
	 */
	public String getPrimaryType ()
	{
		return primaryType;
	}

	/**
	 * Set the primary type of the journal entry
	 *
	 * @param The
	 *            primary type
	 */
	public void setPrimaryType (String primaryType)
	{
		this.primaryType = primaryType;
	}

	/**
	 * Get the secondary type of the journal entry
	 *
	 * @return The secondary type
	 */
	public String getSecondaryType ()
	{
		return secondaryType;
	}

	/**
	 * Set the secondary type of the journal entry
	 *
	 * @param The
	 *            secondary type
	 */
	public void setSecondaryType (String secondaryType)
	{
		this.secondaryType = secondaryType;
	}

	/**
	 * Return the foreign key for extended infos about the jounal entry
	 *
	 * @return The extendend info id
	 */
	public Integer getExtendedInfoId ()
	{
		return extendedInfoId;
	}

	/**
	 * Set the extended info id
	 *
	 * @param extendedInfoId
	 *            The extended info id
	 */
	public void setExtendedInfoId (Integer extendedInfoId)
	{
		this.extendedInfoId = extendedInfoId;
	}

	/**
	 * Return the extendended info type
	 *
	 * @return The info type
	 */
	public String getExtendedInfoType ()
	{
		return extendedInfoType;
	}

	/**
	 * Set the extended info type.
	 *
	 * @param extendedInfoType
	 *            The extended info type
	 */
	public void setExtendedInfoType (String extendedInfoType)
	{
		this.extendedInfoType = extendedInfoType;
	}

	/**
	 * Return the message
	 *
	 * @return The message
	 */
	public String getMessage ()
	{
		return message;
	}

	/**
	 * Set the message
	 *
	 * @param message
	 *            The message
	 */
	public void setMessage (String message)
	{
		this.message = message;
	}

	/**
	 * Return the short message
	 *
	 * @return The short message
	 */
	public String getShortMessage ()
	{
		return shortMessage;
	}

	/**
	 * Set short message address
	 *
	 * @param shortMessage
	 *            The shot message
	 */
	public void setShortMessage (String shortMessage)
	{
		this.shortMessage = shortMessage;
	}

	/**
	 * Return the tags
	 *
	 * @return The tags
	 */
	public String getTags ()
	{
		return tags;
	}

	/**
	 * Set the tags
	 *
	 * @param tags
	 *            The tags
	 */
	public void setTags (String tags)
	{
		this.tags = tags;
	}

	/**
	 * Return the producer type
	 *
	 * @return The producer type
	 */
	public String getProducerType ()
	{
		return producerType;
	}

	/**
	 * Set the producer type
	 *
	 * @param producerType
	 *            The producer type
	 */
	public void setProducerType (String producerType)
	{
		this.producerType = producerType;
	}

	/**
	 * Return the producer type
	 *
	 * @return The producer type
	 */
	public String getOwnerType ()
	{
		return ownerType;
	}

	/**
	 * Return the owner type
	 *
	 * @param ownerType
	 *            The owner type
	 */
	public void setOwnerType (String ownerType)
	{
		this.ownerType = ownerType;
	}

	/**
	 * Return the owner group type
	 *
	 * @return The owner group type
	 */
	public String getOwnerGroupType ()
	{
		return ownerGroupType;
	}

	/**
	 * Set the owner group type
	 *
	 * @param ownerGroupType
	 *            The owner group type
	 */
	public void setOwnerGroupType (String ownerGroupType)
	{
		this.ownerGroupType = ownerGroupType;
	}

	/**
	 * The searchable text
	 *
	 * @return The searchable text
	 */
	public String getSearchableText ()
	{
		return searchableText;
	}

	/**
	 * Set the searchable text
	 *
	 * @param searchableText
	 *            The searchable text
	 */
	public void setSearchableText (String searchableText)
	{
		this.searchableText = searchableText;
	}

	/**
	 * Return the misc text
	 *
	 * @return The misc text
	 */
	public String getMisc ()
	{
		return misc;
	}

	/**
	 * Set the misc text
	 *
	 * @param misc
	 *            The misc text
	 */
	public void setMisc (String misc)
	{
		this.misc = misc;
	}

	/**
	 * Return the raw data
	 *
	 * @return The raw data
	 */
	public String getRawData ()
	{
		return rawData;
	}

	/**
	 * Set the raw data
	 *
	 * @param rawData
	 *            The raw data
	 */
	public void setRawData (String rawData)
	{
		this.rawData = rawData;
	}

	/**
	 * Return 1 if the entry is unreaded
	 *
	 * @return The flag
	 */
	public Boolean getNewFlag ()
	{
		return newFlag;
	}

	/**
	 * Set the new flag for this entry
	 *
	 * @param newFlag
	 *            The new flag
	 */
	public void setNewFlag (Boolean newFlag)
	{
		this.newFlag = newFlag;
	}
}
