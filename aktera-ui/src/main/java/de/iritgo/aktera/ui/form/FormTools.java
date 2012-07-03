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


import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.beanutils.*;
import de.iritgo.aktera.configuration.SystemConfigManager;
import de.iritgo.aktera.configuration.preferences.Preferences;
import de.iritgo.aktera.hibernate.StandardDao;
import de.iritgo.aktera.model.*;
import de.iritgo.aktera.persist.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.aktera.tools.ModelTools;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.aktera.ui.el.ExpressionLanguageContext;
import de.iritgo.aktera.ui.ng.ModelRequestWrapper;
import de.iritgo.aktera.ui.tools.UserTools;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;


/**
 * Utility class that helps to create input elements for persistent objects.
 */
public class FormTools
{
	/** Regular expression for validating numbers. */
	private static Pattern reNumber;

	/** Regular expression for validating integers. */
	private static Pattern reInteger;

	/** Regular expression for validating digits. */
	private static Pattern reDigits;

	/** Regular expression for validating real numbers. */
	private static Pattern reRealNumber;

	/** Regular expression for validating ip addresses. */
	private static Pattern reIpAddress;

	/** Regular expression for validating host names. */
	private static Pattern reHostname;

	/** Regular expression for validating no whitespace text. */
	private static Pattern reNoWhiteSpace;

	/**
	 * Regular expression for validating no whitespace and special character
	 * text.
	 */
	private static Pattern reIdentifier;

	/** Regular expression for validating email addresses. */
	private static Pattern reEmail;

	/** DateFormat used to format time property values. */
	private static DateFormat formatPropertyTime = new SimpleDateFormat("HH:mm:00");

	static
	{
		try
		{
			reNumber = Pattern.compile("[0-9]*");
			reInteger = Pattern.compile("([-+])?[0-9]*");
			reDigits = Pattern.compile("[0-9]*");
			reIpAddress = Pattern
							.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
			// reIpAddress =
			// Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(/\\d\\d)?");
			reHostname = Pattern.compile("^(([\\w][\\w\\-\\.]*)\\.)?([\\w][\\w\\-]+)(\\.([\\w][\\w\\.]*))?$");
			reNoWhiteSpace = Pattern.compile("[^ ]+");
			reIdentifier = Pattern.compile("\\w+");
			reRealNumber = Pattern.compile("[0-9]+(\\.[0-9]+)?");
			reEmail = Pattern.compile("[\\w\\.-]+((@){1}?[\\w\\.-]+)?");
		}
		catch (PatternSyntaxException x)
		{
			System.out.println("FormTools: " + x);
		}
	}

	/**
	 * Create the key under which the persistents are stored in the user
	 * context.
	 *
	 * @param contextId The context id.
	 * @param id The object id.
	 * @return The context key.
	 */
	public static String createContextKey(String contextId, Object id)
	{
		return "aktera.formular:" + contextId + ":" + id;
	}

	/**
	 * Retrieve the request id of the formular persistents.
	 *
	 * @param req A model request.
	 * @param keyName Alternate id name.
	 * @return The persistent id.
	 */
	public static Object getPersistentId(ModelRequest req, String formName, String keyName)
	{
		if (req.getParameter("new") == null)
		{
			String kname = "";

			if (keyName != null)
			{
				kname = keyName;
			}
			else if (req.getParameter("prevId") != null)
			{
				kname = "prevId";
			}
			else
			{
				kname = "id";
			}

			if (req.getParameter(kname) instanceof String[])
			{
				Object id = ((String[]) req.getParameter(kname))[0];

				return id != null ? id : new Integer(- 1);
			}
			else
			{
				Object id = req.getParameter(kname);

				return id != null ? id : new Integer(- 1);
			}
		}

		return new Integer(- 1);
	}

	/**
	 * Retrieve a formular descriptor that is stored in the user context.
	 *
	 * @param req A model request.
	 * @param formName The name of the formular.
	 * @param id The persistents id.
	 * @return The formular.
	 */
	public static FormularDescriptor getFormularFromContext(ModelRequest req, String formName, Object id)
	{
		String formId = createContextKey(formName, id);
		FormularDescriptor formular = (FormularDescriptor) UserTools.getContextObject(req, formId);

		return formular;
	}

	/**
	 * Retrieve a formular descriptor that is stored in the user context.
	 *
	 * @param req A model request.
	 * @param formName The name of the formular.
	 * @param id The persistents id.
	 * @return The formular.
	 */
	public static FormularDescriptor getFormularFromContext(UIRequest request, String formName, Object id)
	{
		String formId = createContextKey(formName, id);
		FormularDescriptor formular = (FormularDescriptor) UserTools.getContextObject(new ModelRequestWrapper(request),
						formId);

		return formular;
	}

	/**
	 * Retrieve a formular descriptor that is stored in the user context.
	 *
	 * @param req A model request.
	 * @param formName The name of the formular.
	 * @param keyName Alternate id name.
	 * @return The formular.
	 */
	public static FormularDescriptor getFormularFromContext(ModelRequest req, String formName, String keyName)
	{
		String formId = createContextKey(formName, getPersistentId(req, formName, keyName));

		return (FormularDescriptor) UserTools.getContextObject(req, formId);
	}

	/**
	 * Create all input and output elements for the given formular.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param formular The formular description.
	 * @param persistents The persistent instances.
	 */
	public static void createResponseElements(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents, ExpressionLanguageContext context)
		throws ModelException, PersistenceException
	{
		createResponseElements(req, res, formular, persistents, null, formular.getReadOnly(), context);
	}

	/**
	 * Create all input and output elements for the given formular.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param formular The formular description.
	 * @param persistents The persistent instances.
	 * @param commands The formular commands.
	 */
	public static void createResponseElements(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents, CommandDescriptor commands, ExpressionLanguageContext context)
		throws ModelException, PersistenceException
	{
		createResponseElements(req, res, formular, persistents, commands, formular.getReadOnly(), context);
	}

