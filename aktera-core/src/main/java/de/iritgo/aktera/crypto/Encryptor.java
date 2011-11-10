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


/**
 *
 *
 */
public interface Encryptor
{
	public static String ROLE = "de.iritgo.aktera.crypto.Encryptor";

	/**
	 * Decrypt the data according to the appropriate algorithm.
	 *
	 * @param   inputData[] The data to be decrypted as a byte array.
	 * @throws  ChainedException If there is an error decrypting the data.
	 * @return The byte array containing the decrypted version of the input
	 * data.
	 */
	public byte[] decrypt(byte[] inputData) throws NestedException;

	/**
	 *  Encrypt the specified data according to the appropriate algorithm.
	 *
	 * @param   inputData[] Data to be encrypted.
	 * @throws  ChainedException If there was an error during encryption.
	 * @return The encrypted data as a byte array.
	 */
	public byte[] encrypt(byte[] inputData) throws NestedException;

	/**
	 * Apply a one-way "hash" to the input data, rendering it unreadable. The
	 * hashed data cannot be decrypted, but the same input data will hash to the
	 * same hashed value. This is appropriate for information such as passwords.
	 *
	 * @param inputData
	 * @return byte[]
	 * @throws NestedException
	 */
	public byte[] hash(byte[] inputData) throws NestedException;

	/**
	 * Set the encryption key - if this algorithm uses such a key
	 * @param newKey The encryption key to be used in subsequent
	 * encryption/decryption calls.
	 */
	public void setKey(String newKey);
}
