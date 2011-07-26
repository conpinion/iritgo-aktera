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

package de.iritgo.aktera.crypto;


import de.iritgo.aktera.core.exception.NestedException;
import de.iritgo.aktera.crypto.Encryptor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 *
 *
 */
public abstract class AbstractEncryptor implements Encryptor
{
	public static String ROLE = "de.iritgo.aktera.crypto.Encryptor";

	public abstract byte[] decrypt (byte[] inputData) throws NestedException;

	public abstract byte[] encrypt (byte[] inputData) throws NestedException;

	/**
	 * "Hash" the given data
	 *
	 * @param   inputData[] Data to be hashed
	 * @return
	 */
	public byte[] hash (byte[] inputData) throws NestedException
	{
		assert inputData.length != 0;

		try
		{
			MessageDigest sha = MessageDigest.getInstance ("SHA");

			return sha.digest (inputData);
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new NestedException ("Error loading SHA Algorithm.", ex);
		}
	}

	public abstract void setKey (String newKey);
}
