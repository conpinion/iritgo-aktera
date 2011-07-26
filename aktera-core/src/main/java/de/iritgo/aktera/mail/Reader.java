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
import java.io.File;
import java.util.Date;
import java.util.Enumeration;


/**
 * This interface defines a Reader service for receiving email from a server.
 *
 * @version        $Revision: 1.3 $        $Date: 2003/10/10 23:12:03 $
 * @author Shash Chatterjee
 * Created on Jan 4, 2003
 */
public interface Reader
{
	/**
	 * The ROLE name used to acquire services that implement this service
	 */
	public String ROLE = "de.iritgo.aktera.mail.Reader";

	/**
	 * Get the client in a state connected to the server
	 * so that it is ready to retrieve messages.  The next
	 * thing to do would be to call read, repeatedly if necessary.
	 * @throws NestedException
	 */
	public void connect () throws NestedException;

	/**
	 * Connect while overriding the user and password supplied by configuration
	 * Saying "connect(null, null, null)" is the same as "connect()"
	 *
	 * @param user Username to use for connect - leave null to use configuration defaults
	 * @param password Password to use to authenticate user - leave null to use configuration default
	 * @throws NestedException If the connect does not succeed
	 */
	public void connect (String host, String user, String password) throws NestedException;

	/**
	 * Close the client-server connection.  Messages can
	 * no longer be accessed.
	 * @throws NestedException
	 */
	public void disconnect () throws NestedException;

	/**
	 * Retrieve all messages from the server
	 * @throws NestedException
	 */
	public void read () throws NestedException;

	/**
	 * Return a count of messages retrieved from the server.
	 * Can only be called after a read().
	 * @return int
	 */
	public int count ();

	/**
	 * Get the size of a message
	 * @param msgIndex A number from 0 to count()-1
	 * @return int The size of the message in bytes
	 * @throws NestedException
	 */
	public int getSize (int msgIndex) throws NestedException;

	/**
	 * Get the number of lines in a message (meaningless for multi-part
	 * messages)
	 * @param msgIndex A number from 0 to count()-1
	 * @return int The number of lines in the message (can return -1)
	 * @throws NestedException
	 */
	public int getLineCount (int msgIndex) throws NestedException;

	/**
	 * Get the subject header from a message
	 * @param msgIndex A number from 0 to count()-1
	 * @return String The value of the subject header
	 * @throws NestedException
	 */
	public String getSubject (int msgIndex) throws NestedException;

	/**
	 * Get the date a message was sent (Date header)
	 * @param msgIndex A number from 0 to count()-1
	 * @return Date The date the message was sent
	 * @throws NestedException
	 */
	public Date getSentDate (int msgIndex) throws NestedException;

	/**
	 * Get the contents of the To: header
	 * @param msgIndex A number from 0 to count()-1
	 * @return String[] A string array of addresses, can be null
	 * @throws NestedException
	 */
	public String[] getTo (int msgIndex) throws NestedException;

	/**
	 * Get the contents of the From: header
	 * @param msgIndex A number from 0 to count()-1
	 * @return String[] A string array of addresses, can be null
	 * @throws NestedException
	 */
	public String[] getFrom (int msgIndex) throws NestedException;

	/**
	 * Get the contents of the ReplyTo: header
	 * @param msgIndex A number from 0 to count()-1
	 * @return String[] A string array of addresses, can be null
	 * @throws NestedException
	 */
	public String[] getReplyTo (int msgIndex) throws NestedException;

	/**
	 * Get the contents of the Cc: header
	 * @param msgIndex A number from 0 to count()-1
	 * @return String[] A string array of addresses, can be null
	 * @throws NestedException
	 */
	public String[] getCc (int msgIndex) throws NestedException;

	/**
	 * Get the contents of the Bcc: header
	 * @param msgIndex A number from 0 to count()-1
	 * @return String[] A string array of addresses, can be null
	 * @throws NestedException
	 */
	public String[] getBcc (int msgIndex) throws NestedException;

	/**
	 * Get all headers for a message
	 * @param msgIndex A number from 0 to count()-1
	 * @return Enumeration An enumeration of javax.mail.Header containing
	 * name/value pairs
	 * @throws NestedException
	 */
	public Enumeration getHeaders (int msgIndex) throws NestedException;

	/**
	 * Check if this message has already been read or not
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if already read
	 * @throws NestedException
	 */
	public boolean isSeen (int msgIndex) throws NestedException;