	/**
	 * Create all input and output elements for the given formular.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param formular The formular description.
	 * @param persistents The persistent instances.
	 * @param commands The formular commands.
	 * @param readOnly If true, all fields are created as read only fields.
	 */
	public static void createResponseElements(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents, CommandDescriptor commands, boolean readOnly,
					ExpressionLanguageContext context) throws ModelException, PersistenceException
	{
		Output labelWidth = res.createOutput("labelWidth");

		labelWidth.setContent(new Integer(formular.getLabelWidth()));
		res.add(labelWidth);

		if (formular.getTitle() != null)
		{
			Output title = res.createOutput("title");

			title.setContent(formular.getTitle());
			title.setAttribute("bundle", formular.getTitleBundle());
			res.add(title);
		}

		if (formular.getIcon() != null)
		{
			Output icon = res.createOutput("icon");

			icon.setContent(formular.getIcon());
			res.add(icon);
		}

		if (! formular.hasVisibleButtons())
		{
			Output out = res.createOutput("hideButtonBar", "Y");

			res.add(out);
		}

		String idName = formular.getIdField();

		if (idName != null)
		{
			if (idName.contains("."))
			{
				String idFieldName = idName.substring(idName.indexOf('.') + 1);
				String idPersistentName = idName.substring(0, Math.max(idName.indexOf('.'), 0));
				Object idPersistent = persistents.get(idPersistentName);
				Input inId = res.createInput("id");

				inId.setDefaultValue(getAttributeFromDataObject(idPersistent, idFieldName));
				res.add(inId);
			}
			else
			{
				Input inId = res.createInput("id");

				inId.setDefaultValue(persistents.getAttribute(idName));
				res.add(inId);
			}
		}

		Output groupList = res.createOutput("groups");

		if (readOnly)
		{
			groupList.setAttribute("readOnly", Boolean.TRUE);
		}

		res.add(groupList);

		int currentPage = Math.max(Math.min(formular.getPage(), formular.getPageCount() - 1), 0);

		Output outCurrentPage = res.createOutput("currentPage");
		outCurrentPage.setContent(currentPage);
		res.add(outCurrentPage);

		Output pageList = null;

		Iterator iGroups = formular.groupIterator();

		if (formular.hasPages())
		{
			pageList = res.createOutput("pages");
			res.add(pageList);

			int pageNum = 0;

			for (Iterator i = formular.pageIterator(); i.hasNext(); ++pageNum)
			{
				PageDescriptor page = (PageDescriptor) i.next();

				Command command = ModelTools.createPreviousModelCommand(req, res);

				command.setName(String.valueOf(pageNum));
				command.setLabel(page.getLabel());
				command.setParameter("page", String.valueOf(pageNum));
				command.setAttribute("page", String.valueOf(pageNum));
				command.setAttribute("bundle", page.getBundle());

				if (page.hasIcon())
				{
					command.setAttribute("icon", page.getIcon());

					command.setAttribute("inactiveIcon",
									page.hasInactiveIcon() ? page.getInactiveIcon() : page.getIcon());
				}

				boolean active = pageNum == currentPage;

				if (active)
				{
					command.setAttribute("active", "Y");
				}

				if (pageNum == 0)
				{
					command.setAttribute("style", active ? "af" : "f");
				}
				else
				{
					if (i.hasNext())
					{
						command.setAttribute("style", active ? "am" : "m");
					}
					else
					{
						command.setAttribute("style", active ? "al" : "l");
					}
				}

				pageList.add(command);
			}

			iGroups = formular.getPage(currentPage).groupIterator();
		}

		int groupNum = 0;

		for (; iGroups.hasNext(); ++groupNum)
		{
			GroupDescriptor group = (GroupDescriptor) iGroups.next();

			if (! group.isVisible())
			{
				continue;
			}

			Output outGroup = res.createOutput(String.valueOf(groupNum), group.getLabel());

			if (StringTools.isNotTrimEmpty(group.getBundle()))
			{
				outGroup.setAttribute("bundle", group.getBundle());
			}
			else
			{
				outGroup.setAttribute("bundle", formular.getBundle());
			}

			if (readOnly)
			{
				outGroup.setAttribute("readOnly", Boolean.TRUE);
			}

			if (! group.isTitleVisible())
			{
				outGroup.setAttribute("hideTitle", Boolean.TRUE);
			}

			if (! StringTools.isTrimEmpty(group.getIcon()))
			{
				outGroup.setAttribute("icon", group.getIcon());
			}

			groupList.add(outGroup);

			int num = 1;

			for (Iterator<FieldDescriptor> j = group.fieldIterator(); j.hasNext();)
			{
				FieldDescriptor field = (FieldDescriptor) j.next();

				createResponseElementsForField(req, res, field, persistents, outGroup, readOnly, num++, context);
			}
		}

		if (req.getParameter("error") != null)
		{
			res.addOutput("focus", req.getParameterAsString("error"));
		}

		if (commands != null)
		{
			for (Iterator i = commands.iterator(); i.hasNext();)
			{
				CommandInfo descriptor = (CommandInfo) i.next();

				if ("save".equals(descriptor.getName()) && readOnly)
				{
					continue;
				}
				else if ("cancel".equals(descriptor.getName()))
				{
					Command command = descriptor.createCancelCommand(req, res, context);

					res.add(command);
				}
				else
				{
					Command command = descriptor.createCommand(req, res, context);

					res.add(command);
				}
			}
		}
	}

