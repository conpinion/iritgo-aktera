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


import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import javax.servlet.jsp.JspException;


/**
 * Create form elements for language editing.
 */
public class CountrySelectTag extends SelectTagBase
{
	/** */
	private static final long serialVersionUID = 1L;

	/** Country strings. */
	static String[] countries = new String[]
	{
					"1.country.00", "1.country.AF", "1.country.EG", "1.country.AL", "1.country.DZ", "1.country.AD",
					"1.country.AO", "1.country.AI", "1.country.AQ", "1.country.AG", "1.country.GQ", "1.country.AR",
					"1.country.AM", "1.country.AW", "1.country.AZ", "1.country.ET", "1.country.AU", "1.country.BS",
					"1.country.BH", "1.country.BD", "1.country.BB", "1.country.BE", "1.country.BZ", "1.country.BJ",
					"1.country.BM", "1.country.BT", "1.country.MM", "1.country.BO", "1.country.BA", "1.country.BW",
					"1.country.BV", "1.country.BR", "1.country.IO", "1.country.BN", "1.country.BG", "1.country.BF",
					"1.country.BI", "1.country.CL", "1.country.CN", "1.country.CX", "1.country.CK", "1.country.CR",
					"1.country.DK", "1.country.DE", "1.country.DJ", "1.country.DM", "1.country.DO", "1.country.EC",
					"1.country.SV", "1.country.CI", "1.country.ER", "1.country.EE", "1.country.FK", "1.country.FO",
					"1.country.FJ", "1.country.FI", "1.country.FR", "1.country.GF", "1.country.PF", "1.country.TF",
					"1.country.GA", "1.country.GM", "1.country.GE", "1.country.GH", "1.country.GI", "1.country.GD",
					"1.country.GR", "1.country.GL", "1.country.GB", "1.country.GP", "1.country.GU", "1.country.GT",
					"1.country.GN", "1.country.GW", "1.country.GY", "1.country.HT", "1.country.HM", "1.country.HN",
					"1.country.HK", "1.country.IN", "1.country.ID", "1.country.IQ", "1.country.IR", "1.country.IE",
					"1.country.IS", "1.country.IL", "1.country.IT", "1.country.JM", "1.country.JP", "1.country.YE",
					"1.country.JO", "1.country.YU", "1.country.KY", "1.country.KH", "1.country.CM", "1.country.CA",
					"1.country.CV", "1.country.KZ", "1.country.KE", "1.country.KG", "1.country.KI", "1.country.CC",
					"1.country.CO", "1.country.KM", "1.country.CG", "1.country.CD", "1.country.HR", "1.country.CU",
					"1.country.KW", "1.country.LA", "1.country.LS", "1.country.LV", "1.country.LB", "1.country.LR",
					"1.country.LY", "1.country.LI", "1.country.LT", "1.country.LU", "1.country.MO", "1.country.MG",
					"1.country.MW", "1.country.MY", "1.country.MV", "1.country.ML", "1.country.MT", "1.country.MP",
					"1.country.MA", "1.country.MH", "1.country.MQ", "1.country.MR", "1.country.MU", "1.country.YT",
					"1.country.MK", "1.country.MX", "1.country.FM", "1.country.MZ", "1.country.MD", "1.country.MC",
					"1.country.MN", "1.country.MS", "1.country.NA", "1.country.NR", "1.country.NP", "1.country.NC",
					"1.country.NZ", "1.country.NI", "1.country.NL", "1.country.AN", "1.country.NE", "1.country.NG",
					"1.country.NU", "1.country.KP", "1.country.NF", "1.country.NO", "1.country.OM", "1.country.AT",
					"1.country.PK", "1.country.PS", "1.country.PW", "1.country.PA", "1.country.PG", "1.country.PY",
					"1.country.PE", "1.country.PH", "1.country.PN", "1.country.PL", "1.country.PT", "1.country.PR",
					"1.country.QA", "1.country.RE", "1.country.RW", "1.country.RO", "1.country.RU", "1.country.LC",
					"1.country.ZM", "1.country.AS", "1.country.WS", "1.country.SM", "1.country.ST", "1.country.SA",
					"1.country.SE", "1.country.CH", "1.country.SN", "1.country.SC", "1.country.SL", "1.country.SG",
					"1.country.SK", "1.country.SI", "1.country.SB", "1.country.SO", "1.country.GS", "1.country.ES",
					"1.country.LK", "1.country.SH", "1.country.KN", "1.country.PM", "1.country.VC", "1.country.KR",
					"1.country.ZA", "1.country.SD", "1.country.SR", "1.country.SJ", "1.country.SZ", "1.country.SY",
					"1.country.TJ", "1.country.TW", "1.country.TZ", "1.country.TH", "1.country.TP", "1.country.TG",
					"1.country.TK", "1.country.TO", "1.country.TT", "1.country.TD", "1.country.CZ", "1.country.TN",
					"1.country.TR", "1.country.TM", "1.country.TC", "1.country.TV", "1.country.UG", "1.country.UA",
					"1.country.HU", "1.country.UY", "1.country.UZ", "1.country.VU", "1.country.VA", "1.country.VE",
					"1.country.AE", "1.country.US", "1.country.VN", "1.country.VG", "1.country.VI", "1.country.WF",
					"1.country.BY", "1.country.EH", "1.country.CF", "1.country.ZW", "1.country.CY"
	};

