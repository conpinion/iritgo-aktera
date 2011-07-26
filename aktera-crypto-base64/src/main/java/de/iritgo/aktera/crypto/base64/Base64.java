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

package de.iritgo.aktera.crypto.base64;


import de.iritgo.aktera.crypto.AbstractEncryptor;


/**
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.crypto.Encryptor
 * @x-avalon.info name=base64
 * @x-avalon.lifestyle type=singleton
 */
public class Base64 extends AbstractEncryptor
{
	/**
	 *
	 */
	public byte[] decrypt (byte[] bdata) throws IllegalArgumentException
	{
		assert bdata.length != 0;

		return org.apache.commons.codec.binary.Base64.decodeBase64 (bdata);
	}

	/**
	 *
	 */
	public byte[] encrypt (byte[] inBuffer) throws IllegalArgumentException
	{
		assert inBuffer.length != 0;

		return org.apache.commons.codec.binary.Base64.encodeBase64 (inBuffer);
	}

	/**
	 * The Key isn't use for this kind of simple encryption
	 */
	public void setKey (String newKey)
	{
	}
}
