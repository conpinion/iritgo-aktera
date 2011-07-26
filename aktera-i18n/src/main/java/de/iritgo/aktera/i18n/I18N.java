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


import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.ui.UIRequest;
import java.util.Locale;


/**
 */
public interface I18N
{
	/** Service id. */
	public static final String ID = "de.iritgo.aktera.i18n.I18N";

	/** Key under which the users locale is store in the user context. */
	public static final String USER_CONTEXT_LOCALE_KEY = "de.iritgo.aktera.i18n.USER_CONTEXT_LOCALE_KEY";

	/** Default locale if no user locale was found. */
	public Locale defaultLocale = Locale.GERMAN;

	/**
	 * Retrieve a string from a resource bundle.

	 * @param locale
	 * @param bundle
	 * @param key
	 * @param params
	 * @return
	 */
	public String msg (Locale locale, String bundle, String key, Object... params);

	/**
	 * Retrieve a string from a resource bundle.
	 *
	 * @param Locale the Locale.
	 * @param bundle The resource bundle.
	 * @param key The resource key.
	 * @return The resource value.
	 */
	public String msg (Locale locale, String bundle, String key);

	/**
	 * Retrieve a string from a resource bundle.
	 *
	 * @param req A model request.
	 * @param bundle The resource bundle.
	 * @param key The resource key.
	 * @return The resource value.
	 */
	public String msg (ModelRequest req, String bundle, String key);

	/**
	 * Retrieve a string from a resource bundle.
	 *
	 * @param request A UI controller request.
	 * @param bundle The resource bundle.
	 * @param key The resource key.
	 * @return The resource value.
	 */
	public String msg (UIRequest request, String bundle, String key);

	/**
	 * Retrieve a string from a resource bundle.
	 *
	 * @param req A model request.
	 * @param bundle The resource bundle.
	 * @param key The resource key.
	 * @param params Replacement parameters.
	 * @return The resource value.
	 */
	public String msg (ModelRequest req, String bundle, String key, Object... params);

	/**
	 * Retrieve a string from a resource bundle.
	 *
	 * @param req A model request.
	 * @param bundle The resource bundle.
	 * @param key The resource key.
	 * @param params Replacement parameters.
	 * @return The resource value.
	 */
	public String msg (UIRequest request, String bundle, String key, Object... params);

	/**
	 * Check the existence of ressource string.
	 *
	 * @param Locale the Locale.
	 * @param bundle The resource bundle.
	 * @param key The resource key.
	 * @return True if the ressource string exists.
	 */
	public boolean hasMsg (Locale locale, String bundle, String key);

	/**
	 * Retrieve a sentence like:
	 * "{Aktera:theHouse} {Aktera:number|15} {Aktera:isGreat}."
	 * Das Haus Nummer 15 ist toll.
	 * It support only one parameter per key!
	 *
	 * @param locale The local
	 * @param defaultBundle The default bundle
	 * @param sentence The sentence
	 * @return The translated sentence
	 */
	public String sentenceMsg (Locale locale, String defaultBundle, String sentence);
}
