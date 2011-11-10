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


import java.io.File;


/**
 * Instances of Email contain all information about a specific email to send.
 */
public class Email
{
	/** The smpt server */
	private String mailHost;

	/** The email address from the sender*/
	private String from;

	/** The receiver email address */
	private String to;

	/** The carbon copy email address */
	private String cc;

	/** The subject for this email */
	private String subject;

	/** The body content for the mail*/
	private String body;

	/** The complete user name for the sender */
	private String name;

	/** The authentication name */
	private String authName;

	/** The authentication password */
	private String authPassword;

	/** The attachement file */
	private String attachmentFileName;

	/** The attachement name */
	private String attachmentName;

	/** The attachement description */
	private String attachmentDescription;

	/** The smtp port */
	private String port;

	/** The reply to */
	private String replyTo;

	private String replyToName;

	private String toName;

	/**
	 * Create a new Email.
	 */
	public Email()
	{
	}

	/**
	 * Create a new Email.
	 *
	 * @param mailHost The smpt server
	 * @param from The sender email address
	 * @param to The receiver email address
	 */
	public Email(String serverUrl, String from, String to)
	{
		this.mailHost = serverUrl;
		this.from = from;
		this.to = to;
	}

	/**
	 * Set the mail server URL.
	 *
	 * @param mailHost The mail server URL
	 */
	public void setMailHost(String mailHost)
	{
		this.mailHost = mailHost;
	}

	/**
	 * Get the mail server URL.
	 *
	 * @return The mail server URL
	 */
	public String getMailHost()
	{
		return mailHost;
	}

	/**
	 * Set the email subject.
	 *
	 * @param subject The email subject
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * Get the email subject.
	 *
	 * @return The email subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Get the sender name.
	 *
	 * @return The sender name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the sender name.
	 *
	 * @param name The sender name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the sender address.
	 *
	 * @return The sender address
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * Set the sender address.
	 *
	 * @param from The sender address
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

	/**
	 * Get the receiver address.
	 *
	 * @return The receiver address
	 */
	public String getTo()
	{
		return to;
	}

	/**
	 * Set the receiver address.
	 *
	 * @param to The receiver address
	 */
	public void setTo(String to)
	{
		this.to = to;
	}

	/**
	 * Get the carbon copy address.
	 *
	 * @return The carbon copy address
	 */
	public String getCC()
	{
		return cc;
	}

	/**
	 * Set the carbon copy address.
	 *
	 * @param to The carbon copy address
	 */
	public void setCC(String cc)
	{
		this.cc = cc;
	}

	/**
	 * Get the email body.
	 *
	 * @return The email body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * Set the email body.
	 *
	 * @param body The email body
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	/**
	 * Get the authentication name.
	 *
	 * @return The authentication name
	 */
	public String getAuthName()
	{
		return authName;
	}

	/**
	 * Set the authentication name.
	 *
	 * @param authName The authentication name
	 */
	public void setAuthName(String authName)
	{
		this.authName = authName;
	}

	/**
	 * Return the reply to name
	 *
	 * @return The name
	 */
	public String getReplyToName()
	{
		return replyToName;
	}

	/**
	 * Set the reply to name
	 *
	 * @param replyToName The name
	 */
	public void setReplyToName(String replyToName)
	{
		this.replyToName = replyToName;
	}

	/**
	 * Get the authentication password.
	 *
	 * @return The authentication password
	 */
	public String getAuthPassword()
	{
		return authPassword;
	}

	/**
	 * Set the authentication password.
	 *
	 * @param authPassword The authentication password
	 */
	public void setAuthPassword(String authPassword)
	{
		this.authPassword = authPassword;
	}

	/**
	 * Get the attachment file name.
	 *
	 * @return The attachment file name
	 */
	public String getAttachmentFileName()
	{
		return attachmentFileName;
	}

	/**
	 * Set the attachment file name.
	 *
	 * @param attachmentFileName The attachment file name
	 */
	public void setAttachmentFileName(String attachmentFilename)
	{
		this.attachmentFileName = attachmentFilename;
	}

	/**
	 * Get the attachment name.
	 *
	 * @return The attachment name
	 */
	public String getAttachmentName()
	{
		return attachmentName;
	}

	/**
	 * Set the attachment name.
	 *
	 * @param attachmentName The attachment name
	 */
	public void setAttachmentName(String attachmentName)
	{
		this.attachmentName = attachmentName;
	}

	/**
	 * Get the attachment description.
	 *
	 * @return The attachment description
	 */
	public String getAttachmentDescription()
	{
		return attachmentDescription;
	}

	/**
	 * Set the attachment description.
	 *
	 * @param attachmentDescription The new attachment description
	 */
	public void setAttachmentDescription(String attachmentDesc)
	{
		this.attachmentDescription = attachmentDesc;
	}

	/**
	 * Fluent interface.
	 */
	public Email from(String address, String name)
	{
		this.from = address;
		this.name = name;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email to(String address)
	{
		this.to = address;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email to(String address, String name)
	{
		this.to = address;
		this.toName = name;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email withSubject(String subject)
	{
		this.subject = subject;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email andBody(String body)
	{
		this.body = body;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email via(String mailHost)
	{
		this.mailHost = mailHost;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email authenticatedBy(String authName, String authPassword)
	{
		this.authName = authName;
		this.authPassword = authPassword;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email attach(String attachmentFileName, String attachmentName, String attachmentDescription)
	{
		this.attachmentFileName = attachmentFileName;
		this.attachmentName = attachmentName;
		this.attachmentDescription = attachmentDescription;

		return this;
	}

	/**
	 * Fluent interface.
	 */
	public Email attach(File attachmentFileName, String attachmentName, String attachmentDescription)
	{
		this.attachmentFileName = attachmentFileName.getAbsolutePath();
		this.attachmentName = attachmentName;
		this.attachmentDescription = attachmentDescription;

		return this;
	}

	/**
	 * The port
	 *
	 * @param smtpPort The smtp port
	 * @return The email Object
	 */
	public Email port(String smtpPort)
	{
		this.port = smtpPort;

		return this;
	}

	/**
	 * @return The cc
	 */
	public String getCc()
	{
		return cc;
	}

	/**
	 * Set cc
	 *
	 * @param cc
	 */
	public void setCc(String cc)
	{
		this.cc = cc;
	}

	/**
	 * @return The port
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 * Set the port
	 *
	 * @param port
	 */
	public void setPort(String port)
	{
		this.port = port;
	}

	/**
	 * @return reply to
	 */
	public String getReplyTo()
	{
		return replyTo;
	}

	/**
	 * Set reply to
	 * @return reply to
	 */
	public void setReplyToTo(String replyTo)
	{
		this.replyTo = replyTo;
	}

	/**
	 * The reply to
	 *
	 * @param replyTo The replyto
	 * @return The email Object
	 */
	public Email replyTo(String replyTo, String replyToName)
	{
		this.replyTo = replyTo;
		this.replyToName = replyToName;

		return this;
	}

	/**
	 * The to name
	 *
	 * @return The name
	 */
	public String getToName()
	{
		return toName;
	}
}
