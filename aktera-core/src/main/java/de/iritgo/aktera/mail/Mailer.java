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
 * This interface defines a Mailer service for sending email through a mail-
 * server.
 *
 * The message is auumed to be in multi-part MIME format, based on the number of
 * attachments provided before sending the email.
 *
 * @version        $Revision: 1.3 $        $Date: 2003/10/10 23:12:03 $
 * @author Shash Chatterjee
 * Created on Jan 1, 2003
 */
public interface Mailer
{
	/**
	 * The ROLE name used to acquire services that implement this service
	 */
	public String ROLE = "de.iritgo.aktera.mail.Mailer";

	/**
	 * The address to send email
	 * @param address A standard address, or a comma-separated list of addresses
	 * @throws NestedException
	 */
	public void setToAddress(String address) throws NestedException;

	/**
	 * Set the addresses to send email to
	 * @param addresses An array of String addresses
	 * @throws NestedException
	 */
	public void setToAddress(String[] addresses) throws NestedException;

	/**
	 * Set the address of the sender of the email
	 * @param address
	 * @throws NestedException
	 */
	public void setFromAddress(String address) throws NestedException;

	/**
	 * Set the reply-to header in the email, usually if different from the
	 * sender's email.
	 *
	 * @param address
	 * @throws NestedException
	 */
	public void setReplyToAddress(String address) throws NestedException;

	/**
	 * Set multiple reply-to addresses
	 *
	 * @param addresses
	 * @throws NestedException
	 */
	public void setReplyToAddress(String[] addresses) throws NestedException;

	/**
	 * Set the carbon-copy address for this email
	 *
	 * @param address
	 * @throws NestedException
	 */
	public void setCcAddress(String address) throws NestedException;

	/**
	 * Set multiple carbon-copy addresses
	 *
	 * @param addresses
	 * @throws NestedException
	 */
	public void setCcAddress(String[] addresses) throws NestedException;

	/**
	 * Set the blind-carbon-copy addresses for this email
	 *
	 * @param address
	 * @throws NestedException
	 */
	public void setBccAddress(String address) throws NestedException;

	/**
	 * Set multiple blibd-carbon-copy addresses
	 *
	 * @param addresses
	 * @throws NestedException
	 */
	public void setBccAddress(String[] addresses) throws NestedException;

	/**
	 * Set the subject of the email
	 *
	 * @param subject
	 * @throws NestedException
	 */
	public void setSubject(String subject) throws NestedException;

	/**
	 * Set an arbitrary header
	 *
	 * @param name
	 * @param value
	 * @throws NestedException
	 */
	public void setHeader(String name, String value) throws NestedException;

	/**
	 * Set the text content of the email, weill be sent as first attachment
	 *
	 * @param message
	 * @throws NestedException
	 */
	public void setMessage(String message) throws NestedException;

	/**
	 * Add a MIME attachment, MIME-type will be determined automatically by the
	 * system
	 *
	 * @param fileName
	 * @throws NestedException
	 */
	public void addMIMEAttachment(String fileName) throws NestedException;

	/**
	 * Add multiple MIME attachments
	 *
	 * @param fileNames
	 * @throws NestedException
	 */
	public void addMIMEAttachment(String[] fileNames) throws NestedException;

	/**
	 * Send the email to the mail-server
	 *
	 * @throws NestedException
	 */
	public void send() throws NestedException;

	/**
	 * Send mail to a different host with a different user and password
	 * @param host
	 * @param user
	 * @param password
	 */
	public void send(String host, String user, String password) throws NestedException;
}
