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


import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;
import lombok.*;


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
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter
	@Getter
	private Integer id;

	@Column(length = 255)
	@Setter
	@Getter
	private String type;

	@Column(length = 255)
	@Setter
	@Getter
	private String category;

	@Setter
	@Getter
	private Integer key;

	@Column(nullable = false)
	@Setter
	@Getter
	private Timestamp occurredAt;

	@Setter
	@Getter
	private Timestamp timestamp1;

	@Setter
	@Getter
	private Timestamp timestamp2;

	@Setter
	@Getter
	private Integer integer1;

	@Setter
	@Getter
	private Integer integer2;

	@Column(length = 255)
	@Setter
	@Getter
	private String string1;

	@Column(length = 255)
	@Setter
	@Getter
	private String string2;

	@Column(length = 255)
	@Setter
	@Getter
	private String string3;

	@Column(length = 255)
	@Setter
	@Getter
	private String string4;

	@Column(length = 255)
	@Setter
	@Getter
	private String string5;

	@Column(length = 255)
	@Setter
	@Getter
	private String string6;
}
