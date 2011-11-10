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

package de.iritgo.aktera.mail;


import de.iritgo.aktera.core.exception.NestedException;


/**
 * This interface defines a MimeTypeResolver service for converting to and
 * from a MIME content-type and file extensions.
 *
 * @version        $Revision: 1.2 $        $Date: 2003/07/03 15:49:33 $
 * @author Shash Chatterjee
 * Created on Jan 5, 2003
 */
public interface MimeTypeResolver
{
	/**
	 * The ROLE name used to acquire services that implement this service
	 */
	public String ROLE = "de.iritgo.aktera.mail.MimeTypeResolver";

	/**
	 * Retrieve an extension, given the content-type
	 * @param type
	 * @return String
	 * @throws NestedException
	 */
	public String getExtension(String type) throws NestedException;

	/**
	 * Retrieve the mime-type, given a file-extension
	 * @param extension
	 * @return String
	 * @throws NestedException
	 */
	public String getType(String extension) throws NestedException;

	/**
	 * Retrieve the type description, given the content-type
	 * @param type
	 * @return String
	 * @throws NestedException
	 */
	public String getDescriptionByType(String type) throws NestedException;

	/**
	 * Retrieve the type description, given a file-extension
	 * @param type
	 * @return String
	 * @throws NestedException
	 */
	public String getDescriptionByExtension(String extension) throws NestedException;
}
