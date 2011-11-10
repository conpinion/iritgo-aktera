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

package de.iritgo.aktera.struts.tags.html;


import de.iritgo.simplelife.string.StringTools;
import org.apache.struts.taglib.TagUtils;
import javax.servlet.jsp.JspException;
import java.util.HashMap;
import java.util.Map;


/**
 * Create form elements for month editing.
 */

/**
 *
 */

/**
 *
 */
public class ProvinceSelectTag extends SelectTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Province strings. */
	static Map<String, String[]> provincesByCountry = new HashMap<String, String[]>();

	/** Province values. */
	static Map<String, String[]> provinceValuesByCountry = new HashMap<String, String[]>();

	static
	{
		provincesByCountry.put("DE", new String[]
		{
						"opt-", "1.province.DE.BE", "1.province.DE.BR", "1.province.DE.BW", "1.province.DE.BY",
						"1.province.DE.HB", "1.province.DE.HE", "1.province.DE.HH", "1.province.DE.MV",
						"1.province.DE.NI", "1.province.DE.NW", "1.province.DE.RP", "1.province.DE.SH",
						"1.province.DE.SL", "1.province.DE.SN", "1.province.DE.ST", "1.province.DE.TH"
		});

		provinceValuesByCountry.put("DE", new String[]
		{
						"", "BE", "BR", "BW", "BY", "HB", "HE", "HH", "MV", "NI", "NW", "RP", "SH", "SL", "SN", "ST",
						"TH"
		});
	}

	/** Read only. */
	protected boolean readOnly = false;

	/** Country. */
	protected String country;

	/** Country property name. */
	protected String countryProperty;

	/**
	 * Set the read only flag.
	 *
	 * @param readOnly Read only flag.
	 */
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Get the read only flag.
	 *
	 * @retrun The read only flag.
	 */
	public boolean getReadOnly()
	{
		return readOnly;
	}

	/**
	 * Set the country.
	 *
	 * @param country The new country
	 */
	public void setCountry(String country)
	{
		this.country = country;
	}

	/**
	 * Get the country.
	 *
	 * @retrun The country.
	 */
	public String getCountry()
	{
		return country;
	}

	/**
	 * Set the country property name.
	 *
	 * @param country The new country property name
	 */
	public void setCountryProperty(String countryProperty)
	{
		this.countryProperty = countryProperty;
	}

	/**
	 * Get the country property name.
	 *
	 * @retrun The country property name.
	 */
	public String getCountryProperty()
	{
		return countryProperty;
	}

	/**
	 * Render the tag.
	 *
	 * @exception JspException if a JSP exception has occurred.
	 */
	public int doEndTag() throws JspException
	{
		String selectedValue = getBeanProperty().toString();

		StringBuffer results = new StringBuffer();

		String countryToUse = country;

		if (selectedValue.contains("."))
		{
			String[] vals = selectedValue.split("\\.");

			countryToUse = vals[0];
			selectedValue = vals[1];
		}

		if (countryToUse == null)
		{
			countryToUse = country;
		}

		if (countryToUse == null && countryProperty != null)
		{
			try
			{
				countryToUse = getNamedBeanProperty(countryProperty).toString();
			}
			catch (Exception ignored)
			{
			}
		}

		if (countryToUse == null)
		{
			try
			{
				countryToUse = getBeanProperty("Country").toString();
			}
			catch (Exception ignored)
			{
			}
		}

		String[] provinces = provincesByCountry.get(countryToUse);
		String[] provinceValues = provinceValuesByCountry.get(countryToUse);

		if (! readOnly)
		{
			createSelectTag(results);

			if (provinces != null && provinceValues != null)
			{
				for (int i = 0; i < provinces.length; ++i)
				{
					results.append("<option value=\"");
					results.append(provinceValues[i]);
					results.append("\"");

					if (provinceValues[i].equals(selectedValue))
					{
						results.append(" selected=\"selected\"");
					}

					results.append(">");
					results.append(TagUtils.getInstance().message(pageContext, bundle, locale, provinces[i]));
					results.append("</option>");
				}
			}

			results.append("</select>\n");
		}
		else
		{
			if (provinces != null && provinceValues != null)
			{
				for (int i = 0; i < provinces.length; ++i)
				{
					if (provinceValues[i].equals(selectedValue))
					{
						results.append(TagUtils.getInstance().message(pageContext, bundle, locale, provinces[i]));
					}
				}
			}
		}

		TagUtils.getInstance().write(pageContext, results.toString());

		return EVAL_PAGE;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release()
	{
		super.release();

		readOnly = false;
		country = null;
		countryProperty = null;
	}
}
