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
 * @persist.persistent
 *   id="JournalEntry"
 *   name="JournalEntry"
 *   table="JournalEntry"
 *   schema="aktera"
 *   securable="true"
 *   am-bypass-allowed="true"
 */
@Entity
public class JournalData implements Serializable
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** The type */
	@Column(length = 255)
	private String type;

	/** The category */
	@Column(length = 255)
	private String category;

	/** The key */
	private Integer key;

	/** The timestamp1 */
	@Column()
	private Timestamp timestamp1;

	/** The timestamp2 */
	@Column()
	private Timestamp timestamp2;

	/** The integer1 */
	private Integer integer1;

	/** The integer2 */
	private Integer integer2;

	/** The string1 */
	@Column(length = 255)
	private String string1;

	/** The string1 */
	@Column(length = 255)
	private String string2;

	/** The string1 */
	@Column(length = 255)
	private String string3;

	/** The string1 */
	@Column(length = 255)
	private String string4;

	/** The string1 */
	@Column(length = 255)
	private String string5;

	/** The string1 */
	@Column(length = 255)
	private String string6;

	/**
	 * Create a default journal.
	 */
	public JournalData ()
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

	public String getCategory ()
	{
		return category;
	}

	public void setCategory (String category)
	{
		this.category = category;
	}

	public Integer getKey ()
	{
		return key;
	}

	public void setKey (Integer key)
	{
		this.key = key;
	}

	public Timestamp getTimestamp1 ()
	{
		return timestamp1;
	}

	public void setTimestamp1 (Timestamp timestamp1)
	{
		this.timestamp1 = timestamp1;
	}

	public Timestamp getTimestamp2 ()
	{
		return timestamp2;
	}

	public void setTimestamp2 (Timestamp timestamp2)
	{
		this.timestamp2 = timestamp2;
	}

	public Integer getInteger1 ()
	{
		return integer1;
	}

	public void setInteger1 (Integer integer1)
	{
		this.integer1 = integer1;
	}

	public Integer getInteger2 ()
	{
		return integer2;
	}

	public void setInteger2 (Integer integer2)
	{
		this.integer2 = integer2;
	}

	public String getString1 ()
	{
		return string1;
	}

	public void setString1 (String string1)
	{
		this.string1 = string1;
	}

	public String getString2 ()
	{
		return string2;
	}

	public void setString2 (String string2)
	{
		this.string2 = string2;
	}

	public String getString3 ()
	{
		return string3;
	}

	public void setString3 (String string3)
	{
		this.string3 = string3;
	}

	public String getString4 ()
	{
		return string4;
	}

	public void setString4 (String string4)
	{
		this.string4 = string4;
	}

	public String getString5 ()
	{
		return string5;
	}

	public void setString5 (String string5)
	{
		this.string5 = string5;
	}

	public String getString6 ()
	{
		return string6;
	}

	public void setString6 (String string6)
	{
		this.string6 = string6;
	}

	public String getType ()
	{
		return type;
	}

	public void setType (String type)
	{
		this.type = type;
	}
}