	/** Country Iso Shortcut values. */
	static String[] countryValues = new String[]
	{
					"", "AF", "EG", "AL", "DZ", "AD", "AO", "AI", "AQ", "AG", "GQ", "AR", "AM", "AW", "AZ", "ET", "AU",
					"BS", "BH", "BD", "BB", "BE", "BZ", "BJ", "BM", "BT", "MM", "BO", "BA", "BW", "BV", "BR", "IO",
					"BN", "BG", "BF", "BI", "CL", "CN", "CX", "CK", "CR", "DK", "DE", "DJ", "DM", "DO", "EC", "SV",
					"CI", "ER", "EE", "FK", "FO", "FJ", "FI", "FR", "GF", "PF", "TF", "GA", "GM", "GE", "GH", "GI",
					"GD", "GR", "GL", "GB", "GP", "GU", "GT", "GN", "GW", "GY", "HT", "HM", "HN", "HK", "IN", "ID",
					"IQ", "IR", "IE", "IS", "IL", "IT", "JM", "JP", "YE", "JO", "YU", "KY", "KH", "CM", "CA", "CV",
					"KZ", "KE", "KG", "KI", "CC", "CO", "KM", "CG", "CD", "HR", "CU", "KW", "LA", "LS", "LV", "LB",
					"LR", "LY", "LI", "LT", "LU", "MO", "MG", "MW", "MY", "MV", "ML", "MT", "MP", "MA", "MH", "MQ",
					"MR", "MU", "YT", "MK", "MX", "FM", "MZ", "MD", "MC", "MN", "MS", "NA", "NR", "NP", "NC", "NZ",
					"NI", "NL", "AN", "NE", "NG", "NU", "KP", "NF", "NO", "OM", "AT", "PK", "PS", "PW", "PA", "PG",
					"PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RE", "RW", "RO", "RU", "LC", "ZM", "AS", "WS",
					"SM", "ST", "SA", "SE", "CH", "SN", "SC", "SL", "SG", "SK", "SI", "SB", "SO", "GS", "ES", "LK",
					"SH", "KN", "PM", "VC", "KR", "ZA", "SD", "SR", "SJ", "SZ", "SY", "TJ", "TW", "TZ", "TH", "TP",
					"TG", "TK", "TO", "TT", "TD", "CZ", "TN", "TR", "TM", "TC", "TV", "UG", "UA", "HU", "UY", "UZ",
					"VU", "VA", "VE", "AE", "US", "VN", "VG", "VI", "WF", "BY", "EH", "CF", "ZW", "CY"
	};

	/** Read only. */
	protected boolean readOnly = false;

	/**
	 * Set the read only flag.
	 *
	 * @param readOnly Read only flag.
	 */
	public void setReadOnly (boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Get the read only flag.
	 *
	 * @retrun The read only flag.
	 */
	public boolean getReadOnly ()
	{
		return readOnly;
	}

	/**
	 * Render the tag.
	 *
	 * @exception JspException if a JSP exception has occurred.
	 */
	public int doEndTag () throws JspException
	{
		String selectedValue = getBeanProperty ().toString ();

		StringBuffer results = new StringBuffer ();

		if (! readOnly)
		{
			createSelectTag (results);

			for (int i = 0; i < countries.length; ++i)
			{
				results.append ("<option value=\"");
				results.append (countryValues[i]);
				results.append ("\"");

				if (countryValues[i].equals (selectedValue))
				{
					results.append (" selected=\"selected\"");
				}

				results.append (">");
				results.append (TagUtils.getInstance ().message (pageContext, bundle, locale, countries[i]));
				results.append ("</option>");
			}

			results.append ("</select>\n");
		}
		else
		{
			for (int i = 0; i < countries.length; ++i)
			{
				if (countryValues[i].equals (selectedValue))
				{
					results.append (TagUtils.getInstance ().message (pageContext, bundle, locale, countries[i]));
				}
			}
		}

		TagUtils.getInstance ().write (pageContext, results.toString ());

		return EVAL_PAGE;
	}

	/**
	 * Reset all tag attributes to their default values.
	 */
	public void release ()
	{
		super.release ();

		readOnly = false;
	}
}
