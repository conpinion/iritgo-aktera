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

package de.iritgo.aktera.email;


import de.iritgo.aktera.logger.Logger;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;


/**
 * Email service implementation.
 */
public class EmailServiceImpl implements EmailService
{
	/** The standard logger */
	private Logger logger;

	/** True enables verbose debug logging */
	private boolean debug;

	/**
	 * Set the logger.
	 */
	public void setLogger (Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * Set true to enable verbose debugging.
	 *
	 * @param debug True enables debugging
	 */
	public void setDebug (boolean debug)
	{
		this.debug = debug;
	}

	/**
	 * @see de.iritgo.aktera.email.EmailService#sendEmail(de.iritgo.aktera.email.Email)
	 */
	public void sendEmail (Email email) throws EmailSendFailedException
	{
		try
		{
			org.apache.commons.mail.Email commonsEmail = email.getAttachmentFileName () != null ? new MultiPartEmail ()
							: new SimpleEmail ();

			commonsEmail.setDebug (debug);

			commonsEmail.setHostName (email.getMailHost ());
			commonsEmail.addTo (email.getTo (), email.getToName ());

			if (email.getCC () != null)
			{
				commonsEmail.addCc (email.getCC ());
			}

			commonsEmail.setFrom (email.getFrom (), email.getName ());
			commonsEmail.setSubject (email.getSubject ());
			commonsEmail.setMsg (email.getBody ());

			if (! StringTools.isTrimEmpty (email.getReplyTo ()))
			{
				commonsEmail.addReplyTo (email.getReplyTo (), email.getReplyToName ());
			}

			if (! StringTools.isEmpty (email.getAuthName ()))
			{
				commonsEmail.setAuthentication (email.getAuthName (), email.getAuthPassword ());
			}

			if (StringTools.isTrimEmpty (email.getPort ()))
			{
				commonsEmail.setSmtpPort (25);
			}
			else
			{
				commonsEmail.setSmtpPort (NumberTools.toInt (email.getPort (), 25));
			}

			if (email.getAttachmentFileName () != null)
			{
				EmailAttachment attachment = new EmailAttachment ();

				attachment.setPath (email.getAttachmentFileName ());
				attachment.setDisposition (EmailAttachment.ATTACHMENT);
				attachment.setDescription (email.getAttachmentDescription ());
				attachment.setName (email.getAttachmentName ());
				commonsEmail = ((MultiPartEmail) commonsEmail).attach (attachment);
			}

			commonsEmail.send ();
		}
		catch (Exception x)
		{
			throw new EmailSendFailedException (x.getCause ().getMessage (), x.getCause ());
		}
	}
}
