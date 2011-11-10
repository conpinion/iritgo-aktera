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

package de.iritgo.aktera.i18n;


import de.iritgo.aktera.authentication.UserEnvironment;
import de.iritgo.aktera.authorization.AuthorizationException;
import de.iritgo.aktera.core.classloader.KeelURLClassLoader;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.ui.UIRequest;
import de.iritgo.simplelife.string.StringTools;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 */
public class I18NImpl implements I18N
{
	/** Regular expression: Template variables ${var}. */
	private static Pattern reTemplateVariables = Pattern.compile("(\\{([.&[^\\{\\}]]*)\\})");

	/** Regular expression: For Template to trim or extends (with space) the length. */
	private static Pattern reTrimOrExtendTemplateVariables = Pattern.compile("^(.+):(\\+?)([0-9]+):?(\\D*)");

	private Map<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();

	/**
	 * @see de.iritgo.aktera.i18n.I18N#msg(de.iritgo.aktera.model.ModelRequest, java.lang.String, java.lang.String)
	 */
	public String msg(ModelRequest req, String bundle, String key)
	{
		return msg(req, bundle, key, (Object[]) null);
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#msg(de.iritgo.aktera.ui.UIRequest, java.lang.String, java.lang.String)
	 */
	public String msg(UIRequest request, String bundle, String key)
	{
		return msg(request.getLocale(), bundle, key);
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#msg(java.util.Locale, java.lang.String, java.lang.String)
	 */
	public String msg(Locale locale, String bundle, String key)
	{
		return msg(locale, bundle, key, (Object[]) null);
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#msg(de.iritgo.aktera.model.ModelRequest, java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public String msg(ModelRequest req, String bundle, String key, Object... params)
	{
		return msg(getLocale(req), bundle, key, params);
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#msg(de.iritgo.aktera.ui.UIRequest, java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public String msg(UIRequest request, String bundle, String key, Object... params)
	{
		return msg(request.getLocale(), bundle, key, params);
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#msg(java.util.Locale, java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public String msg(Locale locale, String bundle, String key, Object... params)
	{
		if (key.startsWith("$"))
		{
			int colonIndex = key.indexOf(":");

			if (colonIndex != - 1)
			{
				bundle = key.substring(1, colonIndex);
				key = key.substring(colonIndex + 1);
			}
		}

		try
		{
			ResourceBundle rb = getResourceBundle(locale, bundle);

			String text = rb.getString(key);

			if (text.startsWith("$"))
			{
				String[] bundleAndKey = text.substring(1).split(":");

				if (bundleAndKey.length == 2)
				{
					return msg(locale, bundleAndKey[0], bundleAndKey[1], params);
				}
				else
				{
					return msg(locale, bundle, bundleAndKey[0], params);
				}
			}
			else
			{
				if (params != null)
				{
					text = StringTools.replaceTemplate(text, reTemplateVariables, reTrimOrExtendTemplateVariables,
									false, params);
				}

				if (text.startsWith("\\$"))
				{
					return text.substring(1);
				}

				return text;
			}
		}
		catch (MissingResourceException x)
		{
		}
		catch (MalformedURLException x)
		{
		}

		return key;
	}

	/**
	 * Describe method getResourceBundle() here.
	 *
	 * @param locale
	 * @param bundle
	 * @return
	 * @throws MalformedURLException
	 */
	private ResourceBundle getResourceBundle(Locale locale, String bundle) throws MalformedURLException
	{
		ResourceBundle rb = bundles.get(bundle + "Resources_" + locale.toString());

		if (rb == null)
		{
			KeelURLClassLoader cl = new KeelURLClassLoader(new URL[]
			{
				new URL("file://" + System.getProperty("keel.config.dir") + "/../resources/")
			});

			rb = ResourceBundle.getBundle(bundle + "Resources", locale, cl);
			bundles.put(bundle + "Resources_" + locale.toString(), rb);
		}

		return rb;
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#sentenceMsg(java.util.Locale, java.lang.String, java.lang.String)
	 */
	public String sentenceMsg(Locale locale, String defaultBundle, String sentence)
	{
		StringBuffer buffer = new StringBuffer();
		Matcher m = reTemplateVariables.matcher(sentence);
		int pos = 0;

		while (m.find(pos))
		{
			buffer.append(sentence.substring(pos, m.start()));

			String propertyName = m.group(2) != null ? m.group(2) : m.group(3);
			String[] split = propertyName.split("|");

			if (split.length > 1)
			{
				buffer.append(msg(locale, defaultBundle, split[0], split[1]));
			}
			else
			{
				buffer.append(msg(locale, defaultBundle, split[0]));
			}
		}

		return buffer.toString();
	}

	/**
	 * Retrieve the users locale.
	 *
	 * @param req A model reuqest.
	 * @return The users locale.
	 */
	protected Locale getLocale(ModelRequest req)
	{
		try
		{
			Context ctx = req.getContext();

			if (ctx == null)
			{
				return defaultLocale;
			}

			UserEnvironment userEnv = (UserEnvironment) ctx.get(UserEnvironment.CONTEXT_KEY);

			if (userEnv == null || userEnv.getUid() != UserEnvironment.ANONYMOUS_UID)
			{
				return defaultLocale;
			}

			if (userEnv.getAttribute(USER_CONTEXT_LOCALE_KEY) == null)
			{
				return defaultLocale;
			}

			return (Locale) userEnv.getAttribute(USER_CONTEXT_LOCALE_KEY);
		}
		catch (ModelException x)
		{
		}
		catch (ContextException x)
		{
		}
		catch (AuthorizationException x)
		{
		}

		return defaultLocale;
	}

	/**
	 * @see de.iritgo.aktera.i18n.I18N#hasMsg(java.util.Locale, java.lang.String, java.lang.String)
	 */
	public boolean hasMsg(Locale locale, String bundle, String key)
	{
		if (key.startsWith("$"))
		{
			int colonIndex = key.indexOf(":");

			if (colonIndex != - 1)
			{
				bundle = key.substring(1, colonIndex);
				key = key.substring(colonIndex + 1);
			}
		}

		try
		{
			ResourceBundle rb = getResourceBundle(locale, bundle);

			rb.getString(key);

			return true;
		}
		catch (MissingResourceException x)
		{
		}
		catch (MalformedURLException x)
		{
		}

		return false;
	}
}
