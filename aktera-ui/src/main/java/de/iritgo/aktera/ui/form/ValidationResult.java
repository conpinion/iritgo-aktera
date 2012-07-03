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

package de.iritgo.aktera.ui.form;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.Output;
import java.util.LinkedList;
import java.util.List;


/**
 * The validateInput method in the FormTools class returns an instance
 * of this class in case of a validation error.
 */
public class ValidationResult
{
	/** Error */
	protected static class Error
	{
		public String field;

		public int type;

		public int pageNum;

		public String errorTextId;

		public Error(String field, int type, int pageNum, String errorTextId)
		{
			this.field = field;
			this.type = type;
			this.pageNum = pageNum;
			this.errorTextId = errorTextId;
		}
	}

	/** Message */
	protected static class Message
	{
		public String messageTextId;

		public Message(String messageTextId)
		{
			this.messageTextId = messageTextId;
		}
	}

	/** Error type */
	public static final int ERROR_GENERAL = 0;

	/** Error type */
	public static final int ERROR_MISSING_DUTY_FIELD = 1;

	/** Error type */
	public static final int ERROR_MISSING_DATE_FIELD = 2;

	/** Error type */
	public static final int ERROR_MISSING_TIME_FIELD = 3;

	/** Error type */
	public static final int ERROR_NOT_A_NUMBER = 4;

	/** Error type */
	public static final int ERROR_NOT_A_REAL_NUMBER = 5;

	/** Error type */
	public static final int ERROR_NOT_A_IP_ADDRESS = 6;

	/** Error type */
	public static final int ERROR_WHITESPACE_NOT_ALLOWED = 7;

	/** Error type */
	public static final int ERROR_WHITESPACE_OR_SPECIAL_NOT_ALLOWED = 8;

	/** Error type */
	public static final int ERROR_VALIDATION_CLASS = 9;

	/** Error type */
	public static final int ERROR_REGEXP = 10;

	/** Error type */
	public static final int ERROR_NO_VALID_VALUES = 11;

	/** Error type */
	public static final int ERROR_NOT_AN_EMAIL = 12;

	/** Error type */
	public static final int ERROR_ONLY_DIGITS_ALLOWED = 13;

	/** Error type */
	public static final int ERROR_NOT_AN_INTEGER = 14;

	/** Error type */
	public static final int ERROR_NOT_A_MACADDRESS = 15;

	/** Error type */
	public static final int ERROR_NOT_A_HOSTNAME = 16;
	
	/** Number of error types */
	public static final int NUM_ERROR_TYPES = 17;

	
	/** List of all errors. */
	protected List<Error> errors;

	/** List of all messages. */
	protected List<Message> messages;

	/**
	 * Create a new ValidationResult.
	 */
	public ValidationResult()
	{
		errors = new LinkedList<Error>();
		messages = new LinkedList<Message>();
	}

	/**
	 * Get the first error field.
	 *
	 * @param formular The formular.
	 * @return The first error field.
	 */
	public String getFirstErrorField(FormularDescriptor formular)
	{
		for (Error error : errors)
		{
			return error.field;
		}

		return "";
	}

	/**
	 * Add an error to this result.
	 *
	 * @param inputName The name of the bad input field.
	 * @param errorType Error type.
	 * @param pageNum The index of the page that contains the field.
	 * @param errorTextId Optional error text id.
	 */
	public void addError(String inputField, int errorType, int pageNum, String errorTextId)
	{
		errors.add(new Error(inputField, errorType, pageNum, errorTextId));
	}

	/**
	 * Add an error to this result.
	 *
	 * @param inputName The name of the bad input field.
	 * @param errorTextId Optional error text id.
	 */
	public void addError(String inputField, String errorTextId)
	{
		errors.add(new Error(inputField, ERROR_GENERAL, 0, errorTextId));
	}

	/**
	 * Add an info message to this result.
	 *
	 * @param errorTextId Optional error text id.
	 */
	public void addInfo(String messageTextId)
	{
		messages.add(new Message(messageTextId));
	}

	/**
	 * Check whether an error occurred.
	 *
	 * @return True if an error occurred.
	 */
	public boolean hasErrors()
	{
		return errors.size() > 0;
	}

	/**
	 * Check whether an error occurred.
	 *
	 * @return True if an error occurred.
	 */
	public boolean isValid()
	{
		return ! hasErrors();
	}