	/**
	 * Mark a message as "read"
	 * @param msgIndex A number from 0 to count()-1
	 * @param state true=set, false=clear
	 * @throws NestedException
	 */
	public void setSeen (int msgIndex, boolean state) throws NestedException;

	/**
	 * Check if this message has been marked by a client as "deleted" or not
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if deleted
	 * @throws NestedException
	 */
	public boolean isDeleted (int msgIndex) throws NestedException;

	/**
	 * Mark a message as "deleted"
	 * @param msgIndex A number from 0 to count()-1
	 * @param state true=set, false=clear
	 * @throws NestedException
	 */
	public void setDeleted (int msgIndex, boolean state) throws NestedException;

	/**
	 * Check if this message has been marked as "answered" by a client
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if answered
	 * @throws NestedException
	 */
	public boolean isAnswered (int msgIndex) throws NestedException;

	/**
	 * Mark a message as "answered"
	 * @param msgIndex A number from 0 to count()-1
	 * @param state true=set, false=clear
	 * @throws NestedException
	 */
	public void setAnswered (int msgIndex, boolean state) throws NestedException;

	/**
	 * Check if this message has been marked by a client as "flagged"
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if flagged
	 * @throws NestedException
	 */
	public boolean isFlagged (int msgIndex) throws NestedException;

	/**
	 * Mark a message as "flagged
	 * @param msgIndex A number from 0 to count()-1
	 * @param state true=set, false=clear
	 * @throws NestedException
	 */
	public void setFlagged (int msgIndex, boolean state) throws NestedException;

	/**
	 * Check if this message  is amrked by the server as "recent" or not
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if recent
	 * @throws NestedException
	 */
	public boolean isRecent (int msgIndex) throws NestedException;

	/**
	 * Check if this message has been marked by some client as "draft" or not
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if draft
	 * @throws NestedException
	 */
	public boolean isDraft (int msgIndex) throws NestedException;

	/**
	 * Mark a message as "draft
	 * @param msgIndex A number from 0 to count()-1
	 * @param state true=set, false=clear
	 * @throws NestedException
	 */
	public void setDraft (int msgIndex, boolean state) throws NestedException;

	/**
	 * Check if the message content is formatted as MIME multi-part
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if multipart
	 * @throws NestedException
	 */
	public boolean isMultiPart (int msgIndex) throws NestedException;

	/**
	 * Check if the message content is formatted as MIME single-part
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if single-part
	 * @throws NestedException
	 */
	public boolean isSinglePart (int msgIndex) throws NestedException;

	/**
	 * Check if the message content is inline
	 * @param msgIndex A number from 0 to count()-1
	 * @return boolean true if inline
	 * @throws NestedException
	 */
	public boolean isInline (int msgIndex) throws NestedException;

	/**
	 * Get the number of parts in a multi-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @return int The number of parts in the message
	 * @throws NestedException
	 */
	public int getMultiPartCount (int msgIndex) throws NestedException;

	/**
	 * Get the contents of a particular part of a multi-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @param partIndex A number from 0 to getMultiPartCount()-1
	 * @return File
	 * @throws NestedException
	 */
	public File getFile (int msgIndex, int partIndex) throws NestedException;

	/**
	 * Get the file name in the header of a part in the multi-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @param partIndex A number from 0 to getMultiPartCount()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getFileName (int msgIndex, int partIndex) throws NestedException;

	/**
	 * Get the content of the disposition header of a part of a multi-part
	 * message
	 * @param msgIndex A number from 0 to count()-1
	 * @param partIndex A number from 0 to getMultiPartCount()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getDisposition (int msgIndex, int partIndex) throws NestedException;

	/**
	 * Get the content of the ContentType header of a part of a multi-part
	 * message
	 * @param msgIndex A number from 0 to count()-1
	 * @param partIndex A number from 0 to getMultiPartCount()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getContentType (int msgIndex, int partIndex) throws NestedException;

	/**
	 * Get the contents of a single-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @return File
	 * @throws NestedException
	 */
	public File getFile (int msgIndex) throws NestedException;

	/**
	 * Get the file name in the header of a single-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getFileName (int msgIndex) throws NestedException;

	/**
	 * Get the content of the disposition header of a single-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getDisposition (int msgIndex) throws NestedException;

	/**
	 * Get the content of the ContentType header of a single-part message
	 * @param msgIndex A number from 0 to count()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getContentType (int msgIndex) throws NestedException;

	/**
	 * Get the content of an inline message
	 * @param msgIndex A number from 0 to count()-1
	 * @return String
	 * @throws NestedException
	 */
	public String getInline (int msgIndex) throws NestedException;
}