	/**
	 * Create an input element for a formular field.
	 *
	 * An input has the following attributes:
	 *
	 * <table>
	 * <tr>
	 * <th>Name</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>editor</td>
	 * <td>string</td>
	 * <td>Id of the field editor.</td>
	 * </tr>
	 * <tr>
	 * <td>size</td>
	 * <td>integer</td>
	 * <td>Display size of the editor.</td>
	 * </tr>
	 * <tr>
	 * <td>readOnly</td>
	 * <td>flag</td>
	 * <td>If set, this is a read only field.</td>
	 * </tr>
	 * <tr>
	 * <td>duty</td>
	 * <td>flag</td>
	 * <td>If set, this is a duty field.</td>
	 * </tr>
	 * </table>
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param persistents The persistent instances.
	 * @param field The formular field descriptor.
	 * @param group An optional grouping output which can be null.
	 * @param readOnly If true, all fields are created as read only fields.
	 * @param num The current element number.
	 */
	public static void createResponseElementsForField(ModelRequest req, ModelResponse res, FieldDescriptor field,
					PersistentDescriptor persistents, Output outGroup, boolean readOnly, int num,
					ExpressionLanguageContext context) throws ModelException, PersistenceException
	{
		ValidationResult result = new ValidationResult();

		String name = field.getName();

		String fieldName = name.substring(name.indexOf('.') + 1);
		String persistentName = name.substring(0, Math.max(name.indexOf('.'), 0));
		String inputName = name.replace('.', '_').replace('(', '_').replace(')', '_');

		Object persistent = null;

		try
		{
			persistent = persistents.get(persistentName);
		}
		catch (ModelException ignored)
		{
		}

		PersistentMetaData meta = null;

		if (persistent != null && persistent instanceof Persistent)
		{
			meta = ((Persistent) persistent).getMetaData();
		}

		if (field.isHideIfNull() && persistent != null && getAttributeFromDataObject(persistent, fieldName) == null)
		{
			return;
		}

		if (field.isOmitted())
		{
			return;
		}

		Input input = res.createInput(inputName + ("list".equals(field.getEditor()) ? "List" : ""));

		input.setLabel(field.getLabel() != null ? field.getLabel() : fieldName);

		input.setAttribute("bundle", field.getBundle());
		input.setAttribute("odd", new Boolean(num % 2 == 1));
		input.setAttribute("even", new Boolean(num % 2 == 0));

		if (field.isSelectable())
		{
			input.setAttribute("selectable", "Y");
		}

		if (field.getToolTip() != null)
		{
			input.setAttribute("toolTip", field.getToolTip());
		}

		input.setAttribute("size", new Integer(field.getSize()));

		if (field.getRows() != 0)
		{
			input.setAttribute("rows", new Integer(field.getRows()));
		}

		if ("date".equals(field.getEditor()))
		{
			input.setAttribute("editor", "datecombo");
		}
		else if ("time".equals(field.getEditor()))
		{
			input.setAttribute("editor", "timecombo");
		}
		else if ("timestamp".equals(field.getEditor()))
		{
			input.setAttribute("editor", "timestampcombo");
		}
		else if (field.getEditor().startsWith("regexp:"))
		{
			input.setAttribute("editor", "text");
		}
		else
		{
			input.setAttribute("editor", field.getEditor());
		}

		if (readOnly || field.isReadOnly() || (field.isBound() && meta != null && meta.isReadOnly(fieldName)))
		{
			input.setAttribute("readOnly", "Y");
		}

		input.setAttribute("disabled", field.isDisabled());

		if (field.isDuty() && ! field.isReadOnly() && ! field.isDisabled())
		{
			input.setAttribute("duty", "Y");
		}

		if (field.hasValidationClass() && ! field.isReadOnly())
		{
			input.setAttribute("duty", "Y");
		}

		if (field.isSubmit())
		{
			input.setAttribute("submit", "Y");
		}

		if (field.isComment())
		{
			input.setAttribute("desc", "Y");

			Preferences preferences = UserTools.getUserPreferences(req);

			if (preferences == null || preferences.getPowerUser() == null
							|| ! preferences.getPowerUser().booleanValue())
			{
				if (outGroup != null)
				{
					outGroup.add(input);
				}

				res.add(input);
			}

			return;
		}

		if (field.isNoLabel())
		{
			input.setAttribute("noLabel", "Y");
		}

		if ("date".equals(field.getEditor()))
		{
			Date date = null;

			if (field.isBound())
			{
				date = (Date) getAttributeFromDataObject(persistent, fieldName);
			}
			else
			{
				date = (Date) persistents.getAttribute(inputName);
			}

			input.setDefaultValue(date);

			Input inputDay = res.createInput(inputName + "Day");

			res.add(inputDay);

			Input inputMonth = res.createInput(inputName + "Month");

			res.add(inputMonth);

			Input inputYear = res.createInput(inputName + "Year");

			res.add(inputYear);

			Calendar calendar = new GregorianCalendar();

			if (date != null)
			{
				calendar.setTime(date);
				inputDay.setDefaultValue(new Integer(calendar.get(Calendar.DAY_OF_MONTH)));
				inputMonth.setDefaultValue(new Integer(calendar.get(Calendar.MONTH) + 1));
				inputYear.setDefaultValue(new Integer(calendar.get(Calendar.YEAR)));
			}
			else
			{
				inputDay.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Day"), - 1));
				inputMonth.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Month"), - 1));
				inputYear.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Year"), - 1));
			}
		}
		else if ("time".equals(field.getEditor()))
		{
			Time time = null;

			if (field.isBound())
			{
				time = (Time) getAttributeFromDataObject(persistent, fieldName);
			}
			else
			{
				time = (Time) persistents.getAttribute(inputName);
			}

			input.setDefaultValue(time);

			Input inputHour = res.createInput(inputName + "Hour");

			res.add(inputHour);

			Input inputMinute = res.createInput(inputName + "Minute");

			res.add(inputMinute);

			Calendar calendar = new GregorianCalendar();

			if (time != null)
			{
				calendar.setTime(time);
				inputHour.setDefaultValue(new Integer(calendar.get(Calendar.HOUR_OF_DAY)));
				inputMinute.setDefaultValue(new Integer(calendar.get(Calendar.MINUTE)));
			}
			else
			{
				inputHour.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Hour"), - 1));
				inputMinute.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Minute"),
								- 1));
			}
		}
		else if ("timestamp".equals(field.getEditor()))
		{
			Timestamp timestamp = null;

			if (field.isBound())
			{
				timestamp = (Timestamp) getAttributeFromDataObject(persistent, fieldName);
			}
			else
			{
				timestamp = (Timestamp) persistents.getAttribute(fieldName);
			}

			input.setDefaultValue(timestamp);

			Input inputDay = res.createInput(inputName + "Day");

			res.add(inputDay);

			Input inputMonth = res.createInput(inputName + "Month");

			res.add(inputMonth);

			Input inputYear = res.createInput(inputName + "Year");

			res.add(inputYear);

			Input inputHour = res.createInput(inputName + "Hour");

			res.add(inputHour);

			Input inputMinute = res.createInput(inputName + "Minute");

			res.add(inputMinute);

			Calendar calendar = new GregorianCalendar();

			if (timestamp != null)
			{
				calendar.setTime(timestamp);
				inputDay.setDefaultValue(new Integer(calendar.get(Calendar.DAY_OF_MONTH)));
				inputMonth.setDefaultValue(new Integer(calendar.get(Calendar.MONTH) + 1));
				inputYear.setDefaultValue(new Integer(calendar.get(Calendar.YEAR)));
				inputHour.setDefaultValue(new Integer(calendar.get(Calendar.HOUR_OF_DAY)));
				inputMinute.setDefaultValue(new Integer(calendar.get(Calendar.MINUTE)));
			}
			else
			{
				inputDay.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Day"), - 1));
				inputMonth.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Month"), - 1));
				inputYear.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Year"), - 1));
				inputHour.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Hour"), - 1));
				inputMinute.setDefaultValue(NumberTools.toIntInstance(persistents.getAttribute(inputName + "Minute"),
								- 1));
			}
		}
		else if ("money".equals(field.getEditor()))
		{
			Input inputValue = res.createInput(inputName + "Value");

			res.add(inputValue);

			if (field.isBound())
			{
				if (getAttributeFromDataObject(persistent, fieldName) != null)
				{
					inputValue.setDefaultValue(getAttributeFromDataObject(persistent, fieldName));
				}
				else
				{
					inputValue.setDefaultValue(getAttributeDefaultValueFromDataObject(persistent, fieldName));
				}
			}

			Input inputCurrency = res.createInput(inputName + "Currency");

			res.add(inputCurrency);

			String currencyFieldName = fieldName + "Currency";

			if (field.isBound())
			{
				if (getAttributeFromDataObject(persistent, currencyFieldName) != null)
				{
					inputCurrency.setDefaultValue(getAttributeFromDataObject(persistent, currencyFieldName));
				}
				else
				{
					inputCurrency.setDefaultValue(getAttributeDefaultValueFromDataObject(persistent, fieldName));
				}
			}
		}
		else if ("combo".equals(field.getEditor()))
		{
			if (field.isBound())
			{
				if (getAttributeFromDataObject(persistent, fieldName) != null)
				{
					Object value = getAttributeFromDataObject(persistent, fieldName);
					if (! value.getClass().isEnum() && ! (value instanceof String) && ! (value instanceof Integer))
					{
						value = getAttributeFromDataObject(persistent, fieldName + ".id");
					}
					input.setDefaultValue(value);
				}
				else
				{
					input.setDefaultValue(getAttributeDefaultValueFromDataObject(persistent, fieldName));
				}

				try
				{
					input.setValidValues(getAttributeValidValuesFromDataObject(persistent, fieldName));
				}
				catch (PersistenceException x)
				{
					Map validValues = (Map) persistents.getAttribute(inputName + "ValidValues");

					if (validValues != null)
					{
						input.setValidValues((Map) persistents.getAttribute(inputName + "ValidValues"));
					}
					else
					{
						TreeMap dummyValues = new TreeMap();
						dummyValues.put("", "---");
						input.setValidValues(dummyValues);
						addError(res, ValidationResult.ERROR_NO_VALID_VALUES, inputName, result, 0);
					}
				}
			}
			else
			{
				input.setDefaultValue(persistents.getAttribute(inputName));

				Map validValues = (Map) persistents.getAttribute(inputName + "ValidValues");

				if (validValues != null)
				{
					input.setValidValues(validValues);
				}
				else
				{
					TreeMap dummyValues = new TreeMap();

					dummyValues.put("", "---");
					input.setValidValues(dummyValues);
					addError(res, ValidationResult.ERROR_NO_VALID_VALUES, inputName, result, 0);
				}
			}
		}
		else if ("list".equals(field.getEditor()))
		{
			input.setAttribute("listId", inputName);
		}
		else if ("none".equals(field.getEditor()))
		{
			input.setDefaultValue("");
		}
		else if ("check".equals(field.getEditor()))
		{
			if (field.isBound())
			{
				if (getAttributeFromDataObject(persistent, fieldName) != null)
				{
					input.setDefaultValue(NumberTools.toBool(getAttributeFromDataObject(persistent, fieldName), false));
				}
				else
				{
					input.setDefaultValue(getAttributeDefaultValueFromDataObject(persistent, fieldName));
				}
			}
			else
			{
				input.setDefaultValue(NumberTools.toBool(persistents.getAttribute(inputName), false));
			}
		}
		else if (field.getEditor().startsWith("jsp:"))
		{
			input.setAttribute("jsp", field.getEditor().substring(4));
		}
		else
		{
			if (persistents.hasAttribute(inputName))
			{
				input.setDefaultValue(persistents.getAttribute(inputName));
			}
			else
			{
				if (req.getParameter(name) != null)
				{
					input.setDefaultValue(req.getParameterAsString(name));
				}
				else if (req.getParameter(fieldName) != null)
				{
					input.setDefaultValue(req.getParameterAsString(fieldName));
				}
				else if (field.isBound())
				{
					if (getAttributeFromDataObject(persistent, fieldName) != null)
					{
						input.setDefaultValue(getAttributeFromDataObject(persistent, fieldName));
					}
					else
					{
						input.setDefaultValue(getAttributeDefaultValueFromDataObject(persistent, fieldName));
					}
				}
				else
				{
					if (persistents.hasAttribute(fieldName))
					{
						input.setDefaultValue(persistents.getAttribute(fieldName));
					}
					else
					{
						input.setDefaultValue(persistents.getAttribute(inputName));
					}
				}
			}
		}

		if (outGroup != null)
		{
			outGroup.add(input);
		}

		if (field.getCommands().commandCount() > 0)
		{
			Output commands = res.createOutput("commands");
			input.setAttribute("commands", commands);
			for (Iterator i = field.getCommands().iterator(); i.hasNext();)
			{
				CommandInfo descriptor = (CommandInfo) i.next();
				Command command = descriptor.createCommand(req, res, context);
				commands.add(command);
			}
		}

		res.add(input);

		if (res.get("focus") == null && ! field.isHidden() && ! field.isReadOnly())
		{
			res.addOutput("focus", inputName);
		}
	}

	/**
	 * Validate the complete formular input against the formular and persistent
	 * constraints. This method generates global and field specific error
	 * objects in the model response.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param formular The formular description.
	 * @param persistents The persistent instances.
	 * @return A validation result.
	 * @throws ModelException
	 */
	public static ValidationResult validateInput(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents) throws ModelException
	{
		ValidationResult result = new ValidationResult();

		if (formular.hasPages())
		{
			int currentPageNum = Math.max(Math.min(formular.getPage(), formular.getPageCount() - 1), 0);

			int pageNum = 0;

			for (Iterator iPages = formular.pageIterator(); iPages.hasNext();)
			{
				PageDescriptor page = (PageDescriptor) iPages.next();

				validateInput(req, res, formular, persistents, page.groupIterator(), result, pageNum);

				++pageNum;
			}
		}
		else
		{
			validateInput(req, res, formular, persistents, formular.groupIterator(), result, 0);
		}

		return result;
	}

	/**
	 * Validate the complete formular input against the formular and persistent
	 * constraints. This method generates global and field specific error
	 * objects in the model response.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param formular The formular description.
	 * @param persistents The persistent instances.
	 * @param result The validation result.
	 * @param pageNum The current page number.
	 * @throws ModelException
	 */
	public static void validateInput(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents, Iterator iGroups, ValidationResult result, int pageNum)
		throws ModelException
	{
		for (; iGroups.hasNext();)
		{
			GroupDescriptor group = (GroupDescriptor) iGroups.next();

			if (! group.isVisible())
			{
				continue;
			}

			for (Iterator<FieldDescriptor> j = group.fieldIterator(); j.hasNext();)
			{
				FieldDescriptor field = (FieldDescriptor) j.next();

				if (field.isReadOnly() || field.isOmitted())
				{
					continue;
				}

				String name = field.getName();
				String fieldName = name.substring(name.indexOf('.') + 1);
				String inputName = name.replace('.', '_').replace('(', '_').replace(')', '_');

				if ("text".equals(field.getEditor()) || "password".equals(field.getEditor())
								|| "number".equals(field.getEditor()) || "integer".equals(field.getEditor())
								|| "digits".equals(field.getEditor()) || "realnumber".equals(field.getEditor())
								|| "ipaddress".equals(field.getEditor()) || "nospacetext".equals(field.getEditor())
								|| "identifier".equals(field.getEditor()) || "country".equals(field.getEditor())
								|| "combo".equals(field.getEditor()) || "email".equals(field.getEditor())
								|| "macaddress".equals(field.getEditor()) || "day".equals(field.getEditor())
								|| "month".equals(field.getEditor()) || "year".equals(field.getEditor())
								|| "hostname".equals(field.getEditor()) || field.getEditor().startsWith("regexp"))
				{
					if (field.isDuty() && ! field.isDisabled() && StringTools.isTrimEmpty(persistents.getAttribute(inputName)))
					{
						addError(res, ValidationResult.ERROR_MISSING_DUTY_FIELD, inputName, result, pageNum);
					}
					else
					{
						if (field.hasValidationClass())
						{
							if (field.getValidationClass().checkValidation(req, persistents, field,
											(String) persistents.getAttribute(inputName)))
							{
								addError(res, ValidationResult.ERROR_VALIDATION_CLASS, inputName, result, field
												.getValidationClass().getErrorTextId(), pageNum);
							}
						}

						if (! StringTools.isTrimEmpty(persistents.getAttribute(inputName)))
						{
							if ("number".equals(field.getEditor())
											&& ! reNumber.matcher(StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_NOT_A_NUMBER, inputName, result, pageNum);
							}
							else if ("integer".equals(field.getEditor())
											&& ! reInteger.matcher(
															StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_NOT_AN_INTEGER, inputName, result, pageNum);
							}
							else if ("digits".equals(field.getEditor())
											&& ! reDigits.matcher(StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_ONLY_DIGITS_ALLOWED, inputName, result, pageNum);
							}
							else if ("realnumber".equals(field.getEditor())
											&& ! reRealNumber.matcher(
															StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_NOT_A_REAL_NUMBER, inputName, result, pageNum);
							}
							else if ("ipaddress".equals(field.getEditor())
											&& ! reIpAddress.matcher(
															StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_NOT_A_IP_ADDRESS, inputName, result, pageNum);
							}
							else if ("hostname".equals(field.getEditor())
											&& ! reHostname.matcher(
															StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_NOT_A_HOSTNAME, inputName, result, pageNum);
							}
							else if ("nospacetext".equals(field.getEditor())
											&& ! reNoWhiteSpace.matcher(
															StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_WHITESPACE_NOT_ALLOWED, inputName, result, pageNum);
							}
							else if ("identifier".equals(field.getEditor())
											&& ! reIdentifier.matcher(
															StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_WHITESPACE_OR_SPECIAL_NOT_ALLOWED, inputName,
												result, pageNum);
							}
							else if ("email".equals(field.getEditor())
											&& ! reEmail.matcher(StringTools.trim(persistents.getAttribute(inputName)))
															.matches())
							{
								addError(res, ValidationResult.ERROR_NOT_AN_EMAIL, inputName, result, pageNum);
							}
							else if (("password".equals(field.getEditor()))
											&& (((String) persistents.getAttribute(inputName)).indexOf(" ") > - 1))
							{
								addError(res, ValidationResult.ERROR_WHITESPACE_OR_SPECIAL_NOT_ALLOWED, inputName,
												result, pageNum);
							}
							else if (("macaddress".equals(field.getEditor())))
							{
								try
								{
									StringTools.normalizeMACAddress((String) persistents.getAttribute(inputName));
								}
								catch (IllegalArgumentException ignored)
								{
									addError(res, ValidationResult.ERROR_NOT_A_MACADDRESS, inputName, result, pageNum);
								}
							}
							else if (field.getEditor().startsWith("regexp:"))
							{
								try
								{
									Pattern re = Pattern.compile(field.getEditor().substring(7));

									if (! re.matcher((String) persistents.getAttribute(inputName)).matches())
									{
										addError(res, ValidationResult.ERROR_REGEXP, inputName, result, pageNum);
									}
								}
								catch (PatternSyntaxException x)
								{
									throw new ModelException("FormTools: Regexp editor unable to compile pattern: " + x);
								}
							}
						}
					}
				}
				else if ("date".equals(field.getEditor()))
				{
					int day = NumberTools.toInt(persistents.getAttribute(inputName + "Day"), - 1);
					int month = NumberTools.toInt(persistents.getAttribute(inputName + "Month"), - 1);
					int year = NumberTools.toInt(persistents.getAttribute(inputName + "Year"), - 1);

					if (field.isDuty() && ! field.isDisabled() && day == - 1 && month == - 1 && year == - 1)
					{
						addError(res, ValidationResult.ERROR_MISSING_DUTY_FIELD, inputName, result, pageNum);
					}

					if ((day != - 1 || month != - 1 || year != - 1) && (day == - 1 || month == - 1 || year == - 1))
					{
						addError(res, ValidationResult.ERROR_MISSING_DATE_FIELD, inputName, result, pageNum);
					}
				}
				else if ("time".equals(field.getEditor()))
				{
					int minute = NumberTools.toInt(persistents.getAttribute(inputName + "Minute"), - 1);
					int hour = NumberTools.toInt(persistents.getAttribute(inputName + "Hour"), - 1);

					if (field.isDuty() && ! field.isDisabled() && minute == - 1 && hour == - 1)
					{
						addError(res, ValidationResult.ERROR_MISSING_DUTY_FIELD, inputName, result, pageNum);
					}

					if ((minute != - 1 || hour != - 1) && (minute == - 1 || hour == - 1))
					{
						addError(res, ValidationResult.ERROR_MISSING_TIME_FIELD, inputName, result, pageNum);
					}
				}
				else if ("money".equals(field.getEditor()))
				{
					if (field.isDuty() && ! field.isDisabled() && StringTools.isTrimEmpty(persistents.getAttribute(inputName)))
					{
						addError(res, ValidationResult.ERROR_MISSING_DUTY_FIELD, inputName, result, pageNum);
					}
				}
			}
		}
	}

	/**
	 * Helper method for the validateInput method.
	 *
	 * This method sets a global error, but only if this is the first time, that
	 * a specific error occurred. In any case a field error is generated.
	 *
	 * @param res The model response.
	 * @param errorType The error type.
	 * @param inputName The name of the input field.
	 * @param result The validation result.
	 * @param pageNum The index of the page that contains the field.
	 * @throws ModelException
	 */
	public static void addError(ModelResponse res, int errorType, String inputName, ValidationResult result, int pageNum)
		throws ModelException
	{
		addError(res, errorType, inputName, result, "", pageNum);
	}

	/**
	 * Helper method for the validateInput method.
	 *
	 * This method sets a global error, but only if this is the first time, that
	 * a specific error occurred. In any case a field error is generated.
	 *
	 * @param res The model response.
	 * @param errorType The error type.
	 * @param inputName The name of the input field.
	 * @param result The validation result.
	 * @param string The errorTextId
	 * @param pageNum The index of the page that contains the field.
	 * @throws ModelException
	 */
	public static void addError(ModelResponse res, int errorType, String inputName, ValidationResult result,
					String errorTextId, int pageNum) throws ModelException
	{
		String name = inputName.replace('.', '_').replace('(', '_').replace(')', '_');

		result.addError(name, errorType, pageNum, errorTextId);
	}

	/**
	 * Add an error to a model response and validation result.
	 *
	 * @param res The model response.
	 * @param result The validation result.
	 * @param field The error field.
	 * @param message The error message.
	 */
	public static void addError(ModelResponse res, ValidationResult result, String field, String message)
	{
		String name = field.replace('.', '_');

		result.addError(name, ValidationResult.ERROR_GENERAL, 0, message);
	}

	/**
	 * Add an info message to a model response and validation result.
	 *
	 * @param res The model response.
	 * @param result The validation result.
	 * @param message The info message.
	 */
	public static void addInfo(ModelResponse res, ValidationResult result, String message)
	{
		result.addInfo(message);
	}

	/**
	 * Transfer the complete formular input into the persistent objects.
	 *
	 * @param req The model request.
	 * @param res The model response.
	 * @param formular The formular description.
	 * @param persistents The persistent instances.
	 * @return True If the stored input has modified the persistents.
	 * @throws ModelException
	 */
	public static boolean storeInput(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents, Logger logger) throws ModelException, PersistenceException
	{
		boolean modified = false;

		Calendar calendar = new GregorianCalendar();

		int page = Math.max(Math.min(formular.getPage(), formular.getPageCount() - 1), 0);

		Iterator iGroups = formular.hasPages() ? formular.getPage(page).groupIterator() : formular.groupIterator();

		for (; iGroups.hasNext();)
		{
			GroupDescriptor group = (GroupDescriptor) iGroups.next();

			if (! group.isVisible())
			{
				continue;
			}

			for (Iterator<FieldDescriptor> j = group.fieldIterator(); j.hasNext();)
			{
				FieldDescriptor field = (FieldDescriptor) j.next();

				if (field.isReadOnly() || field.isDisabled() || field.isOmitted() || field.isComment() || field.getEditor().startsWith("jsp"))
				{
					continue;
				}

				String name = field.getName();
				String fieldName = name.substring(name.indexOf('.') + 1);
				String persistentName = name.substring(0, Math.max(name.indexOf('.'), 0));
				String inputName = name.replace('.', '_').replace('(', '_').replace(')', '_');

				Object persistent = null;
				PersistentMetaData meta = null;

				if (field.isSelectable() && req.getParameter(inputName + "_Selected") != null)
				{
					persistents.setAttributeSelected(inputName, true);
				}

				if (field.isBound())
				{
					persistent = persistents.get(persistentName);

					if (persistent instanceof Persistent)
					{
						meta = ((Persistent) persistent).getMetaData();
					}
				}

				if ("money".equals(field.getEditor()))
				{
					if (! StringTools.isTrimEmpty(req.getParameter(inputName)))
					{
						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
										NumberTools.toDoubleInstance(req.getParameter(inputName), 0.0));
						persistents.putAttribute(inputName, req.getParameterAsString(inputName));
					}
					else
					{
						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, null);
						persistents.removeAttribute(inputName);
					}

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName
									+ "Currency", req.getParameterAsString(inputName + "Currency"));
					persistents.putAttribute(inputName + "Currency", req.getParameterAsString(inputName + "Currency"));
				}
				else if ("date".equals(field.getEditor()))
				{
					int day = NumberTools.toInt(req.getParameter(inputName + "Day"), - 1);
					int month = NumberTools.toInt(req.getParameter(inputName + "Month"), - 1);
					int year = NumberTools.toInt(req.getParameter(inputName + "Year"), - 1);

					persistents.putAttribute(inputName + "Day", new Integer(day));
					persistents.putAttribute(inputName + "Month", new Integer(month));
					persistents.putAttribute(inputName + "Year", new Integer(year));

					if (day != - 1 && month != - 1 && year != - 1)
					{
						calendar.setTimeInMillis(System.currentTimeMillis());
						calendar.set(Calendar.DAY_OF_MONTH, day);
						calendar.set(Calendar.MONTH, month - 1);
						calendar.set(Calendar.YEAR, year);

						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
										new Date(calendar.getTime().getTime()));
						persistents.putAttribute(inputName, new Date(calendar.getTime().getTime()));
					}
					else
					{
						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, null);
						persistents.removeAttribute(inputName);
					}
				}
				else if ("time".equals(field.getEditor()))
				{
					int hour = NumberTools.toInt(req.getParameter(inputName + "Hour"), - 1);
					int minute = NumberTools.toInt(req.getParameter(inputName + "Minute"), - 1);

					persistents.putAttribute(inputName + "Hour", new Integer(hour));
					persistents.putAttribute(inputName + "Minute", new Integer(minute));

					if (minute != - 1 && hour != - 1)
					{
						Time time = Time.valueOf(hour + ":" + minute + ":0");

						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, time);
						persistents.putAttribute(inputName, time);
					}
					else
					{
						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, null);
						persistents.removeAttribute(inputName);
					}
				}
				else if ("timestamp".equals(field.getEditor()))
				{
					int day = NumberTools.toInt(req.getParameter(inputName + "Day"), - 1);
					int month = NumberTools.toInt(req.getParameter(inputName + "Month"), - 1);
					int year = NumberTools.toInt(req.getParameter(inputName + "Year"), - 1);
					int hour = NumberTools.toInt(req.getParameter(inputName + "Hour"), - 1);
					int minute = NumberTools.toInt(req.getParameter(inputName + "Minute"), - 1);

					persistents.putAttribute(inputName + "Day", new Integer(day));
					persistents.putAttribute(inputName + "Month", new Integer(month));
					persistents.putAttribute(inputName + "Year", new Integer(year));
					persistents.putAttribute(inputName + "Hour", new Integer(hour));
					persistents.putAttribute(inputName + "Minute", new Integer(minute));

					if (day != - 1 && month != - 1 && year != - 1 && minute != - 1 && hour != - 1)
					{
						calendar.setTimeInMillis(System.currentTimeMillis());
						calendar.set(Calendar.DAY_OF_MONTH, day);
						calendar.set(Calendar.MONTH, month - 1);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MINUTE, minute);
						calendar.set(Calendar.HOUR_OF_DAY, hour);

						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
										new Timestamp(calendar.getTime().getTime()));
						persistents.putAttribute(inputName, new Timestamp(calendar.getTime().getTime()));
					}
					else
					{
						setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, null);
						persistents.removeAttribute(inputName);
					}
				}
				else if ("check".equals(field.getEditor()))
				{
					boolean oldValue = persistents.hasAttribute(inputName) ? ((Boolean) persistents
									.getAttribute(inputName)).booleanValue() : false;

					modified |= (NumberTools.toBool(req.getParameter(inputName), false) != oldValue);

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
									NumberTools.toBoolInstance(req.getParameter(inputName), false));
					persistents.putAttribute(inputName, NumberTools.toBoolInstance(req.getParameter(inputName), false));
				}
				else if ("number".equals(field.getEditor()) && ! StringTools.isTrimEmpty(req.getParameter(inputName)))
				{
					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
									NumberTools.toIntInstance(req.getParameter(inputName), 0));
					persistents.putAttribute(inputName, req.getParameterAsString(inputName));
				}
				else if ("macaddress".equals(field.getEditor())
								&& ! StringTools.isTrimEmpty(req.getParameter(inputName)))
				{
					String macAddress = req.getParameterAsString(inputName);

					try
					{
						macAddress = StringTools.normalizeMACAddress(macAddress);
					}
					catch (IllegalArgumentException ignored)
					{
					}

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, macAddress);
					persistents.putAttribute(inputName, req.getParameterAsString(inputName));
				}
				else if ("list".equals(field.getEditor()))
				{
				}
				else if ("dipswitch".equals(field.getEditor()))
				{
					String oldVal = (String) persistents.getAttribute(inputName);
					StringBuffer val = new StringBuffer();

					if (oldVal != null)
					{
						for (int i = 0; i < oldVal.length(); ++i)
						{
							val.append(req.getParameter(inputName + i) != null ? "1" : "0");
						}
					}

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
									val.toString());
					persistents.putAttribute(inputName, val.toString());
				}
				else if ("weekdaycheck".equals(field.getEditor()))
				{
					StringBuffer val = new StringBuffer();

					for (int i = 0; i < 7; ++i)
					{
						val.append(req.getParameter(inputName + i) != null ? (i + ",") : "");
					}

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName,
									val.toString());
					persistents.putAttribute(inputName, val.toString());
				}
				else if ("seconds".equals(field.getEditor()) || "minutes".equals(field.getEditor())
								|| "hours".equals(field.getEditor()) || "days".equals(field.getEditor())
								|| "months".equals(field.getEditor()) || "weekdays".equals(field.getEditor()))
				{
					Object vs = req.getParameter(inputName);
					String[] values = null;

					if (vs instanceof String[])
					{
						values = (String[]) vs;
					}
					else
					{
						values = vs != null ? new String[]
						{
							vs.toString()
						} : new String[0];
					}

					StringBuilder commaValues = new StringBuilder();

					if (values != null)
					{
						for (String value : values)
						{
							StringTools.appendWithDelimiter(commaValues, value, ",");
						}
					}

					String newValue = commaValues.toString();

					if (persistents.hasAttribute(inputName))
					{
						modified |= ! newValue.equals(persistents.getAttribute(inputName));
					}
					else
					{
						modified |= ! newValue.equals(getAttributeFromDataObject(persistent, fieldName));
					}

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, newValue);
					persistents.putAttribute(inputName, newValue);
				}
				else
				{
					String value = req.getParameterAsString(inputName);

					if (! "textarea".equals(field.getEditor()))
					{
						value = StringTools.trim(value);
					}

					if (value != null && meta != null && meta.getLength(fieldName) > 0)
					{
						value = value.substring(0, Math.min(meta.getLength(fieldName), value.length()));
					}

					if (persistents.hasAttribute(inputName))
					{
						modified |= ! value.equals(persistents.getAttribute(inputName));
					}

					setAttributeOnDataObjectSafe(logger, persistents, persistent, persistentName, fieldName, value);
					persistents.putAttribute(inputName, value);
				}
			}
		}

		return modified;
	}

	/**
	 * This method sets the input values by reading the data of a property
	 * table. The persistents of this table must contain the fields 'category',
	 * 'name', 'type', 'validValues' and 'value'.
	 *
	 * @param req The model request.
	 * @param formular The formular description.
	 * @param persistents The persistent descriptor in which to store the input
	 *            values.
	 * @param persistentId The id of the persistent objects of the property
	 *            table.
	 */
	public static void createInputValuesFromPropertyTable(ModelRequest req, FormularDescriptor formular,
					PersistentDescriptor persistents, String persistentId) throws ModelException, PersistenceException
	{
		createInputValuesFromPropertyTable(req, formular, persistents, persistentId, null);
	}

	/**
	 * This method sets the input values by reading the data of a property
	 * table. The persistents of this table must contain the fields 'category',
	 * 'name', 'type', 'validValues' and 'value'.
	 *
	 * @param req The model request.
	 * @param formular The formular description.
	 * @param persistents The persistent descriptor in which to store the input
	 *            values.
	 * @param persistentId The id of the persistent objects of the property
	 *            table.
	 * @param category Only create input values for fields that match this
	 *            category.
	 */
	public static void createInputValuesFromPropertyTable(ModelRequest req, FormularDescriptor formular,
					PersistentDescriptor persistents, String persistentId, String category)
		throws ModelException, PersistenceException
	{
		createInputValuesFromPropertyTable(req, formular, persistents, persistentId, category,
						UserTools.getCurrentUserId(req));
	}

	/**
	 * This method sets the input values by reading the data of a property
	 * table. The persistents of this table must contain the fields 'category',
	 * 'name', 'type', 'validValues' and 'value'.
	 *
	 * @param req The model request.
	 * @param formular The formular description.
	 * @param persistents The persistent descriptor in which to store the input
	 *            values.
	 * @param persistentId The id of the persistent objects of the property
	 *            table.
	 * @param category Only create input values for fields that match this
	 *            category.
	 */
	public static void createInputValuesFromPropertyTable(ModelRequest req, FormularDescriptor formular,
					PersistentDescriptor persistents, String persistentId, String category, Integer userId)
		throws ModelException, PersistenceException
	{
		PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE,
						req.getDomain());

		Persistent sample = persistentManager.create(persistentId);

		if (sample.getMetaData().hasField("userId"))
		{
			sample.setField("userId", userId);
		}

		if (! StringTools.isTrimEmpty(category))
		{
			sample.setField("category", category);
		}

		for (Iterator i = sample.query().iterator(); i.hasNext();)
		{
			Persistent persistent = (Persistent) i.next();

			String fieldName = persistent.getFieldString("category") + "." + persistent.getFieldString("name");

			if (! formular.containsField(fieldName))
			{
				continue;
			}

			String key = persistent.getFieldString("category") + "_" + persistent.getFieldString("name");

			switch (persistent.getFieldString("type").charAt(0))
			{
				case 'S':
					persistents.putAttribute(key, persistent.getFieldString("value"));

					break;

				case 'B':
					persistents.putAttribute(key, NumberTools.toBoolInstance(persistent.getFieldString("value")));

					break;

				case 'I':
					persistents.putAttribute(key, NumberTools.toIntInstance(persistent.getFieldString("value")));

					break;

				case 'C':
				{
					StringTokenizer st = new StringTokenizer(persistent.getFieldString("validValues"), "|");
					TreeMap validValues = new TreeMap();

					while (st.hasMoreTokens())
					{
						String aValidValue = st.nextToken();

						validValues.put(aValidValue, "$" + aValidValue);
					}

					persistents.putAttribute(key, persistent.getFieldString("value"));
					persistents.putAttribute(key + "ValidValues", validValues);

					break;
				}

				case 'T':
					persistents.putAttribute(key, Time.valueOf(persistent.getFieldString("value")));

					break;
			}
		}
	}

	/**
	 * This method stores the input values into a property table. The
	 * persistents of this table must contain the fields 'category', 'name',
	 * 'type', 'validValues' and 'value'.
	 *
	 * @param req The model request.
	 * @param formular The formular description.
	 * @param persistents The persistent descriptor in which to store the input
	 *            values.
	 * @param persistentId The id of the persistent objects of the property
	 *            table.
	 * @return True if any of the values have changed.
	 */
	public static boolean storeInputValuesToPropertyTable(ModelRequest req, FormularDescriptor formular,
					PersistentDescriptor persistents, String persistentId) throws ModelException, PersistenceException
	{
		return storeInputValuesToPropertyTable(req, formular, persistents, persistentId, null);
	}

	/**
	 * This method stores the input values into a property table. The
	 * persistents of this table must contain the fields 'category', 'name',
	 * 'type', 'validValues' and 'value'.
	 *
	 * @param req The model request.
	 * @param formular The formular description.
	 * @param persistents The persistent descriptor in which to store the input
	 *            values.
	 * @param persistentId The id of the persistent objects of the property
	 *            table.
	 * @param category Only store input values for fields that match this
	 *            category.
	 * @return True if any of the values have changed.
	 */
	public static boolean storeInputValuesToPropertyTable(ModelRequest req, FormularDescriptor formular,
					PersistentDescriptor persistents, String persistentId, String category)
		throws ModelException, PersistenceException
	{
		return storeInputValuesToPropertyTable(req, formular, persistents, persistentId, category,
						UserTools.getCurrentUserId(req));
	}

	/**
	 * This method stores the input values into a property table. The
	 * persistents of this table must contain the fields 'category', 'name',
	 * 'type', 'validValues' and 'value'.
	 *
	 * @param req The model request.
	 * @param formular The formular description.
	 * @param persistents The persistent descriptor in which to store the input
	 *            values.
	 * @param persistentId The id of the persistent objects of the property
	 *            table.
	 * @param category Only store input values for fields that match this
	 *            category.
	 * @return True if any of the values have changed.
	 */
	public static boolean storeInputValuesToPropertyTable(ModelRequest req, FormularDescriptor formular,
					PersistentDescriptor persistents, String persistentId, String category, Integer userId)
		throws ModelException, PersistenceException
	{
		boolean modified = false;

		PersistentFactory persistentManager = (PersistentFactory) req.getService(PersistentFactory.ROLE,
						req.getDomain());

		Persistent sample = persistentManager.create(persistentId);

		if (sample.getMetaData().hasField("userId"))
		{
			sample.setField("userId", userId);
		}

		if (! StringTools.isTrimEmpty(category))
		{
			sample.setField("category", category);
		}

		for (Iterator i = sample.query().iterator(); i.hasNext();)
		{
			Persistent persistent = (Persistent) i.next();

			String fieldName = persistent.getFieldString("category") + "." + persistent.getFieldString("name");

			if (! formular.containsField(fieldName))
			{
				continue;
			}

			String key = persistent.getFieldString("category") + "_" + persistent.getFieldString("name");

			modified |= ! StringTools.trim(persistent.getField("value")).equals(
							StringTools.trim(persistents.getAttribute(key)));

			switch (persistent.getFieldString("type").charAt(0))
			{
				case 'S':
					persistent.setField("value", StringTools.trim(persistents.getAttribute(key)));

					break;

				case 'B':
					persistent.setField("value", NumberTools.toBoolInstance(persistents.getAttribute(key)));

					break;

				case 'I':
					persistent.setField("value", NumberTools.toIntInstance(persistents.getAttribute(key)));

					break;

				case 'C':
					persistent.setField("value", persistents.getAttribute(key));

					break;

				case 'T':

					try
					{
						persistent.setField("value", formatPropertyTime.format(persistents.getAttribute(key)));
					}
					catch (Exception x)
					{
						persistent.setField("value", "00:00:00");
					}

					break;
			}

			persistent.update();

			SystemConfigManager systemConfigManager = (SystemConfigManager) req.getSpringBean(SystemConfigManager.ID);

			systemConfigManager.invalidate(persistent.getFieldString("category"), persistent.getFieldString("name"));
		}

		return modified;
	}

	/**
	 */
	public static void loadPersistents(FormularDescriptor formular, PersistentDescriptor persistents)
		throws ModelException, PersistenceException
	{
		if (formular.hasPages())
		{
			int currentPageNum = Math.max(Math.min(formular.getPage(), formular.getPageCount() - 1), 0);

			int pageNum = 0;

			for (Iterator iPages = formular.pageIterator(); iPages.hasNext();)
			{
				PageDescriptor page = (PageDescriptor) iPages.next();

				loadPersistents(formular, persistents, page.groupIterator());

				++pageNum;
			}
		}
		else
		{
			loadPersistents(formular, persistents, formular.groupIterator());
		}
	}

	/**
	 */
	public static void loadPersistents(FormularDescriptor formular, PersistentDescriptor persistents, Iterator iGroups)
		throws ModelException, PersistenceException
	{
		for (; iGroups.hasNext();)
		{
			GroupDescriptor group = (GroupDescriptor) iGroups.next();

			if (! group.isVisible())
			{
				continue;
			}

			for (Iterator<FieldDescriptor> j = group.fieldIterator(); j.hasNext();)
			{
				FieldDescriptor field = (FieldDescriptor) j.next();

				if (field.isReadOnly() || ! field.isBound() || field.isOmitted())
				{
					continue;
				}

				String name = field.getName();
				String fieldName = name.substring(name.indexOf('.') + 1);
				String persistentName = name.substring(0, Math.max(name.indexOf('.'), 0));
				String inputName = name.replace('.', '_').replace('(', '_').replace(')', '_');

				if (StringTools.isTrimEmpty(persistentName) && field.isBound())
				{
					throw new ModelException("No persistent specified for bound parameter '" + inputName + "'");
				}

				Object attributeValue = getAttributeFromDataObject(persistents.get(persistentName), fieldName);

				if ("text".equals(field.getEditor()) || "textarea".equals(field.getEditor())
								|| "password".equals(field.getEditor()) || "number".equals(field.getEditor())
								|| "integer".equals(field.getEditor()) || "digits".equals(field.getEditor())
								|| "realnumber".equals(field.getEditor()) || "ipaddress".equals(field.getEditor())
								|| "nospacetext".equals(field.getEditor()) || "identifier".equals(field.getEditor())
								|| "country".equals(field.getEditor()) || "macaddress".equals(field.getEditor())
								|| "combo".equals(field.getEditor()) || "email".equals(field.getEditor())
								|| field.getEditor().startsWith("regexp"))
				{
					persistents.putAttribute(inputName, StringTools.trim(attributeValue));
				}
				else if ("check".equals(field.getEditor()))
				{
					persistents.putAttribute(inputName, NumberTools.toBoolInstance(attributeValue));
				}
			}
		}
	}

	/**
	 * Retrieve an attribute either from a Persistent object or a general bean
	 * object.
	 *
	 * @param o The object
	 * @param name The attribute name
	 * @return The attribute value
	 * @throws PersistenceException If the attribute couldn't be retrieved
	 */
	protected static Object getAttributeFromDataObject(Object o, String name) throws PersistenceException
	{
		if (o instanceof Persistent)
		{
			return ((Persistent) o).getField(name);
		}
		else
		{
			try
			{
				return PropertyUtils.getNestedProperty(o, name);
			}
			catch (IllegalAccessException x)
			{
			}
			catch (InvocationTargetException x)
			{
			}
			catch (NoSuchMethodException x)
			{
			}
		}

		throw new PersistenceException("Object of type " + o.getClass() + " doesn't contain attribute '" + name + "'");
	}

	/**
	 * Set an attribute either on a Persistent object or a general bean object.
	 *
	 * @param o The object
	 * @param value The attribute vale
	 * @throws PersistenceException If the attribute couldn't be retrieved
	 */
	private static void setAttributeOnDataObject(Logger logger, Object o, String name, Object value)
		throws PersistenceException
	{
		if (o == null)
		{
			return;
		}

		if (o instanceof Persistent)
		{
			((Persistent) o).setField(name, value);

			return;
		}
		else
		{
			try
			{
				PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(o, name);

				if (descriptor != null && Enum.class.isAssignableFrom(descriptor.getPropertyType()))
				{
					value = MethodUtils.invokeExactStaticMethod(descriptor.getPropertyType(), "valueOf", value);
				}
				else if (descriptor != null
								&& descriptor.getPropertyType().getAnnotation(javax.persistence.Entity.class) != null)
				{
					StandardDao standardDAO = (StandardDao) SpringTools.getBean(StandardDao.ID);
					value = standardDAO.get(descriptor.getPropertyType().getName(), NumberTools.toInt(value, - 1));
				}

				PropertyUtils.setNestedProperty(o, name, value);

				return;
			}
			catch (IllegalAccessException x)
			{
				logger.error("Unable to set property " + name + " in object " + o, x);
			}
			catch (InvocationTargetException x)
			{
				logger.error("Unable to set property " + name + " in object " + o, x);
			}
			catch (NoSuchMethodException x)
			{
				logger.error("Unable to set property " + name + " in object " + o, x);
			}
		}

		throw new PersistenceException("Object of type " + o.getClass() + " doesn't contain attribute '" + name + "'");
	}

	/**
	 * Set an attribute either on a Persistent object, a general bean object or
	 * as a persistent attribute.
	 *
	 * @param persistents
	 * @param persistent
	 * @param persistentName
	 * @param fieldName
	 * @param value
	 * @throws PersistenceException
	 */
	private static void setAttributeOnDataObject(Logger logger, PersistentDescriptor persistents, Object persistent,
					String persistentName, String fieldName, Object value) throws PersistenceException
	{
		try
		{
			setAttributeOnDataObject(logger, persistent, fieldName, value);
		}
		catch (PersistenceException x)
		{
			if (persistent instanceof Persistent)
			{
				persistents.putAttribute((Persistent) persistent, persistentName, fieldName, value.toString());
			}
		}
	}

	/**
	 * Set an attribute either on a Persistent object, a general bean object or
	 * as a persistent attribute. Ignore any exceptions.
	 *
	 * @param persistents
	 * @param persistent
	 * @param persistentName
	 * @param fieldName
	 * @param value
	 * @throws PersistenceException
	 */
	private static void setAttributeOnDataObjectSafe(Logger logger, PersistentDescriptor persistents,
					Object persistent, String persistentName, String fieldName, Object value)
	{
		try
		{
			setAttributeOnDataObject(logger, persistents, persistent, persistentName, fieldName, value);
		}
		catch (PersistenceException ignored)
		{
		}
		catch (IllegalArgumentException ignored)
		{
		}
	}

	/**
	 * Retrieve an attribute's valid values either from a Persistent object or a
	 * general bean object.
	 *
	 * @param o The object
	 * @param name The attribute name
	 * @return The attribute's valid values
	 * @throws PersistenceException If the attribute couldn't be retrieved
	 */
	protected static Map getAttributeValidValuesFromDataObject(Object o, String name) throws PersistenceException
	{
		if (o instanceof Persistent)
		{
			return ((Persistent) o).getValidValues(name);
		}
		else
		{
			// TODO: Try to get valid values from the entity bean
			throw new PersistenceException();
		}
	}

	/**
	 * Retrieve the default value of an attribute either from a Persistent
	 * object or a general bean object.
	 *
	 * @param o The object
	 * @param name The attribute name
	 * @return The attribute value
	 * @throws PersistenceException If the attribute couldn't be retrieved
	 */
	protected static Object getAttributeDefaultValueFromDataObject(Object o, String name) throws PersistenceException
	{
		if (o instanceof Persistent)
		{
			return ((Persistent) o).getMetaData().getDefaultValue(name);
		}
		else
		{
			try
			{
				return PropertyUtils.getNestedProperty(o, name + "DefaultValue");
			}
			catch (IllegalAccessException x)
			{
			}
			catch (InvocationTargetException x)
			{
			}
			catch (NoSuchMethodException x)
			{
			}
		}

		return null;
	}

	/**
	 * @param req
	 * @param res
	 * @param formular
	 * @param persistents
	 * @param commands
	 * @throws ModelException
	 * @throws PersistenceException
	 */
	public static void createResponseElements(ModelRequest req, ModelResponse res, FormularDescriptor formular,
					PersistentDescriptor persistents, CommandDescriptor commands)
		throws ModelException, PersistenceException
	{
		createResponseElements(req, res, formular, persistents, commands, new ExpressionLanguageContext(req));
	}
}