	/**
	 * Create response elements for every visible error.
	 *
	 * @param res Model response.
	 * @param formular The formular.
	 */
	public void createResponseElements(ModelResponse res, FormularDescriptor formular)
	{
		if (messages.size() > 0)
		{
			try
			{
				Output outMessages = res.createOutput("IRITGO_formMessages");

				res.add(outMessages);

				int num = 1;

				for (Message message : messages)
				{
					String[] parts = message.messageTextId.split(":");

					if (parts.length == 2)
					{
						Output outMessage = res.createOutput(String.valueOf(num++), parts[1]);

						outMessage.setAttribute("bundle", parts[0]);
						outMessages.add(outMessage);
					}
				}
			}
			catch (ModelException x)
			{
			}
		}

		if (errors.size() > 0)
		{
			boolean[] errorFlags = new boolean[NUM_ERROR_TYPES];

			for (Error error : errors)
			{
				if (formular == null || ! formular.hasPages()
								|| formular.getPageWithField(error.field.replaceAll("_", ".")) == formular.getPage())
				{
					switch (error.type)
					{
						case ERROR_GENERAL:
							res.addError(error.field, "$" + error.errorTextId);
							res.addError("GLOBAL_" + error.field, "$" + error.errorTextId);

							break;

						case ERROR_MISSING_DUTY_FIELD:
							res.addError(error.field, "$missingDutyField");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_missingDutyField", "$missingDutyField");
							}

							break;

						case ERROR_MISSING_DATE_FIELD:
							res.addError(error.field, "$fillInAllDateFields");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_fillInAllDateFields", "$fillInAllDateFields");
							}

							break;

						case ERROR_MISSING_TIME_FIELD:
							res.addError(error.field, "$fillInAllTimeFields");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_fillInAllTimeFields", "$fillInAllTimeFields");
							}

							break;

						case ERROR_NOT_A_NUMBER:
							res.addError(error.field, "$notANumber");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notANumber", "$notANumber");
							}

							break;

						case ERROR_ONLY_DIGITS_ALLOWED:
							res.addError(error.field, "$onlyDigitsAllowed");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_onlyDigitsAllowed", "$onlyDigitsAllowed");
							}

							break;

						case ERROR_NOT_A_REAL_NUMBER:
							res.addError(error.field, "$notARealNumber");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notARealNumber", "$notARealNumber");
							}

							break;

						case ERROR_NOT_A_IP_ADDRESS:
							res.addError(error.field, "$notAIpAddress");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notAIpAddress", "$notAIpAddress");
							}

							break;

						case ERROR_WHITESPACE_NOT_ALLOWED:
							res.addError(error.field, "$whiteSpaceNotAllowed");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_whiteSpaceNotAllowed", "$whiteSpaceNotAllowed");
							}

							break;

						case ERROR_WHITESPACE_OR_SPECIAL_NOT_ALLOWED:
							res.addError(error.field, "$whiteSpaceOrSpecialNotAllowed");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_whiteSpaceOrSpecialNotAllowed", "$whiteSpaceOrSpecialNotAllowed");
							}

							break;

						case ERROR_VALIDATION_CLASS:
							res.addError(error.field, "$" + error.errorTextId);

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_errorValidationClass", "$" + error.errorTextId);
							}

							break;

						case ERROR_REGEXP:
							res.addError(error.field, "$regexpNotMatched");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_regexpNotMatched", "$regexpNotMatched");
							}

							break;

						case ERROR_NO_VALID_VALUES:
							res.addError(error.field, "$noValidValuesDefined");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_noValidValuesDefined", "$noValidValuesDefined");
							}

							break;

						case ERROR_NOT_AN_EMAIL:
							res.addError(error.field, "$notAnEmail");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notAnEmail", "$notAnEmail");
							}

							break;

						case ERROR_NOT_AN_INTEGER:
							res.addError(error.field, "$notAnInteger");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notAnInteger", "$notAnInteger");
							}

							break;

						case ERROR_NOT_A_MACADDRESS:
							res.addError(error.field, "$notAMacAddress");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notAMacAddress", "$notAMacAddress");
							}
							break;
							
						case ERROR_NOT_A_HOSTNAME:
							res.addError(error.field, "$notAHostname");

							if (! errorFlags[error.type])
							{
								res.addError("GLOBAL_notAHostname", "$notAHostname");
							}

							break;
					}

					errorFlags[error.type] = true;
				}
			}
		}
	}
}
